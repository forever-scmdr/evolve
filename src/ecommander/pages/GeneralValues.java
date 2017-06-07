package ecommander.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
/**
 * Значения переданных через форму параметров.
 * Значения могут иметь тип либо String либо FileItem.
 * Значения также могут храниться как по одиночке, так и в массиве ArrayList, если для одного
 * параметра передано много значений
 * @author E
 *
 */
public class GeneralValues implements Serializable {
	private static final long serialVersionUID = -1721171544301221441L;

	private HashMap<Object, Object> map = new HashMap<>();
	public GeneralValues() {}
	
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
	
	public boolean isMultiple(Object paramId) {
		return map.get(paramId) != null && map.get(paramId) instanceof  List<?>;
	}
	
	public Object get(Object paramId) {
		return map.get(paramId);
	}

	public void remove(Object paramId) {
		map.remove(paramId);
	}

	public String getString(Object paramId) {
		Object value = map.get(paramId);
		if (value == null)
			return null;
		if (value instanceof List<?> && !((List<?>)value).isEmpty())
			return ((List<?>)value).get(0).toString();
		return value.toString();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getList(Object paramId) {
		Object value = map.get(paramId);
		if (value == null)
			return new ArrayList<>(0);
		if (value instanceof List<?>)
			return (ArrayList<Object>)value;
		ArrayList<Object> values = new ArrayList<>();
		values.add(value);
		return values;
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
}