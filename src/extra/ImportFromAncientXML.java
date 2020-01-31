package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anton on 08.11.2018.
 */
public class ImportFromAncientXML extends IntegrateBase implements CatalogConst{
	private File exportFile;
	//	private String base = "http://mizida.by";
	private static final Pattern SRC = Pattern.compile("src=\"(?<src>sitefiles/(\\d+/)+(?<filename>[^\"]+))\"");
	private static final String OLD_SECTION_NAME = "каталог со старого сайта";

	@Override
	protected boolean makePreparations() throws Exception {
		String contextPath = AppContext.getContextPath();
		exportFile = Paths.get(contextPath, "upload", "export.xml").toFile();
		if(exportFile.exists())
		ServerLogger.debug("Export file found. Size: " + Files.size(exportFile.toPath()));
		return exportFile.exists();
	}

	@Override
	protected void integrate() throws Exception{
		info.setOperation("Разбор старого каталога");


		Document oldCatalog = Jsoup.parse(new FileInputStream(exportFile), "UTF-8", "", Parser.xmlParser());

		int sectionCount = oldCatalog.select("section").size();
		int productsCount = oldCatalog.select("device").size();
		info.setToProcess(sectionCount + productsCount);
		ServerLogger.debug("Jsoup parsing complete. <section> ="+sectionCount+". <device>="+ productsCount);

		info.pushLog("Обнаружено разделов: "+sectionCount);
		info.pushLog("Обнаружено товаров: "+productsCount);

		Item existingCatalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		ServerLogger.debug("Catalog loaded.");

		ItemQuery q = new ItemQuery(SECTION_ITEM, Item.STATUS_HIDDEN);
		q.addParameterCriteria(NAME_PARAM, OLD_SECTION_NAME, "=", null, Compare.SOME);
		Item oldBigSection = q.loadFirstItem();

		ServerLogger.debug((oldBigSection == null)? "old section not exists" : "old section exists");

		if(oldBigSection != null){
			ServerLogger.debug("Adding delete commands.");
			executeCommandUnit(ItemStatusDBUnit.delete(oldBigSection.getId()).noFulltextIndex());
			ServerLogger.debug("delete commands added");
			commitCommandUnits();
			ServerLogger.debug("delete commands executed");
		}

		ServerLogger.debug("creating ancient catalog");
		Item bigSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), existingCatalog);

		bigSection.setValue(NAME_PARAM, OLD_SECTION_NAME);
		String id = oldCatalog.getElementsByTag("catalog").first().getElementsByTag("id").first().ownText();
		bigSection.setValueUI(CATEGORY_ID_PARAM,id);

		ServerLogger.debug("executing save ancient catalog commands...");
		executeAndCommitCommandUnits(SaveItemDBUnit.get(bigSection).noFulltextIndex());
		ServerLogger.debug("ancient catalog created");
		executeAndCommitCommandUnits(ItemStatusDBUnit.hide(bigSection).noFulltextIndex());
		ServerLogger.debug("ancient catalog hidden");

		Elements sections = oldCatalog.select("catalog > section");
		ServerLogger.debug("processing sections. Section level 1 count: "+sections.size());
		for (Element sectionElement : sections){
			processSectionElement(sectionElement, bigSection);
		}

	}


	private void processSectionElement(Element sectionElement, Item parentSection) throws Exception{

		String sectionName = sectionElement.select("name").eq(0).first().ownText();
		String sectionCode = sectionElement.select("id").eq(0).first().ownText();
		String sectionText = sectionElement.select("text").eq(0).first().ownText();

		ServerLogger.debug("processing section. name="+sectionName);

		Item section = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), parentSection);
		ServerLogger.debug("section created");

		section.setValue(NAME_PARAM, sectionName);
		section.setValue(CATEGORY_ID_PARAM, sectionCode);
		section.setValue(PARENT_ID_PARAM, parentSection.getValue(CATEGORY_ID_PARAM));
		ServerLogger.debug("parameters set");
		executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
		ServerLogger.debug("section saved");

		Item seo = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.SEO), section);
		ServerLogger.debug("SEO-item created");
		seo.setValue(ItemNames.seo_.TITLE, sectionName);
		seo.setValue(ItemNames.seo_.TEXT, sectionText);
		ServerLogger.debug("SEO-item parameters added");
		executeAndCommitCommandUnits(SaveItemDBUnit.get(seo).noFulltextIndex().ignoreFileErrors(true));
		ServerLogger.debug("SEO-item parameters saved");

		Elements children = sectionElement.children();
		ServerLogger.debug(children.size()+" child elements found.");

		for(Element child : children){
			String nodeName = child.tagName();
			if(nodeName.equals("section")) {
				processSectionElement(child, section);
			}else if(nodeName.equals("device")){
				processProductElement(child, section);
			}
		}
		info.increaseProcessed();
	}

	private void processTextPics(Item item, String picParamName, String textParamName, String text) throws Exception {
		executeAndCommitCommandUnits(SaveItemDBUnit.get(item).noFulltextIndex().ignoreFileErrors(true));
		String folder = item.getRelativeFilesPath();
		Matcher matcher = SRC.matcher(text);
		HashMap<String, String> replacementMap = new HashMap<>();
		while (matcher.find()){
			String src = matcher.group("src");
			String fileName = matcher.group("filename");
			String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
			src = src.replace("\\Q"+fileName+"\\E", fileNameDecoded);
			//URL url = new URL(base+"/"+src);\"
			File old = new File((AppContext.getContextPath()+src.replace("\\Q"+"\\sitefiles"+"\\E", "sitefiles")).replace(" ", "").trim());
			String newSrc = "files/"+ folder + Strings.translit(fileNameDecoded);
			replacementMap.put(src, newSrc);
			if(!new File(newSrc).exists()) {
				item.setValue(picParamName, old);
			}
		}
		for(Map.Entry<String,String> e : replacementMap.entrySet()){
			text = text.replaceAll("\\Q"+e.getKey()+"\\E", e.getValue());
		}
		item.setValue(textParamName, text);
	}

	private void processProductElement(Element productElement, Item section) throws Exception {
		String name = productElement.select("name").eq(0).first().ownText();
		String code = productElement.select("code").eq(0).first().ownText();
		code = (StringUtils.isAllBlank(code))?  productElement.select("id").eq(0).first().ownText() : code;
		String price = productElement.select("price").eq(0).first().ownText();
		String unit =  productElement.select("measure").eq(0).first().ownText();
		String shortText =  productElement.select("short").eq(0).first().ownText();
		String oldURL =  productElement.select("show_device").eq(0).first().ownText();
		String text = productElement.select("text").eq(0).first().ownText();
		String bigPic = productElement.select("big").eq(0).first().ownText();
		//String smallPic = productElement.select("small").eq(0).first().ownText();
		ServerLogger.debug("device element loaded from Jsoup. Device name = \"name\"");

		Item product = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT_ITEM), section);
		ServerLogger.debug("Product item created.");
		product.setValue(NAME_PARAM, name);
		product.setValue(CODE_PARAM, code);
		product.setValueUI(PRICE_PARAM, price.replaceAll("\\s", ""));
		product.setValue("unit", unit);
		product.setValue(DESCRIPTION_PARAM, shortText);
		product.setValue(URL_PARAM, oldURL);
		//product.setValue(MAIN_PIC_PARAM, new URL(base+"/"+bigPic));
		//product.setValue(SMALL_PIC_PARAM,new URL(base+"/"+smallPic));
		Path ofp = Paths.get(AppContext.getContextPath(), "sitefiles", bigPic);
		product.setValue("old_file", ofp.toString());
		ServerLogger.debug("old main img path: " + ofp.toString());
		product.setValue(MAIN_PIC_PARAM, ofp.toFile());
		//product.setValue(SMALL_PIC_PARAM, Paths.get(AppContext.getContextPath(), "sitefiles", smallPic).toFile());
		//processTextPics(product, TEXT_PICS_PARAM, TEXT_PARAM, text);
		product.setValue(TEXT_PARAM, text);
		ServerLogger.debug("Trying to save product...");
		executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors(true));
		ServerLogger.debug("product saved. new file path: " + product.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(product.isFileProtected())));
		info.increaseProcessed();
	}
}
