/*
 * Created on 16.09.2008
 */
package ecommander.model.item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import ecommander.common.ServerLogger;

/**
 * Иерархия наследования айтемов
 * 
 * @author EEEE
 * 
 * TODO <enhance> перевести представление айтемов из названий их типов на ID их типов
 */
class TypeHierarchyRegistry {
	
	private static final byte NONE = 0;
	private static final byte PARENT = 1;
	private static final byte CHILD = 2;
	private static final byte SELF = 3;
	
	private HashMap<String, Integer> itemIndeces = new HashMap<String, Integer>(); // отображение айтем => индекс
	private ArrayList<String> items = new ArrayList<String>(); // Массив айтемов (отображение индекс => айтем)
	// Это отображение нужно для возможности напрямую через ID получать наследников и предшественников айтема
	private HashMap<Integer, Integer> itemIdIndeсes = new HashMap<Integer, Integer>(); // аналогично itemIndeces, только в качестве ключа - ID айтема
	private ArrayList<Integer> itemIds = new ArrayList<Integer>(); // Массив айтемов (отображение индекс => ID айтема)
	// Изначальный (НЕ транзитивное замыкание) список смежности всех типов: тип => список прямых (непосредственных) предшественников
	private HashMap<String, ArrayList<String>> basicIncedenceList = new HashMap<String, ArrayList<String>>();
	private byte[][] extendedIncedenceMatrix = null;
	private int matrixDimension = 0;
									

	private static TypeHierarchyRegistry SINGLETON = null;
	
