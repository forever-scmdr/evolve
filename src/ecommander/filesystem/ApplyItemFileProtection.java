package ecommander.filesystem;

import ecommander.controllers.AppContext;

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
			moveItemDirectory(AppContext.getCommonFilesDirPath(), AppContext.getProtectedFilesDirPath(), itemIds);
		else
			moveItemDirectory(AppContext.getProtectedFilesDirPath(), AppContext.getCommonFilesDirPath(), itemIds);
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