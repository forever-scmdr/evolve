package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.unbescape.html.HtmlEscape;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class CatalogCreationHandler_3 extends DefaultHandler {

	public static final String ELEMENT_PROCESS_TIMER_NAME = "CatalogCreationHandler_element";
	public static final String DB_STATUS_TIMER_NAME = "CatalogCreationHandler_DB_status";
	public static final String DB_SAVE_TIMER_NAME = "CatalogCreationHandler_DB_save";
	public static final String DB_LOAD_TIMER_NAME = "CatalogCreationHandler_DB_load";
	public static final String DB_XML_TIMER_NAME = "CatalogCreationHandler_DB_XML";
	public static final String DB_SECTION_TIMER_NAME = "CatalogCreationHandler_DB_section";
	public static final String FILE_TIMER_NAME = "CatalogCreationHandler_file";
	public static final String POST_PROCESS_TIMER_NAME = "CatalogCreationHandler_post_process";


	private static final SimpleDateFormat SOON_FORMAT = new SimpleDateFormat("ddMMyy");
	private static final HashMap<String, String> PRODUCT_COMMON_PARAMS = new HashMap<>();
	private static final HashMap<String, String> CURRENCIES_COMMON_PARAMS = new HashMap<>();
	private static final char[] ETC = new char[] {'a','c','d','e','f'};

	static {
		PRODUCT_COMMON_PARAMS.put(IConst.NAME_ELEMENT, Product.NAME);
		PRODUCT_COMMON_PARAMS.put(IConst.MARK_ELEMENT, Product.NAME_EXTRA);
		PRODUCT_COMMON_PARAMS.put(IConst.PRODUCER_ELEMENT, Product.VENDOR);
		PRODUCT_COMMON_PARAMS.put(IConst.CODE_ELEMENT, Product.CODE);
		PRODUCT_COMMON_PARAMS.put(IConst.PRICE_ELEMENT, Product.PRICE);
		PRODUCT_COMMON_PARAMS.put(IConst.DESCRIPTION_ELEMENT, Product.DESCRIPTION);
		PRODUCT_COMMON_PARAMS.put(IConst.COUNTRY_ELEMENT, Product.COUNTRY);
		PRODUCT_COMMON_PARAMS.put(IConst.PIC_PATH_ELEMENT, Product.PIC_PATH);
		PRODUCT_COMMON_PARAMS.put(IConst.ANALOG_ELEMENT, Product.ANALOG);
		PRODUCT_COMMON_PARAMS.put(IConst.FILE_ELEMENT, Product.FILE);
		PRODUCT_COMMON_PARAMS.put(IConst.QTY_ELEMENT, Product.QTY);
		PRODUCT_COMMON_PARAMS.put(IConst.QTY_S1_ELEMENT, Product.QTY);
		//PRODUCT_COMMON_PARAMS.put(IConst.QTY_S2_ELEMENT, null);
		PRODUCT_COMMON_PARAMS.put(IConst.UNIT_ELEMENT, Product.UNIT);
		PRODUCT_COMMON_PARAMS.put(IConst.MIN_QTY_ELEMENT, Product.MIN_QTY);
		PRODUCT_COMMON_PARAMS.put(IConst.BARCODE_ELEMENT, Product.BARCODE);
		PRODUCT_COMMON_PARAMS.put(IConst.SPECIAL_PRICE_ELEMENT, Product.SPECIAL_PRICE);
		PRODUCT_COMMON_PARAMS.put(IConst.ANALOG_CODE_ELEMENT, Product.ANALOG_CODE);
		PRODUCT_COMMON_PARAMS.put(IConst.TEXT_TOP_ELEMENT, Product.TEXT);

		CURRENCIES_COMMON_PARAMS.put(IConst.EUR_ELEMENT, Currencies.EUR_RATE);
		CURRENCIES_COMMON_PARAMS.put(IConst.USD_ELEMENT, Currencies.USD_RATE);
		CURRENCIES_COMMON_PARAMS.put(IConst.RUB_ELEMENT, Currencies.RUB_RATE);
	}



	private Stack<Item> stack = new Stack<>();
	private Locator locator;
	private boolean parameterReady = false;
	private boolean currencyReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();
	private XmlDocumentBuilder xmlParams = null;
	private ArrayList<Integer> bigArts = new ArrayList<>();

	private SynchronousTransaction transaction = new SynchronousTransaction(User.getDefaultUser());
	private String className = null;
	private boolean fatalError = false;

	private Integrate_2.Info info; // информация для пользователя
	private Item currencies = null; // курсы валют


	public CatalogCreationHandler_3(Item catalog, Integrate_2.Info info) {
		stack.push(catalog);
		this.info = info;
	}


	@Override
	public void endElement(String uri, String localName, String qName) {
		try {
			if (fatalError) return;
			if (StringUtils.equalsAnyIgnoreCase(qName, IConst.PRODUCT_ELEMENT, IConst.SECTION_ELEMENT, IConst.KURS_ELEMENT)) {
				Item top = stack.pop();
				// Товар может быть уже сохранен, поэтому нужна проверка top.getId() == 0
				if (qName.equals(IConst.PRODUCT_ELEMENT) && top.getId() == 0) {
					Item product = null;
					String code = top.getStringValue(Product.CODE, "");
					info.getTimer().start(DB_LOAD_TIMER_NAME);
					ArrayList<Item> prods = ItemQuery.loadByParamValue(ItemNames.PRODUCT, Product.CODE, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
					info.getTimer().stop(DB_LOAD_TIMER_NAME);
					if (prods.size() > 0) {
						product = prods.get(0);
						if (product.getStatus() == Item.STATUS_HIDDEN) {
							info.getTimer().start(DB_STATUS_TIMER_NAME);
							transaction.executeCommandUnit(ItemStatusDBUnit.restore(product));
							info.getTimer().stop(DB_STATUS_TIMER_NAME);
						}
						Item.updateParamValues(top, product);
					} else {
						product = top;
					}
					// Установка значения нормы заказа (берется из раздела в случае если не установлена в товаре)
					if (product.getDoubleValue(Product.MIN_QTY, 0d) == 0d) {
						Item section = stack.peek();
						if (section.getDoubleValue(Section.NORM, 0d) != 0d)
							product.setValue(Product.MIN_QTY, section.getValue(Section.NORM));
						else
							product.setValue(Product.MIN_QTY, 1d);
					}
					// Установка преанализированного значения для поиска
					String name = product.getStringValue(Product.NAME, "");
					String mark = product.getStringValue(Product.NAME_EXTRA, "");
					String fullName = BelchipStrings.fromRtoE(name + ' ' + mark);
					String fullNameAnalyzed = BelchipStrings.preanalyze(name) + ' ' + BelchipStrings.preanalyze(mark);
					product.clearValue(Product.SEARCH);
					product.setValueUI(Product.SEARCH, fullName);
					product.setValueUI(Product.SEARCH, fullNameAnalyzed);
					product.setValueUI(Product.SEARCH, code);
					String strictSearch = name + ' ' + mark + ' ' + code;
					product.clearValue(Product.STRICT_SEARCH);
					if (strictSearch.length() > 80) {
						product.setValueUI(Product.STRICT_SEARCH, strictSearch.substring(0, 79));
					} else {
						product.setValueUI(Product.STRICT_SEARCH, strictSearch);
					}

					//Ignore Analogs from XML file FIX 29.10.2018
					//top.removeValue(ItemNames.product.ANALOG_CODE);
					
					String analog = product.getStringValue(Product.ANALOG, "");
					if (!StringUtils.isBlank(analog)) {
						product.clearValue(Product.ANALOG_SEARCH);
						product.setValueUI(Product.ANALOG_SEARCH, BelchipStrings.fromRtoE(analog));
						product.setValueUI(Product.ANALOG_SEARCH, BelchipStrings.preanalyze(analog));
					}
					// Установка количества товаров на складах					
					Double qty = product.getDoubleValue(IConst.QTY_ELEMENT);
					if (qty == null) {
						product.setValue(IConst.QTY_ELEMENT, 0d);
					}
					// Проверка наличия на складе
					byte avlb = (product.getDoubleValue(Product.QTY) < product.getDoubleValue(Product.MIN_QTY, 1d) && product.getLongValue("soon",0L) < 1)? (byte) 0 : (byte) 1;
					
					//adding to new products
					if (avlb > 0) {
						int codeInt = Integer.parseInt(code);
						bigArts.add(codeInt);
					}
					product.setValue(Product.AVAILABLE, avlb);
					
					if("Услуга".equalsIgnoreCase(name)) {
						product.setValue("is_service", (byte)1);
						product.setValue(IConst.QTY_ELEMENT, 100000d);
						product.setValue(Product.AVAILABLE, (byte)1);
					} else {
						product.setValue("is_service", (byte)0);
					}
					
					
					String contextPath = AppContext.getContextPath();
					for (char c : ETC) {
						String extra = "sitepics/" + product.getStringValue("pic_path")+c+".jpg";
						info.getTimer().start(FILE_TIMER_NAME);
						File ep = Paths.get(contextPath, extra).toFile();
						if (ep.isFile()) {
							product.setValue("extra_pic", extra);
						}
						info.getTimer().stop(FILE_TIMER_NAME);
					}
					
					// Добавление команды сохранения продукта
					if (product.hasChanged()) {
						info.getTimer().start(DB_SAVE_TIMER_NAME);
						transaction.executeCommandUnit(SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra());
						transactionExecute();
						info.getTimer().stop(DB_SAVE_TIMER_NAME);
					}

					// Айтем параметров XML
					info.getTimer().start(DB_LOAD_TIMER_NAME);
					Item paramsXml = new ItemQuery(ItemNames.PARAMS_XML, Item.STATUS_NORMAL, Item.STATUS_HIDDEN).setParentId(product.getId(), false).loadFirstItem();
					info.getTimer().stop(DB_LOAD_TIMER_NAME);
					if (paramsXml == null)
						paramsXml = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PARAMS_XML), product);
					String oldXml = HtmlEscape.unescapeHtml(paramsXml.getStringValue(Params_xml.XML, "").replace("\n", "").replace("\r", "").replaceAll("\\s+",""));
					paramsXml.setValueUI(Params_xml.XML, xmlParams.toString());
					String newXml = HtmlEscape.unescapeHtml(paramsXml.getStringValue(Params_xml.XML, "").replace("\n", "").replace("\r", "").replaceAll("\\s+",""));
					if (!StringUtils.equals(oldXml, newXml)) {
						info.getTimer().start(DB_XML_TIMER_NAME);
						paramsXml.setValue(Params_xml.CHANGED_FLAG, (byte) 1);
						transaction.executeCommandUnit(SaveItemDBUnit.get(paramsXml).noFulltextIndex().noTriggerExtra());
						info.getTimer().stop(DB_XML_TIMER_NAME);
					}
					transactionExecute();
					info.increaseProcessed();
					xmlParams = null;
				} else if (qName.equals(IConst.SECTION_ELEMENT)) {
					transactionExecute();
					info.increaseProcessed();
				} else if (StringUtils.equalsIgnoreCase(qName, IConst.KURS_ELEMENT)) {
					transaction.executeCommandUnit(SaveItemDBUnit.get(currencies).noFulltextIndex());
					transactionExecute();
				}
			}
			// Установка значения параметра
			if (parameterReady) {
				Item product = stack.peek();
				if (StringUtils.equalsIgnoreCase(qName, IConst.PARAMETER_ELEMENT)) {
					xmlParams.startElement(IConst.PARAMETER_ELEMENT);
					xmlParams.addElement(IConst.NAME_ELEMENT, paramName);
					xmlParams.addElement(IConst.VALUE_ELEMENT, paramValue.toString());
					xmlParams.endElement();
				} else {
					product.setValueUI(paramName, paramValue.toString().trim());
				}
			}
			else if (currencyReady) {
				currencies.setValueUI(paramName, paramValue.toString().trim());
			}

			parameterReady = false;
			currencyReady = false;
			// Инфо
			info.setLineNumber(locator.getLineNumber());
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
		info.getTimer().stop(ELEMENT_PROCESS_TIMER_NAME);
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		try {
			if ((!parameterReady && !currencyReady) || fatalError)
				return;
			paramValue.append(ch, start, length);
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		info.getTimer().start(ELEMENT_PROCESS_TIMER_NAME);
		parameterReady = false;
		if (fatalError) return;
		try {
			// Раздел
			if (IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				String name = attributes.getValue(IConst.NAME_ATTRIBUTE);
				String code = attributes.getValue(IConst.CODE_ATTRIBUTE);
				String picPath = attributes.getValue(IConst.PIC_PATH_ATTRIBUTE);
				info.getTimer().start(DB_SECTION_TIMER_NAME);
				Item section = ItemQuery.loadByParamValue(Section._NAME, Section.CODE, code).get(0);
				if(StringUtils.isNotBlank(picPath)) {
					section.setValue(Section.PIC_PATH, picPath);
					transaction.executeCommandUnit(SaveItemDBUnit.get(section).ignoreUser(true).noFulltextIndex());
					transactionExecute();
				}
				info.getTimer().stop(DB_SECTION_TIMER_NAME);
				
				className = name;
				if (stack.size() != 1) {
					className = parent.getStringValue(Section.NAME) + " " + className;
				}
				className = Strings.createXmlElementName(className);
				stack.push(section);
			}
			// Продукт
			else if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				ItemType productDesc = ItemTypeRegistry.getItemType(className);
				if (productDesc == null) // Возможно в случае, если в разделе не было ни одного продукта с дополнительными (нестандартными) параметрами
					productDesc = ItemTypeRegistry.getItemType(Product._NAME);
				Item product = Item.newChildItem(productDesc, parent);
				byte hitFlag = Boolean.parseBoolean(attributes.getValue(IConst.HIT_ATTRIBUTE)) ? (byte)1 : (byte)0;
				byte newFlag = Boolean.parseBoolean(attributes.getValue(IConst.NEW_ATTRIBUTE)) ? (byte)1 : (byte)0;
				Date soonFlag = (StringUtils.isBlank(attributes.getValue(IConst.SOON_ATTRIBUTE))) ? null : SOON_FORMAT.parse(attributes.getValue(IConst.SOON_ATTRIBUTE));
				product.setValue(Product.HIT, hitFlag);
				product.setValue(Product.NEW, newFlag);
				product.setValue(Product.TYPE, parent.getStringValue(Section.NAME));
				if (soonFlag != null) {
					product.setValue(IConst.SOON_ATTRIBUTE, soonFlag.getTime()+3*60*60*1000);
				}
				xmlParams = XmlDocumentBuilder.newDocPart();
				stack.push(product);
			}
			// Курсы валют
			else if (IConst.KURS_ELEMENT.equalsIgnoreCase(qName)) {
				currencies = ItemUtils.ensureSingleChild(ItemNames.CURRENCIES, User.getDefaultUser(), stack.firstElement());
				stack.push(currencies);
			}
			else if (CURRENCIES_COMMON_PARAMS.containsKey(qName) && stack.peek() == currencies) {
				paramName = CURRENCIES_COMMON_PARAMS.get(qName);
				currencyReady = true;
			}

			// Параметры продуктов (общие)
			else if (PRODUCT_COMMON_PARAMS.containsKey(qName.toLowerCase())) {
				paramName = PRODUCT_COMMON_PARAMS.get(qName);
				parameterReady = true;
			}
			// Пользовательские параметры продуктов
			else if (IConst.PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
				paramName = attributes.getValue(IConst.NAME_ATTRIBUTE);
				parameterReady = true;
			}
			paramValue = new StringBuilder();
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
		// Инфо
		info.setLineNumber(locator.getLineNumber());
	}

	@Override
	public void endDocument() {
		try {
			info.getTimer().start(POST_PROCESS_TIMER_NAME);
			transaction.commit();
			info.increaseProcessed();
			info.setOperation("Поиск новых товаров");
			Collections.sort(bigArts, Collections.reverseOrder());
			int i = 0;
			for(int art : bigArts){
				String code = String.format("%05d",art);
				Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, Product.CODE , code);
				if(product.getDoubleValue(Product.QTY,0d) < product.getDoubleValue(Product.MIN_QTY, 1d)) continue;
				if(i++>19)break;
				product.setValue(Product.NEW, (byte)1);
				transaction.executeCommandUnit(SaveItemDBUnit.get(product).ignoreUser(true).noFulltextIndex());
			}
			transaction.commit();
			info.getTimer().stop(POST_PROCESS_TIMER_NAME);
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
	}

	private void transactionExecute() throws Exception {
		if (transaction.getUncommitedCount() >= 200)
			transaction.commit();
	}
}
