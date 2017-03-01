package ecommander.model;

import java.io.File;
import java.util.ArrayList;

import ecommander.fwk.ValidationException;
import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.pages.PageModelBuilder;
import ecommander.persistence.DelayedTransaction;

/**
 * @author EEEE
 *
 *
 */
public class DataModelBuilder {

	private static final Object SEMAPHORE = new Object();

	/**
	 * Перезагрузка модели данных в случае если она не заблокирована
	 * @throws Exception
	 */
	public static boolean tryLockAndReloadModel(boolean forceModel) throws Exception {
		if (!ItemTypeRegistry.isLocked()) {
			synchronized (SEMAPHORE) {
				if (!ItemTypeRegistry.isLocked()) {
					boolean updated = false;
					try {
						ItemTypeRegistry.lock();
						PageController.clearCache();
						updated = reloadModel(forceModel);
						PageModelBuilder.invalidate();
					} finally {
						if (updated)
							ItemTypeRegistry.unlockSumbit();
						else
							ItemTypeRegistry.unlockRollback();
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Парсит XML модели данных и сохраняет все в БД одной транзакцией
	 * Не проверяет блокировку модели данных.
	 * Метод, который вызывает reloadModel сам должен выставлять блокировку модели.
	 * @param forceModel
	 * @return если апдейт произощел - true, иначе - false
	 * @throws Exception
	 */
	private static synchronized boolean reloadModel(boolean forceModel) throws Exception {
		ArrayList<File> modelFiles = DataModelCreateCommandUnit.findModelFiles(new File(AppContext.getMainModelPath()), null);
		DataModelCreationValidator validator = new DataModelCreationValidator(modelFiles);
		validator.validate();
		if (!validator.isSuccessful()) {
			throw new ValidationException("model.xml validation failed", null, validator.getResults());
		}
		// Тестовый запуск, если он нужен
		boolean doUpdate = forceModel;
		DelayedTransaction transaction = new DelayedTransaction(null);
		if (!forceModel) {
			DataModelCreateCommandUnit create = new DataModelCreateCommandUnit(true);
			transaction.addCommandUnit(create);
			transaction.execute();
			doUpdate |= create.isNoDeletionNeeded();
		}
		if (doUpdate) {
			transaction.addCommandUnit(new DataModelCreateCommandUnit(true));
			transaction.execute();
			return true;
		}
		return false;
	}
	
}
