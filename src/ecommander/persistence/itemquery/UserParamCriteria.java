package ecommander.persistence.itemquery;

import ecommander.model.item.COMPARE_TYPE;
import ecommander.model.item.ParameterDescription;
/**
 * Одиночный критерий - Один параметр, одно значение
 * @author EEEE
 *
 */
class UserParamCriteria extends SingleParamCriteria {
	
	UserParamCriteria(long userId, String tableName) {
		super(ParameterDescription.USER, null, userId + "", "=", null, tableName, COMPARE_TYPE.SOME);
	}
}