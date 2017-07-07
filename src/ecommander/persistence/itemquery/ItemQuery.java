package ecommander.persistence.itemquery;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.MysqlConnector.ConnectionCount;
import ecommander.model.*;
import ecommander.model.filter.FilterDefinition;
import ecommander.model.item.*;
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
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Этот класс строит и выволняет запрос на извлечение айтемов
 * 
 * 
 * Именование частей запроса (значения вида <<...>>)
 *
 * Описание всех запросов в текстовом файле item_query.txt
 *
 *
 * 
 * @author E
 *
 */
public class ItemQuery implements DBConstants.ItemTbl, DBConstants.ItemParent, DBConstants.UniqueItemKeys, DBConstants.ItemIndexes {

	interface Const {
		String JOIN = "<<JOIN_PART>>";
		String STATUS = "<<STATUS_PART>>";
		String WHERE = "<<WHERE_PART>>";
		String ORDER = "<<ORDER_PART>>";
		String LIMIT = "<<LIMIT_PART>>";
		String GROUP_PARAMS_SELECT = "<<GROUP_PARAMS_PART>>";
		String GROUP = "<<GROUP_PART>>";
		String PARENT_ID = "<<PARENT_ID_PART>>";

		String GROUP_PARAM_COL = "GV";
		String GROUP_MAIN_TABLE = "G.";
		String PARENT_ID_COL = "PID";
		String PARENT_TABLE = "P.";
		String ITEM_TABLE = "I.";
	}

	private static final String COMMON_QUERY
			= "SELECT DISTINCT I.*, <<PARENT_ID_PART>> AS PID "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN (<<STATUS_PART>>) "
			+ "<<WHERE_PART>> <<ORDER_PART>> <<LIMIT_PART>>";

	private static final String PARENT_QUERY
			= "SELECT DISTINCT I.*, P." + IP_PARENT_ID + " AS PID "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN (<<STATUS_PART>>) <<WHERE_PART>>";


	private static final String GROUP_COMMON_QUERY
			= "SELECT DISTINCT <<PARENT_ID_PART>> AS PID <<GROUP_PARAMS_PART>> "
			+ "FROM " + ITEM_TBL + " AS I <<JOIN_PART>> "
			+ "WHERE I." + I_STATUS + " IN (<<STATUS_PART>>) "
			+ "<<WHERE_PART>> GROUP BY <<GROUP_PART>> <<ORDER_PART>>";


	private boolean hasParent = false; // Предок искомого айтема. Может быть null, если айтем не имеет предка
	private ItemType itemDesc; // Искомый айтем

	private FilterSQLCreator filter = null; // Фильтр (параметры, сортировка, группировка, пользователь, группа пользователей)
	private LimitCriteria limit = null; // Ограничение количества и страницы
	private FulltextCriteria fulltext = null; // Полнотекстовый поиск
	private ArrayList<Long> loadedIds = null; // Загруженные предки айтема (может быть null, если предов нет)
	private String assocName = null; // название ассоциации для загрузки
	private User user = null; // критерий пользователя-владельца айтема (для персональных айтемов)
	private String userGroupName = null; // критерий группы, которой принадлежит айтем

	
	public ItemQuery(ItemType itemDesc) {
		this.itemDesc = itemDesc;
	}

