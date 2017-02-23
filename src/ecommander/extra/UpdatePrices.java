package ecommander.extra;



import org.apache.commons.lang3.StringUtils;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ItemVariablesContainer;
import ecommander.pages.ItemVariablesContainer.ItemVariables;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

public class UpdatePrices extends Command {
		
	@Override
	public ResultPE execute() throws Exception {
		ItemVariablesContainer varContainer = getItemVariables();
		for (ItemVariables vars : varContainer.getItemPosts()) {
			Item item = ItemQuery.loadById(vars.getItemId());
			for (String inputName : vars.getPostedInputs()) {
				String paramName = StringUtils.substringBeforeLast(inputName, "_inp");
				item.setValueUI(paramName, vars.getValue(inputName));
			}
			executeCommandUnit(new UpdateItemDBUnit(item, false, true).fulltextIndex(false));
		}
		commitCommandUnits();
		return getResult("success");
	}

}
