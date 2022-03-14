package ecommander.fwk.external_shops.ruelectronics;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.external_shops.ExternalShopPriceCalculator;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RuelectronicsProductHandler extends DefaultHandler implements CatalogConst {
	private static final HashMap<String, String> PARAM_NAMES = new HashMap<>();
	private static final String PARAMS_EL = "techinfo";
	private static final String PARAM_EL = "parameter";
	private static final String NAME_EL = "name";
	private static final String VALUE_EL = "value";

	static {
		PARAM_NAMES.put("article", CODE_PARAM);
		PARAM_NAMES.put("product_name", NAME_PARAM);
		PARAM_NAMES.put("subgroup_name", NAME_EXTRA_PARAM);
		PARAM_NAMES.put("brand", VENDOR_ELEMENT);
		PARAM_NAMES.put("quant", QTY_PARAM);
		PARAM_NAMES.put("link_photo", "pic_link");
		PARAM_NAMES.put("price", PRICE_ORIGINAL_PARAM);
		PARAM_NAMES.put("optprice", PRICE_OPT_PARAM);
		PARAM_NAMES.put("vipprice", PRICE_OPT_OLD_PARAM);
		PARAM_NAMES.put("link_scheme", "pic_link");
		PARAM_NAMES.put("link_file", "pic_link");
		PARAM_NAMES.put(VALUE_EL, "");
		PARAM_NAMES.put(PARAM_EL, "");
		PARAM_NAMES.put(NAME, "");
	}

	private Item catalog;
	private Item currency;
	private IntegrateBase.Info info;
	private User user;

	private Locator locator;
	private boolean needSaveTagValue = false;
	private boolean isInsideParam = false;
	private StringBuilder paramValue = new StringBuilder();
	private XmlDocumentBuilder paramsXmlBuilder = XmlDocumentBuilder.newDocPart();
	private HashMap<String, String> singleParamsMap;
	private LinkedHashSet<String> picSet;
	private int x = 0;


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		paramValue = new StringBuilder();
		needSaveTagValue = false;

		if (qName.equalsIgnoreCase(PARAMS_EL)) {
			paramsXmlBuilder.startElement("p");
		} else if (PARAM_EL.equalsIgnoreCase(qName)) {
			isInsideParam = true;
		} else if (isInsideParam && NAME.equalsIgnoreCase(qName)) {
			paramsXmlBuilder.startElement("strong");
		}
		if (PRODUCT_ITEM.equalsIgnoreCase(qName)) {
			singleParamsMap = new HashMap<>();
			picSet = new LinkedHashSet<>();
			paramsXmlBuilder = XmlDocumentBuilder.newDocPart();
		}
		if (PARAM_NAMES.containsKey(qName)) {
			needSaveTagValue = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (PRODUCT_ITEM.equalsIgnoreCase(qName)) {
				String code = singleParamsMap.get(CODE_PARAM);
				List<Item> products = loadByCode(code, Item.STATUS_HIDDEN);
				products = products.size() > 0? products : loadByCode(code, Item.STATUS_NORMAL);
				Item product = null;
				if (products.size() > 0) {
					product = products.remove(0);
					if(product.getStatus() == Item.STATUS_HIDDEN) {
						DelayedTransaction.executeSingle(user, ItemStatusDBUnit.restore(product).ignoreUser().noFulltextIndex());
					}
				}
				for (Item p : products) {
					DelayedTransaction.executeSingle(user, ItemStatusDBUnit.delete(p).ignoreUser().noFulltextIndex());
					info.setToProcess(++x);
				}

				product = product == null ? ItemUtils.newChildItem(PRODUCT_ITEM, catalog) : product;

				for (Map.Entry<String, String> entry : singleParamsMap.entrySet()) {
					product.setValueUI(entry.getKey(), entry.getValue());
				}
				for (String picLink : picSet) {
					product.setValueUI("pic_link", picLink);
				}

				setBynPrice(product);
				setSearchString(product);

				product.setValueUI(TAG_PARAM, "external_shop");
				product.setValueUI(TAG_PARAM, "ruelectronics.com");
				if (!"<p></p>".equalsIgnoreCase(paramsXmlBuilder.toString())) {
					product.setValueUI(DESCRIPTION_PARAM, paramsXmlBuilder.toString());
				}

				DelayedTransaction.executeSingle(user, SaveItemDBUnit.get(product).ignoreUser().noFulltextIndex());
				info.increaseProcessed();

			} else if (PARAMS_EL.equalsIgnoreCase(qName)) {
				paramsXmlBuilder.endElement();
			} else if (PARAM_EL.equalsIgnoreCase(qName)) {
				paramsXmlBuilder.addEmptyElement("br");
				isInsideParam = false;
			} else if (NAME_EL.equalsIgnoreCase(qName) && isInsideParam) {
				paramsXmlBuilder.addText(StringUtils.normalizeSpace(paramValue.toString())).endElement();
			} else if (VALUE_EL.equalsIgnoreCase(qName)) {
				paramsXmlBuilder.addText(StringUtils.normalizeSpace(paramValue.toString()));
			} else {
				String paramName = PARAM_NAMES.get(qName);
				if (StringUtils.isBlank(paramName)) {
					return;
				}
				if ("pic_link".equalsIgnoreCase(paramName)) {
					picSet.add(paramValue.toString());
				} else {
					singleParamsMap.put(paramName, StringUtils.normalizeSpace(paramValue.toString()));
				}
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.setLineNumber(locator.getLineNumber());
			info.setLinePosition(locator.getColumnNumber());
			info.addError(e);
		}
	}

	private List<Item> loadByCode(String code, byte status) throws Exception {
		ItemQuery query = new ItemQuery(PRODUCT_ITEM, status);
		query.setParentId(catalog.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
		query.addParameterCriteria(CODE_PARAM, code, "=", null, Compare.SOME);
		return query.loadItems();
	}

	private void setSearchString(Item product) throws Exception {
		StringBuilder search = new StringBuilder();
		search.append(product.getValue(NAME_EXTRA_PARAM)).append(' ');
		search.append(product.getValue(NAME)).append(' ');
		if (paramsXmlBuilder.length() > 0 && !"<p></p>".equalsIgnoreCase(paramsXmlBuilder.toString())) {
			String content = paramsXmlBuilder.toString();
			content = content.replaceAll("<\\/strong>", " ");
			content = content.replaceAll("<\\/?\\w+>", "");
			content = content.replaceAll("<br/>", " ");
			search.append(content);
		}
		product.setValueUI(SEARCH_PARAM, StringUtils.normalizeSpace(search.toString()));
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (needSaveTagValue)
			paramValue.append(ch, start, length);
	}

	private void setBynPrice(Item product) {
		BigDecimal priceRur = product.getDecimalValue(PRICE_OPT_PARAM, BigDecimal.ZERO);
		BigDecimal priceByn = ExternalShopPriceCalculator.convertToByn(priceRur, currency, catalog);
		product.setValue(PRICE_PARAM, priceByn);
	}

	public RuelectronicsProductHandler(Item catalog, Item currency, IntegrateBase.Info info, User user) {
		this.catalog = catalog;
		this.currency = currency;
		this.info = info;
		this.user = user;
		info.setProcessed(0);
	}
}
