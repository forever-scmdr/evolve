package extra;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class CatalogCreationHandler extends DefaultHandler {

	private static final Set<String> PRODUCT_COMMON_PARAMS = new HashSet<>();
	static {
		CollectionUtils.addAll(PRODUCT_COMMON_PARAMS, IConst.COMMON_PARAMS);
	}
	
	private Stack<Item> stack = new Stack<>();
	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();
	
	private DelayedTransaction transaction = new DelayedTransaction(User.getDefaultUser());
	private String className = null;
	private boolean fatalError = false;

	private Integrate_2.Info info; // информация для пользователя
	private int сreated = 0; // информация для пользователя
	
	
	CatalogCreationHandler(Item catalog, Integrate_2.Info info) {
		stack.push(catalog);
		this.info = info;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (fatalError) return;
			if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName) || IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
				Item top = stack.pop();
				// Товар может быть уже сохранен, поэтому нужна проверка top.getId() == 0
				if (qName.equals(IConst.PRODUCT_ELEMENT) && top.getId() == 0) {
					// Установка значения нормы заказа (берется из раздела в случае если не установлена в товаре)
					if (top.getDoubleValue(ItemNames.PRODUCT.MIN_QTY, 0d) == 0d) {
						Item section = stack.peek();
						if (section.getDoubleValue(ItemNames.SECTION.NORM, 0d) != 0d)
							top.setValue(ItemNames.PRODUCT.MIN_QTY, section.getValue(ItemNames.SECTION.NORM));
						else
							top.setValue(ItemNames.PRODUCT.MIN_QTY, 1d);
					}
					// Установка преанализированного значения для поиска
					String name = top.getStringValue(ItemNames.PRODUCT.NAME, "");
					String mark = top.getStringValue(ItemNames.PRODUCT.MARK, "");
					String code = top.getStringValue(ItemNames.PRODUCT.CODE, "");
					String fullName = BelchipStrings.fromRtoE(name + ' ' + mark);
					String fullNameAnalyzed = BelchipStrings.preanalyze(name) + ' ' + BelchipStrings.preanalyze(mark);
					top.setValue(ItemNames.PRODUCT.SEARCH, fullName);
					top.setValue(ItemNames.PRODUCT.SEARCH, fullNameAnalyzed);
					top.setValue(ItemNames.PRODUCT.SEARCH, code);
					String analog = top.getStringValue(ItemNames.PRODUCT.ANALOG, "");
					if (!StringUtils.isBlank(analog)) {
						top.setValue(ItemNames.PRODUCT.ANALOG_SEARCH, BelchipStrings.fromRtoE(analog));
						top.setValue(ItemNames.PRODUCT.ANALOG_SEARCH, BelchipStrings.preanalyze(analog));
					}
					// Установка поличества товаров на складах
					Double qty = top.getDoubleValue(IConst.QTY_ELEMENT);
					Double qtyS1 = top.getDoubleValue(IConst.QTY_S1_ELEMENT);
					Double qtyS2 = top.getDoubleValue(IConst.QTY_S2_ELEMENT);
					if (qtyS1 != null || qtyS2 != null) {
						qtyS1 = (qtyS1 == null || qtyS1 < 0) ? 0 : qtyS1;
						qtyS2 = (qtyS2 == null || qtyS2 < 0) ? 0 : qtyS2;
						double sumStores = qtyS1 + qtyS2;
						if (sumStores > 0 || qty == null)
							top.setValue(IConst.QTY_ELEMENT, sumStores);
						else
							top.setValue(IConst.QTY_ELEMENT, 0d);
						top.setValue(IConst.QTY_S1_ELEMENT, qtyS1);
						top.setValue(IConst.QTY_S2_ELEMENT, qtyS2);
					} else if (qty == null) {
						top.setValue(IConst.QTY_ELEMENT, 0d);
					}
					// Добавление команды сохранения продукта
					transaction.addCommandUnit(SaveItemDBUnit.new_(top, stack.peek()).noFulltextIndex());
					if (transaction.getCommandCount() == 20) {
						сreated += transaction.execute();
						info.setProductsCreated(сreated);
					}
				} else if (qName.equals(IConst.SECTION_ELEMENT)) {
					сreated += transaction.execute();
					info.setProductsCreated(сreated);
				}
			}
			// Установка значения параметра
			if (parameterReady) {
				Item product = stack.peek();
				product.setValueUI(paramName, paramValue.toString().trim());
			}
			parameterReady = false;
			// Инфо
			info.setCurrentLine(locator.getLineNumber());
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		try {
			if (!parameterReady || fatalError)
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
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		parameterReady = false;
		if (fatalError) return;
		try {
			// Раздел
			if (IConst.SECTION_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				String name = attributes.getValue(IConst.NAME_ATTRIBUTE);
				String code = attributes.getValue(IConst.CODE_ATTRIBUTE);
//-- PIC PATH FIX
//-- @author Anton
				String picPath = attributes.getValue(IConst.PIC_PATH_ATTRIBUTE);
				Item section = ItemQuery.loadByParamValue(ItemNames.SECTION._ITEM_NAME, ItemNames.SECTION.CODE, code).get(0);
//-- Сразу сохраняем картинку раздела.
				if(StringUtils.isNotBlank(picPath)){
					section.setValue(ItemNames.SECTION.PIC_PATH, picPath);
					transaction.addCommandUnit(SaveItemDBUnit.get(section));
				}
				className = name;
				if (stack.size() != 1) {
					className = parent.getStringValue(ItemNames.SECTION.NAME) + " " + className;
				}
				className = Strings.createXmlElementName(className);
				stack.push(section);
			}
			// Продукт
			else if (IConst.PRODUCT_ELEMENT.equalsIgnoreCase(qName)) {
				Item parent = stack.peek();
				ItemType productDesc = ItemTypeRegistry.getItemType(className);
				if (productDesc == null) // Возможно в случае, если в разделе не было ни одного продукта с дополнительными (нестандартными) параметрами
					productDesc = ItemTypeRegistry.getItemType(ItemNames.PRODUCT._ITEM_NAME);
				Item product = Item.newChildItem(productDesc, parent);
				byte hitFlag = Boolean.parseBoolean(attributes.getValue(IConst.HIT_ATTRIBUTE)) ? (byte)1 : (byte)0;
				byte newFlag = Boolean.parseBoolean(attributes.getValue(IConst.NEW_ATTRIBUTE)) ? (byte)1 : (byte)0;
				product.setValue(ItemNames.PRODUCT.HIT, hitFlag);
				product.setValue(ItemNames.PRODUCT.NEW, newFlag);
				stack.push(product);
			}

			// Параметры продуктов (общие)
			else if (PRODUCT_COMMON_PARAMS.contains(qName.toLowerCase())) {
				paramName = qName;
				parameterReady = true;
			}
			// Пользовательские параметры продуктов
			else if (IConst.PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
				paramName = Strings.createXmlElementName(attributes.getValue(IConst.NAME_ATTRIBUTE));
				parameterReady = true;
			}
			paramValue = new StringBuilder();
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
		// Инфо
		info.setCurrentLine(locator.getLineNumber());
	}

	@Override
	public void endDocument() throws SAXException {
		try {
			transaction.execute();
			сreated += transaction.execute();
			info.setProductsCreated(сreated);
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
	}
}
