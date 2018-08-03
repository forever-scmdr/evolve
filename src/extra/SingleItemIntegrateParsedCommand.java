package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.*;
import ecommander.model.datatypes.FileDataType;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by E on 3/5/2018.
 */
public class SingleItemIntegrateParsedCommand extends IntegrateBase implements ItemNames, CatalogConst {


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
		return true;
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
		Item parseCatalog = ItemQuery.loadSingleItemByName(PARSE_CATALOG);
		processSubsections(parseCatalog, catalog);
	}

	private void processSubsections(Item parseParent, Item catalogParent) throws Exception {

		List<Item> parseSections = new ItemQuery(PARSE_SECTION).setParentId(parseParent.getId(), false).loadItems();
		for (Item parseSection : parseSections) {
			Item section = Item.newChildItem(sectionType, catalogParent);
			section.setValue(section_.NAME, parseSection.getValue(NAME));
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
			processSubsections(parseSection, section);
		}
		List<Item> parseItems = new ItemQuery(PARSE_ITEM).setParentId(parseParent.getId(), false).loadItems();
		for (Item parseItem : parseItems) {
			Document productDoc = null;
			String url = parseItem.getStringValue(parse_item_.URL);
			try {
				productDoc = Jsoup.parse(parseItem.getStringValue(parse_item_.XML), "localhost", Parser.xmlParser());
			} catch (Exception e) {
				ServerLogger.error("Error parsing product xml file", e);
				info.addError("Документ для товара '" + url + "' содержит ошибки", url);
			}
			Element prodEl = productDoc.getElementsByTag(PRODUCT).first();
			if (prodEl == null) {
				info.addError("Документ для товара '" + url + "' содержит ошибки", url);
			}
			String code = prodEl.attr(ID_ATTR);
			String name = prodEl.getElementsByTag(NAME).first().ownText();
			String type = prodEl.getElementsByTag(TYPE_PARAM).first().ownText();
			String nameExtra = prodEl.getElementsByTag(NAME_EXTRA_PARAM).first().ownText();
			String shortTxt = prodEl.getElementsByTag(SHORT_PARAM).first().html();
			//String description = prodEl.getElementsByTag(EXTRA_PARAM).first().html();
			String tech = prodEl.getElementsByTag(TECH_PARAM).first().html();
			String paramsXmlString = prodEl.getElementsByTag(PARAMS_XML).first().html();
			Elements manuals = prodEl.getElementsByTag(MANUAL_PARAM);
			String extraXml = "";
			if (manuals.size() > 0) {
				extraXml += manuals.first().outerHtml();
			}
			ArrayList<Path> gallery = new ArrayList<>();
			Elements pics = prodEl.getElementsByTag(GALLERY_PARAM).first().getElementsByTag(PIC_PARAM);
			for (Element pic : pics) {
				Path file = FileDataType.getItemFile(parseItem, Strings.getFileName(pic.attr(LINK_PARAM)));
				if (file != null && file.toFile().exists())
					gallery.add(file);
			}
//			ArrayList<String> assocCodes = new ArrayList<>();
//			Elements codeEls = prodEl.getElementsByTag(ASSOC_PARAM).first().getElementsByTag(CODE_PARAM);
//			for (Element codeEl : codeEls) {
//				assocCodes.add(codeEl.ownText());
//			}

			// Продукт

			Product product = Product.get(Item.newChildItem(productType, catalogParent));
			product.set_name(name);
			product.set_code(code);
			product.set_type(type);
			product.set_name_extra(nameExtra);
			product.set_short(shortTxt);
			//product.set_description(description);
			product.set_extra_xml(extraXml);
			/*
			for (String assocCode : assocCodes) {
				product.setValue(ASSOC_CODE, assocCode);
			}
			*/
			for (Path path : gallery) {
				product.add_gallery(path.toFile());
			}
			if (gallery.size() > 0) {
				Path firstPic = gallery.get(0);
				Path newMainPic = firstPic.resolveSibling("main_" + firstPic.getFileName());
 				ByteArrayOutputStream bos = ResizeImagesFactory.resize(firstPic.toFile(), 0, 400);
				Files.write(newMainPic, bos.toByteArray());
				product.set_main_pic(newMainPic.toFile());
			}

			executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());

			// Параметры XML

			if (StringUtils.isNotBlank(tech)) {
				Item paramsXml = Item.newChildItem(paramsXmlType, product);
				paramsXml.setValue(XML_PARAM, paramsXmlString);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXml).noFulltextIndex().ignoreFileErrors());

				Item techItem = Item.newChildItem(productExtraType, product);
				techItem.setValue(NAME, "tech");
				techItem.setValue(TEXT_PARAM, tech);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(techItem).noFulltextIndex().ignoreFileErrors());
			}

			// Другие дополнительные сабайтемы продукта

			Element elementsEl = prodEl.getElementsByTag("elements").first();
			Element extraPartsEl = prodEl.getElementsByTag("extra_parts").first();
			if (elementsEl != null) {
				Item elementsItem = Item.newChildItem(productExtraType, product);
				elementsItem.setValue(NAME, "elements");
				// первое сохранение для того, чтобы появился ID
				executeCommandUnit(SaveItemDBUnit.get(elementsItem).noFulltextIndex().ignoreFileErrors());
				fixHtmlImageLinks(parseItem, elementsItem, elementsEl, product_extra_.TEXT_PICS, product_extra_.TEXT);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(elementsItem).noFulltextIndex().ignoreFileErrors());
			}

			if (extraPartsEl != null) {
				Item extraPartsItem = Item.newChildItem(productExtraType, product);
				extraPartsItem.setValue(NAME, "extra_parts");
				// первое сохранение для того, чтобы появился ID
				executeCommandUnit(SaveItemDBUnit.get(extraPartsItem).noFulltextIndex().ignoreFileErrors());
				fixHtmlImageLinks(parseItem, extraPartsItem, extraPartsEl, product_extra_.TEXT_PICS, product_extra_.TEXT);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(extraPartsItem).noFulltextIndex().ignoreFileErrors());
			}

			info.increaseProcessed();
		}
	}


	private void fixHtmlImageLinks(Item imgSource, Item imgDest, Element html, String destFileParamName, String destHtmlParamName) {
		if (imgDest.isNew())
			throw new IllegalStateException("Impossible to fix image paths in a new item due to absence of an ID");
		for (Element img : html.getElementsByTag("img")) {
			String fileName = Strings.getFileName(img.attr("src"));
			Path srcFile = FileDataType.getItemFile(imgSource, fileName);
			if (srcFile.toFile().exists()) {
				imgDest.setValue(destFileParamName, srcFile.toFile());
				img.attr("src", FileDataType.getItemFileUrl(imgDest, fileName));
			}
		}
		imgDest.setValue(destHtmlParamName, html.html());
	}

	@Override
	protected void terminate() throws Exception {

	}
}
