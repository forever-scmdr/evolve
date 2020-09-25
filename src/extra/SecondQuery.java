package extra;

import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

public class SecondQuery extends FirstQuery {

	private String stopSymbols = null;

	protected String getStopSymbols() {
		if (stopSymbols == null) {
			try {
				Item common = ItemQuery.loadSingleItemByName("common");
				if (common != null) {
					stopSymbols = common.getStringValue("search_stop_chars", null);
				}
			} catch (Exception e) {
				ServerLogger.error("Error loading stop symbols for search query", e);
			}
			if (stopSymbols == null)
				stopSymbols = "";
		}
		return stopSymbols;
	}

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, BooleanClause.Occur occur) {
		getStopSymbols();
		value = StringUtils.replaceChars(value, stopSymbols, StringUtils.leftPad("", stopSymbols.length()));
		return createQueryBase(parser, param, value, occur);
	}
}
