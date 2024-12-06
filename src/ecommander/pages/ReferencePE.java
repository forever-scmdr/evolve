package ecommander.pages;

import ecommander.pages.var.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Элемент - референс <reference>
 * Он может ссылаться на ID айтема, переданного через ссылку (urlReference)
 * или на ID страничного айтема (pageReference)
 * 
 * Также есть вариант, когда передается не ID айтема, а занчение некоторого параметра айтема, которое его уникально определяет.
 * В этом случае передается имя переменной (urlReference) и название параметра (paramName)
 * @author EEEE
 *
 */
public class ReferencePE implements PageElement {
	public static final String ELEMENT_NAME = "reference";
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public interface ReferenceContainer {
		void addReference(ReferencePE referencePE);
	}
	
	private String pageVarName = null;
	private String paramName = null;
	private String pageItemIds = null;
	private ExecutablePagePE pageModel = null;
	
	private ReferencePE(String pageVarName, String paramName, String pageItemIds) {
		this.pageVarName = pageVarName;
		this.pageItemIds = pageItemIds;
		this.paramName = paramName;
	}
	
	private ReferencePE(String pageVarName, String paramName, String pageItemIds, ExecutablePagePE parentPage) {
		this(pageVarName, paramName, pageItemIds);
		this.pageModel = parentPage;
	}
	
	public static ReferencePE createUrlReference(String pageVarName) {
		return new ReferencePE(pageVarName, null, null);
	}
	
	public static ReferencePE createPageReference(String pageItemIds) {
		return new ReferencePE(null, null, pageItemIds);
	}
	
	public static ReferencePE createParameterUrlReference(String pageVarName, String paramName) {
		return new ReferencePE(pageVarName, paramName, null);
	}

	public boolean isUrlReference() {
		return pageVarName != null;
	}
	
	public boolean isVarParamReference() {
		return paramName != null && pageVarName != null;
	}
	
	public boolean isPageReference() {
		return pageItemIds != null && paramName == null;
	}

	public boolean isUrlKeyUnique() {
		return pageVarName != null && paramName == null && pageModel.getInitVariablePE(pageVarName).isStyleKey();
	}

	public List<String> getKeysUnique() {
		if (isUrlKeyUnique()) {
			Variable variable = pageModel.getVariable(pageVarName);
			if (variable == null || variable.isEmpty())
				return new ArrayList<>(0);
			if (pageModel.getInitVariablePE(pageVarName).isStyleKeyPath()) {
				return Arrays.asList(variable.writeSingleValue());
			} else {
				return variable.writeAllValues();
			}
		}
		return new ArrayList<>(0);
	}

	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		ReferencePE clone = new ReferencePE(pageVarName, paramName, pageItemIds, parentPage);
		if (container != null)
			((ReferenceContainer)container).addReference(clone);
		return clone;
	}
	/**
	 * Вернуть массив значений либо переменной, которая являестя основой ссылки в случае reference-var, 
	 * либо параметра айтема, на который ссылается ссылка в случае reference-item
	 * @return
	 */
	public List<String> getValuesArray() {
		// Получить значение переменной, в которой хранится ID айтема (или значение параметра), нужного для загрузки
		Variable variable = pageModel.getVariable(pageVarName);
		if (variable == null || variable.isEmpty())
			return new ArrayList<>(0);
		return variable.writeAllValues();
	}

	/**
	 * Получить ID всех найденных айтемов
	 * @return
	 */
	public List<Long> getItemIds() {
		// Получить значение переменной, в которой хранится ID айтема (или значение параметра), нужного для загрузки
		ArrayList<Long> Ids = new ArrayList<>();

		ArrayList<String> itemIds = new ArrayList<>(Arrays.asList(pageItemIds.split(",; ")));

		for(String itemId: itemIds) {
			ExecutableItemPE pageItem = pageModel.getItemPEById(itemId);

			if (pageItem != null) {
				Ids.addAll(pageItem.getFoundItemIds());
			}
		}

		return Ids;
	}
	/**
	 * Получить название параметра
	 * @return
	 */
	public String getParamName() {
		return paramName;
	}
	/**
	 * Получить страничный айтем, на который указывает данная ссылка
	 * @return
	 */
	public ExecutableItemPE getPageItem() {
		return pageModel.getItemPEById(pageItemIds);
	}

	public void validate(String elementPath, ValidationResults results) {
		// Есть ли айтем с заданным ID
		boolean hasVariable = pageVarName != null && pageModel.getInitVariablePE(pageVarName) != null;
		boolean hasItem = true;

		if (pageItemIds != null) {
			ArrayList<String> itemIds = new ArrayList<>(Arrays.asList(pageItemIds.split(",; ")));

			for(String itemId: itemIds) {
				hasItem = itemId != null && pageModel.getItemPEById(itemId) != null;

				if (!hasItem) break;
			}
		}

		if (!hasVariable && !hasItem)
			results.addError(elementPath + " > " + getKey(), "There is neither variable '" + pageVarName + "' nor page item '"
					+ pageItemIds + "'");
		// TODO <fix> доделать валидацию (содержится ли параметр в айтеме)
	}

	public String getKey() {
		return "Reference";
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}
	
}
