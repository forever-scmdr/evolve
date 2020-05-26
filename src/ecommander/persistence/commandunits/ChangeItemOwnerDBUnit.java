package ecommander.persistence.commandunits;

import ecommander.fwk.UserNotAllowedException;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import java.sql.PreparedStatement;

/**
 * Поменять владельца айтема
 * Новый владелец устанавливается а все вложенные айтемы по первичной иерархии
 * Created by E on 26/3/2017.
 */
public class ChangeItemOwnerDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent, DBConstants.ItemTbl {

	private ItemBasics item;
	private int newUser;
	private byte newGroup;

	private ChangeItemOwnerDBUnit(ItemBasics item, int newUser, byte newGroup) {
		this.item = item;
		this.newUser = newUser;
		this.newGroup = newGroup;
	}

	public static ChangeItemOwnerDBUnit newGroup(ItemBasics item, byte newGroup) {
		return new ChangeItemOwnerDBUnit(item, User.ANONYMOUS_ID, newGroup);
	}

	public static ChangeItemOwnerDBUnit newUser(ItemBasics item, int newUser, byte newGroup) {
		return new ChangeItemOwnerDBUnit(item, newUser, newGroup);
	}

	@Override
	public void execute() throws Exception {
		User admin = getTransactionContext().getInitiator();
		User user = null;
		// Проверить, является ли текущий пользователь админом обоих групп, новой и старой для айтема
		if (!admin.isAdmin(item.getOwnerGroupId()) && !ignoreUser) {
			throw new UserNotAllowedException("User '" + admin.getName() + "' is not admin of '"
					+ UserGroupRegistry.getGroup(item.getOwnerGroupId()) + "' group");
		}
		if (!admin.isAdmin(newGroup) && !ignoreUser) {
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
		updateItemOwner.UPDATE(ITEM_TBL).SET().col(I_GROUP).byte_(newGroup)._col(I_USER).int_(newUser)
				.WHERE().col(I_ID).long_(item.getId()).sql(";\r\n");
		// Потом обновить все сабайтемы по первичной иерархии
		updateItemOwner.UPDATE(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID)
				.SET().col(I_GROUP).byte_(newGroup)._col(I_USER).int_(newUser)
				.WHERE().col(IP_PARENT_ID).long_(item.getId())
				.AND().col(IP_ASSOC_ID).byte_(ItemTypeRegistry.getPrimaryAssocId());
		try(PreparedStatement pstmt = updateItemOwner.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
	}
}
