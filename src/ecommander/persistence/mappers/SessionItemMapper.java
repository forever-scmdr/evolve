/*
 * Created on 17.12.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ecommander.persistence.mappers;

import ecommander.controllers.SessionContext;
import ecommander.fwk.Strings;
import ecommander.model.*;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * @author EEEE
 * TODO <enhance> Переработать хранение айтемов в сеансе. Аналогично айтему, хранить только XML текст параметров и разбирать его по требованию
 */
public class SessionItemMapper {
	private static final byte ID = -1;
	private static final byte TAG_NAME = -2; // Чаще всего - имя типа айтема, либо имя типа предшественника айтема по иерархии наследования (
	private static final byte PARENT_ID = -3;

	public abstract static class SessionItemMemento {
		long id;
		long parentId;
		String typeName;
		String tagName;

		private SessionItemMemento(long id, long parentId, String typeName, String tagName) {
			this.id = id;
			this.parentId = parentId;
			this.typeName = typeName;
			this.tagName = tagName;
		}

		/**
		 * Восстанавливает нормальный айтем (объект Item) из объекта SessionItemMemento (this)
		 * @return
		 * @throws Exception
		 */
		public abstract Item restoreItem() throws Exception;

		public long getId() {
			return id;
		}

		public String getTypeName() {
			return typeName;
		}

		public String getTagName() {
			return tagName;
		}

		public long getParentId() {
			return parentId;
		}
	}

	/**
	 * Класс, который должен сохранять временный объект, который существует только в сеансе (т. е. создается в сеансе и заканчивает свое
	 * существование при прекращении сеанса)
	 * @author EEEE
	 */
	private static class TransientMemento extends SessionItemMemento {

		private int userId;
		private byte groupId;

		private HashMap<Integer, Object> parameters = null; // Массив объектов SessionParameter (название и значение параметра)
		private String[] extras = null; // дополнительные значения айтема
		/**
		 * Создает SessionItem из обычного Item Это нужно для нормального сохранения айтема в сеансе. По идее этот метод должен использоваться
		 * только для сохранения айтемов, которые вновь созданы в сеансе (не хранятся в БД)
		 * @param item
		 * @param tagName
		 */
		private TransientMemento(Item item, String tagName) {
			super(item.getId(), item.getContextParentId(), item.getTypeName(), tagName);
			this.userId = item.getOwnerUserId();
			this.groupId = item.getOwnerGroupId();
			parameters = new HashMap<>();
			Collection<Parameter> parameters = item.getAllParameters();
			for (Parameter parameter : parameters) {
				if (parameter.isDescMultiple()) {
					ArrayList<Object> list = new ArrayList<>();
					this.parameters.put(parameter.getParamId(), list);
					for (SingleParameter sp : ((MultipleParameter) parameter).getValues()) {
						list.add(sp.getValue());
					}
				} else {
					this.parameters.put(parameter.getParamId(), parameter.getValue());
				}
			}
			if (item.hasExtras()) {
				extras = new String[item.getExtraKeys().size() * 2];
				int i = 0;
				for (String key : item.getExtraKeys()) {
					extras[i++] = key;
					extras[i++] = (String) item.getExtra(key);
				}
			}
		}
		/**
		 * Восстанавливает нормальный айтем (объект Item) из объекта SessionItem (this)
		 * Подразумевается что сеансовые айтемы не могут быть ссылками на другие сеансовые айтемы
		 * @return
		 * @throws Exception
		 */
		public Item restoreItem() throws Exception {
			Item item = Item.existingItem(ItemTypeRegistry.getItemType(typeName), id, ItemTypeRegistry.getPrimaryAssoc(),
					parentId, userId, groupId, Item.STATUS_NORMAL, Strings.EMPTY, null, null, 0, false);
			if (parameters != null) {
				for (Integer paramId : parameters.keySet()) {
					if (item.getItemType().getParameter(paramId).isMultiple()) {
						@SuppressWarnings("unchecked")
						ArrayList<Object> list = (ArrayList<Object>) parameters.get(paramId);
						for (Object value : list) {
							item.setValue(paramId, value);
						}
					} else {
						item.setValue(paramId, parameters.get(paramId));
					}
				}
			}
			if (extras != null) {
				for (int i = 0; i < extras.length; i += 2)
					item.setExtra(extras[i], extras[i + 1]);
			}
			return item;
		}
	}
	/**
	 * Хранилище айтемов (класс SessionItem) Параметры, по которым осуществляется доступ: - ID айтема (Long) - ID родителя айтема (Long) -
	 * название типа айтема (String)
	 *
	 * @author EEEE
	 */
	private static class SessionStorageImp extends SessionObjectStorage {
		protected Object getParameter(Object object, int parameterId) {
			switch (parameterId) {
			case ID:
				return ((SessionItemMemento) object).getId();
			case PARENT_ID:
				return ((SessionItemMemento) object).getParentId();
			case TAG_NAME:
				return ((SessionItemMemento) object).getTagName();
			default:
				return ((TransientMemento) object).parameters.get(parameterId);
			}
		}
	}

