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
	private HashMap<String, Integer> itemIndices = new HashMap<>(); // отображение айтем => индекс
	private ArrayList<String> items = new ArrayList<>(); // Массив айтемов (отображение индекс => ID айтема)
	// Изначальный (НЕ транзитивное замыкание) список смежности всех типов: тип => список прямых (непосредственных) предшественников
	private HashMap<String, ArrayList<String>> basicIncedenceList = new HashMap<>();
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
	 */
	TypeHierarchyRegistry(ArrayList<String[]> basicParentChildPairs) {
		int indexCount = 0;
		// Заполнение индексов айтемов и определение размеров матрицы
		for (String[] parentChild : basicParentChildPairs) {
			String parent = parentChild[0];
			String child = parentChild[1];
			// Запоминание индексов айтемов
			if (!itemIndices.containsKey(parent)) {
				items.add(parent);
				indexCount++;
			}
			if (!itemIndices.containsKey(child)) {
				itemIndices.put(child, indexCount);
				items.add(child);
				indexCount++;
			}
			// Заполнение списка связности
			ArrayList<String> parentsList = basicIncedenceList.get(child);
			if (parentsList == null) {
				parentsList = new ArrayList<>();
				basicIncedenceList.put(child, parentsList);
			}
			parentsList.add(parent);
		}
		// Создание матрицы
		matrixDimension = indexCount;
		extendedIncedenceMatrix = new byte[matrixDimension][matrixDimension];
		// Первоначальное заполнение таблицы
		for (String[] parentChild : basicParentChildPairs) {
			String parent = parentChild[0];
			String child = parentChild[1];
			int i = itemIndices.get(parent);
			int j = itemIndices.get(child);
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
		return new TypeHierarchyRegistry(new ArrayList<String[]>());
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
	 * @param item
	 * @return массив GeneralItem
	 */
	LinkedHashSet<String> getItemExtenders(String item) {
		LinkedHashSet<String> extenders = new LinkedHashSet<>();
		extenders.add(item);
		if (itemIndices.containsKey(item)) {
			int itemIndex = itemIndices.get(item);
			for (int j = 0; j < matrixDimension; j++) {
				if (extendedIncedenceMatrix[itemIndex][j] == PARENT) {
					extenders.add(items.get(j));
				}
			}
		}
		return extenders;
	}

	/**
	 * Получить всех предков айтема
	 *
	 * @param item
	 * @return массив GeneralItem
	 */
	LinkedHashSet<String> getItemPredecessors(String item) {
		LinkedHashSet<String> predecessors = new LinkedHashSet<>();
		if (itemIndices.containsKey(item)) {
			int itemIndex = itemIndices.get(item);
			for (int j = 0; j < matrixDimension; j++) {
				if (extendedIncedenceMatrix[itemIndex][j] == CHILD) {
					predecessors.add(items.get(j));
				}
			}
		}
		return predecessors;
	}

	/**
	 * Создать объекты, представляющие иерархии всех айтемов в удобочитаемом виде
	 *
	 * @return
	 */
	TypeHierarchy getHierarchies(Collection<String> itemNames) {
		TypeHierarchy root = new TypeHierarchy("ROOT");
		for (String itemName : itemNames) {
			Integer itemIdx = itemIndices.get(itemName);
			if (itemIdx == null) {
				TypeHierarchy hchy = new TypeHierarchy(itemName);
				root.getExtenders().add(hchy);
			} else {
				boolean isRoot = true;
				for (int relative = 0; relative < matrixDimension; relative++) {
					if (extendedIncedenceMatrix[itemIdx][relative] == CHILD) {
						isRoot = false;
						break;
					}
				}
				if (isRoot) {
					TypeHierarchy hchy = new TypeHierarchy(itemName);
					root.getExtenders().add(hchy);
					HashSet<Integer> predecessors = new HashSet<>();
					predecessors.add(itemIdx);
					addChildren(predecessors, hchy, itemIdx);
				}
			}
		}
		return root;
	}

//	/** TODO <delete> delete
//	 * Получить базовый тип (предок) айтема из ограниченного списка возможных предков
//	 * !!! Если предков несколько, то возвращается первый встреченный в списке возможный предок
//	 *
//	 * @param possiblePredecessors
//	 * @param item
//	 * @return
//	 */
//	String findItemPredecessor(Collection<String> possiblePredecessors, String item) {
//		if (possiblePredecessors.contains(item))
//			return item;
//		if (itemIndices.containsKey(item)) {
//			int itemIndex = itemIndices.get(item);
//			for (String possiblePred : possiblePredecessors) {
//				Integer predIndex = itemIndices.get(possiblePred);
//				if (predIndex != null) {
//					if (extendedIncedenceMatrix[itemIndex][predIndex] == CHILD || extendedIncedenceMatrix[itemIndex][predIndex] == SELF)
//						return possiblePred;
//				}
//			}
//		}
//		return null;
//	}

	private void addChildren(Set<Integer> processed, TypeHierarchy parentHchy, int parentIdx) {
		if (parentIdx < 0)
			return;
		for (int itemIdx = 0; itemIdx < matrixDimension; itemIdx++) {
			if (extendedIncedenceMatrix[parentIdx][itemIdx] == PARENT/* && !processed.contains(item)*/) {
//				boolean add = true;
//				for (int relative = 0; relative < matrixDimension; relative ++) {
//					if (extendedIncidenceMatrix[item][relative] == CHILD && parent != relative && processed.contains(relative)) {
//						add = false;
//						break;
//					}
//				}
//				if (add) {
				TypeHierarchy newHchy = new TypeHierarchy(items.get(itemIdx));
				parentHchy.getExtenders().add(newHchy);
				processed.add(itemIdx);
				addChildren(processed, newHchy, itemIdx);
//				}
			}
		}
	}

	/**
	 * Получить прямых предков айтема
	 *
	 * @param item
	 * @return
	 */
	ArrayList<String> getDirectParents(String item) {
		return basicIncedenceList.get(item);
	}

	/**
	 * Получить всех предков айтема + сам айтем
	 *
	 * @param item
	 * @return
	 * @throws Exception
	 */
	Set<String> getItemPredecessorsExt(String item) {
		Set<String> predecessors = getItemPredecessors(item);
		predecessors.add(item);
		return predecessors;
	}

}