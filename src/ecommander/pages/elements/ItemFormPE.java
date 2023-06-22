package ecommander.pages.elements;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.pages.elements.variables.VariablePE;

/**
 * Класс, который представляет форму, построенную на основе либо конкретного айтема,
 * либо класса айтема.
 * 
 * 	<form item="page_item_id"/>	// Если айтем известен и загружен
 *	<form item-name="item_name" parent-item="parent_item_page_id"> // Если айтем не известен
 *		<parameter name="some_item_param"/> // Если указаны параметры, то только они будут в форме, иначе будут все параметры айтема
 *		<parameter .../>
 *		<link name="submit_one" target="some_page"/>
 *		<link name="submit_two" target="some_page">
 *			<var name="variable_one" var="ref_var"/>
 *			<var .../>
 *		</link>
 *	</form>
 *
 * restore-var - форма в команде может сохранятся в сеансе при помощи вызова соответствующего метода
 * Если нужно, чтобы значения полей формы восстановились из сеанса, значение переменной restore-var 
 * не должно быть пустым (может быть любым). Если переменная не задана, форма не восстанавливается
 * <form item-name="item_name" parent-item="parent_item_page_id" restore-var="some_var">
 * 		...
 * </form>
 * 
 * TODO <?> добавить хтмл параметры
 * TODO <?> сделать вывод параметров заранее неизветсного айтема в формате XML в классе,
 * который будет отвечать за создание XML (ели параметр множественный, то писать <multiple> и тд)
 * 
 * @author EEEE
 *
 */
public class ItemFormPE extends PageElementContainer {
	public static final String ELEMENT_NAME = "form";
	
	public static final long NO_ID = 0;
	
	private String itemPageId = null;
	private String itemName = null;
	private String parentItemPageId = null;
	private String tag = null;
	private String restoreVarName = null;
	private String formId = null; // для того, чтобы форма могла восстанавливаться на другой странице
	private ExecutablePagePE pageModel;
	private ArrayList<String>paramNames;
	
	public ItemFormPE(String itemName, String pageItemId, String pageItemParentId, String formId, String restoreVar) {
		if (!StringUtils.isBlank(itemName))
			this.itemName = itemName;
		if (!StringUtils.isBlank(pageItemId))
			this.itemPageId = pageItemId;
		if (!StringUtils.isBlank(pageItemParentId))
			this.parentItemPageId = pageItemParentId;
		if (!StringUtils.isBlank(formId))
			this.formId = formId;
		if (!StringUtils.isBlank(restoreVar))
			this.restoreVarName = restoreVar;
	}
	
	private ItemFormPE(String itemName, String pageItemParentId, String pageItemId, String tag, String formId, String restoreVar) {
		this.itemName = itemName;
		this.parentItemPageId = pageItemParentId;
		this.itemPageId = pageItemId;
		this.tag = tag;
		this.formId = formId;
		this.restoreVarName = restoreVar;
	}

	public void addParameter(String paramName) {
		if (paramNames == null)
			paramNames = new ArrayList<String>();
		paramNames.add(paramName);
	}
	
	public boolean hasParameters() {
		return paramNames != null && paramNames.size() > 0;
	}
	/**
	 * Вернуть объект ItemHttpPostForm, построенный на базе имеющейся иформации
	 * Если форма с полученным ID существует в сенасе, то вернуть ее, иначе вернуть форму, которая создалась
	 * @return
	 */
	public ItemHttpPostForm getItemHtmlForm() {
		String defaultFormId = pageModel.getPageName() + "_" + StringUtils.defaultString(tag, "");
		String actualFormId = StringUtils.defaultString(formId, defaultFormId);
		ItemHttpPostForm newForm = null;
		long itemParentId = Item.DEFAULT_ID;
		// Если указан родительский айтем
		if (pageModel.getItemPEById(parentItemPageId) != null) {
			Item parent = ((ExecutableItemPE) pageModel.getItemPEById(parentItemPageId)).getParentRelatedFoundItemIterator()
					.getCurrentItem();
			if (parent != null)
				itemParentId = parent.getId();
		}
		if (itemPageId != null) {
			Item item = pageModel.getItemPEById(itemPageId).getParentRelatedFoundItemIterator().getCurrentItem();
			if (item != null) {
				if (hasParameters())
					newForm = new ItemHttpPostForm(item, actualFormId, paramNames);
				else
					newForm = new ItemHttpPostForm(item, actualFormId, item.getItemType().getParameterNames());
			} else {
				ItemType itemDesc = ItemTypeRegistry.getItemType(pageModel.getItemPEById(itemPageId).getItemName());
				newForm = new ItemHttpPostForm(itemDesc, itemParentId, actualFormId);
			}
		} else {
			ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
			if (hasParameters()) {
				newForm = new ItemHttpPostForm(itemDesc, itemParentId, actualFormId, paramNames);
			} else {
				newForm = new ItemHttpPostForm(itemDesc, itemParentId, actualFormId, itemDesc.getParameterNames());
			}
		}
		// Проверка, нужно ли восстаналвивать форму из сеанса
		if (!StringUtils.isBlank(restoreVarName)) {
			VariablePE restoreVar = pageModel.getVariable(restoreVarName);
			if (restoreVar != null && !restoreVar.isEmpty()) {
				ItemHttpPostForm savedForm = pageModel.getSessionContext().getForm(newForm.getFormId());
				if (savedForm != null)
					return savedForm;
			}
		}
		return newForm;
	}
	/**
	 * Установить тэг для формы
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	 * Вернуть тэг, с помощью которого обозначается форма
	 * @return
	 */
	public String getTag() {
		if (StringUtils.isBlank(tag)) {
			if (itemPageId != null)
				return pageModel.getItemPEById(itemPageId).getItemName();
			return itemName;
		}
		return tag;
	}

	public String getKey() {
		return "Form " + itemName + " " + itemPageId + " " + tag;
	}

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		ItemFormPE clone = new ItemFormPE(itemName, parentItemPageId, itemPageId, tag, formId, restoreVarName);
		clone.paramNames = paramNames;
		clone.pageModel = parentPage;
		return clone;
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		// Есть ли страничный айтем или тип айтема
		boolean itemExists = !StringUtils.isBlank(itemPageId) && pageModel.getItemPEById(itemPageId) != null;
		boolean descExists = !StringUtils.isBlank(itemName) && ItemTypeRegistry.getItemType(itemName) != null;
		if (!itemExists && !descExists)
			results.addError(elementPath + " > " + getKey(), "there is neither '" + itemPageId + "' page item nor '" + itemName + "' item");
		// Существует ли родительский айтем
		if (!StringUtils.isBlank(parentItemPageId) && pageModel.getItemPEById(parentItemPageId) == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + parentItemPageId + "' page item");
		return true;
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

}