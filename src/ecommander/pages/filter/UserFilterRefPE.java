package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ServerLogger;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.filter.FilterDefinition;
import ecommander.pages.*;
import ecommander.pages.var.FilterStaticVariable;
import ecommander.pages.var.Variable;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

/**
 * Пользовательский фильтр.
 * Может содержаться в фильтре либо в ассоциированных критериях
 * Created by E on 22/3/2018.
 */
public class UserFilterRefPE implements PageElement {

	private String itemId;    // ID страничного айтема
	private String paramName; // название параметра страничного айтема, хранящего определение фильтра
	private String filterVarName;   // имя страничной переменной, которая хранит ввод пользователя сайта (критерии фильтрации)
	private boolean needPreloadDomains; // нужно ли подгружать возможные значения списочных полей ввода для пользовательского фильтра
	private FilterDefinition filterDef; // Объект Фильтр, который является значением параметра userFilterParamName айтема userFilterItemId

	public UserFilterRefPE(String itemId, String paramName, String filterVarName, boolean needPreloadDomains) {
		this.itemId = itemId;
		this.paramName = paramName;
		this.filterVarName = filterVarName;
		this.needPreloadDomains = needPreloadDomains;
	}

	private ExecutablePagePE parentPage;

	@Override
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		UserFilterRefPE clone = new UserFilterRefPE(itemId, paramName, filterVarName, needPreloadDomains);
		clone.parentPage = parentPage;
		return clone;
	}

	@Override
	public void validate(String elementPath, ValidationResults results) {
		// Сущетвование айтемов и параметров
		ItemPE pageItem = parentPage.getItemPEById(itemId);
		if (pageItem == null) {
			results.addError(elementPath + " > " + getKey(), "there is no '" + itemId + "' page item on current page");
		}
		ItemType itemDesc = ItemTypeRegistry.getItemType(pageItem.getItemName());
		if (itemDesc.getParameter(paramName) == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + paramName
					+ "' parameter in '" + itemDesc.getName() + "' item");
		if (!filterVarName.startsWith("$") && parentPage.getVariable(filterVarName) == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + filterVarName + "' page variable on current page");
		// Корректность использования кеширования
		if (needPreloadDomains && !pageItem.hasCacheVars()) {
			results.addError(elementPath + " > " + getKey(), "Page " + pageItem.getKey()
					+ " must have attribute cache-vars set in order to operate filter cache correctly");
		}
	}

	@Override
	public String getKey() {
		return "User Filter Reference";
	}

	@Override
	public String getElementName() {
		return null;
	}

	boolean isCorrect() {
		return StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(paramName) && StringUtils.isNotBlank(filterVarName);
	}

	/**
	 * Применить фильтр к запросу
	 * @param query
	 * @throws EcommanderException
	 */
	void apply(ItemQuery query) throws EcommanderException {
		getFilterDef();
		Variable var = parentPage.getVariable(filterVarName);
		FilterStaticVariable filterVar = new FilterStaticVariable(filterVarName, var.writeSingleValue());
		parentPage.addVariable(filterVar); // добавить переменную для последующего вывода при выводе XML страницы
		query.applyUserFilter(filterDef, filterVar);
	}

	/**
	 * Вернуть определение фильтра, создать его, в случае если оно еще не создано
	 * @return
	 */
	FilterDefinition getFilterDef() {
		if (filterDef == null) {
			if (!StringUtils.isBlank(itemId) && !StringUtils.isBlank(paramName)) {
				String filterXML = "";
				if (parentPage.getItemPEById(itemId).hasFoundItems())
					filterXML = parentPage.getItemPEById(itemId).getSingleFoundItem().getStringValue(paramName);
				try {
					filterDef = FilterDefinition.create(filterXML);
				} catch (Exception e) {
					ServerLogger.error("User filter has wrong format", e);
				}
			}
		}
		return filterDef;
	}

	/**
	 * Является ли фильтр пригодным для применения
	 * @return
	 */
	boolean isValid() {
		return getFilterDef() != null;
	}

	boolean needPreloadDomains() {
		return needPreloadDomains;
	}

	String getParamName() {
		return paramName;
	}

	public String getFilterVarName() {
		return filterVarName;
	}

	public String getItemId() {
		return itemId;
	}
}
