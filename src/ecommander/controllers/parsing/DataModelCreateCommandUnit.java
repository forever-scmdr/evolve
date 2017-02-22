package ecommander.controllers.parsing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.codemodel.JClassAlreadyExistsException;

import ecommander.application.extra.ItemEventCommandFactory;
import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.common.exceptions.EcommanderException;
import ecommander.common.exceptions.ValidationException;
import ecommander.controllers.AppContext;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeContainer;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.model.ParameterDescription.Quantifier;
import ecommander.pages.elements.ValidationResults;
import ecommander.persistence.TransactionException;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;
import ecommander.users.User;
/**
 * Юнит для чтения XML модели данных и сохранения дескрипшенов айтемов

 * Класс, который читает XML файл с моделью данных и сохраняет эту модель в БД
 * Файл модели данных
 * 
<?xml version="1.0" encoding="utf-8"?>
<items>


// 		***********************   ОБЩИЕ ПОНЯТИЯ   ***********************


<item name="index_text" caption="Главная страница" description="">
	<parameter name="text" type="text" quantifier="single" caption="Текст" description=""/>
	<parameter name="picture" type="picture" quantifier="multiple" caption="Изображение" description=""/>
</item>

<item name="production" caption="Продукция" description="">
	<subitem name="section" quantifier="multiple"/>
</item>

// Типы параметров
// text, short-text, tiny-text, plain-text -	длинные строки, отличаются только тем, что для них выводятся разные html редакторы в CMS
//												plain-text выводится как простой поле ввода <textarea> без редактора. Хранятся в айтеме
//												в виде XML с эскейпингом
// string, byte, integer, long, double -	соответствуют примитивным типам данных java. Строки хранятся в XML айтема с эскейпингом
// date - 	дата, хранится в виде long. Значение в CMS может задаваться с учетом заданного формата (параметра format)
// file, picture -	файлы. Хранится название файла в виде строки. При загрузке файла через форму хранится объект библиотеки fileupload
// filter -	по сути также длинная строка. Однако имеет особый XML-формат и хранит определение фильтра, которое создается пользователем
//			и потом используется при фильтрации айтемов. Для редактирования параметров этого типа используестя специальный редактор.
// xml -	так же как и filter хранит значение в виде XML. Однако значение такого типа может содержать XML любого вида, 
// 			без специального формата, главное чтобы оно было валидным. Значение этого параметра не подвергается XML эскейпингу.
// associated -	объекты long, которые представляют собой ID айтемов, связанный по смыслу с айтемом, содержашим параметр данного типа.
				Например, это могут быть товары, сопутствующие некоторому товару.

// Принадлежность (доступность для редактирования) айтемов.
//
// Айтемы могуть быть либо общими либо персональными.
// У общих айтемов нет определенного владельца (USER_ID = 0), но есть определенная группа (USER_GROUP != 0).
// У персональных айтемов есть как владелец (USER_ID != 0), так и группа (USER_GROUP != 0)
//
// Если сабайтем помечен как owner="group", значит все пользователи группы, указанной в атрибуте owner-group, имеют к нему доступ.
// Все пользователи разделяют одни и те же такие айтемы, изменения, сделанные одним пользователем,
// доступны всем другим пользователям.
//
// Если сабайтем помечен как owner="personal", значит каждый пользователь группы owner-group имеет свои персональные
// айтемы. Другие пользователи этой группы имеют также свои собственные (другие) айтемы, 
// и не имеют доступа к айтемам других пользователей этой группы.
<item name="product" caption="Продукт">
	<subitem name="desc_wiki" quantifier="single" owner="group" owner-group="editors"/> // Описание продукта в формате википедии
	<subitem name="comment" quantifier="multiple" owner="personal" owner-group="users"/> // Комментарий к товару
</item>

<item 
	name="news_item_lenta" 
	caption="Новость в новостной ленте" 
	description="" 
	key="header date">
	
	<parameter name="header" type="string" quantifier="single" caption="Заголовок новости" description=""/>
	<parameter name="date" type="date" quantifier="single" caption="Дата" description=""/>
	<parameter name="short_description" type="text" quantifier="single" caption="Краткое описание новости" description=""/>
</item>

// extends - Наследование. В extends через пробел перечисляются все предки айтема
// Добавление параметров предков айтема происходит в соответствии с порядком следования названий преков в вписке extends.
// По умолчанию считается, что параметры самого айтема добавляются после параметров всех предшественников. Однако, если
// параметры самого айтема должны идти, к примеру, в начале, то этот факт обозначатеся внесением символа * в соответствующее
// место в списке наследования (в начало в данном случае). Символ * обозначает сам айтем в списке наследования.
//
// name-old - если надо переименовать айтем, здесь указывается старое название айтема 
// (этот атрибут удаляется автоматически из файла после первого разбора)
// key-unique="true" - значит для каждого такого айтема надо задавать уникальный ключ, который можно передавать через URL вместо ID
// (в общем случае - транслитерация значения ключа)
<item 
	name="catalog_item" 
	name-old="catalog_itm"
	caption="Устройство" 
	extends="* described searchable" 
	key="device_name"
	key-unique="true">
	
	<parameter name="device_name" type="string" quantifier="single" caption="Название устройства"/>
	<parameter name="device_picture" type="picture" quantifier="single" caption="Изображение устройства"/>
	<parameter name="device_description" type="text" quantifier="single" caption="Описание устройства"/>
	<subitem name="catalog_menu_subitem" quantifier="multiple"/>
</item>

// virtual="true" примененное к айетму означает, что не нужно создавать таблицу для данного айтема
<item name="cart" virtual="true">
	<subitem name="bought" quantifier="multiple"/>
</item>

// name-old - если надо переименовать параметр, здесь указывается старое название параметра 
// type-old - если надо поменять тип параметра, здесь указывается старый тип параметра 
// (оба этих атрибута удаляются автоматически из файла после первого разбора)
<item name="bought" virtual="true">
	<parameter name="quantity" name-old="count" type="integer" type-old="double" format="##" quantifier="single"/>
	<subitem name="device" quantifier="single"/>
</item>

// virtual="true" примененное к сабайтему означает, что в системе управления этот сабайтем может создаваться только в виде ссылки 
// (прикреплением)
<item name="main">
	<parameter .../>
	<subitem name="news_item" quantifier="multiple" virtual="true"/>
</item>

// Корневые айтемы
// Каждой группе пользователей может соответствовать свой корневой айтем.
// Корневой айтем определяет входную точку дерева айтемов начиная с которой пользователь группы, укзазанной в 
// атрибуте group, может создавать и редактировать айтемы (персональные либо групповые, см. описание <subitem>).
// Корневой айтем является родительским для персональных айтемов пользователей группы.
//
// В случаях, когда родительским для айтемов пользователя определенной группы является корень не его группы 
// (эти айтемы являются сабайтемами айтемов других групп, в которых owner-group указывает на текущую группу),
// система управления загружает дерево айтемов, потенциальных и реальных предшественников айтемов этой группы, которые
// однако сами принадлежат другим группам. При этом загружаются только ключи айтемов, но не их параметры, и из всех
// операций с ними доступна только операция сделать текущим (без генерации формы для параметров).
//
// Какие именно айтемы надо брать для этих целей система определяет автоматически на базе иерархической 
// структуры айтемов и сабайтемов всех корней.
//
<root group="common">
	<subitem name="products" quantifier="single"/>
</root>

<item name="products">
	<subitem name="history" quantifier="single" owner="personal" owner-group="dealers"/>
</item>

// item-name - название айтема, group - название группы, в которой он создан
<root group="dealers">
	<reference item-name="products" group="common"/>
</root>



// 		***********************   ДОМЕНЫ   ***********************


<parameter name="type" type="string" domain="types" quantifier="single"/>


// 		***********************   Улучшенный вывод в админской части   *********************** 


<item name="gallery" caption="Галерея картинок">
	<subitem name="picture_pair" quantifier="multiple"/>
</item>
// inline="true" означает, что этот сабайтем надо выводить для редактирования на той же странице, что и родительский айтем
// т. е. для его редактирования не обязательно заходить в айтем picture_pair
<item name="picture_pair" caption="Пара картинок" inline="true">

// extendable="true" значит, что этот айтем может быть расширен пользователем, т.е. пользователь может создать дочерний айтем в этой иерархии
<item name="product" caption="Продукт" extendable="true"/>

<item name="picture_pair" caption="Пара картинок" key="name">
	<parameter name="name" type="string" caption="Название" description="для наглядности"/>
	<parameter name="small" caption="Уменьшенное изображение" type="picture"/>
	// hidden="true" означает, что при выводе параметров для их редактирования, этот параметр по умолчанию скрыт и чтобы его показать,
	// надо нажать на соответствующую ссылку
	<parameter name="big" caption="Большое изображение" type="picture" hidden="true"/>
</item>


// ***********************   Полнотекстовая индексация   ***********************


// text-index - способ индексации (fulltext - производится полный разбор текста, filter - значение сохраняется целиком без разбора)
// text-index-parameter - название параметра, который должен содержать текст. Один полнотекстовый параметр может объединять несколько
						  параметров айтема. В этом случае значение этого параметра оъединяется из нескольких значений параметров айтема
// text-index-boost - увеличение веса определенного параметра при поиске по нескольким параметрам при ранжировании
// text-index-parser - парсер для разбора значения параметра (html, doc, pdf)

<item name="product" caption="Продукт">
	<parameter name="name" type="string" caption="Название" text-index="fulltext" text-index-boost="2"/>
	<parameter name="short" type="text" caption="Краткое описание новости" text-index="fulltext" text-index-parser="html"/>
	<parameter name="keywords" type="string" caption="Ключевый слова" text-index="fulltext" text-index-parameter="name"/>
	<parameter name="code" type="string" caption="Артикул" text-index="filter"/>
	<parameter name="size" type="string" caption="Размер" text-index="filter" text-index-parameter="name"/>
	<parameter name="documentation" type="file" caption="Документация" text-index="fulltext" text-index-parser="pdf" />
</item>


// ***********************   Дополнительная обработка   ***********************


// extra-handler	содержит название класса, который производит дополнительные действия с айтемами
// 					в определенный момент, в данном случае, после сохранения. Класс расширяет PersistenceCommandUnit
//					и представлен фабрикой, которая его создает (фабрика реализует ItemEventCommandFactory)
<item name="product" caption="Продукт" extra-handler="ecommander.persistence.commandunits.extra.ResizeImages$Factory">
....
</item>

</items>
 * TODO <fix> сделать удаление всех айтемов рута при удалении рута
 * 
 * @author EEEE
 * 
 */
