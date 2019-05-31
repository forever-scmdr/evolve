package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.LinkedHashMap;

/**
 * Created by E on 4/5/2019.
 */
public class UpdateSeoItemHandler implements ItemEventCommandFactory {

	public static final String SEO = "seo";
	public static final String KEY_UNIIQUE = "key_unique";

	private static class Command extends DBPersistenceCommandUnit {
		private Item seo;

		public Command(Item seo) {
			this.seo = seo;
		}

		@Override
		public void execute() throws Exception {
			String keyUnique = seo.getStringValue(KEY_UNIIQUE);
			LinkedHashMap<String, Item> found = ItemQuery.loadByUniqueKey(keyUnique);
			Item contentItem = found.get(keyUnique);
			if (contentItem != null) {
				try {
					executeCommand(CreateAssocDBUnit.childExistsSoft(seo, contentItem, ItemTypeRegistry.getAssocId(SEO)));
				} catch (Exception e) {
					// ничего не делать
				}
			}
		}
	}
	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new Command(item);
	}
}
