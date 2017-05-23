package ecommander.pages.variables;

import java.util.ArrayList;
import java.util.List;

import ecommander.model.Item;
import ecommander.pages.ExecutableItemPE;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ValidationResults;

/**
 * Получает значение из текущего айтема бандла, который обозначен pageId
 * 
 * Является итерируемой.
 * Итерируемая переменная принимает разное значение в зависимости от текущего состояния обхода дерева айтемов страницы.
 * Фактически по логике у такой переменной одно значение, но оно может быть разным в зависимости от текущего айтема во время обхода.
 * Такие переменные присутствуют один раз в модели страниц, но выводятся много раз в результирующем XML документе
 * TODO <usability> сделать проверку на существование страничного атйема с ID pageId
 * @author E
 */
public class ItemVariablePE extends VariablePE {

	//	 Является ли переменная итерируемой.
	//	 Итерируемая переменная принимает разное значение в зависимости от текущего состояния обхода дерева айтемов страницы.
	//	 Фактически по логике у такой переменной одно значение, но оно может быть разным в зависимости от текущего айтема во время обхода.
	//	 Такие переменные присутствуют один раз в модели страниц, но выводятся много раз в результирующем XML документе
	private String itemPageId;
	private ArrayList<String> valuesCache = null;
	
	public ItemVariablePE(String varId, String itemPageId) {
		super(varId);
		this.itemPageId = itemPageId;
	}

	protected ItemVariablePE(ItemVariablePE source, ExecutablePagePE parentPage) {
		super(source, parentPage);
		itemPageId = source.itemPageId;
	}

	@Override
	public String output() {
		Item item = getPageItem().getParentRelatedFoundItemIterator().getCurrentItem();
		return outputItem(item);
	}

	@Override
	public boolean isEmpty() {
		return outputArray().isEmpty();
	}

	protected final String outputItem(Item item) {
		if (item != null) {
			if (style == Style.translit)
				return item.getKeyUnique();
			return ((Long)item.getId()).toString();
		} else {
			if (style == Style.translit)
				return "none";
			return "0";
		}
	}
	
	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		return new ItemVariablePE(this, parentPage);
	}

	public void validate(String elementPath, ValidationResults results) {
		if (pageModel.getItemPEById(itemPageId) == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + itemPageId + "' page item");
	}

	@Override
	public List<String> outputArray() {
		if (valuesCache == null) {
			valuesCache = new ArrayList<String>();
			ExecutableItemPE.AllFoundIterator iter = getPageItem().getAllFoundItemIterator();
			while (iter.next())
				valuesCache.add(outputItem(iter.getCurrentItem()) + "");
		}
		return valuesCache;
	}

	@Override
	public boolean isMultiple() {
		/* TODO Закомментировано. Надо хорошо протестировать, но по идее эта переменная всегда single,
		 * т. к. в случае если она должна работать как multiple, создается StaticItemVariablePE
		 * return !getPageItem().isSingle();
		 */
		return false;
	}
	
	protected ExecutableItemPE getPageItem() {
		return (ExecutableItemPE) pageModel.getItemPEById(itemPageId);
	}
}