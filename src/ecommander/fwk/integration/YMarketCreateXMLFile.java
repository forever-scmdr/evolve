package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ParameterDescription;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

	@Override
	public ResultPE execute() throws Exception {
		name = getVarSingleValue(NAME_ELEMENT);
		company = getVarSingleValue(COMPANY_ELEMENT);
		catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, User.getDefaultUser(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		return createYandex();
	}

	private ResultPE createYandex() throws EcommanderException {
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
		BigDecimal zero = new BigDecimal(0);
		for (Item baseProduct : products) {
			List<Item> subProducts;
			boolean hasLines = baseProduct.getByteValue(HAS_LINE_PRODUCTS, (byte)0) == (byte) 1;
			if (hasLines) {
				subProducts = new ItemQuery(LINE_PRODUCT_ITEM).setParentId(baseProduct.getId(), false).loadItems();
			} else {
				subProducts = new ArrayList<>(1);
				subProducts.add(baseProduct);
			}

			// Пользовательские параметры
			Item baseParams = new ItemQuery(PARAMS_ITEM).setParentId(baseProduct.getId(), false).loadFirstItem();

			for (Item product : subProducts) {
				BigDecimal price = product.getDecimalValue(PRICE_PARAM, zero);
				String avail = price.doubleValue() <= 0.001d ? "false" : "true";
				xml.startElement(OFFER_ELEMENT, ID_ATTR, product.getStringValue(CODE_PARAM), AVAILABLE_ATTR, avail);
				String url = getUrlBase() + "/" + baseProduct.getKeyUnique() + "/";
				xml.startElement(URL_ELEMENT).addText(url).endElement();
				xml.startElement(PRICE_ELEMENT).addText(product.getDecimalValue(PRICE_PARAM)).endElement();
				xml.startElement(CURRENCY_ID_ELEMENT).addText("BYN").endElement();
				xml.startElement(CATEGORY_ID_ELEMENT).addText(category.getId()).endElement();
				String name = baseProduct.getStringValue(NAME_PARAM);
				if (hasLines)
					name += " " + product.getStringValue(NAME_PARAM);
				if (baseProduct.isValueNotEmpty(TYPE_PARAM))
					name = baseProduct.getStringValue(TYPE_PARAM) + " " + name;
				xml.startElement(NAME_ELEMENT).addText(name).endElement();
				if (baseProduct.isValueNotEmpty(VENDOR_CODE_PARAM))
					xml.startElement(VENDOR_CODE_ELEMENT).addText(baseProduct.getStringValue(VENDOR_CODE_PARAM)).endElement();
				xml.startElement(MODEL_ELEMENT).addText(baseProduct.getStringValue(NAME_PARAM)).endElement();
				String text = baseProduct.getStringValue(TEXT_PARAM);
				if (StringUtils.isBlank(text))
					text = baseProduct.getStringValue(DESCRIPTION_PARAM);
				if (StringUtils.isNotBlank(text)) {
					Document doc = Jsoup.parse(text);
					xml.startElement(DESCRIPTION_ELEMENT).addText(doc.body().text()).endElement();
				}

				// Галерея
				boolean hasGallery = false;
				for (String picName : baseProduct.outputValues(GALLERY_PARAM)) {
					xml.startElement("picture").addText(getUrlBase() + "/" + AppContext.getFilesUrlPath(false) +
							Item.createItemFilesPath(baseProduct.getId()) + picName).endElement();
					hasGallery = true;
				}
				if (!hasGallery && baseProduct.isValueNotEmpty(MAIN_PIC_PARAM)) {
					xml.startElement("picture").addText(getUrlBase() + "/" + AppContext.getFilesUrlPath(false) +
							Item.createItemFilesPath(baseProduct.getId()) + baseProduct.outputValue(MAIN_PIC_PARAM)).endElement();
				}

				Item subParams = null;
				if (hasLines)
					subParams = new ItemQuery(PARAMS_ITEM).setParentId(product.getId(), false).loadFirstItem();

				if (baseParams != null) {
					for (ParameterDescription paramDesc : baseParams.getItemType().getParameterList()) {
						String value = baseParams.outputValue(paramDesc.getName());
						if (hasLines && subParams != null) {
							String subValue = subParams.outputValue(paramDesc.getName());
							if (StringUtils.isNotBlank(subValue))
								value = subValue;
						}
						xml.startElement(PARAM_ELEMENT, NAME_ATTR, paramDesc.getCaption()).addText(value).endElement();
					}
				}

				// Закрыть offer
				xml.endElement();
				//processed++;
				//setProcessed(processed);
			}
		}
		List<Item> subCats = new ItemQuery(SECTION_ITEM).setParentId(category.getId(), false).loadItems();
		for (Item subCat : subCats) {
			processCategoryProducts(subCat);
		}
	}
}
