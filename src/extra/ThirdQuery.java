package extra;

import org.apache.commons.lang3.StringUtils;

public class ThirdQuery extends SecondQuery {
	@Override
	protected String prepareSingleTermStr(String input) {
		return StringUtils.substring(input, 0, 8);
	}
}
