package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by E on 21/9/2018.
 */
public class CreateQuartals extends Command {
	@Override
	public ResultPE execute() throws Exception {
		String mFromStr = getVarSingleValue("m_from");
		String mToStr = getVarSingleValue("m_to");
		if (StringUtils.isNoneBlank(mFromStr, mToStr)) {
			DateTime from = new DateTime(Long.parseLong(mFromStr), DateTimeZone.UTC);
			DateTime to = new DateTime(Long.parseLong(mToStr), DateTimeZone.UTC);
			StringBuilder string = new StringBuilder();
			while (!from.isAfter(to)) {
				addDate(string, from);
				from = from.plusMonths(3);
			}
			setPageVariable("quartals", string.substring(1).toString());
		}
		return null;
	}

	private void addDate(StringBuilder builder, DateTime date) {
		builder.append('!').append((date.monthOfYear().get() - 1) / 3 + 1).append('*').append(date.year().get());
	}
}
