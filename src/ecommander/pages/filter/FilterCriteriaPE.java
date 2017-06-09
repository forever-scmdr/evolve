package ecommander.pages.filter;

import java.util.ArrayList;
import java.util.List;

import ecommander.model.Compare;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.var.VariablePE;
import org.apache.commons.lang3.StringUtils;

import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;

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
 * @author EEEE
 *
 */
public abstract class FilterCriteriaPE implements PageElement {
	
	public static final String ELEMENT_NAME = "parameter";
	
	protected ArrayList<VariablePE> values = new ArrayList<VariablePE>(3);
	protected String sign;
	protected String pattern; // Для строковых критериев со знаком like. Формат: %v% - сначала символ %, потом значение параметра, потом опять %
	protected Compare compareType = Compare.ANY;
	/**
	 * Конструктор создания исполяемой копии
	 * @param template
	 * @param container
	 * @param parentPage
	 */
	protected FilterCriteriaPE(FilterCriteriaPE template, ExecutablePagePE parentPage) {
		sign = template.sign;
		pattern = template.pattern;
		compareType = template.compareType;
		for (VariablePE var : template.values) {
			addValue((VariablePE) var.createExecutableClone(null, parentPage));
		}
	}
	
	protected FilterCriteriaPE(String sign, String pattern, Compare compType) {
		this.sign = sign;
		this.pattern = pattern;
		this.compareType = compType;
	}
	
	public final List<String> getValueArray() {
		ArrayList<String> result = new ArrayList<String>();
		for (VariablePE var : values) {
			for (String varVal : var.outputArray()) {
				result.add(varVal);
			}
		}
		return result;
	}
	
	public void addValue(VariablePE value) {
		values.add(value);
	}
	
	public boolean isValid() {
		for (VariablePE var : values) {
			if (!var.isEmpty())
				return true;
		}
		return false;
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
	
	public static FilterCriteriaPE create(String paramName, String paramNameVar, String paramIdVar, String sign,
			String pattern, Compare compType) {
		if (compType == null)
			compType = Compare.ANY;
		if (!StringUtils.isBlank(paramName)) {
			return new HardParameterCriteriaPE(paramName, sign, pattern, compType);
		} else if (!StringUtils.isBlank(paramNameVar)) {
			return new VariableParameterCriteriaPE(paramNameVar, sign, pattern, compType);
		} else if (!StringUtils.isBlank(paramIdVar)) {
			return new IdVariableParameterCriteriaPE(paramIdVar, sign, pattern, compType);
		} else {
			throw new IllegalArgumentException("Neither paramName nor paramNameVar supplied for filter criteria parameter");
		}
	}
	
	public final String getKey() {
		return "Filter criteria";
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}
}
