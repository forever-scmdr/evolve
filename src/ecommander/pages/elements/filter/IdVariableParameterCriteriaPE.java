package ecommander.pages.elements.filter;

import ecommander.model.item.Compare;
import ecommander.model.item.ItemType;
import ecommander.model.item.ParameterDescription;
/**
 * Критерий фильтра, в котором параметр определяется по его ID, который хранится в переменной страницы
 * @author EEEE
 *
 */
class IdVariableParameterCriteriaPE extends VariableParameterCriteriaPE {
	
	IdVariableParameterCriteriaPE(String paramIdVar, String sign, String pattern, Compare compType) {
		super(paramIdVar, sign, pattern, compType);
	}

	@Override
	public ParameterDescription getParam(ItemType itemDesc) {
		int paramId = Integer.parseInt(pageModel.getVariable(paramNameVar).output());
		return itemDesc.getParameter(paramId);
	}

}
