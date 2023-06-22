package ecommander.pages.elements;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.controllers.BasicServlet;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.MultipleParameter;
import ecommander.model.item.Parameter;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.SingleParameter;
import ecommander.users.User;

/**
 * Класс, который представляет форму для редактирования айтема
 * URL, на который нужен переход, задается в форме. Этот урл - один из вложенных в страницу (или айтем) урлов.
 * На этом уровне абстракции этот урл не нужен, он нужен только в XSL файле
 * @author EEEE
 *
 */
public class ItemHttpPostForm implements Serializable {
	private static final long serialVersionUID = 4449373285150234982L;

	public static final long NO_ID = 0;

	private static final Object NO_VALUE = null;

	private static final String FORM_HIDDEN_TYPE_ID = "-ti-";
	private static final String FORM_HIDDEN_ITEM_ID = "-ii-";
	private static final String FORM_HIDDEN_ITEM_PARENT_ID = "-ip-";
	private static final String FORM_HIDDEN_FORM_ID = "-fi-";
	public static final String FORM_ITEM_UNIQUE_KEY = "ukey";
	private static final String PARAM_PREFIX = "-param-";
	public static final int PARAM_PREFIX_LENGTH = PARAM_PREFIX.length();
	
	private int itemTypeId;
	private String itemCaption;
	private long itemId;
	private long itemParentId;
	private String formId; // часть для уникального ID формы, например название страницы, с которой пришла форма (для сохранения форм в сеансе)
	private ParameterValues parameterValues; //Значения хранятся или в виде String (обычные парамтеры) или в виде FileItem (файлы)
	private LinkedHashSet<Integer> paramIds;
	private String predIdPath; // Путь к айтему начиная от корня
	private ParameterValues extra; // дополнительные параметры запроса (НЕ параметры айтема)
	
