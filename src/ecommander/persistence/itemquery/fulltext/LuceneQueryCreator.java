package ecommander.persistence.itemquery.fulltext;

import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

import java.util.ArrayList;

/**
 * Класс, который должен создавать запрос Lucene по имеющийся строке запроса и параметрам запроса
 * @author E
 *
 */
public abstract class LuceneQueryCreator {

	public Query createLuceneQuery(String queryStr, String[] paramNames, Occur occur) {
		if (paramNames.length == 1) {
			QueryParser parser = createQueryParser(paramNames[0]);
			return createQuery(parser, paramNames[0], queryStr, occur);
		}
		ArrayList<Query> queries = new ArrayList<>();
		for (String paramName : paramNames) {
			QueryParser parser = createQueryParser(paramName);
			queries.add(createQuery(parser, paramName, queryStr, occur));
		}
		return new DisjunctionMaxQuery(queries, 0.1f);
	}

	protected QueryParser createQueryParser(String paramName) {
		QueryParser parser = LuceneIndexMapper.createQueryParser(paramName);
		parser.setAllowLeadingWildcard(true);
		return parser;
	}

	protected abstract Query createQuery(QueryParser parser, String param, String value, Occur occur);
}
