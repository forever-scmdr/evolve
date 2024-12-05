package extra;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.OkWebClient;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ApiJsonToXMLCommand extends Command {


	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue(ExecutablePagePE.PAGEURL_VALUE);
		String pageName = getVarSingleValue(ExecutablePagePE.PAGENAME_VALUE);
		String apiBase = getVarSingleValue("api_base");
		String token = getVarSingleValue("token");
		String tokenHeaderName = getVarSingleValue("token_header");
		String rootElementName = getVarSingleValueDefault("root_element", "provider");
		String toAdd = StringUtils.substringAfterLast(url, pageName);
		toAdd = StringUtils.startsWith(toAdd, "/") ? StringUtils.substringAfter(toAdd, "/") : toAdd;
		toAdd = StringUtils.startsWith(toAdd, "?") ? StringUtils.substringAfter(toAdd, "?") : toAdd;
		String urlToGet = apiBase + toAdd;

		String replace = getVarSingleValueDefault("replace", "");
		String[] replacePairs = StringUtils.split(replace, ", ");
		HashMap<String, String> replacements = new HashMap<>();
		for (String replacePair : replacePairs) {
			String[] keyValue = StringUtils.split(replacePair, ":=");
			if (keyValue.length == 2)
				replacements.put(keyValue[0], keyValue[1]);
		}
		try {
			//String response = OkWebClient.getInstance().getString(urlToGet);
			//OkWebClient.getInstance().setProxy("91.235.136.192", 3128, "sankon", "yNUtRh6mp2hc");
			String response = OkWebClient.getInstance().getStringHeaders(urlToGet, tokenHeaderName, token);
			String xml = JsoupUtils.transformJsonToXml(response, rootElementName, replacements);
			return getResult("success").setValue(xml);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return getResult("error").setValue(sw.toString());
		}
	}

}