	/**
	 * Конструктор, получает иерархию в виде скиска смежности и перовначально заполняет матрицу смежности
	 * @param parentChildPairs
	 * @param validation
	 */
	TypeHierarchyRegistry(ArrayList<String[]> parentChildPairs, boolean validation) {
		int indexCount = 0;
		// Заполнение индексов айтемов и определение размеров матрицы
		for (String[] parentChild : parentChildPairs) {
			String parent = parentChild[0];
			String child = parentChild[1];
			// Запоминание индексов айтемов
			if (!itemIndeces.containsKey(parent)) {
				itemIndeces.put(parent, indexCount);
				items.add(parent);
				if (!validation) {
					int parentId = ItemTypeRegistry.getItemType(parent).getTypeId();
					itemIdIndeсes.put(parentId, indexCount);
					itemIds.add(parentId);
				}
				indexCount++;
			}
			if (!itemIndeces.containsKey(child)) {
				itemIndeces.put(child, indexCount);
				items.add(child);
				if (!validation) {
					int childId = ItemTypeRegistry.getItemType(child).getTypeId();
					itemIdIndeсes.put(childId, indexCount);
					itemIds.add(childId);
				}
				indexCount++;
			}
			// Заполнение списка связности
			ArrayList<String> parentsList = basicIncedenceList.get(child);
			if (parentsList == null) {
				parentsList = new ArrayList<String>();
				basicIncedenceList.put(child, parentsList);
			}
			parentsList.add(parent);
		}
		// Создание матрицы
		matrixDimension = indexCount;
		extendedIncedenceMatrix = new byte[matrixDimension][matrixDimension];
		// Первоначальное заполнение таблицы
		for (String[] parentChild : parentChildPairs) {
			String parent = parentChild[0];
			String child = parentChild[1];
			int i = itemIndeces.get(parent);
			int j = itemIndeces.get(child);
			extendedIncedenceMatrix[i][i] = SELF;
			extendedIncedenceMatrix[j][j] = SELF;
			extendedIncedenceMatrix[i][j] = PARENT;
			extendedIncedenceMatrix[j][i] = CHILD;
		}
		fillTheMatrix();
	}
	/**
	 * Окончательно заполняет матрицу смежности
	 */
	private void fillTheMatrix() {
		for (int i = 0; i < matrixDimension; i++) {
			boolean changePerformed = false;
			for (int j = 0; j < matrixDimension; j++) {
				if (extendedIncedenceMatrix[i][j] == PARENT) {
					// Здесь j - столбец, в котором стоит 1
					for (int k = 0; k < matrixDimension; k++) {
						if (extendedIncedenceMatrix[i][k] == CHILD && extendedIncedenceMatrix[k][j] == NONE) {
							// Здесь k - столбец, в котором стоит 2
							extendedIncedenceMatrix[k][j] = PARENT;
							extendedIncedenceMatrix[j][k] = CHILD;
							changePerformed = true;
						}
					}
				}
			}
			if (changePerformed)
				i = -1;
		}
	}
	/**
	 * Получить синглетон
	 * 
	 * @return
	 * @throws SQLException
	 */
	static TypeHierarchyRegistry getSingleton() {
		if (SINGLETON == null)
			SINGLETON = createEmptySingleton();
		return SINGLETON;
	}
	/**
	 * Заглушка чтобы не было NullPointerException
	 * @return
	 */
	private static TypeHierarchyRegistry createEmptySingleton() {
		ServerLogger.warn("TypeHierarchyRegistry - NO SINGLETON SET. CREATING EMPTY ONE");
		return new TypeHierarchyRegistry(new ArrayList<String[]>(), true);
	}
	/**
	 * Установить новый синглетон для этого класса
	 */
	static void setSingleton(TypeHierarchyRegistry newInstance) {
		SINGLETON = newInstance;
	}
	/**
	 * Получить всех потомков айтема (сам айтем тоже считается потомком)
	 * 
	 * @param itemName
	 * @return массив GeneralItem
	 */
	LinkedHashSet<String> getItemExtenders(String itemName) {
		LinkedHashSet<String> extenders = new LinkedHashSet<String>();
		extenders.add(itemName);
		if (itemIndeces.containsKey(itemName)) {
			int itemIndex = itemIndeces.get(itemName);
			for (int j = 0; j < matrixDimension; j++) {
				if (extendedIncedenceMatrix[itemIndex][j] == PARENT) {
					extenders.add(items.get(j));
				}
			}
		}
		return extenders;
	}
	/**
	 * Получить всех потомков айтема (сам айтем тоже считается потомком)
	 * Возвращается массив ID айтемов
	 * @param itemId
	 * @return
	 */
	LinkedHashSet<Integer> getItemExtendersIdsSet(int itemId) {
		LinkedHashSet<Integer> extenders = new LinkedHashSet<Integer>();
		extenders.add(itemId);
		if (itemIdIndeсes.containsKey(itemId)) {
			int itemIndex = itemIdIndeсes.get(itemId);
			for (int j = 0; j < matrixDimension; j++) {
				if (extendedIncedenceMatrix[itemIndex][j] == PARENT) {
					extenders.add(itemIds.get(j));
				}
			}
		}
		return extenders;
	}
	/**
	 * Получить всех потомков айтема (сам айтем тоже считается потомком)
	 * Возвращается массив ID айтемов
	 * @param itemId
	 * @return
	 */
	Integer[] getItemExtendersIds(int itemId) {
		LinkedHashSet<Integer> extenders = getItemExtendersIdsSet(itemId);
		return extenders.toArray(new Integer[extenders.size()]);
	}
	/**
	 * Получить всех предков айтема
	 * 
	 * @param itemName
	 * @return массив GeneralItem
	 */
	LinkedHashSet<String> getItemPredecessors(String itemName) {
		LinkedHashSet<String> predecessors = new LinkedHashSet<String>();
		if (itemIndeces.containsKey(itemName)) {
			int itemIndex = itemIndeces.get(itemName);
			for (int j = 0; j < matrixDimension; j++) {
				if (extendedIncedenceMatrix[itemIndex][j] == CHILD) {
					predecessors.add(items.get(j));
				}
			}
		}
		return predecessors;
	}
	/**
	 * Получить всех предков айтема
	 * Возвращается массив ID айтемов
	 * @param itemId
	 * @return массив GeneralItem
	 */
	LinkedHashSet<Integer> getItemPredecessorsIdsSet(int itemId) {
		LinkedHashSet<Integer> predecessors = new LinkedHashSet<Integer>();
		if (itemIdIndeсes.containsKey(itemId)) {
			int itemIndex = itemIdIndeсes.get(itemId);
			for (int j = 0; j < matrixDimension; j++) {
				if (extendedIncedenceMatrix[itemIndex][j] == CHILD) {
					predecessors.add(itemIds.get(j));
				}
			}
		}
		return predecessors;
	}
	/**
	 * Получить всех предков айтема
	 * Возвращается массив ID айтемов
	 * @param itemId
	 * @return массив GeneralItem
	 */
	Integer[] getItemPredecessorsIds(int itemId) {
		LinkedHashSet<Integer> predecessors = getItemPredecessorsIdsSet(itemId);
		return predecessors.toArray(new Integer[predecessors.size()]);
	}
	/**
	 * Создать объекты, представляющие иерархии всех айтемов в удобочитаемом виде
	 * @return
	 */
	TypeHierarchy getHierarchies(Collection<String> itemNames) {
		TypeHierarchy root = new TypeHierarchy("ROOT");
		for (String itemName : itemNames) {
			Integer item = itemIndeces.get(itemName);
			if (item == null) {
				TypeHierarchy hchy = new TypeHierarchy(itemName);
				root.getExtenders().add(hchy);
			} else {
				boolean isRoot = true;
				for (int relative = 0; relative < matrixDimension; relative++) {
					if (extendedIncedenceMatrix[item][relative] == CHILD) {
						isRoot = false;
						break;
					}
				}
				if (isRoot) {
					TypeHierarchy hchy = new TypeHierarchy(itemName);
					root.getExtenders().add(hchy);
					HashSet<Integer> predecessors = new HashSet<Integer>();
					predecessors.add(item);
					addChildren(predecessors, hchy, item);
				}
			}
		}
		return root;
	}

