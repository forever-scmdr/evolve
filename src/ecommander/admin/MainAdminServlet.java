package ecommander.admin;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.controllers.SessionContext;
import ecommander.filesystem.DeleteItemFileUnit;
import ecommander.filesystem.SaveItemFileUnit;
import ecommander.filesystem.SaveItemFilesUnit;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ResizeImagesFactory;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.*;
import ecommander.pages.MultipleHttpPostForm;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Класс контроллер для админской части
 * @author EEEE
 */
public class MainAdminServlet extends BasicAdminServlet {
	
	private static final long serialVersionUID = 1388493441159548824L;
	public static final String ENABLE_VISUAL_EDITING_ACTION = "admin_visual";
	public static final String TARGET_PARAM = "target";
	
	
	private static class UserInput {
		// ID типа айтема
		private int itemTypeId= -1;
		// Является ли действие пользователя действием в режиме визуального редактирования
		private boolean isVisual = false;
		// Является ли создаваемый айтем персональным (или общим для группы)
		@SuppressWarnings("unused")
		private boolean isPersonal = false;
		// ID айтема
		private long itemId = -1;
		// Поисковый запрос (Антон)
		private String searchQuery = null;
		// ID айтема (родительский)
		private long parentId = -1;
		// Название и ID айтема, который перемещается или в который перемещается текущий
		private String movingItem = null;
		// Название множественного парамтера
		private int paramId = -1;
		// ID ассоциации
		private byte assocId = -1;
		private int index = -1;
		private int newItemPosition = -1;
		// Страница, в случае если есть ограничение по выводу айтемов
		private int page = 1;
		// Айтемы для операций по стозданию ссылок
		private HashMap<String, String> mount = new HashMap<>();
		// Запрашиваемый вид страницы для вывода
		private String viewType = "base";
		// Сеанс пользователя
		private HttpSession session = null;
	}
	
	/**
	 * Выбор действия, которое должно выполниться
	 */
	protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		AdminPage result = null;
		
//		if (!checkUser(req, resp, MainAdminPageCreator.INITIALIZE_ACTION + ".action")) return;
		
		if (!checkUser(req, resp, getRequestStrig(req))) return;
		
