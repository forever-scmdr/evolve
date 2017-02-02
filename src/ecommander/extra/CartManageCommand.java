package ecommander.extra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import ecommander.application.extra.ItemUtils;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ItemVariablesContainer;
import ecommander.pages.elements.ItemVariablesContainer.ItemVariables;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;

public class CartManageCommand extends Command {
	public static final String BYN_CUR_CODE = "BYN";
	public static final String EUR_CUR_CODE = "EUR";
	public static final String RUB_CUR_CODE = "RUB";
	
	private static final String BYN_CUR_NAME = "бел. руб.";
	private static final String EUR_CUR_NAME = "евро";
	private static final String RUB_CUR_NAME = "рос. руб.";
	
	private static final String ACTION = "action";
	private static final String ADD = "add";
	private static final String DELETE = "delete";
	private static final String PAY_ONLY = "pay_only";
	private static final String PAY_AND_STAY = "pay_and_stay";
	private static final String BOOK = "book";
	private static final String SUBMIT = "submit";
	private static final String SUBMIT_PIN = "submit_pin";
	private static final String FREE_ROOM_ID = "fr";
	private static final String ROOM_SEARCH = "room_search";
	private static final String ORDER = "order";
	private static final String MESSAGE = "message";
	private static final String CITIZEN = "citizen";
	private static final String CITIZEN_NAME = "citizen_name";
	private static final String CONFIRM = "confirm";
	private static final String PIN = "pin";
	
	public static final String SAN_VOUCHER = "Санаторно-курортная";
	public static final String OZD_VOUCHER = "Оздоровительная";
	public static final String RB_CITIZEN = "РБ";
	public static final String EAES_CITIZEN = "ЕАЭС";
	
	private static final String PERSON_TYPE_KID = "Ребенок";
	private static final String PERSON_TYPE_ADULT = "Взрослый";
	
	private Item cart = null;
	