public class DataModelCreateCommandUnit extends DBPersistenceCommandUnit {

	private class CreateHandler extends DefaultHandler {

		private Exception exception;
		private String itemName;
		private String rootGroupName;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (exception != null)
				return;
			try {
				if (ITEM_ELEMENT.equalsIgnoreCase(qName)) {
					itemName = readItem(attributes);
				} else if (SUBITEM_ELEMENT.equalsIgnoreCase(qName)) {
					readSubitem(attributes, itemName, rootGroupName);
				} else if (PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
					readParameter(attributes, itemName);
				} else if (ROOT_ELEMENT.equalsIgnoreCase(qName)) {
					rootGroupName = readRoot(attributes);
					itemName = RootItemType.createRootName(rootGroupName);
				}
			} catch (Exception e) {
				exception = e;
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (exception != null)
				return;
		}
	}
	
	private static class ParamToDelete {
		private final int paramId;
		private final String paramName;
		private final String itemName;
		private ParamToDelete(int paramId, String paramName, String itemName) {
			this.paramId = paramId;
			this.paramName = paramName;
			this.itemName = itemName;
		}
	}
	/**
	 * Элементы
	 */
	public static final String ITEM_ELEMENT = "item";
	public static final String ROOT_ELEMENT = "root";
	public static final String PARAMETER_ELEMENT = "parameter";
	public static final String SUBITEM_ELEMENT = "subitem";
	/**
	 * Атрибуты
	 */
	public static final String NAME_ATTRIBUTE = "name";
	public static final String NAME_OLD_ATTRIBUTE = "name-old";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String TYPE_OLD_ATTRIBUTE = "type-old";
	public static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	public static final String CAPTION_ATTRIBUTE = "caption";
	public static final String DESCRIPTION_ATTRIBUTE = "description";
	public static final String DOMAIN_ATTRIBUTE = "domain";
	public static final String FORMAT_ATTRIBUTE = "format";
	public static final String GROUP_ATTRIBUTE = "group";
	public static final String DATAMODEL_ELEMENT = "data-model";
	public static final String KEY_ATTRIBUTE = "key";
	public static final String PROPERTY_ATTRIBUTE = "property";
	public static final String EXTENDS_ATTRIBUTE = "extends";
	public static final String PERSISTENCE_ATTRIBUTE = "persistence";
	public static final String VIRTUAL_ATTRIBUTE = "virtual"; // айтем либо параметр является виртуальным, т.е. не хранится в БД и используется только для спецдействий
	public static final String OWNER_ATTRIBUTE = "owner"; // тип владения этим айтемом (персональный или групповой)
	public static final String OWNER_GROUP_ATTRIBUTE = "owner-group"; // группа пользователей, которая может владеть этим айтемом
	public static final String USER_DEF_ATTRIBUTE = "user-def"; // Айтем определен пользователем
	public static final String INLINE_ATTRIBUTE = "inline"; // сабайтем надо выводить для редактирования на той же странице, что и родительский айтем
	public static final String EXTENDABLE_ATTRIBUTE = "extendable";
	public static final String KEY_UNIQUE_ATTRIBUTE = "key-unique"; // должны ли айтемы этого типа содержать уникальный ключ-название (для передачи через URL)
	public static final String HIDDEN_ATTRIBUTE = "hidden"; // параметр по умолчанию скрыт (hidden="true")

