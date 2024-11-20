package ecommander.pages.elements;

import java.util.Collection;
import java.util.HashMap;

import ecommander.common.exceptions.EcommanderException;
import ecommander.pages.elements.variables.StaticVariablePE;

/**
 * Результат выполнения команды
 * @author E
 *
 */
public class ResultPE implements PageElement {
	public static final String ELEMENT_NAME = "result";
	
	public static enum ResultType {
		forward, redirect, xml, plain_text, none;
	}

	private String name; // название страничной ссылки (по которой надо переходить после выполнения команды)
	private ResultType type; // тип команды
	private HashMap<String, StaticVariablePE> vars; // переменные, значения которых устанавливается в команде (если необходимо)
	private String value; // значение, которое получилось в результате выполнения команды,
						  // может быть значением переменной с названием variable или другим произвольным текстом
	private boolean doRollback = false; // Нужно ли производить откат транзакции после завершения выполнения всех команд страницы
										// устанавливается в true в случае если все изменения команд нужно откатить

	public ResultPE(String name, String typeName) {
		this(name, ResultType.valueOf(typeName));
		this.name = name;
	}
	
	public ResultPE(String name, ResultType type) {
		this.name = name;
		this.type = type;
	}
	
	private ResultPE(ResultPE src) {
		this.name = src.name;
		this.type = src.type;
		if (src.vars != null)
			this.vars = new HashMap<String, StaticVariablePE>(src.vars);
	}

	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new ResultPE(this);
	}

	public void validate(String elementPath, ValidationResults results) {
		// Валидация должна происходить в другом месте
	}

	public String getKey() {
		return "Command result";
	}

	public String getValue() {
		return value;
	}

	public ResultPE setValue(String value) {
		this.value = value;
		return this;
	}
	/**
	 * Установить значение переменной (если такая переменная не предусмотрена - выбрасывается исключение)
	 * Новое значение добавляется к существующим значениям, не заменяя их
	 * @param name
	 * @param value
	 * @throws EcommanderException
	 */
	public ResultPE addVariable(String name, String value) throws EcommanderException {
		if (vars == null) {
			vars = new HashMap<String, StaticVariablePE>();
		}
		StaticVariablePE var = vars.get(name);
		if (var == null) {
			var = new StaticVariablePE(name, value);
			vars.put(name, var);
		} else {
			var.addValue(value);
		}
		return this;
	}
	/**
	 * Установить значение переменной (если такая переменная не предусмотрена - выбрасывается исключение)
	 * Новое значение заменяет старые значения переменной, если они были
	 * @param name
	 * @param value
	 * @throws EcommanderException
	 */
	public ResultPE setVariable(String name, String value) throws EcommanderException {
		if (vars == null) {
			vars = new HashMap<String, StaticVariablePE>();
		}
		vars.put(name, new StaticVariablePE(name, value));
		return this;
	}
	/**
	 * Устанавливались ли значения переменных
	 * @return
	 */
	public boolean hasVariables() {
		return vars != null;
	}
	
	public String getName() {
		return name;
	}

	public ResultType getType() {
		return type;
	}

	public Collection<StaticVariablePE> getVariables() {
		return vars.values();
	}

	public void rollback() {
		doRollback = true;
	}
	
	public boolean needRollback() {
		return doRollback;
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}
	
}
