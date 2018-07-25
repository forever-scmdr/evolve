package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.common.PersistenceCommandUnit;

/**
 * При изменении уникального текстового ключа айтема меняет соответствующий параметр (key_unique)
 * сео айтема
 * Created by E on 25/7/2018.
 */
public class UpdateSeoItemFactory implements ItemEventCommandFactory {

	private static class UpdateSEO extends DBPersistenceCommandUnit {

		@Override
		public void execute() throws Exception {

		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return null;
	}
}
