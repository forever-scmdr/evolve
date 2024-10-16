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

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		Query query = parser.createBooleanQuery(param, value);
		if (query == null && !StringUtils.isBlank(value)) {
			query = createTermPrefixQuery(new Term(param, value));
		} else if (query instanceof BooleanQuery) {
			BooleanQuery boolQuery = (BooleanQuery) query;
			BooleanQuery.Builder resultBuilder = new BooleanQuery.Builder();
			for (BooleanClause clause : boolQuery.clauses()) {
				Query subquery = clause.getQuery();
				if (subquery instanceof TermQuery) {
					resultBuilder.add(new BooleanClause(createTermPrefixQuery(((TermQuery) subquery).getTerm()), occur));
				} else {
					resultBuilder.add(subquery, occur);
				}
			}
			query = resultBuilder.build();
		} else if (query instanceof TermQuery) {
			query = createTermPrefixQuery(((TermQuery) query).getTerm());
		}
		return query;
	}

	public static BooleanQuery createTermPrefixQuery(Term term) {
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(new PrefixQuery(term), Occur.SHOULD);
		builder.add(new TermQuery(term), Occur.SHOULD);
		return builder.build();
	}
}
