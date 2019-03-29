package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.persistence.commandunits.CopyItemDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import extra._generated.Section;
import lunacrawler.fwk.Parse_item;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Размещает на сайте информацию, полученную с помощью парсинга
 * Created by E on 15/2/2018.
 */
public class DeployParsedSingle extends MetaboIntegrateParsedCommand {

	protected final byte USER_GROUP_ID = UserGroupRegistry.getDefaultGroup();
	protected final int USER_ID = User.ANONYMOUS_ID;

	protected final String PRODUCT = "product";
	protected final String ID = "id";
	protected final String CODE = "code";
	protected final String VENDOR_CODE = "vendor_code";
	protected final String NAME = "name";
	protected final String SHORT = "short";
	protected final String GALLERY = "gallery";
	protected final String PIC = "pic";
	protected final String VIDEO = "video";
	protected final String TEXT = "text";
	protected final String APPLY = "apply";
	protected final String TEXTPICS = "textpics";
	protected final String IMG = "img";
	protected final String DOWNLOAD = "download";
	protected final String ASSOCIATED = "associated";
	protected final String ACCESSORY = "accessory";
	protected final String SET = "set";
	protected final String PROBE = "probe";
	protected final String TECH = "tech";
	protected final String TAG = "tag";
	protected final String PARAMETER = "parameter";
	protected final String VALUE = "value";
	protected final String PARAMS_XML = "params_xml";
	protected final String DESCRIPTION = "description";

	private final String CONTAINS = "contains";

	@Override
	protected boolean makePreparations() throws Exception {
		info.setOperation("Перенос разобранных товаров в каталог");
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		int processed = 0;
		info.limitLog(5000);
		final byte CONTAINS_ASSOC = ItemTypeRegistry.getAssoc(CONTAINS).getId();
		Item backCatalog = ItemUtils.ensureSingleRootItem("back_catalog", getInitiator(), USER_GROUP_ID, USER_ID);
		HashMap<String, Item> backSections = new HashMap<>();
		LinkedHashMap<Long, Item> sections = getLoadedItems("sec");
		LuceneIndexMapper.getSingleton().startUpdate();
		for (Item sec : sections.values()) {
			info.pushLog("Обработка раздела {}", sec.getStringValue(NAME));
			LinkedHashMap<Long, Item> secPIs = getLoadedChildItems("pi", sec.getId());
			if (secPIs.size() == 0)
				continue;

			// Создать невидимый раздел (если в нем будут товары)
			String backSecKey = sec.getKeyUnique();
			Item backSection = backSections.get(backSecKey);
			if (backSection == null) {
				backSection = ItemQuery.loadSingleItemByParamValue("back_section", "code", backSecKey);
				if (backSection == null) {
					backSection = ItemUtils.newChildItem("back_section", backCatalog);
					backSection.setValue("name", sec.getValue("name"));
					backSection.setValue("code", backSecKey);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(backSection));
					info.pushLog("Создан новый невидимый раздел {}", sec.getStringValue(NAME));
				}
				backSections.put(backSecKey, backSection);
			}

			// Создать и заполнить все товары
			for (Item item : secPIs.values()) {
				Parse_item pi = Parse_item.get(item);
				Item prod = deployParsed(pi, backSection, false);
				if (prod == null) {
					info.pushLog("ОШИБКА ! Товар {} НЕ ДОБАВЛЕН в раздел {}", pi.get_url(), sec.getStringValue("name"));
					continue;
				}
				if (!ItemQuery.isAncestor(prod.getId(), sec.getId(), CONTAINS_ASSOC)) {
					//executeCommandUnit(new CreateAssocDBUnit(prod, sec, CONTAINS_ASSOC));
					commitCommandUnits();
					info.pushLog("Товар {} добавлен в раздел {}", prod.getValue("name"), sec.getStringValue("name"));
				} else {
					rollbackCommandUnits();
					info.pushLog("ДУБЛЬ! Товар {}, раздел {}", prod.getValue("name"), sec.getStringValue("name"));
				}
				info.setProcessed(++processed);
			}
		}
		LuceneIndexMapper.getSingleton().finishUpdate();
	}

	/**
	 * Размещение товара в каталоге
	 * Коммит транзакции не присходит в методе, за это отвечает вызывающий метод.
	 * Это нужно чтобы исключить ошибки, которые не могут быть проверены в этом методе.
	 * @param pi
	 * @param parentSection
	 * @param doCopy
	 * @return
	 * @throws Exception
	 */
	protected Item deployParsed(Parse_item pi, Item parentSection, boolean doCopy) throws Exception {
		// Если айтем для парсинга - дублированный, найти оригинальный айтем
		if (pi.get_duplicated() == (byte) 1) {
			Item original = new ItemQuery(Parse_item._NAME)
					.addParameterCriteria(Parse_item.DUPLICATED, "0", "=", null, Compare.ANY)
					.addParameterCriteria(Parse_item.URL, pi.get_url(), "=", null, Compare.SOME)
					.loadFirstItem();
			if (original != null)
				pi = Parse_item.get(original);
		}

		// Разобрать XML, чтобы можно было найти код
		if (StringUtils.isBlank(pi.get_xml()))
			return null;
		Document doc = Jsoup.parse(pi.get_xml(), "localhost", Parser.xmlParser());
		Item prod = null;
		Elements prodEls = doc.getElementsByTag(PRODUCT);
		for (Element prodEl : prodEls) {
			prod = deployProduct(prodEl, parentSection, pi);
		}

		return prod;
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

	@Override
	protected void terminate() throws Exception {

	}

	private String RF_to_RB(String rf) {
		//return StringUtils.replace(rf, " РФ", " РБ");
		return rf;
	}
}