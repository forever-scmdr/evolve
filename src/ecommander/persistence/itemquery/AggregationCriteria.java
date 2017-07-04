package ecommander.persistence.itemquery;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;

/**
 * 
 * Используестя в aggregation. По нему происходит группировка парамтера группировки.
 * Делает все аналогично FilterParameterCriteria, только добавляет еще 2 дополнительный куска SQL - в SELECT и в GROUP BY
 * Может быть много таких критериев.
 *
 * Нахождение минимальной цены для телевизоров по группам размера экрана (не менее 42 дюймов) и
 * технологии экрана (без критерия)
 *
 * Например
 *  - 42 TFT - 300
 *  - 42 VA - 350
 *  - 42 OLED - 1500
 *  - 46 VA - 500
 *  - 46 PLASMA - 1000
 *  - 46 OLED - 2000
 *  ...
 *
 *  <item name="tv">
 *      <aggragation function="MIN" parameter="price">
 *          <parameter name="size" sign="&gt;="><var var="42"/></parameter>
 *          <parameter name="technology"/>
 *      </aggragation>
 *  </item>
 * @author EEEE
 *
 */
class AggregationCriteria implements FilterCriteria, ItemQuery.Const {
	
	protected final FilterParameterCriteria baseCriteria;
	
	AggregationCriteria(FilterParameterCriteria baseCriteria) {
		this.baseCriteria = baseCriteria;
	}

	public void appendQuery(TemplateQuery query) {
		final String VALUE_COL = baseCriteria.getParameterColumnName();
		// Добавление в блок SELECT параметра группировки
		query.getSubquery(GROUP_PARAMS_SELECT).sql(", " + VALUE_COL);
		// Добавление в блок GROUP BY параметра группировки
		TemplateQuery group = query.getSubquery(GROUP);
		if (!group.isEmpty())
			group.sql(", ");
		group.sql(VALUE_COL);
		// Добавление базового критерия
		baseCriteria.appendQuery(query);
	}

	ParameterDescription getParam() {
		return baseCriteria.param;
	}

	/**
	 * Вернуть колонку для группировки (нужна для сортировки по этой колонке)
	 * @return
	 */
	String getParameterColumnName() {
		return baseCriteria.getParameterColumnName();
	}

	public boolean isNotBlank() {
		return true;
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		return baseCriteria.appendLuceneQuery(queryBuilder, occur);
	}

	public boolean isEmptySet() {
		return baseCriteria.isEmptySet();
	}
}
