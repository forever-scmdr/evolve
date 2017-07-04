package ecommander.persistence.itemquery;

import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Параметр, значение которого подвергается группировке
 * @author E
 *
 */
class MainAggregationCriteria extends AggregationCriteria implements ItemQuery.Const {

	private String function = null;
	private ArrayList<AggregationCriteria> groupByExtra = new ArrayList<>(3);

	MainAggregationCriteria(ParameterDescription param, ItemType item, String tableName, String function) {
		super(new GroupOnlyParamCriteria(param, item, tableName));
		if (!StringUtils.isBlank(function))
			this.function = function;
	}

	@Override
	public void appendQuery(TemplateQuery query) {
		String valCol = baseCriteria.INDEX_TABLE + '.' + DBConstants.ItemIndexes.II_VALUE;
		String selectPart = valCol;
		// Добавление в блок SELECT параметра группировки
		if (!StringUtils.isEmpty(function)) {
			selectPart = function + "(" + valCol + ")";
		}
		query.getSubquery(GROUP_PARAMS_SELECT).sql(selectPart + " AS " + GROUP_PARAM_COL);
		// группировка происходит по значению данного параметра (не по значениям других параметров)
		boolean isSelfGrouping = groupByExtra.size() == 0;
		// Если не заданы параметры группировки...
		if (isSelfGrouping) {
			// Добавление в блок GROUP BY параметра группировки
			TemplateQuery groupPart = query.getSubquery(GROUP);
			if (!groupPart.isEmpty())
				groupPart.sql(", ");
			// ... и не задана функция - в их качестве задается значение самого параметра 
			// в GROUP BY Подставляется колонка значений параметра, который агрегируется
			if (function == null)
				groupPart.sql(valCol);
			// ... и задана функция - то в их качестве задается ID самого параметра (в запросе вернется только одно значение - MAX, MIN и т.д.)
			// в GROUP BY Подставляется ID параметра, который агрегируется
			else
				groupPart.sql(baseCriteria.INDEX_TABLE + '.' + DBConstants.ItemIndexes.II_PARAM);
			// Добавление базового критерия
			baseCriteria.appendQuery(query);
		} else {
			// Добавление базового критерия
			baseCriteria.appendQuery(query);
			for (AggregationCriteria aggregationCriteria : groupByExtra) {
				aggregationCriteria.appendQuery(query);
			}
		}
	}

	public void addAggregationParameterCriteria(AggregationCriteria extra) {
		groupByExtra.add(extra);
	}

	// TODO ORDER BY в фильтре

}
