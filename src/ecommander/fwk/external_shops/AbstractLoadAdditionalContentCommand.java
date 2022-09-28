package ecommander.fwk.external_shops;

import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public abstract class AbstractLoadAdditionalContentCommand extends Command {

	protected String content = "";

	@Override
	public ResultPE execute() throws Exception {
		String code = getCode();
		Item urls = getSessionMapper().getSingleRootItemByName("external_urls_container");
		if(urls != null){
			Item externalInfo = getSessionMapper().getSingleItemByParamValue("external_url", "code", code);
			if(externalInfo != null){
				//content = externalInfo.getStringValue("content", content);
				if(StringUtils.isBlank(content)){
					String url = externalInfo.getStringValue("url");

					StringBuilder sb = new StringBuilder();
					String line;

					URLConnection urlConnection = new URL(url).openConnection();
					urlConnection.addRequestProperty("User-Agent", "Mozilla");
					urlConnection.setReadTimeout(5000);
					urlConnection.setConnectTimeout(5000);

					try (InputStream is = urlConnection.getInputStream();
						 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

						while ((line = br.readLine()) != null) {
							sb.append(line);
						}

					}

					//String html = FluentWebClient.getString(url);

					content = extractContent(Jsoup.parse(sb.toString()));
					externalInfo.setValueUI("content", content);
					getSessionMapper().saveTemporaryItem(externalInfo);
				}
			}
		}
		return defineResult(content);
	}


	protected abstract String extractContent(Document html);

	protected String getCode(){
		return getVarSingleValue("code");
	}

	protected abstract ResultPE defineResult(String content) throws EcommanderException;
}
