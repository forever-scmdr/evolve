package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class YMarketProductCreationHandler extends DefaultHandler implements CatalogConst {

	private static final HashSet<String> COMMON_PARAMS = new HashSet<>();

	private static final String TITLE = "title";
	private static final String ARTIST = "artist";
	private static final String STARRING = "starring";
	private static final String DIRECTOR = "director";
	private static final String AUTHOR = "author";
	private static final String SERIES = "series";
	private static final String MEDIA = "media";
	private static final String YEAR = "year";
	private static final String ORIGINAL_NAME = "originalName";
	private static final String COUNTRY_OF_ORIGIN = "country_of_origin";
	private static final String COUNTRY = "country";
	private static final String PUBLISHER = "publisher";
	private static final String PAGE_EXTENT = "page_extent";
	private static final String LANGUAGE = "language";
	private static final String ISBN = "ISBN";
	private static final String PICTURE = "picture";
	private static final String BARCODE = "barcode";
	private static final String STATUS = "Статус";
	private static final String PUBLISH_TYPE = "Вид издания";
	private static final String BOOKINISTIC = "Букинистическое издание";
	private static final String BOOKINISTIC_1 = "Букинистика";
	private static final String OLD_PRICE_ELEMENT = "oldprice";

	static {
		COMMON_PARAMS.add(URL_ELEMENT);
		COMMON_PARAMS.add(PRICE_ELEMENT);
		COMMON_PARAMS.add(OLD_PRICE_ELEMENT);
		COMMON_PARAMS.add(CURRENCY_ID_ELEMENT);
		COMMON_PARAMS.add(CATEGORY_ID_ELEMENT);
		COMMON_PARAMS.add(NAME_ELEMENT);
		COMMON_PARAMS.add(VENDOR_CODE_ELEMENT);
		COMMON_PARAMS.add(DESCRIPTION_ELEMENT);
		COMMON_PARAMS.add(COUNTRY_OF_ORIGIN_ELEMENT);
		COMMON_PARAMS.add(MODEL_ELEMENT);

		COMMON_PARAMS.add(ARTIST);
		COMMON_PARAMS.add(STARRING);
		COMMON_PARAMS.add(DIRECTOR);
		COMMON_PARAMS.add(AUTHOR);
		COMMON_PARAMS.add(SERIES);
		COMMON_PARAMS.add(MEDIA);
		COMMON_PARAMS.add(YEAR);
		COMMON_PARAMS.add(ORIGINAL_NAME);
		COMMON_PARAMS.add(COUNTRY_OF_ORIGIN);
		COMMON_PARAMS.add(COUNTRY);
		COMMON_PARAMS.add(PUBLISHER);
		COMMON_PARAMS.add(PAGE_EXTENT);
		COMMON_PARAMS.add(LANGUAGE);
		COMMON_PARAMS.add(ISBN);
		COMMON_PARAMS.add(BARCODE);
		COMMON_PARAMS.add(TITLE);
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
//	private ItemType paramsXmlType;
	private ArrayList<String> picUrls;
	private User initiator;
	private boolean isInsideOffer = false;
	private BigDecimal level_1, level_2, quotient_1, quotient_2, quotient_3, quotient_buk;
	private Assoc catalogLinkAssoc;
	private boolean isBookinistic = false;
	private HashSet<String> ignoreCodes;

	
	public YMarketProductCreationHandler(HashMap<String, Item> sections, IntegrateBase.Info info, User initiator, HashSet<String> ignoreCodes) {
		this.info = info;
		this.sections = sections;
		this.productType = ItemTypeRegistry.getItemType("book");
//		this.paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
		this.initiator = initiator;
		this.catalogLinkAssoc = ItemTypeRegistry.getAssoc("catalog_link");
		this.ignoreCodes = ignoreCodes;
		try {
			Item course = new ItemQuery("course").loadFirstItem();
			level_1 = course.getDecimalValue("level_1");
			quotient_1 = course.getDecimalValue("quotient_1");
			level_2 = course.getDecimalValue("level_2");
			quotient_2 = course.getDecimalValue("quotient_2");
			quotient_3 = course.getDecimalValue("quotient_3");
			quotient_buk = course.getDecimalValue("quotient_bukinistic", BigDecimal.ZERO);
		} catch (Exception e) {
			info.addError("Не задан курс российского рубля", "Каталог продукции");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
				String code = commonParams.get(ID_ATTR);
				String secCode = commonParams.get(CATEGORY_ID_ELEMENT);
				Item section = sections.get(secCode);
				// пропустить некоторые разделы
				if (ignoreCodes.contains(secCode)) {
					return;
				}
				//Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, OFFER_ID_PARAM, code);
				Item product = new ItemQuery(PRODUCT_ITEM, Item.STATUS_NORMAL, Item.STATUS_HIDDEN)
						.addParameterEqualsCriteria(OFFER_ID_PARAM, code).loadFirstItem();
				if(product == null && section == null){
					info.addError("Не найден раздел с номером " + secCode, locator.getLineNumber(), locator.getColumnNumber());
					return;
				}
				boolean isProductNew = false;
				if (product == null) {
					//if (section != null) {
						product = Item.newChildItem(productType, section);
						product.setValue("parent_id", secCode);
						isProductNew = true;
//					} else {
//						info.addError("Не найден раздел с номером " + secCode, locator.getLineNumber(), locator.getColumnNumber());
//						return;
//					}
					product.setValue(CODE_PARAM, code);
					product.setValue(OFFER_ID_PARAM, code);
					product.setValue(AVAILABLE_PARAM, StringUtils.equalsIgnoreCase(commonParams.get(AVAILABLE_ATTR), TRUE_VAL) ? (byte) 1 : (byte) 0);
					product.setValue(GROUP_ID_PARAM, commonParams.get(GROUP_ID_ATTR));
					product.setValue(URL_PARAM, commonParams.get(URL_ELEMENT));
					product.setValue(CURRENCY_ID_PARAM, commonParams.get(CURRENCY_ID_ELEMENT));
					product.setValue(CATEGORY_ID_PARAM, commonParams.get(CATEGORY_ID_ELEMENT));
					product.setValue(NAME_PARAM, commonParams.get(NAME_ELEMENT));
					if (product.isValueEmpty(NAME_PARAM))
						product.setValue(NAME_PARAM, commonParams.get(TITLE));
					product.setValue(DESCRIPTION_PARAM, commonParams.get(DESCRIPTION_ELEMENT));
					product.setValue(VENDOR_PARAM, commonParams.get(VENDOR_ELEMENT));

					product.setValue(ARTIST, commonParams.get(ARTIST));
					product.setValue(STARRING, commonParams.get(STARRING));
					product.setValue(DIRECTOR, commonParams.get(DIRECTOR));
					product.setValue(AUTHOR, commonParams.get(AUTHOR));
					product.setValue(SERIES, commonParams.get(SERIES));
					product.setValue(MEDIA, commonParams.get(MEDIA));
					product.setValue(YEAR, commonParams.get(YEAR));
					product.setValue(ORIGINAL_NAME, commonParams.get(ORIGINAL_NAME));
					product.setValue(COUNTRY_OF_ORIGIN, commonParams.get(COUNTRY_OF_ORIGIN));
					product.setValue(COUNTRY, commonParams.get(COUNTRY));
					product.setValue(PUBLISHER, commonParams.get(PUBLISHER));
					product.setValue(PAGE_EXTENT, commonParams.get(PAGE_EXTENT));
					product.setValue(LANGUAGE, commonParams.get(LANGUAGE));
					product.setValue(ISBN, commonParams.get(ISBN));
					product.setValue(VENDOR_CODE_PARAM, commonParams.get(BARCODE));
					product.setValue("tag", commonParams.get(MEDIA));

					for (String picUrl : picUrls) {
						product.setValue(PICTURE, picUrl);
					}
				}

//				String productParentCode = product.getStringValue("parent_id");
//				if(StringUtils.isBlank(productParentCode)){
//					ecommander.fwk.Timer.getTimer().start("loading_parent_section");
//					ItemQuery q = new ItemQuery(SECTION_ITEM, Item.STATUS_NORMAL, Item.STATUS_HIDDEN, Item.STATUS_DELETED);
//					q.setChildId(product.getId(), false);
//					Item sec = q.loadFirstItem();
//					long nanos = Timer.getTimer().getNanos("loading_parent_section");
//					Timer.getTimer().stop("loading_parent_section");
//					if(nanos/1000000 > 100){
//						//String queryLog = String.format(q.getSqlForLog() + ". Took: %,d ms.", nanos/1000000);
//						info.addSlowQuery(q.getSqlForLog(), nanos);
//						//info.pushLog(queryLog);
//					}
//					productParentCode = sec.getStringValue(CATEGORY_ID_PARAM,"");
//					product.setValue("parent_id", productParentCode);
//				}
//				secCode = (StringUtils.isNoneBlank(productParentCode))? productParentCode : secCode;

				info.setCurrentJob("раздел " + section.getStringValue(NAME_PARAM) + " * товар " + product.getStringValue(NAME_PARAM));

				//if (getPrice)
				product.setValueUI(PRICE_PARAM, commonParams.get(PRICE_ELEMENT));
				product.setValueUI(PRICE_ORIGINAL_PARAM, commonParams.get(PRICE_ELEMENT));
				product.setValueUI(PRICE_OLD_PARAM, commonParams.get(OLD_PRICE_ELEMENT));
				product.setValueUI("price_old_original", commonParams.get(OLD_PRICE_ELEMENT));
				BigDecimal price = product.getDecimalValue(PRICE_PARAM);
				BigDecimal oldPrice = product.getDecimalValue(PRICE_OLD_PARAM);
				//else
				//	product.setValueUI(PRICE_PARAM, "0");
				isBookinistic = isBookinistic || secCode.equals("16546") || BOOKINISTIC.equalsIgnoreCase(specialParams.get(STATUS)) || BOOKINISTIC.equalsIgnoreCase(specialParams.get(PUBLISH_TYPE));
				product.setValue(PRICE_PARAM, getCorrectPrice(price).setScale(1, RoundingMode.CEILING));
				if(oldPrice != null){
					if (price.compareTo(level_1) < 0) {
						oldPrice =   oldPrice.multiply(quotient_1);
					}
					else if (isBookinistic && quotient_buk.compareTo(BigDecimal.ZERO) != 0) oldPrice =   oldPrice.multiply(quotient_buk);
					else if (price.compareTo(level_2) < 0) {
						oldPrice =  oldPrice.multiply(quotient_2);
					}
					else {
						oldPrice = oldPrice.multiply(quotient_3);
					}
					product.setValue(PRICE_OLD_PARAM, oldPrice.setScale(1, RoundingMode.CEILING));
				}

				if(isBookinistic){
					//info.pushLog("BUK: "+ commonParams.get(NAME_ELEMENT));
					product.removeEqualValue("tag", BOOKINISTIC);
					product.setValue("tag", BOOKINISTIC_1);
				}

				else{
					//info.pushLog("NOT BUK: "+ commonParams.get(NAME_ELEMENT));
					product.removeEqualValue("tag", BOOKINISTIC);
					product.removeEqualValue("tag", BOOKINISTIC_1);
				}

				if (isProductNew) {
//					// Удалить айтемы с параметрами продукта, если продукт ранее уже существовал
//					Item paramsXml = ItemUtils.ensureSingleItem(PARAMS_XML_ITEM, initiator,
//							product.getId(), product.getOwnerGroupId(), product.getOwnerUserId());
					// Создать айтем с параметрами продукта
					XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
					for (String name : specialParams.keySet()) {
						String value = specialParams.get(name);
						xml.startElement(PARAMETER)
								.startElement(NAME).addText(name).endElement()
								.startElement(VALUE).addText(value).endElement()
								.endElement();
					}
					product.setValue("extra_xml", xml.toString());
					//paramsXml.setValue(XML_PARAM, xml.toString());
					//DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(paramsXml).noFulltextIndex());
				}

				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra());

				if (!isProductNew) {
					// Сделать товар видимым
					DelayedTransaction.executeSingle(initiator, ItemStatusDBUnit.restore(product));
					// Загрузить разделы, содержащие товар
					List<Item> secs = new ItemQuery(SECTION_ITEM).setChildId(product.getId(), false,
							ItemTypeRegistry.getPrimaryAssoc().getName(), catalogLinkAssoc.getName()).loadItems();
					// Создать ассоциацию товара с разделом, если ее еще не существует
					boolean needLink = true;
					for (Item sec : secs) {
						if (sec.getId() == section.getId()) {
							needLink = false;
							break;
						}
					}
					if (needLink) {
						DelayedTransaction.executeSingle(initiator,
								CreateAssocDBUnit.childExistsSoft(product, section, catalogLinkAssoc.getId()));
					}
				}

				info.increaseProcessed();
				isInsideOffer = false;
				isBookinistic = false;
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
			paramName = attributes.getValue(NAME_ATTR);
			if(paramName.equalsIgnoreCase("Сохранность")) isBookinistic = true;
			parameterReady = true;
		}
	}

	private BigDecimal getCorrectPrice(BigDecimal price) {
		if (price.compareTo(level_1) < 0) {
			return price.multiply(quotient_1);
		}
		if (isBookinistic && quotient_buk.compareTo(BigDecimal.ZERO) != 0){
			return price.multiply(quotient_buk);
		}
		if (price.compareTo(level_2) < 0) {
			return price.multiply(quotient_2);
		}
		return price.multiply(quotient_3);
	}
}
