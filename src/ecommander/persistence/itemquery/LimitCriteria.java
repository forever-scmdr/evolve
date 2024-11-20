package ecommander.persistence.itemquery;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;

import ecommander.persistence.common.TemplateQuery;
/**
 * 
 * @author E
 *
 */
class LimitCriteria implements FilterCriteria {

	private int limit;
	private int page;
	
	LimitCriteria(int limit, int page) {
		this.limit = limit > 0 ? limit : 1;
		this.page = page;
	}

	public void appendQuery(TemplateQuery query) {
		query = query.getSubquery(ItemQuery.LIMIT_OPT);
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

	public BooleanQuery appendLuceneQuery(BooleanQuery query, Occur occur) {
		// Ничего не добавляется
		return query;
	}

	public boolean isEmptySet() {
		return false;
	}
}
