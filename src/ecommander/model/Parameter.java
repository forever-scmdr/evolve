package ecommander.model;

import ecommander.model.datatypes.DataType.Type;

/**
 * Общий класс для одиночных и множественных параметров
 * @author EEEE
 */
public abstract class Parameter {

	protected ParameterDescription desc;
	protected Item item;

	protected Parameter(ParameterDescription desc, Item item) {
		this.desc = desc;
		this.item = item;
	}
	
	public final String getName() {
		return desc.getName();
	}
	/**
	 * Создать правильное значение, полученное из интерфейса пользователя в форме строки
	 * @param value
	 */
	protected Object createTypeDependentValue(String value) {
		return desc.getDataType().createValue(value, desc.getFormatter());
	}
	
	public final Type getType() {
		return desc.getType();
	}
	
	ParameterDescription getDesc() {
		return desc;
	}
	
	public final int getParamId() {
		return desc.getId();
	}
	
	public final boolean isDescMultiple() {
		return desc.isMultiple();
	}
	
	public final boolean isVirtual() {
		return desc.isVirtual();
	}
	
	public final boolean needsDBIndex() {
		return desc.needsDBIndex();
	}
	
	public abstract boolean isMultiple();

	/**
	 * Создать значение из строки и установить
	 * @param value
	 * @param isConsistent - при загрузке из БД - true, при изменении в процессе работы приложения - false
	 * @return - если значение параметра изменилось - true, если нет, то false
	 */
	abstract SingleParameter createAndSetValue(String value, boolean isConsistent);

	/**
	 * Установить значение напрямую без создания
	 * @param value
	 * @param isConsistent - при загрузке из БД - true, при изменении в процессе работы приложения - false
	 * @return - если значение параметра изменилось - true, если нет, то false
	 */
	abstract boolean setValue(Object value, boolean isConsistent);
	public abstract boolean isEmpty();
	public abstract boolean containsValue(Object value);
	/**
	 * Возвращает одно значение
	 * В случае одиночного параметра - его значение, в случае множественного - первое значение
	 * @return
	 */
	public abstract Object getValue();

	/**
	 * Очистить значение параметра
	 */
	public abstract boolean clear();

	/**
	 * Узнать, менялся ли параметр с момента загрузки айтема
	 * @return
	 */
	public abstract boolean hasChanged();
}