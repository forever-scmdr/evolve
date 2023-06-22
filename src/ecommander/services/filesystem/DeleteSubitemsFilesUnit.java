package ecommander.services.filesystem;

import java.io.File;
import java.io.IOException;

import ecommander.common.FileUtilz;
import ecommander.common.exceptions.EcommanderException;
import ecommander.controllers.AppContext;

/**
 * Удаляет все файлы сабайтемов данного айтема.
 * Применяется при удалении айтема сабайтемов айтема
 * @author EEEE
 * TODO <low priority> переделать для возможности использования в одной команде с удалением айтема
 */
public class DeleteSubitemsFilesUnit extends FilePersistenceCommandUnit {
	
	long itemId;
	String predecessorsPath;
	
	public DeleteSubitemsFilesUnit(long itemId, String predIdpath) {
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
			try {
				FileUtilz.deleteSubdirs(systemItemFilesFolder);
			} catch (IOException e) {
				throw new EcommanderException("Files from '" + AppContext.getFilesDirPath() + itemFilesDirectory + "' have not been moved successfully");
			}
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