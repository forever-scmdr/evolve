package ecommander.output;

import org.apache.commons.lang3.StringUtils;

import ecommander.pages.ExecutableItemPE;
import ecommander.pages.LinkPE;
import ecommander.pages.PageElement;
import ecommander.pages.filter.FilterPE;
import ecommander.pages.variables.StaticVariablePE;
import ecommander.pages.variables.VariablePE;
/**
 * Метод write работает не на базе фильтра, а на базе страничного айтема
 * @author EEEE
 *
 */
public class FilterPEWriter implements PageElementWriter {

	private static final String PAGES_ELEMENT_SUFFIX = "_pages";
	private static final String PAGE_ELEMENT = "page";
	private static final String CURRENT_PAGE_ELEMENT = "current_page";
	private static final String PAGE_NUMBER_ELEMENT = "number";
	private static final String PAGE_LINK_ELEMENT = "link";
	private static final String PAGE_NEXT_ELEMENT = "next";
	private static final String PAGE_PREVIOUS_ELEMENT = "previous";

	
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception {
		ExecutableItemPE item = (ExecutableItemPE)elementToWrite;
		if (!item.hasFilter()) return;
		FilterPE filter = item.getFilter();
		if (filter.hasUserFilter()) {
			xml.startElement(filter.getUserFilterParamName());
			xml.addElements(item.getFilter().getCachedContents());
			xml.endElement();
		}
		if (filter.hasPage()) {
			int totalItems = item.getParentRelatedFoundItemIterator().getTotalQuantity();
			if (totalItems > 1) {
				// Номера страниц
				int totalPages = (int) Math.ceil(((double) totalItems) / filter.getLimit());
				
				// Выводить только если страниц больше 1
				if (totalPages > 1) {
					// <~item~_pages>
					String tagName = StringUtils.isBlank(item.getTag()) ? item.getItemName() : item.getTag();
					xml.startElement(tagName + PAGES_ELEMENT_SUFFIX);
					
					LinkPE linkBase = item.getPageModel().getRequestLink();
					VariablePE filterPageVar = filter.getPageVariable();
					// Найти переменную, которая обозначает номер страницы
					VariablePE pageVarBase = linkBase.getVariable(filterPageVar.getName());
					// Если страница не указана, то считать что она первая.
					if (pageVarBase == null) {
						pageVarBase = new StaticVariablePE(filter.getPageVariable().getName(), "1");
						pageVarBase.setStyle(filterPageVar.getStyle());
						filter.addPage(pageVarBase);
					}
					int currentPage = filter.getPage();
					for (int i = 1; i <= totalPages; i++) {
						// Создать ссылку (клонированием) и параметр ссылки для номера страницы
						LinkPE link = (LinkPE)linkBase.createExecutableClone(null, null);
						VariablePE pageNumberVar = new StaticVariablePE(pageVarBase.getName(), new Integer(i).toString());
						pageNumberVar.setStyle(filterPageVar.getStyle());
						link.addVariable(pageNumberVar);
						// Номер страницы
						String pageNumber = new Integer(i).toString();
						// Элемент типа <page> или <current_page>
						if (i == currentPage) {
							xml.startElement(CURRENT_PAGE_ELEMENT);
						} else {
							xml.startElement(PAGE_ELEMENT);
						}
						// Элемент <number>
						xml.startElement(PAGE_NUMBER_ELEMENT).addText(pageNumber).endElement();
						// Элемент <link>
						xml.startElement(PAGE_LINK_ELEMENT).addText(link.serialize()).endElement();
						// </page> или </current_page>
						xml.endElement();
					}
					// Назад
					if (currentPage > 1) {
						LinkPE link = (LinkPE)linkBase.createExecutableClone(null, null);
						VariablePE nextNumberVar = new StaticVariablePE(pageVarBase.getName(), new Integer(filter.getPage() - 1).toString());
						nextNumberVar.setStyle(filterPageVar.getStyle());
						link.addVariable(nextNumberVar);
						xml
							.startElement(PAGE_PREVIOUS_ELEMENT)
							.startElement(PAGE_LINK_ELEMENT)
							.addText(link.serialize())
							.endElement()
							.endElement();
					}
					// Вперед
					if (currentPage < totalPages) {
						LinkPE link = (LinkPE)linkBase.createExecutableClone(null, null);
						VariablePE prevNumberVar = new StaticVariablePE(pageVarBase.getName(), new Integer(filter.getPage() + 1).toString());
						prevNumberVar.setStyle(filterPageVar.getStyle());
						link.addVariable(prevNumberVar);
						xml
							.startElement(PAGE_NEXT_ELEMENT)
							.startElement(PAGE_LINK_ELEMENT)
							.addText(link.serialize())
							.endElement()
							.endElement();
					}
					// </~item~_pages>
					xml.endElement();
				}
			}
		}
		for (PageElement element : filter.getAllNested()) {
			PageElementWriterRegistry.getWriter(element).write(element, xml);
		}
	}

}