		UserInput input = start(req);
		String actionName = getAction(req);
		MainAdminPageCreator pageCreator = new MainAdminPageCreator(getCurrentAdmin(), getContextPath(req));
		if (actionName.equalsIgnoreCase(MainAdminPageCreator.INITIALIZE_ACTION))
			result = initialize(pageCreator);
		else if (actionName.equals(MainAdminPageCreator.SET_ITEM_ACTION))
			result = setItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.CREATE_ITEM_ACTION))
			result = createItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.DELETE_ITEM_ACTION))
			result = deleteItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.SAVE_ITEM_ACTION))
			result = saveItem(input, req, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.REORDER_ACTION))
			result = setNewItemIndex(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.ADD_PARAMETER_ACTION))
			result = addParameter(input, req, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.DELETE_PARAMETER_ACTION))
			result = deleteParameter(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.SET_MOUNT_TO_PARENT_ACTION))
			result = setMountToParentItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.SET_TO_MOUNT_PARENT_ACTION))
			result = setToMountParentItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.SET_ASSOCIATE_PARENT_ACTION))
			result = setAssociatedParentItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.SET_MOVE_TO_PARENT_ACTION))
			result = setMoveToParentItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.SET_TO_MOVE_PARENT_ACTION))
			result = setToMoveParentItem(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.CREATE_MOUNT_TO_ACTION))
			result = createMountedToReferences(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.CREATE_TO_MOUNT_ACTION))
			result = createAssociations(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.CREATE_ASSOCIATED_ACTION))
			result = createAssociations(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.MOVE_TO_ACTION))
			result = moveTo(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.TO_MOVE_ACTION))
			result = move(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.DELETE_REFERENCE_ACTION))
			result = deleteReference(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.DELETE_ASSOCIATED_ACTION))
			result = deleteAssociated(input, pageCreator);
		else if (actionName.equals(MainAdminPageCreator.GET_VIEW_ACTION))
			result = getView(input, pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.REINDEX_ACTION))
			result = reindex(pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.DROP_ALL_CACHES_ACTION))
			result = dropAllCaches(pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.UPLOAD_IMG_ACTION))
			result = uploadImage(input, req, pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.COPY_ACTION))
			result = copy(input, pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.DELETE_PASTE_ACTION))
			result = deletePaste(input, pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.PASTE_ACTION))
			result = paste(input, pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.STATUS_ACTION))
			result = toggleItem(input, pageCreator);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.NEW_GROUP_ACTION))
			result = setNewUserGroup(input, pageCreator, false);
		else if (actionName.equalsIgnoreCase(MainAdminPageCreator.NEW_USER_ACTION))
			result = setNewUserGroup(input, pageCreator, true);
		else if (actionName.equalsIgnoreCase(ENABLE_VISUAL_EDITING_ACTION)) {
			SessionContext.createSessionContext(req).setContentUpdateMode(true);
			String target = req.getParameter(TARGET_PARAM);
			resp.sendRedirect(target);
			return;
		}
		// Форвард
		if (result != null) {
			result.output(resp);
		}
	}
	/**
	 * Начало работы
	 * @throws FileUploadException
	 * @throws UnsupportedEncodingException
	 */
	private UserInput start(HttpServletRequest req) throws FileUploadException, UnsupportedEncodingException {
		UserInput input = new UserInput();
		// Если запрос multipart/formdata, то обрабатывать его как file upload
		input.mount = new HashMap<>();
//		if (!ServletFileUpload.isMultipartContent(req)) {
			// Простые параметры
			input.movingItem = req.getParameter(MainAdminPageCreator.MOVING_ITEM_INPUT);
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.ITEM_ID_INPUT)))
				input.itemId = Long.parseLong(req.getParameter(MainAdminPageCreator.ITEM_ID_INPUT));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.ITEM_TYPE_INPUT)))
				input.itemTypeId = Integer.parseInt(req.getParameter(MainAdminPageCreator.ITEM_TYPE_INPUT));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.PARENT_ID_INPUT)))
				input.parentId = Long.parseLong(req.getParameter(MainAdminPageCreator.PARENT_ID_INPUT));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.ASSOC_ID_INPUT)))
				input.assocId = Byte.parseByte(req.getParameter(MainAdminPageCreator.ASSOC_ID_INPUT));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.INDEX_INPUT)))
				input.index = Integer.parseInt(req.getParameter(MainAdminPageCreator.INDEX_INPUT));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.NEW_ITEM_POSITION)))
				input.newItemPosition = Integer.parseInt(req.getParameter(MainAdminPageCreator.NEW_ITEM_POSITION));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.PARAM_ID_INPUT)))
				input.paramId = Integer.parseInt(req.getParameter(MainAdminPageCreator.PARAM_ID_INPUT));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.VIEW_TYPE_INPUT)))
				input.viewType = req.getParameter(MainAdminPageCreator.VIEW_TYPE_INPUT);
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.VISUAL_INPUT)))
				input.isVisual = Boolean.parseBoolean(req.getParameter(MainAdminPageCreator.VISUAL_INPUT));
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.SEARCH_INPUT)))
				input.searchQuery = req.getParameter(MainAdminPageCreator.SEARCH_INPUT);
			if (!StringUtils.isBlank(req.getParameter(MainAdminPageCreator.PAGE_INPUT)))
				input.page = NumberUtils.toInt(req.getParameter(MainAdminPageCreator.PAGE_INPUT), 1);
			// Создание ссылок
			Enumeration<String> paramNames = req.getParameterNames();
			while (paramNames.hasMoreElements()) {
				String inputName = paramNames.nextElement();
				if (inputName.startsWith(MainAdminPageCreator.MOUNT_INPUT_PREFIX) || inputName.startsWith(MainAdminPageCreator.UNMOUNT_INPUT_PREFIX))
					input.mount.put(inputName, req.getParameter(inputName));
			}
			input.session = req.getSession();
