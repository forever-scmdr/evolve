package ecommander.persistence.itemquery;

import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
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
abstract class ParameterCriteria implements FilterCriteria, ItemQuery.Const, DBConstants.ItemIndexes, DBConstants.ItemTbl {

	protected final ParameterDescription param; // параметр айтема, по которому происходит сравнение
	private final ItemType item; // тип айтемов, которые подвергаются фильтрации
	protected final String INDEX_TABLE; // Псевдоним таблицы с параметрами (ItemIndex) для данного параметра

	ParameterCriteria(ParameterDescription param, ItemType item, String tableName) {
		this.param = param;
		this.INDEX_TABLE = tableName;
		this.item = item;
	}
	
	public final void appendQuery(TemplateQuery query) {
		final String INDEX_DOT = INDEX_TABLE + ".";

		// Добавление таблицы в INNER JOIN
		TemplateQuery join = query.getSubquery(JOIN);
		String indexTableName = DataTypeMapper.getTableName(param.getType());
		join.INNER_JOIN(indexTableName + " AS " + INDEX_TABLE, ITEM_TABLE + I_ID, INDEX_DOT + II_ITEM_ID);

		TemplateQuery wherePart = query.getSubquery(WHERE);

		// Добавление ID параметра
		wherePart.col(INDEX_DOT + II_PARAM).int_(param.getId());

		// Добавление критерия типа айтема
		// Только для пользовательских фильтров
		if (item.isUserDefined()) {
			wherePart.AND().col(INDEX_DOT + II_ITEM_TYPE, " IN").intIN(ItemTypeRegistry.getItemExtendersIds(item.getTypeId()));
		}
		
		// Добавление значения параметра
		if (isNotBlank())
			appendParameterValue(query);
	}

	protected abstract void appendParameterValue(TemplateQuery query);

	String getParameterColumnName() {
		return INDEX_TABLE + "." + II_VALUE;
	}

	public boolean isEmptySet() {
		return false;
	}

}
