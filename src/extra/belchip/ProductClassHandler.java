package extra.belchip;

import ecommander.fwk.ServerLogger;
import ecommander.model.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import extra._generated.ItemNames;
import extra._generated.Section;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashSet;
import java.util.Stack;

/**
 * Класс создает структуру каталога продукции (разделы и подразделы) и классы товаров
 * 
 * @author E
 *
 */
public class ProductClassHandler extends DefaultHandler {


	private Stack<Item> stack = new Stack<>();
	private Locator locator;
	private String paramName;
	private HashSet<String> allInFilter = new HashSet<>();
	private DelayedTransaction transaction = new DelayedTransaction(User.getDefaultUser());
	private Integrate_2.Info info; // информация для пользователя
	private int сreated = 0; // информация для пользователя

	public ProductClassHandler(Item catalog, Integrate_2.Info info) {
		stack.push(catalog);
		this.info = info;
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		// просто извлечение товара из стека
		if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
			stack.pop();
		}
		// Сохранение фильтра в разделе
		else if (IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
			Item section = stack.pop();
			try {
				section.setValueUI(Section.EXTRA, StringUtils.join(allInFilter, ";"));
				// Синхронное выполнение, чтобы создался ID раздела и можно было бы создавать подразделы и товары
				transaction.addCommandUnit(SaveItemDBUnit.get(section).noFulltextIndex());
				// Раздел сохраняется сразу
				transaction.execute();
			} catch (Exception e) {
				ServerLogger.error("Integration error", e);
				info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			}
		}
		// Инфо
		info.setLineNumber(locator.getLineNumber());
	}

	@Override
	public void characters(char[] ch, int start, int length) {

	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
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
				Item section = Item.newChildItem(itemDesc, parent);
				section.setValue(Section.NAME, name);
				section.setValue(Section.CODE, code);
				section.setValue(Section.PIC_PATH, picPath);
				section.setValueUI(Section.LIMIT_1, limit_1);
				section.setValueUI(Section.LIMIT_2, limit_2);
				section.setValueUI(Section.DISCOUNT_1, discount_1);
				section.setValueUI(Section.DISCOUNT_2, discount_2);
				section.setValueUI(Section.NORM, norm);
				// Синхронное выполнение, чтобы создался ID раздела и можно было бы создавать подразделы и товары
				transaction.addCommandUnit(SaveItemDBUnit.get(section).noFulltextIndex());
				// Раздел сохраняется сразу
				transaction.execute();
				// Информация
				info.setProcessed(++сreated);
				stack.push(section);
				allInFilter = new HashSet<>();
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
			}
			// Пользовательские параметры продуктов
			else if (IConst.PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
				paramName = attributes.getValue(IConst.NAME_ATTRIBUTE);
				allInFilter.add(paramName);
				String notInFilter = attributes.getValue("filter");
				if (notInFilter != null && notInFilter.equalsIgnoreCase("false"))
					allInFilter.remove(paramName);
			}
			// Инфо
			info.setLineNumber(locator.getLineNumber());
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

		
	@Override
	public void endDocument() {
		try {
			transaction.execute();
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			//info.addError(ExceptionUtils.getExceptionStackTrace(e), locator.getLineNumber(), locator.getColumnNumber());
			info.addError(e.toString(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

}
