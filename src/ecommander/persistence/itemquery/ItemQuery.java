package ecommander.persistence.itemquery;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.MysqlConnector.ConnectionCount;
import ecommander.fwk.Pair;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.filter.FilterDefinition;
import ecommander.pages.var.FilterStaticVariable;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Этот класс строит и выволняет запрос на извлечение айтемов
 * 
 * 
 * Именование частей запроса (значения вида <<...>>)
 *
 * Описание всех запросов в текстовом файле item_query.txt
 *
 *
 * TODO <fix> привести критерий в фильтре по однинаковым параметрам к виду
 * <parameter name="param">
 *     <var value="..." sign="&gt;"/>
 *     <var value="..." sign="!="/>
 * </parameter>
 * Это нужно для того, чтобы избавиться от двойного соеднинения с одной и той же таблицей в случае
 * <parameter name="param"><var value="..." sign="&gt;"/></parameter>
 * <parameter name="param"><var value="..." sign="!="/></parameter>
 * Аналогично можно избавиться от двойного соединения с таблицей сортировки, в случае если в фильтре уже есть
 * критерий по тому же параметру, что и сортировка. Такой бывает часто в случае с ценой (не более определенной с
 * сортировкой по возрастанию)
 * @author E
 *
 */
public class ItemQuery implements DBConstants.ItemTbl, DBConstants.ItemParent, DBConstants.UniqueItemKeys, DBConstants.ItemIndexes {

	public static final int MAX_PAGE = 20;

	public interface Const {
		String DISTINCT = "<<DISTINCT>>";
		String JOIN = "<<JOIN_PART>>";
		String STATUS = "<<STATUS_PART>>";
		String WHERE = "<<WHERE_PART>>";
		String ORDER = "<<ORDER_PART>>";
		String LIMIT = "<<LIMIT_PART>>";
		String DEFINITE_LIMIT = "<<DEF_LIMIT_PART>>";
		String GROUP_PARAMS_SELECT = "<<GROUP_PARAMS_PART>>";
		String GROUP = "<<GROUP_PART>>";
		String PARENT_ID = "<<PARENT_ID_PART>>";
		String DEFINITE_PARENT_ID = "<<DEF_PARENT_ID_PART>>";
		String COUNT_TABLE_PART = "<<COUNT_TABLE>>";

		String GROUP_PARAM_COL = "GV";
		String GROUP_MAIN_TABLE = "G";
		String PARENT_ID_COL = "PID";
		String DEFINITE_PARENT_ID_COL = "DPID";
		String PARENT_ASSOC_COL = "PAS";
		String PARENT_TABLE = "P.";
		String TREE_PARENT_TABLE = "TP.";
		String ITEM_TABLE = "I.";
	}

	private final String GV = Const.GROUP_PARAM_COL;
	private final String G_DOT = Const.GROUP_MAIN_TABLE;
	private final String PID = Const.PARENT_ID_COL;
	private final String P_DOT = Const.PARENT_TABLE;
	private final String I_DOT = Const.ITEM_TABLE;
	private final String TP_DOT = Const.TREE_PARENT_TABLE;

	private static final String COMMON_QUERY
			= "SELECT <<DISTINCT>> I.*, <<PARENT_ID_PART>> AS PID "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN(<<STATUS_PART>>) "
			+ "<<WHERE_PART>> <<ORDER_PART>> <<LIMIT_PART>>";

	private static final String PARENT_QUERY
			= "SELECT <<DISTINCT>> I.*, <<PARENT_ID_PART>> AS PID "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN(<<STATUS_PART>>) <<WHERE_PART>>";


	private static final String GROUP_COMMON_QUERY
			= "SELECT <<PARENT_ID_PART>> AS PID, <<GROUP_PARAMS_PART>> "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN(<<STATUS_PART>>) "
			+ "<<WHERE_PART>> GROUP BY <<GROUP_PART>> <<ORDER_PART>>";

	private static final String COMMON_QUANTITY_QUERY
			= "SELECT COUNT(<<DISTINCT>> I." + I_ID + "), <<PARENT_ID_PART>> AS PID "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN(<<STATUS_PART>>) "
			+ "<<WHERE_PART>> GROUP BY PID";

	private static final String SINGLE_QUANTITY_QUERY
			= "SELECT COUNT(*), <<DEF_PARENT_ID_PART>> AS DPID FROM ( "
			+ "SELECT <<DISTINCT>> I." + I_ID + ", <<PARENT_ID_PART>> AS PID "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN(<<STATUS_PART>>) "
			+ "<<WHERE_PART>> <<DEF_LIMIT_PART>>) AS <<COUNT_TABLE>>";


	private boolean hasParent = false; // Есть ли критерий предка у искомого айтема (нужно ли искать потомков определенных предков)
	private LinkedList<ItemType> itemDescStack = new LinkedList<>(); // Искомый айтем (стек используется при фильтрации по ассоциированным айтемам)

	private FilterSQLCreator filter = null; // Фильтр (параметры, сортировка, группировка, пользователь, группа пользователей)
	private LimitCriteria limit = null; // Ограничение количества и страницы
	private FulltextCriteria fulltext = null; // Полнотекстовый поиск
	private Long[] ancestorIds = null; // Загруженные предки айтема (может быть null, если предов нет)
	private Byte[] assocId = null; // ID ассоциаций для загрузки
	private boolean isTransitive = false; // нужна ли транзитивная загрузка
	private boolean isParent = false; // искомый айтем ялвяется не потомком, а предком загруженных ранее айтемов
	private User user = null; // критерий пользователя-владельца айтема (для персональных айтемов)
	private String userGroupName = null; // критерий группы, которой принадлежит айтем
	private Byte[] status = null; // статус айтема (нормальный, скрытый, удаленный)
	private boolean isTree = false; // результат загрузки должен быть деревом (true) или списком (false)
	private boolean isVeryLargeResult = false; // ожидается ли очень длинный результат (после загрузки количества)
	private boolean isIdSequential = false; // последовательная пакетная загрузка в порядке возрастания ID айтема
	private long idSequentialStart = -1; // начальный ID айтема для последовательной загрузки (не включен в результат)

	public ItemQuery(ItemType itemDesc, Byte... status) {
		this.itemDescStack.push(itemDesc);
		if (status.length > 0)
			this.status = status;
		else
			this.status = new Byte[] {Item.STATUS_NORMAL};
	}

	public ItemQuery(String itemName, Byte... status) {
		this(ItemTypeRegistry.getItemType(itemName), status);
	}

	private ItemType getItemDesc() {
		return itemDescStack.peek();
	}

