package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;

/**
 * Переменная, которая используется в ссылках (элементах <link/>)
 * Created by E on 9/6/2017.
 */
public class LinkVariablePE extends VariablePE {

	public LinkVariablePE(String varName) {
		super(varName);
	}

	protected LinkVariablePE(VariablePE var, ExecutablePagePE parentPage) {
		super(var, parentPage);
	}

	@Override
	protected Variable getVariable() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		return null;
	}
}
