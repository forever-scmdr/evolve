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
 * Создает в памяти полную модель данных (имеется в виду модель из General объектов)
 * Модель создается из XML файла, записывается в БД и потом каждый следующий раз использования 
 * грузится уже из БД
 * 
 * Для сайта должен быть только один экземпляр этой модели
 * 
 * @author E
 */
public class ItemTypeRegistry {

	private Map<String, ItemTypeContainer> itemsByNames = null; // Все возможные айтемы и корни по именам (имя айтема => объект ItemTypeContainer)
	private ArrayList<String> itemNames = null; // Именя всех айтемов
	private Map<Integer, ItemType> itemsByIds = null; // Числовые ID всех айтемов (для оптимизации)
	

	private HashMap<String, HashSet<String>> deepSubitemGroups = null;	// все группы пользователей, которым могут принадлежать сабайтемы (любого уровня вложенности) 
																		// заданного айтема или корня
																		// отображение 'название айтема + _ + название сабайтема  ' => 'разрешенные группы'
																		// Запись содержится только в случае если какие-либо группы указаны. Если группы для сабайтема не
																		// указаны, то этот сабайтем вообще 
	
	private Map<Integer, Integer[]> itemExtenders = null;	// Список всех наследников всех айтемов
	private Map<Integer, Integer[]> itemContainers = null;	// Список всех айтемов, которые могут содержать заданный айтем
															// например, айтем "продукт" могут содержать все наследники айтема "раздел"
															// Этот список нужен для загрузки successor

	
	private static ItemTypeRegistry singleton = new ItemTypeRegistry();
	private static ItemTypeRegistry tempCopy = null;	// копия реестра, которая возвращается всем потокам кроме обновляющего (modifyThread) во время обновления,
														// т.е. во время, когда реестр заблокирован (locked). Копия создается в момент блокировки и 
														// удаляется в момент разблокировки
	private static volatile Thread modifyThread = null; // Пока полностью не загрузилась модель данных, реестр считается заблокированным. Из него нельзя получать информацию.
														// Поток, который модифицирует модель данных. Он может быть только один и ему разрешено читать модель в процессе модификации
	
	private static final RootItemType SESSION_ROOT = new RootItemType(null, -1);
	
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
	 * @param generalItemName
	 * @return
	 */
	public static ItemType getItemType(String generalItemName) {
		// TODO <usability> сделать эксэпшен, когда не найден айтем с таким названием
		return (ItemType) getSingleton().itemsByNames.get(generalItemName);
	}
	/**
	 * Получить айтем либо рут
	 * @param generalItemName
	 * @return
	 */
	public static ItemTypeContainer getItemTypeContainer(String generalItemName) {
		return getSingleton().itemsByNames.get(generalItemName);
	}
	
