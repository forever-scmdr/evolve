package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.Strings;
import ecommander.model.datatypes.DataType;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;

/**
 * Класс создает структуру каталога продукции (разделы и подразделы)
 * и классы товаров
 * @author E
 *
 */
public class YMarketProductClassHandler extends DefaultHandler implements CatalogConst {
	/**
	 * Типы и названия параметров
	 * Тип параметра может быть одним из трех
	 * integer
	 * double
	 * string
	 * @author E
	 *
	 */
	static class Params {

		LinkedHashMap<String, DataType.Type> paramTypes = new LinkedHashMap<>();
		LinkedHashMap<String, String> paramCaptions = new LinkedHashMap<>();
		HashSet<String> notInFilter = new HashSet<>();
		private static final NumberFormat eng_format = NumberFormat.getInstance(new Locale("en"));
		private static final NumberFormat ru_format = NumberFormat.getInstance(new Locale("ru"));

		
		private void addParameter(String name, String value) {
			String paramName = Strings.createXmlElementName(name);
			if (!paramTypes.containsKey(paramName)) {
				paramTypes.put(paramName, DataType.Type.INTEGER);
				paramCaptions.put(paramName, name);
			}
			DataType.Type currentType = paramTypes.get(paramName);
			if (currentType == DataType.Type.INTEGER) {
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
			} else if (currentType == DataType.Type.DOUBLE) {
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
	
	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();
	private HashMap<String, Params> sectionParams;
	private IntegrateBase.Info info; // информация для пользователя
	private Params currentCategoryParams = null;
	private int сreated = 0; // информация для пользователя

	public YMarketProductClassHandler(IntegrateBase.Info info) {
		this.info = info;
		sectionParams = new HashMap<>();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// убирается текущий раздел
		if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
			currentCategoryParams = null;
		}
		// Установка значения параметра
		else if (parameterReady && StringUtils.equalsIgnoreCase(qName, PARAM_ELEMENT)) {
			currentCategoryParams.addParameter(paramName, StringUtils.trim(paramValue.toString()));
		}
		// Запоминание раздела по ID раздела
		else if (StringUtils.equalsIgnoreCase(qName, CATEGORY_ID_ELEMENT)) {
			String categoryId = StringUtils.trim(paramValue.toString());
			currentCategoryParams = sectionParams.computeIfAbsent(categoryId, k -> new Params());
		}
		parameterReady = false;
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
		paramValue = new StringBuilder();
		parameterReady = false;
		if (StringUtils.equalsIgnoreCase(qName, PARAM_ELEMENT)) {
			paramName = attributes.getValue(NAME_ATTR);
			parameterReady = true;
		}
		else if (StringUtils.equalsIgnoreCase(qName, CATEGORY_ID_ELEMENT)) {
			parameterReady = true;
		}
	}

	public HashMap<String, Params> getParams() {
		return sectionParams;
	}

}