	private static HashSet<String> MANDATORY_FORM_PARAMS = new HashSet<String>();
	static {
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.ADDRESS);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.BIRTH_DATE);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.CITIZEN);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.FIRST_NAME);
		//MANDATORY_FORM_PARAMS.add(ItemNames.order_form.ID);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.IS_CONTRACTOR);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.LAST_NAME);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.PASSPORT);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.PASSPORT_ISSUED);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.PERSON_TYPE);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.PHONE);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.SECOND_NAME);
		MANDATORY_FORM_PARAMS.add(ItemNames.order_form.VOUCHER_TYPE);
	}
	
	@Override
	public ResultPE execute() throws Exception {
		// Создание или загрузка корзины
		cart = ItemUtils.ensureSingleSessionRootItem(ItemNames.ORDER, getSessionMapper());
		// Удалить флаг об ознакомлении с условиями договора
		cart.removeValue(ItemNames.order.CONTRACT_AGREED);
		getSessionMapper().saveTemporaryItem(cart);
		
		// Сохранить состояние корзины (всех полей ввода)
		
		ItemVariablesContainer itemVars = getItemVariables();
		String action = itemVars.getExtra(ACTION);
		// Сохранить все айтемы, которые редактируются с помощью инпутов
		// В случае если заполняется форма по пин-коду все изменения происходят в БД
		if (!StringUtils.equalsIgnoreCase(action, SUBMIT_PIN)) {
			if (itemVars != null) {
				for (ItemVariables post : itemVars.getItemPosts()) {
					Item item = getSessionMapper().getItemSingle(post.getItemId());
					for (String inputName : post.getPostedInputs()) {
						String paramName = StringUtils.substringAfter(inputName, "new_");
						item.setValueUI(paramName, post.getValue(inputName));
					}
					getSessionMapper().saveTemporaryItem(item);
				}
			}
		}
		cart = ItemUtils.ensureSingleSessionRootItem(ItemNames.ORDER, getSessionMapper());
		
		// Доабвить комнату в заявку
		
		if (StringUtils.equalsIgnoreCase(action, ADD)) {
			long freeId = Long.parseLong(itemVars.getExtra(FREE_ROOM_ID));
			Item freeRoom = ItemQuery.loadById(freeId);
			if (freeRoom != null) {
				// Создание заявки на комнату
				freeRoom.setDirectParentId(cart.getId());
				freeRoom.setValue(ItemNames.free_room.FROM, null);
				freeRoom.setValue(ItemNames.free_room.TO, null);
				getSessionMapper().saveTemporaryItem(freeRoom);
				// Создание заявок (анкет) на каждое место
				Item roomType = ItemQuery.loadById(freeRoom.getLongValue(ItemNames.free_room.TYPE, -1));
				for (int i = 0; i < roomType.getIntValue(ItemNames.room.BASE_BEDS); i++) {
					Item form = getSessionMapper().createSessionItem(ItemNames.ORDER_FORM, cart.getId());
					getSessionMapper().saveTemporaryItem(form);
					freeRoom.setValue(ItemNames.free_room.ORDER_FORM_BASE, form.getId());
				}
				for (int i = 0; i < roomType.getIntValue(ItemNames.room.EXTRA_BEDS); i++) {
					Item form = getSessionMapper().createSessionItem(ItemNames.ORDER_FORM, cart.getId());
					getSessionMapper().saveTemporaryItem(form);
					freeRoom.setValue(ItemNames.free_room.ORDER_FORM_EXTRA, form.getId());
				}
				getSessionMapper().saveTemporaryItem(freeRoom);
			}
			return getResult(ROOM_SEARCH);
		}
		
		// Удалить комнату из заявки
		
		else if (StringUtils.equalsIgnoreCase(action, DELETE)) {
			long freeId = Long.parseLong(itemVars.getExtra(FREE_ROOM_ID));
			Item freeRoom = getSessionMapper().getItemSingle(freeId);
			if (freeRoom != null) {
				for (long formId : freeRoom.getLongValues(ItemNames.free_room.ORDER_FORM_BASE)) {
					getSessionMapper().removeItems(formId);
				}
				for (long formId : freeRoom.getLongValues(ItemNames.free_room.ORDER_FORM_EXTRA)) {
					getSessionMapper().removeItems(formId);
				}
			}
			getSessionMapper().removeItems(freeId);
			return getResult(ROOM_SEARCH);
		}
		
		// Перейти к оформлению
		
		else if (StringUtils.equalsIgnoreCase(action, BOOK)) {
			ArrayList<Item> rooms = getSessionMapper().getItemsByName(ItemNames.FREE_ROOM, cart.getId());
			
			// Проверка заполненности дат
			LocalDate today = new LocalDate(DateTimeZone.UTC);
			for (Item room : rooms) {
				LocalDate fromDate = new LocalDate(room.getLongValue(ItemNames.free_room.FROM, 0L), DateTimeZone.UTC);
				LocalDate toDate = new LocalDate(room.getLongValue(ItemNames.free_room.TO, 0L), DateTimeZone.UTC);
				if (fromDate.isBefore(today) || toDate.isBefore(fromDate))
					return getResult(ROOM_SEARCH).addVariable(MESSAGE,
							"Заполните, пожалуйста, корректно даты заезда и выезда для каждого из номеров");
				int days = Days.daysBetween(fromDate, toDate).getDays();
				if (days < 4)
					return getResult(ROOM_SEARCH).addVariable(MESSAGE, "Внимание! Минимальный срок бронирования - четыре дня");
			}
			
			// Сохранение гражданства и валюты
			String citizen = getVarSingleValue(CITIZEN);
			String citizenName = getVarSingleValue(CITIZEN_NAME);
			if (StringUtils.isNotBlank(citizen)) {
				ArrayList<Item> forms = getSessionMapper().getItemsByName(ItemNames.ORDER_FORM, cart.getId());
				for (Item form : forms) {
					form.setValue(ItemNames.order_form.CITIZEN, citizen);
					form.setValue(ItemNames.order_form.CITIZEN_NAME, citizenName);
					getSessionMapper().saveTemporaryItem(form);
				}
				setOrderCitizen(cart, citizen, citizenName);
				getSessionMapper().saveTemporaryItem(cart);
			}
			
			// Для подсчета суммы
			List<Item> roomTypes = ItemQuery.newItemQuery(ItemNames.ROOM).loadItems();
			HashMap<Long, Item> roomPrices = new HashMap<Long, Item>();
			for (Item type : roomTypes) {
				roomPrices.put(type.getId(), type);
			}
			Item roomsMeta = ItemQuery.newItemQuery(ItemNames.ROOMS).loadFirstItem();
			
			// Проверить, чтобы в каждой комнате был хотя-бы один взрослый и чтобы
			// у детей была бы только санаторно-курортная путевка
			// Также подсчет суммы
			boolean wrongKidVoucher = false;
			boolean wrongAdultPlacement = false; // когда взрослый на доп. месте при ребенке на основном месте
			boolean noBaseAdults = false; // когда нет взрослых на основных местах
			boolean isBlankVoucher = false;
			double sum = 0;
			for (Item room : rooms) {
				boolean hasBaseKids = false;
				boolean hasBaseAdults = false;
				for (Long formId : room.getLongValues(ItemNames.free_room.ORDER_FORM_BASE)) {
					Item baseBed = getSessionMapper().getItemSingle(formId);
					boolean isKid = StringUtils.equalsIgnoreCase(baseBed.getStringValue(ItemNames.order_form.PERSON_TYPE), PERSON_TYPE_KID);
					String voucherType = baseBed.getStringValue(ItemNames.order_form.VOUCHER_TYPE, "");
					boolean isSan = StringUtils.equalsIgnoreCase(voucherType, SAN_VOUCHER);
					hasBaseKids |= isKid;
					hasBaseAdults |= !isKid;
					wrongKidVoucher |= isKid && isSan;
					isBlankVoucher |= StringUtils.isBlank(voucherType);
					sum += getOrderSum(room, baseBed, roomPrices, roomsMeta, false);
				}
				for (Long formId : room.getLongValues(ItemNames.free_room.ORDER_FORM_EXTRA)) {
					Item extraBed = getSessionMapper().getItemSingle(formId);
					if (isBlankForm(extraBed))
						continue;
					boolean isKid = StringUtils.equalsIgnoreCase(extraBed.getStringValue(ItemNames.order_form.PERSON_TYPE), PERSON_TYPE_KID);
					String voucherType = extraBed.getStringValue(ItemNames.order_form.VOUCHER_TYPE, "");
					boolean isSan = StringUtils.equalsIgnoreCase(voucherType, SAN_VOUCHER);
					wrongAdultPlacement |= !isKid && hasBaseKids;
					wrongKidVoucher |= isKid && isSan;
					isBlankVoucher |= StringUtils.isBlank(voucherType);
					sum += getOrderSum(room, extraBed, roomPrices, roomsMeta, true);
				}
				noBaseAdults |= !hasBaseAdults;
			}
			// Если была ошилка - вернуть сообщение об ошибке
			ResultPE result = getResult(ROOM_SEARCH);
			if (wrongKidVoucher)
				result.addVariable(MESSAGE, "Внимание! Санаторно-курортная путевка не доступна для детей");
			if (noBaseAdults)
				result.addVariable(MESSAGE, "Внимание! В номере основное место должен обязательно занимать хотя-бы один взрослый");
			if (wrongAdultPlacement)
				result.addVariable(MESSAGE, "Ребенок не должен занимать основное место если дополнительное место занято взрослым");
			if (isBlankVoucher)
				result.addVariable(MESSAGE, "Внимание! Выберите, пожалуйста, тип путевки для каждого отдыхающего");
			if (wrongKidVoucher || noBaseAdults || wrongAdultPlacement || isBlankVoucher)
				return result;
			// Сохранение суммы и основной формы (анкета того кто платит)
			cart.setValue(ItemNames.order.SUM, (double) Math.round(sum * 100) / (double) 100);
			cart.setValue(ItemNames.order.MAIN_FORM, rooms.get(0).getLongValues(ItemNames.free_room.ORDER_FORM_BASE).get(0));
			getSessionMapper().saveTemporaryItem(cart);
			return getResult(ORDER);
			
		}
		
		// Зазазчик только платит
		
		else if (StringUtils.equalsIgnoreCase(action, PAY_ONLY)) {
			// Найти первую взрослую заявку в первой комнате
			Item mainForm = getSessionMapper().getItemSingle(cart.getLongValue(ItemNames.order.MAIN_FORM));
			ArrayList<Item> rooms = getSessionMapper().getItemsByParamValue(ItemNames.FREE_ROOM, ItemNames.free_room.ORDER_FORM_BASE,
					mainForm.getId());
			Item room = rooms.get(0);
			// Удалить новую основную форму из комнаты и вставить ее напрямую в заказ
			// а в комнату добавить новую взрослую форму
			Item newAdultForm = getSessionMapper().createSessionItem(ItemNames.ORDER_FORM, cart.getId());
			Item.updateParamValues(mainForm, newAdultForm, ItemNames.order_form.CITIZEN, ItemNames.order_form.PERSON_TYPE,
					ItemNames.order_form.VOUCHER_TYPE);
			getSessionMapper().saveTemporaryItem(newAdultForm);
			room.removeEqualValue(ItemNames.free_room.ORDER_FORM_BASE, mainForm.getId());
			room.setValue(ItemNames.free_room.ORDER_FORM_BASE, newAdultForm.getId());
			getSessionMapper().saveTemporaryItem(room);
			// Сохранение основной формы
			mainForm.setValue(ItemNames.order_form.PAY_ONLY, (byte) 1);
			getSessionMapper().saveTemporaryItem(mainForm);
			return getResult(ORDER).setVariable(MESSAGE, "Внимание, добавлена новая форма для заполнения");
		}
		
		// Заказчик и платит и едет сам
		
		else if (StringUtils.equalsIgnoreCase(action, PAY_AND_STAY)) {
			// Найти первую взрослую заявку в первой комнате
			ArrayList<Item> rooms = getSessionMapper().getItemsByName(ItemNames.FREE_ROOM, cart.getId());
			Item room = rooms.get(0);
			Item mainForm = getSessionMapper().getItemSingle(cart.getLongValue(ItemNames.order.MAIN_FORM));
			Item firstForm = null;
			for (Long formId : room.getLongValues(ItemNames.free_room.ORDER_FORM_BASE)) {
				Item form = getSessionMapper().getItemSingle(formId);
				if (StringUtils.equalsIgnoreCase(form.getStringValue(ItemNames.order_form.PERSON_TYPE), PERSON_TYPE_ADULT)) {
					firstForm = form;
					break;
				}
			}
			// Удалить новую основную форму из комнаты и вставить ее напрямую в заказ
			// а в комнату добавить новую взрослую форму
			room.removeEqualValue(ItemNames.free_room.ORDER_FORM_BASE, firstForm.getId());
			room.setValue(ItemNames.free_room.ORDER_FORM_BASE, mainForm.getId());
			mainForm.setValue(ItemNames.order_form.PAY_ONLY, (byte) 0);
			getSessionMapper().removeItems(firstForm.getId());
			getSessionMapper().saveTemporaryItem(mainForm);
			getSessionMapper().saveTemporaryItem(room);
			return getResult(ORDER).setVariable(MESSAGE, "Внимание, из первой комнаты удалена лишняя форма");
		}
		
		// Подтвердить заявку
		
		else if (StringUtils.equalsIgnoreCase(action, SUBMIT)) {
			//return getResult("error_order");
			ArrayList<Item> forms = getSessionMapper().getItemsByName(ItemNames.ORDER_FORM, cart.getId());
			// Проверка
			for (Item form : forms) {
				if (isBlankForm(form))
					continue;
				for (String paramName : MANDATORY_FORM_PARAMS) {
					if (form.getParameterByName(paramName).isEmpty()
							&& form.getByteValue(ItemNames.order_form.IS_CONTRACTOR, (byte) 0) == (byte) 1)
						return getResult(ORDER).setVariable(MESSAGE, "Заполните, пожалуйста, все поля анкеты");
				}
				if (form.getByteValue(ItemNames.order_form.IS_CONTRACTOR, (byte) 0) == (byte) 1
						&& form.getParameterByName(ItemNames.order_form.EMAIL).isEmpty()) {
					return getResult(ORDER).setVariable(MESSAGE, "Заполните, пожалуйста, все поля анкеты");
				}
				if (cart.getByteValue(ItemNames.order.CONTRACT_AGREED, (byte)0) == (byte)0)
					return getResult(ORDER).setVariable(MESSAGE, "Ознакомьтесь, пожалуйста, с договором и поставьте отметку об этом в анкете");
			}
			// Сохранение заказа
			Item booking = ItemUtils.ensureSingleRootItem(ItemNames.BOOKING, User.getDefaultUser(), false);
			Item orders = ItemUtils.ensureSingleItem(ItemNames.ORDERS, booking.getId(), User.getDefaultUser(), false);
			ArrayList<Item> rooms = getSessionMapper().getItemsByName(ItemNames.FREE_ROOM, cart.getId());
			
			// Сохранение самого заказа
			long oldCartId = cart.getId();
			cart.setId(Item.DEFAULT_ID);
			cart.setDirectParentId(orders.getId());
			cart.setValue(ItemNames.order.RECEIVED_DATE, DateTime.now(DateTimeZone.UTC).getMillis());
			// Т. к. ID главной формы будет меняться, его надо сохранить
			long oldMainFormId = cart.getLongValue(ItemNames.order.MAIN_FORM);
			cart.removeValue(ItemNames.order.MAIN_FORM);
			executeCommandUnit(new SaveNewItemDBUnit(cart, false));
			
			// Сохранение форм
			HashMap<Long, Item> formsByOldIds = new HashMap<Long, Item>();
			for (Item form : forms) {
				if (isBlankForm(form))
					continue;
				long oldId = form.getId();
				form.setId(Item.DEFAULT_ID);
				form.setDirectParentId(cart.getId());
				executeCommandUnit(new SaveNewItemDBUnit(form, false));
				formsByOldIds.put(oldId, form);
			}
			
			// Сохранение комнат
			for (Item room : rooms) {
				room.setId(Item.DEFAULT_ID);
				room.setDirectParentId(cart.getId());
				ArrayList<Long> baseOldOrderIds = room.getLongValues(ItemNames.free_room.ORDER_FORM_BASE);
				ArrayList<Long> extraOldOrderIds = room.getLongValues(ItemNames.free_room.ORDER_FORM_EXTRA);
				room.removeValue(ItemNames.free_room.ORDER_FORM_BASE);
				room.removeValue(ItemNames.free_room.ORDER_FORM_EXTRA);
				for (Long oldBaseId : baseOldOrderIds) {
					Item form = formsByOldIds.get(oldBaseId);
					if (form != null)
						room.setValue(ItemNames.free_room.ORDER_FORM_BASE, form.getId());
				}
				for (Long oldExtraId : extraOldOrderIds) {
					Item form = formsByOldIds.get(oldExtraId);
					if (form != null)
						room.setValue(ItemNames.free_room.ORDER_FORM_EXTRA, form.getId());
				}
				executeCommandUnit(new SaveNewItemDBUnit(room, false));
			}
			
			// Установка и сохранение нового ID главной формы в заказе
			cart.setValue(ItemNames.order.MAIN_FORM, formsByOldIds.get(oldMainFormId).getId());
			cart.setValue(ItemNames.order.STATUS, (byte)0); // новый заказ
			executeCommandUnit(new UpdateItemDBUnit(cart));
			
			// Коммит изменений
			commitCommandUnits();
			// Удаление заказа из сеанса
			getSessionMapper().removeItems(oldCartId);
			return getResult(CONFIRM).addVariable("order", cart.getId() + "");
		}
		
		// Загрузить по пин коду
		
		else if (StringUtils.equalsIgnoreCase(action, SUBMIT_PIN)) {
			String pin = getVarSingleValueDefault(PIN, "0");
			Item order = ItemQuery.loadSingleItemByParamValue(ItemNames.ORDER, ItemNames.order.PIN, pin);
			if (order == null)
				return getResult(ORDER).setVariable(MESSAGE, "Внимание, введен несуществующий пин-код. Регистрация невозможна")
						.setVariable("wrong_pin", "1");
			boolean formsAreNotFilled = false;
			if (itemVars != null) {
				for (ItemVariables post : itemVars.getItemPosts()) {
					Item form = ItemQuery.loadById(post.getItemId());
					for (String inputName : post.getPostedInputs()) {
						String paramName = StringUtils.substringAfter(inputName, "new_");
						form.setValueUI(paramName, post.getValue(inputName));
					}
					for (String paramName : MANDATORY_FORM_PARAMS) {
						if (form.getParameterByName(paramName).isEmpty()
								&& form.getByteValue(ItemNames.order_form.IS_CONTRACTOR, (byte) 0) == (byte) 1) {
							formsAreNotFilled = true;
							break;
						}
					}
					if (form.getByteValue(ItemNames.order_form.IS_CONTRACTOR, (byte) 0) == (byte) 1
							&& form.getParameterByName(ItemNames.order_form.EMAIL).isEmpty()) {
						formsAreNotFilled = true;
					}
					executeCommandUnit(new UpdateItemDBUnit(form));
				}
				commitCommandUnits();
			}
			if (formsAreNotFilled)
				return getResult(ORDER).setVariable(MESSAGE, "Заполните, пожалуйста, все поля анкеты").setVariable(PIN, pin);
			return getResult(CONFIRM).addVariable("order", order.getId() + "");
		}
		
		return getResult(ORDER);
	}

	/**
	 * Является ли форма пустой
	 * @param form
	 * @return
	 */
	private static boolean isBlankForm(Item form) {
		return StringUtils.isBlank(form.getStringValue(ItemNames.order_form.PERSON_TYPE));
	}
	/**
	 * Получить стоимость одной путевки в определенный номер
	 * @param room
	 * @param orderFrom
	 * @param roomPrices
	 * @param roomsMeta
	 * @param isExtra
	 * @return
	 */
	public static double getOrderSum(Item room, Item orderFrom, Map<Long, Item> roomPrices, Item roomsMeta, boolean isExtra) {
		if (isBlankForm(orderFrom))
			return 0d;
		LocalDate firstStart = new LocalDate(roomsMeta.getLongValue(ItemNames.rooms.FIRST_START), DateTimeZone.UTC);
		LocalDate secondStart = new LocalDate(roomsMeta.getLongValue(ItemNames.rooms.SECOND_START), DateTimeZone.UTC);
		LocalDate thirdStart = new LocalDate(roomsMeta.getLongValue(ItemNames.rooms.THIRD_START), DateTimeZone.UTC);
		double quotient = isExtra ? roomsMeta.getDoubleValue(ItemNames.rooms.EXTRA_QUOTIENT, 0.8d) : 1d;
		String paramName = "price";
		if (orderFrom.getStringValue(ItemNames.order_form.VOUCHER_TYPE).equalsIgnoreCase(SAN_VOUCHER))
			paramName += "_san";
		else
			paramName += "_ozd";
		if (orderFrom.getStringValue(ItemNames.order_form.CITIZEN).equalsIgnoreCase(RB_CITIZEN))
			paramName += "_bel";
		else if (orderFrom.getStringValue(ItemNames.order_form.CITIZEN).equalsIgnoreCase(EAES_CITIZEN))
			paramName += "_rus";
		else
			paramName += "_eur";
		LocalDate fromDate = new LocalDate(room.getLongValue(ItemNames.free_room.FROM), DateTimeZone.UTC);
		if ((fromDate.isAfter(firstStart) || fromDate.isEqual(firstStart)) && fromDate.isBefore(secondStart))
			paramName += "_first";
		else if ((fromDate.isAfter(secondStart) || fromDate.isEqual(secondStart)) && fromDate.isBefore(thirdStart))
			paramName += "_second";
		else
			paramName += "_third";
		double price = roomPrices.get(room.getLongValue(ItemNames.free_room.TYPE)).getDoubleValue(paramName);
		LocalDate toDate = new LocalDate(room.getLongValue(ItemNames.free_room.TO), DateTimeZone.UTC);
		int days = Days.daysBetween(fromDate, toDate).getDays();
		return days * price * quotient;
	}
	/**
	 * Установить валюту заказа в зависимости от гражданства контрагента
	 * @param order
	 * @param citizen
	 */
	public static void setOrderCitizen(Item order, String citizen, String citizenName) {
		order.setValue(ItemNames.order.CITIZEN, citizen);
		order.setValue(ItemNames.order.CITIZEN_NAME, citizenName);
		if (citizen.equalsIgnoreCase(RB_CITIZEN)) {
			order.setValue(ItemNames.order.CUR_CODE, BYN_CUR_CODE);
			order.setValue(ItemNames.order.CUR, BYN_CUR_NAME);
		} else if (citizen.equalsIgnoreCase(EAES_CITIZEN)) {
			order.setValue(ItemNames.order.CUR_CODE, RUB_CUR_CODE);
			order.setValue(ItemNames.order.CUR, RUB_CUR_NAME);
		} else {
			order.setValue(ItemNames.order.CUR_CODE, EUR_CUR_CODE);
			order.setValue(ItemNames.order.CUR, EUR_CUR_NAME);
		}
	}
}
