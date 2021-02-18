package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import lunacrawler.fwk.Parse_item;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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
	private final String TAG = "tag";
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
	private final String ASSOC_CODE = "assoc_code";
	private final String ASSOC = "assoc";
	private final String URL = "url";
	private final String MANUAL = "manual";
	private final String PARTS = "parts";



	private ParsedInfoProvider infoProvider;
	private ItemType sectionType;
	private ItemType productType;
	private ItemType productExtraType;
	private ItemType paramsXmlType;

	public MetaboIntegrateParsedCommand() {
		sectionType = ItemTypeRegistry.getItemType(SECTION);
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		productExtraType = ItemTypeRegistry.getItemType(PRODUCT_EXTRA);
		paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML);
	}

	@Override
	protected boolean makePreparations() throws Exception {
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
			try {
				deployProduct(productDoc, parent, null);
			} catch (Exception e) {
				ServerLogger.error("Error while deploying product");
				info.addError("Товар '" + code + "' не может быть размещен корректно", code);
			}
		}
	}

	Item deployProduct(Element productEl, Item parent, Parse_item pi) throws Exception {
		String code = productEl.getElementsByTag(CODE).first().ownText();
		String name = productEl.getElementsByTag(NAME).first().ownText();
		String tag = StringUtils.substringBefore(name, " ");
		String type = productEl.getElementsByTag(TYPE).first().ownText();
		String nameExtra = productEl.getElementsByTag(NAME_EXTRA).first().ownText();
		String shortTxt = productEl.getElementsByTag(SHORT).first().html();
		String description = productEl.getElementsByTag(EXTRA).first().html();
		String text = productEl.getElementsByTag(DESCRIPTION).first().html();
		String tech = productEl.getElementsByTag(TECH).first().html();
		String packageTxt = productEl.getElementsByTag(PACKAGE).first().html();
		String extraXml = productEl.getElementsByTag(SYMBOLS).first().html();
		Elements spinEls = productEl.getElementsByTag(SPIN);
		for (Element spinEl : spinEls) {
			extraXml += spinEl.outerHtml();
		}
		Elements videoEls = productEl.getElementsByTag(VIDEO);
		for (Element videoEl : videoEls) {
			extraXml += videoEl.outerHtml();
		}
		Elements manuals = productEl.getElementsByTag(MANUAL);
		for (Element manualEl : manuals) {
			extraXml += manualEl.outerHtml();
		}
		Elements spareParts = productEl.getElementsByTag(PARTS);
		if (spareParts.size() > 0) {
			extraXml += spareParts.first().outerHtml();
		}
		ArrayList<Path> gallery = new ArrayList<>();
		Elements pics = productEl.getElementsByTag(GALLERY).first().getElementsByTag(PIC);
		if (infoProvider != null) {
			for (Element pic : pics) {
				Path file =  infoProvider.getFile(code, pic.attr(LINK));
				if (file != null)
					gallery.add(file);
			}
		} else if (pi != null) {
			HashMap<String, Path> picFiles = new HashMap<>();
			List<File> allFiles = pi.getAll_file();
			for (File file : allFiles) {
				picFiles.put(file.getName(), Paths.get(file.getAbsolutePath()));
			}
			for (Element pic : pics) {
				String fileName = Strings.getFileName(pic.attr("download"));
				Path picFile = picFiles.get(fileName);
				if (picFile != null) {
					gallery.add(picFile);
					picFiles.remove(fileName);
				}
			}
		}
		ArrayList<String> assocCodes = new ArrayList<>();
		Elements urlEls = productEl.getElementsByTag(ASSOC).first().getElementsByTag(URL);
		for (Element urlEl : urlEls) {
			Element assocProduct = infoProvider.getTree().getElementsByAttributeValue(URL, urlEl.ownText()).first();
			if (assocProduct != null) {
				assocCodes.add(assocProduct.attr(ID));
			}
		}
		// Продукт

		// Проверка, если айтем существует - создать копию этого продукта
		Item product = ItemQuery.loadSingleItemByParamValue("product", CODE, code);
		if (product == null) {
			product = Item.newChildItem(productType, parent);
		}
		product.setValue(NAME, name);
		product.setValue(CODE, code);
		product.setValue(TYPE, type);
		product.setValue(NAME_EXTRA, nameExtra);
		product.setValue(SHORT, shortTxt);
		product.setValue(DESCRIPTION, description);
		product.setValue(TEXT, text);
		product.setValue(EXTRA_XML, extraXml);
		product.setValue(TAG, tag);
		for (String assocCode : assocCodes) {
			product.setValue(ASSOC_CODE, assocCode);
		}
		for (Path path : gallery) {
			product.setValue(GALLERY, path.toFile());
		}
		if (gallery.size() > 0) {
			Path firstPic = gallery.get(0);
			Path newMainPic = firstPic.resolveSibling("main_" + firstPic.getFileName());
			FileUtils.copyFile(firstPic.toFile(), newMainPic.toFile());
            try {
	            ByteArrayOutputStream bos = ResizeImagesFactory.resize(firstPic.toFile(), 0, 400);
	            Files.write(newMainPic, bos.toByteArray());
            } catch (Exception e) {
            	ServerLogger.error("Error while resizing file for small pic", e);
	            FileUtils.copyFile(firstPic.toFile(), newMainPic.toFile());
            }
			product.setValue(MAIN_PIC, newMainPic.toFile());
		}

		executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());

		// Параметры XML

		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		if (StringUtils.isNotBlank(tech)) {
			Element techEl = productEl.getElementsByTag(TECH).first();
			Elements trs = techEl.getElementsByTag("tr");
			for (Element tr : trs) {
				Elements tds = tr.getElementsByTag("td");
				if (tds.size() != 2)
					continue;
				Element nameTd = tds.get(0);
				Element valueTd = tds.get(1);
				if (nameTd.hasAttr("class"))
					continue;
				String value = StringUtils.normalizeSpace(valueTd.ownText());
				if (StringUtils.isBlank(value) || StringUtils.startsWith(value, "&"))
					continue;
				String paramName = nameTd.ownText();
				xml.startElement("parameter").startElement("name").addText(paramName).endElement();
				xml.startElement("value").addText(value).endElement();
				xml.endElement(); // параметр закрывается
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
			Document packDoc = Jsoup.parse(packageTxt);
			for (Element imgEl : packDoc.getElementsByTag("img")) {
				String relSrc = imgEl.attr("src");
				File newFile = new File(AppContext.getRealPath(relSrc));
				File dir = newFile.getParentFile();
				try {
					dir.mkdirs();
					String src = "https://laserlevel.ru" + imgEl.attr("src");
					WebClient.saveFile(src, newFile.getParent(), newFile.getName());
					//FileUtils.copyURLToFile(new URL(src), newFile, 5000, 5000);
				} catch (Exception e) {
					ServerLogger.error("Error while coopying files from HTML", e);
					info.addError("Error while coopying file from HTML: " + relSrc, newFile.getAbsolutePath());
				}
			}
			Item packageItem = Item.newChildItem(productExtraType, product);
			packageItem.setValue(NAME, "package");
			packageItem.setValue(TEXT, packageTxt);

			executeAndCommitCommandUnits(SaveItemDBUnit.get(packageItem).noFulltextIndex().ignoreFileErrors());
		}

		info.increaseProcessed();
		return product;
	}


	@Override
	protected void terminate() throws Exception {

	}
}
