package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by user on 15.10.2018.
 */
public class ExterminateSEO extends Command{

	@Override
	public ResultPE execute() throws Exception {
		try {
			ItemQuery q = new ItemQuery(ItemNames.SEO);
			List<Item> loadedItems = q.loadItems();
			for (Item item : loadedItems) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(item));
			}
			return getResult("success");
		}catch (Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			ResultPE error = getResult("error");
			error.setVariable("message", sw.toString());
			return error;
		}
	}
}
