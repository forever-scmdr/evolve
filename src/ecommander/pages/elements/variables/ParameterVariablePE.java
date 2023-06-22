package ecommander.pages.elements.variables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.MultipleParameter;
import ecommander.model.item.Parameter;
import ecommander.model.item.SingleParameter;
import ecommander.pages.elements.ExecutableItemPE;
import ecommander.pages.elements.ExecutableItemPE.AllFoundIterator;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.ItemPE;
import ecommander.pages.elements.ValidationResults;

/**
 * Получает значение из параметра paramName текущего айтема бандла, который обозначен itemPageId
 * 
 * Является итерируемой.
 * Итерируемая переменная принимает разное значение в зависимости от текущего состояния обхода дерева айтемов страницы.
 * Фактически по логике у такой переменной одно значение, но оно может быть разным в зависимости от текущего айтема во время обхода.
 * Такие переменные присутствуют один раз в модели страниц, но выводятся много раз в результирующем XML документе
 * @author E
 */
public class ParameterVariablePE extends VariablePE {

	private String itemPageId;
	private String paramName;
	private List<String> valuesCache;
	
	public ParameterVariablePE(String varId, String pageItemId, String paramName) {
		super(varId);
		this.itemPageId = pageItemId;
		this.paramName = paramName;
	}

	protected ParameterVariablePE(ParameterVariablePE source, ExecutablePagePE parentPage) {
		super(source, parentPage);
		this.itemPageId = source.itemPageId;
		this.paramName = source.paramName;
	}
	/**
	 * Для использования в ссылках
	 * @return
	 */
	private Object getIteratedValue() {
		ExecutableItemPE pageItem = (ExecutableItemPE) pageModel.getItemPEById(itemPageId);
		if (pageItem.getParentRelatedFoundItemIterator().getCurrentItem() != null)
			return pageItem.getParentRelatedFoundItemIterator().getCurrentItem().getParameterByName(paramName);
		return null;
	}
	
	@Override
	public String output() {
		Object value = getIteratedValue();
		if (value != null && value instanceof SingleParameter)
			return ((SingleParameter)value).outputValue();
		if (value != null && value instanceof MultipleParameter) {
			Collection<SingleParameter> params = ((MultipleParameter)value).getValues();
			ArrayList<String> values = new ArrayList<String>(params.size());
			for (SingleParameter param : params) {
				values.add(param.outputValue());
			}
			return StringUtils.join(values, ',');
		}
		return Strings.EMPTY;
	}

	@Override
	public boolean isEmpty() {
		return outputArray().isEmpty();
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		return new ParameterVariablePE(this, parentPage);
	}

	public void validate(String elementPath, ValidationResults results) {
		ItemPE pageItem = pageModel.getItemPEById(itemPageId);
		if (pageItem == null) {
			results.addError(elementPath + " > " + getKey(), "there is no '" + itemPageId + "' page item on current page");
		} else {
			ItemType itemDesc = ItemTypeRegistry.getItemType(pageItem.getItemName());
			if (itemDesc.getParameter(paramName) == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + paramName + "' parameter in '" + itemDesc.getName() + "' item");
		}
	}

	@Override
	public List<String> outputArray() {
		if (valuesCache == null) {
			ExecutableItemPE pageItem = getPageItem();
			ArrayList<SingleParameter> params = new ArrayList<SingleParameter>();
			AllFoundIterator iter = pageItem.getAllFoundItemIterator();
			while (iter.next()) {
				Parameter param = iter.getCurrentItem().getParameterByName(paramName);
				if (param.isMultiple()) {
					params.addAll(((MultipleParameter)param).getValues());
				} else {
					params.add((SingleParameter)param);
				}
			}
			valuesCache = new ArrayList<String>(params.size());
			for (SingleParameter param : params) {
				valuesCache.add(param.outputValue());
			}
		}
		return valuesCache;
	}

	@Override
	public boolean isMultiple() {
		/* TODO Закомментировано. Надо хорошо протестировать, но по идее эта переменная всегда single,
		 * т. к. в случае если она должна работать как multiple, создается StaticParameterVariablePE
		 * 	return outputArray().size() > 1;
		 */
		return false;
	}

	private ExecutableItemPE getPageItem() {
		return (ExecutableItemPE) pageModel.getItemPEById(itemPageId);
	}
	
}
