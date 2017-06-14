package ecommander.persistence.commandunits;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import ecommander.pages.output.DomainMDWriter;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Domain;

/**
 * Команда для сохранения нового и изменения существующего домена
 */
public class SaveNewDomainDBUnit extends DomainModelFilePersistenceCommandUnit {

	private Domain domain;
	
	public SaveNewDomainDBUnit(Domain domain) {
		this.domain = domain;
	}	

	@Override
	protected void executeInt() throws Exception {
		backup();
		DomainMDWriter writer = new DomainMDWriter(domain);
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		writer.write(xml);
		Document doc = Jsoup.parse(getFileContents(), "", Parser.xmlParser());
		Elements root = doc.getElementsByTag(ROOT_ELEMENT);
		root.append(xml.toString());
		doc.outputSettings().outline(true);
		doc.outputSettings().indentAmount(4);
		doc.outputSettings().prettyPrint(true);
		setFileContents(doc.outerHtml());
		saveFile();
	}

}
