package ecommander.persistence.commandunits;

import ecommander.filesystem.SaveItemFilesUnit;
import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
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
	private ItemMapper.Mode mode = ItemMapper.Mode.UPDATE;

	UpdateItemParamsDBUnit(Item item) {
		this.item = item;
	}

	UpdateItemParamsDBUnit(Item item, boolean forceUpdate) {
		this.item = item;
		if (forceUpdate) {
			this.mode = ItemMapper.Mode.FORCE_UPDATE;
			this.item.forceInitialInconsistent();
		}
	}

	public void execute() throws Exception {
		// Проверка прав пользователя
		testPrivileges(item);
		Connection conn = getTransactionContext().getConnection();

		// Сначала сохраняются файлы, это надо делать вначале, чтобы сгенерировалась метаинформация по файлам
		// Фактическое сохранение файлов происходит в этой команде
		try {
			executeCommand(new SaveItemFilesUnit(item));
		} catch (Exception e) {
			if (!ignoreFileErrors)
				throw e;
			else
				ServerLogger.warn("Ignoring file error while updating item", e);
		}

		// Сохранть новое уникальное ключевое значение, если это надо делать (если оно было изменено)
		if (item.getItemType().isKeyUnique() && !StringUtils.equals(item.getKeyUnique(), item.getOldKeyUnique())) {
			long sameKeyItemId = -1;
			byte sameKeyItemStatus = Item.STATUS_NORMAL;
			// Запрос на получение значения
			TemplateQuery keySelect = new TemplateQuery("key select");
			keySelect.SELECT(I_ID, I_STATUS).FROM(UNIQUE_KEY_TBL).INNER_JOIN(ITEM_TBL, UK_ID, I_ID)
					.WHERE().col(UK_KEY).string(item.getKeyUnique()).AND().col_IN(I_STATUS).byteIN(Item.STATUS_NORMAL, Item.STATUS_HIDDEN, Item.STATUS_DELETED);
			try (PreparedStatement pstmt = keySelect.prepareQuery(conn)) {
				pstmt.setString(1, item.getKeyUnique());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					sameKeyItemId = rs.getLong(1);
					sameKeyItemStatus = rs.getByte(2);
				}
			}

			TemplateQuery query = new TemplateQuery("Update key unique");
			// Если новое значение ключа не уникально - нужно добавить ID айтема
			if (sameKeyItemId > 0) {
				if (sameKeyItemStatus == Item.STATUS_DELETED) {
					TemplateQuery delete = new TemplateQuery("delete key");
					delete.DELETE_FROM_WHERE(UNIQUE_KEY_TBL).col(UK_ID).long_(sameKeyItemId);
					try (PreparedStatement pstmt = delete.prepareQuery(conn)) {
						pstmt.executeUpdate();
					}
				} else if (sameKeyItemId != item.getId()) {
					item.setKeyUnique(item.getKeyUnique() + item.getId());
				}
			}

			query.INSERT_INTO(UNIQUE_KEY_TBL)
					.SET().col(UK_ID).long_(item.getId())._col(UK_KEY).string(item.getKeyUnique())
					.ON_DUPLICATE_KEY_UPDATE(UK_KEY).string(item.getKeyUnique());
			/*
			// Айтем имел значение уникального ключа
			if (StringUtils.isNotBlank(item.getOldKeyUnique())) {
				query.UPDATE(UNIQUE_KEY_TBL).SET().col(UK_KEY).string(item.getKeyUnique())
						.WHERE().col(UK_KEY).string(item.getOldKeyUnique()).AND().col(UK_ID).long_(item.getId());
			}
			// Айтем раньше не имел уникального ключа
			else {
				query.INSERT_INTO(UNIQUE_KEY_TBL).SET().col(UK_ID).long_(item.getId())._col(UK_KEY).string(item.getKeyUnique());
			}
*/
			try (PreparedStatement pstmt = query.prepareQuery(conn)) {
				pstmt.executeUpdate();
			}
		}

		// Обновление параметров в таблице айтема
		TemplateQuery updateItem = new TemplateQuery("Update item");
		updateItem.UPDATE(ItemTbl.ITEM_TBL).SET()
				.col(ItemTbl.I_KEY).string(item.getKey())
				._col(ItemTbl.I_T_KEY).string(item.getKeyUnique())
				._col(ItemTbl.I_PARAMS).string(item.outputValues())
				.WHERE().col(ItemTbl.I_ID).long_(item.getId());
		try (PreparedStatement pstmt = updateItem.prepareQuery(conn)) {
			pstmt.executeUpdate();
		}

		// Теперь сохраняются параметры в таблицах индексов
		ItemMapper.insertItemParametersToIndex(item, mode, getTransactionContext());

		// Вставка в Lucene индекс
		if (insertIntoFulltextIndex)
			LuceneIndexMapper.getSingleton().updateItem(item);

		// Дополнительная обработка
		if (triggerExtra && item.getItemType().hasExtraHandlers(ItemType.Event.update)) {
			for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers(ItemType.Event.update)) {
				PersistenceCommandUnit command = fac.createCommand(item);
				executeCommandInherited(command);
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		if (ItemTypeRegistry.hasAffectedComputedSupertypes(item.getModifiedParams())) {
			addItemPredecessorsToComputedLog(item.getId(), ItemTypeRegistry.getAllAssocIds());
		}
	}

}
