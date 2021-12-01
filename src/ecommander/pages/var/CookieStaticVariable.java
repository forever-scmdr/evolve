package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;

/**
 * Статическая переменная, которая хранится в куки
 * Created by E on 9/6/2017.
 */
public class CookieStaticVariable extends SessionStaticVariable {

	private static final String VALUE_SEP = "~~~";

	public CookieStaticVariable(ExecutablePagePE parentPage, String name, Object... values) {
		super(parentPage, name, values);
	}

	@Override
	public void update(Variable variable) {
		name = variable.getName();
		clean();
		for (Object value : variable.getAllValues()) {
			this.addValue(value);
		}
		String strValue = variable.isEmpty() ? null : StringUtils.join(variable.getAllValues(), VALUE_SEP);
		parentPage.getSessionContext().setCookie(name, strValue);
	}

	@Override
	public void restore() {
		String savedValue = parentPage.getSessionContext().getCookie(name);
		if (StringUtils.isNotBlank(savedValue)) {
			String[] values = StringUtils.split(savedValue, VALUE_SEP);
			for (String value : values) {
				addValue(value);
			}
		}
	}

	/**
	 * Вернуть значение куки в виде, в котором оно будет храниться у пользователя
	 * @return
	 */
	public String getCookiePlain() {
		return parentPage.getSessionContext().getCookie(name);
	}

	/**
	 * Удалить значение из сеанса
	 */
	public void remove() {
		clean();
		parentPage.getSessionContext().setCookie(name, null);
	}

	@Override
	public void removeValue(Object value) {
		if (isEmpty())
			restore();
		if (!isEmpty()) {
			getAllValues().remove(value);
		}
		if (isEmpty()) {
			remove();
		} else {
			parentPage.getSessionContext().setCookie(name, getSingleValue().toString());
		}
	}
}
