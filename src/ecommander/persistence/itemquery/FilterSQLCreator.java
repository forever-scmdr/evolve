package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс, который создает SQL запрос для поиска по фильтру
 * @author EEEE
 *
 */
public final class FilterSQLCreator extends CriteriaGroup {
	// Опции фильтра (блоки критериев, объединенные логическим знаком OR)
	// обычно в фильтре нет опций, их спользование - редкий случай
	private ArrayList<CriteriaGroup> options = null;
	// Агрегация. При ее наличии выполняется не обычный запрос, а запрос GROUP BY
	// Если нужна группировка по нескольким независимым параметрам, то они добавляются прямо в это поле
	// Также каждый критерий группировки хранит признак, нужно ли сортировать по нему.
	// При наличии группировки обычные критерии сортировки (SortingCriteria) игнорируются
	private MainAggregationCriteria mainAggCriteria = null;
	// Критерии сортировки. Используются только в случае, если нет группировки
	private ArrayList<SortingCriteria> sortings = null;

	FilterSQLCreator(ItemType item) {
		super("", item);
	}

	final void addSorting(ParameterDescription param, String direction, List<String> values) {
		if (sortings == null)
			sortings = new ArrayList<>();
		sortings.add(new SortingCriteria(param, item, "S" + criterias.size(), direction, values));
	}
	/**
	 * Добавить параметр, по значениям которого должна происходить групировка
	 * Этот параметр также может содержать критерий поиска
	 * @param param
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	final void addAggregationParameterCriteria(ParameterDescription param, List<String> values, String sign,
	                                           String pattern, Compare compType, String sortDirection) {
		if (mainAggCriteria == null)
			throw new IllegalArgumentException("Must not add independent grouping criteria before main aggregation criteria is set");
		mainAggCriteria.addAggregationParameterCriteria(param, values, sign, pattern, compType, sortDirection);
	}
	/**
	 * Добавить параметр, значения которого подвергаются группировке
	 * @param param
	 * @param function
	 * @param sorting
	 */
	final void addMainAggregationParameterCriteria(ParameterDescription param, String function, String sorting) {
		mainAggCriteria = new MainAggregationCriteria(param, item, ItemQuery.Const.GROUP_MAIN_TABLE, function, sorting);
	}

	@Override
	public void appendQuery(TemplateQuery query) {
		// Добавляется простой притерий
		if (super.isNotBlank()) {
			query.AND();
			super.appendQuery(query);
		}
		// Добавляются опции
		boolean wereNoOptions = true;
		for (CriteriaGroup option : options) {
			if (!option.isEmptySet() && option.isNotBlank()) {
				if (wereNoOptions) {
					query.sql(" AND ((");
					wereNoOptions = false;
				} else {
					query.sql(") OR (");
				}
				option.appendQuery(query);
				query.sql(")");
			}
		}
		if (!wereNoOptions) {
			query.sql(")");
		}
		// Добавляется группировка
		if (hasAggregation()) {
			mainAggCriteria.appendQuery(query);
		} else {
			// Добавляется обычная сортировка
			for (SortingCriteria sorting : sortings) {
				sorting.appendQuery(query);
			}
		}
	}

	boolean hasAggregation() {
		return mainAggCriteria != null;
	}
	
	MainAggregationCriteria getMainAggregationCriteria() {
		return mainAggCriteria;
	}

	boolean hasSorting() {
		return sortings != null;
	}

	@Override
	public boolean isNotBlank() {
		if (super.isNotBlank())
			return true;
		for (CriteriaGroup option : options) {
			if (!option.isNotBlank())
				return false;
		}
		return true;
	}

	@Override
	public boolean isEmptySet() {
		if (super.isEmptySet())
			return true;
		boolean isEmptySet = true;
		for (CriteriaGroup option : options) {
			isEmptySet &= option.isEmptySet();
		}
		return isEmptySet;
	}
}
