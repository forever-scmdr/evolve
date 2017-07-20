package ecommander.model;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ValidationException;
import ecommander.pages.PageModelBuilder;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author EEEE
 *
 *
 */
public class DataModelBuilder {

	private static final Object SEMAPHORE = new Object();

	private ArrayList<String> itemsToBeDeleted = null;
	private DataModelCreateCommandUnit.Mode mode;

	private DataModelBuilder(DataModelCreateCommandUnit.Mode mode) {
		this.mode = mode;
	}

	/**
	 * Создать строитель на базе загруженного из БД model.xml
	 * @return
	 */
	public static DataModelBuilder newLoader() {
		return new DataModelBuilder(DataModelCreateCommandUnit.Mode.load);
	}
	/**
	 * Создать строитель на базе файлов model.xml (безопасное обновление)
	 * @return
	 */
	public static DataModelBuilder newSafeUpdate() {
		return new DataModelBuilder(DataModelCreateCommandUnit.Mode.safe_update);
	}
	/**
	 * Создать строитель на базе файлов model.xml (форсировать обновление)
	 * @return
	 */
	public static DataModelBuilder newForceUpdate() {
		return new DataModelBuilder(DataModelCreateCommandUnit.Mode.force_update);
	}
	/**
	 * Перезагрузка модели данных в случае если она не заблокирована
	 * @throws Exception
	 */
	public boolean tryLockAndReloadModel() throws Exception {
		if (!ItemTypeRegistry.isLocked()) {
			synchronized (SEMAPHORE) {
				boolean updated = false;
				if (!ItemTypeRegistry.isLocked()) {
					try {
						ItemTypeRegistry.lock();
						PageController.clearCache();
						updated = reloadModel();
						PageModelBuilder.invalidate();
						ItemTypeRegistry.unlockSumbit();
					} catch (Exception e) {
						ItemTypeRegistry.unlockRollback();
						throw e;
					}
				}
				return updated;
			}
		}
		return false;
	}

	/**
	 * Парсит XML модели данных и сохраняет все в БД одной транзакцией
	 * Не проверяет блокировку модели данных.
	 * Метод, который вызывает reloadModel сам должен выставлять блокировку модели.
	 * @return если апдейт произощел - true, иначе - false
	 * @throws Exception
	 */
	public boolean reloadModel() throws Exception {
		// Валидация сохраненной в БД копии файла model.xml
		if (mode == DataModelCreateCommandUnit.Mode.load) {
			ArrayList<String> xml = new ArrayList<>();
			try (Connection connection = MysqlConnector.getConnection();
			     Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + DBConstants.ModelXML.MODEL_XML_TBL);
				while (rs.next())
					xml.add(rs.getString(DBConstants.ModelXML.XML_XML));
				rs.close();
			}
			if (xml.size() != 0) {
				DataModelCreationValidator validator = new DataModelCreationValidator(xml);
				validator.validate();
				if (!validator.isSuccessful()) {
					throw new ValidationException("DB model.xml is corrupted. Model must be recreated with XML files", null, validator.getResults());
				}
			} else {
				mode = DataModelCreateCommandUnit.Mode.safe_update;
			}
		}
		// Валидация файла model.xml
		if (mode != DataModelCreateCommandUnit.Mode.load) {
			ArrayList<File> modelFiles = DataModelCreateCommandUnit.findModelFiles(new File(AppContext.getMainModelPath()), null);
			ArrayList<String> xml = new ArrayList<>();
			for (File file : modelFiles) {
				xml.add(FileUtils.readFileToString(file, "UTF-8"));
			}
			DataModelCreationValidator validator = new DataModelCreationValidator(xml);
			validator.validate();
			if (!validator.isSuccessful()) {
				throw new ValidationException("model.xml validation failed", null, validator.getResults());
			}
		}
		// Тестовый запуск, если он нужен
		boolean doUpdate = mode == DataModelCreateCommandUnit.Mode.force_update;
		if (mode != DataModelCreateCommandUnit.Mode.force_update) {
			DataModelCreateCommandUnit create = new DataModelCreateCommandUnit(mode);
			DelayedTransaction transaction = new DelayedTransaction(null);
			transaction.addCommandUnit(create);
			transaction.execute();
			itemsToBeDeleted = create.getElementsToDelete();
			doUpdate |= create.isNoDeletionNeeded() && mode == DataModelCreateCommandUnit.Mode.safe_update;
		}
		if (doUpdate) {
			DelayedTransaction transaction = new DelayedTransaction(null);
			transaction.addCommandUnit(new DataModelCreateCommandUnit(DataModelCreateCommandUnit.Mode.force_update));
			transaction.execute();
			return true;
		}
		return false;
	}

	/**
	 * Получить список айтемов и параметров, которые будут удалены в случае апдейта модели данных
	 * @return
	 */
	public ArrayList<String> getItemsToBeDeleted() {
		return itemsToBeDeleted;
	}
}
