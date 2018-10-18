package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;

/**
 * Переменная, которая поступила как параметр запроса (находится в элементе <variables></variables>)
 * Если имя переменной начинается с символа - (минус), то значение этой переменной должно удаляться
 * из переменной с именем без минуса
 * Created by E on 9/6/2017.
 */
public class RequestVariablePE extends VariablePE {

	private static final String REQUEST_VARIABLE = "request_variable";

	public enum Scope {
		request, session, cookie;
		public static Scope getValue(String val) {
			if (StringUtils.isBlank(val) || StringUtils.equalsIgnoreCase("single", val))
				return request;
			if (StringUtils.equalsIgnoreCase("session", val))
				return session;
			if (StringUtils.equalsIgnoreCase("cookie", val))
				return cookie;
			throw new IllegalArgumentException("there is no Request Variable Scope value for '" + val + "' string");
		}
	}

	protected StaticVariable var;
	private String defaultValue = null;
	private Scope scope;
	private boolean isNegative; // если переменная негативная, то ее значение должно удаляться из переменной с таким же именем, но без минуса впереди

	public RequestVariablePE(String varName, Scope scope, Style style, String... defaultValue) {
		super(varName, style);
		this.scope = scope;
		if (defaultValue != null && defaultValue.length > 0) {
			this.defaultValue = defaultValue[0];
		}
		this.isNegative = varName != null && !varName.isEmpty() && varName.charAt(0) == VariablePE.NEGATIVE;
	}

	public RequestVariablePE(String varName, String varValue) {
		this(varName, Scope.request, Style.query);
		resetValue(varValue);
	}

	/**
	 * Установить новое значение
	 * @param value
	 */
	public void resetValue(String value) {
		if (var == null) {
			var = new StaticVariable(name, value);
		} else {
			var.clean();
			var.addValue(value);
		}
	}

	/**
	 * Добавить значение к переменной
	 * @param value
	 */
	public void addValue(String value) {
		if (var == null) {
			var = new StaticVariable(name, value);
		} else {
			var.addValue(value);
		}
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public Variable getVariable() {
		return var;
	}

	@Override
	public boolean isEmpty() {
		return var == null || var.isEmpty();
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		RequestVariablePE clone = new RequestVariablePE(name, scope, style, defaultValue);
		if (scope == Scope.cookie) {
			clone.var = new CookieStaticVariable(parentPage, name);
			//((CookieStaticVariable)clone.var).restore();
		} else if (scope == Scope.session) {
			clone.var = new SessionStaticVariable(parentPage, name);
			//((SessionStaticVariable)clone.var).restore();
		} else {
			clone.var = new StaticVariable(name);
		}
		if (!isEmpty()) {
			for (Object val : var.getAllValues()) {
				clone.var.addValue(val);
			}
		}
		return clone;
	}

	@Override
	public String getElementName() {
		return REQUEST_VARIABLE;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public boolean hasDefaultValue() {
		return StringUtils.isNotBlank(defaultValue);
	}

	/**
	 * Является ли переменная негативной (т.е. значение которой нужно удалять, а не добавлять)
	 * @return
	 */
	public boolean isNegative() {
		return isNegative;
	}

	/**
	 * Получить имя переменной, из которой надо удалять значение, в случае если переменная негативна
	 * @return
	 */
	public String getPositiveName() {
		if (isNegative) {
			return name.substring(1);
		} else {
			return name;
		}
	}
}
