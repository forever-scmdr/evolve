package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;

/**
 * Created by E on 26/12/2018.
 */
public class ReindexCommand extends IntegrateBase implements CatalogConst{
	@Override
	protected boolean makePreparations() throws Exception {
		info.indexsationStarted();
		return true;
	}

	@Override
	protected void integrate() throws Exception{
		info.indexsationStarted();
		Item shitSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, "16");
		if(shitSection != null){
		//	executeAndCommitCommandUnits(ItemStatusDBUnit.hide(shitSection.getId()));
		}
		LuceneIndexMapper.getSingleton().reindexAll();
		if(shitSection != null){
		//	executeAndCommitCommandUnits(ItemStatusDBUnit.restore(shitSection.getId()));
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
