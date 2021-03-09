package ecommander.persistence.commandunits;

import ecommander.fwk.Timer;

/**
 * Один цикл удаления.
 * Ограничен максимальным временем выполнения
 * Created by E on 6/3/2019.
 */
public class PerformDeletedCleaningDBUnit extends DBPersistenceCommandUnit {

	private static final String TIMER_NAME = "background_delete";

	private int batchSize;
	private int maxWorkingSeconds;

	public PerformDeletedCleaningDBUnit(int batchSize, int maxSeconds) {
		this.batchSize = batchSize;
		this.maxWorkingSeconds = maxSeconds;
	}

	@Override
	public void execute() throws Exception {
		CleanDeletedItemsDBUnit delete = new CleanDeletedItemsDBUnit(batchSize);
		try {
			Timer.getTimer().start(TIMER_NAME);
			do {
				executeCommand(delete);
			} while (delete.getDeletedCount() > 0 && Timer.getTimer().getSeconds(TIMER_NAME) < maxWorkingSeconds);
		} finally {
			Timer.getTimer().stop(TIMER_NAME);
		}
	}
}
