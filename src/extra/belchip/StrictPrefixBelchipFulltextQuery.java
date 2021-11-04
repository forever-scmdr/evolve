package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;

public class StrictPrefixBelchipFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		value = BelchipStrings.preanalyzeNoSplit(value);
		return createWildcardQuery(new Term(param, value));
	}
	
	private static WildcardQuery createWildcardQuery(Term term) {
		return new WildcardQuery(new Term(term.field(), term.text() + '*'));
	}

}
