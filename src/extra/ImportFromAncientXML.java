package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CleanDeletedItemsDBUnit;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Anton on 08.11.2018.
 */
public class ImportFromAncientXML extends IntegrateBase implements CatalogConst{
	//	private String base = "http://mizida.by";
	private Item catalog;
	private Item seoContainer;
	private HashSet<String> sectionURLs = new HashSet<>();
	private String baseURL;
	private String start_url;
	private HashMap<String, Item> sectionsMap = new HashMap<>();
	private LinkedList<String> urls = new LinkedList<>();


	@Override
	protected boolean makePreparations() throws Exception {
		baseURL = getVarSingleValue("base_url");
		start_url = getVarSingleValue("start_url");
		return true;
	}

	@Override
	protected void integrate() throws Exception{
		setOperation("Импорт каталога со старого сатйа");
		catalog = ItemUtils.ensureSingleRootAnonymousItem(CATALOG_ITEM, getInitiator());
		seoContainer = ItemUtils.ensureSingleRootAnonymousItem("seo_container", getInitiator());
		sectionsMap.put("catalog", catalog);

		info.setCurrentJob("Подключене к списку разделов на старом сайте");
		Document doc = Jsoup.parse(new URL(baseURL+'/'+start_url), 5000);
		Elements sections = doc.getElementsByTag("section");

		CleanDeletedItemsDBUnit delete = new CleanDeletedItemsDBUnit(1000);
		executeAndCommitCommandUnits(delete);

		setOperation("Создание списка разделов");
		sections.forEach((section)->{
			String name = section.select(NAME).first().text();
			info.setCurrentJob("Созднание раздела: \""+name+"\"");
			String code = section.attr("id");
			String oldURL = section.select("show_section").first().text();
			Element textNode = section.select("text").first();
			String text = textNode == null? "" : textNode.html();
			String productsURL = section.select("show_xml").first().text();
			urls.add(productsURL);
			Element parentTag = section.parent();
			Item parentItem = catalog;
			if(!parentTag.is("catalog")){
				parentItem = sectionsMap.get(parentTag.attr("id"));
			}
			Item sectionItem = ItemUtils.newChildItem(SECTION_ITEM, parentItem);

			sectionsMap.put(code, sectionItem);
			try {
				sectionItem.setValueUI(CATEGORY_ID_PARAM, code);
				sectionItem.setValueUI("old_url", oldURL);
				sectionItem.setValueUI(NAME_PARAM, name);
				sectionItem.setKeyUnique(Strings.translit(name));
				executeAndCommitCommandUnits(SaveItemDBUnit.get(sectionItem).ignoreUser(true).noFulltextIndex());
				if(StringUtils.isNotBlank(text)) {
					Item seo = ItemUtils.newChildItem(ItemNames.SEO, seoContainer);
					seo.setValueUI(ItemNames.seo_.KEY_UNIQUE, sectionItem.getKeyUnique());
					seo.setValueUI(ItemNames.seo_.TEXT, processSectionText(text));
					executeAndCommitCommandUnits(SaveItemDBUnit.get(seo).ignoreUser(true).noTriggerExtra().noFulltextIndex());
					executeAndCommitCommandUnits(CreateAssocDBUnit.childExistsSoft(seo, sectionItem, ItemTypeRegistry.getAssocId("seo")));
				}
				info.increaseProcessed();
			} catch (Exception e) {
				info.addError(e);
				ServerLogger.error(e);
			}

		});
		pushLog("Созднание разделов завершено");
	}

	private String processSectionText(String text){
		if(StringUtils.isBlank(text)) return "";
		text = text.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		text = StringUtils.substringBeforeLast(text, "<div class=\"image\" style=\"text-align: justify;\"> <table style=\"height: 25px; width: 630px;\">");
		text = StringUtils.substringAfter(text, "<div class=\"content_fb\" style=\"display: block;\">");
		return text;
	}

	@Override
	protected void terminate() throws Exception {

	}

	private void processSectionElement(Element sectionElement, Item parentSection) throws Exception{

	}

	private void processTextPics(Item item, String picParamName, String textParamName, String text) throws Exception {

	}

	private void processProductElement(Element productElement, Item section) throws Exception {

	}
}
