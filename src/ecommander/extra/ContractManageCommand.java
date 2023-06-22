package ecommander.extra;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import ecommander.application.extra.EmailUtils;
import ecommander.application.extra.ItemUtils;
import ecommander.application.extra.POIExcelWrapper;
import ecommander.application.extra.POIUtils;
import ecommander.application.extra.POIUtils.Cells;
import ecommander.application.extra.Pair;
import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.common.exceptions.EcommanderException;
import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.extra._generated.ItemNames;
import ecommander.extra._generated.Order;
import ecommander.extra._generated.Order_form;
import ecommander.model.datatypes.DateDataType;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.ItemVariablesContainer;
import ecommander.pages.elements.ItemVariablesContainer.ItemVariables;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.DeleteItemBDUnit;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;

public class ContractManageCommand extends Command {

	private static final String ACTION = "action";
	private static final String NEW_CONTRACT = "new_contract";
	private static final String ADD_FORM = "add_form";
	private static final String ADD_ROOM = "add_room";
	private static final String SAVE = "save";
	private static final String DELETE_ROOM = "delete_room";
	private static final String DELETE_FORM = "delete_form";
	private static final String SET_MAIN_FORM = "set_main_form";
	private static final String SEND_DOCS = "send_docs";
	private static final String CONFIRM = "confirm";
	private static final String DELETE_ORDER = "delete";
	private static final String SET_PAID = "paid";
	private static final String SET_NOT_PAID = "not_paid";
	
	private static final String CONTRACT = "contract";
	private static final String ORDER = "order";
	private static final String ROOMS = "ofroom";
	private static final String FORMS = "oform";
	private static final String ROOM_TYPE = "room_type";
	private static final String ROOM = "room";
	private static final String ROOMS_META = "rooms";
	private static final String FORM = "form";
	private static final String BOOKING = "booking";
	
	private static final String SAN_VOUCHER_FILE = "санаторно-курортную";
	private static final String OZD_VOUCHER_FILE = "оздоровительную";
	private static final String BASE_BED = "base";
	private static final String EXTRA_BED = "дополнительное место";
	
	private Item contract = null;
	private Item roomsMeta = null;
	private Item mainForm = null;
	private LinkedHashMap<Long, Item> rooms = null;
	private LinkedHashMap<Long, Item> forms = null;
	private LinkedHashMap<Long, Item> allItems = null;
	private LinkedHashMap<Long, Item> roomTypes = null;

	/**
	 * Комната (название) + данные по комнате => Тип путевки => основное или дополнительное место => количество единиц + стоимость
	 */
	private LinkedHashMap<String, Pair<LinkedHashMap<String, LinkedHashMap<String, Pair<Integer, Double>>>, Item>> billData 
		= new LinkedHashMap<String, Pair<LinkedHashMap<String,LinkedHashMap<String,Pair<Integer,Double>>>,Item>>();
	
	private static final HashMap<String, String> MONTH_NAMES = new HashMap<String, String>();
	static {
		MONTH_NAMES.put("01", "января");
		MONTH_NAMES.put("02", "февраля");
		MONTH_NAMES.put("03", "марта");
		MONTH_NAMES.put("04", "апреля");
		MONTH_NAMES.put("05", "мая");
		MONTH_NAMES.put("06", "июня");
		MONTH_NAMES.put("07", "июля");
		MONTH_NAMES.put("08", "августа");
		MONTH_NAMES.put("09", "сентября");
		MONTH_NAMES.put("10", "октября");
		MONTH_NAMES.put("11", "ноября");
		MONTH_NAMES.put("12", "декабря");
	}
	
