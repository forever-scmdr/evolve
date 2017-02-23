package ecommander.pages.variables;

import ecommander.pages.ExecutablePagePE;

/**
 * Переменная, которая сохраняет свое значение в сеансе
 * Если при создании объекта было задано значение (значение было не пустым), оно сохранится в сеансе при вызове метода setPageModel()
 * Если при создании объекта значение было пустой строкой "", то значение удалится из сеанса при вызове метода setPageModel()
 * Если при создании объекта значение было null, то значение установится из сеанса при вызове метода setPageModel()
 * @author EEEE
 */
public class SessionStaticVariablePE extends StaticVariablePE {
	
	public SessionStaticVariablePE(String varId, String value) {
		super(varId, value);
	}

	protected SessionStaticVariablePE(SessionStaticVariablePE source, ExecutablePagePE parentPage) {
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
		pageModel.getSessionContext().setVariableValue(name, variable.output());
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		SessionStaticVariablePE clone = new SessionStaticVariablePE(this, parentPage);
		String savedValue = parentPage.getSessionContext().getVariableValue(name);
		if (savedValue != null)
			clone.reset(savedValue);
		return clone;
	}

	
}