package ecommander.fwk.integration;

import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.Strings;
import ecommander.model.DataTypeRegistry;
import ecommander.model.Item;
import ecommander.model.datatypes.DataType;
import ecommander.model.datatypes.DataTypeRegistry;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.ParameterDescription.Quantifier;
import ecommander.model.item.filter.CriteriaDef;
import ecommander.model.item.filter.FilterDefinition;
import ecommander.model.item.filter.InputDef;
import ecommander.persistence.DelayedTransaction;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.commandunits.SaveNewItemTypeDBUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.users.User;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Stack;
/**
 * Класс создает структуру каталога продукции (разделы и подразделы)
 * и классы товаров
 * @author E
 *
 */
public class YMarketProductClassHandler extends DefaultHandler {
	/**
	 * Типы и названия параметров
	 * Тип параметра может быть одним из трех
	 * integer
	 * double
	 * string
	 * @author E
	 *
	 */
	private static class Params {
		private final String className;
		private final String classCaption;
		private LinkedHashMap<String, DataType.Type> paramTypes = new LinkedHashMap<>();
		private LinkedHashMap<String, String> paramCaptions = new LinkedHashMap<String, String>();
		private HashSet<String> notInFilter = new HashSet<String>();
		private static final NumberFormat eng_format = NumberFormat.getInstance(new Locale("en"));
		private static final NumberFormat ru_format = NumberFormat.getInstance(new Locale("ru"));
		
		private Params(String caption, String className) {
			this.classCaption = caption;
			this.className = Strings.createXmlElementName(className);
		}
		
		private void addParameter(String name, String value) {
			String paramName = Strings.createXmlElementName(name);
			if (!paramTypes.containsKey(paramName)) {
				paramTypes.put(paramName, DataType.Type.INTEGER);
				paramCaptions.put(paramName, name);
			}
			String currentType = paramTypes.get(paramName);
			if (currentType.equals(DataTypeRegistry.INTEGER_TYPE_NAME)) {
				try {
					Integer.parseInt(value);
					// оставить тип integer
				} catch (NumberFormatException ie) {
					if (testDouble(value))
						// Заменить тип на double
						paramTypes.put(paramName, DataTypeRegistry.DOUBLE_TYPE_NAME);
					else
						// Заменить тип на string
						paramTypes.put(paramName, DataTypeRegistry.STRING_TYPE_NAME);
				}
			} else if (currentType.equals(DataTypeRegistry.DOUBLE_TYPE_NAME)) {
				if (!testDouble(value))
					// Заменить тип на string
					paramTypes.put(paramName, DataTypeRegistry.STRING_TYPE_NAME);
			}
		}
		
		private void addNotInFilter(String name) {
			String paramName = Strings.createXmlElementName(name);
			notInFilter.add(paramName);
		}
		
		private static boolean testDouble(String value) {
			ParsePosition pp = new ParsePosition(0);
			ru_format.parse(value, pp);
			if (pp.getIndex() != value.length()) {
				pp = new ParsePosition(0);
				eng_format.parse(value, pp);
				if (pp.getIndex() != value.length())
					return false;
			}
			return true;
		}
	}
	
	private Stack<Item> stack = new Stack<Item>();
	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();
	private Params sectionParams;
	private DelayedTransaction transaction = new DelayedTransaction(User.getDefaultUser());
	private IntegrateBase.Info info; // информация для пользователя
	private int сreated = 0; // информация для пользователя
	
