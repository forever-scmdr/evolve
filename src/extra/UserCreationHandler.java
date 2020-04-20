package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.model.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.User_jur;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

public class UserCreationHandler extends DefaultHandler implements ItemNames.user_jur_ {

	private static final String CUSTOMER_EL = "customer";
	public static final String REGISTERED_GROUP = "registered";

	private static final HashMap<String, String> ELEMENT_PARAM = new HashMap<>();
	static {
		ELEMENT_PARAM.put("email", EMAIL);
		ELEMENT_PARAM.put("contract_number", CONTRACT_NUMBER);
		ELEMENT_PARAM.put("unp", UNP);
		ELEMENT_PARAM.put("company_name", ORGANIZATION);
		ELEMENT_PARAM.put("person", CONTACT_NAME);
		ELEMENT_PARAM.put("phone_number", PHONE);
		ELEMENT_PARAM.put("city", CITY);
		ELEMENT_PARAM.put("route", ROUTE);
		ELEMENT_PARAM.put("debt", DEBT);
		ELEMENT_PARAM.put("discount", DISCOUNT);
	}


	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();

	private IntegrateBase.Info info;  // информация для пользователя
	private ItemType userType;
	private User initiator;
	private boolean isInsideUser = false;
	private Item userCatalog;
	private HashMap<String, String> singleParams;

	public UserCreationHandler(IntegrateBase.Info info, User initiator) throws Exception {
		this.info = info;
		this.userType = ItemTypeRegistry.getItemType(User_jur._NAME);
		this.initiator = initiator;
		userCatalog = ItemQuery.loadSingleItemByName(ItemNames.REGISTERED_CATALOG);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (StringUtils.equalsIgnoreCase(qName, CUSTOMER_EL)) {
				String email = singleParams.get(EMAIL);
				String unp = singleParams.get(UNP);
				if (StringUtils.isAllBlank(email, unp)) {
					info.addError("Запись пользователя не валидна (отсутствуют email и УНП)", locator.getLineNumber(), 0);
					return;
				}
				Item userItem = null;
				User user;
				String userName = StringUtils.isNotBlank(email) ? email : unp;
				String password = StringUtils.isNotBlank(unp) ? unp : email;
				if (StringUtils.isNotBlank(email)) {
					userItem = ItemQuery.loadSingleItemByParamValue(ItemNames.USER, EMAIL, email);
				} else if (StringUtils.isNotBlank(unp)) {
					userItem = ItemQuery.loadSingleItemByParamValue(ItemNames.USER, EMAIL, email);
				}
				user = UserMapper.getUser(userName);
				if (user == null) {
					user = new User(userName, password, "registered user", User.ANONYMOUS_ID);
					user.addGroup(REGISTERED_GROUP, UserGroupRegistry.getGroup(REGISTERED_GROUP), User.SIMPLE);
					DelayedTransaction.executeSingle(initiator, new SaveNewUserDBUnit(user).ignoreUser());
				}
				if (userItem == null) {
					userItem = Item.newChildItem(userType, userCatalog);
				}
				if (userItem.getOwnerUserId() != user.getUserId()) {
					userItem.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), user.getUserId());
				}
				for (String element : singleParams.keySet()) {
					userItem.setValueUI(ELEMENT_PARAM.get(element), singleParams.get(element));
				}
				userItem.setValue(REGISTERED, (byte) 1);
				userItem.setValue(EMAIL, userName);
				userItem.setValue(PASSWORD, password);
				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(userItem).noTriggerExtra().ignoreUser());
				info.increaseProcessed();
				isInsideUser = false;
			}

			else if (isInsideUser && ELEMENT_PARAM.containsKey(qName) && parameterReady) {
				singleParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			parameterReady = false;
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(parameterReady)
			paramValue.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		parameterReady = false;
		paramValue = new StringBuilder();
		// Продукт
		if (StringUtils.equalsIgnoreCase(qName, CUSTOMER_EL)) {
			singleParams = new HashMap<>();
			isInsideUser = true;
		}
		// Параметры продуктов (общие)
		else if (isInsideUser && ELEMENT_PARAM.containsKey(qName)) {
			paramName = qName;
			parameterReady = true;
		}
	}

}
