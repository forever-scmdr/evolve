package ecommander.services.filesystem;

import java.io.File;

import org.apache.commons.io.FileUtils;

import ecommander.common.exceptions.EcommanderException;
import ecommander.controllers.AppContext;

/**
 * Удаляет все файлы данного айтема и всех его сабайтемов.
 * Применяется при удалении айтема
 * @author EEEE
 * TODO <low priority> переделать для возможности использования в одной команде с удалением айтема
 */
public class DeleteItemAndSubitemsFilesUnit extends FilePersistenceCommandUnit {
	
	long itemId;
	String predecessorsPath;
	
	public DeleteItemAndSubitemsFilesUnit(long itemId, String predIdpath) {
		this.itemId = itemId;
		this.predecessorsPath = predIdpath;
	}

	public void execute() throws Exception {
		String itemFilesDirectory = createItemFileDirectoryName(itemId, predecessorsPath);
		File systemItemFilesFolder = new File(AppContext.getFilesDirPath() + itemFilesDirectory);
		/* Временно директория просто удаляется TODO <low priority>
		boolean success = systemItemFilesFolder.renameTo(new File(TEMP_FOLDER + itemFilesDirectory));
		*/
		if (systemItemFilesFolder.exists()) {
			if (!FileUtils.deleteQuietly(systemItemFilesFolder))
				throw new EcommanderException("Files from '" + AppContext.getFilesDirPath() + itemFilesDirectory + "' have not been moved successfully");
		}
	}

	public void rollback() throws Exception {
		/* Временно ничего не происходит TODO <low priority>
		String itemFilesDirectory = createItemFileDirectoryName(itemId);
		File systemTempFilesDirectory = new File(TEMP_FOLDER + itemFilesDirectory);
		boolean success = systemTempFilesDirectory.renameTo(new File(FILES_FOLDER + itemFilesDirectory));
		if (!success) {
			throw new Exception("Files from '" + TEMP_FOLDER + itemFilesDirectory + "' have not been moved successfully");
		}
		*/
	}

}