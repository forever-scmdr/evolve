package ecommander.pages;

import ecommander.model.Item;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Создает имя (name) для инпута айтема в специальном формате, в котором кодируются атрибуты айтема.
 *
 * Названия полей состоят из следующих составляющих:
 *
 * 1) ID айтема i (item). Форма существующего или нового айтема, задан ID айтема. Новые айтемы
 *    также имеют ID, но он отрицательный
 * 2) ID родительского айтема a (ancestor). Нужна только для новых айтемов
 * 3) ID айтемов-предков айтема на странице. Несколько h+ID (hierarchy), для построения дерева
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
 * Created by E on 22/5/2017.
 */
public class ItemInputName implements Serializable {

	private static final long serialVersionUID = 200L;

	private final static char ITEM = 'i';
	private final static char ANCESTOR = 'a';
	private final static char HIERARCHY_PARENT = 'h';
	private final static char TYPE = 't';
	private final static char PARAM = 'p';
	private final static char VAR = 'v';
	private final static char SEPARATOR = '_';

	final static String ITEM_INPUT_PREFIX = "~ii~";

	private final static int PREFIX_LENGTH = ITEM_INPUT_PREFIX.length();

	private long parentId;
	private long itemId;
	private int itemType;
	private int paramId;
	private String varName;
	private ArrayList<Long> predecessors = new ArrayList<>();

	private String inputName;

	ItemInputName(String inputName) {
		if (!isItemInput(inputName))
			throw new IllegalArgumentException("Item URL in illegal format");
		inputName = inputName.substring(PREFIX_LENGTH);
		this.inputName = inputName;
		String[] parts = StringUtils.split(inputName, SEPARATOR);
		for (String part : parts) {
			if (part.length() > 0) {
				char desc = part.charAt(0);
				String value = StringUtils.substring(part, 1);
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

	ItemInputName(long itemId, long parentId, int itemType, int paramId, String varName, Long...predecessors) {
		if (StringUtils.contains(varName, SEPARATOR))
			throw new IllegalArgumentException("Item variable name must not contain _ character: " + varName);
		this.parentId = parentId;
		this.itemId = itemId;
		this.itemType = itemType;
		this.paramId = paramId;
		this.varName = varName;
		StringBuilder sb = new StringBuilder(ITEM_INPUT_PREFIX);
		sb.append(ITEM).append(itemId);
		if (parentId != Item.DEFAULT_ID)
			sb.append(SEPARATOR).append(ANCESTOR).append(parentId);
		for (Long predecessor : predecessors) {
			this.predecessors.add(predecessor);
			sb.append(SEPARATOR).append(HIERARCHY_PARENT).append(predecessor);
		}
		sb.append(SEPARATOR).append(TYPE).append(itemType);
		if (paramId > 0)
			sb.append(SEPARATOR).append(PARAM).append(paramId);
		if (StringUtils.isNotBlank(varName))
			sb.append(SEPARATOR).append(VAR).append(varName);
		inputName = sb.toString();
	}

	static boolean isItemInput(String inputName) {
		return StringUtils.startsWith(inputName, ITEM_INPUT_PREFIX);
	}

	public long getParentId() {
		return parentId;
	}

	public long getItemId() {
		return itemId;
	}

	public int getItemType() {
		return itemType;
	}

	public int getParamId() {
		return paramId;
	}

	public String getVarName() {
		return varName;
	}

	public ArrayList<Long> getPredecessors() {
		return predecessors;
	}

	public String getInputName() {
		return inputName;
	}

	public boolean isParameter() {
		return paramId > 0;
	}

	public boolean isVariable() {
		return StringUtils.isNotBlank(varName);
	}

	@Override
	public int hashCode() {
		return inputName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return StringUtils.equals(inputName, ((ItemInputName) obj).inputName);
	}

	@Override
	public String toString() {
		return inputName;
	}
}
