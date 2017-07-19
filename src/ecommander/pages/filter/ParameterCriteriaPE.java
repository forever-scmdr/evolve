package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.var.Variable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Критерий фильтра
 * Значение критерия - любая страничная переменная
 * 
 * Одиночные критерии фильтра
 * Критерий состоит из:
 * 1. Название параметра текущего айтема (в котором находится этот фильтр)
 * 		1a. Может указываться конкретное имя параметра (name)
 * 		1b. Может храниться название переменной, которая хранит название параметра (nameVar)
 * 2. Операция сравнения (знак)
 * 3. Название переменной, если критерий динамический
 * 4. Значение параметра. Оно устанавливается только после того, как получена соответсвующая переменная, если параметр динамический.
 *    Также это значение уже установлено когда параметр десериализован.
 * 5. Тип сравнения. Если переменная содержит множество значений, то должен ли параметр совпадать с одним из значений этой переменной,
 *    либо с каждым ее значением.
 *    
 * Множественные критерии фильтра
 * Критерий может содержать несколько переменных, тогда значение критерия берется из всех этих переменных
 *
 *
 * ДОПОЛНИТЕЛЬНО
 *
 *
 * Критерий, определяющий значение параметра некоторого потомка айтема.
 * Например, Вывести разделы, которые содержат товары с ценой, меньшей определенного значения.
 * Подразумевается, что сами товары выводить не нужно.
 *
 * @author EEEE
 *
 */
public abstract class ParameterCriteriaPE implements FilterCriteriaPE {
	
	public static final String ELEMENT_NAME = "parameter";
	
	protected ArrayList<Variable> values = new ArrayList<>(3);
	protected String sign;
	protected String pattern; // Для строковых критериев со знаком like. Формат: %v% - сначала символ %, потом значение параметра, потом опять %
	protected Compare compareType = Compare.ANY;
	/**
	 * Конструктор создания исполяемой копии
	 * @param template
	 * @param parentPage
	 */
	protected ParameterCriteriaPE(ParameterCriteriaPE template, ExecutablePagePE parentPage) {
		sign = template.sign;
		pattern = template.pattern;
		compareType = template.compareType;
		for (Variable var : template.values) {
			addValue(var.getInited(parentPage));
		}
	}
	
	protected ParameterCriteriaPE(String sign, String pattern, Compare compType) {
		this.sign = sign;
		this.pattern = pattern;
		this.compareType = compType;
	}

	public final List<String> getValueArray() {
		ArrayList<String> result = new ArrayList<>();
		for (Variable var : values) {
			for (String varVal : var.writeAllValues()) {
				result.add(varVal);
			}
		}
		return result;
	}
	
	public void addValue(Variable value) {
		values.add(value);
	}

	public abstract ParameterDescription getParam(ItemType itemDesc);

	public final String getSign() {
		return sign;
	}

	public final String getPattern() {
		return pattern;
	}
	
	public final boolean hasPattern() {
		return !StringUtils.isBlank(pattern);
	}
	
	public final Compare getCompareType() {
		return compareType;
	}

	public static ParameterCriteriaPE create(String paramName, String paramNameVar, String paramIdVar, String sign,
	                                         String pattern, Compare compType) {
		ParameterCriteriaPE instance;
		if (compType == null)
			compType = Compare.ANY;
		if (!StringUtils.isBlank(paramName)) {
			instance = new HardParameterCriteriaPE(paramName, sign, pattern, compType);
		} else if (!StringUtils.isBlank(paramNameVar)) {
			instance = new VariableParameterCriteriaPE(paramNameVar, sign, pattern, compType);
		} else if (!StringUtils.isBlank(paramIdVar)) {
			instance = new IdVariableParameterCriteriaPE(paramIdVar, sign, pattern, compType);
		} else {
			throw new IllegalArgumentException("Neither paramName nor paramNameVar supplied for filter criteria parameter");
		}
		return instance;
	}
	
	public final String getKey() {
		return "Filter criteria";
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	public void process(FilterCriteriaContainer cont) throws EcommanderException {
		cont.processParameterCriteria(this);
	}

	final boolean hasValues() {
		return values.size() > 0;
	}
}
