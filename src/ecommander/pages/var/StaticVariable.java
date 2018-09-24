package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Статическая переменная. Хранит неизменный набор значений.
 * Такой может быть, например, переменная, представляющая параметр запроса
 * Created by E on 9/6/2017.
 */
public class StaticVariable extends Variable {

	private ArrayList<Object> values = new ArrayList<>(5);

	public StaticVariable(String name, Object... values) {
		super(null, name);
		if (values != null) {
			for (Object value : values) {
				if (value != null && StringUtils.isNotBlank(value.toString()))
					this.values.add(value);
			}
		}
	}

	/**
	 * Добавить значение к массиву значений переменной (в случае множества значений)
	 * @param value
	 */
	public void addValue(Object value) {
		if (StringUtils.isNotBlank(value.toString()) && !values.contains(value))
			values.add(value);
	}

	protected final void clean() {
		values = new ArrayList<>(5);
	}

	/**
	 * Поменять значения переменной
	 * @param variable
	 */
	public void update(Variable variable) {
		values = new ArrayList<>(5);
		for (Object val : variable.getAllValues()) {
			addValue(val);
		}
	}

	@Override
	public ArrayList<Object> getAllValues() {
		return values;
	}

	@Override
	public Object getSingleValue() {
		return getSingleLocalValue();
	}

	@Override
	public Variable getInited(ExecutablePagePE parentPage) {
		return this;
	}

	@Override
	public ArrayList<String> getLocalValues() {
		ArrayList<String> result = new ArrayList<>(values.size());
		for (Object value : values) {
			result.add(value.toString());
		}
		return result;
	}

	@Override
	public String getSingleLocalValue() {
		if (values.size() > 0)
			return values.get(0).toString();
		return null;
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public void removeValue(Object value) {
		values.remove(value);
	}
}
