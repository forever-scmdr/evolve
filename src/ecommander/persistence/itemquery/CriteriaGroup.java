package ecommander.persistence.itemquery;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import ecommander.model.item.COMPARE_TYPE;
import ecommander.model.item.ItemType;
import ecommander.model.item.LOGICAL_SIGN;
import ecommander.model.item.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;

/**
 * Группа критериев, объединенная одним логическим знаком
 * Можно считать, что группа критериев это логическое выражение в скобках в SQL запросе, например 
 * ... WHERE ... (some_size > 0 AND some_size <= 10 AND quantity > 0)
 * 
 * TODO <fix> сделать так, чтобы sorting всегда использовал логический знак AND при добавлении к списку притериев вне зависимости от
 * знака, установленного в группе критериев
 * @author EEEE
 *
 */
class CriteriaGroup implements FilterCriteria {

	protected final List<FilterCriteria> criterias;
	protected final LOGICAL_SIGN sign;
	protected final String groupId; // ID группы, нужен для названия таблиц параметров
	
	CriteriaGroup(LOGICAL_SIGN sign, String groupId) {
		this.sign = sign;
		this.groupId = groupId;
		criterias = new ArrayList<FilterCriteria>();
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
			COMPARE_TYPE compType) {
		String tableName = groupId + 'F' + criterias.size();
		// Одно значение
		if (values.size() == 1)
			criterias.add(new SingleParamCriteria(param, item, values.get(0), sign, pattern, tableName, compType));
		// Множество значений с выбором любого варианта (параметр соответствует любому из значений)
		else if (values.size() > 0 && (compType == COMPARE_TYPE.ANY || compType == COMPARE_TYPE.SOME))
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
	/**
	 * Добавить группу критериев
	 * @param sign
	 */
	final CriteriaGroup addGroup(LOGICAL_SIGN sign) {
		CriteriaGroup group = new CriteriaGroup(sign, "G" + criterias.size());
		criterias.add(group);
		return group;
	}

	public boolean isNotBlank() {
		for (FilterCriteria criteria : criterias) {
			if (criteria.isNotBlank())
				return true;
		}
		return false;
	}
	
	public boolean isEmptySet() {
		if (sign == LOGICAL_SIGN.AND) {
			for (FilterCriteria criteria : criterias) {
				if (criteria.isEmptySet())
					return true;
			}
		} else {
			for (FilterCriteria criteria : criterias) {
				if (!criteria.isEmptySet())
					return false;
			}
		}
		return false;
	}

	public void useParentCriteria() {
		for (FilterCriteria crit : criterias) {
			crit.useParentCriteria();
		}
	}

	protected Occur getOccur() {
		if (sign == LOGICAL_SIGN.OR)
			return Occur.SHOULD;
		return Occur.MUST;
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
