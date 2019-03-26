package ecommander.fwk;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.DeleteAssocDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by E on 5/2/2019.
 */
public class CreateCatalogLink implements ItemEventCommandFactory {

	private static final String PARENT_ID = "parent_id";
	private static final String CATEGORY_ID = "category_id";

	private static final String CATALOG_LINK_ASSOC = "catalog_link";
	private static final String SECTION = "section";


	private static class CreateLink extends DBPersistenceCommandUnit {

		private Item src;

		public CreateLink(Item src) {
			this.src = src;
		}

		@Override
		public void execute() throws Exception {
			List<Item> parents = new ItemQuery(SECTION).setChildId(src.getId(), false, CATALOG_LINK_ASSOC).loadItems();
			Item directParent = new ItemQuery(SECTION).setChildId(src.getId(), false).loadFirstItem();
			HashMap<String, Item> existingParents = new HashMap<>();
			for (Item parent : parents) {
				String code = parent.getStringValue(CATEGORY_ID);
				if (StringUtils.isNotBlank(code)) {
					existingParents.put(code, parent);
				}
			}

			String[] requiredParentCodesStr;
			if (StringUtils.equalsIgnoreCase(src.getTypeName(), SECTION)) {
				requiredParentCodesStr = StringUtils.split(src.getStringValue(PARENT_ID), ',');
			} else {
				requiredParentCodesStr = StringUtils.split(src.getStringValue(CATEGORY_ID), ',');
			}

			HashSet<String> requiredParentCodes = new HashSet<>();
			for (String code : requiredParentCodesStr) {
				if (StringUtils.isNotBlank(StringUtils.trim(code))) {
					requiredParentCodes.add(StringUtils.trim(code));
				}
			}
			if (directParent != null) // в случае создания нового айтема
				requiredParentCodes.remove(StringUtils.trim(directParent.getStringValue(CATEGORY_ID)));

			byte catalogLinkAssocId = ItemTypeRegistry.getAssocId(CATALOG_LINK_ASSOC);

			// Связи, которые удаляются
			HashSet<String> deletingLinks = new HashSet<>(existingParents.keySet());
			deletingLinks.removeAll(requiredParentCodes);

			// Связи, которые создаются
			HashSet<String> creatingLinks = new HashSet<>(requiredParentCodes);
			creatingLinks.removeAll(existingParents.keySet());

			for (String deletingLink : deletingLinks) {
				Item parent = existingParents.get(deletingLink);
				executeCommandInherited(new DeleteAssocDBUnit(src, parent, catalogLinkAssocId));
			}

			for (String creatingLink : creatingLinks) {
				Item parent = ItemQuery.loadSingleItemByParamValue(SECTION, CATEGORY_ID, creatingLink);
				if (parent != null) {
					executeCommandInherited(CreateAssocDBUnit.childExistsStrict(src, parent, catalogLinkAssocId));
				}
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new CreateLink(item);
	}
}
