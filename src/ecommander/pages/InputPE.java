package ecommander.pages;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.Item;
/**
 * Поле ввода, которое относится к определенному айтему
 * @author EEEE
 *
 * @deprecated TODO delete
 */
public class InputPE implements PageElement {
	
	public static final String ELEMENT_NAME = "input";
	
	private String name;
	private String itemPageId;
	private ExecutablePagePE pageModel;
	
	public InputPE(String name, String itemPageId) {
		this.name = name;
		this.itemPageId = itemPageId;
	}

	private InputPE(String name, String itemPageId, ExecutablePagePE parentPage) {
		this.name = name;
		this.itemPageId = itemPageId;
		this.pageModel = parentPage;
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new InputPE(name, itemPageId, parentPage);
	}
	
	public String getName() {
		return name;
	}
	
	public String getHtmlInputName() {
		Item item = ((ExecutableItemPE)pageModel.getItemPEById(itemPageId)).getParentRelatedFoundItemIterator().getCurrentItem();
		return UrlParameterFormatConverter.createInputName(item.getTypeId(), item.getRefId(), name);
	}

	public void validate(String elementPath, ValidationResults results) {
		if (StringUtils.isBlank(name))
			results.addError(elementPath + " > " + getKey(), "input name is not set");
		if (StringUtils.isBlank(itemPageId) || pageModel.getItemPEById(itemPageId) == null)
			results.addError(elementPath + " > " + getKey(), "there is no page item with ID '" + itemPageId + "'");
	}

	public String getKey() {
		return "Input '" + name + "'";
	}
	
	public String getElementName() {
		return ELEMENT_NAME;
	}
}