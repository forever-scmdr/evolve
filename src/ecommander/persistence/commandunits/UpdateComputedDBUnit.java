package ecommander.persistence.commandunits;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Обновить computed-значения параметров одного айтема
 * Created by E on 30/6/2017.
 */
public class UpdateComputedDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemTbl,
		DBConstants.ItemParent, DBConstants.ItemIndexes, DBConstants.ComputedLog {
	private long itemId;

	public UpdateComputedDBUnit(long itemId) {
		this.itemId = itemId;
	}

	@Override
	public void execute() throws Exception {
		Item item = ItemQuery.loadById(itemId, getTransactionContext().getConnection());
		if (item.getStatus() == Item.STATUS_NORMAL) {
			for (ParameterDescription paramDesc : item.getItemType().getParameterList()) {
				if (paramDesc.isComputed()) {
					ComputedDescription computed = paramDesc.getComputed();
					ArrayList<ComputedDescription.Ref> refs = computed.getBasicParams();
					if (refs.size() != 1) {
						throw new EcommanderException(ErrorCodes.VALIDATION_FAILED,
								"Currently unable to process more than one basic parameter for computed parameter");
					}
					ComputedDescription.Ref ref = refs.get(0);
					ItemType refItem = ItemTypeRegistry.getItemType(ref.item);
					ParameterDescription refParam = refItem.getParameter(ref.param);
					byte assocId = ItemTypeRegistry.getAssoc(ref.assoc).getId();
					String indexTableName = DataTypeMapper.getTableName(refParam.getType());
					TemplateQuery query = new TemplateQuery("Select computed parameter value");
					query
							.SELECT(computed.getFunc() + "(" + II_VALUE + ")")
							.FROM(ITEM_PARENT_TBL)
							.INNER_JOIN(indexTableName, IP_CHILD_ID, II_ITEM_ID)
							.INNER_JOIN(ITEM_TBL, IP_CHILD_ID, I_ID)
							.WHERE()
							.col(IP_PARENT_ID).setLong(itemId).AND()
							.col(IP_ASSOC_ID).setByte(assocId)
							.col(II_PARAM).setInt(refParam.getId())
							.col(I_STATUS).setByte(Item.STATUS_NORMAL);
					Object value = null;
					try(PreparedStatement pstmt = query.prepareQuery(getTransactionContext().getConnection())) {
						ResultSet rs = pstmt.executeQuery();
						if (rs.next()) {
							value = DataTypeMapper.createValue(paramDesc.getType(), rs);
						}
					}
					item.setValue(paramDesc.getId(), value);
					executeCommand(SaveItemDBUnit.get(item).noFulltextIndex().ignoreUser().ingoreComputed());
				}
			}
		}
		// Удалить из очереди на обновление
		TemplateQuery query = new TemplateQuery("Delete item from computed update log");
		query.DELETE_FROM_WHERE(COMPUTED_LOG_TBL).col(L_ITEM).setLong(itemId);
		try(PreparedStatement pstmt = query.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
	}
}
