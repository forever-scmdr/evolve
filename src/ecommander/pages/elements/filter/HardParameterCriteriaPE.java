package ecommander.pages.elements.filter;

import ecommander.model.item.Compare;
import ecommander.model.item.ItemType;
import ecommander.model.item.ParameterDescription;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.PageElementContainer;
import ecommander.pages.elements.ValidationResults;
import ecommander.pages.elements.variables.VariablePE;
/**
 * Критерий фильтра, в котором название параметра жестко задано в коде страницы
 * @author EEEE
 *
 */
class HardParameterCriteriaPE extends FilterCriteriaPE {
	protected String paramName;
	
	HardParameterCriteriaPE(String paramName, String sign, String pattern, Compare compType) {
		super(sign, pattern, compType);
		this.paramName = paramName;
	}
	
	private HardParameterCriteriaPE(HardParameterCriteriaPE template, ExecutablePagePE parentPage) {
		super(template, parentPage);
		this.paramName = ((HardParameterCriteriaPE) template).paramName;
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new HardParameterCriteriaPE(this, parentPage);
	}

	public void validate(String elementPath, ValidationResults results) {
		for (VariablePE var : values) {
			var.validate(elementPath, results);
		}
		ItemType desc = (ItemType)results.getBufferData();
		if (desc.getParameter(paramName) == null) {
			results.addError(elementPath + " > " + getKey(), "'" + desc.getName() + "' item does not contain '" + paramName + "'");
		}
	}

	@Override
	public ParameterDescription getParam(ItemType itemDesc) {
		return itemDesc.getParameter(paramName);
	}
	
}
