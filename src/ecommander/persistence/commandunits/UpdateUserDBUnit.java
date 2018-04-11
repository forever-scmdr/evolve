package ecommander.persistence.commandunits;

import ecommander.fwk.UserExistsExcepion;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Обновить пользователя
 * Можно обновить всего пользователя (с логином и паролем) или только его группы
 *
 * Политика удаления пользователя - оставлять или нет айтемы, которые ему принадлежат (персональные)
 * Если айтемы сохраняются, то они становятся общими для группы, которой они принадлежали.
 * Если айтемы удаляются, то они просто удаляются.
 *
 * @author EEEE
 *
 */
public class UpdateUserDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemTbl, DBConstants.UsersTbl, DBConstants.UserGroups {

	private User user;
	private boolean deleteItems;

	public UpdateUserDBUnit(User user, boolean deleteItems) {
		this.user = user;
		this.deleteItems = deleteItems;
	}
	
	public void execute() throws Exception {

		// Проверка, изменились ли параметры пользователя
		User oldUser = UserMapper.getUser(user.getUserId(), getTransactionContext().getConnection());
		boolean justGroups = StringUtils.equals(user.getName(), oldUser.getName())
				&& StringUtils.equals(user.getPassword(), oldUser.getPassword())
				&& StringUtils.equals(user.getDescription(), oldUser.getDescription());

		// Проверка прав
		testPrivileges(user, justGroups);

		// Проверка существования нового логина
		if (!justGroups) {
			int existingId = UserMapper.getUserId(user.getName(), getTransactionContext().getConnection());
			if (existingId >= 0 && existingId != user.getUserId())
				throw new UserExistsExcepion(user.getName());
		}

		// Сохранение новых групп
		HashSet<String> newGroups = user.groupsExtraOf(oldUser);
		if (newGroups.size() > 0) {
			TemplateQuery insertGroups = new TemplateQuery("Insert new user groups");
			insertGroups.INSERT_INTO(USER_GROUP_TBL, UG_USER_ID, UG_GROUP_ID, UG_GROUP_NAME, UG_ROLE)
					.sql(" VALUES ");
			boolean isNotFirst = false;
			for (String newGroup : newGroups) {
				String sqlStart = isNotFirst ? ", (" : "(";
				insertGroups.sql(sqlStart).int_(user.getUserId()).com()
						.byte_(UserGroupRegistry.getGroup(newGroup)).com()
						.string(newGroup).com()
						.byte_(user.getRole(newGroup)).sql(")");
				isNotFirst = true;
			}
			try (PreparedStatement pstmt = insertGroups.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}

		// Удаление старых групп
		HashSet<String> deletedGroups = user.groupsNotInOf(oldUser);
		if (deletedGroups.size() > 0) {
			ArrayList<Byte> groupIds = new ArrayList<>();
			for (String deletedGroup : deletedGroups) {
				groupIds.add(UserGroupRegistry.getGroup(deletedGroup));
			}
			TemplateQuery deleteGroups = new TemplateQuery("Delete user groups");
			deleteGroups
					.DELETE_FROM_WHERE(USER_GROUP_TBL).col(UG_USER_ID).int_(user.getUserId()).AND()
					.col_IN(UG_GROUP_ID).byteIN(groupIds.toArray(new Byte[groupIds.size()]));
			try (PreparedStatement pstmt = deleteGroups.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}

		// Обновление ролей в группах
		HashSet<String> commonGroups = user.commonGroups(oldUser);
		if (commonGroups.size() > 0) {
			boolean hasChanged = false;
			TemplateQuery updateRoles = new TemplateQuery("Update user group roles");
			for (String commonGroup : commonGroups) {
				if (user.getRole(commonGroup) != oldUser.getRole(commonGroup)) {
					updateRoles.UPDATE(USER_GROUP_TBL).SET().col(UG_ROLE).byte_(user.getRole(commonGroup))
							.WHERE().col(UG_USER_ID).int_(user.getUserId())
							.AND().col(UG_GROUP_ID).byte_(UserGroupRegistry.getGroup(commonGroup)).sql(";\r\n");
					hasChanged = true;
				}
			}
			if (hasChanged) {
				try(PreparedStatement pstmt = updateRoles.prepareQuery(getTransactionContext().getConnection())) {
					pstmt.executeUpdate();
				}
			}
		}

		// Удаление айтемов пользователя, либо установка им нулевого владельца (сделать общими)
		if (deletedGroups.size() > 0) {
			ArrayList<Byte> groupIds = new ArrayList<>();
			for (String deletedGroup : deletedGroups) {
				groupIds.add(UserGroupRegistry.getGroup(deletedGroup));
			}
			TemplateQuery modifyUserItems = new TemplateQuery("Modify or delete user items");
			modifyUserItems.UPDATE(ITEM_TBL).SET();
			if (deleteItems) {
				modifyUserItems.col(I_STATUS).byte_(Item.STATUS_DELETED);
			} else {
				modifyUserItems.col(I_USER).int_(User.ANONYMOUS_ID);
			}
			modifyUserItems
					.WHERE().col_IN(I_GROUP).byteIN(groupIds.toArray(new Byte[groupIds.size()]))
					.AND().col(I_USER).int_(user.getUserId());
			try(PreparedStatement pstmt = modifyUserItems.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}

		// Сохранение пользователя
		if (!justGroups) {
			TemplateQuery updateUser = new TemplateQuery("Update user attributes");
			updateUser
					.UPDATE(USER_TBL).SET()
					.col(U_LOGIN).string(user.getName())
					._col(U_PASSWORD).string(user.getPassword())
					._col(U_DESCRIPTION).string(user.getDescription())
					.WHERE().col(U_ID).int_(user.getUserId());
			try (PreparedStatement pstmt = updateUser.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		if (deleteItems && processComputed && ItemTypeRegistry.hasComputedItems() && deletedGroups.size() > 0) {
			ArrayList<Byte> groupIds = new ArrayList<>();
			for (String deletedGroup : deletedGroups) {
				groupIds.add(UserGroupRegistry.getGroup(deletedGroup));
			}
			TemplateQuery logInsert = new TemplateQuery("Insert into update log");
			final String P = "P.";
			final String I1 = "I1.";
			final String I2 = "I2.";
			logInsert
					.INSERT_INTO(COMPUTED_LOG_TBL, L_ITEM)
					.SELECT(I1 + I_ID)
					.FROM(ITEM_TBL + " AS I1").INNER_JOIN(ITEM_PARENT_TBL + " AS P", I1 + I_ID, P + IP_PARENT_ID)
					.INNER_JOIN(ITEM_TBL + " AS I2", P + IP_CHILD_ID, I2 + I_ID)
					.WHERE().col_IN(I2 + I_GROUP).byteIN(groupIds.toArray(new Byte[groupIds.size()]))
					.AND().col(I2 + I_USER).int_(user.getUserId())
					.AND().col_IN(I1 + I_SUPERTYPE).intArray(ItemTypeRegistry.getAllComputedSupertypes())
					.ON_DUPLICATE_KEY_UPDATE(L_ITEM).sql(L_ITEM);
			try(PreparedStatement pstmt = logInsert.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}
	}

}