package lunacrawler;

import ecommander.fwk.IntegrateBase;
import lunacrawler.fwk.SingleItemCrawlerController;

/**
 * Парсинг известных заранее урлов
 * Created by E on 7/2/2018.
 */
public class SingleCrawlerCommand extends IntegrateBase {
	private static final String STAGE_PARAM = "stage";
	private static final String RESET_TO_STAGE_PARAM = "reset_to_stage";

	private static SingleItemCrawlerController controller;

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		String stage = getVarSingleValueDefault(STAGE_PARAM, "INIT");
		String resetToStage = getVarSingleValueDefault(RESET_TO_STAGE_PARAM, "INIT");
		info.limitLog(30);
		info.setOperation("Парсинг сайта");
		SingleItemCrawlerController.State state;
		try {
			state = SingleItemCrawlerController.State.valueOf(stage);
		} catch (IllegalArgumentException e) {
			state = SingleItemCrawlerController.State.INIT;
		}
		SingleItemCrawlerController.State resetToState;
		try {
			resetToState = SingleItemCrawlerController.State.valueOf(resetToStage);
		} catch (IllegalArgumentException e) {
			resetToState = SingleItemCrawlerController.State.INIT;
		}
		controller = new SingleItemCrawlerController(info);
		if (resetToState != SingleItemCrawlerController.State.INIT) {
			state = controller.resetToStage(resetToState);
		}
		controller.startStage(state);
		info.setOperation("Парсинг окончен");
	}

	@Override
	protected void terminate() throws Exception {
		controller.terminate();
	}
}
