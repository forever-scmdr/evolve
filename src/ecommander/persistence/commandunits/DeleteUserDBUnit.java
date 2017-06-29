package ecommander.persistence.commandunits;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserMapper;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * Удалить пользователя и его айтемы (или сделать их общими)
 */
public class DeleteUserDBUnit extends DBPersistenceCommandUnit implements DBConstants.UsersTbl, DBConstants.UserGroups, DBConstants.ItemTbl {

	private int userId;
	private boolean deleteItems;

	public DeleteUserDBUnit(int userId, boolean deleteItems) {
		this.userId = userId;
		this.deleteItems = deleteItems;
	}

	public void execute() throws Exception {
		User user = UserMapper.getUser(userId, getTransactionContext().getConnection());

		// Проверка прав
		testPrivileges(user, false);

		// Удаление групп и пользователя
		ArrayList<Byte> groupIds = new ArrayList<>();
		for (User.Group deletedGroup : user.getGroups()) {
			groupIds.add(deletedGroup.id);
		}
		TemplateQuery delete = new TemplateQuery("Delete user and groups");
		delete
				.DELETE_FROM_WHERE(USER_GROUP).col(UG_USER_ID).setInt(user.getUserId()).AND()
				.col(UG_GROUP_ID, " IN").byteArrayIN(groupIds.toArray(new Byte[groupIds.size()])).sql(";\r\n")
				.DELETE_FROM_WHERE(USER).col(U_ID).setInt(user.getUserId());
		try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		// Удаление айтемов пользователя, либо установка им нулевого владельца (сделать общими)
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


		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		if (deleteItems && processComputed && ItemTypeRegistry.hasComputedItems()) {
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
