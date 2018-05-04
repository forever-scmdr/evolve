package lunacrawler;

import ecommander.fwk.IntegrateBase;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import lunacrawler.fwk.BasicCrawler;
import lunacrawler.fwk.CrawlerController;
import lunacrawler.fwk.CrawlerController.Mode;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

import java.net.URLDecoder;

public class XSLTransformCrawler extends BasicCrawler {
	
	
	@Override
	protected String processUrl(Page page) {
		if (page.getParseData() instanceof HtmlParseData) {
			String url = page.getWebURL().getURL();
			try {
				url = URLDecoder.decode(url, "UTF-8");
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				String html = htmlParseData.getHtml();
				HtmlCleaner cleaner = new HtmlCleaner();
				CleanerProperties props = cleaner.getProperties();
				props.setTrimAttributeValues(true);
				props.setAllowHtmlInsideAttributes(false);
				props.setOmitComments(true);
				props.setNamespacesAware(false);
				props.setOmitDoctypeDeclaration(true);
				TagNode node = cleaner.clean(html);
	
	//			Document doc = Jsoup.parse(html);
	//			// Удалить все тэги с неймспейсом
	//			for (Element el : doc.getAllElements()) {
	//				if (StringUtils.contains(el.tagName(), ':'))
	//					el.remove();
	//			}
	//			// Удалить все скрипты
	//			doc.getElementsByTag("script").remove();
	//			doc.outputSettings().syntax(Syntax.xml);
	//			doc.outputSettings().escapeMode(EscapeMode.xhtml);
				// Вставить атрибут source с текущим урлом в тэг body
	//			String url = page.getWebURL().getURL();
	//			doc.body().attr("source", url);
	//			return doc.body().outerHtml();
				

				node.findElementByName("body", false).addAttribute("source", url);
				return new PrettyXmlSerializer(props).getAsString(node);
			} catch (Exception e) {
				CrawlerController.getInfo().pushLog("Exception while parsing initial html of " + url, e);
			}
		}
		return null;
	}

	public static void startCrawling(IntegrateBase.Info info, String mode, UrlModifier... modifier) {
		try {
			CrawlerController.startCrawling(XSLTransformCrawler.class, info, Mode.valueOf(mode), modifier);
		} catch (Exception e) {
			info.pushLog("Some error", "<pre>" + ExceptionUtils.getStackTrace(e) + "</pre>");
		}
	}
	
	public static void terminate() {
		CrawlerController.terminate();
	}
	
	public static void main(String[] args) {
		try {
			CrawlerController.startCrawling(XSLTransformCrawler.class, null, null);
		} catch (Exception e) {
			//info.pushLog("Some error", e);
		}
	}
	
}
