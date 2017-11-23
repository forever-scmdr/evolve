package ecommander.filesystem;

import ecommander.controllers.AppContext;
import ecommander.fwk.ServerLogger;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.PreparedStatement;

/**
 * Перемещает директорию айтема в новое место в зависимости от измененного статуса айтема или зещищенности его файлов
 * @author EEEE
 */
public class ApplyItemFileProtection extends ItemDirectoryCommandUnit implements DBConstants.ItemTbl {

	private long[] itemIds;
	private boolean makeProtected;

	public ApplyItemFileProtection(boolean makeProtected, long...itemIds) {
		this.itemIds = itemIds;
		this.makeProtected = makeProtected;
	}

	public void execute() throws Exception {
		if (makeProtected)
			moveItemDirectory(AppContext.getCommonFilesDirPath(), AppContext.getProtectedFilesDirPath(), itemIds);
		else
			moveItemDirectory(AppContext.getProtectedFilesDirPath(), AppContext.getCommonFilesDirPath(), itemIds);
		TemplateQuery query = new TemplateQuery("protect files");
		query.UPDATE(ITEM_TBL).SET().col(I_PROTECTED).byte_(makeProtected ? (byte) 1 : (byte) 0)
				.WHERE().col_IN(I_PROTECTED).longIN(ArrayUtils.toObject(itemIds));
		try (PreparedStatement pstmt = query.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		} catch (Exception e) {
			ServerLogger.error("Unable to protect/unprotect files", e);
			if (makeProtected)
				moveItemDirectory(AppContext.getProtectedFilesDirPath(), AppContext.getCommonFilesDirPath(), itemIds);
			else
				moveItemDirectory(AppContext.getCommonFilesDirPath(), AppContext.getProtectedFilesDirPath(), itemIds);
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