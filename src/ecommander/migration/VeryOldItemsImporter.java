package ecommander.migration;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ecommander.fwk.ServerLogger;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;

public class VeryOldItemsImporter extends DBPersistenceCommandUnit {

	private static final String DUMP_NAME = "items.xml";
	
	public void execute() throws Exception {
		Document doc = Jsoup.parse(new File(AppContext.getCommonFilesDirPath() + DUMP_NAME), "UTF-8");
		int itemCount = 0;
		long currentParentId = 1;
		DelayedTransaction transaction = new DelayedTransaction(null);
		for (Element itemNode : doc.getElementsByTag("item")) {
			ItemType type = ItemTypeRegistry.getItemType(itemNode.attr("type"));
			long id = Long.parseLong(itemNode.attr("id"));
			long refId = Long.parseLong(itemNode.attr("ref-id"));
			long parentId = Long.parseLong(itemNode.attr("parent"));
			String predecessors = itemNode.attr("baseItems");
			long userId = Long.parseLong(itemNode.attr("user"));
			if (userId == 1L)
				userId = 0L;
			int groupId = Integer.parseInt(itemNode.attr("group"));
			
			// Выполнить транзакцию
			if (parentId != currentParentId || itemCount % 10 == 0)
				transaction.execute();
			currentParentId = parentId;
			itemCount++;
			
			// Добавление команд в транзакцию
			if (refId == id) {
				Item item = Item.newItem(type, parentId, predecessors, userId, groupId);
				item.setId(id);
				item.setRefId(refId);
				for (Element paramNode : itemNode.getElementsByTag("parameter")) {
					item.setValueUI(paramNode.attr("name"), paramNode.ownText());
				}
				transaction.addCommandUnit(new SaveNewItemDBUnit(item).fulltextIndex(false));
			} else {
				transaction.addCommandUnit(new CreateExtendedReferenceDBUnit(refId, parentId, id));
			}
		}
		transaction.execute();
		ServerLogger.warn("Imported " + itemCount + " items");
	}

}