	/**
	 * Для создания формы на базе вновь создаваемого айтема
	 * @param itemDesc
	 */
	public ItemHttpPostForm(ItemType itemDesc, long parentId, String formId, Collection<String> parametersToEdit) {
		itemId = NO_ID;
		itemTypeId = itemDesc.getTypeId();
		itemCaption = itemDesc.getCaption();
		itemParentId = parentId;
		this.formId = formId;
		paramIds = new LinkedHashSet<Integer>();
		predIdPath = "";
		parameterValues = new ParameterValues();
		for (String paramName : parametersToEdit) {
			ParameterDescription param = itemDesc.getParameter(paramName);
			if (param.isVirtual())
				continue;
			paramIds.add(param.getId());
			parameterValues.add(param.getId(), NO_VALUE);
		}
		if (itemDesc.isKeyUnique()) {
			extra = new ParameterValues();
			extra.add(FORM_ITEM_UNIQUE_KEY, "");
		}
	}
	/**
	 * Создание формы для нового айтема на базе типа айтема и ID предка
	 * @param itemDesc
	 * @param parentId
	 */
	public ItemHttpPostForm(ItemType itemDesc, long parentId, String formId) {
		this(itemDesc, parentId, formId, itemDesc.getParameterNames());
	}
	/**
	 * Создание формы на базе ранее созданного (существующего) айтема
	 * @param item
	 */
	public ItemHttpPostForm(Item item, String formId, Collection<String> parametersToEdit) {
		itemId = item.getRefId();
		itemTypeId = item.getTypeId();
		itemCaption = item.getKey();
		itemParentId = item.getDirectParentId();
		this.formId = formId;
		paramIds = new LinkedHashSet<Integer>();
		predIdPath = item.getPredecessorsAndSelfPath();
		parameterValues = new ParameterValues();
		for (String paramName : parametersToEdit) {
			Parameter param = item.getParameterByName(paramName);
			if (param.isVirtual())
				continue;
			if (!param.isMultiple()){
				paramIds.add(param.getParamId());
				parameterValues.add(param.getParamId(), ((SingleParameter)param).outputValue());
			} else {
				paramIds.add(param.getParamId());
				for (SingleParameter singleParam : ((MultipleParameter)param).getValues()) {
					parameterValues.add(param.getParamId(), singleParam.outputValue());
				}
			}
		}
		if (item.getItemType().isKeyUnique()) {
			putExtra(FORM_ITEM_UNIQUE_KEY, item.getKeyUnique() == null ? "" : item.getKeyUnique());
		}
		if (item.hasExtras()) {
			for (String key : item.getExtraKeys()) {
				putExtra(key, item.getExtra(key));
			}
		}
	}
	/**
	 * Создание формы на базе существующего айтема
	 * @param item
	 */
	public ItemHttpPostForm(Item item, String srcPage) {
		this(item, srcPage, item.getItemType().getParameterNames());
	}
	/**
	 * Создание формы на базе полученных от пользователя даных формы
	 * (пары название поля -> значение поля)
	 * @param request
	 * @param targetUrl
	 * @throws FileUploadException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	public ItemHttpPostForm(HttpServletRequest request, LinkPE targetUrl) throws FileUploadException,
			UnsupportedEncodingException {
		paramIds = new LinkedHashSet<Integer>();
		parameterValues = new ParameterValues();
		if (targetUrl == null) {
			String urlStr = BasicServlet.getUserUrl(request);
			targetUrl = LinkPE.parseLink(urlStr);
		}
		this.itemTypeId = Strings.parseIntDefault(targetUrl.getVariable(FORM_HIDDEN_TYPE_ID).output(), 0);
		this.itemId = Strings.parseLongDefault(targetUrl.getVariable(FORM_HIDDEN_ITEM_ID).output(), 0);
		this.itemParentId = Strings.parseLongDefault(targetUrl.getVariable(FORM_HIDDEN_ITEM_PARENT_ID).output(), 0);
		this.formId = targetUrl.getVariable(FORM_HIDDEN_FORM_ID).output();
		// Удалить из ссылки служебные переменные
		targetUrl.removeVariable(FORM_HIDDEN_TYPE_ID);
		targetUrl.removeVariable(FORM_HIDDEN_ITEM_ID);
		targetUrl.removeVariable(FORM_HIDDEN_ITEM_PARENT_ID);
		targetUrl.removeVariable(FORM_HIDDEN_FORM_ID);
		targetUrl.removeVariable(FORM_ITEM_UNIQUE_KEY);
		// Разбор параметров айтема
		ParameterValues extra = new ParameterValues();
		if (ServletFileUpload.isMultipartContent(request)) {
			DiskFileItemFactory filesFactory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(filesFactory);
			String encoding = Strings.SYSTEM_ENCODING;
			upload.setHeaderEncoding(encoding);
			List<FileItem> values = upload.parseRequest(request);
			for (FileItem fileItem : values) {
				// Параметр айтема
		    	if (fileItem.getFieldName().startsWith(PARAM_PREFIX)) {
					String[] inputParts = UrlParameterFormatConverter.splitInputName(fileItem.getFieldName());
					int paramId = Integer.parseInt(inputParts[0].substring(PARAM_PREFIX_LENGTH));
					if (fileItem.isFormField()) {
						parameterValues.add(paramId, fileItem.getString(encoding));
						paramIds.add(paramId);
					}
					else if (!StringUtils.isBlank(fileItem.getName())) {
						parameterValues.add(paramId, fileItem);
						paramIds.add(paramId);
					}
		    	}
				// Дополнительные параметры
		    	else {
		    		if (fileItem.isFormField())
		    			extra.add(fileItem.getFieldName(), fileItem.getString(encoding));
		    		else if (!StringUtils.isBlank(fileItem.getName()))
		    			extra.add(fileItem.getFieldName(), fileItem);
		    	}
			}
		} else {
			Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());
			params.remove(FORM_HIDDEN_TYPE_ID);
			params.remove(FORM_HIDDEN_ITEM_ID);
			params.remove(FORM_HIDDEN_ITEM_PARENT_ID);
			params.remove(FORM_HIDDEN_FORM_ID);
			params.remove(FORM_ITEM_UNIQUE_KEY);
			for (String paramName : params.keySet()) {
				String[] paramValues = params.get(paramName);
				// Параметр айтема
		    	if (paramName.startsWith(PARAM_PREFIX)) {
					String[] inputParts = UrlParameterFormatConverter.splitInputName(paramName);
					int paramId = Integer.parseInt(inputParts[0].substring(PARAM_PREFIX_LENGTH));
					for (String val : paramValues) {
						parameterValues.add(paramId, val);
					}
					paramIds.add(paramId);
		    	}
				// Дополнительные параметры
		    	else {
		    		if (targetUrl.getVariable(paramName) == null) {
		    			for (String val : paramValues) {
			    			targetUrl.addStaticVariable(paramName, val);							
						}
		    		}
		    		for (String value : paramValues) {
			    		extra.add(paramName, value);
					}
		    	}
			}
		}
		if (extra.isNotEmpty())
			this.extra = extra;
	}
	/**
	 * Вернуть все хидден поля для формы айтема (название поля - значение)
	 * @return
	 */
	public HashMap<String, String> getHiddenFields() {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put(FORM_HIDDEN_TYPE_ID, new Integer(itemTypeId).toString());
		result.put(FORM_HIDDEN_ITEM_ID, new Long(itemId).toString());
		result.put(FORM_HIDDEN_ITEM_PARENT_ID,  new Long(itemParentId).toString());
		result.put(FORM_HIDDEN_FORM_ID,  formId);
		return result;
	}
	/**
	 * Вернуть поле ввода для определенного одиночного параметра
	 * @param paramId
	 * @return
	 */
	public String getParamFieldName(int paramId) {
		return createParamInputName(itemTypeId, itemId, paramId);
	}
	/**
	 * Вернуть значение одиночного параметра
	 * @param paramId
	 * @return
	 */
	public String getValueStr(int paramId) {
		String value = (String)parameterValues.get(paramId);
		if (value == NO_VALUE) return "";
		return value;
	}
	/**
	 * Вернуть значение одиночного параметра
	 * @param paramName
	 * @return
	 */
	public String getValueStr(String paramName) {
		ParameterDescription param = ItemTypeRegistry.getItemType(itemTypeId).getParameter(paramName);
		if (param != null)
			return getValueStr(param.getId());
		return getValueStr(0);
	}
	/**
	 * Вернуть массив значений множественного параметра
	 * @param paramId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getValuesStr(int paramId) {
		Object value = parameterValues.get(paramId);
		if (value == NO_VALUE) return new ArrayList<String>();
		if (value instanceof List)
			return (List<String>) value;
		ArrayList<String> values = new ArrayList<String>();
		values.add((String) value);
		return values;
	}

	public int getItemTypeId() {
		return itemTypeId;
	}

	public String getItemTypeName() {
		return ItemTypeRegistry.getItemType(itemTypeId).getName();
	}
	
	public long getItemId() {
		return itemId;
	}

	public long getItemParentId() {
		return itemParentId;
	}
	
	public String getPredIdPath() {
		return predIdPath;
	}

	public String getItemCaption() {
		return itemCaption;
	}
	
	public static String getUniqueKeyInput() {
		return FORM_ITEM_UNIQUE_KEY;
	}
	/**
	 * Возвращает названия параметров, которые были установлены пользователем
	 * @return
	 */
	public Collection<Integer> getParameterIds() {
		return paramIds;
	}
	/**
	 * Возвращает строковое значение установленного пользователем параметра
	 * Желательно использовать в свзяке с getPostedParameterNames()
	 * @param paramName
	 * @return
	 */
	public Object getValue(int paramId) {
		return parameterValues.get(paramId);
	}
	/**
	 * Возвращает строковое значение установленного пользователем параметра
	 * Желательно использовать в свзяке с getPostedParameterNames()
	 * @param paramName
	 * @return
	 */
	public Object getValue(String paramName) {
		ParameterDescription paramDesc = ItemTypeRegistry.getItemType(itemTypeId).getParameter(paramName);
		if (paramDesc == null)
			return null;
		return getValue(paramDesc.getId());
	}
	/**
	 * Проверяет, заполнено ли значение определенного параметра
	 * @param paramName
	 * @return
	 */
	public boolean isParameterSet(String paramName) {
		Object value = getValue(paramName);
		if (value == null)
			return false;
		if (value instanceof List<?>)
			return ((List<?>)value).size() > 0;
		return !StringUtils.isBlank(value.toString());
	}
	/**
	 * Создает новый айтем на базе HTML формы и заполняет его парамтеры параметрами, переданными из формы
	 * Заполняются также и файлы в виде оберток FileItem
	 * Если через форму был передан ID айтема, то он тоже устанавливается
	 * @param user
	 * @param parentId
	 * @return
	 * @throws Exception 
	 */
	public Item createItem(User user, long parentId) throws Exception {
		return createItem(user.getUserId(), user.getGroupId(), parentId);
	}
	/**
	 * Создает новый айтем на базе HTML формы и заполняет его парамтеры параметрами, переданными из формы
	 * Заполняются также и файлы в виде оберток FileItem
	 * Если через форму был передан ID айтема, то он тоже устанавливается
	 * @param user
	 * @param parentId
	 * @return
	 * @throws Exception 
	 */
	public Item createItem(User user) throws Exception {
		return createItem(user.getUserId(), user.getGroupId(), itemParentId);
	}
	/**
	 * Создает новый айтем на базе HTML формы и заполняет его парамтеры параметрами, переданными из формы
	 * Заполняются также и файлы в виде оберток FileItem
	 * Если через форму был передан ID айтема, то он тоже устанавливается
	 * @param userId
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public Item createItem(long userId, int groupId) throws Exception {
		return createItem(userId, groupId, itemParentId);
	}
	/**
	 * Создает новый айтем на базе HTML формы и заполняет его парамтеры параметрами, переданными из формы
	 * Заполняются также и файлы в виде оберток FileItem
	 * Если через форму был передан ID айтема, то он тоже устанавливается
	 * @param userId
	 * @param groupId
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Item createItem(long userId, int groupId, long parentId) throws Exception {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemTypeId);
		Item result = Item.newItem(itemDesc, parentId, userId, groupId);
		for (Integer paramId : paramIds) {
			Object paramValue = parameterValues.get(paramId);
			if (itemDesc.getParameter(paramId).getDataType().isFile()) {
				if (paramValue instanceof List) {
					for (Object file : (List<Object>) paramValue) {
						result.setValue(paramId, file);
					}
				} else {
					result.setValue(paramId, paramValue);
				}
			} else {
				if (paramValue instanceof List) {
					for (String val : (List<String>) paramValue) {
						result.setValueUI(paramId, val);
					}
				} else {
					result.setValueUI(paramId, (String)paramValue);
				}
			}
		}
		// Установка ID айтема
		if (itemId != NO_ID)
			result.setId(itemId);
		if (extra != null) {
			String keyUnique = (String) extra.get(FORM_ITEM_UNIQUE_KEY);
			if (keyUnique != null) result.setKeyUnique(keyUnique);
		}
		return result;
	}
	/**
	 * Получить значение дополнительного поля (которое не относится к айтему)
	 * Дополнительные поля могут использоваться для удобства некоторых операций и не участвуют в основной логике работы 
	 * этого класса
	 * @param extraName
	 * @return
	 */
	public String getSingleExtra(Object extraName) {
		if (extra != null) {
			return extra.getString(extraName);
		}
		return "";
	}
	/**
	 * Получить значение дополнительного поля (которое не относится к айтему)
	 * Дополнительные поля могут использоваться для удобства некоторых операций и не участвуют в основной логике работы 
	 * этого класса
	 * @param extraName
	 * @return
	 */
	public List<Object> getMultipleExtra(Object extraName) {
		if (extra != null) {
			return extra.getList(extraName);
		}
		return new ArrayList<Object>();
	}
	/**
	 * Дополнительный параметр (поле)
	 * @param name
	 * @param value
	 */
	public void putExtra(String name, String value) {
		if (extra == null)
			extra = new ParameterValues();
		extra.add(name, value);
	}
	/**
	 * Вернуть названия дополнительных полей
	 * @return
	 */
	public Collection<Object> getExtras() {
		if (extra == null)
			return new ArrayList<Object>();
		return extra.getExtraNames();
	}
	/**
	 * Возвращает уникальный ID, который однозначно идентифицирует именно эту форму
	 * (форму конкретного айтема на конкретной странице с конкретным тэгом)
	 * @return
	 */
	public String getFormId() {
		return getItemId() + "_" + getItemTypeId() + "_" + formId;
	}
	/**
	 * Создать название инпута для определенного параметра определенного айтема
	 * @param itemName
	 * @param itemId
	 * @param paramName
	 * @return
	 */
	private static String createParamInputName(int itemTypeId, long itemId, int paramId) {
		return UrlParameterFormatConverter.createInputName(itemTypeId, itemId, PARAM_PREFIX + paramId);
	}
	/**
	 * Поменять параметры существующего айтема на параметры, переданные из формы
	 * Если надо обновить значения определенных полей (особенно чекбоксов), то надо передавать список соответствующих параметров
	 * @param itemToEdit
	 * @param resetParams - параметры, значения которых нужно обновить в любом случае, даже если они не присутствуют в форме
	 * @throws Exception
	 */
	public void editExistingItem(Item itemToEdit, String... resetParams) throws Exception {
		editExistingItem(this, itemToEdit, resetParams);
	}
	/**
	 * Заменяет старые параметры айтема на новые, полученные из формы
	 * Если надо обновить значения определенных полей (особенно чекбоксов), то надо передавать список соответствующих параметров
	 * @param itemForm
	 * @param itemToEdit
	 * @param resetParams - параметры, значения которых нужно обновить в любом случае, даже если они не присутствуют в форме
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void editExistingItem(ItemHttpPostForm itemForm, Item itemToEdit, String... resetParams) throws Exception {
		for (String param : resetParams) {
			itemToEdit.removeValue(param);
		}
		for (Integer paramId : itemForm.getParameterIds()) {
			ParameterDescription paramDesc = itemToEdit.getItemType().getParameter(paramId);
			if (paramDesc != null) {
				Object paramValue = itemForm.getValue(paramId);
				if (paramDesc.getDataType().isFile()) {
					if (paramValue instanceof FileItem) {
						itemToEdit.setValue(paramId, paramValue);
					} else if (paramValue instanceof List) {
						for (Object file : (List<Object>)paramValue) {
							if (file instanceof FileItem)
								itemToEdit.setValue(paramId, file);
						}
					}
				} else {
					if (paramValue instanceof List) {
						for (String val : ((List<String>)paramValue)) {
							if (!StringUtils.isBlank(val) && !itemToEdit.getParameter(paramId).containsValue(val))
								itemToEdit.setValueUI(paramId, val);
						}
					} else {
						String val = (String) paramValue;
						if (paramDesc.isMultiple()) {
							if (!StringUtils.isBlank(val) && !itemToEdit.getParameter(paramId).containsValue(val))
								itemToEdit.setValueUI(paramId, val);
						} else
							itemToEdit.setValueUI(paramId, val);
					}
				}
			}
		}
		if (itemForm.extra != null && itemForm.extra.containsValue(FORM_ITEM_UNIQUE_KEY))
			itemToEdit.setKeyUnique(itemForm.extra.getString(FORM_ITEM_UNIQUE_KEY));
	}
}
