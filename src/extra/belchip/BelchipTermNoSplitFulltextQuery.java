package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.persistence.itemquery.fulltext.LuceneQueryCreator;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

import java.util.Locale;

public class BelchipTermNoSplitFulltextQuery extends LuceneQueryCreator {

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, BooleanClause.Occur occur) {
		Locale loc = AppContext.getCurrentLocale();
		if (loc == null)
			loc = new Locale("ru");
		return parser.createBooleanQuery(param, /*BelchipStrings.fromRtoE(value)*//*value*/BelchipStrings.fromRtoE(value).toLowerCase(loc), occur);
	}

}