package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import java.util.ArrayList;

public class StrictPrefixBelchipFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		value = BelchipStrings.preanalyzeNoSplit(value);
		Query parsedQuery = parser.createPhraseQuery(param, value);
		if (parsedQuery instanceof PhraseQuery) {
			PhraseQuery phraseQuery = (PhraseQuery) parsedQuery;
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (Term term : phraseQuery.getTerms()) {
				builder.add(createWildcardQuery(term), Occur.MUST);
			}
			return builder.build();
		} else if (parsedQuery instanceof TermQuery) {
			return createWildcardQuery(new Term(param, value));
		}
		return null;
	}
	
	protected WildcardQuery createWildcardQuery(Term term) {
		return new WildcardQuery(new Term(term.field(), term.text() + '*'));
	}

	@Override
	protected QueryParser createQueryParser(String paramName) {
		return new QueryParser(paramName, LuceneIndexMapper.getKyewordAnalyzer());
	}

}
