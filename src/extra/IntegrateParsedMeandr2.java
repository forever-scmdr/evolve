package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.*;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Custom_page;
import extra._generated.ItemNames;
import extra._generated.Product_extra_file;
import extra._generated.Product_extra_page;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by E on 3/5/2018.
 */
public class IntegrateParsedMeandr2 extends IntegrateBase implements ItemNames, CatalogConst {


	private ParsedInfoProvider infoProvider;
	private ItemType sectionType;
	private ItemType productType;
	//private ItemType productExtraType;
	private ItemType manualType;
	private ItemType paramsXmlType;
	private ItemType productExtraPageType;
	private ItemType productExtraFileType;

	@Override
	protected boolean makePreparations() throws Exception {
		sectionType = ItemTypeRegistry.getItemType(SECTION);
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		//productExtraType = ItemTypeRegistry.getItemType(PRODUCT_EXTRA);
		//manualType = ItemTypeRegistry.getItemType(MANUAL);
		paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML);
		productExtraPageType = ItemTypeRegistry.getItemType(PRODUCT_EXTRA_PAGE);
		productExtraFileType = ItemTypeRegistry.getItemType(PRODUCT_EXTRA_FILE);
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
		}
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		Document tree = infoProvider.getTree();
		Element root = tree.getElementsByTag("data").first();
		processSubsections(root, catalog);
	}

	private void processSubsections(Element root, Item parent) throws Exception {
		Elements sectionEls = root.select("> section");
		for (Element sectionEl : sectionEls) {
			String secCode = sectionEl.attr(ID_ATTR);
			String secName = infoProvider.getItem(secCode).getElementsByTag(NAME_ELEMENT).first().ownText();
			Item section = Item.newChildItem(sectionType, parent);
			//section.setValue(CODE_PARAM, secCode);
			section.setValue(NAME_PARAM, secName);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
			processSubsections(sectionEl, section);
		}
		Elements productEls = root.select("> product");
		for (Element productElTree : productEls) {
			String url = null;
			String name = null;
			String code = null;
			try {
				url = productElTree.attr(ID_ATTR);
				if (StringUtils.isBlank(url))
					continue;
				ParsedInfoProvider.InfoAccessor productDoc;
				try {
					productDoc = infoProvider.getAccessor(url);
				} catch (Exception e) {
					ServerLogger.error("Error parsing product xml file", e);
					info.addError("Документ для товара '" + url + "' содержит ошибки", url);
					continue;
				}
				String header = productDoc.getNodeText(HEADER);
				name = productDoc.getNodeText(NAME, header);
				code = productDoc.getNodeText(CODE_PARAM);
				String shortDesc = productDoc.getNodeHtml(SHORT_PARAM);
				String fullDesc = productDoc.getNodeHtml(DESCRIPTION_ELEMENT);
				String text = productDoc.getNodeHtml(TEXT_PARAM);
				String paramsXml = productDoc.getNodeHtml(TECH_PARAM);
				/*
				Elements videoEls = productDoc.getRoot().getElementsByTag(VIDEO_PARAM);
				String extraXml = "";
				for (Element videoEl : videoEls) {
					extraXml += videoEl.outerHtml();
				}*/
				Elements manuals = productDoc.getRoot().getElementsByTag(MANUAL_PARAM);
				ArrayList<Path> gallery = new ArrayList<>();
				ArrayList<String> assocCodes = new ArrayList<>();
				Elements pics = productDoc.getFirst(GALLERY_PARAM).getElementsByTag(PIC_PARAM);
				for (Element pic : pics) {
					Path file = infoProvider.getFile(url, pic.attr(LINK_PARAM));
					if (file != null)
						gallery.add(file);
				}
				Elements codeEls = productDoc.getFirst(ASSOC_PARAM).getElementsByTag(ASSOC_CODE_ELEMENT);
				for (Element codeEl : codeEls) {
					assocCodes.add(codeEl.ownText());
				}
				// Продукт

				Product product = Product.get(Item.newChildItem(productType, parent));
				product.set_name(name);
				product.set_code(code);
				product.set_name_extra(header);
				product.set_description(shortDesc);
				product.set_text(fullDesc);

				for (String assocCode : assocCodes) {
					ParsedInfoProvider.InfoAccessor assocAcc = infoProvider.getAccessor(assocCode);
					product.setValue(ASSOC_CODE_PARAM, assocAcc.getNodeText(CODE_PARAM));
				}
				for (Path path : gallery) {
					product.add_gallery(path.toFile());
				}
				product.set_main_pic(infoProvider.getFile(url, productDoc.getFirst(MAIN_PIC_PARAM).attr(LINK_PARAM)).toFile());
				/*
				if (gallery.size() > 0) {
					try {
						Path firstPic = gallery.get(0);
						Path newMainPic = firstPic.resolveSibling("main_" + firstPic.getFileName());
						ByteArrayOutputStream bos = ResizeImagesFactory.resize(firstPic.toFile(), 0, 400);
						if (bos != null) {
							Files.write(newMainPic, bos.toByteArray());
							product.set_main_pic(newMainPic.toFile());
						}
					} catch (Exception e) {
						ServerLogger.error("Image resize error", e);
						info.addError("Image resize error", name + " " + url);
					}
				}
				*/
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
				}

				// страница с описанием
				Item extraPage = Item.newChildItem(productExtraPageType, product);
				extraPage.setValueUI(TEXT_PARAM, text);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(extraPage).noFulltextIndex());
				setTextWithPics(extraPage, url, productDoc, Custom_page.TEXT, Product_extra_page.TEXT_PICS, TEXT_PARAM);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(extraPage).noFulltextIndex());

				// файлы мануалов
				for (Element manual : manuals) {
					Product_extra_file file = Product_extra_file.get(Item.newChildItem(productExtraFileType, product));
					file.set_name(StringUtils.normalizeSpace(manual.getElementsByTag("file").first().ownText()));
					file.set_desc(StringUtils.normalizeSpace(manual.getElementsByTag("title").first().ownText()));
					file.set_size(StringUtils.normalizeSpace(manual.getElementsByTag("size").first().ownText()));
					String fileName = StringUtils.normalizeSpace(manual.getElementsByTag("file").first().ownText());
					if (StringUtils.isNotBlank(fileName)) {
						file.set_file(infoProvider.getFile(url, fileName).toFile());
						executeAndCommitCommandUnits(SaveItemDBUnit.get(file).noFulltextIndex().ignoreFileErrors());
					}
				}

				info.increaseProcessed();
			} catch (Exception e) {
				ServerLogger.error("Product save error", e);
				info.addError("Product save error, ID = " + url + ", name = " + name, parent.getStringValue(NAME_PARAM));
			}
		}
	}

	protected void updatePics(Item item, String textParamName, String picParamName) {
		HashSet<String> picNames = new HashSet<>();
		picNames.addAll(item.outputValues(picParamName));
		Document doc = Jsoup.parse(item.getStringValue(textParamName));
		// Элементы img src
		updateDocTagAttributeWithPic(doc, "img", "src", item, picNames);
		// Элементы a href
		updateDocTagAttributeWithPic(doc, "a", "href", item, picNames);
		item.setValue(textParamName, JsoupUtils.outputHtmlDoc(doc));
	}

	protected void updateDocTagAttributeWithPic(Element parentEl, String tag, String attr, Item item, HashSet<String> pics) {
		for (Element el : parentEl.getElementsByTag(tag)) {
			String attrOldFileName = el.attr(attr);
			String correctFileName = Strings.getFileName(attrOldFileName);
			if (pics.contains(correctFileName)) {
				String newAttrFileName = AppContext.getFilesUrlPath(item.isFileProtected()) + item.getRelativeFilesPath() + correctFileName;
				el.attr(attr, newAttrFileName);
			}
		}
	}

	protected void setTextWithPics(Item item, String code, ParsedInfoProvider.InfoAccessor accessor, String textParamName,
	                               String picParamName, String textTag, String picsGalleryTag, String picTag) {
		String text = accessor.getNodeHtml(textTag);
		if ((StringUtils.isNotBlank(text))) {
			Elements textPics = accessor.getChildrenOfFirst(picsGalleryTag, picTag);
			for (Element textPic : textPics) {
				Path path = infoProvider.getFile(code, textPic.attr(LINK_PARAM));
				if (path != null)
					item.setValue(picParamName, path.toFile());
			}
			item.setValue(textParamName, text);
			updatePics(item, textParamName, picParamName);
		}
	}

	protected void setTextWithPics(Item item, String code, ParsedInfoProvider.InfoAccessor accessor, String textParamName, String picParamName, String textTag) {
		setTextWithPics(item, code, accessor, textParamName, picParamName, textTag, textTag + "_pics", "pic");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
