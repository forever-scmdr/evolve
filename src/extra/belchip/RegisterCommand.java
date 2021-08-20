package extra.belchip;

import ecommander.fwk.ItemUtils;
import ecommander.fwk.UserExistsExcepion;
import ecommander.model.*;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewUserDBUnit;
import ecommander.persistence.commandunits.UpdateUserDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;


/**
 * Добавляет или удаляет связь предприятия с разделом каталога
 * 
 * @author EEEE
 *
 */
public class RegisterCommand extends Command implements ItemNames, CartConstants {


	private static final String MESSAGE_EXTRA = "user_message";
	private static final String STATE_VALUE = "Устава";
	private static final String YES_VALUE = "да";
	public static final String REGISTERED_GROUP = "registered";

	public static final String P1_EXTRA = "p1";
	public static final String P2_EXTRA = "p2";
	public static final String P3_EXTRA = "p3";

	/**
	 * Проверка заполненности полей пользователя юридического лица
	 * @param jur
	 * @return
	 */
	private LinkedHashSet<String> validateJur(Item jur) {
		LinkedHashSet<String> emptyFields = new LinkedHashSet<>();
		if (jur.isValueEmpty(user_jur_.ORGANIZATION)) emptyFields.add(user_jur_.ORGANIZATION);
		if (jur.isValueEmpty(user_.PHONE)) emptyFields.add(user_.PHONE);
		if (jur.isValueEmpty(user_.EMAIL)) emptyFields.add(user_.EMAIL);
		if (jur.isValueEmpty(user_jur_.CONTACT_NAME)) emptyFields.add(user_jur_.CONTACT_NAME);
		if (jur.isValueEmpty(user_jur_.CONTACT_PHONE)) emptyFields.add(user_jur_.CONTACT_PHONE);
		if (jur.isValueEmpty(user_jur_.ADDRESS)) emptyFields.add(user_jur_.ADDRESS);
		if (jur.isValueEmpty(user_jur_.UNP)) emptyFields.add(user_jur_.UNP);
		if (jur.isValueEmpty(user_jur_.DIRECTOR)) emptyFields.add(user_jur_.DIRECTOR);
		if (jur.isValueEmpty(user_jur_.BASE)) emptyFields.add(user_jur_.BASE);
		if (jur.isValueEmpty(user_.PASSWORD)) emptyFields.add(user_.PASSWORD);
		
		boolean needsBaseNumber = !StringUtils.equalsIgnoreCase(jur.getStringValue(user_jur_.BASE), STATE_VALUE);
		if(needsBaseNumber) {
			if(jur.isValueEmpty(user_jur_.BASE_NUMBER)) emptyFields.add(user_jur_.BASE_NUMBER);
			if(jur.isValueEmpty(user_jur_.BASE_DATE)) emptyFields.add(user_jur_.BASE_DATE);
		}
		boolean hasAccount = !StringUtils.equalsIgnoreCase(YES_VALUE, jur.getStringValue(user_jur_.NO_ACCOUNT));
		if(hasAccount) {
			if(jur.isValueEmpty(user_jur_.ACCOUNT)) emptyFields.add(user_jur_.ACCOUNT);
			if(jur.isValueEmpty(user_jur_.BANK_CODE)) emptyFields.add(user_jur_.BANK_CODE);
			if(jur.isValueEmpty(user_jur_.BANK_ADDRESS)) emptyFields.add(user_jur_.BANK_ADDRESS);
		}
		return emptyFields;
	}

