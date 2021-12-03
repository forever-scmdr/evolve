package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.EmailUtils;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.model.*;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.*;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra.CartManageCommand;
import extra._generated.ItemNames;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class BelchipCartCommand extends CartManageCommand implements CartConstants, ItemNames {

	private float minimalOrderSum = (float) 0;
	private String lessThenMinimalOrderMessage = "";

	private static final String MARKER_SEPARATOR = ":=:";
	private static final String ITEM_SEPARATOR = ":item:";


	private boolean needPost = false;
	private Item userInfo = null;

	private static final String FAV_COOKIE = "favourites";
	private static final String FORM_PHYS_FORM = "form_phys";
	private static final String FORM_JUR_FORM = "form_jur";

	/**
	 * Удаляет все из корзины
	 * @throws Exception
	 */
	public ResultPE deleteAll() throws Exception {
		cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		if(cart != null){
			getSessionMapper().removeItems(cart.getId());
			Item userInfo = loadUserInfo(getInitiator());
			if(userInfo != null) {
				userInfo.clearValue(user_.BOUGHTS_SERIALIZED);
				userInfo.clearValue(user_.CUSTOM_BOUGHTS_SERIALIZED);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(userInfo).ignoreUser(true));
				getSessionMapper().removeItems(USER);
			} else {
				setCookieVariable(CART_COOKIE, null);
			}
		}
		return getResult("cart");
	}

	/**
	 * Восстанавливает корзину из сохраненной ранее в БД
	 * @throws Exception
	 */
	private void restoreCartFromUserInfo() throws Exception {
		Item userInfo = getUserInfo();
		if (userInfo == null)
			return;
		restoreBoughtsFromTemp(userInfo);
	}

	/**
	 * Добавить в корзину заказы из временно сохраненной корзины (из пользовательского айтема)
	 * @param userInfo
	 * @throws Exception
	 */
	private void restoreBoughtsFromTemp(Item userInfo) throws Exception {
		if (userInfo == null || (userInfo.isValueEmpty(user_.BOUGHTS_SERIALIZED) && userInfo.isValueEmpty(user_.CUSTOM_BOUGHTS_SERIALIZED)))
			return;

		boolean hasNoRegularBoughts = cart == null || getSessionMapper().getItemsByName(BOUGHT, cart.getId()).isEmpty();
		boolean hasNoCustomBoughts = cart == null || getSessionMapper().getItemsByParamValue(CUSTOM_BOUGHT, custom_bought_.NONEMPTY, "true").isEmpty();

		// Простые заказы (айтемы bought)
		if (hasNoRegularBoughts) {
			String boughts = userInfo.getStringValue(user_.BOUGHTS_SERIALIZED);
			String[] codeQtys = StringUtils.split(boughts, ";/");
			if (codeQtys.length > 0 && cart == null)
				createNewSessionCart();
			for (String codeQty : codeQtys) {
				String[] pair = StringUtils.split(codeQty, ':');
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.CODE, pair[0]);
				double qty = DoubleDataType.parse(pair[1]);
				if (product != null) {
					addProduct(product, qty);
				}
			}
		}

		// Персональный заказ (айтемы custom_bought)
		if (hasNoCustomBoughts) {
			String customBoughtsString = userInfo.getStringValue(user_.CUSTOM_BOUGHTS_SERIALIZED);
			String[] cbSerialized = StringUtils.splitByWholeSeparator(customBoughtsString, ITEM_SEPARATOR);
			if (cbSerialized != null && cbSerialized.length > 0) {
				if (cart == null)
					cart = getSessionMapper().getSingleRootItemByName(CART);
				if (cart == null)
					createNewSessionCart();
				List<Item> customBoughts = getSessionMapper().getItemsByName(CUSTOM_BOUGHT, cart.getId());
				if (customBoughts.isEmpty()) {
					createCustomBoughts();
					customBoughts = getSessionMapper().getItemsByName(CUSTOM_BOUGHT, cart.getId());
				}
				for (int i = 0; i < cbSerialized.length && i < customBoughts.size(); i++) {
					Item cb = customBoughts.get(i);
					Item.restoreParamValues(cb, cbSerialized[i]);
					getSessionMapper().saveTemporaryItem(cb);
				}
			}
		}
	}

	/**
	 * Добавить в корзину все товары, которые были в некотором заказе из истории
	 * (повторить определнный заказ, но не удаляя ранее добавленные товары)
	 * @param purchase
	 * @throws Exception
	 */
	private void addOldPurchaseToCart(Item purchase) throws Exception {
		if (purchase == null)
			return;
		MultipleHttpPostForm form = getItemForm();
		List<Item> oldBoughts = new ItemQuery(BOUGHT).setParentId(purchase.getId(), false).loadItems();
		for (Item bought : oldBoughts) {
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.CODE, bought.getStringValue(bought_.CODE));
			if (product == null)
				continue;
			double oldQty = bought.getDoubleValue(bought_.QTY_TOTAL);
			Double newQty = oldQty;
			if (form != null) {
				ItemInputValues input = form.getReadOnlyItemValues(bought.getId());
				if (input != null) {
					newQty = DoubleDataType.parse(input.getStringParam(bought_.QTY));
					newQty = newQty == null ? oldQty : newQty;
				}
			}
			addProduct(product, newQty);
		}
	}

	/**
	 * Загрузить айтем регистрации пользователя по текущему залогиненному пользователю
	 * @return
	 * @throws Exception
	 */
	private Item getUserInfo() throws Exception {
		if (userInfo == null) {
			userInfo = getSessionMapper().getSingleRootItemByName(USER);
		}
		if (userInfo == null) {
			userInfo = loadUserInfo(getInitiator());
			if (userInfo != null) {
				getSessionMapper().saveTemporaryItem(userInfo, USER);
			}
		}
		return userInfo;
	}

	/**
	 * Загрузить айтем регистрации пользователя
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private Item loadUserInfo(User user) throws Exception {
		if (user.inGroup(REGISTERED_GROUP)) {
			List<Item> register = new ItemQuery(USER).setUser(user).loadItems();
			if (!register.isEmpty())
				return register.get(0);
		}
		return null;
	}

	/**
	 * Восстанавливает корзину из строки вида product_id_1:qty1;product_id_2:qty2;....
	 * (обычно куки)
	 * @param cookie
	 * @throws Exception
	 */
	private void restoreCartFromCookieString(String cookie) throws Exception {
		if (StringUtils.isBlank(cookie) || cookie.indexOf(':') == -1)
			return;
		String[] idQtys = StringUtils.split(cookie, ";/");
		if (idQtys.length > 0 && cart == null)
			createNewSessionCart();
		for (String idQty : idQtys) {
			String[] pair = StringUtils.split(idQty, ':');
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.CODE, pair[0]);
			if (product == null)
				continue;
			double qty = DoubleDataType.parse(pair[1]);
			addProduct(product, qty);
		}
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
		String code = product.getStringValue(product_.CODE);
		ArrayList<Item> boughts = getSessionMapper().getItemsByParamValue(BOUGHT, bought_.CODE, code);
		switch (boughts.size()) {
			case 0: {
				if (qty <= 0)
					return;
				Item section = new ItemQuery(SECTION).setChildId(product.getId(), false).loadFirstItem();
				Item bought = getSessionMapper().createSessionItem(BOUGHT, cart.getId());
				qty = round(qty, product.getDoubleValue(product_.MIN_QTY, 1));
				bought.setValue(bought_.NAME, product.getStringValue(product_.NAME) + " " + product.getStringValue(product_.NAME_EXTRA));
				bought.setValue(bought_.QTY_TOTAL, qty);
				bought.setValue(bought_.LIMIT_1, section.getValue(bought_.LIMIT_1));
				bought.setValue(bought_.LIMIT_2, section.getValue(bought_.LIMIT_2));
				bought.setValue(bought_.DISCOUNT_1, section.getValue(section_.DISCOUNT_1));
				bought.setValue(bought_.DISCOUNT_2, section.getValue(section_.DISCOUNT_2));
				bought.setValue(bought_.TYPE, product.getItemType().getCaption());
				bought.setValue(bought_.CODE, code);
				getSessionMapper().saveTemporaryItem(bought);
				product.setContextPrimaryParentId(bought.getId());
				getSessionMapper().saveTemporaryItem(product, PRODUCT);
				break;
			}
			case 1: {
				Item bought = boughts.get(0);
				qty = round(qty, product.getDoubleValue(product_.MIN_QTY, 1));
				bought.setValue(bought_.QTY_TOTAL, qty);
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
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());
		BigDecimal sum = BigDecimal.ZERO; // полная сумма
		double currencySum = 0;
		BigDecimal discountSum = BigDecimal.ZERO; // сумма, с которой предоставляется скидка
		double currencyDiscountSum = 0;
		double regularQuantity = 0;
		double zeroQuantity = 0;
		double customQuantity = 0;
		double total = 0;
		boolean result = true;

		String currencyVar = getVarSingleValue("currency");
		currencyVar = StringUtils.isBlank(currencyVar) ? DEFAULT_CURRENCY : currencyVar;
		Item currencies = ItemQuery.loadSingleItemByName(CURRENCIES);

		int scale = StringUtils.equalsIgnoreCase(currencyVar, RUB_CURRENCY) ? 0 : 2;

		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT, bought.getId());
			double maxQuantity = product.getDoubleValue(product_.QTY, 0d);
			double qtyTotal = bought.getDoubleValue(bought_.QTY_TOTAL, 0d);
			qtyTotal = round(qtyTotal, product.getDoubleValue(product_.MIN_QTY, 1));
			double qtyAvailable = Math.min(qtyTotal, maxQuantity);
			double qtyZero = qtyTotal - maxQuantity;
			qtyZero = (qtyZero < 0) ? 0d : qtyZero;
			zeroQuantity += qtyZero;
			total += qtyTotal;
			if (product.getByteValue("is_service") == 1) {
				//maxQuantity = qtyTotal;
				qtyAvailable = qtyTotal;
				qtyZero = 0d;
				zeroQuantity = 0d;
			}
			bought.setValue(bought_.QTY_ZERO, qtyZero);
			bought.setValue(bought_.QTY_AVAIL, qtyAvailable);
			bought.setValue(bought_.QTY, qtyAvailable);
			if (qtyAvailable >= 0) {
				BigDecimal price = convert(product.getDecimalValue(product_.PRICE, BigDecimal.ZERO), currencies, currencyVar);
				bought.setValue(product_.PRICE, price);
				BigDecimal productSum = price.multiply(BigDecimal.valueOf(qtyAvailable)).setScale(scale, BigDecimal.ROUND_UP);
				regularQuantity += qtyAvailable;
				bought.setValue(bought_.SUM, productSum);
				sum = sum.add(productSum);
				if (product.getStringValue(product_.SPECIAL_PRICE, FALSE_VALUE).equals(FALSE_VALUE))
					discountSum = discountSum.add(productSum);

				getSessionMapper().saveTemporaryItem(bought);
			}
			if (qtyTotal <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT);
				result = false;
			}
		}
		// Персональные заказы

		ArrayList<Item> customBoughts = getSessionMapper().getItemsByName(CUSTOM_BOUGHT, cart.getId());
		for (Item bought : customBoughts) {
			if (StringUtils.equals("true", bought.getStringValue(custom_bought_.NONEMPTY))) {
				customQuantity = customQuantity + 1;
			}
		}
		BigDecimal simpleSum = sum;
		BigDecimal discount = BigDecimal.ZERO;
		double quotient = 0;
		// Скидка с суммы (в случае если заказано более 1 товара)
		if (regularQuantity > 1) {
			if (simpleSum.compareTo(convert(SUM_1, currencies, currencyVar)) >= 0 && simpleSum.compareTo(convert(SUM_2, currencies, currencyVar)) < 0) {
				discount = BigDecimal.valueOf(DISCOUNT_1);
				quotient = (double) (DISCOUNT_1) / (double) 100;
			} else if (simpleSum.compareTo(convert(SUM_2, currencies, currencyVar)) >= 0) {
				discount = BigDecimal.valueOf(DISCOUNT_2);
				quotient = (double) (DISCOUNT_2) / (double) 100;
			}
			sum = sum.subtract(discountSum.multiply(BigDecimal.valueOf(quotient)));
		}
		// Округление суммы
		sum = sum.setScale(2, RoundingMode.CEILING);
		cart.setValue(cart_.SIMPLE_SUM, simpleSum);
		cart.setValue(cart_.SUM, sum);
		cart.setValue(cart_.QTY, regularQuantity);
		cart.setValue(cart_.ZERO_QTY, zeroQuantity);
		cart.setValue(cart_.CUSTOM_QTY, customQuantity);
		cart.setValue(cart_.DISCOUNT, discount.setScale(0, BigDecimal.ROUND_HALF_EVEN).intValue());
		cart.setValue(cart_.MARGIN, simpleSum.subtract(sum));
		cart.setValue(cart_.CURRENCY, currencyVar);
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
	private boolean checkMinSum(boolean isJur) throws Exception {
		double min;
		Item orderVars = ItemQuery.loadSingleItemByName(ORDER_EMAILS);
		if (!isJur) {
			if (needPost) {
				min = Double.parseDouble(getVarSingleValue("min_phys_post_sum"));
				min = (orderVars == null) ? min : orderVars.getDoubleValue(order_emails_.MIN_POST, min);
				this.minimalOrderSum = (float) min;
				lessThenMinimalOrderMessage = POST_LTM;
			} else {
				min = Double.parseDouble(getVarSingleValue("min_phys_sum"));
				min = (orderVars == null) ? min : orderVars.getDoubleValue(order_emails_.MIN_PHYS, min);
				this.minimalOrderSum = (float) min;
				lessThenMinimalOrderMessage = PHYS_LTM;
			}
			return cart.getDecimalValue(cart_.SUM, BigDecimal.ZERO).compareTo(BigDecimal.valueOf(min)) >= 0;
		} else {
			min = Double.parseDouble(getVarSingleValue("min_jur_sum"));
			min = (orderVars == null) ? min : orderVars.getDoubleValue(order_emails_.MIN_JUR, min);
			ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());
			BigDecimal sum = BigDecimal.ZERO;
			for (Item bought : boughts) {
				double q = bought.getDoubleValue(bought_.QTY_TOTAL, 0d);
				Item product = getSessionMapper().getSingleItemByName(PRODUCT, bought.getId());
				BigDecimal price = product.getDecimalValue(product_.PRICE, BigDecimal.ZERO);
				sum = sum.add(price.multiply(BigDecimal.valueOf(q)));
			}
			this.minimalOrderSum = (float) min;
			lessThenMinimalOrderMessage = JUR_LTM;
			return sum.compareTo(BigDecimal.valueOf(min)) >= 0;
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
			Item product = getSessionMapper().getSingleItemByName(PRODUCT, bought.getId());
			String filePath = AppContext.getFilesDirPath(false) + BARCODE_DIR;
			String fileName = product.getStringValue(product_.CODE) + PNG_EXT;
			File bcFile = new File(filePath + fileName);
			if (!bcFile.exists()) {
				try {
					new File(filePath).mkdirs();
					String bc12 = StringUtils.substring(product.getStringValue(product_.BARCODE, "none"), 0, -1);
					Barcode barcode = BarcodeFactory.createEAN13(bc12);
					barcode.setLabel(bc12);
					barcode.setResolution(36);
					BarcodeImageHandler.savePNG(barcode, bcFile);
				} catch (Exception e) {
					ServerLogger.debug("Cannot generate barcode for " + product.getStringValue(product_.BARCODE, "none"), e);
				}
			}
		}
	}

	/**
	 * Переписать контакты из айтема юзера в заказ.
	 * Юзер берется текущий (который сейчас залогинен)
	 * @param purchase
	 * @throws Exception
	 */
	private void addContactsToPurchase(Item purchase) throws Exception {
		Item sessionUser = getSessionMapper().getSingleRootItemByName(USER);
		Item userInfo = getUserInfo();
		purchase.clearValue(purchase_.CLIENT_ID);
		purchase.clearValue(purchase_.LOGIN);
		if (userInfo != null) {
			purchase.setValue(purchase_.CLIENT_ID, userInfo.getId());
			purchase.setValue(purchase_.LOGIN, getInitiator().getName());
		}
		boolean isPhys = StringUtils.equalsIgnoreCase(sessionUser.getTypeName(), USER_PHYS);
		purchase.setValueUI(purchase_.PHYS, isPhys ? "1" : "0");
		String phone = (isPhys) ? sessionUser.getStringValue(user_phys_.PHONE)
				: sessionUser.getStringValue(user_jur_.CONTACT_PHONE);
		String name = (isPhys) ? sessionUser.getStringValue(user_phys_.NAME)
				: sessionUser.getStringValue(user_jur_.CONTACT_NAME) + ". Организация: "
				+ sessionUser.getStringValue(user_jur_.ORGANIZATION);
		purchase.setValueUI(purchase_.PHONE, phone);
		purchase.setValueUI(purchase_.CLIENT_NAME, name);
		purchase.setValueUI(purchase_.PAYMENT, sessionUser.getStringValue(user_.PAYMENT, ""));
		purchase.setValueUI(purchase_.IF_ABSENT, sessionUser.getStringValue(user_.IF_ABSENT, ""));
		purchase.setValueUI(purchase_.ADDRESS, sessionUser.getStringValue(user_.POST_ADDRESS, ""));
		purchase.setValueUI(purchase_.CITY, sessionUser.getStringValue(user_.POST_CITY, ""));
		purchase.setValueUI(purchase_.REGION, sessionUser.getStringValue(user_.POST_REGION, ""));
		purchase.setValueUI(purchase_.COUNTRY, sessionUser.getStringValue(user_.POST_COUNTRY, ""));
		purchase.setValueUI(purchase_.INDEX, sessionUser.getStringValue(user_.POST_INDEX, ""));
		purchase.setValueUI(purchase_.EMAIL, sessionUser.getStringValue(user_.EMAIL, ""));
		String delivery;
		if (isPhys) {
			delivery = sessionUser.getStringValue(user_phys_.SHIP_TYPE);
			purchase.setValueUI(purchase_.SECOND_NAME, sessionUser.getStringValue(user_phys_.SECOND_NAME, ""));
		} else {
			delivery = StringUtils.isNotBlank(sessionUser.getStringValue(user_jur_.POST_INDEX, "")) ? "доставка почтой"
					: "самовывоз";
		}
		purchase.setValueUI(purchase_.DELIVERY, delivery);
	}

	/**
	 * Окончательно сохранить заказ в истории.
	 * При этом создаются все сопутсвующие айтемы, такие как bought и custom_bought,
	 * поторые хранятся как дочерние для заказа
	 * @param userInfo
	 * @param date
	 * @param orderNumber
	 * @param displayOrderNumber
	 * @throws Exception
	 */
	private void savePurchaseToHisotry(Item userInfo, DateTime date, int orderNumber, String displayOrderNumber) throws Exception {
		Item userDB = ItemQuery.loadSingleItemByParamValue(USER_ITEM, user_.EMAIL, userInfo.getStringValue(user_.EMAIL));
		if (isRegistered()) {
			userDB = loadUserInfo(getInitiator());
		} else if (userDB == null) {
			Item registeredCatalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG, User.getDefaultUser(),
					UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			userInfo.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), registeredCatalog.getId());
			userInfo.setValue(user_.REGISTERED, (byte)0);
			executeCommandUnit(SaveItemDBUnit.get(userInfo).ignoreUser());
			userDB = userInfo;
		} else if (userDB.getOwnerUserId() == userInfo.getOwnerUserId() && !userDB.isPersonal()) {
			Item.updateParamValues(userInfo, userDB, user_.PASSWORD, user_.REGISTERED);
			executeCommandUnit(SaveItemDBUnit.get(userDB).ignoreUser());
		}
		Item purchase = Item.newChildItem(ItemTypeRegistry.getItemType(PURCHASE), userDB);
		addContactsToPurchase(purchase);
		purchase.setValue(purchase_.CURRENCY, cart.getStringValue(cart_.CURRENCY));
		purchase.setValue(purchase_.NUM, displayOrderNumber);
		purchase.setValue(purchase_.INT_NUMBER, orderNumber);
		purchase.setValue(purchase_.DATE, date.getMillis());
		purchase.setValue(purchase_.STATUS, (byte) OrderManageCommand.orderStatus.WAITING.ordinal());
		purchase.setValue(purchase_.STATUS_LOG, (byte) OrderManageCommand.orderStatus.WAITING.ordinal());
		purchase.setValue(purchase_.STATUS_DATE, date.getMillis());
		purchase.setValue(purchase_.SUM, cart.getDecimalValue(cart_.SUM));
		purchase.setValue(purchase_.SIMPLE_SUM, cart.getDecimalValue(cart_.SIMPLE_SUM));
		purchase.setValue(purchase_.DISCOUNT, cart.getIntValue(cart_.DISCOUNT));
		purchase.setValue(purchase_.MARGIN, cart.getDecimalValue(cart_.MARGIN));
		purchase.setValue(purchase_.QTY, cart.getDoubleValue(cart_.QTY));
		executeCommandUnit(SaveItemDBUnit.get(purchase).ignoreUser());
		List<Item> customBoughts = getSessionMapper().getItemsByName(CUSTOM_BOUGHT, cart.getId());
		for (Item customBought : customBoughts) {
			if (StringUtils.equals("true", customBought.getStringValue(custom_bought_.NONEMPTY))) {
				Item historyCustomBought = Item.newChildItem(ItemTypeRegistry.getItemType(CUSTOM_BOUGHT), purchase);
				Item.updateParamValues(customBought, historyCustomBought);
				executeCommandUnit(SaveItemDBUnit.get(historyCustomBought).ignoreUser());
			}
		}
		List<Item> boughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());
		for (Item bought : boughts) {
			Item historyBought = Item.newChildItem(ItemTypeRegistry.getItemType(BOUGHT), purchase);
			Item.updateParamValues(bought, historyBought);
			executeCommandUnit(SaveItemDBUnit.get(historyBought).ignoreUser());
		}
		commitCommandUnits();
	}

	/**
	 * Сериализовать обычные заказы корзины
	 * @return
	 * @throws Exception
	 */
	private String serializeBoughts() throws Exception {
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());
		ArrayList<String> codeQtys = new ArrayList<>();
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT, bought.getId());
			double quantity = bought.getDoubleValue(bought_.QTY_TOTAL);
			codeQtys.add(product.getStringValue(product_.CODE) + ":" + quantity);
		}
		return StringUtils.join(codeQtys, '/');
	}

	/**
	 * Сериализовать персональные заказы корзины
	 * @return
	 * @throws Exception
	 */
	private String serializeCustomBoughts() throws Exception {
		ArrayList<Item> customBoughts = getSessionMapper().getItemsByName(CUSTOM_BOUGHT, cart.getId());
		StringBuilder customBoughtString = new StringBuilder();
		for (Item bought : customBoughts) {
			if (StringUtils.equals("true", bought.getStringValue(custom_bought_.NONEMPTY))) {
				customBoughtString.append(ITEM_SEPARATOR).append(Item.setializeParamValues(bought));
			}
		}
		return customBoughtString.toString();
	}

	/**
	 * Получить новое значение счетчка номера договора.
	 * Сохраняет новое значение счетчика, но транзакция не комитится, т.к. возможны ошибки в дальнейших действиях
	 * @return
	 * @throws Exception
	 */
	private int incrementAndGetOrderNumber() throws Exception {
		Item counter = new ItemQuery(COUNTER).loadItems().get(0);
		if (counter == null) {
			Item system = ItemUtils.ensureSingleRootAnonymousItem(SYSTEM, User.getDefaultUser());
			counter = ItemUtils.ensureSingleChild(COUNTER, User.getDefaultUser(), system);
		}
		int count = counter.getIntValue(counter_.COUNT, 0) + 1;
		if (count > 99999)
			count = 1;
		String orderNumber = String.format("%05d", count);
		cart.setValue("order_num", orderNumber);
		// Сохранение нового значения счетчика, чтобы дублей не было.
		counter.setValue(counter_.COUNT, count);
		executeCommandUnit(SaveItemDBUnit.get(counter).ignoreUser(true));
		return count;
	}

	/**
	 * Проверка, я вляется ли текущий пользователь зарегистрированным
	 * @return
	 */
	private boolean isRegistered() {
		return getInitiator().inGroup(REGISTERED_GROUP) && !getInitiator().isAdmin("common");
	}

	/**
	 * Перевод цены товара из белорусских рублей в заданную валюту
	 * @param price
	 * @param currencies
	 * @param convertToCurrencyCode
	 * @return
	 */
	private BigDecimal convert(BigDecimal price, Item currencies, String convertToCurrencyCode) {
		if (currencies == null || StringUtils.isBlank(convertToCurrencyCode))
			return price;
		BigDecimal rate = currencies.getDecimalValue(convertToCurrencyCode + RATE_PART, BigDecimal.valueOf(1));
		BigDecimal scale = currencies.getDecimalValue(convertToCurrencyCode + SCALE_PART, BigDecimal.valueOf(1));
		BigDecimal extraQuotient = BigDecimal.valueOf(currencies.getDoubleValue(convertToCurrencyCode + EXTRA_QUOTIENT_PART, 1));
		boolean ceil = currencies.getByteValue(convertToCurrencyCode + CEIL_PART, (byte) 0) == (byte) 1;
		price = price.divide(rate, RoundingMode.HALF_EVEN).multiply(scale).multiply(extraQuotient);
		return price.setScale(ceil ? 0 : 2, RoundingMode.CEILING);
	}

	/**
	 * Сохранить изменения в корзине после ее изменения, например, добавления товара
	 * Имеется в виду сохранение корзины в БД или в куки
	 * @throws Exception
	 */
	private void saveCartChangesInDBAndCookie() throws Exception {
		List<Item> boughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());
		Item userInfo = null;
		if (isRegistered())
			userInfo = loadUserInfo(getInitiator());
		if (isRegistered() && userInfo != null) {
			String regularBoughts = serializeBoughts();
			if (StringUtils.isNotBlank(regularBoughts))
				userInfo.setValue(user_.BOUGHTS_SERIALIZED, regularBoughts);
			String customBoughts = serializeCustomBoughts();
			if (StringUtils.isNotBlank(customBoughts))
				userInfo.setValue(user_.CUSTOM_BOUGHTS_SERIALIZED, customBoughts);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(userInfo).ignoreUser());
		} else {
			StringBuilder sb = new StringBuilder();
			for (Item bought : boughts) {
				sb.append(bought.getStringValue(bought_.CODE)).append(':')
						.append(bought.getDoubleValue(bought_.QTY_TOTAL, 0d)).append(';');
			}
			setCookieVariable(CART_COOKIE, sb.toString());
		}
	}



	/**
	 * Проанализировать ввод пользователя и пересчитать все в корзине
	 *
	 * @throws Exception
	 */
	private boolean doRecalculate() throws Exception {
		if (cart == null)
			cart = getSessionMapper().getSingleRootItemByName(CART);
		if (cart == null)
			restoreFromCookie();
		if (cart == null)
			return false;
		boolean result = true;
		boolean hasCustom = false;
		int customBoughtItemId = ItemTypeRegistry.getItemTypeId(CUSTOM_BOUGHT);

		ArrayList<Item> allBoughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());
		for (Item bought : allBoughts) {
			ItemInputValues boughtInput = getItemForm().getReadOnlyItemValues(bought.getId());
			if (boughtInput.getStringParam(bought_.QTY) != null) {
				double quantity = -1;
				try {
					quantity = DoubleDataType.parse(boughtInput.getStringParam(bought_.QTY));
				} catch (NumberFormatException e) {/* */}
				if (quantity > 0) {
					bought.setValue(bought_.QTY_TOTAL, quantity);
					getSessionMapper().saveTemporaryItem(bought);
					Item product = getSessionMapper().getSingleItemByName(PRODUCT, bought.getId());
					if (product == null) {
						String code = bought.getStringValue(bought_.CODE);
						product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.CODE, code);
						if (product != null) {
							product.setContextPrimaryParentId(bought.getId());
							getSessionMapper().saveTemporaryItem(product);
						}
					}
				} else {
					getSessionMapper().removeItems(bought.getId());
					result = false;
				}
			}
		}
		ArrayList<Item> allCustom = getSessionMapper().getItemsByName(CUSTOM_BOUGHT, cart.getId());
		ArrayList<ItemTreeNode> nodes = getItemForm().getItemTree().findChildren(CUSTOM_BOUGHT);
		for (ItemTreeNode customNode : nodes) {
			Item formCustom = customNode.getItem();
			Item sessionCustom = getSessionMapper().getItem(formCustom.getId(), CUSTOM_BOUGHT);
			if (sessionCustom != null) {
				Item.updateParamValues(formCustom, sessionCustom, custom_bought_.POSITION);
				if (formCustom.isValueNotEmpty(custom_bought_.LINK) || formCustom.isValueNotEmpty(custom_bought_.MARK)) {
					sessionCustom.setValue(custom_bought_.NONEMPTY, "true");
					hasCustom = true;
				} else {
					sessionCustom.clearValue(custom_bought_.NONEMPTY);
				}
				getSessionMapper().saveTemporaryItem(sessionCustom);
			}
		}
		boolean recalc = recalculateCart();
		saveCartChangesInDBAndCookie();
		return (recalc || hasCustom) && result;
	}


	/**
	 * Обновить айтем пользователя, хранящийся в сеансе, значенями из формы сведений о пользователе
	 * @return
	 * @throws Exception
	 */
	private Item updateSessionUserWithForm() throws Exception {
		Item sessionUser = getSessionMapper().getSingleRootItemByName(USER);
		Item formUser = getItemForm().getItemSingleTransient();
		if (sessionUser == null) {
			sessionUser = getSessionMapper().createSessionRootItem(formUser.getTypeName());
		} else {
			if (sessionUser.getItemType().getTypeId() != formUser.getItemType().getTypeId()) {
				getSessionMapper().removeItems(USER);
				sessionUser = getSessionMapper().createSessionRootItem(formUser.getTypeName());
			}
		}
		Item.updateParamValues(formUser, sessionUser, user_.PASSWORD, user_.REGISTERED);
		//ItemHttpPostForm.editExistingItem(form, sessionUser, NEED_POST_ADDRESS_PARAM, JUR_NEED_POST_ADDRESS_PARAM, MESSAGE_PARAM);
		for (Parameter param : sessionUser.getAllParameters()) {
			String strValue = sessionUser.outputValue(param.getName());
			strValue = strValue.trim().replaceAll("\\s+", " ");
			sessionUser.setValueUI(param.getName(), strValue);
		}

		if (StringUtils.equalsIgnoreCase(sessionUser.getTypeName(), USER_PHYS)) {
			try {
				setDeliveryAndPayment(sessionUser);
			} catch (Exception e) { /* */ }
		}

		getSessionMapper().saveTemporaryItem(sessionUser, USER);
		return sessionUser;
	}

	/**
	 * Загрузить айтемы delivery и payment из БД по их ID, установленным в форме ввода контактных данных,
	 * и установить соответствующие значения из этих айтемов в поля формы пользователя
	 * @param user
	 * @throws Exception
	 */
	private void setDeliveryAndPayment(Item user) throws Exception {
		String deliveryId = user.getStringValue(user_.GET_ORDER_FROM,"");
		String paymentId = user.getStringValue(user_.PAYMENT,"");
		Item delivery = ItemQuery.loadById(Long.parseLong(deliveryId));
		Item payment = ItemQuery.loadById(Long.parseLong(paymentId));
		needPost = delivery != null && delivery.getByteValue(delivery_.ASK_ADDRESS, (byte)0) != 0;
		user.setValue(user_.GET_ORDER_FROM, delivery != null ? delivery.getValue(delivery_.NAME) : "");
		user.setValue(user_.PAY_TYPE, payment != null ? payment.getValue(payment_.NAME) : "");
		String suffix = (delivery != null ? delivery.getStringValue("suffix","") : "")
				+ (payment != null ? payment.getStringValue("suffix","") : "");
		user.setExtra("suffix", suffix);
	}

	/**
	 * Создает нужный заголовок номера заказа согласно шаблону
	 * @param orderNumber
	 * @return
	 * @throws Exception
	 */
	private String createDisplayOrderNumber(int orderNumber) throws Exception {
		Item user = getSessionMapper().getSingleRootItemByName(USER);
		String r = user.getStringExtra("suffix");
		r = StringUtils.isBlank(r)? "" : '-'+r;
		if(cart.getValue(cart_.CURRENCY,  "BYN").equals("RUR")) {
			r = "-РУ";
		}
		return String.format("%05d", orderNumber) + r;
	}

	/**
	 * Создать новый файл с XML заказа
	 * @param isPhys
	 * @param displayOrderNumber
	 * @param currency
	 * @throws Exception
	 */
	private String saveOrderToXMLFile(boolean isPhys, String displayOrderNumber, String currency) throws Exception {
		String folder = AppContext.getRealPath(
				"WEB-INF/"
						+ (isPhys ? getVarSingleValueDefault("phys_folder", "phys")
						: getVarSingleValueDefault("jur_folder", "jur"))
		);
		File dir = new File(folder);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		Item user = getSessionMapper().getSingleRootItemByName(USER);
		LinkPE orderFileLink = LinkPE.newDirectLink("link", "order_file", false);
		orderFileLink.addStaticVariable("order_num", displayOrderNumber);
		orderFileLink.addStaticVariable("deivery", user.getStringValue("get_order_from"));
		orderFileLink.addStaticVariable("payment", user.getStringValue("pay_by"));
		orderFileLink.addStaticVariable("currency", currency);

		ExecutablePagePE orderFileTemplate = getExecutablePage(orderFileLink.serialize());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PageController.newSimple().executePage(orderFileTemplate, out);
		File file = new File(folder + "/" + displayOrderNumber + ".xml");
		FileUtils.writeByteArrayToFile(file, out.toByteArray());

		return orderFileLink.serialize();
	}

	/**
	 * Проверить заполненность полей для юзера - юридического лица
	 * @return
	 * @throws Exception
	 */
	private List<String> checkMandatoryJur() throws Exception {
		ArrayList<String> notSet = new ArrayList<>();
		Item user = getSessionMapper().getSingleRootItemByName(USER);
		for (String param : JUR_MANDATORY) {
			if (user.isValueEmpty(param))
				notSet.add(param);
		}
		if (!user.getStringValue(user_jur_.BASE).equals(STATE_VALUE)) {
			for (String param : JUR_BASE_MANDATORY) {
				if (user.isValueEmpty(param))
					notSet.add(param);
			}
		}
		if (!StringUtils.equalsIgnoreCase(YES_VALUE, user.getStringValue(user_jur_.NO_ACCOUNT))) {
			for (String param : JUR_NO_ACCOUNT_MANDATORY) {
				if (user.isValueEmpty(param))
					notSet.add(param);
			}
		}
		if (StringUtils.equalsIgnoreCase(YES_VALUE, user.getStringValue(user_.NEED_POST_ADDRESS))) {
			for (String param : JUR_ADDRESS_MANDATORY) {
				if (user.isValueEmpty(param))
					notSet.add(param);
			}
		}
		return notSet;
	}

	/**
	 * Проверить заполненность полей для юзера - физического лица
	 * @return
	 * @throws Exception
	 */
	private List<String> checkMandatoryPhys() throws Exception {
		ArrayList<String> notSet = new ArrayList<>();
		Item user = getSessionMapper().getSingleRootItemByName(USER);
		for (String param : PHYS_MANDATORY) {
			if (StringUtils.isBlank(user.getStringValue(param)))
				notSet.add(param);
			String name = user.getStringValue(user_phys_.NAME);
			String secondName = user.getStringValue(user_phys_.SECOND_NAME);
			if (StringUtils.isNotBlank(name) && name.length() < 2) {
				notSet.add(name);
			}
			if (StringUtils.isNotBlank(secondName) && secondName.length() < 2) {
				notSet.add(secondName);
			}
		}

		if (needPost) {
			for (String param : PHYS_ADDRESS_MANDATORY) {
				if (user.isValueEmpty(param))
					notSet.add(param);
			}
		}
		return notSet;
	}


	public void editFav(boolean add) throws Exception {
		String varValue = getVarSingleValue(PRODUCT_PARAM);
		HashSet<Object> values = new HashSet<>(getCookieVarValues(FAV_COOKIE));
		if (add)
			values.add(varValue);
		else
			values.remove(varValue);
		setCookieVariable(FAV_COOKIE, values.toArray(new Object[0]));
		if (isRegistered())	{
			Item user = loadUserInfo(getInitiator());
			if (user != null) {
				user.setValueUI(user_.FAV_COOKIE, getCookieVarPlainValue(FAV_COOKIE));
				executeAndCommitCommandUnits(SaveItemDBUnit.get(user));
			}
		}
	}

	/**
	 * Создать новую корзину в сеансе
	 * @return
	 */
	private Item createNewSessionCart() {
		cart = getSessionMapper().createSessionRootItem(CART);
		getSessionMapper().saveTemporaryItem(cart);
		return cart;
	}




	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//                                  МЕТОДЫ ДЛЯ PAGES.XML
	//
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Добавляет к корзине айтемы для товаров, которых нет на сайте и чел хочет чтобы они появились
	 * (custom_bought). Добавляется 10 айтемов, показывается не сайте 5
	 * @throws Exception
	 */
	public ResultPE createCustomBoughts() throws Exception {
		if (cart == null)
			cart = getSessionMapper().getSingleRootItemByName(CART);
		if (cart == null)
			createNewSessionCart();
		if (getSessionMapper().getItemsByName(CUSTOM_BOUGHT, cart.getId()).size() > 0)
			return null;
		// Добавление айтемов для персонального заказа
		for (int i = 0; i < CUSTOM_BOUGHT_COUNT; i++) {
			Item custom = getSessionMapper().createSessionItem(CUSTOM_BOUGHT, cart.getId());
			custom.setValue(custom_bought_.POSITION, i);
			getSessionMapper().saveTemporaryItem(custom);
		}
		getSessionMapper().saveTemporaryItem(cart);
		return null;
	}

	/**
	 * Заказ товара. Создает новую корзину если нужно.
	 */
	public ResultPE addToCart() throws Exception {
		preloadCart();
		if (cart == null) {
			createNewSessionCart();
		} else if (cart.getByteValue(cart_.PROCESSED, (byte) 0) != 0) {
			getSessionMapper().removeItems(cart.getId());
			createNewSessionCart();
		}
		long productId = Long.parseLong(getVarSingleValue(PRODUCT_PARAM));
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_REQ_PARAM));
		} catch (Exception e) {/* */}
		Item product = ItemQuery.loadById(productId);
		addProduct(product, quantity);
		recalculateCart();
		saveCartChangesInDBAndCookie();
		return getResult("cart_ajax");
	}

	/**
	 * Создает корзину и восстанавливает ее из сеанса, куки или БД
	 * Вызывается страницами сайта
	 * Вненший метод
	 * @return
	 * @throws Exception
	 */
	public ResultPE preloadCart() throws Exception {
		// если несколько корзин
		try {
			cart = getSessionMapper().getSingleRootItemByName(CART);
		} catch (Exception e) {
			getSessionMapper().removeItems(CART);
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
		if (isRegistered()) {
			restoreCartFromUserInfo();
		} else {
			restoreCartFromCookieString(getVarSingleValue(CART_COOKIE));
		}
		if (cart != null) {
			recalculateCart();
			createCustomBoughts();
		}
		return null;
	}


	/**
	 * Переход на стараницу ввода контактных данных
	 *
	 * @throws Exception
	 * @throws EcommanderException
	 */
	public ResultPE proceed() throws Exception {
		Item userData = getUserInfo();
		if (userData == null) {
			Item newPhysUserData = getSessionMapper().createSessionRootItem(USER_PHYS);
			getSessionMapper().saveTemporaryItem(newPhysUserData, USER);
		}
		return doRecalculate() ? getResult("post") : getResult("cart");
	}

	/**
	 * Проанализировать ввод пользователя и переcчитать корзину
	 *
	 * @throws Exception
	 */
	public ResultPE recalculate() throws Exception {
		doRecalculate();
		String redirect = getVarSingleValueDefault("redirect", "cart");
		return getResult(redirect);
	}

	/**
	 * Логаут
	 * @throws Exception
	 */
	public ResultPE logout() throws Exception {
		Item userInfo = loadUserInfo(getInitiator());
		if(userInfo != null) {
			String favCookie = getVarSingleValueDefault(FAV_COOKIE, "");
			userInfo.setValue(user_.FAV_COOKIE, favCookie);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(userInfo));
		}
		getSessionMapper().removeItems(USER);
		getSessionMapper().removeItems(CART);
		endUserSession();

		return getResult("logout_ajax");
	}

	/**
	 * Добавить к текущей корзине некоторый предыдущий завершенный заказ
	 * ID айтема заказа передается через переменную страницы cookie
	 * @return
	 * @throws Exception
	 */
	public ResultPE merge() throws Exception {
		cart = getSessionMapper().getSingleRootItemByName(CART);
		if (cart == null) {
			createNewSessionCart();
		} else if (cart.getByteValue(cart_.PROCESSED, (byte) 0) != 0) {
			getSessionMapper().removeItems(cart.getId());
			createNewSessionCart();
		}
		String purchaseId = getVarSingleValue("purchase");
		if (StringUtils.isNotBlank(purchaseId)) {
			Item purchase = ItemQuery.loadById(Long.parseLong(purchaseId));
			addOldPurchaseToCart(purchase);
			recalculateCart();
			saveCartChangesInDBAndCookie();
		}
		return getResult("general_redirect").setVariable("message", "Заказ добавлен в корзину").setVariable("success", "true");
	}

	/**
	 * Логин зарегистрированного пользтвателя
	 * Происходит загрузка в сеанс анкеты пользователя, а также
	 * восстановление незаконченного заказа и избранного
	 * @return
	 * @throws Exception
	 */
	public ResultPE login() throws Exception {
		String pass = getVarSingleValue(PASSWORD_PARAM);
		User user = UserMapper.getUser(getVarSingleValue(LOGIN_PARAM), pass);
		if (user == null) {
			return getResult("login_error");
		}
		userInfo = loadUserInfo(user);
		if (userInfo != null) {
			getSessionMapper().removeItems(USER);
			getSessionMapper().saveTemporaryItem(userInfo, USER);
		}
		startUserSession(user);
		cart = getSessionMapper().getSingleRootItemByName(CART);
		if (cart == null) {
			createNewSessionCart();
			restoreCartFromUserInfo();
			if (cart != null)
				recalculateCart();
		} else {
			restoreCartFromUserInfo();
			recalculateCart();
			saveCartChangesInDBAndCookie();
		}

		//getSessionMapper().removeItems("chosen");
		if (userInfo != null) {
			if (userInfo.isValueNotEmpty(user_.FAV_COOKIE)) {
				//setPageVariable("favourites", userInfo.getStringValue(user_.FAV_COOKIE));
				setCookieVariable(FAV_COOKIE, userInfo.getStringValue(user_.FAV_COOKIE));
			}
		}

		return getResult("login_ajax");
	}

	/**
	 * Удалить товар
	 * @return
	 * @throws Exception
	 */
	public ResultPE delete() throws Exception {
		cart = getSessionMapper().getSingleRootItemByName(CART);
		// В этом случае ID не самого продукта, а объекта bought
		long boughtId = Long.parseLong(getVarSingleValue(BOUGHT));
		getSessionMapper().removeItems(boughtId, BOUGHT);
		recalculateCart();
		saveCartChangesInDBAndCookie();
		return getResult("cart");
	}

	/**
	 * Удалить заказ
	 * @return
	 * @throws Exception
	 */
	public ResultPE deletePurchase() throws Exception {
		String purchaseId = getVarSingleValue("purchase");
		if (StringUtils.isNotBlank(purchaseId)) {
			Item purchase = ItemQuery.loadById(Long.parseLong(purchaseId));
			if (purchase != null)
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(purchase));
		}
		return getResult("general_redirect").setVariable("message", "Заказ удален").setVariable("success", "true");
	}

	/**
	 * Отправка заказа для юридического лица
	 * @return
	 * @throws Exception
	 */
	public ResultPE postJur() throws Exception {
		cart = getSessionMapper().getSingleRootItemByName(CART);
		if (StringUtils.equalsIgnoreCase(cart.getStringExtra(IN_PROGRESS), "true") || cart.getByteValue(cart_.PROCESSED, (byte) 0) == 1) {
			return getResult("proceed");
		}
		cart.setExtra(IN_PROGRESS, "true");
		getSessionMapper().saveTemporaryItem(cart);

		Item userForm = getItemForm().getItemSingleTransient();
		saveSessionForm(userForm.getTypeName());
		Item userSession = updateSessionUserWithForm();

		List<String> mandatoryNotSet = checkMandatoryJur();
		if (mandatoryNotSet.size() > 0) {
			return getMandatoryNotSetResult(mandatoryNotSet);
		}

		boolean hasRegularBoughts = false;
		boolean hasZeroBoughts = false;

		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());

		for (Item bought : boughts) {
			double maxQuantity = bought.getDoubleValue(bought_.QTY_TOTAL, 0d);
			if (maxQuantity > 0)
				hasRegularBoughts = true;
			if (bought.getDoubleValue(bought_.QTY_ZERO) > 0)
				hasZeroBoughts = true;
			if (hasRegularBoughts && hasZeroBoughts)
				break;
		}
		boolean needPost = StringUtils.equalsIgnoreCase(YES_VALUE, userForm.getStringValue(user_.NEED_POST_ADDRESS));

		if (!checkMinSum(true) && hasRegularBoughts) {
			return getSumTooSmallResult();
		}

		int orderNumber = incrementAndGetOrderNumber();
		String displayOrderNumber = createDisplayOrderNumber(orderNumber);
		DateTime date = DateTime.now();

		boolean hasCustomBougths = !getSessionMapper().getItemsByParamValue(CUSTOM_BOUGHT, custom_bought_.NONEMPTY, "true").isEmpty();

		final String customerEmail = userForm.getStringValue(user_.EMAIL).trim();
		LinkPE regularLink = LinkPE.parseLink("");
		if (hasRegularBoughts) {
			String regularTopic = "Заказ " + userForm.getStringValue(user_jur_.ORGANIZATION) + " №" + displayOrderNumber + " от "
					+ DAY_FORMATTER.print(DateTime.now());
			regularLink = LinkPE.newDirectLink("link", "order_email", false);
			regularLink.addStaticVariable("order_num", displayOrderNumber);
			regularLink.addStaticVariable("action", "post_jur");
			if (hasZeroBoughts || hasCustomBougths) {
				regularLink.addStaticVariable("also", "Z");
			}

			List<String> emails = new ArrayList<String>() {{ add(customerEmail.trim()); }};

			// shop email
			//emails.add(getVarSingleValue(EMAIL_CUSTOM));
			emails.add(getVarSingleValue(EMAIL_JUR));

			try {
				sendEmail(emails, regularTopic, regularLink);
			} catch (Exception e) {
				return getEmailSendingErrorResult(e);
			}
		}

		if (hasZeroBoughts || hasCustomBougths) {
			String requestOrderNumber = String.format("%05d", orderNumber);
			String customTopic = "Запрос " + userForm.getStringValue(user_jur_.ORGANIZATION) + " №" + requestOrderNumber + "-Z от "
					+ DAY_FORMATTER.print(DateTime.now());

			LinkPE customLink = LinkPE.newDirectLink("link", "request_email", false);
			customLink.addStaticVariable("order_num", requestOrderNumber);
			customLink.addStaticVariable("action", "post_jur");
			if (hasRegularBoughts) {
				String also = (StringUtils.isBlank(requestOrderNumber)) ? "self" : requestOrderNumber.replaceAll("\\d", "");
				customLink.addStaticVariable("also", also);
			}

			try {
				sendEmail(Arrays.asList(customerEmail.trim(), getVarSingleValue(/*EMAIL_CUSTOM*/EMAIL_JUR).trim()), customTopic, customLink);
			} catch (Exception e) {
				return getEmailSendingErrorResult(e);
			}
		}

		// Generate barcodes
		generateBarcodes(boughts);

		// save as XML
		saveOrderToXMLFile(false, displayOrderNumber, cart.getStringValue(cart_.CURRENCY));

		// Finaly save order to history
		savePurchaseToHisotry(userSession, date, orderNumber, displayOrderNumber);
		removeTempCart();

		cart.setValue(cart_.ORDER_NUM, displayOrderNumber);
		cart.setExtra(IN_PROGRESS, "false");
		cart.setValue(cart_.PROCESSED, (byte) 1);
		getSessionMapper().saveTemporaryItem(cart);

		setCookieVariable(CART_COOKIE, null);

		return getResult("confirm")/*.setVariable("p1", regularLink.serialize()).setVariable("p2", customLink.serialize()).setVariable("p3", link)*/;
	}

	/**
	 * Отправка заказа для физического лица
	 * @return
	 * @throws Exception
	 */
	public ResultPE postPhys() throws Exception {
		cart = getSessionMapper().getSingleRootItemByName(CART);
		if (StringUtils.equalsIgnoreCase(cart.getStringExtra(IN_PROGRESS), "true") || cart.getByteValue(cart_.PROCESSED, (byte) 0) == 1) {
			return getResult("proceed");
		}
		cart.setExtra(IN_PROGRESS, "true");
		getSessionMapper().saveTemporaryItem(cart);

		Item userForm = getItemForm().getItemSingleTransient();
		saveSessionForm(userForm.getTypeName());
		Item userSession = updateSessionUserWithForm();

		List<String> mandatoryNotSet = checkMandatoryPhys();
		if (mandatoryNotSet.size() > 0) {
			return getMandatoryNotSetResult(mandatoryNotSet);
		}

		boolean hasRegularBoughts = false;
		boolean hasZeroBoughts = false;

		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT, cart.getId());

		for (Item bought : boughts) {
			double maxQuantity = bought.getDoubleValue(bought_.QTY_TOTAL, 0d);
			if (maxQuantity > 0)
				hasRegularBoughts = true;
			if (bought.getDoubleValue(bought_.QTY_ZERO) > 0)
				hasZeroBoughts = true;
			if (hasRegularBoughts && hasZeroBoughts)
				break;
		}

		if (!checkMinSum(false) && hasRegularBoughts) {
			return getSumTooSmallResult();
		}

		int orderNumber = incrementAndGetOrderNumber();
		String displayOrderNumber = createDisplayOrderNumber(orderNumber);
		DateTime date = DateTime.now();

		boolean hasCustomBougths = !getSessionMapper().getItemsByParamValue(CUSTOM_BOUGHT, custom_bought_.NONEMPTY, "true").isEmpty();

		final String customerEmail = userSession.getStringValue(EMAIL_PARAM);

		LinkPE regularLink = LinkPE.parseLink("");
		if (hasRegularBoughts) {
			String regularTopic = "Заказ " + userForm.getStringValue(user_phys_.SECOND_NAME) + " №" + displayOrderNumber + " от "
					+ DAY_FORMATTER.print(DateTime.now());

			regularLink = LinkPE.newDirectLink("link", "order_email", false);
			regularLink.addStaticVariable("order_num", displayOrderNumber);
			regularLink.addStaticVariable("action", "post_phys");
			if (hasZeroBoughts || hasCustomBougths) {
				regularLink.addStaticVariable("also", "Z");
			}

			List<String> emails = new ArrayList<String>() {{ add(customerEmail.trim()); }};

			// shop email
			needPost = StringUtils.contains(userSession.getStringValue(user_.GET_ORDER_FROM, ""), "почтой");
			boolean isGetFromB = StringUtils.contains(userForm.getStringValue(user_.GET_ORDER_FROM), "Беды");

			/*
			final String ORDER_B_EMAIL = getVarSingleValue(EMAIL_B);
			final String ORDER_S_EMAIL = getVarSingleValue(EMAIL_S);
			final String ORDER_POST_EMAIL = getVarSingleValue(EMAIL_P);

			String regEmail = ORDER_S_EMAIL;
			if (isGetFromB)
				regEmail = ORDER_B_EMAIL;
			else if (needPost)
				regEmail = ORDER_POST_EMAIL;
			emails.add(regEmail);
			 */
			emails.add(getVarSingleValue(EMAIL_PHYS));


			try {
				sendEmail(emails, regularTopic, regularLink);
			} catch (Exception e) {
				return getEmailSendingErrorResult(e);
			}
		}

		if (hasZeroBoughts || hasCustomBougths) {
			String requestOrderNumber = String.format("%05d", orderNumber);
			String customTopic = "Запрос " + userForm.getStringValue(user_phys_.SECOND_NAME) + " №" + requestOrderNumber + "-Z от "
					+ DAY_FORMATTER.print(DateTime.now());

			LinkPE customLink = LinkPE.newDirectLink("link", "request_email", false);
			customLink.addStaticVariable("order_num", requestOrderNumber);
			customLink.addStaticVariable("action", "post_phys");
			if (hasRegularBoughts) {
				String also = (StringUtils.isBlank(requestOrderNumber)) ? "self" : requestOrderNumber.replaceAll("\\d", "");
				customLink.addStaticVariable("also", also);
			}

			try {
				sendEmail(Arrays.asList(customerEmail.trim(), getVarSingleValue(/*EMAIL_CUSTOM*/EMAIL_JUR).trim()), customTopic, customLink);
			} catch (Exception e) {
				return getEmailSendingErrorResult(e);
			}
		}

		// Generate barcodes
		generateBarcodes(boughts);

		// Generate XML file

		saveOrderToXMLFile(true, displayOrderNumber, cart.getStringValue(cart_.CURRENCY));

		// Finaly save order to history
		savePurchaseToHisotry(userSession, date, orderNumber, displayOrderNumber);
		removeTempCart();

		cart.setValue(cart_.ORDER_NUM, displayOrderNumber);
		cart.setExtra(IN_PROGRESS, "false");
		cart.setValue(cart_.PROCESSED, (byte) 1);
		getSessionMapper().saveTemporaryItem(cart);

		setCookieVariable(CART_COOKIE, null);

		return getResult("confirm");
	}

	/**
	 * Удалить корзину
	 * @return
	 */
	public ResultPE removeCart() throws Exception {
		getSessionMapper().removeItems(CART);
		removeTempCart();
		return null;
	}

	/**
	 * Удалить сохраненную корзину
	 * @throws Exception
	 */
	public void removeTempCart() throws Exception {
		if (isRegistered()) {
			Item user = loadUserInfo(getInitiator());
			if (user != null) {
				user.clearValue(user_.BOUGHTS_SERIALIZED);
				user.clearValue(user_.CUSTOM_BOUGHTS_SERIALIZED);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(user).ignoreUser());
			}
		}
	}

	/**
	 * Добавить значение к куки избранного и сохранить этот куки в айтеме пользователя
	 * @return
	 * @throws EcommanderException
	 */
	public ResultPE addFav() throws Exception {
		editFav(true);
		return getResult("cookie");
	}

	/**
	 * Удалить значение из куки избранного и сохранить этот куки в айтеме пользователя
	 * @return
	 * @throws Exception
	 */
	public ResultPE removeFav() throws Exception {
		editFav(false);
		return getResult("cookie");
	}
}
