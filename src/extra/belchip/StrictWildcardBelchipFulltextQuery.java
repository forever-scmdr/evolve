package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;

public class StrictWildcardBelchipFulltextQuery extends StrictPrefixBelchipFulltextQuery {

	protected WildcardQuery createWildcardQuery(Term term) {
		return new WildcardQuery(new Term(term.field(), '*' + term.text() + '*'));
	}

}
