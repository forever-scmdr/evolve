package ecommander.persistence.commandunits;

import ecommander.filesystem.SaveItemFilesUnit;
import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Triple;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
	private int[] weightArg;	// вес. Иногда можно передать вес заранее, чтобы ускорить процесс сохранения.
									// Например при интеграции каталога (можно просто считать потомков у родителя)
	SaveNewItemDBUnit(Item item, int... weight) {
		this.item = item;
		this.weightArg = weight;
	}

	SaveNewItemDBUnit(Item item, ItemBasics parent, int... weight) {
		this.item = item;
		this.parent = parent;
		this.weightArg = weight;
	}

	public void execute() throws Exception {
		getTransactionContext().getTimer().start("ALL ITEM SAVE");
		// Создать значение ключа
		startQuery("SAVE NEW ITEM: prepare to save");
		getTransactionContext().getTimer().start("ITEM SAVE - prepare");
		item.prepareToSave();
		getTransactionContext().getTimer().stop("ITEM SAVE - prepare");

		// Загрузка и валидация родительского айтема, если надо
		startQuery("SAVE NEW ITEM: load parent");
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
		itemInsert.INSERT_INTO(ITEM_TBL).SET()
				.col(I_SUPERTYPE).int_(item.getBasicSupertypeId())
				._col(I_TYPE_ID).int_(item.getTypeId())
				._col(I_KEY).string(item.getKey())
				._col(I_T_KEY).string(item.getKeyUnique())
				._col(I_PROTECTED).byte_(item.isFileProtected() ? (byte)1 : (byte)0)
				._col(I_GROUP).byte_(item.getOwnerGroupId())
				._col(I_USER).int_(item.getOwnerUserId())
				._col(I_STATUS).byte_(item.getStatus())
				._col(I_PARAMS).string(item.outputValues());
		// Иногда (например, при переносе со старой версии CMS) ID айтема уже задан (не равняется 0)
		boolean hasId = item.getId() > 0;
		if (hasId)
			itemInsert._col(I_ID).long_(item.getId());

		startQuery(itemInsert.getSimpleSql());
		getTransactionContext().getTimer().start("ITEM SAVE - item table");
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
		getTransactionContext().getTimer().stop("ITEM SAVE - item table");
		endQuery();

		/////////////////////////////////////////////////////////////////////////////////////
		// Шаг 2.   Если айтем имеет уникальный текстовый ключ, то происходит его сохранение в
		//          таблицу ключей. Также может произойти обнлвение таблицы айтема, в случае
		//          если заданный тектовый ключ неуникален и был сгенерирован другой
		//
		getTransactionContext().getTimer().start("ITEM SAVE - key");
		if (item.getItemType().isKeyUnique()) {
			long sameKeyItemId = -1;
			byte sameKeyItemStatus = Item.STATUS_NORMAL;
			boolean keyUpdated = false;
			// Запрос на получение значения
			TemplateQuery keySelect = new TemplateQuery("key select");
			keySelect.SELECT(I_ID, I_STATUS).FROM(UNIQUE_KEY_TBL).INNER_JOIN(ITEM_TBL, UK_ID, I_ID)
					.WHERE().col(UK_KEY).string(item.getKeyUnique()).AND().col_IN(I_STATUS).byteIN(Item.STATUS_NORMAL, Item.STATUS_HIDDEN, Item.STATUS_DELETED);
			startQuery(keySelect.getSimpleSql());
			try (PreparedStatement pstmt = keySelect.prepareQuery(conn)) {
				pstmt.setString(1, item.getKeyUnique());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					sameKeyItemId = rs.getLong(1);
					sameKeyItemStatus = rs.getByte(2);
				}
			}
			endQuery();
			if (sameKeyItemId > 0) {
				if (sameKeyItemStatus == Item.STATUS_DELETED) {
					TemplateQuery delete = new TemplateQuery("delete key");
					delete.DELETE_FROM_WHERE(UNIQUE_KEY_TBL).col(UK_ID).long_(sameKeyItemId);
					try (PreparedStatement pstmt = delete.prepareQuery(conn)) {
						pstmt.executeUpdate();
					}
				} else {
					item.setKeyUnique(item.getKeyUnique() + item.getId());
					keyUpdated = true;
				}
			}

			TemplateQuery uniqueKeyInsert = new TemplateQuery("Unique key insert");
			uniqueKeyInsert
					.INSERT_INTO(UNIQUE_KEY_TBL).SET()
					.col(UK_ID).long_(item.getId())
					._col(UK_KEY).string(item.getKeyUnique());
			startQuery(uniqueKeyInsert.getSimpleSql());
			try (PreparedStatement keyUniqueStmt = uniqueKeyInsert.prepareQuery(conn)) {
				keyUniqueStmt.executeUpdate();
			}
			endQuery();
			// Обновление уникального ключа айтема (если это нужно)
			if (keyUpdated) {
				TemplateQuery keyUpdate = new TemplateQuery("Item unique key update");
				keyUpdate.UPDATE(ITEM_TBL)
						.SET().col(I_T_KEY).string(item.getKeyUnique())
						.WHERE().col(I_ID).long_(item.getId());
				startQuery(keyUpdate.getSimpleSql());
				try (PreparedStatement pstmt = keyUpdate.prepareQuery(conn)) {
					pstmt.executeUpdate();
				}
				endQuery();
			}
		}
		getTransactionContext().getTimer().stop("ITEM SAVE - key");

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 3.   Сохранить связь нового айтема с его предшественниками по иерархии ассоциации
		//
		if (item.hasParent()) {
			executeCommandInherited(CreateAssocDBUnit.childIsNew(item, parent, item.getContextAssoc().getId(), weightArg));
		} else {
			TemplateQuery rootQuery = new TemplateQuery("Insert pseudoroot assoc with self");
			rootQuery
					.INSERT_INTO(ITEM_PARENT_TBL).SET()
					.col(IP_CHILD_ID).long_(item.getId())
					._col(IP_PARENT_ID).long_(item.getId())
					._col(IP_ASSOC_ID).byte_(ItemTypeRegistry.getRootAssocId())
					._col(IP_PARENT_DIRECT).byte_((byte)1)
					._col(IP_CHILD_SUPERTYPE).int_(item.getBasicSupertypeId())
					._col(IP_WEIGHT).int_(0);
			startQuery(rootQuery.getSimpleSql());
			try (PreparedStatement pstmt = rootQuery.prepareQuery(conn)) {
				pstmt.executeUpdate();
			}
			endQuery();
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 4.   Сохранение файлов айтема
		//
		try {
			getTransactionContext().getTimer().start("ITEM SAVE - files");
			executeCommandInherited(new SaveItemFilesUnit(item));
			getTransactionContext().getTimer().stop("ITEM SAVE - files");
		} catch (Exception e) {
			if (!ignoreFileErrors)
				throw e;
			else
				ServerLogger.warn("Ignoring file error while saving new item", e);
		}
		// Если сохранение файлов привело к обновлению айтема (например, скачались файлы по заданному URL),
		// надо обновить параметры айтема
		if (item.hasChanged()) {
			TemplateQuery updateItem = new TemplateQuery("Update item");
			updateItem.UPDATE(DBConstants.ItemTbl.ITEM_TBL).SET()
					.col(DBConstants.ItemTbl.I_PARAMS).string(item.outputValues())
					.WHERE().col(DBConstants.ItemTbl.I_ID).long_(item.getId());
			startQuery(updateItem.getSimpleSql());
			getTransactionContext().getTimer().start("ITEM SAVE - item table update params");
			try (PreparedStatement pstmt = updateItem.prepareQuery(conn)) {
				pstmt.executeUpdate();
			}
			getTransactionContext().getTimer().stop("ITEM SAVE - item table update params");
			endQuery();
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 5.   Сохранить параметры айтема в таблицах индексов
		//
		startQuery("SAVE NEW ITEM: insert parameters");
		getTransactionContext().getTimer().start("ITEM SAVE - index tables write");
		ItemMapper.insertItemParametersToIndex(item, ItemMapper.Mode.INSERT, getTransactionContext());
		getTransactionContext().getTimer().stop("ITEM SAVE - index tables write");
		endQuery();

		////////////////////////////////////////////////////////////////////////////////////////////////
		// Шаг 6.   Дополнительная обработка
		//
		getTransactionContext().getTimer().start("ITEM SAVE - extra");
		if (triggerExtra && item.getItemType().hasExtraHandlers(ItemType.Event.create)) {
			for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers(ItemType.Event.create)) {
				PersistenceCommandUnit command = fac.createCommand(item);
				executeCommandInherited(command);
			}
		}
		getTransactionContext().getTimer().stop("ITEM SAVE - extra");

		// Добавление в полнотекстовый индекс
		if (insertIntoFulltextIndex) {
			LinkedHashMap<Long, ArrayList<Triple<Byte, Long, Integer>>> ancestors = ItemMapper.loadItemAncestorsTriple(item.getId());
			LuceneIndexMapper.getSingleton().updateItem(item, ancestors.get(item.getId()));
		}

		getTransactionContext().getTimer().stop("ALL ITEM SAVE");
	}

}