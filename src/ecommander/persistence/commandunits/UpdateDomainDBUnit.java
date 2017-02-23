package ecommander.persistence.commandunits;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import ecommander.output.DomainMDWriter;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.Domain;

/**
 * Обновляет описание в базе данных
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 *         WARNING
 * После выполнения этой команды надо заново загружать модель айтемов и расширений айтемов        
 * !!!!!!!!!!!!!!!!!!!!!!!!!!
 * 
 * @author EEEE
 *
 */
public class UpdateDomainDBUnit extends DomainModelFilePersistenceCommandUnit {
	private Domain domain;
	private String oldName;
	
	public UpdateDomainDBUnit(Domain domain, String oldName) {
		this.domain = domain;
		if (!StringUtils.isBlank(oldName))
			this.oldName = oldName;
		else
			this.oldName = domain.getName();
	}
	
	public UpdateDomainDBUnit(Domain domain) {
		this(domain, null);
	}

	@Override
	protected void executeInt() throws Exception {
		backup();
		String tempName = "~" + oldName + "~";
		DomainMDWriter writer = new DomainMDWriter(domain);
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		writer.write(xml);
		Document doc = Jsoup.parse(getFileContents(), "", Parser.xmlParser());
		Element domainEl = doc.getElementsByAttributeValue(DomainMDWriter.NAME_ATTRIBUTE, oldName).first();
		domainEl.attr(DomainMDWriter.NAME_ATTRIBUTE, tempName);
		domainEl.after(xml.toString());
		domainEl.remove();
		doc.outputSettings().outline(true);
		doc.outputSettings().indentAmount(4);
		doc.outputSettings().prettyPrint(true);
		setFileContents(doc.outerHtml());
		saveFile();
	}
}