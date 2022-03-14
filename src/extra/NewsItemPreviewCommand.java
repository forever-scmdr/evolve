package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;

public class NewsItemPreviewCommand extends Command {

	@Override
	public ResultPE execute() throws Exception {
		long id = Long.parseLong(getVarSingleValue("id"));
		Item newsItem = ItemQuery.loadById(id);



		return result;
	}
}