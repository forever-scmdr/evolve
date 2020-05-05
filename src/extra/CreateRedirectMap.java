package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.itemquery.ItemQuery;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateRedirectMap extends IntegrateBase implements CatalogConst {

	private XmlDocumentBuilder rdrList;
	private int lineCounter = 0;
	private static final Byte[] GENERAL_ASSOC = new Byte[]{ItemTypeRegistry.getPrimaryAssocId()};
	private FileWriter fileWriter;
	private boolean appendMode = false;

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Создание карты редиректов");
		Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		ArrayList<Item> sections = ItemQuery.loadByParentId(catalog.getId(), GENERAL_ASSOC);

		Path redirects = Paths.get(AppContext.getContextPath(), "redirect.txt");
		Files.deleteIfExists(redirects);
		try (FileWriter writer = new FileWriter(redirects.toString(), true)) {
			fileWriter = writer;
			for (Item sec : sections) {
				processSection(sec, "/");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void processSection(Item sec, String s) throws Exception {
		info.setCurrentJob(sec.getStringValue(NAME_PARAM));
		ItemQuery q = new ItemQuery(SECTION_ITEM);
		q.setParentId(sec.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());

		List<Item> sections = q.loadItems();
		String newUrl = s + sec.getKeyUnique() + '/';
		String oldUrl = (sections.size() > 0) ? "/sections/" + sec.getKeyUnique() : "/section/" + sec.getKeyUnique();

		appendRule(oldUrl, newUrl);

		for (Item section : sections) {
			processSection(section, newUrl);
		}
		processProducts(sec, newUrl, 0);
	}

	private void appendRule(String oldUrl, String newUrl) throws IOException {
		rdrList = XmlDocumentBuilder.newDocPart();
		rdrList.startElement("rule")
				.startElement("from", "casesensitive", "false").addText(oldUrl).endElement()
				.startElement("to", "type", "permanent-redirect", "qsappend", "false", "last", "true").addText(newUrl).endElement()
				.endElement();
		if (appendMode) {
			fileWriter.write(System.lineSeparator() + rdrList.toString());
		}else{
			fileWriter.write(rdrList.toString());
			appendMode = true;
		}
		lineCounter += 4;
		setLineNumber(lineCounter);
	}

	private void processProducts(Item sec, String s, long id) throws Exception {
		ItemQuery q = new ItemQuery(PRODUCT_ITEM);
		q.setParentId(sec.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
		q.setIdSequential(id);
		q.setLimit(1000);
		List<Item> products = q.loadItems();
		long pid = 0;
		for (Item product : products) {
			String oldUrl = "/product/" + product.getKeyUnique();
			String newUrl = s + product.getKeyUnique();
			appendRule(oldUrl, newUrl);
			pid = product.getId();
			info.increaseProcessed();
		}
		if (products.size() == 1000) {
			processProducts(sec, s, pid);
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
