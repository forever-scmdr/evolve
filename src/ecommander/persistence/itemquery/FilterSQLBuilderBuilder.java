package ecommander.persistence.itemquery;

import java.util.List;
import java.util.Stack;

import ecommander.common.exceptions.FilterProcessException;
import ecommander.model.item.COMPARE_TYPE;
import ecommander.model.item.ItemType;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.filter.CriteriaDef;
import ecommander.model.item.filter.CriteriaGroupDef;
import ecommander.model.item.filter.FilterDefPart;
import ecommander.model.item.filter.FilterDefinitionVisitor;
import ecommander.model.item.filter.FilterRootDef;
import ecommander.model.item.filter.InputDef;
import ecommander.pages.elements.variables.FilterStaticVariablePE;

class FilterSQLBuilderBuilder implements FilterDefinitionVisitor {
	private FilterSQLBuilder builder;
	private Stack<CriteriaGroupDef> sourceStack;
	private Stack<CriteriaGroup> destStack;
	private ItemQuery query;
	private FilterStaticVariablePE userInput;
	
	FilterSQLBuilderBuilder(ItemQuery query, FilterStaticVariablePE userInput) {
		this.sourceStack = new Stack<CriteriaGroupDef>();
		this.destStack = new Stack<CriteriaGroup>();
		this.query = query;
		this.userInput = userInput;
	}
	
	public void visitGroup(CriteriaGroupDef group) throws FilterProcessException {
		if (builder == null) {
			FilterRootDef filterRoot = (FilterRootDef) group;
			builder = query.createFilter(filterRoot.getSign());
			sourceStack.push(filterRoot);
			destStack.push(builder);
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
			destStack.peek().addParameterCriteria(param, item, values, criteria.getSign(), criteria.getPattern(), COMPARE_TYPE.ANY);
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
	
	public FilterSQLBuilder getBuilder() {
		return builder;
	}
}
