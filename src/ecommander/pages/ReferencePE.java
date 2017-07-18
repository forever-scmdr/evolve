package ecommander.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ecommander.pages.var.Variable;
import ecommander.pages.var.VariablePE;
import org.apache.commons.lang3.StringUtils;

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
	private String pageItemId = null;
	private ExecutablePagePE pageModel = null;
	
	private ReferencePE(String pageVarName, String paramName, String pageItemId) {
		this.pageVarName = pageVarName;
		this.pageItemId = pageItemId;
		this.paramName = paramName;
	}
	
	private ReferencePE(String pageVarName, String paramName, String pageItemId, ExecutablePagePE parentPage) {
		this(pageVarName, paramName, pageItemId);
		this.pageModel = parentPage;
	}
	
	public static ReferencePE createUrlReference(String pageVarName) {
		return new ReferencePE(pageVarName, null, null);
	}
	
	public static ReferencePE createPageReference(String pageItemId) {
		return new ReferencePE(null, null, pageItemId);
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
		return pageItemId != null && paramName == null;
	}

	public boolean isUrlKeyUnique() {
		return pageVarName != null && paramName == null && pageModel.getInitVariablePE(pageVarName).isStyleTranslit();
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		ReferencePE clone = new ReferencePE(pageVarName, paramName, pageItemId, parentPage);
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
		return pageModel.getItemPEById(pageItemId);
	}

	public void validate(String elementPath, ValidationResults results) {
		// Есть ли айтем с заданным ID
		boolean hasVariable = pageVarName != null && pageModel.getVariable(pageVarName) != null;
		boolean hasItem = pageItemId != null && pageModel.getItemPEById(pageItemId) != null;
		if (!hasVariable && !hasItem)
			results.addError(elementPath + " > " + getKey(), "There is neither variable '" + pageVarName + "' nor page item '"
					+ pageItemId + "'");
		// TODO <fix> доделать валидацию (содержится ли параметр в айтеме)
	}

	public String getKey() {
		return "Reference";
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}
	
}
