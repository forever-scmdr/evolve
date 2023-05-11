package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.model.*;
import ecommander.model.datatypes.DateDataType;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Управление корзиной
 * Created by E on 2/3/2018.
 */
public abstract class BasicCartManageCommand extends Command {

	public enum Strategy {
		extra_line_overbuy, deny_overbuy, ignore_overbuy;

		public static Strategy create(String name) {
			name = StringUtils.lowerCase(name);
			if (StringUtils.equalsAny(name, "extra_line_overbuy",
					"extra-line-overbuy", "extralineoverbuy", "extra_line_over_buy", "extra-line-over-buy"))
				return extra_line_overbuy;
			if (StringUtils.equalsAny(name, "ignore_overbuy",
					"ignore-overbuy", "ignoreoverbuy", "ignore_over_buy", "ignore-over-buy"))
				return ignore_overbuy;
			return deny_overbuy;
		}
	}

	public enum CookieStrategy{
		COOKIE, USER_ITEM, COOKIE_AND_USER_ITEM
	}

	protected static final String PRODUCT_ITEM = "abstract_product";
	protected static final String CART_ITEM = "cart";
	protected static final String BOUGHT_ITEM = "bought";
	protected static final String PURCHASE_ITEM = "purchase";
	protected static final String USER_ITEM = "user";
	protected static final String PRICE_PARAM = "price";
	protected static final String NOT_AVAILABLE = "not_available";
	protected static final String QTY_PARAM = "qty";
	protected static final String QTY_AVAIL_PARAM = "qty_avail";
	protected static final String QTY_TOTAL_PARAM = "qty_total";
	protected static final String SUM_PARAM = "sum";
	protected static final String PROD_PARAM = "prod";
	protected static final String CODE_PARAM = "code";
	protected static final String NAME_PARAM = "name";
	protected static final String PROCESSED_PARAM = "processed";
	protected static final String SYSTEM_ITEM = "system";
	protected static final String COUNTER_ITEM = "counter";
	protected static final String COUNT_PARAM = "count";
	protected static final String NUM_PARAM = "num";
	protected static final String DATE_PARAM = "date";
	protected static final String EMAIL_PARAM = "email";
	protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";
	public static final String REGISTERED_GROUP = "registered";


	private static final String CART_COOKIE = "cart_cookie";
	private static final String COMPLEX_COOKIE = "cart_complex_cookie";
	private static final String STRATEGY_VAR = "strategy";

	final String IN_PROGRESS = "in_progress";
	final String TRUE = "true";


	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


	protected static final Double MAX_QTY = 1000000000000d;


	protected Item cart;
	protected Item userItem;
	protected Strategy strategy = Strategy.deny_overbuy;
	protected CookieStrategy cookieStrategy = CookieStrategy.USER_ITEM;


	protected void checkStrategy() {
		strategy = Strategy.create(getVarSingleValueDefault(STRATEGY_VAR, Strategy.extra_line_overbuy.name()));
	}

