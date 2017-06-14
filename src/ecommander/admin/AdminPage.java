package ecommander.admin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import ecommander.fwk.EcommanderException;
import ecommander.controllers.AppContext;
import ecommander.controllers.XmlXslOutputController;
import ecommander.fwk.ServerLogger;
import ecommander.pages.output.LeafMDWriter;
import ecommander.pages.output.MetaDataWriter;
import ecommander.fwk.XmlDocumentBuilder;
/**
 * Страница админа (XML текст)
 * 
 * @author EEEE
 *
 */
public class AdminPage {
	
	private XmlDocumentBuilder pageText;
	private boolean isNotPrepared;
	private ArrayList<MetaDataWriter> writers;
	private String name;
	private String userName;

	public AdminPage(String name, String siteDomain, String userName) {
		writers = new ArrayList<>();
		writers.add(new LeafMDWriter("domain", siteDomain));
		this.name = name;
		this.userName = userName;
		isNotPrepared = true;
	}
	
	public AdminPage(String name) {
		this(name, "", "");
	}
	
	private void prepare() throws SQLException {
		if (isNotPrepared) {
			pageText = XmlDocumentBuilder.newDoc();
			pageText.startElement("admin-page", "name", name, "username", userName);
			for (MetaDataWriter writer : writers) {
				writer.write(pageText);
			}
			pageText.endElement();
			isNotPrepared = false;
		}
	}
	
	public final void output(HttpServletResponse response) throws TransformerException, IOException, SQLException, EcommanderException {
		prepare();
		String xslFileName = AppContext.getRealPath("admin/xsl/" + name + ".xsl");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ServerLogger.debug(pageText);
		XmlXslOutputController.outputXmlTransformed(bos, pageText, xslFileName);
		response.setContentType("text/html");
		bos.writeTo(response.getOutputStream());
	}
	
	public void addElement(MetaDataWriter element) {
		writers.add(element);
		isNotPrepared = true;
	}
	
	public void addMessage(String message, boolean isError) {
		writers.add(new LeafMDWriter("message", message, "error", isError));
		isNotPrepared = true;
	}
	
	public String getName() {
		return name;
	}
}