	public static final String TEXT_INDEX = "text-index"; // тип полнотекстового индекса (если он нужен)
	public static final String TEXT_INDEX_PARAMETER = "text-index-parameter"; // название параметра для полнотекстового индекса
	public static final String TEXT_INDEX_BOOST = "text-index-boost"; // увеличение веса параметра полнотекстового индекса
	public static final String TEXT_INDEX_PARSER = "text-index-parser"; // парсер для значения параметра
	
	public static final String EXTRA_HANDLER = "extra-handler"; // дополнительные действия после сохранения, удаления
	
	/**
	 * Значения
	 */
	public static final String SINGLE_VALUE = "single";
	public static final String MULTIPLE_VALUE = "multiple";
	
	public static final String PERSONAL_VALUE = "personal";
	
	public static final String TRUE_VALUE = "true";
	
	public static final long ROOT_PARENT_ID = 0;
	
	/**
	 * Поля класса
	 */
	private ArrayList<String[]> extensionParentChildPairs = new ArrayList<String[]>();
	private HashMap<String, Integer> itemIds = new HashMap<String, Integer>(); // Название параметра => ID параметра
	// ID айтема => (название параметра => ID параметра)
	private HashMap<Integer, HashMap<String, Integer>> paramIds = new HashMap<Integer, HashMap<String,Integer>>();
	private HashMap<Integer, ArrayList<ParameterDescription>> params = new HashMap<Integer, ArrayList<ParameterDescription>>();
	private HashSet<Integer> itemsToDelete = new HashSet<Integer>();
	private HashMap<Integer, ParamToDelete> paramsToDelete = new HashMap<Integer, ParamToDelete>();
	private HashSet<Integer> paramsToDeleteFromIndex = new HashSet<Integer>();
	private boolean containsOldTags = false; // были ли изменения имен или типов параметров и айтемов
	private boolean dbChanged = false; // были ли изменения в БД
	
