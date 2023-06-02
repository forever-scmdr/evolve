package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.model.Item;
import ecommander.pages.ExecutablePagePE;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class CreateBigSiteMap extends IntegrateBase {

	private static final int LOAD_BATCH_SIZE = 1000;
	private static final String SECTION_ULS_PAGE = "sitemap";
	private static final String PRODUCT_URLS_PAGE = "sitemap_section";
	private static final String COMMENT_PATTERN = "<!--(?<comment>.*)-->";
	//private static final String SCHEMA_LOCATION = "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\"";

	private static final int URLS_PER_FILE = 30000;
	private int urlCounter = 0;
	private int fileCounter = 1;
	private Path currentSitemap;

	@Override
	protected void integrate() throws Exception {
		setOperation("Creating Sitemap");
		currentSitemap = Paths.get(AppContext.getContextPath(), "sitemap" + fileCounter + ".xml");
		createCommonSitemap();
		fileCounter++;
		currentSitemap = startFile("sitemap" + fileCounter + ".xml");
		addProducts();

		LocalDate now = LocalDate.now();
		String modified = DateTimeFormatter.ofPattern("YYYY-MM-dd").format(now);

		setOperation("Creating sitemap.xml");
		XmlDocumentBuilder builder = XmlDocumentBuilder.newDoc();
		builder.startElement("sitemapindex","xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
		for (int i = 1 ; i < fileCounter + 1; i++) {
			builder.startElement("sitemap");
			String base = getUrlBase();
			base = base.endsWith("/")? base : base+"/";
			builder.startElement("loc").addText(base + "sitemap"+i+".xml").endElement();
			builder.startElement("lastmod").addText(modified).endElement();
			builder.endElement();
		}
		builder.endElement();
		Files.write(Paths.get(AppContext.getContextPath(), "sitemap.xml"), builder.toString().getBytes(StandardCharsets.UTF_8));
		pushLog("sitemap.xml created");
	}


	private void addProducts() throws Exception {
		String base = StringUtils.endsWith(getUrlBase(),"/")? getUrlBase() : getUrlBase() + '/';
		LinkedList<Item> products = new LinkedList<>(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, 0L));
		long id = 0;
		XmlDocumentBuilder part = XmlDocumentBuilder.newDocPart();
		while (products.size() > 0) {
			while (products.size() != 0) {
				Item product = products.poll();
				id = product.getId();
				part.startElement("url")
						.startElement("loc")
						.addText(base + "product/" + product.getKeyUnique() + "/")
						.endElement()
						.startElement("changefreq")
						.addText("daily")
						.endElement()
						.startElement("priority")
						.addText("0.80")
						.endElement()
						.endElement();

				urlCounter++;
				info.increaseProcessed();
				if (urlCounter >= URLS_PER_FILE) {
					Files.write(currentSitemap, part.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
					urlCounter = 0;
					endFile();
					fileCounter++;
					currentSitemap = startFile("sitemap" + fileCounter + ".xml");
					part = XmlDocumentBuilder.newDocPart();
				}
			}
			products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, id));
		}
		if (urlCounter != 0) {
			Files.write(currentSitemap, part.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			urlCounter = 0;
			endFile();
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

	/**
	 * Создает стандартный sitemap со всеми ссылками кроме товаров
	 * @throws Exception
	 */
	private void createCommonSitemap() throws Exception {
		ExecutablePagePE siteMap = getExecutablePage("sitemap");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PageController.newSimple().executePage(siteMap, bos);
		//String pageContent = bos.toString("UTF-8");
		bos.close();
		Files.write(currentSitemap, bos.toByteArray());
	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void terminate() throws Exception {
	}
}
