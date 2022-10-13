package ecommander.fwk.external_shops.arrow;

/**
 * Тут хранятся названия полей JSON-бъекта который возвращает API arrow
 * Что заканчвается на "_ARR" - массив. Остальное - объекты.
 * API doc: http://api.arrow.com
 */
public interface ArrowJSONConst {
	String ROOT = "itemserviceresult";
	String RESPONSE_ARR = "transactionArea";
	String RESPONSE = "response";
	String ERROR_MSG = "returnMsg";
	String RESULTS_ARR = "data";
	String STORES_ARR = "webSites";
	String PRODUCTS_ARR = "PartList";
	String ID = "itemId";
	String VENDOR_CODE = "partNum";
	String NAME = "desc";
	String PACKAGE = "packageType";
	String INVENTORY = "InvOrg";
	String SOURCES_ARR = "sources";
	String CURRENCY = "currency";
	String STORE_ID = "sourceCd";
	String VERICAL_VAL = "Verical.com";
	String OFFERS = "sourceParts";
	String PRICE_LV1 = "Prices";
	String PRICE_LV2_ARR = "resaleList";
	String PRICE = "price";
	String MIN_QTY = "minQty";
	String MAX_QTY = "fohQty";
	String IN_STOCK = "availabilityMessage";
	String COUNTRY = "countryOfOrigin";
	String SEARCH_SUCCESS = "success";
	String CODE = "sourcePartId";
	String AVAILABILITY_ARR = "Availability";
	String SHIPMENT = "shipsIn";
	String VENDOR = "manufacturer";
	String VENDOR_NAME = "mfrName";

	String URI_ARR = "resources";
	String URI_TYPE = "type";
	String URI = "uri";
	String PDF_VAL = "datasheet";
	String IMG_SMALL_VAL = "image_small";
	String IMG_VAL = "image_large";
	String PARAMS_VAL = "cloud_part_detail";
	String STEP = "packSize";
}