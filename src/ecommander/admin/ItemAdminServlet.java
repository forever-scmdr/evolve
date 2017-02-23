package ecommander.admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.controllers.StartController;
import ecommander.output.AggregateMDWriter;
import ecommander.output.ItemTypeMDWriter;
import ecommander.output.LeafMDWriter;
import ecommander.output.MetaDataWriter;
import ecommander.output.ParameterDescriptionMDWriter;
import ecommander.model.DataModelBuilder;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.model.TypeHierarchy;
import ecommander.persistence.InPlaceTransaction;
import ecommander.persistence.commandunits.DeleteItemTypeBDUnit;
import ecommander.persistence.commandunits.DeleteParameterDescriptionBDUnit;
import ecommander.persistence.commandunits.ItemModelFilePersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveNewItemTypeDBUnit;
import ecommander.persistence.commandunits.SaveNewParameterDescriptionDBUnit;
import ecommander.persistence.commandunits.UpdateItemTypeDBUnit;
import ecommander.persistence.commandunits.UpdateParameterDescriptionDBUnit;
import ecommander.model.Domain;
import ecommander.model.DomainRegistry;

public class ItemAdminServlet extends BasicAdminServlet {
	
	public static final String INIT_PAGE = "init";
	public static final String NEW_ITEM_PAGE = "new_item";
	public static final String EDIT_ITEM_PAGE = "edit_item";
		
	public static final String ITEMS_TAG = "items";
	public static final String SELECTED_ITEM_TAG = "selected-itemdesc";
	public static final String UPDATING_ITEM_TAG = "updating-itemdesc";
	public static final String DATA_TAG = "data";
	public static final String DOMAINS_TAG = "domains";
	public static final String DOMAIN_TAG = "domain";
	
	public static final String INIT_ACTION = "admin_types_init";
	public static final String SET_ACTION = "admin_types_set";
	public static final String CREATE_ACTION = "admin_types_create";
	public static final String SAVE_NEW_ACTION = "admin_types_save_new";
	public static final String UPDATE_ACTION = "admin_types_update";
	public static final String PARAM_ORDER_ACTION = "admin_types_po_update";
	public static final String DELETE_ACTION = "admin_types_delete";
	public static final String SAVE_NEW_PARAM_ACTION = "admin_types_params_save_new";
	public static final String UPDATE_PARAM_ACTION = "admin_types_params_update";
	public static final String DELETE_PARAM_ACTION = "admin_types_params_delete";
	
	
	public static final String NAME_INPUT = "name";
	public static final String ID_INPUT = "id";
	public static final String NEW_ID_INPUT = "newId";
	public static final String CAPTION_INPUT = "caption";
	public static final String DESCRIPTION_INPUT = "description";
	public static final String TYPE_NAME_INPUT = "typeName";
	public static final String DOMAIN_NAME_INPUT = "domainName";
	public static final String PARAM_NAME_INPUT = "paramName";
	public static final String PARAM_ID_INPUT = "paramId";
	public static final String QUANTIFIER_INPUT = "quantifier";
	public static final String FORMAT_INPUT = "format";
	public static final String HIDDEN_INPUT = "hidden";
	public static final String EXTENDS_INPUT = "extends";
	public static final String PARAMETER_ORDER = "paramOrder";
	
	public static final byte NOT_SET = -1;
	
	public static final String EXT = ".type";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5895027369367150323L;

	private String name;
	private int id;
	private int newId;
	private String caption;
	private String description;
	private String typeName;
	private String domainName;
	private String paramName;
	private int paramId;
	private String quantifier;
	private String format;
	private boolean hidden;
	private String itemExtends;
	private ArrayList<Integer> paramOrder;
	