	/**
	 * Добавить товар в корзину
	 *
	 * @return
	 * @throws Exception
	 */
	public ResultPE addToCart() throws Exception {
		checkStrategy();
		String idStr = getVarSingleValue(PROD_PARAM);
		double quantity = 0;
		long prodId = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM));
			prodId = Long.parseLong(idStr);
		} catch (Exception e) {/**/}
		addProduct(prodId, quantity);
		recalculateCart();

		List<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId())
				.stream()
				.filter(b -> b.getByteValue("is_complex", (byte)0) == 0)
				.collect(Collectors.toList());

		saveCartCookie(boughts);
		return getResult("ajax");
	}


	public ResultPE delete() throws Exception {
		// В этом случае ID не самого продукта, а объекта bought
		long boughtId = Long.parseLong(getVarSingleValue(BOUGHT_ITEM));
		loadCart();
		Item bought = getSessionMapper().getItem(boughtId, BOUGHT_ITEM);
		List<Object> o = bought.getValues("option");
		if (o.size() == 0) {
			getSessionMapper().removeItems(boughtId, BOUGHT_ITEM);
			recalculateCart();
			saveCartCookies();
		}
		return getResult("cart");
	}


	public ResultPE recalculate() throws Exception {
		checkStrategy();
		updateQtys();
		recalculateCart();
		saveCartCookies();
		return getResult("cart");
	}


	public ResultPE proceed() throws Exception {
		checkStrategy();
		updateQtys();
		recalculateCart();
		saveCartCookies();
		return getResult("proceed");
	}


	public ResultPE customerForm() throws Exception {
		// Сохранение формы в сеансе (для унификации с персональным айтемом анкеты)
		Item form = getItemForm().getItemSingleTransient();

		List<Object> dates = form.getListExtra("p-date");
		List<Object> sums = form.getListExtra("p-sum");
		if(dates.size() != sums.size()){
			return getResult("cart");
		}

		loadCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());

		MultipleHttpPostForm f = getItemForm();


		for (Item bought : boughts) {
			ItemInputValues vals = f.getReadOnlyItemValues(bought.getId());
			String dealerDate = vals.getStringParam("proposed_dealer_date");
			long d = DateDataType.parseDate(dealerDate, DATE_FORMATTER);
			bought.setValue("proposed_dealer_date", d);
			getSessionMapper().saveTemporaryItem(bought);
		}

		//	recalculateCart();
		saveCartCookies();

		if (!validate()) {
			return getResult("validation_failed");
		}

		loadCart();
		if (StringUtils.equalsIgnoreCase(cart.getStringExtra(IN_PROGRESS), TRUE)) {
			return getResult("confirm");
		}
		cart.setExtra(IN_PROGRESS, TRUE);
		getSessionMapper().saveTemporaryItem(cart);
		if ((Byte) cart.getValue(PROCESSED_PARAM, (byte) 0) == (byte) 1) {
			return getResult("confirm");
		}

		Item system = ItemUtils.ensureSingleRootAnonymousItem(SYSTEM_ITEM, getInitiator());
		Item counter = ItemUtils.ensureSingleAnonymousItem(COUNTER_ITEM, getInitiator(), system.getId());

		boolean simpleExists = StringUtils.isNotBlank(getCartCookieString(CART_COOKIE));
		boolean complexExists = StringUtils.isNotBlank(getCartCookieString(COMPLEX_COOKIE));

		ResultPE res = getResult("confirm");

		try {
			if (simpleExists) {
				processOrder(counter, form);
				res.setVariable("simple_order_num", cart.getStringValue("order_num"));
			}

			if (complexExists) {
				processPreOrder(counter, form);
				res.setVariable("complex_order_num", cart.getStringValue("order_num"));
			}
		} catch (MessagingException e) {
			ServerLogger.error("Unable to send email", e);
			cart.setExtra(IN_PROGRESS, null);
			getSessionMapper().saveTemporaryItem(cart);
			return getResult("email_send_failed").setVariable("message", "Не удалось отправить email.");
		} catch (Exception e) {
			ServerLogger.error("Cart failed", e);
			cart.setExtra(IN_PROGRESS, null);
			getSessionMapper().saveTemporaryItem(cart);
			return getResult("general_error");
		}

		cart.setValue(PROCESSED_PARAM, (byte) 1);
		cart.setExtra(IN_PROGRESS, null);
		getSessionMapper().saveTemporaryItem(cart);

		clearCookies();

		return res;
	}

	protected  void clearCookies() throws Exception {
		setCookieVariable(CART_COOKIE, null);
		setCookieVariable(COMPLEX_COOKIE, null);
		if(userItem != null){
			userItem.clearValue(CART_COOKIE);
			userItem.clearValue(COMPLEX_COOKIE);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(userItem).ignoreUser());
		}
	}

	private void processPreOrder(Item counter, Item form) throws Exception {
		String orderNumber = generateOrderNumber(counter);

		// Подготовка тела письма
		String regularTopic = "Предзаказ №" + orderNumber + " от " + DATE_FORMAT.format(new Date());

		final String customerEmail = userItem.getStringValue("email");
		final String shopEmail = getVarSingleValue("email");
		final String customerEmailTemplate = "order_email";
		final String shopEmailTemplate = pageExists("shop_email") ? "shop_email" : customerEmailTemplate;

		// Письмо для продавца
		// sendEmail(regularTopic, shopEmail, shopEmailTemplate);
		// Письмо для покупателя
		//sendEmail(regularTopic, customerEmail, customerEmailTemplate, true);

		List<Item> boughts = getSessionMapper().getItemsByParamValue(BOUGHT_ITEM, "is_complex", (byte) 1);

		List<Object> dates = form.getListExtra("p-date");
		List<Object> sums = form.getListExtra("p-sum");

		TreeMap<String, String> payments = new TreeMap<>();

		for(int i = 0; i < dates.size(); i++){
			payments.put(dates.get(i).toString(), sums.get(i).toString());
		}

		String xml = buildPreOrderXml(boughts, payments);
		saveToFile(xml, orderNumber);
		saveToHistory(boughts, payments, form, "p_sum", "p_sum_discount", "p_sum_saved");
		updateCounterItem(counter, cart.getStringValue("order_num"));
	}

	private void saveToFile(String xml, String name) throws IOException {
		Path p = Paths.get(AppContext.getContextPath(), "orders", name+".xml");
		FileUtils.writeStringToFile(p.toFile(), xml, StandardCharsets.UTF_8);
	}

	protected String buildPreOrderXml(Collection<Item> boughts, Map<String, String> payments, boolean... isComplex) throws Exception {

		boolean complex = isComplex.length == 0 || isComplex[0];

		XmlDocumentBuilder orderXml = XmlDocumentBuilder.newDoc();
		orderXml.startElement("order", "date", DATE_FORMAT.format(new Date()), "number", cart.getValue("order_num"));

		//Buyers's contacts
		orderXml.startElement("buyer");
		ItemType userType = userItem.getItemType();
		for (String paramName : userType.getParameterNames()) {
			Object value = userItem.getValue(paramName);

			if("password".equals(paramName) || "registered".equals(paramName) || "cart_cookie".equals(paramName) || "cart_complex_cookie".equals(paramName)) continue;

			if (value != null && StringUtils.isNotBlank(value.toString())) {
				orderXml.addElement(paramName, value);
			}
		}
		orderXml.endElement();

		//Shopping list
		orderXml.startElement("products");

		for (Item bought : boughts) {

			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());

			//product
			orderXml
					.startElement("prodcut")
					.addElement(CODE_PARAM, bought.getStringValue(CODE_PARAM))
					.addElement(NAME_PARAM, bought.getStringValue(NAME_PARAM))
					.addElement("custom_name", bought.getStringValue("сomplectation_name"))
					.addElement(PRICE_PARAM, product.getValue(PRICE_PARAM))
					.addElement(SUM_PARAM, bought.getValue(SUM_PARAM));

			if(complex) {
				//options
				orderXml.startElement("options");

				List<Item> options = getSessionMapper().getItemsByName("pseudo_option", bought.getId());


				for (Item option : options) {
					orderXml
							.startElement("option")
							.addElement(CODE_PARAM, option.getStringValue(CODE_PARAM))
							.addElement(NAME_PARAM, option.getStringValue(NAME_PARAM))
							.addElement(PRICE_PARAM, option.getValue(PRICE_PARAM))
							.endElement();
				}

				orderXml.endElement();
			}else{
				orderXml.addElement(QTY_PARAM, bought.getValue(QTY_TOTAL_PARAM));
			}
			orderXml.endElement();
		}
		orderXml.endElement();

		//sum
		if(complex) {
			orderXml
					.startElement("total")
					.addElement(SUM_PARAM, cart.getValue("p_sum"))
					.addElement("sum_discount", cart.getValue("p_sum_discount"))
					.addElement("sum_saved", cart.getValue("p_sum_saved"))
					.endElement();
		}else{
			orderXml
					.startElement("total")
					.addElement(SUM_PARAM, cart.getValue("sum"))
					.addElement("sum_discount", cart.getValue("sum_discount"))
					.addElement("sum_saved", cart.getValue("sum_saved"))
					.endElement();
		}

		//payments
		if(payments != null){
			orderXml.startElement("payments");
			for(Map.Entry<String, String> entry : payments.entrySet()){

				long d = DateDataType.parseDate(entry.getKey(), DATE_FORMATTER);

				orderXml.startElement("payment");
				orderXml.addElement("date", DateDataType.outputDate(d));
				orderXml.addElement("sum", entry.getValue());
				orderXml.endElement();
			}
			orderXml.endElement();
		}


		orderXml.endElement();
		return orderXml.toString();
	}

	private void processOrder(Item counter, Item form) throws Exception {
		String orderNumber = generateOrderNumber(counter);

		// Подготовка тела письма
		String regularTopic = "Заказ №" + orderNumber + " от " + DATE_FORMAT.format(new Date());

		final String customerEmail = userItem.getStringValue("email");
		final String shopEmail = getVarSingleValue("email");
		final String customerEmailTemplate = "order_email";
		final String shopEmailTemplate = pageExists("shop_email") ? "shop_email" : customerEmailTemplate;

		LinkPE customerLink = LinkPE.newDirectLink("link", customerEmailTemplate, false);
		customerLink.addStaticVariable("is_complex", "1");

		LinkPE shopLink = LinkPE.newDirectLink("link", shopEmailTemplate, false);
		shopLink.addStaticVariable("is_complex", "0");

		// Письмо для продавца
		// sendEmail(regularTopic, shopEmail, shopLink);
		// Письмо для покупателя
		//sendEmail(regularTopic, customerEmail, customerLink, true);

		List<Item> boughts = getSessionMapper()
				.getItemsByName(BOUGHT_ITEM, cart.getId())
				.stream()
				.filter(b -> b.getByteValue("is_complex", (byte) 0) == 0)
				.collect(Collectors.toList());

		String xml = buildPreOrderXml(boughts, null, false);
		saveToFile(xml, orderNumber);

		saveToHistory(boughts, null, form, "sum", "sum_discount", "sum_saved");
		updateCounterItem(counter, cart.getStringValue("order_num"));
	}


	private boolean pageExists(String pageName) throws UnsupportedEncodingException, UserNotAllowedException {
		try {
			getExecutablePage(LinkPE.newDirectLink("link", pageName, false).serialize());
		} catch (PageNotFoundException e) {
			return false;
		}
		return true;
	}

	private void sendEmail(String topic, String email, LinkPE templatePageLink, boolean... isCustomerEmail) throws Exception{
		if (StringUtils.isBlank(email)) return;

		Multipart multipart = new MimeMultipart();
		MimeBodyPart textPart = new MimeBodyPart();

		ExecutablePagePE templatePage = getExecutablePage(templatePageLink.serialize());
		ByteArrayOutputStream customerEmailBytes = new ByteArrayOutputStream();
		PageController.newSimple().executePage(templatePage, customerEmailBytes);
		textPart.setContent(customerEmailBytes.toString("UTF-8"), templatePage.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
				+ ";charset=UTF-8");
		multipart.addBodyPart(textPart);
		addExtraEmailBodyPart(isCustomerEmail.length > 0 && isCustomerEmail[0], multipart);
		EmailUtils.sendGmailDefault(email, topic, multipart);
	}

	private void sendEmail(String topic, String email, String templatePageName, boolean... isCustomerEmail) throws Exception {
		if (StringUtils.isBlank(email)) return;

		LinkPE templatePageLink = LinkPE.newDirectLink("link", templatePageName, false);
		sendEmail(topic, email, templatePageLink, isCustomerEmail);
	}

	private void saveToHistory(List<Item> boughts, Map<String, String> payments, Item form, String sumParam, String sumDiscountParam, String sumSavedParam) throws Exception {
		///////////////////////////////////////////////////////////////////////////////////////////////////
		// Сохранить историю
		//

		// 3. Если пользователь не нашелся по email, надо создать нового пользователя
		//    сам пользователь не создается (логин-пароль), только айтем пользователя
		if (userItem == null) {
			if (StringUtils.isNotBlank(form.getStringValue(EMAIL_PARAM))) {
				Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG_ITEM, User.getDefaultUser(),
						UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
				form.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), catalog.getId());
				form.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), User.ANONYMOUS_ID);
				executeCommandUnit(SaveItemDBUnit.get(form).ignoreUser().noTriggerExtra());
				userItem = form;
			}
		}

		// 4. Сохранить все покупки в истории, если пользователь нашелся или был создан
		if (userItem != null) {
			Item purchase = Item.newChildItem(ItemTypeRegistry.getItemType(PURCHASE_ITEM), userItem);
			purchase.setValue(NUM_PARAM, cart.getValue("order_num"));
			purchase.setValue(DATE_PARAM, System.currentTimeMillis());
			purchase.setValue(QTY_AVAIL_PARAM, cart.getValue(QTY_AVAIL_PARAM));
			purchase.setValue(QTY_TOTAL_PARAM, cart.getValue(QTY_TOTAL_PARAM));
			purchase.setValue(SUM_PARAM, cart.getValue(sumParam));
			purchase.setValue("sum_discount", cart.getValue(sumDiscountParam));
			purchase.setValue("sum_saved", cart.getValue(sumSavedParam));

			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());

			for (Item bought : boughts) {
				Item boughtToSave = new Item(bought);
				boughtToSave.setContextPrimaryParentId(purchase.getId());
				boughtToSave.setOwner(userItem.getOwnerGroupId(), userItem.getOwnerUserId());
				executeCommandUnit(SaveItemDBUnit.get(boughtToSave).ignoreUser());
				if(boughtToSave.getByteValue("is_complex", (byte)0) == 1){
					List<Item> options = getSessionMapper().getItemsByName("pseudo_option", bought.getId());
					saveOptionsHisotry(options, boughtToSave);
				}
			}

			if(payments != null) {
				for (Map.Entry<String, String> entry : payments.entrySet()) {
					Item payment = Item.newChildItem(ItemTypeRegistry.getItemType("payment_stage"), purchase);
					payment.setValue("date",DateDataType.parseDate(entry.getKey(), DATE_FORMATTER));
					payment.setValueUI("sum", entry.getValue());
					executeCommandUnit(SaveItemDBUnit.get(payment).ignoreUser());
				}
			}
		}
		//
		//
		///////////////////////////////////////////////////////////////////////////////////////////////////

		// Подтвердить изменения
		commitCommandUnits();
	}

	protected void saveOptionsHisotry(List<Item> options, Item boughtToSave) throws Exception {
		commitCommandUnits();
		for(Item option : options){
			Item optionToSave = ItemUtils.newChildItem("pseudo_option", boughtToSave);
			for(String name : ItemTypeRegistry.getItemType("pseudo_option").getParameterNames()){
				optionToSave.setValue(name, option.getValue(name));
			}
			optionToSave.setOwner(userItem.getOwnerGroupId(), userItem.getOwnerUserId());
			executeCommandUnit(SaveItemDBUnit.get(optionToSave).ignoreUser());
		}
	}


	private String generateOrderNumber(Item counter) {
		int count = counter.getIntValue(COUNT_PARAM, 0) + 1;
		if (count > 99999)
			count = 1;
		String orderNumber = String.format("%05d", count);

		cart.setValue("order_num", orderNumber);
		getSessionMapper().saveTemporaryItem(cart);
		return orderNumber;
	}

	private void updateCounterItem(Item counter, String latestOrderNumber) throws Exception {
		// Сохранение нового значения счетчика, если все отправлено удачно
		int n = Integer.parseInt(latestOrderNumber.replace("\\D", ""));
		counter.setValue(COUNT_PARAM, n);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(counter).ignoreUser());
	}

	protected abstract boolean validate() throws Exception;

	protected boolean addExtraEmailBodyPart(boolean isCustomerEmail, Multipart mp) throws Exception {
		return true;
	}

	private void updateQtys() throws Exception {
		MultipleHttpPostForm form = getItemForm();
		// Обновление параметров
		loadCart();
		if (cart == null)
			return;
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		for (Item bought : boughts) {
			ItemInputValues vals = form.getReadOnlyItemValues(bought.getId());
			String qty = vals.getStringParam(QTY_PARAM);

			String dealerDate = vals.getStringParam("proposed_dealer_date");
			long d = DateDataType.parseDate(dealerDate, DATE_FORMATTER);
			bought.setValue("proposed_dealer_date", d);

			if (StringUtils.isNotBlank(qty)) {
				double quantity = -1;
				try {
					quantity = DoubleDataType.parse(qty);
				} catch (NumberFormatException e) { /**/ }
				if (quantity > 0) {
					Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
					setBoughtQtys(product, bought, quantity);
					getSessionMapper().saveTemporaryItem(bought);
				} else {
					getSessionMapper().removeItems(bought.getId());
				}
			}
			// Добавление в bought значений дополнительных инпутов
			for (String extraInputName : vals.getExtraInputNames()) {
				bought.setExtra(extraInputName, null);
				bought.setExtra(extraInputName, vals.getExtra(extraInputName));
			}
			if (vals.getExtraInputNames().size() > 0) {
				getSessionMapper().saveTemporaryItem(bought);
			}
		}
	}

	public ResultPE addComplexToCart() throws Exception {
		checkStrategy();
		ensureCart();
		String idStr = getVarSingleValue(PROD_PARAM);
		String boughtIdStr = getVarSingleValue("complectation_id");
		String name = getVarSingleValue("сomplectation_name");

		long boughtId = StringUtils.isBlank(boughtIdStr)? 0L : Long.parseLong(boughtIdStr);
		long prodId = Long.parseLong(idStr);
		Item product = ItemQuery.loadById(prodId);

		if(StringUtils.isBlank(name)){
			name = generateDefaultName(product.getStringValue(CODE_PARAM));
		}

		Item bought = boughtId == 0? getOrCreateBought(product, name) : getSessionMapper().getItemSingle(boughtId);

		bought.setValueUI("is_complex", "1");

		//remove old options
		List<Item> old = getSessionMapper().getItemsByName("pseudo_option", bought.getId());
		for (Item o : old) {
			getSessionMapper().removeItem(o.getId(), bought.getId(), "pseudo_option");
		}

		//create new options
		List<Object> input = getVarValues("option");
		List<Long> ids = input.stream().map(v -> Long.parseLong(v.toString())).collect(Collectors.toList());

		BigDecimal sum = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);

		for (long id : ids) {
			Item option = ItemQuery.loadById(id);
			sum = addOptionToBought(bought, sum, option);
		}

		bought.setValue(SUM_PARAM, sum);
		bought.setValue("сomplectation_name", name);
		getSessionMapper().saveTemporaryItem(bought);

		recalculateCart();

		List<Item> boughts = getSessionMapper().getItemsByParamValue(BOUGHT_ITEM, "is_complex", (byte)1);
		saveCartComplexCookie(boughts);

		ResultPE res = getResult("complect_ajax");
		res.setVariable(CODE_PARAM, product.getStringValue(CODE_PARAM));

		String message = StringUtils.isBlank(boughtIdStr)? "Создан список опций: " + name  : "Обнолен список опций: "+ name;
		res.setVariable("message", message);

		return res;
	}

	private String generateDefaultName(String code) throws Exception {
		List<Item> boughts = getSessionMapper().getItemsByParamValue(BOUGHT_ITEM, CODE_PARAM, code);
		boolean nameExists;
		int c = boughts.size() + 1;
		String name = "Комплектация " + c;
		do{
			nameExists = false;
			for(Item bought : boughts){
				String n = bought.getStringValue("сomplectation_name", "");
				if(n.equals(name)){
					nameExists = true;
					c++;
					name = "Комплектация " + c;
					break;
				}
			}
		}while (nameExists);
		return name;
	}


	private Item getOrCreateBought(Item product, String name) throws Exception {
		String code = product.getStringValue(CODE_PARAM);
		List<Item> boughts = getSessionMapper().getItemsByParamValue(BOUGHT_ITEM, CODE_PARAM, code);

		for (Item bought : boughts) {
			if (bought.getStringValue("сomplectation_name", "").equals(name)) {
				return bought;
			}
		}
		return createBought(product, 1d);
	}


	private Item createBought(Item product, double qty) throws Exception {
		Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
		bought.setValue(NAME_PARAM, product.getStringValue(NAME_PARAM));
		bought.setValue(CODE_PARAM, product.getStringValue(CODE_PARAM));
		setBoughtQtys(product, bought, qty);
		// Сохраняется bought
		getSessionMapper().saveTemporaryItem(bought);
		// Сохраняется девайс
		product.setContextPrimaryParentId(bought.getId());
		getSessionMapper().saveTemporaryItem(product, PRODUCT_ITEM);
		// Загрузка и сохранение родительского продукта (для продуктов, вложенных в другие продукты)
		Item parent = new ItemQuery(PRODUCT_ITEM).setChildId(product.getId(), false).loadFirstItem();
		if (parent != null) {
			parent.setContextPrimaryParentId(product.getId());
			getSessionMapper().saveTemporaryItem(parent);
		}
		return bought;
	}

	private Item createBought(long prodId, double qty) throws Exception {
		if (qty <= 0)
			return null;
		Item product = ItemQuery.loadById(prodId);
		if (product == null)
			return null;
		return createBought(product, qty);
	}

	private void addProduct(long prodId, double qty) throws Exception {
		checkStrategy();
		ensureCart();
		// Проверка, есть ли уже такой девайс в корзине (если есть, изменить количество)
		Item boughtProduct = getSessionMapper().getItemSingle(prodId);
		if (boughtProduct == null) {
			createBought(prodId, qty);
		} else {
			Item bought = getSessionMapper().getItem(boughtProduct.getContextParentId(), BOUGHT_ITEM);
			if (qty <= 0) {
				getSessionMapper().removeItems(bought.getId());
				return;
			}
			setBoughtQtys(boughtProduct, bought, qty);
			getSessionMapper().saveTemporaryItem(bought);
		}
	}

	/**
	 * Установить значения для всех параметров количества (в наличии, общего)
	 *
	 * @param product
	 * @param bought
	 * @param qtyWanted
	 */
	protected void setBoughtQtys(Item product, Item bought, double qtyWanted) {
		byte b = getInitiator().getRole("registered");
		String qp = b > -1 ? "qty_opt" : QTY_PARAM;
		double maxQuantity = product.getDoubleValue(qp, MAX_QTY);

		//fix 16.02.2021 Product quantity step added
		double step = product.getDoubleValue("step", product.getDoubleValue("min_qty", 1));

		//double sucks! use BigDecimal
		BigDecimal wanted = new BigDecimal(qtyWanted);
		BigDecimal stepD = new BigDecimal(step);
		BigDecimal min = new BigDecimal(product.getDoubleValue("min_qty", 0));

		wanted = wanted.setScale(6, RoundingMode.HALF_UP);
		stepD = stepD.setScale(6, RoundingMode.DOWN);
		min = min.setScale(6, RoundingMode.HALF_UP);

		BigDecimal res = wanted.subtract(min);
		res = res.divide(stepD, RoundingMode.CEILING);
		res = res.multiply(stepD);
		res = res.add(min);
		res = res.setScale(6, RoundingMode.HALF_UP);

		qtyWanted = res.doubleValue();

		double qtyAvail = 0;
		double qtyTotal = 0;
		double qty = 0;
		switch (strategy) {
			case deny_overbuy:
				qtyAvail = Math.min(qtyWanted, maxQuantity);
				qtyTotal = (qtyAvail < 0.000001) ? qtyWanted : qtyAvail;
				qty = qtyAvail;
				break;
			case extra_line_overbuy:
				qtyAvail = Math.min(qtyWanted, maxQuantity);
				qtyTotal = qtyWanted;
				qty = qtyTotal;
				break;
			case ignore_overbuy:
				qtyTotal = qtyWanted;
				qtyAvail = qtyWanted;
				qty = qtyWanted;
		}
		bought.setValue(QTY_AVAIL_PARAM, qtyAvail);
		bought.setValue(QTY_TOTAL_PARAM, qtyTotal);
		bought.setValue(QTY_PARAM, qty);
		bought.setValue(NOT_AVAILABLE, qtyAvail == qtyTotal ? (byte) 0 : (byte) 1);
	}


	/**
	 * Загрузить корзину из сеанса или создать новую корзину
	 *
	 * @throws Exception
	 */
	protected void ensureCart() throws Exception {
		if (cart == null) {
			cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
			if (cart == null) {
				cart = getSessionMapper().createSessionRootItem(CART_ITEM);
				getSessionMapper().saveTemporaryItem(cart);
			}
		}
		if (userItem == null){
			ensureUserItem();
		}
		refreshCart();
	}

	private  void ensureUserItem() throws Exception {
		// 1. Сначала нужно попробовать текущего пользователя (если он залогинен)
		userItem = new ItemQuery(USER_ITEM).setUser(getInitiator()).loadFirstItem();

		// 2. Потом надо попробовать загружить пользователя по введенному email
		if (userItem == null) {
			String email = getInitiator().getName();
			if (StringUtils.isNotBlank(email))
				userItem = new ItemQuery(USER_ITEM).addParameterCriteria(EMAIL_PARAM, email, "=", null, Compare.SOME).loadFirstItem();
		}
	}



	/**
	 * Если корзина уже была отправлена, создать ее заново
	 */
	private void refreshCart() {
		if (cart != null) {
			// Если корзина уже была отправлена, создать ее заново
			byte processed = cart.getByteValue(PROCESSED_PARAM, (byte) 0);
			if (processed == (byte) 1) {
				getSessionMapper().removeItems(cart.getId());
				cart = getSessionMapper().createSessionRootItem(CART_ITEM);
				getSessionMapper().saveTemporaryItem(cart);
			}
		}
	}

	/**
	 * Загрузить корзину, но не создавать в случае если корзина не найдена
	 */
	protected void loadCart() throws Exception {
		if (cart == null) {
			cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		}
		refreshCart();
	}

	/**
	 * Сохранить корзину в куки на всякий случай (если будет разрыв сеанса, корзину можно восстановить)
	 *
	 * @throws Exception
	 */
	protected void saveCartCookies() throws Exception {
		ensureCart();

		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		ArrayList<Item> simpleBoughts = new ArrayList<>();
		ArrayList<Item> complexBoughts = new ArrayList<>();

		for (Item b : boughts) {
			if (b.getByteValue("is_complex", (byte) 0) == 0) {
				simpleBoughts.add(b);
			} else {
				complexBoughts.add(b);
			}
		}

		saveCartCookie(simpleBoughts);
		saveCartComplexCookie(complexBoughts);
	}

	/**
	 * Сохраняет куки со списком для предзаказа
	 *
	 * @param boughts - list of bought items that CAN have options
	 */
	private void saveCartComplexCookie(Collection<Item> boughts) throws Exception {
		if (boughts.size() == 0) {
			setCookieVariable(COMPLEX_COOKIE, null);
			return;
		}
		StringBuilder cmplBulder = new StringBuilder();
		for (Item bought : boughts) {
			String encodedName = Base64.getEncoder()
					.encodeToString(
							bought
									.getStringValue("сomplectation_name", "")
									.getBytes()
					);
			String code = bought.getStringValue(CODE_PARAM);
			cmplBulder.append(encodedName).append('\'');
			cmplBulder.append(code);

			List<Item> options = getSessionMapper().getItemsByName("pseudo_option", bought.getId());
			for (Item o : options) {
				cmplBulder.append(',').append(o.getStringValue(CODE_PARAM));
			}
			cmplBulder.append('|');
		}
		String cookie = cmplBulder.toString();
		persistCookie(COMPLEX_COOKIE, cookie);
	}

	/**
	 * Сохраняет куки со списком для предзаказа
	 *
	 * @param boughts - list of bought items that CANNOT have options
	 */
	private void saveCartCookie(Collection<Item> boughts) throws Exception {
		if (boughts.size() == 0) {
			setCookieVariable(CART_COOKIE, null);
			return;
		}
		ArrayList<String> codeQtys = new ArrayList<>();
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double quantity = bought.getDoubleValue(QTY_TOTAL_PARAM);
			if (quantity < 0.001) continue;
			codeQtys.add(product.getStringValue(CODE_PARAM) + ":" + quantity);
		}

		String cookie = codeQtys.size() > 0? StringUtils.join(codeQtys, '/') : null;
		persistCookie(CART_COOKIE, cookie);
	}

	private void persistCookie(String varName, String cookie) throws Exception {
		switch(cookieStrategy){
			case COOKIE: setCookieVariable(varName, cookie); break;
			case USER_ITEM:
				ensureUserItem();
				if(StringUtils.isBlank(cookie)){
					userItem.clearValue(varName);
				}
				else{
					userItem.setValue(varName, cookie);
				}
				executeAndCommitCommandUnits(SaveItemDBUnit.get(userItem));
				break;
			case COOKIE_AND_USER_ITEM:
				if(getInitiator().inGroup(REGISTERED_GROUP)){
					ensureUserItem();
					if(StringUtils.isBlank(cookie)){
						userItem.clearValue(varName);
					}
					else{
						userItem.setValue(varName, cookie);
					}
					executeAndCommitCommandUnits(SaveItemDBUnit.get(userItem));
				}else{
					setCookieVariable(varName, cookie);
				}
		}
	}

	/**
	 * Восстановить товары с комплектациями из куки
	 *
	 * @throws Exception
	 */
	public void restoreComplexFromCookie() throws Exception {
		String cookie = getCartCookieString(COMPLEX_COOKIE);
		if (StringUtils.isBlank(cookie)) return;

		String[] complects = StringUtils.split(cookie, '|');
		ensureCart();

		for (String c : complects) {
			restoreSingleComplex(c);
		}
	}

	protected void restoreSingleComplex(String c) throws Exception {
		if (StringUtils.isBlank(c)) return;

		String[] codes = StringUtils.substringAfter(c, "'").split(",");
		Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, codes[0]);

		Item bought = createBought(product, 1d);
		bought.setValue("is_complex", (byte) 1);

		String name64 = StringUtils.substringBefore(c, "'");
		String name = new String(Base64.getDecoder().decode(name64.getBytes()));
		bought.setValue("сomplectation_name", name);
		getSessionMapper().saveTemporaryItem(bought);

		BigDecimal sum = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);

		if (codes.length > 1) {
			for (int i = 1; i < codes.length; i++) {
				Item option = ItemQuery.loadSingleItemByParamValue("abstract_product", CODE_PARAM, codes[i], Item.STATUS_NORMAL);
				sum = addOptionToBought(bought, sum, option);
			}
		}

		bought.setValue(SUM_PARAM, sum);

		getSessionMapper().saveTemporaryItem(bought);
	}

	private BigDecimal addOptionToBought(Item bought, BigDecimal sum, Item option) {
		Item pseudo = getSessionMapper().createSessionItem("pseudo_option", bought.getId());
		pseudo.setValue(NAME_PARAM, option.getValue(NAME_PARAM));
		pseudo.setValue(CODE_PARAM, option.getValue(CODE_PARAM));
		BigDecimal price = option.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);
		pseudo.setValue(PRICE_PARAM, price);
		getSessionMapper().saveTemporaryItem(pseudo);
		sum = sum.add(price);
		return sum;
	}

	/**
	 * Восстановить корзину из куки
	 *
	 * @return
	 * @throws Exception
	 */
	public ResultPE restoreFromCookie() throws Exception {
		checkStrategy();
		loadCart();
		if (cart != null) return null;

		restoreSimpleFromCookie();
		restoreComplexFromCookie();

		recalculateCart();
		return null;
	}

	private String getCartCookieString(String varName) throws Exception {
		switch (cookieStrategy){
			case COOKIE: return getVarSingleValue(varName);
			case USER_ITEM:
				ensureUserItem();
				return userItem.getStringValue(varName);
			case COOKIE_AND_USER_ITEM:
				if(getInitiator().inGroup(REGISTERED_GROUP)){
					ensureUserItem();
					return userItem.getStringValue(varName);
				}else{
					return getVarSingleValue(varName);
				}
				default: return null;
		}
	}

	private void restoreSimpleFromCookie() throws Exception {
		String cookie = getCartCookieString(CART_COOKIE);

		if (StringUtils.isBlank(cookie)) return;

		String[] codeQtys = StringUtils.split(cookie, '/');
		for (String codeQty : codeQtys) {
			String[] pair = StringUtils.split(codeQty, ':');
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, pair[0]);
			double qty = DoubleDataType.parse(pair[1]);
			if (product != null) {
				addProduct(product.getId(), qty);
			}
		}
	}

	/**
	 * Пересчитывает данные для одного enterprise_bought, когда в корзине произошли какие-то изменения
	 *
	 * @throws Exception
	 */
	protected boolean recalculateCart(String... priceParamName) throws Exception {
		checkStrategy();
		loadCart();
		if(userItem == null){
			ensureUserItem();
		}

		if (cart == null) return false;
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		BigDecimal totalSum = new BigDecimal(0); // полная сумма
		BigDecimal preOrderSum = new BigDecimal(0);
		int preOrderQantity = 0;
		double totalQuantity = 0;
		boolean result = true;

		final String PRICE = (priceParamName != null && priceParamName.length > 0) ? priceParamName[0] : PRICE_PARAM;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {

			if (bought.getByteValue("is_complex", (byte) 0) == 1) {
				BigDecimal sum = bought.getDecimalValue(SUM_PARAM, BigDecimal.ZERO);
				preOrderQantity++;
				preOrderSum = preOrderSum.add(sum);
			} else {

				Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
				double availableQty = bought.getDoubleValue(QTY_AVAIL_PARAM);
				double totalQty = bought.getDoubleValue(QTY_TOTAL_PARAM);
				if (totalQty <= 0) {
					getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
					result = false;
				} else {
					// Первоначальная сумма
					BigDecimal price = product.getDecimalValue(PRICE, new BigDecimal(0));
					BigDecimal productSum = price.multiply(new BigDecimal(availableQty));
					totalQuantity += totalQty;
					bought.setValue(PRICE_PARAM, price);
					bought.setValue(SUM_PARAM, productSum);
					totalSum = totalSum.add(productSum);
					// Сохранить bought
					getSessionMapper().saveTemporaryItem(bought);
				}
			}
		}

		BigDecimal discountQuotient = userItem.getDecimalValue("personal_discount", BigDecimal.ZERO).divide(new BigDecimal(
				100
		));

		BigDecimal sumSaved = totalSum.multiply(discountQuotient);
		BigDecimal sumDiscount = totalSum.subtract(sumSaved);

		cart.setValue(SUM_PARAM, totalSum);
		cart.setValue("sum_discount", sumDiscount);
		cart.setValue("sum_saved", sumSaved);
		cart.setValue(QTY_PARAM, totalQuantity);

		BigDecimal sumSavedPreOrder = preOrderSum.multiply(discountQuotient);
		BigDecimal sumPreOrderDiscount = preOrderSum.subtract(sumSavedPreOrder);

		cart.setValue("p_sum", preOrderSum);
		cart.setValue("p_sum_discount", sumPreOrderDiscount);
		cart.setValue("p_sum_saved", sumSavedPreOrder);

		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		return result && (totalQuantity > 0 || preOrderQantity > 0);
	}

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}
}
