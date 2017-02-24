package ecommander.pages;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.itemquery.ItemQuery;

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
	public static final String SUCCESSOR_ELEMENT_NAME = "successor";
	public static final String PARENT_OF_ELEMENT_NAME = "parent-of";
	public static final String PREDECESSORS_OF_ELEMENT_NAME = "baseItems-of";
	
	// Какой это айтем - personal, session или обычный
	private ItemRootType itemRootType;
	// Какой группе пользователей принадлежит айтем (когда пользователю из одной группы надо загрузить данные пользователей других групп)
	private String itemRootGroupName;
	// Название айтема (типа айтема)
	protected String itemName = null;
	// Название айтема, уникальное для него на странице
	protected String itemPageId = null;
	// Название тэга, который будет обозначать этот айтем на странице. По умолчанию - название (name) айтема
	private String tag = null;
	// Надо ли выводить этот айтем в XML виде. Некоторые айтемы выводить не надо, т.к. они нужны только чтобы загрузить другие айтемы
	private boolean virtual = false;
	// Тип
	private ItemQuery.Type itemType;
	// Множественный или одиночный айтем
	private boolean quantifierSingle = false;
	// Можно ли кешировать данный айтем
	private boolean cacheable = false;
	// Список названий переменных страницы для идентификации айтема при кешировании
	protected ArrayList<String> cacheVars = null;
	// Особый класс айтема (когда нужна нестандартная загрузка)
	private Constructor<ExecutableItemPE> specialLoaderConstructor = null;
	
	public ItemPE(ItemQuery.Type itemType, String itemName, String itemPageId, String tag, ItemRootType itemRootType,
			String itemRootGroupName, boolean isSingle, boolean isCacheable, boolean isVirtual, ArrayList<String> cacheVarNames) {
		super();
		this.itemType = itemType;
		this.itemName = itemName;
		this.itemPageId = itemPageId;
		this.tag = tag;
		this.itemRootType = itemRootType;
		this.itemRootGroupName = itemRootGroupName;
		this.quantifierSingle = isSingle;
		this.cacheable = isCacheable;
		this.virtual = isVirtual;
		this.cacheVars = cacheVarNames;
	}
	
	@SuppressWarnings("unchecked")
	public final void setSpecialLoader(String className) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		if (!StringUtils.isBlank(className)) {
			Class<ExecutableItemPE> specialLoader = (Class<ExecutableItemPE>) Class.forName(className);
			specialLoaderConstructor = specialLoader.getConstructor(ItemQuery.Type.class, String.class, String.class, String.class,
					ItemRootType.class, String.class, Boolean.TYPE, Boolean.TYPE, ArrayList.class, ExecutablePagePE.class);
		}
	}
	
	public final String getItemName() {
		return itemName;
	}

	public final String getTag() {
		return tag;
	}
	
	public final ItemQuery.Type getItemQueryType() {
		return itemType;
	}

	public final ItemRootType getItemRootType() {
		return itemRootType;
	}
	
	public final String getItemRootGroupName() {
		return itemRootGroupName;
	}

	@Override
	protected final PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		ExecutableItemPE clone = null;
		if (specialLoaderConstructor != null) {
			try {
				clone = specialLoaderConstructor.newInstance(itemType, itemName, itemPageId, tag, itemRootType,
						itemRootGroupName, quantifierSingle, cacheable, cacheVars, parentPage);
			} catch (Exception e) {
				throw new RuntimeException("Can not create special loader", e);
			}
		} else {
			clone = new ExecutableItemPE(itemType, itemName, itemPageId, tag, itemRootType,
					itemRootGroupName, quantifierSingle, cacheable, virtual, cacheVars, parentPage);			
		}
		if (container != null)
			((ExecutableItemContainer)container).addExecutableItem(clone);
		return clone;
	}

	public final String getId() {
		return itemPageId;
	}
	
	protected final boolean hasId() {
		return !StringUtils.isBlank(itemPageId);
	}
	
	public final boolean isSession() {
		return itemRootType == ItemRootType.SESSION;
	}
	
	public final boolean isPersonal() {
		return itemRootType == ItemRootType.PERSONAL;
	}
	
	public final boolean isSingle() {
		return quantifierSingle;
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

	public final String getKey() {
		return "Item '" + itemName + "'";
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
		// Есть ли айтем с заданным именем
		if (itemDesc == null) {
			results.addError(elementPath + " > " + getKey(), "there is no '" + itemName + "' item in site model");
			return false;
		}
		// Установить данные для последующей валидации (ItemDescription страничного айтема)
		results.setBufferData(itemDesc);
		return true;
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

}