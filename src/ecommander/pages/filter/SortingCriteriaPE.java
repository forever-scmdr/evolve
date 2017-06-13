package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.model.ItemType;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import ecommander.pages.var.StaticVariable;
import ecommander.pages.var.Variable;
import ecommander.pages.var.VariablePE;

/**
 * Критерий сортировки
 * Created by E on 13/6/2017.
 */
public class SortingCriteriaPE implements FilterCriteria {

	private Variable sortingParameter = null;
	private Variable sortingDirection = new StaticVariable("dir", "ASC");

	private SortingCriteriaPE(Variable sortingParameter, Variable sortingDirection, ExecutablePagePE parentPage) {
		this.sortingParameter = sortingParameter.getInited(parentPage);
		if (sortingDirection != null)
			this.sortingDirection = sortingDirection.getInited(parentPage);
	}

	public SortingCriteriaPE(Variable sortingParameter, Variable sortingDirection) {
		this.sortingParameter = sortingParameter;
		if (sortingDirection != null)
			this.sortingDirection = sortingDirection;
	}

	@Override
	public void process(FilterCriteriaContainer cont) throws EcommanderException {
		cont.processSortingCriteriaPE(this);
	}

	@Override
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new SortingCriteriaPE(sortingParameter, sortingDirection, parentPage);
	}

	@Override
	public void validate(String elementPath, ValidationResults results) {
		sortingParameter.validate(elementPath + " > " + getKey(), results);
		if (results.isSuccessful()) {
			if (sortingParameter instanceof StaticVariable
					&& ((ItemType) results.getBufferData()).getParameter(sortingParameter.writeSingleValue()) == null) {
				results.addError(elementPath + " > " + getKey(), "There is no '" + sortingParameter.writeSingleValue() + "' parameter in '"
						+ ((ItemType) results.getBufferData()).getName() + "' item");
			}
			sortingParameter.validate(elementPath + " > " + getKey(), results);
			if (!"ASC".equals(sortingDirection.writeSingleValue()) && !"DESC".equals(sortingDirection.writeSingleValue()))
				results.addError(elementPath + " > " + getKey(),
						"'" + sortingDirection.writeSingleValue() + "' is not valid sorting direction");
		}
	}

	@Override
	public String getKey() {
		return "Sorting";
	}

	@Override
	public String getElementName() {
		return "Sorting";
	}

	public Variable getSortingParameter() {
		return sortingParameter;
	}

	public Variable getSortingDirection() {
		return sortingDirection;
	}
}
