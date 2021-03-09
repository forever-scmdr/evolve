package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;

public class PrefixBelchipFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		value = BelchipStrings.preanalyze(value);
		Query query = parser.createBooleanQuery(param, value, occur);
		if (query == null && !StringUtils.isBlank(value)) {
			query = new PrefixQuery(new Term(param, value));
		} else if (query instanceof BooleanQuery) {
			BooleanQuery boolQuery = (BooleanQuery) query;
			BooleanQuery.Builder resultBuilder = new BooleanQuery.Builder();
			for (BooleanClause clause : boolQuery.clauses()) {
				Query subquery = clause.getQuery();
				if (subquery instanceof TermQuery) {
					resultBuilder.add(new BooleanClause(createTermPrefixQuery(((TermQuery) subquery).getTerm()), occur));
				}
			}
			query = resultBuilder.build();
		} else if (query instanceof TermQuery) {
			query = new PrefixQuery(((TermQuery) query).getTerm());
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
