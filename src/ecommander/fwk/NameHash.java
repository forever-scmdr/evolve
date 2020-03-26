package ecommander.fwk;

import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;

public class NameHash implements ItemEventCommandFactory  {
	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		String name = item.getStringValue("name");
		item.setValue("hash", name.hashCode());
		return SaveItemDBUnit.get(item).noTriggerExtra();
	}
}
