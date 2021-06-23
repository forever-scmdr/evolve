package extra;

import ecommander.controllers.AppContext;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class ShedTestCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		Path log = Paths.get(AppContext.getContextPath(), getPageName()+"_log.txt");
		if(Files.notExists(log)){Files.createFile(log);}
		Files.write(log, ("\nstart: "+ new Date()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		return null;
	}
}
