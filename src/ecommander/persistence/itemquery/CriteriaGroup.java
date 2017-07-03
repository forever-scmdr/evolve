package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import java.util.ArrayList;
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
	protected final String optionId; // ID группы, нужен для названия таблиц параметров
	
	CriteriaGroup(String optionId) {
		this.optionId = optionId;
		criterias = new ArrayList<>();
	}

	public void appendQuery(TemplateQuery query) {
		if (!isNotBlank())
			return;
		TemplateQuery wherePart = query.getSubquery(WHERE);
		wherePart.sql(" (");
		boolean notFirst = false;
		for (FilterCriteria criteria : criterias) {
			if (criteria.isNotBlank()) {
				if (notFirst)
					wherePart.AND();
				criteria.appendQuery(query);
				notFirst = true;
			}
		}
		wherePart.sql(") ");
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
		String tableName = optionId + 'F' + criterias.size();
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
				tableName = optionId + 'F' + criterias.size();
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

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		if (!isNotBlank())
			return queryBuilder;
		BooleanQuery.Builder innerBuilder = new BooleanQuery.Builder();
		Occur innerOccur = Occur.MUST;
		for (FilterCriteria criteria : criterias) {
			if (criteria.isNotBlank())
				appendLuceneQuery(innerBuilder, innerOccur);
		}
		if (queryBuilder == null)
			return innerBuilder;
		queryBuilder.add(innerBuilder.build(), occur);
		return queryBuilder;
	}
}
