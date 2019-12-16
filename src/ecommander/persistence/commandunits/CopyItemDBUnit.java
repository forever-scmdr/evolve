package ecommander.persistence.commandunits;

import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.model.Item;
import ecommander.model.ItemTypeContainer;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.model.datatypes.DataType.Type;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Перемещение айтема (прикрепление айтема к другому родителю)
 * - нельзя перемещать айтемы в собственные сабайтемы (также в самого себя и в прямого родителя)
 * - нельзя перемещать айтемы в айтемы, не содержашие совместимых по типу сабайтемов
 * @author EEEE
 *
 */
public class CopyItemDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent, DBConstants.ItemTbl, DBConstants.ComputedLog {
	
	private static final HashSet<Type> TEXT_PARAM_TYPES = new HashSet<>();
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
		TemplateQuery check = new TemplateQuery("Check if copying possible");
		check.SELECT(IP_CHILD_ID).FROM(ITEM_PARENT_TBL).WHERE()
				.col(IP_PARENT_ID).long_(baseItem.getId()).AND()
				.col(IP_CHILD_ID).long_(newParent.getId());
		try (PreparedStatement pstmt = check.prepareQuery(getTransactionContext().getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() || newParent.getId() == baseItem.getId())
				throw new EcommanderException(ErrorCodes.ASSOC_NODES_ILLEGAL,
						"Unable to copy item ID " + baseItem.getId() + " to own subitem (" + newParent.getId() + ")");
		}
		boolean possibleSubitem = false;
		for (ItemTypeContainer.ChildDesc child : newParent.getItemType().getAllChildren()) {
			if (child.assocName.equals(ItemTypeRegistry.getPrimaryAssoc().getName()))
				possibleSubitem |= ItemTypeRegistry.getItemExtenders(child.itemName).contains(baseItem.getTypeName());
		}
		if (!possibleSubitem) {
			throw new EcommanderException(ErrorCodes.INCOMPATIBLE_ITEM_TYPES, "Unable to copy item ID " + baseItem.getId() +
					" to parent ID " + newParent.getId() + ". Incompatible types.");
		}

		// Если проверки прошли успешно - продолжение
		// Шаг 1. - Создать новый айтем с копией всех параметров базового, установить заданного родителя
		Item item = Item.newChildItem(baseItem.getItemType(), newParent);
		Item.updateParamValues(baseItem, item);

		// Шаг 2. - Установить во все параметры-файлы объекты типа File, чтобы эти файлы были скопированы при сохранении айтема
		for (ParameterDescription paramDesc : baseItem.getItemType().getParameterList()) {
			if (paramDesc.getDataType().isFile()) {
				ArrayList<File> files = baseItem.getFileValues(paramDesc.getName(),
						AppContext.getFilesDirPath(baseItem.isFileProtected()));
				item.clearValue(paramDesc.getName()); // для того, чтобы не было дублирования
				for (File file : files) {
					if(file.exists() && file.isFile()) {
						item.setValue(paramDesc.getName(), file);
					} else {
						item.setValue(paramDesc.getName(), file.getName());
					}
				}
			}
		}

		// Шаг 3. - Сохранить новый айтем
		executeCommand(new SaveNewItemDBUnit(item, newParent).ignoreFileErrors(ignoreFileErrors));

		// Шаг 4. - Обновить пути к файлам во всех текстовых параметрах айтема
		boolean corrections = false;
		for (ParameterDescription paramDesc : item.getItemType().getParameterList()) {
			if (TEXT_PARAM_TYPES.contains(paramDesc.getType())) {
				String value = item.getStringValue(paramDesc.getName(), "");
				String oldPath = baseItem.getRelativeFilesPath();
				String newPath = item.getRelativeFilesPath();
				if (StringUtils.contains(value, oldPath)) {
					corrections = true;
					item.setValue(paramDesc.getName(), StringUtils.replace(value, oldPath, newPath));
				}
			}
		}
		if (corrections)
			executeCommand(new UpdateItemParamsDBUnit(item).noFulltextIndex().noTriggerExtra());

		// Шаг 5. - Выполнить команду копирования для всех сабайтемов копируемого айтема
		TemplateQuery allSubitems = new TemplateQuery("Load item primary subitems");
		allSubitems.SELECT("*").FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID).WHERE()
				.col(IP_ASSOC_ID).byte_(ItemTypeRegistry.getPrimaryAssoc().getId()).AND()
				.col(IP_PARENT_ID).long_(baseItem.getId()).AND()
				.col(IP_PARENT_DIRECT).byte_((byte) 1);
		ArrayList<Item> subitems = new ArrayList<>();
		try (PreparedStatement pstmt = allSubitems.prepareQuery(getTransactionContext().getConnection())) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				subitems.add(ItemMapper.buildItem(rs, ItemTypeRegistry.getPrimaryAssoc().getId(), baseItem.getId()));
			}
		}
		for (Item subitem : subitems) {
			executeCommand(new CopyItemDBUnit(subitem, item));
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		addItemPredecessorsToComputedLog(item.getId(), ItemTypeRegistry.getPrimaryAssoc().getId());
	}

}