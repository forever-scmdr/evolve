package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class BelchipYandexProductCreationHandler extends DefaultHandler implements CatalogConst {

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
		SINGLE_PARAMS.add(QUANTITY_OPT_ELEMENT);
		SINGLE_PARAMS.add(VENDOR_ELEMENT);
		SINGLE_PARAMS.add(OLDPRICE_ELEMENT);
		SINGLE_PARAMS.add(OPTPRICE_ELEMENT);
		SINGLE_PARAMS.add(OLDOPTPRICE_ELEMENT);
		SINGLE_PARAMS.add(MIN_QUANTITY_ELEMENT);
		SINGLE_PARAMS.add(MIN_QTY_PARAM);
		SINGLE_PARAMS.add(STATUS_ELEMENT);
		SINGLE_PARAMS.add(NEXT_DELIVERY_ELEMENT);

		MULTIPLE_PARAMS.add(CATEGORY_ID_ELEMENT);
		MULTIPLE_PARAMS.add(PICTURE_ELEMENT);
		MULTIPLE_PARAMS.add(ANALOG_ELEMENT);
		MULTIPLE_PARAMS.add("download");
		MULTIPLE_PARAMS.add("related");
	}

	private final BigDecimal quotient;
	private final Map<String, BigDecimal> specialQuotients = new HashMap<>();


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

	public BelchipYandexProductCreationHandler(HashMap<String, Item> sections, IntegrateBase.Info info, User initiator) throws Exception {
		this.info = info;
		this.sections = sections;
		this.productType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
		this.paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
		this.initiator = initiator;
		this.catalogLinkAssoc = ItemTypeRegistry.getAssoc("catalog_link");
		Item belchipQuotients = ItemQuery.loadSingleItemByName("belchip_quotients");
		quotient = belchipQuotients.getDecimalValue("belchip_q", BigDecimal.ONE);
		initQuotients(belchipQuotients);
	}

	private void initQuotients(Item belchipQuotients) {
		String specialSections = belchipQuotients.getStringValue("add_q");
		if (StringUtils.isNotBlank(specialSections)) {
			for (String q : specialSections.split("[\\n;]")) {
				String[] pair = q.split("=");
				BigDecimal qtn = new BigDecimal(pair[1].trim().replace(',', '.'));
				specialQuotients.put(pair[0].trim(), qtn);
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
				HashSet<String> productContainers = new HashSet<>();
				String code = singleParams.get(ID_ATTR);
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, OFFER_ID_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
				boolean isExistingProduct = product != null;
				LinkedHashSet<String> categoryIds = multipleParams.getOrDefault(CATEGORY_ID_ELEMENT, new LinkedHashSet<>());
				String secCode = "-000-";
				if (categoryIds.size() > 0) {
					secCode = categoryIds.iterator().next();
				}
				if (!isExistingProduct) {
					Item section = sections.get(secCode);
					if (section != null) {
						product = Item.newChildItem(productType, section);
						productContainers.add(secCode);
					} else {
						info.addError("Не найден раздел с номером " + secCode, locator.getLineNumber(), locator.getColumnNumber());
						return;
					}
				}
				product.setValue(CODE_PARAM, "b_" + code);
				product.setValue(OFFER_ID_PARAM, code);
				product.setValue(AVAILABLE_PARAM, StringUtils.equalsIgnoreCase(singleParams.get(AVAILABLE_ATTR), TRUE_VAL) ? (byte) 1 : (byte) 0);
				product.setValueUI(QTY_PARAM, singleParams.get(QUANTITY_ELEMENT));
				product.setValueUI(GROUP_ID_PARAM, singleParams.get(GROUP_ID_ATTR));
				product.setValueUI(URL_PARAM, singleParams.get(URL_ELEMENT));
				if (product.getItemType().hasParameter(CURRENCY_ID_PARAM))
					product.setValueUI(CURRENCY_ID_PARAM, singleParams.get(CURRENCY_ID_ELEMENT));
				if (product.getItemType().hasParameter(CATEGORY_ID_PARAM))
					product.setValueUI(CATEGORY_ID_PARAM, singleParams.get(CATEGORY_ID_ELEMENT));
				product.setValueUI(NAME_PARAM, singleParams.get(NAME_ELEMENT));
				if (product.isValueEmpty(NAME_PARAM))
					product.setValueUI(NAME_PARAM, singleParams.get(MODEL_ELEMENT));
				if (product.getItemType().hasParameter(VENDOR_CODE_PARAM))
					product.setValueUI(VENDOR_CODE_PARAM, singleParams.get(VENDOR_CODE_ELEMENT));
				if (product.getItemType().hasParameter(VENDOR_PARAM))
					product.setValueUI(VENDOR_PARAM, singleParams.get(VENDOR_ELEMENT));
				if (product.getItemType().hasParameter(DESCRIPTION_PARAM))
					product.setValueUI(DESCRIPTION_PARAM, singleParams.get(DESCRIPTION_ELEMENT));
				if (product.getItemType().hasParameter(COUNTRY_PARAM))
					product.setValueUI(COUNTRY_PARAM, singleParams.get(COUNTRY_OF_ORIGIN_ELEMENT));
				if (product.getItemType().hasParameter(PRICE_OPT_PARAM))
					product.setValue(PRICE_OPT_PARAM, calculatePrice(singleParams.get(OPTPRICE_ELEMENT), secCode));
				if (product.getItemType().hasParameter(PRICE_OLD_PARAM))
					product.setValue(PRICE_OLD_PARAM, calculatePrice(singleParams.get(OLDPRICE_ELEMENT), secCode));
				if (product.getItemType().hasParameter(PRICE_OPT_OLD_PARAM))
					product.setValue(PRICE_OPT_OLD_PARAM, calculatePrice(singleParams.get(OLDOPTPRICE_ELEMENT), secCode));
				product.setValue(PRICE_PARAM, calculatePrice(singleParams.get(PRICE_ELEMENT), secCode));
				if (product.getItemType().hasParameter(MIN_QTY_PARAM)) {
					String minQty = singleParams.get(MIN_QUANTITY_ELEMENT);
					if (StringUtils.isBlank(minQty))
						minQty = singleParams.get(MIN_QTY_PARAM);
					product.setValueUI(MIN_QTY_PARAM, minQty);
				}
				if (product.getItemType().hasParameter(TAG_PARAM)) {
					product.clearValue(TAG_PARAM);
					product.setValueUI(TAG_PARAM, singleParams.get(STATUS_ELEMENT));
				}
				if (product.getItemType().hasParameter(NEXT_DELIVERY_PARAM))
					product.setValueUI(NEXT_DELIVERY_PARAM, singleParams.get(NEXT_DELIVERY_ELEMENT));

				if (product.getItemType().hasParameter("assoc_code") && multipleParams.containsKey("related")) {
					product.clearValue("assoc_code");
					for (String val : multipleParams.get("related")) {
						String[] parts = StringUtils.split(val, ',');
						for (String part : parts) {
							product.setValueUI("assoc_code", "b_" + StringUtils.trim(part));
						}
					}
				}
				if (product.getItemType().hasParameter(ANALOG_CODE_PARAM) && multipleParams.containsKey(ANALOG_ELEMENT)) {
					product.clearValue(ANALOG_CODE_PARAM);
					for (String val : multipleParams.get(ANALOG_ELEMENT)) {
						String[] parts = StringUtils.split(val, ',');
						for (String part : parts) {
							product.setValueUI(ANALOG_CODE_PARAM, "b_" + StringUtils.trim(part));
						}
					}
				}
				if (product.getItemType().hasParameter("pdf") && multipleParams.containsKey("download")) {
					product.clearValue("pdf");
					for (String val : multipleParams.get("download")) {
						String[] parts = StringUtils.split(val, ',');
						for (String part : parts) {
							product.setValueUI("pdf", StringUtils.trim(part));
						}
					}
				}

				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex());
				//hide if not available. Restore if available.
				PersistenceCommandUnit itemStatusCommand = product.getByteValue(AVAILABLE_PARAM) > 0 ? ItemStatusDBUnit.restore(product.getId()).noFulltextIndex().ignoreUser(true) : ItemStatusDBUnit.hide(product.getId()).noFulltextIndex().ignoreUser(true);
				DelayedTransaction.executeSingle(initiator, itemStatusCommand);

				// Удалить айтемы с параметрами продукта, если продукт ранее уже существовал
				if (isExistingProduct) {
					List<Item> paramsXmls = new ItemQuery(paramsXmlType.getName(), Item.STATUS_NORMAL, Item.STATUS_HIDDEN).setParentId(product.getId(), false).loadItems();
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

				if (isExistingProduct) {
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
				ArrayList<File> galleryPics = product.getFileValues(GALLERY_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
				for (File galleryPic : galleryPics) {
					if (!galleryPic.isFile()) {
						product.removeEqualValue(GALLERY_PARAM, galleryPic.getName());
					}
				}
				LinkedHashSet<String> picUrls = multipleParams.getOrDefault(PICTURE_ELEMENT, new LinkedHashSet<>());
				for (String picUrl : picUrls) {
					try {
						String fileName = Strings.getFileName(picUrl);
						if (!product.containsValue(GALLERY_PARAM, fileName) && !product.containsValue(GALLERY_PARAM, GALLERY_PARAM + "_" + fileName)) {
							product.setValue(GALLERY_PARAM, new URL(picUrl));
							needSave = true;
						}
					} catch (Exception e) {
						info.addError("Неверный формат картинки: " + picUrl, picUrl);
					}
				}

				// Генерация маленького изображения
				boolean noMainPic = product.isValueEmpty(MAIN_PIC_PARAM);
				if (!noMainPic) {
					File mainPic = product.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
					if (!mainPic.exists()) {
						product.clearValue(MAIN_PIC_PARAM);
						noMainPic = true;
					}
				}
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
			} else if (isInsideOffer && SINGLE_PARAMS.contains(qName) && parameterReady) {
				singleParams.put(paramName, StringUtils.trim(paramValue.toString()));
			} else if (isInsideOffer && StringUtils.equalsIgnoreCase(PARAM_ELEMENT, qName) && parameterReady) {
				specialParams.put(paramName, StringUtils.trim(paramValue.toString()));
			} else if (isInsideOffer && MULTIPLE_PARAMS.contains(qName) && parameterReady) {
				LinkedHashSet<String> vals = multipleParams.computeIfAbsent(qName, k -> new LinkedHashSet<>());
				if (StringUtils.isNotBlank(StringUtils.trim(paramValue.toString())))
					vals.add(paramValue.toString());
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.setLineNumber(locator.getLineNumber());
			info.setLinePosition(locator.getColumnNumber());
			info.addError(e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (parameterReady)
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
		else if (isInsideOffer && (SINGLE_PARAMS.contains(qName) || MULTIPLE_PARAMS.contains(qName))) {
			paramName = qName;
			parameterReady = true;
		}
		// Пользовательские параметры продуктов
		else if (isInsideOffer && StringUtils.equalsIgnoreCase(PARAM_ELEMENT, qName)) {
			paramName = attributes.getValue(NAME_ATTR);
			parameterReady = true;
		}
	}


	private BigDecimal calculatePrice(String value, String secCode) {
		BigDecimal price = DecimalDataType.parse(value, 2);
		if (price != null) {
			price = price.multiply(quotient);
			if (specialQuotients.containsKey(secCode)) {
				price = price.multiply(specialQuotients.get(secCode));
			}
		}
		return price;
	}

}
