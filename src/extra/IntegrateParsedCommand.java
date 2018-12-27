package extra;

import ecommander.fwk.*;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.*;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
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
public class IntegrateParsedCommand extends IntegrateBase implements ItemNames, CatalogConst {


	private ParsedInfoProvider infoProvider;
	private ItemType sectionType;
	private ItemType productType;
	//private ItemType productExtraType;
	private ItemType manualType;
	private ItemType paramsXmlType;

	@Override
	protected boolean makePreparations() throws Exception {
		sectionType = ItemTypeRegistry.getItemType(SECTION);
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		//productExtraType = ItemTypeRegistry.getItemType(PRODUCT_EXTRA);
		//manualType = ItemTypeRegistry.getItemType(MANUAL);
		paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML);
		infoProvider = new ParsedInfoProvider();
		return infoProvider.isValid();
	}

	@Override
	protected void integrate() throws Exception {
		info.setToProcess(0);
		info.setProcessed(0);
		info.limitLog(300);
		List<Item> catalogs = new ItemQuery(CATALOG).loadItems();
		if (catalogs.size() > 0) {
			for (Item catalog : catalogs) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(catalog));
			}
			executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(10, null).noFulltextIndex());
		}
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		Document tree = infoProvider.getTree();
		Element root = tree.getElementsByTag("data").first();
		Element catalogElement = root.select("> section[id=0]").first();
		if (catalogElement != null)
			root = catalogElement;
		processSubsections(root, catalog);
	}

	private void processSubsections(Element root, Item parent) throws Exception {
		Elements sectionEls = root.select("> section");
		for (Element sectionEl : sectionEls) {
			String secCode = sectionEl.attr(ID_ATTR);
			String secName = infoProvider.getItem(secCode).getElementsByTag(NAME_ELEMENT).first().ownText();
			Item section = Item.newChildItem(sectionType, parent);
			section.setValue(CODE_PARAM, secCode);
			section.setValue(NAME_PARAM, secName);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
			processSubsections(sectionEl, section);
		}
		Elements productEls = root.select("> product");
		for (Element productElTree : productEls) {
			String code = null;
			String name = null;
			try {
				code = productElTree.attr(ID_ATTR);
				if (StringUtils.isBlank(code))
					continue;
				Document productDoc;
				try {
					productDoc = infoProvider.getItem(code);
				} catch (Exception e) {
					ServerLogger.error("Error parsing product xml file", e);
					info.addError("Документ для товара '" + code + "' содержит ошибки", code);
					continue;
				}
				name = productDoc.getElementsByTag(NAME).first().ownText();
				String type = productDoc.getElementsByTag(TYPE_PARAM).first().ownText();
				String nameExtra = productDoc.getElementsByTag(NAME_EXTRA_PARAM).first().ownText();
				String vendorCode = productDoc.getElementsByTag(VENDOR_CODE_PARAM).first().ownText();
				String vendor = productDoc.getElementsByTag(VENDOR_ELEMENT).first().html();
				//String description = productDoc.getElementsByTag(EXTRA_PARAM).first().html();
				String text = productDoc.getElementsByTag(TEXT_PARAM).first().html();
				String paramsXml = productDoc.getElementsByTag(PARAMS_XML_ELEMENT).first().html();
				//String packageTxt = productDoc.getElementsByTag(PACKAGE_PARAM).first().html();
				//String extraXml = productDoc.getElementsByTag(SYMBOLS_PARAM).first().html();
				/*
				Elements spinEls = productDoc.getElementsByTag(SPIN_PARAM);
				for (Element spinEl : spinEls) {
					extraXml += spinEl.outerHtml();
				}
				Elements videoEls = productDoc.getElementsByTag(VIDEO_PARAM);
				for (Element videoEl : videoEls) {
					extraXml += videoEl.outerHtml();
				}
				Elements manuals = productDoc.getElementsByTag(MANUAL_PARAM);
				if (manuals.size() > 0) {
					extraXml += manuals.first().outerHtml();
				}
				Elements spareParts = productDoc.getElementsByTag(PARTS_PARAM);
				if (spareParts.size() > 0) {
					extraXml += spareParts.first().outerHtml();
				}
				*/
				ArrayList<Path> gallery = new ArrayList<>();
				Elements pics = productDoc.getElementsByTag(GALLERY_PARAM).first().getElementsByTag(PIC_PARAM);
				for (Element pic : pics) {
					Path file = infoProvider.getFile(code, pic.attr(LINK_PARAM));
					if (file != null)
						gallery.add(file);
				}
				ArrayList<String> assocCodes = new ArrayList<>();
				Elements codeEls = productDoc.getElementsByTag(ASSOC_PARAM).first().getElementsByTag(ASSOC_CODE_ELEMENT);
				for (Element codeEl : codeEls) {
					assocCodes.add(codeEl.ownText());
				}
				// Продукт

				Product product = Product.get(Item.newChildItem(productType, parent));
				product.set_name(name);
				product.set_code(code);
				product.set_type(type);
				product.set_name_extra(nameExtra);
				product.set_vendor_code(vendorCode);
				product.set_vendor(vendor);
				//product.set_short(shortTxt);
				//product.set_description(description);
				product.set_text(text);
				//product.set_extra_xml(extraXml);
				for (String assocCode : assocCodes) {
					product.setValue(ASSOC_CODE_PARAM, assocCode);
				}
				for (Path path : gallery) {
					product.add_gallery(path.toFile());
				}
				if (gallery.size() > 0) {
					try {
						Path firstPic = gallery.get(0);
						Path newMainPic = firstPic.resolveSibling("main_" + firstPic.getFileName());
						ByteArrayOutputStream bos = ResizeImagesFactory.resize(firstPic.toFile(), 0, 400);
						Files.write(newMainPic, bos.toByteArray());
						product.set_main_pic(newMainPic.toFile());
					} catch (Exception e) {
						ServerLogger.error("Image resize error", e);
						info.addError("Image resize error", name + " " + code);
					}
				}

				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());

				// Параметры XML

				//XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
				if (StringUtils.isNotBlank(paramsXml)) {
				/*
				Element techEl = productDoc.getElementsByTag(TECH_PARAM).first().getElementById("attributes");
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
				*/

					Item paramsXmlItem = Item.newChildItem(paramsXmlType, product);
					paramsXmlItem.setValue(XML_PARAM, paramsXml);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXmlItem).noFulltextIndex().ignoreFileErrors());

				/*
				Item techItem = Item.newChildItem(productExtraType, product);
				techItem.setValue(NAME, "tech");
				techItem.setValue(TEXT_PARAM, tech);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(techItem).noFulltextIndex().ignoreFileErrors());
				*/
				}
				/*
				Elements manuals = productDoc.getElementsByTag(MANUALS_ELEMENT).first().getElementsByTag(MANUAL);
				for (Element manual : manuals) {
					Item manualItem = Item.newChildItem(manualType, product);
					manualItem.setValue(NAME, manual.getElementsByTag(NAME_ELEMENT).first().ownText());
					manualItem.setValue(LINK_PARAM, manual.getElementsByTag(FILE_ELEMENT).first().ownText());
					executeAndCommitCommandUnits(SaveItemDBUnit.get(manualItem).noFulltextIndex().ignoreFileErrors());
				}
				*/
				// комплектность поставки
/*
			if (StringUtils.isNotBlank(packageTxt)) {
				Item packageItem = Item.newChildItem(productExtraType, product);
				packageItem.setValue(NAME, "package");
				packageItem.setValue(TEXT_PARAM, packageTxt);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(packageItem).noFulltextIndex().ignoreFileErrors());
			}
*/
				info.increaseProcessed();
			} catch (Exception e) {
				ServerLogger.error("Product save error", e);
				info.addError("Product save error, ID = " + code + ", name = " + name, parent.getStringValue(NAME_PARAM));
			}
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
