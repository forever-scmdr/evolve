package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.CleanDeletedItemsDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anton on 08.11.2018.
 */
public class ImportFromAncientXML extends IntegrateBase implements CatalogConst {
	private Item catalog;
	private Item seoContainer;
	private HashSet<String> sectionURLs = new HashSet<>();
	private String baseURL;
	private String start_url;
	private HashMap<String, Item> sectionsMap = new HashMap<>();
	private LinkedHashSet<String> urls = new LinkedHashSet<>();
	private LinkedHashMap<String, String> missingPictures = new LinkedHashMap<>();
	private LinkedHashMap<String, String> redirectMap = new LinkedHashMap<>();


	@Override
	protected boolean makePreparations() throws Exception {
		baseURL = getVarSingleValue("base_url");
		start_url = getVarSingleValue("start_url");
		setProcessed(0);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Импорт каталога со старого сатйа");
		catalog = ItemUtils.ensureSingleRootAnonymousItem(CATALOG_ITEM, getInitiator());
		seoContainer = ItemUtils.ensureSingleRootAnonymousItem("seo_catalog", getInitiator());
		sectionsMap.put("catalog", catalog);

		setOperation("Удаление старых записей. Подождите!");
		CleanDeletedItemsDBUnit delete = new CleanDeletedItemsDBUnit(15000);
		executeAndCommitCommandUnits(delete);

		info.setCurrentJob("Подключене к списку разделов на старом сайте");
		Document doc = Jsoup.parse(new URL(baseURL + '/' + start_url), 5000);
		Elements sections = doc.getElementsByTag("section");

		setOperation("Создание списка разделов");
		sections.forEach((section) -> {
			String name = section.select(NAME).first().text();
			info.setCurrentJob("Созднание раздела: \"" + name + "\"");
			String code = section.attr("id");
			if(code.equals("17717")) return;
			String oldURL = section.select("show_section").first().text();
			Element textNode = section.select("text").first();
			String text = textNode == null ? "" : textNode.html();

			Element parentTag = section.parent();
			Item parentItem = catalog;
			if (!parentTag.is("catalog")) {
				parentItem = sectionsMap.get(parentTag.attr("id"));
			}
			Item sectionItem = ItemUtils.newChildItem(SECTION_ITEM, parentItem);

			try {
				sectionItem.setValueUI(CATEGORY_ID_PARAM, code);
				sectionItem.setValueUI("old_url", oldURL);
				sectionItem.setValueUI(NAME_PARAM, name);
				sectionItem.setKeyUnique(Strings.translit(name));
				executeAndCommitCommandUnits(SaveItemDBUnit.get(sectionItem).ignoreUser(true).noFulltextIndex());
				if (StringUtils.isNotBlank(text)) {
					Item seo = ItemUtils.newChildItem(ItemNames.SEO, sectionItem);
					seo.setValueUI(ItemNames.seo_.KEY_UNIQUE, sectionItem.getKeyUnique());
					seo.setValueUI(ItemNames.seo_.TEXT, processSectionText(text));
					executeAndCommitCommandUnits(SaveItemDBUnit.get(seo).ignoreUser(true).noFulltextIndex());
				}
				if (!sectionsMap.containsKey(code)) {
					sectionsMap.put(code, sectionItem);
					String productsURL = section.select("show_xml").first().text();
					urls.add(productsURL);
					//adding to redirect map
					if (parentItem == catalog) {
						String url = '/' + sectionItem.getKeyUnique() + '/';
						redirectMap.put(oldURL, url);
					} else {
						String url = redirectMap.get(parentItem.getStringValue("old_url")) + sectionItem.getKeyUnique() + '/';
						redirectMap.put(oldURL, url);
					}
				}
				info.increaseProcessed();
			} catch (Exception e) {
				info.addError(e);
				ServerLogger.error(e);
			}

		});
		pushLog("Созднание разделов завершено");
		processProducts();
		createRedirectMap();
		createMissingPicturesList();
		info.indexsationStarted();
		LuceneIndexMapper.getSingleton().reindexAll();

	}

	private void createMissingPicturesList() throws IOException {
		setOperation("Созднание списка отсутствующих изображений");
		StringBuilder sb = new StringBuilder();
		final boolean[] x = {false};
		missingPictures.forEach((code, pic) ->{
			if(x[0]) sb.append(System.lineSeparator());
			sb.append(code).append(" : ").append(pic);
			x[0] = true;
		});
		info.pushLog("Созднание списка отсутствующих изображений завершено");
		setOperation("Сохранение списка отсутствующих изображений");
		String fileName = "missing_img.txt";
		FileWriter fileWriter = new FileWriter(new File(AppContext.getContextPath()+File.separator+fileName));
		fileWriter.write(sb.toString());
		fileWriter.close();
		info.pushLog("Сохранение списка отсутствующих изображений завершено");
	}

	private void createRedirectMap() throws IOException {
		setOperation("Созднание фрагмента карты редиректов");
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDocPart();
		redirectMap.forEach((oldLink, newUrl) -> {
			doc.startElement("rule")
					.startElement("from", "casesensitive", "false").addText(oldLink).endElement()
					.startElement("to", "type", "permanent-redirect", "qsappend", "false", "last", "true").addText(newUrl).endElement()
				.endElement();
		});
		info.pushLog("Созднание фрагмента карты редиректов завершено");
		setOperation("Сохранение  фрагмента карты редиректов");
		String fileName = "redirect_part.txt";
		FileWriter fileWriter = new FileWriter(new File(AppContext.getContextPath()+File.separator+fileName));
		fileWriter.write(doc.toString());
		fileWriter.close();
		info.pushLog("Сохранение фрагмента карты редиректов завершено");
	}

