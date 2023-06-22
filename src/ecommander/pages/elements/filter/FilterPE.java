package ecommander.pages.elements.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.common.exceptions.EcommanderException;
import ecommander.model.item.COMPARE_TYPE;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.LOGICAL_SIGN;
import ecommander.model.item.filter.FilterDefinition;
import ecommander.pages.elements.CacheablePE;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.ItemPE;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.LinkPE.LinkContainer;
import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.PageElementContainer;
import ecommander.pages.elements.ValidationResults;
import ecommander.pages.elements.variables.FilterStaticVariablePE;
import ecommander.pages.elements.variables.ReferenceVariablePE;
import ecommander.pages.elements.variables.StaticVariablePE;
import ecommander.pages.elements.variables.VariablePE;
import ecommander.persistence.itemquery.ItemQuery;
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
public class FilterPE extends PageElementContainer implements CacheablePE, LinkContainer {
	public static final String ELEMENT_NAME = "filter";
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public static interface FilterContainer {
		void addFilter(FilterPE filterPE);
	}
	/**
	 * Класс для критериев predecessor и successor
	 * @author EEEE
	 *
	 */
	private static class ParentalCriteria {
		private final String pageItemId; // страничный ID предшественников или потомков айтема
		private final String sign; // IN или NOT IN
		private final COMPARE_TYPE compType; // строгий критерий или нет

		private ParentalCriteria(String pageItemId, String sign, COMPARE_TYPE compType) {
			this.pageItemId = pageItemId;
			this.sign = " " + sign + " ";
			this.compType = compType;
		}
	}
	
	private static class SortingCriteria {
		private VariablePE sortingParameter = null;
		private VariablePE sortingDirection = new StaticVariablePE("dir", "ASC");
		
		private SortingCriteria(VariablePE sortingParameter, VariablePE sortingDirection, ExecutablePagePE parentPage) {
			this.sortingParameter = (VariablePE)sortingParameter.createExecutableClone(null, parentPage);
			if (sortingDirection != null)
				this.sortingDirection = (VariablePE)sortingDirection.createExecutableClone(null, parentPage);
		}
		
		private SortingCriteria(VariablePE sortingParameter, VariablePE sortingDirection) {
			this.sortingParameter = sortingParameter;
			if (sortingDirection != null)
				this.sortingDirection = sortingDirection;
		}
	}
	
//	private VariablePE sortingParameter = null;
//	private VariablePE sortingDirection = new StaticVariablePE("dir", "ASC");
	private ArrayList<SortingCriteria> sorting = null; // Массив параметров сортировки
	private LOGICAL_SIGN operation = LOGICAL_SIGN.AND;
	private VariablePE limit = null;
	private VariablePE page = null;
	private ArrayList<ParentalCriteria> predecessors = null; // Массив страничных ID предшественников айтема
	private ArrayList<ParentalCriteria> successors = null; // Массив страничных ID потомков айтема
	private String userFilterItemId; // При использовании пользовательского фильтра - ID страничного айтема
	private String userFilterParamName; // При использовании пользовательского фильтра - название параметра
	private String userFilterVarName; // При использовании пользовательского фильтра - имя страничной переменной, 
									  // которая хранит ввод пользователя сайта (критерии фильтрации)
	private boolean needPreloadDomains; // нужно ли подгружать возможные значения списочных полей ввода для пользовательского фильтра
	private FilterDefinition filterDef; // Объект Фильтр, который является значением параметра userFilterParamName айтема userFilterItemId
	private FulltextCriteriaPE fulltext; // Запрос полнотекстового поиска
	private ExecutablePagePE parentPage;
	
