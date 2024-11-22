package ecommander.fwk;

/**
 * Created by user on 12.03.2019.
 */

import ecommander.controllers.AppContext;
import ecommander.filesystem.SingleItemDirectoryFileUnit;
import ecommander.model.Item;
import ecommander.model.Parameter;
import ecommander.persistence.common.PersistenceCommandUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Iterator;

public class CopyToUpload implements ItemEventCommandFactory {
	public CopyToUpload() {
	}

	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new CopyToUpload.CopyFilePCU(item);
	}

	public static class CopyFilePCU extends SingleItemDirectoryFileUnit {
		private static final String PARAM_NAME = "big_integration";

		public CopyFilePCU(Item item) {
			super(item);
		}

		public void execute() throws Exception {
			Parameter param = this.item.getParameterByName(PARAM_NAME);
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
						Collection destFile = FileUtils.listFiles(contextPath.toFile(), new String[]{"xls", "xlsx"}, false);
						Iterator var6 = destFile.iterator();

						while(var6.hasNext()) {
							File file = (File)var6.next();
							FileUtils.deleteQuietly(file);
						}
					}

					File destFile1 = Paths.get(AppContext.getContextPath(), new String[]{"upload", this.item.getValue(param.getParamId()).toString()}).toFile();
					FileUtils.copyFile(srcFile, destFile1);
					if(SystemUtils.IS_OS_LINUX){
						Path ecXml = Paths.get(AppContext.getContextPath(),new String[]{"upload", this.item.getValue(param.getParamId()).toString()});
						Runtime.getRuntime().exec(new String[]{"chmod", "775", ecXml.toAbsolutePath().toString()});
					}
				}

			}
		}

		public void rollback() throws Exception {
		}
	}
}
