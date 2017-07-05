package ecommander.persistence.itemquery;

import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
/**
 * Критерий только для группировки, не содержит значений
 * @author EEEE
 *
 */
class GroupOnlyParamCriteria extends ParameterCriteria {

	GroupOnlyParamCriteria(ParameterDescription param, ItemType item, String tableName) {
		super(param, item, tableName);
	}

	@Override
	protected void appendParameterValue(TemplateQuery query) {
		// Ничего добавлять не надо
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		// Ничего не добавляется
		return queryBuilder;
	}

	public boolean isNotBlank() {
		return true;
	}
}
