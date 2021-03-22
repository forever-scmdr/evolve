package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.User;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.pages.var.Variable;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Поиск товавров по ключевым словам на сайтах arrow.com
 * Используется arrow REST API v4 http://api.arrow.com
 * Режим поиска - token. Руководстство по API: http://developers.arrow.com/api/index.php/site/page?view=v4isSearchToken
 *
 * @author anton
 */

public class ArrowSearchCommand extends Command implements ArrowJSONConst {


	@Override
	public ResultPE execute() throws Exception {
		JSONObject searchResult = loadJsonFromFile();
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDoc();
		xml.startElement("page", "name", getPageName());

		addPageBasics(xml);
		boolean hasProducts = addGeneralResponseInfo(xml, searchResult);
		if (hasProducts) {
			addProducts(xml, searchResult);
		}
		xml.endElement();

		ResultPE result = getResult("success");
		result.setValue(xml.toString());
		return result;
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
		for(int i = 0; i<data.length(); i++){
			JSONObject d = data.getJSONObject(i);
			JSONArray products = d.getJSONArray(PRODUCTS_ARR);
			for(int j = 0; j < products.length(); j++){
				JSONObject product = products.getJSONObject(i);
				addProduct(xml, product);
			}
		}
		xml.endElement();
	}

	/**
	 * Переводит продукт из JSON в XML и добавляет его в документ
	 * @param xml
	 * @param product
	 */
	private void addProduct(XmlDocumentBuilder xml, JSONObject product) {
		xml.startElement("product", "id", product.getLong(ID));

		//product basics
		String displayCode = product.getString(VENDOR_CODE);

		//xml.addElement("code", code);
		xml.addElement("code", displayCode);
		xml.addElement("name", product.getString(NAME));
		xml.addElement("main_pic", getUri(product, IMG_VAL));
		xml.addElement("small_pic", getUri(product, IMG_SMALL_VAL));
		xml.addElement("params_link", getUri(product, PARAMS_VAL));
		xml.addElement("doc", getUri(product, PDF_VAL));

		//prices and availability
		JSONArray priceSources = product.getJSONObject(INVENTORY).getJSONArray(SOURCES_ARR);
		xml.startElement("offers");
		for(int i = 0; i < priceSources.length(); i++){
			JSONObject source = priceSources.getJSONObject(i);

			if(source.getString(STORE_ID).equals(VERICAL_VAL)){
				JSONArray offers = source.getJSONArray(OFFERS);
				for(int j = 0; j < offers.length(); j++){
					processOffer(xml, offers.getJSONObject(i));
				}
			}

		}
		xml.endElement();
		xml.endElement();
	}

	/**
	 * Добавляет цены и сведения о наличи товара. В одном товаре может быть несколько цен и наличий.
	 */

	private void processOffer(XmlDocumentBuilder xml, JSONObject offer) {
		xml.startElement("offer");
		String code = "vrc_" + offer.getString(CODE);
		xml.addElement("code", code);
		xml.addElement("country", COUNTRY);
		JSONObject priceLv1 = offer.getJSONObject(PRICE_LV1);
		JSONArray priceLv2 = offer.getJSONArray(PRICE_LV2_ARR);
		for(int i = 0; i < priceLv2.length(); i++){
			JSONObject price = priceLv2.getJSONObject(i);
			xml.addElement("price", price.getDouble(PRICE));
			xml.addElement("min_qty", MIN_QTY);
		}
		JSONArray availability = offer.getJSONArray(AVAILABILITY_ARR);
		for(int i = 0; i < availability.length(); i++){
			JSONObject av = availability.getJSONObject(i);
			xml.addElement("qty", av.getInt(MAX_QTY));
			xml.addElement("available", av.getBoolean(IN_STOCK)? 1 : 0);
		}
		xml.endElement();
	}


	/**
	 * Извленкает ссылку с нужным именем из массива "resources", вложенного в некоторый оъект.
	 * @param resourceContainer
	 * @param key
	 */
	private String getUri(JSONObject resourceContainer, String key){
		JSONArray resources = resourceContainer.getJSONArray(URI_ARR);
		if(resources != null){
			for(int i = 0; i < resources.length(); i++){
				JSONObject link = resources.getJSONObject(i);
				if(link.getString(URI_TYPE).equals(key)){
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


	/**
	 * Добавляет базовую информацию о странице (source_link, base, user, variables).
	 *
	 * @param xml
	 */
	private void addPageBasics(XmlDocumentBuilder xml) {
		//source link
		xml.addElement("source_link", getRequestLink().getOriginalUrl());

		//user
		User u = getInitiator();
		xml.startElement("user", "id", u.getUserId(), "name", u.getName(), "visual", false);
		for (User.Group group : u.getGroups()) {
			xml.addEmptyElement("group", "name", group.name, "id", group.id, "role", group.role);
		}
		xml.endElement();

		//base
		xml.addElement("base", getUrlBase());

		//variables
		xml.startElement("variables");
		for (Variable var : getAllVariables()) {
			String varName = var.getName();
			if(varName.startsWith("$")) continue;
			for (String val : var.writeAllValues()) {
				xml.addElement(varName, val);
			}
		}
		xml.endElement();
	}


	private JSONObject loadJsonFromFile() throws IOException {
		String fileContent = FileUtils.readFileToString(Paths.get(AppContext.getContextPath(), "arrow.json").toFile(), "UTF-8");
		return new JSONObject(fileContent);
	}

}
