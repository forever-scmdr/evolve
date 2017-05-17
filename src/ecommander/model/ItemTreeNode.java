package ecommander.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Элемент, на базе которого можно строить деревья айтемов
 *
 * Created by E on 17/5/2017.
 */
public class ItemTreeNode {
	private ArrayList<ItemTreeNode> children = new ArrayList<>();
	private ItemTreeNode parent = null;
	private Item item = null;

	private long newIdGenerator = -1;

	private HashMap<Long, ItemTreeNode> nodesByItemId = new HashMap<>();

	private ItemTreeNode() {

	}

	private ItemTreeNode(Item item) {
		this.item = item;
	}

	public static ItemTreeNode createPureRoot() {
		return new ItemTreeNode();
	}

	public static ItemTreeNode createRoot(Item item) {
		return new ItemTreeNode(item);
	}

	private void addChild(ItemTreeNode child) {
		children.add(child);
		child.parent = this;
		register(child);
	}

	private void register(ItemTreeNode successor) {
		if (successor.item.isNew()) {
			successor.item.setId(newIdGenerator--);
		}
		nodesByItemId.put(successor.item.getId(), successor);
		if (parent != null)
			parent.register(successor);
	}

	/**
	 * Возвращает вновь созданный узел (не родительский)
	 * @param item
	 * @return
	 */
	public ItemTreeNode addChild(Item item) {
		ItemTreeNode newNode = new ItemTreeNode(item);
		addChild(newNode);
		return newNode;
	}

	public ArrayList<ItemTreeNode> getChildren() {
		return children;
	}

	/**
	 * Найти потомка (не обязательно прямого) по ID айтема
	 * @param itemId
	 * @return
	 */
	public ItemTreeNode findSuccessor(long itemId) {
		return nodesByItemId.get(itemId);
	}

	/**
	 * Найти прямого потока по названию айтема
	 * @param typeName
	 * @return
	 */
	public ArrayList<ItemTreeNode> findChildren(String typeName) {
		ArrayList<ItemTreeNode> result = new ArrayList<>();
		for (ItemTreeNode child : children) {
			if (ItemTypeRegistry.getItemExtenders(typeName).contains(child.item.getTypeName()))
				result.add(child);
		}
		return result;
	}

	/**
	 * Получить айтем
	 * @return
	 */
	public Item getItem() {
		return item;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public boolean isPureRoot() {
		return parent == null && item == null;
	}
}
