package ecommander.extra;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.EmailUtils;
import ecommander.common.ServerLogger;
import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.ItemHttpPostForm;
import ecommander.pages.elements.ItemVariablesContainer;
import ecommander.pages.elements.ItemVariablesContainer.ItemVariables;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.PagePE;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;
import ecommander.users.UserMapper;

/**
 * Добавляет или удаляет связь предприятия с разделом каталога
 * @author EEEE
 *
 */
public class CartManageCommand extends Command {

	private static final double SUM_1 = 150;
	private static final double SUM_2 = 550;
	private static final int DISCOUNT_1 = 5;
	private static final int DISCOUNT_2 = 10;
	private static final int CUSTOM_BOUGHT_COUNT = 10;
	
	private static final String BOUGHT_ITEM = "bought";
	private static final String CUSTOM_BOUGHT_ITEM = "custom_bought";
	private static final String CART_ITEM = "cart";
	private static final String PRODUCT_ITEM = "product";
	private static final String COUNTER_ITEM = "counter";
	private static final String QUANTITY_PARAM = "quantity";
	private static final String ZERO_QUANTITY_PARAM = "zero_quantity";
	private static final String CUSTOM_QUANTITY_PARAM = "custom_quantity";
	private static final String PRICE_PARAM = "price";
	private static final String QTY_PARAM = "qty";
	private static final String MIN_QTY_PARAM = "min_qty";
	private static final String NEW_QUANTITY_PARAM = "new_quantity";
	private static final String SUM_PARAM = "sum";
	//private static final String DATE_PARAM = "date";
	private static final String COUNT_PARAM = "count";
	private static final String CODE_PARAM = "code";


	
	private static final String ACTION_PARAM = "action";
	private static final String PRODUCT_PARAM = "product";
	private static final String PROCESSED_PARAM = "processed";
	private static final String BARCODE_PARAM = "barcode";
	private static final String POSITTION_PARAM = "position";
	private static final String NONEMPTY_PARAM = "nonempty";
	//private static final String USER_MESSAGE_PARAM = "user_message";

	
	private static final String MESSAGE_PARAM = "user_message";
	
	private static final String SECOND_NAME_PARAM = "second_name";
	private static final String NAME_PARAM = "name";
	private static final String S_NAME_PARAM = "s_name";
	private static final String PHONE_PARAM = "phone";
	private static final String EMAIL_PARAM = "email";
	private static final String POST_CITY_PARAM = "post_city";
	private static final String IF_ABSENT_PARAM = "if_absent";
	private static final String GET_ORDER_FROM_PARAM = "get_order_from";
	
	private static final String ORGANIZATION_PARAM = "organization";
	private static final String ADDRESS_PARAM = "address";
	private static final String JUR_PHONE_PARAM = "jur_phone";
	private static final String JUR_EMAIL_PARAM = "jur_email";
	private static final String NO_ACCOUNT_PARAM = "no_account";
	private static final String ACCOUNT_PARAM = "account";
	private static final String BANK_PARAM = "bank";
	private static final String BANK_ADDRESS_PARAM = "bank_address";
	private static final String BANK_CODE_PARAM = "bank_code";
	private static final String UNP_PARAM = "unp";
	private static final String DIRECTOR_PARAM = "director";
	private static final String BASE_PARAM = "base";
	private static final String NEED_POST_ADDRESS_PARAM = "need_post_address";
	private static final String POST_ADDRESS_PARAM = "post_address";
	private static final String POST_INDEX_PARAM = "post_index";
	private static final String JUR_NEED_POST_ADDRESS_PARAM = "jur_need_post_address";
	private static final String JUR_POST_ADDRESS_PARAM = "jur_post_address";
	private static final String JUR_POST_INDEX_PARAM = "jur_post_index";
	private static final String JUR_POST_CITY_PARAM = "jur_post_city";
	private static final String CONTACT_NAME_PARAM = "contact_name";
	private static final String CONTACT_PHONE_PARAM = "contact_phone";
	private static final String BASE_NUMBER_PARAM = "base_number";
	private static final String BASE_DATE_PARAM = "base_date";
	private static final String JUR_AIM_PARAM = "jur_aim";
	private static final String JUR_FUND_PARAM = "jur_fund";
	
	private static final String LOGIN_PARAM = "login";
	private static final String PASSWORD_PARAM = "password";

	private static final String JUR_ITEM = "register_jur";
	private static final String PHYS_ITEM = "register_phys";
	
	private static final String BARCODE_DIR = "barcodes/";
	private static final String PNG_EXT = ".png";
	private static final String FALSE_VALUE = "false";
	private static final String STATE_VALUE = "Устава";
	private static final String YES_VALUE = "да";
	
	private static final String CART_COOKIE = "cart_cookie";
	
	private static final String EMAIL_B = "email_b";
	private static final String EMAIL_S = "email_s";
	private static final String EMAIL_CUSTOM = "email_custom";
	
