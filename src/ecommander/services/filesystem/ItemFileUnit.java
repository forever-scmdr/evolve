package ecommander.services.filesystem;

import java.io.File;

import org.apache.commons.io.FileUtils;

import ecommander.common.Strings;
import ecommander.controllers.AppContext;
import ecommander.model.Item;

/**
 * Базовый класс для всех команд, которые работают с айтемом и его файлами
 * @author EEEE
 *
 */
public abstract class ItemFileUnit extends FilePersistenceCommandUnit {
	
	protected Item item;
	
	public ItemFileUnit(Item item) {
		this.item = item;
	}
	/**
	 * Имя файла параметра (путь начиная от директории хранения всех файлов приложения)
	 * @param paramValue
	 * @return
	 */
	protected final String createParameterFileName(String paramValue) {
		return item.getPredecessorsPath() + item.getId() + Strings.SLASH + paramValue;
	}
	/**
	 * Директория хранения файлов айтема (путь начиная от директории хранения всех файлов приложения)
	 * @return
	 */
	protected String createItemFileDirectoryName() {
		return AppContext.getFilesDirPath() + item.getPredecessorsAndSelfPath();
	}
	
	public static boolean deleteItemDirectory(Item item) {
		return FileUtils.deleteQuietly(new File(AppContext.getFilesDirPath() + item.getPredecessorsAndSelfPath()));
	}
}
