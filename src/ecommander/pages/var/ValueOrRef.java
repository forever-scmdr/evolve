package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ValidationResults;

import java.util.ArrayList;

/**
 * Переменная, которая может либо хранить статическое значение либо быть ссылкой на другую страничную переменную
 * Created by E on 12/6/2017.
 */
public class ValueOrRef extends Variable {

	private boolean isRef;
	private Variable var;

	private ValueOrRef(String name, boolean isRef) {
		super(null, name);
		this.isRef = isRef;
		if (!isRef)
			var = new StaticVariable(name, name);
	}

	/**
	 * Создать переменную со статическим значением
	 * @param value
	 * @return
	 */
	public static ValueOrRef newValue(String value) {
		return new ValueOrRef(value, false);
	}

	/**
	 * Создать ссылку на другую переменную
	 * @param refName
	 * @return
	 */
	public static ValueOrRef newRef(String refName) {
		return new ValueOrRef(refName, true);
	}

	public boolean isRef() {
		return isRef;
	}

	public boolean isValue() {
		return !isRef;
	}

	@Override
	public ArrayList<Object> getAllValues() {
		return var.getAllValues();
	}

	@Override
	public Object getSingleValue() {
		return var.getSingleValue();
	}

	@Override
	public Variable getInited(ExecutablePagePE parentPage) {
		ValueOrRef clone = new ValueOrRef(name, isRef);
		clone.parentPage = parentPage;
		return clone;
	}

	private void getActualVar() {
		if (isRef && var == null)
			var = parentPage.getVariable(name);
	}

	@Override
	public ArrayList<String> getLocalValues() {
		getActualVar();
		return var.getLocalValues();
	}

	@Override
	public String getSingleLocalValue() {
		getActualVar();
		return var.getSingleLocalValue();
	}

	@Override
	public boolean isEmpty() {
		getActualVar();
		return var.isEmpty();
	}

	@Override
	public void validate(String elementPath, ValidationResults results) {
		if (isRef) {
			if (!name.startsWith("$") && parentPage.getInitVariablePE(name) == null)
				results.addError(elementPath, "there is no '" + name + "' page variable in current page");
		}
	}

	@Override
	public void removeValue(Object value) {
		getActualVar();
		var.removeValue(value);
	}
}
