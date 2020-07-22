package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Класс, который представляет переменную. Переменная - это НЕ элемент <var></var>, а значение, которое может быть
 * использовано в процессе обработкти страницы. Например, направление сортировки, параметр сортировки,
 * название параметра фильтра, значение параметра фильтра и т.д. Элемент <var></var> должен включать в себя
 * экземпляр этого класса, т.к. он должен иметь некоторое значение.
 *
 * Пемеменная может иметь одно или несколько значений.
 * Значения переменной делятся на ВСЕ значения и ЛОКАЛЬНЫЕ значения.
 * ВСЕ значения используются при загрузке страницы в логике загрузки и в командах,
 * ЛОКАЛЬНЫЕ значения используются при выводе страницы в виде XML (для генерации ссылок)
 *
 *
 * Created by E on 9/6/2017.
 */
public abstract class Variable {
	protected ExecutablePagePE parentPage;
	protected String name = "unnamed";

	Variable(ExecutablePagePE parentPage, String name) {
		this.parentPage = parentPage;
		this.name = name;
	}

	Variable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * Вернуть ВСЕ значения (не локальные)
	 * @return
	 */
	public abstract ArrayList<Object> getAllValues();

	/**
	 * Вернуть единственное значение (когда известно, что значение может быть только одно)
	 * @return
	 */
	public abstract Object getSingleValue();

	/**
	 * Вернуть едниственное значение в виде строки
	 * Если переменная не имеет значения - возвращаетс япустая строка
	 * @return
	 */
	public final String writeSingleValue() {
		if (isEmpty())
			return "";
		return getSingleValue().toString();
	}

	/**
	 * Вернуть все значения в виде строки
	 * @return
	 */
	public ArrayList<String> writeAllValues() {
		ArrayList<String> result = new ArrayList<>();
		if (isEmpty())
			return result;
		for (Object val : getAllValues()) {
			result.add(val.toString());
		}
		return result;
	}

	public abstract Variable getInited(ExecutablePagePE parentPage);

	/**
	 * Вернуть локальные значения. Т.е. значения, относящиеся только к одному айтему в процессе итерации.
	 * Если переменная не итериуемая, то возвращаются все значения
	 * @return
	 */
	public abstract ArrayList<String> getLocalValues();

	/**
	 * Вернуть единственное локальное значение. Аналогично getLocalValues, но когда известно, что долджно вернуться
	 * только одно значение, например, когда переменной представляется одиночный параметр айтема
	 * @return
	 */
	public abstract String getSingleLocalValue();

	/**
	 * Является ли переменная пустой
	 * @return
	 */
	public abstract boolean isEmpty();

	/**
	 * Валидация
	 * @param elementPath
	 * @param results
	 */
	public void validate(String elementPath, ValidationResults results) {
		// по умолчанию ничего не делать
	}

	public abstract void removeValue(Object value);

	@Override
	public String toString() {
		return name + ": " + StringUtils.join(writeAllValues(), ", ");
	}
}