	public YMarketProductClassHandler(Item catalog, IntegrateBase.Info info) {
		stack.push(catalog);
		this.info = info;
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// просто извлечение товара из стека
		if (YMarketConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
			stack.pop();
		} 
		// Сохранение фильтра в разделе
		else if (YMarketConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
			Item section = stack.pop();
			if (sectionParams != null) {
				try {
					if (sectionParams.paramTypes.size() > 0) {
						// Создать фильтр и установить его в айтем
						FilterDefinition filter = FilterDefinition.create("");
						filter.setRoot(sectionParams.className);
						for (String paramName : sectionParams.paramTypes.keySet()) {
							if (sectionParams.notInFilter.contains(paramName))
								continue;
	//						String type = sectionParams.paramTypes.get(paramName);
							String caption = sectionParams.paramCaptions.get(paramName);
	//						// Создать два поля "от" и "до"
	//						if (type.equals(DataTypeRegistry.INTEGER_TYPE_NAME) || type.equals(DataTypeRegistry.DOUBLE_TYPE_NAME)) {
	//							CriteriaGroupDef fromto = new CriteriaGroupDef(caption, "", "AND");
	//							filter.addPart(fromto);
	//							InputDef fromInp = new InputDef("droplist", "От", "", "");
	//							fromto.addPart(fromInp);
	//							InputDef toInp = new InputDef("droplist", "До", "", "");
	//							fromto.addPart(toInp);
	//							fromInp.addPart(new CriteriaDef(">=", paramName, ""));
	//							toInp.addPart(new CriteriaDef("<=", paramName, ""));
	//						}
	//						// Создать одно поле ввода
	//						else {
								InputDef input = new InputDef("droplist", caption, "", "");
								filter.addPart(input);
								input.addPart(new CriteriaDef("=", paramName, sectionParams.paramTypes.get(paramName), ""));
	//						}
						}
						section.setValue(YMarketConst.FILTER_PARAMETER, filter.generateXML());
						transaction.addCommandUnit(new UpdateItemDBUnit(section));
						transaction.execute();
					}
					
					// Создать класс для продуктов из этого раздела
					ItemType newClass = new ItemType(sectionParams.className, 0, sectionParams.classCaption, "", "",
							ItemNames.PRODUCT._ITEM_NAME, false, true, false, false, true);
					for (String paramName : sectionParams.paramTypes.keySet()) {
						String type = sectionParams.paramTypes.get(paramName);
						String caption = sectionParams.paramCaptions.get(paramName);
						String descr = StringUtils.substringAfterLast(caption, ",");
						newClass.putParameter(new ParameterDescription(paramName, 0, type, Quantifier.single, 0, "", caption, descr, "", false, false));
					}
					transaction.addCommandUnit(new SaveNewItemTypeDBUnit(newClass));
					transaction.execute();
				} catch (Exception e) {
					info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
				}
			}
			sectionParams = null;
		}
		// Установка значения параметра
		else if (parameterReady && YMarketConst.PARAMETER_ELEMENT.equals(qName)) {
			sectionParams.addParameter(paramName, paramValue.toString().trim());
		}
		parameterReady = false;
		// Инфо
		info.setCurrentLine(locator.getLineNumber());
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (!parameterReady)
			return;
		paramValue.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		parameterReady = false;
		try {
			// Раздел
			if (YMarketConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				String name = attributes.getValue(YMarketConst.NAME_ATTRIBUTE);
				String code = attributes.getValue(YMarketConst.CODE_ATTRIBUTE);
				String picPath = attributes.getValue(YMarketConst.PIC_PATH_ATTRIBUTE);
				String limit_1 = attributes.getValue(YMarketConst.LIMIT_1_PARAM);
				String limit_2 = attributes.getValue(YMarketConst.LIMIT_2_PARAM);
				String discount_1 = attributes.getValue(YMarketConst.DISCOUNT_1_PARAM);
				String discount_2 = attributes.getValue(YMarketConst.DISCOUNT_2_PARAM);
				String norm = attributes.getValue(YMarketConst.NORM_PARAM);
				ItemType itemDesc = ItemTypeRegistry.getItemType(ItemNames.SECTION._ITEM_NAME);
				String className = name;
				String caption = name;
				if (stack.size() == 1) {
					itemDesc = ItemTypeRegistry.getItemType(ItemNames.SECTION_FIRST._ITEM_NAME);
				} else {
					caption = parent.getStringValue(ItemNames.SECTION.NAME) + " / " + className;
					className = parent.getStringValue(ItemNames.SECTION.NAME) + " " + className;
				}
				Item section = Item.newChildItem(itemDesc, parent);
				section.setValue(ItemNames.SECTION.NAME, name);
				section.setValue(ItemNames.SECTION.CODE, code);
				section.setValue(ItemNames.SECTION.PIC_PATH, picPath);
				section.setValueUI(ItemNames.SECTION.LIMIT_1, limit_1);
				section.setValueUI(ItemNames.SECTION.LIMIT_2, limit_2);
				section.setValueUI(ItemNames.SECTION.DISCOUNT_1, discount_1);
				section.setValueUI(ItemNames.SECTION.DISCOUNT_2, discount_2);
				section.setValueUI(ItemNames.SECTION.NORM, norm);
				stack.push(section);
				// Создание нового объекта params
				sectionParams = new Params(caption, className);
				// Синхронное выполнение, чтобы создался ID раздела и можно было бы создавать подразделы и товары
				transaction.addCommandUnit(new SaveNewItemDBUnit(section).fulltextIndex(false));
				// Раздел сохраняется сразу
				transaction.execute();
				// Информация
				info.setSectionsCreated(++сreated);
			}
			// Продукт
			else if (YMarketConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				Item product = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT._ITEM_NAME), parent);
				stack.push(product);
			}

			// Параметры продуктов (общие)
			else if (YMarketConst.NAME_ELEMENT.equalsIgnoreCase(qName) || YMarketConst.MARK_ELEMENT.equalsIgnoreCase(qName)
					|| YMarketConst.PRODUCER_ELEMENT.equalsIgnoreCase(qName) || YMarketConst.CODE_ELEMENT.equalsIgnoreCase(qName)
					|| YMarketConst.PRICE_ELEMENT.equalsIgnoreCase(qName) || YMarketConst.DESCRIPTION_ELEMENT.equalsIgnoreCase(qName)
					|| YMarketConst.SEARCH_ELEMENT.equalsIgnoreCase(qName) || YMarketConst.PIC_PATH_ELEMENT.equalsIgnoreCase(qName)
					|| YMarketConst.ANALOG_ELEMENT.equalsIgnoreCase(qName) || YMarketConst.FILE_ELEMENT.equalsIgnoreCase(qName)
					|| YMarketConst.QTY_ELEMENT.equalsIgnoreCase(qName) || YMarketConst.UNIT_ELEMENT.equalsIgnoreCase(qName)
					|| YMarketConst.MIN_QTY_ELEMENT.equalsIgnoreCase(qName) || YMarketConst.BARCODE_ELEMENT.equalsIgnoreCase(qName)
					|| YMarketConst.COUNTRY_ELEMENT.equalsIgnoreCase(qName)) {
				paramName = qName;
				parameterReady = true;
			}
			// Пользовательские параметры продуктов
			else if (YMarketConst.PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
				paramName = attributes.getValue(YMarketConst.NAME_ATTRIBUTE);
				String notInFilter = attributes.getValue("filter");
				if (notInFilter != null && notInFilter.equalsIgnoreCase("false"))
					sectionParams.addNotInFilter(paramName);
				parameterReady = true;
			}
			paramValue = new StringBuilder();
			// Инфо
			info.setCurrentLine(locator.getLineNumber());
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

	@Override
	public void endDocument() throws SAXException {
		try {
			transaction.execute();
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}	
	
}
