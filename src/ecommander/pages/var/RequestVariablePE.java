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

	public RequestVariablePE(String varName, Scope scope, Style style, String... defaultValue) {
		super(varName, style);
		this.scope = scope;
		if (defaultValue.length > 0) {
			this.defaultValue = defaultValue[0];
		}
	}

	public RequestVariablePE(String varName, String varValue) {
		this(varName, Scope.request, Style.query);
		setValue(varValue);
	}

	/**
	 * Установить новое значение
	 * @param value
	 */
	public void setValue(String value) {
		if (var == null) {
			var = new StaticVariable(name, value);
		} else {
			var.clean();
			var.addValue(value);
		}
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	protected Variable getVariable() {
		return var;
	}

	@Override
	public boolean isEmpty() {
		return var.isEmpty();
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		RequestVariablePE clone = new RequestVariablePE(name, scope, style, defaultValue);
		if (scope == Scope.cookie) {
			clone.var = new CookieStaticVariable(parentPage, name);
		} else if (scope == Scope.session) {
			clone.var = new SessionStaticVariable(parentPage, name);
		} else {
			clone.var = new StaticVariable(name);
		}
		return clone;
	}
}
