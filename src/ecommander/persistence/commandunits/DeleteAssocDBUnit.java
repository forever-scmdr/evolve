package ecommander.persistence.commandunits;

import ecommander.model.Assoc;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

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
		Assoc assoc = ItemTypeRegistry.getAssoc(assocId);
		if (assoc.isTransitive()) {
			HashSet<Long> toUnlink = new HashSet<>();
			toUnlink.add(parentId);

			// Загрузить всех предков родтельского айтема (который перестает быть родительским)
			TemplateQuery loadUnlinkParents = new TemplateQuery("Unlink parents");
			loadUnlinkParents.SELECT(IP_PARENT_ID).FROM(ITEM_PARENT_TBL)
					.WHERE().col(IP_CHILD_ID).long_(parentId)
					.AND().col(IP_ASSOC_ID).byte_(ItemTypeRegistry.getPrimaryAssocId());
			try (PreparedStatement pstmt = loadUnlinkParents.prepareQuery(getTransactionContext().getConnection())) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					toUnlink.add(rs.getLong(1));
			}

			// Загрузить всех предков айтема, являющихся другими непосредсвенными родителями (кроме отсоединяемого)
			// и предками этих родителей
			TemplateQuery loadKeepParents = new TemplateQuery("Keep parents");
			loadKeepParents.SELECT("PAR." + IP_PARENT_ID).FROM(ITEM_PARENT_TBL + " AS PAR")
					.INNER_JOIN(ITEM_PARENT_TBL + " AS CH", "PAR." + IP_CHILD_ID, "CH." + IP_PARENT_ID)
					.WHERE().col("CH." + IP_CHILD_ID).long_(childId)
					.AND().col("CH." + IP_PARENT_ID, "<>").long_(parentId)
					.AND().col("CH." + IP_ASSOC_ID).byte_(assocId)
					.AND().col("CH." + IP_PARENT_DIRECT).byte_((byte) 1)
					.AND().col("PAR." + IP_ASSOC_ID).byte_(assocId);
			try (PreparedStatement pstmt = loadUnlinkParents.prepareQuery(getTransactionContext().getConnection())) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					toUnlink.remove(rs.getLong(1));
			}

			// Удалить связи с вычисленными предками
			TemplateQuery delete = new TemplateQuery("Delete assoc by list");
			delete.DELETE_FROM_WHERE(ITEM_PARENT_TBL)
					.col_IN(IP_PARENT_ID).longIN(toUnlink.toArray(new Long[0])).AND()
					--.col(IP_CHILD_ID).long_(childId).AND() // TODO связь не только с самим айтемом, но и с его потомками
					.col(IP_ASSOC_ID).byte_(assocId);
			try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}

		} else {
			TemplateQuery delete = new TemplateQuery("Delete assoc");
			delete.DELETE_FROM_WHERE(ITEM_PARENT_TBL)
					.col(IP_PARENT_ID).long_(parent.getId()).AND()
					.col(IP_CHILD_ID).long_(item.getId()).AND()
					.col(IP_ASSOC_ID).byte_(assocId).sql(" \r\n");
			try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		addItemPredecessorsToComputedLog(item.getId(), assocId);
	}
}
