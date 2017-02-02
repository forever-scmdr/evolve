package ecommander.persistence.commandunits;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import ecommander.controllers.output.DomainMDWriter;

/**
 * Удаление домена по названию
 * @author EEEE
 */
public class DeleteDomainDBUnit extends DomainModelFilePersistenceCommandUnit {
	
	private String domainName;

	public DeleteDomainDBUnit(String domainName) {
		this.domainName = domainName;
	}
	
	@Override
	protected void executeInt() throws Exception {
		backup();
		Document doc = Jsoup.parse(getFileContents(), "", Parser.xmlParser());
		Element domainEl = doc.getElementsByAttributeValue(DomainMDWriter.NAME_ATTRIBUTE, domainName).first();
		domainEl.remove();
		doc.outputSettings().outline(true);
		doc.outputSettings().indentAmount(4);
		doc.outputSettings().prettyPrint(true);
		setFileContents(doc.outerHtml());
		saveFile();
	}

}