package ecommander.filesystem;

import java.io.File;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.datatypes.FileDataType;

/**
 * Базовый класс для всех команд, которые работают с айтемом и его файлами
 * @author EEEE
 *
 */
public abstract class SingleItemDirectoryFileUnit extends ItemDirectoryCommandUnit {
	
	protected Item item;
	
	public SingleItemDirectoryFileUnit(Item item) {
		this.item = item;
	}
	/**
	 * Имя файла параметра (путь начиная от директории хранения всех файлов приложения)
	 * @param paramValue
	 * @return
	 */
	protected final String createParameterFileName(String paramValue) {
		return createItemDirectoryName() + '/' + paramValue;
	}
	/**
	 * Директория хранения файлов айтема (путь начиная от директории хранения всех файлов приложения)
	 * @return
	 */
	protected String createItemDirectoryName() {
		return FileDataType.getItemFilePath(item);
	}


}
