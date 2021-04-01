package extra;

import ecommander.fwk.ItemUtils;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CleanDeletedItemsDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anton on 08.11.2018.
 */
public class ImportFromAncientXML extends CreateParametersAndFiltersCommand implements CatalogConst {
	private Item catalog;
	private Item seoWrap;
	private Item currentSection;
	private String base;
	private String startUrl;
	private static final String PRODUCT_CARD = "product_card";
	private static final String LINK = "page link";
	private static final String PAGE_NUMBER = "variables page";
	private static final String KEY = "key";
	private static final String SELF_PRICE = "self_price";
	private static final String SECTION_NAME = "section name";
	private static final String PRODUCT_COUNT = "product_count";
	private static final String PAGES = "product_pages";
	private static final String DOC_REF = "doc_ref";
	private static final String PATH = "path";
	private static final String PARAM_EL = "param";
	private static final String OTHER_SEC = "Прочее";
	private static final String CAPTION_ATTR = "caption";
	private boolean isOtherSec = false;
	private static final Pattern HOST = Pattern.compile("https?://([\\w]+\\.[\\w]+)+/");
	private LinkedList<String> urlsQueue = new LinkedList<>();
	private LinkedList<Item> newSections = new LinkedList<>();
	private int imgDownloadStopper = 0;


