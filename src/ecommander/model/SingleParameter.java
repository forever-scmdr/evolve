package ecommander.model;

import java.util.ArrayList;

/**
 * Одиночный параметр
 * @author EEEE
 *
 */
public class SingleParameter extends Parameter {
	
	private Object value = null;
	private ArrayList<Object> oldValues = null;
	
	public SingleParameter(ParameterDescription desc) {
		super(desc);
	}
	/**
	 * Установить готовое (уже правильное) значение
	 * @param value
	 */
	public final boolean setValue(Object value) {
		if (containsValue(value))
			return false;
		storeOldValue();
		this.value = value;
		return true;
	}
	/**
	 * Установить значение, полученное из интерфейса пользователя в форме строки
	 * @param value
	 */
	public final boolean createAndSetValue(String value, boolean isConsistent) {
		Object val = createTypeDependentValue(value);
		if (!isConsistent) {
			if (containsValue(val))
				return false;
			storeOldValue();
		}
		this.value = val;
		return true;
	}
	/**
	 * Вернуть старые значения параметра.
	 * Старые значения, это значения, которые были до последней установки значения.
	 * Если значение параметра устанавливалось несколько раз, то старых значений
	 * будет тоже несколько
	 * @return
	 */
	public ArrayList<Object> getOldValues() {
		if (oldValues == null)
			return new ArrayList<>(0);
		return oldValues;
	}

	/**
	 * Проверяет, изменилось ли значение параметра после загрузки
	 * Если параметр был пустым вначале, а потом получил значение, то
	 * считается что параметр изменился
	 * @return
	 */
	public boolean hasChanged() {
		return oldValues != null && !containsValue(oldValues.get(0));
	}

	public final Object getValue() {
		return value;
	}

	@Override
	public void clear() {
		setValue(null);
	}

	public String outputValue() {
		return desc.getDataType().outputValue(value, desc.getFormatter());
	}
	
	public final boolean isMultiple() {
		return false;
	}
	
	private void storeOldValue() {
		if (oldValues == null)
			oldValues = new ArrayList<>();
		oldValues.add(value);
	}

	@Override
	public final boolean isEmpty() {
		return (value == null || value.toString().length() == 0);
	}

	@Override
	public final String toString() {
		return getName() + ": " + getValue();
	}
	@Override
	public final boolean containsValue(Object value) {
		return (value != null && value.equals(this.value)) || (value == null && this.value == null);
	}
	@Override
	public final boolean equals(Object obj) {
		return (value == null && ((SingleParameter)obj).value == null) ||
				value != null && value.equals(((SingleParameter)obj).value);
	}
	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

}