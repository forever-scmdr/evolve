package ecommander.fwk.integration;

import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class YMarketProductCreationHandler extends DefaultHandler implements CatalogConst {

	private static final HashSet<String> COMMON_PARAMS = new HashSet<>();
	static {
		COMMON_PARAMS.add(URL_ELEMENT);
		COMMON_PARAMS.add(PRICE_ELEMENT);
		COMMON_PARAMS.add(CURRENCY_ID_ELEMENT);
		COMMON_PARAMS.add(CATEGORY_ID_ELEMENT);
		COMMON_PARAMS.add(NAME_ELEMENT);
		COMMON_PARAMS.add(VENDOR_CODE_ELEMENT);
		COMMON_PARAMS.add(DESCRIPTION_ELEMENT);
		COMMON_PARAMS.add(COUNTRY_OF_ORIGIN_ELEMENT);
	}


	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();

	private HashMap<String, Item> sections = null;

	private IntegrateBase.Info info; // информация для пользователя
	private HashMap<String, String> commonParams;
	private LinkedHashMap<String, String> specialParams;
	private ItemType productType;
	private ItemType paramsXmlType;
	private ArrayList<String> picUrls;
	private User initiator;
	boolean isInsideOffer = false;

	
	public YMarketProductCreationHandler(HashMap<String, Item> sections, IntegrateBase.Info info, User initiator) {
		this.info = info;
		this.sections = sections;
		this.productType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
		this.paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
		this.initiator = initiator;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
				String code = commonParams.get(ID_ATTR);
				String secCode = commonParams.get(CATEGORY_ID_ELEMENT);
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, OFFER_ID_PARAM, code);
				if (product == null) {
					Item section = sections.get(secCode);
					if (section != null) {
						product = Item.newChildItem(productType, section);
					} else {
						info.addError("Не найден раздел с номером " + secCode, locator.getLineNumber(), locator.getColumnNumber());
						return;
					}
				}

				product.setValue(CODE_PARAM, code);
				product.setValue(OFFER_ID_PARAM, code);
				product.setValue(AVAILABLE_PARAM, StringUtils.equalsIgnoreCase(commonParams.get(AVAILABLE_ATTR), TRUE_VAL) ? (byte) 1 : (byte) 0);
				product.setValue(GROUP_ID_PARAM, commonParams.get(GROUP_ID_ATTR));
				product.setValue(URL_PARAM, commonParams.get(URL_ELEMENT));
				product.setValue(CURRENCY_ID_PARAM, commonParams.get(CURRENCY_ID_ELEMENT));
				product.setValue(CATEGORY_ID_PARAM, commonParams.get(CATEGORY_ID_ELEMENT));
				product.setValue(NAME_PARAM, commonParams.get(NAME_ELEMENT));
				product.setValue(VENDOR_CODE_PARAM, commonParams.get(VENDOR_CODE_ELEMENT));
				product.setValue(DESCRIPTION_PARAM, commonParams.get(DESCRIPTION_ELEMENT));
				product.setValue(COUNTRY_PARAM, commonParams.get(COUNTRY_OF_ORIGIN_ELEMENT));

				//product.setValueUI(PRICE_PARAM, commonParams.get(PRICE_ELEMENT));
				product.setValueUI(PRICE_PARAM, "2");

				// Качать картинки только для новых товаров
				if (product.isNew()) {
					for (String picUrl : picUrls) {
						product.setValue(GALLERY_PARAM, new URL(picUrl));
					}
				}

				boolean noMainPic = product.isValueEmpty(MAIN_PIC_PARAM);
				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex());

				// Генерация маленького изображения
				if (noMainPic) {
					if (picUrls.size() > 0) {
						product.setValue(MAIN_PIC_PARAM, new URL(picUrls.get(0)));
					}
					DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());
					try {
						DelayedTransaction.executeSingle(initiator, new ResizeImagesFactory.ResizeImages(product));
						DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex());
					} catch (Exception e) {
						info.addError("Some error while saving files", product.getStringValue(NAME_PARAM));
					}
				}

				// Создать айтем с параметрами продукта
				if (specialParams.size() > 0) {
					XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
					for (String name : specialParams.keySet()) {
						String value = specialParams.get(name);
						xml.startElement(PARAMETER)
								.startElement(NAME).addText(name).endElement()
								.startElement(VALUE).addText(value).endElement()
								.endElement();
					}

					Item paramsXml = Item.newChildItem(paramsXmlType, product);
					paramsXml.setValue(XML_PARAM, xml.toString());
					DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(paramsXml).ignoreFileErrors());
				}

				info.increaseProcessed();
				isInsideOffer = false;
			}

			else if (isInsideOffer && COMMON_PARAMS.contains(qName) && parameterReady) {
				commonParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			else if (isInsideOffer && StringUtils.equalsIgnoreCase(PARAM_ELEMENT, qName) && parameterReady) {
				specialParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			else if (isInsideOffer && StringUtils.equalsIgnoreCase(qName, PICTURE_ELEMENT)) {
				picUrls.add(paramValue.toString());
			}

			parameterReady = false;
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(parameterReady)
			paramValue.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		parameterReady = false;
		paramValue = new StringBuilder();
		// Продукт
		if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
			commonParams = new HashMap<>();
			specialParams = new LinkedHashMap<>();
			picUrls = new ArrayList<>();
			commonParams.put(ID_ATTR, attributes.getValue(ID_ATTR));
			commonParams.put(AVAILABLE_ATTR, attributes.getValue(AVAILABLE_ATTR));
			commonParams.put(GROUP_ID_ATTR, attributes.getValue(GROUP_ID_ATTR));
			isInsideOffer = true;
		}
		// Параметры продуктов (общие)
		else if (isInsideOffer && (COMMON_PARAMS.contains(qName) || StringUtils.equalsIgnoreCase(qName, PICTURE_ELEMENT))) {
			paramName = qName;
			parameterReady = true;
		}
		// Пользовательские параметры продуктов
		else if (isInsideOffer && StringUtils.equalsIgnoreCase(PARAM_ELEMENT, qName)) {
			paramName = Strings.createXmlElementName(attributes.getValue(NAME_ATTR));
			parameterReady = true;
		}
	}
}
