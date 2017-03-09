package ecommander.admin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import ecommander.output.AggregateMDWriter;
import ecommander.output.ItemFormMDWriter;
import ecommander.output.LeafMDWriter;
import ecommander.output.MetaDataWriter;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.datatypes.FileDataType;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeContainer;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.pages.ItemHttpPostForm;
import ecommander.pages.UrlParameterFormatConverter;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.model.User;

/**
 * Создает все страницы (которые являются частями общей админской страницы и загружаются с помощь AJAX), 
 * которые нужны для отображения админской страницы
 * Не знанимается бизнес логикой.
 * Занимаестя загрузкой и подготовокой необходимых для отображения объектов
 * 
	Страницы выдаются в следующем виде
	
	Основа страницы (часть, которая вызывает все остальные части страницы):
	
	<admin-page name="base">
		<domain>test.forever-ds.com</domain>
		<view-type>parameters</view-type> // вид страницы (параметры, перемещение, ссылки)
		<base-id>15</base-id> // базовый ID айтема (-1 в случае корневого айтема)
		<base-type>22</base-type> // ID типа базового айтема
		<item name="section" id="15" caption="Телевизоры"> // Базовый айтем (который редактируется в данный момент)
		<path>
			<item name="section" id="33" caption="Телевизоры">
				<edit_link>admin.admin?command=admin_set_item&id=33?itemName</edit_link> // Ссылка для выбора айтема для редактирования
			</item>
			<item ...>
				...
			</item>
		</path>
		<link name="parameters">admin.admin?vt=parameters&command=change_view&id=33</link> // Ссылки для получения соответствующих старинц
		<link name="subitems">admin.admin?vt=subitems&command=change_view&id=33</link>
		<link name="mountTo">admin.admin?vt=mountTo&command=change_view&id=33</link>
		<link name="toMount">admin.admin?vt=toMount&command=change_view&id=33</link>
		<link name="moveTo">admin.admin?vt=moveTo&command=change_view&id=33</link>
		<link name="toMove">admin.admin?vt=toMove&command=change_view&id=33</link>
	</admin-page>
	
	
	Айтемы для добавления и уже добавленные айтемы:
	
	<admin-page name="subitems">
		<item-to-add name="section" id="22" caption="Раздел каталога" virtual="false">
			<item name="section_first" caption="Главный раздел" id="23">
				<create-link>admin.admin?command=create_item&name=section_first&pid=598</create_link> // Ссылка для создания айтема этого типа
			</item>
			<item name="section_last" caption="Раздел последнего уровня" id="24">
				<create-link>admin.admin?command=create_item&name=section_last&pid=598</create_link>
			</item>
			<item name="section" caption="Раздел каталога" id="22" default="true"> // элемент по умолчанию
				<create-link>admin.admin?command=create_item&name=section&pid=598</create_link>
			</item>
			<create-link>admin.admin?command=create_item&name=section&pid=598</create_link> // Ссылка для создания айтема, если нет эктендеров
		</item-to-add>
		<item name="section" id="33" caption="Телевизоры">
			<edit-link>admin.admin?command=admin_set_item&id=33?itemName</edit_link> // Ссылка для выбора айтема для редактирования
			<delete-link>admin.admin?command=admin_delete_item&id=33</delete_link> // Ссылка для выбора айтема для редактирования
		</item>
		<item ...>
			...
		</item>
		<link name="reorder">admin_reorder.admin?itemId=55&weight_before=64&weight_after=128</link>
	</admin-page>
	
	Айтемы для создания ссылок и уже созданные ссылки:
	
	<admin-page>
		<item name="section" id="22" caption="Девайсы">
		<path>
			<item name="section" id="33" caption="Телевизоры">
				<link>admin.admin?command=admin_set_item&id=33?itemName</link> // Ссылка для выбора айтема для редактирования
			</item>
			<item ...>
				...
			</item>
		</path>
		<mount>
			<link>admin.admin?command=mount...</link> // Ссылка для создания привязки айтемов
			<type caption="Раздел"> // Айтемы группируются по своему типу
				<item ...>
					<input name="xxx:ccc:333">add</mount-input>
					<link>admin.admin?command=admin_set_item&id=33?itemName</link>
				</item>
				<item ...>
				...
				</item>
				...
			</type>
			<type caption="Новость">
				<item...>
				...
			</type>
			...
		</mount>
		<mounted>
			<link>admin.admin?command=mount...</link> // Ссылка для удаления привязки айтемов
			<type caption="Раздел">
				<item ...>
					<input name="xxx:ccc:333">delete</input>
				</item>
			</type>
			<type...>
			...
		</mounted>
	</admin-page>

	Перемещение текущего айтема в выбранный и перемещение выбранных айтемов в текущий:
	
	<admin-page>
		<item name="section" id="22" caption="Девайсы">
		<path>
			<item name="section" id="33" caption="Телевизоры">
				<link>admin.admin?command=admin_set_item&id=33?itemName</link> // Ссылка для выбора айтема для редактирования
			</item>
			<item ...>
				...
			</item>
		</path>
		<move>
			<link>admin.admin?command=mount...</link> // Ссылка для перемещения
			<type caption="Раздел">
				<item ...>
					<input name="xxx:ccc:333"></mount-input>
					<link>admin.admin?command=admin_set_item&id=33?itemName</link>
				</item>
				...
			</type>
			<type>
			...
			</type>
			...
		</move>
	</admin-page>

 * @author EEEE
 *
 */
