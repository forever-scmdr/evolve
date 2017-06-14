package ecommander.pages;

import ecommander.model.Item;
import ecommander.model.ParameterDescription;

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

	ItemInputs(Item item, Long... predecessors) {
		this.item = item;
		this.predecessors = predecessors;
	}

	void addAllParameters() {
		for (ParameterDescription paramDesc : item.getItemType().getParameterList()) {
			Object value = item.getValue(paramDesc.getId());
			ItemInputName input = new ItemInputName(item.getId(), item.getContextParentId(),
					item.getTypeId(), paramDesc.getId(), null, predecessors);
			inputs.add(input, value);
		}
	}

	void addParameters(String... paramNames) {
		for (String paramName : paramNames) {
			ParameterDescription paramDesc = item.getItemType().getParameter(paramName);
			Object value = item.getValue(paramName);
			ItemInputName input = new ItemInputName(item.getId(), item.getContextParentId(),
					item.getTypeId(), paramDesc.getId(), null, predecessors);
			inputs.add(input, value);
		}
	}

	void addExtra(String... extraNames) {
		for (String extraName : extraNames) {
			Object value = item.getExtra(extraName);
			ItemInputName input = new ItemInputName(item.getId(), item.getContextParentId(),
					item.getTypeId(), 0, extraName, predecessors);
			inputs.add(input, value);
		}
	}

	public InputValues getInputs() {
		return inputs;
	}
}
