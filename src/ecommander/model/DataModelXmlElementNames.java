package ecommander.model;

/**
 * Элементы файла модели данных
 * Created by E on 23/2/2017.
 */
public interface DataModelXmlElementNames {
	/**
	 * Элементы
	 */
	String ASSOC = "assoc";
	String ITEM = "item";
	String ITEMDESC = "itemdesc";
	String ROOT = "root";
	String USER_GROUP = "user-group";
	String PARAMETER = "parameter";
	String CHILD = "child";
	String BASE_CHILD = "base-child";
	String BASE_PARENT = "base-parent";
	String MODEL = "model";
	String ON_CREATE = "on-create";
	String ON_UPDATE = "on-update";
	String ON_DELETE = "on-delete";
	String VALUE = "value";
	/**
	 * Атрибуты
	 */
	String NAME = "name";
	String CAPTION = "caption";
	String DESCRIPTION = "description";
	String TRANSITIVE = "transitive";
	String GROUP = "group";
	String AG_ID = "ag-id";
	String AG_HASH = "ag-hash";
	String SUPER = "super";
	String TYPE = "type";
	String FUNCTION = "function";
	String FORMAT = "format";
	String CLASS = "class";
	String VIRTUAL = "virtual";
	String INLINE = "inline";
	String SORTING = "sorting";
	String LIMIT = "limit";
	String CHILDREN_PER_PAGE = "children-per-page";
	String CHILDREN_SORTING = "children-sorting";
	String DOMAIN = "domain";
	String KEY = "key";
	String DEFAULT_PAGE = "default-page";
	String USER_DEF = "user-def";
	String EXTENDABLE = "extendable";
	String KEY_UNIQUE = "key-unique";
	String HIDDEN = "hidden";
	String SINGLE = "single";
	String MULTIPLE = "multiple";
	String DEFAULT = "default";
	String OWNER_ID = "owner-id";
	String PARAM = "param";
	String ID = "id";

	String TEXT_INDEX = "text-index";
	String TEXT_INDEX_PARAMETER = "text-index-parameter";
	String TEXT_INDEX_BOOST = "text-index-boost";
	String TEXT_INDEX_ITEM = "text-index-item";
	String TEXT_INDEX_PARSER = "text-index-parser";
	String TEXT_INDEX_ANALYZER = "text-index-analyzer";

	/**
	 * Значения
	 */


	String TRUE_VALUE = "true";
	String FALSE_VALUE = "false";

	String SIMPLE_VALUE = "simple";
	String ADMIN_VALUE = "admin";
}