	private static final HashSet<String> JUR_MANDATORY = new HashSet<String>();
	private static final HashSet<String> JUR_BASE_MANDATORY = new HashSet<String>();
	private static final HashSet<String> JUR_NO_ACCOUNT_MANDATORY = new HashSet<String>();
	private static final HashSet<String> PHYS_MANDATORY = new HashSet<String>();
	private static final HashSet<String> PHYS_ADDRESS_MANDATORY = new HashSet<String>();
	private static final HashSet<String> JUR_ADDRESS_MANDATORY = new HashSet<String>();
	private static final ArrayList<String> CUSTOM_BOUGHT_PARAMS = new ArrayList<String>();
	static {
		JUR_MANDATORY.add(ORGANIZATION_PARAM);
		JUR_MANDATORY.add(JUR_PHONE_PARAM);
		JUR_MANDATORY.add(JUR_EMAIL_PARAM);
		JUR_MANDATORY.add(CONTACT_NAME_PARAM);
		JUR_MANDATORY.add(CONTACT_PHONE_PARAM);
		JUR_MANDATORY.add(ADDRESS_PARAM);
		JUR_MANDATORY.add(UNP_PARAM);
		JUR_MANDATORY.add(DIRECTOR_PARAM);
		JUR_MANDATORY.add(BASE_PARAM);
		JUR_MANDATORY.add(JUR_AIM_PARAM);
		JUR_MANDATORY.add(JUR_FUND_PARAM);
		
		JUR_BASE_MANDATORY.add(BASE_NUMBER_PARAM);
		JUR_BASE_MANDATORY.add(BASE_DATE_PARAM);
		
		JUR_NO_ACCOUNT_MANDATORY.add(ACCOUNT_PARAM);
		JUR_NO_ACCOUNT_MANDATORY.add(BANK_PARAM);
		JUR_NO_ACCOUNT_MANDATORY.add(BANK_CODE_PARAM);
		JUR_NO_ACCOUNT_MANDATORY.add(BANK_ADDRESS_PARAM);
		
		PHYS_MANDATORY.add(SECOND_NAME_PARAM);
		PHYS_MANDATORY.add(PHONE_PARAM);
		PHYS_MANDATORY.add(EMAIL_PARAM);
		PHYS_MANDATORY.add(IF_ABSENT_PARAM);
		PHYS_MANDATORY.add(GET_ORDER_FROM_PARAM);
		
		PHYS_ADDRESS_MANDATORY.add(POST_ADDRESS_PARAM);
		PHYS_ADDRESS_MANDATORY.add(POST_INDEX_PARAM);
		PHYS_ADDRESS_MANDATORY.add(POST_CITY_PARAM);
		PHYS_ADDRESS_MANDATORY.add(S_NAME_PARAM);
		PHYS_ADDRESS_MANDATORY.add(NAME_PARAM);

		JUR_ADDRESS_MANDATORY.add(JUR_POST_ADDRESS_PARAM);
		JUR_ADDRESS_MANDATORY.add(JUR_POST_INDEX_PARAM);
		JUR_ADDRESS_MANDATORY.add(JUR_POST_CITY_PARAM);
		
		CUSTOM_BOUGHT_PARAMS.add("mark");
		CUSTOM_BOUGHT_PARAMS.add("type");
		CUSTOM_BOUGHT_PARAMS.add("case");
		CUSTOM_BOUGHT_PARAMS.add("qty");
		CUSTOM_BOUGHT_PARAMS.add("link");
		CUSTOM_BOUGHT_PARAMS.add("extra");
		CUSTOM_BOUGHT_PARAMS.add(NONEMPTY_PARAM);
	}
	
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private Item cart = null;
	
