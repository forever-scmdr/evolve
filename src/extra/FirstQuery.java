package extra;

import ecommander.persistence.itemquery.fulltext.ParsedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

public class FirstQuery extends ParsedQuery {


	@Override
	protected Query createQuery(QueryParser parser, String param, String value, BooleanClause.Occur occur) {
		return createQueryBase(parser, param, value, occur);
	}


	protected Query createQueryBase(QueryParser parser, String param, String input, BooleanClause.Occur occur) {
		input = escapeInput(input);
		Query query = parser.createBooleanQuery(param, input, BooleanClause.Occur.MUST);
		if (query == null && !StringUtils.isBlank(input)) {
			String queryStr = prepareSingleTermStr(input);
			if (!StringUtils.endsWith(queryStr, "*"))
				queryStr += "*";
			query = new WildcardQuery(new Term(param, queryStr));
		} else if (query instanceof BooleanQuery) {
			BooleanQuery boolQuery = (BooleanQuery) query;
			BooleanQuery.Builder resultBuilder = new BooleanQuery.Builder();
			for (BooleanClause clause : boolQuery.clauses()) {
				Query subquery = clause.getQuery();
				if (subquery instanceof TermQuery) {
					String queryStr = prepareSingleTermStr(((TermQuery) subquery).getTerm().text());
					if (!StringUtils.endsWith(queryStr, "*"))
						queryStr += "*";
					resultBuilder.add(new WildcardQuery(new Term(param, queryStr)), occur);
				} else {
					resultBuilder.add(subquery, occur);
				}
			}
		} else if (query instanceof TermQuery) {
			String queryStr = prepareSingleTermStr(input);
			if (!StringUtils.endsWith(queryStr, "*"))
				queryStr += "*";
			query = new WildcardQuery(new Term(param, queryStr));
		}
		return query;
	}

	protected String prepareSingleTermStr(String input) {
		return input;
	}
}
