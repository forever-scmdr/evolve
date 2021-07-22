package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;

/**
 * При изменении уникального текстового ключа айтема меняет соответствующий параметр (key_unique)
 * сео айтема
 * Created by E on 25/7/2018.
 */
public class UpdateSeoContainerItemFactory implements ItemEventCommandFactory {

	public static final String SEO = "seo";
	public static final String KEY_UNIIQUE = "key_unique";

	private static class UpdateSEO extends DBPersistenceCommandUnit {

		private Item item;

		public UpdateSEO(Item item) {
			this.item = item;
		}

		@Override
		public void execute() throws Exception {
			Item seo = new ItemQuery(SEO).setParentId(item.getId(), false, SEO).loadFirstItem();
			if (seo != null) {
				seo.setValueUI(KEY_UNIIQUE, item.getKeyUnique());
				executeCommand(SaveItemDBUnit.get(seo).noTriggerExtra());
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new UpdateSEO(item);
	}
}
