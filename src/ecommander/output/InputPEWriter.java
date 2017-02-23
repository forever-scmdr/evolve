package ecommander.output;

import ecommander.pages.InputPE;
import ecommander.pages.PageElement;

public class InputPEWriter implements PageElementWriter {

	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) {
		xml.startElement(((InputPE)elementToWrite).getName());
		xml.addText(((InputPE)elementToWrite).getHtmlInputName());
		xml.endElement();
	}

}
