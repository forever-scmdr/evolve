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
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class CatalogCreationHandler extends DefaultHandler {

	public static final String ELEMENT_PROCESS_TIMER_NAME = "CatalogCreationHandler_element";
	public static final String DB_TIMER_NAME = "CatalogCreationHandler_DB";
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
		PRODUCT_COMMON_PARAMS.put(IConst.VIDEO, Product.FILEVID);
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
	
	
	public CatalogCreationHandler(Item catalog, Integrate_2.Info info) {
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
					// Установка значения нормы заказа (берется из раздела в случае если не установлена в товаре)
					if (top.getDoubleValue(Product.MIN_QTY, 0d) == 0d) {
						Item section = stack.peek();
						if (section.getDoubleValue(Section.NORM, 0d) != 0d)
							top.setValue(Product.MIN_QTY, section.getValue(Section.NORM));
						else
							top.setValue(Product.MIN_QTY, 1d);
					}
					// Установка преанализированного значения для поиска (и поиска по аналогам)
					String name = top.getStringValue(Product.NAME, "");
					String mark = top.getStringValue(Product.NAME_EXTRA, "");
					String code = top.getStringValue(Product.CODE, "");
					fillSearchParams(top);

					// Установка количества товаров на складах					
					Double qty = top.getDoubleValue(IConst.QTY_ELEMENT);
					if (qty == null) {
						top.setValue(IConst.QTY_ELEMENT, 0d);
					}
					// Проверка наличия на складе
					byte avlb = (top.getDoubleValue(Product.QTY) < top.getDoubleValue(Product.MIN_QTY, 1d) && top.getLongValue("soon",0L) < 1)? (byte) 0 : (byte) 1;
					
					//adding to new products
					if(avlb > 0){		
						int codeInt = Integer.parseInt(code);
						bigArts.add(codeInt);
					}					
					top.setValue(Product.AVAILABLE, avlb);
					
					if("Услуга".equalsIgnoreCase(name)) {
						top.setValue("is_service", (byte)1);
						top.setValue(IConst.QTY_ELEMENT, 100000d);
						top.setValue(Product.AVAILABLE, (byte)1);
					} else {
						top.setValue("is_service", (byte)0);
					}
					
					
					String contextPath = AppContext.getContextPath();
					for(char c : ETC) {
						String extra = "sitepics/"+top.getStringValue("pic_path")+c+".jpg";
						info.getTimer().start(FILE_TIMER_NAME);
						File ep = Paths.get(contextPath, extra).toFile();
						if (ep.isFile()) {
							top.setValue("extra_pic", extra);
						}
						info.getTimer().stop(FILE_TIMER_NAME);
					}
					
					// Добавление команды сохранения продукта
					info.getTimer().start(DB_TIMER_NAME);
					transaction.executeCommandUnit(SaveItemDBUnit.get(top).noFulltextIndex().noTriggerExtra());
					transactionExecute();
					info.getTimer().stop(DB_TIMER_NAME);
					Item paramsXml = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PARAMS_XML), top);
					paramsXml.setValueUI(Params_xml.XML, xmlParams.toString());
					info.getTimer().start(DB_TIMER_NAME);
					transaction.executeCommandUnit(SaveItemDBUnit.get(paramsXml).noFulltextIndex().noTriggerExtra());
					transactionExecute();
					info.getTimer().stop(DB_TIMER_NAME);
					info.increaseProcessed();
					xmlParams = null;
				} else if (qName.equals(IConst.SECTION_ELEMENT)) {
					transactionExecute();
					info.increaseProcessed();
				} else if (StringUtils.equalsIgnoreCase(qName, IConst.KURS_ELEMENT)) {
					currencies.setValue(Currencies.RUB_SCALE, BigDecimal.valueOf(100));
					currencies.setValue(Currencies.EUR_SCALE, BigDecimal.ONE);
					currencies.setValue(Currencies.USD_SCALE, BigDecimal.ONE);
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
				info.getTimer().start(DB_TIMER_NAME);
				Item section = ItemQuery.loadByParamValue(Section._NAME, Section.CODE, code).get(0);
				if(StringUtils.isNotBlank(picPath)){
					section.setValue(Section.PIC_PATH, picPath);
					transaction.executeCommandUnit(SaveItemDBUnit.get(section).ignoreUser(true).noFulltextIndex());
					transactionExecute();
				}
				info.getTimer().stop(DB_TIMER_NAME);
				
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

	/**
	 * Заполнить параметры поиска, которые не указаны напрямую в файле интеграции
	 * (генерируются из установленных значений названия и кода товара, а также аналоги)
	 * @param prod
	 */
	public static void fillSearchParams(Item prod) {
		String name = prod.getStringValue(Product.NAME, "");
		String mark = prod.getStringValue(Product.NAME_EXTRA, "");
		String code = prod.getStringValue(Product.CODE, "");
		String fullName = BelchipStrings.fromRtoE(name + ' ' + mark);
		String fullNameAnalyzed = BelchipStrings.preanalyze(name) + ' ' + BelchipStrings.preanalyze(mark);
		prod.setValue(Product.SEARCH, fullName);
		prod.setValue(Product.SEARCH, fullNameAnalyzed);
		prod.setValue(Product.SEARCH, code);
		String strictSearch = name + ' ' + mark + ' ' + code;
		if (strictSearch.length() > 99) {
			prod.setValue(Product.STRICT_SEARCH, strictSearch.substring(0, 98));
		} else {
			prod.setValue(Product.STRICT_SEARCH, strictSearch);
		}
		prod.setValue(Product.STRICT_SEARCH, code);

		//Ignore Analogs from XML file FIX 29.10.2018
		//top.removeValue(ItemNames.product.ANALOG_CODE);

		String analog = prod.getStringValue(Product.ANALOG, "");
		if (!StringUtils.isBlank(analog)) {
			prod.setValue(Product.ANALOG_SEARCH, BelchipStrings.fromRtoE(analog));
			prod.setValue(Product.ANALOG_SEARCH, BelchipStrings.preanalyze(analog));
		}
	}
}
