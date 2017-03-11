package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.persistence.PersistenceCommandUnit;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;
import ecommander.filesystem.ItemFileUnit;
import ecommander.filesystem.SaveItemFilesUnit;

/**
 * Сохранение нового айтема в базоне
 * 
 * Возможно использование в условиях, когда на сайте не использдуются ссылки
 * Тогда создание выполняется гораздо быстрее
 * 
 * @author EEEE
 */
class SaveNewItemDBUnit extends DBPersistenceCommandUnit {

	private Item item;
	private boolean usingRefs = false;
	
	public SaveNewItemDBUnit(Item item) {
		this(item, false);
	}
	/**
	 * Можно отменить использование ссылок.
	 * В этом случае информация для связей с родительскими айтемами гораздо проще
	 * и запрос выполняется гораздо быстрее
	 * @param item
	 * @param usingRefs
	 */
	public SaveNewItemDBUnit(Item item, boolean usingRefs) {
		this.item = item;
		this.usingRefs = usingRefs;
	}
	
	public void execute() throws Exception {
		// Создать значение ключа
		item.prepareToSave();

		Connection conn = getTransactionContext().getConnection();
		String selectSql = null;
		ResultSet rs = null;
		
		// Подсчет общего количества прямых потомков родителя айтема для установления его порядкового номера
		int weight = findNewWeight(conn, item.getDirectParentId());
		
		// Строка PRED_ID_PATH (если указана и если не указана)
		String predIdPath = item.getPredecessorsPath();
		if (StringUtils.isBlank(predIdPath)) {
			selectSql
				= "SELECT CONCAT(" + DBConstants.Item.PRED_ID_PATH + ", " + DBConstants.Item.ID
				+ ", '/') FROM " + DBConstants.Item.TABLE 
				+ " WHERE " + DBConstants.Item.ID + " = " + item.getDirectParentId();
			ServerLogger.debug(selectSql);
			PreparedStatement predPathStmt = conn.prepareStatement(selectSql);
			rs = predPathStmt.executeQuery(selectSql);
			rs.next();
			predIdPath = rs.getString(1);
			item.setPredecessorsPath(predIdPath);
			rs.close();
			predPathStmt.close();
		}

		// Затем сохраняется айтем (без сохранения параметров, только для того чтобы получить ID)
		TemplateQuery builder = new TemplateQuery("New item insert");
		builder.sql(
				"INSERT INTO " + 
				DBConstants.Item.TABLE + 
				" SET " +
				DBConstants.Item.TYPE_ID + "=").setLong(item.getTypeId()).sql(", " +
				DBConstants.Item.REF_ID + "=").setLong(item.getRefId()).sql(", " + 
				DBConstants.Item.DIRECT_PARENT_ID + "=").setLong(item.getDirectParentId()).sql(", " + 
				DBConstants.Item.PRED_ID_PATH + "=").setString(predIdPath).sql(", " +
				DBConstants.Item.OWNER_GROUP_ID + "=").setLong(item.getOwnerGroupId()).sql(", " +
				DBConstants.Item.OWNER_USER_ID + "=").setLong(item.getOwnerUserId()).sql(", " + 
				DBConstants.Item.KEY + "=").setString(item.getKey()).sql(", " + 
				DBConstants.Item.INDEX_WEIGHT + "=").setInt(weight);
		// Иногда (например, при переносе со старой версии CMS) ID айтема уже задан (не равняется 0)
		boolean hasId = item.getId() > 0;
		if (hasId)
			builder.sql(", " + DBConstants.Item.ID + "=").setLong(item.getId());
		
		PreparedStatement insertItemStmt = builder.prepareQuery(conn, true);
		insertItemStmt.executeUpdate();
	
		// Получается ID нового айтема и устанавливается в этот объект айтема
		// Также устанавливается REF_ID айтема в случае, если айтем не является ссылочным айтемом (в этом классе - всегда)
		if (!hasId) {
			rs = insertItemStmt.executeQuery("SELECT LAST_INSERT_ID()");
			//rs = insertItemStmt.getGeneratedKeys();
			if (rs.next()) {
				long newItemId = rs.getLong(1);
				item.setId(newItemId);
				item.setRefId(item.getId());
			}
			rs.close();
		}
		insertItemStmt.close();
		
		// В этот момент если нужно происходит проверка существования уникального текстового ключа айтема
		if (item.getItemType().isKeyUnique()) {
			builder = new TemplateQuery("Unique key insert");
			builder.sql(
					"INSERT INTO " +
					DBConstants.UniqueItemKeys.TABLE + "(" +
					DBConstants.UniqueItemKeys.ID + ", " + 
					DBConstants.UniqueItemKeys.KEY + ") VALUES ("
					).setLong(item.getId()).sql(", ").setString(item.getKeyUnique()).sql(")");
			PreparedStatement keyUniqueStmt = builder.prepareQuery(conn);
			try {
				keyUniqueStmt.executeUpdate();
			} catch (Exception e) {
				// Значит такой ключ уже существует, добавить к ключу ID айтема
				item.setKeyUnique(item.getKeyUnique() + item.getId());
				keyUniqueStmt.setString(2, item.getKeyUnique());
				keyUniqueStmt.executeUpdate();
			}
			keyUniqueStmt.close();
		}

		// Т.к. получен ID нового айтема, можно сохранять файлы айтема
		try {
			executeCommand(new SaveItemFilesUnit(item));
		} catch (Exception e) {
			if (!ignoreFileErrors)
				throw e;
			else
				ServerLogger.warn("Ignoring file error while saving new item", e);
		}
		
		// Сохраняется значение REF_ID равное ID для нормальных (не ссылочных) айтемов,
		// сохраняются параметры айтема в виде строки XML
		// Также установить новый уникальный текстовый ключ, он мог поменяться
		builder = new TemplateQuery("Item parameters main table update");
		builder
			.sql("UPDATE " + DBConstants.Item.TABLE + " SET " + DBConstants.Item.REF_ID + "=").setLong(item.getId())
			.sql(", " + DBConstants.Item.TRANSLIT_KEY + "=").setString(item.getKeyUnique())
			.sql(", " + DBConstants.Item.PARAMS + "=").setString(item.outputValues())
			.sql(" WHERE " + DBConstants.Item.ID + "=").setLong(item.getId());
		PreparedStatement updateKeyUniqueStmt = builder.prepareQuery(conn);
		updateKeyUniqueStmt.executeUpdate();
		
		// Сохраняется связь айтема с предком и все предыдущие связи (сам айтем считается своим предком)
		// Поскольку сам айтем считается своим предком, то делать явно запись связи с предком в данном случае не надо,
		// т.к. она получится из второго запроса (предыдущие связи)
		if (usingRefs) {
			String parentSql
				= "REPLACE INTO "
				+ DBConstants.ItemParent.TABLE + "("
				+ DBConstants.ItemParent.REF_ID + ", "
				+ DBConstants.ItemParent.ITEM_ID + ", "
				+ DBConstants.ItemParent.PARENT_ID + ", "
				+ DBConstants.ItemParent.ITEM_TYPE + ", "
				+ DBConstants.ItemParent.PARENT_LEVEL
				+ ") VALUES (" + item.getId() + ", " + item.getRefId() + ", " + item.getId() + ", " + item.getTypeId() + ", 0)";
			ServerLogger.debug(parentSql);
			PreparedStatement insertParentSelfStmt = conn.prepareStatement(parentSql);
			insertParentSelfStmt.executeUpdate();
			insertParentSelfStmt.close();
			
			// Предыдущие связи
			String predecessorSql
				= "REPLACE INTO "
				+ DBConstants.ItemParent.TABLE + "(" 
				+ DBConstants.ItemParent.REF_ID + ", "
				+ DBConstants.ItemParent.ITEM_ID + ", "
				+ DBConstants.ItemParent.ITEM_TYPE + ", "
				+ DBConstants.ItemParent.PARENT_ID + ", "
				+ DBConstants.ItemParent.PARENT_LEVEL
				+ ") SELECT DISTINCT " + item.getId() + ", " + item.getRefId() + ", " + item.getTypeId() + ", "
				+ DBConstants.ItemParent.PARENT_ID + ", "
				+ DBConstants.ItemParent.PARENT_LEVEL + "+1 FROM "
				+ DBConstants.ItemParent.TABLE + " WHERE "
				+ DBConstants.ItemParent.REF_ID + "=" + item.getDirectParentId();
			ServerLogger.debug(predecessorSql);
			PreparedStatement insertParentPredsStmt = conn.prepareStatement(predecessorSql);
			insertParentPredsStmt.executeUpdate();
			insertParentPredsStmt.close();
		} else {
			// Все связи берутся из строки предшественников
			StringBuilder parentSql = new StringBuilder(
				"REPLACE INTO "
				+ DBConstants.ItemParent.TABLE + "("
				+ DBConstants.ItemParent.REF_ID + ", "
				+ DBConstants.ItemParent.ITEM_ID + ", "
				+ DBConstants.ItemParent.PARENT_ID + ", "
				+ DBConstants.ItemParent.ITEM_TYPE + ", "
				+ DBConstants.ItemParent.PARENT_LEVEL
				+ ") VALUES (" 
				+ item.getId() + ", " + item.getRefId() + ", " + item.getId() + ", " + item.getTypeId() + ", 0)");
			String[] parents = StringUtils.split(predIdPath, '/');
			for (int i = 0; i < parents.length; i++) {
				parentSql.append(", (" 
						+ item.getId() + ", " 
						+ item.getRefId() + ", " 
						+ parents[i] + ", " 
						+ item.getTypeId() + ", " 
						+ (parents.length - i) + ")");
			}
			ServerLogger.debug(parentSql.toString());
			PreparedStatement insertParentSimpleStmt = conn.prepareStatement(parentSql.toString());
			insertParentSimpleStmt.executeUpdate();
			insertParentSimpleStmt.close();
		}
		
		// Выполнить запросы для сохранения параметров
		ItemMapper.insertItemParametersToIndex(item, false, getTransactionContext());
		
		// Дополнительная обработка
		if (item.getItemType().hasExtraHandlers()) {
			for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers()) {
				PersistenceCommandUnit command = fac.createSaveCommand(item);
				if (command != null) {
					if (command instanceof DBPersistenceCommandUnit) {
						((DBPersistenceCommandUnit) command).fulltextIndex(insertIntoFulltextIndex, closeLuceneWriter);
						((DBPersistenceCommandUnit) command).ignoreUser(ignoreUser);
						((DBPersistenceCommandUnit) command).ignoreFileErrors(ignoreFileErrors);
					}
					executeCommand(command);
				}
			}
		}

