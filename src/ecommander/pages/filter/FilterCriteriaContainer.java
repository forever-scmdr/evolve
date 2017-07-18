package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;

/**
 * Контейнер критериев фильтрации
 * Должен поддерживать добавление всех типов критериев
 * Каждый метод добавляет к конструируемому запросу соответствующую часть
 * Created by E on 13/6/2017.
 */
public interface FilterCriteriaContainer {
	/**
	 * Обычный критерий параметра
	 * @param crit
	 * @throws EcommanderException
	 */
	void processParameterCriteria(ParameterCriteriaPE crit) throws EcommanderException;

	/**
	 * Критерий предшественника
	 * @param crit
	 * @throws EcommanderException
	 */
	void processParentalCriteria(ParentalCriteriaPE crit) throws EcommanderException;

	/**
	 * Сортировка
	 * @param crit
	 * @throws EcommanderException
	 */
	void processSortingCriteriaPE(SortingCriteriaPE crit) throws EcommanderException;

	/**
	 * Полнотекстовый поиск
	 * @param crit
	 * @throws Exception
	 */
	void processFulltextCriteriaPE(FulltextCriteriaPE crit) throws Exception;

	/**
	 * Блок параметров (одна из нескольких опций, объединенных OR)
	 * @param option
	 * @throws Exception
	 */
	void processOption(FilterOptionPE option) throws Exception;

	/**
	 * Критерий не самого искомого, а ассоциированного с ним айтема
	 * @param associated
	 * @throws Exception
	 */
	void processAssociatedCriteria(AssociatedItemCriteriaPE associated) throws Exception;
}
