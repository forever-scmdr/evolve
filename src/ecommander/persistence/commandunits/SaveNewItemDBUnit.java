package ecommander.persistence.commandunits;

import ecommander.filesystem.SaveItemFilesUnit;
import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.model.ItemType;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Сохранение нового айтема в базоне
 * 
 * Возможно использование в условиях, когда на сайте не использдуются ссылки
 * Тогда создание выполняется гораздо быстрее
 * 
 * @author EEEE
 */
class SaveNewItemDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemTbl, DBConstants.UniqueItemKeys {

	private Item item;
	private ItemBasics parent;
	private boolean triggerExtra = true;

	SaveNewItemDBUnit(Item item) {
		this.item = item;
	}

	SaveNewItemDBUnit(Item item, ItemBasics parent) {
		this.item = item;
		this.parent = parent;
	}

	SaveNewItemDBUnit(Item item, boolean triggerExtra) {
		this.item = item;
		this.triggerExtra = triggerExtra;
	}

	SaveNewItemDBUnit(Item item, ItemBasics parent, boolean triggerExtra) {
		this.item = item;
		this.parent = parent;
		this.triggerExtra = triggerExtra;
	}

	public void execute() throws Exception {
		// Создать значение ключа
		item.prepareToSave();

		// Загрузка и валидация родительского айтема, если надо
		Connection conn = getTransactionContext().getConnection();
		if (item.hasParent()) {
			if (parent == null)
				parent = ItemMapper.loadItemBasics(item.getContextParentId(), conn);
			testPrivileges(parent);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 1.   Сохранение айтема в таблицу айтемов, получение и установка в объект айтема нового ID
		//          Файловые параметры айтема уже можно сохранять, т.к. их значения уже можно получить
		//          (для этого не нужно выполнять команду сохранения файлов)
		//
		TemplateQuery itemInsert = new TemplateQuery("New item insert");
		itemInsert.INSERT_INTO(I_TABLE).SET()
				.col(I_SUPERTYPE).setInt(item.getBasicSupertypeId())
				._col(I_TYPE_ID).setInt(item.getTypeId())
				._col(I_KEY).setString(item.getKey())
				._col(I_T_KEY).setString(item.getKeyUnique())
				._col(I_PROTECTED).setByte(item.isFileProtected() ? (byte)1 : (byte)0)
				._col(I_GROUP).setByte(item.getOwnerGroupId())
				._col(I_USER).setInt(item.getOwnerUserId())
				._col(I_STATUS).setByte(item.getStatus())
				._col(I_PARAMS).setString(item.outputValues());
		// Иногда (например, при переносе со старой версии CMS) ID айтема уже задан (не равняется 0)
		boolean hasId = item.getId() > 0;
		if (hasId)
			itemInsert._col(I_ID).setLong(item.getId());

		if (!hasId) {
			try (PreparedStatement pstmt = itemInsert.prepareQuery(conn, true)) {
				pstmt.executeUpdate();
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next())
					item.setId(rs.getLong(1));
			}
		} else {
			try (PreparedStatement pstmt = itemInsert.prepareQuery(conn)) {
				pstmt.executeUpdate();
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////
		// Шаг 2.   Если айтем имеет уникальный текстовый ключ, то происходит его сохранение в
		//          таблицу ключей. Также может произойти обнлвение таблицы айтема, в случае
		//          если заданный тектовый ключ неуникален и был сгенерирован другой
		//
		if (item.getItemType().isKeyUnique()) {
			TemplateQuery uniqueKeyInsert = new TemplateQuery("Unique key insert");
			uniqueKeyInsert
					.INSERT_INTO(UK_TABLE).SET()
					.col(UK_ID).setLong(item.getId())
					._col(UK_KEY).setString(item.getKeyUnique());
			PreparedStatement keyUniqueStmt = uniqueKeyInsert.prepareQuery(conn);
			boolean needItemUpdate = false;
			try {
				keyUniqueStmt.executeUpdate();
			} catch (Exception e) {
				// Значит такой ключ уже существует, добавить к ключу ID айтема
				item.setKeyUnique(item.getKeyUnique() + item.getId());
				keyUniqueStmt.setString(2, item.getKeyUnique());
				keyUniqueStmt.executeUpdate();
				needItemUpdate = true;
			} finally {
				if (keyUniqueStmt != null)
					keyUniqueStmt.close();
			}
			// Обновление уникального ключа айтема (если это нужно)
			if (needItemUpdate) {
				TemplateQuery keyUpdate = new TemplateQuery("Item unique key update");
				keyUpdate.UPDATE(I_TABLE)
						.SET().col(I_T_KEY).setString(item.getKeyUnique())
						.WHERE().col(I_ID).setLong(item.getId());
				try (PreparedStatement pstmt = keyUpdate.prepareQuery(conn)) {
					pstmt.executeUpdate();
				}
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 3.   Сохранить связь нового айтема с его предшественниками по иерархии ассоциации
		//
		if (item.hasParent()) {
			executeCommand(new CreateAssocDBUnit(item, parent, item.getContextAssoc().getId(), true));
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 4.   Сохранить параметры айтема в таблицах индексов
		//
		ItemMapper.insertItemParametersToIndex(item, true, getTransactionContext());

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 5.   Сохранение файлов айтема
		//
		try {
			executeCommand(new SaveItemFilesUnit(item));
		} catch (Exception e) {
			if (!ignoreFileErrors)
				throw e;
			else
				ServerLogger.warn("Ignoring file error while saving new item", e);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 6.   Дополнительная обработка
		//
		if (triggerExtra && item.getItemType().hasExtraHandlers(ItemType.Event.create)) {
			for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers(ItemType.Event.create)) {
				PersistenceCommandUnit command = fac.createCommand(item);
				executeCommandInherited(command);
			}
		}

		// Добавление в полнотекстовый индекс
		if (insertIntoFulltextIndex)
			LuceneIndexMapper.insertItem(item, closeLuceneWriter);
	}

}