	@Override
	public ResultPE execute() throws Exception {
		String action = getItemVariables().getExtra(ACTION);
		
		// Новый договор, просто создать новый заказ и перейти на его страницу
		if (StringUtils.equalsIgnoreCase(action, NEW_CONTRACT)) {
			Item booking = ItemUtils.ensureSingleRootItem(ItemNames.BOOKING, User.getDefaultUser(), false);
			Item orders = ItemUtils.ensureSingleItem(ItemNames.ORDERS, booking.getId(), User.getDefaultUser(), false);
			Order order = Order.get(Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.ORDER), orders));
			order.set_received_date(new DateTime(DateTimeZone.UTC).getMillis());
			order.set_status((byte)1);
			executeAndCommitCommandUnits(new SaveNewItemDBUnit(order));
			return getResult(CONTRACT).setVariable(ORDER, order.getId() + "");
		}
		
		// Установка значений полей класса
		initialize();
		// Удаление выполняется до сохранения значения полей
		if (StringUtils.equalsIgnoreCase(action, DELETE_ORDER)) {
			executeAndCommitCommandUnits(new DeleteItemBDUnit(contract));
			return getResult(BOOKING);
		}
		// Сохранить все поля всех элементов заказа
		saveContractNoCommit();
		
		if (StringUtils.equalsIgnoreCase(action, SAVE)) {
			
		}

		else if (StringUtils.equalsIgnoreCase(action, ADD_FORM)) {
			ItemType formType = ItemTypeRegistry.getItemType(ItemNames.ORDER_FORM);
			Item form = Item.newChildItem(formType, contract);
			executeCommandUnit(new SaveNewItemDBUnit(form, false));
		}

		else if (StringUtils.equalsIgnoreCase(action, DELETE_FORM)) {
			long formId = NumberUtils.toLong(getVarSingleValue(FORM), 0);
			Item form  = forms.get(formId);
			if (form == null)
				return getResult(CONTRACT);
			// Найти комнату с этой формой и удалить ее значение из комнаты
			for (Item room : rooms.values()) {
				boolean removed = false;
				if (room.containsValue(ItemNames.free_room.ORDER_FORM_BASE, formId)) {
					room.removeEqualValue(ItemNames.free_room.ORDER_FORM_BASE, formId);
					removed = true;
				}
				if (room.containsValue(ItemNames.free_room.ORDER_FORM_EXTRA, formId)) {
					room.removeEqualValue(ItemNames.free_room.ORDER_FORM_EXTRA, formId);
					removed = true;
				}
				if (removed)
					executeCommandUnit(new UpdateItemDBUnit(room));
			}
			// Удалить форму
			executeCommandUnit(new DeleteItemBDUnit(form));
		}
		
		else if (StringUtils.equalsIgnoreCase(action, SET_MAIN_FORM)) {
			Item newMainForm = forms.get(NumberUtils.toLong(getVarSingleValue(FORM), 0));
			if (newMainForm == null)
				return getResult(CONTRACT);
			CartManageCommand.setOrderCitizen(contract, newMainForm.getStringValue(ItemNames.order_form.CITIZEN),
					newMainForm.getStringValue(ItemNames.order_form.CITIZEN_NAME));
			contract.setValue(ItemNames.order.MAIN_FORM, newMainForm.getId());
			executeCommandUnit(new UpdateItemDBUnit(contract));
		}
		
		else if (StringUtils.equalsIgnoreCase(action, ADD_ROOM)) {
			long roomId = NumberUtils.toLong(getVarSingleValue(ROOM_TYPE), 0);
			Item roomType = roomTypes.get(roomId);
			if (roomType == null)
				return getResult(CONTRACT);
			ItemType freeRoomType = ItemTypeRegistry.getItemType(ItemNames.FREE_ROOM);
			Item newRoom = Item.newChildItem(freeRoomType, contract);
			newRoom.setValue(ItemNames.free_room.TYPE, roomId);
			newRoom.setValue(ItemNames.free_room.TYPE_NAME, roomType.getStringValue(ItemNames.room.NAME));
			executeCommandUnit(new SaveNewItemDBUnit(newRoom, false));
		}

		else if (StringUtils.equalsIgnoreCase(action, DELETE_ROOM)) {
			long roomId = NumberUtils.toLong(getVarSingleValue(ROOM), 0);
			Item room  = rooms.get(roomId);
			if (room == null)
				return getResult(CONTRACT);
			executeCommandUnit(new DeleteItemBDUnit(room));
			rooms.remove(room.getId());
		}
		
		else if (StringUtils.equalsIgnoreCase(action, SET_PAID)) {
			contract.setValue(ItemNames.order.STATUS, (byte)3);
			executeAndCommitCommandUnits(new UpdateItemDBUnit(contract));
		}
		
		else if (StringUtils.equalsIgnoreCase(action, SET_NOT_PAID)) {
			contract.setValue(ItemNames.order.STATUS, (byte)2);
			executeAndCommitCommandUnits(new UpdateItemDBUnit(contract));
		}
		
		recalculate();

		if (StringUtils.equalsIgnoreCase(action, SEND_DOCS)) {
			if (!sendEmail())
				return getResult(CONTRACT).addVariable("message",
						"Ошибка отправки письма, проверьте правильность электронной почты контрагента");
			executeAndCommitCommandUnits(new UpdateItemDBUnit(contract));
		}

		else if (StringUtils.equalsIgnoreCase(action, CONFIRM)) {
			if (contract.getParameterByName(ItemNames.order.NUM).isEmpty()
					|| contract.getParameterByName(ItemNames.order.PAY_UNTIL_DATE).isEmpty()) {
				return getResult(CONTRACT).addVariable("message", "Невозможно подтвердить договор, не задан номер договора или срок оплаты");
			}
			DateTime now = new DateTime(DateTimeZone.UTC);
			contract.setValue(ItemNames.order.CONTRACT_DATE, now.getMillis());
			if (!sendEmail())
				return getResult(CONTRACT).addVariable("message",
						"Ошибка отправки письма, проверьте правильность электронной почты контрагента. Заявка не подтверждена");
			contract.setValue(ItemNames.order.STATUS, (byte)2);
			executeAndCommitCommandUnits(new UpdateItemDBUnit(contract));
		}
		
		return getResult(CONTRACT);
	}

	
	/**
	 * Сохранить текущее состояние заказа
	 * @return
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void saveContractNoCommit() throws NumberFormatException, Exception {
		ItemVariablesContainer itemVars = getItemVariables();
		// Обновить параметры айтемов, которые редактируются с помощью инпутов
		if (itemVars != null) {
			for (ItemVariables post : itemVars.getItemPosts()) {
				Item item = allItems.get(post.getItemId());
				// Удалить ассоциированные формы для всех комнат
				// т. к. они каждый раз устанавливаются заново
				if (item.getTypeName().equals(ItemNames.FREE_ROOM)) {
					item.removeValue(ItemNames.free_room.ORDER_FORM_BASE);
					item.removeValue(ItemNames.free_room.ORDER_FORM_EXTRA);
				}
				for (String inputName : post.getPostedInputs()) {
					String paramName = StringUtils.substringAfter(inputName, "new_");
					for (String value : post.getValues(inputName)) {
						item.setValueUI(paramName, value);						
					}
				}
			}
		}
		
		// Установить одинаковое гражданство во всех формах (как у главной формы)
		if (mainForm != null) {
			String citizen = mainForm.getStringValue(ItemNames.order_form.CITIZEN);
			String citizenName = mainForm.getStringValue(ItemNames.order_form.CITIZEN_NAME);
			CartManageCommand.setOrderCitizen(contract, citizen, citizenName);
			for (Item form : forms.values()) {
				form.setValue(ItemNames.order_form.CITIZEN, citizen);
				form.setValue(ItemNames.order_form.CITIZEN_NAME, citizenName);
			}
		}
		
		// Установить статус договора в значение 1 или 2
		byte status = contract.getByteValue(ItemNames.order.STATUS, (byte)0);
		if (status == (byte)0)
			contract.setValue(ItemNames.order.STATUS, (byte)1);
		
		// Сохранить изменения параметров всех айтемов
		for (Item item : allItems.values()) {
			executeCommandUnit(new UpdateItemDBUnit(item));
		}
	}
	
	/**
	 * Пересчитать стоимость всех путевок и установить результат в заказ
	 * @throws Exception 
	 */
	private void recalculate() throws Exception {
		double sum = 0;
		for (Item room : rooms.values()) {
			for (Long formId : room.getLongValues(ItemNames.free_room.ORDER_FORM_BASE)) {
				Item baseBed = forms.get(formId);
				double formSum = CartManageCommand.getOrderSum(room, baseBed, roomTypes, roomsMeta, false);
				addBillData(baseBed, room, formSum, false);
				sum += formSum;
			}
			for (Long formId : room.getLongValues(ItemNames.free_room.ORDER_FORM_EXTRA)) {
				Item extraBed = forms.get(formId);
				double formSum = CartManageCommand.getOrderSum(room, extraBed, roomTypes, roomsMeta, true);
				addBillData(extraBed, room, formSum, true);
				sum += formSum;
			}
		}
		// Для белорусских рублей выполнять округление до копеек,
		// для остальных валют - до целых значений
		String curCode = contract.getStringValue(ItemNames.order.CUR_CODE);
		if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.BYN_CUR_CODE)) {
			sum = Math.round(sum * 100d) / 100d;
		} else {
			sum = Math.round(sum);			
		}
		contract.setValue(ItemNames.order.SUM, sum);
		executeAndCommitCommandUnits(new UpdateItemDBUnit(contract));
	}
	
	private void addBillData(Item form, Item room, double sum, boolean isExtra) {
		String voucherType = form.getStringValue(ItemNames.order_form.VOUCHER_TYPE);
		String roomName = room.getStringValue(ItemNames.free_room.TYPE_NAME);
		// Тип комнаты
		Pair<LinkedHashMap<String, LinkedHashMap<String, Pair<Integer, Double>>>, Item> roomInfo = billData.get(roomName);
		if (roomInfo == null) {
			roomInfo = new Pair<LinkedHashMap<String, LinkedHashMap<String, Pair<Integer, Double>>>, Item>(
					new LinkedHashMap<String, LinkedHashMap<String, Pair<Integer, Double>>>(), room);
			billData.put(roomName, roomInfo);
		}
		// Тип путевки
		LinkedHashMap<String, Pair<Integer, Double>> voucherInfo = roomInfo.getLeft().get(voucherType);
		if (voucherInfo == null) {
			voucherInfo = new LinkedHashMap<String, Pair<Integer,Double>>();
			roomInfo.getLeft().put(voucherType, voucherInfo);
		}
		// Основное / дополнительное место
		String bedType = isExtra ? EXTRA_BED : BASE_BED;
		Pair<Integer, Double> bedInfo = voucherInfo.get(bedType);
		if (bedInfo == null) {
			bedInfo = new Pair<Integer, Double>(1, sum);
			voucherInfo.put(bedType, bedInfo);
		} else {
			bedInfo.setLeft(bedInfo.getLeft() + 1);
		}
	}
	
	private void initialize() {
		contract = getSingleLoadedItem(ORDER);
		roomsMeta = getSingleLoadedItem(ROOMS_META);
		rooms = getLoadedItems(ROOMS);
		forms = getLoadedItems(FORMS);
		roomTypes = getLoadedItems(ROOM_TYPE);
		allItems = new LinkedHashMap<Long, Item>();
		allItems.putAll(rooms);
		allItems.putAll(forms);
		allItems.put(contract.getId(), contract);
		mainForm = forms.get(contract.getLongValue(ItemNames.order.MAIN_FORM));
	}
	
	/**
	 * Создает doc файл с договором
	 * @throws Exception
	 */
	private void createDoc(Item booking) throws Exception {
		if (booking == null)
			return;
		XWPFDocument doc = new XWPFDocument(
				new FileInputStream(booking.getFileValue(ItemNames.booking.CONTRACT_TEMPLATE, AppContext.getFilesDirPath())));
		
		////////////////////////////////////////
		// Комнаты и цена
		//
		XWPFRun run = POIUtils.findSingleDocRun(doc, "voucherstart");
		XWPFParagraph textParagraph = (XWPFParagraph) run.getParent();
		String text = textParagraph.getText();
		for (int i = textParagraph.getRuns().size() - 1; i > 0; i--) {
			textParagraph.removeRun(i);
		}
		
		// Путевки
		int qtySan = 0;
		int qtyOzd = 0;
		for (Item form : forms.values()) {
			if (form.containsValue(ItemNames.order_form.VOUCHER_TYPE, CartManageCommand.SAN_VOUCHER)) {
				if (!form.containsValue(ItemNames.order_form.PAY_ONLY, (byte)1))
					qtySan++;
			} else {
				if (!form.containsValue(ItemNames.order_form.PAY_ONLY, (byte)1))
					qtyOzd++;
			}
		}
		String voucherTemplate = StringUtils.substringBetween(text, "voucherstart", "voucherend");
		String voucherText = "";
		if (qtySan > 0) {
			voucherText = StringUtils.replace(voucherTemplate, "vtype", SAN_VOUCHER_FILE);
			voucherText = StringUtils.replace(voucherText, "vqty", qtySan + "");
		} 
		if (StringUtils.isNotBlank(voucherText) && qtyOzd > 0)
			voucherText += ", ";
		if (qtyOzd > 0) {
			voucherText += StringUtils.replace(voucherTemplate, "vtype", OZD_VOUCHER_FILE);
			voucherText = StringUtils.replace(voucherText, "vqty", qtyOzd + "");
		}
		
		// Комнаты
		String roomTemplate = StringUtils.substringBetween(text, "roomstart", "roomend");
		String roomsText = "";
		for (Item room : rooms.values()) {
			String fromDate = DateDataType.outputDate(room.getLongValue(ItemNames.free_room.FROM), DateDataType.DAY_FORMATTER);
			String toDate = DateDataType.outputDate(room.getLongValue(ItemNames.free_room.TO), DateDataType.DAY_FORMATTER);
			DateTime from = new DateTime(room.getLongValue(ItemNames.free_room.FROM), DateTimeZone.UTC);
			DateTime to = new DateTime(room.getLongValue(ItemNames.free_room.TO), DateTimeZone.UTC);
			int days = Days.daysBetween(from.toLocalDate(), to.toLocalDate()).getDays();
			if (StringUtils.isNotBlank(roomsText))
				roomsText += ", ";
			String roomName = room.getStringValue(ItemNames.free_room.TYPE_NAME);
			roomName = "«" + StringUtils.replace(roomName, " номер", "") + "»";
			String roomText = StringUtils.replace(roomTemplate, "rname", roomName);
			roomText = StringUtils.replace(roomText, "rfrom", writeDate(fromDate));
			roomText = StringUtils.replace(roomText, "rto", writeDate(toDate));
			roomText = StringUtils.replace(roomText, "rdays", days + "");
			roomsText += roomText;
		}
		
		// Общая стоимость путевок
		
		String sumText = StringUtils.substringAfter(text, "roomend");
		double sum = contract.getDoubleValue(ItemNames.order.SUM);
		// Для белорусских рублей выполнять округление до копеек,
		// для остальных валют - до целых значений
		String curCode = contract.getStringValue(ItemNames.order.CUR_CODE);
		Object[] sums = writeSumAndCurrency(sum, curCode);
		String priceText = sums[0] + " (" + Strings.numberToRusWords((Long)sums[0]) + ") " + sums[1];
		if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.BYN_CUR_CODE)) {
			priceText += " " + sums[2] + " (" + Strings.numberToRusWords((Long)sums[2]) + ") " + sums[3];
		}
		sumText = StringUtils.replace(sumText, "csum", priceText);

		// Установка строки в абзац про путевки, комнаты и стоимость
		textParagraph.getRuns().get(0).setText(voucherText + roomsText + sumText, 0);
		//
		// КОНЕЦ Комнаты и цена
		////////////////////////////////////////
		
		////////////////////////////////////////
		// Список отдыхающих
		//
		XWPFTable personTable = doc.getTables().get(0);
		int rowNum = 0;
		for (Item item : forms.values()) {
			Order_form form = Order_form.get(item); 
			if (form.contains_pay_only((byte)1))
				continue;
			XWPFTableRow row = personTable.getRow(rowNum + 1);
			row.getCell(1).getParagraphs().get(0).getRuns().get(0)
					.setText(form.get_last_name() + " " + form.get_first_name() + " " + form.get_second_name(), 0);
			row.getCell(2).getParagraphs().get(0).getRuns().get(0)
					.setText(DateDataType.outputDate(form.get_birth_date(), DateDataType.DAY_FORMATTER), 0);
			row.getCell(3).getParagraphs().get(0).getRuns().get(0).setText(form.get_citizen_name(), 0);
			rowNum++;
		}
		// удалить лишние колонки из таблицы
		for (int i = personTable.getNumberOfRows() - 1; i > rowNum; i--) {
			personTable.removeRow(i);
		}
		//
		// КОНЕЦ Список отдыхающих
		////////////////////////////////////////
		
		////////////////////////////////////////
		// Простые текстовые данные
		//
		POIUtils.replaceDocTextDirect(doc, "cnumber", contract.getStringValue(ItemNames.order.NUM));
		POIUtils.replaceDocTextDirect(doc, "cdate",
				writeDate(DateDataType.outputDate(contract.getLongValue(ItemNames.order.CONTRACT_DATE), DateDataType.DAY_FORMATTER)));
		
		Order_form mf = Order_form.get(mainForm);
		POIUtils.replaceDocTextDirect(doc, "anamefull", mf.get_last_name() + " " + mf.get_first_name() + " " + mf.get_second_name());
		POIUtils.replaceDocTextDirect(doc, "acitizen", mf.get_citizen_name());
		POIUtils.replaceDocTextDirect(doc, "aaddress", mf.get_address());
		POIUtils.replaceDocTextDirect(doc, "apassport", mf.get_passport());
		POIUtils.replaceDocTextDirect(doc, "aissued", mf.get_passport_issued());
		//POIUtils.replaceDocTextDirect(doc, "aid", mf.get_id());
		//
		// КОНЕЦ Простые текстовые данные
		////////////////////////////////////////
		
		