	/**
	 * Загруженные предшественники
	 * @param predIds
	 */
	public ItemQuery setParentIds(Collection<Long> predIds, boolean isTransitive, String...assocName) {
		this.isTransitive = isTransitive;
		this.hasParent = true;
		if (predIds != null && predIds.size() > 0) {
			this.ancestorIds = predIds.toArray(new Long[0]);
		} else {
			this.ancestorIds = new Long[0];
		}
		if (assocName.length > 0) {
			this.assocId = new Byte[assocName.length];
			for (int i = 0; i < assocName.length; i++) {
				this.assocId[i] = ItemTypeRegistry.getAssoc(assocName[i]).getId();
			}
		} else {
			this.assocId = new Byte[1];
			this.assocId[0] = ItemTypeRegistry.getPrimaryAssocId();
		}
		return this;
	}

	/**
	 * Загруженные потомки
	 * @param childrenIds
	 * @param isTransitive
	 * @param assocName
	 * @return
	 */
	public ItemQuery setChildrenIds(Collection<Long> childrenIds, boolean isTransitive, String...assocName) {
		this.isParent = true;
		return setParentIds(childrenIds, isTransitive, assocName);
	}
	/**
	 * Загруженный предшественник (один)
	 * @param predId
	 * @param assocName
	 * @return
	 */
	public ItemQuery setParentId(long predId, boolean isTransitive, String...assocName) {
		return setParentIds(Collections.singletonList(predId), isTransitive, assocName);
	}

	/**
	 * Загруженный потомок (один)
	 * @param childId
	 * @param isTransitive
	 * @param assocName
	 * @return
	 */
	public ItemQuery setChildId(long childId, boolean isTransitive, String...assocName) {
		this.isParent = true;
		return setParentId(childId, isTransitive, assocName);
	}
	/**
	 * Установить группировку (параметр, значение которого извлекается)
	 * @param paramName
	 * @param function
	 * @param sorting
	 */
	public ItemQuery setAggregation(String paramName, String function, String sorting) {
		return setAggregation(getItemDesc().getParameter(paramName), function, sorting);
	}

	/**
	 * Когда нужно производить перебор всех вариантов пакетно.
	 * Много выполнений одного запроса.
	 * @param startingId
	 */
	public ItemQuery setIdSequential(long startingId) {
		this.isIdSequential = true;
		this.idSequentialStart = startingId;
		return this;
	}
	/**
	 * Должен ли результат загрузки быть деревом
	 * Для дерева нужно дополнительно извлекать прямого родителя для каждого айтема
	 * @param tree
	 */
	public void setNeedTree(boolean tree) {
		this.isTree = tree;
		this.isTransitive = tree;
	}
	/**
	 * Установить группировку (параметр, значение которого извлекается)
	 * @param paramDesc
	 * @param function
	 * @param sorting
	 */
	public ItemQuery setAggregation(ParameterDescription paramDesc, String function, String sorting) {
		ensureFilter();
		filter.addMainAggregationParameterCriteria(paramDesc, function, sorting);
		return this;
	}
	/**
	 * Добавить параметр, по которому происходит группировка
	 * @param paramName
	 */
	public ItemQuery addAggregationGroupBy(String paramName, String...sorting) {
		return addAggregationGroupBy(getItemDesc().getParameter(paramName), null, null, null, null, sorting);
	}
	/**
	 * Добавить параметр, по которому происходит группировка
	 * @param paramDesc
	 */
	public ItemQuery addAggregationGroupBy(ParameterDescription paramDesc, String...sorting) {
		return addAggregationGroupBy(paramDesc, null, null, null, null, sorting);
	}
	/**
	 * Добавить параметр, по которому происходит группировка
	 * к этому параметру применяется некоторый критерий
	 * @param paramName
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	public ItemQuery addAggregationGroupBy(String paramName, List<String> values, String sign, String pattern, Compare compType, String...sorting) {
		return addAggregationGroupBy(getItemDesc().getParameter(paramName), values, sign, pattern, compType, sorting);
	}
	/**
	 * Добавить параметр, по которому происходит группировка
	 * к этому параметру применяется некоторый критерий
	 * @param paramDesc
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	public ItemQuery addAggregationGroupBy(ParameterDescription paramDesc, List<String> values, String sign, String pattern, Compare compType, String...sorting) {
		ensureFilter();
		String orderBy = sorting.length > 0 ? sorting[0] : null;
		filter.addAggregationParameterCriteria(paramDesc, values, sign, pattern, compType, orderBy);
		return this;
	}
	/**
	 * Добавить критерий поиска по параметру
	 * @param paramName
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	public ItemQuery addParameterCriteria(String paramName, List<String> values, String sign, String pattern, Compare compType) {
		ensureFilter();
		filter.addParameterCriteria(getItemDesc().getParameter(paramName), getItemDesc(), values, sign, pattern, compType);
		return this;
	}
	/**
	 * Добавить критерий поиска по параметру
	 * @param paramName
	 * @param value
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	public ItemQuery addParameterCriteria(String paramName, String value, String sign, String pattern, Compare compType) {
		return addParameterCriteria(paramName, Collections.singletonList(value), sign, pattern, compType);
	}

	/**
	 * Добавить критерий поиска по параметру, в котором параметр должен быть равен заданному значению
	 * @param paramName
	 * @param value
	 * @return
	 */
	public ItemQuery addParameterEqualsCriteria(String paramName, String value) {
		return addParameterCriteria(paramName, value, "=", null, Compare.SOME);
	}
	/**
	 * Добавить критерий поиска по параметру
	 * @param paramDesc
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	public ItemQuery addParameterCriteria(ParameterDescription paramDesc, List<String> values, String sign, String pattern, Compare compType) {
		ensureFilter();
		filter.addParameterCriteria(paramDesc, getItemDesc(), values, sign, pattern, compType);
		return this;
	}

	/**
	 * Добавить опцию (группу критериев, которая обхединяется с другими опциями знаком OR)
	 * @return
	 */
	public ItemQuery startOption() {
		ensureFilter();
		filter.startOption();
		return this;
	}

	/**
	 * Закрыть опцию
	 * @return
	 */
	public ItemQuery endOption() {
		ensureFilter();
		filter.endOption();
		return this;
	}

	/**
	 * Начать создание группы критериев по потомку айтема с определенной ассоциацией
	 * После создания группы и до ее закрытия все критерии будут добавляться именно в эту группу
	 * @param item
	 * @param assocId
	 * @return
	 */
	public ItemQuery startChildCriteria(ItemType item, Byte... assocId) {
		ensureFilter();
		itemDescStack.push(item);
		filter.startAssociatedGroup(item, assocId, AssociatedItemCriteriaGroup.Type.CHILD);
		return this;
	}

