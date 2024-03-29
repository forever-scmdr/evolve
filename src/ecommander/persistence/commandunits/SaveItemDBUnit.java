package ecommander.persistence.commandunits;

import ecommander.model.Item;

/**
 * Сохраняет айтем в зависимости от того, новый он или нет
 * Created by E on 11/3/2017.
 */
public class SaveItemDBUnit {

	private SaveItemDBUnit() {}

	public static DBPersistenceCommandUnit get(Item item) {
		if (item.isNew())
			return new SaveNewItemDBUnit(item);
		else
			return new UpdateItemParamsDBUnit(item);
	}

	public static DBPersistenceCommandUnit forceUpdate(Item item) {
		return new UpdateItemParamsDBUnit(item, true);
	}

	public static DBPersistenceCommandUnit new_(Item item, Item parent) {
		return new SaveNewItemDBUnit(item, parent);
	}
}
