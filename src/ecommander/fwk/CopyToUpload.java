package ecommander.fwk;

/**
 * Created by user on 12.03.2019.
 */

import ecommander.controllers.AppContext;
import ecommander.filesystem.SingleItemDirectoryFileUnit;
import ecommander.model.Item;
import ecommander.model.Parameter;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Iterator;

public class CopyToUpload implements ItemEventCommandFactory, DBConstants.ItemTbl {
	public CopyToUpload() {
	}

	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new CopyToUpload.CopyFilePCU(item);
	}

	public static class CopyFilePCU extends SingleItemDirectoryFileUnit {
		private static final String FILE_PARAM_NAME = "big_integration";
		private static final String HASH_PARAM_NAME = "file_hash";
		private TransactionContext transaction;

		public CopyFilePCU(Item item) {
			super(item);
		}

		public void execute() throws Exception {
			if(item.getItemType().getParameter(FILE_PARAM_NAME) == null) return;
			Parameter param = this.item.getParameterByName(FILE_PARAM_NAME);
			if(param != null) {
				if(param.hasChanged() && !param.isEmpty()) {
					File srcFile = new File(this.createItemDirectoryName() + "/" + this.item.getValue(param.getParamId()));
					if(!srcFile.exists()) {
						return;
					}

					Path contextPath = Paths.get(AppContext.getContextPath(), new String[]{"upload"});
					if(!contextPath.toFile().exists()) {
						Files.createDirectory(contextPath, new FileAttribute[0]);
					} else {
						Collection destFile = FileUtils.listFiles(contextPath.toFile(), new String[]{srcFile.getName()}, false);
						Iterator var6 = destFile.iterator();

						while(var6.hasNext()) {
							File file = (File)var6.next();
							FileUtils.deleteQuietly(file);
						}
					}

					File destFile1 = Paths.get(AppContext.getContextPath(), new String[]{"upload", this.item.getValue(param.getParamId()).toString()}).toFile();
					FileUtils.copyFile(srcFile, destFile1);
				}

			}

			if(item.getItemType().getParameter(HASH_PARAM_NAME) != null) {
				if (param.hasChanged() && !param.isEmpty()) {
					File srcFile = new File(this.createItemDirectoryName() + "/" + item.getValue(param.getParamId()));
					item.setValue(HASH_PARAM_NAME, srcFile.hashCode());
				} else if (!item.getFileValue(FILE_PARAM_NAME, AppContext.getFilesDirPath(item.isFileProtected())).isFile()) {
					item.clearValue(HASH_PARAM_NAME);
				}
			}
			// Апдейт базы данных (сохранение новых параметров айтема)
			if (item.hasChanged()) {
				Connection conn = getTransactionContext().getConnection();
				// Сохранить новое ключевое значение и параметры в основную таблицу
				String sql = "UPDATE " + ITEM_TBL + " SET " + I_KEY + "=?, " + I_T_KEY + "=?, " + I_PARAMS + "=?, "
						+ I_UPDATED + "=NULL WHERE " + I_ID + "=" + item.getId();
				try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
					pstmt.setString(1, item.getKey());
					pstmt.setString(2, item.getKeyUnique());
					pstmt.setString(3, item.outputValues());
					pstmt.executeUpdate();
					pstmt.close();

					// Выполнить запросы для сохранения параметров
					ItemMapper.insertItemParametersToIndex(item, ItemMapper.Mode.UPDATE, getTransactionContext());
				}
			}
		}

		public void rollback() throws Exception {
		}

		@Override
		public TransactionContext getTransactionContext() {
			return transaction;
		}
		@Override
		public void setTransactionContext(TransactionContext context) {
			this.transaction = context;
		}
	}
}
