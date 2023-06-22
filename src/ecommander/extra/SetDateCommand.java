package ecommander.extra;

import java.util.Calendar;
import java.util.Date;

import ecommander.model.datatypes.DateDataType;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;

public class SetDateCommand extends Command {
	private static final String DATE_VAR_NAME = "date";
	private static final String START_DATE_VAR_NAME = "start_date";
	
	@Override
	public ResultPE execute() throws Exception {
		String dateStr = getVarSingleValueDefault(DATE_VAR_NAME, null);
		Long endDate = DateDataType.parseDate(dateStr);
		if (endDate == null) {
			Calendar dayAgo = Calendar.getInstance();
			dayAgo.setTime(new Date());
			dayAgo.add(Calendar.DATE, -1);
			endDate = dayAgo.getTimeInMillis();
			setPageVariable(DATE_VAR_NAME, DateDataType.outputDate(endDate.longValue(), DateDataType.DAY_FORMATTER));
		}
		String startStr = getVarSingleValueDefault(START_DATE_VAR_NAME, "");
		Long startDate = DateDataType.parseDate(startStr);
		if (startDate == null) {
			Calendar monthAgo = Calendar.getInstance();
			monthAgo.setTime(new Date(endDate));
			monthAgo.add(Calendar.MONTH, -1);
			setPageVariable(START_DATE_VAR_NAME, DateDataType.outputDate(monthAgo.getTimeInMillis(), DateDataType.DAY_FORMATTER));
		}
		return null;
	}

}
