package ecommander.filesystem;

import java.io.File;
import java.io.IOException;

import ecommander.fwk.EcommanderException;
import ecommander.controllers.AppContext;
import org.apache.commons.io.FileUtils;

/**
 * Удаляет все файлы сабайтемов данного айтема.
 * Применяется при удалении айтема сабайтемов айтема
 * @author EEEE
 */
public class DeleteItemsDirectoriesUnit extends ItemDirectoryCommandUnit {
	
	Long[] itemIds;

	public DeleteItemsDirectoriesUnit(Long...itemIds) {
		this.itemIds = itemIds;
	}

	public void execute() throws Exception {
		for (long itemId : itemIds) {
			boolean deleted = deleteItemDirectory(AppContext.getFilesDirPath(), itemId);
			if (!deleted)
				deleteItemDirectory(AppContext.getProtectedDirPath(), itemId);
		}
	}

	public void rollback() throws Exception {
		/* Временно ничего не происходит TODO <low priority>
		String itemFilesDirectory = createItemFilesDirectoryName(itemId);
		File systemTempFilesDirectory = new File(TEMP_FOLDER + itemFilesDirectory);
		boolean success = systemTempFilesDirectory.renameTo(new File(FILES_FOLDER + itemFilesDirectory));
		if (!success) {
			throw new Exception("Files from '" + TEMP_FOLDER + itemFilesDirectory + "' have not been moved successfully");
		}
		*/
	}

}