public class MainAdminPageCreator {
	private static final String DOT_ACTION = ".action";
	/**
	 * Разные экшены
	 */
	public static final String INITIALIZE_ACTION = "admin_initialize";
	public static final String SET_ITEM_ACTION = "admin_set_item";
	public static final String CREATE_ITEM_ACTION = "admin_create_item";
	public static final String DELETE_ITEM_ACTION = "admin_delete_item";
	public static final String SAVE_ITEM_ACTION = "admin_save_item";
	public static final String REORDER_ACTION = "admin_reorder";
	public static final String ADD_PARAMETER_ACTION = "admin_add_parameter";
	public static final String DELETE_PARAMETER_ACTION = "admin_delete_parameter";
	public static final String SET_MOUNT_TO_PARENT_ACTION = "admin_set_mount_to_parent";
	public static final String SET_TO_MOUNT_PARENT_ACTION = "admin_set_to_mount_parent";
	public static final String SET_ASSOCIATE_PARENT_ACTION = "admin_set_associate_parent";
	public static final String SET_MOVE_TO_PARENT_ACTION = "admin_set_move_to_parent";
	public static final String SET_TO_MOVE_PARENT_ACTION = "admin_set_to_move_parent";
	public static final String CREATE_MOUNT_TO_ACTION = "admin_create_mount_to";
	public static final String CREATE_TO_MOUNT_ACTION = "admin_create_to_mount";
	public static final String CREATE_ASSOCIATED_ACTION = "admin_create_associated";
	public static final String MOVE_TO_ACTION = "admin_move_to";
	public static final String TO_MOVE_ACTION = "admin_to_move";
	public static final String DELETE_REFERENCE_ACTION = "admin_delete_reference";
	public static final String DELETE_ASSOCIATED_ACTION = "admin_delete_associated";
	public static final String GET_VIEW_ACTION = "get_view";
	public static final String USERS_INITIALIZE_ACTION = "admin_users_initialize";
	public static final String SET_USER_ACTION = "admin_set_user";
	public static final String DELETE_USER_ACTION = "admin_delete_user";
	public static final String SAVE_USER_ACTION = "admin_save_user";
	public static final String DOMAINS_INITIALIZE_ACTION = "admin_domains_initialize";
	public static final String CREATE_DOMAIN_ACTION = "admin_create_domain";
	public static final String DELETE_DOMAIN_ACTION = "admin_delete_domain";
	public static final String SET_DOMAIN_ACTION = "admin_set_domain";
	public static final String UPDATE_DOMAIN_ACTION = "admin_update_domain";
	public static final String ADD_DOMAIN_VALUE_ACTION = "admin_add_domain_value";
	public static final String DELETE_DOMAIN_VALUE_ACTION = "admin_delete_domain_value";
	public static final String EXIT_ACTION = "admin_exit";
	public static final String REINDEX_ACTION = "admin_reindex";
	public static final String DROP_ALL_CACHES_ACTION = "admin_drop_all_caches";
	public static final String UPLOAD_START_ACTION = "admin_upload_start";
	public static final String UPLOAD_IMG_ACTION = "admin_upload_img";
	public static final String COPY_ACTION = "admin_copy";
	public static final String PASTE_ACTION = "admin_paste";
	public static final String DELETE_PASTE_ACTION = "admin_delete_paste";
	/**
	 * Инпуты
	 */
	public static final String ITEM_TYPE_INPUT = "itemType";
	public static final String MOVING_ITEM_INPUT = "movingItem";
	public static final String PARAM_ID_INPUT = "multipleParamId";
	public static final String MULTIPLE_PARAM_VALUE_INPUT = "multipleParamValue";
	public static final String ITEM_ID_INPUT = "itemId";
	public static final String PARENT_ID_INPUT = "parentId";
	public static final String INDEX_INPUT = "index";
	public static final String WEIGHT_BEFORE_INPUT = "weight_before";
	public static final String WEIGHT_AFTER_INPUT = "weight_after";
	public static final String VIEW_TYPE_INPUT = "vt";
	public static final String INLINE_INPUT = "inl";
	public static final String MOUNT_INPUT_PREFIX = "mount";
	public static final String UNMOUNT_INPUT_PREFIX = "unmount";
	public static final String PERSONAL_INPUT = "prs";
	public static final String HEIGHT_INPUT = "height";
	public static final String WIDTH_INPUT = "width";
	public static final String ALT_INPUT = "alt";
	public static final String PARAM_INPUT = "param";
	public static final String VISUAL_INPUT = "vis";
	/**
	 * Значения
	 */
	private static final String ADD_VALUE = "add";
	private static final String DELETE_VALUE = "delete";
	private static final String MOVE_VALUE = "move";
	private static final String REORDER_VALUE = "reorder";
	/**
	 * Виды страницы
	 */
	public static final String PARAMS_VIEW_TYPE = "parameters";
	public static final String SUBITEMS_VIEW_TYPE = "subitems";
	public static final String INLINE_VIEW_TYPE = "inline";
	public static final String MOUNT_TO_VIEW_TYPE = "mountTo";
	public static final String TO_MOUNT_VIEW_TYPE = "toMount";
	public static final String MOVE_TO_VIEW_TYPE = "moveTo";
	public static final String TO_MOVE_VIEW_TYPE = "toMove";
	public static final String ASSOCIATE_VIEW_TYPE = "associate";
	public static final String PASTE_VIEW_TYPE = "paste";
	/**
	 * Названия страниц
	 */
	public static final String BASE_PAGE = "main/base";
	public static final String SUBITEMS_PAGE = "main/subitems";
	public static final String PARAMETERS_PAGE = "main/parameters";
	public static final String INLINE_PAGE = "main/inline_parameters";
	public static final String MOUNT_TO_PAGE = "main/mount_to";
	public static final String TO_MOUNT_PAGE = "main/to_mount";
	public static final String ASSOCIATE_PAGE = "main/associate";
	public static final String MOVE_TO_PAGE = "main/move_to";
	public static final String TO_MOVE_PAGE = "main/to_move";
	public static final String IMG_UPLOAD_PAGE = "main/image_upload";
	public static final String IMG_UPLOADED_PAGE = "main/image_uploaded";
	public static final String PASTE_PAGE = "main/paste";
	/**
	 * Параметры сеанса
	 */
	public static final String PASTE_LIST = "admin_paste_list";
	
	private static class ItemToAdd extends MetaDataWriter {
		private final String baseItem; // Имя айтема-родоначальника 
		private final String defaultExtender;
		private final ArrayList<String> extenders;
		private final long parentId;
		private final boolean isVirtual;
		private final boolean isPersonal;
		
		private ItemToAdd(String baseItem, String defaultExtender, long parentId, boolean isVirtual, boolean isPersonal) throws SQLException {
			extenders = new ArrayList<String>();
			this.baseItem = baseItem;
			this.defaultExtender = defaultExtender;
			this.parentId = parentId;
			this.isVirtual = isVirtual;
			this.isPersonal = isPersonal;
			LinkedHashSet<String> toAdd = ItemTypeRegistry.getItemExtenders(baseItem);
			if (toAdd.size() > 1) {
				for (String itemName : toAdd) {
					if (!ItemTypeRegistry.getItemType(itemName).isVirtual())
						extenders.add(itemName);
				}
			}
		}

