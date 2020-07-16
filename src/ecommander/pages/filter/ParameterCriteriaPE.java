package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
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
 * 6. Для типа данных map
 *      6a. Может указываться конкретый ключ (key)
 *      6b. Может храниться название переменной, которая хранит ключ (keyVar)
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
public class ParameterCriteriaPE implements FilterCriteriaPE {

	public static final String ELEMENT_NAME = "parameter";
	
	private ArrayList<Variable> values = new ArrayList<>(3);
	private String sign;
	private String pattern; // Для строковых критериев со знаком like. Формат: %v% - сначала символ %, потом значение параметра, потом опять %
	private Compare compareType = Compare.ANY;
	private String sort; // Направление сортировки при группировке

	private String paramName = null;
	private String paramNameVar = null;
	private String paramIdVar = null;
	private String tupleKey = null;
	private String tupleKeyVar = null;
	private ExecutablePagePE pageModel;


	private ParameterCriteriaPE(ParameterCriteriaPE template, ExecutablePagePE parentPage) {
		sign = template.sign;
		pattern = template.pattern;
		compareType = template.compareType;
		sort = template.sort;

		paramName = template.paramName;
		paramNameVar = template.paramNameVar;
		paramIdVar = template.paramIdVar;
		tupleKey = template.tupleKey;
		tupleKeyVar = template.tupleKeyVar;

		this.pageModel = parentPage;

		for (Variable var : template.values) {
			addValue(var.getInited(parentPage));
		}
	}

	
	private ParameterCriteriaPE(String paramName, String paramNameVar, String paramIdVar, String tupleKey, String tupleKeyVar,
	                            String sign, String pattern, Compare compType, String sort) {
		this.sign = sign;
		this.pattern = pattern;
		this.compareType = compType;
		this.sort = sort;

		if (StringUtils.isNotBlank(paramName)) this.paramName = paramName;
		if (StringUtils.isNotBlank(paramNameVar)) this.paramNameVar = paramNameVar;
		if (StringUtils.isNotBlank(paramIdVar)) this.paramIdVar = paramIdVar;
		if (StringUtils.isNotBlank(tupleKey)) this.tupleKey = tupleKey;
		if (StringUtils.isNotBlank(tupleKeyVar)) this.tupleKeyVar = tupleKeyVar;
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

	public ParameterDescription getParam(ItemType itemDesc) {
		if (paramName != null) {
			return itemDesc.getParameter(paramName);
		}
		if (paramNameVar != null) {
			itemDesc.getParameter(pageModel.getVariable(paramNameVar).writeSingleValue());
		}
		if (paramIdVar != null) {
			int paramId = Integer.parseInt(pageModel.getVariable(paramNameVar).writeSingleValue());
			return itemDesc.getParameter(paramId);
		}
		return null;
	}

	public boolean hasTupleKey() {
		return tupleKey != null || tupleKeyVar != null;
	}

	public String getTupleKey() {
		if (tupleKey != null)
			return tupleKey;
		if (tupleKeyVar != null)
			return pageModel.getVariable(tupleKeyVar).writeSingleValue();
		return null;
	}

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

	public String getSort() {
		return sort;
	}

	public static ParameterCriteriaPE create(String paramName, String paramNameVar, String paramIdVar,
	                                         String tupleKey, String tupleKeyVar, String sign,
	                                         String pattern, Compare compType, String sort) {
		if (compType == null)
			compType = Compare.ANY;
		if (StringUtils.isBlank(paramName) && StringUtils.isBlank(paramNameVar) && StringUtils.isBlank(paramIdVar)) {
			throw new IllegalArgumentException("Neither paramName nor paramNameVar supplied for filter criteria parameter");
		}
		return new ParameterCriteriaPE(paramName, paramNameVar, paramIdVar, tupleKey, tupleKeyVar, sign, pattern, compType, sort);
	}

	@Override
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new ParameterCriteriaPE(this, parentPage);
	}

	@Override
	public void validate(String elementPath, ValidationResults results) {
		for (Variable var : values) {
			var.validate(elementPath, results);
		}
		ItemType desc = (ItemType) results.getBufferData();
		if (paramName != null) {
			if (desc.getParameter(paramName) == null) {
				results.addError(elementPath + " > " + getKey(), "'" + desc.getName() + "' item does not contain '" + paramName + "'");
			}
			if (paramNameVar != null || paramIdVar != null) {
				results.addError(elementPath + " > " + getKey(), "Only one attribyte of (name, name-var, id-var) are allowed");
			}
		} else if (paramNameVar != null) {
			if (!paramNameVar.startsWith("$") && pageModel.getInitVariablePE(paramNameVar) == null) {
				results.addError(elementPath + " > " + getKey(), "There is no '" + paramNameVar + "' variable in current page");
			}
			if (paramIdVar != null) {
				results.addError(elementPath + " > " + getKey(), "Only one attribyte of (name, name-var, id-var) are allowed");
			}
		}
		if (tupleKeyVar != null) {
			if (!tupleKeyVar.startsWith("$") && pageModel.getInitVariablePE(tupleKeyVar) == null) {
				results.addError(elementPath + " > " + getKey(), "There is no '" + tupleKeyVar + "' variable in current page");
			}
		}
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