//		RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT);
//		System.out.println(nf.format(1234567));		


		File docFile = new File(AppContext.getFilesDirPath() + booking.getPredecessorsAndSelfPath() + "contract.docx");
		doc.write(new FileOutputStream(docFile));
		contract.setValue(ItemNames.order.CONTRACT, docFile);
	}
	
	/**
	 * Создать счет-фактуру по договору
	 * @throws Exception 
	 */
	private void createBillXls(Item booking) throws Exception {
		if (booking == null)
			return;
		File template = null;
		String curCode = contract.getStringValue(ItemNames.order.CUR_CODE);
		if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.EUR_CUR_CODE))
			template = booking.getFileValue(ItemNames.booking.BILL_EUR,	AppContext.getFilesDirPath());
		else if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.RUB_CUR_CODE))
			template = booking.getFileValue(ItemNames.booking.BILL_RUS,	AppContext.getFilesDirPath());
		else
			template = booking.getFileValue(ItemNames.booking.BILL_BEL,	AppContext.getFilesDirPath());
		if (template == null || !template.exists())
			throw new EcommanderException("There is no template file for bill");

		// Скопировать файл шаблона в договор
		contract.setValue(ItemNames.order.BILL, template);
		executeCommandUnit(new UpdateItemDBUnit(contract));
		
		// Открытие шаблона документа excel
		POIExcelWrapper excel = POIExcelWrapper.create(contract.getFileValue(ItemNames.order.BILL, AppContext.getFilesDirPath()));
		XSSFWorkbook wb = (XSSFWorkbook) excel.getWorkbook();
		XSSFSheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());
		
		////////////////////////////////////////
		// Список позиций для оплаты
		//
		Cells firstItemRowCoords = POIUtils.findRowContaining(sheet, "biname").get(0);
		int itemRowNum = firstItemRowCoords.row;
		for (String roomName : billData.keySet()) {
			Pair<LinkedHashMap<String, LinkedHashMap<String, Pair<Integer, Double>>>, Item> roomInfo = billData.get(roomName);
			for (String voucherType : roomInfo.getLeft().keySet()) {
				LinkedHashMap<String, Pair<Integer, Double>> voucherInfo = roomInfo.getLeft().get(voucherType);
				for (String bedType : voucherInfo.keySet()) {
					Pair<Integer, Double> bedInfo = voucherInfo.get(bedType);
					
					Item room = roomInfo.getRight();
					LocalDate fromDate = new LocalDate(room.getLongValue(ItemNames.free_room.FROM), DateTimeZone.UTC);
					LocalDate toDate = new LocalDate(room.getLongValue(ItemNames.free_room.TO), DateTimeZone.UTC);
					int days = Days.daysBetween(fromDate, toDate).getDays();
					String daysStr = days + Strings.numberEnding(days, " день", " дня", " дней");
					String fromStr = DateDataType.outputDate(room.getLongValue(ItemNames.free_room.FROM), DateDataType.DAY_FORMATTER);
					String bedTypeStr = StringUtils.equalsIgnoreCase(bedType, EXTRA_BED) ? " (" + EXTRA_BED + ") скидка 20%" : "";
					String citizenStr = StringUtils.equalsIgnoreCase(contract.getStringValue(ItemNames.order.CITIZEN),
							CartManageCommand.RB_CITIZEN) ? "" : " для нерезидентов РБ";
					String itemName = voucherType + " путевка с " + fromStr + "г на " + daysStr + " в " + roomName + citizenStr + bedTypeStr;
					
					double bedPrice = bedInfo.getRight();
					double bedSum = bedInfo.getRight() * bedInfo.getLeft();
					
					POIUtils.replaceXlsTextDirect(sheet, itemRowNum, "biname", itemName);
					POIUtils.replaceXlsTextDirect(sheet, itemRowNum, "biqty", bedInfo.getLeft().toString());
					POIUtils.replaceXlsTextDirect(sheet, itemRowNum, "biprice", writeSum(bedPrice, curCode));
					POIUtils.replaceXlsTextDirect(sheet, itemRowNum, "bidiscount", "0");
					POIUtils.replaceXlsTextDirect(sheet, itemRowNum, "bipricediscount", writeSum(bedPrice, curCode));
					POIUtils.replaceXlsTextDirect(sheet, itemRowNum, "bisum", writeSum(bedSum, curCode));
					POIUtils.replaceXlsTextDirect(sheet, itemRowNum, "bisumnds", writeSum(bedSum, curCode));
					
					itemRowNum++;
				}
			}
		}
		// Удалить лишние строки
		ArrayList<Cells> blankItemRowCoords = POIUtils.findRowContaining(sheet, "biname");
		for (Cells cells : blankItemRowCoords) {
			XSSFRow row = sheet.getRow(cells.row);
			Iterator<Cell> cellIter = row.cellIterator();
			while (cellIter.hasNext()) {
				Cell cell = cellIter.next();
				cell.setCellValue("");
			}
			sheet.getRow(cells.row).setZeroHeight(true);
		}
		//
		// КОНЕЦ Список позиций для оплаты
		////////////////////////////////////////		
		
		// Одиночные строковые занчения
		String[] parts = StringUtils.split(contract.outputValues(ItemNames.order.CONTRACT_DATE).get(0), ".");
		String dayMonth = parts[0] + " " + MONTH_NAMES.get(parts[1]);
		String y1 = StringUtils.substring(parts[2], 0, 2);
		String y2 = StringUtils.substring(parts[2], 2);
		double sum = contract.getDoubleValue(ItemNames.order.SUM);
		Object[] sums = writeSumAndCurrency(contract.getDoubleValue(ItemNames.order.SUM), contract.getStringValue(ItemNames.order.CUR_CODE));
		
		String sumText = StringUtils.capitalize(Strings.numberToRusWords((Long)sums[0])) + " " + sums[1];
		if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.BYN_CUR_CODE))
			sumText += " " + Strings.numberToRusWords((Long)sums[2]) + " " + sums[3];
		
		POIUtils.replaceXlsTextDirect(sheet, "bnum", contract.getStringValue(ItemNames.order.NUM));
		POIUtils.replaceXlsTextDirect(sheet, "bdaymonth", dayMonth);
		POIUtils.replaceXlsTextDirect(sheet, "by1", y1);
		POIUtils.replaceXlsTextDirect(sheet, "by2", y2);
		POIUtils.replaceXlsTextDirect(sheet, "anamefull", mainForm.getStringValue(ItemNames.order_form.LAST_NAME) + " "
				+ mainForm.getStringValue(ItemNames.order_form.FIRST_NAME) + " " + mainForm.getStringValue(ItemNames.order_form.SECOND_NAME));
		POIUtils.replaceXlsTextDirect(sheet, "aaddress", mainForm.getStringValue(ItemNames.order_form.ADDRESS));
		POIUtils.replaceXlsTextDirect(sheet, "bsumnds", writeSum(sum, curCode));
		POIUtils.replaceXlsTextDirect(sheet, "bsumwords", sumText);
		
		// Генерация выходного файла
		File xlsFile = new File(AppContext.getFilesDirPath() + contract.getPredecessorsAndSelfPath() + "bill.xlsx");
		if (xlsFile.exists())
			xlsFile.delete();
		wb.write(new FileOutputStream(xlsFile));
		excel.close();
		contract.setValue(ItemNames.order.BILL, xlsFile.getName());
	}
	/**
	 * Отправить письмо с договором и счет-фактурой заказчику
	 * @return
	 * @throws Exception
	 */
	private boolean sendEmail() throws Exception {
		Item booking = ItemQuery.loadSingleItemByName(ItemNames.BOOKING);
		createDoc(booking);
		createBillXls(booking);
		try {
			// Создание письма
			Multipart mp = new MimeMultipart();
			// Текст письма из шаблона
			MimeBodyPart textPart = new MimeBodyPart();
			mp.addBodyPart(textPart);
			LinkPE emailTemplate = LinkPE.newDirectLink("link", "contract_email", false);
			emailTemplate.addStaticVariable("o", contract.getId() + "");
			ExecutablePagePE emailPage = getExecutablePage(emailTemplate.serialize());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PageController.newSimple().executePage(emailPage, bos);
			textPart.setContent(bos.toString("UTF-8"), emailPage.getResponseHeaders().get("Content-Type") + ";charset=UTF-8");
			bos.close();
			// Счет-фактура (Excel)
			File xlsx = contract.getFileValue(ItemNames.order.BILL, AppContext.getFilesDirPath());
			DataSource xlsxSource = new FileDataSource(xlsx); // , "application/vnd.ms-excel"
			MimeBodyPart xlsxPart = new MimeBodyPart();
			xlsxPart.setDataHandler(new DataHandler(xlsxSource));
			xlsxPart.setFileName(xlsx.getName());
			mp.addBodyPart(xlsxPart);
			// Договор (Word)
			File word = contract.getFileValue(ItemNames.order.CONTRACT, AppContext.getFilesDirPath());
			DataSource wordSource = new FileDataSource(word); // , "application/vnd.ms-excel"
			MimeBodyPart wordPart = new MimeBodyPart();
			wordPart.setDataHandler(new DataHandler(wordSource));
			wordPart.setFileName(word.getName());
			mp.addBodyPart(wordPart);
			
			// Отправка письма
			EmailUtils.sendGmailDefault(mainForm.getStringValue(ItemNames.order_form.EMAIL),
					"Подтверждение бронирования номеров санатория Спутник", mp);
		} catch (Exception e) {
			ServerLogger.error("Ошибка отправки заказа", e);
			return false;
		}
		return true;
	}
	
	private String writeDate(String formatted) {
		if (StringUtils.isBlank(formatted))
			return "";
		String[] parts = StringUtils.split(formatted, ".");
		return "«" + parts[0] + "» " + MONTH_NAMES.get(parts[1]) + " " + parts[2] + "г.";
	}
	/**
	 * Сумма целых значений валюты и дробных значений валюты и название этих частей
	 * В массиве
	 * 0 - целая сумма валюты
	 * 1 - название валюты
	 * 2 - дробная сумма валюты
	 * 3 - название дробной валюты
	 * @param sum
	 * @param curCode
	 * @return
	 */
	private Object[] writeSumAndCurrency(double sum, String curCode) {
		Object[] result = new Object[4];
		long integerPart = Math.round(Math.floor(sum));
		long fractionPart = Math.round((sum - integerPart) * 100d);
		result[0] = integerPart;
		result[2] = fractionPart;
		if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.RUB_CUR_CODE)) {
			result[1] = Strings.numberEnding(integerPart, "российский рубль", "российских рубля", "российских рублей");
			result[3] = Strings.numberEnding(fractionPart, "копейка", "копейки", "копеек");
		} else if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.EUR_CUR_CODE)) {
			result[1] = Strings.numberEnding(integerPart, "евро", "евро", "евро");
			result[3] = Strings.numberEnding(fractionPart, "цент", "цента", "центов");
		} else {
			result[1] = Strings.numberEnding(integerPart, "рубль", "рубля", "рублей");
			result[3] = Strings.numberEnding(fractionPart, "копейка", "копейки", "копеек");
		}
		return result;
	}
	
	private String writeSum(double sum, String curCode) {
		if (StringUtils.equalsIgnoreCase(curCode, CartManageCommand.BYN_CUR_CODE)) {
			return new BigDecimal(sum).setScale(2, RoundingMode.HALF_UP).toPlainString();
		}
		return Math.round(sum) + "";
	}
}
