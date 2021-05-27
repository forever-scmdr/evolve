package extra;

import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupXmlFixer {
	private static final String TAG = "<\\w+\\s+((\\w+=\"[^\"]+\"\\s*)+)\\/?>";
	private static final String ATTRIBUTE = "(?<name>\\w+)=\"(?<val>[^\"]+)\"";

	public static String fix(String in) {
		Pattern tagPattern = Pattern.compile(TAG);
		Pattern attrPattern = Pattern.compile(ATTRIBUTE);
		Matcher tagMatcher = tagPattern.matcher(in);
		while (tagMatcher.find()) {
			String tag = tagMatcher.group();
			Matcher attrMatcher = attrPattern.matcher(tag);
			while (attrMatcher.find()) {
				String value = attrMatcher.group("val");
				String fixedValue = StringEscapeUtils.escapeXml10(value);
				if (!value.equals(fixedValue)) {
					String name = attrMatcher.group("name");
					String all = attrMatcher.group();
					in = in.replace(all, name + '=' + '"' + fixedValue + '"');
				}
			}
		}
		return in;
	}
}
