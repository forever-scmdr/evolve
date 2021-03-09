package extra.belchip;

import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.Query;

public class BelchipTermFulltextQueryKey extends LuceneQueryCreator {
	@Override
	protected Query createQuery(QueryParser parser, String param, String value, Occur occur) {
		return parser.createBooleanQuery(param, BelchipStrings.preanalyze(BelchipStrings.fromRToEKeyboard(value)), occur);
	}

}
