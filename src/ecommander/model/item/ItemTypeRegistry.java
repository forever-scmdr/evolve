/*
 * Created on 18.08.2007
 */
package ecommander.model.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ecommander.users.User;

/**
 * Создает в памяти полную модель данных
 * Модель загружается из XML файла при старте сервера
 * Для сайта должен быть только один экземпляр этой модели
 *
 * @author E
 */
public class ItemTypeRegistry {

	private Map<String, ItemTypeContainer> itemsByNames = null; // Все возможные айтемы и корни по именам (имя айтема => объект ItemTypeContainer)
	private ArrayList<String> itemNames = null; // Именя всех айтемов
	private Map<Integer, ItemType> itemsByIds = null; // Числовые ID всех айтемов (для оптимизации)

	private Map<Integer, Integer[]> itemExtenders = null;    // Список всех наследников всех айтемов

	private static ItemTypeRegistry singleton = new ItemTypeRegistry();
	private static ItemTypeRegistry tempCopy = null;    // копия реестра, которая возвращается всем потокам кроме обновляющего (modifyThread) во время обновления,
	// т.е. во время, когда реестр заблокирован (locked). Копия создается в момент блокировки и
	// удаляется в момент разблокировки
	private static volatile Thread modifyThread = null; // Пока полностью не загрузилась модель данных, реестр считается заблокированным. Из него нельзя получать информацию.
	// Поток, который модифицирует модель данных. Он может быть только один и ему разрешено читать модель в процессе модификации

	private static final long SESSION_ROOT_ID = -1L;
	private static final long DEFAULT_ROOT_ID = 0L;

//	private static final RootItemDescription NO_ROOT_DESC = new RootItemDescription("no_group", Persistence.PERSISTENT, NO_ROOT_ID);

	/**
	 * В конструкторе выполняется загрузка модели из базона
	 */
	private ItemTypeRegistry() {
		itemsByNames = Collections.synchronizedMap(new HashMap<String, ItemTypeContainer>());
		itemsByIds = Collections.synchronizedMap(new HashMap<Integer, ItemType>());
		itemNames = new ArrayList<String>();
		itemExtenders = new HashMap<Integer, Integer[]>();
	}

	/**
	 * Возвращает один айтем
	 *
	 * @param itemName
	 * @return
	 */
	public static ItemType getItemType(String itemName) {
		return (ItemType) getSingleton().itemsByNames.get(itemName);
	}

	public static ItemType getItemType(int typeId) {
		return getSingleton().itemsByIds.get(typeId);
	}

	/**
	 * Возвращает корень по умолчанию, т. е. корень группы по умолчанию (common)
	 *
	 * @return
	 */
	public static long getDefaultRootId() {
		return DEFAULT_ROOT_ID;
	}

	/**
	 * Возвращает корневой айтем для сеансовых айтемов
	 *
	 * @return
	 */
	public static long getSessionRootId() {
		return SESSION_ROOT_ID;
	}

	/**
	 * Использовать только при загрузке айтем дескрипшенов.
	 * Добавляет дескрипшен к общему реестру
	 *
	 * @param itemDesc
	 */
	public static void addItemDescription(ItemType itemDesc) {
		getSingleton().itemsByNames.put(itemDesc.getName(), itemDesc);
		getSingleton().itemsByIds.put(itemDesc.getTypeId(), itemDesc);
		getSingleton().itemNames.add(itemDesc.getName());
	}

	/**
	 * Получение синглтона
	 * Если реестр заблокирован (производится модификация), доступ к нему может получить только модифицирующий его поток
	 *
	 * @return
	 */
	private static ItemTypeRegistry getSingleton() {
		if (modifyThread != null && modifyThread != Thread.currentThread()) {
			return tempCopy;
		}
		return singleton;
	}

	/**
	 * Очистить реестр для последующего наполнения
	 */
	public static synchronized void clearRegistry() {
		if (modifyThread != Thread.currentThread())
			throw new IllegalStateException("Illegal attempt to clear item type registry while it is locked");
		singleton = new ItemTypeRegistry();
	}

	/**
	 * Заблокировать реестр для обновления информации
	 */
	public static synchronized void lock() {
		if (modifyThread != null)
			throw new IllegalStateException("Illegal attempt to lock item type registry while it is already locked");
		modifyThread = Thread.currentThread();
		tempCopy = singleton;
	}

	/**
	 * Разблокировать реестр после внесения изменений
	 */
	public static synchronized void unlock() {
		if (modifyThread != Thread.currentThread())
			throw new IllegalStateException("Illegal attempt to unlock item type registry by nonmodifying thread");
		modifyThread = null;
		tempCopy = null;
	}

	/**
	 * Заблокирован ли реестр другим потоком для модификации
	 *
	 * @return
	 */
	public static synchronized boolean isLocked() {
		return modifyThread != null;
	}

