package ecommander.model.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

/**
 * Класс, который может содержать в себе айтемы
 * @author EEEE
 *
 */
public abstract class ItemTypeContainer {

	private static class SubitemDesc {
		private final boolean isSingle;
		private final boolean isVirtual;
		private final boolean isPersonal; // является ли айтем персональным
		private final String ownerGroup; // группа пользователей, которые могут владеть этим айтемом
		private final boolean isOwn; // айтем является первоначальным владельцем сабайтема (а не получил сабайтем путем наследования)
		
		private SubitemDesc(boolean isSingle, boolean isVirtual, boolean isOwn, boolean isPersonal, String ownerGroup) {
			this.isSingle = isSingle;
			this.isVirtual = isVirtual;
			this.isOwn = isOwn;
			this.isPersonal = isPersonal;
			if (!StringUtils.isBlank(ownerGroup))
				this.ownerGroup = ownerGroup;
			else 
				this.ownerGroup = null;
		}
	}
	
	private HashMap<String, SubitemDesc> subitemDescriptions = null; // Описания сабайтемов (первые 2 бита - множественность, вторые - виртуальность)
	
	public ItemTypeContainer() {
		subitemDescriptions = new HashMap<String, SubitemDesc>();
	}
	
	/**
	 * Добавляет айтем как сабайтем.
	 * При этом, айтем, в который добавляется сабайтем, будет являться изначальным владельцем сабайтема (не унаследованным)
	 * @param map
	 */
	public void addOwnSubitem(String subitemName, boolean single, boolean virtual, boolean isPersonal, String ownerGroup) {
		subitemDescriptions.put(subitemName, new SubitemDesc(single, virtual, true, isPersonal, ownerGroup));
	}
	/**
	 * Возвращает все названия сабайтемов, возможных в этом контейнере для определенной группы пользователей
	 * @param userGroup
	 * @return
	 */
	public Collection<String> getAllowedSubitemNames(String userGroup) {
		HashSet<String> subitems = new HashSet<String>();
		for (String subitem : subitemDescriptions.keySet()) {
			SubitemDesc siDesc = subitemDescriptions.get(subitem);
			if (siDesc.ownerGroup == null || siDesc.ownerGroup.equals(userGroup))
				subitems.add(subitem);
		}
		return subitems;
	}
	/**
	 * Возвращает все названия сабайтемов, возможных в этом контейнере
	 * @return
	 */
	public Collection<String> getAllSubitemNames() {
		return subitemDescriptions.keySet();
	}
	/**
	 * Возможны ли сабайтемы в данном контейнере
	 * @return
	 */
	public boolean hasSubitems() {
		return subitemDescriptions.size() > 0;
	}
	/**
	 * Есть ли сабайтем с таким именем
	 * @param name
	 * @return
	 */
	public boolean hasSubitem(String name) {
		return subitemDescriptions.containsKey(name);
	}
	/**
	 * Проверяет, является ли сабайтем множественным
	 * @return
	 */
	public boolean isSubitemMultiple(String subitemName) {
		return !subitemDescriptions.get(subitemName).isSingle;
	}
	/**
	 * Проверяет, является ли сабайтем виртуальным
	 * @param subitemName
	 * @return
	 */
	public boolean isSubitemVirtual(String subitemName) {
		return subitemDescriptions.get(subitemName).isVirtual;
	}
	/**
	 * Проверяет, не унаследован ли этот сабайтем (является собственностью данного айтема)
	 * @param subitemName
	 * @return
	 */
	public boolean isSubitemOwn(String subitemName) {
		return subitemDescriptions.get(subitemName).isOwn;
	}
	/**
	 * Является ли сабайтем персональным (каждый пользователь имеет свои личные недоступные для других копии)
	 * @param subitemName
	 * @return
	 */
	boolean isSubitemPersonal(String subitemName) {
		return subitemDescriptions.get(subitemName).isPersonal;
	}
	/**
	 * Вернуть группу пользователей, которые могут владеть этим айтемом
	 * @param subitemName
	 * @return
	 */
	public String getSubitemOwnerGroup(String subitemName) {
		return subitemDescriptions.get(subitemName).ownerGroup;
	}
	/**
	 * Должен ли этот сабайтем принадлжать другой группе пользователей (не той, которой принадлежит родительский айтем)
	 * @param subitemName
	 * @return
	 */
	public final boolean hasSubitemSpecialOwner(String subitemName) {
		return subitemDescriptions.get(subitemName).ownerGroup != null;
	}
	/**
	 * Добавляет все сабайтемы из container в текущий контейнер
	 * При этом все добавляемые сабайтемы помечаются как унаследованные, а не прямые
	 * @param container
	 */
	public void addAllSubitems(ItemTypeContainer container) {
		for (String name : container.subitemDescriptions.keySet()) {
			SubitemDesc desc = container.subitemDescriptions.get(name);
			if (desc != null)
				subitemDescriptions.put(name, new SubitemDesc(desc.isSingle, desc.isVirtual, false, desc.isPersonal, desc.ownerGroup));
		}
	}
	/**
	 * Проверяет, является ли сабайтем одиночным
	 * @param subitemName
	 * @return
	 */
	public boolean isSubitemSingle(String subitemName) {
		return !isSubitemMultiple(subitemName);
	}
	/**
	 * Найти все сабайтемы, которые могут принадлежать определенной заданной группе пользователей
	 * и являются общими или персональными
	 * @param itemGroup - название группы
	 * @param personal - персональные сабайтемы (если нужны общие - false). Если нужны все - null
	 * @return
	 */
	Collection<String> getUserGroupAllowedSubitems(String userGroup, Boolean personal) {
		ArrayList<String> groupSubitems = new ArrayList<String>();
		for (String subitemName : subitemDescriptions.keySet()) {
			SubitemDesc si = subitemDescriptions.get(subitemName);
			if ((si.ownerGroup == null || userGroup.equals(si.ownerGroup)) 
					&& (personal == null || personal == si.isPersonal))
				groupSubitems.add(subitemName);
		}
		return groupSubitems;
	}
	/**
	 * Вернуть название контейнера
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
