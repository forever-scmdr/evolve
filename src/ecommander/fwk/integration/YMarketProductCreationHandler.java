package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ResizeImagesFactory;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.*;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.util.*;

public class YMarketProductCreationHandler extends DefaultHandler implements CatalogConst {

	private static final HashSet<String> SINGLE_PARAMS = new HashSet<>();
	private static final HashSet<String> MULTIPLE_PARAMS = new HashSet<>();
	static {
		SINGLE_PARAMS.add(URL_ELEMENT);
		SINGLE_PARAMS.add(PRICE_ELEMENT);
		SINGLE_PARAMS.add(CURRENCY_ID_ELEMENT);
		//SINGLE_PARAMS.add(CATEGORY_ID_ELEMENT);
		SINGLE_PARAMS.add(NAME_ELEMENT);
		SINGLE_PARAMS.add(VENDOR_CODE_ELEMENT);
		SINGLE_PARAMS.add(DESCRIPTION_ELEMENT);
		SINGLE_PARAMS.add(COUNTRY_OF_ORIGIN_ELEMENT);
		SINGLE_PARAMS.add(MODEL_ELEMENT);
		SINGLE_PARAMS.add(QUANTITY_ELEMENT);
		SINGLE_PARAMS.add(VENDOR_ELEMENT);
		SINGLE_PARAMS.add(OLDPRICE_ELEMENT);
		SINGLE_PARAMS.add(OPTPRICE_ELEMENT);
		SINGLE_PARAMS.add(OLDOPTPRICE_ELEMENT);
		SINGLE_PARAMS.add(MIN_QUANTITY_ELEMENT);
		SINGLE_PARAMS.add(STATUS_ELEMENT);

		MULTIPLE_PARAMS.add(CATEGORY_ID_ELEMENT);
		MULTIPLE_PARAMS.add(PICTURE_ELEMENT);
		MULTIPLE_PARAMS.add(ANALOG_ELEMENT);
		MULTIPLE_PARAMS.add(SIMILAR_ITEMS_ELEMENT);
		MULTIPLE_PARAMS.add(SUPPORT_ITEMS_ELEMENT);
	}


	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();

	private HashMap<String, Item> sections = null;

	private IntegrateBase.Info info; // информация для пользователя
	private HashMap<String, String> singleParams;
	private HashMap<String, LinkedHashSet<String>> multipleParams;
	private LinkedHashMap<String, String> specialParams;
	private ItemType productType;
	private ItemType paramsXmlType;
	private User initiator;
	private boolean isInsideOffer = false;
	private boolean getPrice = false;
	private Assoc catalogLinkAssoc;

	
	public YMarketProductCreationHandler(HashMap<String, Item> sections, IntegrateBase.Info info, User initiator) {
		this.info = info;
		this.sections = sections;
		this.productType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
		this.paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
		this.initiator = initiator;
		this.catalogLinkAssoc = ItemTypeRegistry.getAssoc("catalog_link");
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
				HashSet<String> productContainers = new HashSet<>();
				String code = singleParams.get(ID_ATTR);
				//String secCode = singleParams.get(CATEGORY_ID_ELEMENT);
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, OFFER_ID_PARAM, code);
				boolean isProductNotNew = true;
				LinkedHashSet<String> categoryIds = multipleParams.getOrDefault(CATEGORY_ID_ELEMENT, new LinkedHashSet<>());
				if (product == null) {
					String secCode = "-000-";
					if (categoryIds.size() > 0) {
						secCode = categoryIds.iterator().next();
					}
					Item section = sections.get(secCode);
					isProductNotNew = false;
					if (section != null) {
						product = Item.newChildItem(productType, section);
						productContainers.add(secCode);
					} else {
						info.addError("Не найден раздел с номером " + secCode, locator.getLineNumber(), locator.getColumnNumber());
						return;
					}
				}

