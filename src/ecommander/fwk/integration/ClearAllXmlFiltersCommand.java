package ecommander.fwk.integration;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

public class ClearAllXmlFiltersCommand extends Command implements CatalogConst {
    @Override
    public ResultPE execute() throws Exception {
        List<Item> sectons = new ItemQuery(SECTION_ITEM).loadItems();
        for (Item section : sectons) {
            section.clearValue(XML_FILTER);
            executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
        }
        return null;
    }
}