	@Override
	protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if (!checkUser(req, resp, INIT_ACTION + ".user")) return;
		start(req);
		String actionName = getAction(req);
		if (actionName.equalsIgnoreCase(INIT_ACTION))
			initialize(req, resp);
		else if (actionName.equalsIgnoreCase(SET_ACTION))
			setItem(req, resp);
		else if (actionName.equalsIgnoreCase(CREATE_ACTION))
			startCreateItem(req, resp);
		else if (actionName.equalsIgnoreCase(SAVE_NEW_ACTION))
			saveNewItem(req, resp);
		else if (actionName.equalsIgnoreCase(SAVE_NEW_PARAM_ACTION))
			saveNewParameter(req, resp);
		else if (actionName.equalsIgnoreCase(DELETE_PARAM_ACTION))
			deleteParameter(req, resp);
		else if (actionName.equalsIgnoreCase(UPDATE_PARAM_ACTION))
			updateParameter(req, resp);
		else if (actionName.equalsIgnoreCase(UPDATE_ACTION))
			updateItem(req, resp);
		else if (actionName.equalsIgnoreCase(PARAM_ORDER_ACTION))
			paramsOrder(req, resp);
		else if (actionName.equalsIgnoreCase(DELETE_ACTION))
			deleteItem(req, resp);
	}

	private void start(HttpServletRequest req) throws Exception {
		name = Strings.EMPTY;
		id = NOT_SET;
		caption = Strings.EMPTY;
		description = Strings.EMPTY;
		newId = NOT_SET;
		typeName = Strings.EMPTY;
		domainName = Strings.EMPTY;
		paramName = Strings.EMPTY;
		paramId = NOT_SET;
		quantifier = Strings.EMPTY;
		format = Strings.EMPTY;
		itemExtends = Strings.EMPTY;
		String paramOrderStr = Strings.EMPTY;
		
		// Старт приложения, если он еще не был осуществлен
		StartController.start(getServletContext());
		name = req.getParameter(NAME_INPUT);
		if (name != null) {
			caption = name;
			name = Strings.createXmlElementName(name.trim());
		}
		if (req.getParameter(ID_INPUT) != null)
			id = Integer.parseInt(req.getParameter(ID_INPUT));
		if (req.getParameter(NEW_ID_INPUT) != null)
			newId = Integer.parseInt(req.getParameter(NEW_ID_INPUT));
		if (req.getParameter(CAPTION_INPUT) != null)
			caption = req.getParameter(CAPTION_INPUT);
		description = req.getParameter(DESCRIPTION_INPUT);
		typeName = req.getParameter(TYPE_NAME_INPUT);
		domainName = req.getParameter(DOMAIN_NAME_INPUT);
		paramName = req.getParameter(PARAM_NAME_INPUT);
		if (req.getParameter(PARAM_ID_INPUT) != null)
			paramId = Integer.parseInt(req.getParameter(PARAM_ID_INPUT));
		quantifier = req.getParameter(QUANTIFIER_INPUT);
		format = req.getParameter(FORMAT_INPUT);
		hidden = Boolean.valueOf(req.getParameter(HIDDEN_INPUT));
		itemExtends = req.getParameter(EXTENDS_INPUT);
		paramOrderStr = req.getParameter(PARAMETER_ORDER);
		paramOrder = new ArrayList<Integer>();
		if (!StringUtils.isBlank(paramOrderStr)) {
			String[] paramIds = StringUtils.split(paramOrderStr, ',');
			for (String paramId : paramIds) {
				paramOrder.add(Integer.parseInt(paramId));
			}
		}
	}
	/**
	 * Инициализация
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void initialize(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		AdminPage page = createInitPage(getContextPath(req));
		page.addMessage("Выберите класс для редкатирования или создания подкласса", false);
		page.output(resp);
	}
	/**
	 * Выбор айтема для редактирования
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void setItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		AdminPage page = createEditPage(getContextPath(req));
		page.addMessage("Класс выбран для редактирования", false);
		page.output(resp);
	}
	/**
	 * Страница создания нового айтема
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void startCreateItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		AdminPage page = createCreatePage(getContextPath(req));
		page.addMessage("Для создания подкласса заполните поля и нажмите кнопку сохранения", false);
		page.output(resp);
	}
	/**
	 * Сохранение нового айтема
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void saveNewItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ItemType parent = ItemTypeRegistry.getItemType(id);
		ItemType test = ItemTypeRegistry.getItemType(name);
		AdminPage page = null;
		if (test != null) {
			page = createCreatePage(getContextPath(req));
			page.addMessage("Класс с заданным именем уже существует, выберите другое имя класса", true);
		} else if (StringUtils.isBlank(name)) {
			page = createCreatePage(getContextPath(req));
			page.addMessage("Не задано имя класса", true);
		}
		// Если была ошибка - выход
		if (page != null) {
			page.output(resp);
			return;
		}
		// Сохранение айтема
		final SaveNewItemTypeDBUnit saveCommand 
				= new SaveNewItemTypeDBUnit(name, caption, description, parent.getName(), parent.getKey(), parent.isInline());
		saveCommand.backup(); // бэкап файла
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(saveCommand);
			}
		}.execute();
		// Загрузка модели
		reloadModel(saveCommand);
		id = ItemTypeRegistry.getItemTypeId(name);
		page = createEditPage(getContextPath(req));
		page.addMessage("Класс успешно создан", false);
		page.output(resp);
	}
	/**
	 * Обновление айтема
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void updateItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ItemType item = ItemTypeRegistry.getItemType(id);
		ItemType test = ItemTypeRegistry.getItemType(name);
		AdminPage page = null;
		if (test != null && test.getTypeId() != id) {
			page = createEditPage(getContextPath(req));
			page.addMessage("Класс с заданным именем уже существует, выберите другое имя класса", true);
		} else if (StringUtils.isBlank(name)) {
			page = createEditPage(getContextPath(req));
			page.addMessage("Не задано имя класса", true);
		}
		// Если была ошибка - выход
		if (page != null) {
			page.output(resp);
			return;
		}
		// Сохранение айтема
		final UpdateItemTypeDBUnit updateCommand 
				= new UpdateItemTypeDBUnit(id, name, caption, description, itemExtends, "false", item.isInline(), paramOrder, item.getKey());
		updateCommand.backup(); // бэкап файла
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(updateCommand);
			}
		}.execute();
		// Загрузка модели
		reloadModel(updateCommand);
		id = ItemTypeRegistry.getItemTypeId(name);
		page = createEditPage(getContextPath(req));
		page.addMessage("Класс успешно сохранен", false);
		page.output(resp);
	}
	/**
	 * Обновление айтема
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void paramsOrder(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ItemType i = ItemTypeRegistry.getItemType(id);
		AdminPage page = null;
		String exts = StringUtils.join(ItemTypeRegistry.getDirectParents(i.getName()), ",");
		// Сохранение айтема
		final UpdateItemTypeDBUnit updateCommand = new UpdateItemTypeDBUnit(i.getTypeId(), i.getName(), i.getCaption(),
				i.getDescription(), exts, i.isVirtual() + "", i.isInline(), paramOrder, i.getKey());
		updateCommand.backup(); // бэкап файла
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(updateCommand);
			}
		}.execute();
		// Загрузка модели
		reloadModel(updateCommand);
		page = createEditPage(getContextPath(req));
		page.addMessage("Порядок следования параметров сохранен", false);
		page.output(resp);
	}
	/**
	 * Удаление айтема
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void deleteItem(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		AdminPage page = null;
		// Удаление айтема
		final DeleteItemTypeBDUnit deleteCommand = new DeleteItemTypeBDUnit(id, newId);
		deleteCommand.backup(); // бэкап файла
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(deleteCommand);
			}
		}.execute();
		// Загрузка модели
		reloadModel(deleteCommand);
		id = NOT_SET;
		newId = NOT_SET;
		page = createInitPage(getContextPath(req));
		page.addMessage("Класс успешно удален", false);
		page.output(resp);
	}	
	/**
	 * Сохранение нового параметра
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void saveNewParameter(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ItemType item = ItemTypeRegistry.getItemType(id);
		AdminPage page = null;
		if (StringUtils.isBlank(paramName) || StringUtils.isBlank(typeName) || StringUtils.isBlank(quantifier)) {
			page = createEditPage(getContextPath(req));
			page.addMessage("Заполните обязательные поля", true);
		} else if (item.getParameter(paramName) != null) {
			page = createEditPage(getContextPath(req));
			page.addMessage("Параметр с заданным именем уже существует в данном классе, выберите другое имя параметра", true);
		}
		// Если была ошибка - выход
		if (page != null) {
			page.output(resp);
			return;
		}
		// Сохранение айтема
		final SaveNewParameterDescriptionDBUnit saveCommand = new SaveNewParameterDescriptionDBUnit(paramName, item.getTypeId(), paramName,
				description, domainName, format, quantifier, typeName, false, hidden);
		saveCommand.backup(); // бэкап файла
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(saveCommand);
			}
		}.execute();
		// Загрузка модели
		reloadModel(saveCommand);
		// Сброс значений, чтобы они не выводились при выводе формы нового параметра
		paramName = "";
		description = "";
		domainName = "";
		format = "";
		quantifier = "";
		typeName = "";
		// Создание страницы
		page = createEditPage(getContextPath(req));
		page.addMessage("Новый параметр успешно создан", false);
		page.output(resp);
	}
	/**
	 * Обновление параметра
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void updateParameter(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ItemType item = ItemTypeRegistry.getItemType(id);
		AdminPage page = null;
		if (StringUtils.isBlank(paramName) || StringUtils.isBlank(typeName)) {
			page = createEditPage(getContextPath(req));
			page.addMessage("Заполните обязательные поля", true);
		}
		ParameterDescription param = item.getParameter(paramName);
		if (param != null && param.getId() != paramId) {
			page = createEditPage(getContextPath(req));
			page.addMessage("Параметр с заданным именем уже существует в данном классе, выберите другое имя параметра", true);
		}
		// Если была ошибка - выход
		if (page != null) {
			page.output(resp);
			return;
		}
		// Сохранение айтема
		final UpdateParameterDescriptionDBUnit updateCommand = new UpdateParameterDescriptionDBUnit(paramId, id, paramName, caption,
				description, domainName, format, quantifier, typeName, false, hidden);
		updateCommand.backup(); // бэкап файла
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(updateCommand);
			}
		}.execute();
		// Загрузка модели
		reloadModel(updateCommand);
		// Сброс значений, чтобы они не выводились при выводе формы нового параметра
		paramName = "";
		description = "";
		domainName = "";
		format = "";
		quantifier = "";
		typeName = "";
		// Создание страницы
		page = createEditPage(getContextPath(req));
		page.addMessage("Параметр успешно сохранен", false);
		page.output(resp);
	}
	/**
	 * Удаление параметра
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void deleteParameter(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		ItemType item = ItemTypeRegistry.getItemType(id);
		ParameterDescription param = item.getParameter(paramId);
		final DeleteParameterDescriptionBDUnit deleteCommand = new DeleteParameterDescriptionBDUnit(item.getTypeId(), param.getId());
		deleteCommand.backup(); // бэкап файла
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(deleteCommand);
			}
		}.execute();
		// Загрузка модели
		reloadModel(deleteCommand);
		// Создание страницы
		AdminPage page = createEditPage(getContextPath(req));
		page.addMessage("Параметр успешно удален", false);
		page.output(resp);
	}
	/**
	 * Загрузка модели
	 * @throws Exception 
	 */
	private void reloadModel(ItemModelFilePersistenceCommandUnit command) throws Exception {
		try {
			DataModelBuilder.tryLockAndReloadModel();
		} catch (Exception e) {
			ServerLogger.error("Error while updating model_custom.xml file", e);
			command.restore();
		}
	}

	public int getId() {
		return id;
	}
	/**
	 * Начальная страница, никакой айтем не выбран
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	private AdminPage createInitPage(String domain) throws Exception {
		AdminPage page = new AdminPage(INIT_PAGE, domain, getCurrentAdmin().getName());
		createItemHierarchy(page);
		page.addElement(new LeafMDWriter("create_link", CREATE_ACTION + EXT));
		return page;
	}
	/**
	 * Страница создания нового айтема, выбран родительский айтем
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	private AdminPage createEditPage(String domain) throws Exception {
		AdminPage page = new AdminPage(EDIT_ITEM_PAGE, domain, getCurrentAdmin().getName());
		createItemHierarchy(page);
		ItemType item = ItemTypeRegistry.getItemType(id);
		ItemTypeMDWriter itemWriter = null;
		itemWriter = writeItemFull(item, page);
		itemWriter.addSubwriter(new LeafMDWriter("delete_link", DELETE_ACTION + EXT + '?' + ID_INPUT + '=' + id));
		itemWriter.addSubwriter(new LeafMDWriter("update_link", UPDATE_ACTION + EXT + '?' + ID_INPUT + '=' + id));
		itemWriter.addSubwriter(new LeafMDWriter("param_order_link", PARAM_ORDER_ACTION + EXT + '?' + ID_INPUT + '=' + id));
		page.addElement(itemWriter);
		String createUrl = CREATE_ACTION + EXT + '?' + ID_INPUT + '=' + id;
		itemWriter.addSubwriter(new LeafMDWriter("create_link", createUrl));
		return page;
	}
	/**
	 * Страница с формой создания нового айтема
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	private AdminPage createCreatePage(String domain) throws Exception {
		AdminPage page = new AdminPage(NEW_ITEM_PAGE, domain, getCurrentAdmin().getName());
		createItemHierarchy(page);
		ItemType itemDesc = ItemTypeRegistry.getItemType(id);
		MetaDataWriter data = new AggregateMDWriter(DATA_TAG);
		data.addSubwriter(new LeafMDWriter(ID_INPUT, id));
		data.addSubwriter(new LeafMDWriter(NAME_INPUT, name));
		data.addSubwriter(new LeafMDWriter(DESCRIPTION_INPUT, description));
		page.addElement(data);
		page.addElement(new ItemTypeMDWriter(itemDesc, "parent-itemdesc"));
		String saveUrl = SAVE_NEW_ACTION + EXT + '?' + ID_INPUT + '=' + id;
		page.addElement(new LeafMDWriter("save_link", saveUrl));
		return page;
	}
	
	private void createItemHierarchy(AdminPage page) throws Exception {
		TypeHierarchy root = ItemTypeRegistry.getHierarchies(ItemTypeRegistry.getItemNames());
		AggregateMDWriter itemsWriter = new AggregateMDWriter(ITEMS_TAG, "");
		page.addElement(itemsWriter);
		HashSet<Integer> writtenItems = new HashSet<Integer>();
		for (TypeHierarchy firstLevel : root.getExtenders()) {
			writeItem(firstLevel, itemsWriter, writtenItems);
		}
	}
	
	private void writeItem(TypeHierarchy itemHierarchy, MetaDataWriter parent, HashSet<Integer> alreadyWritten) throws SQLException, Exception {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemHierarchy.getItemName());
		// Не выводить айтемы повторно
		if (alreadyWritten.contains(itemDesc.getTypeId()))
			return;
		alreadyWritten.add(itemDesc.getTypeId());
		ItemTypeMDWriter itemWriter = null;
		if (itemDesc.getTypeId() == id)
			itemWriter = new ItemTypeMDWriter(itemDesc, SELECTED_ITEM_TAG);
		else
			itemWriter = new ItemTypeMDWriter(itemDesc);
		parent.addSubwriter(itemWriter);
		itemWriter.addSubwriter(new LeafMDWriter("edit_link", SET_ACTION + EXT + '?' + ID_INPUT + '=' + itemDesc.getTypeId()));
		for (TypeHierarchy childName : itemHierarchy.getExtenders()) {
			writeItem(childName, itemWriter, alreadyWritten);
		}
	}
	
	private ItemTypeMDWriter writeItemFull(ItemType item, AdminPage page) throws Exception {
		ItemTypeMDWriter itemWriter = new ItemTypeMDWriter(item, UPDATING_ITEM_TAG);
		// Домены
		AggregateMDWriter domains = new AggregateMDWriter(DOMAINS_TAG);
		Iterator<Domain> domainIter = DomainRegistry.getAllDomainsIterator();
		while (domainIter.hasNext())
			domains.addSubwriter(new LeafMDWriter(DOMAIN_TAG, domainIter.next().getName()));
		page.addElement(domains);
		// Параметры
		for (ParameterDescription param : item.getParameterList()) {
			ParameterDescriptionMDWriter paramWriter = new ParameterDescriptionMDWriter(param);
			String updateUrl = 
					UPDATE_PARAM_ACTION + EXT + '?' + 
					ID_INPUT + '=' + id + '&' + 
					PARAM_ID_INPUT + '=' + param.getId();
			paramWriter.addSubwriter(new LeafMDWriter("update_link", updateUrl));
			String deleteUrl = 
					DELETE_PARAM_ACTION + EXT + '?' + 
					ID_INPUT + '=' + id + '&' + 
					PARAM_ID_INPUT + '=' + param.getId();
			paramWriter.addSubwriter(new LeafMDWriter("delete_link", deleteUrl));
			itemWriter.addSubwriter(paramWriter);
		}
		// Создание нового параметра
		MetaDataWriter data = new AggregateMDWriter(DATA_TAG);
		data.addSubwriter(new LeafMDWriter(ID_INPUT, id));
		data.addSubwriter(new LeafMDWriter(NAME_INPUT, paramName));
		data.addSubwriter(new LeafMDWriter(DESCRIPTION_INPUT, description));
		data.addSubwriter(new LeafMDWriter(TYPE_NAME_INPUT, typeName));
		data.addSubwriter(new LeafMDWriter(QUANTIFIER_INPUT, quantifier));
		data.addSubwriter(new LeafMDWriter(FORMAT_INPUT, format));
		data.addSubwriter(new LeafMDWriter(DOMAIN_NAME_INPUT, domainName));
		page.addElement(data);
		// Ссылка - создать новый параметр
		String createUrl = 
				SAVE_NEW_PARAM_ACTION + EXT + '?' + 
				ID_INPUT + '=' + id;
		itemWriter.addSubwriter(new LeafMDWriter("new_param_link", createUrl));
		return itemWriter;
	}
}