	/**
	 * Начать создание группы критериев по предку айтема с определенной ассоциацией
	 * После создания группы и до ее закрытия все критерии будут добавляться именно в эту группу
	 * @param item
	 * @param assocId
	 * @return
	 */
	public ItemQuery startParentCriteria(ItemType item, Byte... assocId) {
		ensureFilter();
		itemDescStack.push(item);
		filter.startAssociatedGroup(item, assocId, AssociatedItemCriteriaGroup.Type.PARENT);
		return this;
	}

	/**
	 * Начать создание группы критериев по потомку айтема с определенной ассоциацией
	 * После создания группы и до ее закрытия все критерии будут добавляться именно в эту группу
	 * @param itemName
	 * @param assocName
	 * @return
	 */
	public ItemQuery startChildCriteria(String itemName, String... assocName) {
		return startAssocCriteria(itemName, AssociatedItemCriteriaGroup.Type.CHILD, assocName);
	}

	/**
	 * Начать создание группы критериев по предку айтема с определенной ассоциацией
	 * После создания группы и до ее закрытия все критерии будут добавляться именно в эту группу
	 * @param itemName
	 * @param assocName
	 * @return
	 */
	public ItemQuery startParentCriteria(String itemName, String... assocName) {
		return startAssocCriteria(itemName, AssociatedItemCriteriaGroup.Type.PARENT, assocName);
	}

	/**
	 * Начать создание группы критериев по предку или потомку айтема с определенной ассоциацией
	 * После создания группы и до ее закрытия все критерии будут добавляться именно в эту группу
	 * @param itemName
	 * @param type
	 * @param assocName
	 * @return
	 */
	private ItemQuery startAssocCriteria(String itemName, AssociatedItemCriteriaGroup.Type type, String... assocName) {
		ensureFilter();
		Byte[] assocId;
		if (assocName.length > 0 && StringUtils.isNotBlank(assocName[0])) {
			assocId = new Byte[assocName.length];
			for (int i = 0; i < assocName.length; i++) {
				assocId[i] = ItemTypeRegistry.getAssoc(assocName[i]).getId();
			}
		} else {
			assocId = new Byte[1];
			assocId[0] = ItemTypeRegistry.getPrimaryAssocId();
		}
		ItemType item = ItemTypeRegistry.getItemType(itemName);
		itemDescStack.push(item);
		filter.startAssociatedGroup(item, assocId, type);
		return this;
	}