//		}
		return input;
	}
	/**
	 * Начало работы с CMS, выбран корневой айтем
	 * 
	 * Параметры
	 *  - не требуются
	 *  
	 * @return
	 * @throws Exception 
	 */
	private AdminPage initialize(MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createPageBase(MainAdminPageCreator.PARAMS_VIEW_TYPE, 0, 0);
		page.addMessage("Приложение готово к работе, выбран корневой элемент", false);
		return page;
	}
	/**
	 * Переиндексация всех айтемов (Lucene)
	 * @param pageCreator
	 * @return
	 * @throws Exception
	 */
	private AdminPage reindex(MainAdminPageCreator pageCreator) throws Exception {
		LuceneIndexMapper.reindexAll();
		AdminPage page = pageCreator.createPageBase(MainAdminPageCreator.PARAMS_VIEW_TYPE, 0, 0);
		page.addMessage("Переиндексация завершена успешно", false);
		return page;
	}
	/**
	 * Сбросить все кеши.
	 * Также загрузить все айтемы и сохранить их заново для того, чтобы
	 * ко всем айтемам применились изменения в параметрах (model.xml, model_custom.xml)
	 * @param pageCreator
	 * @return
	 * @throws Exception
	 */
	private AdminPage dropAllCaches(MainAdminPageCreator pageCreator) throws Exception {
		PageController.clearCache();
		Connection conn = null;
		Statement stmt = null;
		int count = 0;
		try {
			conn = MysqlConnector.getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT " + DBConstants.ItemTbl.I_ID + " FROM " + DBConstants.ItemTbl.ITEM_TBL
					+ " WHERE " + DBConstants.ItemTbl.I_TYPE_ID + " > 0");
			DelayedTransaction tr = new DelayedTransaction(getCurrentAdmin());
			while (rs.next()) {
				Item item = AdminLoader.loadItem(rs.getLong(1), getCurrentAdmin());
				if (item == null)
					continue;
				item.forceInitialInconsistent();
				tr.addCommandUnit(SaveItemDBUnit.get(item).ignoreUser().dontCloseFulltextIndexWriter());
				if (tr.getCommandCount() >= 10) {
					tr.execute();
				}
				count++;
				if (count % 500 == 0)
					ServerLogger.warn("Updated " + count + " items");
			}
			tr.execute();
			LuceneIndexMapper.commit();
			LuceneIndexMapper.closeWriter();
		} catch (Exception e) {
			ServerLogger.error(e);
		} finally {
			MysqlConnector.closeStatement(stmt); // TODO !!!!!!!!!!!!!!
			MysqlConnector.closeConnection(conn);			
		}
		AdminPage page = pageCreator.createPageBase(MainAdminPageCreator.PARAMS_VIEW_TYPE, 0, 0);
		page.addMessage("Все кеши очищены успешно", false);
		return page;
	}
	/**
	 * Переключить на другой режим редактирования
	 * 
	 * Параметры:
	 * itemId - ID базового айтема
	 * itemTypeId - ID типа базового айтема
	 * viewType - вид страницы
	 * 
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private AdminPage getView(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = null;
		if (MainAdminPageCreator.SUBITEMS_VIEW_TYPE.equals(in.viewType)) {
			page = pageCreator.createSubitemsPage(in.itemId, in.itemTypeId, in.page, in.searchQuery);
		} else if (MainAdminPageCreator.PARAMS_VIEW_TYPE.equals(in.viewType)) {
			page = pageCreator.createParamsPage(in.itemId, in.isVisual);
			page.addMessage("Включен режим редактирования параметров выбранного элемента", false);
		} else if (MainAdminPageCreator.MOUNT_TO_VIEW_TYPE.equals(in.viewType)) {
			page = pageCreator.createMountToPage(in.itemId, 0);
			page.addMessage("Включен режим создания ссылок из текущего элемента другие элементы", false);
		} else if (MainAdminPageCreator.MOVE_TO_VIEW_TYPE.equals(in.viewType)) {
			page = pageCreator.createMoveToPage(in.itemId, 0);
			page.addMessage("Включен режим перемещения текущего элемента в другой элемент", false);
		} else if (MainAdminPageCreator.ASSOCIATE_VIEW_TYPE.equals(in.viewType)) {
			page = pageCreator.createAssociatedPage(in.itemId, in.parentId, in.assocId);
		} else if (MainAdminPageCreator.PASTE_VIEW_TYPE.equals(in.viewType)) {
			page = pageCreator.createPastePage(in.session, in.parentId, in.itemTypeId);
		} else if (MainAdminPageCreator.USERS_VIEW_TYPE.equals(in.viewType)) {
			page = pageCreator.createUsersPage(in.searchQuery, in.itemId);
		}
		return page;
	}
	/**
	 * Выбор айтема для редактирования
	 * 
	 * Параметры:
	 * itemId - ID базового айтема
	 * itemTypeId - тип базового айтема
	 * 
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private AdminPage setItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createPageBase(MainAdminPageCreator.PARAMS_VIEW_TYPE, in.itemId, in.itemTypeId);
		page.addMessage("Выбран элемент для редактирования. После редактирования нажмите кнопку 'Сохранить'", false);
		return page;
	}
	/**
	 * Создать форму для нового айтемы
	 * 
	 * Параметры:
	 * itemTypeId - тип создаваемого айтема
	 * parentId - ID родительского айтема
	 * 
	 * @return
	 * @throws Exception 
	 */
	private AdminPage createItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createParamsPage(in.itemTypeId, in.parentId, in.isVisual);
		page.addMessage("Создан новый элемент. Заполните необходимые параметры и нажмите 'Сохранить'. ВНИМАНИЕ!!!" +
				" Возможность добавлять дополнительные параметры и создавать вложенные элементы появится только после нажатия" +
				" кнопки 'Сохранить'", false);
		return page;
	}
	/**
	 * Сохраняет айтем
	 * 
	 * Параметры:
	 * html форма айтема (SingleItemHttpPostFormDeprecated)
	 * 
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private AdminPage saveItem(UserInput in, HttpServletRequest req, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		MultipleHttpPostForm itemForm = new MultipleHttpPostForm(req);
		Item formItem = itemForm.getItemTree().getFirstChild().getItem();
		Item item;
		// Сохраняется новый айтем
		if (formItem.isNew()) {
			int userId;
			byte groupId;
			if (formItem.getContextParentId() == ItemTypeRegistry.getPrimaryRootId()) {
				userId = User.ANONYMOUS_ID;
				groupId = UserGroupRegistry.getDefaultGroup();
			} else {
				ItemBasics parent = AdminLoader.loadItem(formItem.getContextParentId(), getCurrentAdmin());
				userId = parent.getOwnerUserId();
				groupId = parent.getOwnerGroupId();
			}
			// Создание айтема
			item = Item.newItem(formItem.getItemType(), formItem.getContextParentId(), userId,
					groupId, Item.STATUS_NORMAL, false);
			Item.updateParamValuesKeepFiles(formItem, item);
			transaction.addCommandUnit(SaveItemDBUnit.get(item));
		}
		// Сохраняется существующий айтем
		else {
			item = AdminLoader.loadItem(formItem.getId(), getCurrentAdmin());
			Item.updateParamValuesKeepFiles(formItem, item);
			transaction.addCommandUnit(SaveItemDBUnit.get(item));
			if (itemForm.getItemTree().getFirstChild().hasChildren()) {
				for (ItemTreeNode node : itemForm.getItemTree().getFirstChild().getChildren()) {
					Item postedItem = node.getItem();
					Item originalItem = AdminLoader.loadItem(postedItem.getId(), getCurrentAdmin());
					Item.updateParamValuesKeepFiles(postedItem, originalItem);
					transaction.addCommandUnit(SaveItemDBUnit.get(originalItem));
				}
			}
		}
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = null;
		// Антоновские изменения
		//page = pageCreator.createPageBase(MainAdminPageCreator.PARAMS_VIEW_TYPE, item.getId(), item.getTypeId());
		boolean toParent = StringUtils.equalsIgnoreCase(itemForm.getSingleStringExtra("parent-url"), "yes");
		long id = (toParent) ? item.getContextParentId() : item.getId();
		if (id == ItemTypeRegistry.getPrimaryRootId()){
			return setItem(in, pageCreator);
		}
		int type = (toParent) ? AdminLoader.loadItem(id, getCurrentAdmin()).getTypeId() : item.getTypeId();
		page = pageCreator.createPageBase(MainAdminPageCreator.PARAMS_VIEW_TYPE, id, type);
		page.addMessage("Изменения успешно сохранены", false);
		return page;
	}
	/**
	 * Удаление айтема
	 * 
	 * Параметры:
	 * itemId - ID удаляемого айтема
	 * parentId - ID родительского айтема (сабайтем которого удаляется)
	 * itemTypeId - ID типа родительского айтема (сабайтем которого удаляется)
	 * 
	 * @return
	 * @throws Exception 
	 */
	private AdminPage deleteItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		transaction.addCommandUnit(ItemStatusDBUnit.delete(in.itemId));
		transaction.execute();
		// Очистить корзину
		transaction.addCommandUnit(new CleanAllDeletedItemsDBUnit(20, null));
		transaction.execute();
		AdminPage page = pageCreator.createSubitemsPage(in.parentId, in.itemTypeId, in.page, in.searchQuery);
		// Удалить айтем из индекса Lucene
		LuceneIndexMapper.commit();
		// Очистить кеш страниц
		PageController.clearCache();
		page.addMessage("Элемент успешно удален", false);
		return page;
	}
	/**
	 * Сделать элемент видимым или скрытым
	 *
	 * Параметры:
	 * itemId - ID айтема
	 * parentId - ID родительского айтема
	 * itemTypeId - ID типа родительского айтема
	 *
	 * @return
	 * @throws Exception
	 */
	private AdminPage toggleItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		transaction.addCommandUnit(ItemStatusDBUnit.toggle(in.itemId));
		transaction.execute();
		AdminPage page = pageCreator.createSubitemsPage(in.parentId, in.itemTypeId, in.page, in.searchQuery);
		// Очистить кеш страниц
		PageController.clearCache();
		page.addMessage("Видимость элемента успешно изменена", false);
		return page;
	}
	/**
	 * Устанавливает новый порядковый номер айтема
	 * 
	 * Параметры:
	 * itemId - ID айтема, который переставляется
	 * parentId - ID родительского айтема (порядок следования сабайтемов которого меняется)
	 * assocId - ID ассоциации
	 * newItemPosition - новый порядковый номер
	 *
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private AdminPage setNewItemIndex(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		Item item = ItemQuery.loadById(in.itemId);
		transaction.addCommandUnit(new SetNewItemWeightByPositionDBUnit(item, in.newItemPosition, in.assocId, in.parentId));
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createSubitemsPage(in.parentId, in.itemTypeId, in.page, in.searchQuery);
		page.addMessage("Порядок следования элементов изменен", false);
		return page;
	}
	/**
	 * Добавить множественный параметр
	 * 
	 * Параметры:
	 * параметр запроса itemId - ID айтема владельца параметра
	 * параметр запроса multipleParamId - ID параметра
	 * параметр запроса multipleParamValue - значение параметра
	 * 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private AdminPage addParameter(UserInput in, HttpServletRequest req, MainAdminPageCreator pageCreator) throws Exception {
		ArrayList<FileItem> uploadedFiles = new ArrayList<>();
		int multipleParamId = 0;
		String multipleParamValue = null;
		long itemId = -1;
		// Если обычный запрос (GET или POST)
		if (ServletFileUpload.isMultipartContent(req)) {
			DiskFileItemFactory filesFactory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(filesFactory);
			String encoding = Strings.SYSTEM_ENCODING;
			upload.setHeaderEncoding(encoding);
			List<FileItem> values = upload.parseRequest(req);
			for (FileItem fileItem : values) {
				if (fileItem.getFieldName().equalsIgnoreCase(MainAdminPageCreator.PARAM_ID_INPUT)) 
		    		multipleParamId = Integer.parseInt(fileItem.getString(encoding));
				else if (fileItem.isFormField() && fileItem.getFieldName().equalsIgnoreCase(MainAdminPageCreator.MULTIPLE_PARAM_VALUE_INPUT)) 
		    		multipleParamValue = fileItem.getString(encoding);
				else if (fileItem.isFormField() && fileItem.getFieldName().equalsIgnoreCase(MainAdminPageCreator.ITEM_ID_INPUT)) 
					itemId = Long.parseLong(fileItem.getString());
				else if (!fileItem.isFormField())
					uploadedFiles.add(fileItem);
			}
		}
		Item item = ItemQuery.loadById(itemId);
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		if (item.getItemType().getParameter(multipleParamId).getDataType().isFile()) {
			for (FileItem fileItem : uploadedFiles) {
				transaction.addCommandUnit(new SaveItemFileUnit(item, multipleParamId, fileItem));				
			}
		} else {
			item.setValueUI(multipleParamId, multipleParamValue);
		}
		transaction.addCommandUnit(SaveItemDBUnit.get(item));
		transaction.execute();
		// Обновить индекс Lucene
		LuceneIndexMapper.commit();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createParamsPage(in.itemId, in.isVisual);
		page.addMessage("Дополнительный параметр добавлен и отображается в колонке справа", false);
		return page;
	}
	/**
	 * Загрузка картинки (плагин tinyMCE)
	 * 
	 * Параметры (все параметры запроса):
	 * itemId - ID айтема владельца параметра
	 * multipleParamId - ID параметра картинки
	 * height - высота картинки (необязательный)
	 * width - ширина картинки (необязательный)
	 * multipleParamValue - подгружаемый файл или несколько файлов (FileItem)
	 * 
	 * @param req
	 * @param pageCreator
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private AdminPage uploadImage(UserInput in, HttpServletRequest req, MainAdminPageCreator pageCreator) throws Exception {
		ArrayList<FileItem> uploadedFiles = new ArrayList<FileItem>();
		int height = 0;
		int width = 0;
		String alt = "";
		// Если обычный запрос (GET или POST)
		if (ServletFileUpload.isMultipartContent(req)) {
			DiskFileItemFactory filesFactory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(filesFactory);
			String encoding = Strings.SYSTEM_ENCODING;
			upload.setHeaderEncoding(encoding);
			List<FileItem> values = upload.parseRequest(req);
			for (FileItem fileItem : values) {
				if (fileItem.isFormField() && fileItem.getFieldName().equalsIgnoreCase(MainAdminPageCreator.HEIGHT_INPUT))
					height = Strings.parseIntDefault(fileItem.getString(), 0);
				else if (fileItem.isFormField() && fileItem.getFieldName().equalsIgnoreCase(MainAdminPageCreator.WIDTH_INPUT)) 
					width = Strings.parseIntDefault(fileItem.getString(), 0);
				else if (fileItem.isFormField() && fileItem.getFieldName().equalsIgnoreCase(MainAdminPageCreator.ALT_INPUT)) 
					alt = fileItem.getString();
				else if (fileItem.getFieldName().equalsIgnoreCase(MainAdminPageCreator.MULTIPLE_PARAM_VALUE_INPUT))
					uploadedFiles.add(fileItem);
			}
		}
		Item item = ItemQuery.loadById(in.itemId);
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		for (FileItem fileItem : uploadedFiles) {
			item.setValue(in.paramId, fileItem);
		}
		transaction.addCommandUnit(new SaveItemFilesUnit(item));
		// Если нужно произвести изменение размеров изображений
		if (height > 0 || width > 0) {
			transaction.addCommandUnit(new ResizeImagesFactory.ResizeImages(item, "height:" + height + ";width:" + width));
			transaction.addCommandUnit(SaveItemDBUnit.get(item));
		} else {
			transaction.addCommandUnit(SaveItemDBUnit.get(item));
		}
		transaction.execute();
		String itemPath = AppContext.getFilesDirPath(item.isFileProtected()) + item.getRelativeFilesPath();
		return pageCreator.createImageUploadedPage(in.itemId, in.paramId, uploadedFiles, itemPath, alt);
	}
	/**
	 * Удалить множественный параметр
	 * 
	 * Параметры:
	 * itemId - ID айтема владельца параметра
	 * multipleParamId - ID параметра
	 * index - индекс параметра
	 * 
	 * @return
	 * @throws Exception 
	 * @throws SQLException 
	 */
	private AdminPage deleteParameter(UserInput in, MainAdminPageCreator pageCreator) throws SQLException, Exception {
		Item item = ItemQuery.loadById(in.itemId);
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		if (item.getItemType().getParameter(in.paramId).getDataType().isFile())
			transaction.addCommandUnit(new DeleteItemFileUnit(item, in.paramId, in.index));
		else
			item.removeMultipleParamValue(in.paramId, in.index);
		transaction.addCommandUnit(SaveItemDBUnit.get(item));
		transaction.execute();
		// Обновить индекс Lucene
		LuceneIndexMapper.commit();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createParamsPage(in.itemId, in.isVisual);
		page.addMessage("Дополниельный параметр удален", false);
		return page;
	}
	/**
	 * Установить текущий родительский айтем для айтемов, в которых может быть созадна ссылка на текущий айтем
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * itemTypeId - ID типа базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * 
	 * @return
	 * @throws Exception 
	 */
	private AdminPage setMountToParentItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createMountToPage(in.itemId, in.parentId);
		page.addMessage("Чтобы создать связь, нужно отметить элементы для создания связи с текущим нажать 'Добавить связи'." +
				" Чтобы удалить связи, нужно отмеить элементы, связь с которыми должна быть удалена, и нажать 'Удалить связи'", false);
		return page;		
	}
	/**
	 * Установить текущий родительский айтем для айтемов, ссылки на которые можно создавать в текущем айтеме
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * itemTypeId - ID типа базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * 
	 * @return
	 * @throws Exception 
	 */
	private AdminPage setToMountParentItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createAssociatedPage(in.itemId, in.parentId, in.assocId);
		page.addMessage("Чтобы создать связь, нужно отметить элементы для создания связи с текущим нажать 'Добавить связи'." +
				" Чтобы удалить связи, нужно отмеить элементы, связь с которыми должна быть удалена, и нажать 'Удалить связи'", false);
		return page;
	}
	/**
	 * Установить текущий родительский айтем для айтемов, ассоциации с которыми можно создавать в текущем айтеме
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * itemTypeId - ID типа базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * 
	 * @return
	 * @throws Exception 
	 */
	private AdminPage setAssociatedParentItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createAssociatedPage(in.itemId, in.parentId, in.assocId);
		page.addMessage("Чтобы создать связь, нужно отметить элементы для создания связи с текущим нажать 'Добавить связи'." +
				" Чтобы удалить связи, нужно отмеить элементы, связь с которыми должна быть удалена, и нажать 'Удалить связи'", false);
		return page;
	}
	/**
	 * Установить текущий родительский айтем для айтемов, в которые можно перемещать текущий айтем
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * itemTypeId - ID типа базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных перемещений
	 * 
	 * @return
	 * @throws Exception 
	 */
	private AdminPage setMoveToParentItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createMoveToPage(in.itemId, in.parentId);
		page.addMessage("Чтобы переместить элемент, нужно отметить новый родительский элемент для текущего и нажать 'Переместить'", false);
		return page;
	}
	/**
	 * Установить текущий родительский айтем для айтемов, которые можно перемещать текущем айтем
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * itemTypeId - ID типа базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных перемещений
	 * 
	 * @return
	 * @throws Exception 
	 */
	private AdminPage setToMoveParentItem(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		AdminPage page = pageCreator.createAssociatedPage(in.itemId, in.parentId, in.assocId);
		page.addMessage("Чтобы переместить элемент, нужно отметить элемент для перемещения и нажать 'Переместить'", false);
		return page;
	}
	/**
	 * Создать ссылки на текущий айтем в выбранных айтемах
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * mount - значения монтирования
	 * 
	 * @throws Exception 
	 */
	private AdminPage createMountedToReferences(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		Item item = ItemQuery.loadById(in.itemId);
		for (String itemInput : in.mount.keySet()) {
			String[] parts = MainAdminPageCreator.splitInputName(itemInput);
			transaction.addCommandUnit(new CreateAssocDBUnit(item, Long.parseLong(parts[2]), in.assocId, false));
		}
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createMountToPage(in.itemId, in.parentId);
		page.addMessage("Новые связи успешно созданы", false);
		return page;	
	}
	/**
	 * Создать ссылки на выбранные айтемы в текущем айтеме
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * mount - значения монтирования
	 * paramId - ID параметра, в котором хранятся ассоциированные айтемы
	 * 
	 * @throws Exception 
	 */
	private AdminPage createAssociations(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		Item parent = ItemQuery.loadById(in.itemId);
		for (String itemInput : in.mount.keySet()) {
			String[] parts = MainAdminPageCreator.splitInputName(itemInput);
			Item child = ItemQuery.loadById(Long.parseLong(parts[2]));
			transaction.addCommandUnit(new CreateAssocDBUnit(child, parent, in.assocId, false));
		}
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createAssociatedPage(in.itemId, in.parentId, in.assocId);
		page.addMessage("Новые ассоциации успешно созданы", false);
		return page;
	}
	/**
	 * Переместить текущий айтем в выбранный айтем
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * movingItem - ???
	 * 
	 * @throws Exception 
	 */
	private AdminPage moveTo(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		String[] parts = MainAdminPageCreator.splitInputName(in.movingItem);
		long newParentId = Long.parseLong(parts[2]);
		transaction.addCommandUnit(new MoveItemDBUnit(in.itemId, newParentId));
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createMoveToPage(in.itemId, in.parentId);
		page.addMessage("Элемент успешно перемещен", false);
		return page;
	}
	/**
	 * Переместить выбранный айтем в текущий айтем
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * movingItem - ???
	 * 
	 * @throws Exception 
	 */
	protected AdminPage move(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		String[] parts = MainAdminPageCreator.splitInputName(in.movingItem);
		long itemToMoveId = Long.parseLong(parts[2]);
		transaction.addCommandUnit(new MoveItemDBUnit(itemToMoveId, in.itemId));
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createAssociatedPage(in.itemId, in.parentId, ItemTypeRegistry.getPrimaryAssoc().getId());
		page.addMessage("Элемент успешно перемещен", false);
		return page;
	}
	/**
	 * Копировать айтем
	 * 
	 * Параметры:
	 * itemId - ID копируемого айтема
	 * parentId - ID айтема, в который вставляется копируемый
	 * itemTypeId - ID типа айтема, в который происходит вставка
	 * 
	 * @param in
	 * @param pageCreator
	 * @return
	 * @throws Exception
	 */
	protected AdminPage copy(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		@SuppressWarnings("unchecked")
		LinkedHashMap<Long, ItemAccessor> buffer = (LinkedHashMap<Long, ItemAccessor>) in.session.getAttribute(MainAdminPageCreator.PASTE_LIST);
		if (buffer == null) {
			buffer = new LinkedHashMap<>();
		}
		ItemAccessor item = AdminLoader.loadItemAccessor(in.itemId);
		buffer.put(item.getId(), item);
		in.session.setAttribute(MainAdminPageCreator.PASTE_LIST, buffer);
		return pageCreator.createPastePage(in.session, in.parentId, in.itemTypeId);
	}
	/**
	 * Произвести копирование айтема
	 * 
	 * Параметры:
	 * itemId - ID копируемого айтема
	 * parentId - ID айтема, в который вставляется копируемый
	 * itemTypeId - ID типа айтема, в который происходит вставка
	 * 
	 * @param in
	 * @param pageCreator
	 * @return
	 * @throws Exception
	 */
	protected AdminPage paste(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		try {
			transaction.addCommandUnit(new CopyItemDBUnit(in.itemId, in.parentId));
			transaction.execute();
			@SuppressWarnings("unchecked")
			LinkedHashMap<Long, ItemAccessor> buffer = (LinkedHashMap<Long, ItemAccessor>) in.session.getAttribute(MainAdminPageCreator.PASTE_LIST);
			if (buffer != null) {
				buffer.remove(in.itemId);
				in.session.setAttribute(MainAdminPageCreator.PASTE_LIST, buffer);
			}
		} catch (Exception e) {
			ServerLogger.error("Unable to copy item", e);
			AdminPage page = pageCreator.createSubitemsPage(in.parentId, in.itemTypeId, in.page, in.searchQuery);
			page.addMessage("Невозможно вставить скопированный элемент", true);
			return page;
		}
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createSubitemsPage(in.parentId, in.itemTypeId, in.page, in.searchQuery);
		page.addMessage("Элемент успешно копирован", false);
		return page;
	}
	/**
	 * Удалить из буфера обмена айтем
	 * @param in
	 * @param pageCreator
	 * @return
	 * @throws Exception
	 */
	private AdminPage deletePaste(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		@SuppressWarnings("unchecked")
		LinkedHashMap<Long, ItemAccessor> buffer = (LinkedHashMap<Long, ItemAccessor>) in.session.getAttribute(MainAdminPageCreator.PASTE_LIST);
		if (buffer != null) {
			buffer.remove(in.itemId);
			in.session.setAttribute(MainAdminPageCreator.PASTE_LIST, buffer);
		}
		return pageCreator.createPastePage(in.session, in.parentId, in.itemTypeId);
	}
	/**
	 * Удалить какую-либо ссылку
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * mount - значения монтирования
	 * viewType - страница
	 * 
	 * @throws Exception 
	 */
	private AdminPage deleteReference(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		Item item = AdminLoader.loadItem(in.itemId, getCurrentAdmin());
		transaction.addCommandUnit(new DeleteAssocDBUnit(item, in.parentId, in.assocId));
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = null;
		if (MainAdminPageCreator.MOUNT_TO_VIEW_TYPE.equals(in.viewType))
			page = pageCreator.createMountToPage(in.itemId, in.parentId);
		else if (MainAdminPageCreator.TO_MOUNT_VIEW_TYPE.equals(in.viewType))
			page = pageCreator.createAssociatedPage(in.itemId, in.parentId, in.assocId);
		page.addMessage("Связи успешно удалены", false);
		return page;
	}
	/**
	 * Удалить какую-либо ссылку
	 * 
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * parentId - ID родительского айтема для списка потенциальных ссылок
	 * mount - значения монтирования
	 * viewType - страница
	 * 
	 * @throws Exception 
	 */
	private AdminPage deleteAssociated(UserInput in, MainAdminPageCreator pageCreator) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		Item baseItem = AdminLoader.loadItem(in.itemId, getCurrentAdmin());
		for (String itemInput : in.mount.keySet()) {
			String[] parts = MainAdminPageCreator.splitInputName(itemInput);
			Item child = AdminLoader.loadItem(Long.parseLong(parts[2]), getCurrentAdmin());
			transaction.addCommandUnit(new DeleteAssocDBUnit(child, baseItem, in.assocId));
		}
		transaction.execute();
		// Очистить кеш страниц
		PageController.clearCache();
		AdminPage page = pageCreator.createAssociatedPage(in.itemId, in.parentId, in.assocId);
		page.addMessage("Ассоциации успешно удалены", false);
		return page;
	}

	/**
	 * Поменять пользователя-владельца или группу-владельца айтема
	 *
	 * Параметры:
	 * itemId - ID базового (выбранного) айтема
	 * paramId - ID группы или пользователя
	 *
	 * @param in
	 * @param pageCreator
	 * @param isUser
	 * @return
	 * @throws Exception
	 */
	private AdminPage setNewUserGroup(UserInput in, MainAdminPageCreator pageCreator, boolean isUser) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		Item baseItem = AdminLoader.loadItem(in.itemId, getCurrentAdmin());
		if (isUser)
			transaction.addCommandUnit(ChangeItemOwnerDBUnit.newGroup(baseItem, (byte) in.paramId));
		else
			transaction.addCommandUnit(ChangeItemOwnerDBUnit.newUser(baseItem, in.paramId, baseItem.getOwnerGroupId()));
		transaction.execute();
		PageController.clearCache();
		AdminPage page = pageCreator.createPageBase(MainAdminPageCreator.PARAMS_VIEW_TYPE, baseItem.getId(), baseItem.getTypeId());
		page.addMessage("Элементу назначена новая группа пользователей", false);
		return page;
	}
}
