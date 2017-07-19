package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;

import java.util.Collection;

/**
 * Критерий параметра потомка или предка искомого айтема
 * Такие критерии могут вкладываться один в другой, тогда это будет, например,
 * параметр предка потомка или параметр потомка потомка
 *
 <list item="product" assoc="hierarchy" transitive="true" id="product">
	 <filter>
		 <!--Загрузить товары, которые содержат вложенный айтем цены с ценой не более заданной -->
		 <!--В свою очередь этот айтем цены должен принадлежать продавцу с определенным регионом-->
		 <child item="price" assoc="seller_price">
			 <parameter name="price" sign="&lt;"><var var="price_to"/></parameter>
			 <parameter name="time_updated" sign="&gt;"><var var="updated"/></parameter>
			 <parent item="seller">
			    <parameter name="region"><var var="region"/></parameter>
			 </parent>
		 </child>
		 <parameter name="display_size" sign="&gt;=" ><var var="diag"/></parameter>
	 </filter>
 </list>
 *
 * Created by E on 5/7/2017.
 */
public class AssociatedItemCriteriaGroup extends CriteriaGroup implements DBConstants.ItemParent, DBConstants.ItemTbl {

	public enum Type {
		PARENT, 		// <parent>
		CHILD; 			// <child>
		public static Type getValue(String val) {
			if (StringUtils.equalsIgnoreCase("parent", val))
				return PARENT;
			if (StringUtils.equalsIgnoreCase("child", val))
				return CHILD;
			throw new IllegalArgumentException("there is associated criteria type value for '" + val + "' string");
		}
	}

	private final byte assocId;
	private final Type type;
	private final AssociatedItemCriteriaGroup parent;
	private final ItemType parentItem;

	AssociatedItemCriteriaGroup(String critId, ItemType item, byte assocId, Type type,
	                            AssociatedItemCriteriaGroup parent, ItemType parentItem) {
		super(critId, item);
		this.assocId = assocId;
		this.type = type;
		this.parent = parent;
		this.parentItem = parentItem;
	}

	@Override
	public AssociatedItemCriteriaGroup addAssociatedCriteria(ItemType item, byte assocId, Type type) {
		String critId = (type == AssociatedItemCriteriaGroup.Type.CHILD ? 'C' : 'P') + assocCriterias.size() + groupId;
		AssociatedItemCriteriaGroup newCrit = new AssociatedItemCriteriaGroup(critId, item, assocId, type, this, this.item);
		assocCriterias.add(newCrit);
		return newCrit;
	}

	@Override
	public void appendQuery(TemplateQuery query) {
		TemplateQuery join = query.getSubquery(JOIN);
		TemplateQuery where = query.getSubquery(WHERE);
		// Добавить связь с двумя таблицами - таблицей предка для критерия предка
		// и таблицей айтема для того, чтобы статус айтема был 0
		final String PARENT_TABLE = groupId + "p";
		final String ITEM_TABLE = groupId + "i";
		// INNER JOIN parent AS с1p ON (i.id = с1p.parent_id)
		String joinWithColumn = parent == null ? ITEM_TABLE + I_ID : parent.groupId + "i." + I_ID;
		String joinColumn = type == Type.CHILD ? IP_PARENT_ID : IP_CHILD_ID;
		join.INNER_JOIN(ITEM_PARENT_TBL + " AS " + PARENT_TABLE, joinWithColumn, PARENT_TABLE + "." + joinColumn);
		// INNER JOIN item AS с1i ON (c1i.id = с1p.child_id)
		joinColumn = type == Type.CHILD ? IP_CHILD_ID : IP_PARENT_ID;
		join.INNER_JOIN(ITEM_TBL + " AS " + ITEM_TABLE, ITEM_TABLE + "." + I_ID, PARENT_TABLE + "." + joinColumn);
		// Добавляется критерий предка во WHERE
		ItemType baseItem = type == Type.CHILD ? item : parentItem;
		Integer[] superTypes = ItemTypeRegistry.getBasicItemExtendersIds(baseItem.getTypeId());
		where.AND().col(ITEM_TABLE + '.' + I_STATUS, "=0")
				.AND().col(PARENT_TABLE + '.' + IP_ASSOC_ID).byte_(assocId)
				.AND().col_IN(PARENT_TABLE + '.' + IP_CHILD_SUPERTYPE).intIN(superTypes);
		// связь с третьей (и последующими) таблицей и добавление критерия осуществляется в ParameterCriteria
		for (FilterCriteria criteria : criterias) {
			criteria.appendQuery(query);
		}
	}

	@Override
	public void addPredecessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		throw new IllegalArgumentException("Impossible to add predecessor criteria to associated parameter criteria");
	}

	@Override
	public void addSuccessors(String assocName, String sign, Collection<Long> itemIds, Compare compType) {
		throw new IllegalArgumentException("Impossible to add successor criteria to associated parameter criteria");
	}

	@Override
	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		return queryBuilder;
	}
}
