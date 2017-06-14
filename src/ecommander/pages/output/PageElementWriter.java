package ecommander.pages.output;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.PageElement;

public interface PageElementWriter {
	void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception;
}