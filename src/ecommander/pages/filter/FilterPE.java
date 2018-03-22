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
	private UserFilterRefPE userFilter;
	private boolean isUserFiltered = false; // включена ли пользовательская флиьтрация непосредственно (а не во вложенный
											// критерий по ассоциации)
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
		if (hasUserFilter()) {
			clone.userFilter = (UserFilterRefPE) userFilter.createExecutableClone(this, parentPage);
		}
		clone.parentPage = parentPage;
		clone.isUserFiltered = isUserFiltered;
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
	public void setUserFilter(String itemId, String paramName, String variableName, boolean needPreloadDomains, boolean isDirectlyFiltered) {
		this.userFilter = new UserFilterRefPE(itemId, paramName, variableName, needPreloadDomains);
		this.isUserFiltered = isDirectlyFiltered;
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
		return userFilter.getFilterDef();
	}
	
	public final boolean hasUserFilter() {
		return userFilter != null;
	}
	
	public final String getUserFilterParamName() {
		return userFilter.getParamName();
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

		dbQuery.createFilter();
		if (isUserFiltered && userFilter.isValid()) {
			userFilter.apply(dbQuery);
			// Заменить значение параметра фильтра новым значением, для того, чтобы фильтр выводился в виде XML,
			// а не в виде escaped XML. Это нужно для того, чтобы можно было напрямую работать с определением фильтра
			// в XSL шаблонах, не прибегая к функции xsl:parse
			updateItemFilterParameter();
		}

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
		// Пользовательский фильтр
		if (hasUserFilter())
			userFilter.validate(elementPath, results);
		// Ограничения количества и страницы
		if (limit != null)
			limit.validate(elementPath + " > " + getKey(), results);
		if (page != null)
			page.validate(elementPath + " > " + getKey(), results);

		return results.isSuccessful();
	}

	public boolean isCacheable() {
		return hasUserFilter() && userFilter.needPreloadDomains();
	}

	public String getCacheableId() {
		return parentPage.getItemPEById(userFilter.getItemId()).getCacheableId();
	}

	public void setCachedContents(String cache) {
		if (parentPage.getItemPEById(userFilter.getItemId()).hasFoundItems())
			parentPage.getItemPEById(userFilter.getItemId()).getSingleFoundItem().setValue(userFilter.getParamName(), cache);
	}

	public String getCachedContents() {
		if (parentPage.getItemPEById(userFilter.getItemId()).hasFoundItems())
			return parentPage.getItemPEById(userFilter.getItemId()).getSingleFoundItem().getStringValue(userFilter.getParamName());
		return Strings.EMPTY;
	}
	/**
	 * Обновить параметр айтема, который хранит определение фильтра
	 * Установить определение фильтра, которое в данный момент хранится в этом объекте FilterPE
	 */
	public void updateItemFilterParameter() {
		setCachedContents(userFilter.getFilterDef().generateXML());
	}

	public void addLink(LinkPE linkPE) {
		// Добавить переменную, которая должна использоваться для передачи значения фильтра
		if (hasUserFilter())
			linkPE.addStaticVariable(LinkPE.VAR_VARIABLE, userFilter.getFilterVarName());
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
		if (values.size() == 0)
			return;
		String varName = crit.getSortingParameter().getName();
		// Если значений много и у переменной есть название, то нужна сортировка по заданным значениям
		if (values.size() > 1 && StringUtils.isNotBlank(varName))
			dbQuery.addSorting(varName, crit.getSortingDirection().writeSingleValue(), values);
		else if (StringUtils.isNotBlank(crit.getSortingParameter().writeSingleValue()))
			dbQuery.addSorting(crit.getSortingParameter().writeSingleValue(), crit.getSortingDirection().writeSingleValue());
	}

	@Override
	public void processFulltextCriteriaPE(FulltextCriteriaPE crit) throws Exception {
		dbQuery.setFulltextCriteria(crit.getTypes(), crit.getQueries(), crit.getMaxResultCount(), crit.getParamNames(),
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
		// Если к этому критерию ассоциации применяется пользовательский фильтр - добавить его
		if (associated.isUserFiltered())
			userFilter.apply(dbQuery);
		for (PageElement element : associated.getAllNested()) {
			((FilterCriteriaPE)element).process(this);
		}
		dbQuery.endCurrentCriteria();
	}

	public ExecutablePagePE getPageModel() {
		return parentPage;
	}
}