		@Override
		public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
			ItemType itemDesc = ItemTypeRegistry.getItemType(baseItem);
			xml.startElement(AdminXML.ITEM_TO_ADD_ELEMENT, AdminXML.NAME_ATTRIBUTE, itemDesc.getName(), AdminXML.ID_ATTRIBUTE,
					itemDesc.getTypeId(), AdminXML.CAPTION_ATTRIBUTE, itemDesc.getCaption(), AdminXML.VIRTUAL_ATTRIBUTE, isVirtual);
			for (String ext : extenders) {
				ItemType extender = ItemTypeRegistry.getItemType(ext);
				String createExtUrl = createAdminUrl(CREATE_ITEM_ACTION, PARENT_ID_INPUT, parentId, ITEM_TYPE_INPUT, extender.getTypeId(),
						PERSONAL_INPUT, isPersonal);
				boolean isDefault = extender.getName().equals(defaultExtender);
				xml.startElement(AdminXML.ITEM_ELEMENT, AdminXML.NAME_ATTRIBUTE, extender.getName(), AdminXML.ID_ATTRIBUTE,
						extender.getTypeId(), AdminXML.CAPTION_ATTRIBUTE, extender.getCaption(), AdminXML.DEFAULT_ATTRIBUTE, isDefault);
				xml.startElement(AdminXML.CREATE_LINK_ELEMENT).addText(createExtUrl).endElement();
				xml.endElement();
			}
			String createBaseUrl = createAdminUrl(CREATE_ITEM_ACTION, PARENT_ID_INPUT, parentId, ITEM_TYPE_INPUT, itemDesc.getTypeId());
			xml.startElement(AdminXML.CREATE_LINK_ELEMENT).addText(createBaseUrl).endElement();
			xml.endElement();
			return xml;
		}
	}

	// Корневой айтем для текущего пользователя
	private RootItemType root = null;
	// Текущий пользователь
	private User currentUser = null;
	// Текущий домен сайта
	private String domain = null;
	
	public MainAdminPageCreator(User user, String domain) {
		root = ItemTypeRegistry.getGroupRoot(user.getGroup());
		this.currentUser = user;
		this.domain = domain;
	}
	/**
	 * Добавить ссылки на разные виды центральной части страницы
	 * @param basePage
	 */
	private void addViewLinks(AdminPage basePage, long baseId) {
		String paramsUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, PARAMS_VIEW_TYPE, ITEM_ID_INPUT, baseId);
		String mountToUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, MOUNT_TO_VIEW_TYPE, ITEM_ID_INPUT, baseId);
		String toMountUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, TO_MOUNT_VIEW_TYPE, ITEM_ID_INPUT, baseId);
		String moveToUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, MOVE_TO_VIEW_TYPE, ITEM_ID_INPUT, baseId);
		String toMoveUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, TO_MOVE_VIEW_TYPE, ITEM_ID_INPUT, baseId);
		basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, paramsUrl, AdminXML.NAME_ATTRIBUTE, PARAMS_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, mountToUrl, AdminXML.NAME_ATTRIBUTE, MOUNT_TO_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, toMountUrl, AdminXML.NAME_ATTRIBUTE, TO_MOUNT_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, moveToUrl, AdminXML.NAME_ATTRIBUTE, MOVE_TO_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, toMoveUrl, AdminXML.NAME_ATTRIBUTE, TO_MOVE_VIEW_TYPE));
	}
	/**
	 * Создает базовую часть страницы, которая сама при помощи клиентских скриптов должна вызывать остальные части страницы, в зависимости
	 * от выбранного пользователем режима редактирования
	 * @param defaultViewType - резим редактирования по умолчанию
	 * @param baseId
	 * @param itemType
	 * @return
	 * @throws Exception
	 */
	public AdminPage createPageBase(String defaultViewType, long baseId, int itemType) throws Exception {
		AdminPage basePage = new AdminPage(BASE_PAGE, domain, currentUser.getName());
		basePage.addElement(new LeafMDWriter(AdminXML.VIEW_TYPE_ELEMENT, defaultViewType));
		basePage.addElement(new LeafMDWriter(AdminXML.BASE_ID_ELEMENT, baseId));
		basePage.addElement(new LeafMDWriter(AdminXML.BASE_TYPE_ELEMENT, itemType));
		// Путь к текущему элементу
		AggregateMDWriter path = new AggregateMDWriter(AdminXML.PATH_ELEMENT);
		ArrayList<ItemAccessor> pathItems = AdminLoader.getLoader().loadWholeBranch(baseId, currentUser);
		// Текущий элемент
		if (baseId > 0 && baseId != root.getItemId()) {
			ItemAccessor item = AdminLoader.getLoader().loadItemAccessor(baseId);
			if (item != null) {
				basePage.addElement(item);
			}
		}
		for (ItemAccessor pred : pathItems) {
			String editUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, pred.getItemId(), ITEM_TYPE_INPUT, pred.getTypeId());
			pred.addSubwriter(new LeafMDWriter(AdminXML.EDIT_LINK_ELEMENT, editUrl));
			path.addSubwriter(pred);
		}
		basePage.addElement(path);
		// Ссылка на сабайтемы и на другие части страницы
		String subitemsUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, SUBITEMS_VIEW_TYPE, ITEM_ID_INPUT, baseId, ITEM_TYPE_INPUT, itemType);
		basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, subitemsUrl, AdminXML.NAME_ATTRIBUTE, SUBITEMS_VIEW_TYPE));
		addViewLinks(basePage, baseId);
		return basePage;
	}
	/**
	 * Создает часть страницы, которая отвечает за создание сабайтемов в выбор для редактирования и удаления существующих сабайтемов
	 * определенного айтема (или корня)
	 * @param baseId - ID базового айтема (родителя) В случае корня - любое число меньше 0, например, -1
	 * @param itemType - ID типа айтема, в случае корня - не требуется
	 * @return
	 * @throws Exception 
	 */
	public AdminPage createSubitemsPage(long baseId, int itemType) throws Exception {
		AdminPage basePage = new AdminPage(SUBITEMS_PAGE, domain, currentUser.getName());
		ArrayList<ItemToAdd> itemsToAdd = new ArrayList<ItemToAdd>();
		if (baseId <= 0) {
			baseId = root.getItemId();
		} else if (itemType <= 0) {
			ItemAccessor baseItem = AdminLoader.getLoader().loadItemAccessor(baseId);
			itemType = baseItem.getTypeId();
		}
		HashMap<String, ArrayList<ItemAccessor>> existingSubitems = createSubitemsInfo(baseId, itemType, itemsToAdd);
		if (!currentUser.getGroup().equals(User.USER_DEFAULT_GROUP) && baseId == root.getItemId()) {
			existingSubitems.putAll(createDefaultRootSubitemsInfo(itemsToAdd));
		}
		ArrayList<ItemAccessor> subitems = new ArrayList<ItemAccessor>();
		for (ArrayList<ItemAccessor> items : existingSubitems.values()) {
			subitems.addAll(items);
		}
		Collections.sort(subitems);
		for (ItemToAdd itemToAdd : itemsToAdd) {
			basePage.addElement(itemToAdd);
		}
		for (ItemAccessor subitem : subitems) {
			String delUrl = createAdminUrl(DELETE_ITEM_ACTION, ITEM_ID_INPUT, subitem.getItemId(), ITEM_TYPE_INPUT, itemType, PARENT_ID_INPUT, baseId);
			String editUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, subitem.getItemId(), ITEM_TYPE_INPUT, subitem.getTypeId());
			String editInlineUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, INLINE_VIEW_TYPE, ITEM_ID_INPUT, subitem.getItemId());
			String copyUrl = createAdminUrl(COPY_ACTION, ITEM_ID_INPUT, subitem.getItemId(), PARENT_ID_INPUT, baseId, ITEM_TYPE_INPUT, itemType);
			subitem.addSubwriter(new LeafMDWriter(AdminXML.DELETE_LINK_ELEMENT, delUrl));
			subitem.addSubwriter(new LeafMDWriter(AdminXML.EDIT_LINK_ELEMENT, editUrl));
			subitem.addSubwriter(new LeafMDWriter(AdminXML.EDIT_INLINE_LINK_ELEMENT, editInlineUrl));
			subitem.addSubwriter(new LeafMDWriter(AdminXML.COPY_LINK_ELEMENT, copyUrl));
			basePage.addElement(subitem);
		}
		String reorderUrl = createAdminUrl(REORDER_ACTION, 
				ITEM_ID_INPUT, ":id:", 
				WEIGHT_BEFORE_INPUT, ":wb:", 
				WEIGHT_AFTER_INPUT, ":wa:",
				ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, baseId);
		String getPasteBufferUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, PASTE_VIEW_TYPE, PARENT_ID_INPUT, baseId, ITEM_TYPE_INPUT,
				itemType);
		basePage.addElement(new LeafMDWriter(AdminXML.GET_PASTE_LINK_ELEMENT, getPasteBufferUrl));
		basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, reorderUrl, AdminXML.NAME_ATTRIBUTE, REORDER_VALUE));
		return basePage;
	}
	/**
	 * Страница ля вставки сопированных айтемов
	 * @param session
	 * @param newParentId
	 * @param newParentTypeId
	 * @param userGroup
	 * @return
	 */
	public AdminPage createPastePage(HttpSession session, long newParentId, int newParentTypeId, String userGroup) {
		AdminPage page = new AdminPage(PASTE_PAGE, domain, currentUser.getName());
		@SuppressWarnings("unchecked")
		LinkedHashMap<Long, ItemAccessor> buffer = (LinkedHashMap<Long, ItemAccessor>) session.getAttribute(PASTE_LIST);
		if (buffer == null)
			return page;
		ItemType parentDesc = ItemTypeRegistry.getItemType(newParentTypeId);
		Set<String> subitemNames = new HashSet<String>();
		if (parentDesc != null)
			subitemNames = ItemTypeRegistry.getUserGroupAllowedSubitems(parentDesc.getName(), userGroup, false);
		for (ItemAccessor item : buffer.values()) {
			item.clearSubwriters();
			String deleteUrl = createAdminUrl(DELETE_PASTE_ACTION, ITEM_ID_INPUT, item.getItemId(), PARENT_ID_INPUT, newParentId,
					ITEM_TYPE_INPUT, newParentTypeId);
			String editUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, item.getItemId(), ITEM_TYPE_INPUT, item.getTypeId());
			item.addSubwriter(new LeafMDWriter(AdminXML.DELETE_LINK_ELEMENT, deleteUrl));
			item.addSubwriter(new LeafMDWriter(AdminXML.EDIT_LINK_ELEMENT, editUrl));
			// Добавлять урл для вставки только при совместимости типов
			if (subitemNames.contains(ItemTypeRegistry.getItemType(item.getTypeId()).getName())) {
				String pasteUrl = createAdminUrl(PASTE_ACTION, ITEM_ID_INPUT, item.getItemId(), PARENT_ID_INPUT, newParentId, ITEM_TYPE_INPUT,
						newParentTypeId);
				item.addSubwriter(new LeafMDWriter(AdminXML.PASTE_LINK_ELEMENT, pasteUrl));
			}
			page.addElement(item);
		}
		return page;
	}
	/**
	 * Создает часть страницы с формой редактирования существующего айтема
	 * @param itemId
	 * @param isInline - нужно ли создавать форму для инлайнового айтема
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public AdminPage createParamsPage(long itemId, boolean isInline, boolean isVisual) throws SQLException, Exception {
		AdminPage basePage = new AdminPage(isInline ? INLINE_PAGE : PARAMETERS_PAGE, domain, currentUser.getName());
		basePage.addElement(new LeafMDWriter(AdminXML.VISUAL_ELEMENT, isVisual));
		Item item = null;
		if (itemId != root.getItemId())
			item = ItemQuery.loadById(itemId);
		if (item != null) {
			ItemHttpPostForm itemForm = new ItemHttpPostForm(item, basePage.getName());
			ItemFormMDWriter formWriter = new ItemFormMDWriter(itemForm, AdminXML.FORM_ELEMENT);
			formWriter.setAction(createAdminUrl(SAVE_ITEM_ACTION));
			basePage.addElement(formWriter);
			basePage.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, createAdminUrl(DELETE_PARAMETER_ACTION, PARAM_ID_INPUT, "")));
			basePage.addElement(new LeafMDWriter(AdminXML.OPEN_ASSOC_LINK_ELEMENT, createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT,
					ASSOCIATE_VIEW_TYPE, ITEM_ID_INPUT, itemId, PARENT_ID_INPUT, 0, PARAM_ID_INPUT, "")));
			// Ссылка на загрузку картинки
			// Надо найти параметр, в котором должна храниться подгружаемая картинка
			// Также надо найти все ассоциированные айтемы и загрузить их. Они хранястя в параметрах типа associated
			int paramId = 0;
			ArrayList<Long> associatedIds = new ArrayList<Long>();
			for (ParameterDescription param : item.getItemType().getParameterList()) {
				if (param.getType() == Type.PICTURE && param.isMultiple() && paramId == 0) {
					paramId = param.getId();
				} else if (param.getType() == Type.ASSOCIATED) {
					associatedIds.addAll(item.getLongValues(param.getName()));
				}
			}
			if (paramId > 0)
				basePage.addElement(new LeafMDWriter(AdminXML.UPLOAD_LINK_ELEMENT, createAdminUrl(UPLOAD_START_ACTION, ITEM_ID_INPUT,
						item.getId(), PARAM_ID_INPUT, paramId)));
			if (associatedIds.size() > 0) {
				ArrayList<ItemAccessor> associatedItems = AdminLoader.getLoader().loadItemAccessors(associatedIds.toArray(new Long[0]));
				AggregateMDWriter associated = new AggregateMDWriter(AdminXML.MOUNT_ELEMENT);
				basePage.addElement(associated);
				for (ItemAccessor acc : associatedItems) {
					associated.addSubwriter(acc);
				}
			}
		}
		// Ссылки на другие виды редактирования
		addViewLinks(basePage, itemId);
		return basePage;
	}
	/**
	 * Создает часть страницы с формой редактирования нового айтема
	 * @param itemType
	 * @param parentId
	 * @param isInline - нужно ли создавать форму для инлайнового атйема
	 * @return
	 */
	public AdminPage createParamsPage(int itemType, long parentId, boolean isInline, boolean isVisual) {
		AdminPage basePage = new AdminPage(isInline ? INLINE_PAGE : PARAMETERS_PAGE, domain, currentUser.getName());
		basePage.addElement(new LeafMDWriter(AdminXML.VISUAL_ELEMENT, isVisual));
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemType);
		ItemHttpPostForm itemForm = new ItemHttpPostForm(itemDesc, parentId, basePage.getName());
		ItemFormMDWriter formWriter = new ItemFormMDWriter(itemForm, AdminXML.FORM_ELEMENT);
		formWriter.setAction(createAdminUrl(SAVE_ITEM_ACTION));
		basePage.addElement(formWriter);
		// Ссылки на другие виды редактирования
		addViewLinks(basePage, parentId);
		return basePage;
	}
	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для прикрепления к ним выбранного айтема, а также
	 * список айтемов, к которым прикреплен выбранный айтем
	 * @param itemId
	 * @param mountToParent
	 * @return
	 * @throws Exception
	 */
	public AdminPage createMountToPage(long itemId, long mountToParent) throws Exception {
		if (mountToParent <= 0)
			mountToParent = root.getItemId();
		AdminPage page = new AdminPage(MOUNT_TO_PAGE, domain, currentUser.getName());
		// Ссылки на другие виды редактирования
		addViewLinks(page, itemId);
		if (itemId <= 0 || itemId == root.getItemId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		ItemAccessor baseItem = mapper.loadItemAccessor(itemId);
		int itemType = baseItem.getTypeId();
		ArrayList<ItemAccessor> mountToParentPathItems = mapper.loadWholeBranch(mountToParent, currentUser);
		ArrayList<ItemAccessor> mountToList = mapper.loadItemsToMountTo(mountToParent, itemId);
		ArrayList<ItemAccessor> mountedList = mapper.loadMountedToItems(itemId, currentUser.getUserId());
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemType);
		// Базовый айтем
		page.addElement(baseItem);
		// Путь к айтемам, к которым можно прикреплять выбранный
		AggregateMDWriter path = new AggregateMDWriter(AdminXML.PATH_ELEMENT);
		for (ItemAccessor pred : mountToParentPathItems) {
			String setMountParentUrl = createAdminUrl(SET_MOUNT_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, pred.getItemId());
			pred.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMountParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для прикрепления к ним
		AggregateMDWriter mountTo = new AggregateMDWriter(AdminXML.MOUNT_ELEMENT);
		page.addElement(mountTo);
		String submitMountFormUrl = createAdminUrl(CREATE_MOUNT_TO_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, mountToParent);
		mountTo.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitMountFormUrl));
		int currentTypeId = -1;
		AggregateMDWriter typeGroup = null;
		for (ItemAccessor item : mountToList) {
			if (item.isParentCompatible()) {
				HashSet<String> predecessors = new HashSet<String>(ItemTypeRegistry.getItemPredecessorsExt(itemDesc.getName()));
				HashSet<String> subitems = new HashSet<String>(ItemTypeRegistry.getItemType(item.getItemName()).getAllChildren());
				subitems.retainAll(predecessors);
				if (subitems.size() > 0) {
					String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), MOUNT_INPUT_PREFIX);
					item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, ADD_VALUE, AdminXML.NAME_ATTRIBUTE, inputName));
				}
			}
			String setMountParentUrl = createAdminUrl(SET_MOUNT_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, item.getItemId());
			item.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMountParentUrl));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				mountTo.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		// Уже прикрепленные элементы
		AggregateMDWriter mounted = new AggregateMDWriter(AdminXML.MOUNTED_ELEMENT);
		page.addElement(mounted);
		String submitUnmountFormUrl = createAdminUrl(DELETE_REFERENCE_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, mountToParent);
		mounted.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitUnmountFormUrl));
		currentTypeId = -1;
		typeGroup = null;
		for (ItemAccessor item : mountedList) {
			String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), UNMOUNT_INPUT_PREFIX);
			item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, DELETE_VALUE, AdminXML.NAME_ATTRIBUTE, inputName));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				mounted.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		return page;
	}
	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для прикрепления к выбранному айтема, а также
	 * список айтемов, которые прикреплены к выбранному айтему
	 * @param itemId
	 * @param mountToParent
	 * @return
	 * @throws Exception
	 */
	public AdminPage createToMountPage(long itemId, long toMountParent) throws Exception {
		if (toMountParent <= 0)
			toMountParent = root.getItemId();
		AdminPage page = new AdminPage(TO_MOUNT_PAGE, domain, currentUser.getName());
		// Ссылки на другие виды редактирования
		addViewLinks(page, itemId);
		if (itemId <= 0 || itemId == root.getItemId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		ItemAccessor baseItem = mapper.loadItemAccessor(itemId);
		int itemType = baseItem.getTypeId();
		ArrayList<ItemAccessor> mountToParentPathItems = mapper.loadWholeBranch(toMountParent, currentUser);
		ArrayList<ItemAccessor> toMountList = mapper.loadItemsToMount(toMountParent, itemId);
		ArrayList<ItemAccessor> mountedList = mapper.loadMountedItems(itemId, currentUser.getUserId());
		// Базовый айтем
		page.addElement(baseItem);
		// Путь к айтемам, которые можно прикреплять к выбранному
		AggregateMDWriter path = new AggregateMDWriter(AdminXML.PATH_ELEMENT);
		for (ItemAccessor pred : mountToParentPathItems) {
			String setMountParentUrl = createAdminUrl(SET_TO_MOUNT_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, pred.getItemId());
			pred.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMountParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для прикрепления
		AggregateMDWriter mountTo = new AggregateMDWriter(AdminXML.MOUNT_ELEMENT);
		page.addElement(mountTo);
		String submitMountFormUrl = createAdminUrl(CREATE_TO_MOUNT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, toMountParent);
		mountTo.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitMountFormUrl));
		int currentTypeId = -1;
		AggregateMDWriter typeGroup = null;
		for (ItemAccessor item : toMountList) {
			if (item.isParentCompatible()) {
				HashSet<String> predecessors = new HashSet<String>(ItemTypeRegistry.getItemPredecessorsExt(item.getItemName()));
				HashSet<String> subitems = new HashSet<String>(ItemTypeRegistry.getItemType(itemType).getAllChildren());
				subitems.retainAll(predecessors);
				if (subitems.size() > 0) {
					String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), MOUNT_INPUT_PREFIX);
					item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, ADD_VALUE, AdminXML.NAME_ATTRIBUTE, inputName));
				}
			}
			String setMountParentUrl = createAdminUrl(SET_TO_MOUNT_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, item.getItemId());
			item.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMountParentUrl));
			mountTo.addSubwriter(item);
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				mountTo.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		// Уже прикрепленные элементы
		AggregateMDWriter mounted = new AggregateMDWriter(AdminXML.MOUNTED_ELEMENT);
		page.addElement(mounted);
		String submitUnmountFormUrl = createAdminUrl(DELETE_REFERENCE_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, toMountParent);
		mounted.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitUnmountFormUrl));
		currentTypeId = -1;
		typeGroup = null;
		for (ItemAccessor item : mountedList) {
			String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), UNMOUNT_INPUT_PREFIX);
			item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, DELETE_VALUE, AdminXML.NAME_ATTRIBUTE, inputName));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				mounted.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		return page;
	}
	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для ассоциации с выбранным айтемом, а также
	 * список айтемов, которые уже ассоциированы с ним
	 * @param itemId
	 * @param associateParent
	 * @param assocParamId
	 * @return
	 * @throws Exception
	 */
	public AdminPage createAssociatedPage(long itemId, long associateParent, int assocParamId) throws Exception {
		if (associateParent <= 0) {
			associateParent = root.getItemId();
		}
		AdminPage page = new AdminPage(ASSOCIATE_PAGE, domain, currentUser.getName());
		if (itemId <= 0 || itemId == root.getItemId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		Item baseItem = ItemQuery.loadById(itemId);
		ItemAccessor baseAcc = new ItemAccessor(baseItem.getTypeId(), baseItem.getId(), baseItem.getRefId(), baseItem.getKey(),
				baseItem.getChildWeight());
		ArrayList<ItemAccessor> mountToParentPathItems = mapper.loadWholeBranch(associateParent, currentUser);
		HashMap<String, ArrayList<ItemAccessor>> toAssocMap = null;
		if (root.getItemId() == associateParent) {
			toAssocMap = mapper.loadClosestSubitems(root, associateParent, currentUser);
		} else {
			Item parent = ItemQuery.loadById(associateParent);
			toAssocMap = mapper.loadClosestSubitems(parent.getItemType(), associateParent, currentUser);
		}
		ArrayList<ItemAccessor> toAssocList = new ArrayList<ItemAccessor>();
		String assocParamName = baseItem.getItemType().getParameter(assocParamId).getName();
		ArrayList<Long> assocIds = baseItem.getLongValues(assocParamName);
		ArrayList<ItemAccessor> associatedList = mapper.loadItemAccessors(assocIds.toArray(new Long[0]));	
		for (ArrayList<ItemAccessor> items : toAssocMap.values()) {
			for (ItemAccessor item : items) {
				if (!assocIds.contains(item.getItemId()))
					item.setParentCompatible(true);
				toAssocList.add(item);
			}
		}

		// Базовый айтем
		page.addElement(baseAcc);
		// Путь к айтемам, которые можно прикреплять к выбранному
		AggregateMDWriter path = new AggregateMDWriter(AdminXML.PATH_ELEMENT);
		for (ItemAccessor pred : mountToParentPathItems) {
			String setAssocParentUrl = createAdminUrl(SET_ASSOCIATE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT,
					baseItem.getTypeId(), PARENT_ID_INPUT, pred.getItemId(), PARAM_ID_INPUT, assocParamId);
			pred.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setAssocParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для прикрепления
		AggregateMDWriter associate = new AggregateMDWriter(AdminXML.MOUNT_ELEMENT);
		page.addElement(associate);
		String submitAssociateFormUrl = createAdminUrl(CREATE_ASSOCIATED_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, baseItem.getTypeId(),
				PARENT_ID_INPUT, associateParent, PARAM_ID_INPUT, assocParamId);
		associate.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitAssociateFormUrl));
		int currentTypeId = -1;
		AggregateMDWriter typeGroup = null;
		for (ItemAccessor item : toAssocList) {
			if (item.isParentCompatible()) {
				String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), MOUNT_INPUT_PREFIX);
				item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, ADD_VALUE, AdminXML.NAME_ATTRIBUTE, inputName));
			}
			String setAssocParentUrl = createAdminUrl(SET_ASSOCIATE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, item.getTypeId(),
					PARENT_ID_INPUT, item.getItemId(), PARAM_ID_INPUT, assocParamId);
			item.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setAssocParentUrl));
			associate.addSubwriter(item);
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				associate.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		// Уже прикрепленные элементы
		AggregateMDWriter associated = new AggregateMDWriter(AdminXML.MOUNTED_ELEMENT);
		page.addElement(associated);
		String submitDeleteAssocFormUrl = createAdminUrl(DELETE_ASSOCIATED_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT,
				baseItem.getTypeId(), PARENT_ID_INPUT, associateParent, PARAM_ID_INPUT, assocParamId);
		associated.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitDeleteAssocFormUrl));
		currentTypeId = -1;
		typeGroup = null;
		for (ItemAccessor item : associatedList) {
			String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), UNMOUNT_INPUT_PREFIX);
			item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, DELETE_VALUE, AdminXML.NAME_ATTRIBUTE, inputName));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				associated.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		
		// Ссылка для перезагрузки родительской страницы
		String reloadUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, baseAcc.getItemId(), ITEM_TYPE_INPUT, baseAcc.getTypeId());
		page.addElement(new LeafMDWriter(AdminXML.LINK_ELEMENT, reloadUrl));
		return page;
	}
	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для перемещения в них выбранного айтема
	 * @param itemId
	 * @param mountToParent
	 * @return
	 * @throws Exception
	 */
	public AdminPage createMoveToPage(long itemId, long moveToParent) throws Exception {
		if (moveToParent <= 0)
			moveToParent = root.getItemId();
		AdminPage page = new AdminPage(MOVE_TO_PAGE, domain, currentUser.getName());
		// Ссылки на другие виды редактирования
		addViewLinks(page, itemId);
		if (itemId <= 0 || itemId == root.getItemId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		Item currentItem = ItemQuery.loadById(itemId);
		ItemAccessor baseItem = new ItemAccessor(currentItem.getTypeId(), currentItem.getId(), currentItem.getRefId(), currentItem.getKey(),
				currentItem.getChildWeight());
		int itemType = baseItem.getTypeId();
		ArrayList<ItemAccessor> moveToParentPathItems = mapper.loadWholeBranch(moveToParent, currentUser);
		ArrayList<ItemAccessor> moveToList = mapper.loadItemsToMoveTo(moveToParent, itemId, currentItem.getDirectParentId());
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemType);
		// Базовый айтем
		page.addElement(baseItem);
		// Путь к айтемам, к которым можно прикреплять выбранный
		AggregateMDWriter path = new AggregateMDWriter(AdminXML.PATH_ELEMENT);
		for (ItemAccessor pred : moveToParentPathItems) {
			String setMoveParentUrl = createAdminUrl(SET_MOVE_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, pred.getItemId());
			pred.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMoveParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для перемещения к ним
		AggregateMDWriter moveTo = new AggregateMDWriter(AdminXML.MOUNT_ELEMENT);
		page.addElement(moveTo);
		String submitMoveFormUrl = createAdminUrl(MOVE_TO_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,	PARENT_ID_INPUT, moveToParent);
		moveTo.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitMoveFormUrl));
		int currentTypeId = -1;
		AggregateMDWriter typeGroup = null;
		for (ItemAccessor item : moveToList) {
			if (item.isParentCompatible()) {
				HashSet<String> predecessors = new HashSet<String>(ItemTypeRegistry.getItemPredecessorsExt(itemDesc.getName()));
				HashSet<String> subitems = new HashSet<String>(ItemTypeRegistry.getItemType(item.getItemName()).getAllChildren());
				subitems.retainAll(predecessors);
				if (subitems.size() > 0) {
					String inputValue = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), MOVE_VALUE);
					item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, inputValue, AdminXML.NAME_ATTRIBUTE, MOVING_ITEM_INPUT));
				}
			}
			String setMoveParentUrl = createAdminUrl(SET_MOVE_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, item.getItemId());
			item.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMoveParentUrl));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				moveTo.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		return page;
	}
	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для перемещения в выбранный айтем
	 * @param itemId
	 * @param mountToParent
	 * @return
	 * @throws Exception
	 */
	public AdminPage createToMovePage(long itemId, long toMoveParent) throws Exception {
		if (toMoveParent <= 0)
			toMoveParent = root.getItemId();
		AdminPage page = new AdminPage(TO_MOVE_PAGE, domain, currentUser.getName());
		// Ссылки на другие виды редактирования
		addViewLinks(page, itemId);
		if (itemId <= 0 || itemId == root.getItemId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		ItemAccessor baseItem = mapper.loadItemAccessor(itemId);
		int itemType = baseItem.getTypeId();
		ArrayList<ItemAccessor> toMoveParentPathItems = mapper.loadWholeBranch(toMoveParent, currentUser);
		ArrayList<ItemAccessor> toMoveList = mapper.loadItemsToMove(toMoveParent, itemId);
		// Базовый айтем
		page.addElement(baseItem);
		// Путь к айтемам, к которым можно прикреплять выбранный
		AggregateMDWriter path = new AggregateMDWriter(AdminXML.PATH_ELEMENT);
		for (ItemAccessor pred : toMoveParentPathItems) {
			String setMoveParentUrl = createAdminUrl(SET_TO_MOVE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, pred.getItemId());
			pred.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMoveParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для прикрепления к ним
		AggregateMDWriter toMove = new AggregateMDWriter(AdminXML.MOUNT_ELEMENT);
		page.addElement(toMove);
		String submitMoveFormUrl = createAdminUrl(TO_MOVE_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType, PARENT_ID_INPUT, toMoveParent);
		toMove.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, submitMoveFormUrl));
		int currentTypeId = -1;
		AggregateMDWriter typeGroup = null;
		for (ItemAccessor item : toMoveList) {
			if (item.isParentCompatible()) {
				HashSet<String> predecessors = new HashSet<String>(ItemTypeRegistry.getItemPredecessorsExt(item.getItemName()));
				HashSet<String> subitems = new HashSet<String>(ItemTypeRegistry.getItemType(itemType).getAllChildren());
				subitems.retainAll(predecessors);
				if (subitems.size() > 0) {
					String inputValue = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getItemId(), MOVE_VALUE);
					item.addSubwriter(new LeafMDWriter(AdminXML.INPUT_ELEMENT, inputValue, AdminXML.NAME_ATTRIBUTE, MOVING_ITEM_INPUT));
				}
			}
			String setMoveParentUrl = createAdminUrl(SET_TO_MOVE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, item.getItemId());
			item.addSubwriter(new LeafMDWriter(AdminXML.LINK_ELEMENT, setMoveParentUrl));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(AdminXML.TYPE_ELEMENT, AdminXML.CAPTION_ATTRIBUTE, desc.getCaption());
				toMove.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		return page;
	}
	/**
	 * Страница загрузки картинки (плагин tinyMCE)
	 * @param itemId
	 * @param paramId
	 * @return
	 */
	public AdminPage createImageUploadPage(long itemId, int paramId) {
		AdminPage page = new AdminPage(IMG_UPLOAD_PAGE, domain, currentUser.getName());
		page.addElement(new LeafMDWriter(AdminXML.UPLOAD_LINK_ELEMENT, createAdminUrl(UPLOAD_IMG_ACTION, ITEM_ID_INPUT, itemId,
				PARAM_ID_INPUT, paramId)));
		return page;
	}
	/**
	 * Страница, выводящаяся после загрузки картинки (через плагин tinyMCE)
	 * @param itemId
	 * @param paramId
	 * @return
	 */
	public AdminPage createImageUploadedPage(long itemId, int paramId, ArrayList<FileItem> pictures, String path, String alt) {
		AdminPage page = new AdminPage(IMG_UPLOADED_PAGE, domain, currentUser.getName());
		page.addElement(new LeafMDWriter(AdminXML.UPLOAD_LINK_ELEMENT, createAdminUrl(UPLOAD_START_ACTION, ITEM_ID_INPUT, itemId,
				PARAM_ID_INPUT, paramId)));
		for (FileItem pic : pictures) {
			String filePath = path + FileDataType.getFileName(pic);
			page.addElement(new LeafMDWriter(AdminXML.PATH_ELEMENT, filePath, AdminXML.ALT_ATTRIBUTE, alt));
		}
		return page;
	}
	/**
	 * Заполнение существующих (созданных) айтемов и айтемов для создания
	 * @param parentId - ID родительского айтема
	 * @param baseType - ID типа родительского айтема
	 * @param itemsToAdd - список сабайтемов для добавления (пустой)
	 * @param existingSubitems - список существующих сабайтемов (пустой)
	 * @throws Exception 
	 */
	private HashMap<String, ArrayList<ItemAccessor>> createSubitemsInfo(long parentId, int baseType, ArrayList<ItemToAdd> itemsToAdd)
			throws Exception {
		// Для корневого айтема
		HashMap<String, ArrayList<ItemAccessor>> existingSubitems = null;
		if (parentId == root.getItemId()) {
			existingSubitems = AdminLoader.getLoader().loadClosestSubitems(root, root.getItemId(), currentUser);
			for (ItemType subitemDesc : ItemTypeRegistry.getAllowedTopLevelItems(root.getGroup())) {
				processItemForParent(root.getItemId(), subitemDesc.getName(), root, itemsToAdd, existingSubitems);
			}
		}
		// Для обычных айтемов
		else {
			ItemType parentDesc = ItemTypeRegistry.getItemType(baseType);
			existingSubitems = AdminLoader.getLoader().loadClosestSubitems(parentDesc, parentId, currentUser);
			for (String baseItem : parentDesc.getAllowedSubitemNames(currentUser.getGroup())) {
				processItemForParent(parentId, baseItem, parentDesc, itemsToAdd, existingSubitems);
			}
		}
		return existingSubitems;
	}
	/**
	 * Заполнение существующих (созданных) айтемов и айтемов для создания для корневого атйема
	 * @param rootId
	 * @param itemsToAdd
	 * @return
	 * @throws Exception
	 */
	private HashMap<String, ArrayList<ItemAccessor>> createDefaultRootSubitemsInfo(ArrayList<ItemToAdd> itemsToAdd)
			throws Exception {
		RootItemType dr = ItemTypeRegistry.getDefaultRoot();
		HashMap<String, ArrayList<ItemAccessor>> existingSubitems = AdminLoader.getLoader().loadClosestSubitems(dr, dr.getItemId(),
				currentUser);
		for (ItemType subitemDesc : ItemTypeRegistry.getAllowedTopLevelItems(dr.getGroup())) {
			processItemForParent(dr.getItemId(), subitemDesc.getName(), dr, itemsToAdd, existingSubitems);
		}
		return existingSubitems;
	}
	/**
	 * Обработать один сабайтем для родителя (используестя как часть метода createSubitemsInfo)
	 * @param itemName
	 * @param parentDesc
	 * @param itemsToAdd
	 * @throws SQLException
	 */
	private void processItemForParent(long parentId, String itemName, ItemTypeContainer parentDesc, ArrayList<ItemToAdd> itemsToAdd,
			HashMap<String, ArrayList<ItemAccessor>> existingSubitems) throws SQLException {
		if (parentDesc.isChildMultiple(itemName) || existingSubitems.get(itemName) == null || existingSubitems.get(itemName).isEmpty()) {
			String defaultExtender = ItemTypeRegistry.findItemPredecessor(parentDesc.getAllChildren(), itemName);
			ArrayList<ItemAccessor> subitems = existingSubitems.get(itemName);
			if (subitems != null) {
				defaultExtender = subitems.get(subitems.size() - 1).getItemName();
			}
			boolean isVirtual = parentDesc.isChildVirtual(itemName);
			boolean isPersonal = ItemTypeRegistry.isSubitemPersonal(parentDesc, itemName);
			itemsToAdd.add(new ItemToAdd(itemName, defaultExtender, parentId, isVirtual, isPersonal));
		}
	}
	/**
	 * Создать админский урл с параметрами
	 * @param action
	 * @param parameters
	 * @return
	 */
	public static String createAdminUrl(String action, Object... parameters) {
		StringBuilder sb = new StringBuilder(action);
		sb.append(".action");
		if (parameters.length > 0) {
			sb.append('?');
		}
		for (int i = 0; i < parameters.length;) {
			sb.append(parameters[i++]).append('=').append(parameters[i++]).append('&');
		}
		if (parameters.length > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	/**
	 * Создать URL для действия админа в CMS.
	 * Передается название действия из констант этого класса и список параметров со значениями
	 * @param action
	 * @param attributes
	 * @return
	 */
	public static String createAdminUrl(String action, String...attributes) {
		StringBuilder sb = new StringBuilder(action).append(DOT_ACTION);
		char union = '?';
		for (int i = 0; i < attributes.length; i += 2) {
			sb.append(union).append(attributes[i]).append('=').append(attributes[i + 1]);
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.print(createAdminUrl("cool", "item_id", 10, "name", "mega", "pid", 55));
	}
}
