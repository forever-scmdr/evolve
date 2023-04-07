package extra;

import ecommander.controllers.AppContext;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public class CheckScheduleCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		LocalDateTime now = LocalDateTime.now();
		Path p = Paths.get(AppContext.getContextPath(), "check_schedule.txt");
		Files.write(p, (now + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND );
		return null;
	}
}
