package ecommander.model;

import java.util.Collection;
import java.util.HashMap;

/**
 * Класс, который может содержать в себе айтемы
 *
 * @author EEEE
 */
public abstract class ItemTypeContainer {

	public static class SubitemDesc {
		private final String assocName;
		private final String itemName;
		private final boolean isSingle;
		private final boolean isVirtual;
		private final boolean isOwn; // Является ли базовым владельцем сабайтема (не путем наследования)

		public SubitemDesc(String assocName, String itemName, boolean isSingle, boolean isVirtual, boolean isOwn) {
			this.assocName = assocName;
			this.itemName = itemName;
			this.isSingle = isSingle;
			this.isVirtual = isVirtual;
			this.isOwn = isOwn;
		}
	}

	private HashMap<String, SubitemDesc> subitemDescriptions = null;

	public ItemTypeContainer() {
		subitemDescriptions = new HashMap<String, SubitemDesc>();
	}

	private static String createMapKey(String assocName, String subitemName) {
		return assocName + "_" + subitemName;
	}

	/**
	 * Добавляет айтем как сабайтем.
	 * При этом, айтем, в который добавляется сабайтем, будет являться изначальным владельцем сабайтема (не
	 * унаследованным)
	 *
	 * @param subitemName
	 * @param assocName
	 * @param single
	 * @param virtual
	 */
	public void addOwnSubitem(String assocName, String subitemName, boolean single, boolean virtual) {
		subitemDescriptions.put(createMapKey(assocName, subitemName), new SubitemDesc(assocName, subitemName, single, virtual, true));
	}

	/**
	 * Возвращает все названия сабайтемов, возможных в этом контейнере
	 *
	 * @return
	 */
	public Collection<SubitemDesc> getAllSubitems() {
		return subitemDescriptions.values();
	}

	/**
	 * Проверяет, является ли сабайтем множественным
	 *
	 * @return
	 */
	public boolean isSubitemMultiple(String assocName, String subitemName) {
		return !subitemDescriptions.get(createMapKey(assocName, subitemName)).isSingle;
	}

	/**
	 * Проверяет, является ли сабайтем виртуальным
	 *
	 * @param subitemName
	 * @return
	 */
	public boolean isSubitemVirtual(String assocName, String subitemName) {
		return subitemDescriptions.get(createMapKey(assocName, subitemName)).isVirtual;
	}

	/**
	 * Проверяет, не унаследован ли этот сабайтем (является собственностью данного айтема)
	 *
	 * @param subitemName
	 * @return
	 */
	public boolean isSubitemOwn(String subitemName) {
		return subitemDescriptions.get(subitemName).isOwn;
	}

	/**
	 * Добавляет все сабайтемы из container в текущий контейнер
	 * При этом все добавляемые сабайтемы помечаются как унаследованные, а не прямые
	 *
	 * @param container
	 */
	public void addAllSubitems(ItemTypeContainer container) {
		for (SubitemDesc sub : container.subitemDescriptions.values()) {
			subitemDescriptions.put(createMapKey(sub.assocName, sub.itemName),
					new SubitemDesc(sub.assocName, sub.itemName, sub.isSingle, sub.isVirtual, false));
		}
	}

	/**
	 * Проверяет, является ли сабайтем одиночным
	 *
	 * @param subitemName
	 * @return
	 */
	public boolean isSubitemSingle(String assocName, String subitemName) {
		return !isSubitemMultiple(assocName, subitemName);
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
