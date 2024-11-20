package ecommander.persistence.commandunits;

import java.util.ArrayList;

import ecommander.model.item.Item;

public class SaveWithSubitemsDBUnit extends DBPersistenceCommandUnit{

	private Item item;
	private ArrayList<Item> kids = new ArrayList<Item>();
	private boolean usingRefs = false;
	
	public SaveWithSubitemsDBUnit(Item item) {
		this(item, false);
	}
	/**
	 * Можно отменить использование ссылок.
	 * В этом случае информация для связей с родительскими айтемами гораздо проще
	 * и запрос выполняется гораздо быстрее
	 * @param item
	 * @param usingRefs
	 */
	public SaveWithSubitemsDBUnit(Item item, boolean usingRefs) {
		this.item = item;
		this.usingRefs = usingRefs;
	}
	
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		executeCommand(new SaveNewItemDBUnit(item, usingRefs));
		for(Item kid : kids){
			kid.setDirectParentId(item.getId());
			executeCommand(new SaveNewItemDBUnit(kid));
		}
	}
	
	protected final Item getItem() {
		return item;
	}

	protected final void setItem(Item item) {
		this.item = item;
	}

	protected final ArrayList<Item> getKids() {
		return kids;
	}

	protected final void setKids(ArrayList<Item> kids) {
		this.kids = kids;
	}

	public void addKid(Item kid){
		kids.add(kid);	
	}
	public void removeKid(Item kid){
		kids.remove(kid);
	}
}
