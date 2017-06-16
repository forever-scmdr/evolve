package ecommander.pages.output;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.ExecutableItemPE;
import ecommander.pages.InputSetPE;
import ecommander.pages.LinkPE;
import ecommander.pages.PageElement;
import ecommander.pages.filter.FilterPE;

import java.util.HashMap;
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
		pageWriters = new HashMap<>();
		pageWriters.put(LinkPE.ELEMENT_NAME, new LinkPEWriter());
		pageWriters.put(InputSetPE.ELEMENT_NAME, new InputSetPEWriter());
		pageWriters.put(ExecutableItemPE.ELEMENT_NAME, itemPEWriter);

		cacheWriters = new HashMap<>();
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
