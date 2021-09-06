package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

public class PreviewFromFutureCommand extends Command {

	@Override
	public ResultPE execute() throws Exception {
		long id = Long.parseLong(getVarSingleValue("id"));
		Item newsItem = ItemQuery.loadById(id);
		Item preview = getSessionMapper().getSingleRootItemByName("news");
		if(preview != null){
			List<Item> old = getSessionMapper().getItemsByName(newsItem.getTypeName(), preview.getId());
			for(Item ni : old){
				getSessionMapper().removeItems(ni.getId());
			}
		}
		preview = preview == null? getSessionMapper().createSessionRootItem("news") : preview;
		getSessionMapper().saveTemporaryItem(preview);
		newsItem.setContextPrimaryParentId(preview.getId());
		getSessionMapper().saveTemporaryItem(newsItem);
		return null;
	}
}
