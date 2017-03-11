package ecommander.persistence.commandunits;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.EcommanderException;
import ecommander.controllers.AppContext;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;

/**
 * Перемещение айтема (прикрепление айтема к другому родителю)
 * - нельзя перемещать айтемы в собственные сабайтемы (также в самого себя и в прямого родителя)
 * - нельзя перемещать айтемы в айтемы, не содержашие совместимых по типу сабайтемов
 * @author EEEE
 *
 */
public class CopyItemDBUnit extends DBPersistenceCommandUnit {
	
	private static final HashSet<Type> TEXT_PARAM_TYPES = new HashSet<Type>();
	static {
		TEXT_PARAM_TYPES.add(Type.PLAIN_TEXT);
		TEXT_PARAM_TYPES.add(Type.SHORT_TEXT);
		TEXT_PARAM_TYPES.add(Type.TEXT);
		TEXT_PARAM_TYPES.add(Type.TINY_TEXT);
	}
	
	private Item baseItem;
	private Item newParent;
	private long newParentId;
	private long baseItemId;
	
	public CopyItemDBUnit(Item itemToCopy, long newParentId) {
		this.baseItem = itemToCopy;
		this.newParentId = newParentId;
	}
	
	public CopyItemDBUnit(Item itemToCopy, Item newParent) {
		this.baseItem = itemToCopy;
		this.newParent = newParent;
	}
	
	public CopyItemDBUnit(long itemToCopyId, long newParentId) {
		this.baseItemId = itemToCopyId;
		this.newParentId = newParentId;
	}
	
	public void execute() throws Exception {
		Statement stmt = null;
		try	{
			Connection conn = getTransactionContext().getConnection();
			stmt = conn.createStatement();
			
			// Загрузка айтемов
			if (baseItem == null)
				baseItem = ItemQuery.loadById(baseItemId, getTransactionContext().getConnection());
			if (newParent == null)
				newParent = ItemQuery.loadById(newParentId, getTransactionContext().getConnection());
			
			////// Проверка прав пользователя на айтем //////
			//
			testPrivileges(baseItem);
			testPrivileges(newParent);
			//
			/////////////////////////////////////////////////
			
			// Проверка, можно ли копировать
			String selectSql 
				= "SELECT " + DBConstants.ItemParent.REF_ID + " FROM " + DBConstants.ItemParent.TABLE 
				+ " WHERE (" + DBConstants.ItemParent.PARENT_ID  + " = " + baseItem.getId()
				+ " AND " + DBConstants.ItemParent.REF_ID + " = " + newParent.getId() + ")";
			ServerLogger.debug(selectSql);
			ResultSet rs = stmt.executeQuery(selectSql);
			if (rs.next() || newParent.getId() == baseItem.getId())
				throw new EcommanderException("Unable to copy item ID " + baseItem.getId() + " to it's subitem (ID " + newParent.getId() + ").");
			boolean possibleSubitem = false;
			for (String subitemName : newParent.getItemType().getAllChildren()) {
				possibleSubitem |= ItemTypeRegistry.getItemExtenders(subitemName).contains(baseItem.getTypeName());
			}
			if (!possibleSubitem)
				throw new EcommanderException("Unable to copy item ID " + baseItem.getId() + " to parent ID " + newParent.getId()
						+ ". Incompatible types.");
			
			// Если проверки прошли успешно - продолжение
			// Шаг 1. - Создать новый айтем с копией всех параметров базового, установить заданного родителя
			Item item = Item.newChildItem(baseItem.getItemType(), newParent);
			Item.updateParamValues(baseItem, item);
			
			// Шаг 2. - Установить во все параметры-файлы объекты типа File, чтобы эти файлы были скопированы при сохранении айтема
			for (Iterator<ParameterDescription> iter = baseItem.getItemType().getParameterList().iterator(); iter.hasNext();) {
				ParameterDescription paramDesc = iter.next();
				if (paramDesc.getDataType().isFile()) {
					ArrayList<File> files = baseItem.getFileValues(paramDesc.getName(), AppContext.getFilesDirPath());
					item.clearParameter(paramDesc.getName()); // для того, чтобы не было дублирования
					for (File file : files) {
						item.setValue(paramDesc.getName(), file);
					}
				}
			}
			
			// Шаг 3. - Созранить новый айтем
			executeCommand(new SaveNewItemDBUnit(item, false));
			
			// Шаг 4. - Обновить пути к файлам во всех текстовых параметрах айтема
			boolean corrections = false;
			for (Iterator<ParameterDescription> iter = item.getItemType().getParameterList().iterator(); iter.hasNext();) {
				ParameterDescription paramDesc = iter.next();
				if (TEXT_PARAM_TYPES.contains(paramDesc.getType())) {
					String value = item.getStringValue(paramDesc.getName(), "");
					String oldPath = baseItem.getPredecessorsAndSelfPath();
					String newPath = item.getPredecessorsAndSelfPath();
					if (StringUtils.contains(value, oldPath)) {
						corrections = true;
						item.setValue(paramDesc.getName(), StringUtils.replace(value, oldPath, newPath));
					}
				}
			}
			if (corrections)
				executeCommand(new UpdateItemDBUnit(item, false, true).fulltextIndex(false));
			
			// Шаг 5. - Выполнить команду копирования для всех сабайтемов копируемого айтема
			ArrayList<Item> subitems = ItemQuery.loadByParentId(baseItem.getId(), conn);
			for (Item subitem : subitems) {
				executeCommand(new CopyItemDBUnit(subitem, item));
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

}