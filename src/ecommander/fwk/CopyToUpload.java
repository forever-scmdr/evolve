package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.filesystem.SingleItemDirectoryFileUnit;
import ecommander.model.Item;
import ecommander.model.Parameter;
import ecommander.persistence.common.PersistenceCommandUnit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created by user on 07.03.2019.
 */
public class CopyToUpload implements ItemEventCommandFactory {

	public static class CopyFilePCU extends SingleItemDirectoryFileUnit {
		private static final String PARAM_NAME = "big_integration";

		public CopyFilePCU(Item item) {
			super(item);
		}

		@Override
		public void execute() throws Exception {
			Parameter param = item.getParameterByName("big_integration");
			if(param == null) return;
			if(param.hasChanged() && !param.isEmpty()){

				File srcFile = new File(createItemDirectoryName() + "/" + item.getValue(param.getParamId()));
				if(!srcFile.exists())return;

				Path contextPath = Paths.get(AppContext.getContextPath(), "upload");
				if(!contextPath.toFile().exists()){
					Files.createDirectory(contextPath);
				}else {
					Collection<File> files = FileUtils.listFiles(contextPath.toFile(), new String[]{"xls", "xlsx"}, false);
					for(File file : files){
						FileUtils.deleteQuietly(file);
					}
				}
				File destFile = Paths.get(AppContext.getContextPath(), "upload", item.getValue(param.getParamId()).toString()).toFile();
				FileUtils.copyFile(srcFile, destFile);
			}

		}

		@Override
		public void rollback() throws Exception {}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {return new CopyFilePCU(item);}
}
