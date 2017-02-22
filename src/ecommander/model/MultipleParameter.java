package ecommander.model;

import java.util.Collection;
import java.util.LinkedHashSet;


/**
 * Множественный параметр, хранит в массиве все значения одного типа
 * Значения могут храниться и устанавливаться в виде строки, разделенной разделителем
 * @author EEEE
 *
 */
public final class MultipleParameter extends Parameter {
	
	private LinkedHashSet<SingleParameter> values;
	
	public MultipleParameter(ParameterDescription desc) {
		super(desc);
		values = new LinkedHashSet<SingleParameter>();
	}
	/**
	 * Добавление значения
	 * @param value
	 */
	public void setValue(Object value) {
		values.add(createSP(value));
	}
	/**
	 * Добавление значения
	 * @param value
	 */
	public void createAndSetValue(String value) {
		SingleParameter param = desc.createSingleParameter();
		param.createAndSetValue(value);
		values.add(param);
	}
	/**
	 * Удаление значения по индексу
	 * @param index
	 */
	public void deleteValue(int index) {
		SingleParameter[] params = values.toArray(new SingleParameter[0]);
		if (index >= params.length)
			return;
		values.remove(params[index]);
	}
	/**
	 * Удалить все включения заданного значения из значений параметра
	 * @param value
	 */
	public void deleteValue(Object value) {
		values.remove(createSP(value));
	}
	
	public void clearValues() {
		values = new LinkedHashSet<SingleParameter>();
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
	
	@Override
	public boolean containsValue(Object value) {
		return values.contains(createSP(value));
	}

	private SingleParameter createSP(Object value) {
		SingleParameter param = desc.createSingleParameter();
		param.setValue(value);
		return param;
	}
	@Override
	public final Object getValue() {
		if (values.size() == 0)
			return null;
		return values.iterator().next().getValue();
	}
}