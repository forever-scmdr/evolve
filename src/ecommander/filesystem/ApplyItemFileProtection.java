package ecommander.filesystem;

import java.io.File;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import org.apache.commons.io.FileUtils;

import ecommander.fwk.EcommanderException;

/**
 * Перемещает директорию айтема в новое место в зависимости от измененного статуса айтема или зещищенности его файлов
 * @author EEEE
 */
public class ApplyItemFileProtection extends ItemDirectoryCommandUnit {

	long[] itemIds;
	boolean makeProtected;

	public ApplyItemFileProtection(boolean makeProtected, long...itemIds) {
		this.itemIds = itemIds;
		this.makeProtected = makeProtected;
	}

	public void execute() throws Exception {
		if (makeProtected)
			moveItemDirectory(AppContext.getFilesDirPath(), AppContext.getProtectedDirPath(), itemIds);
		else
			moveItemDirectory(AppContext.getProtectedDirPath(), AppContext.getFilesDirPath(), itemIds);
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