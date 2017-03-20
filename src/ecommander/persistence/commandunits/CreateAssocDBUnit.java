package ecommander.persistence.commandunits;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.model.Item;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * Created by User on 20.03.2017.
 */
public class CreateAssocDBUnit extends DBPersistenceCommandUnit implements DBConstants, ErrorCodes {

	private Item item;
	private long parentId;
	private byte assocId;
	private boolean isItemNew = false;

	public CreateAssocDBUnit(Item item, long parentId, byte assocId, boolean isItemNew) {
		this.assocId = assocId;
		this.item = item;
		this.parentId = parentId;
		this.isItemNew = isItemNew;
	}

	public CreateAssocDBUnit(Item item, long parentId, byte assocId) {
		this.assocId = assocId;
		this.item = item;
		this.parentId = parentId;
	}

	@Override
	public void execute() throws Exception {

		// У ассоциируемых айтемов не должно быть общих предков
		// Также у них должны быть одинаковые группы владельцев и сами владельцы

		TemplateQuery checkQuery = new TemplateQuery("check assoc validity");
		checkQuery.SELECT(ItemParent.PARENT_ID, ItemParent.GROUP, ItemParent.USER, ItemParent.CHILD_ID)
				.FROM(ItemParent.TABLE)
				.WHERE().crit(ItemParent.CHILD_ID, "=").setLong(item.getId())
				.AND().crit(ItemParent.ASSOC_ID, "=").setByte(assocId)
				.UNION_ALL()
				.SELECT(ItemParent.PARENT_ID, ItemParent.GROUP, ItemParent.USER, ItemParent.CHILD_ID)
				.FROM(ItemParent.TABLE)
				.WHERE().crit(ItemParent.CHILD_ID, "=").setLong(parentId)
				.AND().crit(ItemParent.ASSOC_ID, "=").setByte(assocId);
		try (
			PreparedStatement pstmt = checkQuery.prepareQuery(getTransactionContext().getConnection());
			ResultSet rs = pstmt.executeQuery()
		) {
			HashSet<Long> nodesParents = new HashSet<>();
			int userId = -1;
			byte groupId = -1;
			while (rs.next()) {
				long parentId = rs.getLong(1);
				byte gId = rs.getByte(2);
				int uId = rs.getInt(3);
				if (nodesParents.contains(parentId))
					throw new EcommanderException(ASSOC_NODES_ILLEGAL,
							"Association parent and child nodes must be in different branches");
				if (userId == -1)
					userId = uId;
				if (groupId == -1)
					groupId = gId;
				if (userId != uId || groupId != gId)
					throw new EcommanderException(ASSOC_USERS_NO_MATCH,
							"Association parent and child nodes must belong to same users of same groups");
				nodesParents.add(parentId);
			}
		}


	}
}
