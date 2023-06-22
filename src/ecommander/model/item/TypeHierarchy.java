package ecommander.model.item;

import java.util.ArrayList;
/**
 * Класс для представления иерархий айтемов в читабельном виде
 * @author EEEE
 *
 */
public class TypeHierarchy implements Comparable<TypeHierarchy> {
	private String itemName;
	private ArrayList<TypeHierarchy> extenders;
	
	TypeHierarchy(String name) {
		itemName = name;
		extenders = new ArrayList<TypeHierarchy>();
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public ArrayList<TypeHierarchy> getExtenders() {
		return extenders;
	}
	
	public boolean hasItems() {
		return extenders.size() > 0;
	}

	public int compareTo(TypeHierarchy o) {
		return itemName.compareTo(o.itemName);
	}

	@Override
	public String toString() {
		return itemName;
	}
}
