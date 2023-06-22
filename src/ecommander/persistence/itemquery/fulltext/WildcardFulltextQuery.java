package ecommander.persistence.itemquery.fulltext;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

public class WildcardFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		Query query = parser.createBooleanQuery(param, value, Occur.MUST);
		if (query == null && !StringUtils.isBlank(value)) {
			query = new WildcardQuery(new Term(param, '*' + value + '*'));
		} else if (query instanceof BooleanQuery) {
			BooleanQuery boolQuery = (BooleanQuery) query;
			for (BooleanClause clause : boolQuery.getClauses()) {
				Query subquery = clause.getQuery();
				clause.setOccur(occur);
				if (subquery instanceof TermQuery) {
					clause.setQuery(new WildcardQuery(((TermQuery) subquery).getTerm()));
				}
			}
		} else if (query instanceof TermQuery) {
			query = new WildcardQuery(new Term(param, '*' + value + '*'));
		}
		return query;
	}
}
