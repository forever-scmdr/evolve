package ecommander.model;

/**
 * Created by E on 23/2/2017.
 */
public interface DataModelXmlElementNames {
	/**
	 * Элементы
	 */
	String ASSOC = "assoc";
	String ITEM = "item";
	String ROOT = "root";
	String PARAMETER = "parameter";
	String CHILD = "child";
	String BASE_CHILD = "base-child";
	String BASE_PARENT = "base-parent";
	String ON_CREATE = "on-create";
	String ON_UPDATE = "on-update";
	String ON_DELETE = "on-delete";
	/**
	 * Атрибуты
	 */
	String NAME = "name";
	String CAPTION = "caption";
	String DESCRIPTION = "description";
	String TRANSITIVE = "transitive";
	String AG_ID = "ag-id";
	String AG_HASH = "ag-hash";
	String SUPER = "super";
	String TYPE = "type";
	String FUNCTION = "function";
	String FORMAT = "format";
	String CLASS = "format";
	String VIRTUAL = "virtual";
	String DOMAIN = "domain";
	String KEY = "key";
	String DEFAULT_PAGE = "default-page";
	String USER_DEF = "user-def";
	String INLINE = "inline";
	String EXTENDABLE = "extendable";
	String KEY_UNIQUE = "key-unique";
	String HIDDEN = "hidden";
	String SINGLE = "single";
	String MULTIPLE = "multiple";

	String TEXT_INDEX = "text-index";
	String TEXT_INDEX_PARAMETER = "text-index-parameter";
	String TEXT_INDEX_BOOST = "text-index-boost";
	String TEXT_INDEX_PARSER = "text-index-parser";

	/**
	 * Значения
	 */


	String TRUE_VALUE = "true";
	String FALSE_VALUE = "false";
}
