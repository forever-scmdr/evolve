package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.persistence.itemquery.fulltext.FulltextQueryCreatorRegistry;
import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.util.ArrayList;
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
	
	private final String[] paramNames;
	private final String[] queryVals;
	private final int maxResultCount;
	private final float threshold;
	private Long[] loadedIds = null;
	private final Compare compType;
	private ArrayList<LuceneQueryCreator> queryCreators = new ArrayList<>();
	
	FulltextCriteria(String[] queryTypes, String[] queryStr, int maxResultCount, String[] paramNames, Compare compType,
			float threshold) throws Exception {
		this.queryVals = queryStr;
		this.paramNames = paramNames;
		this.maxResultCount = maxResultCount;
		this.threshold = threshold;
		this.compType = compType;
		for (String type : queryTypes) {
			queryCreators.add(FulltextQueryCreatorRegistry.getCriteria(type));
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
	void loadItems() throws IOException {
		if (isValid()) {
			ArrayList<Query> queries = new ArrayList<>();
			Occur occur = Occur.SHOULD;
			if (compType == Compare.EVERY || compType == Compare.ALL)
				occur = Occur.MUST;
			if (queryVals.length == 1) {
				for (LuceneQueryCreator creator : queryCreators) {
					Query query = creator.createLuceneQuery(queryVals[0], paramNames, occur);
					if (query != null)
						queries.add(query);
				}
			} else {
				for (LuceneQueryCreator creator : queryCreators) {
					BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
					boolean isEmpty = true;
					for (String term : queryVals) {
						Query part = creator.createLuceneQuery(term, paramNames, occur);
						if (part != null) {
							boolQuery.add(part, occur);
							isEmpty = false;
						}
					}
					if (!isEmpty)
						queries.add(boolQuery.build());
				}
			}
			if (!queries.isEmpty()) {
				loadedIds = LuceneIndexMapper.getItems(queries, maxResultCount, threshold);
				return;
			}
		}
		loadedIds = new Long[0];
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
			return ArrayUtils.subarray(loadedIds, start, end);
//			if (start >= loadedIds.length) // TODO <fix> delete
//				return new ArrayList<Long>(0);
//			else
//				return ArrayUtils.subarray(loadedIds, start, end);
		}
		return loadedIds;
	}
	
	Long[] getLoadedIds() {
		return loadedIds;
	}
	
	boolean isLoaded() {
		return loadedIds != null;
	}
	
	Compare getCompareType() {
		return compType;
	}
}
