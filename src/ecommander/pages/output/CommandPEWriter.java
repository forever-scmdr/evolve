package ecommander.pages.output;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.CommandPE;
import ecommander.pages.PageElement;
import ecommander.pages.ResultPE;

/**
 * Created by E on 15/11/2018.
 */
public class CommandPEWriter implements PageElementWriter {
	@Override
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception {
		CommandPE command = (CommandPE) elementToWrite;
		if (command.hasExecutionResult()) {
			ResultPE result = command.getExecutionResult();
			ResultPE.ResultType t = result.getType();
			if (t == ResultPE.ResultType.plain_text || t == ResultPE.ResultType.inline_text) {
				xml
						.startElement(command.getTag())
						.startElement(command.getExecutionResult().getName())
						.addText(result.getValue())
						.endElement()
						.endElement();
			}else if(t == ResultPE.ResultType.xml || t == ResultPE.ResultType.inline_xml){
				xml
						.startElement(command.getTag())
						.startElement(command.getExecutionResult().getName())
						.addElements(result.getValue())
						.endElement()
						.endElement();
			}
		}
	}
}