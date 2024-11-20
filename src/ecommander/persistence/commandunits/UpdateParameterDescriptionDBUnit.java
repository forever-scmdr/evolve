package ecommander.persistence.commandunits;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.controllers.output.ItemTypeMDWriter;
import ecommander.controllers.output.ParameterDescriptionMDWriter;
import ecommander.controllers.output.XmlDocumentBuilder;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.ParameterDescription.Quantifier;

/**
 * Сохраняет новый тип айтема в БД
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 *         WARNING
 * После выполнения этой команды надо заново загружать модель айтемов
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 * @author EEEE
 *
 */
public class UpdateParameterDescriptionDBUnit extends ItemModelFilePersistenceCommandUnit {

	public static final String SINGLE_VALUE = "single";
	public static final String MULTIPLE_VALUE = "multiple";
	
	private ParameterDescription newDesc;
	private ParameterDescription oldDesc;
	
	public UpdateParameterDescriptionDBUnit(int paramId, int itemId, String name, String caption, String description, 
			String domain, String format, String quantifierStr, String typeStr, boolean isVirtual, boolean isHidden) throws Exception {
		
		if (name == null) name = Strings.EMPTY;
		if (caption == null) caption = Strings.EMPTY;
		if (description == null) description = Strings.EMPTY;
		if (domain == null) domain = Strings.EMPTY;
		if (format == null) format = Strings.EMPTY;
		if (typeStr == null) typeStr = Strings.EMPTY;
		if (quantifierStr == null) quantifierStr = Strings.EMPTY;
		Quantifier quantifier = Quantifier.single;
		try {
			quantifier = Quantifier.valueOf(quantifierStr);
		} catch (Exception e) {
			throw new Exception("Parsing Model XML: Parameter of an item '" + itemId + "' is not 'single' or 'multiple'");
		}
		oldDesc = ItemTypeRegistry.getItemType(itemId).getParameter(paramId);
		newDesc = new ParameterDescription(name, paramId, typeStr, quantifier, itemId, domain, caption, description, format, isVirtual, isHidden);
	}

	@Override
	protected void executeInt() throws Exception {
		ParameterDescriptionMDWriter paramWriter = new ParameterDescriptionMDWriter(newDesc);
		// Если поменялось имя параметра - надо добавить атрибут old-name
		if (!oldDesc.getName().equals(newDesc.getName())) {
			paramWriter.setNameOld(oldDesc.getName());
		}
		// Если поменялся тип параметра - надо добавить атрибут old-type
		if (!oldDesc.getType().equals(newDesc.getType())) {
			paramWriter.setTypeOld(oldDesc.getType().toString());
		}
		ItemType itemDesc = ItemTypeRegistry.getItemType(newDesc.getOwnerItemId());
		ItemTypeMDWriter itemWriter = new ItemTypeMDWriter(itemDesc, ITEM_ELEMENT);
		String startMark = getStartMark(itemDesc.getName());
		String endMark = getEndMark(itemDesc.getName());
		String startPart = StringUtils.substringBefore(getFileContents(), startMark);
		String endPart = StringUtils.substringAfter(getFileContents(), endMark);
		// Теперь добавить параметры (сабайтемы добавлять не надо, т.к. пользовательские айтемы не содержат сабайтемы)
		for (ParameterDescription param : itemDesc.getParameterList()) {
			if (param.getOwnerItemId() == itemDesc.getTypeId()) {
				if (param.getId() == newDesc.getId())
					itemWriter.addSubwriter(paramWriter);
				else
					itemWriter.addSubwriter(new ParameterDescriptionMDWriter(param));
			}
		}
		XmlDocumentBuilder itemXML = writeEntity(itemWriter, itemDesc.getName());
		setFileContents(startPart + itemXML.toString() + endPart);
		saveFile();
	}
}