package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;

/**
 * Создание сео айтема в каталоге сео
 * Created by E on 25/7/2018.
 */
public class CreateSeoItemFactory implements ItemEventCommandFactory {
	public static final String SEO_CATALOG = "seo_catalog";
	public static final String KEY_UNIIQUE = "key_unique";
	public static final String SEO = "seo";

	private static class CreateSEO extends DBPersistenceCommandUnit {

		private Item seo;

		public CreateSEO(Item seo) {
			this.seo = seo;
		}

		@Override
		public void execute() throws Exception {
			Item seoCatalog = ItemUtils.ensureSingleRootItem(SEO_CATALOG, User.getDefaultUser(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			Item parent = ItemQuery.loadById(seo.getContextParentId(), getTransactionContext().getConnection());
			seo.setValueUI(KEY_UNIIQUE, parent.getKeyUnique());
			Item newSeo = Item.newChildItem(ItemTypeRegistry.getItemType(SEO), seoCatalog);
			Item.updateParamValues(seo, newSeo);
			executeCommand(SaveItemDBUnit.get(newSeo, false));
			executeCommand(new CreateAssocDBUnit(newSeo, parent, ItemTypeRegistry.getAssocId(SEO)));
			executeCommand(ItemStatusDBUnit.delete(seo));
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new CreateSEO(item);
	}
}
