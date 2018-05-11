package extra;

import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by E on 3/5/2018.
 */
public class MetaboIntegrateParsedCommand extends IntegrateBase {

	private final String CATALOG = "catalog";
	private final String SECTION = "section";
	private final String PRODUCT = "product";
	private final String PRODUCT_EXTRA = "product_extra";
	private final String PARAMS_XML = "params_xml";

	private final String NAME = "name";
	private final String ID = "id";
	private final String NAME_EXTRA = "name_extra";
	private final String DESCRIPTION = "description";
	private final String SHORT = "short";
	private final String TEXT = "text";
	private final String CODE = "code";
	private final String TYPE = "type";
	private final String EXTRA = "extra";
	private final String TECH = "tech";
	private final String PACKAGE = "package";
	private final String EXTRA_XML = "extra_xml";
	private final String SYMBOLS = "symbols";
	private final String SPIN = "spin";
	private final String VIDEO = "video";
	private final String GALLERY = "gallery";
	private final String PIC = "pic";
	private final String LINK = "link";
	private final String MAIN_PIC = "main_pic";
	private final String XML = "xml";



	private ParsedInfoProvider infoProvider;
	private ItemType sectionType;
	private ItemType productType;
	private ItemType productExtraType;
	private ItemType paramsXmlType;

	@Override
	protected boolean makePreparations() throws Exception {
		sectionType = ItemTypeRegistry.getItemType(SECTION);
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		productExtraType = ItemTypeRegistry.getItemType(PRODUCT_EXTRA);
		paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML);
		infoProvider = new ParsedInfoProvider();
		return infoProvider.isValid();
	}

	@Override
	protected void integrate() throws Exception {
		info.setToProcess(0);
		info.setProcessed(0);
		List<Item> catalogs = new ItemQuery(CATALOG).loadItems();
		if (catalogs.size() > 0) {
			for (Item catalog : catalogs) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(catalog));
			}
			executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(10, null));
		}
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		Document tree = infoProvider.getTree();
		processSubsections(tree.getElementsByTag("data").first(), catalog);
	}

	private void processSubsections(Element root, Item parent) throws Exception {
		Elements sectionEls = root.select("> section");
		for (Element sectionEl : sectionEls) {
			String[] path = StringUtils.split(sectionEl.attr(ID), '_');
			if (path.length <= 0)
				continue;
			String secName = path[path.length - 1];
			Item section = Item.newChildItem(sectionType, parent);
			section.setValue(NAME, secName);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
			processSubsections(sectionEl, section);
		}
		Elements productEls = root.select("> product");
		for (Element productElTree : productEls) {
			String code = productElTree.attr(ID);
			if (StringUtils.isBlank(code))
				continue;
			Document productDoc = null;
			try {
				productDoc = infoProvider.getItem(code);
			} catch (Exception e) {
				ServerLogger.error("Error parsing product xml file", e);
				info.addError("Документ для товара '" + code + "' содержит ошибки", code);
			}
			String name = productDoc.getElementsByTag(NAME).first().ownText();
			String type = productDoc.getElementsByTag(TYPE).first().ownText();
			String nameExtra = productDoc.getElementsByTag(NAME_EXTRA).first().ownText();
			String shortTxt = productDoc.getElementsByTag(SHORT).first().html();
			String description = productDoc.getElementsByTag(EXTRA).first().html();
			String text = productDoc.getElementsByTag(DESCRIPTION).first().html();
			String tech = productDoc.getElementsByTag(TECH).first().html();
			String packageTxt = productDoc.getElementsByTag(PACKAGE).first().html();
			String extraXml = productDoc.getElementsByTag(SYMBOLS).first().html();
			Elements spinEls = productDoc.getElementsByTag(SPIN);
			for (Element spinEl : spinEls) {
				extraXml += spinEl.outerHtml();
			}
			Elements videoEls = productDoc.getElementsByTag(VIDEO);
			for (Element videoEl : videoEls) {
				extraXml += videoEl.outerHtml();
			}
			ArrayList<Path> gallery = new ArrayList<>();
			Elements pics = productDoc.getElementsByTag(GALLERY).first().getElementsByTag(PIC);
			for (Element pic : pics) {
				Path file = infoProvider.getFile(code, pic.attr(LINK));
				if (file != null)
					gallery.add(file);
			}

			// Продукт

			Item product = Item.newChildItem(productType, parent);
			product.setValue(NAME, name);
			product.setValue(CODE, code);
			product.setValue(TYPE, type);
			product.setValue(NAME_EXTRA, nameExtra);
			product.setValue(SHORT, shortTxt);
			product.setValue(DESCRIPTION, description);
			product.setValue(TEXT, text);
			product.setValue(EXTRA_XML, extraXml);
			for (Path path : gallery) {
				product.setValue(GALLERY, path.toFile());
			}
			if (gallery.size() > 0) {
				Path firstPic = gallery.get(0);
				Path newMainPic = firstPic.resolveSibling("main_" + firstPic.getFileName());
 				ByteArrayOutputStream bos = ResizeImagesFactory.resize(firstPic.toFile(), 0, 400);
				Files.write(newMainPic, bos.toByteArray());
				product.setValue(MAIN_PIC, newMainPic.toFile());
			}

			executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());

			// Параметры XML

			XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
			if (StringUtils.isNotBlank(tech)) {
				Element techEl = productDoc.getElementsByTag(TECH).first().getElementById("attributes");
				Elements childrenDivs = techEl.children();
				for (Element div : childrenDivs) {
					String divClass = div.attr("class");
					if (StringUtils.equalsIgnoreCase(divClass, "attributesGroup")) {
						xml.endElement();
						xml.startElement("group").startElement("name").addText(div.ownText()).endElement();
					} else if (StringUtils.equalsIgnoreCase(divClass, "attributes")) {
						Elements rows = div.children();
						for (Element row : rows) {
							Elements nameValue = row.children();
							if (nameValue.size() > 0) {
								String paramName = nameValue.get(0).ownText();
								xml.startElement("parameter").startElement("name").addText(paramName).endElement();
								if (nameValue.size() > 1) {
									String paramValue = nameValue.get(1).ownText();
									xml.startElement("value").addText(paramValue).endElement();
								}
								xml.endElement(); // параметр закрывается
							}
						}
						xml.endElement(); // группа закрывается
					}
				}

				Item paramsXml = Item.newChildItem(paramsXmlType, product);
				paramsXml.setValue(XML, xml.toString());
				executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXml).noFulltextIndex().ignoreFileErrors());

				Item techItem = Item.newChildItem(productExtraType, product);
				techItem.setValue(NAME, "tech");
				techItem.setValue(TEXT, tech);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(techItem).noFulltextIndex().ignoreFileErrors());
			}

			// комплектность поставки

			if (StringUtils.isNotBlank(packageTxt)) {
				Item packageItem = Item.newChildItem(productExtraType, product);
				packageItem.setValue(NAME, "package");
				packageItem.setValue(TEXT, tech);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(packageItem).noFulltextIndex().ignoreFileErrors());
			}

			info.increaseProcessed();
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
