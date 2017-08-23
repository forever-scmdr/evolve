package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.filter.FilterDefinition;
import ecommander.pages.*;
import ecommander.pages.var.FilterStaticVariable;
import ecommander.pages.var.ValueOrRef;
import ecommander.pages.var.Variable;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
/**
 * Используется для хранения параметров (не Parameter, а просто парамтеров), 
 * которые нужны для загрузки нужных айтемов
 * Есть методы для сериализации и десериализации (для передачи в URL)
 * 
 
// Если есть какой-то специальный загрузчик (должен быть потомком SearchLoaderDecorator)
// Если применяется пользовательский фильтр, то должны указываться айтем и его параметр, в котором хранится фильтр
// Если в фильтре есть поля ввода, требующие выбора значения из списка, то все эти значения можно подгрузить в момент загрузки страницы,
// для этого надо указать preload-domains="yes"
<filter loader="ecommander.specific.SomeSpecialSiteSearchLoaderDecorator" item="some_id" parameter="param_name" preload-domains="yes">  
	<sorting direction="asc">
		<... variable .../>
	</sorting>
	<limit>
		<... variable .../>
	</limit>
	<pages>
		<... variable .../>
	</pages>
	<parameter name="имя параметра" sign="=">
		<... variable .../>
	</parameter>
	<parameter name="имя параметра 2" sign="=" operation="AND">
		<... variable .../>
	</parameter>
	<fulltext name="имя полнотекстового параметра" limit="500"> // полнотекстовый поиск по определенному параметру
		<... variable .../>
	</fulltext>
	<fulltext limit="500" type="prefix">	// полнотекстовый поиск по всем параметрам. Для разбора запроса и в качестве алгоритма
		<... variable .../>					// поиска используется стандартный класс с зарегистрированным названием "prefix"
	</fulltext>

	<fulltext limit="10" type="ecommander.persistence.itemquery.TermPrefixFulltextCriteria$Factory">	
		<... variable .../>					// используется нестандартный класс (указывается фактори для создания объектов этого класса)
	</fulltext>								// Этот класс должен расширять класс FulltextCriteria
	<predecessor item="parent_item_page_id"/> // Предшественник айтема по иерархии
	<successor item="child_item_page_id"/> // Потомок айтема по иерархии
</filter>

// Продвинутая строковая фильтрация
// pattern - строка шаблона для сравнения. v - вместо нее (v) подставляется значение переменной
// Символ $ надо всегда эскейпить символом \ (\$) как в примере
<filter operation="OR">
	<parameter name="third_param" sign="rlike" pattern="(^|,|, )+v(,|\$)+">
		<var...>
	</parameter>
	<parameter>
		...
	</parameter>
</filter>

// Пользовательский фильтр (фильтр создан пользвателем и сохранен как значение параметра определенного айтема)
// item - страничный ID айтема, который хранит определение фильтра
// parameter - параметр, который хранит определение фильтра
// var - название страничной переменной, которая хранит пользовательский ввод
<filter item="ID айтема" parameter="название параметра" var="название переменной">
	... // может иметь свои дополнительные статические параметры
</filter>

 * @author EEEE
 * TODO <usability> сделать проверку наличия параметров фильтра на наличие в айтеме, который фильтруется
 */
public class FilterPE extends PageElementContainer implements CacheablePE, LinkPE.LinkContainer, FilterCriteriaContainer {
	public static final String ELEMENT_NAME = "filter";

	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public interface FilterContainer {
		void addFilter(FilterPE filterPE);
	}

	private Variable limit = null;
	private Variable page = null;
	private String userFilterItemId; // При использовании пользовательского фильтра - ID страничного айтема
	private String userFilterParamName; // При использовании пользовательского фильтра - название параметра
	private String userFilterVarName; // При использовании пользовательского фильтра - имя страничной переменной, 
									  // которая хранит ввод пользователя сайта (критерии фильтрации)
	private boolean needPreloadDomains; // нужно ли подгружать возможные значения списочных полей ввода для пользовательского фильтра
	private FilterDefinition filterDef; // Объект Фильтр, который является значением параметра userFilterParamName айтема userFilterItemId
	private ExecutablePagePE parentPage;

