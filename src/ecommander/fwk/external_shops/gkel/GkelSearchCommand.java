package ecommander.fwk.external_shops.gkel;

import ecommander.fwk.Strings;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class GkelSearchCommand extends Command {
	private static final String GKEL_URL = "https://gkel.ru/efind_full_db.php?zapros=";
	private static final String ENCODING = "Cp1251";

	@Override
	public ResultPE execute() throws Exception {
		String query = getVarSingleValue("q");
		if(StringUtils.isBlank(query)){
			setPageVariable("error", "empty_query");
			return getResult("result");
		}
		query = URLEncoder.encode(query, ENCODING);
		URL url = new URL(GKEL_URL + query);

		StringBuilder sb = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), ENCODING))){
			String line;
			boolean useful = false;
			while ((line = reader.readLine()) != null){
				if(!useful){
					useful = line.trim().equalsIgnoreCase("<item>");
				}
				if(!useful) continue;

				if(StringUtils.startsWith(line, "<part>")){
					String code = line.replace("<part>","").replace("</part>","");
					code = Strings.translit(code);
					sb.append("<code>"+code+"</code>");
				}

				sb.append(escapeLine(line));
				useful = !line.equalsIgnoreCase("</item>");
			}
		}catch (Exception e){
			setPageVariable("error", "connection_error");
			return getResult("result");
		}
		ResultPE result = getResult("result");
		result.setValue(sb.toString());
		return result;
	}

	private String escapeLine(String line){
		if(StringUtils.startsWith(line,"<") && StringUtils.endsWith(line,">") && line.indexOf("</") > 0){
			String openTagEnd = ">";
			String closeTagStart = "</";

			String content = StringUtils.substringAfter(line, openTagEnd);
			content = StringUtils.substringBeforeLast(content,closeTagStart);
			content = StringEscapeUtils.escapeXml10(content);
			String tag = StringUtils.substringBefore(line, openTagEnd)+openTagEnd;
			String tagEnd = closeTagStart+ StringUtils.substringAfterLast(line,closeTagStart);
			line = tag+content+tagEnd;
		}
		return StringUtils.normalizeSpace(line);
	}
}
