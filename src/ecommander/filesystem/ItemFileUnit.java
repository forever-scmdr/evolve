package ecommander.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

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
		return createItemFilesDirectoryName() + File.separator + paramValue;
	}
	/**
	 * Директория хранения файлов айтема (путь начиная от директории хранения всех файлов приложения)
	 * @return
	 */
	protected String createItemFilesDirectoryName() {
		if (item.isFileProtected())
			return AppContext.getProtectedDirPath() + item.getFilesPath();
		if (!item.isStatusNormal())
			return AppContext.getHiddenDirPath() + item.getFilesPath();
		return AppContext.getFilesDirPath() + item.getFilesPath();
	}


}
