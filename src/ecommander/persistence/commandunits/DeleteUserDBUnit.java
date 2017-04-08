package ecommander.persistence.commandunits;

import ecommander.model.Item;
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
				.DELETE_FROM_WHERE(UG_TABLE).col(UG_USER_ID).setInt(user.getUserId()).AND()
				.col(UG_GROUP_ID, " IN(").setByteArray(groupIds.toArray(new Byte[groupIds.size()])).sql(");\r\n")
				.DELETE_FROM_WHERE(U_TABLE).col(U_ID).setInt(user.getUserId());
		try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		// Удаление айтемов пользователя, либо установка им нулевого владельца (сделать общими)
		TemplateQuery modifyUserItems = new TemplateQuery("Modify or delete user items");
		modifyUserItems.UPDATE(I_TABLE).SET();
		if (deleteItems) {
			modifyUserItems.col(I_STATUS).setByte(Item.STATUS_DELETED);
		} else {
			modifyUserItems.col(I_USER).setInt(User.ANONYMOUS_ID);
		}
		modifyUserItems
				.WHERE().col(I_GROUP, " IN(").setByteArray(groupIds.toArray(new Byte[groupIds.size()])).sql(") ")
				.AND().col(I_USER).setInt(user.getUserId());
		try(PreparedStatement pstmt = modifyUserItems.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
	}

}
