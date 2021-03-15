package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by user on 14.06.2019.
 */
public class ImportXmlFromTools extends IntegrateBase implements CatalogConst {

	private static final String URL = "http://www.tools.by/tools_yml.php?unp=800014103";
	private static final String TOOLS_NAME = "Парсинг www.tools.by";
	private Content content;


	@Override
	protected boolean makePreparations() throws Exception {
		setOperation("Соединение с " + URL);
		content = Request.Get(URL).execute().returnContent();
		return content != null;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Создание раздела");
		info.setLineNumber(-1);
		info.setProcessed(0);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		ItemQuery q = new ItemQuery(SECTION_ITEM, Item.STATUS_HIDDEN);
		q.addParameterCriteria(NAME_PARAM, TOOLS_NAME, "=", null, Compare.SOME);
		Item section = q.loadFirstItem();
		if (section == null) {
			Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
			section = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), catalog);
			section.setValue(NAME_PARAM, TOOLS_NAME);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex().ignoreUser());
			executeAndCommitCommandUnits(ItemStatusDBUnit.hide(section.getId()).noFulltextIndex().noTriggerExtra().ignoreUser());
		}
		ToolsParser toolsParser = new ToolsParser(section, getInitiator());
		toolsParser.setInfo(info);
		parser.parse(content.asStream(), toolsParser);
	}

	@Override
	protected void terminate() throws Exception {

	}

	private static class ToolsParser extends DefaultHandler {

		private Locator locator;
		private boolean parameterReady = false;
		private String paramName;
		private StringBuilder paramValue = new StringBuilder();
		private Item section;
		private Item product;
		private boolean isInsideOffer = false;

		private static final String URL = "url";
		private static final String PRICE = "priceRec";
		private static final String CURRENCY_ID = "currencyId";
		private static final String PICTURE = "picture";
		private static final String CODE = "article";
		private static final String COUNTRY_OF_ORIGIN = "country_of_origin";
		private static final String AVAILABLE = "available";
		private static final String DESCRIPTION = "description";

		private HashMap<String, String> currentParams;
		private static final HashSet<String> PARAMS = new HashSet<>();
		private static User initiator;
		private static Info info1;

		public static void setInfo(Info info) {
			ToolsParser.info1 = info;
		}

		static {
			PARAMS.add(URL);
			PARAMS.add(PICTURE);
			PARAMS.add(CURRENCY_ID);
			PARAMS.add(CODE);
			PARAMS.add(COUNTRY_OF_ORIGIN);
			PARAMS.add(DESCRIPTION);
			PARAMS.add(PRICE);
			PARAMS.add(VENDOR_ELEMENT);
			PARAMS.add(ID_ATTR);
			PARAMS.add(NAME);
		}

		private ToolsParser(Item section, User initiator) {
			info1.setOperation("Импорт информации");
			this.section = section;
			this.initiator = initiator;
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (isInsideOffer && PARAMS.contains(qName) && parameterReady) {
				currentParams.put(paramName, StringUtils.trim(paramValue.toString()));
				parameterReady = false;
			} else if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
				String code = currentParams.get(CODE);
				code = StringUtils.isAllBlank(code) ? currentParams.get(ID_ATTR) : code;
				ItemQuery q = new ItemQuery(PRODUCT_ITEM, Item.STATUS_HIDDEN);
				q.setParentId(section.getId(), false);
				q.addParameterCriteria(CODE_PARAM, code, "=", null, Compare.SOME);
				try {
					Item product = q.loadFirstItem();
					if (product == null)
						product = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT_ITEM), section);
					product.setValue(CODE_PARAM, code);
					product.setValue(NAME_PARAM, currentParams.get(NAME));
					product.setValue(VENDOR_PARAM, currentParams.get(VENDOR_ELEMENT));
					product.setValue(DESCRIPTION_PARAM, currentParams.get(DESCRIPTION));
					product.setValue(URL_PARAM, currentParams.get(URL));
					product.setValueUI(AVAILABLE_PARAM, "true".equalsIgnoreCase(currentParams.get(AVAILABLE)) ? "1" : "0");
					product.setValueUI(PRICE_PARAM, currentParams.get(PRICE));
					product.setValue(CURRENCY_ID_PARAM, currentParams.get(CURRENCY_ID));
					File pic = product.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
					if (!pic.isFile()) {
						URL picUrl = null;
						try {
							picUrl = new URL(currentParams.get(PICTURE));
						} catch (Exception e) {
							info1.pushLog("Img not found. URL:\"" + currentParams.get(PICTURE) + "\"");
						}
						if (picUrl != null) {
							product.setValue(MAIN_PIC_PARAM, picUrl);
						}
					}
					DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors().ignoreUser());
					DelayedTransaction.executeSingle(initiator, ItemStatusDBUnit.hide(product.getId()).noFulltextIndex().ignoreFileErrors().ignoreUser().noTriggerExtra());
					info1.increaseProcessed();
				} catch (Exception e) {
					info1.addError(ExceptionUtils.getStackTrace(e), locator.getLineNumber(), locator.getColumnNumber());
				}
				isInsideOffer = false;
				parameterReady = false;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (parameterReady) paramValue.append(ch, start, length);
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			parameterReady = false;
			paramValue = new StringBuilder();
			info1.setLineNumber(locator.getLineNumber());
			if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
				currentParams = new HashMap<>();
				currentParams.put(AVAILABLE, attributes.getValue(AVAILABLE));
				currentParams.put(ID_ATTR, attributes.getValue(ID_ATTR));
				isInsideOffer = true;
			} else if (isInsideOffer) {
				paramName = qName;
				parameterReady = true;
			}
		}

	}
}
