package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import extra._generated.Section;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class CatalogCreationHandler extends DefaultHandler {
	
	private static final SimpleDateFormat SOON_FORMAT = new SimpleDateFormat("ddMMyy");
	private static final Set<String> PRODUCT_COMMON_PARAMS = new HashSet<String>();
	private static final char[]ETC = new char[] {'a','c','d','e','f'};
	
	static {
		CollectionUtils.addAll(PRODUCT_COMMON_PARAMS, IConst.COMMON_PARAMS);
	}
	
	private Stack<Item> stack = new Stack<>();
	private Locator locator;
	private boolean parameterReady = false;
	private boolean currencyReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();
	private ArrayList<Integer> bigArts = new ArrayList<>();
	
	private DelayedTransaction transaction = new DelayedTransaction(User.getDefaultUser());
	private String className = null;
	private boolean fatalError = false;

	private Integrate_2.Info info; // информация для пользователя
	private int сreated = 0; // информация для пользователя
	
	
	public CatalogCreationHandler(Item catalog, Integrate_2.Info info) {
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
					if (top.getDoubleValue(Product.MIN_QTY, 0d) == 0d) {
						Item section = stack.peek();
						if (section.getDoubleValue(Section.NORM, 0d) != 0d)
							top.setValue(Product.MIN_QTY, section.getValue(Section.NORM));
						else
							top.setValue(Product.MIN_QTY, 1d);
					}
					// Установка преанализированного значения для поиска
					String name = top.getStringValue(Product.NAME, "");
					String mark = top.getStringValue(Product.MARK, "");
					String code = top.getStringValue(Product.CODE, "");
					String fullName = BelchipStrings.fromRtoE(name + ' ' + mark);
					String fullNameAnalyzed = BelchipStrings.preanalyze(name) + ' ' + BelchipStrings.preanalyze(mark);
					top.setValue(Product.SEARCH, fullName);
					top.setValue(Product.SEARCH, fullNameAnalyzed);
					top.setValue(Product.SEARCH, code);
					String strictSearch = name + ' ' + mark + ' ' + code;
					int lim = strictSearch.length() < 80? strictSearch.length() : 80;
					top.setValue(Product.STRICT_SEARCH, strictSearch.substring(0, lim));
					if(strictSearch.length() > 80) {
						top.setValue(Product.STRICT_SEARCH, strictSearch.substring(strictSearch.length()/2));
					}
					
					//Ignore Analogs from XML file FIX 29.10.2018
					//top.removeValue(ItemNames.product.ANALOG_CODE);
					
					String analog = top.getStringValue(Product.ANALOG, "");
					if (!StringUtils.isBlank(analog)) {
						top.setValue(Product.ANALOG_SEARCH, BelchipStrings.fromRtoE(analog));
						top.setValue(Product.ANALOG_SEARCH, BelchipStrings.preanalyze(analog));
					}
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
					}else {
						top.setValue("is_service", (byte)0);
					}
					
					
					String contextPath = AppContext.getContextPath();
					for(char c : ETC) {
						String extra = "sitepics/"+top.getStringValue("pic_path")+c+".jpg";
						File ep = Paths.get(contextPath, extra).toFile();
						if(ep.isFile()) {
							top.setValue("extra_pic", extra);
						}
					}
					
					// Добавление команды сохранения продукта
					transaction.addCommandUnit(SaveItemDBUnit.get(top).noFulltextIndex());
					if (transaction.getCommandCount() >= 20) {
						сreated += transaction.execute();
						info.setProductsCreated(сreated);
					}
				} else if (qName.equals(IConst.SECTION_ELEMENT)) {
					сreated += transaction.execute();
					info.setProductsCreated(сreated);
				}
			}
			// Установка значения параметра
			if (parameterReady && !(IConst.QTY_S1_ELEMENT.equals(paramName)||IConst.QTY_S2_ELEMENT.equals(paramName))) {
				Item product = stack.peek();
				String pvs = paramValue.toString();
				product.setValueUI(paramName, pvs.trim());
			}else if(IConst.QTY_S1_ELEMENT.equals(paramName)){
				Item product = stack.peek();
				String pvs = paramValue.toString();
				product.setValueUI(Product.QTY, pvs.trim());
			}
			else if("rub".equalsIgnoreCase(qName)) {
				Item rub = ItemQuery.loadSingleItemByParamValue("currency", "name", "rub");
				if(rub != null) {
					rub.setValueUI("ratio", paramValue.toString().trim());
					currencyReady = false;
					transaction.addCommandUnit(SaveItemDBUnit.get(rub).ignoreUser(true).noFulltextIndex());
				}
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
				Item section = ItemQuery.loadByParamValue(Section._NAME, Section.CODE, code).get(0);
//-- Сразу сохраняем картинку раздела.
				if(StringUtils.isNotBlank(picPath)){
					section.setValue(Section.PIC_PATH, picPath);
					transaction.addCommandUnit(SaveItemDBUnit.get(section).ignoreUser(true));
				}
				
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
				product.setValue("type", productDesc.getCaption());
				if(soonFlag != null){
					product.setValue(IConst.SOON_ATTRIBUTE, soonFlag.getTime()+3*60*60*1000);
				}
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
			else if("rub".equalsIgnoreCase(qName)) {
				currencyReady = true;
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
			info.setOperation("Поиск новых товаров");
			Collections.sort(bigArts, Collections.reverseOrder());
			int i = 0;
			for(int art : bigArts){				
				String code = String.format("%05d",art);
				Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, Product.CODE , code);
				if(product.getDoubleValue(Product.QTY,0d) < product.getDoubleValue(Product.MIN_QTY, 1d)) continue;
				if(i++>19)break;
				product.setValue(Product.NEW, (byte)1);
				transaction.addCommandUnit(SaveItemDBUnit.get(product).ignoreUser(true).noFulltextIndex());
			}
			transaction.execute();
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}
	}
}
