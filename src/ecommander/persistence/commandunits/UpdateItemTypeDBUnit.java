package ecommander.persistence.commandunits;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.controllers.output.ItemTypeMDWriter;
import ecommander.controllers.output.ParameterDescriptionMDWriter;
import ecommander.controllers.output.XmlDocumentBuilder;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.ParameterDescription;

/**
 * Обновляет описание в базе данных
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 *         WARNING
 * После выполнения этой команды надо заново загружать модель айтемов и расширений айтемов        
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * @author EEEE
 *
 */
public class UpdateItemTypeDBUnit extends ItemModelFilePersistenceCommandUnit {

	private LinkedHashSet<Integer> paramsOrder = new LinkedHashSet<Integer>();
	private String extendsStr;
	private ItemType oldDesc;
	private ItemType newDesc;
	
	public UpdateItemTypeDBUnit(int id, String itemName, String caption, String description, String strExtends, String virtualStr,
			boolean inline, ArrayList<Integer> order, String itemKey) {
		if (strExtends != null) this.extendsStr = strExtends;
		if (order != null) paramsOrder.addAll(order);
		oldDesc = ItemTypeRegistry.getItemType(id);
		// Проверить, все ли параметры есть в айтеме. Отсутствующие удаляются из массива
		for (int i = 0; i < order.size(); i++) {
			if (oldDesc.getParameter(order.get(i)) == null) {
				order.remove(i);
				i--;
			}
		}
		// Добавить в порядок параметров все остальные параметры, которых нет в переданном списке
		for (ParameterDescription param : oldDesc.getParameterList()) {
			paramsOrder.add(param.getId());
		}
		boolean isKeyUnique = oldDesc.isKeyUnique();
		if (!StringUtils.isBlank(this.extendsStr)) {
			ItemType parent = ItemTypeRegistry.getItemType(this.extendsStr);
			isKeyUnique = parent != null && parent.isKeyUnique();
		}
		itemName = Strings.createXmlElementName(itemName);
		newDesc = new ItemType(itemName.trim(), id, caption, description, itemKey, extendsStr, Boolean.parseBoolean(virtualStr), true, inline,
				true, isKeyUnique);
	}
	
	public UpdateItemTypeDBUnit(ItemType desc) {
		this.oldDesc = desc;
		this.newDesc = desc;
		paramsOrder = null;
	}

	@Override
	protected void executeInt() throws Exception {
		ItemTypeMDWriter writer = new ItemTypeMDWriter(newDesc, ITEM_ELEMENT);
		// Если поменялось имя айтема - надо добавить атрибут old-name
		if (!oldDesc.getName().equals(newDesc.getName())) {
			writer.setNameOld(oldDesc.getName());
		}
		String startMark = getStartMark(oldDesc.getName());
		String endMark = getEndMark(oldDesc.getName());
		String startPart = StringUtils.substringBefore(getFileContents(), startMark);
		String endPart = StringUtils.substringAfter(getFileContents(), endMark);
		// Теперь добавить параметры (сабайтемы добавлять не надо, т.к. пользовательские айтемы не содержат сабайтемы)
		// Параметры брать из старого описания, т.к. в новом нет параметров
		if (paramsOrder != null) {
			for (Integer paramId : paramsOrder) {
				ParameterDescription paramDesc = oldDesc.getParameter(paramId);
				if (paramDesc.getOwnerItemId() == oldDesc.getTypeId())
					writer.addSubwriter(new ParameterDescriptionMDWriter(paramDesc));
			}
		} else {
			for (ParameterDescription paramDesc : newDesc.getParameterList()) {
				if (paramDesc.getOwnerItemId() == oldDesc.getTypeId())
					writer.addSubwriter(new ParameterDescriptionMDWriter(paramDesc));
			}
		}
		XmlDocumentBuilder itemXML = writeEntity(writer, newDesc.getName());
		setFileContents(startPart + itemXML.toString() + endPart);
		saveFile();
	}
}