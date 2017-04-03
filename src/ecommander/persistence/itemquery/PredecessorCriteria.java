package ecommander.persistence.itemquery;

import java.util.ArrayList;
import java.util.Collection;

import ecommander.model.Compare;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
/**
 * Часть запроса для критерия фильтрации <predecessor>
 * Критерий типа айтема добавляется только в случае если этот критерий является главным,
 * т. е. фактически если идет первым в списке критериев фильтра
 * @author E
 *
 */
class PredecessorCriteria implements FilterCriteria, PossibleMainCriteria {

	private static final String IN = "IN";
	private static final String NOT_IN = "NOT IN";
	
	protected final String tableName; // Псевдоним таблицы с параметрами (ItemIndex) для данного параметра
	protected final ArrayList<Long> itemIds;
	protected final String sign;
	// Если критерий является главным, то все остальные критерии приравнивают извлекаемый ID айтема к ID, извлекаемому этим критерием
	private boolean isMainCriteria = false;
	private boolean useParentCriteria = false; // нужно ли использовать критерий поиска по родителю (предку)
	private final boolean isEmptySet;
	
	
	PredecessorCriteria(String sign, Collection<Long> predItemIds, String tableName, Compare type) {
		this.itemIds = new ArrayList<Long>();
		if (predItemIds != null)
			this.itemIds.addAll(predItemIds);
		this.tableName = tableName;
		this.sign = sign.trim();
		if (itemIds.size() == 0 && this.sign.equals(IN) && (type == Compare.EVERY || type == Compare.SOME))
			isEmptySet = true;
		else
			isEmptySet = false;
	}
	
	public final void appendQuery(TemplateQuery query) {
		// Добавление таблицы во FROM
		TemplateQuery fromPart = query.getSubquery(ItemQuery.FROM_OPT);
		if (!fromPart.isEmpty())
			fromPart.sql(", ");
		fromPart.sql(DBConstants.ItemParent.IP_TABLE + " AS " + tableName);
		
		TemplateQuery wherePart = query.getSubquery(ItemQuery.WHERE_OPT);
		
		// Добавление связи между таблицей этого критерия и общей таблицей
		//     !!!     для того, чтобы в результат не попали ссылки (только нормальные айтемы)  DBConstants.ItemParent.ITEM_ID а не REF_ID
		// сейчас REF_ID, потому что по нему построен индекс, а по ITEM_ID индекса нет
		TemplateQuery joinPart = wherePart.getSubquery(ItemQuery.FILTER_JOIN_OPT);
		if (isMainCriteria) {
			joinPart.getOrCreateSubquery(ItemQuery.COMMON_COL_OPT).sql(tableName + '.' + DBConstants.ItemParent.REF_ID);
		} else {
			joinPart.sql(tableName + '.' + DBConstants.ItemParent.REF_ID + " = ").subquery(ItemQuery.COMMON_COL_OPT).sql(" AND ");
		}
		// Чтобы ссылки не попадали - дополнительное условие что ITEM_ID = REF_ID
		joinPart.sql(tableName + '.' + DBConstants.ItemParent.REF_ID + " = " + tableName + '.' + DBConstants.ItemParent.ITEM_ID + " AND ");
		
		// -- НАЧАЛО --  Добавление критерия предшественника
		TemplateQuery critPart = wherePart.getSubquery(ItemQuery.FILTER_CRITS_OPT);
		
		if (useParentCriteria) {
		
			// Добавление списка ID родителей
			if (itemIds.size() > 0) {
				critPart
					.sql(tableName + '.' + DBConstants.ItemParent.IP_PARENT_ID).subquery(ItemQuery.PARENT_CRIT_OPT);
			} else {
				critPart.sql(tableName + '.' + DBConstants.ItemParent.IP_PARENT_ID + " " + sign + " (-1)");
			}
			
			// Добавление критерия родительского айтема (производится централизованно другом месте)
			String parentCritTableName = tableName + "P";
			fromPart.sql(", " + DBConstants.ItemParent.IP_TABLE + " AS " + parentCritTableName);
			joinPart.sql(parentCritTableName + '.' + DBConstants.ItemParent.REF_ID + " = ").subquery(ItemQuery.COMMON_COL_OPT).sql(" AND ");
			critPart.sql(" AND " + parentCritTableName + '.' + DBConstants.ItemParent.IP_PARENT_ID + " IN (")
				.setLongArray(itemIds.toArray(new Long[itemIds.size()]))
				.sql(")");
			
		} else {
			
			// Добавление списка ID родителей
			if (itemIds.size() > 0) {
				critPart
					.sql(tableName + '.' + DBConstants.ItemParent.IP_PARENT_ID + " " + sign + " (")
					.setLongArray(itemIds.toArray(new Long[itemIds.size()]))
					.sql(")");
			} else {
				critPart.sql(tableName + '.' + DBConstants.ItemParent.IP_PARENT_ID + " " + sign + " (-1)");
			}
			
		}

		// Добавление критерия типа айтема (производится централизованно другом месте)
		// Критерий типа айтема добавляется только в случае если этот критерий является главным,
		// т. е. фактически если идет первым в списке критериев фильтра
		if (isMain())
			critPart.sql(" AND " + tableName + '.' + DBConstants.ItemParent.ITEM_TYPE).subquery(ItemQuery.TYPE_CRIT_OPT);
		
		// -- КОНЕЦ -- Добавление критерия предшественника
	}

	public void setMain() {
		this.isMainCriteria = true;
	}
	
	public boolean isNotBlank() {
		return itemIds.size() > 0;
	}

	public boolean isMain() {
		return isMainCriteria;
	}

	public String getTableName() {
		return tableName;
	}

	public void useParentCriteria() {
		this.useParentCriteria = true;
	}

	public String getSelectedColumnName() {
		return tableName + '.' + DBConstants.ItemParent.ITEM_ID;
	}

	public BooleanQuery appendLuceneQuery(BooleanQuery query, Occur occur) {
		if (!sign.equals(IN) && !sign.equals(NOT_IN))
			return query;
		BooleanQuery innerQuery = new BooleanQuery();
		Occur innerOccur = Occur.SHOULD;
		if (sign.equals("NOT IN"))
			innerOccur = Occur.MUST_NOT;
		for (Long value : itemIds) {
			innerQuery.add(new TermQuery(new Term(DBConstants.Item.DIRECT_PARENT_ID, value.toString())), innerOccur);
		}
		query.add(innerQuery, occur);
		return query;
	}

	public String getParentColumnName() {
		return tableName + '.' + DBConstants.ItemParent.IP_PARENT_ID;
	}

	public boolean isEmptySet() {
		return isEmptySet;
	}
}