	/**
	 * Загруженные предшественники
	 * @param predIds
	 */
	public ItemQuery setParentIds(Collection<Long> predIds, String...assocName) {
		if (predIds != null) {
			this.loadedIds = new ArrayList<>(predIds);
			this.hasParent = true;
		}
		if (assocName.length > 0)
			this.assocName = assocName[0];
		else
			this.assocName = ItemTypeRegistry.getPrimaryAssoc().getName();
		return this;
	}
	/**
	 * Загруженный предшественник (один)
	 * @param predId
	 * @param assocName
	 * @return
	 */
	public ItemQuery setParentId(long predId, String...assocName) {
		return setParentIds(Collections.singletonList(predId), assocName);
	}
	/**
	 * Установить группировку (параметр, значение которого извлекается)
	 * @param paramName
	 * @param function
	 * @param sorting
	 */
	public ItemQuery setAggregation(String paramName, String function, String sorting) {
		return setAggregation(itemDesc.getParameter(paramName), function, sorting);
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
		return addAggregationGroupBy(itemDesc.getParameter(paramName), null, null, null, null, sorting);
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
		return addAggregationGroupBy(itemDesc.getParameter(paramName), values, sign, pattern, compType, sorting);
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
		filter.addParameterCriteria(itemDesc.getParameter(paramName), itemDesc, values, sign, pattern, compType);
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
	 * Добавить критерий поиска по параметру
	 * @param paramDesc
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	public ItemQuery addParameterCriteria(ParameterDescription paramDesc, List<String> values, String sign, String pattern, Compare compType) {
		ensureFilter();
		filter.addParameterCriteria(paramDesc, itemDesc, values, sign, pattern, compType);
		return this;
	}
	/**
	 * Добавить критерий полнотекстового поиска (запрос и список параметров, по которым происходит поиск)
	 * @param types - типы полнотекстового критерия (например near или term, или название фактори класса)
	 * @param queries - список запросов (обрабатываются отдельно)
	 * @param maxResults - максимальное количество результатов
	 * @param paramName - название параметра, по которому происходит поиск
	 * @param compType - тип стравнения (в том числе определяет что делать, если задан пустой запрос)
	 * @param threshold - Рубеж релевантности. Часть (от 0 до 1) от рейтинга первого результата, результаты с рейтингом меньше которой считаются нерелевантными
	 * @return
	 * @throws EcommanderException
	 */
	public ItemQuery setFulltextCriteria(String[] types, String[] queries, int maxResults, String paramName, Compare compType,
			float threshold) throws Exception {
		String[] paramNames = null;
		if (StringUtils.isBlank(paramName)) {
			paramNames = itemDesc.getFulltextParams().toArray(new String[0]);
		} else {
			paramNames = new String[1];
			paramNames[0] = paramName;
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
	 * @param paramName
	 * @throws EcommanderException
	 */
	public ItemQuery setFulltextCriteria(String type, String query, int maxResults, String paramName, Compare compType) throws Exception {
		return setFulltextCriteria(new String[] {type}, new String[] {query}, maxResults, paramName, compType, -1);
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
		filter.addSorting(itemDesc.getParameter(paramName), direction, vals);
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
	 * Создать фильтр исходя из определения фильтра и ввода пользователя
	 * @param filterDef
	 * @param userInput
	 * @return
	 * @throws EcommanderException
	 */
	public final FilterSQLCreator createFilter(FilterDefinition filterDef, FilterStaticVariable userInput) throws EcommanderException {
		ItemType userFilterItem = ItemTypeRegistry.getItemType(filterDef.getBaseItemName());
		if (userFilterItem != null)
			itemDesc = userFilterItem;
		FilterSQLCreatorBuilder builderBuilder = new FilterSQLCreatorBuilder(this, userInput);
		filterDef.iterate(builderBuilder);
		return filter = builderBuilder.getSqlCreator();
	}
	/**
	 * Создать новый фильтр и вернуть его
	 * @return
	 */
	public final FilterSQLCreator createFilter() {
		return filter = new FilterSQLCreator(itemDesc);
	}
	/**
	 * Обеспечить наличие фильтра
	 */
	private void ensureFilter() {
		if (!hasFilter())
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
	private boolean isEmptySet() throws EcommanderException {
		return (hasParent && (loadedIds == null || loadedIds.size() == 0)) || (filter != null && filter.isEmptySet());
	}
	/**
	 * Добавление критерия родителя для фильтра
	 * Вызывать после добавления критериев самого фильтра
	 */
	private void createFilterParentCriteria(TemplateQuery dbQuery, ArrayList<Long> parentIds) {
		if (queryType == Type.ITEM) {
			dbQuery.getSubquery(PARENT_ID_REQ).sql(filter.getParentColoumnName());
			if (hasParent)
				dbQuery.getSubquery(WHERE_OPT).getSubquery(FILTER_CRITS_OPT)
					.getSubquery(PARENT_CRIT_OPT).sql(" IN (").longArray(parentIds.toArray(new Long[parentIds.size()])).sql(")");
		} else if (queryType == Type.SUCCESSOR) {
			// Извлечение ID предка
			dbQuery.getSubquery(PARENT_ID_REQ).sql(PARENT_TABLE + '.' + DBConstants.ItemParent.IP_PARENT_ID);
			// Подстановка таблицы предков айтемов
			TemplateQuery fromPart = dbQuery.getSubquery(FROM_OPT);
			if (!fromPart.isEmpty())
				fromPart.sql(", ");
			fromPart.sql(DBConstants.ItemParent.ITEM_PARENT_TBL + " AS " + PARENT_TABLE);
			// Подстановка критерия ID предков и типа предков
			TemplateQuery wherePart = dbQuery.getSubquery(WHERE_OPT);
			wherePart.getSubquery(FILTER_JOIN_OPT)
				.sql(PARENT_TABLE + '.' + DBConstants.ItemParent.IP_PARENT_ID + " IN (")
				.longArray(parentIds.toArray(new Long[parentIds.size()]))
				.sql(")")
				
				// Временно закоментирован критерий типа предка.
				// Сделано это потому что по идее главным критерием (который отсеивает максимальное количество результатов) 
				// при фильтрации должен быть критерий по параметру.
				
//				.sql(" AND " + PARENT_TABLE  + '.' + DBConstants.ItemParent.ITEM_TYPE + " IN (")
//				.intArray(ItemTypeRegistry.getItemContainers(itemDesc.getTypeId()))	// Тип предка потому что в таблицах индекса хранятся ID предков,
//																						// и тип у них тоже должен быть типом предка
				//.intArray(ItemTypeRegistry.getItemExtendersIds(itemDesc.getTypeId()))
				.sql(" AND "); // Удалена закрывающая скобка перед AND в связи с коментированием критерия типа предка
			// Связь таблицы предков с таблицами параметров фильтра
			TemplateQuery parentCrit = wherePart.getSubquery(FILTER_CRITS_OPT).getSubquery(PARENT_CRIT_OPT);
			if (parentCrit != null)
				parentCrit.sql(" = " + PARENT_TABLE + '.' + DBConstants.ItemParent.REF_ID);
		}
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
		BooleanQuery.Builder typeQuery = new BooleanQuery.Builder();
		for (int i = 0; i < fulltext.getParamNames().length && needConcreteTypeCriteria; i++) {
			ParameterDescription fulltextParam = itemDesc.getParameter(fulltext.getParamNames()[i]);
			// параметр может быть равен null в случае если полнотекстовый параметр был создан не на базе параметра айтема
			if (fulltextParam != null)
				needConcreteTypeCriteria &= fulltextParam.getOwnerItemId() != itemDesc.getTypeId();
		}
		if (needConcreteTypeCriteria) {
			for (Integer typeId : ItemTypeRegistry.getItemExtendersIds(itemDesc.getTypeId())) {
				typeQuery.add(new TermQuery(new Term(I_TYPE_ID, typeId.toString())), Occur.SHOULD);
			}
		} else {
			typeQuery.add(new TermQuery(new Term(I_TYPE_ID, itemDesc.getTypeId() + "")), Occur.MUST);
		}
		query.add(typeQuery.build(), Occur.MUST);
		/*
		// Родительский критерий (только для обычных айтемов и successor айтемов)
		if (hasParent && (queryType != Type.PARENT_OF && queryType != Type.PREDECESSORS_OF)) {
			BooleanQuery parentQuery = new BooleanQuery();
			for (Long parentId : loadedIds) {
				parentQuery.add(new TermQuery(new Term(DBConstants.Item.DIRECT_PARENT_ID, parentId.toString())), Occur.SHOULD);
			}
			query.add(parentQuery, Occur.MUST);
		}
		*/
		// Добавить другие (неполнотектовые) критерии фильтра, если они есть
		if (hasFilter()) {
			BooleanQuery.Builder luceneFilterQuery = filter.appendLuceneQuery(null, null);
			query.add(luceneFilterQuery.build(), Occur.MUST);
		}
		fulltext.loadItems(query.build());
	}
	/**
	 * Добавление критерия родителя для обычной загрузки (без фильтра)
	 * Вызывать после добавления критериев самого фильтра
	 * @param query
	 * @param parentIds
	 */
	private void createCommonParentCriteria(TemplateQuery query, ArrayList<Long> parentIds) {
		if (queryType == Type.ITEM) {
			query.getSubquery(PARENT_ID_REQ).sql(DBConstants.Item.DIRECT_PARENT_ID);
			if (hasParent) {
				query.getSubquery(WHERE_OPT).sql(" AND " + DBConstants.Item.DIRECT_PARENT_ID + " IN (")
						.longArray(parentIds.toArray(new Long[parentIds.size()])).sql(")");
			}
		}
		else if (queryType == Type.SUCCESSOR) {
			query.getSubquery(PARENT_ID_REQ).sql(DBConstants.ItemParent.IP_PARENT_ID);
			query.getSubquery(FROM_OPT).sql(", " + DBConstants.ItemParent.ITEM_PARENT_TBL);
			query.getSubquery(WHERE_OPT)
					.sql(" AND " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.REF_ID + " AND "
							+ DBConstants.ItemParent.IP_PARENT_ID + " IN (")
					.longArray(parentIds.toArray(new Long[parentIds.size()])).sql(")");
		}
		else if (queryType == Type.PARENT_OF) {
			query.getSubquery(PARENT_ID_REQ).sql(DBConstants.ItemParent.REF_ID);
			query.getSubquery(FROM_OPT).sql(", " + DBConstants.ItemParent.ITEM_PARENT_TBL);
			query.getSubquery(WHERE_OPT)
					.sql(" AND " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.IP_PARENT_ID + " AND "
							+ DBConstants.ItemParent.REF_ID + " IN (")
					.longArray(parentIds.toArray(new Long[parentIds.size()])).sql(")")
					.sql(" AND " + DBConstants.ItemParent.IP_PARENT_DIRECT + "=1");
		}
		else if (queryType == Type.PREDECESSORS_OF) {
			query.getSubquery(PARENT_ID_REQ).sql(DBConstants.ItemParent.REF_ID);
			query.getSubquery(FROM_OPT).sql(", " + DBConstants.ItemParent.ITEM_PARENT_TBL);
			query.getSubquery(WHERE_OPT)
					.sql(" AND " + DBConstants.Item.ID + " = " + DBConstants.ItemParent.IP_PARENT_ID + " AND "
							+ DBConstants.ItemParent.REF_ID + " IN (")
					.longArray(parentIds.toArray(new Long[parentIds.size()])).sql(")");
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
			return new ArrayList<Item>();
		PreparedStatement pstmt = null;
		Long[] order = null;
		boolean isOwnConnection = false;
		Connection connection = null;
		try {
			if (conn == null || conn.length == 0) {
				isOwnConnection = true;
				connection = MysqlConnector.getConnection();
			} else {
				connection = conn[0];
			}
			
			// Если есть фильтр
			// Загрузка ID айтемов по фильтру, загрузка айтемов по ID. Возвращение результата
			// !!!!!!!!   ANCESTOR-OF НЕ ИСПОЛЬЗУЕТ ФИЛЬТР   !!!!!!!!!!!!!!
			if (hasFilter() && queryType != Type.PARENT_OF && queryType != Type.PREDECESSORS_OF) {
				if (hasAggregation())
					return loadGroupedItems(connection);
				LinkedHashMap<Long, List<Long>> itemIds = loadFilteredIds(connection);
				LinkedHashMap<Long, Long> allIdsSorted = new LinkedHashMap<Long, Long>();
				for (Long parentId : itemIds.keySet()) {
					for (Long childId : itemIds.get(parentId)) {
						allIdsSorted.put(childId, parentId);
					}
				}
				ArrayList<Item> sorted = null;
				if (filter.hasSorting()) {
					sorted = loadByIdsLong(allIdsSorted.keySet(), allIdsSorted.keySet().toArray(new Long[0]), conn);
				} else {
					sorted = loadByIdsLong(allIdsSorted.keySet(), conn);
				}
				for (Item item : sorted) {
					item.setContextParentId(allIdsSorted.get(item.getId()));
				}
// TODO <fix> удалить
//				int i = 0;
//				for (Long parentId : itemIds.keySet()) {
//					List<Long> list = itemIds.get(parentId);
//					for (int j = 0; j < list.size(); j++) {
//						if (sorted.size() > i)
//							sorted.get(i++).setContextParentId(parentId);
//					}
//				}
				return sorted;
			}

			// Простая загрузка айтемов - без фильтра и без полнотекстового поиска
			// Загрузить айтемы без параметров поиска но с применением параметра родительского айтема
			TemplateQuery dbQuery = TemplateQuery.createFromString(COMMON_SELECT_QUERY, queryType.toString() + " SIMPLE " + itemDesc.getName());
			// Установка критерия типа айтема
			dbQuery.getSubquery(WHERE_OPT).sql(DBConstants.Item.TYPE_ID + " IN (")
				.intArray(ItemTypeRegistry.getItemExtendersIds(itemDesc.getTypeId())).sql(")");
			
			// Если есть полнотекстовый поиск
			// Загрузка ID айтемов из Lucene индекса, добавление критерия ID найденных айтемов в основной запрос
			if (hasFulltext()) {
				loadFulltextIds();
				Long[] ids = fulltext.getLoadedIds(limit);
				// Если результат не найден, сразу вернуть пустое множество
				if (ids.length == 0 && (fulltext.getCompareType() == Compare.SOME || fulltext.getCompareType() == Compare.EVERY))
					return new ArrayList<Item>();
				dbQuery.getSubquery(WHERE_OPT).sql(" AND " + DBConstants.Item.ID + " IN (").longArray(ids).sql(")");
				order = ids;
// TODO <fix> удалить
//				ArrayList<Item> result = loadByIdsLong(idsToLoad, idsToLoad.toArray(new Long[0]), conn);
//				if (!hasParent)
//					return result;
//				// Если был задан предок - надо распределить найденные айтемы по предкам правильно
//				for (Item item : result) {
//					for (Long parentId : parents.get(item.getId())) {
//						if (loadedIds.contains(parentId)) {
//							item.setContextParentId(parentId);
//							break;
//						}
//					} 
//				}
//				return result;
			}

			// Применение количественных ограничений и критерия предка (если несколько предков - для каждого предка в отдельности)
			if (hasLimit() && limit.getLimit() > 0 && hasParent && loadedIds.size() > 1) {
				TemplateQuery union = new TemplateQuery("UNION");
				union.sql("(");
				Iterator<Long> iter = loadedIds.iterator();
				int i = 0;
				while (iter.hasNext()) {
					TemplateQuery baseClone = (TemplateQuery)dbQuery.createClone();
					ArrayList<Long> singleIdArray = new ArrayList<Long>(1);
					singleIdArray.add(iter.next());
					// Заполнение критериев родительского элемента
					createCommonParentCriteria(baseClone, singleIdArray);
					// Установка критерия ограничения количества
					limit.appendQuery(baseClone);
					// Добавляется очередная часть UNIONа и замещается сгенерированным запросом
					String subqueryName = "U" + i;
					union.subquery(subqueryName).getSubquery(subqueryName).replace(baseClone);
					if (iter.hasNext())
						union.sql(") UNION ALL (");
					i++;
				}
				// Нужно восстановить LoadedIds
				union.sql(")");
				dbQuery.replace(union);
			} else {
				createCommonParentCriteria(dbQuery, loadedIds);
				// Установка критерия ограничения количества
				if (hasLimit())
					limit.appendQuery(dbQuery);
			}
			return loadByQuery(dbQuery, PARENT_ID_COL, order, conn);
		} finally {
			MysqlConnector.closeStatement(pstmt);
			if (isOwnConnection) MysqlConnector.closeConnection(connection);
		}
	}
	/**
	 * Загрузить первый из айтемов, соответствующих установленным критериям запроса
	 * @return
	 * @throws Exception
	 */
	public Item loadFirstItem() throws Exception {
		List<Item> all = loadItems();
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
		ArrayList<Item> result = loadByQuery(query, "PID", null, conn);
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
	public static ArrayList<Item> loadByParentId(long id, byte assocId, Connection... conn) throws Exception {
		TemplateQuery query = new TemplateQuery("load by parent");
		query.SELECT(ITEM_TBL + ".*", IP_PARENT_ID).FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_CHILD_ID)
				.WHERE().col(IP_PARENT_ID).long_(id).AND().col(IP_PARENT_DIRECT).byte_((byte)1)
				.AND().col(IP_ASSOC_ID).byte_(assocId);
		return loadByQuery(query, IP_PARENT_ID, null, conn);
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
				.FROM(ITEM_TBL).WHERE().col(I_ID, " IN").longIN(ids.toArray(new Long[ids.size()]));
		return loadByQuery(query, "PID", order, conn);
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
		idsSelect.SELECT(UK_ID).FROM(UNIQUE_KEY_TBL).WHERE().col(UK_KEY, " IN").stringIN(keys.toArray(new String[0]));
		boolean isOwnConnection = false;
		Connection connection = null;
		if (conn == null || conn.length == 0) {
			isOwnConnection = true;
			connection = MysqlConnector.getConnection();
		} else {
			connection = conn[0];
		}
		ArrayList<Long> ids = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
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
			query.AND().col(II_ITEM_TYPE, " IN").intIN(extenders);
		}
		return loadByQuery(query, "PID", null, conn);
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
		ItemQuery query = newItemQuery(itemName);
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
		return itemDesc;
	}
	/**
	 * Загрузить по готовому запросу
	 * @param query
	 * @param parentIdCol
	 * @param conn
	 * @param order - опциональный параметр, который хранит порядок следования айтемов в результате
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<Item> loadByQuery(TemplateQuery query, String parentIdCol, byte assocId, Long[] order, Connection... conn) throws Exception {
		boolean isOwnConnection = false;
		Connection connection = null;
		if (conn == null || conn.length == 0) {
			isOwnConnection = true;
			connection = MysqlConnector.getConnection();
		} else {
			connection = conn[0];
		}
		ArrayList<Item> result = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			pstmt = query.prepareQuery(connection);
			ResultSet rs = pstmt.executeQuery();
			if (order == null) {
				while (rs.next())
					result.add(ItemMapper.buildItem(rs, assocId, parentIdCol));
			} else {
				MultiValuedMap<Long, Item> items = MultiMapUtils.newListValuedHashMap();
				//HashMap<Long, Item> items = new HashMap<Long, Item>();
				while (rs.next()) {
					Item item = ItemMapper.buildItem(rs, assocId, parentIdCol);
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
		return result;
	}
	/**
	 * Загружает отображение PARENT_ID => (ITEM_ID, в порядке сортировки) всех айтемов в случае если есть критерии фильтрации
	 * При этом извлекаются все ID айтемов, вне зависимости, есть ли ограничения (limit и страницы) или нет.
	 * После загрузки всех ID айтемов применяется критерий ограничения и страницы
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private LinkedHashMap<Long, List<Long>> loadFilteredIds(Connection conn) throws SQLException, IOException {
		// Создать запрос из шаблона для фильтра
		TemplateQuery dbQuery = TemplateQuery.createFromString(FILTER_IDS_SELECT_QUERY, queryType.toString() + " FILTER " + itemDesc.getName());
		// Проверка, нужно ли использовать родительский критерий
		if (hasParent)
			filter.useParentCriteria();
		// Заполнение критериев фильтрации (параметров)
		filter.appendQuery(dbQuery);
		// Установка критерия типа айтема (иногда может не понадобиться, когда параметры фильтрации уникальны для типа айтема)
		TemplateQuery typeCrit = dbQuery.getSubquery(WHERE_OPT).getSubquery(FILTER_CRITS_OPT).getSubquery(TYPE_CRIT_OPT);
		if (typeCrit != null)
			typeCrit.sql(" IN (").intArray(ItemTypeRegistry.getItemExtendersIds(itemDesc.getTypeId())).sql(")");
		// Подстановка нужной колонки для извлечения ID айтема
		dbQuery.getSubquery(ITEM_ID_REQ).sql(filter.getIdColoumnName());
		// Установка полнотекстового критерия, если он есть
		if (hasFulltext()) {
			loadFulltextIds();
			dbQuery.getSubquery(WHERE_OPT).getSubquery(FILTER_CRITS_OPT).sql(" AND ").sql(filter.getIdColoumnName())
				.sql(" IN (").longArray(fulltext.getLoadedIds()).sql(")");
		}
		// Применение количественных ограничений (если несколько предков - для каждого предка в отдельности)
		createFilterParentCriteria(dbQuery, loadedIds);
		
		// Выполнение запроса к БД
		LinkedHashMap<Long, List<Long>> result = new LinkedHashMap<Long, List<Long>>();
		HashMap<Long, Long> itemParents = new HashMap<Long, Long>();
		boolean needFulltextSorting = hasFulltext() && !filter.hasSorting();
		PreparedStatement pstmt = null;
		try {
			pstmt = dbQuery.prepareQuery(conn);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				long parentId = rs.getLong(2);
				if (!hasParent) parentId = 0;
				long itemId = rs.getLong(1);
				if (needFulltextSorting) {
					itemParents.put(itemId, parentId);
				} else {
					List<Long> children = result.get(parentId);
					if (children == null) {
						children = new ArrayList<Long>();
						result.put(parentId, children);
					}
					children.add(itemId);
				}
			}
			rs.close();
			queryFinished(conn);
		} finally {
			if (pstmt != null) pstmt.close();
		}
		
		// Изменение порядка следования в случае если был полнотекстовый поиск
		// и не было сортировки - расстановка по релевантности
		if (needFulltextSorting) {
			for (Long itemId : fulltext.getLoadedIds()) {
				// Не все результаты полнотекстового поиска будут соответствовать результатам поиска по фильтру
				// (для этого он и нужен), поэтому надо проверять
				if (itemParents.containsKey(itemId)) {
					long parentId = itemParents.get(itemId);
					List<Long> children = result.get(parentId);
					if (children == null) {
						children = new ArrayList<Long>();
						result.put(parentId, children);
					}
					children.add(itemId);
				}
			}
		}
		
		// Применение ограничения (limit) и страницы
		if (hasLimit() && limit.getLimit() > 0) {
			int startIndex = (limit.getPage() - 1) * limit.getLimit();
			int endIndex = limit.getPage() * limit.getLimit();
			for (Entry<Long, List<Long>> entry : result.entrySet()) {
				List<Long> itemIds = entry.getValue();
				if (itemIds.size() <= startIndex) {
					entry.setValue(new ArrayList<Long>(0));
				} else {
					entry.setValue(itemIds.subList(startIndex, endIndex > itemIds.size() ? itemIds.size() : endIndex));
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
	public HashMap<Long, Integer> loadTotalQuantities(Connection... conn) throws SQLException, NamingException, IOException, EcommanderException {
		if (isEmptySet())
			return new HashMap<Long, Integer>();
		// Создать запрос из шаблона для фильтра
		TemplateQuery dbQuery = TemplateQuery.createFromString(QUANTITY_SELECT_QUERY, queryType.toString() + " QUANTITY " + itemDesc.getName());
		if (hasFilter()) {
			// Проверка, нужно ли использовать родительский критерий
			if (hasParent)
				filter.useParentCriteria();
			// Заполнение критериев фильтрации (параметров)
			filter.appendQuery(dbQuery);
			// Установка критерия типа айтема (иногда может не понадобиться, когда параметры фильтрации уникальны для типа айтема)
			TemplateQuery typeCrit = dbQuery.getSubquery(WHERE_OPT).getSubquery(FILTER_CRITS_OPT).getSubquery(TYPE_CRIT_OPT);
			if (typeCrit != null)
				typeCrit.sql(" IN (").intArray(ItemTypeRegistry.getItemExtendersIds(itemDesc.getTypeId())).sql(")");
			// Подстановка нужной колонки для извлечения ID айтема
			dbQuery.getSubquery(ITEM_ID_REQ).sql(filter.getIdColoumnName());
			// Установка критерия предка
			createFilterParentCriteria(dbQuery, loadedIds);
			// Если был полнотектовый поиск
			if (hasFulltext()) {
				loadFulltextIds();
				dbQuery.getSubquery(WHERE_OPT).getSubquery(FILTER_CRITS_OPT)
					.sql(" AND " + filter.getIdColoumnName() + " IN (").longArray(fulltext.getLoadedIds()).sql(")");
			}
		} else {
			// Подстановка главной таблицы айтема
			dbQuery.getSubquery(FROM_OPT).sql(DBConstants.Item.TABLE);
			// Установка критерия типа айтема
			dbQuery.getSubquery(WHERE_OPT).sql(DBConstants.Item.TYPE_ID + " IN (")
				.intArray(ItemTypeRegistry.getItemExtendersIds(itemDesc.getTypeId())).sql(")");
			// Подстановка нужной колонки для извлечения ID айтема
			dbQuery.getSubquery(ITEM_ID_REQ).sql(DBConstants.Item.ID);
			// Установка критерия предка
			createCommonParentCriteria(dbQuery, loadedIds);
			// Если был полнотектовый поиск
			if (hasFulltext()) {
				loadFulltextIds();
				dbQuery.getSubquery(WHERE_OPT)
					.sql(" AND " + DBConstants.Item.ID + " IN (").longArray(fulltext.getLoadedIds()).sql(")");
			}
		}
		// Использовать группировку по родительскому ID только в случае, если есть родитель
		if (hasParent)
			dbQuery.getSubquery(WHERE_OPT).sql(" GROUP BY PID");
		// Выполнение запроса к БД
		HashMap<Long, Integer> result = new HashMap<Long, Integer>();
		boolean isOwnConnection = false;
		Connection connection = null;
		if (conn == null || conn.length == 0) {
			isOwnConnection = true;
			connection = MysqlConnector.getConnection();
		} else {
			connection = conn[0];
		}
		PreparedStatement pstmt = null;
		try {
			pstmt = dbQuery.prepareQuery(connection);
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
	 * Загрузить группу айтемов
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 */
	private ArrayList<Item> loadGroupedItems(Connection conn) throws SQLException, IOException {
		// Создать запрос из шаблона для фильтра
		TemplateQuery dbQuery = TemplateQuery.createFromString(GROUP_VALS_SELECT_QUERY, queryType.toString() + " GROUP " + itemDesc.getName());
		// Проверка, нужно ли использовать родительский критерий
		if (hasParent) {
			filter.useParentCriteria();
			dbQuery.getSubquery(GROUP_PARAM_REQ).sql(PARENT_ID_COL);
		}
		// Заполнение критериев фильтрации (параметров)
		filter.appendQuery(dbQuery);
		// Установка критерия типа айтема (иногда может не понадобиться, когда параметры фильтрации уникальны для типа айтема)
		TemplateQuery typeCrit = dbQuery.getSubquery(WHERE_OPT).getSubquery(FILTER_CRITS_OPT).getSubquery(TYPE_CRIT_OPT);
		if (typeCrit != null)
			typeCrit.sql(" IN (").intArray(ItemTypeRegistry.getItemExtendersIds(itemDesc.getTypeId())).sql(")");
		// Установка критерия предка
		createFilterParentCriteria(dbQuery, loadedIds);
		// Если был полнотекстовый поиск - установка списка найденных ID айтемов
		if (hasFulltext()) {
			loadFulltextIds();
			dbQuery.getSubquery(WHERE_OPT).getSubquery(FILTER_CRITS_OPT).sql(" AND ")
					.sql(GROUP_MAIN_TABLE + "." + DBConstants.ItemIndexes.REF_ID)
					.sql(" IN (").longArray(fulltext.getLoadedIds()).sql(")");
		}
		// Выполнение запроса к БД
		ArrayList<Item> result = new ArrayList<Item>();
		PreparedStatement pstmt = null;
		try {
			pstmt = dbQuery.prepareQuery(conn);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Item item = Item.existingItem(itemDesc, -1, rs.getLong(PARENT_COL), "", -1, 0, 0, 0, "", "", "", 0);
				ParameterDescription aggParam = filter.getMainAggregationCriteria().getParam();
				item.setValue(aggParam.getId(), DataTypeMapper.createValue(aggParam.getType(), rs, MAIN_COL));
				if (filter.hasAggregationGroupByParams()) {
					for (int i = 0; i < filter.getAggregationCriterias().size(); i++) {
						ParameterDescription desc = filter.getAggregationCriterias().get(i).getParam();
						item.setValue(desc.getId(), DataTypeMapper.createValue(desc.getType(), rs, i + ADDITIONAL_COL));
					}
				}
				result.add(item);
			}
			rs.close();
			queryFinished(conn);
		} finally {
			if (pstmt != null) pstmt.close();
		}
		return result;
	}
	
	public static void queryFinished(Connection conn) {
		if (conn instanceof ConnectionCount)
			((ConnectionCount) conn).queryFinished();
	}
}
