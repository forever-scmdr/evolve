package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Paths;

/**
 * Поиск товавров по ключевым словам на сайтах arrow.com
 * Используется arrow REST API v4 http://api.arrow.com
 * Режим поиска - token. Руководстство по API: http://developers.arrow.com/api/index.php/site/page?view=v4isSearchToken
 *
 * @author anton
 */

public class ArrowSearchCommand extends Command implements ArrowJSONConst {

	private static final String LOGIN = "chipelectronics1";
	private static final String KEY = "65647ca3414db6b933a1dbdc14cf51e1ee9ad2f85730db19bdc1760b7ea4c651";
	private static final String REQUEST_URL = "http://api.arrow.com/itemservice/v4/en/search/token";

	@Override
	public ResultPE execute() throws Exception {
		String query = getVarSingleValue("q");
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		if (StringUtils.isNotBlank(query)) {
			JSONObject searchResult = loadFromArrowApi(query);
			if (searchResult != null) {
				boolean hasProducts = addGeneralResponseInfo(xml, searchResult);
				if (hasProducts) {
					addProducts(xml, searchResult);
				}
			}
		}
		ResultPE result = getResult("result");
		result.setValue(xml.toString());
		return result;
	}

	private JSONObject loadFromArrowApi(String searchRequest) throws Exception {
		String login = URLEncoder.encode(LOGIN, "UTF-8");
		String query = URLEncoder.encode(searchRequest, "UTF-8");
		URL ArrowAPIUrl = new URL(REQUEST_URL + "?login=" + login + "&apikey=" + KEY + "&search_token=" + query + "&rows=25");
		InputStreamReader is = new InputStreamReader(ArrowAPIUrl.openStream(), Charset.forName("UTF-8"));
		try (BufferedReader reader = new BufferedReader(is)) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return new JSONObject(sb.toString());
		}
	}

	/**
	 * Добавляет список найденных товааров.
	 *
	 * @param xml
	 * @param searchResult
	 */
	private void addProducts(XmlDocumentBuilder xml, JSONObject searchResult) {
		xml.startElement("products");
		JSONArray data = searchResult.getJSONObject(ROOT).getJSONArray(RESULTS_ARR);
		for (int i = 0; i < data.length(); i++) {
			JSONArray products = data.getJSONObject(i).getJSONArray(PRODUCTS_ARR);
			for (int j = 0; j < products.length(); j++) {
				addProduct(xml, products.getJSONObject(j));
			}
		}
		xml.endElement();
	}

	/**
	 * Переводит продукт из JSON в XML и добавляет его в документ
	 *
	 * @param xml
	 * @param product
	 */
	private void addProduct(XmlDocumentBuilder xml, JSONObject product) {
		xml.startElement("product", "id", product.getLong(ID));

		//product basics
		String displayCode = product.getString(VENDOR_CODE);

		xml.addElement("code", displayCode);
		xml.addElement("name", product.getString(NAME));
		xml.addElement("main_pic", getUri(product, IMG_VAL));
		xml.addElement("small_pic", getUri(product, IMG_SMALL_VAL));
		//xml.addElement("params_link", StringUtils.substringAfterLast(getUri(product, PARAMS_VAL), "products/"));
		xml.addElement("doc", getUri(product, PDF_VAL));

		JSONObject vendor = product.getJSONObject(VENDOR);
		String vendorName = vendor.getString(VENDOR_NAME);

		xml.addElement("vendor", vendorName);

		//prices and availability
		JSONArray stores = product.getJSONObject(INVENTORY).getJSONArray(STORES_ARR);

		for (int i = 0; i < stores.length(); i++) {
			JSONObject store = stores.getJSONObject(i);
			xml.addElement("site", store.getString("name"));
			JSONArray priceSources = store.getJSONArray(SOURCES_ARR);
			for (int j = 0; j < priceSources.length(); j++) {
				JSONArray offers = priceSources.getJSONObject(j).getJSONArray(OFFERS);
				for (int k = 0; k < offers.length(); k++) {
					processOffer(xml, offers.getJSONObject(k));
				}
			}
		}
		xml.endElement();
	}

	/**
	 * Добавляет цены и сведения о наличи товара. В одном товаре может быть несколько цен и наличий.
	 */
	private void processOffer(XmlDocumentBuilder xml, JSONObject offer) {
		xml.startElement("offer");
		String code = "vrc_" + offer.getString(CODE);
		xml.addElement("code", code);
		String country = null;
		try {
			country = offer.getString(COUNTRY);
		} catch (Exception e) {
		}
		xml.addElement("country", country);
		xml.addElement("shipment", getShipment(offer));
		xml.addElement("step", offer.getInt(STEP));
		if (offer.has(PRICE_LV1)) {
			JSONObject priceLv1 = offer.getJSONObject(PRICE_LV1);
			JSONArray priceLv2 = priceLv1.getJSONArray(PRICE_LV2_ARR);
			for (int i = 0; i < priceLv2.length(); i++) {
				JSONObject price = priceLv2.getJSONObject(i);
				xml.addElement("price", price.getDouble(PRICE));
				xml.addElement("min_qty", price.getInt(MIN_QTY));
			}
		} else {
			xml.addElement("no_price", "");
		}
		JSONArray availability = offer.getJSONArray(AVAILABILITY_ARR);
		for (int i = 0; i < availability.length(); i++) {
			JSONObject av = availability.getJSONObject(i);
			xml.addElement("qty", av.getInt(MAX_QTY));
			xml.addElement("available", av.getString(IN_STOCK).equals("In Stock") ? 1 : 0);
		}
		xml.endElement();
	}

	private int getShipment(JSONObject offer) {
		int shipment = 0;
		String s = offer.getString(SHIPMENT);
		s = s.replaceAll("\\D", "");
		if (StringUtils.isNotBlank(s)) {
			shipment += Integer.parseInt(s);
		}
		return shipment;
	}

	/**
	 * Извленкает ссылку с нужным именем из массива "resources", вложенного в некоторый оъект.
	 *
	 * @param resourceContainer
	 * @param key
	 */
	private String getUri(JSONObject resourceContainer, String key) {
		JSONArray resources = resourceContainer.getJSONArray(URI_ARR);
		if (resources != null) {
			for (int i = 0; i < resources.length(); i++) {
				JSONObject link = resources.getJSONObject(i);
				if (link.getString(URI_TYPE).equals(key)) {
					return link.getString(URI);
				}
			}
		}
		return "";
	}


	/**
	 * Добавляет общие сведения об ответе (статус, количество результатов, ошибки).
	 *
	 * @param xml
	 * @param searchResult
	 */
	private boolean addGeneralResponseInfo(XmlDocumentBuilder xml, JSONObject searchResult) {
		JSONArray responses = searchResult.getJSONObject(ROOT).getJSONArray(RESPONSE_ARR);
		boolean ok = false;

		xml.startElement("summery");
		for (int i = 0; i < responses.length(); i++) {
			JSONObject response = responses.getJSONObject(i).getJSONObject(RESPONSE);
			boolean success = response.getBoolean(SEARCH_SUCCESS);
			if (response.getBoolean(SEARCH_SUCCESS)) {
				ok = true;
			}
			xml.startElement("response");
			xml.addElement("success", success);
			if (!success) {
				xml.addElement("error", response.getString(ERROR_MSG));
			}
			xml.endElement();
		}
		xml.endElement();
		return ok;
	}

	private JSONObject loadJsonFromFile() throws IOException {
		String fileContent = FileUtils.readFileToString(Paths.get(AppContext.getContextPath(), "arrow.json").toFile(), "UTF-8");
		return new JSONObject(fileContent);
	}
}