package ecommander.persistence.commandunits;

import ecommander.controllers.AppContext;
import ecommander.filesystem.ApplyItemFileProtection;
import ecommander.fwk.ServerLogger;
import ecommander.model.ItemBasics;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * Команда, которая защищает файлы для определенного айтема.
 * Могут защищаться файлы только для одного айтема, или дополнительно для всех вложенных айтемов
 * Created by E on 23/11/2017.
 */
public class ProtectItemFilesDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent {

	private ItemBasics item;
	boolean makeProtected;

	public ProtectItemFilesDBUnit(ItemBasics item, boolean makeProtected) {
		this.item = item;
		this.makeProtected = makeProtected;
	}

	@Override
	public void execute() throws Exception {
		testPrivileges(item);
		HashSet<Long> itemIds = new HashSet<>();
		itemIds.add(item.getId());
		// Загрузка сабайтемов
		TemplateQuery query = new TemplateQuery("subitems query");
		query.SELECT(IP_CHILD_ID).FROM(ITEM_PARENT_TBL).WHERE().col(IP_PARENT_ID).long_(item.getId())
				.AND().col(IP_ASSOC_ID).byte_(ItemTypeRegistry.getPrimaryAssoc().getId());
		try (PreparedStatement pstmt = query.prepareQuery(getTransactionContext().getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				itemIds.add(rs.getLong(1));
		}
		long[] longIds = ArrayUtils.toPrimitive(itemIds.toArray(new Long[0]));
		executeCommand(new ApplyItemFileProtection(makeProtected, longIds));
		// Обновить статус айтемов
		query = new TemplateQuery("protect files");
		query.UPDATE(ITEM_TBL).SET().col(I_PROTECTED).byte_(makeProtected ? (byte) 1 : (byte) 0)
				.WHERE().col_IN(I_ID).longIN(itemIds.toArray(new Long[0]));
		try (PreparedStatement pstmt = query.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		} catch (Exception e) {
			// Вернуть файлы назад в случае если статус айтема не обновляется
			ServerLogger.error("Unable to protect/unprotect files", e);
			executeCommand(new ApplyItemFileProtection(!makeProtected, longIds));
		}
	}
}
