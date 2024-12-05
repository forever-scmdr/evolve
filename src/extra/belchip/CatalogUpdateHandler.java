package extra.belchip;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Currencies;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CatalogUpdateHandler extends DefaultHandler {
	
	private static final SimpleDateFormat SOON_FORMAT = new SimpleDateFormat("ddMMyy");

	private Item product = null;

	private static enum Qnames {
		price, name, mark, code, unit, qty1, qty2, pic_path, special_price
	};

	private String price;
	private String name;
	private String nameExtra;
	private String unit;
	private String qty1;
	private String picPath;
	
	private boolean nw = false;
	private boolean hit = false;
	private String spec = null;
	private Date soon;

	private Locator locator;
	private boolean parameterReady = false;
	private StringBuilder paramValue = new StringBuilder();

	private SynchronousTransaction transaction = new SynchronousTransaction(User.getDefaultUser());
	private boolean fatalError = false;

	private int сreated = 0;
	private IntegrateBase parentIntegration;
	private Item catalog;
	private Currencies currencies;

	private boolean currencyReady;
	//private FileWriter writer;

	protected CatalogUpdateHandler(IntegrateBase parent) throws Exception {
		this.parentIntegration = parent;
		catalog = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.CATALOG, User.getDefaultUser());
		currencies = Currencies.get(ItemUtils.ensureSingleChild(ItemNames.CURRENCIES, User.getDefaultUser(), catalog));
		//File f = Paths.get(AppContext.getContextPath(), "timer_log.txt").toFile();
		//writer = new FileWriter(f);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (fatalError)
			return;
		try {
			if (ItemNames.PRODUCT.equalsIgnoreCase(qName)) {
				if(product == null) return;
				//Timer.getTimer().start("Сравнение", "Сравнение значений из файла со значениями из БД");
				boolean needsSave = false;
				double q1 = (StringUtils.isBlank(qty1))? 0d: Double.parseDouble(qty1.replaceAll("[^0-9,.-]", "").replace(',', '.'));
				//double q2 = (StringUtils.isBlank(qty2))? 0d: Double.parseDouble(qty2.replaceAll("[^0-9,.-]", "").replace(',', '.'));
				
				q1 = (q1<0)?0d:q1;
				//q2 = (q2<0)?0d:q2;
				double q = Math.round((q1)*100d)/100d;
				
				byte hitProduct = (hit)? (byte)1: product.getByteValue(Product.HIT, (byte)0);
				byte newProduct = (nw)? (byte)1: product.getByteValue(Product.NEW, (byte)0);
				long soonLong = (soon != null)? soon.getTime()+3*60*60*1000 : 0L;
				byte inStock = (q > 0 && StringUtils.isNotBlank(price) || soon != null)? (byte)1:(byte)0;
				
				//double
				if (StringUtils.isNotBlank(price)) {
					needsSave = true;
					product.setValueUI(Product.PRICE, price);
				}
//				if((StringUtils.isNotBlank(qty1) || StringUtils.isNotBlank(qty1)) && product.getDoubleValue(Product.QTY,0d) != q){
//					needsSave = true;
					//product.clearValue(Product.QTY);
					product.setValue(Product.QTY, q);
//				}

				
				//byte
				boolean overrideAvlb = StringUtils.isNotBlank(qty1) || soon != null;
				
				if(product.getByteValue("is_service", (byte)0) == 1) {
					overrideAvlb = true;
					inStock = (byte)1;
					needsSave = true;
					product.setValue(Product.QTY, 100000d);
				}
				
				if((overrideAvlb && product.getByteValue(Product.AVAILABLE,(byte)0) != inStock) || product.getByteValue(Product.AVAILABLE) == null){
					needsSave = true;
					product.setValue(Product.AVAILABLE, inStock);
				}
				if((overrideAvlb && product.getDoubleValue(Product.AVAILABLE2, (double)0) != inStock) || product.getDoubleValue(Product.AVAILABLE2) == null){
					needsSave = true;
					product.setValue(Product.AVAILABLE2, (double)inStock);
				}
				if(product.getByteValue(Product.NEW,(byte)0) != newProduct || product.getByteValue(Product.NEW) == null){
					needsSave = true;
					product.setValue(Product.NEW, newProduct);
				}
				if(product.getByteValue(Product.HIT,(byte)0) != hitProduct || product.getByteValue(Product.HIT) == null){
					needsSave = true;
					product.setValue(Product.HIT, hitProduct);
				}
				
				//long (date)
				if(soon != null && product.getLongValue(Product.SOON, 0L) != soonLong){
					needsSave = true;
					product.setValue(Product.SOON, soonLong);
				}
				
				//String
				if(StringUtils.isNotBlank(name) && !product.getStringValue(Product.NAME,"").equals(name)){
					needsSave = true;
					product.setValue(Product.NAME, name);
				}
				if(StringUtils.isNotBlank(nameExtra) && !product.getStringValue(Product.NAME_EXTRA,"").equals(nameExtra)){
					needsSave = true;
					product.setValue(Product.NAME_EXTRA, nameExtra);
				}
				if(StringUtils.isNotBlank(unit) && !product.getStringValue(Product.UNIT,"").equals(unit)){
					needsSave = true;
					product.setValue(Product.UNIT, unit);
				}
				if(StringUtils.isNotBlank(picPath) && !product.getStringValue(Product.PIC_PATH,"").equals(picPath)){
					needsSave = true;
					product.setValue(Product.PIC_PATH, picPath);
				}
				if(!product.getStringValue(Product.SPECIAL_PRICE,"").equals(spec)){
					needsSave = true;
					if (StringUtils.isBlank(spec))
						product.clearValue(Product.SPECIAL_PRICE);
					else
						product.setValue(Product.SPECIAL_PRICE, spec);
				}
				
//				if(needsSave){
					transaction.executeCommandUnit(SaveItemDBUnit.get(product).noTriggerExtra().noFulltextIndex().ignoreFileErrors(true));
					transactionExecute();
					parentIntegration.setProcessed(++сreated);
					
//				}
			} else if(parameterReady) {
					if (!EnumUtils.isValidEnum(Qnames.class, qName))
						return;
					Qnames ordinal = Qnames.valueOf(qName);
					switch (ordinal) {
					case price:
						price = paramValue.toString();
						break;
					case name:
						name = paramValue.toString().trim();
						break;
					case unit:
						unit = paramValue.toString().trim();
						break;
					case qty1:
						qty1 = paramValue.toString();
						break;
					case mark:
						nameExtra = paramValue.toString().trim();
						break;
					case pic_path:
						picPath = paramValue.toString().trim();
						break;
					case special_price:
						spec = paramValue.toString().trim();
						break;
					case code:
						String code = paramValue.toString();
						if(StringUtils.isBlank(code)) {
							parentIntegration.addError("DEVICE CODE EMPTY!", String.valueOf(locator.getLineNumber()));
						}
						else {
							try {
								product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, Product.CODE, code);
							}catch(Exception e) {
								parentIntegration.addError("unable to load product with code "+ code, String.valueOf(locator.getLineNumber()));
								ServerLogger.error(e);
							}
						}
						//ItemQuery q = ItemQuery.newItemQuery(ItemNames.PRODUCT);
						//q.addParameterCriteria(ItemNames.product.CODE, code, "=", null, COMPARE_TYPE.SOME);
						//product = q.loadFirstItem();
						break;
					default:
						break;
					}
			}
			else if ("rub".equalsIgnoreCase(qName)) {
				//Currencies currencies = Currencies.get(ItemQuery.loadSingleItemByName(ItemNames.CURRENCIES));
				if (currencies != null) {
					currencies.setUI_RUB_rate(paramValue.toString().trim());
					currencies.set_RUB_scale(BigDecimal.valueOf(100));
					currencyReady = false;
					transaction.executeCommandUnit(SaveItemDBUnit.get(currencies).ignoreUser(true).noFulltextIndex());
				}
			}
			else if ("usd".equalsIgnoreCase(qName)) {
				//Currencies currencies = Currencies.get(ItemQuery.loadSingleItemByName(ItemNames.CURRENCIES));
				if (currencies != null) {
					currencies.setUI_USD_rate(paramValue.toString().trim());
					currencies.set_USD_scale(BigDecimal.ONE);
					currencyReady = false;
					transaction.executeCommandUnit(SaveItemDBUnit.get(currencies).ignoreUser(true).noFulltextIndex());
				}
			}
			else if ("eur".equalsIgnoreCase(qName)) {
				//Currencies currencies = Currencies.get(ItemQuery.loadSingleItemByName(ItemNames.CURRENCIES));
				if (currencies != null) {
					currencies.setUI_EUR_rate(paramValue.toString().trim());
					currencies.set_EUR_scale(BigDecimal.ONE);
					currencyReady = false;
					transaction.executeCommandUnit(SaveItemDBUnit.get(currencies).ignoreUser(true).noFulltextIndex());
				}
			}
			
			parameterReady = false;
			// Инфо
			parentIntegration.setLineNumber(locator.getLineNumber());
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString();
			
			String vv = "productId = "+product.getId();
			vv += " unit = "+unit;
			
			parentIntegration.addError(sStackTrace, locator.getLineNumber(), locator.getColumnNumber());
			parentIntegration.addError( vv , locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;
		}

	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		parameterReady = false;
		if (fatalError)
			return;
		try {
			paramValue = new StringBuilder();
			if (EnumUtils.isValidEnum(Qnames.class, qName)) {
				// paramName = qName;
				parameterReady = true;
			} else if(ItemNames.PRODUCT.equalsIgnoreCase(qName)) {
				product = null;
				price = null;
				name = null;
				nameExtra = null;
				unit = null;
				qty1 = null;
				picPath = null;
				nw = false;
				hit = false;
				soon = null;
				spec = null;
				nw = Boolean.parseBoolean(attributes.getValue(IConst.NEW_ATTRIBUTE));
				hit = Boolean.parseBoolean(attributes.getValue(IConst.HIT_ATTRIBUTE));
				soon =(StringUtils.isBlank(attributes.getValue(IConst.SOON_ATTRIBUTE))) ? null : SOON_FORMAT.parse(attributes.getValue(IConst.SOON_ATTRIBUTE));
			}
			else if("rub".equalsIgnoreCase(qName) || "usd".equalsIgnoreCase(qName) || "eur".equalsIgnoreCase(qName)) {
				currencyReady = true;
				paramValue = new StringBuilder();
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			parentIntegration.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());

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
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString();
			parentIntegration.addError(sStackTrace, locator.getLineNumber(), locator.getColumnNumber());
			fatalError = true;

		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void endDocument() throws SAXException {
		if (fatalError)	return;
		try {
			parentIntegration.setOperation("Создание списка новинок");
			ArrayList<Item> allProducts = new ArrayList<Item>();
			ItemQuery q = new ItemQuery(ItemNames.PRODUCT);
			q.addSorting(Product.CODE, "DESC");
			q.setLimit(500,1);

			allProducts.addAll(q.loadItems());
			Collections.sort(allProducts, new Comparator<Item>() {
				public int compare(Item o1, Item o2) {
					int c1 = Integer.parseInt(o1.getStringValue(Product.CODE,"0"));
					int c2 = Integer.parseInt(o2.getStringValue(Product.CODE,"0"));
					if(c2 > c1) return 1;
					else if(c1 > c2) return -1;
					return 0;
				}
			});
			int i = 0;
			for(Item product : allProducts) {
				
				if(product.getDoubleValue(Product.QTY,0d) > 0){
					product.setValue(Product.NEW, (byte)1);
					transaction.executeCommandUnit(SaveItemDBUnit.get(product).ignoreUser(true).noFulltextIndex());
					if(++i>19)break;

				}
				
			}
			transaction.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	};

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}
	
	public static void main (String[]args){
		String test = "1 456,56";
		System.out.println(test.replaceAll("\\s", "").replace(',', '.'));
	}

	private void transactionExecute() throws Exception {
		if (transaction.getUncommitedCount() >= 100)
			transaction.commit();
	}
}
