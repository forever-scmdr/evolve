package ecommander.persistence.commandunits;

import ecommander.filesystem.SaveItemFilesUnit;
import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Команда для сохранения уже существующего айтема
 * @author EEEE
 *
 */
class UpdateItemParamsDBUnit extends DBPersistenceCommandUnit implements DBConstants.UniqueItemKeys, DBConstants {
	
	private Item item;
	private boolean triggerExtra = true;

	public UpdateItemParamsDBUnit(Item item, boolean triggerExtra) {
		this.item = item;
		this.triggerExtra = triggerExtra;
	}
	
	public UpdateItemParamsDBUnit(Item item) {
		this(item, true);
	}
	
	public void execute() throws Exception {
		// Проверка прав пользователя
		testPrivileges(item);
		Connection conn = getTransactionContext().getConnection();

		// Сохранть новое уникальное ключевое значение, если это надо делать (если оно было изменено)
		if (item.getItemType().isKeyUnique() && !StringUtils.equals(item.getKeyUnique(), item.getOldKeyUnique())) {
			long itemId = 0;
			// Запрос на получение значения
			String selectSql = "SELECT " + ID + " FROM " + TABLE + " WHERE " + KEY + "=?";
			try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
				pstmt.setString(1, item.getKeyUnique());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next())
					itemId = rs.getLong(1);
			}

			TemplateQuery query = new TemplateQuery("Update key unique");
			// Если новое значение ключа не уникально - нужно добавить ID айтема
			if (itemId != 0)
				item.setKeyUnique(item.getKeyUnique() + item.getId());
			// Айтем имел значение уникального ключа
			if (StringUtils.isNotBlank(item.getOldKeyUnique())) {
				query.UPDATE(TABLE).SET().col(KEY).setString(item.getKeyUnique())
						.WHERE().col(KEY).setString(item.getOldKeyUnique()).AND().col(ID).setLong(item.getId());
			}
			// Айтем раньше не имел уникального ключа
			else {
				query.INSERT_INTO(TABLE).SET().col(ID).setLong(item.getId()).col(KEY).setString(item.getKeyUnique());
			}

			try (PreparedStatement pstmt = query.prepareQuery(conn)) {
				pstmt.executeUpdate();
			}
		}

		// Обновление параметров в таблице айтема
		TemplateQuery updateItem = new TemplateQuery("Update item");
		updateItem.UPDATE(ItemTbl.TABLE).SET()
				.col(ItemTbl.KEY).setString(item.getKey())
				.col(ItemTbl.TRANSLIT_KEY).setString(item.getKeyUnique())
				.col(ItemTbl.PARAMS).setString(item.outputValues());
		try (PreparedStatement pstmt = updateItem.prepareQuery(conn)) {
			pstmt.executeUpdate();
		}

		// Теперь сохраняются параметры в таблицах индексов и файлы
		ItemMapper.insertItemParametersToIndex(item, false, getTransactionContext());
		try {
			executeCommand(new SaveItemFilesUnit(item));
		} catch (Exception e) {
			if (!ignoreFileErrors)
				throw e;
			else
				ServerLogger.warn("Ignoring file error while updating item", e);
		}

		// Вставка в Lucene индекс
		if (insertIntoFulltextIndex)
			LuceneIndexMapper.updateItem(item, closeLuceneWriter);

		// Дополнительная обработка
		if (triggerExtra && item.getItemType().hasExtraHandlers()) {
			for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers(ItemType.Event.update)) {
				PersistenceCommandUnit command = fac.createCommand(item);
				executeCommandInherited(command);
			}
		}
	}

}
