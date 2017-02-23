package ecommander.output;

import ecommander.controllers.UserUrlMapper;
import ecommander.pages.LinkPE;
import ecommander.pages.PageElement;
/**
 * 
 * @author EEEE
 * TODO <usability> сделать возможность выводить урл без c.eco, либо придумать как использовать его в ссылках 
 */
public class LinkPEWriter implements PageElementWriter {
	
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) {
		String cmsLinkString = ((LinkPE) elementToWrite).serialize();
		// Вставить ссылку в структуру XML
//		xml.addEmptyElement(((LinkPE)elementToWrite).getLinkName(), "url", UserUrlMapper.getUserUrl(cmsLinkString));
		
		
		xml.startElement(((LinkPE)elementToWrite).getLinkName());
		xml.addText(UserUrlMapper.getUserUrl(cmsLinkString));
		xml.endElement();
	}
}