	public void execute() throws Exception {
		// Очистить реестр айтемов
		ItemTypeRegistry.clearRegistry();
		
		// Загрузить ID всех параметров и айтемов
		loadIds();
		
		// Создать служебные параметры и айтемы, если они еще не созданы
		createServiceItemsAndParameters();
		
		// Создать парсер
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		CreateHandler handler = new CreateHandler();

		// Прасить основную модель model.xml (получить базовые определения айтемов и параметров)
		ArrayList<File> modelFiles = findModelFiles(new File(AppContext.getMainModelPath()), null);
		for (File modelFile : modelFiles) {
			parser.parse(modelFile, handler);			
		}
		
		// Парсить модели пользовательских айтемов
		if (Files.exists(Paths.get(AppContext.getUserModelPath())))
			parser.parse(AppContext.getUserModelPath(), handler);
		
		// Выбросить эксэпшен, если он случился
		if (handler.exception != null) {
			ValidationResults results = new ValidationResults();
			results.setException(handler.exception);
			throw new ValidationException("primary validation error", results);
		}
		
		// Создать иерархию айтемов
		ItemTypeRegistry.createHierarchy(extensionParentChildPairs, false);
		
		// Распределить параметры и сабайтемы по айтемам
		HashSet<String> processed = new HashSet<String>(20);
		for (String itemName : ItemTypeRegistry.getItemNames()) {
			addParametersAndSubitems(ItemTypeRegistry.getItemType(itemName), processed);
		}
//			TypeHierarchy root = ItemTypeRegistry.getHierarchies(ItemTypeRegistry.getItemNames());
//			addParametersAndSubitems(root);
		
		// Удалить все ненужные записи в таблицах ID и в таблицах индексов по параметрам (делается автоматически через foreign key)
		mergeModel();
		
		// Удалить атрибуты name-old и type-old
		if (containsOldTags) {
			removeOldTags(AppContext.getMainModelPath());
			if (Files.exists(Paths.get(AppContext.getUserModelPath())))
				removeOldTags(AppContext.getUserModelPath());
		}
		
		// Создать java класс с текстовыми константами для всех айтемов
		createJavaConstants();
		
		// Если было исключение - выбросить его
		if(handler.exception != null)
			throw handler.exception;
	}
	/**
	 * Читает корневой айтем
	 * Сохраняет атрибуты коренвого атйема в столбцы таблицы обычного атйема:
	 * KEY_PARAMETER - group
	 * CAPTION - persistence
	 * @param attributes
	 * @return - название группы пользователей этого корня
	 * @throws Exception
	 */
	protected String readRoot(Attributes attributes) throws Exception {
		Statement stmt = null;
		try {
			String rootGroup = attributes.getValue(GROUP_ATTRIBUTE);
			if (rootGroup == null) rootGroup = User.USER_DEFAULT_GROUP;
			String rootName = RootItemType.createRootName(rootGroup);
			// Загрузить ID корней из БД
			stmt = getTransactionContext().getConnection().createStatement();
			String sql 
					= "SELECT " + DBConstants.Item.ID + " FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.REF_ID + "=0 AND " + DBConstants.Item.KEY + " = '" + rootName + "'";			
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			long rootId = -1;
			if (rs.next())
				rootId = rs.getLong(1);
			// Сохранить рут в таблице конкретных айтемов, в случае если такого рута нет
			if (rootId == -1) {
				sql
					= "INSERT INTO "
					+ DBConstants.Item.TABLE
					+ " ("
					+ DBConstants.Item.INDEX_WEIGHT + ", "
					+ DBConstants.Item.DIRECT_PARENT_ID + ", "
					+ DBConstants.Item.REF_ID + ", "
					+ DBConstants.Item.OWNER_GROUP_ID + ", "
					+ DBConstants.Item.OWNER_USER_ID + ", "
					+ DBConstants.Item.TYPE_ID + ", "
					+ DBConstants.Item.PRED_ID_PATH + ", "
					+ DBConstants.Item.KEY 
					+ ") SELECT 0, " + ROOT_PARENT_ID + ", 0, 0, 0, 0, '', '" + rootName + "' "
					+ " FROM DUAL WHERE NOT EXISTS (SELECT * FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.REF_ID + "=0 AND " + DBConstants.Item.KEY + "='" + rootName + "')";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
				rs.next();
				rootId = rs.getLong(1);
				rs.close();
				// Сохранить рут в таблице предков айтемов
				sql 
					= "INSERT INTO " + DBConstants.ItemParent.TABLE + " ("
					+ DBConstants.ItemParent.ITEM_ID + ", "
					+ DBConstants.ItemParent.REF_ID + ", "
					+ DBConstants.ItemParent.PARENT_ID + ", "
					+ DBConstants.ItemParent.ITEM_TYPE + ", "
					+ DBConstants.ItemParent.PARENT_LEVEL 
					+ ") VALUES (" 
					+ rootId + ", " + rootId + ", " + rootId + ", " + ItemType.SERVICE_ITEM_ID + ", 0)";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
			// Добавить рут в реестр айтемов
			ItemTypeRegistry.addRootDescription(new RootItemType(rootGroup, rootId));
			return rootGroup;
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}
	/**
	 * Читает айтем
	 * @param itemNode
	 * @param conn
	 * @throws Exception
	 */
	private String readItem(Attributes attributes) throws Exception {
		String name = Strings.createXmlElementName(attributes.getValue(NAME_ATTRIBUTE));
		String nameOld = attributes.getValue(NAME_OLD_ATTRIBUTE);
		boolean nameUpdate = !StringUtils.isBlank(nameOld);
		String key = attributes.getValue(KEY_ATTRIBUTE);
		if (key == null) 
			key = Strings.EMPTY;
		String caption = attributes.getValue(CAPTION_ATTRIBUTE);
		String description = attributes.getValue(DESCRIPTION_ATTRIBUTE);
		String exts = attributes.getValue(EXTENDS_ATTRIBUTE);
		boolean virtual = Boolean.parseBoolean(attributes.getValue(VIRTUAL_ATTRIBUTE));
		boolean userDefined = Boolean.parseBoolean(attributes.getValue(USER_DEF_ATTRIBUTE));
		boolean isInline = Boolean.parseBoolean(attributes.getValue(INLINE_ATTRIBUTE));
		boolean isExt = Boolean.parseBoolean(attributes.getValue(EXTENDABLE_ATTRIBUTE));
		boolean isKeyUnique = Boolean.parseBoolean(attributes.getValue(KEY_UNIQUE_ATTRIBUTE));
		String extraHandler = attributes.getValue(EXTRA_HANDLER);
		Integer itemId = null;
		// Если надо обновить название айтема
		if (nameUpdate) {
			itemId = itemIds.get(nameOld);
			if (itemId == null) {
				ValidationResults results = new ValidationResults();
				results.addError("Item '" + name + "'", "there was no item named '" + nameOld + "'");
				throw new ValidationException("Item update error", results);
			}
			// Обновить название айтема в таблице ID айтемов
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql 
					= "UPDATE " + DBConstants.ItemIds.TABLE 
					+ " SET " + DBConstants.ItemIds.ITEM_NAME + "='" + name 
					+ "' WHERE " + DBConstants.ItemIds.ITEM_ID + "=" + itemId;
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			} finally {
				stmt.close();
			}
			// Заменить название айтема в списке ID айтемов
			itemIds.remove(nameOld);
			itemIds.put(name, itemId);
			containsOldTags = true;
		} else {
			itemId = itemIds.get(name);
		}
		// Если появился новый айтем, которого не было раньше - получить ID для него
		if (itemId == null) {
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql = "INSERT " + DBConstants.ItemIds.TABLE + " (" + DBConstants.ItemIds.ITEM_NAME + ") VALUES ('" + name + "')";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet keys = stmt.getGeneratedKeys();
				keys.next();
				itemId = keys.getInt(1);
				itemIds.put(name, itemId);
				paramIds.put(itemId, new HashMap<String, Integer>());
				dbChanged = true;
			} finally {
				stmt.close();
			}
		}
		// Временно сохранить сведения о иерархии наследования
		if (!StringUtils.isBlank(exts)) {
			String[] parents = StringUtils.split(exts, ItemType.COMMON_DELIMITER + ItemType.ITEM_SELF);
			for (String parent : parents) {
				String[] pair = {parent, name};
				extensionParentChildPairs.add(pair);
			}
		}
		// Добавить описание айтема в реестр
		ItemType item = new ItemType(name, itemId, caption, description, key, exts, virtual, userDefined, isInline, isExt, isKeyUnique);
		ItemTypeRegistry.addItemDescription(item);
		// Добавить обработчик сохранения, если он есть
		if (!StringUtils.isBlank(extraHandler)) {
			try {
				ItemEventCommandFactory factory = (ItemEventCommandFactory) Class.forName(extraHandler).getConstructor().newInstance();
				item.addExtraHandler(factory);
			} catch (Exception e) {
				throw new EcommanderException(e);
			}
		}
		// Пометить этот айтем для неудаления
		itemsToDelete.remove(itemId);
		return name;
	}
	/**
	 * Читает параметры
	 * Сначала параметры добавляются во временную структуру, 
	 * чтобы была возможность добавлять параметры во все потомки айтема сразу,
	 * а для этого надо сначала создать иерархию айтемов
	 * @param attributes
	 * @param itemName
	 * @throws Exception
	 */
	protected void readParameter(Attributes attributes, String itemName) throws Exception {
		String name = attributes.getValue(NAME_ATTRIBUTE);
		String nameOld = attributes.getValue(NAME_OLD_ATTRIBUTE);
		boolean nameUpdate = !StringUtils.isBlank(nameOld);
		String caption = attributes.getValue(CAPTION_ATTRIBUTE);
		String description = attributes.getValue(DESCRIPTION_ATTRIBUTE);
		String domainName = attributes.getValue(DOMAIN_ATTRIBUTE);
		String quantifierStr = attributes.getValue(QUANTIFIER_ATTRIBUTE);
		Quantifier quantifier = Quantifier.single;
		if (!StringUtils.isEmpty(quantifierStr)) 
			quantifier = Quantifier.valueOf(Quantifier.class, quantifierStr);
		String dataTypeName = attributes.getValue(TYPE_ATTRIBUTE);
		String dataTypeNameOld = attributes.getValue(TYPE_OLD_ATTRIBUTE);
		boolean typeUpdate = !StringUtils.isBlank(dataTypeNameOld);
		String format = attributes.getValue(FORMAT_ATTRIBUTE);
		boolean isHidden = Boolean.valueOf(attributes.getValue(HIDDEN_ATTRIBUTE));
		boolean isVirtual = Boolean.parseBoolean(attributes.getValue(VIRTUAL_ATTRIBUTE));
		String textIndexStr = attributes.getValue(TEXT_INDEX);
		ParameterDescription.TextIndex textIndex = ParameterDescription.TextIndex.none;
		String textIndexParameter = attributes.getValue(TEXT_INDEX_PARAMETER);
		String textIndexParser = attributes.getValue(TEXT_INDEX_PARSER);
		String textIndexBoostStr = attributes.getValue(TEXT_INDEX_BOOST);
		float textIndexBoost = -1f;
		if (!StringUtils.isBlank(textIndexStr)) {
			try {
				textIndex = ParameterDescription.TextIndex.valueOf(ParameterDescription.TextIndex.class, textIndexStr);
				if (!StringUtils.isBlank(textIndexBoostStr))
					textIndexBoost = Float.parseFloat(textIndexBoostStr);
			} catch (Exception e) {
				ValidationResults results = new ValidationResults();
				results.addError("Item '" + itemName + "'", "fulltext search parameters provided are not correct");
				throw new ValidationException("Fulltext search setup incorrect", results);
			}
		}
		
		int itemId = itemIds.get(itemName);
		Integer paramId = null;
		// Если надо обновить название параметра
		if (nameUpdate) {
			paramId = paramIds.get(itemId).get(nameOld);
			if (paramId == null) {
				ValidationResults results = new ValidationResults();
				results.addError("Item '" + itemName + "'", "there was no parameter named '" + nameOld + "' in this item");
				throw new ValidationException("Item update error", results);
			}
			// Обновить название параметра в таблице ID параметров
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql 
					= "UPDATE " + DBConstants.ParamIds.TABLE 
					+ " SET " + DBConstants.ParamIds.PARAM_NAME + "='" + name 
					+ "' WHERE " + DBConstants.ParamIds.PARAM_ID + "=" + paramId;
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			} finally {
				stmt.close();
			}
			// Заменить название параметра в списке ID параметров
			paramIds.get(itemId).remove(nameOld);
			paramIds.get(itemId).put(name, paramId);
			containsOldTags = true;
		} else {
			paramId = paramIds.get(itemId).get(name);
		}
		// Если появился новый параметр, которого не было раньше - получить ID для него
		if (paramId == null) {
			Statement stmt = getTransactionContext().getConnection().createStatement();
			try {
				String sql 
					= "INSERT " + DBConstants.ParamIds.TABLE + " (" 
					+ DBConstants.ParamIds.ITEM_ID + ", " + DBConstants.ParamIds.PARAM_NAME 
					+ ") VALUES (" + itemId + ", '" + name + "')";
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				ResultSet keys = stmt.getGeneratedKeys();
				keys.next();
				paramId = keys.getInt(1);
				paramIds.get(itemId).put(name, paramId);
				dbChanged = true;
			} finally {
				stmt.close();
			}
		} else {
			// Пометить этот айтем для неудаления
			if (typeUpdate) {
				containsOldTags = true;
				paramsToDeleteFromIndex.add(paramId);
			}
			paramsToDelete.remove(paramId);
		}
		ParameterDescription param = new ParameterDescription(name, paramId, dataTypeName, quantifier, itemId, domainName, caption,
				description, format, isVirtual, isHidden);
		if (textIndex != ParameterDescription.TextIndex.none)
			param.setFulltextSearch(textIndex, textIndexParameter, textIndexBoost, textIndexParser);
		// Добавление в реестр невозможно, т.к. неизвестны еще все потомки данного айтема по иерархии,
		// поэтому сначала добавление в специальную структуру
		ArrayList<ParameterDescription> itemParams = params.get(itemId);
		if (itemParams == null) {
			itemParams = new ArrayList<ParameterDescription>();
			params.put(itemId, itemParams);
		}
		itemParams.add(param);
	}
	/**
	 * Распределение параметров и сабайтемов по айтемам с учетом их иерархии
	 * @param item
	 * @param processedItems
	 */
	private void addParametersAndSubitems(ItemType item, HashSet<String> processedItems) {
		if (processedItems.contains(item.getName()))
			return;
		String[] exts = StringUtils.split(item.getExtendsString(), ItemType.COMMON_DELIMITER);
		for (String ext : exts) {
			if (ext.equals(ItemType.ITEM_SELF)) {
				ArrayList<ParameterDescription> itemParams = params.get(item.getTypeId());
				// Добавление всех параметров в сам айтем
				if (itemParams != null) {
					for (ParameterDescription param : itemParams) {
						item.putParameter(param);
					}
				}
			} else {
				// Предшественник
				ItemType predecessor = ItemTypeRegistry.getItemType(ext);
				addParametersAndSubitems(predecessor, processedItems);
				// Добавление всех параметров предшественника
				for (ParameterDescription param : predecessor.getParameterList()) {
					item.putParameter(param);
				}
				// Добавление ключевого параметра
				if (predecessor.hasKey()) {
					item.addKeyParameter(predecessor.getKey(), predecessor.isKeyUnique());
				}
				// Добавление сабайтемов
				item.addAllSubitems(predecessor);
				// Установить extra-handler
				if (predecessor.hasExtraHandlers()) {
					for (ItemEventCommandFactory factory : predecessor.getExtraHandlers()) {
						item.addExtraHandler(factory);
					}
				}
			}
		}
		// Айтем обработан - добавить в список обработанных
		processedItems.add(item.getName());
	}
	/**
	 * Читает сабайтемы
	 * @param subitemNode
	 * @param conn
	 * @param itemName
	 * @throws Exception
	 */
	protected void readSubitem(Attributes attributes, String itemName, String rootGroup) throws Exception {
		// По очереди все сабайтемы
		String subitemName = attributes.getValue(NAME_ATTRIBUTE);
		if (subitemName == null) subitemName = Strings.EMPTY;
		String quantifierStr = attributes.getValue(QUANTIFIER_ATTRIBUTE);
		if (quantifierStr == null) quantifierStr = Strings.EMPTY;
		boolean isSingle = !quantifierStr.equalsIgnoreCase(MULTIPLE_VALUE);
		boolean isVitrual = Boolean.valueOf(attributes.getValue(VIRTUAL_ATTRIBUTE));
		boolean isPersonal = PERSONAL_VALUE.equalsIgnoreCase(attributes.getValue(OWNER_ATTRIBUTE));
		String ownerGroup = attributes.getValue(OWNER_GROUP_ATTRIBUTE);
		// Не учитывать пренадлежность группе, которой атйем и так принадлежит
		if (ownerGroup != null && ownerGroup.equalsIgnoreCase(rootGroup))
			ownerGroup = null;
		ItemTypeContainer desc = ItemTypeRegistry.getItemTypeContainer(itemName);
		desc.addOwnSubitem(subitemName, isSingle, isVitrual, isPersonal, ownerGroup);
	}
	/**
	 * Очищает модель данных от удаленных айтемов и параметров.
	 * Изменяет старые параметры в соответствии с новой моделью (при перемещении параметров из одного атйема в другой)
	 * @throws SQLException 
	 */
	protected void mergeModel() throws TransactionException, SQLException {
		// Из удаляемых айтемов и параметров удаляются служебные
		itemsToDelete.remove(ParameterDescription.USER.getOwnerItemId());
		paramsToDelete.remove(ParameterDescription.USER.getId());
		paramsToDelete.remove(ParameterDescription.GROUP.getId());
		
		Statement stmt = getTransactionContext().getConnection().createStatement();
		try {
			// ********************** Очистить таблицы ******************************
			String sql;
			// Удаление из таблицы ID айтемов
			if (itemsToDelete.size() > 0) {
				sql = "DELETE FROM " + DBConstants.ItemIds.TABLE + " WHERE " + DBConstants.ItemIds.ITEM_ID + " IN " + createIn(itemsToDelete);
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
			// Удаление параметров, если нужно удалять, 
			// изменение ID параметров, если параметр переместился в родительский класс
			if (paramsToDelete.size() > 0) {
				for (ParamToDelete pd : paramsToDelete.values()) {
					ParameterDescription newParamDesc = null; // новый ID параметра, на который надо поменять старый ID в таблицах индексов
					Set<String> predNames = ItemTypeRegistry.getItemPredecessors(pd.itemName);
					for (String predName : predNames) {
						newParamDesc = ItemTypeRegistry.getItemType(predName).getParameter(pd.paramName);
						if (newParamDesc != null)
							break;
					}
					// Поменять ID параметра в таблицах-индексах
					if (newParamDesc != null) {
						String table = DataTypeMapper.getTableName(newParamDesc.getType());
						sql = "UPDATE " + table + " SET " + DBConstants.ItemIndexes.ITEM_PARAM + "=" + newParamDesc.getId() + " WHERE "
								+ DBConstants.ItemIndexes.ITEM_PARAM + "=" + pd.paramId;
						ServerLogger.debug(sql);
						stmt.executeUpdate(sql);
					} else {
						paramsToDeleteFromIndex.add(pd.paramId);
					}
					// Удалить параметр из таблицы ID параметров
					sql = "DELETE FROM " + DBConstants.ParamIds.TABLE + " WHERE " + DBConstants.ParamIds.PARAM_ID + "=" + pd.paramId;
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
					dbChanged = true;
				}
			}
			// Удаление из таблиц индексов параметров
			if (paramsToDeleteFromIndex.size() > 0) {
				String[] tables 
					= {DBConstants.ItemIndexes.INT_TABLE_NAME, DBConstants.ItemIndexes.DOUBLE_TABLE_NAME, DBConstants.ItemIndexes.STRING_TABLE_NAME};
				for (String table : tables) {
					sql = "DELETE FROM " + table + " WHERE " + DBConstants.ItemIndexes.ITEM_PARAM + " IN " + createIn(paramsToDeleteFromIndex);
					ServerLogger.debug(sql);
					stmt.executeUpdate(sql);
				}
				dbChanged = true;
			}
			// Удаление из главной таблицы айтемов
			if (itemsToDelete.size() > 0) {
				sql = "DELETE FROM " + DBConstants.Item.TABLE + " WHERE " + DBConstants.Item.TYPE_ID + " IN " + createIn(itemsToDelete);
				ServerLogger.debug(sql);
				stmt.executeUpdate(sql);
				dbChanged = true;
			}
		} finally {
			stmt.close();
		}
		//clearSql = "SET FOREIGN_KEY_CHECKS=0";
		//clearSql = "SET FOREIGN_KEY_CHECKS=1";
	}
	
	private void loadIds() throws SQLException {
		Statement stmt = getTransactionContext().getConnection().createStatement();
		String selectItemIds = "SELECT * FROM " + DBConstants.ItemIds.TABLE;
		ServerLogger.debug(selectItemIds);
		ResultSet rs = stmt.executeQuery(selectItemIds);
		HashMap<Integer, String> itemNames = new HashMap<Integer, String>();
		while (rs.next()) {
			int itemId = rs.getInt(DBConstants.ItemIds.ITEM_ID);
			String itemName = rs.getString(DBConstants.ItemIds.ITEM_NAME);
			itemNames.put(itemId, itemName);
			itemIds.put(itemName, itemId);
			itemsToDelete.add(itemId);
			paramIds.put(itemId, new HashMap<String, Integer>());
		}
		rs.close();
		// удаление ненужных но почему-то присутствующих параметров
		String deleteParamIds 
			= "DELETE FROM " + DBConstants.ParamIds.TABLE + " WHERE " + DBConstants.ParamIds.ITEM_ID 
			+ " NOT IN (SELECT " + DBConstants.ItemIds.ITEM_ID + " FROM " + DBConstants.ItemIds.TABLE + ")";
		ServerLogger.debug(deleteParamIds);
		stmt.executeUpdate(deleteParamIds);

		String selectParamIds = "SELECT * FROM " + DBConstants.ParamIds.TABLE;
		ServerLogger.debug(selectParamIds);
		rs = stmt.executeQuery(selectParamIds);
		while (rs.next()) {
			int itemId = rs.getInt(DBConstants.ParamIds.ITEM_ID);
			int paramId = rs.getInt(DBConstants.ParamIds.PARAM_ID);
			String paramName = rs.getString(DBConstants.ParamIds.PARAM_NAME);
			paramIds.get(itemId).put(paramName, paramId);
			paramsToDelete.put(paramId, new ParamToDelete(paramId, paramName, itemNames.get(itemId)));
		}
		stmt.close();
	}
	/**
	 * Создать служебные айтемы и параметры
	 * @throws SQLException
	 */
	private void createServiceItemsAndParameters() throws SQLException {
		ParameterDescription USER = ParameterDescription.USER;
		ParameterDescription GROUP = ParameterDescription.GROUP;
		if (!itemIds.containsKey(USER.getOwnerItemId())) {
			String sql 
				= "INSERT INTO " + DBConstants.ItemIds.TABLE + "(" + DBConstants.ItemIds.ITEM_NAME + ", "
				+ DBConstants.ItemIds.ITEM_ID + ") VALUES ('_SERVICE_', " + USER.getOwnerItemId() 
				+ ") ON DUPLICATE KEY UPDATE " + DBConstants.ItemIds.ITEM_ID + " = " + DBConstants.ItemIds.ITEM_ID + ";"
				+ "INSERT INTO " + DBConstants.ParamIds.TABLE + "(" + DBConstants.ParamIds.PARAM_NAME + ", "
				+ DBConstants.ParamIds.ITEM_ID + ", " + DBConstants.ParamIds.PARAM_ID + ") VALUES ('" 
				+ USER.getName() + "', " + USER.getOwnerItemId() + ", " + USER.getId() + "), ('"
				+ GROUP.getName() + "', " + GROUP.getOwnerItemId() + ", " + GROUP.getId() 
				+ ") ON DUPLICATE KEY UPDATE " + DBConstants.ParamIds.PARAM_ID + " = " + DBConstants.ParamIds.PARAM_ID + ";";
			Statement stmt = getTransactionContext().getConnection().createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		}
	}
	/**
	 * Создать SQL критерий IN
	 * @param ids
	 * @return
	 */
	private String createIn(Collection<Integer> ids) {
		StringBuilder result = new StringBuilder("(");
		for (Integer id : ids) {
			result.append(id).append(',');
		}
		result.deleteCharAt(result.length() - 1);
		result.append(')');
		return result.toString();
	}
	/**
	 * Удалить тэги -old из файла
	 * @param fileName
	 * @throws IOException 
	 */
	private void removeOldTags(String fileName) throws IOException {
		Charset utf8 = Charset.forName("UTF-8");
		byte[] bytes = Files.readAllBytes(Paths.get(fileName));
		String xml = new String(bytes, utf8);
		xml = xml.replaceAll(" " + NAME_OLD_ATTRIBUTE + "=\"[^\"]*\"", "");
		xml = xml.replaceAll(" " + NAME_OLD_ATTRIBUTE + "='[^']*\"", "");
		xml = xml.replaceAll(" " + TYPE_OLD_ATTRIBUTE + "=\"[^\"]*\"", "");
		xml = xml.replaceAll(" " + TYPE_OLD_ATTRIBUTE + "='[^']*\"", "");
		Files.write(Paths.get(fileName), xml.getBytes(StandardCharsets.UTF_8));
	}
	
	private void createJavaConstants() {
		if (!dbChanged)
			return;
		CodeGenerator.createJavaConstants();
	}
	/**
	 * Находит все файлы pages.xml
	 * @param startFile
	 * @param files
	 * @return
	 */
	static ArrayList<File> findModelFiles(File startFile, ArrayList<File> files) {
		if (files == null) {
			files = new ArrayList<File>();
			if (startFile.isFile())
				files.add(startFile);
			else
				return findModelFiles(startFile, files);
		} else {
			if (startFile.isDirectory()) {
				File[] filesList = startFile.listFiles();
				for (File file : filesList) {
					if (file.isFile())
						files.add(file);
					else if (file.isDirectory())
						findModelFiles(file, files);
				}
			}
		}
		return files;
	}
	
	public static void main(String[] args) throws JClassAlreadyExistsException, IOException {
//		String str = "<eeee cool='rrrr' mega=\"dddd\" ultra='ffff'/><vvvv cool='dfdf' mega=\"ccccccc\"/>";
//		str = str.replaceAll(" mega=\"[^\"]*\"", "");
//		System.out.println(str.replaceAll(" mega=\"[^\"]*\"", ""));
//        URL location = DataModelCreateCommandUnit.class.getProtectionDomain().getCodeSource().getLocation();
//        System.out.println(location.getFile());

	}
}
