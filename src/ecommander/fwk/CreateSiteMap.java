package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.pages.ExecutablePagePE;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateSiteMap extends IntegrateBase {

	private static final String SECTION_ULS_PAGE = "sitemap";
	private static final String PRODUCT_URLS_PAGE = "sitemap_section";
	private static final String COMMENT_PATTERN = "<!--(?<comment>.*)-->";
	//private static final String SCHEMA_LOCATION = "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\"";

	private static final int URLS_PER_FILE = 49999;
	private int urlCounter = 0;
	private int fileCounter = 0;

	@Override
	protected void integrate() throws Exception {
		setOperation("Creating Sitemap");
		String sectionUrls = getPageContent(SECTION_ULS_PAGE);
		Document doc = Jsoup.parse(sectionUrls, "", Parser.xmlParser());
		doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		Elements urls = doc.select("url");

		Path map = startFile("sitemap.xml");

		map = addUrlsToSiteMap(map, urls);

		Pattern pattern = Pattern.compile(COMMENT_PATTERN);
		Matcher matcher = pattern.matcher(sectionUrls);

		while(matcher.find()){
			String keyUnique = matcher.group("comment");
			String url = PRODUCT_URLS_PAGE + '/' + keyUnique;
			String content = getPageContent(url);
			doc = Jsoup.parse(content, "", Parser.xmlParser());
			doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
			urls = doc.select("url");
			map = addUrlsToSiteMap(map, urls);
		}
		if(urlCounter > 0){
			endFile(Paths.get(AppContext.getContextPath(), "sitemap"+fileCounter+".xml"));
		}

	}

	private void endFile(Path path) throws IOException {
		Files.write(path, "</urlset>".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		info.pushLog(path.getFileName()+" created");
	}

	private Path startFile(String s) throws IOException {
		Path res = Paths.get(AppContext.getContextPath(), s);
		String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n" +
				"        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"        xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n";
		Files.write(res, start.getBytes(StandardCharsets.UTF_8));
		return res;
	}


	private Path addUrlsToSiteMap(Path file, Elements urls) throws IOException {
		for(Element urlEl : urls){
			String link = urlEl.outerHtml()+'\n';
			Files.write(file, link.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			urlCounter++;
			info.increaseProcessed();
			if(urlCounter >= URLS_PER_FILE){
				urlCounter = 0;
				endFile(file);
				fileCounter++;
				file = startFile("sitemap"+fileCounter+".xml");
			}
		}
		return file;
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
