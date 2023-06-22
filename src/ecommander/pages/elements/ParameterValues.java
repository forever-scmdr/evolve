package ecommander.pages.elements;

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
class ParameterValues implements Serializable {
	private static final long serialVersionUID = -1721171544301221441L;

	private HashMap<Object, Object> map = new HashMap<Object, Object>();
	ParameterValues() {}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void add(Object paramId, Object value) {
		Object stored = map.get(paramId);
		if (stored == null) {
			map.put(paramId, value);
		} else {
			if (stored instanceof List<?>) {
				((ArrayList) stored).add(value);
			} else {
				ArrayList<Object> newVal = new ArrayList<Object>();
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
			return new ArrayList<Object>(0);
		if (value instanceof List<?>)
			return (ArrayList<Object>)value;
		ArrayList<Object> values = new ArrayList<Object>();
		values.add(value);
		return values;
	}
	
	public boolean isNotEmpty() {
		return !map.isEmpty();
	}
	
	public Collection<Object> getExtraNames() {
		return map.keySet();
	}
	
	public boolean containsValue(Object paramId) {
		return map.containsKey(paramId);
	}
}