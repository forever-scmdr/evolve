package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

public class BelchipTermNoSplitFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, BooleanClause.Occur occur) {
		return parser.createBooleanQuery(param, BelchipStrings.fromRtoE(value), occur);
	}

}