	@SuppressWarnings("unchecked")
	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		FilterPE clone = new FilterPE();
		if (container != null)
			((FilterContainer)container).addFilter(clone);
		if (sorting != null) {
			clone.sorting = new ArrayList<SortingCriteria>();
			for (SortingCriteria sort : sorting) {
				clone.sorting.add(new SortingCriteria(sort.sortingParameter, sort.sortingDirection, parentPage));
			}
		}
//		if (sortingParameter != null)
//			clone.sortingParameter = (VariablePE)sortingParameter.createExecutableClone(null, parentPage);
//		clone.sortingDirection = (VariablePE)sortingDirection.createExecutableClone(null, parentPage);
		if (limit != null)
			clone.limit = (VariablePE)limit.createExecutableClone(null, parentPage);
		if (page != null)
			clone.page = (VariablePE)page.createExecutableClone(null, parentPage);
		if (predecessors != null)
			clone.predecessors = (ArrayList<ParentalCriteria>)predecessors.clone();
		if (successors != null)
			clone.successors = (ArrayList<ParentalCriteria>)successors.clone();
		if (fulltext != null)
			clone.fulltext = (FulltextCriteriaPE)fulltext.createExecutableClone(null, parentPage);
		if (!StringUtils.isBlank(userFilterItemId) && !StringUtils.isBlank(userFilterParamName) && !StringUtils.isBlank(userFilterVarName)) {
			clone.userFilterItemId = userFilterItemId;
			clone.userFilterParamName = userFilterParamName;
			clone.userFilterVarName = userFilterVarName;
		}
		clone.operation = operation;
		clone.parentPage = parentPage;
		clone.needPreloadDomains = needPreloadDomains;
		return clone;
	}

	public void addSorting(VariablePE sortingVar, String sortingDirection, String directionVarName) {
		if (sorting == null)
			sorting = new ArrayList<SortingCriteria>();
		VariablePE sortingDir = null;
		if (!StringUtils.isBlank(sortingDirection))
			sortingDir = new StaticVariablePE("dir", sortingDirection);
		else if (!StringUtils.isBlank(directionVarName))
			sortingDir = new ReferenceVariablePE("dir", directionVarName);
		sorting.add(new SortingCriteria(sortingVar, sortingDir));
	}
	/**
	 * Установить операцию (применяется для всего фильтра, т.к. для отдельных критериев не имеет смысла)
	 * @param operation
	 */
	public void setOperation(LOGICAL_SIGN operation) {
		this.operation = operation;
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
	
	public void addLimit(VariablePE limitVar) {
		this.limit = limitVar;
	}
	
	public void addPage(VariablePE pageVar) {
		this.page = pageVar;
	}

	public void addCriteria(FilterCriteriaPE criteria) {
		addElement(criteria);
	}

	public void setFulltext(FulltextCriteriaPE fulltext) {
		this.fulltext = fulltext;
	}
	
	public void addPredecessor(String predecessorId, String sign, COMPARE_TYPE compType) {
		if (predecessors == null)
			predecessors = new ArrayList<ParentalCriteria>();
		if (StringUtils.isBlank(sign))
			sign = " IN ";
		predecessors.add(new ParentalCriteria(predecessorId, sign, compType));
	}
	
	public void addSuccessors(String successorId, String sign, COMPARE_TYPE compType) {
		if (successors == null)
			successors = new ArrayList<ParentalCriteria>();
		if (StringUtils.isBlank(sign))
			sign = " IN ";
		successors.add(new ParentalCriteria(successorId, sign, compType));
	}

	public boolean hasPage() {
		return page != null && !StringUtils.isBlank(page.output());
	}
	
	public boolean hasLimit() {
		return limit != null && !StringUtils.isBlank(limit.output());
	}
	
	public int getPage() {
		return Integer.parseInt(page.output());
	}
	
	public VariablePE getPageVariable() {
		return page;
	}
	
	public int getLimit() {
		return Integer.parseInt(limit.output());
	}
	
	public FulltextCriteriaPE getFulltext() {
		return fulltext;
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
	@SuppressWarnings("unchecked")
	public boolean appendCriteriasToQuery(ItemQuery dbQuery) throws EcommanderException {
		
		/* *** Добавление пользовательского фильтра ****/ 
		
		if (hasUserFilter()) {
			VariablePE var = parentPage.getVariable(userFilterVarName);
			FilterStaticVariablePE filterVar = new FilterStaticVariablePE(userFilterVarName, var.output());
			parentPage.addVariable(filterVar); // добавить переменную для последующего вывода при выводе XML страницы
			dbQuery.createFilter(filterDef, filterVar);
			// Заменить значение параметра фильтра новым значением, для того, чтобы фильтр выводился в виде XML,
			// а не в виде escaped XML. Это нужно для того, чтобы можно было напрямую работать с определением фильтра
			// в XSL шаблонах, не прибегая к функции xsl:parse
			updateItemFilterParameter();
		}
		
		/* *** Добавление статического фильтра из определения страницы ****/

		if (!dbQuery.hasFilter())
			dbQuery.createFilter(operation);
		// Параметры
		Boolean isValid = null;
		for (PageElement element : getAllNested()) {
			if (element instanceof FilterCriteriaPE) {
				FilterCriteriaPE crit = (FilterCriteriaPE) element;
				if (crit.isValid()) {
					// Переменная-значение критерия может хранить как один параметр, так и массив параметров
					dbQuery.addParameterCriteria(crit.getParam(dbQuery.getItemToFilter()), crit.getValueArray(), crit.getSign(),
							crit.getPattern(), crit.getCompareType());
					isValid = true;
				}
				// Если критерий имеет тип сравнения SOME или EVERY и не является валидным (не содержит образец для сравнения),
				// то фильтр должен вернуть пустое множество, при условии что критерии фильтра соединяются логическим знаком И,
				// (т. е. в большинстве случаев)
				else if ((crit.getCompareType() == COMPARE_TYPE.SOME || crit.getCompareType() == COMPARE_TYPE.EVERY)) {
					if (operation == LOGICAL_SIGN.AND)
						return false;
					else
						isValid = isValid == null ? false : isValid || false;
				}
			}
		}
		if (isValid != null && !isValid)
			return false;
		
		// Полнотекстовый поиск
		if (fulltext != null) {
			dbQuery.setFulltextCriteria(fulltext.getTypes(), fulltext.getQueries(), fulltext.getMaxResultCount(), fulltext.getParamName(),
					fulltext.getCompareType(), fulltext.getThreshold());
		}
		
		// Предшественники
		if (predecessors != null && predecessors.size() > 0) {
			for (ParentalCriteria pred : predecessors) {
				dbQuery.addPredecessors(pred.sign, parentPage.getItemPEById(pred.pageItemId).getFoundItemRefIds(), pred.compType);
			}
		}
		
		// Потомки
		if (successors != null && successors.size() > 0) {
			for (ParentalCriteria succ : successors) {
				dbQuery.addSuccessors(succ.sign, parentPage.getItemPEById(succ.pageItemId).getFoundItemRefIds(), succ.compType);
			}
		}
		
		// Сортировка
		if (sorting != null) {
			for (SortingCriteria sort : sorting) {
				List<String> values = sort.sortingParameter.outputArray();
				String varName = sort.sortingParameter.getName();
				// Если значений много и у переменной есть название, то нужна сортировка по заданным значениям
				if (values.size() > 1 || StringUtils.isNotBlank(varName))
					dbQuery.addSorting(varName, sort.sortingDirection.output(), values);
				else if (StringUtils.isNotBlank(sort.sortingParameter.output()))
					dbQuery.addSorting(sort.sortingParameter.output(), sort.sortingDirection.output());
			}
		}
		
		// Лимит
		if (hasLimit()) {
			int page = 1;
			if (hasPage())
				page = getPage();
			dbQuery.setLimit(getLimit(), page);
		}
		
		return true;
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
			if (pageItem == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + userFilterItemId + "' page item on current page");
			ItemType itemDesc = ItemTypeRegistry.getItemType(pageItem.getItemName());
			if (itemDesc.getParameter(userFilterParamName) == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + userFilterParamName 
						+ "' parameter in '" + itemDesc.getName() + "' item");
			if (!userFilterVarName.startsWith("$") && parentPage.getVariable(userFilterVarName) == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + userFilterVarName + "' page variable on current page");
			// Корректность использования кеширования
			if (needPreloadDomains && !pageItem.hasCacheVars()) {
				Integer[] containerIds = ItemTypeRegistry.getItemContainers(itemDesc.getTypeId());
				for (Integer id : containerIds) {
					if (ItemTypeRegistry.getItemType(id).isSubitemMultiple(itemDesc.getName())) {
						results.addError(elementPath + " > " + getKey(), "Page " + pageItem.getKey() 
								+ " must have attribute cache-vars set in order to operate filter cache correctly");
						break;
					}
				}
			}
		}
		
		// Предшественники и последователи
		ArrayList<ParentalCriteria> pred_succ = new ArrayList<ParentalCriteria>();
		if (predecessors != null)
			pred_succ.addAll(predecessors);
		if (successors != null)
			pred_succ.addAll(successors);
		for (ParentalCriteria crit : pred_succ) {
			if (parentPage.getItemPEById(crit.pageItemId) == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + crit.pageItemId + "' page item on current page");
		}
		
		// Сортировка
		if (sorting != null) {
			for (SortingCriteria s : sorting) {
				s.sortingParameter.validate(elementPath + " > " + getKey(), results);
				if (results.isSuccessful()) {
					if (s.sortingParameter instanceof StaticVariablePE
							&& ((ItemType) results.getBufferData()).getParameter(s.sortingParameter.output()) == null) {
						results.addError(elementPath + " > " + getKey(), "There is no '" + s.sortingParameter.output() + "' parameter in '"
								+ ((ItemType) results.getBufferData()).getName() + "' item");
					}
					s.sortingParameter.validate(elementPath + " > " + getKey(), results);
					if (!"ASC".equals(s.sortingDirection.output()) && !"DESC".equals(s.sortingDirection.output()))
						results.addError(elementPath + " > " + getKey(),
								"'" + s.sortingDirection.output() + "' is not valid sorting direction");
				}
			}
		}
		
		// Ограничения количества и страницы
		if (limit != null)
			limit.validate(elementPath + " > " + getKey(), results);
		if (page != null)
			page.validate(elementPath + " > " + getKey(), results);
		
		// Полнотекстовый поиск
		if (fulltext != null)
			fulltext.validate(elementPath + " > " + getKey(), results);
		
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
}