	@Override
	protected boolean makePreparations() throws Exception {
		//init URLs
		startUrl = getVarSingleValue("start_url");
		Matcher m = HOST.matcher(startUrl);
		if (!m.find()) return false;
		base = m.group(0);

		//Init items
		catalog = ItemUtils.ensureSingleRootAnonymousItem(CATALOG_ITEM, getInitiator());
		seoWrap = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.SEO_CATALOG, getInitiator());
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		clearGraveyard();
		importSections();
		importProducts();
		createFiltersAndItemTypes();
		info.setOperation("Индексация названий товаров");
		LuceneIndexMapper.getSingleton().reindexAll();
		setOperation("Интеграция завершена");
	}

	private void importProducts() throws Exception {
		setOperation("Наполнение разделов");
		String url;
		setProcessed(0);
		info.setToProcess(0);
		while ((url = urlsQueue.poll()) != null) {
			Document doc = fetchPage(url);
			int t = Integer.parseInt(getText(doc, PRODUCT_COUNT,"0"));
			if (t == 0) continue;

			int page = Integer.parseInt(getText(doc, PAGE_NUMBER,"1"));
			String name = doc.select(SECTION_NAME).first().text();
			String key = doc.select(SECTION_ITEM).first().attr(KEY);
			currentSection = ItemQuery.loadByUniqueKey(doc.select(SECTION_ITEM).first().attr(KEY)).get(key);
			info.setCurrentJob(name + ". Страница: " + page);
			isOtherSec = name.intern() == OTHER_SEC;
			if (page == 1) {
				info.setToProcess(t + info.getToProcess());
				if (!isOtherSec) {
					newSections.add(currentSection);
				}
				addPageUrls(doc.select(PAGES));
			}
			LinkedHashMap<String, Element[]> productInfo = new LinkedHashMap<>();
			for (Element product : doc.select(PRODUCT_ITEM)) {
				String code = getText(product, CODE_PARAM);
				if(StringUtils.isNotBlank(code)) {
					Element[] elArr = new Element[2];
					elArr[0] = product;
					productInfo.put(code, elArr);
				}
			}
			for (Element card : doc.select(PRODUCT_CARD)) {
				String code = getText(card, CODE_PARAM);;
				if(StringUtils.isNotBlank(code)) {
					productInfo.get(code)[1] = card;
				}
			}

			for (Map.Entry<String, Element[]> entry : productInfo.entrySet()) {
				String k = entry.getKey();
				Element[] v = entry.getValue();
				addProduct(v[0], v[1], currentSection);
			}

		}
	}

	private void addPageUrls(Elements pages) {
		if(pages == null) return;
		for (Element link : pages.select(LINK)) {
			urlsQueue.offer(base + link.text());
		}
	}

	private void addProduct(Element productEl, Element cardEl, Item currentSection) throws Exception {
		Item product = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT_ITEM), currentSection);
		product.setValue(HAS_LINE_PRODUCTS, (byte) 0);
		String code = getText(productEl, CODE_PARAM);
		String keyUnique = getAttr(productEl, KEY);
		String name = getText(productEl, NAME_PARAM);
		String price = getText(productEl, PRICE_PARAM).replaceAll("\\s", "");
		String margin = getText(productEl, MARGIN_PARAM).replaceAll("\\s", "");

		String search = getText(productEl, SEARCH_PARAM);
		String unit = getText(productEl, UNIT_PARAM);
		String qty = getText(productEl, QTY_PARAM).replaceAll("\\s", "");

		String minQty = getText(productEl, MIN_QTY_PARAM).replaceAll("\\s", "");

		String selfPrice = getText(productEl, SELF_PRICE).replaceAll("\\s", "");
		if (cardEl != null) {
			String dscr = getHtml(cardEl, SHORT_PARAM);
			String pdf = getText(cardEl, DOC_REF);
			String path = cardEl.attr(PATH);
			String large =  getText(cardEl,"large_pic");
			processPics(product, path, large);
			product.setValueUI(DESCRIPTION_PARAM, dscr);
			product.setValueUI("pdf", pdf);
		}

		product.setKeyUnique(keyUnique);
		product.setValue(CODE_PARAM, code);
		product.setValue(NAME_PARAM, name);
		product.setValueUI(PRICE_PARAM, price);
		product.setValueUI(MARGIN_PARAM, margin);
		product.setValueUI(SEARCH_PARAM, (search + " " + currentSection.getStringValue(NAME_PARAM).replace("Прочее","")).trim());
		product.setValueUI(UNIT_PARAM, unit);
		product.setValueUI(QTY_PARAM, qty);
		product.setValueUI(MIN_QTY_PARAM, minQty);
		byte avlb = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO).compareTo(BigDecimal.ZERO) == 1 && (product.getDoubleValue(QTY_PARAM, 0d) > product.getDoubleValue(MIN_QTY_PARAM, 1d)) ? (byte) 1 : (byte) 0;
		product.setValue(AVAILABLE_PARAM, avlb);
		product.setValueUI(PRICE_OPT_PARAM, selfPrice);

		executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreFileErrors().noFulltextIndex());

		if (!isOtherSec) {
			addAuxParams(product, productEl);
		}

		info.increaseProcessed();
	}

	private void addAuxParams(Item product, Element productEl) throws Exception {
		Elements params = productEl.select(PARAM_EL);
		if(params.isEmpty()) return;
		Item paramsXML = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PARAMS_XML), product);
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		for(Element param : params){
			String name = param.attr(CAPTION_ATTR);
			String value = param.text();
			xml.startElement("parameter")
					.startElement("name")
					.addText(firstUpperCase(name))
					.endElement()
					.startElement("value")
					.addText(value)
					.endElement()
					.endElement();
		}
		paramsXML.setValue(XML_PARAM, xml.toString());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXML).noFulltextIndex().noTriggerExtra());
	}

	private void processPics(Item product, String path, String large) throws MalformedURLException {
		//if(imgDownloadStopper++ > 20) return;

		if (StringUtils.isNotBlank(large)) {
			product.setValue(ItemNames.product_.MAIN_PIC, new URL(base + path + large));
		}
	}

	private void createFiltersAndItemTypes() throws Exception {
		if (newSections.size() == 0) return;
		setOperation("Создание классов и фильтров");
		doCreate(newSections);
	}

	private Document fetchPage(String url) throws IOException {
		info.setCurrentJob("Подключене к: " + url);
		return Jsoup.parse(new URL(url), 5000);
	}

	private void clearGraveyard() throws Exception {
		setOperation("Удаление старых записей. Подождите!");
		for(int i = 0; i< 10; i++) {
			CleanDeletedItemsDBUnit delete = new CleanDeletedItemsDBUnit(15000);
			executeAndCommitCommandUnits(delete);
		}
	}

	private void importSections() throws Exception {
		setOperation("Составление списка разделов");
		Document doc = fetchPage(startUrl);
		info.setProcessed(0);

		Element catalogElement = doc.select(CATALOG_ITEM).first();
		info.setToProcess(catalogElement.select(SECTION_ITEM).size());
		Elements children = catalogElement.children();

		for (Element el : children) {
			currentSection = catalog;
			if (el.is(SECTION_ITEM)) {
				processSectionElement(el, currentSection);
			}
		}
	}

	@Override
	protected void terminate() throws Exception {

	}

	private void processSectionElement(Element sectionElement, Item parentSection) throws Exception {
		info.setCurrentJob(sectionElement.select(NAME_PARAM).first().text());
		Item section = ItemUtils.newChildItem(SECTION_ITEM, parentSection);
		section.setKeyUnique(sectionElement.attr(KEY));
		Elements children = sectionElement.children();
		boolean needsSave = true;
		for (Element el : children) {
			currentSection = section;
			if (el.is(NAME_PARAM)) {
				currentSection.setValue(NAME_PARAM, el.text());
			} else if (el.is(CODE_PARAM)) {
				section.setValue(CODE_PARAM, el.text());
			} else if (el.is("show_section")) {
				urlsQueue.add(base + el.text());
			} else if (el.is(SECTION_ITEM)) {
				if (currentSection.getId() == 0) {
					executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noFulltextIndex().noTriggerExtra());
					needsSave = false;
				}
				processSectionElement(el, currentSection);
			}
		}
		if (needsSave) {
			executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noFulltextIndex().noTriggerExtra().ignoreFileErrors());
		}
		info.increaseProcessed();
	}

	public static void main(String[] args) {
		String url = "https://www.litmir.me/br/?b=47785&p=1";
		Matcher m = HOST.matcher(url);
		m.find();
		System.out.println(m.group(0));
	}

	private String getText(Element parent, String selector){
		Elements res = parent.select(selector);
		if(res == null || res.isEmpty()) return "";
		return res.first().text();
	}

	private String getHtml(Element parent, String selector){
		Elements res = parent.select(selector);
		if(res == null || res.isEmpty()) return "";
		return res.first().html();
	}

	private String getAttr(Element parent, String selector){
		if(parent == null) return "";
		return parent.attr(selector);
	}

	private String getAttr(Elements parent, String selector){
		Elements res = parent.select(selector);
		if(res == null || res.isEmpty()) return "";
		return res.attr(selector);
	}

	private String getText(Document parent, String selector){
		Elements res = parent.select(selector);
		if(res == null || res.isEmpty()) return "";
		return res.first().text();
	}

	private String getText(Document parent, String selector, String defaultValue){
		Elements res = parent.select(selector);
		if(res == null || res.isEmpty()) return defaultValue;
		return res.first().text();
	}

	private String firstUpperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
