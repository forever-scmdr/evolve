package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;

/**
 * Created by E on 30/11/2018.
 */
public class ReindexInfoCommand extends IntegrateBase implements ItemNames {

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.pushLog("Индексация");
		info.indexsationStarted();

		LuceneIndexMapper.getSingleton().reindexAll();

		info.pushLog("Индексация завершена");
		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}

}
