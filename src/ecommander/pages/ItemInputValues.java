package ecommander.pages;

import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Параметры, переданные через HTTP для одного айтема
 * Created by E on 5/3/2018.
 */
public class ItemInputValues {

	private HashMap<String, Object> params = new HashMap<>();
	private HashMap<String, Object> extras = new HashMap<>();

	public ItemInputValues(InputValues vals) {
		if (vals != null) {
			for (Object o : vals.getKeys()) {
				if (o instanceof String) {
					extras.put((String) o, vals.get(o));
				} else if (o instanceof ItemInputName) {
					ItemInputName key = (ItemInputName) o;
					ItemType item = ItemTypeRegistry.getItemType(key.getItemType());
					if (item != null) {
						ParameterDescription param = item.getParameter(key.getParamId());
						if (param != null) {
							params.put(param.getName(), vals.get(o));
						}
					}
				}
			}
		}
	}

	private static ArrayList<Object> getValueList(HashMap<String, Object> map, String paramName) {
		Object value = map.get(paramName);
		if (value == null)
			return new ArrayList<>(0);
		if (value instanceof ArrayList<?>) {
			return (ArrayList<Object>) value;
		}
		ArrayList<Object> result = new ArrayList<>();
		result.add(value);
		return result;
	}

	private static Object getValue(HashMap<String, Object> map, String paramName) {
		Object value = map.get(paramName);
		if (value instanceof ArrayList<?>) {
			if (((ArrayList<Object>) value).size() > 0)
				return ((ArrayList<Object>) value).get(0);
			return null;
		}
		return value;
	}

	public ArrayList<Object> getParamList(String paramName) {
		return getValueList(params, paramName);
	}

	public Object getParam(String paramName) {
		return getValue(params, paramName);
	}

	public String getStringParam(String paramName) {
		return (String) getParam(paramName);
	}

	public ArrayList<Object> getExtraList(String paramName) {
		return getValueList(extras, paramName);
	}

	public Object getExtra(String paramName) {
		return getValue(extras, paramName);
	}

	public String getStringExtra(String paramName) {
		return (String) getExtra(paramName);
	}

}
