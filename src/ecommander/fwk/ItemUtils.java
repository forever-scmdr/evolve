package ecommander.fwk;

import java.util.List;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.SessionItemMapper;
import ecommander.model.User;

public class ItemUtils {
	/**
	 * Загрузить определенный одиночный айтем по его названию. Если айтем не найден, то создать его.
	 * @param itemName - название айтема
	 * @param initiator - пользователь, который инициировал действие (текущий пользователь)
	 * @param parentId - ID родительского айтема
	 * @param groupId - ID группы-владельца
	 * @param userId - ID пользователя-владельца
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleItem(String itemName, User initiator, long parentId, byte groupId, int userId) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(initiator);
		ItemQuery dbQuery = ItemQuery.newItemQuery(itemName);
		dbQuery.setParentId(parentId);
		List<Item> items = dbQuery.loadItems();
		Item item = null;
		if (items.size() == 1) {
			item = items.get(0);
		} else if (items.size() == 0) {
			item = Item.newItem(ItemTypeRegistry.getItemType(itemName),	parentId, userId, groupId, Item.STATUS_NORMAL, false);
			transaction.addCommandUnit(SaveItemDBUnit.get(item));
			transaction.execute();
		}
		return item;
	}
	/**
	 * Загрузить определенный одиночный корневой айтем по его названию. Если айтем не найден, то создать его.
	 * @param itemName - название айтема
	 * @param initiator - пользователь, который инициировал действие (текущий пользователь)
	 * @param groupId - ID группы-владельца
	 * @param userId - ID пользователя-владельца
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleRootItem(String itemName, User initiator, byte groupId, int userId) throws Exception {
		return ensureSingleItem(itemName, initiator, ItemTypeRegistry.getPrimaryRootId(), groupId, userId);
	}
	/**
	 * Загрузить определенный одиночный айтем по его названию из сеанса. Если айтем не найден, то создать его и сохранить в сеансе.
	 * @param itemName
	 * @param parentId
	 * @param mapper
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleSessionItem(String itemName, long parentId, SessionItemMapper mapper) throws Exception {
		Item item = mapper.getSingleItemByName(itemName, parentId);
		if (item == null) {
			item = mapper.createSessionItem(itemName, parentId);
			mapper.saveTemporaryItem(item);
		}
		return item;
	}
	/**
	 * Загрузить определенный одиночный корневой айтем по его названию из сеанса. Если айтем не найден, то создать его и сохранить в сеансе.
	 * @param itemName
	 * @param mapper
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleSessionRootItem(String itemName, SessionItemMapper mapper) throws Exception {
		Item item = mapper.getSingleRootItemByName(itemName);
		if (item == null) {
			item = mapper.createSessionRootItem(itemName);
			mapper.saveTemporaryItem(item);
		}
		return item;
	}
}
