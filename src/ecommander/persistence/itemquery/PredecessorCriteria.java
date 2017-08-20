package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
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
class PredecessorCriteria implements FilterCriteria, ItemQuery.Const, DBConstants.ItemParent, DBConstants.ItemTbl {

	private static final String IN = "IN";
	private static final String NOT_IN = "NOT IN";
	
	protected final String OTHER_PARENT_TABLE; // Псевдоним таблицы с параметрами (ItemIndex) для данного параметра
	protected final ArrayList<Long> itemIds;
	protected final String sign;
	// Если критерий является главным, то все остальные критерии приравнивают извлекаемый ID айтема к ID, извлекаемому этим критерием
	private final boolean isEmptySet;
	private final byte assocId;
	private final ItemType item;

	
	PredecessorCriteria(ItemType item, String sign, Collection<Long> predItemIds, byte assocId, String tableName, Compare type) {
		this.itemIds = new ArrayList<>();
		if (predItemIds != null)
			this.itemIds.addAll(predItemIds);
		this.OTHER_PARENT_TABLE = tableName;
		this.sign = sign.trim();
		this.assocId = assocId;
		this.item = item;
		if (itemIds.size() == 0 && this.sign.equals(IN) && (type == Compare.EVERY || type == Compare.SOME))
			isEmptySet = true;
		else
			isEmptySet = false;
	}
	
	public final void appendQuery(TemplateQuery query) {
		final String PARENT_DOT = OTHER_PARENT_TABLE + ".";

		// Добавление таблицы в INNER JOIN
		TemplateQuery join = query.getSubquery(JOIN);
		join.INNER_JOIN(ITEM_PARENT_TBL + " AS " + OTHER_PARENT_TABLE, ITEM_TABLE + I_ID, PARENT_DOT + IP_CHILD_ID);

		TemplateQuery wherePart = query.getSubquery(WHERE).AND();

		// Добавление списка ID родителей
		if (itemIds.size() > 0) {
			wherePart.col(PARENT_DOT + IP_PARENT_ID, " " + sign + " ").longIN(itemIds.toArray(new Long[itemIds.size()]));
		} else {
			wherePart.col(PARENT_DOT + IP_PARENT_ID, " " + sign + " (-1)");
		}
		wherePart.AND()
				.col(PARENT_DOT + IP_ASSOC_ID).byte_(assocId).AND()
				.col_IN(PARENT_DOT + IP_CHILD_SUPERTYPE).intIN(ItemTypeRegistry.getBasicItemExtendersIds(item.getTypeId()));
	}

	public boolean isNotBlank() {
		return itemIds.size() > 0 || isEmptySet;
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		/*
		if (!sign.equals(IN) && !sign.equals(NOT_IN))
			return queryBuilder;
		BooleanQuery.Builder innerQuery = new BooleanQuery.Builder();
		Occur innerOccur = Occur.SHOULD;
		if (sign.equals("NOT IN"))
			innerOccur = Occur.MUST_NOT;
		for (Long value : itemIds) {
			innerQuery.add(new TermQuery(new Term(IP_PARENT_ID, value.toString())), innerOccur);
		}
		if (queryBuilder == null)
			return innerQuery;
		queryBuilder.add(innerQuery.build(), occur);
		return queryBuilder;
		*/

		// Временно ничего не делает
		return queryBuilder;
	}

	public boolean isEmptySet() {
		return isEmptySet;
	}
}
