package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.persistence.mappers.LuceneIndexMapper;

/**
 * Created by E on 26/12/2018.
 */
public class ReindexCommand extends IntegrateBase {
	@Override
	protected boolean makePreparations() throws Exception {
		info.indexsationStarted();
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.indexsationStarted();
		LuceneIndexMapper.getSingleton().reindexAll();
		info.setProcessed(LuceneIndexMapper.getSingleton().getCountProcessed());
	}

	@Override
	protected void terminate() throws Exception {

	}
}
