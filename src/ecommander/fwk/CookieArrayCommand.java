package ecommander.fwk;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Устанавливает (добавляет и удаляет) значения в переменные cookie
 * Created by E on 14/3/2018.
 */
public class CookieArrayCommand extends Command {

	public static final String VAR_NAME_VAR = "update_cookie_name";
	public static final String VAR_VALUE_VAR = "update_cookie_value";

	public CookieArrayCommand(Command outer) {
		super(outer);
	}

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}

	public ResultPE add() throws EcommanderException {
		String varName = getVarSingleValue(VAR_NAME_VAR);
		if (StringUtils.isBlank(varName))
			return getResult("success");
		String varValue = getVarSingleValue(VAR_VALUE_VAR);
		List<Object> values = getCookieVarValues(varName);
		values.add(varValue);
		setCookieVariable(varName, values.toArray(new Object[0]));
		return getResult("success");
	}

	public ResultPE remove() throws EcommanderException {
		String varName = getVarSingleValue(VAR_NAME_VAR);
		if (StringUtils.isBlank(varName))
			return getResult("success");
		String varValue = getVarSingleValue(VAR_VALUE_VAR);
		List<Object> values = getCookieVarValues(varName);
		Iterator<Object> valuesIter = values.iterator();
		while (valuesIter.hasNext()) {
			String value = valuesIter.next().toString();
			if (StringUtils.equalsIgnoreCase(varValue, value))
				valuesIter.remove();
		}
		setCookieVariable(varName, values.toArray(new Object[0]));
		return getResult("success");
	}

	public ResultPE clear() throws EcommanderException {
		String varName = getVarSingleValue(VAR_NAME_VAR);
		if (StringUtils.isBlank(varName))
			return getResult("success");
		setCookieVariable(varName, null);
		return getResult("success");
	}
}
