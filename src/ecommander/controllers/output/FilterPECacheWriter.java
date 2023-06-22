package ecommander.controllers.output;

import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.filter.FilterPE;
/**
 * Запись подгруженного фильтра (подгружены значения списковы выбора для полей ввода) в кеш
 * @author E
 *
 */
public class FilterPECacheWriter implements PageElementWriter {

	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception {
		FilterPE filter = (FilterPE) elementToWrite;
		if (filter.isCacheable()) {
			xml.addElements(filter.getCachedContents());
		}
	}

}
