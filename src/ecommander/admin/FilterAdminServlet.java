package ecommander.admin;

import ecommander.controllers.PageController;
import ecommander.controllers.StartController;
import ecommander.fwk.Strings;
import ecommander.model.*;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.filter.*;
import ecommander.pages.output.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.InPlaceTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
/**
 * TODO <enhance> Перенести все операции по обновлению в сами элементы фильтра. Необходимые проверки проводить там же (выдавать эксэпшен)
 * @author EEEE
 *
 */
public class FilterAdminServlet extends BasicAdminServlet {
	
	public static final String FILTER_PAGE = "filter";
	public static final String NEW_ITEM_PAGE = "new_item";
	public static final String EDIT_ITEM_PAGE = "edit_item";
	
	public static final String ITEMS_TAG = "items";
	public static final String DOMAINS_TAG = "domains";
	public static final String DOMAIN_TAG = "domain";
	public static final String FILTER_ITEM_TAG = "base-item";
	
	public static final String CREATE_FILTER_LINK_TAG = "create_filter_link";
	public static final String DELETE_GROUP_LINK_TAG = "delete_group_link";
	public static final String CREATE_GROUP_LINK_TAG = "create_group_link";
	public static final String UPDATE_GROUP_LINK_TAG = "update_group_link";
	public static final String DELETE_INPUT_LINK_TAG = "delete_input_link";
	public static final String CREATE_INPUT_LINK_TAG = "create_input_link";
	public static final String UPDATE_INPUT_LINK_TAG = "update_input_link";
	public static final String DELETE_CRIT_LINK_TAG = "delete_crit_link";
	public static final String CREATE_CRIT_LINK_TAG = "create_crit_link";
	public static final String UPDATE_CRIT_LINK_TAG = "update_crit_link";
	
	public static final String INIT_ACTION = "admin_filter_init";
	public static final String CREATE_FILTER_ACTION = "admin_filter_create";
	public static final String SAVE_NEW_GROUP_ACTION = "admin_filter_save_new_group";
	public static final String DELETE_GROUP_ACTION = "admin_filter_delete_group";
	public static final String UPDATE_GROUP_ACTION = "admin_filter_update_group";
	public static final String SAVE_NEW_INPUT_ACTION = "admin_filter_save_new_input";
	public static final String DELETE_INPUT_ACTION = "admin_filter_delete_input";
	public static final String UPDATE_INPUT_ACTION = "admin_filter_update_input";
	public static final String SAVE_NEW_CRIT_ACTION = "admin_filter_save_new_crit";
	public static final String DELETE_CRIT_ACTION = "admin_filter_delete_crit";
	public static final String UPDATE_CRIT_ACTION = "admin_filter_update_crit";
	
	public static final String NAME_INPUT = "name";
	public static final String SIGN_INPUT = "sign";
	public static final String TYPE_INPUT = "type";
	public static final String DOMAIN_INPUT = "domain";
	public static final String ITEM_ID_INPUT = "itemId";
	public static final String BASE_NAME_INPUT = "baseName";
	public static final String PARAM_NAME_INPUT = "paramName";
	public static final String CRIT_PARAM_NAME_INPUT = "critParamName";
	public static final String PATTERN_INPUT = "pattern";
	public static final String ID_INPUT = "id";
	public static final String DESCRIPTION_INPUT = "description";
	
	public static final byte NOT_SET = -1;
	public static final String STR_NOT_SET = "";
	
	public static final String EXT = ".afilter";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5895027369367150323L;

	private String name;
	private long itemId;
	private String baseName;
	private int id;
	private String description;
	private String paramName;
	private String critParamName;
	private String pattern;
	private String sign;
	private String type;
	private String domain;
	
	private FilterDefinition filter;
	private String filterStr;
	private Item item;
	
