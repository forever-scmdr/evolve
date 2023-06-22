package ecommander.controllers.output;

import java.util.HashMap;

import ecommander.pages.elements.ItemFormPE;
import ecommander.pages.elements.ItemHttpPostForm;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.PageElementContainer;

/**
 * Выводит результат выполнения ItemFormMDWriter
 * Дополнительно выводит ссылки, которые содержатся в форме
 * 
 * @author EEEE
 *
 */
public class ItemFormPEWriter implements PageElementWriter {
	
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception {
		ItemHttpPostForm htmlForm = ((ItemFormPE) elementToWrite).getItemHtmlForm();
		String tag = ((ItemFormPE) elementToWrite).getTag();
		ItemFormMDWriter writer = new ItemFormMDWriter(htmlForm, tag);
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
