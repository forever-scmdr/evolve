package ecommander.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;

/**
 * Класс, который может содержать в себе айтемы
 *
 * @author EEEE
 */
public abstract class ItemTypeContainer {

	public static class ChildDesc {
		public final String assocName;
		public final String itemName;
		public final boolean isSingle;
		public final boolean isVirtual;
		public final boolean isOwn; // Является ли базовым владельцем сабайтема (не путем наследования)
		public final boolean isInline;
		public final boolean isInlineTextIndex; // нужно ли присоединять параметры полнотекстового индекса вложенного айтема к родительскому

		ChildDesc(String assocName, String itemName, boolean isSingle, boolean isVirtual, boolean isOwn, boolean isInline, boolean isInlineTextIndex) {
			this.assocName = assocName;
			this.itemName = itemName;
			this.isSingle = isSingle;
			this.isVirtual = isVirtual;
			this.isOwn = isOwn;
			this.isInline = isInline;
			this.isInlineTextIndex = isInlineTextIndex;
		}
	}

	private HashMap<String, ChildDesc> childDescriptions = null;

	public ItemTypeContainer() {
		childDescriptions = new HashMap<>();
	}

	private static String createMapKey(String assocName, String childName) {
		return assocName + "_" + childName;
	}

	/**
	 * Добавляет айтем как сабайтем.
	 * При этом, айтем, в который добавляется сабайтем, будет являться изначальным владельцем сабайтема (не
	 * унаследованным)
	 *
	 * @param childName
	 * @param assocName
	 * @param single
	 * @param virtual
	 */
	void addOwnChild(String assocName, String childName, boolean single, boolean virtual, boolean isInline, boolean isInlineTextIndex) {
		if (StringUtils.isBlank(assocName))
			assocName = AssocRegistry.PRIMARY_NAME;
		childDescriptions.put(createMapKey(assocName, childName),
				new ChildDesc(assocName, childName, single, virtual, true, isInline, isInlineTextIndex));
	}

	/**
	 * Возвращает все названия сабайтемов, возможных в этом контейнере
	 *
	 * @return
	 */
	public Collection<ChildDesc> getAllChildren() {
		return childDescriptions.values();
	}

	/**
	 * Проверяет, является ли сабайтем множественным
	 *
	 * @return
	 */
	public boolean isChildMultiple(String assocName, String childName) {
		return !childDescriptions.get(createMapKey(assocName, childName)).isSingle;
	}

	/**
	 * Проверяет, является ли сабайтем виртуальным
	 *
	 * @param childName
	 * @return
	 */
	public boolean isChildVirtual(String assocName, String childName) {
		return childDescriptions.get(createMapKey(assocName, childName)).isVirtual;
	}

	/**
	 * Проверить, содержит ли айтем указанный вложенный айтем
	 * @param childName
	 * @return
	 */
	public boolean hasChild(String assocName, String childName) {
		return childDescriptions.containsKey(createMapKey(assocName, childName));
	}
	/**
	 * Добавляет все сабайтемы из container в текущий контейнер
	 * При этом все добавляемые сабайтемы помечаются как унаследованные, а не прямые
	 *
	 * @param container
	 */
	void addAllChildren(ItemTypeContainer container) {
		for (ChildDesc sub : container.childDescriptions.values()) {
			childDescriptions.put(createMapKey(sub.assocName, sub.itemName),
					new ChildDesc(sub.assocName, sub.itemName, sub.isSingle, sub.isVirtual, false, sub.isInline, sub.isInlineTextIndex));
		}
	}

	/**
	 * Проверяет, является ли сабайтем одиночным
	 *
	 * @param subitemName
	 * @return
	 */
	public boolean isChildSingle(String assocName, String subitemName) {
		return !isChildMultiple(assocName, subitemName);
	}

	/**
	 * Есть ли среди потомков инлайновые
	 * @return
	 */
	public boolean hasInlineChildren() {
		for (ChildDesc desc : childDescriptions.values()) {
			if (desc.isInline)
				return true;
		}
		return false;
	}

	/**
	 * Есть ли среди потомков такие, чьи текста нужно добавлять к полнотекстовому индексу родителя
	 * @return
	 */
	public boolean hasInlineTextIndexChildren() {
		for (ChildDesc desc : childDescriptions.values()) {
			if (desc.isInlineTextIndex)
				return true;
		}
		return false;
	}
	/**
	 * Вернуть название контейнера
	 *
	 * @return
	 */
	public abstract String getName();

	@Override
	public boolean equals(Object obj) {
		return getName().equals(obj);
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

}
