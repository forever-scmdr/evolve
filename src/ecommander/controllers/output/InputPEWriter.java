package ecommander.controllers.output;

import ecommander.pages.elements.InputPE;
import ecommander.pages.elements.PageElement;

public class InputPEWriter implements PageElementWriter {

	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) {
		xml.startElement(((InputPE)elementToWrite).getName());
		xml.addText(((InputPE)elementToWrite).getHtmlInputName());
		xml.endElement();
	}

}
