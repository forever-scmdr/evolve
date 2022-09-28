package ecommander.fwk.external_shops;

import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;

public abstract class AbstractLoadAdditionalContentCommand extends Command {

	protected String content = "";

	@Override
	public ResultPE execute() throws Exception {
		String code = getCode();
		Item urls = getSessionMapper().getSingleRootItemByName("external_urls_container");
		if(urls != null){
			Item externalInfo = getSessionMapper().getSingleItemByParamValue("external_url", "code", code);
			if(externalInfo != null){
				content = externalInfo.getStringValue("content", content);
				if(StringUtils.isBlank(content)){
					String url = externalInfo.getStringValue("url");
					Document html = Jsoup.parse(new URL(url), 25000);
					content = extractContent(html);
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
