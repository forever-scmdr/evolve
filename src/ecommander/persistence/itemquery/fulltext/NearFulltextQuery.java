package ecommander.persistence.itemquery.fulltext;

import java.util.ArrayList;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
/**
 * Простой критерий. Не префиксный (полное совпадение с учетом стемминга)
 * @author E
 *
 */
public class NearFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		Query parsedQuery = parser.createPhraseQuery(param, value);
		ArrayList<SpanQuery> parts = new ArrayList<SpanQuery>(4);
		if (parsedQuery instanceof PhraseQuery) {
			PhraseQuery phraseQuery = (PhraseQuery) parsedQuery;
			for (Term term : phraseQuery.getTerms()) {
				parts.add(new SpanTermQuery(term));
			}
		} else if (parsedQuery instanceof TermQuery) {
			return parsedQuery;
		}
		return new SpanNearQuery(parts.toArray(new SpanQuery[0]), 1, false);
	}

}
