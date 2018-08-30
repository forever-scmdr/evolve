package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;

/**
 * Прикрепляет айтем SEO ко вновь созданному айтему-контейнеру сео, если найден подходящий айтем сео
 * Created by E on 2/8/2018.
 */
public class CreateSeoContainerItemFactory implements ItemEventCommandFactory {
	public static final String SEO = "seo";
	public static final String KEY_UNIIQUE = "key_unique";

	private static class LinkSEO extends DBPersistenceCommandUnit {

		private Item item;

		public LinkSEO(Item item) {
			this.item = item;
		}

		@Override
		public void execute() throws Exception {
			Item seo = new ItemQuery(SEO).addParameterCriteria(KEY_UNIIQUE, item.getKeyUnique(), "=", null, Compare.SOME).loadFirstItem();
			if (seo != null) {
				executeCommand(new CreateAssocDBUnit(seo, item, ItemTypeRegistry.getAssocId(SEO)));
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new LinkSEO(item);
	}
}
