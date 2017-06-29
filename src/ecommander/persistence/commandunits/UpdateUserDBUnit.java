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
			insertGroups.INSERT_INTO(USER_GROUP, UG_USER_ID, UG_GROUP_ID, UG_GROUP_NAME, UG_ROLE)
					.sql(" VALUES ");
			boolean isNotFirst = false;
			for (String newGroup : newGroups) {
				String sqlStart = isNotFirst ? ", (" : "(";
				insertGroups.sql(sqlStart).setInt(user.getUserId()).com()
						.setByte(UserGroupRegistry.getGroup(newGroup)).com()
						.setString(newGroup).com()
						.setByte(user.getRole(newGroup)).sql(")");
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
					.DELETE_FROM_WHERE(USER_GROUP).col(UG_USER_ID).setInt(user.getUserId()).AND()
					.col(UG_GROUP_ID, " IN").byteArrayIN(groupIds.toArray(new Byte[groupIds.size()]));
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
					updateRoles.UPDATE(USER_GROUP).SET().col(UG_ROLE).setByte(user.getRole(commonGroup))
							.WHERE().col(UG_USER_ID).setInt(user.getUserId())
							.AND().col(UG_GROUP_ID).setByte(UserGroupRegistry.getGroup(commonGroup)).sql(";\r\n");
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
			modifyUserItems.UPDATE(ITEM).SET();
			if (deleteItems) {
				modifyUserItems.col(I_STATUS).setByte(Item.STATUS_DELETED);
			} else {
				modifyUserItems.col(I_USER).setInt(User.ANONYMOUS_ID);
			}
			modifyUserItems
					.WHERE().col(I_GROUP, " IN").byteArrayIN(groupIds.toArray(new Byte[groupIds.size()]))
					.AND().col(I_USER).setInt(user.getUserId());
			try(PreparedStatement pstmt = modifyUserItems.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}

		// Сохранение пользователя
		if (!justGroups) {
			TemplateQuery updateUser = new TemplateQuery("Update user attributes");
			updateUser
					.UPDATE(USER).SET()
					.col(U_LOGIN).setString(user.getName())
					._col(U_PASSWORD).setString(user.getPassword())
					._col(U_DESCRIPTION).setString(user.getDescription());
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
					.INSERT_INTO(COMPUTED_LOG, L_ITEM)
					.SELECT(I1 + I_ID)
					.FROM(ITEM + " AS I1").INNER_JOIN(ITEM_PARENT + " AS P", I1 + I_ID, P + IP_PARENT_ID)
					.INNER_JOIN(ITEM + " AS I2", P + IP_CHILD_ID, I2 + I_ID)
					.WHERE().col(I2 + I_GROUP, " IN").byteArrayIN(groupIds.toArray(new Byte[groupIds.size()]))
					.AND().col(I2 + I_USER).setInt(user.getUserId())
					.AND().col(I1 + I_SUPERTYPE, " IN").setIntArray(ItemTypeRegistry.getAllComputedSupertypes())
					.ON_DUPLICATE_KEY_UPDATE(L_ITEM).sql(L_ITEM);
			try(PreparedStatement pstmt = logInsert.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}
	}

}