package extra.belchip;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.filter.CriteriaDef;
import ecommander.model.filter.FilterDefinition;
import ecommander.model.filter.InputDef;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewItemTypeDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import extra._generated.ItemNames;
import extra._generated.Section;
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
 * Класс создает структуру каталога продукции (разделы и подразделы) и классы товаров
 * 
 * @author E
 *
 */
public class ProductClassHandler extends DefaultHandler {
	/**
	 * Типы и названия параметров Тип параметра может быть одним из трех integer double string
	 * 
	 * @author E
	 *
	 */
	private static class Params {
		private final String className;
		private final String classCaption;
		private LinkedHashMap<String, DataType.Type> paramTypes = new LinkedHashMap<>();
		private LinkedHashMap<String, String> paramCaptions = new LinkedHashMap<>();
		private HashSet<String> notInFilter = new HashSet<>();
		private static final NumberFormat eng_format = NumberFormat.getInstance(new Locale("en"));
		private static final NumberFormat ru_format = NumberFormat.getInstance(new Locale("ru"));
		static {
			eng_format.setMaximumFractionDigits(12);
			ru_format.setMaximumFractionDigits(12);
		}

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
			DataType.Type currentType = paramTypes.get(paramName);

			if (currentType.equals(DataType.Type.INTEGER)) {
				if (value.matches("0\\d+")) {
					paramTypes.put(paramName, DataType.Type.STRING);
				} else {
					try {
						Integer.parseInt(value);
						// оставить тип integer
					} catch (NumberFormatException ie) {
						if (testDouble(value))
							// Заменить тип на double
							paramTypes.put(paramName, DataType.Type.DOUBLE);
						else
							// Заменить тип на string
							paramTypes.put(paramName, DataType.Type.STRING);
					}
				}
			} else if (currentType.equals(DataType.Type.DOUBLE)) {
				if (!testDouble(value))
					// Заменить тип на string
					paramTypes.put(paramName, DataType.Type.STRING);
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

	private Stack<Item> stack = new Stack<>();
	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();
	private Params sectionParams;
	private DelayedTransaction transaction = new DelayedTransaction(User.getDefaultUser());
	private Integrate_2.Info info; // информация для пользователя
	private int сreated = 0; // информация для пользователя

	public ProductClassHandler(Item catalog, Integrate_2.Info info) {
		stack.push(catalog);
		this.info = info;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// просто извлечение товара из стека
		if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
			stack.pop();
		}
		// Сохранение фильтра в разделе
		else if (IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
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
							String caption = sectionParams.paramCaptions.get(paramName);
							InputDef input = new InputDef("droplist", caption, "", "");
							filter.addPart(input);
							input.addPart(new CriteriaDef("=", paramName, sectionParams.paramTypes.get(paramName), ""));

						}
						section.setValue(IConst.FILTER_PARAMETER, filter.generateXML());
						transaction.addCommandUnit(SaveItemDBUnit.get(section));
						transaction.execute();
					}

					// Создать класс для продуктов из этого раздела
					ItemType newClass = new ItemType(sectionParams.className, 0, sectionParams.classCaption, "", "", ItemNames.PRODUCT, "", false,
							true, false, false);
					for (String paramName : sectionParams.paramTypes.keySet()) {
						DataType.Type type = sectionParams.paramTypes.get(paramName);
						String caption = sectionParams.paramCaptions.get(paramName);
						String descr = StringUtils.substringAfterLast(caption, ",");
						newClass.putParameter(
								new ParameterDescription(paramName, 0, type.getName(), false, 0, "", caption, descr, "", false, false, "", null));
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
		else if (parameterReady && IConst.PARAMETER_ELEMENT.equals(qName)) {
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
			if (IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				String name = attributes.getValue(IConst.NAME_ATTRIBUTE);
				String code = attributes.getValue(IConst.CODE_ATTRIBUTE);
				String picPath = attributes.getValue(IConst.PIC_PATH_ATTRIBUTE);
				String limit_1 = attributes.getValue(IConst.LIMIT_1_PARAM);
				String limit_2 = attributes.getValue(IConst.LIMIT_2_PARAM);
				String discount_1 = attributes.getValue(IConst.DISCOUNT_1_PARAM);
				String discount_2 = attributes.getValue(IConst.DISCOUNT_2_PARAM);
				String norm = attributes.getValue(IConst.NORM_PARAM);
				ItemType itemDesc = ItemTypeRegistry.getItemType(ItemNames.SECTION);
				String className = name;
				String caption = name;
				if (stack.size() != 1) {
					caption = parent.getStringValue(Section.NAME) + " / " + className;
					className = parent.getStringValue(Section.NAME) + " " + className;
				}
				Item section = Item.newChildItem(itemDesc, parent);
				section.setValue(Section.NAME, name);
				section.setValue(Section.CODE, code);
				section.setValue(Section.PIC_PATH, picPath);
				section.setValueUI(Section.LIMIT_1, limit_1);
				section.setValueUI(Section.LIMIT_2, limit_2);
				section.setValueUI(Section.DISCOUNT_1, discount_1);
				section.setValueUI(Section.DISCOUNT_2, discount_2);
				section.setValueUI(Section.NORM, norm);
				stack.push(section);
				// Создание нового объекта params
				sectionParams = new Params(caption, className);
				// Синхронное выполнение, чтобы создался ID раздела и можно было бы создавать подразделы и товары
				transaction.addCommandUnit(SaveItemDBUnit.get(section).noFulltextIndex());
				// Раздел сохраняется сразу
				transaction.execute();
				// Информация
				info.setSectionsCreated(++сreated);
			}
			// Продукт
			else if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				Item product = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT), parent);
				stack.push(product);
			}

			// Параметры продуктов (общие)
			else if (IConst.NAME_ELEMENT.equalsIgnoreCase(qName) || IConst.MARK_ELEMENT.equalsIgnoreCase(qName)
					|| IConst.PRODUCER_ELEMENT.equalsIgnoreCase(qName) || IConst.CODE_ELEMENT.equalsIgnoreCase(qName)
					|| IConst.PRICE_ELEMENT.equalsIgnoreCase(qName) || IConst.DESCRIPTION_ELEMENT.equalsIgnoreCase(qName)
					|| IConst.SEARCH_ELEMENT.equalsIgnoreCase(qName) || IConst.PIC_PATH_ELEMENT.equalsIgnoreCase(qName)
					|| IConst.ANALOG_ELEMENT.equalsIgnoreCase(qName) || IConst.FILE_ELEMENT.equalsIgnoreCase(qName)
					|| IConst.QTY_ELEMENT.equalsIgnoreCase(qName) || IConst.UNIT_ELEMENT.equalsIgnoreCase(qName)
					|| IConst.MIN_QTY_ELEMENT.equalsIgnoreCase(qName) || IConst.BARCODE_ELEMENT.equalsIgnoreCase(qName)
					|| IConst.COUNTRY_ELEMENT.equalsIgnoreCase(qName)) {
				paramName = qName;
				parameterReady = true;
			}
			// Пользовательские параметры продуктов
			else if (IConst.PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
				paramName = attributes.getValue(IConst.NAME_ATTRIBUTE);
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
			//info.addError(ExceptionUtils.getExceptionStackTrace(e), locator.getLineNumber(), locator.getColumnNumber());
			info.addError(e.toString(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

}
