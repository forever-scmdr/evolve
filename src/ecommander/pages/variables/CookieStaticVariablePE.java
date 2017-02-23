package ecommander.pages.variables;

import ecommander.pages.ExecutablePagePE;

/**
 * Переменная, которая сохраняет свое значение в куки
 * @author EEEE
 */
public class CookieStaticVariablePE extends SessionStaticVariablePE {
	
	public CookieStaticVariablePE(String varId, String value) {
		super(varId, value);
	}

	private CookieStaticVariablePE(CookieStaticVariablePE source, ExecutablePagePE parentPage) {
		super(source, parentPage);
		this.pageModel = parentPage;
	}
	/**
	 * TODO <fix> добавить поддержку множественных значений переменных
	 * @param variable
	 */
	public void update(VariablePE variable) {
		this.name = variable.getName();
		this.reset(variable.output());
		pageModel.getSessionContext().setCookie(name, variable.output());
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		CookieStaticVariablePE clone = new CookieStaticVariablePE(this, parentPage);
		String savedValue = parentPage.getSessionContext().getCookie(name);
		if (savedValue != null)
			clone.reset(savedValue);
		return clone;
	}
	
}