package ecommander.persistence.itemquery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.search.BooleanQuery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.LOGICAL_SIGN;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;

/**
 * Класс, который создает SQL запрос для поиска по фильтру
 * @author EEEE
 *
 */
public final class FilterSQLCreator extends CriteriaGroup {

	private ArrayList<CriteriaGroup> options = null; // Опции фильтра (блоки критериев, объединенные логическим знаком OR)
	private ArrayList<AggregationCriteria> aggCriterias = null;
	private MainAggregationCriteria mainAggCriteria = null;
	private boolean isSelfGrouping = true;
	private String itemIdColoumn = null;
	private String itemParentColoumn = null;
	private boolean hasSorting = false;
	private ItemType item;
	
	FilterSQLCreator(ItemType item, LOGICAL_SIGN sign) {
		super(sign, "");
		this.item = item;
	}

	final void addPredecessors(String sign, Collection<Long> itemIds, Compare compType) {
		criterias.add(new PredecessorCriteria(sign, itemIds, groupId + 'R' + criterias.size(), compType));
	}
	
	final void addSuccessors(String sign, Collection<Long> itemIds, Compare compType) {
		criterias.add(new SuccessorCriteria(sign, itemIds, groupId + 'R' + criterias.size(), compType));
	}
	
	final void addSorting(ParameterDescription param, String direction, List<String> values) {
		criterias.add(new SortingCriteria(param, item, "S" + criterias.size(), direction, values));
		hasSorting = true;
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
	final void addAggregationParameterCriteria(ParameterDescription param, List<String> values, String sign, String pattern, Compare compType) {
		FilterParameterCriteria baseCrit = null;
		String tableName = groupId + 'G' + criterias.size();
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
		AggregationCriteria crit = new AggregationCriteria(baseCrit);
		criterias.add(crit);
		if (aggCriterias == null)
			aggCriterias = new ArrayList<AggregationCriteria>(3);
		aggCriterias.add(crit);
		isSelfGrouping = false;
	}
	/**
	 * Добавить параметр, значения которого подвергаются группировке
	 * @param param
	 * @param tableName
	 * @param function
	 * @param sorting
	 */
	final void addMainAggregationParameterCriteria(ParameterDescription param, String function, String sorting) {
		mainAggCriteria = new MainAggregationCriteria(param, item, ItemQuery.GROUP_MAIN_TABLE, function, sorting);
//		if (aggCriterias == null)
//			aggCriterias = new ArrayList<AggregationCriteria>(3);
//		aggCriterias.add(mainAggCriteria);
		criterias.add(mainAggCriteria);
	}
	/**
	 * Добавить критерий поиска по ID пользователя
	 * @param userId
	 */
	final void addUserCriteria(long userId) {
		criterias.add(new UserParamCriteria(userId, "UID"));
	}
	/**
	 * Доабвить критерий поиска по ID группы
	 * @param userId
	 */
	final void addUserGroupCriteria(int groupId) {
		criterias.add(new UserGroupParamCriteria(groupId, "UG"));
	}
	
	@Override
	public void appendQuery(TemplateQuery query) {
		boolean mainFound = findAndSetMainCriteria(this);
		if (!mainFound) {
			throw new RuntimeException("Illegal filter format: there is no valid criteria");
		}
		if (mainAggCriteria != null)
			mainAggCriteria.setSelfGrouping(isSelfGrouping);
		super.appendQuery(query);
	}

	private boolean findAndSetMainCriteria(CriteriaGroup group) {
		for (FilterCriteria crit : group.criterias) {
			if (crit.isNotBlank()) {
				if (crit instanceof PossibleMainCriteria) {
					((PossibleMainCriteria) crit).setMain();
					itemIdColoumn = ((PossibleMainCriteria) crit).getSelectedColumnName();
					itemParentColoumn = ((PossibleMainCriteria) crit).getParentColumnName();
					return true;
				} else if (crit instanceof CriteriaGroup) {
					if (findAndSetMainCriteria((CriteriaGroup) crit))
						return true;
				}
			}
		}
		return false;
	}
	/**
	 * Создать запрос для полнотекстового поиска.
	 * Этот запрос должен быть потом преобразован в Filter, чтобы убрать ранжирование
	 * @param crit
	 * @return
	 */
	BooleanQuery createLuceneFilterQuery() {
		return appendLuceneQuery(null, null);
	}
	
	boolean hasAggregation() {
		return mainAggCriteria != null;
	}
	
	MainAggregationCriteria getMainAggregationCriteria() {
		return mainAggCriteria;
	}
	
	ArrayList<AggregationCriteria> getAggregationCriterias() {
		return aggCriterias;
	}
	
	boolean hasAggregationGroupByParams() {
		return aggCriterias != null;
	}
	
	String getIdColoumnName() {
		return itemIdColoumn;
	}

	String getParentColoumnName() {
		return itemParentColoumn;
	}
	
	boolean hasSorting() {
		return hasSorting;
	}
	
}
