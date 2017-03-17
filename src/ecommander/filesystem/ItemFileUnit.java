package ecommander.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import ecommander.fwk.Strings;
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
		return createItemFileDirectoryName() + File.separator + paramValue;
	}
	/**
	 * Директория хранения файлов айтема (путь начиная от директории хранения всех файлов приложения)
	 * @return
	 */
	protected String createItemFileDirectoryName() {
		if (item.isFileProtected())
			return AppContext.getProtectedDirPath() + item.getFilesPath();
		if (!item.isStatusNormal())
			return AppContext.getHiddenDirPath() + item.getFilesPath();
		return AppContext.getFilesDirPath() + item.getFilesPath();
	}

	/**
	 * Удаляет директорию айтема.
	 * Если при удалении директории в родительской не осталось ни одного файла и директории, то удалить и родительскую,
	 * предыдущую родительскоу и т.д.
	 * @return
	 */
	protected void deleteItemDirectory() throws IOException {
		Path itemDir = Paths.get(AppContext.getFilesDirPath() + item.getFilesPath());
		FileUtils.deleteQuietly(itemDir.toFile());
		boolean deleted = true;
		while (deleted) {
			itemDir = itemDir.subpath(0, itemDir.getNameCount() - 1);
			if (Files.exists(itemDir) && isDirEmpty(itemDir)) {
				Files.delete(itemDir);
				deleted = true;
			}
		}
	}

	protected static boolean isDirEmpty(final Path directory) throws IOException {
		try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
			return !dirStream.iterator().hasNext();
		}
	}
}
