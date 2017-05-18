package ecommander.pages;

import ecommander.model.Item;
import ecommander.model.ItemTreeNode;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
 * 3) Тип айтема (начинается с t)
 * 4) Параметр айтема (начинается с p). Возможно при отсутствии части 6. (parameter)
 * 5) Переменная айтема (начинается с v). Возможно при отсутствии части 5. (variable)
 *
 * !!!  ПОЯСНЕНИЕ - теперь все айтемы, даже новые, имеют уникальный ID, поэтому можно использовать эти ID
 *      для уникальной идентификации формы (набора инпутов) айтема
 *
 *
 * Название поля состоит из частей, разделенных подчеркиванием (_), например
 * i15_t8_p22 - существующий айтем с ID=15, тип айтема 8, параметр 22
 * i-3_a-1_t22_p35 - новый айтем ID=-3, родитель - тоже новый айтем с ID=-1, тип айтема 22, параметр 35
 * i15_t8_vukey - существующий айтем с ID=15, тип айтема 8, переменная айтема с названием ukey (уникальный ключ)
 *
 * Created by E on 17/5/2017.
 */
public class MultipleHttpPostForm implements Serializable{

	private final static char ITEM = 'i';
	private final static char ANCESTOR = 'a';
	private final static char TYPE = 't';
	private final static char PARAM = 'p';
	private final static char VAR = 'v';

	private static class InputDesc {
		private long parentId;
		private long itemId;
		private int itemType;
		private int paramId;
		private String varName;

		private String inputName;

		private InputDesc(String inputName) {
			this.inputName = inputName;
			String[] parts = StringUtils.split(inputName);
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
						case TYPE:
							itemType = Integer.parseInt(value);
							break;
						case PARAM:
							paramId = Integer.parseInt(value);
							break;
						case VAR:
							varName = value;
							break;
					}
				}
			}
		}

		private InputDesc(long itemId, long parentId, int itemType, int paramId, String varName) {
			this.parentId = parentId;
			this.itemId = itemId;
			this.itemType = itemType;
			this.paramId = paramId;
			this.varName = varName;
			StringBuilder sb = new StringBuilder();
			sb.append(ITEM).append(itemId);
			if (parentId != Item.DEFAULT_ID)
				sb.append(ANCESTOR).append(parentId);
			sb.append(TYPE).append(itemType);
			if (paramId > 0)
				sb.append(PARAM).append(paramId);
			if (StringUtils.isNotBlank(varName))
				sb.append(VAR).append(varName);
			inputName = sb.toString();
		}
	}

	private int formIdGenerator = 1;
	private String cacheId;
	private HashMap<Long, HttpInputValues> inputs = new HashMap<>();

	private transient ItemTreeNode items;

	MultipleHttpPostForm() { }

	public MultipleHttpPostForm(String cacheId, ItemTreeNode items) {
		this.cacheId = cacheId;
		this.items = items;
		for (Long itemId : items.getAllIds()) {
			Item item = items.find(itemId).getItem();
			InputDesc input = new InputDesc(item.getId(), item.getContextParentId(), item.getTypeId(), )
		}
	}

}
