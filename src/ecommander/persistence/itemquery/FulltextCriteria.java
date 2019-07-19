package ecommander.persistence.itemquery;

import ecommander.fwk.Pair;
import ecommander.model.Compare;
import ecommander.persistence.itemquery.fulltext.FulltextQueryCreatorRegistry;
import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Полнотекстовый критерий
 * Метод appendLuceneQuery требует реализации.
 * Класс предусматирвает алгоритм поиска по умолчанию - результат запроса, созданного в методе appendLuceneQuery
 * Для реализации других алгоритмов поиска требудется переопределить метод loadItems
 * (например, если первоначальный запрос не вернул результатов, применить более мягкие критерии)
 * 
 * @author E
 *
 */
class FulltextCriteria {

	public static final String HIGHLIGHT_EXTRA_NAME = "highlight";
	public static final String QUERY = "query";

	private final String[] paramNames;
	private final String[] queryVals;
	private final int maxResultCount;
	private final float threshold;
	private LinkedHashMap<Long, ArrayList<Pair<String, String>>> loadedIdsAndQueryAndHighlight = null;
	private final Compare compType;
	private ArrayList<ArrayList<LuceneQueryCreator>> queryCreators = new ArrayList<>();
	
	FulltextCriteria(List<String[]> queryTypes, String[] queryStr, int maxResultCount, String[] paramNames, Compare compType,
	                 float threshold) throws Exception {
		this.queryVals = queryStr;
		this.paramNames = paramNames;
		this.maxResultCount = maxResultCount;
		this.threshold = threshold;
		this.compType = compType;
		for (String[] typeGroup : queryTypes) {
			ArrayList<LuceneQueryCreator> group = new ArrayList<>();
			queryCreators.add(group);
			for (String type : typeGroup) {
				group.add(FulltextQueryCreatorRegistry.getCriteria(type));
			}
		}
	}

	String[] getParamNames() {
		return paramNames;
	}
	
	boolean isValid() {
		return queryVals.length > 0 && !StringUtils.isBlank(queryVals[0]) && paramNames != null && paramNames.length >= 1;
	}
	/**
	 * Загрузить ID всех айтемов, которые подходят по полнотекстовому запросу
	 * Этот метод может быть переопределен в подклассах для реализации других алгоритмов поиска
	 * 
	 * @throws IOException
	 */
	void loadItems(Query filter) throws IOException {
		loadedIdsAndQueryAndHighlight = new LinkedHashMap<>(0);
		if (isValid()) {
			Occur occur = Occur.SHOULD;
			if (compType == Compare.EVERY || compType == Compare.ALL)
				occur = Occur.MUST;
			//if (queryVals.length == 1) {
			for (String queryVal : queryVals) {
				ArrayList<ArrayList<Query>> queries = new ArrayList<>();
				for (ArrayList<LuceneQueryCreator> creatorGroup : queryCreators) {
					ArrayList<Query> queryGroup = new ArrayList<>();
					queries.add(queryGroup);
					for (LuceneQueryCreator creator : creatorGroup) {
						Query query = creator.createLuceneQuery(queryVal, paramNames, occur);
						if (query != null)
							queryGroup.add(query);
					}
				}
				if (!queries.isEmpty()) {
					LinkedHashMap<Long, String> loaded = LuceneIndexMapper.getSingleton().getItems(queries, filter, paramNames, maxResultCount, threshold);
					for (Long loadedId : loaded.keySet()) {
						ArrayList<Pair<String, String>> queryAndHighlight = loadedIdsAndQueryAndHighlight.get(loadedId);
						if (queryAndHighlight == null) {
							queryAndHighlight = new ArrayList<>();
							loadedIdsAndQueryAndHighlight.put(loadedId, queryAndHighlight);
						}
						queryAndHighlight.add(new Pair<>(queryVal, loaded.get(loadedId)));
					}
				}
			}
		}
	}
	/**
	 * Получить часть массива найденных ID
	 * Если нужны все найденные ID, то передавать в качестве первого аргумента (limit) 0
	 * @param limit - количество найденных элементов
	 * @return
	 */
	Long[] getLoadedIds(LimitCriteria limit) {
		if (limit != null && limit.getLimit() > 0) {
			int start = (limit.getPage() - 1) * limit.getLimit();
			int end = limit.getPage() * limit.getLimit();
			ArrayList<Long> ids = new ArrayList<>();
			Iterator<Long> loadedIter = loadedIdsAndQueryAndHighlight.keySet().iterator();
			for (int i = 0; i < start; i++) {
				if (loadedIter.hasNext())
					loadedIter.next();
			}
			for (int i = start; i <= end; i++) {
				if (loadedIter.hasNext()) {
					Long itemId = loadedIter.next();
					ids.add(itemId);
				}
			}
			return ids.toArray(new Long[0]);
		}
		return loadedIdsAndQueryAndHighlight.keySet().toArray(new Long[0]);
	}

	/**
	 * Вернуть только ID найденных айтемов (без подсвеченного текста)
	 * @return
	 */
	Long[] getLoadedIds() {
		return loadedIdsAndQueryAndHighlight.keySet().toArray(new Long[0]);
	}

	/**
	 * Вернуть текст с подсветкой совпадающих с запросом фрагментов для заданного айтема
	 * @param itemId - ID найденного полнотекстовым поиском айтема
	 * @return
	 */
	ArrayList<Pair<String,String>> getQueryAndHighlightedText(Long itemId) {
		return loadedIdsAndQueryAndHighlight.get(itemId);
	}

	boolean isLoaded() {
		return loadedIdsAndQueryAndHighlight != null;
	}
	
	Compare getCompareType() {
		return compType;
	}
}
