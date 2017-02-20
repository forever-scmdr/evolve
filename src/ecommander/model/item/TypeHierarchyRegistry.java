/*
 * Created on 16.09.2008
 */
package ecommander.model.item;

import java.sql.SQLException;
import java.util.*;

import ecommander.common.ServerLogger;

/**
 * Иерархия наследования айтемов
 *
 * @author EEEE
 */
class TypeHierarchyRegistry {

	private static final byte NONE = 0;
	private static final byte PARENT = 1;
	private static final byte CHILD = 2;
	private static final byte SELF = 3;

	// Это отображение нужно для возможности напрямую через ID получать наследников и предшественников айтема
	private HashMap<Integer, Integer> itemIdIndices = new HashMap<>(); // отображение айтем => индекс
	private ArrayList<Integer> itemIds = new ArrayList<>(); // Массив айтемов (отображение индекс => ID айтема)
	// Изначальный (НЕ транзитивное замыкание) список смежности всех типов: тип => список прямых (непосредственных) предшественников
	private HashMap<Integer, ArrayList<Integer>> basicIncedenceList = new HashMap<>();
	// Расширенная матрица смежности (транзитивное замыкание) графа наследования
	// Если два айетма связаны через наследоввание каким-либо образом (непосредсвенно или нет), то соответсвующая
	// ячейка матрицы будет содержать значение PARENT или CHILD
	private byte[][] extendedIncedenceMatrix = null; // i - родитель, j - потомок
	private int matrixDimension = 0;


	private static TypeHierarchyRegistry SINGLETON = null;

	/**
	 * Конструктор, получает иерархию в виде скиска смежности и перовначально заполняет матрицу смежности
	 *
	 * @param basicParentChildPairs
	 * @param validation
	 */
	TypeHierarchyRegistry(ArrayList<int[]> basicParentChildPairs, boolean validation) {
		int indexCount = 0;
		// Заполнение индексов айтемов и определение размеров матрицы
		for (int[] parentChild : basicParentChildPairs) {
			int parent = parentChild[0];
			int child = parentChild[1];
			// Запоминание индексов айтемов
			if (!itemIdIndices.containsKey(parent)) {
				itemIds.add(parent);
				indexCount++;
			}
			if (!itemIdIndices.containsKey(child)) {
				itemIdIndices.put(child, indexCount);
				itemIds.add(child);
				indexCount++;
			}
			// Заполнение списка связности
			ArrayList<Integer> parentsList = basicIncedenceList.get(child);
			if (parentsList == null) {
				parentsList = new ArrayList<Integer>();
				basicIncedenceList.put(child, parentsList);
			}
			parentsList.add(parent);
		}
		// Создание матрицы
		matrixDimension = indexCount;
		extendedIncedenceMatrix = new byte[matrixDimension][matrixDimension];
		// Первоначальное заполнение таблицы
		for (int[] parentChild : basicParentChildPairs) {
			int parent = parentChild[0];
			int child = parentChild[1];
			int i = itemIdIndices.get(parent);
			int j = itemIdIndices.get(child);
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
	 *
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
	 * @param itemId
	 * @return массив GeneralItem
	 */
	LinkedHashSet<Integer> getItemExtenders(int itemId) {
		LinkedHashSet<Integer> extenders = new LinkedHashSet<>();
		extenders.add(itemId);
		if (itemIdIndices.containsKey(itemId)) {
			int itemIndex = itemIdIndices.get(itemId);
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
	 *
	 * @param itemId
	 * @return
	 */
	Integer[] getItemExtendersIds(int itemId) {
		LinkedHashSet<Integer> extenders = getItemExtenders(itemId);
		return extenders.toArray(new Integer[extenders.size()]);
	}

	/**
	 * Получить всех предков айтема
	 *
	 * @param itemId
	 * @return массив GeneralItem
	 */
	LinkedHashSet<Integer> getItemPredecessors(int itemId) {
		LinkedHashSet<Integer> predecessors = new LinkedHashSet<>();
		if (itemIdIndices.containsKey(itemId)) {
			int itemIndex = itemIdIndices.get(itemId);
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
	 *
	 * @param itemId
	 * @return массив GeneralItem
	 */
	Integer[] getItemPredecessorsIds(int itemId) {
		LinkedHashSet<Integer> predecessors = getItemPredecessors(itemId);
		return predecessors.toArray(new Integer[predecessors.size()]);
	}

	/**
	 * Создать объекты, представляющие иерархии всех айтемов в удобочитаемом виде
	 *
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
	 *
	 * @param possiblePredecessors
	 * @param itemId
	 * @return
	 */
	int findItemPredecessor(Collection<Integer> possiblePredecessors, int itemId) {
		if (possiblePredecessors.contains(itemId))
			return itemId;
		if (itemIdIndices.containsKey(itemId)) {
			int itemIndex = itemIdIndices.get(itemId);
			for (int possiblePred : possiblePredecessors) {
				Integer predIndex = itemIdIndices.get(possiblePred);
				if (predIndex != null) {
					if (extendedIncedenceMatrix[itemIndex][predIndex] == CHILD || extendedIncedenceMatrix[itemIndex][predIndex] == SELF)
						return possiblePred;
				}
			}
		}
		return -1;
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
	 *
	 * @param itemName
	 * @return
	 */
	ArrayList<Integer> getDirectParents(int itemId) {
		return basicIncedenceList.get(itemId);
	}

	/**
	 * Получить всех предков айтема + сам айтем
	 *
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	Set<Integer> getItemPredecessorsExt(Integer itemId) {
		Set<String> predecessors = getItemPredecessors(itemName);
		predecessors.add(itemName);
		return predecessors;
	}

}