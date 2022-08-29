package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

public class AddTodayVariable extends Command {
	@Override
	public ResultPE execute() throws Exception {
		String dayStr = getVarSingleValue("$day");
		if (StringUtils.isNotBlank(dayStr)) {
			setPageVariable("today", StringUtils.substringBeforeLast(dayStr, ".") + ".0000");
		}
		return null;
	}
}
