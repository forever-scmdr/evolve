package lunacrawler;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import edu.uci.ics.crawler4j.url.WebURL;
import lunacrawler.fwk.CrawlerController;
import lunacrawler.fwk.Parse_item;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

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


	public ResultPE testPI() throws Exception {
		String idStr = getVarSingleValue("pi");
		Parse_item parseItem = Parse_item.get(ItemQuery.loadById(Long.parseLong(idStr)));
		if (parseItem == null) {
			return getResult("test_xsl").setValue("NO PARSE ITEM FOUND");
		}
		// Подготовка HTML (убирание необъявленных сущностей и т.д.)
		//Document jsoupDoc = Jsoup.parse(parseItem.get_html());
		//String html = JsoupUtils.outputHtmlDoc(jsoupDoc);
		//html = JsoupUtils.prepareValidXml(html);
		//String html = Jsoup.clean(parseItem.get_html(), Whitelist.relaxed());
		String html = Strings.cleanHtml(parseItem.get_html());
		html = JsoupUtils.prepareValidXml(html);
		String xml = CrawlerController.transformString(html, parseItem.get_url());
		return getResult("test_xsl").setValue(xml);
	}


	@Override
	public void modifyUrl(WebURL url) {
		// По умолчанию ничего не делает
	}
}
