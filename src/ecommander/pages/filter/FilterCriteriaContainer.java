package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;

/**
 * Контейнер критериев фильтрации
 * Должен поддерживать добавление всех типов критериев
 * Created by E on 13/6/2017.
 */
public interface FilterCriteriaContainer {
	void processParameterCriteria(ParameterCriteriaPE crit) throws EcommanderException;
	void processDescendantParameterCriteria(ParameterCriteriaPE crit) throws EcommanderException;
	void processParentalCriteria(ParentalCriteriaPE crit) throws EcommanderException;
	void processSortingCriteriaPE(SortingCriteriaPE crit) throws EcommanderException;
	void processFulltextCriteriaPE(FulltextCriteriaPE crit) throws Exception;
}
