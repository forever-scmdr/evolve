package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
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
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 08.11.2018.
 */
public class ImportFromAncientXML extends IntegrateBase implements CatalogConst{
	private File exportFile;
	private String base = "http://mizida.by";
	private static final Pattern SRC = Pattern.compile("src=\"(?<src>sitefiles/(\\d+/)+(?<filename>[^\"]+))\"");
	private static final String OLD_SECTION_NAME = "каталог со старого сайта";

	@Override
	protected boolean makePreparations() throws Exception {
		String contextPath = AppContext.getContextPath();
		exportFile = Paths.get(contextPath, "upload", "export.xml").toFile();
		return exportFile.exists();
	}

	@Override
	protected void integrate() throws Exception{
		info.setOperation("Разбор старого каталога");

		Document oldCatalog = Jsoup.parse(new FileInputStream(exportFile), "UTF-8", "", Parser.xmlParser());
		Item existingCatalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		ItemQuery q = new ItemQuery(SECTION_ITEM, Item.STATUS_HIDDEN);
		q.addParameterCriteria(NAME_PARAM, OLD_SECTION_NAME, "=", null, Compare.SOME);
		Item oldBigSection = q.loadFirstItem();
		if(oldBigSection != null){
			executeCommandUnit(ItemStatusDBUnit.delete(oldBigSection.getId()).noFulltextIndex());
			executeCommandUnit(new CleanAllDeletedItemsDBUnit(20, null));
			commitCommandUnits();
		}
		Item bigSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), existingCatalog);
		bigSection.setValue(NAME_PARAM, OLD_SECTION_NAME);
		String id = oldCatalog.getElementsByTag("catalog").first().getElementsByTag("id").first().ownText();
		bigSection.setValueUI(CATEGORY_ID_PARAM,id);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(bigSection).noFulltextIndex());
		executeAndCommitCommandUnits(ItemStatusDBUnit.hide(bigSection).noFulltextIndex());
		Elements sections = oldCatalog.select("catalog > section");

		int sectionCount = oldCatalog.select("section").size();
		int productsCount = oldCatalog.select("device").size();
		info.setToProcess(sectionCount + productsCount);
		info.pushLog("Обнаружено разделов: "+sectionCount);
		info.pushLog("Обнаружено товаров: "+productsCount);

		for (Element sectionElement : sections){
			processSectionElement(sectionElement, bigSection);
		}

	}

	@Override
	protected void terminate() throws Exception {

	}

	private void processSectionElement(Element sectionElement, Item parentSection) throws Exception{

		String sectionName = sectionElement.select("name").eq(0).first().ownText();
		String sectionCode = sectionElement.select("id").eq(0).first().ownText();
		String sectionText = sectionElement.select("text").eq(0).first().ownText();

		Item section = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), parentSection);

		section.setValue(NAME_PARAM, sectionName);
		section.setValue(CATEGORY_ID_PARAM, sectionCode);
		section.setValue(PARENT_ID_PARAM, parentSection.getValue(CATEGORY_ID_PARAM));
		executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
		Item seo = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.SEO), section);
		seo.setValue(ItemNames.seo_.TITLE, sectionName);
		processTextPics(seo, ItemNames.seo_.TEXT_PIC, ItemNames.seo_.TEXT, sectionText);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(seo).noFulltextIndex().ignoreFileErrors(true));

		Elements children = sectionElement.children();

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
		String smallPic = productElement.select("small").eq(0).first().ownText();

		Item product = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT_ITEM), section);
		product.setValue(NAME_PARAM, name);
		product.setValue(CODE_PARAM, code);
		product.setValueUI(PRICE_PARAM, price.replaceAll("\\s", ""));
		product.setValue("unit", unit);
		product.setValue(DESCRIPTION_PARAM, shortText);
		product.setValue(URL_PARAM, oldURL);
		//product.setValue(MAIN_PIC_PARAM, new URL(base+"/"+bigPic));
		//product.setValue(SMALL_PIC_PARAM,new URL(base+"/"+smallPic));
		product.setValue(MAIN_PIC_PARAM, Paths.get(AppContext.getContextPath(), "sitefiles", bigPic).toFile());
		product.setValue(SMALL_PIC_PARAM, Paths.get(AppContext.getContextPath(), "sitefiles", smallPic).toFile());
		processTextPics(product, TEXT_PICS_PARAM, TEXT_PARAM, text);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors(true));
		info.increaseProcessed();
	}
}
