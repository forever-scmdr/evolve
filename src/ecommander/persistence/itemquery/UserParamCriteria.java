package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ParameterDescription;
/**
 * Одиночный критерий - Один параметр, одно значение
 * @author EEEE
 *
 */
class UserParamCriteria extends SingleParamCriteria {
	
	UserParamCriteria(long userId, String tableName) {
		super(ParameterDescription.USER, null, userId + "", "=", null, tableName, Compare.SOME);
	}
}