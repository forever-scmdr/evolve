package ecommander.pages.filter;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import ecommander.pages.var.Variable;
/**
 * Критерий фильтра, в котором название параметра жестко задано в коде страницы
 * @author EEEE
 *
 */
class HardParameterCriteriaPE extends ParameterCriteriaPE {
	protected String paramName;
	
	HardParameterCriteriaPE(String paramName, String sign, String pattern, Compare compType, String sort) {
		super(sign, pattern, compType, sort);
		this.paramName = paramName;
	}
	
	private HardParameterCriteriaPE(HardParameterCriteriaPE template, ExecutablePagePE parentPage) {
		super(template, parentPage);
		this.paramName = template.paramName;
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new HardParameterCriteriaPE(this, parentPage);
	}

	public void validate(String elementPath, ValidationResults results) {
		for (Variable var : values) {
			var.validate(elementPath, results);
		}
		ItemType desc = (ItemType) results.getBufferData();
		if (desc.getParameter(paramName) == null) {
			results.addError(elementPath + " > " + getKey(), "'" + desc.getName() + "' item does not contain '" + paramName + "'");
		}
	}

	@Override
	public ParameterDescription getParam(ItemType itemDesc) {
		return itemDesc.getParameter(paramName);
	}
	
}
