package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Группа критериев, которая является одной из нескольких, каждая из которых является достаточной для удовлетворения
 * криетриев фильтра.
 * Каждая такая группа объединяется с другими такими группами логическим знаком OR в общем SQL запросе
 * @author EEEE
 *
 */
class CriteriaGroup implements FilterCriteria, ItemQuery.Const {

	protected final List<FilterCriteria> criterias;
	protected final ArrayList<AssociatedItemCriteriaGroup> assocCriterias;
	protected final String groupId; // ID группы, нужен для названия таблиц параметров
	protected final ItemType item;
	protected final HashMap<Integer, String> paramTableNames = new HashMap<>();

	CriteriaGroup(String optionId, ItemType item) {
		this.groupId = optionId;
		this.item = item;
		criterias = new ArrayList<>();
		assocCriterias = new ArrayList<>();
	}

	public void appendQuery(TemplateQuery query) {
		if (!isNotBlank())
			return;
		for (FilterCriteria criteria : criterias) {
			if (criteria.isNotBlank()) {
				criteria.appendQuery(query);
			}
		}
		for (FilterCriteria assocCriteria : assocCriterias) {
			if (assocCriteria.isNotBlank()) {
				assocCriteria.appendQuery(query);
			}
		}
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
		String tableName = "F" + criterias.size();
		// Одно значение
		if (values.size() == 1)
			addOptimized(new SingleParamCriteria(param, item, values.get(0), sign, pattern, tableName, groupId, isOption(), compType));
		// Множество значений с выбором любого варианта (параметр соответствует любому из значений)
		else if (values.size() > 0 && (compType == Compare.ANY || compType == Compare.SOME))
			addOptimized(new MultipleParamCriteria(param, item, values, sign, tableName, groupId, isOption(), compType));
		// Множество значений с выбором каждого варианта (параметр соответствует всем значениям)
		else if (values.size() > 0) {
			Compare localComp = compType == Compare.EVERY ? Compare.SOME : Compare.ANY;
			for (String value : values) {
				criterias.add(new SingleParamCriteria(param, item, value, sign, pattern, tableName, groupId, isOption(), localComp));
				tableName = groupId + "F" + criterias.size();
			}
		} else
			addOptimized(new SingleParamCriteria(param, item, "", sign, pattern, tableName, groupId, isOption(), compType));
	}

	private void addOptimized(ParameterCriteria crit) {
		optimizeJoin(crit);
		criterias.add(crit);
	}
	/**
	 * Оптимизировать join для критерия
	 * Если уже есть соединение с индексной таблицей такого параметра, то повторное соединение не нужно
	 * @param crit
	 */
	protected void optimizeJoin(ParameterCriteria crit) {
		if (crit.isNotBlank()) {
			String joinedTableName = paramTableNames.get(crit.getParameterId());
			if (joinedTableName != null) {
				crit.optimizeJoins(joinedTableName);
			} else {
				paramTableNames.put(crit.getParameterId(), crit.getIndexTableName());
			}
		}
	}

	/**
	 * Добавить критерий по параметру потомка
	 * @param item
	 * @param assocId
	 * @param type
	 * @return
	 */
	public AssociatedItemCriteriaGroup addAssociatedCriteria(ItemType item, byte assocId, AssociatedItemCriteriaGroup.Type type) {
		String critId = (type == AssociatedItemCriteriaGroup.Type.CHILD ? "C" : "P") + assocCriterias.size() + groupId;
		AssociatedItemCriteriaGroup newCrit = new AssociatedItemCriteriaGroup(critId, item, assocId, type, null, this.item);
		assocCriterias.add(newCrit);
		return newCrit;
	}

	/**
	 * Добавить критерий предшественника
	 * @param assocName
	 * @param sign
	 * @param itemIds
	 * @param compType
	 */
	public void addPredecessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		criterias.add(new PredecessorCriteria(item, sign, itemIds, ItemTypeRegistry.getAssocId(assocName),
				groupId + "R" + criterias.size(), compType));
	}

	/**
	 * Добавить критерий потомка
	 * @param assocName
	 * @param sign
	 * @param itemIds
	 * @param compType
	 */
	public void addSuccessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		criterias.add(new SuccessorCriteria(sign, itemIds, ItemTypeRegistry.getAssocId(assocName),
				groupId + "R" + criterias.size(), compType));
	}

	public boolean isNotBlank() {
		for (FilterCriteria criteria : criterias) {
			if (criteria.isNotBlank())
				return true;
		}
		for (AssociatedItemCriteriaGroup criteria : assocCriterias) {
			if (criteria.isNotBlank())
				return true;
		}
		return false;
	}
	
	public boolean isEmptySet() {
		for (FilterCriteria criteria : criterias) {
			if (criteria.isEmptySet())
				return true;
		}
		for (AssociatedItemCriteriaGroup criteria : assocCriterias) {
			if (criteria.isEmptySet())
				return true;
		}
		return false;
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		if (!isNotBlank())
			return queryBuilder;
		BooleanQuery.Builder innerBuilder = new BooleanQuery.Builder();
		Occur innerOccur = Occur.MUST;
		for (FilterCriteria criteria : criterias) {
			if (criteria.isNotBlank())
				criteria.appendLuceneQuery(innerBuilder, innerOccur);
		}
		if (queryBuilder == null)
			return innerBuilder;
		if (occur == null)
			occur = Occur.MUST;
		BooleanQuery innerQuery = innerBuilder.build();
		if (innerQuery.clauses().size() > 0)
			queryBuilder.add(innerBuilder.build(), occur);
		return queryBuilder;
	}

	protected boolean isOption() {
		return true;
	}
}
