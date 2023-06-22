package ecommander.persistence.itemquery;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import ecommander.model.item.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
/**
 * 
 * Используестя в aggregation. По нему происходит группировка парамтера группировки.
 * Делает все аналогично FilterParameterCriteria, только добавляет еще 2 дополнительный куска SQL - в SELECT и в GROUP BY
 * Может быть много таких критериев.
 * @author EEEE
 *
 */
class AggregationCriteria implements FilterCriteria, PossibleMainCriteria {
	
	protected final FilterParameterCriteria baseCriteria;
	
	AggregationCriteria(FilterParameterCriteria baseCriteria) {
		this.baseCriteria = baseCriteria;
	}

	public void appendQuery(TemplateQuery query) {
		String valCol = getSelectedColumnName();
		// Добавление в блок SELECT параметра группировки
		query.getSubquery(ItemQuery.AGG_PARAMS_OPT).sql(", " + valCol);
		// Добавление в блок GROUP BY параметра группировки
		TemplateQuery groupPart = query.getSubquery(ItemQuery.GROUP_PARAM_REQ);
		if (!groupPart.isEmpty())
			groupPart.sql(", ");
		groupPart.sql(valCol);
		// Добавление базового критерия
		baseCriteria.appendQuery(query);
	}

	String getResultColumnName() {
		return baseCriteria.tableName + '.' + DBConstants.ItemIndexes.VALUE;
	}
	
	ParameterDescription getParam() {
		return baseCriteria.param;
	}

	public void setMain() {
		baseCriteria.setMain();
	}

	public boolean isMain() {
		return baseCriteria.isMain();
	}

	public void useParentCriteria() {
		baseCriteria.useParentCriteria();
	}

	public boolean isNotBlank() {
		return baseCriteria.isNotBlank();
	}

	public String getSelectedColumnName() {
		return baseCriteria.getTableName() + '.' + DBConstants.ItemIndexes.VALUE;
	}

	public BooleanQuery appendLuceneQuery(BooleanQuery query, Occur occur) {
		return baseCriteria.appendLuceneQuery(query, occur);
	}

	public String getParentColumnName() {
		return baseCriteria.getTableName() + '.' + DBConstants.ItemIndexes.ITEM_PARENT;
	}

	public boolean isEmptySet() {
		return baseCriteria.isEmptySet();
	}

	public String getTableName() {
		return baseCriteria.getTableName();
	}
}
