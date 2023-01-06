package extra;

import ecommander.fwk.OkWebClient;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ApiProxyCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue(ExecutablePagePE.PAGEURL_VALUE);
		String pageName = getVarSingleValue(ExecutablePagePE.PAGENAME_VALUE);
		String apiBase = getVarSingleValue("api_base");
		String token = getVarSingleValue("token");
		String tokenHeaderName = getVarSingleValue("token_header");
		String urlToGet = apiBase + StringUtils.substringAfterLast(url, pageName + "/");
		try {
			//String response = OkWebClient.getInstance().getString(urlToGet);
			String response = OkWebClient.getInstance().getStringHeaders(urlToGet, tokenHeaderName, token);
			return getResult("success").setValue(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return getResult("success").setValue(sw.toString());
		}
	}
}
