package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.LinkedList;

public class AddNameHashes extends IntegrateBase {

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Подсчет айтемов.");
		LinkedList<Item> items = new LinkedList<>();
		items.addAll(new ItemQuery("featured").loadItems());
		items.addAll(new ItemQuery("news_item").loadItems());
		items.addAll(new ItemQuery("small_news_item").loadItems());
		info.setToProcess(items.size());
		setOperation("Добавление хешей");
		Item item;
		while ((item = items.poll()) != null){
			String name = item.getStringValue("name","");
			item.setValue("hash", name.hashCode());
			executeAndCommitCommandUnits(SaveItemDBUnit.get(item).noTriggerExtra().noFulltextIndex().ignoreFileErrors().ignoreUser());
			info.increaseProcessed();
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
