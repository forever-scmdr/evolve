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
public class CreateAssocDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent, ErrorCodes {

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
		checkQuery.SELECT(PARENT_ID, GROUP, USER, CHILD_ID)
				.FROM(TABLE)
				.WHERE().col(CHILD_ID, "=").setLong(item.getId())
				.AND().col(ASSOC_ID, "=").setByte(assocId)
				.UNION_ALL()
				.SELECT(PARENT_ID, GROUP, USER, CHILD_ID)
				.FROM(TABLE)
				.WHERE().col(CHILD_ID, "=").setLong(parentId)
				.AND().col(ASSOC_ID, "=").setByte(assocId);
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

		//////////////////////////////////////////////////////////////////////////////////////////
		//                    Подготовка запроса записи в таблицу ItemParent                    //
		//////////////////////////////////////////////////////////////////////////////////////////


		long childId = item.getId();
		int superTypeId = item.getItemType().getSuperType().getTypeId();
		int userId = item.getOwnerUserId();
		int groupId = item.getOwnerGroupId();
		byte status = item.getStatus();
		TemplateQuery insert = new TemplateQuery("New assoc insert");
		insert.INSERT_INTO(TABLE, PARENT_ID, CHILD_ID, ASSOC_ID, CHILD_SUPERTYPE, PARENT_LEVEL, STATUS, USER, GROUP);

		// Шаг 1. Вставить запись непосредственного предка и потомка
		insert.SELECT(parentId, childId, assocId, superTypeId, 1, status, userId, groupId, "MAX(" + WEIGHT + ") + 64")
				.FROM(TABLE).WHERE().col(PARENT_ID).setLong(parentId).AND().col(ASSOC_ID).setByte(assocId).sql(" \r\n");

		// Шаг 2. Добавить для нового потомка в качестве новых предков всех предков нового непосредственного родителя
		insert.UNION_ALL()
				.SELECT(PARENT_ID, childId, assocId, superTypeId, 0, status, userId, groupId)
				.FROM(TABLE).WHERE()
				.col(CHILD_ID).setLong(parentId).AND().col(ASSOC_ID).setByte(assocId).sql(" \r\n");

		// Шаг 3. Повторить предыдущий шаг для каждого потомка ассоциируемого айтема ("нового потомка" из шага 2)
		if (!isItemNew) {
			insert.UNION_ALL()
					.SELECT("PRED." + PARENT_ID, "SUCC." + CHILD_ID, assocId, "SUCC." + CHILD_SUPERTYPE, 0,
							"SUCC." + STATUS, "SUCC." + USER, "SUCC." + GROUP)
					.FROM(TABLE + " AS PRED", TABLE + " AS SUCC")
					.WHERE()
					.col("PRED." + CHILD_ID).setLong(parentId).AND().col("PRED." + ASSOC_ID).setByte(assocId)
					.AND()
					.col("SUCC." + PARENT_ID).setLong(childId).AND().col("SUCC." + ASSOC_ID).setByte(assocId);
		}


	}
}
