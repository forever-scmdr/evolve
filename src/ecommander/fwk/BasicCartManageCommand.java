package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.model.*;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Управление корзиной
 * Created by E on 2/3/2018.
 */
public abstract class BasicCartManageCommand extends Command {

	protected static final String PRODUCT_ITEM = "product";
	protected static final String CART_ITEM = "cart";
	protected static final String BOUGHT_ITEM = "bought";
	protected static final String PURCHASE_ITEM = "purchase";
	protected static final String USER_ITEM = "user";
	protected static final String PRICE_PARAM = "price";
	protected static final String QTY_PARAM = "qty";
	protected static final String MIN_QTY_PARAM = "min_qty";
	protected static final String SUM_PARAM = "sum";
	protected static final String CODE_PARAM = "code";
	protected static final String NAME_PARAM = "name";
	protected static final String PROCESSED_PARAM = "processed";
	protected static final String COUNTER_ITEM = "counter";
	protected static final String COUNT_PARAM = "count";
	protected static final String NUM_PARAM = "num";
	protected static final String DATE_PARAM = "date";
	protected static final String EMAIL_PARAM = "email";

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";
	public static final String REGISTERED_GROUP = "registered";

	public static final int BIG_DECIMAL_SCALE_6 = 6;

	protected static final String CART_COOKIE = "cart_cookie";


	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


	protected static final Double MAX_QTY = 1000000000000d;


	protected Item cart;