	private SessionContext sess = null;
	private long rootItemId;

	/**
	 * Приватный конструктор
	 */
	private SessionItemMapper(SessionContext session, long rootId) {
		this.sess = session;
		this.rootItemId = rootId;
	}
	/**
	 * Создает новый объект хранилища
	 * Этот объект хранилища должен храниться в сеансе
	 * @return
	 */
	public static SessionObjectStorage createSessionStorage() {
		return new SessionStorageImp();
	}
	/**
	 * Устанавливает пользователя, который обслуживается текущей операцией, и его сеансовое хранилище
	 * Возвращает единственный для потока SessionItemMapper с вновь установленными пользователем и хранилищем
	 * @param sessionContext
	 * @return
	 */
	public static SessionItemMapper getMapper(SessionContext sessionContext) {
		return new SessionItemMapper(sessionContext, ItemTypeRegistry.getSessionRootId());
	}
	/**
	 * Добавляет временный айтем в хранилище сеанса
	 * Родителем этого айтема будет текущий установленный в айтеме родитель (parentId)
	 * @param item
	 */
	public void saveTemporaryItem(Item item) {
//		SessionItemMemento sessionItem = new TransientMemento(item);
//		// Удалить старый айтем, если такой уже есть
//		storage.delete(PARAM_ID, item.getId());
//		storage.addObject(sessionItem);
		saveTemporaryItem(item, item.getTypeName());
	}
	/**
	 * Добавляет временный айтем в хранилище сеанса
	 * itemTag нужен в двух случаях:
	 * 1) Когда множество айтемов имеет неизвестный тип, но нужно каким-то образом определить этот тип чтобы обращаться впоследствие к этомим айтемам,
	 *    например, загружать на странице. В этом случае всем таким айтемам присвается один общий тэг
	 * 2) Когда один и тот же айтем должен присутствовать в сеансе несколько раз и трактоваться как разные айтемы.
	 *    В этом случае каждой копии айтема присваиваются разные тэги
	 *    
	 * Если не задан предок айтема, то считается что этот айтем добавляется как корневой
	 * @param item
	 * @param itemTag - название типа айтема для динамических типов, определенных пользователем
	 */
	public void saveTemporaryItem(Item item, String itemTag) {
		// сгенерировать новый ID для нового айтема (в случае если он действительно новый)
		if (item.getId() == Item.DEFAULT_ID)
			item.setId(forceGetStorage().generateId());
		// сделать корень сеанса родительским айтемом в случае если родитель не установлен либо родитель не найден в сеансе
		if (item.getContextParentId() == Item.DEFAULT_ID || item.getContextParentId() == rootItemId) {
			item.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), rootItemId);
		} else {
			int[] parameters = { ID };
			Object[] paramValues = { item.getContextParentId() };
			ArrayList<Object> mementoArray = getStorage().select(parameters, paramValues);
			if (mementoArray.size() == 0)
				item.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), rootItemId);
		}
		SessionItemMemento sessionItem = new TransientMemento(item, itemTag);
		// Удалить старый айтем, если такой уже есть
		int[] paramIds = { ID, TAG_NAME };
		Object[] paramValues = { item.getId(), itemTag };
		forceGetStorage().delete(paramIds, paramValues);
		forceGetStorage().addObject(sessionItem);
	}
	/**
	 * Создает новый айтем не добавляя его в сеанс В этом методе создается сеансовый айтем (временный)
	 * Если ID родителя айтема равно 0, то создается айтем - прямой потомок корневого айтема
	 * @param itemName
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public Item createSessionItem(String itemName, long parentId) {
		long itemId = forceGetStorage().generateId();
		if (parentId == 0)
			return createSessionItem(itemName, rootItemId);
		return Item.existingItem(ItemTypeRegistry.getItemType(itemName), itemId, ItemTypeRegistry.getPrimaryAssoc(),
				parentId, sess.getUser().getUserId(), UserGroupRegistry.getDefaultGroup(), Item.STATUS_NORMAL,
				Strings.EMPTY, null, null, 0, false);
	}
	/**
	 * Создает новый айтем первого уровня не добавляя его в сеанс В этом методе создается сеансовый айтем (временный)
	 * @param itemName
	 * @return
	 */
	public Item createSessionRootItem(String itemName) {
		return createSessionItem(itemName, rootItemId);
	}
