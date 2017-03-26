package ecommander.persistence.commandunits;

import ecommander.model.Item;

/**
 * Created by E on 11/3/2017.
 */
public class SaveItemDBUnit {

	public static DBPersistenceCommandUnit get(Item item) {
		if (item.isNew())
			return new SaveNewItemDBUnit(item);
		else
			return new UpdateItemParamsDBUnit(item);
	}
}
