package ecommander.controllers.parsing;

import java.io.File;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ecommander.controllers.AppContext;
import ecommander.view.domain.Domain;
import ecommander.view.domain.DomainRegistry;

/**
 * 
<domains>
	<domain name="Регион" format="" view-type="combobox"> // Домен (name - название домена, format - формат ввода-вывода зхначений 
																   // домена (пока не используется), view-type - тип поля ввода для домена
		<value>Минская область</value>
		<value>Гродненская область</value>
	</domain>
	<domain name="сумма" view-type="radiogroup">
		<value>10 000</value>
		<value>250 000</value>
	</domain>
</domains>
	
Типы полей ввода:
	checkbox - страничная переменная
	combobox - поля ввода, которые относятся к определенному айтему
	radiogroup - форма ввода для заполнения парамтеров айтема
 * 
 * @author EEEE
 *
 */
public class DomainBuilder {
	public static final String DOMAIN_ELEMENT = "domain";
	public static final String VALUE_ELEMENT = "value";
	
	public static final String NAME_ATTRIBUTE = "name";
	public static final String FORMAT_ATTRIBUTE = "format";
	public static final String VIEW_TYPE_ATTRIBUTE = "view-type";

	public static final long MODIFIED_TEST_INTERVAL = 10000; // время, через которое проводится проверка обновления domains.xsl
	private static long fileLastChecked = 0;
	private static long fileLastModified = 0;
	
	private static final Object SEMAPHORE = new Object();
	
	/**
	 * Проверяет актуальность файла и выполняет еро разбор при необходимости
	 * @throws Exception
	 */
	public static void testActuality() throws Exception {
		if (System.currentTimeMillis() - fileLastChecked > MODIFIED_TEST_INTERVAL) {
			synchronized (SEMAPHORE) {
				if (System.currentTimeMillis() - fileLastChecked > MODIFIED_TEST_INTERVAL) {
					File domainsFile = new File(AppContext.getDomainsModelPath());
					if (!domainsFile.exists()) {
						fileLastChecked = Long.MAX_VALUE;
						return;
					}
					if (domainsFile.lastModified() > fileLastModified) {
						reloadDomains();
						fileLastModified = domainsFile.lastModified();
					}
					fileLastChecked = System.currentTimeMillis();
				}
			}
		}
	}
	/**
	 * Считывает домен
	 * @param domainNode
	 * @return
	 */
	private static Domain readDomain(Element domainNode) {
		String name = domainNode.getAttribute(NAME_ATTRIBUTE);
		String format = domainNode.getAttribute(FORMAT_ATTRIBUTE);
		String viewType = domainNode.getAttribute(VIEW_TYPE_ATTRIBUTE);
		Domain domain = new Domain(name, viewType, format);
		for (Node domainSubnode = domainNode.getFirstChild(); domainSubnode != null; domainSubnode = domainSubnode.getNextSibling()) {
			if (domainSubnode.getNodeType() == Node.ELEMENT_NODE && domainSubnode.getNodeName().equalsIgnoreCase(VALUE_ELEMENT)) {
				Element valueNode = (Element) domainSubnode;
				domain.addValue(valueNode.getTextContent().trim());
			}
		}
		return domain;
	}

	private static void reloadDomains() throws Exception {
		File domainsFile = new File(AppContext.getDomainsModelPath());
		if (!domainsFile.exists())
			return;
		// Парсить документ
		DOMParser parser = new DOMParser();
		parser.parse(AppContext.getDomainsModelPath());
		final Document document = parser.getDocument();

		NodeList domains = document.getElementsByTagName(DOMAIN_ELEMENT);
		for (int i = 0; i < domains.getLength(); i++) {
			Element domainNode = (Element)domains.item(i);
			Domain domain = readDomain(domainNode);
			DomainRegistry.addDomain(domain);
		}
		
	}

}
