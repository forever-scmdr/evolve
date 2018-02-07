package lunacrawler;

import ecommander.fwk.IntegrateBase;
import lunacrawler.fwk.SingleItemCrawlerController;

/**
 * Парсинг известных заранее урлов
 * Created by E on 7/2/2018.
 */
public class SingleCrawlerCommand extends IntegrateBase {
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		String mode = getVarSingleValueDefault("pmode", "none");
		info.limitLog(30);
		info.setOperation("Парсинг сайта");
		SingleItemCrawlerController.State state;
		try {
			state = SingleItemCrawlerController.State.valueOf(mode);
		} catch (IllegalArgumentException e) {
			state = SingleItemCrawlerController.State.INIT;
		}
		SingleItemCrawlerController.getSingleton(info).startStage(state);
		info.setOperation("Парсинг окончен");
	}

	@Override
	protected void terminate() throws Exception {
		SingleItemCrawlerController.terminate();
	}
}
