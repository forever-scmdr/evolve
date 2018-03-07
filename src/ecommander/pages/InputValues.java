package ecommander.pages;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Значения переданных через форму параметров.
 * Значения могут иметь тип либо String либо FileItem.
 * Значения также могут храниться как по одиночке, так и в массиве ArrayList, если для одного
 * параметра передано много значений
 * @author E
 *
 */
public class InputValues implements Serializable {
	private static final long serialVersionUID = -1721171544301221441L;

	private LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
	private HashMap<String, String> validationErrors = new HashMap<>();
	private String validationMessage;

	public InputValues() {}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void add(Object paramId, Object value) {
		Object stored = map.get(paramId);
		if (stored == null) {
			map.put(paramId, value);
		} else {
			if (stored instanceof List<?>) {
				((ArrayList) stored).add(value);
			} else {
				ArrayList<Object> newVal = new ArrayList<>();
				newVal.add(stored);
				newVal.add(value);
				map.put(paramId, newVal);
			}
		}
	}

	public void replace(Object paramId, Object value) {
		map.put(paramId, value);
	}

	public boolean isMultiple(Object paramId) {
		return map.get(paramId) != null && map.get(paramId) instanceof  List<?>;
	}
	
	public Object get(Object paramId) {
		return map.get(paramId);
	}

	public void remove(Object paramId) {
		map.remove(paramId);
	}

	private String getString(Object paramId) {
		Object value = map.get(paramId);
		if (value == null)
			return null;
		if (value instanceof List<?> && !((List<?>)value).isEmpty())
			return ((List<?>)value).get(0).toString();
		return value.toString();
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Object> getList(Object paramId) {
		Object value = map.get(paramId);
		if (value == null)
			return new ArrayList<>(0);
		if (value instanceof List<?>)
			return (ArrayList<Object>)value;
		ArrayList<Object> values = new ArrayList<>();
		values.add(value);
		return values;
	}

	public ArrayList<Object> getExtraList(String extraKey) {
		return getList(extraKey);
	}

	public String getExtraString(String extraKey) {
		return getString(extraKey);
	}

	public boolean isNotEmpty() {
		return !map.isEmpty();
	}
	
	public Collection<Object> getKeys() {
		return map.keySet();
	}
	
	public boolean containsValue(Object paramId) {
		return map.containsKey(paramId);
	}

	public boolean hasError(String paramName) {
		return StringUtils.isNotBlank(validationErrors.get(paramName));
	}

	public String getError(String paramName) {
		return validationErrors.get(paramName);
	}

	public void setError(String paramName, String errorMessage) {
		validationErrors.put(paramName, errorMessage);
	}

	public void setMessage(String message) {
		this.validationMessage = message;
	}

	public String getMessage() {
		return validationMessage;
	}

	public boolean hasMessage() {
		return StringUtils.isNotBlank(validationMessage);
	}

	public boolean hasValue(Object key) {
		return map.get(key) != null;
	}

	/**
	 * Скопировать соответствюущие значения инпутов из объекта InputValues
	 * @param values
	 */
	void update(InputValues values) {
		if (values == null)
			return;
		for (Object key : getKeys()) {
			if (values.getKeys().contains(key)) {
				replace(key, values.get(key));
			}
		}
		if (values.hasMessage()) {
			setMessage(values.getMessage());
		}
		for (String s : values.validationErrors.keySet()) {
			validationErrors.put(s, values.validationErrors.get(s));
		}
		validationMessage = values.validationMessage;
	}
}