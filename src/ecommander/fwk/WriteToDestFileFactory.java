package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.filesystem.ItemDirectoryCommandUnit;
import ecommander.model.Item;
import ecommander.model.ParameterDescription;
import ecommander.model.datatypes.DataType.Type;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by user on 16.05.2018.
 */
public class WriteToDestFileFactory implements ItemEventCommandFactory, DBConstants.ItemTbl {

	private static final String SRC_PARAM = "src";
	private static final String DEST = "dest";

	public static class SingleWriteToDestFile implements PersistenceCommandUnit {
		private TransactionContext transaction;
		private String format;
		private Item item;

		public SingleWriteToDestFile(Item item) {
			this.item = item;
			format = null;
		}

		public SingleWriteToDestFile(Item item, String format) {
			this.item = item;
			this.format = format;
		}


		@Override
		public TransactionContext getTransactionContext() {
			return transaction;
		}

		@Override
		public void setTransactionContext(TransactionContext context) {
			this.transaction = context;
		}

		public void execute() throws Exception {
			for (ParameterDescription param : item.getItemType().getParameterList()) {
				if ((param.getType() == Type.PLAIN_TEXT || param.getType() == Type.FILE) && (param.hasFormat() || format != null)) {
					String currentFormat = format;
					if (param.hasFormat()) currentFormat = param.getFormat();
					if (StringUtils.isBlank(currentFormat)) return;
					String destFile = null;
					String[] tmp = StringUtils.split(currentFormat, ';');
					for (String s : tmp) {
						if (StringUtils.startsWith(s, SRC_PARAM)) {
							String value = StringUtils.substringAfter(s, ":");
							destFile = item.getStringValue(value.trim());
							break;
						}
						if (StringUtils.startsWith(s, DEST) && StringUtils.isBlank(destFile)) {
							destFile = StringUtils.substringAfter(s, ":").trim();
						}
					}
					if (StringUtils.isNotBlank(destFile)) {
						destFile = AppContext.getContextPath() + destFile;
						if (param.getType() == Type.PLAIN_TEXT) {
							String fileContent = item.getStringValue(param.getName());
							if (StringUtils.isNotBlank(fileContent)) {
								Files.write(Paths.get(destFile), fileContent.getBytes("UTF-8"));
							}
						}else if(param.getType() == Type.FILE){
							File srcFile = item.getFileValue(param.getName(), AppContext.getFilesDirPath(false));
							if(!srcFile.exists() || srcFile.isDirectory()) return;
							Files.copy(srcFile.toPath(), Paths.get(destFile), StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
		}

		@Override
		public void rollback() throws Exception {

		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new SingleWriteToDestFile(item);
	}
}
