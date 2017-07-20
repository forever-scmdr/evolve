package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Параметр, значение которого подвергается группировке
 * @author E
 *
 */
class MainAggregationCriteria extends AggregationCriteria implements ItemQuery.Const {

	private final String function;
	private final ArrayList<AggregationCriteria> groupByExtra = new ArrayList<>(3);
	private final ItemType item;

	MainAggregationCriteria(ParameterDescription param, ItemType item, String tableName, String function, String sortDirection) {
		super(new GroupOnlyParamCriteria(param, item, tableName), sortDirection);
		if (!StringUtils.isBlank(function))
			this.function = function;
		else
			this.function = null;
		this.item = item;
	}

	@Override
	public void appendQuery(TemplateQuery query) {
		String valCol = baseCriteria.INDEX_TABLE + '.' + DBConstants.ItemIndexes.II_VALUE;
		String selectPart = valCol;
		// Добавление в блок SELECT параметра группировки
		if (StringUtils.isNotBlank(function)) {
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
		// Добавление сортировки
		if (hasSorting()) {
			boolean needSorting = query.getSubquery(ORDER) != null;
			if (needSorting) {
				TemplateQuery orderByPart = query.getSubquery(ORDER);
				orderByPart.sql(" ORDER BY ");
				if (isSelfGrouping) {
					orderByPart.sql(getParameterColumnName()).sql(" " + sort);
				} else {
					boolean isNotFirst = false;
					for (AggregationCriteria extra : groupByExtra) {
						if (isNotFirst)
							orderByPart.sql(", ");
						orderByPart.sql(extra.getParameterColumnName()).sql(" " + extra.getSortingDirection());
						isNotFirst = true;
					}
				}
			}
		}
	}

	/**
	 * Добавить независимый притерий группировки
	 * @param param
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 * @param sortDirection
	 */
	public void addAggregationParameterCriteria(ParameterDescription param, List<String> values, String sign,
	                                            String pattern, Compare compType, String sortDirection) {
		ParameterCriteria baseCrit;
		String tableName = baseCriteria.INDEX_TABLE + 'G' + groupByExtra.size();
		// Нет значений
		if (values == null || values.size() == 0)
			baseCrit = new GroupOnlyParamCriteria(param, item, tableName);
			// Одно значение
		else if (values.size() == 1)
			baseCrit = new SingleParamCriteria(param, item, values.get(0), sign, pattern, tableName, compType);
			// Множество значений с выбором любого варианта (параметр соответствует любому из значений)
		else if (values.size() > 0 && (compType == Compare.ANY || compType == Compare.SOME))
			baseCrit = new MultipleParamCriteria(param, item, values, sign, tableName, compType);
			// Множество значений с выбором каждого варианта (параметр соответствует всем значениям)
		else
			throw new IllegalArgumentException("Unsupported filter grouping format");
		AggregationCriteria crit = new AggregationCriteria(baseCrit, sortDirection);
		groupByExtra.add(crit);
	}

	ArrayList<AggregationCriteria> getGroupByExtra() {
		return groupByExtra;
	}

	boolean hasGroupByExtra() {
		return groupByExtra.size() > 0;
	}

	@Override
	String getParameterColumnName() {
		return GROUP_PARAM_COL;
	}
}
