package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.EmailUtils;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.PagePE;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra.CartManageCommand;
import extra._generated.ItemNames;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BelchipCartCommand extends CartManageCommand implements CartConstants {

	private float minimalOrderSum = (float) 0;
	private String lessThenMinimalOrderMessage = "";

	/**
	 * Проверка, я вляется ли текущий пользователь зарегистрированным
	 * @return
	 */
	private boolean isRegistered() {
		for (User.Group group : getInitiator().getGroups()) {
			if (StringUtils.equalsIgnoreCase(REGISTERED_GROUP, group.name))
				return true;
		}
		return false;
	}

	/**
	 * Создает корзину и восстанавливает ее из сеанса, куки или БД
	 * Вызывается страницами сайта
	 * Вненший метод
	 * @return
	 * @throws Exception
	 */
	public ResultPE createOrLoadCart() throws Exception {
		// если несколько корзин
		try {
			cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		} catch (Exception e) {
			getSessionMapper().removeItems(CART_ITEM);
			cart = null;
		}
		if (cart == null) {
			restoreFromCookie();
		}
		return null;
	}

	/**
	 * Восстанавливает корзину из куки
	 * В куки хранится либо ID айтема заказа пользователя, если он зарегистрирован, либо
	 * сам заказ, если пользователь не зарегистрирован
	 * @return
	 * @throws Exception
	 */
	public ResultPE restoreFromCookie() throws Exception {
		cart = getSessionMapper().createSessionRootItem(ItemNames.CART);
		getSessionMapper().saveTemporaryItem(cart);

		if (isRegistered()) {
			restoreFromUserInfo();
		} else {
			restoreFromString(getVarSingleValue(CART_COOKIE));
		}
		recalculateCart();
		addCustomBoughts();
		return null;
	}

	/**
	 * Восстанавливает корзину из сохраненной ранее в БД
	 * @throws Exception
	 */
	private void restoreFromUserInfo() throws Exception {
		Item userInfo = loadUserInfo();
		if (userInfo == null)
			return;
		addBoughtsFromHistory(userInfo);
	}

	/**
	 * Добавить в корзину заказы из истории заказов из сохраенной
	 * временной корзины
	 * @param userInfo
	 * @throws Exception
	 */
	private void addBoughtsFromHistory(Item userInfo) throws Exception {
		if (userInfo == null)
			return;
		Item order = new ItemQuery(ItemNames.PURCHASE).setParentId(userInfo.getId(), false, ASSOC_GENERAL).loadFirstItem();
		if (order == null)
			return;
		List<Item> boughts = new ItemQuery(ItemNames.BOUGHT).setParentId(order.getId(), false).loadItems();
		for (Item bought : boughts) {
			Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product_.CODE, bought.getStringValue(ItemNames.bought_.CODE));
			if (product == null)
				continue;
			addProduct(product, bought.getDoubleValue(ItemNames.bought_.QTY_TOTAL));
		}
	}

	/**
	 * Загрузить айтем регистрации пользователя по текущему залогиненному пользователю
	 * @return
	 * @throws Exception
	 */
	private Item loadUserInfo() throws Exception {
		return loadUserInfo(getInitiator());
	}

	/**
	 * Загрузить айтем регистрации пользователя
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private Item loadUserInfo(User user) throws Exception {
		List<Item> register = new ItemQuery(ItemNames.USER).setUser(user).loadItems();
		if (!register.isEmpty())
			return register.get(0);
		return null;
	}

	/**
	 * Добавляет к корзине айтемы для товаров, которых нет на сайте и чел хочет чтобы они появились
	 * (custom_bought). Добавляется 10 айтемов, показывается не сайте 5
	 * @throws Exception
	 */
	private void addCustomBoughts() throws Exception {
		if (getSessionMapper().getItemsByName(CUSTOM_BOUGHT_ITEM, cart.getId()).size() > 0)
			return;
		// Добавление айтемов для персонального заказа
		for (int i = 0; i < CUSTOM_BOUGHT_COUNT; i++) {
			Item custom = getSessionMapper().createSessionItem(CUSTOM_BOUGHT_ITEM, cart.getId());
			custom.setValue(POSITTION_PARAM, i);
			getSessionMapper().saveTemporaryItem(custom);
		}
		getSessionMapper().saveTemporaryItem(cart);
	}

	/**
	 * Восстанавливает корзину из строки вида product_id_1:qty1;product_id_2:qty2;....
	 * (обычно куки)
	 * @param cookie
	 * @throws Exception
	 */
	private void restoreFromString(String cookie) throws Exception {
		if (StringUtils.isBlank(cookie) || cookie.indexOf(':') == -1)
			return;
		String[] idQtys = StringUtils.split(cookie, ";/");
		for (String idQty : idQtys) {
			String[] pair = StringUtils.split(idQty, ':');
			Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product_.CODE, pair[0]);
			if (product == null)
				continue;
			double qty = DoubleDataType.parse(pair[1].replace(',', '.'));
			addProduct(product, qty);
		}
		return;
	}


	/**
	 * Просто добавляет товар в корзину. Предполагается что корзина существует
	 * Внутренний метод
	 * @param product - товар
	 * @param qty - количество товара
	 */
	private void addProduct(Item product, double qty) throws Exception {
		if (product == null)
			return;
		String code = product.getStringValue(ItemNames.product_.CODE);
		ArrayList<Item> boughts = getSessionMapper().getItemsByParamValue(ItemNames.BOUGHT, ItemNames.bought_.CODE, code);
		switch (boughts.size()) {
			case 0: {
				if (qty <= 0)
					return;
				Item section = new ItemQuery(ItemNames.SECTION).setChildId(product.getId(), false).loadFirstItem();
				Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
				qty = round(qty, product.getDoubleValue(MIN_QTY_PARAM, (double) 1));
				bought.setValue(ItemNames.bought_.QTY_TOTAL, qty);
				bought.setValue(IConst.LIMIT_1_PARAM, section.getValue(IConst.LIMIT_1_PARAM));
				bought.setValue(IConst.LIMIT_2_PARAM, section.getValue(IConst.LIMIT_2_PARAM));
				bought.setValue(IConst.DISCOUNT_1_PARAM, section.getValue(IConst.DISCOUNT_1_PARAM));
				bought.setValue(IConst.DISCOUNT_2_PARAM, section.getValue(IConst.DISCOUNT_2_PARAM));
				bought.setValue(IConst.TYPE_PARAM, product.getItemType().getCaption());
				bought.setValue(ItemNames.bought_.CODE, code);
				getSessionMapper().saveTemporaryItem(bought);
				product.setContextPrimaryParentId(bought.getId());
				getSessionMapper().saveTemporaryItem(product, PRODUCT_ITEM);
				break;
			}
			case 1: {
				Item bought = boughts.get(0);
				qty = round(qty, product.getDoubleValue(MIN_QTY_PARAM, (double) 1));
				bought.setValue(ItemNames.bought_.QTY_TOTAL, qty);
				getSessionMapper().saveTemporaryItem(bought);
				break;
			}
		}
	}

	/**
	 * Внутренний метод пересчета корзины. Вызывается из других методов когда нужно пересчитать сумму корзины
	 * @return
	 * @throws Exception
	 */
	protected boolean recalculateCart() throws Exception {
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		double sum = 0; // полная сумма
		double discountSum = 0; // сумма, с которой предоставляется скидка
		double regularQuantity = 0;
		double zeroQuantity = 0;
		double customQuantity = 0;
		double total = 0;
		boolean result = true;

		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double maxQuantity = product.getDoubleValue(QTY_PARAM, 0d);
			double totalQuantity = bought.getDoubleValue(ItemNames.bought_.QTY_TOTAL, 0d);
			totalQuantity = round(totalQuantity, product.getDoubleValue(MIN_QTY_PARAM, (double) 1));
			double quantity = (totalQuantity > maxQuantity) ? maxQuantity : totalQuantity;
			double delta = totalQuantity - maxQuantity;
			delta = (delta < 0) ? 0d : delta;
			zeroQuantity += delta;
			total += totalQuantity;
			if (product.getByteValue("is_service") == 1) {
				maxQuantity = totalQuantity;
				quantity = totalQuantity;
				delta = 0d;
				zeroQuantity = 0d;
			}
			bought.setValue(ItemNames.bought_.QTY_ZERO, delta);
			if (quantity >= 0) {
				double productSum = Math.round(product.getDoubleValue(PRICE_PARAM, 0d) * quantity * 100) / 100d;
				regularQuantity += quantity;
				bought.setValue(ItemNames.bought_.QTY, quantity);
				bought.setValue(SUM_PARAM, productSum);
				sum += productSum;
				if (product.getStringValue(ItemNames.product_.SPECIAL_PRICE, FALSE_VALUE).equals(FALSE_VALUE))
					discountSum += productSum;

				getSessionMapper().saveTemporaryItem(bought);
			}
			if (totalQuantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
				result = false;
			}
		}
		// Персональные заказы

		ArrayList<Item> customBoughts = getSessionMapper().getItemsByName(CUSTOM_BOUGHT_ITEM, cart.getId());
		for (Item bought : customBoughts) {
			if (StringUtils.equals("true", bought.getStringValue(NONEMPTY_PARAM))) {
				customQuantity = customQuantity + 1;
			}
		}
		double simpleSum = sum;
		int discount = 0;
		double quotient = 0;
		// Скидка с суммы (в случае если заказано более 1 товара)
		if (regularQuantity > 1) {
			if (simpleSum >= SUM_1 && simpleSum < SUM_2) {
				discount = DISCOUNT_1;
				quotient = (double) (DISCOUNT_1) / (double) 100;
			} else if (simpleSum >= SUM_2) {
				discount = DISCOUNT_2;
				quotient = (double) (DISCOUNT_2) / (double) 100;
			}
			sum = sum - discountSum * quotient;
		}
		// Округление суммы
		sum = Math.round(sum * 100) / 100d;
		cart.setValue(ItemNames.cart_.SIMPLE_SUM, simpleSum);
		cart.setValue(SUM_PARAM, sum);
		cart.setValue(QUANTITY_PARAM, regularQuantity);
		cart.setValue(ZERO_QUANTITY_PARAM, zeroQuantity);
		cart.setValue(CUSTOM_QUANTITY_PARAM, customQuantity);
		cart.setValue(ItemNames.cart_.SUM_DISCOUNT, discount);
		cart.setValue(ItemNames.cart_.MARGIN, simpleSum - sum);
		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		return result && total > 0;
	}


	/**
	 * Округляет количество товара до числа, кратного минимальному заказу
	 *
	 * @throws Exception
	 */
	private double round(double qty, double min_qty) {
		if (qty > 0 && qty < min_qty)
			return min_qty;
		double quotient = Math.ceil(qty / min_qty);
		return min_qty * quotient;
	}


	/**
	 * Проверяет, набрана ли минималньая сумма заказа Существуют разные мин. суммы для физ. лиц юр. лиц и физ. отправка почтой. При заказе юр.
	 * лицами учитывается стоимость товаров, отсутствующих на складе.
	 *
	 * @throws Exception
	 */
	private boolean checkMinSum(boolean isJur, boolean needPost) throws Exception {
		double min = 0d;
		Item orderVars = ItemQuery.loadSingleItemByName(ItemNames.ORDER_EMAILS);
		if (!isJur) {
			if (needPost) {
				min = Double.parseDouble(getVarSingleValue("min_phys_post_sum"));
				min = (orderVars == null) ? min : orderVars.getDoubleValue(ItemNames.order_emails_.MIN_POST, min);
				this.minimalOrderSum = (float) min;
				lessThenMinimalOrderMessage = POST_LTM;
			} else {
				min = Double.parseDouble(getVarSingleValue("min_phys_sum"));
				min = (orderVars == null) ? min : orderVars.getDoubleValue(ItemNames.order_emails_.MIN_PHYS, min);
				this.minimalOrderSum = (float) min;
				lessThenMinimalOrderMessage = PHYS_LTM;
			}
			return cart.getDoubleValue(ItemNames.cart_.SUM, 0d) >= min;
		} else {
			min = Double.parseDouble(getVarSingleValue("min_jur_sum"));
			min = (orderVars == null) ? min : orderVars.getDoubleValue(ItemNames.order_emails_.MIN_JUR, min);
			ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
			double sum = 0d;
			for (Item bought : boughts) {
				double q = bought.getDoubleValue(ItemNames.bought_.QTY_TOTAL, 0d);
				Item product = getSessionMapper().getSingleItemByName(ItemNames.PRODUCT, bought.getId());
				double price = product.getDoubleValue(ItemNames.product_.PRICE, 0d);
				sum += q * price;
			}
			this.minimalOrderSum = (float) min;
			lessThenMinimalOrderMessage = JUR_LTM;
			return sum >= min;
		}
	}

	/**
	 * Отправить письмо с заданным шаблоном по заданным адресам
	 * Обработка ошибок переложена на вызывающий метод (здесь не производится)
	 * @param emails
	 * @param topic
	 * @param templateLink
	 * @throws Exception
	 */
	private void sendEmail(List<String> emails, String topic, LinkPE templateLink) throws Exception {
		ExecutablePagePE page = getExecutablePage(templateLink.serialize());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PageController.newSimple().executePage(page, bos);
		Multipart letter = new MimeMultipart();
		MimeBodyPart textPart = new MimeBodyPart();
		letter.addBodyPart(textPart);
		textPart.setContent(bos.toString("UTF-8"), page.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER) + ";charset=UTF-8");
		String emailsCsv = String.join(",", emails);
		EmailUtils.sendGmailDefault(emailsCsv, topic, letter);
	}

	/**
	 * Подготовить результат ошибки отправки письма
	 * @param e
	 * @return
	 * @throws EcommanderException
	 */
	private ResultPE getEmailSendingErrorResult(Exception e) throws EcommanderException {
		ServerLogger.error("Unable to send email", e);
		cart.setExtra(MESSAGE_PARAM, "Отправка письма невозможна. Попробуйте позже или проверьте введенный email.");
		cart.setExtra(IN_PROGRESS, "false");
		getSessionMapper().saveTemporaryItem(cart);
		return getResult("not_set");
	}

	/**
	 * Подготовить результат ошибки незаданных обязательных полей
	 * @param notSetParams
	 * @return
	 * @throws Exception
	 */
	private ResultPE getMandatoryNotSetResult(List<String> notSetParams) throws Exception {
		ResultPE result = getResult("not_set");
		for (String param : notSetParams) {
			result.addVariable("not_set", param);
		}
		cart.setExtra(MESSAGE_PARAM, "Заполните, пожалуйста, обязательные поля");
		cart.setExtra(IN_PROGRESS, "false");
		getSessionMapper().saveTemporaryItem(cart);
		return result;
	}

	/**
	 * Подготовить результат ошибки недостаточной суммы корзины
	 * @return
	 * @throws Exception
	 */
	private ResultPE getSumTooSmallResult() throws Exception {
		cart.setExtra(MESSAGE_PARAM, String.format(lessThenMinimalOrderMessage, minimalOrderSum));
		cart.setExtra(IN_PROGRESS, "false");
		getSessionMapper().saveTemporaryItem(cart);
		return getResult("not_set");
	}

	/**
	 * Сгенерировать файл со штрихкодами для всех заказанных товаров
	 * @param boughts
	 * @throws Exception
	 */
	private void generateBarcodes(ArrayList<Item> boughts) throws Exception {
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			String filePath = AppContext.getFilesDirPath(false) + BARCODE_DIR;
			String fileName = product.getStringValue(ItemNames.product_.CODE) + PNG_EXT;
			File bcFile = new File(filePath + fileName);
			if (!bcFile.exists()) {
				try {
					new File(filePath).mkdirs();
					String bc12 = StringUtils.substring(product.getStringValue(BARCODE_PARAM, "none"), 0, -1);
					Barcode barcode = BarcodeFactory.createEAN13(bc12);
					barcode.setLabel(bc12);
					barcode.setResolution(36);
					BarcodeImageHandler.savePNG(barcode, bcFile);
				} catch (Exception e) {
					ServerLogger.debug("Cannot generate barcode for " + product.getStringValue(BARCODE_PARAM, "none"), e);
				}
			}
		}
	}
