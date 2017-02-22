package ecommander.model.filter;

import ecommander.common.exceptions.EcommanderException;

/**
 * Посетитель для структуты фильтра.
 * Реализации интерфейса нужны когда надо выполнять различные между собой операции с разными компонентами фильтра (группой и инпутом)
 * @author EEEE
 *
 */
public interface FilterDefinitionVisitor {
	abstract void visitGroup(CriteriaGroupDef group) throws EcommanderException;
	abstract void visitInput(InputDef input) throws EcommanderException;
}
