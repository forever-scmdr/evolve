package ecommander.persistence.itemquery;

import ecommander.model.item.Compare;
import ecommander.model.item.ParameterDescription;
/**
 * Одиночный критерий - Один параметр, одно значение
 * @author EEEE
 *
 */
class UserGroupParamCriteria extends SingleParamCriteria {
	
	UserGroupParamCriteria(int groupId, String tableName) {
		super(ParameterDescription.GROUP, null, groupId + "", "=", null, tableName, Compare.SOME);
	}
}