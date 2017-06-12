package ecommander.pages.filter;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.pages.PageElement;
import ecommander.pages.ValidationResults;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElementContainer;
import ecommander.pages.var.VariablePE;

/**
 * Критерий фильтра, в котором название параметра хранится в переменной страницы
 * @author EEEE
 *
 */
class VariableParameterCriteriaPE extends FilterCriteriaPE {
	protected String paramNameVar;
	protected ExecutablePagePE pageModel;

	VariableParameterCriteriaPE(String paramNameVar, String sign, String pattern, Compare compType) {
		super(sign, pattern, compType);
		this.paramNameVar = paramNameVar;
	}
	
	private VariableParameterCriteriaPE(VariableParameterCriteriaPE template, ExecutablePagePE parentPage) {
		super(template, parentPage);
		this.paramNameVar = ((VariableParameterCriteriaPE) template).paramNameVar;
		pageModel = parentPage;
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new VariableParameterCriteriaPE(this, parentPage);
	}

	public void validate(String elementPath, ValidationResults results) {
		for (VariablePE var : values) {
			var.validate(elementPath, results);
		}
		if (pageModel.getVariable(paramNameVar) == null) {
			results.addError(elementPath + " > " + getKey(), "There is no '" + paramNameVar + "' variable in current page");
		}
	}

	@Override
	public ParameterDescription getParam(ItemType itemDesc) {
		return itemDesc.getParameter(pageModel.getVariable(paramNameVar).writeSingleValue());
	}

	@Override
	public boolean isValid() {
		return super.isValid() && !pageModel.getVariable(paramNameVar).isEmpty();
	}

}
