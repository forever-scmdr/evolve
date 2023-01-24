package extra;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

	private HashMap<String, String> replacements = new HashMap<>();

	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue(ExecutablePagePE.PAGEURL_VALUE);
		String pageName = getVarSingleValue(ExecutablePagePE.PAGENAME_VALUE);
		String apiBase = getVarSingleValue("api_base");
		String token = getVarSingleValue("token");
		String tokenHeaderName = getVarSingleValue("token_header");
		String rootElementName = getVarSingleValueDefault("root_element", "provider");
		String urlToGet = apiBase + StringUtils.substringAfterLast(url, pageName + "/");

		String replace = getVarSingleValueDefault("replace", "");
		String[] replacePairs = StringUtils.split(replace, ", ");
		for (String replacePair : replacePairs) {
			String[] keyValue = StringUtils.split(replacePair, ":=");
			if (keyValue.length == 2)
				replacements.put(keyValue[0], keyValue[1]);
		}
		try {
			//String response = OkWebClient.getInstance().getString(urlToGet);
			String response = OkWebClient.getInstance().getStringHeaders(urlToGet, tokenHeaderName, token);
			JsonElement element = JsonParser.parseString(response);
			XmlDocumentBuilder xml = XmlDocumentBuilder.newDoc();
			processElement(element, xml, rootElementName);
			return getResult("success").setValue(xml.toString());
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return getResult("error").setValue(sw.toString());
		}
	}

	private void processElement(JsonElement element, XmlDocumentBuilder xml, String rootElement) {
		String tagToCreate = replacements.getOrDefault(rootElement, rootElement);
		if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			for (JsonElement arrayItem : array) {
				processElement(arrayItem, xml, rootElement);
			}
		} else if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			Map<String, JsonElement> map = object.asMap();
			xml.startElement(tagToCreate);
			for (String key : map.keySet()) {
				processElement(map.get(key), xml, key);
			}
			xml.endElement();
		} else if (element.isJsonPrimitive()) {
			xml.addElement(tagToCreate, element.getAsString());
		} else if (element.isJsonNull()) {
			xml.addElement(tagToCreate, "");
		}
	}
}
