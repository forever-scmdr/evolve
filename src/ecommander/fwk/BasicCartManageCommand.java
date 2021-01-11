package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.model.*;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Управление корзиной
 * Created by E on 2/3/2018.
 */
public abstract class BasicCartManageCommand extends Command {

	private static final String PRODUCT_ITEM = "product";
	private static final String CART_ITEM = "cart";
	private static final String BOUGHT_ITEM = "bought";
	private static final String PURCHASE_ITEM = "purchase";
	private static final String USER_ITEM = "user";
	private static final String PRICE_PARAM = "price";
	private static final String QTY_PARAM = "qty";
	private static final String SUM_PARAM = "sum";
	private static final String CODE_PARAM = "code";
	private static final String NAME_PARAM = "name";
	private static final String PROCESSED_PARAM = "processed";
	private static final String COUNTER_ITEM = "counter";
	private static final String COUNT_PARAM = "count";
	private static final String NUM_PARAM = "num";
	private static final String DATE_PARAM = "date";
	private static final String EMAIL_PARAM = "email";

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";
	public static final String REGISTERED_GROUP = "registered";
	private static Item common;



	private static final String CART_COOKIE = "cart_cookie";


	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


	private Item cart;
	private Item cartContacts;
	private Item delivery;
	private Item payment;

