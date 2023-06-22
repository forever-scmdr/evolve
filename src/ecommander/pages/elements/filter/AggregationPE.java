package ecommander.pages.elements.filter;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.exceptions.EcommanderException;
import ecommander.model.item.COMPARE_TYPE;
import ecommander.model.item.LOGICAL_SIGN;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.PageElementContainer;
import ecommander.pages.elements.ValidationResults;
import ecommander.pages.elements.variables.ReferenceVariablePE;
import ecommander.pages.elements.variables.StaticVariablePE;
import ecommander.pages.elements.variables.VariablePE;
import ecommander.persistence.itemquery.ItemQuery;
/**
	***********************   ГРУППИРОВКА   ***********************
	
	Аналогично группировке в SQL
	
	<item name="price" quantifier="multiple" id="price_by_device">
		<aggregation function="MIN" parameter="price"> - к этому параметру применяется агрегирующия функция
			<parameter name="code" sign="="> - по этому параметру происходит группировка
				<var...>
			</parameter>
			<sorting.../> // аналогично фильтру, только могут использоваться только параметры, используемые в этой группировке
		</aggregation>
	</item>
	
	<item name="price" quantifier="multiple" id="price_by_device">
		<aggregation parameter="code"/> - просто список всех возможных значений одного параметра
	</item>
	
	<item name="price" quantifier="multiple" id="price_by_device">
		<aggregation parameter-var="some_var"/> - список всех возможных значений одного параметра, имя которого хранит переменная
	</item>

 * @author EEEE
 * TODO <usability> сделать проверку наличия параметров фильтра на наличие в айтеме, который фильтруется
 */
public class AggregationPE extends PageElementContainer {
	public static final String ELEMENT_NAME = "aggregation";
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public static interface AggregationContainer {
		void addAggregate(AggregationPE aggregate);
	}
	
	private VariablePE groupParameter; // к этому параметру применяется агрегирующия функция
	private String function; // Функция группировки
	private VariablePE sortingParameter = null;
	private VariablePE sortingDirection = null;
	private LOGICAL_SIGN operation = LOGICAL_SIGN.AND;
	
	public AggregationPE(VariablePE parameter) {
		super();
		this.groupParameter = parameter;
	}

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		AggregationPE clone = new AggregationPE((VariablePE)groupParameter.createExecutableClone(null, parentPage));
		clone.setFunction(function);
		if (container != null)
			((AggregationContainer)container).addAggregate(clone);
		if (sortingParameter != null)
			clone.sortingParameter = (VariablePE)sortingParameter.createExecutableClone(null, parentPage);
		if (sortingDirection != null)
			clone.sortingDirection = (VariablePE)sortingDirection.createExecutableClone(null, parentPage);
		return clone;
	}

	public void setFunction(String function) {
		this.function = function;
	}
	
	public void setOperation(LOGICAL_SIGN operation) {
		this.operation = operation;
	}
	
	public boolean hasFunction() {
		return !StringUtils.isBlank(function);
	}
	
	public boolean hasSorting() {
		return sortingParameter != null && !StringUtils.isBlank(sortingParameter.output());
	}
	/**
	 * Есть ли параметры, по которым происходит группировка
	 * @return
	 */
	public boolean hasGrouping() {
		return hasNested();
	}
	/**
	 * Добавить параметр группировки
	 * @param grouping
	 */
	public void addGroupBy(FilterCriteriaPE grouping) {
		addElement(grouping);
	}
	
	public String getParameter() {
		return groupParameter.output();
	}

	public String getFunction() {
		return function;
	}
	
//	private String getSortParam() {
//		return sortingParameter.output();
//	}
	
	private String getSortingDirection() {
		if (sortingDirection == null)
			return null;
		return sortingDirection.output();
	}
	
	public void addSorting(VariablePE sortingVar, String sortingDirection, String directionVarName) {
		this.sortingParameter = sortingVar;
		if (!StringUtils.isBlank(sortingDirection))
			this.sortingDirection = new StaticVariablePE("dir", sortingDirection);
		else if (!StringUtils.isBlank(directionVarName))
			this.sortingDirection = new ReferenceVariablePE("dir", directionVarName);
			
	}
	/**
	 * Создать билдер для конструирования SQL запроса, содержащего критерии фильтрации данного фильтра
	 * @param itemToGroup - описание айтема, загрузка которого требует применения данного фильтра
	 * @return
	 * @throws EcommanderException
	 */
	public boolean appendCriteriasToQuery(ItemQuery dbQuery) throws EcommanderException {
		if (!dbQuery.hasFilter())
			dbQuery.createFilter(operation);
		
		// Добавление параметров группировки (по которым происходит группировка)
		for (PageElement criteriaPE : getAllNested()) {
			FilterCriteriaPE crit = (FilterCriteriaPE)criteriaPE;
			// Переменная-значение критерия может хранить как один параметр, так и массив параметров
			if (crit.isValid())
				dbQuery.addAggregationGroupBy(crit.getParam(dbQuery.getItemToFilter()), crit.getValueArray(), crit.getSign(),
						crit.getPattern(), /*COMPARE_TYPE.ALL ???*/ COMPARE_TYPE.SOME);
			else {
				// Если критерий имеет тип сравнения SOME или EVERY и не является валидным (не содержит образец для сравнения),
				// то фильтр должен вернуть пустое множество, при условии что критерии фильтра соединяются логическим знаком И,
				// (т. е. в большинстве случаев)
				if ((crit.getCompareType() == COMPARE_TYPE.SOME || crit.getCompareType() == COMPARE_TYPE.EVERY)
						&& operation == LOGICAL_SIGN.AND) {
					return false;
				}
				// Этот случай происходит, когда нужна группировка по значениям этого параметра 
				// (всем значениям, без каких-либо критериев)
				dbQuery.addAggregationGroupBy(crit.getParam(dbQuery.getItemToFilter()));
			}
		}
		
		// Добавление агрегируемого параметра (значения которого подвергаются группировке)
		dbQuery.setAggregation(getParameter(), function, getSortingDirection());
		return true;		
//		// Сортировка
//		if (hasSorting())
//			builder.addGroupSortingCriteria(itemToGroup.getParameter(getSortParam()), getSortingDirection());
	}

	public String getKey() {
		return "Aggregation";
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		if (hasSorting()) {
			sortingParameter.validate(elementPath + " > " + getKey(), results);
			sortingDirection.validate(elementPath + " > " + getKey(), results);
		}
		groupParameter.validate(elementPath + " > " + getKey(), results);
		return true;
	}
	
	public String getElementName() {
		return ELEMENT_NAME;
	}
}
