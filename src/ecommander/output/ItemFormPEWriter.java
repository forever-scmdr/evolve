package ecommander.output;

import java.util.HashMap;

import ecommander.pages.*;
import ecommander.pages.SingleItemHttpPostFormDeprecated;

/**
 * Выводит результат выполнения ItemInputsMDWriter
 * Дополнительно выводит ссылки, которые содержатся в форме
 * 
 * @author EEEE
 * @deprecated
 *
 */
public class ItemFormPEWriter implements PageElementWriter {
	
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception {
		SingleItemHttpPostFormDeprecated htmlForm = ((ItemFormPE) elementToWrite).getItemHtmlForm();
		String tag = ((ItemFormPE) elementToWrite).getTag();
		ItemInputsMDWriter writer = new ItemInputsMDWriter(htmlForm, tag);
		if (((PageElementContainer)elementToWrite).hasNested()) {
			HashMap<String, String> hiddenFields = htmlForm.getHiddenFields();
			XmlDocumentBuilder linkXml = XmlDocumentBuilder.newDocPart();
			for (PageElement element : ((ItemFormPE) elementToWrite).getAllNested()) {
				LinkPE link = (LinkPE) element;
				for (String varName : hiddenFields.keySet()) {
					link.removeVariable(varName);
					link.addStaticVariable(varName, hiddenFields.get(varName));
				}
				PageElementWriterRegistry.getWriter(link).write(link, linkXml);
			}
			writer.addSubwriter(new LeafMDStringWriter(linkXml.toString()));
		}
		writer.write(xml);
	}

}
