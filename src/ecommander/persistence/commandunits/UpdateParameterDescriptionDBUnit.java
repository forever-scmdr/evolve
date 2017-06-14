package ecommander.persistence.commandunits;

import ecommander.fwk.Strings;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.pages.output.ItemTypeMDWriter;
import ecommander.pages.output.ParameterDescriptionMDWriter;
import ecommander.fwk.XmlDocumentBuilder;
import org.apache.commons.lang3.StringUtils;

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
			String domain, String format, boolean isMultiple, String typeStr, boolean isVirtual, boolean isHidden, String defaultValue) throws Exception {
		
		if (name == null) name = Strings.EMPTY;
		if (caption == null) caption = Strings.EMPTY;
		if (description == null) description = Strings.EMPTY;
		if (domain == null) domain = Strings.EMPTY;
		if (format == null) format = Strings.EMPTY;
		if (typeStr == null) typeStr = Strings.EMPTY;
		oldDesc = ItemTypeRegistry.getItemType(itemId).getParameter(paramId);
		newDesc = new ParameterDescription(name, paramId, typeStr, isMultiple, itemId, domain, caption, description,
				format, isVirtual, isHidden, defaultValue, null);
	}

	@Override
	protected void executeInt() throws Exception {
		ParameterDescriptionMDWriter paramWriter = new ParameterDescriptionMDWriter(newDesc);
		ItemType itemDesc = ItemTypeRegistry.getItemType(newDesc.getOwnerItemId());
		ItemTypeMDWriter itemWriter = new ItemTypeMDWriter(itemDesc, ITEM);
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