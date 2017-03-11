package ecommander.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * Множественный параметр, хранит в массиве все значения одного типа
 * Значения могут храниться и устанавливаться в виде строки, разделенной разделителем
 * @author EEEE
 *
 */
public final class MultipleParameter extends Parameter {
	
	private ArrayList<SingleParameter> values;
	private ArrayList<SingleParameter> backupValues;
	
	public MultipleParameter(ParameterDescription desc) {
		super(desc);
		values = new ArrayList<>();
	}
	/**
	 * Добавление значения
	 * @param value
	 */
	public void setValue(Object value) {
		if (value == null || containsValue(value))
			return;
		backup();
		values.add(createSP(value));
	}
	/**
	 * Добавление значения
	 * @param value
	 */
	public void createAndSetValue(String value, boolean isConsistent) {
		if (StringUtils.isBlank(value))
			return;
		SingleParameter param = desc.createSingleParameter();
		param.createAndSetValue(value, true);
		if (values.contains(param))
			return;
		if (!isConsistent)
			backup();
		values.add(param);
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

	public byte valCount() {
		return (byte) values.size();
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
			backupValues = new ArrayList<>();
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
		values = new ArrayList<>();
	}

	@Override
	public boolean hasChanged() {
		return backupValues != null && !singleParamArraysEqual(backupValues, values);
	}

	@Override
	public boolean equals(Object obj) {
		return singleParamArraysEqual(values, ((MultipleParameter)obj).values);
	}

	private boolean singleParamArraysEqual(ArrayList<SingleParameter> first, ArrayList<SingleParameter> second) {
		if (first.size() != second.size())
			return false;
		Iterator<SingleParameter> firstIter = first.iterator();
		Iterator<SingleParameter> secondIter = second.iterator();
		while (firstIter.hasNext()) {
			if (!firstIter.next().equals(secondIter.next()))
				return false;
		}
		return true;
	}
}