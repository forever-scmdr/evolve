package ecommander.admin;

import ecommander.model.*;
import ecommander.model.datatypes.DataType.Type;
import ecommander.model.datatypes.FileDataType;
import ecommander.output.*;
import ecommander.pages.ItemHttpPostForm;
import ecommander.pages.UrlParameterFormatConverter;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.*;

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
public class MainAdminPageCreator implements AdminXML {
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
		private final ArrayList<String> extenders;
		private final long parentId;
		private final boolean isVirtual;
		private final byte assocId;

		private ItemToAdd(String baseItem, long parentId, byte assocId, boolean isVirtual) throws SQLException {
			extenders = new ArrayList<>();
			this.baseItem = baseItem;
			this.parentId = parentId;
			this.assocId = assocId;
			this.isVirtual = isVirtual;
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
			xml.startElement(ITEM_TO_ADD_ELEMENT, NAME_ATTRIBUTE, itemDesc.getName(), ID_ATTRIBUTE,
					itemDesc.getTypeId(), CAPTION_ATTRIBUTE, itemDesc.getCaption(), VIRTUAL_ATTRIBUTE, isVirtual);
			for (String ext : extenders) {
				ItemType extender = ItemTypeRegistry.getItemType(ext);
				String createExtUrl = createAdminUrl(CREATE_ITEM_ACTION, PARENT_ID_INPUT, parentId, ITEM_TYPE_INPUT, extender.getTypeId());
				xml.startElement(ITEM_ELEMENT, NAME_ATTRIBUTE, extender.getName(), ID_ATTRIBUTE,
						extender.getTypeId(), CAPTION_ATTRIBUTE, extender.getCaption());
				xml.startElement(CREATE_LINK_ELEMENT).addText(createExtUrl).endElement();
				xml.endElement();
			}
			String createBaseUrl = createAdminUrl(CREATE_ITEM_ACTION, PARENT_ID_INPUT, parentId, ITEM_TYPE_INPUT, itemDesc.getTypeId());
			xml.startElement(CREATE_LINK_ELEMENT).addText(createBaseUrl).endElement();
			xml.endElement();
			return xml;
		}
	}

	// Текущий пользователь
	private User currentUser = null;
	// Текущий домен сайта
	private String domain = null;
	
	MainAdminPageCreator(User user, String domain) {
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
		basePage.addElement(new LeafMDWriter(LINK_ELEMENT, paramsUrl, NAME_ATTRIBUTE, PARAMS_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(LINK_ELEMENT, mountToUrl, NAME_ATTRIBUTE, MOUNT_TO_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(LINK_ELEMENT, toMountUrl, NAME_ATTRIBUTE, TO_MOUNT_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(LINK_ELEMENT, moveToUrl, NAME_ATTRIBUTE, MOVE_TO_VIEW_TYPE));
		basePage.addElement(new LeafMDWriter(LINK_ELEMENT, toMoveUrl, NAME_ATTRIBUTE, TO_MOVE_VIEW_TYPE));
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
	AdminPage createPageBase(String defaultViewType, long baseId, int itemType) throws Exception {
		AdminPage basePage = new AdminPage(BASE_PAGE, domain, currentUser.getName());
		basePage.addElement(new LeafMDWriter(VIEW_TYPE_ELEMENT, defaultViewType));
		basePage.addElement(new LeafMDWriter(BASE_ID_ELEMENT, baseId));
		basePage.addElement(new LeafMDWriter(BASE_TYPE_ELEMENT, itemType));
		// Путь к текущему элементу
		AggregateMDWriter path = new AggregateMDWriter(PATH_ELEMENT);
		ArrayList<ItemAccessor> pathItems = AdminLoader.getLoader().loadWholeBranch(baseId, ItemTypeRegistry.getPrimaryAssoc().getId());
		// Текущий элемент
		if (baseId > 0 && baseId != ItemTypeRegistry.getDefaultRoot().getId()) {
			ItemAccessor item = AdminLoader.getLoader().loadItemAccessor(baseId);
			if (item != null) {
				basePage.addElement(item);
			}
		}
		for (ItemAccessor pred : pathItems) {
			String editUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, pred.getId(), ITEM_TYPE_INPUT, pred.getTypeId());
			pred.addSubwriter(new LeafMDWriter(EDIT_LINK_ELEMENT, editUrl));
			path.addSubwriter(pred);
		}
		basePage.addElement(path);
		// Ссылка на сабайтемы и на другие части страницы
		String subitemsUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, SUBITEMS_VIEW_TYPE, ITEM_ID_INPUT, baseId, ITEM_TYPE_INPUT, itemType);
		basePage.addElement(new LeafMDWriter(LINK_ELEMENT, subitemsUrl, NAME_ATTRIBUTE, SUBITEMS_VIEW_TYPE));
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
	AdminPage createSubitemsPage(long baseId, int itemType) throws Exception {
		AdminPage basePage = new AdminPage(SUBITEMS_PAGE, domain, currentUser.getName());
		ArrayList<ItemToAdd> itemsToAdd = new ArrayList<>();
		if (baseId <= 0) {
			baseId = ItemTypeRegistry.getDefaultRoot().getId();
		} else if (itemType <= 0) {
			ItemAccessor baseItem = AdminLoader.getLoader().loadItemAccessor(baseId);
			itemType = baseItem.getTypeId();
		}
		ArrayList<ItemAccessor> subitems = createSubitemsInfo(baseId, itemType, itemsToAdd);
		Collections.sort(subitems);
		// Отсортировать по ассоциации
		Collections.sort(itemsToAdd, new Comparator<ItemToAdd>() {
			@Override
			public int compare(ItemToAdd o1, ItemToAdd o2) {
				return o1.assocId - o2.assocId;
			}
		});
		byte currentAssoc = -1;
		AggregateMDWriter assocWriter = new AggregateMDWriter("empty");
		for (ItemToAdd itemToAdd : itemsToAdd) {
			if (currentAssoc != itemToAdd.assocId) {
				Assoc assoc = ItemTypeRegistry.getAssoc(itemToAdd.assocId);
				assocWriter = new AggregateMDWriter(ASSOC_ELEMENT, NAME_ATTRIBUTE, assoc.getName(),
						CAPTION_ATTRIBUTE, assoc.getCaption(), ID_ATTRIBUTE, assoc.getId());
				basePage.addElement(assocWriter);
				currentAssoc = itemToAdd.assocId;
			}
			assocWriter.addSubwriter(itemToAdd);
		}
		currentAssoc = -1;
		assocWriter = new AggregateMDWriter("empty");
		for (ItemAccessor subitem : subitems) {
			if (currentAssoc != subitem.getContextAssoc()) {
				Assoc assoc = ItemTypeRegistry.getAssoc(subitem.getContextAssoc());
				assocWriter = new AggregateMDWriter(ASSOC_ELEMENT, NAME_ATTRIBUTE, assoc.getName(),
						CAPTION_ATTRIBUTE, assoc.getCaption(), ID_ATTRIBUTE, assoc.getId());
				basePage.addElement(assocWriter);
				currentAssoc = subitem.getContextAssoc();
			}
			String delUrl = createAdminUrl(DELETE_ITEM_ACTION, ITEM_ID_INPUT, subitem.getId(), ITEM_TYPE_INPUT, itemType, PARENT_ID_INPUT, baseId);
			String editUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, subitem.getId(), ITEM_TYPE_INPUT, subitem.getTypeId());
			String copyUrl = createAdminUrl(COPY_ACTION, ITEM_ID_INPUT, subitem.getId(), PARENT_ID_INPUT, baseId, ITEM_TYPE_INPUT, itemType);
			subitem.addSubwriter(new LeafMDWriter(DELETE_LINK_ELEMENT, delUrl));
			subitem.addSubwriter(new LeafMDWriter(EDIT_LINK_ELEMENT, editUrl));
			subitem.addSubwriter(new LeafMDWriter(COPY_LINK_ELEMENT, copyUrl));
			assocWriter.addSubwriter(subitem);
		}
		String reorderUrl = createAdminUrl(REORDER_ACTION, 
				ITEM_ID_INPUT, ":id:", 
				WEIGHT_BEFORE_INPUT, ":wb:", 
				WEIGHT_AFTER_INPUT, ":wa:",
				ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, baseId);
		String getPasteBufferUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, PASTE_VIEW_TYPE, PARENT_ID_INPUT, baseId, ITEM_TYPE_INPUT,
				itemType);
		basePage.addElement(new LeafMDWriter(GET_PASTE_LINK_ELEMENT, getPasteBufferUrl));
		basePage.addElement(new LeafMDWriter(LINK_ELEMENT, reorderUrl, NAME_ATTRIBUTE, REORDER_VALUE));
		return basePage;
	}

	/**
	 * Страница для вставки сопированных айтемов
	 * @param session
	 * @param newParentId
	 * @param newParentTypeId
	 * @return
	 */
	AdminPage createPastePage(HttpSession session, long newParentId, int newParentTypeId) {
		AdminPage page = new AdminPage(PASTE_PAGE, domain, currentUser.getName());
		@SuppressWarnings("unchecked")
		LinkedHashMap<Long, ItemAccessor> buffer = (LinkedHashMap<Long, ItemAccessor>) session.getAttribute(PASTE_LIST);
		if (buffer == null)
			return page;
		for (ItemAccessor item : buffer.values()) {
			item.clearSubwriters();
			String deleteUrl = createAdminUrl(DELETE_PASTE_ACTION, ITEM_ID_INPUT, item.getId(), PARENT_ID_INPUT, newParentId,
					ITEM_TYPE_INPUT, newParentTypeId);
			String editUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, item.getId(), ITEM_TYPE_INPUT, item.getTypeId());
			item.addSubwriter(new LeafMDWriter(DELETE_LINK_ELEMENT, deleteUrl));
			item.addSubwriter(new LeafMDWriter(EDIT_LINK_ELEMENT, editUrl));
			String pasteUrl = createAdminUrl(PASTE_ACTION, ITEM_ID_INPUT, item.getId(), PARENT_ID_INPUT, newParentId, ITEM_TYPE_INPUT,
					newParentTypeId);
			item.addSubwriter(new LeafMDWriter(PASTE_LINK_ELEMENT, pasteUrl));
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
	AdminPage createParamsPage(long itemId, boolean isInline, boolean isVisual) throws Exception {
		AdminPage basePage = new AdminPage(isInline ? INLINE_PAGE : PARAMETERS_PAGE, domain, currentUser.getName());
		basePage.addElement(new LeafMDWriter(VISUAL_ELEMENT, isVisual));
		Item item = null;
		if (itemId != ItemTypeRegistry.getDefaultRoot().getId())
			item = ItemQuery.loadById(itemId);
		if (item != null) {
			ItemHttpPostForm itemForm = new ItemHttpPostForm(item, basePage.getName());
			ItemFormMDWriter formWriter = new ItemFormMDWriter(itemForm, FORM_ELEMENT);
			formWriter.setAction(createAdminUrl(SAVE_ITEM_ACTION));
			basePage.addElement(formWriter);
			basePage.addElement(new LeafMDWriter(LINK_ELEMENT, createAdminUrl(DELETE_PARAMETER_ACTION, PARAM_ID_INPUT, "")));
			basePage.addElement(new LeafMDWriter(OPEN_ASSOC_LINK_ELEMENT, createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT,
					ASSOCIATE_VIEW_TYPE, ITEM_ID_INPUT, itemId, PARENT_ID_INPUT, 0, PARAM_ID_INPUT, "")));
			// Ссылка на загрузку картинки
			// Надо найти параметр, в котором должна храниться подгружаемая картинка
			// Также надо найти все ассоциированные айтемы и загрузить их. Они хранястя в параметрах типа associated
			int paramId = 0;
			for (ParameterDescription param : item.getItemType().getParameterList()) {
				if (param.getType() == Type.PICTURE && param.isMultiple() && paramId == 0) {
					paramId = param.getId();
				}
			}
			if (paramId > 0)
				basePage.addElement(new LeafMDWriter(UPLOAD_LINK_ELEMENT, createAdminUrl(UPLOAD_START_ACTION, ITEM_ID_INPUT,
						item.getId(), PARAM_ID_INPUT, paramId)));
		}
		// Ссылки на другие виды редактирования
		addViewLinks(basePage, itemId);
		return basePage;
	}
	/**
	 * Создает часть страницы с формой редактирования нового айтема
	 * @param itemType
	 * @param parentId
	 * @return
	 */
	AdminPage createParamsPage(int itemType, long parentId, boolean isVisual) {
		AdminPage basePage = new AdminPage( PARAMETERS_PAGE, domain, currentUser.getName());
		basePage.addElement(new LeafMDWriter(VISUAL_ELEMENT, isVisual));
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemType);
		ItemHttpPostForm itemForm = new ItemHttpPostForm(itemDesc, parentId, basePage.getName());
		ItemFormMDWriter formWriter = new ItemFormMDWriter(itemForm, FORM_ELEMENT);
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
	AdminPage createMountToPage(long itemId, long mountToParent) throws Exception {
		if (mountToParent <= 0)
			mountToParent = ItemTypeRegistry.getDefaultRoot().getId();
		AdminPage page = new AdminPage(MOUNT_TO_PAGE, domain, currentUser.getName());
		// Ссылки на другие виды редактирования
		addViewLinks(page, itemId);
		if (itemId <= 0 || itemId == ItemTypeRegistry.getDefaultRoot().getId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		ItemAccessor baseItem = mapper.loadItemAccessor(itemId);
		int itemType = baseItem.getTypeId();
		ArrayList<ItemAccessor> mountToParentPathItems = mapper.loadWholeBranch(mountToParent, ItemTypeRegistry.getPrimaryAssoc().getId());
		ArrayList<ItemAccessor> mountToList = mapper.loadClosestSubitems(mountToParent, currentUser);
		ArrayList<ItemAccessor> mountedList = mapper.loadDirectParents(itemId, currentUser);
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemType);
		// Базовый айтем
		page.addElement(baseItem);
		// Путь к айтемам, к которым можно прикреплять выбранный
		AggregateMDWriter path = new AggregateMDWriter(PATH_ELEMENT);
		for (ItemAccessor pred : mountToParentPathItems) {
			String setMountParentUrl = createAdminUrl(SET_MOUNT_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, pred.getId());
			pred.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setMountParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для прикрепления к ним
		AggregateMDWriter mountTo = new AggregateMDWriter(MOUNT_ELEMENT);
		page.addElement(mountTo);
		String submitMountFormUrl = createAdminUrl(CREATE_MOUNT_TO_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, mountToParent);
		mountTo.addSubwriter(new LeafMDWriter(LINK_ELEMENT, submitMountFormUrl));
		byte currentAssocId = -1;
		AggregateMDWriter assocWriter = new AggregateMDWriter("empty");
		for (ItemAccessor item : mountToList) {
			Set<Assoc> assocs = ItemTypeRegistry.getDirectContainerAssocs(item.getTypeId(), baseItem.getTypeId());
			for (Assoc assoc : assocs) {
				String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getId(), MOUNT_INPUT_PREFIX);
				item.addSubwriter(new LeafMDWriter(INPUT_ELEMENT, ADD_VALUE, NAME_ATTRIBUTE, inputName));
			}
			String setMountParentUrl = createAdminUrl(SET_MOUNT_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, item.getId());
			item.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setMountParentUrl));
			// Создать новую группу по ассоциации, если у текущего айтема ассоциацйия не совпадает с ассоциацией предыдущего айтема
			if (item.getContextAssoc() != currentAssocId) {
				Assoc assoc = ItemTypeRegistry.getAssoc(item.getContextAssoc());
				assocWriter = new AggregateMDWriter(ASSOC_ELEMENT, NAME_ATTRIBUTE, assoc.getName(),
						CAPTION_ATTRIBUTE, assoc.getCaption(), ID_ATTRIBUTE, assoc.getId());
				mountTo.addSubwriter(assocWriter);
				currentAssocId = item.getContextAssoc();
			}
			assocWriter.addSubwriter(item);
		}
		// Уже прикрепленные элементы
		AggregateMDWriter mounted = new AggregateMDWriter(MOUNTED_ELEMENT);
		page.addElement(mounted);
		String submitUnmountFormUrl = createAdminUrl(DELETE_REFERENCE_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
				PARENT_ID_INPUT, mountToParent);
		mounted.addSubwriter(new LeafMDWriter(LINK_ELEMENT, submitUnmountFormUrl));
		currentAssocId = -1;
		assocWriter = new AggregateMDWriter("empty");
		for (ItemAccessor item : mountedList) {
			String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getId(), UNMOUNT_INPUT_PREFIX);
			item.addSubwriter(new LeafMDWriter(INPUT_ELEMENT, DELETE_VALUE, NAME_ATTRIBUTE, inputName));
			// Создать новую группу по ассоциации, если у текущего айтема ассоциацйия не совпадает с ассоциацией предыдущего айтема
			if (item.getContextAssoc() != currentAssocId) {
				Assoc assoc = ItemTypeRegistry.getAssoc(item.getContextAssoc());
				assocWriter = new AggregateMDWriter(ASSOC_ELEMENT, NAME_ATTRIBUTE, assoc.getName(),
						CAPTION_ATTRIBUTE, assoc.getCaption(), ID_ATTRIBUTE, assoc.getId());
				mounted.addSubwriter(assocWriter);
				currentAssocId = item.getContextAssoc();
			}
			assocWriter.addSubwriter(item);
		}
		return page;
	}

	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для ассоциации с выбранным айтемом
	 * Эта страница выводится, когда пользователь нажимает ссылку "Создать связь" рядом с названием ассоциации
	 * в части страницы, где перечислены новые айтемы для создания или сабайетмы, сгруппированные по ассоциации
	 * @param itemId
	 * @param associateParent
	 * @param assocId
	 * @return
	 * @throws Exception
	 */
	AdminPage createAssociatedPage(long itemId, long associateParent, byte assocId) throws Exception {
		if (associateParent <= 0) {
			associateParent = ItemTypeRegistry.getDefaultRoot().getId();
		}
		AdminPage page = new AdminPage(ASSOCIATE_PAGE, domain, currentUser.getName());
		if (itemId <= 0 || itemId == ItemTypeRegistry.getDefaultRoot().getId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		ItemAccessor baseAcc = mapper.loadItemAccessor(itemId);
		ArrayList<ItemAccessor> mountToParentPathItems = mapper.loadWholeBranch(associateParent, ItemTypeRegistry.getPrimaryAssoc().getId());
		ArrayList<ItemAccessor> toAssoc = mapper.loadClosestSubitems(associateParent, currentUser);

		// Базовый айтем
		page.addElement(baseAcc);
		// Путь к айтемам, которые можно прикреплять к выбранному
		AggregateMDWriter path = new AggregateMDWriter(PATH_ELEMENT);
		for (ItemAccessor pred : mountToParentPathItems) {
			String setAssocParentUrl = createAdminUrl(SET_ASSOCIATE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT,
					baseAcc.getTypeId(), PARENT_ID_INPUT, pred.getId(), PARAM_ID_INPUT, assocId);
			pred.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setAssocParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для прикрепления
		AggregateMDWriter associate = new AggregateMDWriter(MOUNT_ELEMENT);
		page.addElement(associate);
		String submitAssociateFormUrl = createAdminUrl(CREATE_ASSOCIATED_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, baseAcc.getTypeId(),
				PARENT_ID_INPUT, associateParent, PARAM_ID_INPUT, assocId);
		associate.addSubwriter(new LeafMDWriter(LINK_ELEMENT, submitAssociateFormUrl));
		byte currentAssocId = -1;
		AggregateMDWriter assocWriter = new AggregateMDWriter("empty");
		for (ItemAccessor item : toAssoc) {
			if (ItemTypeRegistry.isDirectContainer(item.getTypeId(), baseAcc.getTypeId(), assocId)) {
				String inputName = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getId(), MOUNT_INPUT_PREFIX);
				item.addSubwriter(new LeafMDWriter(INPUT_ELEMENT, ADD_VALUE, NAME_ATTRIBUTE, inputName));
			}
			String setAssocParentUrl = createAdminUrl(SET_ASSOCIATE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, item.getTypeId(),
					PARENT_ID_INPUT, item.getId(), PARAM_ID_INPUT, assocId);
			item.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setAssocParentUrl));
			associate.addSubwriter(item);
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getContextAssoc() != currentAssocId) {
				Assoc assoc = ItemTypeRegistry.getAssoc(item.getContextAssoc());
				assocWriter = new AggregateMDWriter(ASSOC_ELEMENT, NAME_ATTRIBUTE, assoc.getName(),
						CAPTION_ATTRIBUTE, assoc.getCaption(), ID_ATTRIBUTE, assoc.getId());
				associate.addSubwriter(assocWriter);
				currentAssocId = item.getContextAssoc();
			}
			assocWriter.addSubwriter(item);
		}

		// Ссылка для перезагрузки родительской страницы
		String reloadUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, baseAcc.getId(), ITEM_TYPE_INPUT, baseAcc.getTypeId());
		page.addElement(new LeafMDWriter(LINK_ELEMENT, reloadUrl));
		return page;
	}
	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для перемещения в них выбранного айтема
	 * @param itemId
	 * @param moveToParent
	 * @return
	 * @throws Exception
	 */
	AdminPage createMoveToPage(long itemId, long moveToParent) throws Exception {
		if (moveToParent <= 0)
			moveToParent = ItemTypeRegistry.getDefaultRoot().getId();
		AdminPage page = new AdminPage(MOVE_TO_PAGE, domain, currentUser.getName());
		// Ссылки на другие виды редактирования
		addViewLinks(page, itemId);
		if (itemId <= 0 || itemId == ItemTypeRegistry.getDefaultRoot().getId())
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
		AggregateMDWriter path = new AggregateMDWriter(PATH_ELEMENT);
		for (ItemAccessor pred : moveToParentPathItems) {
			String setMoveParentUrl = createAdminUrl(SET_MOVE_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, pred.getId());
			pred.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setMoveParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для перемещения к ним
		AggregateMDWriter moveTo = new AggregateMDWriter(MOUNT_ELEMENT);
		page.addElement(moveTo);
		String submitMoveFormUrl = createAdminUrl(MOVE_TO_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,	PARENT_ID_INPUT, moveToParent);
		moveTo.addSubwriter(new LeafMDWriter(LINK_ELEMENT, submitMoveFormUrl));
		int currentTypeId = -1;
		AggregateMDWriter typeGroup = null;
		for (ItemAccessor item : moveToList) {
			if (item.isParentCompatible()) {
				HashSet<String> predecessors = new HashSet<>(ItemTypeRegistry.getItemPredecessorsExt(itemDesc.getName()));
				HashSet<String> subitems = new HashSet<>(ItemTypeRegistry.getItemType(item.getItemName()).getAllChildren());
				subitems.retainAll(predecessors);
				if (subitems.size() > 0) {
					String inputValue = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getId(), MOVE_VALUE);
					item.addSubwriter(new LeafMDWriter(INPUT_ELEMENT, inputValue, NAME_ATTRIBUTE, MOVING_ITEM_INPUT));
				}
			}
			String setMoveParentUrl = createAdminUrl(SET_MOVE_TO_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, item.getId());
			item.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setMoveParentUrl));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(TYPE_ELEMENT, CAPTION_ATTRIBUTE, desc.getCaption());
				moveTo.addSubwriter(typeGroup);
			}
			typeGroup.addSubwriter(item);
		}
		return page;
	}
	/**
	 * Создает часть страницы, которая содержит список айтемов, доступных для перемещения в выбранный айтем
	 * @param itemId
	 * @param toMoveParent
	 * @return
	 * @throws Exception
	 */
	AdminPage createToMovePage(long itemId, long toMoveParent) throws Exception {
		if (toMoveParent <= 0)
			toMoveParent = ItemTypeRegistry.getDefaultRoot().getId();
		AdminPage page = new AdminPage(TO_MOVE_PAGE, domain, currentUser.getName());
		// Ссылки на другие виды редактирования
		addViewLinks(page, itemId);
		if (itemId <= 0 || itemId == ItemTypeRegistry.getDefaultRoot().getId())
			return page;
		AdminLoader mapper = AdminLoader.getLoader();
		ItemAccessor baseItem = mapper.loadItemAccessor(itemId);
		int itemType = baseItem.getTypeId();
		ArrayList<ItemAccessor> toMoveParentPathItems = mapper.loadWholeBranch(toMoveParent, currentUser);
		ArrayList<ItemAccessor> toMoveList = mapper.loadItemsToMove(toMoveParent, itemId);
		// Базовый айтем
		page.addElement(baseItem);
		// Путь к айтемам, к которым можно прикреплять выбранный
		AggregateMDWriter path = new AggregateMDWriter(PATH_ELEMENT);
		for (ItemAccessor pred : toMoveParentPathItems) {
			String setMoveParentUrl = createAdminUrl(SET_TO_MOVE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, pred.getId());
			pred.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setMoveParentUrl));
			path.addSubwriter(pred);
		}
		page.addElement(path);
		// Элементы для прикрепления к ним
		AggregateMDWriter toMove = new AggregateMDWriter(MOUNT_ELEMENT);
		page.addElement(toMove);
		String submitMoveFormUrl = createAdminUrl(TO_MOVE_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType, PARENT_ID_INPUT, toMoveParent);
		toMove.addSubwriter(new LeafMDWriter(LINK_ELEMENT, submitMoveFormUrl));
		int currentTypeId = -1;
		AggregateMDWriter typeGroup;
		for (ItemAccessor item : toMoveList) {
			if (item.isParentCompatible()) {
				HashSet<String> predecessors = new HashSet<>(ItemTypeRegistry.getItemPredecessorsExt(item.getItemName()));
				HashSet<String> subitems = new HashSet<>(ItemTypeRegistry.getItemType(itemType).getAllChildren());
				subitems.retainAll(predecessors);
				if (subitems.size() > 0) {
					String inputValue = UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getId(), MOVE_VALUE);
					item.addSubwriter(new LeafMDWriter(INPUT_ELEMENT, inputValue, NAME_ATTRIBUTE, MOVING_ITEM_INPUT));
				}
			}
			String setMoveParentUrl = createAdminUrl(SET_TO_MOVE_PARENT_ACTION, ITEM_ID_INPUT, itemId, ITEM_TYPE_INPUT, itemType,
					PARENT_ID_INPUT, item.getId());
			item.addSubwriter(new LeafMDWriter(LINK_ELEMENT, setMoveParentUrl));
			// Создать новую группу по типам, если у текущего айтема тип не совпадает с типом предыдущего айтема
			if (item.getTypeId() != currentTypeId) {
				currentTypeId = item.getTypeId();
				ItemType desc = ItemTypeRegistry.getItemType(currentTypeId);
				typeGroup = new AggregateMDWriter(TYPE_ELEMENT, CAPTION_ATTRIBUTE, desc.getCaption());
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
	AdminPage createImageUploadPage(long itemId, int paramId) {
		AdminPage page = new AdminPage(IMG_UPLOAD_PAGE, domain, currentUser.getName());
		page.addElement(new LeafMDWriter(UPLOAD_LINK_ELEMENT, createAdminUrl(UPLOAD_IMG_ACTION, ITEM_ID_INPUT, itemId,
				PARAM_ID_INPUT, paramId)));
		return page;
	}
	/**
	 * Страница, выводящаяся после загрузки картинки (через плагин tinyMCE)
	 * @param itemId
	 * @param paramId
	 * @return
	 */
	AdminPage createImageUploadedPage(long itemId, int paramId, ArrayList<FileItem> pictures, String path, String alt) {
		AdminPage page = new AdminPage(IMG_UPLOADED_PAGE, domain, currentUser.getName());
		page.addElement(new LeafMDWriter(UPLOAD_LINK_ELEMENT, createAdminUrl(UPLOAD_START_ACTION, ITEM_ID_INPUT, itemId,
				PARAM_ID_INPUT, paramId)));
		for (FileItem pic : pictures) {
			String filePath = path + FileDataType.getFileName(pic);
			page.addElement(new LeafMDWriter(PATH_ELEMENT, filePath, ALT_ATTRIBUTE, alt));
		}
		return page;
	}
	/**
	 * Заполнение существующих (созданных) айтемов и айтемов для создания
	 * @param parentId - ID родительского айтема
	 * @param baseType - ID типа родительского айтема
	 * @param itemsToAdd - список сабайтемов для добавления (пустой)
	 * @throws Exception
	 * @return
	 */
	private ArrayList<ItemAccessor> createSubitemsInfo(long parentId, int baseType, ArrayList<ItemToAdd> itemsToAdd)
			throws Exception {
		// Для корневого айтема
		long rootId = ItemTypeRegistry.getDefaultRoot().getId();
		ArrayList<ItemAccessor> existingSubitems;
		if (parentId == rootId) {
			existingSubitems = AdminLoader.getLoader().loadSuperUserRootItems(currentUser);
			for (ItemTypeContainer.ChildDesc childDesc : ItemTypeRegistry.getDefaultRoot().getAllChildren()) {
				processItemForParent(rootId, childDesc, ItemTypeRegistry.getDefaultRoot(), itemsToAdd, existingSubitems);
			}
		}
		// Для обычных айтемов
		else {
			ItemType parentDesc = ItemTypeRegistry.getItemType(baseType);
			existingSubitems = AdminLoader.getLoader().loadClosestSubitems(parentId, currentUser);
			for (ItemTypeContainer.ChildDesc childDesc : parentDesc.getAllChildren()) {
				processItemForParent(parentId, childDesc, parentDesc, itemsToAdd, existingSubitems);
			}
		}
		return existingSubitems;
	}
	/**
	 * Обработать один сабайтем для родителя (используестя как часть метода createSubitemsInfo)
	 * @param parentDesc
	 * @param itemsToAdd
	 * @throws SQLException
	 */
	private void processItemForParent(long parentId, ItemTypeContainer.ChildDesc childDesc, ItemTypeContainer parentDesc,
	                                  ArrayList<ItemToAdd> itemsToAdd, ArrayList<ItemAccessor> existingSubitems) throws SQLException {
		boolean addItemToAdd = false;
		byte assocId = ItemTypeRegistry.getAssoc(childDesc.assocName).getId();
		if (!parentDesc.isChildMultiple(childDesc.assocName, childDesc.itemName)) {
			for (ItemAccessor subitem : existingSubitems) {
				if (subitem.getContextAssoc() == assocId
						&& ItemTypeRegistry.getItemPredecessorsExt(subitem.getItemName()).contains(childDesc.itemName)) {
					break;
				}
			}
		} else {
			addItemToAdd = true;
		}
		if (addItemToAdd) {
			boolean isVirtual = parentDesc.isChildVirtual(childDesc.assocName, childDesc.itemName);
			itemsToAdd.add(new ItemToAdd(childDesc.itemName, parentId, assocId, isVirtual));
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
	private static String createAdminUrl(String action, String... attributes) {
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
