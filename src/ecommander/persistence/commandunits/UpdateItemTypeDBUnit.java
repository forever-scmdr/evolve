package ecommander.persistence.commandunits;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.Strings;
import ecommander.output.ItemTypeMDWriter;
import ecommander.output.ParameterDescriptionMDWriter;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;

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

	private LinkedHashSet<Integer> paramsOrder = new LinkedHashSet<>();
	private String extendsStr;
	private ItemType oldDesc;
	private ItemType newDesc;
	
	public UpdateItemTypeDBUnit(int id, String itemName, String caption, String description, String strExtends,
			boolean inline, ArrayList<Integer> order, String itemKey) {
		if (strExtends != null) this.extendsStr = strExtends;
		if (order != null) paramsOrder.addAll(order);
		oldDesc = ItemTypeRegistry.getItemType(id);
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
		newDesc = new ItemType(itemName.trim(), id, caption, description, itemKey, extendsStr,
				null, false, true, inline,true, isKeyUnique);
	}
	
	UpdateItemTypeDBUnit(ItemType desc) {
		this.oldDesc = desc;
		this.newDesc = desc;
		paramsOrder = null;
	}

	@Override
	protected void executeInt() throws Exception {
		ItemTypeMDWriter writer = new ItemTypeMDWriter(newDesc, ITEM);
		String startMark = getStartMark(oldDesc.getName());
		String endMark = getEndMark(oldDesc.getName());
		String startPart = StringUtils.substringBefore(getFileContents(), startMark);
		String endPart = StringUtils.substringAfter(getFileContents(), endMark);
		// Теперь добавить параметры (сабайтемы добавлять не надо, т.к. пользовательские айтемы не содержат сабайтемы)
		// Параметры брать из старого описания, т.к. в новом нет параметров
		if (paramsOrder != null) {
			for (Integer paramId : paramsOrder) {
				ParameterDescription paramDesc = oldDesc.getParameter(paramId);
				if (paramDesc == null)
					continue;
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