	/**
	 * Добавить товар в корзину
	 * @return
	 * @throws Exception
	 */
	public ResultPE addToCart() throws Exception {
		String code = getVarSingleValue(CODE_PARAM);
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM));
		} catch (Exception e) {/**/}
		addProduct(code, quantity);
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
		Item form = getItemForm().getTransientSingleItem();
		getSessionMapper().saveTemporaryItem(form, "user");

		if (!validate()) {
			return getResult("validation_failed");
		}

		final String IN_PROGRESS = "in_progress";
		final String TRUE = "true";
		final String FALSE = "false";
		loadCart();
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

		final String customerEmail = getItemForm().getTransientSingleItem().getStringValue("email");
		final String shopEmail = getVarSingleValue("email");

		// Письмо для покупателя
		Multipart customerMultipart = new MimeMultipart();
		MimeBodyPart customerTextPart = new MimeBodyPart();
		customerMultipart.addBodyPart(customerTextPart);
		LinkPE customerEmailLink = LinkPE.newDirectLink("link", "customer_email", false);
		customerEmailLink.addStaticVariable("order_num", orderNumber + "");
		ExecutablePagePE customerTemplate = getExecutablePage(customerEmailLink.serialize());
		ByteArrayOutputStream customerEmailBytes = new ByteArrayOutputStream();
		PageController.newSimple().executePage(customerTemplate, customerEmailBytes);
		customerTextPart.setContent(customerEmailBytes.toString("UTF-8"), customerTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
				+ ";charset=UTF-8");

		// Письмо для продавца
		Multipart shopMultipart = new MimeMultipart();
		MimeBodyPart shopTextPart = new MimeBodyPart();
		shopMultipart.addBodyPart(shopTextPart);
		try {
			LinkPE shopEmailLink = LinkPE.newDirectLink("link", "shop_email", false);
			shopEmailLink.addStaticVariable("order_num", orderNumber + "");
			ExecutablePagePE shopTemplate = getExecutablePage(shopEmailLink.serialize());
			ByteArrayOutputStream shopEmailBytes = new ByteArrayOutputStream();
			PageController.newSimple().executePage(shopTemplate, shopEmailBytes);
			shopTextPart.setContent(shopEmailBytes.toString("UTF-8"), shopTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
					+ ";charset=UTF-8");
		} catch (Exception e) {
			shopTextPart.setContent(customerEmailBytes.toString("UTF-8"), customerTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
					+ ";charset=UTF-8");
		}

		// Отправка на ящик заказчика
		try {
			if (StringUtils.isNotBlank(customerEmail))
				EmailUtils.sendGmailDefault(customerEmail, regularTopic, customerMultipart);
		} catch (Exception e) {
			ServerLogger.error("Unable to send email", e);
			cart.setExtra (IN_PROGRESS, null);
			getSessionMapper().saveTemporaryItem(cart);
			return getResult("email_send_failed").setVariable("message", "Не удалось отправить сообщение на указанный ящик");
		}
		// Отправка на ящик магазина
		try {
			EmailUtils.sendGmailDefault(shopEmail, regularTopic, shopMultipart);
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

		///////////////////////////////////////////////////////////////////////////////////////////////////
		// Сохранить историю
		//

		// 1. Сначала нужно попробовать текущего пользователя (если он залогинен)
		Item userItem = new ItemQuery(USER_ITEM).setUser(getInitiator()).loadFirstItem();

		// 2. Потом надо попробовать загружить пользователя по введенному email
		if (userItem == null) {
			String email = form.getStringValue(EMAIL_PARAM);
			if (StringUtils.isNotBlank(email))
				userItem = new ItemQuery(USER_ITEM).addParameterCriteria(EMAIL_PARAM, email, "=", null, Compare.SOME).loadFirstItem();
		}

		// 3. Если пользователь не нашелся по email, надо создать нового пользователя
		//    сам пользователь не создается (логин-пароль), только айтем пользователя
		if (userItem == null) {
			if (StringUtils.isNotBlank(form.getStringValue(EMAIL_PARAM))) {
				Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG_ITEM, User.getDefaultUser(),
						UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
				form.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), catalog.getId());
				form.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), User.ANONYMOUS_ID);
				executeCommandUnit(SaveItemDBUnit.get(form).ignoreUser());
				userItem = form;
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
		setCookieVariable(CART_COOKIE, null);
		getSessionMapper().saveTemporaryItem(cart);
		return getResult("confirm");
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
					double maxQuantity = product.getDecimalValue(QTY_PARAM, new BigDecimal(MAX_QTY)).doubleValue();
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


	protected abstract void extraLoading(Item product) throws Exception;


	private void addProduct(String code, double qty) throws Exception {
		ensureCart();
		// Проверка, есть ли уже такой девайс в корзине (если есть, изменить количество)
		Item boughtProduct = getSessionMapper().getSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
		BigDecimal decimalQty = new BigDecimal(qty).setScale(BIG_DECIMAL_SCALE_6, BigDecimal.ROUND_HALF_EVEN);
		if (boughtProduct == null) {
			if (qty <= 0)
				return;
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
			Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			BigDecimal maxQuantity = product.getDecimalValue(QTY_PARAM, new BigDecimal(MAX_QTY));
			BigDecimal minQuantity = product.getDecimalValue(MIN_QTY_PARAM, new BigDecimal(1));

			if (minQuantity.intValue() == 0) minQuantity = new BigDecimal(1);
			BigDecimal qtyMinQtyFraction = decimalQty.divide(minQuantity, BigDecimal.ROUND_HALF_EVEN);
			if (!isIntegerValue(qtyMinQtyFraction))
				decimalQty = minQuantity.multiply(qtyMinQtyFraction.setScale(0, BigDecimal.ROUND_CEILING));
			if (maxQuantity.compareTo(new BigDecimal(0)) > 0)
				decimalQty = maxQuantity.compareTo(decimalQty) > 0 ? decimalQty : maxQuantity;
			bought.setValue(QTY_PARAM, decimalQty.doubleValue());
			bought.setValue(NAME_PARAM, product.getStringValue(NAME_PARAM));
			bought.setValue(CODE_PARAM, product.getStringValue(CODE_PARAM));
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
			extraLoading(product);
		} else {
			Item bought = getSessionMapper().getItem(boughtProduct.getContextParentId(), BOUGHT_ITEM);
			if (qty <= 0) {
				getSessionMapper().removeItems(bought.getId());
				return;
			}
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			BigDecimal minQuantity = product.getDecimalValue(MIN_QTY_PARAM, new BigDecimal(1));
			if (minQuantity.intValue() == 0) minQuantity = new BigDecimal(1);
			BigDecimal qtyMinQtyFraction = decimalQty.divide(minQuantity, BigDecimal.ROUND_HALF_EVEN);
			if (!isIntegerValue(qtyMinQtyFraction))
				decimalQty = decimalQty.setScale(0, RoundingMode.CEILING);
			BigDecimal maxQuantity = product.getDecimalValue(QTY_PARAM, new BigDecimal(MAX_QTY));
			if (maxQuantity.compareTo(new BigDecimal(0)) > 0)
				decimalQty = maxQuantity.compareTo(decimalQty) > 0 ? decimalQty : maxQuantity;
			bought.setValue(QTY_PARAM, decimalQty.doubleValue());
			getSessionMapper().saveTemporaryItem(bought);
		}
	}



	/**
	 * Загрузить корзину из сеанса или создать новую корзину
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
		refreshCart();
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
	protected void loadCart() throws Exception {
		if (cart == null) {
			cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		}
		refreshCart();
	}

	/**
	 * Сохранить корзину в куки на всякий случай (если будет разрыв сеанса, корзину можно восстановить)
	 * @throws Exception
	 */
	protected void saveCookie() throws Exception {
		ensureCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		ArrayList<String> codeQtys = new ArrayList<>();
		for (Item bought : boughts) {
			Object extra = bought.getExtra("map");
			if(bought.getExtra("map") != null) continue;
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
	protected boolean recalculateCart() throws Exception {
		loadCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		BigDecimal sum = new BigDecimal(0); // полная сумма
		double zeroQuantity = 0;
		double regularQuantity = 0;
		boolean result = true;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double maxQuantity = product.getDecimalValue(QTY_PARAM, new BigDecimal(MAX_QTY)).doubleValue();
			double quantity = bought.getDoubleValue(QTY_PARAM);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
				result = false;
			} else {
				// Первоначальная сумма
				BigDecimal price = product.getDecimalValue(PRICE_PARAM, new BigDecimal(0));
				BigDecimal productSum = price.multiply(new BigDecimal(quantity));
				if (maxQuantity <= 0) {
					productSum = new BigDecimal(0);
					zeroQuantity += quantity;
				} else {
					regularQuantity += quantity;
				}
				bought.setValue(PRICE_PARAM, price);
				bought.setValue(SUM_PARAM, productSum);
				sum = sum.add(productSum);
				// Сохранить bought
				getSessionMapper().saveTemporaryItem(bought);
			}
		}
		cart.setValue(SUM_PARAM, sum);
		cart.setValue(QTY_PARAM, regularQuantity);
		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		saveCookie();
		return result && regularQuantity > 0;
	}

	public static boolean isIntegerValue(BigDecimal bd) {
		return bd.stripTrailingZeros().scale() <= 0;
	}

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}
}
