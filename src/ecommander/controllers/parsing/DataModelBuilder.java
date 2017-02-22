package ecommander.controllers.parsing;

import java.io.File;
import java.util.ArrayList;

import ecommander.common.exceptions.ValidationException;
import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.model.DataModelCreationValidator;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.DelayedTransaction;

/**
 * @author EEEE
 *
 *
 */
public class DataModelBuilder {
	public static final long MODIFIED_TEST_INTERVAL = 10000; // время, через которое проводится проверка обновления model.xml
	private static volatile long fileLastChecked = 0;
	private static volatile long fileLastModified = 0;
	
	private static final Object SEMAPHORE = new Object();
	
	/**
	 * Проверяет, были ли произведены изменения в модели страниц, и если были, выполняет обновление реестра страниц
	 * @throws Exception 
	 */
	public static void testActuality() throws Exception {
		if (!ItemTypeRegistry.isLocked() && System.currentTimeMillis() - fileLastChecked > MODIFIED_TEST_INTERVAL) {
			synchronized (SEMAPHORE) {
				if (!ItemTypeRegistry.isLocked() && System.currentTimeMillis() - fileLastChecked > MODIFIED_TEST_INTERVAL) {
					ArrayList<File> modelFiles = DataModelCreateCommandUnit.findModelFiles(new File(AppContext.getMainModelPath()), null);
					modelFiles.add(new File(AppContext.getUserModelPath()));
					long modified = 0;
					for (File file : modelFiles) {
						if (file.lastModified() > modified)
							modified = file.lastModified();
					}
					if (modified > fileLastModified && !ItemTypeRegistry.isLocked()) {
						try {
							ItemTypeRegistry.lock();
							PageController.clearCache();
							reloadModel();
							PageModelBuilder.invalidate();
						} finally {
							ItemTypeRegistry.unlock();
						}
					}
					fileLastChecked = System.currentTimeMillis();
				}
			}
		}
	}
	/**
	 * Перезагрузка модели данных в случае если она не заблокирована
	 * @throws Exception
	 */
	public static boolean tryLockAndReloadModel() throws Exception {
		if (!ItemTypeRegistry.isLocked()) {
			synchronized (SEMAPHORE) {
				if (!ItemTypeRegistry.isLocked()) {
					try {
						ItemTypeRegistry.lock();
						PageController.clearCache();
						reloadModel();
						PageModelBuilder.invalidate();
					} finally {
						ItemTypeRegistry.unlock();
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
	 * @throws Exception 
	 */
	private static synchronized void reloadModel() throws Exception {
		DataModelCreationValidator validator = new DataModelCreationValidator();
		validator.validate();
		if (!validator.isSuccessful()) {
			throw new ValidationException("model.xml validation failed", null, validator.getResults());
		}
		DelayedTransaction transaction = new DelayedTransaction(null);
		transaction.addCommandUnit(new DataModelCreateCommandUnit());
		transaction.execute();
		fileLastModified = System.currentTimeMillis();
	}
	
}
