package ecommander.persistence.itemquery.fulltext;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class TermPrefixFulltextQuery extends LuceneQueryCreator {

	public static final class TermPrefixQuery extends BooleanQuery {
		public TermPrefixQuery(Term term) {
			add(new PrefixQuery(term), Occur.SHOULD);
			add(new TermQuery(term), Occur.SHOULD);
		}
	}

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		Query query = parser.createBooleanQuery(param, value);
		if (query == null && !StringUtils.isBlank(value)) {
			query = new TermPrefixQuery(new Term(param, value));
		} else if (query instanceof BooleanQuery) {
			BooleanQuery boolQuery = (BooleanQuery) query;
			for (BooleanClause clause : boolQuery.getClauses()) {
				Query subquery = clause.getQuery();
				clause.setOccur(occur);
				if (subquery instanceof TermQuery) {
					clause.setQuery(new TermPrefixQuery(((TermQuery) subquery).getTerm()));
				}
			}
		} else if (query instanceof TermQuery) {
			query = new TermPrefixQuery(((TermQuery) query).getTerm());
		}
		return query;
	}
}
