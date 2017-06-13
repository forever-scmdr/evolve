package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.pages.PageElement;

/**
 * Критерий фильтрации.
 * Добавляет себя в контейнер, вызывая определенный метод контейнера
 * Created by E on 13/6/2017.
 */
public interface FilterCriteria extends PageElement {
	void process(FilterCriteriaContainer cont) throws EcommanderException;
}
