package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.model.Compare;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import ecommander.pages.var.ValueOrRef;
import ecommander.pages.var.Variable;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
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
	public interface AggregationContainer {
		void addAggregate(AggregationPE aggregate);
	}
	
	private Variable groupParameter; // к этому параметру применяется агрегирующия функция
	private String function; // Функция группировки
	private Variable sortingParameter = null;
	private Variable sortingDirection = null;

	public AggregationPE(Variable parameter) {
		super();
		this.groupParameter = parameter;
	}

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		AggregationPE clone = new AggregationPE(groupParameter.getInited(parentPage));
		clone.setFunction(function);
		if (container != null)
			((AggregationContainer)container).addAggregate(clone);
		if (sortingParameter != null)
			clone.sortingParameter = sortingParameter.getInited(parentPage);
		if (sortingDirection != null)
			clone.sortingDirection = sortingDirection.getInited(parentPage);
		return clone;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public boolean hasFunction() {
		return !StringUtils.isBlank(function);
	}
	
	public boolean hasSorting() {
		return sortingParameter != null && !StringUtils.isBlank(sortingParameter.writeSingleValue());
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
	public void addGroupBy(ParameterCriteriaPE grouping) {
		addElement(grouping);
	}
	
	public String getParameter() {
		return groupParameter.writeSingleValue();
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
		return sortingDirection.writeSingleValue();
	}
	
	public void addSorting(Variable sortingVar, String sortingDirection, String directionVarName) {
		this.sortingParameter = sortingVar;
		if (!StringUtils.isBlank(sortingDirection))
			this.sortingDirection = ValueOrRef.newValue(sortingDirection);
		else if (!StringUtils.isBlank(directionVarName))
			this.sortingDirection = ValueOrRef.newRef(directionVarName);
			
	}
	/**
	 * Создать билдер для конструирования SQL запроса, содержащего критерии фильтрации данного фильтра
	 * @param dbQuery
	 * @return
	 * @throws EcommanderException
	 */
	public boolean appendCriteriasToQuery(ItemQuery dbQuery) throws EcommanderException {
		if (!dbQuery.hasFilter())
			dbQuery.createFilter();

		// Добавление агрегируемого параметра (значения которого подвергаются группировке)
		dbQuery.setAggregation(getParameter(), function, getSortingDirection());

		// Добавление параметров группировки (по которым происходит группировка)
		for (PageElement criteriaPE : getAllNested()) {
			ParameterCriteriaPE crit = (ParameterCriteriaPE)criteriaPE;
			// Переменная-значение критерия может хранить как один параметр, так и массив параметров
			if (crit.hasValues())
				dbQuery.addAggregationGroupBy(crit.getParam(dbQuery.getItemToFilter()), crit.getValueArray(), crit.getSign(),
						crit.getPattern(), /*Compare.ALL ???*/ Compare.SOME);
			else {
				// Если критерий имеет тип сравнения SOME или EVERY и не является валидным (не содержит образец для сравнения),
				// то фильтр должен вернуть пустое множество, при условии что критерии фильтра соединяются логическим знаком И,
				// (т. е. в большинстве случаев)
				if ((crit.getCompareType() == Compare.SOME || crit.getCompareType() == Compare.EVERY)) {
					return false;
				}
				// Этот случай происходит, когда нужна группировка по значениям этого параметра 
				// (всем значениям, без каких-либо критериев)
				dbQuery.addAggregationGroupBy(crit.getParam(dbQuery.getItemToFilter()));
			}
		}

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
