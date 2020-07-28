package extra;

import extra._generated.ItemNames;

public interface DigiKeyJSONConst {
	String JSON_PRODUCT_COUNT = "ProductsCount";
	String JSON_EXACT = "ExactDigiKeyProduct";
	String JSON_MANUFACTURER_PRODUCT = "ExactManufacturerProducts";
	//Array
	String JSON_RESULTS = "Products";
	String JSON_VALUE = "Value";
	String JSON_CODE = "DigiKeyPartNumber";
	String JSON_NAME = "ProductDescription";
	String JSON_PIC = "PrimaryPhoto";
	String JSON_PRICE = "UnitPrice";
	String JSON_VENDOR_CODE = "ManufacturerPartNumber";
	//Object
	String JSON_VENDOR = "Manufacturer";
	//Array
	String JSON_PRICE_MAP = "StandardPricing";
	String JSON_SPEC_QTY = "BreakQuantity";
	String JSON_MIN_QTY = "MinimumOrderQuantity";
	String JSON_QTY = "QuantityAvailable";
	String JSON_DESCRIPTION = "DetailedDescription";
	String JSON_URL = "ProductUrl";
	String JSON_MANUAL = "PrimaryDatasheet";
	String JSON_PARAMS = "Parameters";
	String JSON_PARAM = "Parameter";
	String JSON_TOTAL = "TotalPrice";



	//XML constants
	String PRODUCT = "product";
	String NAME = "name";
	String CODE = "code";
	String VENDOR_CODE = ItemNames.product_.VENDOR_CODE;
	String VENDOR = ItemNames.product_.VENDOR;
	String MAIN_PIC = "main_pic";
	String PRICE = "price";
	String MIN_QTY = "min_qty";
	String QTY = "qty";
	String DESCRIPTION = "description";
	String OLD_URL = "url";
	String MANUAL = "doc_ref";
	String PARAM = "parameter";
	String SUM = "sum";
}
