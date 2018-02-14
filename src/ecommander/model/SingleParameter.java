package ecommander.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Одиночный параметр
 * @author EEEE
 *
 */
public class SingleParameter extends Parameter {
	
	private Object value = null;
	private ArrayList<Object> oldValues = null;
	private HashMap<String, String> metas = null;
	private int hash = 0;
	
	public SingleParameter(ParameterDescription desc, Item item) {
		super(desc, item);
	}
	/**
	 * Установить готовое (уже правильное) значение
	 * @param value
	 */
	final boolean setValue(Object value, boolean isConsistent) {
		if (containsValue(value))
			return false;
		storeOldValue();
		performSetValue(value, isConsistent);
		return true;
	}
	/**
	 * Установить значение, полученное из интерфейса пользователя в форме строки
	 * @param value
	 */
	public final SingleParameter createAndSetValue(String value, boolean isConsistent) {
		Object val = createTypeDependentValue(value);
		if (!isConsistent) {
			if (containsValue(val))
				return this;
			storeOldValue();
		}
		performSetValue(val, isConsistent);
		return this;
	}

	/**
	 * Установить значение и если нужно сгенерировать метаданные
	 * @param val
	 * @param isConsistent
	 */
	private void performSetValue(Object val, boolean isConsistent) {
		this.value = val;
		this.hash = desc.getDataType().getHashCode(val);
		if (desc.getDataType().hasMeta() && !isConsistent) {
			metas = desc.getDataType().createMeta(val, item);
		}
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
		setValue(null, true);
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

	public void setMeta(String key, String value) {
		if (metas == null)
			metas = new HashMap<>();
		metas.put(key, value);
	}

	public boolean hasMetas() {
		return metas != null;
	}

	ArrayList<String> getAllMetas() {
		ArrayList<String> allMetas = new ArrayList<>();
		for (Map.Entry<String, String> entry : metas.entrySet()) {
			allMetas.add(entry.getKey());
			allMetas.add(entry.getValue());
		}
		return allMetas;
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
		return desc.getDataType().getEquals(value, ((SingleParameter)obj).value);
	}
	@Override
	public final int hashCode() {
		return hash;
	}

}