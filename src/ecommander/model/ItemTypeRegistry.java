/*
 * Created on 18.08.2007
 */
package ecommander.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private AssocRegistry assocRegistry = null; // реестр ассоциаций
	private TypeHierarchyRegistry hierarchyRegistry = null; // реестр наследования

	private static ItemTypeRegistry singleton = new ItemTypeRegistry();
	private static ItemTypeRegistry tempCopy = null;    // копия реестра, которая возвращается всем потокам кроме обновляющего (modifyThread) во время обновления,
	// т.е. во время, когда реестр заблокирован (locked). Копия создается в момент блокировки и
	// удаляется в момент разблокировки
	private static volatile Thread modifyThread = null; // Пока полностью не загрузилась модель данных, реестр считается заблокированным. Из него нельзя получать информацию.
	// Поток, который модифицирует модель данных. Он может быть только один и ему разрешено читать модель в процессе модификации

	private static final RootType root = new RootType();

	private static final long SESSION_ROOT_ID = -1L;

//	private static final RootItemDescription NO_ROOT_DESC = new RootItemDescription("no_group", Persistence.PERSISTENT, NO_ROOT_ID);

	/**
	 * В конструкторе выполняется загрузка модели из базона
	 */
	private ItemTypeRegistry() {
		itemsByNames = new HashMap<>();
		itemsByIds = new HashMap<>();
		itemNames = new ArrayList<>();
		itemExtenders = new HashMap<>();

		assocRegistry = new AssocRegistry();
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
	 * Возвращает корень по умолчанию
	 * @return
	 */
	public static RootType getPrimaryRoot() {
		return root;
	}

	public static long getPrimaryRootId() {
		return root.getId();
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
	static void addItemDescription(ItemType itemDesc) {
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
	static synchronized void clearRegistry() {
		if (modifyThread != Thread.currentThread())
			throw new IllegalStateException("Illegal attempt to clear item type registry while it is locked");
		singleton = new ItemTypeRegistry();
	}

	/**
	 * Заблокировать реестр для обновления информации
	 */
	static synchronized void lock() {
		if (modifyThread != null)
			throw new IllegalStateException("Illegal attempt to lock item type registry while it is already locked");
		modifyThread = Thread.currentThread();
		tempCopy = singleton;
	}

	/**
	 * Разблокировать реестр после внесения изменений
	 * Копия, используемая другими потоками, очищается и им начинает выдаваться измененный экземпляр
	 */
	static synchronized void unlockSumbit() {
		if (modifyThread != Thread.currentThread())
			throw new IllegalStateException("Illegal attempt to unlock item type registry by nonmodifying thread");
		modifyThread = null;
		tempCopy = null;
	}

	/**
	 * Разблокировать реестр после внесения изменений с возвратом в начальное состояние (до внесения изменений)
	 * Копия, используемая другими потоками, заменяет измененный экземпляр
	 */
	static synchronized void unlockRollback() {
		if (modifyThread != Thread.currentThread())
			throw new IllegalStateException("Illegal attempt to unlock item type registry by nonmodifying thread");
		modifyThread = null;
		singleton = tempCopy;
		tempCopy = null;
	}

	/**
	 * Заблокирован ли реестр другим потоком для модификации
	 *
	 * @return
	 */
	static synchronized boolean isLocked() {
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
	 * Можно запускать в обычном режиме и режиме валидации.
	 * В режиме валидации не заполняется кеш наследников
	 *
	 * @param parentChildPairs
	 * @param validation
	 */
	static synchronized TypeHierarchyRegistry createHierarchy(ArrayList<String[]> parentChildPairs, boolean validation) {
		TypeHierarchyRegistry object = new TypeHierarchyRegistry(parentChildPairs);
		if (!validation) {
			getSingleton().hierarchyRegistry = object;
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
			LinkedHashSet<String> extenders = hierarchyRegistry.getItemExtenders(parent);
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
		return getSingleton().hierarchyRegistry.getItemExtenders(itemName);
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
		return getSingleton().hierarchyRegistry.getItemPredecessors(itemName);
	}

	/**
	 * Создать объекты, представляющие иерархии всех айтемов в удобочитаемом виде
	 *
	 * @return
	 */
	public static TypeHierarchy getHierarchies(Collection<String> itemNames) {
		return getSingleton().hierarchyRegistry.getHierarchies(itemNames);
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
		return getSingleton().hierarchyRegistry.getDirectParents(itemName);
	}

	/**
	 * Проверить, может ли один айтем непосредственно вкладываться в другой айтем
	 * @param parentTypeId
	 * @param childTypeId
	 * @param assocId
	 * @return
	 */
	public static boolean isDirectContainer(int parentTypeId, int childTypeId, byte assocId) {
		Set<String> preds = getItemPredecessorsExt(getItemType(childTypeId).getName());
		ItemType parentType = getItemType(parentTypeId);
		String assocName = getAssoc(assocId).getName();
		for (String pred : preds) {
			if (parentType.hasChild(assocName, pred))
				return true;
		}
		return false;
	}

	/**
	 * Вернуть все ассоциации, которыми могут быть связаны родитель и прямой потомок заданных типов
	 * Если эти типы не могут быть связаны, возвращается пустое множество
	 * @param parentTypeId
	 * @param childTypeId
	 * @return
	 */
	public static Set<Assoc> getDirectContainerAssocs(int parentTypeId, int childTypeId) {
		Set<String> preds = getItemPredecessorsExt(getItemType(childTypeId).getName());
		ItemType parentType = getItemType(parentTypeId);
		LinkedHashSet<Assoc> result = new LinkedHashSet<>();
		for (ItemTypeContainer.ChildDesc childDesc : parentType.getAllChildren()) {
			if (preds.contains(childDesc.itemName))
				result.add(getAssoc(childDesc.assocName));
		}
		return result;
	}
	/**
	 * Получить всех предков айтема + сам айтем
	 *
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	public static Set<String> getItemPredecessorsExt(String itemName) {
		return getSingleton().hierarchyRegistry.getItemPredecessorsExt(itemName);
	}

	/**
	 * Получить все ассоциации определенного айтема
	 * @param itemName
	 * @return
	 */
	public static Set<Byte> getItemOwnAssocIds(String itemName) {
		HashSet<Byte> ids = new HashSet<>();
		ItemTypeContainer item = getSingleton().itemsByNames.get(itemName);
		for (ItemTypeContainer.ChildDesc childDesc : item.getAllChildren()) {
			ids.add(getSingleton().assocRegistry.getAssoc(childDesc.assocName).getId());
		}
		return ids;
	}

	/**
	 * Добавить ассоциацию
	 * @param assoc
	 */
	static void addAssoc(Assoc assoc) {
		getSingleton().assocRegistry.addAssoc(assoc);
	}

	/**
	 * Получить ассоциацию
	 * @param name
	 * @return
	 */
	public static Assoc getAssoc(String name) {
		return getSingleton().assocRegistry.getAssoc(name);
	}

	/**
	 * Получить ассоциацию
	 * @param id
	 * @return
	 */
	public static Assoc getAssoc(byte id) {
		return getSingleton().assocRegistry.getAssoc(id);
	}

	/**
	 * Получить первичную ассоциацию
	 * @return
	 */
	public static Assoc getPrimaryAssoc() {
		return getSingleton().assocRegistry.getPrimary();
	}

	/**
	 * Получить ID всех ассоциаций из модели данных (включая базовую)
	 * @return
	 */
	public static Byte[] getAllAssocIds() {
		return getSingleton().assocRegistry.getAllAssocIds();
	}

}