package ecommander.model;

import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * Множественный параметр, хранит в массиве все значения одного типа
 * Значения могут храниться и устанавливаться в виде строки, разделенной разделителем
 * @author EEEE
 *
 */
public final class MultipleParameter extends Parameter {
	
	private LinkedHashSet<SingleParameter> values;
	private HashSet<SingleParameter> backupValues;
	
	public MultipleParameter(ParameterDescription desc) {
		super(desc);
		values = new LinkedHashSet<>();
	}
	/**
	 * Добавление значения
	 * @param value
	 */
	public boolean setValue(Object value) {
		if (value == null || containsValue(value))
			return false;
		backup();
		values.add(createSP(value));
		return true;
	}
	/**
	 * Добавление значения
	 * @param value
	 */
	public boolean createAndSetValue(String value, boolean isConsistent) {
		if (StringUtils.isBlank(value))
			return false;
		SingleParameter param = desc.createSingleParameter();
		param.createAndSetValue(value, true);
		if (values.contains(param))
			return false;
		if (!isConsistent)
			backup();
		values.add(param);
		return true;
	}
	/**
	 * Удаление значения по индексу
	 * @param index
	 */
	public void deleteValue(int index) {
		if (index >= values.size())
			return;
		backup();
		values.remove(index);
	}
	/**
	 * Удалить все включения заданного значения из значений параметра
	 * @param value
	 */
	public void deleteValue(Object value) {
		values.remove(createSP(value));
	}

	public Collection<SingleParameter> getValues() {
		return values;
	}
	
	@Override
	public boolean isMultiple() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return values.size() == 0;
	}

	/**
	 * Количество значений параметра
	 * @return
	 */
	public byte valCount() {
		return (byte) values.size();
	}

	/**
	 * Количество изначальных значений параметра при загрузке из БД
	 * @return
	 */
	public byte initialValCount() {
		if (backupValues == null)
			return (byte) values.size();
		return (byte) backupValues.size();
	}
	
	@Override
	public boolean containsValue(Object value) {
		return values.contains(createSP(value));
	}

	private SingleParameter createSP(Object value) {
		SingleParameter param = desc.createSingleParameter();
		param.setValue(value);
		return param;
	}

	/**
	 * Создать резервную копию всех значений параметра, для того, чтобы
	 * потом можно было определить менялся он или нет
	 */
	private void backup() {
		if (backupValues == null) {
			backupValues = new HashSet<>();
			backupValues.addAll(values);
		}
	}

	@Override
	public final Object getValue() {
		if (values.size() == 0)
			return null;
		return values.iterator().next().getValue();
	}

	@Override
	public void clear() {
		if (values.size() == 0)
			return;
		backup();
		values = new LinkedHashSet<>();
	}

	@Override
	public boolean hasChanged() {
		return backupValues != null && (!values.containsAll(backupValues) || !backupValues.containsAll(values));
	}

	@Override
	public boolean equals(Object obj) {
		MultipleParameter mp = (MultipleParameter)obj;
		return values.containsAll(mp.values) && mp.values.containsAll(values);
	}
}