	@Override
	protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		if (!checkUser(req, resp, INIT_ACTION + ".user")) return;
		start(req);
		String actionName = getAction(req);
		if (actionName.equalsIgnoreCase(INIT_ACTION))
			initialize(req, resp);
		else if (actionName.equalsIgnoreCase(SAVE_NEW_GROUP_ACTION))
			saveNewGroup(resp);
		else if (actionName.equalsIgnoreCase(UPDATE_GROUP_ACTION))
			updateGroup(resp);
		else if (actionName.equalsIgnoreCase(SAVE_NEW_INPUT_ACTION))
			saveNewInput(resp);
		else if (actionName.equalsIgnoreCase(UPDATE_INPUT_ACTION))
			updateInput(resp);
		else if (actionName.equalsIgnoreCase(SAVE_NEW_CRIT_ACTION))
			saveNewCriteria(resp);
		else if (actionName.equalsIgnoreCase(UPDATE_CRIT_ACTION))
			updateCriteria(resp);
		else if (actionName.equalsIgnoreCase(DELETE_GROUP_ACTION) 
				|| actionName.equalsIgnoreCase(DELETE_CRIT_ACTION) 
				|| actionName.equalsIgnoreCase(DELETE_INPUT_ACTION))
			delete(resp);
		else if (actionName.equalsIgnoreCase(CREATE_FILTER_ACTION))
			createFilter(resp);
	}

	private void start(HttpServletRequest req) throws Exception {
		name = Strings.EMPTY;
		itemId = NOT_SET;
		description = Strings.EMPTY;
		baseName = STR_NOT_SET;
		paramName = STR_NOT_SET;
		id = NOT_SET;
		sign = Strings.EMPTY;
		item = null;
		type = Strings.EMPTY;
		domain = Strings.EMPTY;
		critParamName = Strings.EMPTY;
		pattern = Strings.EMPTY;
		
		// Старт приложения, если он еще не был осуществлен
		StartController.getSingleton().start(getServletContext());
		name = req.getParameter(NAME_INPUT);
		try {
			if (req.getParameter(ITEM_ID_INPUT) != null)
				itemId = Integer.parseInt(req.getParameter(ITEM_ID_INPUT));
		} catch (NumberFormatException e) { /* Ничего не делать*/ }
		description = req.getParameter(DESCRIPTION_INPUT);
		try {
			if (req.getParameter(PARAM_NAME_INPUT) != null)
				paramName = req.getParameter(PARAM_NAME_INPUT);
		} catch (NumberFormatException e) { /* Ничего не делать*/ }
		try {
			if (req.getParameter(ID_INPUT) != null)
				id = Integer.parseInt(req.getParameter(ID_INPUT));
		} catch (NumberFormatException e) { /* Ничего не делать*/ }
		try {
			if (req.getParameter(CRIT_PARAM_NAME_INPUT) != null)
				critParamName = req.getParameter(CRIT_PARAM_NAME_INPUT);
		} catch (NumberFormatException e) { /* Ничего не делать*/ }
		pattern = req.getParameter(PATTERN_INPUT);
		sign = req.getParameter(SIGN_INPUT);
		type = req.getParameter(TYPE_INPUT);
		domain = req.getParameter(DOMAIN_INPUT);
		
		
		// Загрузка айтема
		item = ItemQuery.loadById(itemId);
		filterStr = item.getStringValue(paramName, "");
		filter = FilterDefinition.create(filterStr);
		if (!filter.isEmpty())
			baseName = filter.getBaseItemName();
			
		// Считывание baseId
		try {
			if (req.getParameter(BASE_NAME_INPUT) != null)
				baseName = req.getParameter(BASE_NAME_INPUT);
		} catch (NumberFormatException e) { /* Ничего не делать*/ }
	}
	/**
	 * Инициализация
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private void initialize(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		AdminPage page = createPage();
		page.addMessage("Редактирование фильтра", false);
		page.output(resp);
	}
	/**
	 * Создать новый фильтр
	 * @param resp
	 * @throws Exception
	 */
	private void createFilter(HttpServletResponse resp) throws Exception {
		AdminPage page;
		if (StringUtils.isBlank(baseName)) {
			page = createPage();
			page.addMessage("Не задан базовый тип", true);
			page.output(resp);
			return;
		}
		filter = FilterDefinition.create("");
		filter.setRoot(baseName);
		saveFilter();
		page = createPage();
		page.addMessage("Фильтр успешно создан", false);
		page.output(resp);
	}
	/**
	 * Сохранить новую группу
	 * @param resp
	 * @throws Exception
	 */
	private void saveNewGroup(HttpServletResponse resp) throws Exception {
		AdminPage page;
		if (StringUtils.isBlank(name) || StringUtils.isBlank(sign)) {
			page = createPage();
			page.addMessage("Не заданы обязательные свойства группы", true);
			page.output(resp);
			return;
		}
		CriteriaGroupDef group = new CriteriaGroupDef(name, description, sign);
		filter.addPart(group);
		saveFilter();
		page = createPage();
		page.addMessage("Новая группа успешно создана", false);
		page.output(resp);
	}
	/**
	 * Обновить группу
	 * @param resp
	 * @throws Exception
	 */
	private void updateGroup(HttpServletResponse resp) throws Exception {
		AdminPage page;
		if (StringUtils.isBlank(name) || StringUtils.isBlank(sign)) {
			page = createPage();
			page.addMessage("Не заданы обязательные свойства группы", true);
			page.output(resp);
			return;
		}
		CriteriaGroupDef group = (CriteriaGroupDef)filter.getPart(id);
		group.update(name, description, sign);
		saveFilter();
		page = createPage();
		page.addMessage("Группа успешно обновлена", false);
		page.output(resp);
	}
	/**
	 * Сохранить новое поле ввода
	 * @param resp
	 * @throws Exception
	 */
	private void saveNewInput(HttpServletResponse resp) throws Exception {
		AdminPage page;
		if (StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
			page = createPage();
			page.addMessage("Не заданы обязательные свойства поля ввода", true);
			page.output(resp);
			return;
		}
		InputDef input = new InputDef(type, name, description, domain);
		CriteriaGroupDef group = (CriteriaGroupDef)filter.getPart(id);
		group.addPart(input);
		saveFilter();
		page = createPage();
		page.addMessage("Новое поле ввода успешно создано", false);
		page.output(resp);
	}
	/**
	 * Обновить поле ввода
	 * @param resp
	 * @throws Exception
	 */
	private void updateInput(HttpServletResponse resp) throws Exception {
		AdminPage page;
		if (StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
			page = createPage();
			page.addMessage("Не заданы обязательные свойства поля ввода", true);
			page.output(resp);
			return;
		}
		InputDef input = (InputDef)filter.getPart(id);
		input.update(type, name, description, domain);
		saveFilter();
		page = createPage();
		page.addMessage("Поле ввода успешно сохранено", false);
		page.output(resp);
	}
	/**
	 * Сохранить новый критерий
	 * @param resp
	 * @throws Exception
	 */
	private void saveNewCriteria(HttpServletResponse resp) throws Exception {
		AdminPage page;
		if (StringUtils.isBlank(sign) || StringUtils.isBlank(critParamName)) {
			page = createPage();
			page.addMessage("Не заданы обязательные свойства критерия", true);
			page.output(resp);
			return;
		}
		if ((sign.equals("like") || sign.equals("rlike")) && StringUtils.isBlank(pattern)) {
			page = createPage();
			page.addMessage("Не задан шаблон сравнения", true);
			page.output(resp);
			return;
		}
		Type paramType = ItemTypeRegistry.getItemType(baseName).getParameter(critParamName).getType();
		CriteriaDef crit = new CriteriaDef(sign, critParamName, paramType, pattern);
		InputDef input = (InputDef)filter.getPart(id);
		input.addPart(crit);
		saveFilter();
		page = createPage();
		page.addMessage("Критерий успешно добавлен к полю ввода", false);
		page.output(resp);
	}
	/**
	 * Обновить критерий
	 * @param resp
	 * @throws Exception
	 */
	private void updateCriteria(HttpServletResponse resp) throws Exception {
		AdminPage page;
		if (StringUtils.isBlank(sign) || StringUtils.isBlank(critParamName)) {
			page = createPage();
			page.addMessage("Не заданы обязательные свойства критерия", true);
			page.output(resp);
			return;
		}
		if ((sign.equals("like") || sign.equals("rlike")) && StringUtils.isBlank(pattern)) {
			page = createPage();
			page.addMessage("Не задан шаблон сравнения", true);
			page.output(resp);
			return;
		}
		Type paramType = ItemTypeRegistry.getItemType(baseName).getParameter(critParamName).getType();
		CriteriaDef crit = (CriteriaDef)filter.getPart(id);
		crit.update(sign, critParamName, paramType, pattern);
		saveFilter();
		page = createPage();
		page.addMessage("Критерий успешно сохранен", false);
		page.output(resp);
	}
	/**
	 * Удалить группу
	 * @param resp
	 * @throws Exception
	 */
	private void delete(HttpServletResponse resp) throws Exception {
		AdminPage page;
		FilterDefPart part = filter.getPart(id);
		if (part == null) {
			page = createPage();
			page.addMessage("Не задан элемент для удаления", true);
			page.output(resp);
			return;
		}
		part.delete();
		saveFilter();
		page = createPage();
		page.addMessage("Элемент успешно удален", false);
		page.output(resp);
	}
	/**
	 * Начальная страница, никакой айтем не выбран
	 * @return
	 * @throws Exception
	 */
	private AdminPage createPage() throws Exception {
		AdminPage page = new AdminPage(FILTER_PAGE);
		// Список типов айтемов
		createItemHierarchy(page);
		if (!StringUtils.isBlank(filterStr))
			page.addElement(new LeafMDStringWriter(filterStr));
		else 
			page.addElement(new LeafMDStringWriter(FilterDefinition.create("").generateXML()));
		// Список доменов
		AggregateMDWriter domains = new AggregateMDWriter(DOMAINS_TAG);
		for (String domainName : DomainRegistry.getDomainNames()) {
			domains.addSubwriter(new LeafMDWriter(DOMAIN_TAG, domainName));
		}
		page.addElement(domains);
		// Ссылки
		page.addElement(new LeafMDWriter(CREATE_FILTER_LINK_TAG, baseLink(CREATE_FILTER_ACTION)));
		page.addElement(new LeafMDWriter(CREATE_GROUP_LINK_TAG, baseLink(SAVE_NEW_GROUP_ACTION)));
		page.addElement(new LeafMDWriter(UPDATE_GROUP_LINK_TAG, baseLink(UPDATE_GROUP_ACTION)));
		page.addElement(new LeafMDWriter(DELETE_GROUP_LINK_TAG, baseLink(DELETE_GROUP_ACTION)));
		page.addElement(new LeafMDWriter(CREATE_INPUT_LINK_TAG, baseLink(SAVE_NEW_INPUT_ACTION)));
		page.addElement(new LeafMDWriter(UPDATE_INPUT_LINK_TAG, baseLink(UPDATE_INPUT_ACTION)));
		page.addElement(new LeafMDWriter(DELETE_INPUT_LINK_TAG, baseLink(DELETE_INPUT_ACTION)));
		page.addElement(new LeafMDWriter(CREATE_CRIT_LINK_TAG, baseLink(SAVE_NEW_CRIT_ACTION)));
		page.addElement(new LeafMDWriter(UPDATE_CRIT_LINK_TAG, baseLink(UPDATE_CRIT_ACTION)));
		page.addElement(new LeafMDWriter(DELETE_CRIT_LINK_TAG, baseLink(DELETE_CRIT_ACTION)));
		return page;
	}
	/**
	 * Установить новое значение парамтера айтема (фильтр) и сохранить айтем
	 * @throws Exception
	 */
	private void saveFilter() throws Exception {
		filterStr = filter.generateXML();
		item.setValueUI(paramName, filterStr);
		new InPlaceTransaction(getCurrentAdmin()) {
			@Override
			public void performTransaction() throws Exception {
				executeCommandUnit(SaveItemDBUnit.get(item).noFulltextIndex());
				PageController.clearCache();
			}
		}.execute();
		// TODO <fix> ??? возможно надо удалить 2 следующие 2 строчки
		filter = FilterDefinition.create(filterStr);
		filterStr = filter.generateXML();
	}
	
	private void createItemHierarchy(AdminPage page) throws Exception {
		TypeHierarchy root = ItemTypeRegistry.getHierarchies(ItemTypeRegistry.getItemNames());
		AggregateMDWriter itemsWriter = new AggregateMDWriter(ITEMS_TAG, "");
		page.addElement(itemsWriter);
		HashSet<String> exist = new HashSet<>();
		for (TypeHierarchy firstLevel : root.getExtenders()) {
			writeItem(firstLevel, itemsWriter, exist);
		}
	}
	
	private void writeItem(TypeHierarchy itemHierarchy, MetaDataWriter parent, HashSet<String> exist) throws Exception {
		if (exist.contains(itemHierarchy.getItemName()))
			return;
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemHierarchy.getItemName());
		ItemTypeMDWriter itemWriter;
		if (itemDesc.getName().equals(baseName))
			itemWriter = writeItemFull(itemDesc);
		else
			itemWriter = new ItemTypeMDWriter(itemDesc);
		parent.addSubwriter(itemWriter);
		exist.add(itemHierarchy.getItemName());
		for (TypeHierarchy childName : itemHierarchy.getExtenders()) {
			writeItem(childName, itemWriter, exist);
		}
	}

	private ItemTypeMDWriter writeItemFull(ItemType itemDesct) {
		ItemTypeMDWriter itemWriter = new ItemTypeMDWriter(itemDesct, FILTER_ITEM_TAG);
		// Параметры
		for (ParameterDescription param : itemDesct.getParameterList()) {
			ParameterDescriptionMDWriter paramWriter = new ParameterDescriptionMDWriter(param);
			itemWriter.addSubwriter(paramWriter);
		}
		return itemWriter;
	}	
	
	private String baseLink(String command) {
		return command + EXT + '?' + ITEM_ID_INPUT + '=' + itemId + '&' + PARAM_NAME_INPUT + '=' + paramName + '&' + ID_INPUT + '=';
	}
}