	/**
	 * Возвращает названия всех айтемов
	 *
	 * @return
	 */
	public static List<String> getItemNames() {
		return getSingleton().itemNames;
	}

	/**
	 * Возвращает ID типа айтема по его названию
	 *
	 * @param itemName
	 * @return
	 */
	public static int getItemTypeId(String itemName) {
		return ((ItemType) getSingleton().itemsByNames.get(itemName)).getTypeId();
	}

	/**
	 * Вернуть список ID типов айтемов по списку названий айтемов
	 *
	 * @param itemNames
	 * @return
	 */
	public static Collection<Integer> getItemTypeIds(Collection<String> itemNames) {
		HashSet<Integer> ids = new HashSet<>();
		for (String itemName : itemNames) {
			ids.add(((ItemType) getSingleton().itemsByNames.get(itemName)).getTypeId());
		}
		return ids;
	}

	/**
	 * Заполнить реестр иерархи наследования айтемов
	 * Можно запускать в обычномрезмие и режиме валидации.
	 * В режиме валидации не заполняется кеш наследников
	 *
	 * @param parentChildPairs
	 * @param validation
	 */
	public static synchronized TypeHierarchyRegistry createHierarchy(ArrayList<String[]> parentChildPairs, boolean validation) {
		TypeHierarchyRegistry object = new TypeHierarchyRegistry(parentChildPairs);
		TypeHierarchyRegistry.setSingleton(object);
		if (!validation) {
			getSingleton().createExtendersCache(parentChildPairs);
		}
		return object;
	}

	/**
	 * Заполнить буфер для наследников айтемов
	 * В этом буфере хранятся массивы айтемов, которые расширяют базовый айтем
	 *
	 * @param parentChildPairs
	 */
	private void createExtendersCache(ArrayList<String[]> parentChildPairs) {
		for (String[] strings : parentChildPairs) {
			String parent = strings[0];
			LinkedHashSet<String> extenders = TypeHierarchyRegistry.getSingleton().getItemExtenders(parent);
			ArrayList<Integer> extIds = new ArrayList<>(extenders.size());
			for (String item: extenders) {
				extIds.add(getItemTypeId(item));
			}
			Integer parentId = getItemTypeId(parent);
			this.itemExtenders.put(parentId, extIds.toArray(new Integer[0]));
		}
	}

	/**
	 * Получить всех потомков айтема (сам айтем тоже считается потомком)
	 *
	 * @param itemName
	 * @return массив GeneralItem
	 */
	public static LinkedHashSet<String> getItemExtenders(String itemName) {
		return TypeHierarchyRegistry.getSingleton().getItemExtenders(itemName);
	}

	/**
	 * Получить всех потомков айтема (сам айтем тоже считается потомком)
	 * Возвращается массив ID айтемов
	 *
	 * @param itemId
	 * @return
	 */
	public static Integer[] getItemExtendersIds(int itemId) {
		Integer[] itemExts = getSingleton().itemExtenders.get(itemId);
		if (itemExts == null) {
			Integer[] exts = new Integer[1];
			exts[0] = itemId;
			return exts;
		}
		return itemExts;
	}

	/**
	 * Получить всех предков айтема
	 *
	 * @param itemName
	 * @return массив GeneralItem
	 */
	public static LinkedHashSet<String> getItemPredecessors(String itemName) {
		return TypeHierarchyRegistry.getSingleton().getItemPredecessors(itemName);
	}

	/**
	 * Создать объекты, представляющие иерархии всех айтемов в удобочитаемом виде
	 *
	 * @return
	 */
	public static TypeHierarchy getHierarchies(Collection<String> itemNames) {
		return TypeHierarchyRegistry.getSingleton().getHierarchies(itemNames);
	}

//	/** TODO <delete>
//	 * Получить базовый тип (предок) айтема из ограниченного списка возможных предков
//	 * !!! Если предков несколько, то возвращается первый встреченный в списке возможный предок
//	 *
//	 * @param possiblePredecessors
//	 * @param itemName
//	 * @return
//	 */
//	public static String findItemPredecessor(Collection<String> possiblePredecessors, String itemName) {
//		return TypeHierarchyRegistry.getSingleton().findItemPredecessor(possiblePredecessors, itemName);
//	}

	/**
	 * Получить прямых предков айтема
	 *
	 * @param itemName
	 * @return
	 */
	public static ArrayList<String> getDirectParents(String itemName) {
		return TypeHierarchyRegistry.getSingleton().getDirectParents(itemName);
	}

	/**
	 * Получить всех предков айтема + сам айтем
	 *
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	public static Set<String> getItemPredecessorsExt(String itemName) {
		return TypeHierarchyRegistry.getSingleton().getItemPredecessorsExt(itemName);
	}
}