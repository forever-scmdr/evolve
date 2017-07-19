package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс, который создает SQL запрос для поиска по фильтру
 * @author EEEE
 *
 */
public final class FilterSQLCreator implements FilterCriteria {
	// Опции фильтра (блоки критериев, объединенные логическим знаком OR)
	// обычно в фильтре нет опций, их спользование - редкий случай
	private ArrayList<CriteriaGroup> options = null;
	// Главные критерии фильтра (без опций)
	private CriteriaGroup mainFilter = null;
	// Агрегация. При ее наличии выполняется не обычный запрос, а запрос GROUP BY
	// Если нужна группировка по нескольким независимым параметрам, то они добавляются прямо в это поле
	// Также каждый критерий группировки хранит признак, нужно ли сортировать по нему.
	// При наличии группировки обычные критерии сортировки (SortingCriteria) игнорируются
	private MainAggregationCriteria mainAggCriteria = null;
	// Критерии сортировки. Используются только в случае, если нет группировки
	private ArrayList<SortingCriteria> sortings = null;
	// Критерии параметров ассоцированных айтемов. Сами критерии хранятся в соответствюущих группах критериев,
	// главной или опциях. Здесь хранится текущая ассоциация, в которую добавляются сами критерии. Полкольку ассоциации
	// могут вкладываться друг в друга, они хранятся в виде стека.
	// Процесс работы такой же как с опциями. Пользователь открывает ассоциацию, добавляет параметры. Может закрыть
	// ассоциацию, а может открыть новую, тогда она будет вложена в ранее открытую, и критерии будут добавлятся в нее.
	private LinkedList<CriteriaGroup> currentGroup = new LinkedList<>();


	FilterSQLCreator(ItemType item) {
		mainFilter = new CriteriaGroup("", item);
		currentGroup.push(mainFilter);
	}

	final void addSorting(ParameterDescription param, String direction, List<String> values) {
		if (sortings == null)
			sortings = new ArrayList<>();
		sortings.add(new SortingCriteria(param, mainFilter.item, "S" + sortings.size(), direction, values));
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
		mainAggCriteria = new MainAggregationCriteria(param, mainFilter.item, ItemQuery.Const.GROUP_MAIN_TABLE, function, sorting);
	}

	/**
	 * Добавить опцию к фильтру (опция содержит один или несколько притериев,
	 * все опции фильтра объединяются знаком OR)
	 * Созданная опция становится активной. Критерии , добавляемые к фильтру, добавляются в новую опцию, а не в фильтр
	 * @return
	 */
	final void startOption() {
		if (currentGroup.size() > 1)
			throw new IllegalStateException("Option must be top level element in filter criteria tree");
		if (options == null)
			options = new ArrayList<>(2);
		CriteriaGroup option = new CriteriaGroup("O" + options.size(), mainFilter.item);
		options.add(option);
		currentGroup.push(option);
	}

	/**
	 * Завершить опцию
	 * Добавляемые критерии после завершения опции будут добавляться в сам фильтр
	 */
	final void endOption() {
		if (currentGroup.size() != 2)
			throw new IllegalStateException("Illegal attempt to close option in wrong position in filter criteria tree");
		if (currentGroup.peek() instanceof AssociatedItemCriteriaGroup)
			throw new IllegalStateException("Illegal attempt to close option while associated criteria is not closed");
		currentGroup.pop();
	}

	/**
	 * Добавить  группу критериев по параметрам потомков или предков
	 * @param item
	 * @param assocId
	 * @param type
	 * @return
	 */
	public void startAssociatedGroup(ItemType item, byte assocId, AssociatedItemCriteriaGroup.Type type) {
		AssociatedItemCriteriaGroup newCrit = currentGroup.peek().addAssociatedCriteria(item, assocId, type);
		currentGroup.push(newCrit);
	}

	/**
	 * Завершить группу критериев по параметрам потомков или предков
	 */
	public void endAssociatedGroup() {
		if (currentGroup.size() < 2)
			throw new IllegalStateException("Illegal attempt to close associated criteria group in wrong position in filter criteria tree");
		if (!(currentGroup.peek() instanceof AssociatedItemCriteriaGroup))
			throw new IllegalStateException("Illegal attempt to close associated criteria group while another group type is open");
		currentGroup.pop();
	}
	/**
	 * Добавить критерий, одиночный или множественный
	 * @param param
	 * @param values
	 * @param sign
	 * @param pattern
	 * @param compType
	 */
	public void addParameterCriteria(ParameterDescription param, ItemType item, List<String> values, String sign, String pattern,
	                                 Compare compType) {
		currentGroup.peek().addParameterCriteria(param, item, values, sign, pattern, compType);
	}

	/**
	 * Добавить критерий предшественника
	 * @param assocName
	 * @param sign
	 * @param itemIds
	 * @param compType
	 */
	public void addPredecessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		currentGroup.peek().addPredecessors(assocName, sign, itemIds, compType);
	}

	/**
	 * Добавить критерий потомка
	 * @param assocName
	 * @param sign
	 * @param itemIds
	 * @param compType
	 */
	public void addSuccessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		currentGroup.peek().addSuccessors(assocName, sign, itemIds, compType);
	}

	@Override
	public void appendQuery(TemplateQuery query) {
		// Добавляется простой притерий
		if (mainFilter.isNotBlank()) {
			query.AND();
			mainFilter.appendQuery(query);
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
			if (hasSorting()) {
				for (SortingCriteria sorting : sortings) {
					sorting.appendQuery(query);
				}
			}
		}
	}

	boolean hasAggregation() {
		return mainAggCriteria != null;
	}

	boolean hasOptions() {
		return options != null;
	}
	
	MainAggregationCriteria getMainAggregationCriteria() {
		return mainAggCriteria;
	}

	boolean hasSorting() {
		return sortings != null;
	}

	@Override
	public boolean isNotBlank() {
		if (mainFilter.isNotBlank())
			return true;
		for (CriteriaGroup option : options) {
			if (!option.isNotBlank())
				return false;
		}
		return true;
	}

	@Override
	public boolean isEmptySet() {
		if (mainFilter.isEmptySet())
			return true;
		boolean isEmptySet = true;
		for (CriteriaGroup option : options) {
			isEmptySet &= option.isEmptySet();
		}
		return isEmptySet;
	}

	@Override
	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		return mainFilter.appendLuceneQuery(queryBuilder, occur);
	}
}
