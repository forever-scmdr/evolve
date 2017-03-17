package ecommander.filesystem;

import java.io.File;

import ecommander.model.Item;
import org.apache.commons.io.FileUtils;

import ecommander.fwk.EcommanderException;
import ecommander.controllers.AppContext;

/**
 * Удаляет все файлы данного айтема и всех его сабайтемов.
 * Применяется при удалении айтема
 * @author EEEE
 * TODO <low priority> переделать для возможности использования в одной команде с удалением айтема
 */
public class DeleteItemFilesUnit extends ItemFileUnit {

	public DeleteItemFilesUnit(Item item) {
		super(item);
	}

	public void execute() throws Exception {
		File itemFilesFolder = new File(createItemFileDirectoryName());
		/* Временно директория просто удаляется TODO <low priority>
		boolean success = systemItemFilesFolder.renameTo(new File(TEMP_FOLDER + itemFilesDirectory));
		*/
		if (itemFilesFolder.exists()) {
			if (!FileUtils.deleteQuietly(itemFilesFolder))
				throw new EcommanderException("Files from '" + itemFilesFolder + "' have not been moved successfully");
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