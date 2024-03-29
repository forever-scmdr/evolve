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
				.DELETE_FROM_WHERE(USER_GROUP_TBL).col(UG_USER_ID).int_(user.getUserId()).AND()
				.col_IN(UG_GROUP_ID).byteIN(groupIds.toArray(new Byte[groupIds.size()])).sql(";\r\n")
				.DELETE_FROM_WHERE(USER_TBL).col(U_ID).int_(user.getUserId());
		try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		// Удаление айтемов пользователя, либо установка им нулевого владельца (сделать общими)
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


		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		if (deleteItems && processComputed && ItemTypeRegistry.hasComputedItems()) {
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
