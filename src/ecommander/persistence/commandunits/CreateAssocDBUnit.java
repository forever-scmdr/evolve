package ecommander.persistence.commandunits;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ErrorCodes;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.ItemMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * Команда для создания новой ассоциации между двумя айтемами. Один айтем - родитель, второй атйем - потомок
 * При передаче в команду загруженного айтема предка, а не его ID, команда будет работать быстрее, т.к. не потребуется
 * его загрузка для проверки прав
 * Created by User on 20.03.2017.
 */
public class CreateAssocDBUnit extends DBPersistenceCommandUnit implements DBConstants.ItemParent, DBConstants.ItemTbl, DBConstants.ComputedLog {

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

		//////////////////////////////////////////////////////////////////////////////////////////
		//                Проверки прав пользователя и корректности агрументов                  //
		//////////////////////////////////////////////////////////////////////////////////////////

		// Проверить права пользователя
		if (parent == null)
			parent = ItemMapper.loadItemBasics(parentId, getTransactionContext().getConnection());
		testPrivileges(item);
		testPrivileges(parent);

		Assoc assoc = ItemTypeRegistry.getAssoc(assocId);

		// Тип родителя должен поддерживать создаваемую связь
		if (!ItemTypeRegistry.isDirectContainer(parent.getTypeId(), item.getTypeId(), assocId)) {
			throw new EcommanderException(ErrorCodes.ASSOC_NODES_ILLEGAL,
					"Association parent and child must be compatible by type");
		}
		if (!isItemNew) {
			if (assoc.isTransitive()) {
				// У ассоциируемых айтемов не должно быть общих предков
				TemplateQuery checkQuery = new TemplateQuery("check assoc validity transitive");
				checkQuery.SELECT(IP_PARENT_ID).FROM(ITEM_PARENT_TBL).WHERE()
						.col(IP_CHILD_ID).long_(item.getId()).AND().col(IP_ASSOC_ID).byte_(assocId)
						.UNION_ALL()
						.SELECT(IP_PARENT_ID).FROM(ITEM_PARENT_TBL).WHERE()
						.col(IP_CHILD_ID).long_(parent.getId()).AND().col(IP_ASSOC_ID).byte_(assocId);
				try (PreparedStatement pstmt = checkQuery.prepareQuery(getTransactionContext().getConnection())) {
					HashSet<Long> nodesParents = new HashSet<>();
					ResultSet rs = pstmt.executeQuery();
					while (rs.next()) {
						long parentId = rs.getLong(1);
						if (nodesParents.contains(parentId))
							throw new EcommanderException(ErrorCodes.ASSOC_NODES_ILLEGAL,
									"Association parent and child nodes must be in different branches");
						nodesParents.add(parentId);
					}
				}
			} else {
				// Предок не должен содержать потомка по этой ассоциации
				TemplateQuery checkQuery = new TemplateQuery("check assoc validity non transitive");
				checkQuery.SELECT(IP_PARENT_ID).FROM(ITEM_PARENT_TBL).WHERE()
						.col(IP_PARENT_ID).long_(parent.getId()).AND()
						.col(IP_CHILD_ID).long_(item.getId()).AND()
						.col(IP_ASSOC_ID).byte_(assocId);
				try (PreparedStatement pstmt = checkQuery.prepareQuery(getTransactionContext().getConnection())) {
					ResultSet rs = pstmt.executeQuery();
					if (rs.next())
						throw new EcommanderException(ErrorCodes.ASSOC_NODES_ILLEGAL,
								"Association parent already has specified child");
				}
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//                          Запрос записи в таблицу ItemParent                          //
		//////////////////////////////////////////////////////////////////////////////////////////


		long childId = item.getId();
		int superTypeId = item.getItemType().getSuperType().getTypeId();
		TemplateQuery insert = new TemplateQuery("New assoc insert");
		insert.INSERT_INTO(ITEM_PARENT_TBL, IP_PARENT_ID, IP_CHILD_ID, IP_ASSOC_ID, IP_CHILD_SUPERTYPE, IP_PARENT_DIRECT, IP_WEIGHT);

		// Шаг 1. Вставить запись непосредственного предка и потомка
		insert.SELECT(parent.getId(), childId, assocId, superTypeId, 1, "COALESCE(MAX(" + IP_WEIGHT + "), 0) + 64")
				.FROM(ITEM_PARENT_TBL).WHERE().col(IP_PARENT_ID).long_(parent.getId()).AND().col(IP_ASSOC_ID).byte_(assocId).sql(" \r\n");

		// Остальные шаги только для транзитивных ассоциаций
		if (assoc.isTransitive()) {
			byte primaryAssocId = ItemTypeRegistry.getPrimaryAssoc().getId();

			// Шаг 2. Добавить для нового потомка в качестве новых предков всех предков нового непосредственного родителя
			insert.UNION_ALL()
					.SELECT(IP_PARENT_ID, childId, assocId, superTypeId, 0, 0)
					.FROM(ITEM_PARENT_TBL).WHERE()
					.col(IP_CHILD_ID).long_(parent.getId()).AND().col(IP_ASSOC_ID).byte_(primaryAssocId).sql(" \r\n");

			// Шаг 3. Повторить предыдущий шаг для каждого потомка ассоциируемого айтема ("нового потомка" из шага 2)
			if (!isItemNew) {
				insert.UNION_ALL()
						.SELECT("PRED." + IP_PARENT_ID, "SUCC." + IP_CHILD_ID, assocId, "SUCC." + IP_CHILD_SUPERTYPE, 0, 0)
						.FROM(ITEM_PARENT_TBL + " AS PRED", ITEM_PARENT_TBL + " AS SUCC")
						.WHERE()
						.col("PRED." + IP_CHILD_ID).long_(parent.getId()).AND().col("PRED." + IP_ASSOC_ID).byte_(primaryAssocId)
						.AND()
						.col("SUCC." + IP_PARENT_ID).long_(childId).AND().col("SUCC." + IP_ASSOC_ID).byte_(primaryAssocId);
			}
			try (PreparedStatement pstmt = insert.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		//         Включить в список обновления предшественников айтема (и его сабайтемов)      //
		//////////////////////////////////////////////////////////////////////////////////////////

		addItemPredecessorsToComputedLog(item.getId(), assocId);
	}
}
