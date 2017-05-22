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

	private int formIdGenerator = 1;
	private String cacheId;
	private HashMap<Long, GeneralValues> inputs = new HashMap<>();
	private GeneralValues extras = new GeneralValues();

	private transient ItemTreeNode items;

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

	public GeneralValues getItemInput(long itemId) {
		return inputs.get(itemId);
	}

	/**
	 * Создает дерево из айтемов, в которых содержатся значения инпутов.
	 * Также в них установлены их ID и ID родителей.
	 * Возвращается корень, который сам не содержит айтем (Pure Root). Он содержит только потомков.
	 * @return
	 */
	public ItemTreeNode getItemTree() {
		HashSet<Long> itemsToAdd = new HashSet<>(inputs.keySet());
		ItemTreeNode root = ItemTreeNode.createPureRoot();
		for (GeneralValues input : inputs.values()) {
			ItemInputName desc = (ItemInputName) input.getKeys().iterator().next();
			ItemTreeNode parent = root;
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
			}
		}
		return root;
	}

	/**
	 * Создать айтем и добавить его в дерево в указанное место (к указанному родителю)
	 * @param parent
	 * @param input
	 * @return
	 */
	private ItemTreeNode insertItem(ItemTreeNode parent, GeneralValues input) {
		ItemInputName desc = (ItemInputName) input.getKeys().iterator().next();
		ItemType itemType = ItemTypeRegistry.getItemType(desc.getItemType());
		Item item = Item.newItem(itemType, desc.getParentId(), User.ANONYMOUS_ID, User.NO_GROUP_ID, Item.STATUS_NORMAL, false);
		item.setId(desc.getItemId());
		for (Object obj : input.getKeys()) {
			ItemInputName inDesc = (ItemInputName) obj;
			if (inDesc.isParameter()) {
				item.setValue(inDesc.getParamId(), input.get(inDesc));
			} else if (inDesc.isVariable()) {
				item.setExtra(inDesc.getVarName(), input.get(inDesc));
			}
		}
		return parent.addChild(item);
	}

	private void addItemInput(ItemInputName inputDesc, Object value) {
		GeneralValues values = inputs.get(inputDesc.getItemId());
		if (values == null) {
			values = new GeneralValues();
			inputs.put(inputDesc.getItemId(), values);
		}
		values.add(inputDesc, value);
	}

}
