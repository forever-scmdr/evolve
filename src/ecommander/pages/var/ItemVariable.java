package ecommander.pages.var;

import ecommander.fwk.Pair;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DataType;
import ecommander.pages.ExecutableItemPE;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ItemPE;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Переменная, которая представляет айтем (ID айтема) или параметр айтема (значение параметра)
 * В зависимости от контекста нужно использовать либо ВСЕ значения (загрузка айтемов, команды) либо
 * ЛОКАЛЬНЫЕ значения (вывод в виде XML, в частности вывод ссылок)
 * Created by E on 9/6/2017.
 */
public class ItemVariable extends Variable {

	private String itemPageId;
	private String paramName;
	private boolean isKey = false;
	private boolean needPath = false;

	private ArrayList<Object> valuesCache = null;
	private ArrayList<String> stringCache = null;
	private DataType.Type paramType = null;

	public ItemVariable(String itemPageId, String paramName) {
		super("unnamed");
		this.itemPageId = itemPageId;
		this.paramName = paramName;
	}

	public ItemVariable(String itemPageId, String paramName, ExecutablePagePE parentPage) {
		super(parentPage, "unnamed");
		this.itemPageId = itemPageId;
		this.paramName = paramName;
	}

	/**
	 * Нужно ли использовать уникальный текстовый ключ при ссылке не айтем в локальных значениях
	 * @param key
	 * @param path
	 */
	public void setKeyPath(boolean key, boolean path) {
		isKey = key;
		needPath = path;
	}

	@Override
	public ArrayList<Object> getAllValues() {
		if (valuesCache == null) {
			boolean hasParam = StringUtils.isNotBlank(paramName);
			ExecutableItemPE pageItem = parentPage.getItemPEById(itemPageId);
			valuesCache = new ArrayList<>();
			ExecutableItemPE.AllFoundIterator iter = pageItem.getAllFoundItemIterator();
			while (iter.next()) {
				Item item = iter.getCurrentItem();
				if (hasParam) {
					if (item.isValueNotEmpty(paramName)) {
						paramType = item.getItemType().getParameter(paramName).getType();
						ArrayList vals = item.getValues(paramName);
						for (Object val : vals) {
							valuesCache.add(val);
						}
					}
				} else {
					valuesCache.add(isKey ? item.getKeyUnique() : item.getId());
				}
			}
		}
		return valuesCache;
	}

	@Override
	public ArrayList<String> writeAllValues() {
		if (stringCache == null) {
			stringCache = new ArrayList<>();
			if (isEmpty())
				return stringCache;
			for (Object val : getAllValues()) {
				if (paramType == DataType.Type.TUPLE)
					stringCache.add(((Pair<String, String>) val).getLeft());
				else
					stringCache.add(val.toString());
			}
		}
		return stringCache;
	}

	@Override
	public Object getSingleValue() {
		getAllValues();
		if (valuesCache.size() > 0)
			return valuesCache.get(0);
		return null;
	}

	@Override
	public Variable getInited(ExecutablePagePE parentPage) {
		return new ItemVariable(itemPageId, paramName, parentPage);
	}

	@Override
	public ArrayList<String> getLocalValues() {
		boolean isTree = parentPage.getItemPEById(itemPageId).getQueryType() == ItemPE.Type.TREE;
		if (isTree && needPath) {
			List<Item> path = parentPage.getItemPEById(itemPageId).getParentRelatedFoundItemIterator().getCurrentItemPath();
			ArrayList<String> result = new ArrayList<>();
			if (StringUtils.isNotBlank(paramName)) {
				for (Item item : path) {
					result.addAll(item.outputValues(paramName));
				}
			} else {
				for (Item item : path) {
					result.add(isKey ? item.getKeyUnique() : item.getId() + "");
				}
			}
			return result;
		} else {
			Item item = parentPage.getItemPEById(itemPageId).getParentRelatedFoundItemIterator().getCurrentItem();
			if (StringUtils.isNotBlank(paramName)) {
				return item.outputValues(paramName);
			} else {
				ArrayList<String> result = new ArrayList<>(1);
				result.add(isKey ? item.getKeyUnique() : item.getId() + "");
				return result;
			}
		}
	}

	@Override
	public String getSingleLocalValue() {
		Item item = parentPage.getItemPEById(itemPageId).getParentRelatedFoundItemIterator().getCurrentItem();
		if (StringUtils.isNotBlank(paramName)) {
			return StringUtils.join(item.outputValues(paramName), ',');
		}
		return isKey ? item.getKeyUnique() : item.getId() + "";
	}

	@Override
	public boolean isEmpty() {
		return !parentPage.getItemPEById(itemPageId).hasFoundItems() || getAllValues().size() == 0;
	}

	private String outputSingleItem(Item item) {
		if (item != null) {
			if (isKey)
				return item.getKeyUnique();
			return ((Long)item.getId()).toString();
		} else {
			if (isKey)
				return "none";
			return "0";
		}
	}

	@Override
	public void validate(String elementPath, ValidationResults results) {
		ItemPE pageItem = parentPage.getItemPEById(itemPageId);
		if (pageItem == null) {
			results.addError(elementPath, "there is no '" + itemPageId + "' page item on current page");
		} else if (StringUtils.isNotBlank(paramName)) {
			ItemType itemDesc = ItemTypeRegistry.getItemType(pageItem.getItemName());
			if (itemDesc.getParameter(paramName) == null)
				results.addError(elementPath, "there is no '" + paramName + "' parameter in '" + itemDesc.getName() + "' item");
		}
	}

	@Override
	public void removeValue(Object value) {
		throw new IllegalStateException("Not allowed to remove values from item variable");
	}
}
