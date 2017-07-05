package ecommander.persistence.itemquery;

import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import java.util.List;
/**
 * При использовании сортировки нужно учитывать и вес айтема, поэтому происходит еще и соединение
 * с таблицей айтемов
 * @author E
 *
 */
class SortingCriteria extends ParameterCriteria {

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
		String colName = INDEX_TABLE + '.' + DBConstants.ItemIndexes.II_VALUE;
		// Нужна такая проверка, потому что при подсчете количества сортировка не применяется и 
		// частей для нее в запросе нет
		boolean needSorting = query.getSubquery(ORDER) != null;
		if (needSorting) {
			// Добавление S.II_STRING asc, в ORDER BY
			TemplateQuery orderByPart = query.getSubquery(ORDER);
			if (orderByPart.isEmpty())
				orderByPart.sql(" ORDER BY ");
			else
				orderByPart.sql(", ");
			if (values != null) {
				orderByPart.sql("FIELD (" + colName + ", ");
				DataTypeMapper.appendPreparedStatementRequestValues(param.getType(), orderByPart, values);
				orderByPart.sql(") ");
			} else {
				orderByPart.sql(colName + " ");
			}
			orderByPart.sql(direction);
		}
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		// Ничего не добавляется
		return queryBuilder;
	}

	public boolean isNotBlank() {
		return true;
	}

}
