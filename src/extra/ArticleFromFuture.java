package extra;

import ecommander.controllers.PageController;
import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;

import java.util.Date;
import java.util.LinkedList;

public class ArticleFromFuture extends Command implements ItemEventCommandFactory {

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		long date = item.getLongValue(ItemNames.news_item_.DATE,0);
		long now = new Date().getTime();
		DBPersistenceCommandUnit commandUnit = null;
		if(date > now){
			commandUnit = ItemStatusDBUnit.hide(item);
		}else{
			commandUnit = ItemStatusDBUnit.restore(item);
		}
		return commandUnit;
	}



	@Override
	public ResultPE execute() throws Exception {
		LinkedList<Item> articles = new LinkedList<Item>();
		long now = new Date().getTime();
		ItemQuery q = new ItemQuery("small_news_item", Item.STATUS_HIDDEN);
		articles.addAll(q.loadItems());

		q = new ItemQuery("news_item", Item.STATUS_HIDDEN);
		articles.addAll(q.loadItems());

		q = new ItemQuery("featured", Item.STATUS_HIDDEN);
		articles.addAll(q.loadItems());

		boolean needReindex = false;
		for(Item a : articles){
			long v = a.getLongValue(ItemNames.news_item_.DATE, 0);
			if(v < now){
				executeAndCommitCommandUnits(ItemStatusDBUnit.restore(a).ignoreUser(true).noTriggerExtra());
				needReindex = true;
			}
		}

		q = new ItemQuery("telegram_link", Item.STATUS_HIDDEN, Item.STATUS_NORMAL);
		Item telegramLink = q.loadFirstItem();
		if(telegramLink != null){
			long from = telegramLink.getLongValue("start" ,0);
			long to = telegramLink.getLongValue("finish", Long.MAX_VALUE);
			boolean show = now >= from && now <= to;
			if(show){
				executeAndCommitCommandUnits(ItemStatusDBUnit.restore(telegramLink).ignoreUser(true).noTriggerExtra());
			}else{
				executeAndCommitCommandUnits(ItemStatusDBUnit.hide(telegramLink).ignoreUser(true).noTriggerExtra());
			}
		}

		if(needReindex){
			PageController.clearCache();
		}
		return null;
	}
}
