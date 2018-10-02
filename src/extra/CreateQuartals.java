package extra;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by E on 21/9/2018.
 */
public class CreateQuartals extends Command implements ItemNames{
	@Override
	public ResultPE execute() throws Exception {
		String mFromStr = getVarSingleValue("m_from");
		String mToStr = getVarSingleValue("m_to");
		Item oldestReport = new ItemQuery(SALE).addSorting(sale_.REGISTER_DATE, "ASC").setLimit(1).loadFirstItem();
		Item newestReport = new ItemQuery(SALE).addSorting(sale_.REGISTER_DATE, "DESC").setLimit(1).loadFirstItem();
		if (StringUtils.isNoneBlank(mFromStr, mToStr)) {
			DateTime from = new DateTime(Long.parseLong(mFromStr), DateTimeZone.UTC);
			DateTime to = new DateTime(Long.parseLong(mToStr), DateTimeZone.UTC);
			DateTime oldest = null;
			if (oldestReport != null)
				oldest = new DateTime(oldestReport.getLongValue(sale_.REGISTER_DATE), DateTimeZone.UTC);
			DateTime newest = null;
			if (newestReport != null)
				newest = new DateTime(newestReport.getLongValue(sale_.REGISTER_DATE), DateTimeZone.UTC);
			if (oldest != null && from.plusMonths(5).isBefore(oldest)) {
				from = oldest.minusMonths(5);
			}
			if (newest != null && to.minusMonths(5).isAfter(newest)) {
				to = newest.plusMonths(5);
			}
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
