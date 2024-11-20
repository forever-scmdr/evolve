package ecommander.pages.elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ecommander.common.Strings;
import ecommander.common.exceptions.EcommanderException;
import ecommander.controllers.AppContext;
import ecommander.controllers.SessionContext;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.LOGICAL_SIGN;
import ecommander.model.item.MultipleParameter;
import ecommander.model.item.Parameter;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.SingleParameter;
import ecommander.model.item.filter.CriteriaDef;
import ecommander.model.item.filter.CriteriaGroupDef;
import ecommander.model.item.filter.FilterDefinitionVisitor;
import ecommander.model.item.filter.InputDef;
import ecommander.model.item.filter.InputDef.INPUT_TYPE;
import ecommander.pages.elements.ReferencePE.ReferenceContainer;
import ecommander.pages.elements.filter.AggregationPE;
import ecommander.pages.elements.filter.AggregationPE.AggregationContainer;
import ecommander.pages.elements.filter.FilterPE;
import ecommander.pages.elements.filter.FilterPE.FilterContainer;
import ecommander.pages.elements.variables.VariablePE;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.SessionItemMapper;
import ecommander.view.domain.DomainRegistry;



/**
 * Страничный айтем, предназанченный для загрузки
 * @author EEEE
 */
public class ExecutableItemPE extends ItemPE implements ExecutableItemContainer, FilterContainer, ReferenceContainer, AggregationContainer,
		CacheablePE, ExecutablePE {
	
	protected static final long NO_PARENT_ID = -1;
	
	/////////////////////////////////////////////////////////////////////////////////////
	//                                  ВНУТРЕННИЕ КЛАССЫ
	//
	/**
	 * Осуществляет итерацию по всем найденным айтемам этого страничного айтема
	 * @author EEEE
	 */
	public static class ParentRelatedFoundIterator {
		private Item currentItem = null;
		private ExecutableItemPE itemPE = null;
		private int currentItemIndex = -1;
		private long currentParentId = NO_PARENT_ID;

		protected ParentRelatedFoundIterator(ExecutableItemPE pageItem) {
			this.itemPE = pageItem;
			init();
		}
		
		private void init() {
			currentItem = itemPE.getSingleFoundItem();
			currentItemIndex = -1;
			currentParentId = NO_PARENT_ID;
		}
		/**
		 * Перемещает указатель на следующий айтем
		 * @return
		 */
		public boolean next() {
			ArrayList<Item> foundItems = null;
			if (itemPE.hasParent()) {
				long parentItemId = itemPE.parentItem.iterator.currentItem.getRefId();
				foundItems = itemPE.getFoundItemsByParent(parentItemId);
				// Сбросить значение индекса, если закончились потомки одного айтема, и начались потомки другого
				if (currentParentId != parentItemId) {
					currentParentId = parentItemId;
					currentItemIndex = -1;
				}
			} else
				foundItems = itemPE.getFoundItemsByParent(currentParentId);
			if (foundItems.size() > currentItemIndex + 1) {
				currentItem = (Item) foundItems.get(++currentItemIndex);
				return true;
			}
			// переинициализация
			init();
			return false;
		}
		/**
		 * Возвращает текущий айтем
		 * @return
		 */
		public Item getCurrentItem() {
			return currentItem;
		}
		/**
		 * Возвращает количество найденных айтемов для определенного (текущего) родителя
		 */
		public int getTotalQuantity() {
			if (itemPE.isSingle()) return 1;
			if (itemPE.hasParent()) {
				Item currentParentItem = itemPE.getParentItemPE().getParentRelatedFoundItemIterator().getCurrentItem();
				return itemPE.getFoundItemsByParentQuantity(currentParentItem.getRefId());
			}
			return itemPE.getFoundItemsByParentQuantity(currentParentId);
		}
	}

	/**
	 * Итератор по всем найденным айтемам
	 * @author EEEE
	 */
	public static class AllFoundIterator {
		private Iterator<FoundItemBundle> mapIterator = null;
		private Iterator<Item> listIterator = null;
		private Item currentItem = null;
		
		protected AllFoundIterator(ExecutableItemPE pageItem) {
			if (pageItem.hasFoundItems()) {
				mapIterator = pageItem.foundItemsByParent.values().iterator();
				if (mapIterator.hasNext()) {
					listIterator = mapIterator.next().items.iterator();
				}
			}
		}
		/**
		 * Перемещает указатель на следующий айтем
		 * @return
		 */
		public boolean next() {
			if (listIterator != null && listIterator.hasNext()) {
				currentItem = listIterator.next();
				return true;
			}
			if (listIterator != null && mapIterator.hasNext()) {
				listIterator = mapIterator.next().items.iterator();
				return next();
			}
			return false;
		}
		/**
		 * Возвращает текущий айтем
		 * @return
		 */
		public Item getCurrentItem() {
			return currentItem;
		}
		/**
		 * Возвращает айтемы по порядку, когда возвращается null, больше айтемов нет
		 * @return
		 */
		public Item getNextItem() {
			if (next())
				return currentItem;
			return null;
		}
	}
	
	/**
	 * Класс, в котором хранятся результаты поиска - объекты Item для определенного предка и общее количество таких объектов, 
	 * принадлежащих этому предку.
	 */
	protected static class FoundItemBundle {
		// Массив найденных айтемов (Item)
		ArrayList<Item> items = new ArrayList<Item>();
		// общее число таких айтемов в базоне, чтобы было известно сколько всего будет страниц, например
		int totalQuantity = 0;
		@Override
		public String toString() {
			String result = "Quantity: " + totalQuantity + "  Items: ";
			for (Item item : items) {
				result += item.getTypeName() + "-" + item.getId() + "-" + item.getRefId() + ", ";
			}
			return result;
		}
	}
	//
	//                             END ВНУТРЕННИЕ КЛАССЫ
	/////////////////////////////////////////////////////////////////////////////////////

	// сабайтемы
	private ArrayList<ExecutableItemPE> subitems;
	// Найденные айтемы, разбитые в группы по ID их предка (ID предка => FoundItemBundle)
	private HashMap<Long, FoundItemBundle> foundItemsByParent = null;
	// Последний найденный айтем. В случае одиночного айтема он будет являться единственным найденным
	private Item lastFoundItem = null;
	// Ссылка на модель страницы
	private ExecutablePagePE parentPageModel = null;
	// Родительский айтем на этой странице
	private ExecutableItemPE parentItem = null;
	// Фильтр
	private FilterPE filter = null;
	// Группировка
	private AggregationPE aggregation = null;
	// Загружался ли уже этот айтем
	private boolean loaded = false;
	// Референс <reference>
	private ReferencePE reference = null;
	// Для итерации по айтемам - индекс текущего найденного айтема
	private ParentRelatedFoundIterator iterator = null;
	// Кеш айтема
	private String cache = null;
	/**
	 * Конструктор
	 * @param itemName
	 * @param itemPageId
	 * @param tag
	 */
	protected ExecutableItemPE(ItemQuery.Type itemType, String itemName, String itemPageId, String tag, ItemRootType itemRootType,
			String itemRootGroupName, boolean isSingle, boolean isCacheable, boolean isVirtual, ArrayList<String> cacheVars,
			ExecutablePagePE parentPage) {
		super(itemType, itemName, itemPageId, tag, itemRootType, itemRootGroupName, isSingle, isCacheable, isVirtual, cacheVars);
		parentPageModel = parentPage;
		foundItemsByParent = new HashMap<Long, FoundItemBundle>();
	}
	
	public final ExecutableItemPE getParentItemPE() {
		return parentItem;
	}
	
	public final boolean hasParent() {
		return parentItem != null;
	}

	public final boolean hasReference() {
		return reference != null;
	}
	
	/**
	 * Возвращает текущий айтем (Item)
	 * @return
	 */
	public final ParentRelatedFoundIterator getParentRelatedFoundItemIterator() {
		if (iterator == null)
			iterator = new ParentRelatedFoundIterator(this);
		return iterator;
	}
	/**
	 * Возвращает итератор по всем найденным айтемам не зависимо от родителя
	 * @return
	 */
	public final AllFoundIterator getAllFoundItemIterator() {
		return new AllFoundIterator(this);
	}
	/**
	 * Возвращает найденные айтемы, которые являются потомками заданного родителя
	 * Если айтемы не найдены, возвращается пустой список
	 * @return
	 */
	public final ArrayList<Item> getFoundItemsByParent(long parentId) {
		FoundItemBundle foundItems = (FoundItemBundle) foundItemsByParent.get(new Long(parentId));
		if (foundItems == null)
			return new ArrayList<Item>();
		return foundItems.items;
	}
	
	/**
	 * Возвращает единственный найденный айтем (в том случае если этот ItemPE single)
	 * @return
	 */
	public final Item getSingleFoundItem() {
		return lastFoundItem;
	}
	/**
	 * Возвращает количество айтемов, находящихся в базе данных, которые являются потомками заданног ородителя
	 * @param parentItemId
	 * @return
	 */
	public final int getFoundItemsByParentQuantity(long parentItemId) {
		FoundItemBundle foundItemBundle = (FoundItemBundle) foundItemsByParent.get(new Long(parentItemId));
		if (foundItemBundle == null)
			return 0;
		return foundItemBundle.totalQuantity;
	}
	/**
	 * Вернуть все ID найденный айтемов (бывает нужно в лоадерах)
	 * @return
	 */
	public final ArrayList<Long> getFoundItemRefIds() {
		ArrayList<Long> result = new ArrayList<Long>();
		if (!isLoaded() || !hasFoundItems())
			return result;
		AllFoundIterator iter = getAllFoundItemIterator();
		while (iter.next())
			result.add(iter.getCurrentItem().getRefId());
		return result;
	}
	/**
	 * Добавляет айтем в найденные
	 */
	private void addFoundItem(Item item, long parentId) {
		if (item == null)
			return;
		// Добавить в отображение по ID родителям
		FoundItemBundle parentSubitems = null;
		if (foundItemsByParent.containsKey(parentId)) {
			parentSubitems = foundItemsByParent.get(parentId);
		} else {
			parentSubitems = new FoundItemBundle();
			foundItemsByParent.put(parentId, parentSubitems);
		}
		parentSubitems.items.add(item);
		// Здесь можно увеличивать счетчик не опасаясь, что полное значение испортится,
		// т. к. полное значение устанавливается еще один раз после этого в айтемах, которые содержат фильтр.
		// В свою очередь, айтемы, которые не содержат фильтр, будут иметь корректное знаачение своего ополного количества по родителям.
		parentSubitems.totalQuantity++;
		// Установить последний найденный айтем
		lastFoundItem = item;
	}
	/**
	 * Устанавливает общее количество айтемов (не найденных, а потенциально существующих в БД)
	 * @param parentId
	 * @param quantity
	 */
	private void setFoundItemQuantity(long parentId, int quantity) {
		if (!foundItemsByParent.containsKey(parentId))
			foundItemsByParent.put(parentId, new FoundItemBundle());
		((FoundItemBundle) foundItemsByParent.get(parentId)).totalQuantity = quantity;
	}

	public final FilterPE getFilter() {
		return filter;
	}
	
	public final AggregationPE getAggregation() {
		return aggregation;
	}
	
	public final ReferencePE getReference() {
		return reference;
	}
	
	public final SessionContext getSessionContext() {
		return parentPageModel.getSessionContext();
	}
	
	public final ExecutablePagePE getPageModel() {
		return parentPageModel;
	}
	
	public final boolean hasFilter() {
		return filter != null;
	}
	
	public final boolean hasAggregation() {
		return aggregation != null;
	}
	/**
	 * Есть ли назденные айтемы
	 * @return
	 */
	public final boolean hasFoundItems() {
		return foundItemsByParent.size() > 0;
	}
	/**
	 * Проверяет, заргружен ли этот страничный айтем
	 * @return
	 */
	public final boolean isLoaded() {
		return loaded;
	}
	/**
	 * Загружает айтем с помощью соответствующего загрузчика
	 * Также загружаются все сабайтемы айтема
	 * @return
	 * @throws Exception 
	 */
	public final ResultPE execute() throws Exception {
		if (isLoaded())
			return null;
		// Сначала проверить, есть ли кеш для этого айтема (в этом случае загружать ничего не надо)
		if (isCacheable() && AppContext.isCacheEnabled() && CacheablePEManager.getCache(this)) {
			loaded = true;
			return null;
		}
		// Если айтем - ссылка на другой страничный айтем, загрузка не требуется (нужно только скопировать найденные ранее айтемы)
		if (hasReference() && getReference().isPageReference()) {
			getReference().getPageItem().execute();
			AllFoundIterator iter = getReference().getPageItem().getAllFoundItemIterator();
			while (iter.next())
				addFoundItem(iter.getCurrentItem(), NO_PARENT_ID);
			loaded = true;
		}
		// Загрузка
		if (!loaded) {
			HashMap<Long, Integer> quantities = new HashMap<Long, Integer>();
			List<Item> items = loadItems(quantities);
			if (hasParent()) {
				for (Item item : items) {
					addFoundItem(item, item.getContextParentId());
				}
			} else {
				for (Item item : items) {
					addFoundItem(item, NO_PARENT_ID);
				}
			}
			// Установка нового общего количества айтемов (в случае если был фильтр)
			if (quantities.size() > 0) {
				if (hasParent()) {
					for (long parentId : quantities.keySet()) {
						setFoundItemQuantity(parentId, quantities.get(parentId));
					}
				} else {
					for (long parentId : quantities.keySet()) {
						setFoundItemQuantity(NO_PARENT_ID, quantities.get(parentId));
					}
				}
			}
			loaded = true;
		}
		// Загрузка всех вложенных айтемов
		if (subitems != null) {
			for (ExecutableItemPE subitem : subitems) {
				subitem.execute();
			}
		}
		// Теперь, после того как айтем и все сабайтемы загружены, надо сохранить его в кеше, если это необходимо
		if (isCacheable() && AppContext.isCacheEnabled())
			CacheablePEManager.createCache(this);
		return null;
	}
	/**
	 * Производит непосредственную загрузку айтемов данного страничного айтема
	 * Возвращает массив айтемов. В случае если ничего не найдено, возвращается пустой список
	 * @throws Exception 
	 */
	protected List<Item> loadItems(HashMap<Long, Integer> quantities) throws Exception {
		// Список загруженных предшественников айтема
		ArrayList<Long> loadedIds = null;
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
		if (hasParent()) {
			loadedIds = getParentItemPE().getFoundItemRefIds();
		}
		
		// Загрузка из БД
		if (!isSession()) {
			// Если есть ссылка, то нет нужды в конструировании запроса
			if (hasReference()) {
				List<String> values = getReference().getValuesArray();
				if (getReference().isUrlKeyUnique()) {
					return ItemQuery.loadByUniqueKey(values, getSessionContext().getDBConnection());
				}
				else if (getReference().isAssociatedReference()) {
					return ItemQuery.loadAssociatedString(values, itemName, getSessionContext().getDBConnection());
				} else {
					if (getReference().isVarParamReference())
						return ItemQuery.loadByParamValue(itemName, getReference().getParamName(), values, getSessionContext()
								.getDBConnection());
					else
						return ItemQuery.loadByIdsString(values, itemName, getSessionContext().getDBConnection());
				}
			}
			// Создание запроса
			ItemQuery query = new ItemQuery(getItemQueryType(), itemDesc, hasParent());
			query.setPredecessorIds(loadedIds);
			// Установка фильтра, если он должен быть
			boolean needLoading = true;
			if (hasFilter())
				needLoading = getFilter().appendCriteriasToQuery(query);
			// Установка группировки, если она должна быть
			if (hasAggregation())
				needLoading &= getAggregation().appendCriteriasToQuery(query);
			// Установить дополнительные параметры (пользователь и группа, если они есть)
			if (getItemRootType() == ItemRootType.GROUP)
				query.setGroup(getItemRootGroupName());
			else if (getItemRootType() == ItemRootType.PERSONAL)
				query.setUser(getSessionContext().getUser());
			// Если есть фильтр и ограничение - загрузка общего числа айтемов
			if (query.hasLimit() && getFilter().hasPage()) {
				quantities.putAll(query.loadTotalQuantities(getSessionContext().getDBConnection()));
			}
			// Выполнение запроса (если это нужно)
			List<Item> items = null;
			if (needLoading)
				items = query.loadItems(getSessionContext().getDBConnection());
			else
				items = new ArrayList<Item>(0);
			// Загрузка фильтра (домены полей ввода пользовательского фильтра)
			// Нужно выполнять после основной загрузки айтемов, т.к. в методе модифицируестя объект ItemQuery
			loadFilter(query);
			return items;
		} 
		
		// Загрузка из сеанса
		else {
			SessionItemMapper storage = SessionItemMapper.getMapper(getSessionContext());
			// Если есть ссылка - загрузка по ID айтема
			if (hasReference()) {
				List<String> values = getReference().getValuesArray();
				long itemId = 0;
				if (values.size() > 0) itemId = Long.parseLong(values.get(0));
				ArrayList<Item> result = new ArrayList<Item>(1);
				result.add(storage.getItem(itemId, itemName));
				return result;
			}
			// Иначе - обычная загрузка по родителю и типу айтема
			return storage.getItemsByName(itemName, loadedIds);
		}
	}
	/**
	 * Выполнить загрузку доменов пользовательского фильтра.
	 * Здесь же осуществляется кеширование фильтра и извлечение его из кеша.
	 * Запрос для загрузки этого (текущего) страничного айтема используется как база для загрузки сгруппированных значений
	 * параметров айтемов, которые могут быть загружены этим страничным айтемом.
	 * @param baseQuery - базовый запрос, по которому извлекался этот (текущий) страничный айтем.
	 * @throws Exception
	 */
	protected void loadFilter(final ItemQuery baseQuery) throws Exception {
		if (hasFilter() && getFilter().isCacheable()) {
			// Если фильтр есть в кеше, загрузить его из кеша и завершить выполнение метода
			if (CacheablePEManager.getCache(getFilter()))
				return;
			final ItemType itemType = ItemTypeRegistry.getItemType(getFilter().getUserFilter().getBaseItemName());
			// Выполнить загрузку фильтра
			getFilter().getUserFilter().iterate(new FilterDefinitionVisitor() {
				public void visitInput(InputDef input) throws EcommanderException {
					// Обрабатывать только поля ввода, которые подразумевают список предопределенных значений
					if (input.getType() == INPUT_TYPE.checkgroup || 
							input.getType() == INPUT_TYPE.droplist || input.getType() == INPUT_TYPE.radiogroup) {
						// Если в поле установлен домен - взять значения из этого домена (если такой домен существует)
						if (input.hasDomain() && DomainRegistry.domainExists(input.getDomain())) {
							for (String value : DomainRegistry.getDomain(input.getDomain()).getValues()) {
								input.addDomainValue(value);
							}
						}
						// Если в поле не установлен домен - загрузить значения отдельно на базе базового запроса
						// Только для простого равенства и когда есть только один критерий
						else if (input.getCriterias().size() == 1) {
							CriteriaDef crit = (CriteriaDef) input.getCriterias().get(0);
							ParameterDescription paramDesc = itemType.getParameter(crit.getParamName());
							baseQuery.createFilter(LOGICAL_SIGN.AND);
							baseQuery.setAggregation(paramDesc, null, "ASC");
							List<Item> items = null;
							try {
								items = baseQuery.loadItems(getSessionContext().getDBConnection());
							} catch (Exception e) {
								throw new EcommanderException("Unable to load filter fields domains", e);
							}
							for (Item item : items) {
								Parameter param = item.getParameter(paramDesc.getId());
								if (param.isMultiple()) {
									for (SingleParameter singleParam : ((MultipleParameter)param).getValues())
										input.addDomainValue(singleParam.outputValue());
								} else
									input.addDomainValue(((SingleParameter)param).outputValue());
							}
						}
					}
				}
				
				public void visitGroup(CriteriaGroupDef group) throws EcommanderException {
					// ничего не нужно делать
				}
			});
			// Установка нового значения параметра фильтра
			getFilter().updateItemFilterParameter();
			// Сохранение фильтра в кеше
			CacheablePEManager.createCache(getFilter());
		}
	}
	/**
	 * Регистрируется добавляемый айтем, добавляемый айтем добавляется в сабайтемы,
	 * устанавливается родительский айтем для добавляемого
	 */
	public final void addExecutableItem(ExecutableItemPE itemPE) {
		if (subitems == null)
			subitems = new ArrayList<ExecutableItemPE>();
		parentPageModel.registerItemPE(itemPE);
		subitems.add(itemPE);
		itemPE.parentItem = this;
	}
	
	public final void addFilter(FilterPE filterPE) {
		filter = filterPE;
	}

	public final void addAggregate(AggregationPE aggregate) {
		aggregation = aggregate;
	}

	public final void addReference(ReferencePE referencePE) {
		reference = referencePE;
	}

	@Override
	public final String toString() {
		return "item: " + getItemName() + ", tag: " + getTag() + ", page ID: " + getId();
	}
	/**
	 * Если айтем кешируемый, то возвращает уникальный идентификатор айтема, которым должен обозначаться кеш этого айтема
	 * Идентификатор состоит из ID этого страничного айтема
	 * @return
	 */
	public final String getCacheableId() {
		String id = null;
		if (hasId()) id = itemPageId;
		else id = itemName;
		if (hasCacheVars()) {
			id += "/";
			for (String varName : cacheVars) {
				VariablePE var = parentPageModel.getVariable(varName);
				if (var != null && !var.isEmpty()) {
					id += var.getName() + "_" + Strings.translit(var.output()) + "_";					
				} else {
					id += "default";
				}
			}
		}
		return id;
	}

	public final void setCachedContents(String cache) {
		this.cache = cache;
	}

	public final String getCachedContents() {
		return cache;
	}
	
	public final boolean hasCacheContents() {
		return cache != null;
	}
}