package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class LoggerFinish extends Command {

	private static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss dd.MM.yy").withZoneUTC().withPivotYear(2050);
	private static final String LOG_FILE = "scheduled.log";
	
	@Override
	public ResultPE execute() throws Exception {
		DateTime now = DateTime.now(DateTimeZone.UTC);
		File file = new File(AppContext.getFilesDirPath(false) + LOG_FILE);
		FileUtils.write(file, "Finished: " + DATE_FORMATTER.print(now) + "\n", StandardCharsets.UTF_8, true);
		return null;
	}

}
