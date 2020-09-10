package ecommander.fwk;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.SessionItemMapper;

import java.util.List;

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
		ItemQuery dbQuery = new ItemQuery(itemName);
		dbQuery.setParentId(parentId, false);
		List<Item> items = dbQuery.loadItems();
		Item item = null;
		if (items.size() == 1) {
			item = items.get(0);
		} else if (items.size() > 1) {
			item = items.get(items.size() - 1);
			for (int i = 0; i < items.size() - 1; i++) {
				transaction.addCommandUnit(ItemStatusDBUnit.delete(items.get(i)));
			}
			transaction.execute();
		} else {
			item = Item.newItem(ItemTypeRegistry.getItemType(itemName),	parentId, userId, groupId, Item.STATUS_NORMAL, false);
			transaction.addCommandUnit(SaveItemDBUnit.get(item));
			transaction.execute();
		}
		return item;
	}

	/**
	 * Создать айтем с заданным родительским и владельцем, взятым от родительского
	 * @param itemName
	 * @param parent
	 * @return
	 */
	public static Item newChildItem(String itemName, Item parent) {
		return Item.newChildItem(ItemTypeRegistry.getItemType(itemName), parent);
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
		Item item = ItemQuery.loadRootItem(itemName, userId, groupId);
		if (item == null) {
			item = Item.newItem(ItemTypeRegistry.getItemType(itemName),	ItemTypeRegistry.getPrimaryRootId(), userId, groupId, Item.STATUS_NORMAL, false);
			DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(item));
		}
		return item;
	}

	/**
	 * Загрузить определенный одиночный айтем по его названию. Если айтем не найден, то создать его.
	 * @param itemName
	 * @param initiator
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleAnonymousItem(String itemName, User initiator, long parentId) throws Exception {
		return ensureSingleItem(itemName, initiator, parentId, UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
	}

	/**
	 * Загрузить определенный одиночный корневой айтем по его названию. Если айтем не найден, то создать его.
	 * @param itemName
	 * @param initiator
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleRootAnonymousItem(String itemName, User initiator) throws Exception {
		return ensureSingleRootItem(itemName, initiator, UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
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
