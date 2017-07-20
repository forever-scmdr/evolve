package ecommander.persistence.itemquery;

import ecommander.persistence.common.TemplateQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
/**
 * 
 * @author E
 *
 */
class LimitCriteria implements FilterCriteria, ItemQuery.Const {

	private int limit;
	private int page;
	
	LimitCriteria(int limit, int page) {
		this.limit = limit > 0 ? limit : 1;
		this.page = page;
	}

	public void appendQuery(TemplateQuery query) {
		query = query.getSubquery(LIMIT);
		if (query != null) {
			// Если есть страница, то нужно ее учитвать
			if (page > 1) {
				int rowsToSkip = (page - 1) * limit;
				query.sql(" LIMIT " + rowsToSkip + ", " + limit);
			} else {
				query.sql(" LIMIT " + limit);
			}
		}
	}

	public boolean isNotBlank() {
		return true;
	}

	int getLimit() {
		return limit;
	}

	int getPage() {
		return page;
	}
	
	public void useParentCriteria() {
		// ничего не делать
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		// Ничего не добавляется
		return queryBuilder;
	}

	public boolean isEmptySet() {
		return false;
	}
}
