package lunacrawler;

import ecommander.fwk.IntegrateBase;
import ecommander.pages.ResultPE;
import edu.uci.ics.crawler4j.url.WebURL;
import lunacrawler.fwk.CrawlerController;

/**
 * Команда для парсинга сайта
 * Created by E on 23/4/2018.
 */
public class CrawlCommand extends IntegrateBase implements UrlModifier {
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		String mode = getVarSingleValue("job");
		XSLTransformCrawler.startCrawling(info, mode, this);
	}

	@Override
	protected void terminate() throws Exception {
		XSLTransformCrawler.terminate();
	}

	public ResultPE test() throws Exception {
		String url = getVarSingleValue("url");
		String xml = CrawlerController.transformUrl(url);
		ResultPE result = getResult("test_xsl");
		result.setValue(xml);
		return result;
	}

	@Override
	public void modifyUrl(WebURL url) {
		// По умолчанию ничего не делает
	}
}
