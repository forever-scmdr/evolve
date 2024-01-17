package extra;

import ecommander.fwk.JsoupUtils;
import ecommander.fwk.OkWebClient;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ResultPE;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class DadataQueryCommand extends Command {


	@Override
	public ResultPE execute() throws Exception {
		String apiBase = getVarSingleValue("api_base");
		String token = getVarSingleValue("token");
		String rootElementName = getVarSingleValueDefault("root_element", "company");
		String inn = getVarSingleValue("inn");

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
			String response = OkWebClient.getInstance().postStringHeaders(apiBase, "{ \"query\": \"" + inn + "\" }", "application/json",
					"Content-Type", "application/json",
					"Accept", "application/json",
					"Authorization", token);
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
