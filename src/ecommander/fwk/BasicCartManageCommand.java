package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.model.*;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
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

	protected static final String PRODUCT_ITEM = "abstract_product";
	protected static final String CART_ITEM = "cart";
	protected static final String BOUGHT_ITEM = "bought";
	protected static final String PURCHASE_ITEM = "purchase";
	protected static final String USER_ITEM = "user";
	protected static final String PRICE_PARAM = "price";
	protected static final String PRICE_OPT_PARAM = "price_opt";
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

	public static final String REGISTERED_CATALOG_ITEM = "registered_catalog";
	public static final String REGISTERED_GROUP = "registered";



	private static final String CART_COOKIE = "cart_cookie";
    private static final String STRATEGY_VAR = "strategy";


	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");


	protected static final Double MAX_QTY = 1000000000000d;


	protected Item cart;
	protected Strategy strategy = Strategy.deny_overbuy;


	protected void checkStrategy() {
	    strategy = Strategy.create(getVarSingleValueDefault(STRATEGY_VAR, Strategy.extra_line_overbuy.name()));
    }

	/**
	 * Добавить товар в корзину
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
	    checkStrategy();
		updateQtys();
		recalculateCart();
		return getResult("cart");
	}


	public ResultPE proceed() throws Exception {
		checkStrategy();
		updateQtys();
		recalculateCart();
		return getResult("proceed");
	}


	public ResultPE customerForm() throws Exception {
		// Сохранение формы в сеансе (для унификации с персональным айтемом анкеты)
        Item form = getItemForm().getItemSingleTransient();
        boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
        // TODO добавить логику выбора параметра цены (оптовая или розничная)
        //recalculateCart(isPhys ? PRICE_PARAM : PRICE_OPT_PARAM);
        recalculateCart();

		//getSessionMapper().saveTemporaryItem(form, "user");

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
		Item system = ItemUtils.ensureSingleRootAnonymousItem(SYSTEM_ITEM, getInitiator());
		Item counter = ItemUtils.ensureSingleAnonymousItem(COUNTER_ITEM, getInitiator(), system.getId());
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

		final String customerEmail = getItemForm().getItemSingleTransient().getStringValue("email");
		final String shopEmail = getVarSingleValue("email");

		// Письмо для продавца
		Multipart shopMultipart = new MimeMultipart();
		MimeBodyPart shopTextPart = new MimeBodyPart();
		shopMultipart.addBodyPart(shopTextPart);
		LinkPE shopEmailLink = LinkPE.newDirectLink("link", "shop_email", false);
		ExecutablePagePE shopTemplate;
		try {
			shopTemplate = getExecutablePage(shopEmailLink.serialize());
		} catch (PageNotFoundException e) {
			shopTemplate = null;
		}
		if (shopTemplate == null) {
			shopEmailLink = LinkPE.newDirectLink("link", "order_email", false);
			shopTemplate = getExecutablePage(shopEmailLink.serialize());
		}
		ByteArrayOutputStream shopEmailBytes = new ByteArrayOutputStream();
		PageController.newSimple().executePage(shopTemplate, shopEmailBytes);
		shopTextPart.setContent(shopEmailBytes.toString("UTF-8"), shopTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
				+ ";charset=UTF-8");
		addExtraEmailBodyPart(false, shopMultipart);

		// Письмо для покупателя
		Multipart customerMultipart = new MimeMultipart();
		MimeBodyPart customerTextPart = new MimeBodyPart();
		customerMultipart.addBodyPart(customerTextPart);
		try {
			LinkPE customerEmailLink = LinkPE.newDirectLink("link", "customer_email", false);
			ExecutablePagePE customerTemplate = getExecutablePage(customerEmailLink.serialize());
			ByteArrayOutputStream customerEmailBytes = new ByteArrayOutputStream();
			PageController.newSimple().executePage(customerTemplate, customerEmailBytes);
			customerTextPart.setContent(customerEmailBytes.toString("UTF-8"), customerTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
					+ ";charset=UTF-8");
			addExtraEmailBodyPart(true, customerMultipart);
		} catch (Exception e) {
			customerTextPart.setContent(shopEmailBytes.toString("UTF-8"), shopTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
					+ ";charset=UTF-8");
			addExtraEmailBodyPart(false, customerMultipart);
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
				executeCommandUnit(SaveItemDBUnit.get(form).ignoreUser().noTriggerExtra());
				userItem = form;
			}
		}

		// 4. Сохранить все покупки в истории, если пользователь нашелся или был создан
		if (userItem != null) {
			Item purchase = Item.newChildItem(ItemTypeRegistry.getItemType(PURCHASE_ITEM), userItem);
			purchase.setValue(NUM_PARAM, orderNumber + "");
			purchase.setValue(DATE_PARAM, System.currentTimeMillis());
			purchase.setValue(QTY_PARAM, cart.getValue(QTY_PARAM));
			purchase.setValue(QTY_AVAIL_PARAM, cart.getValue(QTY_AVAIL_PARAM));
			purchase.setValue(QTY_TOTAL_PARAM, cart.getValue(QTY_TOTAL_PARAM));
			purchase.setValue(SUM_PARAM, cart.getValue(SUM_PARAM));
			executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());
			ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
			for (Item bought : boughts) {
				//long bufParentId = bought.getContextParentId();
				//byte bufOwnerGroup = bought.getOwnerGroupId();
				//int bufOwnerUser = bought.getOwnerUserId();
				Item boughtToSave = new Item(bought);
				boughtToSave.setContextPrimaryParentId(purchase.getId());
				boughtToSave.setOwner(userItem.getOwnerGroupId(), userItem.getOwnerUserId());
				executeCommandUnit(SaveItemDBUnit.get(boughtToSave).ignoreUser());
				//bought.setContextPrimaryParentId(bufParentId);
				//bought.setOwner(bufOwnerGroup, bufOwnerUser);
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



    private Item createBought(long prodId, double qty) throws Exception {
        if (qty <= 0)
            return null;
        Item product = ItemQuery.loadById(prodId);
        if (product == null)
            return null;
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

//        qtyWanted = Math.max(product.getDoubleValue("min_qty", 0), qtyWanted);
//       	double minQ = product.getDoubleValue("min_qty", 0);
//       	double q = qtyWanted - minQ;
//        qtyWanted = minQ + Math.ceil(q / step) * step;

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
	 * Если корзина уже была отправлена, создать ее заново
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
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double quantity = bought.getDoubleValue(QTY_TOTAL_PARAM);
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
		String cookie = getVarSingleValue(CART_COOKIE);
		if (StringUtils.isBlank(cookie))
			return null;
		checkStrategy();
		loadCart();
		if (cart != null)
			return null;
		String[] codeQtys = StringUtils.split(cookie, '/');
		for (String codeQty : codeQtys) {
			String[] pair = StringUtils.split(codeQty, ':');
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, pair[0]);
			double qty = DoubleDataType.parse(pair[1]);
			if (product != null) {
				addProduct(product.getId(), qty);
			}
		}
		recalculateCart();
		return null;
	}

	/**
	 * Пересчитывает данные для одного enterprise_bought, когда в корзине произошли какие-то изменения
	 * @throws Exception
	 */
	protected boolean recalculateCart(String...priceParamName) throws Exception {
	    checkStrategy();
		loadCart();
		if(cart == null) return false;
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		BigDecimal totalSum = new BigDecimal(0); // полная сумма
		double totalQuantity = 0;
		boolean result = true;

		final String PRICE = (priceParamName != null && priceParamName.length > 0) ? priceParamName[0] : PRICE_PARAM;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {
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
		cart.setValue(SUM_PARAM, totalSum);
		cart.setValue(QTY_PARAM, totalQuantity);
		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		saveCookie();
		return result && totalQuantity > 0;
	}

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}
}
