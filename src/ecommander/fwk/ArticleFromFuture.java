package ecommander.fwk;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import extra._generated.ItemNames;

import java.util.Date;

public class ArticleFromFuture extends Command implements ItemEventCommandFactory {
    @Override
    public PersistenceCommandUnit createCommand(Item item) throws Exception {
        long date = item.getLongValue(ItemNames.news_item_.DATE,0);
        long now = new Date().getTime();
        if(date > now){
            item.setValueUI("is_future", "1");
        }else{
            item.setValueUI("is_future", "0");
        }
        return SaveItemDBUnit.get(item).noTriggerExtra().noFulltextIndex();
    }

    public ResultPE checkFuture(){

        return null;
    }

    @Override
    public ResultPE execute() throws Exception {
        return null;
    }
}
