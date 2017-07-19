package ecommander.pages.var;

import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.ExecutableItemPE;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ItemPE;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Переменная, которая представляет айтем (ID айтема) или параметр айтема (значение параметра)
 * В зависимости от контекста нужно использовать либо ВСЕ значения (загрузка айтемов, команды) либо
 * ЛОКАЛЬНЫЕ значения (вывод в виде XML, в частности вывод ссылок)
 * Created by E on 9/6/2017.
 */
public class ItemVariable extends Variable {

	private String itemPageId;
	private String paramName;
	boolean isTranslit = false;

	private ArrayList<Object> valuesCache = null;

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
	 * @param translit
	 */
	public void setTranslit(boolean translit) {
		isTranslit = translit;
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
				if (hasParam)
					valuesCache.add(item.getValue(paramName));
				else
					valuesCache.add(isTranslit ? item.getKeyUnique() : item.getId());
			}
		}
		return valuesCache;
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
		Item item = parentPage.getItemPEById(itemPageId).getParentRelatedFoundItemIterator().getCurrentItem();
		if (StringUtils.isNotBlank(paramName)) {
			return item.outputValues(paramName);
		} else {
			ArrayList<String> result = new ArrayList<>(1);
			result.add(isTranslit ? item.getKeyUnique() : item.getId() + "");
			return result;
		}
	}

	@Override
	public String getSingleLocalValue() {
		Item item = parentPage.getItemPEById(itemPageId).getParentRelatedFoundItemIterator().getCurrentItem();
		if (StringUtils.isNotBlank(paramName)) {
			return StringUtils.join(item.outputValues(paramName), ',');
		}
		return isTranslit ? item.getKeyUnique() : item.getId() + "";
	}

	@Override
	public boolean isEmpty() {
		return !parentPage.getItemPEById(itemPageId).hasFoundItems();
	}

	private String outputSingleItem(Item item) {
		if (item != null) {
			if (isTranslit)
				return item.getKeyUnique();
			return ((Long)item.getId()).toString();
		} else {
			if (isTranslit)
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
}
