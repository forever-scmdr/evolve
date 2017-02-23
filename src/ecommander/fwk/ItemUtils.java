package ecommander.fwk;

import java.util.List;

import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.DelayedTransaction;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.SessionItemMapper;
import ecommander.model.User;

public class ItemUtils {
	/**
	 * Загрузить определенный одиночный айтем по его названию. Если айтем не найден, то создать его.
	 * @param itemName - название айтема
	 * @param parentId - ID родительского айтема
	 * @param owner - пользователь владелец айтема
	 * @param isPersonal - является ли айтем персональным
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleItem(String itemName, long parentId, User owner, boolean isPersonal) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(owner);
		ItemQuery dbQuery = ItemQuery.newItemQuery(itemName);
		dbQuery.setPredecessorId(parentId);
		List<Item> items = dbQuery.loadItems();
		Item item = null;
		if (items.size() == 1) {
			item = items.get(0);
		} else if (items.size() == 0) {
			long userId = isPersonal ? owner.getUserId() : User.NO_USER_ID;
			item = Item.newItem(ItemTypeRegistry.getItemType(itemName),	parentId, userId, owner.getGroupId());
			transaction.addCommandUnit(new SaveNewItemDBUnit(item));
			transaction.execute();
		}
		return item;
	}
	/**
	 * Загрузить определенный одиночный корневой айтем по его названию. Если айтем не найден, то создать его.
	 * @param itemName - название айтема
	 * @param owner - пользователь владелец айтема
	 * @param isPersonal - является ли айтем персональным
	 * @return
	 * @throws Exception
	 */
	public static Item ensureSingleRootItem(String itemName, User owner, boolean isPersonal) throws Exception {
		RootItemType root = ItemTypeRegistry.getGroupRoot(owner.getGroup());
		return ensureSingleItem(itemName, root.getItemId(), owner, isPersonal);
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