//	/**
//	 * Добавляет постоянный айтем в хранилище
//	 * @param itemName
//	 * @param itemId
//	 * @param parentId
//	 */
//	public void addPersistentItem(String itemName, long itemId, long parentId) {
//		SessionItemMemento sessionItem = new PersistentMemento(itemId, parentId, itemName);
//		forceGetStorage().addObject(sessionItem);
//	}
	/**
	 * Удаляет айтем и все его сабайтемы.
	 * itemTag нужен в двух случаях:
	 * 1) Когда множество айтемов имеет неизвестный тип, но нужно каким-то образом определить этот тип чтобы обращаться впоследствие к этомим айтемам,
	 *    например, загружать на странице. В этом случае всем таким айтемам присвается один общий тэг
	 * 2) Когда один и тот же айтем должен присутствовать в сеансе несколько раз и трактоваться как разные айтемы.
	 *    В этом случае каждой копии айтема присваиваются разные тэги
	 * @param itemId
	 * @param itemTag - название типа айтема для динамических типов, определенных пользователем
	 */
	public void removeItems(long itemId, String itemTag) {
		int[] paramIds = { ID, TAG_NAME };
		Object[] paramValues = { itemId, itemTag };
		removeItems(itemId, paramIds, paramValues);
	}
	/**
	 * Удаляет айтем и его сабайтемы. Аналогично removeItems(long itemId, String itemTag)
	 * @param itemId
	 * @param parentId
	 * @param itemTag
	 */
	public void removeItem(long itemId, long parentId, String itemTag) {
		// Удаление айтема
		int[] paramIds = { ID, PARENT_ID, TAG_NAME };
		Object[] paramValues = { itemId, parentId, itemTag };
		removeItems(itemId, paramIds, paramValues);
	}

	private void removeItems(long itemId, int[] paramIds, Object[] paramValues) {
		forceGetStorage().delete(paramIds, paramValues);
		// Удаление потомков
		ArrayList<Object> children = forceGetStorage().select(PARENT_ID, itemId);
		for (Object memento : children) {
			SessionItemMemento sim = (SessionItemMemento) memento;
			removeItem(sim.getId(), itemId, sim.tagName);
		}
	}
	/**
	 * Удаляет айтем и все его сабайтемы.
	 * @param itemId
	 */
	public void removeItems(long itemId) {
		// получить всех потомков и удалить их
		ArrayList<Object> children = forceGetStorage().select(PARENT_ID, itemId);
		for (Object memento : children) {
			removeItems(((SessionItemMemento)memento).getId());
		}
		forceGetStorage().delete(ID, itemId);
	}
	/**
	 * Удаляет айтем и все его сабайтемы.
	 * @param itemTag
	 */
	public void removeItems(String itemTag) {
		// получить всех потомков и удалить их
		ArrayList<Object> items = forceGetStorage().select(TAG_NAME, itemTag);
		for (Object memento : items) {
			removeItems(((SessionItemMemento)memento).getId());
		}
	}
	/**
	 * Возвращает айтем определенного типа с определенным ID
	 * @param itemId
	 * @param itemTag
	 * @return
	 * @throws Exception
	 */
	public Item getItem(long itemId, String itemTag) throws Exception {
		int[] parameters = { ID, TAG_NAME };
		Object[] paramValues = { itemId, itemTag };
		ArrayList<Object> mementoArray = getStorage().select(parameters, paramValues);
		if (mementoArray.size() == 0)
			return null;
		return ((SessionItemMemento) mementoArray.get(0)).restoreItem();
	}
	/**
	 * Вернуть один айтем (первый, если таких несколько) с заданным ID
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public Item getItemSingle(long itemId) throws Exception {
		int[] parameters = { ID };
		Object[] paramValues = { itemId };
		ArrayList<Object> mementoArray = getStorage().select(parameters, paramValues);
		if (mementoArray.size() == 0)
			return null;
		return ((SessionItemMemento) mementoArray.get(0)).restoreItem();
	}
	/**
	 * Возвращает сабайтемы заданного айтема с определенным именем
	 * @param itemName
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Item> getItemsByName(String itemName, long parentId) throws Exception {
		int[] parameters = { TAG_NAME, PARENT_ID };
		Object[] paramValues = { ItemTypeRegistry.getItemExtenders(itemName), parentId };
		ArrayList<Object> mementoArray = getStorage().select(parameters, paramValues);
		return restoreItemsArray(mementoArray);
	}
	/**
	 * Возвращает сабайтемы заданных айтемов с определенным именем
	 * @param itemName
	 * @param parentIds
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Item> getItemsByName(String itemName, Collection<Long> parentIds) throws Exception {
		int[] parameters = { TAG_NAME, PARENT_ID };
		Object[] paramValues = { ItemTypeRegistry.getItemExtenders(itemName), parentIds };
		ArrayList<Object> mementoArray = getStorage().select(parameters, paramValues);
		return restoreItemsArray(mementoArray);
	}
	/**
	 * Возвращает одиночный сабайтем заданного айтема с определенным именем
	 * @param itemName
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public Item getSingleItemByName(String itemName, long parentId) throws Exception {
		int[] parameters = { TAG_NAME, PARENT_ID };
		Object[] paramValues = { ItemTypeRegistry.getItemExtenders(itemName), parentId };
		ArrayList<Object> sessionItems = getStorage().select(parameters, paramValues);
		if (sessionItems.size() == 0)
			return null;
		return ((SessionItemMemento) sessionItems.get(0)).restoreItem();
	}
	/**
	 * Возвращает одиночный сабайтем корневого айтема с определенным именем
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	public Item getSingleRootItemByName(String itemName) throws Exception {
		return getSingleItemByName(itemName, rootItemId);
	}
	/**
	 * Возвращает сабайтемы корневого айтема по названию
	 * @param itemName
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Item> getRootItemsByName(String itemName) throws Exception {
		return getItemsByName(itemName, rootItemId);
	}
	/**
	 * Возвращает айтемы, которые содержатся в корне сеанса
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Item> getRootItems() throws Exception {
		return getClosestSubitems(rootItemId);
	}
	/**
	 * Возвращает сабайтемы заданного айтема (объекты Item)
	 * @param parentId
	 * @return
	 */
	public ArrayList<Item> getClosestSubitems(long parentId) throws Exception {
		ArrayList<Object> mementoArray = getStorage().select(PARENT_ID, parentId);
		return restoreItemsArray(mementoArray);
	}
	/**
	 * Возвращает айтемы определенного типа с заданным значением параметра
	 * @param itemName
	 * @param paramName
	 * @param paramValue
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Item> getItemsByParamValue(String itemName, String paramName, Object paramValue) throws Exception {
		ItemType item = ItemTypeRegistry.getItemType(itemName);
		ArrayList<Object> mementoArray = getStorage().select(item.getParameter(paramName).getId(), paramValue);
		return restoreItemsArray(mementoArray);
	}

	/**
	 * Загрузить айтем определенного типа с заданным значением определенного параметра
	 * @param itemName
	 * @param paramName
	 * @param paramValue
	 * @return
	 * @throws Exception
	 */
	public Item getSingleItemByParamValue(String itemName, String paramName, Object paramValue) throws Exception {
		ArrayList<Item> found = getItemsByParamValue(itemName, paramName, paramValue);
		if (found.size() > 0)
			return found.get(0);
		return null;
	}
	/**
	 * Загружает массив айтемов по массиву memento
	 * @param mementoArray
	 * @return
	 */
	protected ArrayList<Item> restoreItemsArray(ArrayList<Object> mementoArray) throws Exception {
		ArrayList<Item> result = new ArrayList<>();
		ArrayList<Long> itemIds = new ArrayList<>();
		// Для всех memento
		for (Object mementoObj : mementoArray) {
			SessionItemMemento memento = (SessionItemMemento) mementoObj;
			// Если memento - временный объект, то просто восстановить из сеанса
			if (memento instanceof TransientMemento) {
				result.add(memento.restoreItem());
			}
			// Если memento - постоянный объект, запомнить ID и потом загрузить пакетно
			else {
				itemIds.add(memento.id);
			}
		}
		// Загрузка айтемов из БД
		result.addAll(ItemQuery.loadByIdsLong(itemIds));
		return result;
	}

	private SessionObjectStorage forceGetStorage() {
		return sess.getStorage(true);
	}
	
	private SessionObjectStorage getStorage() {
		return sess.getStorage(false);
	}
}