	private void processProducts() throws Exception {
		setOperation("Наполнение разделов товарами");
		for (String url : urls) {
			Item parentSection = sectionsMap.get(StringUtils.substringAfterLast(url, "="));
			Document doc = Jsoup.parse(new URL(baseURL + '/' + url), 5000);
			Elements products = doc.getElementsByTag(PRODUCT_ITEM);
			for (Element productElement : products) {

				String code = productElement.attr("id");
				String name = productElement.select(NAME).first().text();
				String oldUrl = productElement.select("show_product").first().text();
				Element descriptionEl = productElement.select("short").first();
				String description = descriptionEl != null ? descriptionEl.html() : "";
				description = processDescription(description);
				Element textEl = productElement.select("text").first();
				String text = textEl != null ? textEl.html() : "";
				text = processDescription(text);
				Element pictureEl = productElement.select("big_picture").first();
				Element seoEl = productElement.select("seo").first();

				if (pictureEl == null) {
					pictureEl = productElement.select("medium_picture").first();
				}
				if (pictureEl == null) {
					pictureEl = productElement.select("small_picture").first();
				}
				String src = pictureEl != null ? pictureEl.text() : "";
				Item product = ItemUtils.newChildItem(PRODUCT_ITEM, parentSection);
				product.setKeyUnique(Strings.translit(name));
				product.setValueUI(NAME_PARAM, name);
				product.setValueUI(CODE_PARAM, code);
				product.setValueUI("old_url", oldUrl);
				product.setValueUI(TEXT_PARAM, text);
				product.setValueUI(DESCRIPTION_PARAM, description);

				Elements assocEls = productElement.select("assoc");
				for(Element assoc: assocEls){
					product.setValue("assoc_code", assoc.text().trim());
				}

				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreUser(true).noFulltextIndex().ignoreFileErrors());

				String parentUrl = redirectMap.get(parentSection.getStringValue("old_url"));
				redirectMap.put(oldUrl, parentUrl + product.getKeyUnique() + '/');

				if (StringUtils.isNotBlank(src)) {
					try {
						URL picUrl = new URL(baseURL + '/' + productElement.attr("path") + src);
						product.setValue(ItemNames.product_.MAIN_PIC, picUrl);
						executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex());
					} catch (Exception e) {
						String pu = baseURL + '/' + productElement.attr("path") + src;
						info.addError("Картинка [" + name + "]  не может быть загружена: " + pu, "");
						missingPictures.put(code, pu);
						continue;
					}
				}
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreUser(true).noFulltextIndex().ignoreFileErrors());
				if (seoEl != null) {
					Element titleEl = seoEl.select("title").first();
					Element descEl = seoEl.select("description").first();
					Element keywordsEl = seoEl.select("keywords").first();
					String title = titleEl != null ? titleEl.text() : "";
					String seoDescription = descEl != null ? descEl.text() : "";
					String seoKeywords = keywordsEl != null ? keywordsEl.text() : "";
					Item seo = ItemUtils.newChildItem(ItemNames.SEO, product);
					seo.setValue(ItemNames.seo_.TITLE, title);
					seo.setValue(ItemNames.seo_.DESCRIPTION, seoDescription);
					seo.setValue(ItemNames.seo_.KEYWORDS, seoKeywords);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(seo).ignoreUser(true).noFulltextIndex());
//					executeAndCommitCommandUnits(CreateAssocDBUnit.childExistsSoft(seo, product, ItemTypeRegistry.getAssocId("seo")));
				}
				info.increaseProcessed();
			}
		}
		pushLog("наполнение картлога завершено");
	}

	private String processDescription(String html){
		if (StringUtils.isBlank(html)) return "";
		String s = html.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
		s = s.replaceAll("<p>&amp;nbsp;</p>", "");
		s = s.replaceAll("&amp;nbsp;", " ");
		s = s.replaceAll("\\s+", " ");
		Matcher m = Pattern.compile("(?<tag><\\w+)(?<attr>(\\s+(class|style)=\"[^\"]*\")+)>").matcher(s);
		StringBuffer buf = new StringBuffer();
		while (m.find()){
			m.appendReplacement(buf, s.substring(m.start(), m.start("attr")) + s.substring(m.end("attr"), m.end()));
		}
		s = m.appendTail(buf).toString();
		s = s.replaceAll("<em>|</em>|<strong>|</strong>|<span>|</span>", "");

		buf = new StringBuffer();
		m = Pattern.compile("(от )?\\d+(,\\d+)? руб").matcher(s);
		while (m.find()){
			m.appendReplacement(buf,"<span class=\"price\">"+s.substring(m.start(), m.end())+"</span>");
		}
		s = m.appendTail(buf).toString();
		for(int i=0; i<5; i++) {
			s = s.replaceAll("<\\w+\\s*>\\s*<\\/\\w+>", "");
		}
		return s;
	}

	private String processSectionText(String text) {
		if (StringUtils.isBlank(text)) return "";
		text = processDescription(text);
		text = StringUtils.substringBeforeLast(text, "<table");
		if(text.indexOf("<!-- /Yandex.Metrika counter -->") != -1) {
			text = StringUtils.substringAfter(text, "<!-- /Yandex.Metrika counter -->");
		}
		return text;
	}

	@Override
	protected void terminate() throws Exception {

	}

}
