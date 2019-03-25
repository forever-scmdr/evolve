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
public class DeployParsedSingle extends IntegrateBase {

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
				Product prod = deployParsed(pi, backSection, false);
				if (prod == null) {
					info.pushLog("ОШИБКА ! Товар {} НЕ ДОБАВЛЕН в раздел {}", pi.get_url(), sec.getStringValue("name"));
					continue;
				}
				if (!ItemQuery.isAncestor(prod.getId(), sec.getId(), CONTAINS_ASSOC)) {
					//executeCommandUnit(new CreateAssocDBUnit(prod, sec, CONTAINS_ASSOC));
					commitCommandUnits();
					info.pushLog("Товар {} добавлен в раздел {}", prod.get_name(), sec.getStringValue("name"));
				} else {
					rollbackCommandUnits();
					info.pushLog("ДУБЛЬ! Товар {}, раздел {}", prod.get_name(), sec.getStringValue("name"));
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
	protected Product deployParsed(Parse_item pi, Item parentSection, boolean doCopy) throws Exception {
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
		Product prod = null;
		Elements prodEls = doc.getElementsByTag(PRODUCT);
		for (Element prodEl : prodEls) {
			String code = JsoupUtils.nodeText(prodEl, CODE);

			// Проверка, если айтем существует - создать копию этого продукта
			Item product = ItemQuery.loadSingleItemByParamValue(Product._NAME, "code", code);
			if (product != null) {
				Item parent = new ItemQuery(Section._NAME).setChildId(product.getId(), false).loadFirstItem();
				if (doCopy && (parent == null || parent.getId() != parentSection.getId())) {
					executeCommandUnit(new CopyItemDBUnit(product, parentSection));
				}
				return Product.get(product);
			}

			// Создать сам продукт и все вложенные айтемы в одной транзакции
			//

			// Создание и заполнение продукта
			prod = Product.get(ItemUtils.newChildItem(Product._NAME, parentSection));
			prod.set_code(code);
			prod.set_vendor_code(JsoupUtils.nodeText(prodEl, VENDOR_CODE));
			prod.set_name(JsoupUtils.nodeText(prodEl, NAME));
			prod.set_description(RF_to_RB(JsoupUtils.nodeHtml(doc, DESCRIPTION)));
			prod.set_text(RF_to_RB(JsoupUtils.nodeHtml(prodEl, TEXT)));
			//prod.set_tech(RF_to_RB(JsoupUtils.nodeHtml(doc, TECH)));
			//prod.set_apply(RF_to_RB(JsoupUtils.nodeHtml(doc, APPLY)));
			Element associated = prodEl.getElementsByTag(ASSOCIATED).first();
			if (associated != null) {
				for (Element access : associated.getElementsByTag(ACCESSORY)) {
					//prod.add_accessiories(access.ownText());
				}
				for (Element set : associated.getElementsByTag(SET)) {
					//prod.add_sets(set.ownText());
				}
				for (Element probe : associated.getElementsByTag(PROBE)) {
					//prod.add_probes(probe.ownText());
				}
			}

			// Заполнение картинок
			HashMap<String, File> picFiles = new HashMap<>();
			List<File> allFiles = pi.getAll_file();
			for (File file : allFiles) {
				picFiles.put(file.getName(), file);
			}
			Element gallery = prodEl.getElementsByTag(GALLERY).first();
			boolean noMainPic = true;
			if (gallery != null) {
				for (Element picEl : gallery.getElementsByTag(PIC)) {
					String fileName = Strings.getFileName(picEl.attr("download"));
					File pic = picFiles.get(fileName);
					if (pic != null) {
						prod.setValue("gallery", pic);
						picFiles.remove(fileName);
						if (noMainPic) {
							try {
								File mainFile = new File(pic.getParentFile().getCanonicalPath() + "/main_" + pic.getName());
								FileUtils.copyFile(pic, mainFile);
								prod.setValue("main_pic", mainFile);
								/*
								ByteArrayOutputStream os = ResizeImagesFactory.resize(pic, 200, -1);
								File smallFile = new File(pic.getParentFile().getCanonicalPath() + "/small_" + pic.getName());
								FileUtils.writeByteArrayToFile(mainFile, os.toByteArray());
								prod.setValue("main_pic", mainFile);
								*/
								noMainPic = false;
							} catch (Exception e) {
								ServerLogger.error("resize error", e);
								info.pushLog("ОШИБКА! {}", e.getLocalizedMessage());
							}
						}
					}
				}
			}
			for (File htmlPic : pi.getAll_html_pic()) {
				picFiles.put(htmlPic.getName(), htmlPic);
			}
			for (File picFile : picFiles.values()) {
				prod.setValue("text_pics", picFile);
			}

			// Заполнение видео
			for (Element vidEl : gallery.getElementsByTag(VIDEO)) {
				prod.setValue("video", vidEl.ownText());
			}

			// Сохранение продукта
			executeCommandUnit(SaveItemDBUnit.get(prod));

			// Параметры XML
			String paramsXml = JsoupUtils.nodeHtml(prodEl, PARAMS_XML);
			if (StringUtils.isNotBlank(paramsXml)) {
				Item xmlItem = ItemUtils.newChildItem(ItemNames.PARAMS_XML, prod);
				xmlItem.setValue(ItemNames.params_xml_.XML, paramsXml);
				executeCommandUnit(SaveItemDBUnit.get(xmlItem));
			}

			// Создание тэгов
			/*
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
			*/

			// Исправить адреса картинок в HTML
			updatePics(prod, ItemNames.product_.TEXT, ItemNames.product_.TEXT_PICS);
			updatePics(prod, ItemNames.product_.DESCRIPTION, ItemNames.product_.TEXT_PICS);
			executeCommandUnit(SaveItemDBUnit.get(prod));
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