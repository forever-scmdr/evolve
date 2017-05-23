/*
 * Created on 22.12.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ecommander.persistence.mappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

/**
 * Класс, который может хранить объекты, которые обладаю некоторыми общими параметрами.
 * Этот класс позволяет совершать поиск и выборку этих обектов по их общим параметрам
 * 
 * @author EEEE
 */
public abstract class ParametricObjectStorage
{
	/**
	 * Этот метод надо переопределять следующим образом
	 * 
	
	protected Object getParameter(Object object, byte parameterId)
	{
		switch (parameterId)
		{
			case ONE: return ((Object)object).getOne();
			case TWO: return ((Object)object).getTwo();
			case THREE: return ((Object)object).getThree();
			default: return null;
		}
	}
	
	 * @param object
	 * @param parameterId
	 * @return
	 */
	protected abstract Object getParameter(Object object, int parameterId);

	// Массив объектов (любые объекты)
	private ArrayList<Object> storage;

	ParametricObjectStorage() {
		storage = new ArrayList<>();
	}

	/**
	 * Делает выборку из массива объектов по поределенному параметру
	 * 
	 * @param parameterId
	 * @param parameterValue
	 * @return
	 */
	public ArrayList<Object> select(final int parameterId, final Object parameterValue) {
		Predicate<Object> equalityPredicate = new Predicate<Object>() {
			@SuppressWarnings("rawtypes")
			public boolean evaluate(Object arg0) {
				Object value = getParameter(arg0, parameterId);
				if (value instanceof List) {
					return ((List) value).contains(parameterValue);
				}
				return parameterValue.equals(getParameter(arg0, parameterId));
			}
		};
		ArrayList<Object> result = new ArrayList<>();
		CollectionUtils.select(storage, equalityPredicate, result);
		return result;
	}

	/**
	 * Делает выборку из массива объектов по набору параметров Каждый параметр может быть как отдельным значением, так и массивом возможных
	 * значений.
	 * 
	 * Если для какого-то параметра значение или значения равны null, то этот критерий не учитвается, т.е. возвращаются объекты
	 * с любым значением данного параметра
	 * 
	 * Чтобы происходило наоборот, т.е. не возвращалось бы ни одного объекта, надо задавать пустое множество в качестве значения параметра
	 * 
	 * @param parameterIds
	 * @param parameterValues
	 * @return
	 */
	public ArrayList<Object> select(final int[] parameterIds, final Object[] parameterValues) {
		Predicate<Object> equalityPredicate = new Predicate<Object>() {
			@SuppressWarnings("unchecked")
			public boolean evaluate(Object arg0) {
				boolean equal = true;
				for (int i = 0; i < parameterIds.length && equal; i++) {
					if (parameterValues[i] != null) {
						if (parameterValues[i] instanceof Collection)
							equal &= ((Collection<Object>) parameterValues[i]).contains(getParameter(arg0, parameterIds[i]));
						else
							equal &= parameterValues[i].equals(getParameter(arg0, parameterIds[i]));
					}
				}
				return equal;
			}
		};
		ArrayList<Object> result = new ArrayList<>();
		if (parameterIds.length <= 0)
			return result;
		CollectionUtils.select(storage, equalityPredicate, result);
		return result;
	}

	/**
	 * Делает выборку из массива объектов. Определенный параметр объекта может принимать одно значение из набора возможных
	 * 
	 * @param parameterId
	 * @param parameterValues
	 * @return
	 */
	public ArrayList<Object> select(final int parameterId, final ArrayList<Object> parameterValues) {
		Predicate<Object> equalityPredicate = new Predicate<Object>() {
			@SuppressWarnings("rawtypes")
			public boolean evaluate(Object arg0) {
				Object value = getParameter(arg0, parameterId);
				if (value instanceof List) {
					return CollectionUtils.containsAny(parameterValues, (List)value);
				}
				return parameterValues.contains(value);
			}
		};
		ArrayList<Object> result = new ArrayList<>();
		CollectionUtils.select(storage, equalityPredicate, result);
		return result;
	}

	/**
	 * Возвращает объект по уникальному значению одного из его параметров (уникальному среди всех объектов) Уникальное значение может быть только
	 * одно, в противном случе возвращается первый найденный объект.
	 * 
	 * @param parameterId
	 * @param parameterValue
	 * @return
	 */
	public Object getObjectById(int parameterId, Object parameterValue) {
		for (Object obj : storage) {
			if (parameterValue.equals(getParameter(obj, parameterId)))
				return obj;
		}
		return null;
	}

	/**
	 * Добавляет один объект
	 * 
	 * @param object
	 */
	public void addObject(Object object) {
		storage.add(object);
	}

	/**
	 * Удаляет все объекты, которые подходят по параметрам
	 * 
	 * @param parameterId
	 * @param parameterValue
	 */
	public void delete(int parameterId, Object parameterValue) {
		storage.removeAll(select(parameterId, parameterValue));
	}
	/**
	 * Удаляет все объекты, которые подходят по параметрам
	 * @param parameterIds
	 * @param parameterValues
	 */
	public void delete(int[] parameterIds, Object[] parameterValues) {
		storage.removeAll(select(parameterIds, parameterValues));
	}

	public boolean isEmpty() {
		return storage.isEmpty();
	}
}