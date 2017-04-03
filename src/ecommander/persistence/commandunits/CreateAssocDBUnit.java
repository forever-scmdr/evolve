package ecommander.persistence.commandunits;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.model.Item;
import ecommander.model.ItemBasics;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * Команда для создания новой ассоциации между двумя айтемами. Один айтем - продитель, второй атйем - потомок
 * При передаче в команду загруженного айтема предка, а не его ID, команда будет работать быстрее, т.к. не потребуется
 * его загрузка для проверки прав
 * Created by User on 20.03.2017.
 */
public class CreateAssocDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent, DBConstants, ErrorCodes {

	private Item item;
	private long parentId;
	private ItemBasics parent;
	private byte assocId;
	private boolean isItemNew = false;

	public CreateAssocDBUnit(Item item, long parentId, byte assocId, boolean isItemNew) {
		this.assocId = assocId;
		this.item = item;
		this.parentId = parentId;
		this.isItemNew = isItemNew;
	}

	public CreateAssocDBUnit(Item item, ItemBasics parent, byte assocId, boolean isItemNew) {
		this.assocId = assocId;
		this.item = item;
		this.parent = parent;
		this.isItemNew = isItemNew;
	}

	public CreateAssocDBUnit(Item item, long parentId, byte assocId) {
		this.assocId = assocId;
		this.item = item;
		this.parentId = parentId;
	}

	public CreateAssocDBUnit(Item item, ItemBasics parent, byte assocId) {
		this.assocId = assocId;
		this.item = item;
		this.parent = parent;
	}

	@Override
	public void execute() throws Exception {

		// Проверить права пользователя
		if (parent == null)
			parent = ItemMapper.loadItemBasics(parentId, getTransactionContext().getConnection());
		testPrivileges(item);
		testPrivileges(parent);

		if (!isItemNew) {
			// У ассоциируемых айтемов не должно быть общих предков
			TemplateQuery checkQuery = new TemplateQuery("check assoc validity");
			checkQuery.SELECT(PARENT_ID)
					.FROM(TABLE)
					.WHERE().col(CHILD_ID).setLong(item.getId())
					.AND().col(ASSOC_ID).setByte(assocId)
					.UNION_ALL()
					.SELECT(PARENT_ID)
					.FROM(TABLE)
					.WHERE().col(CHILD_ID).setLong(parentId)
					.AND().col(ASSOC_ID).setByte(assocId);
			try (PreparedStatement pstmt = checkQuery.prepareQuery(getTransactionContext().getConnection())) {
				HashSet<Long> nodesParents = new HashSet<>();
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					long parentId = rs.getLong(1);
					if (nodesParents.contains(parentId))
						throw new EcommanderException(ASSOC_NODES_ILLEGAL,
								"Association parent and child nodes must be in different branches");
					nodesParents.add(parentId);
				}
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//                          Запрос записи в таблицу ItemParent                          //
		//////////////////////////////////////////////////////////////////////////////////////////


		long childId = item.getId();
		int superTypeId = item.getItemType().getSuperType().getTypeId();
		TemplateQuery insert = new TemplateQuery("New assoc insert");
		insert.INSERT_INTO(TABLE, PARENT_ID, CHILD_ID, ASSOC_ID, CHILD_SUPERTYPE, PARENT_LEVEL, WEIGHT);

		// Шаг 1. Вставить запись непосредственного предка и потомка
		insert.SELECT(parentId, childId, assocId, superTypeId, 1, "MAX(" + WEIGHT + ") + 64")
				.FROM(TABLE).WHERE().col(PARENT_ID).setLong(parentId).AND().col(ASSOC_ID).setByte(assocId).sql(" \r\n");

		// Шаг 2. Добавить для нового потомка в качестве новых предков всех предков нового непосредственного родителя
		insert.UNION_ALL()
				.SELECT(PARENT_ID, childId, assocId, superTypeId, 0, 0)
				.FROM(TABLE).WHERE()
				.col(CHILD_ID).setLong(parentId).AND().col(ASSOC_ID).setByte(assocId).sql(" \r\n");

		// Шаг 3. Повторить предыдущий шаг для каждого потомка ассоциируемого айтема ("нового потомка" из шага 2)
		if (!isItemNew) {
			insert.UNION_ALL()
					.SELECT("PRED." + PARENT_ID, "SUCC." + CHILD_ID, assocId, "SUCC." + CHILD_SUPERTYPE, 0, 0)
					.FROM(TABLE + " AS PRED", TABLE + " AS SUCC")
					.WHERE()
					.col("PRED." + CHILD_ID).setLong(parentId).AND().col("PRED." + ASSOC_ID).setByte(assocId)
					.AND()
					.col("SUCC." + PARENT_ID).setLong(childId).AND().col("SUCC." + ASSOC_ID).setByte(assocId);
		}
		try (PreparedStatement pstmt = insert.prepareQuery(getTransactionContext().getConnection())) {
			pstmt.executeUpdate();
		}
	}
}
