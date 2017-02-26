package ecommander.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.datatypes.DataType.Type;

/**
 * Все сеттеры протектед потому что использовать их можно только из сабклассов,
 * в частности из класса, который будет читать XML файл модели данных
 * 
 * Характеристики сабайтема quantifier - одиночный или множественный сабайтем
 * persistence - если айтем принадлежит юзеру, то он может храниться как в базе,
 * так и в сеансе.
 * @author E
 */
public class ItemType extends ItemTypeContainer {
	public static enum Event {
		create, update, delete;
		public static Event get(String eventName) {
			if (StringUtils.endsWith(eventName, "create"))
				return create;
			if (StringUtils.endsWith(eventName, "update"))
				return update;
			if (StringUtils.endsWith(eventName, "delete"))
				return delete;
			return null;
		}
	}


	public static final int SERVICE_ITEM_ID = -1; 	// ID айтема, который используется как заглушка для обеспечения целостности данных таблиц БД
													// для хранения служебных и специальных значений и параметров
	
	public static final char COMMON_DELIMITER = ' ';
	public static final String ITEM_SELF = "*";

	private String typeName = StringUtils.EMPTY; // Имя айтема в XML файле на английском языке
	private int typeId; // ID типа айтема (для ускорения)
	private String caption =  StringUtils.EMPTY; // Символьное обозначение для юзера на русском языке
	private String description =  StringUtils.EMPTY; // Описание на русском
	private String key =  StringUtils.EMPTY; // Ключевые параметры, разделенные пробелом
	private String defaultPage = null; // страница по умолчанию для
	private boolean virtual;
	private boolean inline; // является ли данный айтем инлайновым (для панели управления)
	private boolean isExtendable; // Может ли пользователь расширять этот айтем
	private boolean isKeyUnique;	    // Должен ли айтем иметь уникальное имя, по которому можно было бы идентифицировать этот айтем
										// при передаче этого имени через URL
	private final boolean userDefined;	// является ли данный айтем пользовательским, т. е. был ли он создан пользователем 
										// (не присутствовал в базовой модели данных)
	private boolean hasXML = false;	// айтем содержит параметры типа XML, которые надо выводить без эскейпинга (в отличие от HTML параметров)
	
	private HashMap<Integer, ParameterDescription> parameters = null; // Параметры. Также хранится порядок следования параметров
	private LinkedHashMap<String, ParameterDescription> paramsByName = null;
	private HashMap<String, LinkedHashSet<ParameterDescription>> fulltextIndexParams = null; // Название параметра, в котором сохраняется 
						// значение этого параметра при полнотекстовом индексировании => список параметров для полнотекстового индекса
	private HashMap<Event, LinkedHashSet<ItemEventCommandFactory>> extraHandlers;	// фактори команд-обработчиков событий айтема
													//(выполнение дополнительных действий после сохранения, удаления)
	private String extendsStr = ITEM_SELF; // перечисление всех предов айтема в порядке добавления параметров, сам айтем в этом списке обозначается *
	
	public ItemType(String typeName, int typeId, String caption, String description, String key, String extendsStr, String defaultPage, boolean virtual,
			boolean isUserDefined, boolean isInline, boolean isExtendable, boolean isKeyUnique) {
		super();
		// Заменить запятую на точку для корректного использования имени айтема в атрибуте extends
		this.typeName = typeName;
		this.typeId = typeId;
		if (caption != null)
			this.caption = caption;
		if (description != null)
			this.description = description;
		if (key != null)
			this.key = key;
		this.virtual = virtual;
		this.userDefined = isUserDefined;
		this.inline = isInline;
		this.isExtendable = isExtendable;
		this.isKeyUnique = isKeyUnique;
		parameters = new HashMap<Integer, ParameterDescription>();
		paramsByName = new LinkedHashMap<String, ParameterDescription>();
		if (!StringUtils.isBlank(extendsStr)) {
			this.extendsStr = extendsStr;
			if (!this.extendsStr.contains(ITEM_SELF))
				this.extendsStr += COMMON_DELIMITER + ITEM_SELF;
		}
		if (StringUtils.isNotBlank(defaultPage))
			this.defaultPage = defaultPage;
	}
	/**
	 * Установить дополнительный обработчик сохранения айтейма
	 * @param factory
	 */
	public void addExtraHandler(Event event, ItemEventCommandFactory factory) {
		if (extraHandlers == null)
			extraHandlers = new HashMap<>(3);
		LinkedHashSet<ItemEventCommandFactory> listeners = extraHandlers.get(event);
		if (listeners == null) {
			listeners = new LinkedHashSet<>(3);
			extraHandlers.put(event, listeners);
		}
		listeners.add(factory);
	}
	/**
	 * @return
	 */
	public String getCaption() {
		return caption;
	}
	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Возвращает только один параметр
	 * 
	 * @param paramName
	 * @return
	 */
	public ParameterDescription getParameter(String paramName) {
		return paramsByName.get(paramName);
	}
	/**
	 * Получить параметр
	 * @param paramId
	 * @return
	 */
	public ParameterDescription getParameter(int paramId) {
		return parameters.get(paramId);
	}
	/**
	 * Содержит ли айтем параметр с определенным ID
	 * @param paramId
	 * @return
	 */
	public final boolean hasParameter(int paramId) {
		return parameters.containsKey(paramId);
	}
	/**
	 * @return
	 */
	public String getName() {
		return typeName;
	}
	
