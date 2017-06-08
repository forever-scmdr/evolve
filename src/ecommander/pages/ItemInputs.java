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
 * Created by E on 22/5/2017.
 */
public class ItemInputs {
	private Item item;

	private InputValues inputs = new InputValues();
	private long[] predecessors;
+
	ItemInputs(Item item, long... predecessors) {
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

	InputValues getInputs() {
		return inputs;
	}
}
