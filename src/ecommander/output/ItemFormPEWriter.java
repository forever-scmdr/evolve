package ecommander.output;

import java.util.HashMap;

import ecommander.pages.ItemFormPE;
import ecommander.pages.ItemHttpPostForm;
import ecommander.pages.LinkPE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;

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
