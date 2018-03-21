package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Размещает на сайте информацию, полученную с помощью парсинга
 * Created by E on 15/2/2018.
 */
public class DeployParsed extends IntegrateBase {

	private final byte USER_GROUP_ID = UserGroupRegistry.getDefaultGroup();
	private final int USER_ID = User.ANONYMOUS_ID;

	private final String ID = "id";
	private final String CODE = "code";
	private final String NAME = "name";
	private final String SHORT = "short";
	private final String GALLERY = "gallery";
	private final String PICTURE = "picture";
	private final String VIDEO = "video";
	private final String TEXT = "text";
	private final String APPLY = "apply";
	private final String TEXTPICS = "textpics";
	private final String IMG = "img";
	private final String DOWNLOAD = "download";
	private final String ASSOCIATED = "associated";
	private final String ACCESSORY = "accessory";
	private final String SET = "set";
	private final String PROBE = "probe";
	private final String TECH = "tech";
	private final String TAG = "tag";
	private final String PARAMETER = "parameter";
	private final String VALUE = "value";

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
		Item backCatalog = ItemUtils.ensureSingleRootItem(ItemNames.BACK_CATALOG, getInitiator(), USER_GROUP_ID, USER_ID);
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
				backSection = ItemQuery.loadSingleItemByParamValue(ItemNames.BACK_SECTION, ItemNames.back_section.CODE, backSecKey);
				if (backSection == null) {
					backSection = ItemUtils.newChildItem(ItemNames.BACK_SECTION, backCatalog);
					backSection.setValue(ItemNames.back_section.NAME, sec.getValue(ItemNames.section.NAME));
					backSection.setValue(ItemNames.back_section.CODE, backSecKey);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(backSection));
					info.pushLog("Создан новый невидимый раздел {}", sec.getStringValue(NAME));
				}
				backSections.put(backSecKey, backSection);
			}

			// Создать и заполнить все товары
			for (Item item : secPIs.values()) {
				Parse_item pi = Parse_item.get(item);
				Product prod = deployParsed(pi, backSection);
				if (prod == null) {
					info.pushLog("ОШИБКА ! Товар {} НЕ ДОБАВЛЕН в раздел {}", pi.get_url(), sec.getStringValue(ItemNames.section.NAME));
					continue;
				}
				if (!ItemQuery.isAncestor(prod.getId(), sec.getId(), CONTAINS_ASSOC)) {
					executeCommandUnit(new CreateAssocDBUnit(prod, sec, CONTAINS_ASSOC));
					commitCommandUnits();
					info.pushLog("Товар {} добавлен в раздел {}", prod.get_name(), sec.getStringValue(ItemNames.section.NAME));
				} else {
					rollbackCommandUnits();
					info.pushLog("ДУБЛЬ! Товар {}, раздел {}", prod.get_name(), sec.getStringValue(ItemNames.section.NAME));
				}
				info.setProcessed(++processed);
			}
		}
		LuceneIndexMapper.getSingleton().finishUpdate();
	}

	private Product deployParsed(Parse_item pi, Item parentSection) throws Exception {
		// Если айтем для парсинга - дублированный, найти оригинальный айтем
		if (pi.get_duplicated() == (byte) 1) {
			Item original = new ItemQuery(ItemNames.PARSE_ITEM)
					.addParameterCriteria(ItemNames.parse_item.DUPLICATED, "0", "=", null, Compare.ANY)
					.addParameterCriteria(ItemNames.parse_item.URL, pi.get_url(), "=", null, Compare.SOME)
					.loadFirstItem();
			if (original != null)
				pi = Parse_item.get(original);
		}

		// Разобрать XML, чтобы можно было найти код
		if (StringUtils.isBlank(pi.get_xml()))
			return null;
		Document doc = Jsoup.parse(pi.get_xml(), "localhost", Parser.xmlParser());
		String code = JsoupUtils.nodeText(doc, CODE);

		// Проверка, если айтем существует - ничего не делать
		// (Просто создать ссылку на этот продукт в вызывающем методе)
		Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product.CODE, code);
		if (product != null)
			return Product.get(product);

		// Создать сам продукт и все вложенные айтемы в одной транзакции
		//

		// Создание и заполнение продукта
		Product prod = Product.get(ItemUtils.newChildItem(ItemNames.PRODUCT, parentSection));
		prod.set_code(code);
		prod.set_name(JsoupUtils.nodeText(doc, NAME));
		prod.set_short(RF_to_RB(JsoupUtils.nodeHtml(doc, SHORT)));
		prod.set_text(RF_to_RB(JsoupUtils.nodeHtml(doc, TEXT)));
		prod.set_tech(RF_to_RB(JsoupUtils.nodeHtml(doc, TECH)));
		prod.set_apply(RF_to_RB(JsoupUtils.nodeHtml(doc, APPLY)));
		Element associated = doc.getElementsByTag(ASSOCIATED).first();
		for (Element access : associated.getElementsByTag(ACCESSORY)) {
			prod.add_accessiories(access.ownText());
		}
		for (Element set : associated.getElementsByTag(SET)) {
			prod.add_sets(set.ownText());
		}
		for (Element probe : associated.getElementsByTag(PROBE)) {
			prod.add_probes(probe.ownText());
		}

		// Заполнение картинок
		HashMap<String, File> picFiles = new HashMap<>();
		List<File> allFiles = pi.getAll_file();
		for (File file : allFiles) {
			picFiles.put(file.getName(), file);
		}
		Element gallery = doc.getElementsByTag(GALLERY).first();
		boolean noMainPic = true;
		for (Element picEl : gallery.getElementsByTag(PICTURE)) {
			String fileName = Strings.getFileName(picEl.ownText());
			File pic = picFiles.get(fileName);
			if (pic != null) {
				prod.setValue(ItemNames.product.GALLERY, pic);
				picFiles.remove(fileName);
				if (noMainPic) {
					try {
						ByteArrayOutputStream os = ResizeImagesFactory.resize(pic, -1, 300);
						File mainFile = new File(pic.getParentFile().getCanonicalPath() + "/main_" + pic.getName());
						FileUtils.writeByteArrayToFile(mainFile, os.toByteArray());
						prod.setValue(ItemNames.product.MAIN_PIC, mainFile);
						noMainPic = false;
					} catch (Exception e) {
						ServerLogger.error("resize error", e);
						info.pushLog("ОШИБКА! {}", e.getLocalizedMessage());
					}
				}
			}
		}
		for (File picFile : picFiles.values()) {
			prod.setValue(ItemNames.product.TEXT_PICS, picFile);
		}

		// Заполнение видео
		for (Element vidEl : gallery.getElementsByTag(VIDEO)) {
			prod.setValue(ItemNames.product.VIDEO, vidEl.ownText());
		}

		// Сохранение продукта
		executeCommandUnit(SaveItemDBUnit.get(prod));

		// Создание тэгов
		for (Element tag : doc.getElementsByTag(TAG)) {
			Tag_first tagFirst = Tag_first.get(ItemUtils.newChildItem(ItemNames.TAG_FIRST, prod));
			tagFirst.set_tag(tag.attr(NAME));
			executeCommandUnit(SaveItemDBUnit.get(tagFirst));
			for (Element param : tag.getElementsByTag(PARAMETER)) {
				String name = JsoupUtils.nodeText(param, NAME);
				for (Element value : param.getElementsByTag(VALUE)) {
					String valStr = value.ownText();
					Tag_second tagSecond = Tag_second.get(ItemUtils.newChildItem(ItemNames.TAG_SECOND, tagFirst));
					tagSecond.set_name(name);
					tagSecond.set_value(valStr);
					tagSecond.set_name_value(name + ":" + valStr);
					executeCommandUnit(SaveItemDBUnit.get(tagSecond));
				}

			}
		}

		// Исправить адреса картинок в HTML
		updatePics(prod, ItemNames.product.TEXT, ItemNames.product.TEXT_PICS);
		updatePics(prod, ItemNames.product.APPLY, ItemNames.product.TEXT_PICS);
		executeCommandUnit(SaveItemDBUnit.get(prod));

		return prod;
	}

	private void updatePics(Item item, String textParamName, String picParamName) {
		HashSet<String> picNames = new HashSet<>();
		picNames.addAll(item.outputValues(picParamName));
		Document doc = Jsoup.parse(item.getStringValue(textParamName));
		// Элементы img src
		updateDocTagAttributeWithPic(doc, "img", "src", item, picNames);
		/*
		for (Element img : doc.getElementsByTag("img")) {
			String src = img.attr("src");
			String fileName = Strings.getFileName(src);
			if (picNames.contains(fileName)) {
				String newSrc = AppContext.getFilesUrlPath(item.isFileProtected()) + item.getRelativeFilesPath() + fileName;
				img.attr("src", newSrc);
			}
		} */
		// Элементы a href
		updateDocTagAttributeWithPic(doc, "a", "href", item, picNames);
		item.setValue(textParamName, JsoupUtils.outputHtmlDoc(doc));
	}

	private void updateDocTagAttributeWithPic(Element parentEl, String tag, String attr, Item item, HashSet<String> pics) {
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
		return StringUtils.replace(rf, " РФ", " РБ");
	}
}
