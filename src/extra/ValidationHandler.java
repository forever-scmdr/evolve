package extra;

import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ValidationHandler extends DefaultHandler {

	private static final Set<String> PRODUCT_COMMON_PARAMS = new HashSet<>();
	static {
		CollectionUtils.addAll(PRODUCT_COMMON_PARAMS, IConst.COMMON_PARAMS);
	}
	
	private Stack<Item> stack = new Stack<>();
	private Locator locator;
	
	private Integrate_2.Info info;

	ValidationHandler(Item catalog, Integrate_2.Info info) {
		stack.push(catalog);
		this.info = info;
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName) || IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
			stack.pop();
		}
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		try {
			// Раздел
			if (IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				if (parent == null || (!parent.getTypeName().equals("catalog")
								&& !parent.getTypeName().equals(IConst.SECTION_ELEMENT)
								&& !parent.getTypeName().equals(IConst.SECTION_FIRST_ELEMENT))) {
					info.addError("Нарушение вложенности раздела", locator.getLineNumber(), locator.getColumnNumber());
					return;
				}
				String name = attributes.getValue(IConst.NAME_ATTRIBUTE);
				String code = attributes.getValue(IConst.CODE_ATTRIBUTE);
				String picPath = attributes.getValue(IConst.PIC_PATH_ATTRIBUTE);
				if (StringUtils.isBlank(name))
					info.addError("Не задано название раздела", locator.getLineNumber(), locator.getColumnNumber());
				if (StringUtils.isBlank(code))
					info.addError("Не задан уникальный код раздела", locator.getLineNumber(), locator.getColumnNumber());
				ItemType itemDesc = ItemTypeRegistry.getItemType(ItemNames.SECTION._ITEM_NAME);
				if (stack.size() == 1)
					itemDesc = ItemTypeRegistry.getItemType(ItemNames.SECTION_FIRST._ITEM_NAME);
				Item section = Item.newChildItem(itemDesc, parent);
				section.setValue(IConst.NAME_ATTRIBUTE, name);
				section.setValue(IConst.CODE_ATTRIBUTE, code);
				section.setValue(IConst.PIC_PATH_ATTRIBUTE, picPath);
				stack.push(section);
			}
			// Продукт
			else if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				if (parent == null || (!parent.getTypeName().equals(IConst.SECTION_ELEMENT) && !parent.getTypeName().equals(
								IConst.SECTION_FIRST_ELEMENT))) {
					info.addError("Нарушение вложенности товара", locator.getLineNumber(), locator.getColumnNumber());
					return;
				}
				Item product = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT._ITEM_NAME), parent);
				stack.push(product);
			}

			// Параметры продуктов (общие)
			else if (PRODUCT_COMMON_PARAMS.contains(qName.toLowerCase())) {
				Item product = stack.peek();
				if (product == null) {
					info.addError("Нарушение вложенности параметров товара", locator.getLineNumber(), locator.getColumnNumber());
				}
			}
			// Пользовательские параметры продуктов
			else if (IConst.PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
				Item product = stack.peek();
				if (product == null) {
					info.addError("Нарушение вложенности параметров товара", locator.getLineNumber(), locator.getColumnNumber());
				}
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}
}
