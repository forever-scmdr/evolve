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
	protected String INDEX_TABLE; // Псевдоним таблицы с параметрами (ItemIndex) для данного параметра
	private String groupId;
	/**
	 * В некоторый случаях в фильтре присутствуют несколько критериев с одним и тем же параметром.
	 * В этом случае нет необходимости делать повторное соединение с индексной таблицей, достаточно использовать
	 * соществующее соединение (и сущестующий псевдоним этой таблицы, который передаются параметром tableName)
	 */
	private boolean needJoin = true; // Нужно ли соединение

	/**
	 * Параметр needJoin:
	 * В некоторый случаях в фильтре присутствуют несколько критериев с одним и тем же параметром.
	 * В этом случае нет необходимости делать повторное соединение с индексной таблицей, достаточно использовать
	 * соществующее соединение (и сущестующий псевдоним этой таблицы, который передаются параметром tableName)
	 * @param param
	 * @param item
	 * @param tableName - псевдоним индексной таблицы
	 * @param groupName - название группы (главный флиьтр - "", опция или ассоциированный айтем)
	 * @param isParentOption - является контейнер этого критерия оцией (тогда нужна связь с глобальной таблией айтема I)
	 */
	ParameterCriteria(ParameterDescription param, ItemType item, String tableName, String groupName, boolean isParentOption) {
		this.param = param;
		this.INDEX_TABLE = groupName + tableName;
		this.item = item;
		if (isParentOption)
			this.groupId = "";
		else
			this.groupId = groupName;
	}
	
	public final void appendQuery(TemplateQuery query) {
		if (needJoin) {
			final String INDEX_DOT = INDEX_TABLE + ".";
			final String GROUP_ITEM_TABLE = groupId + "I.";

			// Добавление таблицы в INNER JOIN
			TemplateQuery join = query.getSubquery(JOIN);
			String indexTableName = DataTypeMapper.getTableName(param.getType());
			join.INNER_JOIN(indexTableName + " AS " + INDEX_TABLE, GROUP_ITEM_TABLE + I_ID, INDEX_DOT + II_ITEM_ID);

			TemplateQuery wherePart = query.getSubquery(WHERE);

			// Добавление критерия типа айтема
			// Только для пользовательских фильтров
			if (item.isUserDefined()) {
				wherePart.AND().col_IN(INDEX_DOT + II_ITEM_TYPE).intIN(ItemTypeRegistry.getItemExtendersIds(item.getTypeId()));
			}

			// Добавление ID параметра
			wherePart.AND().col(INDEX_DOT + II_PARAM).int_(param.getId());
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

	public final String getIndexTableName() {
		return INDEX_TABLE;
	}

	public final int getParameterId() {
		return param.getId();
	}

	public final void optimizeJoins(String newTableName) {
		this.INDEX_TABLE = newTableName;
		needJoin = false;
	}
}
