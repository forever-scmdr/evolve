package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;

/**
 * Статическая переменная, которая хранится в сеансе
 * Created by E on 9/6/2017.
 */
public class SessionStaticVariable extends StaticVariable {
	public SessionStaticVariable(ExecutablePagePE parentPage, String name, Object... values) {
		super(name, values);
		this.parentPage = parentPage;
	}

	@Override
	public void update(Variable variable) {
		name = variable.getName();
		clean();
		for (Object value : variable.getAllValues()) {
			this.addValue(value);
		}
		String strValue = variable.isEmpty() ? null : variable.getSingleValue().toString();
		parentPage.getSessionContext().setVariableValue(name, strValue);
	}

	public void restore() {
		String savedValue = parentPage.getSessionContext().getVariableValue(name);
		if (StringUtils.isNotBlank(savedValue))
			addValue(savedValue);
	}

	/**
	 * Удалить значение из сеанса
	 */
	public void remove() {
		clean();
		parentPage.getSessionContext().setVariableValue(name, null);
	}
}
