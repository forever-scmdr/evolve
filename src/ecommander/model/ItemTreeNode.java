package ecommander.model;

import java.util.ArrayList;
import java.util.Collection;
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

	private HashMap<Long, ItemTreeNode> nodesByItemId = new HashMap<>();

	private ItemTreeNode() {

	}

	private ItemTreeNode(Item item) {
		this.item = item;
		register(this);
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
		if (successor.item.getContextParentId() == Item.DEFAULT_ID) {
			if (parent != null && parent.item != null)
				successor.item.setContextPrimaryParentId(parent.item.getContextParentId());
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

	public ItemTreeNode getFirstChild() {
		if (children.size() > 0)
			return children.get(0);
		return null;
	}

	/**
	 * Найти потомка (не обязательно прямого) по ID айтема
	 * @param itemId
	 * @return
	 */
	public ItemTreeNode find(long itemId) {
		return nodesByItemId.get(itemId);
	}

	public Collection<Long> getAllIds() {
		return nodesByItemId.keySet();
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

	public boolean isRoot() {
		return parent == null;
	}
}
