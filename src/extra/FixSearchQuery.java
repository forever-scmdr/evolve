package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixSearchQuery extends Command {
	@Override
	public ResultPE execute() throws Exception {
		String query = getVarSingleValue("q");
		setPageVariable("q", null);
		setPageVariable("old_query", query);
		if(StringUtils.isNotBlank(query)) {
			query = processString(query);
		}else {
			query = "empty_search_query";
		}
		setPageVariable("q", null);
		setPageVariable("q", query);
		return null;
	}

	public static String processString(String arg) {
		if(StringUtils.isBlank(arg)) return "";
		String regexp = "(?<number>\\d+([.,/]\\d+)*)";
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(arg);
		String x;
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String n = m.group("number");
			m.appendReplacement(sb, " " + n + " ");
		}
		m.appendTail(sb);
		x = sb.toString();
		x = x.replaceAll("[+-]", "");
		x = x.replaceAll("\\(", "");
		x = x.replaceAll("\\)", "");
		x = x.replaceAll("\\s+", " ");
		x = x.trim();
		return x;
	}
}
