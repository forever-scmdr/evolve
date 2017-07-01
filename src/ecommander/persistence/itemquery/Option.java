package ecommander.persistence.itemquery;

import java.util.ArrayList;
import java.util.List;

import ecommander.model.Compare;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import ecommander.model.ItemType;
import ecommander.model.LOGICAL_SIGN;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;

/**
 * Группа критериев, которая является одной из нескольких, каждая из которых является достаточной для удовлетворения
 * криетриев фильтра.
 * Каждая такая группа объединяется с другими такими группами логическим знаком OR в общем SQL запросе
 * @author EEEE
 *
 */
class Option implements FilterCriteria {

	protected final List<FilterCriteria> criterias;
	protected final String groupId; // ID группы, нужен для названия таблиц параметров
	
	Option(String groupId) {
		this.groupId = groupId;
		criterias = new ArrayList<>();
	}

	public void appendQuery(TemplateQuery query) {
		if (!isNotBlank())
			return;
		TemplateQuery wherePart = query.getSubquery(ItemQuery.WHERE_OPT);
		if (wherePart.getSubquery(ItemQuery.FILTER_JOIN_OPT) == null) {
			if (!wherePart.isEmpty())
				wherePart.sql(" AND ");
			wherePart.subquery(ItemQuery.FILTER_JOIN_OPT).subquery(ItemQuery.FILTER_CRITS_OPT);
		}
		TemplateQuery filterCondition = wherePart.getSubquery(ItemQuery.FILTER_CRITS_OPT);
		filterCondition.sql("(");
		boolean notFirst = false;
		for (FilterCriteria criteria : criterias) {
			if (notFirst && criteria.isNotBlank()) {
				filterCondition.sql(sign.toString());
			}
			if (criteria.isNotBlank()) {
				notFirst = true;
				criteria.appendQuery(query); // !!! Передается изначальный запрос query, а не filterCondition !!!
			}
		}
		filterCondition.sql(")");
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
		String tableName = groupId + 'F' + criterias.size();
		// Одно значение
		if (values.size() == 1)
			criterias.add(new SingleParamCriteria(param, item, values.get(0), sign, pattern, tableName, compType));
		// Множество значений с выбором любого варианта (параметр соответствует любому из значений)
		else if (values.size() > 0 && (compType == Compare.ANY || compType == Compare.SOME))
			criterias.add(new MultipleParamCriteria(param, item, values, sign, tableName, compType));
		// Множество значений с выбором каждого варианта (параметр соответствует всем значениям)
		else if (values.size() > 0) {
			for (String value : values) {
				criterias.add(new SingleParamCriteria(param, item, value, sign, pattern, tableName, compType));
				tableName = groupId + 'F' + criterias.size();
			}
		} else 
			criterias.add(new SingleParamCriteria(param, item, "", sign, pattern, tableName, compType));
	}

	public boolean isNotBlank() {
		for (FilterCriteria criteria : criterias) {
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
		return false;
	}

	public BooleanQuery appendLuceneQuery(BooleanQuery query, Occur occur) {
		if (!isNotBlank())
			return query;
		BooleanQuery innerQuery = new BooleanQuery();
		Occur innerOccur = getOccur();
		for (FilterCriteria criteria : criterias) {
			if (criteria.isNotBlank())
				criteria.appendLuceneQuery(innerQuery, innerOccur);
		}
		if (query == null)
			return innerQuery;
		query.add(innerQuery, occur);
		return query;
	}
}
