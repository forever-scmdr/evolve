package ecommander.persistence.itemquery;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import ecommander.model.item.ItemType;
import ecommander.model.item.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
/**
 * Критерий только для группировки, не содержит значений
 * @author EEEE
 *
 */
class GroupOnlyParamCriteria extends FilterParameterCriteria {

	GroupOnlyParamCriteria(ParameterDescription param, ItemType item, String tableName) {
		super(param, item, tableName);
	}

	@Override
	protected void appendParameterValue(TemplateQuery query) {
		// Ничего добавлять не надо
	}

	public BooleanQuery appendLuceneQuery(BooleanQuery query, Occur occur) {
		// Ничего не добавляется
		return query;
	}

	public String getParentColumnName() {
		return tableName + '.' + DBConstants.ItemIndexes.ITEM_PARENT;
	}

	public boolean isNotBlank() {
		return true;
	}
}
