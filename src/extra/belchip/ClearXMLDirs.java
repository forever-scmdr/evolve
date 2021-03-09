package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class ClearXMLDirs extends Command {

	@Override
	public ResultPE execute() throws Exception {
		String jurFolder = AppContext.getRealPath("WEB-INF/" + getVarSingleValueDefault("jur_folder", "jur"));
		String physFolder = AppContext.getRealPath("WEB-INF/" + getVarSingleValueDefault("phys_folder", "phys"));
		File jurDir = new File(jurFolder);
		File physDir = new File(physFolder);
		if (jurDir.exists())
			FileUtils.cleanDirectory(jurDir);
		if (physDir.exists())
			FileUtils.cleanDirectory(physDir);
		return null;
	}

}
