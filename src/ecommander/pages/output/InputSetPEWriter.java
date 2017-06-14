package ecommander.pages.output;

import ecommander.controllers.UserUrlMapper;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.InputSetPE;
import ecommander.pages.ItemInputs;
import ecommander.pages.LinkPE;
import ecommander.pages.PageElement;

/**
 * Выводит набор инпутов для айтема (элемент InputSetPE)
 * Created by E on 14/6/2017.
 */
public class InputSetPEWriter implements PageElementWriter {
	@Override
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception {
		InputSetPE inputPE = (InputSetPE) elementToWrite;
		ItemInputs inputs = inputPE.getAllInputs();
		if (inputs != null) {
			for (Object inputName : inputs.getInputs().getKeys()) {

			}
			xml.startElement(((LinkPE)elementToWrite).getLinkName());
			xml.addText(UserUrlMapper.getUserUrl(cmsLinkString));
			xml.endElement();
		}
	}
}
