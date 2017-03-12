package ecommander.persistence.commandunits;

import ecommander.fwk.Strings;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;

/**
 * Сохраняет новый тип айтема в БД
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 *         WARNING
 * После выполнения этой команды надо заново загружать модель айтемов
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 * @author EEEE
 *
 */
public class SaveNewParameterDescriptionDBUnit extends ItemModelFilePersistenceCommandUnit {

	public static final String SINGLE_VALUE = "single";
	public static final String MULTIPLE_VALUE = "multiple";
	
	private int itemId;
	private String name;
	private String caption;
	private String description;
	private String domain;
	private String format;
	private String type;
	private boolean isMultiple = false;
	private boolean hidden = false;
	private boolean virtual = false;
	private String defaultValue;

	public SaveNewParameterDescriptionDBUnit(String paramName, int itemId, String caption, String description, String domain,
			String format, boolean isMultiple, String typeStr, boolean isVirtual, boolean isHidden, String defaultValue) throws Exception {
		this.itemId = itemId;
		this.hidden = isHidden;
		this.virtual = isVirtual;
		if (paramName == null) paramName = Strings.EMPTY;
		paramName = Strings.createXmlElementName(paramName);
		this.name = paramName;
		this.caption = caption;
		if (caption == null) caption = Strings.EMPTY;
		this.description = description;
		if (description == null) description = Strings.EMPTY;
		this.domain = domain;
		if (domain == null) domain = Strings.EMPTY;
		this.format = format;
		if (format == null) format = Strings.EMPTY;
		this.type = typeStr;
		if (type == null) type = Strings.EMPTY;
		this.isMultiple = isMultiple;
		this.defaultValue = defaultValue;
	}

	@Override
	protected void executeInt() throws Exception {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemId);
		itemDesc.putParameter(new ParameterDescription(name, 0, type, isMultiple, itemId, domain, caption,
				description, format, virtual, hidden, defaultValue, null));
		executeCommand(new UpdateItemTypeDBUnit(itemDesc));
	}
}