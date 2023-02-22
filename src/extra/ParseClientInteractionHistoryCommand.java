package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.model.*;
import ecommander.persistence.commandunits.ChangeItemOwnerDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ParseClientInteractionHistoryCommand extends IntegrateBase {


	private static final Path INTEGRATION_DIR = Paths.get(AppContext.getContextPath(), "upload", "interactions");

	//clear all buffer variable on start of this tag
	private static final String DEALER_EL = "dealer";
	//save user on end of this tag
	private static final String USER_EL = "profile";
	//device with options
	private static final String ORDER_COMPLEX_EL = "order_Tovar";
	//device with options
	private static final String ORDER_SIMPLE_EL = "order_Parts";
	//single bought
	private static final String BOUGHT_EL = "purchase";

	private Collection<File> xmls;

	@Override
	protected boolean makePreparations() throws Exception {
		if (!Files.isDirectory(INTEGRATION_DIR)) {
			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return false;
		}
		xmls = FileUtils.listFiles(INTEGRATION_DIR.toFile(), new String[]{"xml"}, true);
		if (xmls == null || xmls.size() == 0) {
			info.addError("Не найдены XML файлы в директории " + INTEGRATION_DIR, "init");
			return false;
		}
		info.setToProcess(xmls.size());
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Creating users and interaction history");

		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		for (File xml : xmls) {
			DefaultHandler handler = new UserInteractionSaxHandler();
			parser.parse(xml, handler);
		}

		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}

	private class UserInteractionSaxHandler extends DefaultHandler {
		private HashMap<String, String> USER_PARAMS = new HashMap<>();
		private HashMap<String, String> PURCHASE_PARAMS = new HashMap<>();
		private HashMap<String, String> DEVICE_PARAMS = new HashMap<>();

		Item root;

		private Locator locator;
		private boolean parameterReady = false;
		private StringBuilder paramValue = new StringBuilder();
		private HashMap<String, String> paramNamesMap = new HashMap<>();

		private HashMap<String, List<String>> currentItemBuffer;
		private Item currentDealerIitem;
		private Item currentOrder;
		private User currentUser;

		private Item parent;

		UserInteractionSaxHandler() {
			super();
			try {
				root = ItemQuery.loadSingleItemByName("registered_catalog");

				USER_PARAMS.put("login", "login");
				USER_PARAMS.put("uid", "uid");
				USER_PARAMS.put("password", "password");
				USER_PARAMS.put("phone", "contact_phone");
				USER_PARAMS.put("email", "email");
				USER_PARAMS.put("u_address", "address");
				USER_PARAMS.put("f_address", "f_address");
				USER_PARAMS.put("p_address", "p_address");
				USER_PARAMS.put("discount", "discount");

				PURCHASE_PARAMS.put("id", "num");
				PURCHASE_PARAMS.put("sum", "sum_discount");
				PURCHASE_PARAMS.put("date", "date");
				PURCHASE_PARAMS.put("status", "status");

				DEVICE_PARAMS.put("id", "code");
				DEVICE_PARAMS.put("name", "name");
				DEVICE_PARAMS.put("sum", "sum");
				DEVICE_PARAMS.put("quantity", "qty_total");

			} catch (Exception e) {
				ServerLogger.error(e);
				info.addError(e);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			try {

				switch (qName) {
					case DEALER_EL:
						clearBuffers();
						break;
					case USER_EL:
						parent = loadAndUpdateItem("user_jur", "login", root);
						currentDealerIitem = parent;
						executeAndCommitCommandUnits(SaveItemDBUnit.get(parent).ignoreUser().noFulltextIndex());
						currentItemBuffer = new HashMap<>();
						currentUser = UserMapper.getUser(currentDealerIitem.getOwnerUserId());
						break;
					case BOUGHT_EL:
						Item bought = loadAndUpdateItem("bought", "code", parent);
						bought.setValue("is_complex", parent.getValue("is_complex"));
						executeCommandUnit(ChangeItemOwnerDBUnit.newUser(bought, currentUser.getUserId(), UserGroupRegistry.getGroup("registered")).ignoreUser());
						executeAndCommitCommandUnits(SaveItemDBUnit.get(bought).ignoreUser().noFulltextIndex());
						currentItemBuffer = new HashMap<>();
						break;
					default:
						setParameter(qName);
				}

			} catch (Exception e) {
				handleException(e);
			}

			parameterReady = false;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			parameterReady = false;
			paramValue = new StringBuilder();

			try {
				switch (qName) {
					case USER_EL:
						paramNamesMap = USER_PARAMS;
						currentDealerIitem = null;
						currentItemBuffer.put("registered", Arrays.asList("1"));
						break;
					case ORDER_COMPLEX_EL:
						paramNamesMap = PURCHASE_PARAMS;
						currentOrder = null;
						currentItemBuffer = new HashMap<>();
						currentItemBuffer.put("is_complex", Arrays.asList("1"));
						break;
					case ORDER_SIMPLE_EL:
						paramNamesMap = PURCHASE_PARAMS;
						currentOrder = null;
						currentItemBuffer = new HashMap<>();
						currentItemBuffer.put("is_complex", Arrays.asList("0"));
						break;
					case BOUGHT_EL:
						paramNamesMap = DEVICE_PARAMS;
						if (currentOrder == null) {
							currentOrder = loadAndUpdateItem("purchase", "num", currentDealerIitem);
							executeCommandUnit(ChangeItemOwnerDBUnit.newUser(currentOrder, currentUser.getUserId(), UserGroupRegistry.getGroup("registered")).ignoreUser());
							executeAndCommitCommandUnits(SaveItemDBUnit.get(currentOrder).ignoreUser());
						}
						parent = currentOrder;
						break;
					default:
						currentItemBuffer = currentItemBuffer == null? new HashMap<>() : currentItemBuffer;
						parameterReady = paramNamesMap.containsKey(qName);
				}
			} catch (Exception e) {
				handleException(e);
			}
		}

		private void setParameter(String qName){
			if(!parameterReady) return;
			String value = StringUtils.normalizeSpace(paramValue.toString());
			String paramName = paramNamesMap.get(qName);
			if(currentItemBuffer.containsKey(paramName)){
				currentItemBuffer.get(paramName).add(value);
			}else{
				ArrayList<String> vals = new ArrayList<>();
				vals.add(value);
				currentItemBuffer.put(paramName, vals);
			}
		}

		private void clearBuffers() {
			parent = root;
			currentItemBuffer = new HashMap<>();
			currentOrder = null;
			currentDealerIitem = null;
			currentUser = null;
		}

		private Item loadAndUpdateItem(String itemName, String key, Item parent) throws Exception {
			String value = currentItemBuffer.get(key).get(0);
			ItemQuery q = new ItemQuery(itemName);
			q.setParentId(parent.getId(), false);
			q.addParameterCriteria(key, value, "=", null, Compare.SOME);

			Item item = q.loadFirstItem();
			ItemType type = ItemTypeRegistry.getItemType(itemName);
			item = item == null ? Item.newChildItem(type, parent) : item;

			for (Map.Entry<String, List<String>> e : currentItemBuffer.entrySet()) {
				if (type.getParameterNames().contains(e.getKey())) {
					for (String s : e.getValue()) {
						item.setValueUI(e.getKey(), s);
					}
				}
			}
			return item;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (parameterReady)	paramValue.append(ch, start, length);
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		private void handleException(Throwable e) {
			ServerLogger.error("Integration error", e);
			info.setLineNumber(locator.getLineNumber());
			info.setLinePosition(locator.getColumnNumber());
			info.addError(e);
		}

	}


}
