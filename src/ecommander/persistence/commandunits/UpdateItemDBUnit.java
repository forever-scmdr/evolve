package ecommander.persistence.commandunits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.Item;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;
import ecommander.filesystem.SaveItemFilesUnit;

/**
 * Команда для сохранения уже существующего айтема
 * @author EEEE
 *
 */
class UpdateItemDBUnit extends DBPersistenceCommandUnit {
	
	private Item item;
	private boolean triggerExtra = true;
	private boolean updateParamsIndex = true;
	
	public UpdateItemDBUnit(Item item, boolean triggerExtra, boolean updateParamsIndex) {
		this.item = item;
		this.triggerExtra = triggerExtra;
		this.updateParamsIndex = updateParamsIndex;
	}
	
	public UpdateItemDBUnit(Item item) {
		this(item, true, true);
	}
	
	public void execute() throws Exception {
		PreparedStatement pstmt = null;
		// Проверка прав пользователя
		testPrivileges(item);
		Item initial = item.getConsistentVersion();
		try	{
			Connection conn = getTransactionContext().getConnection();
			// Сохранть новое уникальное ключевое значение, если это надо делать
			if (item.getItemType().isKeyUnique()) {
				long itemId = 0;
				// Запрос на получение значения
				String selectSql 
					= "SELECT " + DBConstants.UniqueItemKeys.ID + " FROM " + DBConstants.UniqueItemKeys.TABLE
					+ " WHERE " + DBConstants.UniqueItemKeys.KEY + "=?";
				pstmt = conn.prepareStatement(selectSql);
				pstmt.setString(1, item.getKeyUnique());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) itemId = rs.getLong(1);
				pstmt.close();
				rs.close();
				// Если надо производить вставку значения
				if (itemId != item.getId()) {
					// Удаление старого значения
					String deleteSql 
						= "DELETE FROM " + DBConstants.UniqueItemKeys.TABLE 
						+ " WHERE " + DBConstants.UniqueItemKeys.ID + "=?";
					pstmt = conn.prepareStatement(deleteSql);
					pstmt.setLong(1, item.getId());
					pstmt.executeUpdate();
					pstmt.close();
					// Запрос на вставку значения
					String insertSql 
						= "INSERT INTO " + DBConstants.UniqueItemKeys.TABLE 
						+ "(" + DBConstants.UniqueItemKeys.ID + ", " + DBConstants.UniqueItemKeys.KEY + ") VALUES (?, ?)";
					pstmt = conn.prepareStatement(insertSql);
					// Найденный ID не равен ID айтема - надо сгенерировать уникальное значение
					if (itemId != 0)
						item.setKeyUnique(item.getKeyUnique() + item.getId());
					// Вставка нового значения
					pstmt.setLong(1, item.getId());
					pstmt.setString(2, item.getKeyUnique());
					pstmt.executeUpdate();
					pstmt.close();
				}
			}
			
			// Теперь сохраняются файлы (перед сохранением значений параметров в БД)
			if (updateParamsIndex) {
				try {
					executeCommand(new SaveItemFilesUnit(item));
				} catch (Exception e) {
					if (!ignoreFileErrors)
						throw e;
					else
						ServerLogger.warn("Ignoring file error while updating item", e);
				}
			}
			
			// Сохранить новое ключевое значение
			String sql 
					= "UPDATE " + DBConstants.Item.TABLE + " SET " 
					+ DBConstants.Item.KEY + "=?, " 
					+ DBConstants.Item.TRANSLIT_KEY + "=?, "
					+ DBConstants.Item.OWNER_USER_ID + "=?, " 
					+ DBConstants.Item.OWNER_GROUP_ID + "=?, ";
			if (updateParamsIndex)
					sql += DBConstants.Item.PARAMS + "=?, ";
			sql += DBConstants.Item.UPDATED + "=NULL WHERE " + DBConstants.Item.REF_ID + "=" + item.getId();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, item.getKey());
			pstmt.setString(2, item.getKeyUnique());
			pstmt.setLong(3, item.getOwnerUserId());
			pstmt.setInt(4, item.getOwnerGroupId());
			if (updateParamsIndex) {
				fixAssociations(conn);
				pstmt.setString(5, item.outputValues());
			}
			pstmt.executeUpdate();
			pstmt.close();
			
			// Выполнить запросы для сохранения параметров
			if (updateParamsIndex)
				ItemMapper.insertItemParametersToIndex(item, true, getTransactionContext());
			
			// Вставка в Lucene индекс
			if (insertIntoFulltextIndex && updateParamsIndex)
				LuceneIndexMapper.updateItem(item, closeLuceneWriter);
			
			// Дополнительная обработка
			if (triggerExtra && item.getItemType().hasExtraHandlers()) {
				for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers()) {
					PersistenceCommandUnit command = fac.createUpdateCommand(item, initial);
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
			
			// Дополнительная обработка обязательная
			if (item.getItemType().hasExtraHandlers()) {
				for (ItemEventCommandFactory fac : item.getItemType().getExtraHandlers()) {
					if (!fac.getClass().getName().startsWith("ecommander.application.extra"))
						continue;
					PersistenceCommandUnit command = fac.createUpdateCommand(item, initial);
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
		} finally {
			if (pstmt != null)
				pstmt.close();
		}
	}

	private static final String SELECT_ASSOC_IDS = "SELECT " + DBConstants.Item.ID + " FROM " + DBConstants.Item.TABLE + " WHERE "
			+ DBConstants.Item.ID + " IN (<<IDS>>)";
	/**
	 * Исправить ассоциации (в частности удаленные айтемы)
	 * @param conn
	 * @throws SQLException
	 */
	private void fixAssociations(Connection conn) throws SQLException {
		for (ParameterDescription paramDesc : item.getItemType().getParameterList()) {
			if (paramDesc.getType() == Type.ASSOCIATED) {
				if (!item.getParameter(paramDesc.getId()).isEmpty()) {
					TemplateQuery query = TemplateQuery.createFromString(SELECT_ASSOC_IDS, "ids select");
					ArrayList<Long> associated = item.getLongValues(paramDesc.getName());
					query.getSubquery("<<IDS>>").setLongArray(associated.toArray(new Long[0]));
					PreparedStatement pstmt = query.prepareQuery(conn);
					ResultSet rs = pstmt.executeQuery();
					HashSet<Long> existingIds = new HashSet<Long>();
					while (rs.next())
						existingIds.add(rs.getLong(1));
					for (Long val : associated) {
						if (!existingIds.contains(val))
							item.removeEqualValue(paramDesc.getName(), val);
					}
				}
			}
		}
	}
	
}
