package ecommander.persistence.itemquery;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
/**
 * Параметр, значение которого подвергается группировке
 * @author E
 *
 */
class MainAggregationCriteria extends AggregationCriteria {

	private String function = null;
	private String sortingDirection = null;
	private boolean isSelfGrouping = true; // группировка происходит по значению данного параметра (не по значениям других параметров)
	
	MainAggregationCriteria(ParameterDescription param, ItemType item, String tableName, String function, String sortingDirection) {
		super(new GroupOnlyParamCriteria(param, item, tableName));
		if (!StringUtils.isBlank(function))
			this.function = function;
		this.sortingDirection = sortingDirection;
	}

	@Override
	public void appendQuery(TemplateQuery query) {
		String valCol = baseCriteria.tableName + '.' + DBConstants.ItemIndexes.II_VALUE;
		String selectPart = valCol;
		// Добавление в блок SELECT параметра группировки
		if (!StringUtils.isEmpty(function)) {
			selectPart = function + "(" + valCol + ")";
		}
		query.getSubquery(ItemQuery.GROUP_PARAM_VALS_REQ).sql(selectPart + " AS " + ItemQuery.GROUP_PARAM_COL);
		// Если не заданы параметры группировки...
		if (isSelfGrouping) {
			// Добавление в блок GROUP BY параметра группировки
			TemplateQuery groupPart = query.getSubquery(ItemQuery.GROUP_PARAM_REQ);
			if (!groupPart.isEmpty())
				groupPart.sql(", ");
			// ... и не задана функция - в их качестве задается значение самого параметра 
			// в GROUP BY Подставляется колонка значений параметра, который агрегируется
			if (function == null)
				groupPart.sql(valCol);
			// ... и задана функция - то в их качестве задается ID самого параметра (в запросе вернется только одно значение - MAX, MIN и т.д.)
			// в GROUP BY Подставляется ID параметра, который агрегируется
			else
				groupPart.sql(baseCriteria.tableName + '.' + DBConstants.ItemIndexes.II_PARAM);
		}
		// Добавление в блок ORDER BY параметра группировки
		if (!StringUtils.isEmpty(sortingDirection))
			query.getSubquery(ItemQuery.SORT_BY_OPT).sql(" ORDER BY ").sql(ItemQuery.GROUP_PARAM_COL + " " + sortingDirection);
		// Добавление базового критерия
		baseCriteria.appendQuery(query);
	}

	void setSelfGrouping(boolean isSelfGrouping) {
		this.isSelfGrouping = isSelfGrouping;
	}

	@Override
	String getResultColumnName() {
		return ItemQuery.GROUP_PARAM_COL;
	}
	
	
}