	@Override
	public ResultPE execute() throws Exception {
		String action = getVarSingleValue(ACTION_PARAM);
		createOrLoadCart();
		//cart.setParameterDirect(USER_MESSAGE_PARAM, "");
		if ("ajax".equalsIgnoreCase(action)) {
			long productId = Long.parseLong(getVarSingleValue(PRODUCT_PARAM));
			double quantity = 0;
			try {
				quantity = DoubleDataType.parse(getVarSingleValue(QUANTITY_PARAM));
			} catch (Exception e) {/**/}
			addProduct(productId, quantity);
			recalculateCart();
			return getResult("cart_ajax");
		} else if ("delete".equalsIgnoreCase(action)) {
			// В этом случае ID не самого продукта, а объекта bought
			long productId = Long.parseLong((String)getVarSingleValue(PRODUCT_PARAM));
			getSessionMapper().removeItems(productId);
			recalculateCart();
			return getResult("cart");
		} else if ("recalculate".equalsIgnoreCase(action)) {
			recalculate();
			return getResult("cart");
		} else if ("clear".equalsIgnoreCase(action)) {
			getSessionMapper().removeItems(cart.getId());
			saveCookie();
			return getResult("cart");
		} else if ("proceed".equalsIgnoreCase(action)) {
			if (recalculate())
				return getResult("post");
			return getResult("cart");
		} else if ("login".equalsIgnoreCase(action)) {
			ItemHttpPostForm form = getItemForm();
			User user = UserMapper.getUser((String) form.getValueStr(LOGIN_PARAM),
					(String) form.getValueStr(PASSWORD_PARAM));
			if (user == null)
				return getResult("login_error");
			List<Item> register = ItemQuery.newItemQuery(JUR_ITEM).setUser(user).loadItems();
			register.addAll(ItemQuery.newItemQuery(PHYS_ITEM).setUser(user).loadItems());
			if (register.size() == 0)
				return getResult("post");
			Item reg = register.get(0);
			Item contacts = getSessionMapper().createSessionRootItem("cart_contacts");
			if (reg.getTypeName().equals(JUR_ITEM)) {
				contacts.setValue(ORGANIZATION_PARAM, reg.getValue(ORGANIZATION_PARAM));
				contacts.setValue(JUR_PHONE_PARAM, reg.getValue(PHONE_PARAM));
				contacts.setValue(JUR_EMAIL_PARAM, reg.getValue(EMAIL_PARAM));
				contacts.setValue(CONTACT_NAME_PARAM, reg.getValue(CONTACT_NAME_PARAM));
				contacts.setValue(CONTACT_PHONE_PARAM, reg.getValue(CONTACT_PHONE_PARAM));
				contacts.setValue(ADDRESS_PARAM, reg.getValue(ADDRESS_PARAM));
				contacts.setValue(NO_ACCOUNT_PARAM, reg.getValue(NO_ACCOUNT_PARAM));
				contacts.setValue(ACCOUNT_PARAM, reg.getValue(ACCOUNT_PARAM));
				contacts.setValue(BANK_PARAM, reg.getValue(BANK_PARAM));
				contacts.setValue(BANK_ADDRESS_PARAM, reg.getValue(BANK_ADDRESS_PARAM));
				contacts.setValue(BANK_CODE_PARAM, reg.getValue(BANK_CODE_PARAM));
				contacts.setValue(UNP_PARAM, reg.getValue(UNP_PARAM));
				contacts.setValue(DIRECTOR_PARAM, reg.getValue(DIRECTOR_PARAM));
				contacts.setValue(BASE_PARAM, reg.getValue(BASE_PARAM));
				contacts.setValue(JUR_POST_ADDRESS_PARAM, reg.getValue(POST_ADDRESS_PARAM));
				contacts.setValue(BASE_DATE_PARAM, reg.getValue(BASE_DATE_PARAM));
				contacts.setValue(BASE_NUMBER_PARAM, reg.getValue(BASE_NUMBER_PARAM));
			} else if (reg.getTypeName().equals(PHYS_ITEM)) {
				contacts.setValue(SECOND_NAME_PARAM, reg.getValue(SECOND_NAME_PARAM));
				contacts.setValue(NAME_PARAM, reg.getValue(NAME_PARAM));
				contacts.setValue(S_NAME_PARAM, reg.getValue(S_NAME_PARAM));
				contacts.setValue(PHONE_PARAM, reg.getValue(PHONE_PARAM));
				contacts.setValue(EMAIL_PARAM, reg.getValue(EMAIL_PARAM));
				contacts.setValue(POST_ADDRESS_PARAM, reg.getValue(POST_ADDRESS_PARAM));
				contacts.setValue(POST_INDEX_PARAM, reg.getValue(POST_INDEX_PARAM));
				contacts.setValue(POST_CITY_PARAM, reg.getValue(POST_CITY_PARAM));
				contacts.setValue(POST_INDEX_PARAM, reg.getValue(POST_INDEX_PARAM));
			}
			getSessionMapper().removeItems("cart_contacts");
			getSessionMapper().saveTemporaryItem(contacts);
			// Залогинить пользователя
			startUserSession(user);
			return getResult("post");
		} else if (action.startsWith("post")) {
			final String IN_PROGRESS = "in_progress";
			final String TRUE = "true";
			final String FALSE = "false";
			if (StringUtils.equalsIgnoreCase(cart.getExtra(IN_PROGRESS), TRUE)) {
				return getResult("success");
			}
			cart.setExtra(IN_PROGRESS, TRUE);
			getSessionMapper().saveTemporaryItem(cart);
			if ((Byte)cart.getValue(PROCESSED_PARAM, (byte)0) == (byte)1) {
				return getResult("success");
			}
			ItemHttpPostForm form = getItemForm();
			saveSessionForm();
			Item contacts = getSessionMapper().getSingleRootItemByName("cart_contacts");
			if (contacts == null)
				contacts = getSessionMapper().createSessionRootItem("cart_contacts");
			ItemHttpPostForm.editExistingItem(form, contacts, NEED_POST_ADDRESS_PARAM, JUR_NEED_POST_ADDRESS_PARAM);
			boolean lessThan20 = false;
			boolean needPost = false;
			if ("post_jur".equalsIgnoreCase(action)) {
				ArrayList<String> mandatoryNotSet = new ArrayList<String>();
				for (String param : JUR_MANDATORY) {
					if (StringUtils.isBlank(form.getValueStr(param)))
						mandatoryNotSet.add(param);
				}
				if (!form.getValueStr(BASE_PARAM).equals(STATE_VALUE)) {
					for (String param : JUR_BASE_MANDATORY) {
						if (StringUtils.isBlank(form.getValueStr(param)))
							mandatoryNotSet.add(param);
					}
				}
				if (!YES_VALUE.equals(form.getValueStr(NO_ACCOUNT_PARAM))) {
					for (String param : JUR_NO_ACCOUNT_MANDATORY) {
						if (StringUtils.isBlank(form.getValueStr(param)))
							mandatoryNotSet.add(param);
					}
				}
				if (YES_VALUE.equals(form.getValueStr(JUR_NEED_POST_ADDRESS_PARAM))) {
					for (String param : JUR_ADDRESS_MANDATORY) {
						if (StringUtils.isBlank(form.getValueStr(param)))
							mandatoryNotSet.add(param);
					}
					needPost = true;
				}
				if (mandatoryNotSet.size() > 0) {
					contacts.setValue(MESSAGE_PARAM, "Заполните, пожалуйста, обязательные поля");
					getSessionMapper().saveTemporaryItem(contacts);
					ResultPE result = getResult("post");
					for (String param : mandatoryNotSet) {
						result.addVariable("not_set", param);
					}
					cart.setExtra(IN_PROGRESS, FALSE);
					getSessionMapper().saveTemporaryItem(cart);
					return result;
				}
				if (cart.getDoubleValue(SUM_PARAM) < 20) {
					lessThan20 = true;
				}
			} else if ("post_phys".equalsIgnoreCase(action)) {
				ArrayList<String> mandatoryNotSet = new ArrayList<String>();
				for (String param : PHYS_MANDATORY) {
					if (StringUtils.isBlank(form.getValueStr(param)))
						mandatoryNotSet.add(param);
				}
				if (YES_VALUE.equals(form.getValueStr(NEED_POST_ADDRESS_PARAM))) {
					for (String param : PHYS_ADDRESS_MANDATORY) {
						if (StringUtils.isBlank(form.getValueStr(param)))
							mandatoryNotSet.add(param);
					}
					mandatoryNotSet.remove(GET_ORDER_FROM_PARAM);
					needPost = true;
				}
				if (mandatoryNotSet.size() > 0) {
					contacts.setValue(MESSAGE_PARAM, "Заполните, пожалуйста, обязательные поля");
					getSessionMapper().saveTemporaryItem(contacts);
					ResultPE result = getResult("post");
					for (String param : mandatoryNotSet) {
						result.addVariable("not_set", param);
					}
					cart.setExtra(IN_PROGRESS, FALSE);
					getSessionMapper().saveTemporaryItem(cart);
					return result;
				}
			}
			
			getSessionMapper().saveTemporaryItem(contacts);
			
			// Проверка, есть ли обычные заказы, заказы с количеством 0 и кастомные заказы
			boolean hasRegularBoughts = false;
			boolean hasZeroBoughts = false;
			boolean hasCustomBoughts = false;
			boolean isPhys = "post_phys".equalsIgnoreCase(action);
			boolean isJur = "post_jur".equalsIgnoreCase(action);
			boolean isGetFromB = StringUtils.contains(form.getValueStr(GET_ORDER_FROM_PARAM), "Беды");
//			boolean isGetFromS = StringUtils.contains(form.getValueStr(GET_ORDER_FROM_PARAM), "Скрыганова");
//			boolean isShipping = StringUtils.equalsIgnoreCase(form.getValueStr(NEED_POST_ADDRESS_PARAM), YES_VALUE);
			ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
			for (Item bought : boughts) {
				Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
				double maxQuantity = product.getDoubleValue(QTY_PARAM, 0d);
				if (maxQuantity > 0)
					hasRegularBoughts = true;
				else
					hasZeroBoughts = true;
			}
			ArrayList<Item> customBoughts = getSessionMapper().getItemsByName(CUSTOM_BOUGHT_ITEM, cart.getId());
			for (Item bought : customBoughts) {
				if (StringUtils.equals("true", bought.getStringValue(NONEMPTY_PARAM))) {
					hasCustomBoughts = true;
					break;
				}
			}
			if (lessThan20 && hasRegularBoughts) {
				contacts.setValue(MESSAGE_PARAM, "Минимальная сумма заказа для юридических лиц - 20 руб. Резерв не отправлен.");
				getSessionMapper().saveTemporaryItem(contacts);
				cart.setExtra(IN_PROGRESS, FALSE);
				getSessionMapper().saveTemporaryItem(cart);
				return getResult("post");
			}
			
			// Загрузка и модификация счетчика
			Item counter = ItemQuery.newItemQuery(COUNTER_ITEM).loadItems().get(0);
			int count = counter.getIntValue(COUNT_PARAM) + 1;
			if (count > 99999)
				count = 1;
			String orderNumber = String.format("%05d", count);
//			String date = counter.getStringValue(DATE_PARAM);
//			String newDate = DATE_FORMAT.format(new Date());
//			if (!newDate.equals(date))
//				count = 1;
//			String orderNumber = count + "-" + newDate;
			cart.setValue("order_num", orderNumber);
			
			// Подготовка тела письма
			String regularTopic 
				= "Заказ " + (isPhys ? form.getValueStr(SECOND_NAME_PARAM) : form.getValueStr(ORGANIZATION_PARAM))
				+ " №" + orderNumber + " от " + DATE_FORMAT.format(new Date());
			String customTopic 
				= "Запрос " + (isPhys ? form.getValueStr(SECOND_NAME_PARAM) : form.getValueStr(ORGANIZATION_PARAM))
				+ " №" + orderNumber + " от " + DATE_FORMAT.format(new Date());
//			StringBuilder regularTopic = new StringBuilder(getVarSingleValue("topic"));
//			StringBuilder customTopic = new StringBuilder(getVarSingleValue("topic"));
			Multipart regularMP = new MimeMultipart();
			Multipart customMP = new MimeMultipart();
			MimeBodyPart regularTextPart = new MimeBodyPart();
			MimeBodyPart customTextPart = new MimeBodyPart();
			regularMP.addBodyPart(regularTextPart);
			customMP.addBodyPart(customTextPart);
			LinkPE regularLink = LinkPE.newDirectLink("link", "order_email", false);
			regularLink.addStaticVariable("order_num", orderNumber + "");
			regularLink.addStaticVariable("action", action);
			ExecutablePagePE regularTemplate = getExecutablePage(regularLink.serialize());
			LinkPE customLink = LinkPE.newDirectLink("link", "request_email", false);
			customLink.addStaticVariable("order_num", orderNumber + "");
			customLink.addStaticVariable("action", action);
			ExecutablePagePE customTemplate = getExecutablePage(customLink.serialize());
			String customerEmail = null;
			if ("post_phys".equalsIgnoreCase(action)) {
				customerEmail = (String) form.getValueStr(EMAIL_PARAM);
//				regularTopic.append(' ').append(contacts.getStringValue(SECOND_NAME_PARAM));
//				customTopic.append(' ').append(contacts.getStringValue(SECOND_NAME_PARAM));
			} else {
				customerEmail = (String) form.getValueStr(JUR_EMAIL_PARAM);
//				regularTopic.append(' ').append(contacts.getStringValue(ORGANIZATION_PARAM));
//				customTopic.append(' ').append(contacts.getStringValue(ORGANIZATION_PARAM));
			}
//			String regularShopEmail = getVarSingleValue("email");
//			String customShopEmail = getVarSingleValue("custom_email");
			final String ORDER_B_EMAIL = getVarSingleValue(EMAIL_B);
			final String ORDER_S_EMAIL = getVarSingleValue(EMAIL_S);
			final String ORDER_CUSTOM_EMAIL = getVarSingleValue(EMAIL_CUSTOM);
			final String ZAKAZ_2 = "zakaz2@belchip.by";
			
			ByteArrayOutputStream regularBos = new ByteArrayOutputStream();
			ByteArrayOutputStream customBos = new ByteArrayOutputStream();
			if (hasRegularBoughts) {
				PageController.newSimple().executePage(regularTemplate, regularBos);
				regularTextPart.setContent(regularBos.toString("UTF-8"), regularTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
						+ ";charset=UTF-8");
			}
			if (hasZeroBoughts || hasCustomBoughts) {
				PageController.newSimple().executePage(customTemplate, customBos);
				customTextPart.setContent(customBos.toString("UTF-8"), customTemplate.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER)
						+ ";charset=UTF-8");
			}
			// Генерация файлов со штрихкодами
			for (Item bought : boughts) {
				Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
				String filePath = AppContext.getFilesDirPath() + BARCODE_DIR;
				String fileName = product.getStringValue(CODE_PARAM) + PNG_EXT;
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
						ServerLogger.error("Cannot generate barcode for " + product.getStringValue(BARCODE_PARAM, "none"), e);
						// Ничего не делать, отправлять без штрихкода
					}
				}
			}
			// Отправка на ящик заказчика
			try {
				if (hasRegularBoughts)
					EmailUtils.sendGmailDefault(customerEmail, /*regularTopic.toString()*/regularTopic, regularMP);
				if (hasZeroBoughts || hasCustomBoughts)
					EmailUtils.sendGmailDefault(customerEmail, /*customTopic.toString()*/customTopic, customMP);
			} catch (Exception e) {
				ServerLogger.error("Unable to send email", e);
				contacts.setValue(MESSAGE_PARAM, "На предоставленный вами e-mail невозможна отправка письма. Резерв не обработан");
				getSessionMapper().saveTemporaryItem(contacts);
				cart.setExtra(IN_PROGRESS, FALSE);
				getSessionMapper().saveTemporaryItem(cart);
				return getResult("post");
			}
			// Отправка на ящик магазина
			try {
				if (hasRegularBoughts) {
					String regEmail = ORDER_S_EMAIL;
					if (isGetFromB)
						regEmail = ORDER_B_EMAIL;
					if (isJur)
						regEmail = ORDER_CUSTOM_EMAIL;
					if (needPost)
						regEmail = ZAKAZ_2;
					EmailUtils.sendGmailDefault(regEmail, /*regularTopic.toString()*/regularTopic, regularMP);
				}
				if (hasZeroBoughts || hasCustomBoughts) {
					EmailUtils.sendGmailDefault(ORDER_CUSTOM_EMAIL, /*customTopic.toString()*/customTopic, customMP);
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to send email", e);
				contacts.setValue(MESSAGE_PARAM, "Отправка резерва временно недоступна, попробуйте позже или звоните по телефону");
				getSessionMapper().saveTemporaryItem(contacts);
				cart.setExtra(IN_PROGRESS, FALSE);
				getSessionMapper().saveTemporaryItem(cart);
				return getResult("post");
			}
			// Сохранение файлов заказа
			String jurFolder = AppContext.getRealPath("WEB-INF/" + getVarSingleValueDefault("jur_folder", "jur"));
			String physFolder = AppContext.getRealPath("WEB-INF/" + getVarSingleValueDefault("phys_folder", "phys"));
			File jurDir = new File(jurFolder);
			File physDir = new File(physFolder);
			if (!jurDir.exists())
				jurDir.mkdirs();
			if (!physDir.exists())
				physDir.mkdirs();
			LinkPE orderFileLink = LinkPE.newDirectLink("link", "order_file", false);
			orderFileLink.addStaticVariable("order_num", orderNumber + "");
			ExecutablePagePE orderFileTemplate = getExecutablePage(orderFileLink.serialize());			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PageController.newSimple().executePage(orderFileTemplate, out);
			File file;
			if (isJur) {
				file = new File(jurFolder + "/" + orderNumber + "_" + form.getValueStr(ORGANIZATION_PARAM) + ".xml");
			} else {
				file = new File(physFolder + "/" + orderNumber + "_" + form.getValueStr(SECOND_NAME_PARAM) + ".xml");
			}
			FileUtils.writeByteArrayToFile(file, out.toByteArray());
			
			// Сохранение нового значения счетчика, если все отправлено удачно
			counter.setValue(COUNT_PARAM, count);
			//counter.setValue(DATE_PARAM, newDate);
			executeAndCommitCommandUnits(new UpdateItemDBUnit(counter).ignoreUser(true));
			
			cart.setValue(PROCESSED_PARAM, (byte)1);
			cart.setExtra(IN_PROGRESS, FALSE);
			setCookieVariable(CART_COOKIE, null);
			getSessionMapper().saveTemporaryItem(cart);
			return getResult("success");
		} else if ("lost_password".equalsIgnoreCase(action)) {
			String email = getVarSingleValue("email");
			ArrayList<Item> regs = new ArrayList<Item>();
			regs.addAll(ItemQuery.loadByParamValue(ItemNames.REGISTER_JUR._ITEM_NAME, ItemNames.REGISTER_JUR.EMAIL, email));
			regs.addAll(ItemQuery.loadByParamValue(ItemNames.REGISTER_PHYS._ITEM_NAME, ItemNames.REGISTER_PHYS.EMAIL, email));
			if (regs.size() <= 0)
				return getResult("lost_password_error");
			Multipart mp = new MimeMultipart();
			MimeBodyPart textPart = new MimeBodyPart();
			mp.addBodyPart(textPart);
			Item reg = regs.get(0);
			textPart.setContent("Логин: " + reg.getStringValue("login") + "\nПароль: " + reg.getStringValue("password"),
					"text/plain;charset=UTF-8");
			try {
				EmailUtils.sendGmailDefault(email, "Восстановление пароля на belchip.by", mp);
			} catch (Exception e) {
				return getResult("lost_password_error");
			}
			return getResult("lost_password_success");
		}
		return getResult("cart_ajax");
	}
	
	private double round(double qty, double min_qty) {
		if (qty > 0 && qty < min_qty)
			return min_qty;
		double quotient = Math.ceil(qty / min_qty);
		return min_qty * quotient;
	}
	/**
	 * Добавить товар в корзину
	 * @param itemId
	 * @param qty
	 * @throws Exception 
	 */
	private void addProduct(long itemId, double qty) throws Exception {
		createOrLoadCart();
		// Если корзина уже была отправлена, создать ее заново
		Byte processed = cart.getByteValue(PROCESSED_PARAM);
		if (processed != null && processed == 1) {
			getSessionMapper().removeItems(cart.getId());
			cart = getSessionMapper().createSessionRootItem(CART_ITEM);
			getSessionMapper().saveTemporaryItem(cart);
		}
		// Проверка, есть ли уже такой девайс в корзине (если есть, изменить количество)
		Item boughtProduct = getSessionMapper().getItem(itemId, PRODUCT_ITEM);
		if (boughtProduct == null) {
			if (qty <= 0)
				return;			
			Item product = ItemQuery.loadById(itemId);
			Item section = ItemQuery.loadById(product.getDirectParentId());
			Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			double maxQuantity = product.getDoubleValue(QTY_PARAM, 0d);
			if (maxQuantity > 0) 
				qty = maxQuantity > qty ? qty : maxQuantity;
			qty = round(qty, product.getDoubleValue(MIN_QTY_PARAM, (double)1));
			bought.setValue(QUANTITY_PARAM, qty);
			if (maxQuantity > 0) {
				bought.setValue(IConst.LIMIT_1_PARAM, section.getValue(IConst.LIMIT_1_PARAM));
				bought.setValue(IConst.LIMIT_2_PARAM, section.getValue(IConst.LIMIT_2_PARAM));
				bought.setValue(IConst.DISCOUNT_1_PARAM, section.getValue(IConst.DISCOUNT_1_PARAM));
				bought.setValue(IConst.DISCOUNT_2_PARAM, section.getValue(IConst.DISCOUNT_2_PARAM));
			}
			bought.setValue(IConst.TYPE_PARAM, product.getItemType().getCaption());
			// Сохраняется bought
			getSessionMapper().saveTemporaryItem(bought);
			// Сохраняется девайс
			product.setDirectParentId(bought.getId());
			getSessionMapper().saveTemporaryItem(product, PRODUCT_ITEM);
		} else {
			Item bought = getSessionMapper().getItem(boughtProduct.getDirectParentId(), BOUGHT_ITEM);
			if (qty <= 0) {
				getSessionMapper().removeItems(bought.getId());
				return;
			}
			double maxQuantity = boughtProduct.getDoubleValue(QTY_PARAM, 0d);
			if (maxQuantity > 0) 
				qty = maxQuantity > qty ? qty : maxQuantity;
			qty = round(qty, boughtProduct.getDoubleValue(MIN_QTY_PARAM, (double)1));
			bought.setValue(QUANTITY_PARAM, qty);
			getSessionMapper().saveTemporaryItem(bought);
		}
	}
	/**
	 * Проанализировать ввод пользователя и пересчитать все в корзине
	 * @throws Exception
	 */
	private boolean recalculate() throws Exception {
		ItemVariablesContainer varContainer = getItemVariables();
		boolean result = true;
		boolean hasCustom = false;
		int customBoughtItemId = ItemTypeRegistry.getItemTypeId(CUSTOM_BOUGHT_ITEM);
		// Обновление custom_bought
		// Сброс старых данных
		ArrayList<Item> allCustom = getSessionMapper().getItemsByName(CUSTOM_BOUGHT_ITEM, cart.getId());
		for (Item custom : allCustom) {
			for (String paramName : CUSTOM_BOUGHT_PARAMS) {
				custom.removeValue(paramName);
			}
			getSessionMapper().saveTemporaryItem(custom);
		}
		// Обновление параметров
		for (ItemVariables itemPost : varContainer.getItemPosts()) {
			if (itemPost.getValue(NEW_QUANTITY_PARAM) != null) {
				Item bought = getSessionMapper().getItem(itemPost.getItemId(), BOUGHT_ITEM);
				double quantity = -1;
				try {
					quantity = DoubleDataType.parse(itemPost.getValue(NEW_QUANTITY_PARAM));
				} catch (NumberFormatException e) { /**/ }
				if (quantity > 0) {
					Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
					double maxQuantity = product.getDoubleValue(QTY_PARAM, 0d);
					if (maxQuantity > 0)
						quantity = maxQuantity > quantity ? quantity : maxQuantity;
					quantity = round(quantity, product.getDoubleValue(MIN_QTY_PARAM, (double)1));
					bought.setValue(QUANTITY_PARAM, quantity);
					getSessionMapper().saveTemporaryItem(bought);
				} else {
					getSessionMapper().removeItems(bought.getId());
					result = false;
				}
			}
			else if (itemPost.getItemTypeId() == customBoughtItemId) {
				Item customBought = getSessionMapper().getItem(itemPost.getItemId(), CUSTOM_BOUGHT_ITEM);
				for (String paramName : CUSTOM_BOUGHT_PARAMS) {
					String inputName = "new_" + paramName;
					String value = itemPost.getValue(inputName);
					if (StringUtils.isNotBlank(value)) {
						customBought.setValue(paramName, itemPost.getValue(inputName));
						customBought.setValue(NONEMPTY_PARAM, "true");
						hasCustom = true;
					}
				}
				getSessionMapper().saveTemporaryItem(customBought);
			}
		}
		return (recalculateCart() || hasCustom) && result;
	}
	/**
	 * Пересчитывает данные для одного enterprise_bought, когда в корзине произошли какие-то изменения
	 * @throws Exception
	 */
	private boolean recalculateCart() throws Exception {
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		double sum = 0; // полная сумма 
		double discountSum = 0; // сумма, с которой предоставляется скидка
		double regularQuantity = 0;
		double zeroQuantity = 0;
		double customQuantity = 0;
		boolean result = true;
		
		// Обычные заказы и заказы с нулевым количеством на складе
		
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double maxQuantity = product.getDoubleValue(QTY_PARAM, 0d);
			double quantity = bought.getDoubleValue(QUANTITY_PARAM);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
				result = false;
			} else {
				// Первоначальная сумма
				double productSum = Math.round(product.getDoubleValue(PRICE_PARAM) * quantity * 100) / 100d;
				if (maxQuantity <= 0) {
					productSum = 0d;
					zeroQuantity += quantity;
				} else {
					regularQuantity += quantity;
				}
//				// Применение скидок
//				double limit1 = bought.getDoubleParameterValue(IConst.LIMIT_1_PARAM, 1000000);
//				double limit2 = bought.getDoubleParameterValue(IConst.LIMIT_2_PARAM, 1000000);
//				double discount1 = bought.getIntParameterValue(IConst.DISCOUNT_1_PARAM, 0);
//				double discount2 = bought.getIntParameterValue(IConst.DISCOUNT_2_PARAM, 0);
//				if (quantity >= limit1 && quantity < limit2) {
//					productSum = (long)(productSum * (100 - discount1) / 100);
//				} else if (quantity >= limit2) {
//					productSum = (long)(productSum * (100 - discount2) / 100);
//				}
//				// Округление суммы
//				productSum = Math.round(productSum / 100) * 100;
				bought.setValue(SUM_PARAM, productSum);
				sum += productSum;
				if (product.getStringValue(ItemNames.PRODUCT.SPECIAL_PRICE, FALSE_VALUE).equals(FALSE_VALUE))
					discountSum += productSum;
				// Сохранить bought
				getSessionMapper().saveTemporaryItem(bought);
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
				quotient = (double)(DISCOUNT_1) / (double)100;
			} else if (simpleSum >= SUM_2) {
				discount = DISCOUNT_2;
				quotient = (double)(DISCOUNT_2) / (double)100;
			}
			sum = sum - discountSum * quotient;
		}
		// Округление суммы
		sum = Math.round(sum * 100) / 100d;
		cart.setValue(ItemNames.CART.SIMPLE_SUM, simpleSum);
		cart.setValue(SUM_PARAM, sum);
		cart.setValue(QUANTITY_PARAM, regularQuantity);
		cart.setValue(ZERO_QUANTITY_PARAM, zeroQuantity);
		cart.setValue(CUSTOM_QUANTITY_PARAM, customQuantity);
		cart.setValue(ItemNames.CART.DISCOUNT, discount);
		cart.setValue(ItemNames.CART.MARGIN, simpleSum - sum);
		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		saveCookie();
		return result && regularQuantity > 0;
	}
	/**
	 * Восстановить корзину из куки
	 * @return
	 * @throws Exception
	 */
	public ResultPE restoreFromCookie() throws Exception {
		cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		if (cart != null)
			return null;
		String cookie = getVarSingleValue(CART_COOKIE);
		if (StringUtils.isBlank(cookie))
			return null;
		String[] idQtys = StringUtils.split(cookie, '/');
		for (String idQty : idQtys) {
			String[] pair = StringUtils.split(idQty, ':');
			long itemId = Long.parseLong(pair[0]);
			double qty = DoubleDataType.parse(pair[1]);
			addProduct(itemId, qty);
		}
		recalculateCart();
		return null;
	}
	/**
	 * Загрузить корзину или создать новую корзину
	 * @return
	 * @throws Exception
	 */
	public ResultPE createOrLoadCart() throws Exception {
		if (cart == null)
			cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		if (cart == null) {
			cart = getSessionMapper().createSessionRootItem(CART_ITEM);
			getSessionMapper().saveTemporaryItem(cart);
			// Добавление айтемов для персонального заказа
			for (int i = 0; i < CUSTOM_BOUGHT_COUNT; i++) {
				Item custom = getSessionMapper().createSessionItem(CUSTOM_BOUGHT_ITEM, cart.getId());
				custom.setValue(POSITTION_PARAM, i);
				getSessionMapper().saveTemporaryItem(custom);
			}
		}
		return null;
	}
	/**
	 * Закончить обработку козрины, нужно для того, чтобы можно было вызвать форму
	 * оплаты карточкой
	 * @return
	 * @throws Exception
	 */
	public ResultPE postProcessCart() throws Exception {
//		Item contacts = getSessionMapper().getSingleRootItemByName("cart_contacts");
		Item cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
		if (cart != null)
			getSessionMapper().removeItems(cart.getId());
//		if (contacts != null)
//			getSessionMapper().removeItems(contacts.getId());
		return null;
	}
	
	private void saveCookie() throws Exception {
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		ArrayList<String> idQtys = new ArrayList<String>();
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double quantity = bought.getDoubleValue(QUANTITY_PARAM);
			idQtys.add(product.getId() + ":" + quantity);
		}
		if (idQtys.size() > 0) {
			String cookie = StringUtils.join(idQtys, '/');
			setCookieVariable(CART_COOKIE, cookie);
		} else {
			setCookieVariable(CART_COOKIE, null);
		}
	}
}
