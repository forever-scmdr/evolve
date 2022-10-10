package extra.belchip;

public interface IConst {
	/**
	 * Основные элементы - айтемы
	 */
	String SECTION_ELEMENT = "section";
	String PRODUCT_ELEMENT = "product";
	/**
	 * Элементы - параметры
	 */
	String PARAMETER_ELEMENT = "parameter";
	String NAME_ELEMENT = "name";
	String VALUE_ELEMENT = "value";
	String MARK_ELEMENT = "mark";
	String PRODUCER_ELEMENT = "producer";
	String CODE_ELEMENT = "code";
	String PRICE_ELEMENT = "price";
	String DESCRIPTION_ELEMENT = "description";
	String SEARCH_ELEMENT = "search";
	String PIC_PATH_ELEMENT = "pic_path";
	String ANALOG_ELEMENT = "analog";
	String QTY_ELEMENT = "qty";
	String QTY_S1_ELEMENT = "qty1";
	String QTY_S2_ELEMENT = "qty2";
	String UNIT_ELEMENT = "unit";
	String FILE_ELEMENT = "file";
	String MIN_QTY_ELEMENT = "min_qty";
	String BARCODE_ELEMENT = "barcode";
	String COUNTRY_ELEMENT = "country";
	String SPECIAL_PRICE_ELEMENT = "special_price";
	String ANALOG_CODE_ELEMENT = "analog_code";
	String TEXT_TOP_ELEMENT = "textop";
	String VIDEO = "filevid";
	String KURS_ELEMENT = "kurs";
	String USD_ELEMENT = "usd";
	String EUR_ELEMENT = "eur";
	String RUB_ELEMENT = "rub";
	String MIN_ORDER_ELEMENT = "min_order";
	String STEP_ORDER_ELEMENT = "step_order";
	String USLUGA_ELEMENT = "usluga";
	
	/**
	 * Атрибуты элементов
	 */
	String NAME_ATTRIBUTE = "name";
	String CODE_ATTRIBUTE = CODE_ELEMENT;
	String CLASS_ATTRIBUTE = "class";
	String HIT_ATTRIBUTE = "hit";
	String NEW_ATTRIBUTE = "new";
	String SOON_ATTRIBUTE = "soon";
	String PIC_PATH_ATTRIBUTE = PIC_PATH_ELEMENT;
	
	String DISCOUNT_1_PARAM = "discount_1";
	String LIMIT_1_PARAM = "limit_1";
	String DISCOUNT_2_PARAM = "discount_2";
	String LIMIT_2_PARAM = "limit_2";
	String NORM_PARAM = "norm";
	String TYPE_PARAM = "type";

	String FILTER_PARAMETER = "params_filter";


	String[] COMMON_PARAMS = { NAME_ELEMENT, MARK_ELEMENT, PRODUCER_ELEMENT, CODE_ELEMENT, PRICE_ELEMENT, DESCRIPTION_ELEMENT,
			COUNTRY_ELEMENT, PIC_PATH_ELEMENT, ANALOG_ELEMENT, FILE_ELEMENT, QTY_ELEMENT, QTY_S1_ELEMENT, QTY_S2_ELEMENT, UNIT_ELEMENT,
			MIN_QTY_ELEMENT, BARCODE_ELEMENT, SPECIAL_PRICE_ELEMENT, ANALOG_CODE_ELEMENT, TEXT_TOP_ELEMENT, VIDEO };


}
