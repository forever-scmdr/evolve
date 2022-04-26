package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class YandexMarketCommand extends IntegrateBase {

	private static final String YANDEX_FILE_NAME = "yandex_market.xml";
	private static final String ORG = "ЧПТУП «БелЧип»";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private static XmlDocumentBuilder xml;
	private LinkedList<Item> products = new LinkedList<>();
	private HashMap<Long, Item> sections = new HashMap<>();
	private int processed = 0;
	private Collection<ParameterDescription> productTypeParams = ItemTypeRegistry.getItemType("product").getParameterList();
	private Path file = Paths.get(AppContext.getFilesDirPath(false) + YANDEX_FILE_NAME);
	private static final String BELCHIP_BASE = "https://belchip.by/";

//	@Override
//	protected void onAttempt() throws Exception {
//		String operation = getVarSingleValue("action");
//		if (("start").equalsIgnoreCase(operation)) {
//			info.pushLog("Создание каталога YandexMarket уже запущено! Дождитесь окончания текущей операции.");
//		}
//	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {

		info.setOperation("Deleting old file");
		FileUtils.deleteQuietly(file.toFile());

		info.setOperation("Creating new file");

		LocalDateTime now = LocalDateTime.now();
		xml = XmlDocumentBuilder.newDoc();
		xml.startElement("yml_catalog", "date", FORMATTER.format(now));
		xml.startElement("shop");

		addShopConstants();

		info.pushLog("Запись разделов");
		xml.startElement("categories");
		
		Item rootSection = ItemQuery.loadSingleItemByName("catalog");

		addCategories(rootSection);
		xml.endElement();
		
		Files.write(file, xml.toString().getBytes("UTF-8"));
		
		info.pushLog("Запись товаров");
		addProducts();

		// shop
		Files.write(file, "\n\t</shop>".getBytes("UTF-8"), StandardOpenOption.APPEND);
		// root
		Files.write(file, "\n</yml_catalog>".getBytes("UTF-8"), StandardOpenOption.APPEND);
		
	}

	private void addProducts() throws Exception {
		Files.write(file, "\n<offers>".getBytes("UTF-8"), StandardOpenOption.APPEND);
		for (Long secId : sections.keySet()) {
			Item sec = sections.get(secId);
			setOperation(sec.getStringValue("name"));
			ItemQuery q = new ItemQuery("product");
			q.setParentId(secId, false);
			products.addAll(q.loadItems());
			Item product;
			while ((product = products.poll()) != null) {
				if(StringUtils.isBlank(sec.getStringValue(ItemNames.section_.CODE))) continue;
				if(product != null) {
					processProduct(product, sec.getStringValue(ItemNames.section_.CODE));
				}
			}
		}
		Files.write(file, "\n</offers>".getBytes("UTF-8"), StandardOpenOption.APPEND);
	}

	private void processProduct(Item product, String parentCode) throws Exception {
		xml = XmlDocumentBuilder.newDocPart();
		
		boolean available = product.getByteValue(ItemNames.product_.AVAILABLE , (byte)0) > 0 && product.getDoubleValue(ItemNames.product_.QTY, 0d) > 0;
		
		xml.startElement("offer", "id", product.getStringValue(ItemNames.product_.CODE), "available",
				String.valueOf(available));

		xml.addElement("url", "");
		xml.addElement("categoryId", parentCode);
		xml.addElement("name", product.getStringValue(ItemNames.product_.NAME) + " " + product.getStringValue(ItemNames.product_.MARK));
		xml.addElement("vendor", product.getStringValue(ItemNames.product_.VENDOR,""));
		xml.addElement("vendorCode", product.getStringValue(ItemNames.product_.MARK));
		xml.addElement("price", product.getValue(ItemNames.product_.PRICE));
		xml.addElement("currencyId", "BYN");
		xml.addElement("quantity", product.getValue(ItemNames.product_.QTY));
		xml.addElement("min-quantity", product.getValue(ItemNames.product_.MIN_QTY));
		xml.addElement("unit", product.getValue(ItemNames.product_.UNIT, "шт"));

		String description = product.getStringValue(ItemNames.product_.DESCRIPTION, "");

		if (StringUtils.isNotBlank(description)) {
			Document doc = Jsoup.parse(description);
			description = doc.body().text();
		}

		xml.addElement("description", description);

		List<String> pdfs = product.getStringValues(ItemNames.product_.FILE);
		for (String pdf : pdfs) {
			String f = BELCHIP_BASE  + Paths.get("sitedocs", pdf).toString().replace('\\', '/');

			f = StringUtils.isNotBlank(product.getStringValue(ItemNames.product_.FILE)) ? f : "";
			xml.addElement("download", f);
		}

		for (String anal : product.getStringValues(ItemNames.product_.ANALOG_CODE)) {
			xml.addElement("analog", anal);
		}
		
		for (String rel : product.getStringValues(ItemNames.product_.REL_CODE)) {
			xml.addElement("related", rel);
		}
		
		String mainPic = product.getStringValue("pic_path");
		if(StringUtils.isNotBlank(mainPic)) {
			xml.addElement("picture", BELCHIP_BASE + Paths.get("sitepics", mainPic+"b.jpg").toString().replace('\\', '/'));
		}
		
		List<String> pics = product.getStringValues("extra_pic");
		for(String pic : pics) {
			xml.addElement("picture", BELCHIP_BASE + Paths.get(pic).toString().replace('\\', '/'));
		}
		
		xml.addElement("param", product.getStringValue(ItemNames.product_.VENDOR,""), "name", "Бренд");
		xml.addElement("param", product.getStringValue(ItemNames.product_.COUNTRY,""), "name", "Страна производитель");

		Item params = new ItemQuery(ItemNames.PARAMS).setParentId(product.getId(), false).loadFirstItem();
		ItemType itemType = params.getItemType();
		
		for(ParameterDescription param: itemType.getParameterList()) {
			try {
				xml.addElement("param", params.getValue(param.getId()), "name", param.getCaption());
			} catch(Exception e) {
//					System.out.println(e);
			}
		}
		
		info.setProcessed(++processed);

		xml.endElement();
		
		Files.write(file, ("\n" + xml).getBytes("UTF-8"), StandardOpenOption.APPEND);

	}

	private void addCategories(Item parent, String...parentCode) throws Exception {
		List<Item> subs = ItemQuery.loadByParentId(parent.getId(), null);
		if (StringUtils.startsWith(parent.getTypeName(), "section")) {
			sections.put(parent.getId(), parent);
			addSingleSection(parent, parentCode);
			String pCode = parent.getStringValue(ItemNames.section_.CODE);
			for(Item sub : subs) {
				if(sub.getItemType().isUserDefined() || sub.getItemType().getName().equals(ItemNames.PRODUCT)) continue;
				addCategories(sub, pCode);
			}
		} else {
			for(Item sub : subs) {
				addCategories(sub);
			}
		}
		
	}
	
	private void addSingleSection(Item section, String... parentCode) {
		if(parentCode.length == 1) {
			xml.addElement("category", section.getStringValue("name"), "id", section.getStringValue(ItemNames.section_.CODE), "parentId", parentCode[0]);
		}else {
			xml.addElement("category", section.getStringValue("name"), "id", section.getStringValue(ItemNames.section_.CODE));
		}
		info.setProcessed(++processed);
	}

	private void addShopConstants() {
		xml.addElement("name", ORG);
		xml.addElement("company", ORG);
		xml.addElement("url", getUrlBase());
		xml.startElement("currencies").addEmptyElement("currency", "id", "BYN", "rate", "1").endElement();
	}

	@Override
	protected void terminate() throws Exception {
		// TODO Auto-generated method stub

	}


}
