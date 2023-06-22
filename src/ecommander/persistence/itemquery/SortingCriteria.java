package ecommander.persistence.itemquery;

import java.util.List;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import ecommander.model.item.ItemType;
import ecommander.model.item.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;
/**
 * При использовании сортировки нужно учитывать и вес айтема, поэтому происходит еще и соединение
 * с таблицей айтемов
 * @author E
 *
 */
class SortingCriteria extends FilterParameterCriteria {

	private String direction;
	private List<String> values; // для сортировки по значениям параметра (когда есть определенный порядок значений)
	
	SortingCriteria(ParameterDescription param, ItemType item, String tableName, String direction, List<String> values) {
		super(param, item, tableName);
		this.direction = direction;
		if (values != null && values.size() > 0)
			this.values = values;
	}

	@Override
	protected void appendParameterValue(TemplateQuery query) {
		// Добавление в SELECT
		String colName = tableName + '.' + DBConstants.ItemIndexes.VALUE;
		// Нужна такая проверка, потому что при подсчете количества сортировка не применяется и 
		// частей для нее в запросе нет
		boolean needSorting = query.getSubquery(ItemQuery.SORT_VAL_OPT) != null;
		if (needSorting) {
//			// Добавление во FROM соединения с таблицей айтема для учета веса айтема при сортировке
//			query.getSubquery(ItemQuery.FROM_OPT).sql(", " + DBConstants.Item.TABLE + " AS I");
//			query.getSubquery(ItemQuery.WHERE_OPT).getSubquery(ItemQuery.FILTER_JOIN_OPT)
//				.sql("I." + DBConstants.Item.REF_ID + " = ").subquery(ItemQuery.COMMON_COL_OPT).sql(" AND ");
//			// Выбор колонки параметра сортировки
//			query.getSubquery(ItemQuery.SORT_VAL_OPT).sql(", " + colName + " AS " + ItemQuery.SORT_PARAM_COL);
			// Добавление S.II_STRING asc, в ORDER BY
			TemplateQuery sortByPart = query.getSubquery(ItemQuery.SORT_BY_OPT);
			if (sortByPart.isEmpty())
				sortByPart.sql(" ORDER BY ");
			else
				sortByPart.sql(", ");
			if (values != null) {
				sortByPart.sql("FIELD (" + colName + ", ");
				DataTypeMapper.appendPreparedStatementRequestValues(param.getType(), sortByPart, values);
				sortByPart.sql(") ");
			} else {
				sortByPart.sql(colName + " ");
			}
			sortByPart.sql(direction);
			
			//query.getSubquery(ItemQuery.SORT_BY_OPT).sql(" ORDER BY " + colName + " " + direction + ", I." + DBConstants.Item.INDEX_WEIGHT + " ASC");
		}
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
