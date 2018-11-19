package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Создать файл Yandex Market XML
 * Created by E on 6/6/2018.
 */
public class YMarketCreateXMLFile extends Command implements CatalogConst {

	private static final String YANDEX_FILE_NAME = "yandex_market.xml";

	private XmlDocumentBuilder xml;
	private Item catalog;
	private String name;
	private String company;
	private boolean hasContainsAssoc;
	private boolean hasParamsItem;
	private boolean hasXmlParams;
	private boolean withPriceOnly;

	@Override
	public ResultPE execute() throws Exception {
		name = getVarSingleValue(NAME_ELEMENT);
		company = getVarSingleValue(COMPANY_ELEMENT);
		catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, User.getDefaultUser(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		hasContainsAssoc = ItemTypeRegistry.getAssoc("contains") != null;
		hasParamsItem = ItemTypeRegistry.getItemType(PARAMS_ITEM) != null;
		hasXmlParams = false;
		ParameterDescription desc = ItemTypeRegistry.getItemType(PRODUCT_ITEM).getParameter("tech");
		hasXmlParams = desc != null && desc.getType() == DataType.Type.XML;
		return createYandex();
	}

	private ResultPE createYandex() throws EcommanderException {
		withPriceOnly = "yes".equalsIgnoreCase(getVarSingleValue("with_price_only"));
		String nowStr = DateDataType.outputDate(System.currentTimeMillis(), DateTimeFormat.forPattern("dd-MM-yyyy HH:mm").withZoneUTC());
		Path file = Paths.get(AppContext.getFilesDirPath(false) + YANDEX_FILE_NAME);
		try {
			FileUtils.deleteQuietly(file.toFile());

			xml = XmlDocumentBuilder.newDoc();
			xml.startElement(YML_CATALOG_ELEMENT, DATE_ATTR, nowStr);
			xml.startElement(SHOP_ELEMENT);
			xml.startElement(NAME_ELEMENT).addText(name).endElement();
			xml.startElement(COMPANY_ELEMENT).addText(company).endElement();
			xml.startElement(URL_ELEMENT).addText(getUrlBase()).endElement();
			xml.startElement(CURRENCIES_ELEMENT).addEmptyElement(CURRENCY_ELEMENT, ID_ATTR, "BYN", RATE_ATTR, "1").endElement(); // currencies
			xml.startElement(CATEGORIES_ELEMENT);
			processCategory(catalog);
			xml.endElement(); // categories
			xml.startElement(OFFERS_ELEMENT);
			processCategoryProducts(catalog);
			xml.endElement(); // offers
			xml.endElement(); // shop
			//xml.addEmptyElement("result");
			xml.endElement(); // yml_catalog

			Files.write(file, xml.toString().getBytes("UTF-8"));
			//catalog.setValue(ItemNames.catalog.YML_FILE, file.toFile());
			//executeAndCommitCommandUnits(new UpdateItemDBUnit(catalog));

			return getResult("success").setValue(AppContext.getCommonFilesUrlPath() + YANDEX_FILE_NAME);
		} catch (Exception e) {
			ServerLogger.error("Create YML file error", e);
			return getResult("error").setValue(file.toString());
		}
	}

	private void processCategory(Item category) throws Exception {
		List<Item> subCats = new ItemQuery(SECTION_ITEM).setParentId(category.getId(), false).loadItems();
		for (Item subCat : subCats) {
			xml.startElement(CATEGORY_ELEMENT, ID_ATTR, subCat.getId());
			if (StringUtils.equals(category.getTypeName(), SECTION_ITEM)) {
				xml.insertAttributes("parentId", category.getId() + "");
			}
			xml.addText(subCat.getStringValue(NAME_PARAM));
			xml.endElement();
			processCategory(subCat);
		}
	}

	private void processCategoryProducts(Item category) throws Exception {
		List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(category.getId(), false).loadItems();
		if (products.size() == 0 && hasContainsAssoc) {
			products = new ItemQuery(PRODUCT_ITEM).setParentId(category.getId(), false, "contains").loadItems();
		}
		BigDecimal zero = new BigDecimal(0);
		for (Item prod : products) {
			BigDecimal price = prod.getDecimalValue(PRICE_PARAM, zero);
			String avail = price.doubleValue() <= 0.001d ? "false" : "true";
			//patch 14.11.2018 fix no products without price or code
			if(StringUtils.isBlank(prod.getStringValue(CODE_PARAM)) || (price.doubleValue() <= 0.001d && withPriceOnly)) continue;

			xml.startElement(OFFER_ELEMENT, ID_ATTR, prod.getStringValue(CODE_PARAM), AVAILABLE_ATTR, avail);
			String url = getUrlBase() + "/" + prod.getKeyUnique() + "/";
			xml.startElement(URL_ELEMENT).addText(url).endElement();
			xml.startElement(PRICE_ELEMENT).addText(prod.getDecimalValue(PRICE_PARAM)).endElement();
			xml.startElement(CURRENCY_ID_ELEMENT).addText("BYN").endElement();
			xml.startElement(CATEGORY_ID_ELEMENT).addText(category.getId()).endElement();
			String name = prod.getStringValue(NAME_PARAM);
			if (prod.isValueNotEmpty(TYPE_PARAM))
				name = prod.getStringValue(TYPE_PARAM) + " " + name;
			xml.startElement(NAME_ELEMENT).addText(name).endElement();
			if (prod.isValueNotEmpty(VENDOR_CODE_PARAM))
				xml.startElement(VENDOR_CODE_ELEMENT).addText(prod.getStringValue(VENDOR_CODE_PARAM)).endElement();
			xml.startElement(MODEL_ELEMENT).addText(prod.getStringValue(NAME_PARAM)).endElement();
			String text = prod.getStringValue(TEXT_PARAM);
			if (StringUtils.isBlank(text))
				text = prod.getStringValue(DESCRIPTION_PARAM);
			if (StringUtils.isNotBlank(text)) {
				Document doc = Jsoup.parse(text);
				xml.startElement(DESCRIPTION_ELEMENT).addText(doc.body().text()).endElement();
			}

			// Галерея
			for (String picName : prod.outputValues(GALLERY_PARAM)) {
				xml.startElement("picture").addText(getUrlBase() + "/" + AppContext.getFilesUrlPath(false) +
						Item.createItemFilesPath(prod.getId()) + picName).endElement();
			}

			// Пользовательские параметры
			if (hasParamsItem) {
				Item prodParams = new ItemQuery(PARAMS_ITEM).setParentId(prod.getId(), false).loadFirstItem();
				if (prodParams != null) {
					for (ParameterDescription paramDesc : prodParams.getItemType().getParameterList()) {
						xml.startElement(PARAM_ELEMENT, NAME_ATTR, paramDesc.getCaption())
								.addText(prodParams.outputValue(paramDesc.getName()))
								.endElement();
					}
				}
			}
			// Параметры в виде XML
			else if (hasXmlParams) {
				String paramsXml = prod.getStringValue("tech");
				if (StringUtils.isNotBlank(paramsXml)) {
					String stringXml = "<params>" + paramsXml + "</params>";
					Document paramsTree = Jsoup.parse(stringXml, "localhost", Parser.xmlParser());
					Elements paramEls = paramsTree.getElementsByTag(PARAMETER);
					for (Element paramEl : paramEls) {
						String caption = StringUtils.trim(paramEl.getElementsByTag(NAME).first().ownText());
						String value = StringUtils.trim(paramEl.getElementsByTag(VALUE).first().ownText());
						xml.startElement(PARAM_ELEMENT, NAME_ATTR, caption).addText(value).endElement();
					}
				}
			}

			// Закрыть offer
			xml.endElement();
			//processed++;
			//setProcessed(processed);
		}
		List<Item> subCats = new ItemQuery(SECTION_ITEM).setParentId(category.getId(), false).loadItems();
		for (Item subCat : subCats) {
			processCategoryProducts(subCat);
		}
	}
}
