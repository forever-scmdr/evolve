package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateSiteMap extends IntegrateBase {

	private static final String SECTION_ULS_PAGE = "sitemap";
	private static final String PRODUCT_URLS_PAGE = "sitemap_section";
	private static final String COMMENT_PATTERN = "<!--(?<comment>.*)-->";
	//private static final String SCHEMA_LOCATION = "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\"";

	private static final int URLS_PER_FILE = 49999;
	private int urlCounter = 0;
	private int fileCounter = 1;
	private Path currentSitemap;

	@Override
	protected void integrate() throws Exception {
		setOperation("Creating Sitemap");
		String sectionUrls = getPageContent(SECTION_ULS_PAGE);
		Document doc = Jsoup.parse(sectionUrls, "", Parser.xmlParser());
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		doc.outputSettings().indentAmount(1);
		doc.outputSettings().prettyPrint(true);
		Elements urls = doc.select("url");

		currentSitemap = startFile("sitemap"+fileCounter+".xml");

		addUrlsToSiteMap(urls);

		Pattern pattern = Pattern.compile(COMMENT_PATTERN);
		Matcher matcher = pattern.matcher(sectionUrls);

		while(matcher.find()){
			String keyUnique = matcher.group("comment");
			String url = PRODUCT_URLS_PAGE + '/' + keyUnique;
			processProductUrls(url);
		}
		if(urlCounter > 0){
			endFile();
		}

		LocalDate now = LocalDate.now();
		String modified = DateTimeFormatter.ofPattern("YYYY-MM-dd").format(now);

		setOperation("Creating sitemap.xml");
		XmlDocumentBuilder builder = XmlDocumentBuilder.newDoc();
		builder.startElement("sitemapindex","xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
		for (int i = 1 ; i < fileCounter+1; i++){
			builder.startElement("sitemap");
			String base = getUrlBase();
			base = base.endsWith("/")? base : base+"/";
			builder.startElement("loc").addText(base + "sitemap"+i+".xml").endElement();
			builder.startElement("lasmod").addText(modified).endElement();
			builder.endElement();
		}
		builder.endElement();
		Files.write(Paths.get(AppContext.getContextPath(), "sitemap.xml"), builder.toString().getBytes(StandardCharsets.UTF_8));
		pushLog("sitemap.xml created");
	}

	private void processProductUrls(String url) throws Exception {

		String content = getPageContent(url);
		Document doc = Jsoup.parse(content, "", Parser.xmlParser());
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		doc.outputSettings().indentAmount(1);
		doc.outputSettings().prettyPrint(true);
		Elements urls = doc.select("url");
		addUrlsToSiteMap(urls);
		Elements n = doc.select("next");
		if(n != null && n.size() > 0) {
			String next = n.first().text();
			if (StringUtils.isNotBlank(next)) {
				processProductUrls(next);
			}
		}
	}

	private void endFile() throws IOException {
		Files.write(currentSitemap, "</urlset>".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		info.pushLog(currentSitemap.getFileName()+" created");
	}

	private Path startFile(String s) throws IOException {
		setOperation("Creating " + s);
		Path res = Paths.get(AppContext.getContextPath(), s);
		String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n" +
				"        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"        xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n";
		Files.write(res, start.getBytes(StandardCharsets.UTF_8));
		return res;
	}


	private void addUrlsToSiteMap(Elements urls) throws IOException {
		for(Element urlEl : urls){
			String link = urlEl.outerHtml()+'\n';
			link = link.replaceAll(" ", "\t");
			Files.write(currentSitemap, link.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			urlCounter++;
			info.increaseProcessed();
			if(urlCounter >= URLS_PER_FILE){
				urlCounter = 0;
				endFile();
				fileCounter++;
				currentSitemap = startFile("sitemap"+fileCounter+".xml");
			}
		}
	}

	private String getPageContent(String url) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ExecutablePagePE siteMap = getExecutablePage(url);
		PageController.newSimple().executePage(siteMap, bos);
		return bos.toString("UTF-8");
	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void terminate() throws Exception {
	}
}
