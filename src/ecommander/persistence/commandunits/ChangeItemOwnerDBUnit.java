package ecommander.persistence.commandunits;

import ecommander.model.Item;
import ecommander.persistence.mappers.DBConstants;

/**
 * Created by E on 26/3/2017.
 */
public class ChangeItemOwnerDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent, DBConstants {

	private Item item;
	private int newUser;
	private byte newGroup;

	public ChangeItemOwnerDBUnit(Item item, int newUser, byte newGroup) {
		this.item = item;
		this.newUser = newUser;
		this.newGroup = newGroup;
	}

	@Override
	public void execute() throws Exception {
		if ()
	}
}
