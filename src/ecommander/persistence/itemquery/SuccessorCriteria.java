package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import java.util.ArrayList;
import java.util.Collection;
/**
 * Часть запроса для критерия фильтрации <predecessor>
 * Критерий типа айтема добавляется только в случае если этот критерий является главным,
 * т. е. фактически если идет первым в списке критериев фильтра
 * @author E
 *
 */
class SuccessorCriteria implements FilterCriteria, ItemQuery.Const, DBConstants.ItemParent, DBConstants.ItemTbl {

	private static final String IN = "IN";
	
	protected final String OTHER_PARENT_TABLE; // Псевдоним таблицы с параметрами (ItemIndex) для данного параметра
	protected final ArrayList<Long> itemIds;
	protected final String sign;
	// Если критерий является главным, то все остальные критерии приравнивают извлекаемый ID айтема к ID, извлекаемому этим критерием
	private final boolean isEmptySet;
	private final byte assocId;
	
	
	SuccessorCriteria(String sign, Collection<Long> predItemIds, byte assocId, String tableName, Compare type) {
		this.itemIds = new ArrayList<>();
		if (predItemIds != null)
			this.itemIds.addAll(predItemIds);
		this.OTHER_PARENT_TABLE = tableName;
		this.sign = sign.trim();
		this.assocId = assocId;
		isEmptySet = itemIds.size() == 0 && sign.trim().equals(IN) && (type == Compare.EVERY || type == Compare.SOME);
	}
	
	public final void appendQuery(TemplateQuery query) {
		final String PARENT_DOT = OTHER_PARENT_TABLE + ".";

		// Добавление таблицы в INNER JOIN
		TemplateQuery join = query.getSubquery(JOIN);
		join.INNER_JOIN(ITEM_PARENT_TBL + " AS " + OTHER_PARENT_TABLE, ITEM_TABLE + I_ID, PARENT_DOT + IP_PARENT_ID);

		TemplateQuery wherePart = query.getSubquery(WHERE);

		// Добавление списка ID потоков
		if (itemIds.size() > 0) {
			wherePart.col(PARENT_DOT + IP_CHILD_ID, " " + sign + " ").longIN(itemIds.toArray(new Long[itemIds.size()]));
		} else {
			wherePart.col(PARENT_DOT + IP_CHILD_ID, " " + sign + " (-1)");
		}
		wherePart.col(PARENT_DOT + IP_ASSOC_ID).byte_(assocId);
	}

	public boolean isNotBlank() {
		return itemIds.size() > 0;
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		return queryBuilder;
	}

	public boolean isEmptySet() {
		return isEmptySet;
	}
}
