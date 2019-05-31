package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

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
			Item parent = ItemQuery.loadById(seo.getContextParentId(), getTransactionContext().getConnection());
			if(!parent.getItemType().isKeyUnique()) return;
			// Проверка, есть ли сео для айтема
			List<Item> seos = new ItemQuery(SEO).setParentId(parent.getId(), false, SEO).loadItems();
			long newSeoId = -1;
			if (seos.size() == 0) {
				Item seoCatalog = ItemUtils.ensureSingleRootItem(SEO_CATALOG, User.getDefaultUser(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
				seo.setValueUI(KEY_UNIIQUE, parent.getKeyUnique());
				Item newSeo = Item.newChildItem(ItemTypeRegistry.getItemType(SEO), seoCatalog);
				Item.updateParamValues(seo, newSeo);
				executeCommand(SaveItemDBUnit.get(newSeo).noTriggerExtra());
				executeCommand(CreateAssocDBUnit.childExistsSoft(newSeo, parent, ItemTypeRegistry.getAssocId(SEO)));
				newSeoId = newSeo.getId();
			} else {
				newSeoId = seos.get(0).getId();
			}
			executeCommand(ItemStatusDBUnit.delete(seo));
			seo.setId(newSeoId); // это надо для того, чтобы в админке был переход на страницу вновь созданного айтема seo
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new CreateSEO(item);
	}
}