	public static ItemType getItemType(int typeId) {
		return getSingleton().itemsByIds.get(typeId);
	}
	/**
	 * Возвращает корень для определенной группы пользователей
	 * @param groupName
	 * @return
	 */
	public static RootItemType getGroupRoot(String groupName) {
		return (RootItemType) getSingleton().itemsByNames.get(RootItemType.createRootName(groupName));
	}
	/**
	 * Возвращает корень по умолчанию, т. е. корень группы по умолчанию (common)
	 * @return
	 */
	public static RootItemType getDefaultRoot() {
		//return NO_ROOT_DESC;
		return getGroupRoot(User.USER_DEFAULT_GROUP);
	}
	/**
	 * Возвращает корневой айтем для потоянных (persistence = database) персональных айтемов
	 * @param groupName
	 * @return
	 */
	public static RootItemType getPersonalRoot(String groupName) {
		return (RootItemType) getSingleton().itemsByNames.get(RootItemType.createRootName(groupName));
	}
	/**
	 * Возвращает корневой айтем для сеансовых (persistence = session) айтемов (которые всегда персональные)
	 * @param groupName
	 * @return
	 */
	public static RootItemType getSessionRoot() {
		return SESSION_ROOT;
	}
	/**
	 * Использовать только при загрузке айтем дескрипшенов.
	 * Добавляет дескрипшен к общему реестру
	 * @param description
	 */
	public static void addItemDescription(ItemType itemDesc) {
		getSingleton().itemsByNames.put(itemDesc.getName(), itemDesc);
		getSingleton().itemsByIds.put(itemDesc.getTypeId(), itemDesc);
		getSingleton().itemNames.add(itemDesc.getName());
	}
	/**
	 * Использовать только при загрузке айтем дескрипшенов.
	 * @param root
	 */
	public static void addRootDescription(RootItemType rootDesc) {
		getSingleton().itemsByNames.put(rootDesc.getName(), rootDesc);
	}
	/**
	 * Возвращает всю структуру айтемов
	 * @return
	 */
	public static ArrayList<ItemType> getAllowedTopLevelItems(String groupName) {
		ArrayList<ItemType> items = new ArrayList<ItemType>();
		Iterator<String> iter = getGroupRoot(groupName).getAllowedSubitemNames(groupName).iterator();
		while (iter.hasNext())
			items.add(getItemType(iter.next()));
		return items;
	}
	/**
	 * Получение синглтона
	 * Если реестр заблокирован (производится модификация), доступ к нему может получить только модифицирующий его поток
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
	 * @return
	 */
	public static synchronized boolean isLocked() {
		return modifyThread != null;
	}
	/**
	 * Возвращает названия всех айтемов
	 * @return
	 */
	public static List<String> getItemNames() {
		return getSingleton().itemNames;
	}
	/**
	 * Возвращает ID типа айтема по его названию
	 * @param itemName
	 * @return
	 */
	public static int getItemTypeId(String itemName) {
		return ((ItemType) getSingleton().itemsByNames.get(itemName)).getTypeId();
	}
	/**
	 * Вернуть список ID типов айтемов по списку названий айтемов
	 * @param itemNames
	 * @return
	 */
	public static Collection<Integer> getItemTypeIds(Collection<String> itemNames) {
		HashSet<Integer> ids = new HashSet<Integer>();
		for (String itemName : itemNames) {
			ids.add(((ItemType) getSingleton().itemsByNames.get(itemName)).getTypeId());
		}
		return ids;
	}
	/**
	 * Создать иерархии
	 * @param parentChildPairs
	 * @param validation
	 */
	public static synchronized TypeHierarchyRegistry createHierarchy(ArrayList<String[]> parentChildPairs, boolean validation) {
		TypeHierarchyRegistry object = new TypeHierarchyRegistry(parentChildPairs, validation);
		TypeHierarchyRegistry.setSingleton(object);
		if (!validation) {
			getSingleton().createExtendersCache(parentChildPairs);
		}
		return object;
	}
	/**
	 *  Заполнить буфер для наследников айтемов
	 *  В этом буфере хранятся массивы айтемов, которые расширяют базовый айтем
	 * @param parentChildPairs
	 */
	private void createExtendersCache(ArrayList<String[]> parentChildPairs) {
		for (String[] strings : parentChildPairs) {
			Integer parentId = getItemTypeId(strings[0]);
//			if (parentId == null)
//				throw new IllegalArgumentException("Item descriptions sem to be not present in ItemTypeRegistry. " + 
//						"First - create item descriptions, then - create item hierarchies");
			itemExtenders.put(parentId, TypeHierarchyRegistry.getSingleton().getItemExtendersIds(parentId));
		}
	}
	/**
	 * Заполнить буфер для айтемов, в которых он может содержаться
	 * В этом буфере хранятся массивы айтемов, которые могут содержать в себе базовый айтем (например, продукт => раздел, основной раздел)
	 */
	private void createContainersCache() {
		itemContainers = new HashMap<Integer, Integer[]>();
		HashMap<Integer, HashSet<Integer>> containers = new HashMap<Integer, HashSet<Integer>>();
		// Для всех сайтемов пройтись по собственным сабайтемам
		for (ItemType item : itemsByIds.values()) {
			for (String subitemName : item.getAllSubitemNames()) {
				if (item.isSubitemOwn(subitemName)) {
					// Каждому айтему, который расширяет сабайтем, добавить в список контейнеров
					// все айтемы, которые расширяют содержащий айтем
					ItemType subitem = getItemType(subitemName);
					Integer[] subitemExts = getItemExtendersIds(subitem.getTypeId());
					Integer[] itemExts = getItemExtendersIds(item.getTypeId());
					for (Integer subitemExtId : subitemExts) {
						HashSet<Integer> conts = containers.get(subitemExtId);
						if (conts == null) {
							conts = new HashSet<Integer>();
							containers.put(subitemExtId, conts);
						}
						for (Integer itemExtId : itemExts) {
							conts.add(itemExtId);
						}
					}
				}
			}
		}
		// Перегнать множества в массивы
		for (Integer id : containers.keySet()) {
			HashSet<Integer> conts = containers.get(id);
			itemContainers.put(id, conts.toArray(new Integer[conts.size()]));
		}
	}
	/**
	 * Вернуть все сабайтемы контейнера, которые потенциально могут содержать айтемы, принадлежащие заданной группе пользователей
	 * @param containerName
	 * @return
	 */
	private Collection<String> getUserGroupAllowedTransitionalSubitemsInt(String containerName, String groupName) {
		if (deepSubitemGroups == null) {
			deepSubitemGroups = new HashMap<String, HashSet<String>>();
			for (String name : itemsByNames.keySet()) {
				processGroupsForItem(name, name, new HashSet<String>());
			}
		}
		ItemTypeContainer container = itemsByNames.get(containerName);
		HashSet<String> result = new HashSet<String>();
		for (String subitemName : container.getAllSubitemNames()) {
			if (!container.isSubitemPersonal(subitemName)) {
				HashSet<String> allowedGroups = deepSubitemGroups.get(getAllowedGroupsForSubitemsMapKey(containerName, subitemName));
				if (allowedGroups == null || allowedGroups.contains(groupName)) {
					result.addAll(getItemExtenders(subitemName));
				}
			}
		}
		return result;
	}
	/**
	 * Вернуть все сабайтемы контейнера, которые могут принадлежать заданной группе пользователей (либо персональные, либо общие)
	 * @param containerName
	 * @param groupName
	 * @param personal - нужны персональные сабайтемы (если нужны общие - false). Если нужны все - null
	 * @return
	 */
	private Set<String> getUserGroupAllowedSubitemsInt(String containerName, String groupName, Boolean personal) {
		ItemTypeContainer item = itemsByNames.get(containerName);
		HashSet<String> result = new HashSet<String>();
		for (String subitemName : item.getUserGroupAllowedSubitems(groupName, personal)) {
			result.addAll(getItemExtenders(subitemName));
		}
		return result;
	}
	/**
	 * Проверяет, является ли сабайтем некоторого айтема персональным или общим
	 * @param container
	 * @param subitemName
	 * @return
	 */
	private boolean isSubitemPersonalInt(ItemTypeContainer container, String subitemName) {
		for (String ext : getItemExtenders(subitemName)) {
			if (container.hasSubitem(ext) && container.isSubitemPersonal(ext))
				return true;
		}
		return false;
	}
	/**
	 * Проверяет группы возможных пользователей для сабайтемов заданного айтема на всех уровнях вложенности
	 * @param containerName
	 * @param currentNode
	 * @param processed
	 */
	private void processGroupsForItem(String containerName, String currentNode, HashSet<String> processed) {
		if (processed.contains(currentNode))
			return;
		processed.add(currentNode);
		processed.add(containerName);
		ItemTypeContainer current = itemsByNames.get(currentNode);
		for (String subitemName : current.getAllSubitemNames()) {
			String groupName = current.getSubitemOwnerGroup(subitemName);
			if (groupName != null) {
				String key = getAllowedGroupsForSubitemsMapKey(containerName, currentNode);
				HashSet<String> groups = deepSubitemGroups.get(key);
				if (groups == null) {
					groups = new HashSet<String>();
					deepSubitemGroups.put(key, groups);
				}
				groups.add(groupName);
			}
			processGroupsForItem(containerName, subitemName, processed);
		}
	}
	/**
	 * Получить ключ для хэшмэпа deepSubitemGroups
	 * @param containerName
	 * @param subitemName
	 * @return
	 */
	private String getAllowedGroupsForSubitemsMapKey(String containerName, String subitemName) {
		return containerName + " / " + subitemName;
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
	 * Извлечь айтемы, которые могут содержать сабайтем определенного типа
	 * @param itemId
	 * @return
	 */
	public static Integer[] getItemContainers(int itemId) {
		return getSingleton().itemContainers(itemId);
	}
	/**
	 * Извлечь айтемы, которые могут содержать сабайтем определенного типа
	 * @param itemId
	 * @return
	 */
	private Integer[] itemContainers(int itemId) {
		if (itemContainers == null)
			createContainersCache();
		return itemContainers.get(itemId);
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
	 * Получить всех предков айтема
	 * Возвращается массив ID айтемов
	 * @param itemName
	 * @return массив GeneralItem
	 */
	public static Integer[] getItemPredecessorsIds(int itemId) {
		return TypeHierarchyRegistry.getSingleton().getItemPredecessorsIds(itemId);
	}
	/**
	 * Создать объекты, представляющие иерархии всех айтемов в удобочитаемом виде
	 * @return
	 */
	public static TypeHierarchy getHierarchies(Collection<String> itemNames) {
		return TypeHierarchyRegistry.getSingleton().getHierarchies(itemNames);
	}
	/**
	 * Получить базовый тип (предок) айтема из ограниченного списка возможных предков
	 * !!! Если предков несколько, то возвращается первый встреченный в списке возможный предок
	 * @param possiblePredecessors
	 * @param item
	 * @return
	 */
	public static String findItemPredecessor(Collection<String> possiblePredecessors, String itemName) {
		return TypeHierarchyRegistry.getSingleton().findItemPredecessor(possiblePredecessors, itemName);
	}
	/**
	 * Получить прямых предков айтема
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
	/**
	 * Получить названия сабайтемов определенного айтема определенной группы пользователей, 
	 * которые (эти сабайтемы) сами принадлежат другой группе, но могут содержать айтемы (на уровне любой глубины вложенности),
	 * принадлежащие заданной группе пользователей
 	 * @param containerName
	 * @param userGroup
	 * @return
	 */
	public static Collection<String> getUserGroupAllowedTransitionalSubitems(String containerName, String userGroup) {
		return getSingleton().getUserGroupAllowedTransitionalSubitemsInt(containerName, userGroup);
	}
	/**
	 * Получить названия сабайтемов определенного айтема определенной группы пользователей, 
	 * которые могут принадлежать заданной группе пользователей (персонально либо в общем пользовании).
	 * Если нужны все, не зависимо от персональности, то personal устанавливать в null
	 * @param containerName
	 * @param userGroup
	 * @param personal
	 * @return
	 */
	public static Set<String> getUserGroupAllowedSubitems(String containerName, String userGroup, Boolean personal) {
		return getSingleton().getUserGroupAllowedSubitemsInt(containerName, userGroup, personal);
	}
	/**
	 * Проверяет, является ли сабайтем атйема персональным.
	 * @param container
	 * @param subitemName
	 * @return
	 */
	public static boolean isSubitemPersonal(ItemTypeContainer container, String subitemName) {
		return getSingleton().isSubitemPersonalInt(container, subitemName);
	}
}