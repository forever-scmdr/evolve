package ecommander.controllers.output;

import ecommander.pages.elements.PageElement;

public interface PageElementWriter {
	void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception;
}