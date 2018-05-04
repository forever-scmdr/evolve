package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.*;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * Created by E on 3/5/2018.
 */
public class MetaboIntegrateParsedCommand extends IntegrateBase {

	private final String CATALOG = "catalog";
	private final String SECTION = "section";
	private final String PRODUCT = "product";

	private final String NAME = "name";
	private final String ID = "id";
	private final String NAME_EXTRA = "name_extra";
	private final String DESCRIPTION = "description";
	private final String SHORT = "short";
	private final String CODE = "code";
	private final String CODE = "code";
	private final String CODE = "code";
	private final String CODE = "code";


	private ParsedInfoProvider infoProvider;
	private ItemType sectionType;
	private ItemType productType;

	@Override
	protected boolean makePreparations() throws Exception {
		sectionType = ItemTypeRegistry.getItemType(SECTION);
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		infoProvider = new ParsedInfoProvider();
		return infoProvider.isValid();
	}

	@Override
	protected void integrate() throws Exception {
		List<Item> catalogs = new ItemQuery(CATALOG).loadItems();
		if (catalogs.size() > 0) {
			for (Item catalog : catalogs) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(catalog));
			}
			executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(10, null));
		}
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		Document tree = infoProvider.getTree();
		processSubsections(tree.getElementsByTag("data").first(), catalog);
	}

	private void processSubsections(Element root, Item parent) throws Exception {
		Elements sectionEls = root.select("> section");
		for (Element sectionEl : sectionEls) {
			String[] path = StringUtils.split(sectionEl.attr(ID), '_');
			if (path.length <= 0)
				continue;
			String secName = path[path.length - 1];
			Item section = Item.newChildItem(sectionType, parent);
			section.setValue(NAME, secName);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
			processSubsections(sectionEl, section);
		}
		Elements productEls = root.select("> product");
		for (Element productEl : productEls) {

		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