		// Добавление в полнотекстовый индекс
		if (insertIntoFulltextIndex)
			LuceneIndexMapper.insertItem(item, closeLuceneWriter);
	}
	/**
	 * Определить вес (порядок следования) вновь создаваемого айтема
	 * @param stmt
	 * @param parentId
	 * @return
	 * @throws SQLException
	 */
	public static int findNewWeight(Connection conn, long parentId) throws SQLException {
		int maxWeight = 0;
		String selectSql
			= "SELECT MAX(" + DBConstants.Item.INDEX_WEIGHT + ") AS W FROM " 
			+ DBConstants.Item.TABLE + " WHERE " +	DBConstants.Item.DIRECT_PARENT_ID + " = " + parentId;
		ServerLogger.debug(selectSql);
		PreparedStatement pstmt = conn.prepareStatement(selectSql);
		ResultSet rs = pstmt.executeQuery(selectSql);
		if (rs.next())
			maxWeight = rs.getInt(1);
		rs.close();
		return maxWeight + Item.WEIGHT_STEP;
	}

	@Override
	public void rollback() throws Exception {
		super.rollback();
		ServerLogger.debug("Deleting item directory '" + item.getPredecessorsAndSelfPath() + "' - " + ItemFileUnit.deleteItemDirectory(item));
	}

}