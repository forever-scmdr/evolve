package ecommander.output;

import ecommander.pages.PageElement;
import ecommander.pages.var.VariablePE;

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
