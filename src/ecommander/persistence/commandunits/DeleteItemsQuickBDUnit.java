package ecommander.persistence.commandunits;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import ecommander.common.MysqlConnector;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

/**
 * Комнада для удаления простых айтемов, которые не должны содержать сабайтемы и файлы.
 * Также игнорируется проверка прав пользователя, дополнительная обработка и полнотекстовое
 * индексирование
 * @author EEEE
 */
public class DeleteItemsQuickBDUnit extends DBPersistenceCommandUnit {

	private ArrayList<Long> itemIds = new ArrayList<Long>();

	public DeleteItemsQuickBDUnit(Long... itemId) {
		CollectionUtils.addAll(itemIds, itemId);
	}
	
	public DeleteItemsQuickBDUnit(Collection<Long> ids) {
		itemIds.addAll(ids);
	}
	
	public void execute() throws Exception {
		if (itemIds.size() == 0)
			return;
		PreparedStatement pstmt = null;
		try	{
			TemplateQuery query = new TemplateQuery("delete");
			query.sql("DELETE " + DBConstants.Item.TABLE + " FROM " + DBConstants.Item.TABLE 
					+ " WHERE " + DBConstants.Item.REF_ID + " IN (").setLongArray(itemIds.toArray(new Long[0])).sql(")");
			pstmt = query.prepareQuery(getTransactionContext().getConnection());
			pstmt.executeUpdate();
		} finally {
			MysqlConnector.closeStatement(pstmt);
		}
	}
	
}