package ecommander.persistence.commandunits;

import ecommander.model.*;
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
		this.parentId = parent.getId();
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
			// Добавляется сам отсоединяемый предок
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

			// Другие прямые предки
			HashSet<Long> otherDirectParents = new HashSet<>();
			TemplateQuery loadKeepParentsParents = new TemplateQuery("Keep parents parents");
			loadKeepParentsParents.SELECT(IP_PARENT_ID).FROM(ITEM_PARENT_TBL)
					.WHERE().col(IP_CHILD_ID).long_(childId)
					.AND().col(IP_PARENT_ID, "<>").long_(parentId)
					.AND().col(IP_ASSOC_ID).byte_(assocId)
					.AND().col(IP_PARENT_DIRECT).byte_((byte) 1);
			try (PreparedStatement pstmt = loadKeepParentsParents.prepareQuery(getTransactionContext().getConnection())) {
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					long parentId = rs.getLong(1);
					toUnlink.remove(parentId);
					otherDirectParents.add(parentId);
				}
			}

			// Предки других прямых предков
			if (otherDirectParents.size() > 0) {
				TemplateQuery loadKeepParentsAncestors = new TemplateQuery("Keep parents ancestors");
				loadKeepParentsAncestors.SELECT(IP_PARENT_ID).FROM(ITEM_PARENT_TBL)
						.WHERE().col_IN(IP_CHILD_ID).longIN(otherDirectParents.toArray(new Long[0]))
						.AND().col(IP_ASSOC_ID).byte_(ItemTypeRegistry.getPrimaryAssocId());
				try (PreparedStatement pstmt = loadUnlinkParents.prepareQuery(getTransactionContext().getConnection())) {
					ResultSet rs = pstmt.executeQuery();
					while (rs.next())
						toUnlink.remove(rs.getLong(1));
				}
			}

			// Удалить связи между вычисленными предками и потомками айтема, с которым разрывается связь (item)
			TemplateQuery delete = new TemplateQuery("Delete assoc item children by list");
			delete.DELETE_FROM_WHERE(ITEM_PARENT_TBL)
					.col_IN(IP_PARENT_ID).longIN(toUnlink.toArray(new Long[0])).AND()
					.col(IP_ASSOC_ID).byte_(assocId).AND()
					.col_IN(IP_CHILD_ID).sql(" (").SELECT(IP_CHILD_ID).FROM().sql(" (")
					.SELECT(IP_CHILD_ID).FROM(ITEM_PARENT_TBL).WHERE().col(IP_PARENT_ID).long_(childId)
					.AND().col(IP_ASSOC_ID).byte_(ItemTypeRegistry.getPrimaryAssocId())
					.sql(") X)");
			try (PreparedStatement pstmt = delete.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}

			// Удалить связи между вычисленными предками и самим айтемом, с которым разрывается связь (item)
			delete = new TemplateQuery("Delete assoc item itself by list");
			delete.DELETE_FROM_WHERE(ITEM_PARENT_TBL)
					.col_IN(IP_PARENT_ID).longIN(toUnlink.toArray(new Long[0])).AND()
					.col(IP_CHILD_ID).long_(childId).AND()
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
