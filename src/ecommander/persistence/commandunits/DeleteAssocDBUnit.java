package ecommander.persistence.commandunits;

import ecommander.model.Assoc;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.PreparedStatement;

/**
 * Команда для создания новой ассоциации между двумя айтемами. Один айтем - родитель, второй атйем - потомок
 * При передаче в команду загруженного айтема предка, а не его ID, команда будет работать быстрее, т.к. не потребуется
 * его загрузка для проверки прав
 * Created by User on 20.03.2017.
 */
public class DeleteAssocDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent {

	private Item item;
	private long parentId;
	private ItemBasics parent;
	private byte assocId;

	public DeleteAssocDBUnit(Item item, long parentId, byte assocId) {
		this.assocId = assocId;
		this.item = item;
		this.parentId = parentId;
	}

	public DeleteAssocDBUnit(Item item, ItemBasics parent, byte assocId) {
		this.assocId = assocId;
		this.item = item;
		this.parent = parent;
	}

	@Override
	public void execute() throws Exception {

		//////////////////////////////////////////////////////////////////////////////////////////
		//                Проверки прав пользователя и корректности агрументов                  //
		//////////////////////////////////////////////////////////////////////////////////////////

		// Проверить права пользователя
		if (parent == null)
			parent = ItemMapper.loadItemBasics(parentId, getTransactionContext().getConnection());
		testPrivileges(item);
		testPrivileges(parent);

		long childId = item.getId();
		TemplateQuery delete = new TemplateQuery("Delete assoc");

		delete.DELETE_FROM_WHERE(ITEM_PARENT_TBL)
				.col(IP_PARENT_ID).long_(parent.getId()).AND()
				.col(IP_CHILD_ID).long_(item.getId()).AND()
				.col(IP_ASSOC_ID).byte_(assocId).sql(" \r\n");

		Assoc assoc = ItemTypeRegistry.getAssoc(assocId);

		if (assoc.isTransitive()) {
			delete
					.DELETE("PRED, SUCC")
					.FROM(ITEM_PARENT_TBL + " AS PRED", ITEM_PARENT_TBL + " AS SUCC")
					.WHERE()
					.col("PRED." + IP_CHILD_ID).long_(parent.getId()).AND().col("PRED." + IP_ASSOC_ID).byte_(assocId)
					.AND()
					.col("SUCC." + IP_PARENT_ID).long_(childId).AND().col("SUCC." + IP_ASSOC_ID).byte_(assocId);
		}
		try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		addItemPredecessorsToComputedLog(item.getId(), assocId);
	}
}
