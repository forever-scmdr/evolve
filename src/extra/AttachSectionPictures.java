package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.io.File;
import java.util.LinkedList;

public class AttachSectionPictures extends IntegrateBase implements CatalogConst {

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		LinkedList<Item> sections = new LinkedList<Item>();
		sections.addAll(new ItemQuery(SECTION_ITEM).loadItems());
		setProcessed(0);
		Item s;
		while ((s = sections.poll()) != null){
			if(s == null) break;
			ItemQuery q = new ItemQuery(PRODUCT_ITEM);
			q.setParentId(s.getId(), true);
			q.addParameterCriteria(MAIN_PIC_PARAM, "-", "!=", null, Compare.SOME);
			q.setLimit(1);
			Item product = q.loadFirstItem();
			if(product != null){
				String folder = AppContext.getFilesDirPath(product.isFileProtected());
				File mp = product.getFileValue(MAIN_PIC_PARAM, folder);
				s.setValue(MAIN_PIC_PARAM, mp);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(s).noFulltextIndex().ignoreFileErrors().noFulltextIndex().ignoreUser());
			}
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
