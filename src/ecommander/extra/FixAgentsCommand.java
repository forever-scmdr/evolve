package ecommander.extra;

import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

public class FixAgentsCommand extends Command{
	
	private static final String REFUZED = "refuzed";
	
	public ResultPE fix() throws Exception {
		int counter = 0;
		for(Item agent :ItemQuery.newItemQuery("agent").loadItems()) {
			byte val = agent.getByteValue(REFUZED, (byte)0);
			if(val == 0) {
				agent.setValue(REFUZED, val);
				executeCommandUnit(new UpdateItemDBUnit(agent, false, true).ignoreFileErrors(true).fulltextIndex(false));
				counter++;
			}
			if(counter > 63) {
				commitCommandUnits();
				counter = 0;
			}
		}
		commitCommandUnits();
		return null;
	}

	@Override
	public ResultPE execute() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
