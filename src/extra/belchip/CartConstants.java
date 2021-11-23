package extra.belchip;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.ibm.icu.text.SimpleDateFormat;
import extra._generated.ItemNames;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public interface CartConstants {
	BigDecimal SUM_1 = BigDecimal.valueOf(150);
	BigDecimal SUM_2 = BigDecimal.valueOf(500);
	int DISCOUNT_1 = 5;
	int DISCOUNT_2 = 10;
	int CUSTOM_BOUGHT_COUNT = 10;
	DateTimeFormatter DAY_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy");

	String ASSOC_GENERAL = "general";
	String DEFAULT_CURRENCY = "BYN";
	String RUB_CURRENCY = "RUB";

	String RATE_PART = "_rate";
	String SCALE_PART = "_scale";
	String EXTRA_QUOTIENT_PART = "_extra_quotient";
	String CEIL_PART = "_ceil";

	String QTY_REQ_PARAM = "qty";
	String NEW_QUANTITY_PARAM = "new_quantity";
	//String SUM_PARAM = "sum";
	String COUNT_PARAM = "count";

	String ACTION_PARAM = "action";
	String PRODUCT_PARAM = "prod";
	String PROCESSED_PARAM = "processed";

	String MESSAGE_PARAM = "user_message";

	String IN_PROGRESS = "in_progress";


	String LOGIN_PARAM = "login";
	String PASSWORD_PARAM = "password";

	String BARCODE_DIR = "barcodes/";
	String PNG_EXT = ".png";
	String FALSE_VALUE = "false";
	String STATE_VALUE = "Устава";
	String YES_VALUE = "да";

	String CART_COOKIE = "cart_cookie";

	String EMAIL_B = "email_bedy";
	String EMAIL_S = "email_skrig";
	String EMAIL_P = "email_post";
	String EMAIL_CUSTOM = "email_custom";
	String EMAIL_JUR = "email_jur";
	String EMAIL_PHYS = "email_phys";

	String JUR_LTM = "Извините, минимальная сумма заказа для юридических лиц - %.2f руб. Заказ не отправлен.";
	String PHYS_LTM = "Извините, минимальная сумма заказа для физических лиц - %.2f руб. Заказ не отправлен.";
	String POST_LTM = "Извините, минимальная сумма заказа при отправке почтой - %.2f руб. Заказ не отправлен.";

	Set<String> JUR_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6154731719400877513L;

		{
			add(ItemNames.user_jur_.ORGANIZATION);
			add(ItemNames.user_jur_.PHONE);
			add(ItemNames.user_jur_.EMAIL);
			add(ItemNames.user_jur_.CONTACT_NAME);
			add(ItemNames.user_jur_.CONTACT_PHONE);
			add(ItemNames.user_jur_.ADDRESS);
			add(ItemNames.user_jur_.UNP);
			add(ItemNames.user_jur_.DIRECTOR);
			add(ItemNames.user_jur_.BASE);
			add(ItemNames.user_jur_.FUND);
			add(ItemNames.user_jur_.SEND_CONTRACT_TO);
		}
	};

	Set<String> JUR_BASE_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3973888116594652711L;

		{
			add(ItemNames.user_jur_.BASE_NUMBER);
			add(ItemNames.user_jur_.BASE_DATE);
		}
	};

	Set<String> JUR_NO_ACCOUNT_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6877987850929815731L;

		{
			add(ItemNames.user_jur_.ACCOUNT);
			add(ItemNames.user_jur_.BANK);
			add(ItemNames.user_jur_.BANK_CODE);
			add(ItemNames.user_jur_.BANK_ADDRESS);
		}
	};

	Set<String> PHYS_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5275087566452782267L;

		{
			add(ItemNames.user_phys_.SECOND_NAME);
			add(ItemNames.user_phys_.PHONE);
			add(ItemNames.user_phys_.EMAIL);
			add(ItemNames.user_phys_.IF_ABSENT);
		}
	};
	
	Set<String> PHYS_ADDRESS_MANDATORY = new HashSet<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 6397781611402904275L;

	{
		add(ItemNames.user_.POST_ADDRESS);
		add(ItemNames.user_.POST_INDEX);
		add(ItemNames.user_.POST_CITY);
		add(ItemNames.user_.POST_REGION);
		add(ItemNames.user_phys_.NAME);
	}};
	
	Set<String> JUR_ADDRESS_MANDATORY = new HashSet<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 8517768710126188679L;

	{
		add(ItemNames.user_.POST_ADDRESS);
		add(ItemNames.user_.POST_INDEX);
		add(ItemNames.user_.POST_CITY);
	}};
	
	Set<String> CUSTOM_BOUGHT_PARAMS = new HashSet<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 3003185279225323811L;

	{
		add(ItemNames.custom_bought_.MARK);
		add(ItemNames.custom_bought_.TYPE);
		add(ItemNames.custom_bought_.CASE);
		add(ItemNames.custom_bought_.QTY);
		add(ItemNames.custom_bought_.LINK);
		add(ItemNames.custom_bought_.EXTRA);
		add(ItemNames.custom_bought_.NONEMPTY );
	}};
}
