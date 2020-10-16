package ecommander.persistence.itemquery.fulltext;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

import ecommander.persistence.mappers.LuceneIndexMapper;

import java.util.ArrayList;

/**
 * Класс, который должен создавать запрос Lucene по имеющийся строке запроса и параметрам запроса
 * @author E
 *
 */
public abstract class LuceneQueryCreator {

	public Query createLuceneQuery(String queryStr, String[] paramNames, Occur occur) {
		QueryParser parser = LuceneIndexMapper.createQueryParser(paramNames[0]);
		parser.setAllowLeadingWildcard(true);
		if (paramNames.length == 1) {
			return createQuery(parser, paramNames[0], queryStr, occur);
		}
		ArrayList<Query> queries = new ArrayList<>();
		for (String paramName : paramNames) {
			queries.add(createQuery(parser, paramName, queryStr, occur));
		}
		return new DisjunctionMaxQuery(queries, 0.1f);
	}
	
	protected abstract Query createQuery(QueryParser parser, String param, String value, Occur occur);
}
