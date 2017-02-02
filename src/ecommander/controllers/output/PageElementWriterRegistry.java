package ecommander.controllers.output;

import java.util.HashMap;

import ecommander.pages.elements.ExecutableItemPE;
import ecommander.pages.elements.InputPE;
import ecommander.pages.elements.ItemFormPE;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.filter.FilterPE;
import ecommander.pages.elements.variables.FilterStaticVariablePE;
import ecommander.pages.elements.variables.StaticVariablePE;
import ecommander.pages.elements.variables.VariablePE;
/**
 * Хранит по одной копии райтера для каждого элемента для каждого потока
 * Flyweight
 * @author EEEE
 *
 */
public class PageElementWriterRegistry {

	private static PageElementWriterRegistry registry = null;
	
	protected static class EmptyElementWriter implements PageElementWriter {

		public void write(PageElement elementToWrite, XmlDocumentBuilder xml) {
			// Ничего не делать
		}
	}

	private HashMap<String, PageElementWriter> pageWriters;
	private HashMap<Class<? extends PageElement>, PageElementWriter> cacheWriters;
	private static EmptyElementWriter emptyWriter = new EmptyElementWriter();

	private static PageElementWriterRegistry getRegistry() {
		if (registry == null)
			registry = new PageElementWriterRegistry();
		return registry;
	}
	
	private PageElementWriterRegistry() {
		ExecutableItemPEWriter itemPEWriter = new ExecutableItemPEWriter();
		VariablePEWriter varWriter = new VariablePEWriter();
		
		pageWriters = new HashMap<String, PageElementWriter>();
		pageWriters.put(LinkPE.ELEMENT_NAME, new LinkPEWriter());
		pageWriters.put(InputPE.ELEMENT_NAME, new InputPEWriter());
		pageWriters.put(ItemFormPE.ELEMENT_NAME, new ItemFormPEWriter());
		pageWriters.put(StaticVariablePE.ELEMENT_NAME, varWriter);
		pageWriters.put(VariablePE.ELEMENT_NAME, varWriter);
		pageWriters.put(ExecutableItemPE.ELEMENT_NAME, itemPEWriter);
		pageWriters.put(FilterStaticVariablePE.ELEMENT_NAME, new FilterStaticVariablePEWriter());
		
		cacheWriters = new HashMap<Class<? extends PageElement>, PageElementWriter>();
		cacheWriters.put(ExecutableItemPE.class, itemPEWriter);
		cacheWriters.put(FilterPE.class, new FilterPECacheWriter());
	}
	
	public static PageElementWriter getWriter(PageElement element) {
		PageElementWriter writer = getRegistry().pageWriters.get(element.getElementName());
		if (writer == null)
			return emptyWriter;
		return writer;
	}
	
	public static PageElementWriter getCacheWriter(PageElement element) {
		PageElementWriter writer = getRegistry().cacheWriters.get(element.getClass());
		if (writer == null)
			return emptyWriter;
		return writer;
	}
}
