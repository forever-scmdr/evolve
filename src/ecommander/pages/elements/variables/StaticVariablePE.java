package ecommander.pages.elements.variables;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.ValidationResults;

/**
 * Статическая переменная. Значение задается сразу.
 * Может содержать не только одно, но и несколько значений.
 * Первое значение задается в конструкторе, последующие значения добавляются посредством вызова соответствующего метода
 * @author EEEE
 */
public class StaticVariablePE extends VariablePE {
	public static final String ELEMENT_NAME = "static-var";
	
	private ArrayList<String> values = new ArrayList<String>(5);
	
	public StaticVariablePE(String varId, String value) {
		super(varId);
		if (!StringUtils.isEmpty(value))
			values.add(value);
	}
	
	protected StaticVariablePE(StaticVariablePE source, ExecutablePagePE parentPage) {
		super(source, parentPage);
		this.values = new ArrayList<String>(source.values);
	}
	/**
	 * Добавить значение к массиву значений переменной (в случае множества значений)
	 * @param value
	 */
	public void addValue(String value) {
		if (!StringUtils.isEmpty(value))
			values.add(value);
	}
	/**
	 * Перезаписать значение переменной
	 * @param value
	 */
	protected final void reset(String value) {
		values = new ArrayList<String>(5);
		values.add(value);
	}
	
	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		return new StaticVariablePE(this, parentPage);
	}

	protected final String getSingleValue() {
		if (values.size() != 0)
			return values.get(0);
		return Strings.EMPTY;
	}
	
	public void validate(String elementPath, ValidationResults results) {
		// Ничего не делать
	}

	@Override
	public String output() {
		if (values.size() == 1)
			return values.get(0);
		if (values.size() == 0)
			return Strings.EMPTY;
		return StringUtils.join(values, ",");
	}

	@Override
	public List<String> outputArray() {
		return values;
	}

	@Override
	public final boolean isMultiple() {
		return values.size() > 1;
	}
	
	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}
	
}