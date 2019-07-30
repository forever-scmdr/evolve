package ecommander.persistence.commandunits;

import ecommander.fwk.Timer;
import ecommander.model.User;
import ecommander.persistence.common.DelayedTransaction;

/**
 * Один цикл удаления.
 * Ограничен максимальным временем выполнения
 * Created by E on 6/3/2019.
 */
public class PerformDeletedCleaningComplex {

	private static final String TIMER_NAME = "background_delete";

	private int batchSize;
	private int maxWorkingSeconds;

	PerformDeletedCleaningComplex(int batchSize, int maxSeconds) {
		this.batchSize = batchSize;
		this.maxWorkingSeconds = maxSeconds;
	}

	public void execute() throws Exception {
		CleanDeletedItemsDBUnit delete = new CleanDeletedItemsDBUnit(batchSize);
		delete.ignoreUser();
		try {
			Timer.getTimer().start(TIMER_NAME);
			do {
				DelayedTransaction.executeSingle(User.getDefaultUser(), delete);
			} while (delete.getDeletedCount() > 0 && Timer.getTimer().getSeconds(TIMER_NAME) < maxWorkingSeconds);
		} finally {
			Timer.getTimer().stop(TIMER_NAME);
		}
	}
}
