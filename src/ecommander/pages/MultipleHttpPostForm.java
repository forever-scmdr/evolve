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
 * Форма на замену ItemHttpPostForm
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
 * Названия полей состоят из следующих составляющих:
 *
 * 1) ID айтема i (item). Форма существующего или нового айтема, задан ID айтема. Новые айтемы
 *    также имеют ID, но он отрицательный
 * 2) ID родительского айтема a (ancestor). Нужна только для новых айтемов
 * 3) ID айтемов-предков атйема на странице. Несколько h+ID (hierarchy), для построения дерева
 * 4) Тип айтема (начинается с t)
 * 5) Параметр айтема (начинается с p). Возможно при отсутствии части 6. (parameter)
 * 6) Переменная айтема (начинается с v). Возможно при отсутствии части 5. (variable)
 *
 * !!!  ПОЯСНЕНИЕ - теперь все айтемы, даже новые, имеют уникальный ID, поэтому можно использовать эти ID
 *      для уникальной идентификации формы (набора инпутов) айтема
 *
 *
 * Для того, чтобы отличить поля, относящиеся к айтему от других полей (которые потом становятся переменными страницы),
 * названия полей начинаются с префикса ~ii~_ (Item Input)
 * Название поля состоит из частей, разделенных подчеркиванием (_), например
 * ~ii~_i15_t8_p22 - существующий айтем с ID=15, тип айтема 8, параметр 22
 * ~ii~_i-3_a-1_h-1_t22_p35 - новый айтем ID=-3, родитель - тоже новый айтем с ID=-1, тип айтема 22, параметр 35
 * ~ii~_i15_t8_vukey - существующий айтем с ID=15, тип айтема 8, переменная айтема с названием ukey (уникальный ключ)
 *
 * Created by E on 17/5/2017.
 */
public class MultipleHttpPostForm implements Serializable {

	private final static char ITEM = 'i';
	private final static char ANCESTOR = 'a';
	private final static char HIERARCHY_PARENT = 'h';
	private final static char TYPE = 't';
	private final static char PARAM = 'p';
	private final static char VAR = 'v';
	private final static String ITEM_INPUT_PREFIX = "~ii~";
	private final static char SEPARATOR = '_';

	private static class InputDesc implements Serializable {
		private long parentId;
		private long itemId;
		private int itemType;
		private int paramId;
		private String varName;
		private ArrayList<Long> predecessors = new ArrayList<>();

		private String inputName;

		private InputDesc(String inputName) {
			this.inputName = inputName;
			String[] parts = StringUtils.split(inputName, SEPARATOR);
			for (String part : parts) {
				if (part.length() > 0) {
					char desc = part.charAt(0);
					String value = org.apache.commons.lang3.StringUtils.substring(part, 1);
					switch (desc) {
						case ITEM:
							itemId = Long.parseLong(value);
							break;
						case ANCESTOR:
							parentId = Long.parseLong(value);
							break;
						case HIERARCHY_PARENT:
							predecessors.add(Long.parseLong(value));
							break;
						case TYPE:
							itemType = Integer.parseInt(value);
							break;
						case PARAM:
							paramId = Integer.parseInt(value);
							break;
						case VAR:
							varName = value;
							break;
						default:
							break;
					}
				}
			}
		}

		private InputDesc(long itemId, long parentId, List<Long> predecessors, int itemType, int paramId, String varName) {
			this.parentId = parentId;
			this.itemId = itemId;
			this.itemType = itemType;
			this.paramId = paramId;
			this.varName = varName;
			this.predecessors = new ArrayList<>(predecessors);
			StringBuilder sb = new StringBuilder();
			sb.append(ITEM).append(itemId);
			if (parentId != Item.DEFAULT_ID)
				sb.append(SEPARATOR).append(ANCESTOR).append(parentId);
			for (Long predecessor : predecessors) {
				sb.append(SEPARATOR).append(HIERARCHY_PARENT).append(predecessor);
			}
			sb.append(SEPARATOR).append(TYPE).append(itemType);
			if (paramId > 0)
				sb.append(SEPARATOR).append(PARAM).append(paramId);
			if (StringUtils.isNotBlank(varName))
				sb.append(SEPARATOR).append(VAR).append(varName);
			inputName = sb.toString();
		}

		@Override
		public int hashCode() {
			return inputName.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return StringUtils.equals(inputName, ((InputDesc) obj).inputName);
		}
	}

	private int formIdGenerator = 1;
	private String cacheId;
	private HashMap<Long, HttpInputValues> inputs = new HashMap<>();
	private HttpInputValues extras = new HttpInputValues();

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
				if (StringUtils.startsWith(fileItem.getFieldName(), ITEM_INPUT_PREFIX)) {
					InputDesc input = new InputDesc(fileItem.getFieldName());
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
				if (StringUtils.startsWith(paramName, ITEM_INPUT_PREFIX)) {
					InputDesc input = new InputDesc(paramName);
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

	public HttpInputValues getItemInput(long itemId) {
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
		for (HttpInputValues input : inputs.values()) {
			InputDesc desc = (InputDesc) input.getInputKeys().iterator().next();
			ItemTreeNode parent = root;
			for (Long pred : desc.predecessors) {
				if (itemsToAdd.contains(pred)) {
					parent = insertItem(parent, inputs.get(pred));
					itemsToAdd.remove(pred);
				} else if (parent.find(pred) != null) {
					parent = parent.find(pred);
				}
			}
			if (itemsToAdd.contains(desc.itemId)) {
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
	private ItemTreeNode insertItem(ItemTreeNode parent, HttpInputValues input) {
		InputDesc desc = (InputDesc) input.getInputKeys().iterator().next();
		ItemType itemType = ItemTypeRegistry.getItemType(desc.itemType);
		Item item = Item.newItem(itemType, desc.parentId, User.ANONYMOUS_ID, User.NO_GROUP_ID, Item.STATUS_NORMAL, false);
		for (Object obj : input.getInputKeys()) {
			InputDesc inDesc = (InputDesc) obj;
			if (inDesc.paramId > 0) {
				item.setValue(inDesc.paramId, input.get(inDesc));
			} else if (StringUtils.isNotBlank(inDesc.varName)) {
				item.setExtra(inDesc.varName, input.get(inDesc));
			}
		}
		return parent.addChild(item);
	}

	private void addItemInput(InputDesc inputDesc, Object value) {
		HttpInputValues values = inputs.get(inputDesc.itemId);
		if (values == null) {
			values = new HttpInputValues();
			inputs.put(inputDesc.itemId, values);
		}
		values.add(inputDesc, value);
	}

}
