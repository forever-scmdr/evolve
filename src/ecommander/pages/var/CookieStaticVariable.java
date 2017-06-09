package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;

/**
 * Статическая переменная, которая хранится в куки
 * Created by E on 9/6/2017.
 */
public class CookieStaticVariable extends SessionStaticVariable {
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
		String strValue = variable.isEmpty() ? null : variable.getSingleValue().toString();
		parentPage.getSessionContext().setCookie(name, strValue);
	}


	/**
	 * Удалить значение из сеанса
	 */
	public void remove() {
		clean();
		parentPage.getSessionContext().setCookie(name, null);
	}
}