	private ItemQuery dbQuery;

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		FilterPE clone = new FilterPE();
		if (container != null)
			((FilterContainer)container).addFilter(clone);
		if (limit != null)
			clone.limit = limit.getInited(parentPage);
		if (page != null)
			clone.page = page.getInited(parentPage);
		if (!StringUtils.isBlank(userFilterItemId) && !StringUtils.isBlank(userFilterParamName) && !StringUtils.isBlank(userFilterVarName)) {
			clone.userFilterItemId = userFilterItemId;
			clone.userFilterParamName = userFilterParamName;
			clone.userFilterVarName = userFilterVarName;
		}
		clone.parentPage = parentPage;
		clone.needPreloadDomains = needPreloadDomains;
		return clone;
	}

	public void addSorting(Variable sortingVar, String sortingDirection, String directionVarName) {
		Variable sortingDir = null;
		if (!StringUtils.isBlank(sortingDirection))
			sortingDir = ValueOrRef.newValue(sortingDirection);
		else if (!StringUtils.isBlank(directionVarName))
			sortingDir = ValueOrRef.newRef(directionVarName);
		addElement(new SortingCriteriaPE(sortingVar, sortingDir));
	}
	/**
	 * Если используется пользовательский фильтр
	 * Инициализация пользовательского фильтра
	 * @param itemId
	 * @param paramName
	 * @param variableName
	 */
	public void setUserFilter(String itemId, String paramName, String variableName, boolean needPreloadDomains) {
		this.userFilterItemId = itemId;
		this.userFilterParamName = paramName;
		this.userFilterVarName = variableName;
		this.needPreloadDomains = needPreloadDomains;
	}
	
	public void addLimit(Variable limitVar) {
		this.limit = limitVar;
	}
	
	public void addPage(Variable pageVar) {
		this.page = pageVar;
	}

	public void setFulltext(FulltextCriteriaPE fulltext) {
		addElement(fulltext);
	}

	public boolean hasPage() {
		return page != null && !page.isEmpty();
	}
	
	public boolean hasLimit() {
		return limit != null && !limit.isEmpty();
	}
	
	public int getPage() {
		return Integer.parseInt(page.writeSingleValue());
	}
	
	public Variable getPageVariable() {
		return page;
	}
	
	public int getLimit() {
		return Integer.parseInt(limit.writeSingleValue());
	}

	/**
	 * Получить пользовательский фильтр
	 * @return если есть фильтр, то вернуть его, если нету, вернуть null
	 */
	public final FilterDefinition getUserFilter() {
		if (filterDef == null) {
			if (!StringUtils.isBlank(userFilterItemId) && !StringUtils.isBlank(userFilterParamName)) {
				String filterXML = getCachedContents();
				try {
					filterDef = FilterDefinition.create(filterXML);
				} catch (Exception e) {
					ServerLogger.error("User filter has wrong format", e);
				}
			}
		}
		return filterDef;
	}
	
	public final boolean hasUserFilter() {
		return getUserFilter() != null && parentPage.getVariable(userFilterVarName) != null;
	}
	
	public final String getUserFilterParamName() {
		return userFilterParamName;
	}
	/**
	 * Добавить критерии фильтрации к запросу для извлечения айтемов.
	 * Может возникнуть ситуация, когда можно сразу определить, что фильтр вернет пустое множество айтемов.
	 * В этом случае метод возвращает false, это значит, что запрос к БД производить не нужно, достаточно вернуть пустое множество.
	 * В других случаях метод возвращает true.
	 * @param dbQuery - запрос ItemQuery, к которому добавляются критерии
	 * @return
	 * @throws EcommanderException
	 */
	public void appendCriteriasToQuery(ItemQuery dbQuery) throws Exception {

		this.dbQuery = dbQuery;

		/* *** Добавление пользовательского фильтра ****/ 
		
		if (hasUserFilter()) {
			Variable var = parentPage.getVariable(userFilterVarName);
			FilterStaticVariable filterVar = new FilterStaticVariable(userFilterVarName, var.writeSingleValue());
			parentPage.addVariable(filterVar); // добавить переменную для последующего вывода при выводе XML страницы
			dbQuery.createFilter(filterDef, filterVar);
			// Заменить значение параметра фильтра новым значением, для того, чтобы фильтр выводился в виде XML,
			// а не в виде escaped XML. Это нужно для того, чтобы можно было напрямую работать с определением фильтра
			// в XSL шаблонах, не прибегая к функции xsl:parse
			updateItemFilterParameter();
		}
		
		/* *** Добавление статического фильтра из определения страницы ****/

		if (!dbQuery.hasFilter())
			dbQuery.createFilter();

		// Все вложенные элементы - критерии
		for (PageElement element : getAllNested()) {
			((FilterCriteriaPE) element).process(this);
		}

		// Лимит
		if (hasLimit()) {
			int page = 1;
			if (hasPage())
				page = getPage();
			dbQuery.setLimit(getLimit(), page);
		}
	}

	public String getKey() {
		return "Filter";
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		boolean userFilter 
			= !StringUtils.isBlank(userFilterItemId) || !StringUtils.isBlank(userFilterParamName) || !StringUtils.isBlank(userFilterVarName);
		
		// Пользовательский фильтр
		if (userFilter) {
			// Сущетвование айтемов и параметров
			ItemPE pageItem = parentPage.getItemPEById(userFilterItemId);
			if (pageItem == null) {
				results.addError(elementPath + " > " + getKey(), "there is no '" + userFilterItemId + "' page item on current page");
				return false;
			}
			ItemType itemDesc = ItemTypeRegistry.getItemType(pageItem.getItemName());
			if (itemDesc.getParameter(userFilterParamName) == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + userFilterParamName 
						+ "' parameter in '" + itemDesc.getName() + "' item");
			if (!userFilterVarName.startsWith("$") && parentPage.getVariable(userFilterVarName) == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + userFilterVarName + "' page variable on current page");
			// Корректность использования кеширования
			if (needPreloadDomains && !pageItem.hasCacheVars()) {
				results.addError(elementPath + " > " + getKey(), "Page " + pageItem.getKey()
						+ " must have attribute cache-vars set in order to operate filter cache correctly");
			}
		}

		// Ограничения количества и страницы
		if (limit != null)
			limit.validate(elementPath + " > " + getKey(), results);
		if (page != null)
			page.validate(elementPath + " > " + getKey(), results);

		return results.isSuccessful();
	}

	public boolean isCacheable() {
		return hasUserFilter() && needPreloadDomains;
	}

	public String getCacheableId() {
		return parentPage.getItemPEById(userFilterItemId).getCacheableId();
	}

	public void setCachedContents(String cache) {
		if (parentPage.getItemPEById(userFilterItemId).hasFoundItems())
			parentPage.getItemPEById(userFilterItemId).getSingleFoundItem().setValue(userFilterParamName, cache);
	}

	public String getCachedContents() {
		if (parentPage.getItemPEById(userFilterItemId).hasFoundItems())
			return parentPage.getItemPEById(userFilterItemId).getSingleFoundItem().getStringValue(userFilterParamName);
		return Strings.EMPTY;
	}
	/**
	 * Обновить параметр айтема, который хранит определение фильтра
	 * Установить определение фильтра, которое в данный момент хранится в этом объекте FilterPE
	 */
	public void updateItemFilterParameter() {
		setCachedContents(filterDef.generateXML());
	}

	public void addLink(LinkPE linkPE) {
		// Добавить переменную, которая должна использоваться для передачи занчения фильтра
		linkPE.addStaticVariable(LinkPE.VAR_VARIABLE, userFilterVarName);
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	public void processParameterCriteria(ParameterCriteriaPE crit) {
		// Переменная-значение критерия может хранить как один параметр, так и массив параметров
		dbQuery.addParameterCriteria(crit.getParam(dbQuery.getItemToFilter()), crit.getValueArray(), crit.getSign(),
				crit.getPattern(), crit.getCompareType());
	}

	@Override
	public void processParentalCriteria(ParentalCriteriaPE crit) {
		if (crit.isPredecessor())
			dbQuery.addPredecessors(crit.getAssocName(), crit.getSign(), crit.getLoadedItemIds(), crit.getCompType());
		else
			dbQuery.addSuccessors(crit.getAssocName(), crit.getSign(), crit.getLoadedItemIds(), crit.getCompType());
	}

	@Override
	public void processSortingCriteriaPE(SortingCriteriaPE crit) {
		List<String> values = crit.getSortingParameter().writeAllValues();
		String varName = crit.getSortingParameter().getName();
		// Если значений много и у переменной есть название, то нужна сортировка по заданным значениям
		if (values.size() > 1 || StringUtils.isNotBlank(varName))
			dbQuery.addSorting(varName, crit.getSortingDirection().writeSingleValue(), values);
		else if (StringUtils.isNotBlank(crit.getSortingParameter().writeSingleValue()))
			dbQuery.addSorting(crit.getSortingParameter().writeSingleValue(), crit.getSortingDirection().writeSingleValue());
	}

	@Override
	public void processFulltextCriteriaPE(FulltextCriteriaPE crit) throws Exception {
		dbQuery.setFulltextCriteria(crit.getTypes(), crit.getQueries(), crit.getMaxResultCount(), crit.getParamName(),
				crit.getCompareType(), crit.getThreshold());
	}

	@Override
	public void processOption(FilterOptionPE option) throws Exception {
		dbQuery.startOption();
		for (PageElement element : option.getAllNested()) {
			((FilterCriteriaPE)element).process(this);
		}
		dbQuery.endOption();
	}

	@Override
	public void processAssociatedCriteria(AssociatedItemCriteriaPE associated) throws Exception {
		if (associated.isParent())
			dbQuery.startParentCriteria(associated.getItemName(), associated.getAssocName());
		else
			dbQuery.startChildCriteria(associated.getItemName(), associated.getAssocName());
		for (PageElement element : associated.getAllNested()) {
			((FilterCriteriaPE)element).process(this);
		}
		dbQuery.endCurrentCriteria();
	}
}
