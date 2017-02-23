package ecommander.output;

import ecommander.pages.PageElement;

public interface PageElementWriter {
	void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception;
}