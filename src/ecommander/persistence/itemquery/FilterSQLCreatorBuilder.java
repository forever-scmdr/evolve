package ecommander.persistence.itemquery;

import ecommander.fwk.FilterProcessException;
import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.model.filter.*;
import ecommander.pages.var.FilterStaticVariable;

import java.util.List;

class FilterSQLCreatorBuilder implements FilterDefinitionVisitor {
	private FilterSQLCreator sqlCreator;
	private ItemQuery query;
	private FilterStaticVariable userInput;
	
	FilterSQLCreatorBuilder(ItemQuery query, FilterStaticVariable userInput) {
		this.query = query;
		this.userInput = userInput;
	}
	
	public void visitGroup(CriteriaGroupDef group) throws FilterProcessException {
		if (sqlCreator == null) {
			sqlCreator = query.createFilter();
		}
	}
	
	public void visitInput(InputDef input) throws FilterProcessException {
		ItemType item = query.getItemToFilter();
		for (FilterDefPart part : input.getCriterias()) {
			CriteriaDef criteria = (CriteriaDef)part;
			ParameterDescription param = item.getParameter(criteria.getParamName());
			List<String> values = userInput.getValue(input.getId());
			sqlCreator.addParameterCriteria(param, item, values, criteria.getSign(), criteria.getPattern(), Compare.ANY);
		}
	}

	FilterSQLCreator getSqlCreator() {
		return sqlCreator;
	}
}
