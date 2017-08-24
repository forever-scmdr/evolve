package ecommander.pages;

import ecommander.model.Assoc;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Страничный айтем
 * 
 * Конкретный айтем на странице
 * Айтем может быть группой, тогда они состоит только из одного параметра (по которому происходит группировка), 
 * но из него могут идти ссылки
 * @author EEEE
 *
 */
public class ItemPE extends PageElementContainer {

	public static final String ELEMENT_NAME = "item";

	public enum Type {
		SINGLE, 		// <single>
		LIST, 			// <list>
		TREE, 			// <tree>
		ANCESTOR,       // <ancestor>
		NEW;	        // <new>
		public static Type getValue(String val) {
			if (StringUtils.equalsIgnoreCase("single", val))
				return SINGLE;
			if (StringUtils.equalsIgnoreCase("list", val))
				return LIST;
			if (StringUtils.equalsIgnoreCase("tree", val))
				return TREE;
			if (StringUtils.equalsIgnoreCase("ancestor", val))
				return ANCESTOR;
			if (StringUtils.equalsIgnoreCase("new", val))
				return NEW;
			throw new IllegalArgumentException("there is no ItemQuery Type value for '" + val + "' string");
		}
	}

	public enum ItemRootType {
		COMMON, SESSION, PERSONAL, GROUP
	}

	// Тип
	private Type type;
	// Название айтема (типа айтема)
	private String itemName = null;
	// Название ассоциации
	private String assocName = null;
	// Название айтема, уникальное для него на странице
	private String pageId = null;
	// ID родительского айтема для новых айтемов (айтемов типа new)
	private String parentPageId = null;
	// Какой это айтем - personal, session или обычный
	private ItemRootType rootType;
	// Какой группе пользователей принадлежит айтем (когда пользователю из одной группы надо загрузить данные пользователей других групп)
	private String rootGroupName;
	// Название тэга, который будет обозначать этот айтем на странице. По умолчанию - название (name) айтема
	private String tag = null;
	// Надо ли выводить этот айтем в XML виде. Некоторые айтемы выводить не надо, т.к. они нужны только чтобы загрузить другие айтемы
	private boolean virtual = false;
	// Нужно ли загружать айтемы транзитивно
	private boolean isTransitive = false;
	// Можно ли кешировать данный айтем
	private boolean cacheable = false;
	// Список названий переменных страницы для идентификации айтема при кешировании
	private ArrayList<String> cacheVars = null;

	ItemPE(Type type, String itemName, String assocName, String pageId, String parentPageId, String tag, ItemRootType rootType,
	       String rootGroupName, boolean isTransitive, boolean isCacheable, boolean isVirtual, ArrayList<String> cacheVarNames) {
		super();
		this.type = type;
		this.itemName = itemName;
		this.assocName = assocName;
		this.pageId = pageId;
		this.parentPageId = parentPageId;
		this.tag = tag;
		this.rootType = rootType;
		this.rootGroupName = rootGroupName;
		this.isTransitive = isTransitive;
		this.cacheable = isCacheable;
		this.virtual = isVirtual;
		this.cacheVars = cacheVarNames;
		if (StringUtils.isBlank(this.assocName))
			this.assocName = ItemTypeRegistry.getPrimaryAssoc().getName();
	}

	public final String getItemName() {
		return itemName;
	}

	public final String getTag() {
		return tag;
	}
	
	public final Type getQueryType() {
		return type;
	}

	public final ItemRootType getRootType() {
		return rootType;
	}
	
	public final String getRootGroupName() {
		return rootGroupName;
	}

	@Override
	protected final PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		ExecutableItemPE clone = new ExecutableItemPE(type, itemName, assocName, pageId, parentPageId, tag, rootType,
				rootGroupName, isTransitive, cacheable, virtual, cacheVars, parentPage);
		if (container != null)
			((ExecutableItemContainer)container).addExecutableItem(clone);
		return clone;
	}

	public final String getId() {
		return pageId;
	}

	public final String getParentId() {
		return parentPageId;
	}
	
	protected final boolean hasId() {
		return !StringUtils.isBlank(pageId);
	}
	
	public final boolean isSession() {
		return rootType == ItemRootType.SESSION;
	}
	
	public final boolean isPersonal() {
		return rootType == ItemRootType.PERSONAL;
	}
	
	public final boolean isTransitive() {
		return isTransitive;
	}

	public final String getAssocName() {
		return assocName;
	}
	
	public final boolean isCacheable() {
		return cacheable;
	}

	public final boolean isVirtual() {
		return virtual;
	}
	
	public final boolean hasCacheVars() {
		return cacheVars != null && !cacheVars.isEmpty();
	}

	protected ArrayList<String> getCacheVars() {
		return cacheVars;
	}

	public final String getKey() {
		return type.toString() + " '" + itemName + "'";
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
		// Есть ли айтем с заданным именем
		if (itemDesc == null) {
			results.addError(elementPath + " > " + getKey(), "there is no '" + itemName + "' item in site model");
			return false;
		}
		Assoc assoc = ItemTypeRegistry.getAssoc(assocName);
		if (assoc == null) {
			results.addError(elementPath + " > " + getKey(), "there is no '" + assocName + "' association in site model");
			return false;
		}
		// Установить данные для последующей валидации (ItemDescription страничного айтема)
		results.pushBufferData(itemDesc);
		return true;
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

}