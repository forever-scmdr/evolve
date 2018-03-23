package ecommander.persistence.itemquery;

import ecommander.fwk.FilterProcessException;
import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.model.filter.*;
import ecommander.pages.var.FilterStaticVariable;

import java.util.List;

/**
 * Класс, который составляет SQL фильтр по пользовательскому фильтру
 */
class UserFilterSQLCreator implements FilterDefinitionVisitor {
	private FilterSQLCreator sqlCreator;
	private ItemType item;
	private FilterStaticVariable userInput;
	
	UserFilterSQLCreator(FilterSQLCreator filterCreator, ItemType item, FilterStaticVariable userInput) {
		if (filterCreator == null || item == null)
			throw new IllegalArgumentException("Impossible to create user filter without FilterSQLCreator or ItemType");
		this.sqlCreator = filterCreator;
		this.item = item;
		this.userInput = userInput;
	}
	
	public void visitGroup(CriteriaGroupDef group) throws FilterProcessException {
		// ничего не делать
	}
	
	public void visitInput(InputDef input) throws FilterProcessException {
		for (FilterDefPart part : input.getCriterias()) {
			CriteriaDef criteria = (CriteriaDef)part;
			ParameterDescription param = item.getParameter(criteria.getParamName());
			List<String> values = userInput.getValue(input.getId());
			if (values.size() > 0)
				sqlCreator.addParameterCriteria(param, item, values, criteria.getSign(), criteria.getPattern(), Compare.ANY);
		}
	}
}
