package ecommander.pages;

import ecommander.fwk.Strings;
import ecommander.model.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Форма на замену SingleItemHttpPostFormDeprecated
 * Форма может представлять более одного айтема.
 * Айтемы могут быть выстроены в иерархическую стркутуру
 * Айтемы в структуре могут быть как новыми так и существующими (ранее созданными)
 * Айтем может содержать как параметры так и дополнительные поля (ItemVariables)
 *
 * Структура формы при приходе ее из запроса (от браузера) определяется только названиями полей формы, без
 * использования дополнительных служебных hidden полей.
 *
 * Названия полей определяются изначальным созданием формы (например, в файле pages.xml)
 *
 *
 * Created by E on 17/5/2017.
 */
public class MultipleHttpPostForm implements Serializable {

	private static final long serialVersionUID = 2L;

	private static final String FORM_ITEM_UNIQUE_KEY = "ukey";

	private HashMap<Long, InputValues> inputs = new HashMap<>();
	private InputValues extras = new InputValues();

	private transient ItemTreeNode treeRoot = null;

	MultipleHttpPostForm() { }

	public MultipleHttpPostForm(HttpServletRequest request) throws FileUploadException, UnsupportedEncodingException {
		if (ServletFileUpload.isMultipartContent(request)) {
			DiskFileItemFactory filesFactory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(filesFactory);
			String encoding = Strings.SYSTEM_ENCODING;
			upload.setHeaderEncoding(encoding);
			List<FileItem> values = upload.parseRequest(request);
			for (FileItem fileItem : values) {
				// Параметр айтема
				if (StringUtils.startsWith(fileItem.getFieldName(), ItemInputName.ITEM_INPUT_PREFIX)) {
					ItemInputName input = new ItemInputName(fileItem.getFieldName());
					if (fileItem.isFormField()) {
						addItemInput(input, fileItem.getString(encoding));
					}
					else if (!StringUtils.isBlank(fileItem.getName())) {
						addItemInput(input, fileItem);
					}
				}
				// Дополнительные параметры
				else {
					if (fileItem.isFormField())
						extras.add(fileItem.getFieldName(), fileItem.getString(encoding));
					else if (!StringUtils.isBlank(fileItem.getName()))
						extras.add(fileItem.getFieldName(), fileItem);
				}
			}
		} else {
			Map<String, String[]> params = request.getParameterMap();
			for (String paramName : params.keySet()) {
				String[] paramValues = params.get(paramName);
				// Параметр айтема
				if (StringUtils.startsWith(paramName, ItemInputName.ITEM_INPUT_PREFIX)) {
					ItemInputName input = new ItemInputName(paramName);
					for (String val : paramValues) {
						addItemInput(input, val);
					}
				}
				// Дополнительные параметры
				else {
					for (String value : paramValues) {
						extras.add(paramName, value);
					}
				}
			}
		}
	}

	public boolean hasExtras() {
		return extras.isNotEmpty();
	}

	public String getSingleStringExtra(String key) {
		return (String) extras.get(key);
	}

	InputValues getItemInput(long itemId) {
		return inputs.get(itemId);
	}

	/**
	 * Получить значения, переданные через запрос для определенного айтема в удобочитаемом виде
	 * (с возможностью получить значение по названию параметра)
	 * @param itemId
	 * @return
	 */
	public ItemInputValues getItemValues(long itemId) {
		return new ItemInputValues(inputs.get(itemId));
	}

	/**
	 * Создает дерево из айтемов, в которых содержатся значения инпутов.
	 * Также в них установлены их ID и ID родителей.
	 * Возвращается корень, который сам не содержит айтем (Pure Root). Он содержит только потомков.
	 * @return
	 */
	public ItemTreeNode getItemTree() throws Exception {
		if (treeRoot == null) {
			HashSet<Long> itemsToAdd = new HashSet<>(inputs.keySet());
			treeRoot = ItemTreeNode.createPureRoot();
			for (InputValues input : inputs.values()) {
				ItemInputName desc = (ItemInputName) input.getKeys().iterator().next();
				ItemTreeNode parent = treeRoot;
				for (Long pred : desc.getPredecessors()) {
					if (itemsToAdd.contains(pred)) {
						parent = insertItem(parent, inputs.get(pred));
						itemsToAdd.remove(pred);
					} else if (parent.find(pred) != null) {
						parent = parent.find(pred);
					}
				}
				if (itemsToAdd.contains(desc.getItemId())) {
					insertItem(parent, input);
					itemsToAdd.remove(desc.getItemId());
				}
			}
		}
		return treeRoot;
	}

	/**
	 * Создать айтем и добавить его в дерево в указанное место (к указанному родителю)
	 * @param parent
	 * @param input
	 * @return
	 */
	private ItemTreeNode insertItem(ItemTreeNode parent, InputValues input) throws Exception {
		ItemInputName desc = (ItemInputName) input.getKeys().iterator().next();
		ItemType itemType = ItemTypeRegistry.getItemType(desc.getItemType());
		Item item = Item.newFormItem(itemType, desc.getItemId(), desc.getParentId());
		for (Object key : input.getKeys()) {
			ItemInputName inDesc = (ItemInputName) key;
			if (inDesc.isParameter()) {
				Object paramValue = input.get(inDesc);
				if (itemType.getParameter(inDesc.getParamId()).getDataType().isFile()) {
					if (paramValue instanceof List) {
						for (Object file : (List<Object>) paramValue) {
							item.setValue(inDesc.getParamId(), file);
						}
					} else {
						item.setValue(inDesc.getParamId(), paramValue);
					}
				} else {
					if (paramValue instanceof List) {
						for (String val : (List<String>) paramValue) {
							item.setValueUI(inDesc.getParamId(), val);
						}
					} else {
						item.setValueUI(inDesc.getParamId(), (String)paramValue);
					}
				}
			} else if (inDesc.isVariable()) {
				if (StringUtils.equalsIgnoreCase(inDesc.getVarName(), FORM_ITEM_UNIQUE_KEY))
					item.setKeyUnique((String) input.get(inDesc));
				else
					item.setExtra(inDesc.getVarName(), input.get(inDesc));
			}
		}
		return parent.addChild(item);
	}

	private void addItemInput(ItemInputName inputDesc, Object value) {
		InputValues values = inputs.get(inputDesc.getItemId());
		if (values == null) {
			values = new InputValues();
			inputs.put(inputDesc.getItemId(), values);
		}
		values.add(inputDesc, value);
	}

}
