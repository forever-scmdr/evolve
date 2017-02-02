package ecommander.controllers.output;

import ecommander.common.ServerLogger;
import ecommander.common.exceptions.FilterProcessException;
import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.variables.FilterStaticVariablePE;
/**
 * Выводит переменную-параметры фильтрации в виде XML
 *

<filter var="название_переменной">
	<input id="1">значение инпута один</input>
	<input id="2">значение инпута два</input>
	<input id="5">первое значение значение инпута 5</input>
	<input id="5">второе значение значение инпута 5</input>
	<page>5</page>
	<sorting direction="asc" param="222"/>
	<limit>30</limit>
</filter>

название_переменной - название переменной в URL страницы
input - поле ввода, заполненное пользователем
id - ID поля ввода, такое, как хранится в определении фильтра
значение инпута - значение, введенное пользователем
первое ... второе ... - инпуты, которые подразумевают несколько значений, просто перечисляются
page - номер страницы
sorting - сортировка
param - ID переметра сортировки
limit - лимит вывода

 * 
 * 
 * 
 * @author EEEE
 *
 */
public class FilterStaticVariablePEWriter implements PageElementWriter {

	private static final String INPUT_TAG = "input";
	private static final String SORTING_TAG = "sorting";
	private static final String PAGE_TAG = "page";
//	private static final String LIMIT_TAG = "limit";
	private static final String DIRECTION_ATTR = "direction";
	private static final String PARAM_ATTR = "param";
	private static final String ID_ATTR = "id";
	
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) {
		try {
			FilterStaticVariablePE filterVar = (FilterStaticVariablePE)elementToWrite;
			xml.startElement(filterVar.getName());
			for (Integer inputId : filterVar.getPostedInputs()) {
				for (String value : filterVar.getValue(inputId)) {
					xml.startElement(INPUT_TAG, ID_ATTR, inputId).addText(value).endElement();
				}
			}
			if (filterVar.hasSorting())
				xml.addEmptyElement(SORTING_TAG, DIRECTION_ATTR, filterVar.getSortingDirection(), PARAM_ATTR, filterVar.getSortingParamId());
			xml.startElement(PAGE_TAG).addText(filterVar.getPageNumber()).endElement();
			
			xml.endElement();
		} catch (FilterProcessException e) {
			ServerLogger.error("unable to process filter", e);
		}
	}

}
