package ecommander.pages;

import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.filter.FilterPE;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Набор полей ввода, может сожержать как одно поле, так и много
 * Может относиться к айтему, а может и не относиться.
 * Может представлять как параметры атйема, так и другие значения, ассоциированные с айтемом
 *
 * Представляет тэги parameter-input и extra-input
 *
 * Created by E on 8/6/2017.
 */
public class InputSetPE implements PageElement {
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление InputSetPE особым образом
	 */
	public interface InputSetContainer {
		void addInputSet(InputSetPE inputSet);
	}


	public static final String ELEMENT_NAME = "input_set";

	private boolean isParameter; // представляет ли параметр айтема (true) или просто значение (false)
	private String refId; // страничный ID айтема, к которому относится
	private List<String> names; // массив названий параметров (в случае если isParameter = true) или полей ввода
	private String formId;  // ID формы ввода. В случае если несколько таких элементов относятся к одной форме.
							// Используется для восстановления формы из сеанса
	private String restoreVar;  // название переменной, которая используется в качестве отметки о том, что нужно
								// восстановить форму из сеанса (форма восстанавливается, если указанная переменная
								// имеет не пустое значение)
	private ExecutablePagePE pageModel;

	private InputSetPE(boolean isParameter, String refId, String formId, String restoreVar, List<String> names) {
		this.isParameter = isParameter;
		this.refId = refId;
		this.formId = formId;
		this.restoreVar = restoreVar;
		this.names = names;
	}

	public static InputSetPE createParams(String refId, String formId, String restoreVar, String... names) {
		return new InputSetPE(true, refId, formId, restoreVar, Arrays.asList(names));
	}

	public static InputSetPE createExtra(String refId, String formId, String restoreVar, String... names) {
		return new InputSetPE(false, refId, formId, restoreVar, Arrays.asList(names));
	}

	@Override
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		InputSetPE clone = new InputSetPE(isParameter, refId, formId, restoreVar, new ArrayList<>(names));
		clone.pageModel = parentPage;
		if (container != null)
			((InputSetContainer)container).addInputSet(clone);
		return clone;
	}

	public ItemInputs getAllInputs() {
		ExecutableItemPE itemPE = pageModel.getItemPEById(refId);
		if (itemPE.hasFoundItems()) {
			Item item = itemPE.getParentRelatedFoundItemIterator().getCurrentItem();
			ArrayList<Long> predIds = new ArrayList<>();
			if (item.isNew()) {
				ExecutableItemPE predPE = itemPE.getParentItemPE();
				while (predPE != null) {
					if (predPE.hasInputsFrom(formId)) {
						predIds.add(predPE.getParentRelatedFoundItemIterator().getCurrentItem().getId());
					}
					predPE = predPE.getParentItemPE();
				}
			}
			ItemInputs inputs = new ItemInputs(item, predIds.toArray(new Long[0]));
			if (isParameter) {
				if (names.size() == 0)
					inputs.addAllParameters();
				else
					inputs.addParameters(names.toArray(new String[0]));
			} else {
				inputs.addExtra(names.toArray(new String[0]));
			}
			return inputs;
		}
		return null;
	}

	@Override
	public void validate(String elementPath, ValidationResults results) {
		if (names.size() == 0 && StringUtils.isBlank(refId))
			results.addError(elementPath + " > " + getKey(), "input name is not set");
		if (StringUtils.isNotBlank(refId) && pageModel.getItemPEById(refId) == null)
			results.addError(elementPath + " > " + getKey(), "there is no page item with ID '" + refId + "'");
		if (isParameter) {
			ItemType item = ItemTypeRegistry.getItemType(pageModel.getItemPEById(refId).getItemName());
			for (String name : names) {
				if (item.getParameter(name) == null)
					results.addError(elementPath + " > " + getKey(), "there is parameter '"
							+ name + "' in '" + item.getName() + "' item");
			}
		}
	}

	@Override
	public String getKey() {
		return ELEMENT_NAME + " - " + refId + " - " + StringUtils.join(names, ",");
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	public boolean isParameter() {
		return isParameter;
	}

	public String getRefId() {
		return refId;
	}

	public List<String> getNames() {
		return names;
	}

	public String getFormId() {
		return formId;
	}

	public String getRestoreVar() {
		return restoreVar;
	}
}
