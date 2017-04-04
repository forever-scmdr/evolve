package ecommander.persistence.commandunits;

import ecommander.fwk.UserNotAllowedException;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.model.UserMapper;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import java.sql.PreparedStatement;

/**
 * Поменять владельца айтема
 * Новый владелец устанавливается а все вложенные айтемы по первичной иерархии
 * Created by E on 26/3/2017.
 */
public class ChangeItemOwnerDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent, DBConstants.ItemTbl {

	private Item item;
	private int newUser;
	private byte newGroup;

	public ChangeItemOwnerDBUnit(Item item, int newUser, byte newGroup) {
		this.item = item;
		this.newUser = newUser;
		this.newGroup = newGroup;
	}

	@Override
	public void execute() throws Exception {
		User admin = getTransactionContext().getInitiator();
		User user = null;
		// Проверить, является ли текущий пользователь админом обоих групп, новой и старой для айтема
		if (!admin.isAdmin(item.getOwnerGroupId())) {
			throw new UserNotAllowedException("User '" + admin.getName() + "' is not admin of '"
					+ UserGroupRegistry.getGroup(item.getOwnerGroupId()) + "' group");
		}
		if (!admin.isAdmin(newGroup)) {
			throw new UserNotAllowedException("User '" + admin.getName() + "' is not admin of '"
					+ UserGroupRegistry.getGroup(newGroup) + "' group");
		}
		// Проверить существование пользователя
		if (newUser != User.ANONYMOUS_ID) {
			user = UserMapper.getUser(newUser, getTransactionContext().getConnection());
			if (user == null)
				throw new IllegalArgumentException("User '" + newUser + "' is not found");
		}
		// Проверить, имеет ли новый указанный пользователь доступ к новой указанной группе
		if (newUser != User.ANONYMOUS_ID) {
			if (!user.inGroup(newGroup))
				throw new UserNotAllowedException("User '" + user.getName() + "' does not belong to '"
				+ UserGroupRegistry.getGroup(newGroup) + "' group");
		}

		// Установить владельца на айтем и сабайтемы
		// Сначала установить владельца на сам айтем
		TemplateQuery updateItemOwner = new TemplateQuery("Update item owner");
		updateItemOwner.UPDATE(I_TABLE).SET().col(I_GROUP).setByte(newGroup)._col(I_USER).setInt(newUser)
				.WHERE().col(I_ID).setLong(item.getId()).sql(";\r\n");
		// Потом обновить все сабайтемы
		updateItemOwner.UPDATE(I_TABLE).INNER_JOIN(IP_TABLE, I_ID, IP_CHILD_ID)
				.SET().col(I_GROUP).setByte(newGroup)._col(I_USER).setInt(newUser)
				.WHERE().col(IP_PARENT_ID).setLong(item.getId());
		try(PreparedStatement pstmt = updateItemOwner.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
	}
}
