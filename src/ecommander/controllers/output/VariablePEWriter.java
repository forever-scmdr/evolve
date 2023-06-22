package ecommander.controllers.output;

import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.variables.VariablePE;

public class VariablePEWriter implements PageElementWriter {

	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) {
		// Вставить переменную в структуру XML
		VariablePE var = ((VariablePE)elementToWrite);
		for (String value : var.outputArray()) {
			xml.startElement(var.getName());
			xml.addText(value);
			xml.endElement();			
		}
	}

}