	/**
	 * Регистрация
	 * Создание нового пользователя, загрузка или создание айтема пользователя, начало сеанса созданного пользователя
	 * @return
	 * @throws Exception
	 */
	public ResultPE register() throws Exception {
		getSessionMapper().removeItems(USER);
		final Item formUser = getItemForm().getItemSingleTransient();
		boolean isJur = USER_JUR.equals(formUser.getTypeName());
		boolean isPhys = USER_PHYS.equals(formUser.getTypeName());
		Item sessionUser = getSessionMapper().createSessionRootItem(formUser.getTypeName());
		String phone;
		if (isJur) {
			Item.updateParamValues(formUser, sessionUser);
			if (validateJur(formUser).size() > 0) {
				sessionUser.setExtra(MESSAGE_EXTRA, "Заполните, пожалуйста, обязательные поля");
				getSessionMapper().saveTemporaryItem(sessionUser, USER);
				return getResult("error");
			}
			phone = (String) formUser.getValue(user_.PHONE);
		} else if (isPhys) {
			Item.updateParamValues(formUser, sessionUser);
			if (formUser.isValueEmpty(user_.EMAIL) || formUser.isValueEmpty(user_.PASSWORD)
					|| formUser.isValueEmpty(user_phys_.NAME) || formUser.isValueEmpty(user_phys_.SECOND_NAME)
					|| formUser.isValueEmpty(user_.PHONE)
			) {
				sessionUser.setExtra(MESSAGE_EXTRA, "Заполните, пожалуйста, обязательные поля");
				getSessionMapper().saveTemporaryItem(sessionUser, USER);
				return getResult("error");
			}
			phone = (String) formUser.getValue(user_.PHONE);
		} else {
			sessionUser.setExtra(MESSAGE_EXTRA, "Ошибка");
			getSessionMapper().saveTemporaryItem(sessionUser, USER);
			return getResult("error");
		}
		if (!StringUtils.containsOnly(phone, "1234567890()+- ")) {
			sessionUser.setExtra(MESSAGE_EXTRA, "Укажите, пожалуйста, телефон в предложенном формате");
			getSessionMapper().saveTemporaryItem(sessionUser, USER);
			return getResult("error");
		}
		if (!StringUtils.equals(formUser.getStringValue(user_.PASSWORD), formUser.getStringExtra(P1_EXTRA))) {
			sessionUser.setExtra(MESSAGE_EXTRA, "Пароль и подтверждение пароля не совпадают");
			getSessionMapper().saveTemporaryItem(sessionUser, USER);
			return getResult("error");
		}

		Item catalog = ItemUtils.ensureSingleRootItem(REGISTERED_CATALOG, User.getDefaultUser(),
				UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		User newUser = new User(formUser.getStringValue(user_.EMAIL), formUser.getStringValue(user_.PASSWORD), "registered user", User.ANONYMOUS_ID);
		newUser.addGroup(REGISTERED_GROUP, UserGroupRegistry.getGroup(REGISTERED_GROUP), User.SIMPLE);
		try {
			executeCommandUnit(new SaveNewUserDBUnit(newUser).ignoreUser());
		} catch (UserExistsExcepion e) {
			getSessionMapper().saveTemporaryItem(sessionUser, USER);
			sessionUser.setExtra(MESSAGE_EXTRA, "Предоставленный email уже используется для учетной записи. Выберите другой email");
			return getResult("user_exists");
		}
		try {
			Item userItem = ItemQuery.loadSingleItemByParamValue(USER, user_.EMAIL, formUser.getStringValue(user_.EMAIL));
			if (userItem != null) {
				Item.updateParamValues(userItem, formUser);
			} else {
				userItem = formUser;
				userItem.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), catalog.getId());
			}
			userItem.setOwner(UserGroupRegistry.getGroup(REGISTERED_GROUP), newUser.getUserId());
			executeCommandUnit(SaveItemDBUnit.get(userItem).ignoreUser().noTriggerExtra());
			startUserSession(newUser);
			commitCommandUnits();
		} catch (Exception e) {
			//contacts.setValue(MESSAGE_PARAM, ExceptionUtils.getExceptionStackTrace(e));
			sessionUser.setExtra(MESSAGE_EXTRA, "Ошибка сервера. Попробуйте позже.");
			getSessionMapper().saveTemporaryItem(sessionUser, USER);
			return getResult("error");
		}
		getSessionMapper().saveTemporaryItem(sessionUser, USER);
		sessionUser.setExtra(MESSAGE_EXTRA, "Регистрация завершена успешно. Спасибо за регистрацию");
		return getResult("success");
	}

	/**
	 * Изменить анкету пользователя (в том числе пароль)
	 * @return
	 * @throws Exception
	 */
	public ResultPE edit() throws Exception {
		long id = Long.parseLong(getVarSingleValue("id"));
		final Item formUser = getItemForm().getItemSingleTransient();
		Item dbUser = ItemQuery.loadById(id);
		if (dbUser == null)
			return getResult("error");
		String p1 = formUser.getStringExtra(P1_EXTRA);
		String p2 = formUser.getStringExtra(P2_EXTRA);
		String p3 = formUser.getStringExtra(P3_EXTRA);
		if (StringUtils.isNotBlank(p1) && StringUtils.isNotBlank(p2) && StringUtils.isNotBlank(p3)) {
			if(!p2.equals(p3)) {
				ResultPE res = getResult("mismatch");
				res.setVariable("message", "Пароль и подтверждение пароля не совпадают");
				return res;
			}
			User currentUser = getInitiator();
			String login = currentUser.getName();
			User test = UserMapper.getUser(login, p1);
			if (test == null){
				ResultPE res = getResult("mismatch");
				res.setVariable("message", "Введен неверный пароль");
				return res;
			}
			currentUser.setNewPassword(p2);
			executeAndCommitCommandUnits(new UpdateUserDBUnit(currentUser, false).ignoreUser(true));
		}
		Item.updateParamValues(formUser, dbUser);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(dbUser).ignoreUser(true));

		getSessionMapper().removeItems(USER);
		Item sessionUser = getSessionMapper().createSessionRootItem(dbUser.getTypeName());
		Item.updateParamValues(dbUser, sessionUser);
		getSessionMapper().saveTemporaryItem(sessionUser, USER);

		return getResult("update_success");
	}



	@Override
	public ResultPE execute() throws Exception {
		return getResult("error");
	}
}