				product.setValue(CODE_PARAM, code);
				product.setValue(OFFER_ID_PARAM, code);
				product.setValue(AVAILABLE_PARAM, StringUtils.equalsIgnoreCase(singleParams.get(AVAILABLE_ATTR), TRUE_VAL) ? (byte) 1 : (byte) 0);
				product.setValueUI(QTY_PARAM, singleParams.get(QUANTITY_ELEMENT));
				product.setValue(GROUP_ID_PARAM, singleParams.get(GROUP_ID_ATTR));
				product.setValue(URL_PARAM, singleParams.get(URL_ELEMENT));
				if (product.getItemType().hasParameter(CURRENCY_ID_PARAM))
					product.setValue(CURRENCY_ID_PARAM, singleParams.get(CURRENCY_ID_ELEMENT));
				if (product.getItemType().hasParameter(CATEGORY_ID_PARAM))
					product.setValue(CATEGORY_ID_PARAM, singleParams.get(CATEGORY_ID_ELEMENT));
				product.setValue(NAME_PARAM, singleParams.get(NAME_ELEMENT));
				if (product.isValueEmpty(NAME_PARAM))
					product.setValue(NAME_PARAM, singleParams.get(MODEL_ELEMENT));
				if (product.getItemType().hasParameter(VENDOR_CODE_PARAM))
					product.setValue(VENDOR_CODE_PARAM, singleParams.get(VENDOR_CODE_ELEMENT));
				if (product.getItemType().hasParameter(VENDOR_PARAM))
					product.setValue(VENDOR_PARAM, singleParams.get(VENDOR_ELEMENT));
				if (product.getItemType().hasParameter(DESCRIPTION_PARAM))
					product.setValue(DESCRIPTION_PARAM, singleParams.get(DESCRIPTION_ELEMENT));
				if (product.getItemType().hasParameter(VENDOR_PARAM))
					product.setValue(VENDOR_PARAM, singleParams.get(VENDOR_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(OPTPRICE_ELEMENT, singleParams.get(OPTPRICE_ELEMENT));
				if (product.getItemType().hasParameter(PRICE_OLD_PARAM))
					product.setValue(PRICE_OLD_PARAM, singleParams.get(OLDPRICE_ELEMENT));
				if (product.getItemType().hasParameter(PRICE_OPT_OLD_PARAM))
					product.setValue(PRICE_OPT_OLD_PARAM, singleParams.get(OLDOPTPRICE_ELEMENT));
				if (product.getItemType().hasParameter(MIN_QTY_PARAM))
					product.setValueUI(MIN_QTY_PARAM, singleParams.get(MIN_QUANTITY_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValue(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));



				if (getPrice)
					product.setValueUI(PRICE_PARAM, singleParams.get(PRICE_ELEMENT));
				else
					product.setValueUI(PRICE_PARAM, "0");

				// Качать картинки только для новых товаров
				boolean wasNew = product.isNew();

				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex());

				// Удалить айтемы с параметрами продукта, если продукт ранее уже существовал
				if (isProductNotNew) {
					List<Item> paramsXmls = new ItemQuery(paramsXmlType.getName()).setParentId(product.getId(), false).loadItems();
					for (Item paramsXml : paramsXmls) {
						DelayedTransaction.executeSingle(initiator, ItemStatusDBUnit.delete(paramsXml));
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

				if (isProductNotNew) {
					// Загрузить разделы, содержащие товар
					List<Item> secs = new ItemQuery(SECTION_ITEM).setChildId(product.getId(), false,
							ItemTypeRegistry.getPrimaryAssoc().getName(), catalogLinkAssoc.getName()).loadItems();
					for (Item sec : secs) {
						productContainers.add(sec.getStringValue(CATEGORY_ID_PARAM));
					}
				}

				// Создать ассоциацию товара с разделом, если ее еще не существует
				categoryIds.removeAll(productContainers);
				for (String categoryId : categoryIds) {
					Item section = sections.get(categoryId);
					if (section != null)
						DelayedTransaction.executeSingle(initiator,
								CreateAssocDBUnit.childExistsSoft(product, section, catalogLinkAssoc.getId()));
				}


				boolean needSave = false;
				LinkedHashSet<String> picUrls = multipleParams.getOrDefault(PICTURE_ELEMENT, new LinkedHashSet<>());
				if (wasNew) {
					for (String picUrl : picUrls) {
						product.setValue(GALLERY_PARAM, new URL(picUrl));
						needSave = true;
					}
				}

				// Генерация маленького изображения
				boolean noMainPic = product.isValueEmpty(MAIN_PIC_PARAM);
				if (noMainPic && picUrls.size() > 0) {
					if (picUrls.size() > 0) {
						product.setValue(MAIN_PIC_PARAM, new URL(picUrls.iterator().next()));
					}
					try {
						DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());
						DelayedTransaction.executeSingle(initiator, new ResizeImagesFactory.ResizeImages(product));
						DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex());
					} catch (Exception e) {
						info.addError("Some error while saving files", product.getStringValue(NAME_PARAM));
					}
					needSave = false;
				}
				if (needSave) {
					try {
						DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());
					} catch (Exception e) {
						info.addError("Some error while saving files", product.getStringValue(NAME_PARAM));
					}
				}

				info.increaseProcessed();
				isInsideOffer = false;
			}

			else if (isInsideOffer && SINGLE_PARAMS.contains(qName) && parameterReady) {
				singleParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			else if (isInsideOffer && StringUtils.equalsIgnoreCase(PARAM_ELEMENT, qName) && parameterReady) {
				specialParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			else if (isInsideOffer && MULTIPLE_PARAMS.contains(qName) && parameterReady) {
				LinkedHashSet<String> vals = multipleParams.computeIfAbsent(qName, k -> new LinkedHashSet<>());
				vals.add(paramValue.toString());
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
			singleParams = new HashMap<>();
			specialParams = new LinkedHashMap<>();
			multipleParams = new LinkedHashMap<>();
			singleParams.put(ID_ATTR, attributes.getValue(ID_ATTR));
			singleParams.put(AVAILABLE_ATTR, attributes.getValue(AVAILABLE_ATTR));
			singleParams.put(GROUP_ID_ATTR, attributes.getValue(GROUP_ID_ATTR));
			isInsideOffer = true;
		}
		// Параметры продуктов (общие)
		else if (isInsideOffer &&
				(SINGLE_PARAMS.contains(qName) || StringUtils.equalsAnyIgnoreCase(qName, PICTURE_ELEMENT, CATEGORY_ID_ELEMENT))) {
			paramName = qName;
			parameterReady = true;
		}
		// Пользовательские параметры продуктов
		else if (isInsideOffer && StringUtils.equalsIgnoreCase(PARAM_ELEMENT, qName)) {
			paramName = attributes.getValue(NAME_ATTR);
			parameterReady = true;
		}
	}

	public void getPrice(boolean getPrice) {
		this.getPrice = getPrice;
	}
}
