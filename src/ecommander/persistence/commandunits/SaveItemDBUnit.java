package ecommander.persistence.commandunits;

import ecommander.model.Item;

/**
 * Сохраняет айтем в зависимости от того, новый он или нет
 * Created by E on 11/3/2017.
 */
public class SaveItemDBUnit {

	public static DBPersistenceCommandUnit get(Item item) {
		if (item.isNew())
			return new SaveNewItemDBUnit(item);
		else
			return new UpdateItemParamsDBUnit(item);
	}

	public static DBPersistenceCommandUnit get(Item item, boolean triggerExtra) {
		if (item.isNew())
			return new SaveNewItemDBUnit(item, triggerExtra);
		else
			return new UpdateItemParamsDBUnit(item, triggerExtra);
	}
}
