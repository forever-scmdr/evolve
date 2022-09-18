package ecommander.fwk.external_shops.mouser;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MouserJsonToXMLConverter implements MouserJsonConst, CatalogConst {

	public static String convert(String json) {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDocPart();

		JSONObject responseBody = new JSONObject(json);
		JSONArray products = responseBody.getJSONObject(RESULT).getJSONArray(PRODUCTS_KEY);
		if (products != null) {
			for (int i = 0; i < products.length(); i++) {
				JSONObject prodcut = products.getJSONObject(i);
				addToXML(doc, prodcut);
			}
		}

		return doc.toString();
	}

	private static void addToXML(XmlDocumentBuilder doc, JSONObject product) {
		doc.startElement(PRODUCT_ITEM, "id", product.get(CODE))
				.addElement(CODE_PARAM, product.get(CODE))
				.addElement(NAME_PARAM, product.get(MouserJsonConst.NAME));
		Object img = product.get(IMG);
		if (img instanceof String) {
			doc.addEmptyElement(MAIN_PIC_PARAM, "path", img);
		}
		try {
			String q = NONE.equals(product.getString(QUANTITY)) ? "0" : product.getString(QUANTITY).replace(" In Stock", "");
			doc.addElement(QTY_PARAM, q);
		} catch (JSONException e) {
			doc.addElement(QTY_PARAM, 0);
		}

		doc.addElement("leadtime", product.getString(TIME).replaceAll("\\D", ""));
		doc.addElement(LINK_PARAM, product.getString(LINK));
		doc.addElement(VENDOR_CODE_PARAM, product.getString(VENDOR_CODE));
		doc.addElement(VENDOR_PARAM, product.getString(VENDOR));
		doc.addElement(MIN_QTY_PARAM, product.getString(MIN_Q));
		doc.addElement(STEP_PARAM, product.getString(STEP));

		addPrices(doc, product);
		addDetails(doc, product);

		doc.endElement();
	}

	private static void addPrices(XmlDocumentBuilder doc, JSONObject product) {
		JSONArray priceBreaks = product.getJSONArray(PRICE);
		if (priceBreaks != null) {
			for (int i = 0; i < priceBreaks.length(); i++) {
				JSONObject p = priceBreaks.getJSONObject(i);
				doc.startElement("price_break");
				doc.addElement(QTY_PARAM, p.get("Quantity"));
				doc.addElement(PRICE_ELEMENT, p.getString("Price").substring(1).replaceAll(",", ""));
				doc.endElement();
			}
		}
	}

	private static void addDetails(XmlDocumentBuilder doc, JSONObject product) {
		doc.startElement("params");
		if (StringUtils.isNotBlank(product.getString("SuggestedReplacement"))) {
			doc.startElement("parameter");
			doc.addElement(NAME_ATTR, "Аналог");
			doc.addElement(VALUE, product.getString("SuggestedReplacement"));
			doc.endElement();
		}

		doc.startElement("parameter");
		doc.addElement(NAME_ATTR, "ROHSStatus");
		doc.addElement(VALUE, product.getString("ROHSStatus"));
		doc.endElement();

		JSONArray params = product.getJSONArray("ProductAttributes");

		if (params != null) {
			for (int i = 0; i < params.length(); i++) {
				JSONObject p = params.getJSONObject(i);
				try {
					doc.startElement("parameter");
					doc.addElement(NAME_PARAM, p.getString("AttributeName"));
					doc.addElement(VALUE, p.getString("AttributeValue"));
					doc.endElement();
				} catch (Exception e) {
				}
			}
		}

		doc.endElement();
	}


}
