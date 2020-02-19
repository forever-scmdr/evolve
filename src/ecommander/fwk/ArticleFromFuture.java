package ecommander.fwk;

import ecommander.controllers.PageController;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
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

//    public ResultPE checkFuture() throws Exception {

//    }

    @Override
    public ResultPE execute() throws Exception {
        ServerLogger.error("CHECK FUTURE COMMAND LAUNCED");
        LinkedList<Item> articles = new LinkedList<Item>();
//        long before = new Date().getTime() - (86400*1000);
        long now = new Date().getTime();
        ItemQuery q = new ItemQuery("small_news_item", Item.STATUS_HIDDEN);
        articles.addAll(q.loadItems());
//        q = new ItemQuery("news_item", Item.STATUS_HIDDEN);
//        articles.addAll(q.loadItems());

        boolean needReindex = false;
        for(Item a : articles){
            long v = a.getLongValue(ItemNames.news_item_.DATE, 0);
            if(v < now){
                executeAndCommitCommandUnits(ItemStatusDBUnit.restore(a).ignoreUser(true));
                needReindex = true;
            }
        }
        if(needReindex){
            LuceneIndexMapper.getSingleton().reindexAll();
            PageController.clearCache();
        }
        return null;
    }
}
