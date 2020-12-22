package ecommander.persistence.itemquery.fulltext;

import ecommander.fwk.ServerLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

public class ParsedQuery extends LuceneQueryCreator {

	private static final String ESCAPE_CHARS = "-!(){}[]^\"~:\\";

	protected String escapeInput(String input) {
		StringBuilder sb = new StringBuilder();
		input = StringUtils.lowerCase(input);
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (StringUtils.contains(ESCAPE_CHARS, c))
				sb.append("\\");
			sb.append(c);
		}
		return sb.toString();
	}

	@Override
	protected Query createQuery(QueryParser parser, String param, String value, BooleanClause.Occur occur) {
		try {
			return parser.parse(param + ":" + escapeInput(value));
		} catch (ParseException e) {
			ServerLogger.error("Error parsing Lucene query", e);
			return parser.createBooleanQuery(param, value, occur);
		}
	}
}
