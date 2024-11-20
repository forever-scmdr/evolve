package ecommander.extra;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.events.StartElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.IntegrateBase;
import ecommander.controllers.AppContext;
import ecommander.controllers.output.XmlDocumentBuilder;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.COMPARE_TYPE;
import ecommander.model.item.Item;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;

public class CatalogXmlExportCommand extends IntegrateBase {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	private static final String SECTIONS_ELEMENT = "sections";
	private static final String SECTION_ELEMENT = "section";
	private static final String NAME_ELEMENT = "name";
	private static final String PICTURE_ELEMENT = "img";
	private static final String TEXT_ELEMENT = "text";
	private static final String TAG_ELEMENT = "tag";
	private static final String DOC_ELEMENT = "doc";
	private static final String USE_EL = "use";
	private XmlDocumentBuilder doc;
	private int processed =0;

	@Override
	protected boolean makePreparations() {
		return true;
	}

	/**
	 *
	 */
	@Override
	protected void integrate() throws Exception {
		setOperation("Создание документа");

		doc = XmlDocumentBuilder.newDoc();
		doc.startElement("catalog", "date", FORMATTER.format(LocalDateTime.now()));

		setOperation("Создание списка разделов");
		addSections();
		setOperation("Создание списка товаров");
		addProducts();
		doc.endElement();
		Path destination = Paths.get(AppContext.getFilesDirPath(), "export.xml");
		Files.deleteIfExists(destination);
		FileUtils.writeStringToFile(destination.toFile(), doc.toString(), "UTF-8");
	}

	private void addProducts() throws Exception {
		doc.startElement("products");
		List<Item> products = ItemQuery.newItemQuery(ItemNames.PRODUCT).loadItems();
		processed=0;
		for (Item product : products) {
			processProduct(product);
		}
		doc.endElement();
	}

	private void processProduct(Item product) throws Exception {
		doc.startElement(ItemNames.PRODUCT, "id", product.getId());
		doc.startElement("parent").addText(product.getDirectParentId()).endElement();
		doc.startElement(NAME_ELEMENT).addText(product.getStringValue(ItemNames.product.NAME)).endElement();
		doc.startElement(PICTURE_ELEMENT)
				.addText(Paths.get(product.getPredecessorsAndSelfPath(), product.getStringValue(ItemNames.product.IMG_BIG, "")).toString().replaceAll("\\\\", "/")).endElement();
		doc.startElement(TEXT_ELEMENT).addCData(product.getStringValue(ItemNames.product.TEXT,"")).endElement();
		doc.startElement(TEXT_ELEMENT, "name","Модификации").addCData(product.getStringValue(ItemNames.product.SIZE,"")).endElement();
		for(String tag : product.getStringValues(ItemNames.product.TAG)) {
			doc.startElement(TAG_ELEMENT).addText(tag).endElement();
		}
		for(long id : product.getLongValues(ItemNames.product.DOC_ASSOC)) {
			Item assoc = ItemQuery.loadById(id);
			if(assoc != null) {
				docToXml(assoc);
			}
		}
		
		ItemQuery q = ItemQuery.newItemQuery(ItemNames.MAIN_SECTION);
		q.addSuccessors("=", Arrays.asList(product.getId()), COMPARE_TYPE.SOME);
		List<Item> secs = q.loadItems();
		
		for(Item sec : secs) {
			q = ItemQuery.newItemQuery(ItemNames.DOC);
			q.addParameterCriteria("scope", sec.getStringValue("name"), "like", "%v%", COMPARE_TYPE.SOME);
			for(Item assoc : q.loadItems()) {
				docToXml(assoc);
			}
		}
		
		doc.endElement();
		processed++;
	}
	
	private void docToXml(Item assoc) {
		String path = assoc.getPredecessorsAndSelfPath();
		String face = "file_doc".equals(assoc.getTypeName())? assoc.getStringValue("file") : assoc.getStringValue("pic","");
		String back = "file_doc".equals(assoc.getTypeName())? "" : assoc.getStringValue("back","");
		face = StringUtils.isNotBlank(face)? Paths.get(path, face).toString().replaceAll("\\\\", "/") : "";
		back = StringUtils.isNotBlank(back)? Paths.get(path, back).toString().replaceAll("\\\\", "/") : "";
		
		if(StringUtils.isNotBlank(face)) {
			doc.addEmptyElement(DOC_ELEMENT, "name", assoc.getStringValue(ItemNames.doc.NAME,""), "face", face, "back", back);
		}
	}

	private void addSections() throws Exception {
		doc.startElement(SECTIONS_ELEMENT);
		Item catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		List<Item> sections = ItemQuery.loadByParentId(catalog.getId());
		processed=0;
		for (Item section : sections) {
			processSection(section);
		}
		doc.endElement();
	}

	private void processSection(Item section, long... parentId) throws Exception {
		if (section.getTypeName().equalsIgnoreCase(ItemNames.SECTION) || section.getTypeName().equalsIgnoreCase(ItemNames.MAIN_SECTION)) {
			List<Item> kids = ItemQuery.loadByParentId(section.getId());
			long parent = parentId.length == 0 ? -1 : parentId[0];
			
			Iterator<Item> iterator = kids.iterator();
			while(iterator.hasNext()) {
				Item next = iterator.next();
				if(next.getTypeName().equals(ItemNames.SECTION) || next.getTypeName().equals(ItemNames.MAIN_SECTION) || next.getTypeName().equals(ItemNames.PRODUCT)) {
					continue;
				}
				iterator.remove();
			}			
			if (!kids.isEmpty()) {
				if (parent > 0) {
					doc.startElement(SECTION_ELEMENT, "id", section.getId(), "parent_id", parent);
				} else {
					doc.startElement(SECTION_ELEMENT, "id", section.getId());
				}
				doc.startElement(NAME_ELEMENT).addText(section.getStringValue(ItemNames.section.NAME)).endElement();

				String sample = "<p class=\"number-sample\">" + section.getStringValue(ItemNames.section.SERIAL_SAMPLE, "") + "</p>\n";
				String text = sample + section.getStringValue(ItemNames.section.TEXT, "");
				doc.startElement(TEXT_ELEMENT).addCData(text).endElement();				
				doc.startElement(USE_EL).addCData(section.getStringValue(USE_EL,"")).endElement();
				doc.endElement();	
				setProcessed(++processed);
				
				for (Item sub : kids) {
					processSection(sub, parent);
				}
			}
		}

	}

}