	/**
	 * Получить базовый тип (предок) айтема из ограниченного списка возможных предков
	 * !!! Если предков несколько, то возвращается первый встреченный в списке возможный предок
	 * @param possiblePredecessors
	 * @param itemName
	 * @return
	 */
	String findItemPredecessor(Collection<String> possiblePredecessors, String itemName) {
		if (possiblePredecessors.contains(itemName))
			return itemName;
		if (itemIndeces.containsKey(itemName)) {
			int itemIndex = itemIndeces.get(itemName);
			for (String possiblePred : possiblePredecessors) {
				Integer predIndex = itemIndeces.get(possiblePred);
				if (predIndex != null) {
					if (extendedIncedenceMatrix[itemIndex][predIndex] == CHILD || extendedIncedenceMatrix[itemIndex][predIndex] == SELF)
						return possiblePred;
				}
			}
		}
		return null;
	}
	
	private void addChildren(Set<Integer> processed, TypeHierarchy parentHchy, int parent) {
		if (parent < 0)
			return;
		for (int item = 0; item < matrixDimension; item++) {
			if (extendedIncedenceMatrix[parent][item] == PARENT/* && !processed.contains(item)*/) {
//				boolean add = true;
//				for (int relative = 0; relative < matrixDimension; relative ++) {
//					if (extendedIncidenceMatrix[item][relative] == CHILD && parent != relative && processed.contains(relative)) {
//						add = false;
//						break;
//					}
//				}
//				if (add) {
					TypeHierarchy newHchy = new TypeHierarchy(items.get(item));
					parentHchy.getExtenders().add(newHchy);
					processed.add(item);
					addChildren(processed, newHchy, item);
//				}
			}
		}
	}
	/**
	 * Получить прямых предков айтема
	 * @param itemName
	 * @return
	 */
	ArrayList<String> getDirectParents(String itemName) {
		return basicIncedenceList.get(itemName);
	}
	/**
	 * Получить всех предков айтема + сам айтем
	 * 
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	Set<String> getItemPredecessorsExt(String itemName) {
		Set<String> predecessors = getItemPredecessors(itemName);
		predecessors.add(itemName);
		return predecessors;
	}

}