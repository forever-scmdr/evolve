package ecommander.pages.filter;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
/**
 * Критерий фильтра, в котором параметр определяется по его ID, который хранится в переменной страницы
 * @author EEEE
 *
 */
class IdVariableParameterCriteriaPE extends VariableParameterCriteriaPE {
	
	IdVariableParameterCriteriaPE(String paramIdVar, String sign, String pattern, Compare compType, String sort) {
		super(paramIdVar, sign, pattern, compType, sort);
	}

	@Override
	public ParameterDescription getParam(ItemType itemDesc) {
		int paramId = Integer.parseInt(pageModel.getVariable(paramNameVar).writeSingleValue());
		return itemDesc.getParameter(paramId);
	}

}
