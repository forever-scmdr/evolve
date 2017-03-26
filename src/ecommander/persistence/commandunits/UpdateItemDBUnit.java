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
import org.apache.commons.lang3.StringUtils;

/**
 * Команда для сохранения уже существующего айтема
 * @author EEEE
 *
 */
class UpdateItemDBUnit extends DBPersistenceCommandUnit implements DBConstants.UniqueItemKeys, DBConstants {
	
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
		// Проверка прав пользователя
		testPrivileges(item);
		try	{
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

			TemplateQuery updateItem = new TemplateQuery("Update item");
			updateItem.UPDATE(ItemTbl.TABLE).SET()
					.col(ItemTbl.KEY).setString(item.getKey())
					.col(ItemTbl.TRANSLIT_KEY).setString(item.getKeyUnique())
					.col(ItemTbl.PROTECTED).setByte(item.isFileProtected() ? (byte) 1 : (byte) 0);


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

			// Теперь сохраняются параметры и файлы
			if (updateParamsIndex) {
				ItemMapper.insertItemParametersToIndex(item, false, getTransactionContext());
				try {
					executeCommand(new SaveItemFilesUnit(item));
				} catch (Exception e) {
					if (!ignoreFileErrors)
						throw e;
					else
						ServerLogger.warn("Ignoring file error while updating item", e);
				}
			}

			
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
