package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.LongDataType;
import ecommander.persistence.mappers.LuceneIndexMapper;

/**
 * Переиндексация, может быть полная или частичная
 * Если частичная - у переиндексируемых айтемов должен быть общий предок (родитель),
 * которые передается через переменную pred
 * Created by E on 26/12/2018.
 */
public class ReindexCommand extends IntegrateBase {
	long parentId = -1;

	@Override
	protected boolean makePreparations() throws Exception {
		getInfo().indexsationStarted();
		String predStr = getVarSingleValueDefault("pred", "-1");
		try {
			parentId = Integer.parseInt(predStr);
		} catch (Exception e) {
			parentId = -1;
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		getInfo().indexsationStarted();
		if (parentId > 0) {
			LuceneIndexMapper.getSingleton().reindexAll(parentId);
		} else {
			LuceneIndexMapper.getSingleton().reindexAll();
		}
		getInfo().setOperation("Индекскация завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
