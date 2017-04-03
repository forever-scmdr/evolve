package ecommander.persistence.itemquery;

import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import ecommander.persistence.mappers.DataTypeMapper;
/**
 * Абстрактная часть SQL запроса, представляющая собой некоторый критерий выбора по параметру.
 * Подразумевает одно соединение с таблицей параметров, выбор некоторого параметра по ID параметра, ID предка айтема и ID типа айтема
 * @author E
 *
 */
abstract class FilterParameterCriteria implements FilterCriteria, PossibleMainCriteria {

	protected final ParameterDescription param; // параметр айтема, по которому происходит сравнение
	private final ItemType item; // тип айтемов, которые подвергаются фильтрации
	protected final String tableName; // Псевдоним таблицы с параметрами (ItemIndex) для данного параметра
	// Если критерий является главным, то все остальные критерии приравнивают извлекаемый ID айтема к ID, извлекаемому этим критерием
	private boolean isMainCriteria = false;
	private boolean useParentCriteria = false; // нужно ли использоватть критерий поиска по родителю (предку)
	
	FilterParameterCriteria(ParameterDescription param, ItemType item, String tableName) {
		this.param = param;
		this.tableName = tableName;
		this.item = item;
	}
	
	public final void appendQuery(TemplateQuery query) {
		// Добавление таблицы во FROM
		TemplateQuery fromPart = query.getSubquery(ItemQuery.FROM_OPT);
		if (!fromPart.isEmpty())
			fromPart.sql(", ");
		String indexTableName = DataTypeMapper.getTableName(param.getType());
		fromPart.sql(indexTableName + " AS " + tableName);
		
		TemplateQuery wherePart = query.getSubquery(ItemQuery.WHERE_OPT);
		
		// Добавление связи между таблицей этого критерия и общей таблицей (в случае если критерий не главный)
		TemplateQuery joinPart = wherePart.getSubquery(ItemQuery.FILTER_JOIN_OPT);
		if (isMainCriteria) {
			joinPart.getOrCreateSubquery(ItemQuery.COMMON_COL_OPT).sql(tableName + '.' + DBConstants.ItemIndexes.REF_ID);
		} else {
			joinPart.sql(tableName + '.' + DBConstants.ItemIndexes.REF_ID + " = ").subquery(ItemQuery.COMMON_COL_OPT).sql(" AND ");
		}
		
		// -- НАЧАЛО --  Добавление критерия параметра (ID параметра, родитель, тип айтема, значение параметра)
		TemplateQuery critPart = wherePart.getSubquery(ItemQuery.FILTER_CRITS_OPT);
		critPart.sql("(");
		
		// Добавление ID параметра
		critPart.sql(tableName + '.' + DBConstants.ItemIndexes.II_PARAM + '=').setInt(param.getId());
		
		// Добавление критерия родительского айтема (производится централизованно другом месте)
		if (useParentCriteria)
			critPart.sql(" AND " + tableName + '.' + DBConstants.ItemIndexes.ITEM_PARENT).subquery(ItemQuery.PARENT_CRIT_OPT);
		
		// Добавление критерия типа айтема (производится централизованно другом месте)
		// Происходит только в случае, когда параметр не принадлежит айтему, который фильтруется. Иначе это действие бесполезно, т.к.
		// все параметры имеют уникальный ID. Однако если параметр принадлежит предку айтема по иерархии, то этот критерий должен быть
		// добавлен, чтобы исключить айтемы не искомых типов (базового и его других потомков), 
		// значение данного параметра которых подходит под критерий фильтра.
		if (item == null || item.getTypeId() != param.getOwnerItemId())
			critPart.sql(" AND " + tableName + '.' + DBConstants.ItemIndexes.II_ITEM_TYPE).subquery(ItemQuery.TYPE_CRIT_OPT);
		
		// Добавление значения параметра
		appendParameterValue(query);
		
		critPart.sql(")");
		// -- КОНЕЦ -- Добавление критерия параметра
	}
	/**
	 * Создать название название таблицы для этого параметра (SQL: SELECT ... AS id)
	 * @param param
	 * @return
	 */
	public static final String createASID(ParameterDescription param) {
		return "p" + param.getId();
	}
	
	public final void setMain() {
		this.isMainCriteria = true;
	}
	
	public boolean isMain() {
		return isMainCriteria;
	}
	
	protected abstract void appendParameterValue(TemplateQuery query);

	public final void useParentCriteria() {
		this.useParentCriteria = true;
	}

	public final String getSelectedColumnName() {
		return tableName + '.' + DBConstants.ItemIndexes.REF_ID;
	}

	public boolean isEmptySet() {
		return false;
	}

	public String getTableName() {
		return tableName;
	}
	
}