	/**
	 * Завершить формирование текущей группы критериев
	 * Если после завершения группы будут добавляться новые критерии, то они будут добавляться уже не в эту группу,
	 * а в родительскую (или основной фильтр или опцию)
	 * @return
	 */
	public ItemQuery endCurrentCriteria() {
		ensureFilter();
		itemDescStack.pop();
		filter.endAssociatedGroup();
		return this;
	}
	/**
	 * Добавить критерий полнотекстового поиска (запрос и список параметров, по которым происходит поиск)
	 * @param types - типы полнотекстового критерия (например near или term, или название фактори класса), сгруппированные
	 * @param queries - список запросов (обрабатываются отдельно)
	 * @param maxResults - максимальное количество результатов
	 * @param paramNames - названия параметров, по которым происходит поиск
	 * @param compType - тип стравнения (в том числе определяет что делать, если задан пустой запрос)
	 * @param threshold - Рубеж релевантности. Часть (от 0 до 1) от рейтинга первого результата, результаты с рейтингом меньше которой считаются нерелевантными
	 * @return
	 * @throws EcommanderException
	 */
	public ItemQuery setFulltextCriteria(List<String[]> types, String[] queries, int maxResults, String[] paramNames, Compare compType,
			float threshold) throws Exception {
		if (paramNames == null || paramNames.length == 0) {
			paramNames = getItemDesc().getFulltextParams().toArray(new String[0]);
		}
		fulltext = new FulltextCriteria(types, queries, maxResults, paramNames, compType, threshold);
		if ((compType == Compare.ANY || compType == Compare.ALL) && !fulltext.isValid())
			fulltext = null;
		return this;
	}
	/**
	 * Добавить критерий полнотекстового поиска (запрос и список параметров, по которым происходит поиск)
	 * @param type
	 * @param query
	 * @param maxResults
	 * @param paramNames
	 * @throws EcommanderException
	 */
	public ItemQuery setFulltextCriteria(String type, String query, int maxResults, String[] paramNames, Compare compType) throws Exception {
		return setFulltextCriteria(Collections.singletonList(new String[] {type}), new String[] {query}, maxResults, paramNames, compType, -1);
 	}
	/**
	 * Доабвить критерий поиска по предшественнику
	 * @param assocName
	 * @param sign
	 * @param itemIds
	 * @param compType
	 * @return
	 */
	public ItemQuery addPredecessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		if (compType == null)
			compType = Compare.EVERY;
		// при условии если предшественников не найдено, а критерий поиска по ним не строгий - не учитывать этот критерий вообще
		if ((itemIds == null || itemIds.size() == 0) && (compType == Compare.ALL || compType == Compare.ANY))
			return this;
		ensureFilter();
		filter.addPredecessors(assocName, sign, itemIds, compType);
		return this;
	}
	/**
	 * Доабвить критерий поиска по потомку
	 * @param sign
	 * @param itemIds
	 */
	public ItemQuery addSuccessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		if (compType == null)
			compType = Compare.EVERY;
		// при условии если потомков не найдено, а критерий поиска по ним не строгий - не учитывать этот критерий вообще
		if ((itemIds == null || itemIds.size() == 0) && (compType == Compare.ALL || compType == Compare.ANY))
			return this;
		ensureFilter();
		filter.addSuccessors(assocName, sign, itemIds, compType);
		return this;
	}
	/**
	 * Установить параметр сортировки
	 * @param paramName
	 * @param direction
	 */
	public ItemQuery addSorting(String paramName, String direction, List<String>...values) {
		ensureFilter();
		List<String> vals = null;
		if (values.length > 0)
			vals = values[0];
		filter.addSorting(getItemDesc().getParameter(paramName), direction, vals);
		return this;
	}
	/**
	 * Установить лимит
	 * @param limit
	 * @param pageNumber
	 */
	public ItemQuery setLimit(int limit, int pageNumber) {
		this.limit = new LimitCriteria(limit, pageNumber);
		return this;
	}

	/**
	 * Установить лимит
	 * @param limit
	 * @return
	 */
	public ItemQuery setLimit(int limit) {
		this.limit = new LimitCriteria(limit, 1);
		return this;
	}
	/**
	 * Установить критерий пользователя
	 * @param user
	 */
	public ItemQuery setUser(User user) {
		this.user = user;
		return this;
	}
	/**
	 * Установить критерий группы пользователей
	 * @param groupName
	 */
	public ItemQuery setGroup(String groupName) {
		if (!User.USER_DEFAULT_GROUP.equals(groupName)) {
			this.userGroupName = groupName;
		}
		return this;
	}

	/**
	 * Этот метод создает пользовательский фильтр по его заданному определению и переданным значениям
	 * Этот метод ЗАМЕНЯЕТ ТИП АЙТЕМА запроса на указанный в определении фильтра, т.к. фильтр конкретизирует
	 * тип искомых айтемов (обычно это пользовательский тип).
	 *
	 * Надо вызывать этот метод ДО добавления других критериев фильтрации
	 *
	 * @param filterDef
	 * @param userInput
	 */
	public final void applyUserFilter(FilterDefinition filterDef, FilterStaticVariable userInput) throws EcommanderException {
		ensureFilter();
		ItemType userFilterItem = ItemTypeRegistry.getItemType(filterDef.getBaseItemName());
		if (itemDescStack == null) {
			itemDescStack = new LinkedList<>();
		} else if (itemDescStack.size() > 0) {
			itemDescStack.pop();
		}
		itemDescStack.push(userFilterItem);
		UserFilterSQLCreator builderBuilder = new UserFilterSQLCreator(filter, userFilterItem, userInput);
		filterDef.iterate(builderBuilder);
	}
	/**
	 * Создать новый фильтр и вернуть его
	 * @return
	 */
	public final FilterSQLCreator createFilter() {
		return filter = new FilterSQLCreator(getItemDesc());
	}
	/**
	 * Обеспечить наличие фильтра
	 */
	private void ensureFilter() {
		if (filter == null)
			createFilter();
	}
	
	private boolean hasAggregation() {
		return filter != null && filter.hasAggregation();
	}
	
	public final boolean hasFilter() {
		return filter != null && filter.isNotBlank();
	}

	public final boolean hasLimit() {
		return limit != null;
	}
	
	public final boolean hasFulltext() {
		return fulltext != null;
	}

	/**
	 * Если запрос должен вернуть пустое множество, возвращается true
	 * @return
	 * @throws EcommanderException 
	 */
	public boolean isEmptySet() throws EcommanderException {
		return (hasParent && (ancestorIds == null || ancestorIds.length == 0)) || (filter != null && filter.isEmptySet());
	}

	/**
	 * Полнотекстовый поиск ID айтемов
	 * Вызывать после добавления критериев фильтра, если они есть
	 * @throws IOException 
	 */
	private void loadFulltextIds() throws IOException {
		// Не выполнять действие повторно
		if (fulltext.isLoaded())
			return;
		BooleanQuery.Builder query = new BooleanQuery.Builder();
		// Критерий типа айтема
		// Нужно добавлять только в том случае, если тип искомого айтема не совпадает с типом айтема, который содержит
		// индексируемый полнотекстовый параметр (т.е. является базовым типом для искомого типа)
		// Проверка принадлежности полнотекстовых параметров к айтему
		boolean needConcreteTypeCriteria = true;
		for (int i = 0; i < fulltext.getParamNames().length && needConcreteTypeCriteria; i++) {
			ParameterDescription fulltextParam = getItemDesc().getParameter(fulltext.getParamNames()[i]);
			// параметр может быть равен null в случае если полнотекстовый параметр был создан не на базе параметра айтема
			if (fulltextParam != null)
				needConcreteTypeCriteria &= fulltextParam.getOwnerItemId() != getItemDesc().getTypeId();
		}
		if (needConcreteTypeCriteria) {
			for (Integer typeId : ItemTypeRegistry.getItemExtendersIds(getItemDesc().getTypeId())) {
				query.add(new TermQuery(new Term(I_TYPE_ID, typeId.toString())), Occur.SHOULD);
			}
		} else {
			query.add(new TermQuery(new Term(I_TYPE_ID, getItemDesc().getTypeId() + "")), Occur.MUST);
		}
		/*
		// Родительский критерий (только для обычных айтемов и successor айтемов)
		if (hasParent && (queryType != Type.PARENT_OF && queryType != Type.PREDECESSORS_OF)) {
			BooleanQuery parentQuery = new BooleanQuery();
			for (Long parentId : ancestorIds) {
				parentQuery.add(new TermQuery(new Term(DBConstants.Item.DIRECT_PARENT_ID, parentId.toString())), Occur.SHOULD);
			}
			query.add(parentQuery, Occur.MUST);
		}
		*/
		// Добавить другие (неполнотектовые) критерии фильтра, если они есть
		if (hasFilter()) {
			filter.appendLuceneQuery(query, Occur.MUST);
		}
		fulltext.loadItems(query.build());
	}
	/**
	 * Добавление критерия родителя для обычной загрузки (без фильтра)
	 * Вызывать после добавления критериев самого фильтра
	 * @param query
	 */
	private void createParentTypeUserCriteria(TemplateQuery query, Long... parentIds) {

		//////////////////////////////////////////
		// Критерий предка и типа (супертипа)
		//
		if (hasParent) {
			// В частности кроме собственно родителя также указывается критерий типа айтема, т.к. супертип хранится в
			// таблице parent
			if (isParent) {
				query.getSubquery(Const.JOIN).INNER_JOIN(ITEM_PARENT_TBL + " AS P", P_DOT + IP_PARENT_ID, I_DOT + I_ID);
				query.getSubquery(Const.WHERE).AND().col_IN(P_DOT + IP_CHILD_ID).longIN(parentIds)
						.AND().col_IN(P_DOT + IP_ASSOC_ID).byteIN(assocId)
						.AND().col_IN(I_DOT + I_SUPERTYPE).intIN(ItemTypeRegistry.getBasicItemExtendersIds(getItemDesc().getTypeId()));
				if (parentIds.length == 1) {
					query.getSubquery(Const.PARENT_ID).long_(parentIds[0]);
				} else {
					query.getSubquery(Const.PARENT_ID).sql(P_DOT + IP_CHILD_ID);
				}
			} else {
				query.getSubquery(Const.JOIN).INNER_JOIN(ITEM_PARENT_TBL + " AS P", P_DOT + IP_CHILD_ID, I_DOT + I_ID);
				query.getSubquery(Const.WHERE).AND().col_IN(P_DOT + IP_PARENT_ID).longIN(parentIds)
						.AND().col_IN(P_DOT + IP_ASSOC_ID).byteIN(assocId)
						.AND().col_IN(P_DOT + IP_CHILD_SUPERTYPE).intIN(ItemTypeRegistry.getBasicItemExtendersIds(getItemDesc().getTypeId()));
				// Для деревьев нужно дополнительно извлечь ID непосредственного предка
				if (isTree) {
					query.getSubquery(Const.JOIN).INNER_JOIN(ITEM_PARENT_TBL + " AS TP", TP_DOT + IP_CHILD_ID, I_DOT + I_ID);
					query.getSubquery(Const.WHERE).AND().col_IN(TP_DOT + IP_ASSOC_ID).byteIN(assocId)
							.AND().col_IN(TP_DOT + IP_CHILD_SUPERTYPE).intIN(ItemTypeRegistry.getBasicItemExtendersIds(getItemDesc().getTypeId()))
							.AND().col(TP_DOT + IP_PARENT_DIRECT).byte_((byte)1);
					query.getSubquery(Const.PARENT_ID).sql(TP_DOT + IP_PARENT_ID);
				} else {
					if (parentIds.length == 1) {
						query.getSubquery(Const.PARENT_ID).long_(parentIds[0]);
					} else {
						query.getSubquery(Const.PARENT_ID).sql(P_DOT + IP_PARENT_ID);
					}
				}
			}
			if (isTransitive) {
				query.getSubquery(Const.WHERE).AND().col_IN(P_DOT + IP_PARENT_DIRECT).byteIN((byte) 1, (byte) 0);
			} else {
				query.getSubquery(Const.WHERE).AND().col(P_DOT + IP_PARENT_DIRECT).byte_((byte) 1);
			}
			// Добавить DISTINCT если загрузка сразу по нескольким ассоциациям
			// DISTINCT добавляется только если результат не ожидается очень большим (для оптимизации)
			if (assocId.length > 1 && !isVeryLargeResult) {
				TemplateQuery distinct = query.getSubquery(Const.DISTINCT);
				if (distinct != null && distinct.isEmpty())
					distinct.sql("DISTINCT");
			}
		} else {
			query.getSubquery(Const.PARENT_ID).sql("0");
			TemplateQuery where = query.getSubquery(Const.WHERE);
			// Если нет родителя, то критерий типа айтема нужно указывать в табилце Item
			where.AND().col_IN(I_DOT + I_SUPERTYPE).intIN(ItemTypeRegistry.getBasicItemExtendersIds(getItemDesc().getTypeId()));
			// Для того, чтобы работал индекс, нужно указывать также пользователя и группу
			// Пользователь и группа при их наличии добавляются в конце метода, здесь указываются
			// только нули если пользователь и группа не заданы
			//
			// Если использовался фильтр, то для поиска строки таблицы будет использоваться первичный ключ,
			// и ухищрения с пользователем и группой не нужны. Поэтому проверка на существование фильтра
			if (!hasFilter()) {
				if (userGroupName == null) {
					where.AND().col_IN(I_DOT + I_GROUP).byteIN(UserGroupRegistry.getAllGroupIds());
				}
			}
		}

		//////////////////////////////////////////
		// Критерий пользователя и группы
		//
		if (user != null || userGroupName != null) {
			HashSet<Byte> groupIds = new HashSet<>();
			if (userGroupName != null) {
				groupIds.add(UserGroupRegistry.getGroup(userGroupName));
			} else if (user != null) {
				HashSet<User.Group> userGroups = user.getGroups();
				for (User.Group userGroup : userGroups) {
					groupIds.add(userGroup.id);
				}
			}
			if (user != null)
				query.getSubquery(Const.WHERE).AND().col(I_DOT + I_USER).int_(user.getUserId());
			if (groupIds.size() > 0)
				query.getSubquery(Const.WHERE).AND().col_IN(I_DOT + I_GROUP).byteIN(groupIds.toArray(new Byte[0]));
		}
	}
	/**
	 * Загрузить айтемы согласно установленным параметрам запроса
	 * @return
	 * @throws Exception
	 */
	public List<Item> loadItems(Connection... conn) throws Exception {
		// Проверка, нужно ли выполнять загрузку
		if (isEmptySet())
			return new ArrayList<>();
		// Если был полнотекстовый поиск - выполнить сначала его (посмотреть, вернет ли он какие-нибудь результаты)
		if (hasFulltext()) {
			loadFulltextIds();
			// Если полнотекстовый поиск не выдал результатов - вернуть пустой массив
			if (fulltext.getLoadedIds().length <= 0)
				return new ArrayList<>();
		}
		// Выбрать нужный запрос
		TemplateQuery query;
		if (hasFilter() && filter.hasAggregation()) {
			query = TemplateQuery.createFromString(GROUP_COMMON_QUERY, "Group query");
		} else {
			if (isParent)
				query = TemplateQuery.createFromString(PARENT_QUERY, "Parent query");
			else
				query = TemplateQuery.createFromString(COMMON_QUERY, "Common query");
		}
		// Установить критерий статуса айтема
		query.getSubquery(Const.STATUS).byteArray(status);
		// Если был полнотекстовый поиск, выполнить его и добавить критерий найденных ID
		if (hasFulltext())
			query.getSubquery(Const.WHERE).AND().col_IN(I_DOT + I_ID).longIN(fulltext.getLoadedIds());
		// Добавить фильтр
		if (hasFilter())
			filter.appendQuery(query);
		// Теперь можно загружать группировку (если она есть), предварительно добавив критерий типа айтема и предка
		if (hasAggregation()) {
			createParentTypeUserCriteria(query, ancestorIds);
			return loadGroupedItems(query, conn);
		}
		// Если в фильтре нет своей сортировки, можно установить сортировку по умолчанию
		// Также если в фильтре нет сортировки, но есть полнотекстовый поиск, то нужно учитывать
		// релевантность
		if (!hasFilter() || !filter.hasSorting()) {
			TemplateQuery orderBy = query.getSubquery(Const.ORDER);
			if (orderBy != null) {
				orderBy.sql(" ORDER BY ");
				if (hasFulltext()) {
					orderBy.sql("FIELD (" + I_DOT + I_ID + ", ").longArray(fulltext.getLoadedIds()).sql(")");
				} else if (hasParent) {
					if (isTree) {
						orderBy.sql(TP_DOT + IP_WEIGHT);
					} else {
						if (isIdSequential) {
							query.getSubquery(Const.WHERE).AND().col(I_DOT + I_ID, ">").long_(idSequentialStart);
							if (isParent)
								orderBy.sql(P_DOT + IP_PARENT_ID);
							else
								orderBy.sql(P_DOT + IP_CHILD_ID);
						} else {
							orderBy.sql(P_DOT + IP_WEIGHT);
							// Оптимизация извлечения - если есть лимит и если он небольшой, ограничить
							// только первыми несколькими записями (чтобы был задействован весь мндекс для сортировки)
							if (hasLimit() && limit.getPage() == 1 && limit.getLimit() < 5) {
								query.getSubquery(Const.WHERE).AND()
										.col(P_DOT + IP_WEIGHT, " < ").int_(Item.WEIGHT_STEP * limit.getLimit() * 2);
							}
						}
					}
				} else {
					orderBy.sql(I_DOT + I_ID);
				}
			}
		}

		// Применение количественных ограничений и критерия предка (если несколько предков - для каждого предка в отдельности)
		if (hasLimit() && limit.getLimit() > 0 && hasParent && ancestorIds.length > 1) {
			TemplateQuery union = new TemplateQuery("Union because of limit criteria");
			union.sql("(");
			for (int i = 0; i < ancestorIds.length; i++) {
				TemplateQuery baseClone = (TemplateQuery) query.createClone();
				// Заполнение критериев родительского элемента
				createParentTypeUserCriteria(baseClone, ancestorIds[i]);
				// Установка критерия ограничения количества
				limit.appendQuery(baseClone);
				// Добавляется очередная часть UNIONа и замещается сгенерированным запросом
				String subqueryName = "U" + i;
				union.subquery(subqueryName).replace(baseClone);
				if (i < ancestorIds.length - 1)
					union.sql(") UNION ALL (");
			}
			// Нужно восстановить LoadedIds
			union.sql(")");
			query.replace(union);
		} else {
			createParentTypeUserCriteria(query, ancestorIds);
			// Установка критерия ограничения количества
			if (hasLimit())
				limit.appendQuery(query);
		}
		return loadByQuery(query, PID, null, fulltext, conn);
	}
	/**
	 * Загрузить первый из айтемов, соответствующих установленным критериям запроса
	 * @return
	 * @throws Exception
	 */
	public Item loadFirstItem(Connection...conn) throws Exception {
		List<Item> all = loadItems(conn);
		if (all.size() == 0)
			return null;
		return all.get(0);
	}
	/**
	 * Загрузить один айтем по его ID
	 * @param id
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static Item loadById(long id, Connection... conn) throws Exception {
		TemplateQuery query = new TemplateQuery("load by id");
		query.SELECT(ITEM_TBL + ".*", "0 AS PID").FROM(ITEM_TBL).WHERE().col(I_ID).long_(id);
		ArrayList<Item> result = loadByQuery(query, "PID", null, null, conn);
		if (result.size() > 0)
			return result.get(0);
		return null;
	}
	/**
	 * Загрузить айтемы по их ID
	 * @param id
	 * @param assocId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByParentId(long id, Byte[] assocId, Connection... conn) throws Exception {
		if (assocId == null || assocId.length == 0) {
			assocId = new Byte[1];
			assocId[0] = ItemTypeRegistry.getPrimaryAssocId();
		}
		TemplateQuery query = new TemplateQuery("load by parent");
		query.SELECT(ITEM_TBL + ".*", IP_PARENT_ID).FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID)
				.WHERE().col(IP_PARENT_ID).long_(id).AND().col(IP_PARENT_DIRECT).byte_((byte)1)
				.AND().col_IN(IP_ASSOC_ID).byteIN(assocId);
		return loadByQuery(query, IP_PARENT_ID, null, null, conn);
	}

	/**
	 * Загрузить корневой айтем (в таблице родителей родитель равен потомку)
	 * @param itemName
	 * @param userId
	 * @param userGroupId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static Item loadRootItem(String itemName, int userId, byte userGroupId, Connection... conn) throws Exception {
		ItemType type = ItemTypeRegistry.getItemType(itemName);
		if (type == null)
			return null;
		TemplateQuery query = new TemplateQuery("load root item");
		query.SELECT(ITEM_TBL + ".*", "0 AS PID")
				.FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID)
				.WHERE().col(I_GROUP).byte_(userGroupId)
				.AND().col(I_USER).int_(userId)
				.AND().col_IN(I_SUPERTYPE).intIN(ItemTypeRegistry.getItemExtendersIds(type.getTypeId()))
				.AND().col_IN(I_STATUS).byteIN(Item.STATUS_NORMAL, Item.STATUS_HIDDEN)
				.AND().col(IP_CHILD_ID).sql(IP_PARENT_ID);
		ArrayList<Item> result = loadByQuery(query, "PID", null, null, conn);
		if (result.size() > 0)
			return result.get(0);
		return null;
	}
	/**
	 * Загрузить айтемы по их ID
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public static ArrayList<Item> loadByIdsLong(Collection<Long> ids, Connection... conn) throws Exception {
		return loadByIdsLong(ids, null, conn);
	}
	/**
	 * Загрузить айтемы по их ID и вернуть в заданном порядке
	 * @param ids
	 * @param conn
	 * @param order
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByIdsLong(Collection<Long> ids, Long[] order, Connection... conn) throws Exception {
		if (ids.size() == 0)
			return new ArrayList<>();
		TemplateQuery query = new TemplateQuery("load by ids");
		query.SELECT(ITEM_TBL + ".*", "0 AS PID")
				.FROM(ITEM_TBL).WHERE().col_IN(I_ID).longIN(ids.toArray(new Long[ids.size()]));
		return loadByQuery(query, "PID", order, null, conn);
	}
	/**
	 * Загрузить айтемы по их ID
	 * @param ids
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByIdsString(Collection<String> ids, Connection... conn) throws Exception {
		if (ids.size() == 0)
			return new ArrayList<>();
		ArrayList<Long> idsLong = new ArrayList<>();
		for (String id : ids) {
			try {
				idsLong.add(Long.parseLong(id));
			} catch (Exception e) { }
		}
		return loadByIdsLong(idsLong, null, conn);
	}
	/**
	 * Загрузить айтемы по их ID с учетом типа айтема
	 * @param ids
	 * @param itemTypeName
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByIdsString(Collection<String> ids, String itemTypeName, Connection... conn) throws Exception {
		ArrayList<Item> byIds = loadByIdsString(ids, conn);
		Set<String> extNames = ItemTypeRegistry.getItemExtenders(itemTypeName);
		Iterator<Item> iter = byIds.iterator();
		while(iter.hasNext()) {
			Item item = iter.next();
			if (!extNames.contains(item.getTypeName()))
				iter.remove();
		}
		return byIds;
	}
	/**
	 * Загрузить айтемы по их уникальному текстовому ключу
	 * @param keys
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByUniqueKey(Collection<String> keys, Connection... conn) throws Exception {
		if (keys.size() == 0)
			return new ArrayList<>(0);
		TemplateQuery idsSelect = new TemplateQuery("ids select");
		idsSelect.SELECT(UK_ID).FROM(UNIQUE_KEY_TBL).WHERE().col_IN(UK_KEY).stringIN(keys.toArray(new String[0]));
		boolean isOwnConnection = false;
		Connection connection = null;
		ArrayList<Long> ids = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			pstmt = idsSelect.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				ids.add(rs.getLong(1));
			rs.close();
			queryFinished(connection);
			return loadByIdsLong(ids, connection);
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection) MysqlConnector.closeConnection(connection);
		}
	}

	/**
	 * Загрузить один айтем по уникальному ключу
	 * @param keys
	 * @return
	 * @throws Exception
	 */
	public static LinkedHashMap<String, Item> loadByUniqueKey(String... keys) throws Exception {
		LinkedHashMap<String, Item> result = new LinkedHashMap<>();
		for (String key : keys) {
			result.put(key, null);
		}
		ArrayList<Item> items = loadByUniqueKey(Arrays.asList(keys));
		for (Item item : items) {
			result.put(item.getKeyUnique(), item);
		}
		return result;
	}
	/**
	 * Загрузить айтем по значению одного параметра
	 * @param itemName
	 * @param paramName
	 * @param paramValue
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByParamValue(String itemName, String paramName, Collection<String> paramValue, Connection... conn)
			throws Exception {
		ItemType item = ItemTypeRegistry.getItemType(itemName);
		Integer[] extenders = ItemTypeRegistry.getItemExtendersIds(item.getTypeId());
		ParameterDescription param = item.getParameter(paramName);
		TemplateQuery query = new TemplateQuery("unique parameter value query");
		query
				.SELECT(ITEM_TBL + ".*, 0 AS PID").FROM(ITEM_TBL)
				.INNER_JOIN(DataTypeMapper.getTableName(param.getType()), I_ID, II_ITEM_ID)
				.WHERE().col(II_PARAM).int_(param.getId()).AND().col(I_STATUS).byte_(Item.STATUS_NORMAL)
				.AND().col(II_VALUE, " IN(");
		DataTypeMapper.appendPreparedStatementRequestValues(param.getType(), query, paramValue);
		query.sql(")");
		// Если параметр принадлежит самому запрашиваемому айтему,
		// то использовать критерий типа айтема не обязательно.
		// Если параметр принадлежит предку айтема, то в случае неиспользования критерия типа айтема
		// могут вернуться лишние айтемы (тип которых является предком искомого типа)
		if (param.getOwnerItemId() != item.getTypeId()) {
			query.AND().col_IN(II_ITEM_TYPE).intIN(extenders);
		}
		return loadByQuery(query, "PID", null, null, conn);
	}
	/**
	 * Загрузить айтем по значению одного параметра
	 * @param itemName
	 * @param paramName
	 * @param paramValue
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Item> loadByParamValue(String itemName, String paramName, String paramValue, Connection... conn) throws Exception {
		return loadByParamValue(itemName, paramName, Collections.singletonList(paramValue), conn);
	}
	/**
	 * Загрзуить один айтем по значению параметра, подразумевается, что значение этого параметра является уникальным
	 * @param itemName
	 * @param paramName
	 * @param paramValue
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public static Item loadSingleItemByParamValue(String itemName, String paramName, String paramValue, Connection... conn) throws Exception {
		ArrayList<Item> items = loadByParamValue(itemName, paramName, paramValue, conn);
		if (items.size() == 0)
			return null;
		return items.get(0);
	}
	/**
	 * Загрузить единственный айтем по названию.
	 * Если айтем не найден - возвращается null
	 * Если айтем не единственный - выбрасывается исключение IllegalArgumentException
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	public static Item loadSingleItemByName(String itemName) throws Exception {
		ItemQuery query = new ItemQuery(itemName);
		List<Item> items = query.loadItems();
		if (items.size() == 0)
			return null;
		if (items.size() > 1)
			throw new IllegalArgumentException("It appears that requested item is not single. This exception is thrown to avoid ambiguity");
		return items.get(0);
	}
	/**
	 * Получить айтем, подлежащий фильтрации
	 * @return
	 */
	public ItemType getItemToFilter() {
		return getItemDesc();
	}
	/**
	 * Загрузить по готовому запросу
	 * @param query
	 * @param parentIdCol
	 * @param order - опциональный параметр, который хранит порядок следования айтемов в результате
	 * @param fulltext - опциональный параметр, нужный для вывода фрагментов с подсвеченным текстом при полнотекстовом поиске
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<Item> loadByQuery(TemplateQuery query, String parentIdCol, Long[] order,
	                                           FulltextCriteria fulltext, Connection... conn) throws Exception {
		boolean isOwnConnection = false;
		Connection connection = null;
		ArrayList<Item> result = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			pstmt = query.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			if (order == null) {
				while (rs.next())
					result.add(ItemMapper.buildItem(rs, (byte) 0, parentIdCol));
			} else {
				MultiValuedMap<Long, Item> items = MultiMapUtils.newListValuedHashMap();
				//HashMap<Long, Item> items = new HashMap<Long, Item>();
				while (rs.next()) {
					Item item = ItemMapper.buildItem(rs, (byte) 0, parentIdCol);
					items.put(item.getId(), item);
				}
				for (Long itemId : order) {
					Collection<Item> sameItems = items.get(itemId);
					if (sameItems != null)
						result.addAll(sameItems);
				}
			}
			rs.close();
			queryFinished(connection);
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection) MysqlConnector.closeConnection(connection);
		}
		// Установка фрагментов подсвеченного текста в айтемы
		if (fulltext != null) {
			for (Item item : result) {
				ArrayList<Pair<String, String>> queryAndHighlight = fulltext.getQueryAndHighlightedText(item.getId());
				if (queryAndHighlight != null) {
					for (Pair<String, String> qandh : queryAndHighlight) {
						item.setExtra(FulltextCriteria.QUERY, qandh.getLeft());
						item.setExtra(FulltextCriteria.HIGHLIGHT_EXTRA_NAME, qandh.getRight());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Загружает общие количества айтемов, сответствующих фильтру и родителю
	 * @return
	 * @throws SQLException 
	 * @throws NamingException 
	 * @throws IOException 
	 * @throws EcommanderException 
	 */
	public HashMap<Long, Integer> oldLoadTotalQuantities(Connection... conn) throws SQLException, NamingException, IOException, EcommanderException {
		if (isEmptySet())
			return new HashMap<>();
		// Если был полнотекстовый поиск - выполнить сначала его (посмотреть, вернет ли он какие-нибудь результаты)
		if (hasFulltext())
			loadFulltextIds();
		// Выбрать нужный запрос
		TemplateQuery query = TemplateQuery.createFromString(COMMON_QUANTITY_QUERY, "Common qty query");
		// Установить критерий статуса айтема
		query.getSubquery(Const.STATUS).byteArray(status);
		// Если был полнотекстовый поиск, выполнить его и добавить критерий найденных ID
		if (hasFulltext())
			query.getSubquery(Const.WHERE).AND().col_IN(I_DOT + I_ID).longIN(fulltext.getLoadedIds());
		// Добавить фильтр
		if (hasFilter())
			filter.appendQuery(query);
		// Критерий родителя и типа айтема
		createParentTypeUserCriteria(query, ancestorIds);
		// Выполнение запроса к БД
		HashMap<Long, Integer> result = new HashMap<>();
		boolean isOwnConnection = false;
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			pstmt = query.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.put(rs.getLong(2), rs.getInt(1));
			}
			rs.close();
			queryFinished(connection);
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection) MysqlConnector.closeConnection(connection);
		}
		return result;
	}
	/**
	 * Загружает общие количества айтемов, сответствующих фильтру и родителю
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 * @throws IOException
	 * @throws EcommanderException
	 */
	public HashMap<Long, Integer> loadTotalQuantities(Connection... conn) throws SQLException, NamingException, IOException, EcommanderException {
		if (isEmptySet())
			return new HashMap<>();
		// Если был полнотекстовый поиск - выполнить сначала его (посмотреть, вернет ли он какие-нибудь результаты)
		if (hasFulltext())
			loadFulltextIds();
		// Выбрать нужный запрос
		TemplateQuery baseQuery = TemplateQuery.createFromString(SINGLE_QUANTITY_QUERY, "Single qty query");
		// Установить критерий статуса айтема
		baseQuery.getSubquery(Const.STATUS).byteArray(status);
		// Если был полнотекстовый поиск, выполнить его и добавить критерий найденных ID
		if (hasFulltext())
			baseQuery.getSubquery(Const.WHERE).AND().col_IN(I_DOT + I_ID).longIN(fulltext.getLoadedIds());
		// Добавить фильтр
		if (hasFilter())
			filter.appendQuery(baseQuery);

		int limitQty = 0;
		if (hasLimit()) {
			limitQty = limit.getPage() * limit.getLimit() + limit.getLimit() * MAX_PAGE;
		}
		TemplateQuery unionQuery = new TemplateQuery("Qty query");
		Long[] ancIds = {new Long(0)};
		if (ancestorIds != null)
			ancIds = ancestorIds;
		for (Long ancestorId : ancIds) {
			if (!unionQuery.isEmpty())
				unionQuery.sql(" UNION ALL ");
			String tableName = "QTY_" + ancestorId;
			TemplateQuery clone = (TemplateQuery) baseQuery.createClone();
			createParentTypeUserCriteria(clone, ancestorId);
			clone.getSubquery(Const.DEFINITE_PARENT_ID).long_(ancestorId);
			clone.getSubquery(Const.COUNT_TABLE_PART).sql(tableName);
			if (hasLimit()) {
				clone.getSubquery(Const.DEFINITE_LIMIT).sql(" LIMIT " + limitQty);
			}
			unionQuery.subquery(tableName).replace(clone);
		}

		// Выполнение запроса к БД
		HashMap<Long, Integer> result = new HashMap<>();
		boolean isOwnConnection = false;
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			pstmt = unionQuery.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int qty = rs.getInt(1);
				if (qty == limitQty)
					isVeryLargeResult = true;
				result.put(rs.getLong(2), qty);
			}
			rs.close();
			queryFinished(connection);
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection) MysqlConnector.closeConnection(connection);
		}
		return result;
	}
	/**
	 * Загрузить группу айтемов
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private ArrayList<Item> loadGroupedItems(TemplateQuery query, Connection... conn) throws SQLException, IOException, NamingException {
		// Выполнение запроса к БД
		ArrayList<Item> result = new ArrayList<>();
		boolean isOwnConnection = false;
		Connection connection = null;
		PreparedStatement pstmt = null;
		final int PARENT_COL = 1;
		final int MAIN_COL = 2;
		final int ADDITIONAL_COL = 3;
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			pstmt = query.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Item item = Item.existingItem(getItemDesc(), -1, ItemTypeRegistry.getPrimaryAssoc(),
						rs.getLong(PARENT_COL), 0, (byte) 0, Item.STATUS_NORMAL, "", "", "", 0, false);
				ParameterDescription aggParam = filter.getMainAggregationCriteria().getParam();
				item.setValue(aggParam.getId(), DataTypeMapper.createValue(aggParam.getType(), rs, MAIN_COL));
				MainAggregationCriteria agg = filter.getMainAggregationCriteria();
				if (agg.hasGroupByExtra()) {
					for (int i = 0; i < agg.getGroupByExtra().size(); i++) {
						ParameterDescription desc = agg.getGroupByExtra().get(i).getParam();
						item.setValue(desc.getId(), DataTypeMapper.createValue(desc.getType(), rs, i + ADDITIONAL_COL));
					}
				}
				result.add(item);
			}
			rs.close();
			queryFinished(connection);
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection)
				MysqlConnector.closeConnection(connection);
		}
		return result;
	}
	
	public static void queryFinished(Connection conn) {
		if (conn instanceof ConnectionCount)
			((ConnectionCount) conn).queryFinished();
	}

	/**
	 * Проверяет, является ли айтем наследником другого айтема (проверяемого предка)
	 * @param childId
	 * @param parentId
	 * @param assocId
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	public static boolean isAncestor(long childId, long parentId, byte assocId, Connection... conn) throws SQLException, NamingException {
		TemplateQuery query = new TemplateQuery("ParsedItem Check");
		query.SELECT("*").FROM(ITEM_PARENT_TBL).WHERE().col(IP_CHILD_ID).long_(childId)
				.AND().col(IP_PARENT_ID).long_(parentId)
				.AND().col(IP_ASSOC_ID).byte_(assocId);
		boolean isOwnConnection = false;
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			pstmt = query.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection)
				MysqlConnector.closeConnection(connection);
		}
	}

	/**
	 * Загрузить все значения одного параметра (для айтема одного типа)
	 * @param item
	 * @param param
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	public static ArrayList<String> loadParameterValues(ItemType item, ParameterDescription param, Connection... conn) throws SQLException, NamingException {
		String tableName = DataTypeMapper.getTableName(param.getType());
		TemplateQuery query = new TemplateQuery("Load Parameter Values");
		query.SELECT(II_VALUE).FROM(tableName).WHERE()
				.col(II_ITEM_TYPE).int_(item.getTypeId()).AND().col(II_PARAM).int_(param.getId())
				.sql(" GROUP BY " + II_VALUE)
				.ORDER_BY(II_VALUE);
		boolean isOwnConnection = false;
		Connection connection = null;
		PreparedStatement pstmt = null;
		ArrayList<String> result = new ArrayList<>();
		DataType paramType = DataTypeRegistry.getType(param.getType());
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			pstmt = query.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				Object value = DataTypeMapper.createValue(param.getType(), rs);
				result.add(paramType.outputValue(value, null));
			}
			return result;
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection)
				MysqlConnector.closeConnection(connection);
		}
	}
}
