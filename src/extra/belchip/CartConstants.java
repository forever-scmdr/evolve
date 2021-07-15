package extra.belchip;

import java.util.HashSet;
import java.util.Set;

import com.ibm.icu.text.SimpleDateFormat;

public interface CartConstants {
	double SUM_1 = 150;
	double SUM_2 = 550;
	int DISCOUNT_1 = 5;
	int DISCOUNT_2 = 10;
	int CUSTOM_BOUGHT_COUNT = 10;
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	String ASSOC_GENERAL = "general";


	//String REGISTERED_GROUP = "registered";
	//String BOUGHT_ITEM = "bought";
	String CUSTOM_BOUGHT_ITEM = "custom_bought";
	//String CART_ITEM = "cart";
	//String PRODUCT_ITEM = "product";
	String COUNTER_ITEM = "counter";
	String QUANTITY_PARAM = "quantity";
	String ZERO_QUANTITY_PARAM = "zero_quantity";
	String CUSTOM_QUANTITY_PARAM = "custom_quantity";
	//String PRICE_PARAM = "price";
	//String QTY_PARAM = "qty";
	String MIN_QTY_PARAM = "min_qty";
	String NEW_QUANTITY_PARAM = "new_quantity";
	//String SUM_PARAM = "sum";
	String COUNT_PARAM = "count";
	String CODE_PARAM = "code";

	String ACTION_PARAM = "action";
	String PRODUCT_PARAM = "product";
	String PROCESSED_PARAM = "processed";
	String BARCODE_PARAM = "barcode";
	String POSITTION_PARAM = "position";
	String NONEMPTY_PARAM = "nonempty";

	String MESSAGE_PARAM = "user_message";

	String SECOND_NAME_PARAM = "second_name";
	String NAME_PARAM = "name";
	String PHONE_PARAM = "phone";
	String EMAIL_PARAM = "email";
	String POST_CITY_PARAM = "post_city";
	String POST_REGION_PARAM = "post_region";
	String IF_ABSENT_PARAM = "if_absent";
	String GET_ORDER_FROM_PARAM = "get_order_from";
	String IN_PROGRESS = "in_progress";

	String ORGANIZATION_PARAM = "organization";
	String ADDRESS_PARAM = "address";
	String JUR_PHONE_PARAM = "jur_phone";
	String JUR_EMAIL_PARAM = "jur_email";
	String NO_ACCOUNT_PARAM = "no_account";
	String ACCOUNT_PARAM = "account";
	String BANK_PARAM = "bank";
	String BANK_ADDRESS_PARAM = "bank_address";
	String BANK_CODE_PARAM = "bank_code";
	String UNP_PARAM = "unp";
	String DIRECTOR_PARAM = "director";
	String BASE_PARAM = "base";
	String NEED_POST_ADDRESS_PARAM = "need_post_address";
	String POST_ADDRESS_PARAM = "post_address";
	String POST_INDEX_PARAM = "post_index";
	String JUR_NEED_POST_ADDRESS_PARAM = "jur_need_post_address";
	String JUR_POST_ADDRESS_PARAM = "jur_post_address";
	String JUR_POST_INDEX_PARAM = "jur_post_index";
	String JUR_POST_CITY_PARAM = "jur_post_city";
	String CONTACT_NAME_PARAM = "contact_name";
	String CONTACT_PHONE_PARAM = "contact_phone";
	String BASE_NUMBER_PARAM = "base_number";
	String BASE_DATE_PARAM = "base_date";
	String JUR_FUND_PARAM = "jur_fund";

	String LOGIN_PARAM = "login";

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

	String JUR_LTM = "Извините, минимальная сумма заказа для юридических лиц - %.2f руб. Заказ не отправлен.";
	String PHYS_LTM = "Извините, минимальная сумма заказа для физических лиц - %.2f руб. Заказ не отправлен.";
	String POST_LTM = "Извините, минимальная сумма заказа при отправке почтой - %.2f руб. Заказ не отправлен.";

	Set<String> JUR_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6154731719400877513L;

		{
			add(ORGANIZATION_PARAM);
			add(JUR_PHONE_PARAM);
			add(JUR_EMAIL_PARAM);
			add(CONTACT_NAME_PARAM);
			add(CONTACT_PHONE_PARAM);
			add(ADDRESS_PARAM);
			add(UNP_PARAM);
			add(DIRECTOR_PARAM);
			add(BASE_PARAM);
			add(JUR_FUND_PARAM);
		}
	};

	Set<String> JUR_BASE_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3973888116594652711L;

		{
			add(BASE_NUMBER_PARAM);
			add(BASE_DATE_PARAM);
		}
	};

	Set<String> JUR_NO_ACCOUNT_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6877987850929815731L;

		{
			add(ACCOUNT_PARAM);
			add(BANK_PARAM);
			add(BANK_CODE_PARAM);
			add(BANK_ADDRESS_PARAM);
		}
	};

	Set<String> PHYS_MANDATORY = new HashSet<String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5275087566452782267L;

		{
			add(SECOND_NAME_PARAM);
			add(PHONE_PARAM);
			add(EMAIL_PARAM);
			add(IF_ABSENT_PARAM);
		}
	};
	
	Set<String> PHYS_ADDRESS_MANDATORY = new HashSet<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 6397781611402904275L;

	{
		add(POST_ADDRESS_PARAM);
		add(POST_INDEX_PARAM);
		add(POST_CITY_PARAM);
		add(POST_REGION_PARAM);
		add(NAME_PARAM);
	}};
	
	Set<String> JUR_ADDRESS_MANDATORY = new HashSet<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 8517768710126188679L;

	{
		add(JUR_POST_ADDRESS_PARAM);
		add(JUR_POST_INDEX_PARAM);
		add(JUR_POST_CITY_PARAM);
	}};
	
	Set<String> CUSTOM_BOUGHT_PARAMS = new HashSet<String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 3003185279225323811L;

	{
		add("mark");
		add("type");
		add("case");
		add("qty");
		add("link");
		add("extra");
		add(NONEMPTY_PARAM);
	}};
}
