package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;

public class WildcardBelchipFulltextQueryKey extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		value = BelchipStrings.preanalyze(BelchipStrings.fromRToEKeyboard(value));
		Query query = parser.createBooleanQuery(param, value, occur);
		if (query == null && !StringUtils.isBlank(value)) {
			query = createWildcardQuery(new Term(param, value));
		} else if (query instanceof BooleanQuery) {
			BooleanQuery boolQuery = (BooleanQuery) query;
			BooleanQuery.Builder resultBuilder = new BooleanQuery.Builder();
			for (BooleanClause clause : boolQuery.clauses()) {
				Query subquery = clause.getQuery();
				if (subquery instanceof TermQuery) {
					resultBuilder.add(new BooleanClause(createWildcardQuery(((TermQuery) subquery).getTerm()), occur));
				}
			}
			query = resultBuilder.build();
		} else if (query instanceof TermQuery) {
			query = createWildcardQuery(((TermQuery) query).getTerm());
		}
		return query;
	}
	
	private static WildcardQuery createWildcardQuery(Term term) {
		return new WildcardQuery(new Term(term.field(), '*' + term.text() + '*'));
	}

}
