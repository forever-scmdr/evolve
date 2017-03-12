package ecommander.persistence.commandunits;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.Strings;
import ecommander.output.ItemTypeMDWriter;
import ecommander.output.ParameterDescriptionMDWriter;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;

/**
 * Сохраняет новый тип айтема в файле пользовательский айтемов.
 * На момент сохранения айтема, в нем нет параметров (они добавляются потом по одному)
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 *         WARNING
 * После выполнения этой команды надо заново загружать модель айтемов и расширений айтемов        
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * TODO <usability> Запретить создавать типы данных с запрещенными символами XML и с пробелом
 * 
 * @author EEEE
 *
 */
public class SaveNewItemTypeDBUnit extends ItemModelFilePersistenceCommandUnit {
	
	private boolean itemVirtual = false;
	private boolean isUserDefined = true;
	
	private ItemType newType = null;
	
	public SaveNewItemTypeDBUnit(String itemName, String caption, String description, String strExtends, String itemKey, boolean inline) {
		itemName = Strings.createXmlElementName(itemName.trim());
		ItemType parent = ItemTypeRegistry.getItemType(strExtends);
		boolean isKeyUnique = parent != null && parent.isKeyUnique();
		newType = new ItemType(itemName, 0, caption, description, itemKey, strExtends, null,
				itemVirtual, isUserDefined, inline, true, isKeyUnique);
	}
	
	public SaveNewItemTypeDBUnit(ItemType type) {
		this.newType = type;
	}

	@Override
	protected void executeInt() throws Exception {
		ItemTypeMDWriter writer = new ItemTypeMDWriter(newType, ITEM);
		for (ParameterDescription param : newType.getParameterList()) {
			writer.addSubwriter(new ParameterDescriptionMDWriter(param));
		}
		XmlDocumentBuilder itemXML = writeEntity(writer, newType.getName());
		itemXML.addComment(EXPAND_MARK);
		String file = StringUtils.replaceOnce(getFileContents(), EXPAND_MARK, itemXML.toString());
		setFileContents(file);
		saveFile();
	}
}