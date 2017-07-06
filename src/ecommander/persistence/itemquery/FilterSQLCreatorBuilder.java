package ecommander.persistence.itemquery;

import java.util.List;
import java.util.Stack;

import ecommander.fwk.FilterProcessException;
import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.model.filter.CriteriaDef;
import ecommander.model.filter.CriteriaGroupDef;
import ecommander.model.filter.FilterDefPart;
import ecommander.model.filter.FilterDefinitionVisitor;
import ecommander.model.filter.FilterRootDef;
import ecommander.model.filter.InputDef;
import ecommander.pages.var.FilterStaticVariable;
import ecommander.pages.variables.FilterStaticVariablePE;

class FilterSQLCreatorBuilder implements FilterDefinitionVisitor {
	private FilterSQLCreator sqlCreator;
	private Stack<CriteriaGroupDef> sourceStack;
	private Stack<CriteriaGroup> destStack;
	private ItemQuery query;
	private FilterStaticVariable userInput;
	
	FilterSQLCreatorBuilder(ItemQuery query, FilterStaticVariable userInput) {
		this.sourceStack = new Stack<>();
		this.destStack = new Stack<>();
		this.query = query;
		this.userInput = userInput;
	}
	
	public void visitGroup(CriteriaGroupDef group) throws FilterProcessException {
		if (sqlCreator == null) {
			FilterRootDef filterRoot = (FilterRootDef) group;
			sqlCreator = query.createFilter(filterRoot.getSign());
			sourceStack.push(filterRoot);
			destStack.push(sqlCreator);
		} else {
			checkParents(group);
			sourceStack.push(group);
			destStack.push(destStack.peek().addGroup(group.getSign()));
		}
	}
	
	public void visitInput(InputDef input) throws FilterProcessException {
		checkParents(input);
		ItemType item = query.getItemToFilter();
		for (FilterDefPart part : input.getCriterias()) {
			CriteriaDef criteria = (CriteriaDef)part;
			ParameterDescription param = item.getParameter(criteria.getParamName());
			List<String> values = userInput.getValue(input.getId());
			destStack.peek().addParameterCriteria(param, item, values, criteria.getSign(), criteria.getPattern(), Compare.ANY);
		}
	}
	/**
	 * Проверить, являестя ли проверяемая часть вложенной в часть на вершине стека.
	 * Если нет, удалять из вершины стека части до тех пор, пока не найдется предок проверяемой части
	 * @param part
	 */
	private void checkParents(FilterDefPart part) throws FilterProcessException {
		while (sourceStack.peek().getId() != part.getParentId()) {
			sourceStack.pop();
			destStack.pop();
		}
		if (destStack.isEmpty())
			throw new FilterProcessException("Filter format is incorrect. No parent match for id=" + part.getParentId());
	}
	
	public FilterSQLCreator getSqlCreator() {
		return sqlCreator;
	}
}