	/**
	 * Добавить товар в корзину
	 * @return
	 * @throws Exception
	 */
	public ResultPE addToCart() throws Exception {
		String code = getVarSingleValue(CODE_PARAM);
		String discount = getVarSingleValue("discount");
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM));
		} catch (Exception e) {/**/}
		addProduct(code, quantity, discount);
		recalculateCart();
		return getResult("ajax");
	}


	public ResultPE delete() throws Exception {
		// В этом случае ID не самого продукта, а объекта bought
		long boughtId = Long.parseLong(getVarSingleValue(BOUGHT_ITEM));
		getSessionMapper().removeItems(boughtId, BOUGHT_ITEM);
		recalculateCart();
		return getResult("cart");
	}


	public ResultPE recalculate() throws Exception {
		updateQtys();
		recalculateCart();
		return getResult("cart");
	}


	public ResultPE proceed() throws Exception {
		updateQtys();
		recalculateCart();
		return getResult("proceed");
	}


	public ResultPE customerForm() throws Exception {
		// Сохранение формы в сеансе (для унификации с персональным айтемом анкеты)
		cartContacts = getItemForm().getItemSingleTransient();

		try {
			delivery = ItemQuery.loadById(Long.parseLong(cartContacts.getStringValue("ship_type", "0")));
			payment = ItemQuery.loadById(Long.parseLong(cartContacts.getStringValue("pay_type", "0")));
		}catch (NumberFormatException e){}

		if(delivery != null){
			cartContacts.setValue("ship_type", delivery.getStringValue("option"));
		}
		if(payment != null){
			cartContacts.setValue("pay_type", payment.getStringValue("option"));
		}
		getSessionMapper().saveTemporaryItem(cartContacts);

		if (!validate()) {
			return getResult("validation_failed");
		}

		final String IN_PROGRESS = "in_progress";
		final String TRUE = "true";
		final String FALSE = "false";
		loadCart();
		// Была ли использована скидка
		//if(discountUsed()){
		BigDecimal simpleSum = cart.getDecimalValue("simple_sum");
		if(simpleSum != null || simpleSum.compareTo(BigDecimal.ZERO) != 0){
			recalculateCart();
			if(discountUsed()) {
				return getResult("discount_used");
			}
		}
		//}

		if (StringUtils.equalsIgnoreCase(cart.getStringExtra(IN_PROGRESS), TRUE)) {
			return getResult("confirm");
		}
		cart.setExtra(IN_PROGRESS, TRUE);
		getSessionMapper().saveTemporaryItem(cart);
		if ((Byte)cart.getValue(PROCESSED_PARAM, (byte)0) == (byte)1) {
			return getResult("confirm");
		}

		// Проверка, есть ли обычные заказы, заказы с количеством 0 и кастомные заказы


		// Загрузка и модификация счетчика
		Item counter = ItemUtils.ensureSingleRootItem(COUNTER_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		int count = counter.getIntValue(COUNT_PARAM, 0) + 1;
		if (count > 99999)
			count = 1;
		String orderNumber = String.format("%05d", count);
//			String date = counter.getStringValue(DATE_PARAM);
//			String newDate = DATE_FORMAT.format(new Date());
//			if (!newDate.equals(date))
//				count = 1;
//			String orderNumber = count + "-" + newDate;
		cart.setValue("order_num", orderNumber);
		getSessionMapper().saveTemporaryItem(cart);

		// Подготовка тела письма
		String regularTopic
				= "Заказ №" + orderNumber + " от " + DATE_FORMAT.format(new Date());
		Multipart regularMP = new MimeMultipart();
		MimeBodyPart regularTextPart = new MimeBodyPart();
		regularMP.addBodyPart(regularTextPart);
		LinkPE regularLink = LinkPE.newDirectLink("link", "order_email", false);
		regularLink.addStaticVariable("order_num", orderNumber + "");
		ExecutablePagePE regularTemplate = getExecutablePage(regularLink.serialize());
		final String customerEmail = getItemForm().getItemSingleTransient().getStringValue("email");
		final String shopEmail = getVarSingleValue("email");

		ByteArrayOutputStream regularBos = new ByteArrayOutputStream();
		PageController.newSimple().executePage(regularTemplate, regularBos);
		regularTextPart.setContent(regularBos.toString("UTF-8"), regularTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
				+ ";charset=UTF-8");

		// Отправка на ящик заказчика
		try {
	//		EmailUtils.sendGmailDefault(customerEmail, regularTopic, regularMP);
		} catch (Exception e) {
			ServerLogger.error("Unable to send email", e);
			cart.setExtra (IN_PROGRESS, null);
			getSessionMapper().saveTemporaryItem(cart);
			return getResult("email_send_failed").setVariable("message", "Не удалось отправить сообщение на ящик " + customerEmail);
		}
		// Отправка на ящик магазина
		try {
	//		EmailUtils.sendGmailDefault(shopEmail, regularTopic, regularMP);
		} catch (Exception e) {
			ServerLogger.error("Unable to send email", e);
			cart.setExtra(IN_PROGRESS, null);
			getSessionMapper().saveTemporaryItem(cart);
			return getResult("email_send_failed").setVariable("message", "Отправка заказа временно недоступна, попробуйте позже или звоните по телефону");
		}

		// Сохранение нового значения счетчика, если все отправлено удачно
		counter.setValue(COUNT_PARAM, count);
		//counter.setValue(DATE_PARAM, newDate);
		executeCommandUnit(SaveItemDBUnit.get(counter).ignoreUser());

		setCookieVariable("discount_used", new Date().getTime());

		///////////////////////////////////////////////////////////////////////////////////////////////////
		// Сохранить историю
		//

		// 1. Сначала нужно попробовать текущего пользователя (если он залогинен)
		Item userItem = new ItemQuery(USER_ITEM).setUser(getInitiator()).loadFirstItem();

		// 2. Потом надо попробовать загружить пользователя по введенному email
		if (userItem == null) {
			String email = cartContacts.getStringValue(EMAIL_PARAM);
			if (StringUtils.isNotBlank(email))
				userItem = new ItemQuery(USER_ITEM).addParameterCriteria(EMAIL_PARAM, email, "=", null, Compare.SOME).loadFirstItem();
		}

		// 3. Если пользователь не нашелся по email, надо создать нового пользователя
		//    сам пользователь не создается (логин-пароль), только айтем пользователя
		if (userItem == null) {
			if (StringUtils.isNotBlank(cartContacts.getStringValue(EMAIL_PARAM))) {
				Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG_ITEM, User.getDefaultUser(),
						UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
				cartContacts.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), catalog.getId());
				cartContacts.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), User.ANONYMOUS_ID);
				executeCommandUnit(SaveItemDBUnit.get(cartContacts).ignoreUser());
				userItem = cartContacts;
			}
		}

		// 4. Сохранить все покупки в истории, если пользователь нашелся или был создан
		if (userItem != null) {
			Item purchase = Item.newChildItem(ItemTypeRegistry.getItemType(PURCHASE_ITEM), userItem);
			purchase.setValue(NUM_PARAM, orderNumber + "");
			purchase.setValue(DATE_PARAM, System.currentTimeMillis());
			purchase.setValue(QTY_PARAM, cart.getValue(QTY_PARAM));
			purchase.setValue(SUM_PARAM, cart.getValue(SUM_PARAM));
			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());
			ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
			for (Item bought : boughts) {
				bought.setContextPrimaryParentId(purchase.getId());
				bought.setOwner(userItem.getOwnerGroupId(), userItem.getOwnerUserId());
				executeCommandUnit(SaveItemDBUnit.get(bought).ignoreUser());
			}
		}
		//
		//
		///////////////////////////////////////////////////////////////////////////////////////////////////

		// Подтвердить изменения
		commitCommandUnits();

		cart.setValue(PROCESSED_PARAM, (byte)1);
		cart.setExtra(IN_PROGRESS, null);
		long seed = System.nanoTime() % System.currentTimeMillis();
		String signature = seed + "920427307№" + cart.getStringValue("order_num") + 1 + "BYN" + cart.getDecimalValue("sum", BigDecimal.ZERO) + "secretKey";
		String digestedSignature = DigestUtils.sha1Hex(signature);
		cart.setExtra("signature", digestedSignature);
		cart.setExtra("seed", String.valueOf(seed));
		cart.setExtra("now", String.valueOf(new Date().getTime()/1000 + 3600 * 24));
		setCookieVariable(CART_COOKIE, null);
		getSessionMapper().saveTemporaryItem(cart);

		ResultPE res = getResult("confirm");
		if(delivery != null){
			res.addVariable("delivery", String.valueOf(delivery.getId()));
		}
		if(payment != null){
			res.addVariable("payment", String.valueOf(payment.getId()));
		}

		return res;
	}

	private boolean discountUsed() {
		String discountUsedCookie = getVarSingleValue("discount_used");
		if(StringUtils.isBlank(discountUsedCookie)){}
		else {
			return DATE_FORMAT.format(new Date()).equals(DATE_FORMAT);
		}
		return false;
	}

	protected abstract boolean validate() throws Exception;


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
			if (StringUtils.isNotBlank(qty)) {
				double quantity = -1;
				try {
					quantity = DoubleDataType.parse(qty);
				} catch (NumberFormatException e) { /**/ }
				if (quantity > 0) {
					Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
					double maxQuantity = product.getDoubleValue(QTY_PARAM, 1000000d);
					if (maxQuantity > 0)
						quantity = maxQuantity > quantity ? quantity : maxQuantity;
					bought.setValue(QTY_PARAM, quantity);
					getSessionMapper().saveTemporaryItem(bought);
				} else {
					getSessionMapper().removeItems(bought.getId());
				}
			}
		}
	}


	private void addProduct(String code, double qty) throws Exception {
		addProduct(code,qty,"");
	}
	private void addProduct(String code, double qty, String discount) throws Exception {
		ensureCart();
		refreshCart();
		// Проверка, есть ли уже такой девайс в корзине (если есть, изменить количество)
		Item boughtProduct = getSessionMapper().getSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
		if (boughtProduct == null) {
			if (qty <= 0)
				return;
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
			Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			double maxQuantity = product.getDoubleValue(QTY_PARAM, 1000000d);
			if (maxQuantity > 0)
				qty = maxQuantity > qty ? qty : maxQuantity;
			bought.setValue(QTY_PARAM, qty);
			bought.setValue(NAME_PARAM, product.getStringValue(NAME_PARAM));
			bought.setValue(CODE_PARAM, product.getStringValue(CODE_PARAM));
			bought.setValue("discount", discount);
			// Сохраняется bought
			getSessionMapper().saveTemporaryItem(bought);
			// Сохраняется девайс
			product.setContextPrimaryParentId(bought.getId());
			getSessionMapper().saveTemporaryItem(product, PRODUCT_ITEM);
		} else {
			Item bought = getSessionMapper().getItem(boughtProduct.getContextParentId(), BOUGHT_ITEM);
			bought.setValue("discount", discount);
			if (qty <= 0) {
				getSessionMapper().removeItems(bought.getId());
				return;
			}
			double maxQuantity = boughtProduct.getDoubleValue(QTY_PARAM, 1000000d);
			if (maxQuantity > 0)
				qty = maxQuantity > qty ? qty : maxQuantity;
			bought.setValue(QTY_PARAM, qty);
			getSessionMapper().saveTemporaryItem(bought);
		}
	}

	/**
	 * Загрузить корзину из сеанса или создать новую корзину
	 * @throws Exception
	 */
	private void ensureCart() throws Exception {
		if (cart == null) {
			cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
			if (cart == null) {
				cart = getSessionMapper().createSessionRootItem(CART_ITEM);
				getSessionMapper().saveTemporaryItem(cart);
			}
		}
		//refreshCart();
	}

	/**
	 * 	Если корзина уже была отправлена, создать ее заново
	 */
	private void refreshCart() {
		if (cart != null) {
			// Если корзина уже была отправлена, создать ее заново
			byte processed = cart.getByteValue(PROCESSED_PARAM, (byte)0);
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
	private void loadCart() throws Exception {
		if (cart == null) {
			cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		}
		//refreshCart();
	}

	/**
	 * Сохранить корзину в куки на всякий случай (если будет разрыв сеанса, корзину можно восстановить)
	 * @throws Exception
	 */
	private void saveCookie() throws Exception {
		ensureCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		ArrayList<String> codeQtys = new ArrayList<>();
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double quantity = bought.getDoubleValue(QTY_PARAM);
			codeQtys.add(product.getStringValue(CODE_PARAM) + ":" + quantity);
		}
		if (codeQtys.size() > 0) {
			String cookie = StringUtils.join(codeQtys, '/');
			setCookieVariable(CART_COOKIE, cookie);
		} else {
			setCookieVariable(CART_COOKIE, null);
		}
	}

	/**
	 * Восстановить корзину из куки
	 * @return
	 * @throws Exception
	 */
	public ResultPE restoreFromCookie() throws Exception {
		loadCart();
		if (cart != null)
			return null;
		String cookie = getVarSingleValue(CART_COOKIE);
		if (StringUtils.isBlank(cookie))
			return null;
		String[] codeQtys = StringUtils.split(cookie, '/');
		for (String codeQty : codeQtys) {
			String[] pair = StringUtils.split(codeQty, ':');
			double qty = DoubleDataType.parse(pair[1]);
			addProduct(pair[0], qty);
		}
		recalculateCart();
		return null;
	}



	/**
	 * Пересчитывает данные для одного enterprise_bought, когда в корзине произошли какие-то изменения
	 * @throws Exception
	 */
	private boolean recalculateCart() throws Exception {
		loadCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		BigDecimal sum = BigDecimal.ZERO; // полная сумма
		BigDecimal simpleSum = BigDecimal.ZERO;
		double zeroQuantity = 0;
		double regularQuantity = 0;
		boolean result = true;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double maxQuantity = product.getDoubleValue(QTY_PARAM, 1000000d);
			double quantity = bought.getDoubleValue(QTY_PARAM);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
				result = false;
			} else {
				// Первоначальная сумма
				//BigDecimal price = applyDiscount(bought);
				//product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);
				BigDecimal q = new BigDecimal(quantity);
				BigDecimal price = applyDiscount(bought);
				BigDecimal productSum = price.multiply(q);

				if (maxQuantity <= 0) {
					productSum = BigDecimal.ZERO;
					zeroQuantity += quantity;
				} else {
					regularQuantity += quantity;
				}
				bought.setValue(PRICE_PARAM, price);
				bought.setValue(SUM_PARAM, productSum);
				sum = sum.add(productSum);
				if(productSum.compareTo(BigDecimal.ZERO) == 1){
					BigDecimal p = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);
					BigDecimal oldPrice = product.getDecimalValue("price_old", BigDecimal.ZERO);
					oldPrice = oldPrice.compareTo(p) > 0? oldPrice : p;
					simpleSum = simpleSum.add(oldPrice.multiply(q));
				}
				// Сохранить bought
				getSessionMapper().saveTemporaryItem(bought);
			}
		}
		if(delivery != null){
			BigDecimal deliveryCost = delivery.getDecimalValue("price", BigDecimal.ZERO);
			sum = sum.add(deliveryCost);
			simpleSum = simpleSum.add(deliveryCost);
		}
		cart.setValue(SUM_PARAM, sum);
		cart.setValue(ItemNames.cart.SIMPLE_SUM, simpleSum);
		cart.setValue(QTY_PARAM, regularQuantity);
		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		saveCookie();
		return result && regularQuantity > 0;
	}

	private BigDecimal  applyDiscount(Item bought) throws Exception{
		common = common == null? ItemQuery.loadSingleItemByName(ItemNames.COMMON) : common;
		Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
		if(cartContacts == null || payment.getByteValue("cancel_discount", (byte)0) == 0){
			double dsc = 1 - common.getDoubleValue(ItemNames.common.DISCOUNT, 0);
			String useDiscount = bought.getStringValue("discount","");
			BigDecimal price = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);
			if(StringUtils.isBlank(useDiscount) || discountUsed()) return price;
			if(product.getStringValue(CODE_PARAM,"").equals(useDiscount)){
				dsc = 1 - product.getDoubleValue("discount",0);
			}
			return price.multiply(new BigDecimal(dsc));
		}else{
			BigDecimal oldPrice = product.getDecimalValue("price_old", BigDecimal.ZERO);
			BigDecimal price = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);
			return oldPrice.compareTo(price) > 0? oldPrice : price;
		}
	}

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}
}