	public int getTypeId() {
		return typeId;
	}
	/**
	 * @param map
	 */
	public final void putParameter(ParameterDescription parameter) {
		parameters.put(parameter.getId(), parameter);
		paramsByName.put(parameter.getName(), parameter);
		// Добавление параметра в список параметров для полнотекстового поиска
		if (parameter.isFulltextSearchable()) {
			if (fulltextIndexParams == null) {
				fulltextIndexParams = new HashMap<String, LinkedHashSet<ParameterDescription>>();
			}
			LinkedHashSet<ParameterDescription> paramParams = fulltextIndexParams.get(parameter.getFulltextIndexParameter());
			if (paramParams == null) {
				paramParams = new LinkedHashSet<ParameterDescription>();
				fulltextIndexParams.put(parameter.getFulltextIndexParameter(), paramParams);
			}
			paramParams.add(parameter);
		}
		// Если параметр имеет тип XML, то установить соотвествующий флаг
		if (parameter.getType() == Type.XML)
			hasXML = true;
	}
	/**
	 * Удаление параметра
	 * @param paramId
	 */
	public final void removeParameter(int paramId) {
		ParameterDescription param = parameters.remove(paramId);
		paramsByName.remove(param.getName());
		if (fulltextIndexParams != null) {
			for (LinkedHashSet<ParameterDescription> ftParams : fulltextIndexParams.values()) {
				ftParams.remove(param);
			}
		}
	}
	/**
	 * Возвращает все параметры в виде коллекции
	 * 
	 * @return объекты Parameter
	 */
	public Collection<ParameterDescription> getParameterList() {
		return paramsByName.values();
	}
	/**
	 * Все названия параметров
	 * @return
	 */
	public Collection<String> getParameterNames() {
		return paramsByName.keySet();
	}
	/**
	 * Список названий параметров для полнотекстового поиска
	 * @return
	 */
	public Collection<String> getFulltextParams() {
		return fulltextIndexParams.keySet();
	}
	/**
	 * Вернуть список параметров, которые составляют один параметр для полнотекстового поиска
	 * @param ftParam
	 * @return
	 */
	public Collection<ParameterDescription> getFulltextParameterList(String ftParam) {
		return fulltextIndexParams.get(ftParam);
	}
	/**
	 * Возможен ли полнотекстовый поиск этого айтема
	 * @return
	 */
	public boolean isFulltextSearchable() {
		return fulltextIndexParams != null;
	}
	/**
	 * Сравнивает имена типов айтемов
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ItemType
				&& ((ItemType) obj).getName().equals(getName())) {
			return true;
		}
		return false;
	}
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "GeneralItem: " + getName();
	}
	/**
	 * Есть ли ключевой параметр
	 * 
	 * @return
	 */
	public boolean hasKey() {
		return !StringUtils.isBlank(key);
	}
	/**
	 * Получить ключевые параметры
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Получить страницу по умолчанию
	 * @return
	 */
	public String getDefaultPage() {
		return defaultPage;
	}
	/**
	 * Добавить дополнительные ключевые параметры к существующим ключевым параметрам
	 * @param keyAddOn
	 * @param isKeyUnique
	 */
	public void addKeyParameter(String keyAddOn, boolean isKeyUnique) {
		this.isKeyUnique |= isKeyUnique;
		if (key.indexOf(keyAddOn) != -1)
			return;
		if (!StringUtils.isBlank(key))
			key += COMMON_DELIMITER;
		key += keyAddOn;
	}
	/**
	 * Яаляется ли айтем виртуальным
	 * @return
	 */
	public boolean isVirtual() {
		return virtual;
	}
	/**
	 * является ли данный айтем пользовательским, т. е. был ли он создан пользователем 
	 * @return
	 */
	public boolean isUserDefined() {
		return userDefined;
	}
	/**
	 * Является ли данный айтем инлайновым (выводить ли его на странице родительского айтема в панели управления)
	 * @return
	 */
	public boolean isInline() {
		return inline;
	}
	/**
	 * Может ли пользователь расширять этот айтем (создавать потомков)
	 * @return
	 */
	public boolean isExtendable() {
		return isExtendable;
	}
	/**
	 * Должен ли айтем содержать уникальный ключ
	 * @return
	 */
	public boolean isKeyUnique() {
		return isKeyUnique;
	}
	/**
	 * Есть ли обработчик события сохранения
	 * @return
	 */
	public boolean hasExtraHandlers() {
		return extraHandlers != null;
	}
	/**
	 * Есть ли обработчики для определенного события
	 * @param event
	 * @return
	 */
	public boolean hasExtraHandlers(Event event) {
		return hasExtraHandlers() && extraHandlers.get(event) != null;
	}
	/**
	 * Содержит ли айтем XML параметры (их значения которых должны выводиться без эскейпинга)
	 * @return
	 */
	public boolean hasXML() {
		return hasXML;
	}
	/**
	 * Вернуть фактори для создания обработчиков сохранения (и других событий) айтема
	 * @return
	 */
	public void addExtraHandlersToItem(ItemType itemToAdd) {
		if (extraHandlers != null) {
			for (Event event: extraHandlers.keySet()) {
				for (ItemEventCommandFactory factory: extraHandlers.get(event)) {
					itemToAdd.addExtraHandler(event, factory);
				}
			}
		}
	}

	/**
	 * Вернуть строку extends (перечисление всех предов айтема в порядке добавления параметров, сам айтем в этом списке обозначается *)
	 * @return
	 */
	public String getExtendsString() {
		return extendsStr;
	}
	/**
	 * Есть ли у айтема предшественники по иерархии наследования
	 * @return
	 */
	public boolean hasPredecessors() {
		return !extendsStr.equals(ITEM_SELF);
	}

	/**
	 * Есть ли у айтема страница по умолчанию
	 * @return
	 */
	public boolean hasDefaultPage() {
		return defaultPage != null;
	}
}