package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;

/**
 * Переменная, которая поступила как параметр запроса (находится в элементе <variables></variables>)
 * Created by E on 9/6/2017.
 */
public class RequestVariablePE extends VariablePE {

	public enum Scope {
		request, session, cookie
	}

	private StaticVariable var;
	private String defaultValue = null;
	private Scope scope;

	public RequestVariablePE(String varName, Scope scope, String defaultValue) {
		super(varName);
		this.scope = scope;
		if (StringUtils.isNotBlank(defaultValue))
			this.defaultValue = defaultValue;
	}

	public void update(Variable var) {
		this.var.update(var);
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
		RequestVariablePE clone = new RequestVariablePE(name, scope, defaultValue);
		if (scope == Scope.cookie) {
			clone.var = new CookieStaticVariable(parentPage, name);
		} else if (scope == Scope.session) {
			clone.var = new SessionStaticVariable(parentPage, name);
		} else {
			clone.var = new StaticVariable(parentPage, name);
		}
		return clone;
	}
}
