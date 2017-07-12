package ecommander.pages;

import ecommander.model.Item;
import ecommander.model.ParameterDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс для создания полей ввода на базе айтема.
 * Т.е. когда надо добавить в форму поля ввода заданного айтема (название поля + значение)
 *
 * Использование:
 * 1)   Создать экземпляр, передав в конструктор айтем и, если необходимо, массив предшественников
 * 2)   Добавить нужные параметры (или все параметры)
 * 3)   Добавить дополнительные поля (поля, которые не соответствуют параметрам айтема)
 * 4)   Получить список полей ввода с установленными значениями
 *
 * Заполнение предшественников:
 * Предшественников надо заполнять только в случае создания нового айтема
 * Извлекаются по очереди все страничные айтемы-предшественники (от прямого родителя вверх по иерархии).
 * Если очередной предшественник содержит элементы InputSetPE, то проверяется ID формы этого набора инпутов.
 * Если ID формы совпадает с текущим, то к текущим инпутам добавляется предшественник - активный айтем
 * найденного страничного айтема.
 *
 * Created by E on 22/5/2017.
 */
public class ItemInputs {
	private Item item;

	private InputValues inputs = new InputValues();
	private Long[] predecessors;

	public ItemInputs(Item item, Long... predecessors) {
		this.item = item;
		this.predecessors = predecessors;
	}

	public void addAllParameters() {
		for (ParameterDescription paramDesc : item.getItemType().getParameterList()) {
			ArrayList<String> vals = item.outputValues(paramDesc.getName());
			ItemInputName input = new ItemInputName(item.getId(), item.getContextParentId(),
					item.getTypeId(), paramDesc.getId(), null, predecessors);
			inputs.add(input, vals);
		}
		// Если айтем не имеет ни одного параметра - создать один псевдопараметр, чтобы было хотя-бы одно поле ввода
		// Это нужно для создания айетмов, которые не имеют параметров, например, каталог продукции
		addExtra("pseudo");
	}

	void addParameters(String... paramNames) {
		for (String paramName : paramNames) {
			ParameterDescription paramDesc = item.getItemType().getParameter(paramName);
			ArrayList<String> vals = item.outputValues(paramName);
			ItemInputName input = new ItemInputName(item.getId(), item.getContextParentId(),
					item.getTypeId(), paramDesc.getId(), null, predecessors);
			inputs.add(input, vals);
		}
	}

	void addExtra(String... extraNames) {
		for (String extraName : extraNames) {
			ArrayList<String> vals = new ArrayList<>();
			List<Object> objVals = item.getListExtra(extraName);
			if (objVals != null) {
				for (Object obj : objVals) {
					vals.add(obj.toString());
				}
			}
			ItemInputName input = new ItemInputName(item.getId(), item.getContextParentId(),
					item.getTypeId(), 0, extraName, predecessors);
			inputs.add(input, vals);
		}
	}

	/**
	 * Получить список всех названий инпутов для айтема
	 * @return
	 */
	public ArrayList<ItemInputName> getAllInputNames() {
		ArrayList<ItemInputName> result = new ArrayList<>();
		for (Object key : inputs.getKeys()) {
			result.add((ItemInputName) key);
		}
		return result;
	}

	/**
	 * Вернуть базовый айтем
	 * @return
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Получить список
	 * @param name
	 * @return
	 */
	public ArrayList<String> getInputValues(ItemInputName name) {
		return (ArrayList<String>) inputs.get(name);
	}
}
