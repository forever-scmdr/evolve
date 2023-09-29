package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.spans.*;

import java.util.ArrayList;

public class StrictPrefixBelchipFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		value = BelchipStrings.preanalyzeNoSplit(value);
		Query parsedQuery = parser.createPhraseQuery(param, value);
		if (parsedQuery instanceof PhraseQuery) {
			PhraseQuery phraseQuery = (PhraseQuery) parsedQuery;
			ArrayList<SpanQuery> parts = new ArrayList<>(4);
			for (Term term : phraseQuery.getTerms()) {
				SpanQuery sq = new SpanMultiTermQueryWrapper<>(createWildcardQuery(term));
				parts.add(sq);
			}
			return new SpanNearQuery(parts.toArray(new SpanQuery[0]), 1, true);
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
