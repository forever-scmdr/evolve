package ecommander.persistence.itemquery.fulltext;

import java.util.ArrayList;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
/**
 * Совпадине с запросом по первым словам текста
 * @author E
 *
 */
public class FirstFulltextQuery extends LuceneQueryCreator {
	
	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		Query parsedQuery = parser.createPhraseQuery(param, value);
		if (parsedQuery instanceof PhraseQuery) {
			ArrayList<SpanQuery> parts = new ArrayList<SpanQuery>(4);
			PhraseQuery phraseQuery = (PhraseQuery) parsedQuery;
			Term[] terms = phraseQuery.getTerms();
			for (Term term : terms) {
				parts.add(new SpanTermQuery(term));
			}
			SpanQuery baseQuery = new SpanNearQuery(parts.toArray(new SpanQuery[0]), 1, false);
			return new SpanFirstQuery(baseQuery, terms.length + 1);
		} else if (parsedQuery instanceof TermQuery) {
			return parsedQuery;
		}
		return null;
	}
}