/*
	private void addContactsToOrder(Item purchase) throws Exception {
		Item sessionUser = getSessionMapper().getSingleRootItemByName(ItemNames.USER);
		Item userInfo = loadUserInfo();
		purchase.clearValue(ItemNames.purchase_.CLIENT_ID);
		purchase.clearValue(ItemNames.purchase_.LOGIN);
		if (userInfo != null) {
			purchase.setValue(ItemNames.purchase_.CLIENT_ID, userInfo.getId());
			purchase.setValue(ItemNames.purchase_.LOGIN, getInitiator().getName());
		}
		boolean isPhys = StringUtils.equalsIgnoreCase(sessionUser.getTypeName(), ItemNames.USER_PHYS);
		purchase.setValueUI(ItemNames.purchase_.PHYS, isPhys ? "1" : "0");
		String phone = (isPhys) ? sessionUser.getStringValue(ItemNames.user_phys_.PHONE)
				: sessionUser.getStringValue(ItemNames.user_jur_.CONTACT_PHONE);
		String name = (isPhys) ? sessionUser.getStringValue(ItemNames.user_phys_.NAME)
				: sessionUser.getStringValue(ItemNames.user_jur_.CONTACT_NAME) + ". Организация: "
				+ sessionUser.getStringValue(ItemNames.user_jur_.ORGANIZATION);
		purchase.setValueUI(ItemNames.purchase_.PHONE, phone);
		purchase.setValueUI(ItemNames.purchase_.CLIENT_NAME, name);
		purchase.setValueUI(ItemNames.purchase_.PAYMENT, sessionUser.getStringValue(ItemNames.user_.PAYMENT, ""));
		purchase.setValueUI("if_absent", sessionUser.getStringValue("if_absent", ""));
		String delivery;
		if (isPhys) {
			delivery = sessionUser.getStringValue(ItemNames.cart_contacts.GET_ORDER_FROM);
		} else {
			delivery = StringUtils.isNotBlank(sessionUser.getStringValue(ItemNames.cart_contacts.JUR_POST_INDEX, "")) ? "доставка почтой"
					: "самовывоз";
		}
		purchase.setValueUI(ItemNames.order.ADDRESS, sessionUser.getStringValue(ItemNames.cart_contacts.POST_ADDRESS, ""));
		purchase.setValueUI(ItemNames.order.CITY, sessionUser.getStringValue(ItemNames.cart_contacts.POST_CITY, ""));
		purchase.setValueUI(ItemNames.order.REGION, sessionUser.getStringValue(ItemNames.cart_contacts.POST_REGION, ""));
		purchase.setValueUI(ItemNames.order.INDEX, sessionUser.getStringValue(ItemNames.cart_contacts.POST_INDEX, ""));
		purchase.setValueUI(ItemNames.order.EMAIL, sessionUser.getStringValue(ItemNames.cart_contacts.EMAIL, ""));
		purchase.setValueUI(ItemNames.order.DELIVERY, delivery);
		purchase.setValueUI("second_name", sessionUser.getStringValue(ItemNames.cart_contacts.SECOND_NAME, ""));
	}

	private void saveToHisotry(List<Item> boughts, Date date, int orderNumber, String displayOrderNumber) throws Exception {
		Item order = ensureOrder();
		addCustomBoughtsToOrder(order);
		addCartToOrder(order, boughts);
		addContactsToOrder(order);
		order.setValue(ItemNames.order.NUMBER, displayOrderNumber);
		order.setValue(ItemNames.order.INT_NUMBER, orderNumber);
		order.setValue(ItemNames.order.DATE, date.getTime());
		order.setValue(ItemNames.order.STATUS, (byte) OrderManageCommand.orderStatus.WAITING.ordinal());
		order.setValue("status_log", (byte) OrderManageCommand.orderStatus.WAITING.ordinal());
		order.setValue("status_date", date.getTime());
		if (order.isNew()) {
			executeAndCommitCommandUnits(new SaveNewItemDBUnit(order).ignoreUser(true));
		} else {
			executeAndCommitCommandUnits(new UpdateItemDBUnit(order).ignoreUser(true));
		}
		if (isRegistered()) {
			Item userInfo = loadUserInfo();
			if (userInfo != null) {
				userInfo.removeValue(LATEST_ORDER_ID_PARAM);
				executeAndCommitCommandUnits(new UpdateItemDBUnit(userInfo).ignoreUser(true));
			}
		}
	}
*/
}
