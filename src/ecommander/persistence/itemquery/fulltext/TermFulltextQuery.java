package ecommander.persistence.itemquery.fulltext;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;

public class TermFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		return parser.createBooleanQuery(param, value, occur);
	}

}
