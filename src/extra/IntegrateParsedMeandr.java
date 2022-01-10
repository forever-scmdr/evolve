package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Custom_page;
import extra._generated.ItemNames;
import extra._generated.Page_text;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.List;

/**
 * Created by E on 3/5/2018.
 */
public class IntegrateParsedMeandr extends IntegrateBase implements ItemNames, CatalogConst {


	private ParsedInfoProvider infoProvider;
	private ItemType customPagesCatalogType;
	private ItemType customPageType;
	private ItemType textPartType;

	private static final String CATALOG_HEADER = "Каталог выпускаемой и поставляемой продукции";
	//private ItemType productExtraType;


	@Override
	protected boolean makePreparations() throws Exception {
		customPagesCatalogType = ItemTypeRegistry.getItemType(CUSTOM_PAGES);
		customPageType = ItemTypeRegistry.getItemType(CUSTOM_PAGE);
		textPartType = ItemTypeRegistry.getItemType(PAGE_TEXT);
		infoProvider = new ParsedInfoProvider();
		return infoProvider.isValid();
	}

	@Override
	protected void integrate() throws Exception {
		info.setToProcess(0);
		info.setProcessed(0);
		info.limitLog(300);
		Item pagesCatalog = ItemUtils.ensureSingleRootAnonymousItem(CUSTOM_PAGES, getInitiator());
		List<Item> rootPages = ItemQuery.loadByParamValue(CUSTOM_PAGE, Custom_page.HEADER, CATALOG_HEADER);
		if (rootPages.size() > 0) {
			for (Item root : rootPages) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(root).ignoreUser().noFulltextIndex().noTriggerExtra());
			}
		}
		Item rootPage = ItemUtils.ensureSingleAnonymousItem(CUSTOM_PAGE, getInitiator(), pagesCatalog.getId());
		rootPage.setValue(Custom_page.HEADER, CATALOG_HEADER);
		Document tree = infoProvider.getTree();
		Element root = tree.getElementsByTag("data").first();
		processSubsections(root, rootPage);
	}

	private void processSubsections(Element root, Item parent) throws Exception {
		Elements sectionEls = root.select("> section");
		for (Element sectionEl : sectionEls) {
			String secCode = null;
			String secName = null;
			try {
				secCode = sectionEl.attr(ID_ATTR);
				ParsedInfoProvider.InfoAccessor doc = infoProvider.getAccessor(secCode);
				secName = doc.getNodeText(NAME_ELEMENT);
				Custom_page section = Custom_page.get(Item.newChildItem(customPageType, parent));
				section.set_name(secName);
				section.set_header(doc.getNodeText(H1_ELEMENT));
				Element mainPicEl = doc.getFirst(MAIN_PIC_PARAM);
				if (mainPicEl != null) {
					section.set_main_pic(infoProvider.getFile(secCode, mainPicEl.attr(LINK_PARAM)).toFile());
				}
				String shortDesc = doc.getNodeHtml(SHORT_PARAM);
				if (StringUtils.isNotBlank(shortDesc)) {
					section.set_short(shortDesc);
				}

				setTextWithPics(section, secCode, doc, Custom_page.TEXT, Custom_page.TEXT_PIC, TEXT_PARAM);

				executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex());
				processSubsections(sectionEl, section);
			} catch (Exception e) {
				ServerLogger.error("Section save error", e);
				info.addError("Section save error, ID = " + secCode + ", name = " + secName, parent.getStringValue(NAME_PARAM));
			}
		}
		Elements productEls = root.select("> product");
		for (Element productElTree : productEls) {
			String code = null;
			String name = null;
			try {
				code = productElTree.attr(ID_ATTR);
				if (StringUtils.isBlank(code))
					continue;
				ParsedInfoProvider.InfoAccessor productDoc;
				try {
					productDoc = infoProvider.getAccessor(code);
				} catch (Exception e) {
					ServerLogger.error("Error parsing product xml file", e);
					info.addError("Документ для товара '" + code + "' содержит ошибки", code);
					continue;
				}
				String header = productDoc.getNodeText(H1_ELEMENT);
				name = productDoc.getNodeText(NAME, header);
				String gallery = productDoc.getNodeHtml("gallery");
				String products = productDoc.getNodeHtml("products");
				String description = productDoc.getNodeHtml(DESCRIPTION_ELEMENT);

				// Продукт

				Custom_page product = Custom_page.get(Item.newChildItem(customPageType, parent));
				product.set_name(name);
				product.set_header(header);
				Element mainPicEl = productDoc.getFirst(MAIN_PIC_PARAM);
				if (mainPicEl != null) {
					product.set_main_pic(infoProvider.getFile(code, mainPicEl.attr(LINK_PARAM)).toFile());
				}
				String shortDesc = productDoc.getNodeHtml(SHORT_PARAM);
				if (StringUtils.isNotBlank(shortDesc)) {
					product.set_short(shortDesc);
				}

				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());

				if (StringUtils.isNotBlank(gallery)) {
					Item galleryTxt = Item.newChildItem(textPartType, product);
					galleryTxt.setValue(Page_text.NAME, "Фотогалерея");
					setTextWithPics(galleryTxt, code, productDoc, Page_text.TEXT, Page_text.TEXT_PIC, "gallery");
					executeAndCommitCommandUnits(SaveItemDBUnit.get(galleryTxt).noFulltextIndex().ignoreFileErrors());
				}

				if (StringUtils.isNotBlank(products)) {
					Item productsTxt = Item.newChildItem(textPartType, product);
					productsTxt.setValue(Page_text.NAME, "Список товаров");
					productsTxt.setValue(Page_text.TEXT, products);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(productsTxt).noFulltextIndex().ignoreFileErrors());
				}

				if (StringUtils.isNotBlank(products)) {
					Item mainTxt = Item.newChildItem(textPartType, product);
					mainTxt.setValue(Page_text.NAME, "Описание");
					setTextWithPics(mainTxt, code, productDoc, Page_text.TEXT, Page_text.TEXT_PIC, "description");
					executeAndCommitCommandUnits(SaveItemDBUnit.get(mainTxt).noFulltextIndex().ignoreFileErrors());
				}
				info.increaseProcessed();
			} catch (Exception e) {
				ServerLogger.error("Product save error", e);
				info.addError("Product save error, ID = " + code + ", name = " + name, parent.getStringValue(NAME_PARAM));
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
				item.setValue(picParamName, infoProvider.getFile(code, textPic.attr(LINK_PARAM)).toFile());
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
