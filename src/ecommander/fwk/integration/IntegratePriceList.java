package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.filter.CriteriaDef;
import ecommander.model.filter.FilterDefinition;
import ecommander.model.filter.InputDef;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewItemTypeDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

/**
 * Created by user on 27.03.2018.
 */
public class IntegratePriceList extends IntegrateBase {
	//headers
	private static final String CODE = "code";
	private static final String NAME = "Наменование";
	private static final String DESCRIPTION = "Описание {html}";
	private static final String PIC = "Картинка";
	private static final String PICS = "Галерея";
	private static final String PRICE = "Цена";
	private static final String TAG = "Тег";

	private ExcelPriceList price;
	private Item currentSection;
	private Item catalog;
	private HashMap<String, ArrayList<String>> pics = new HashMap<>();
	private List<Item> sections = new ArrayList<>();

	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		if (catalog == null)
			return false;
		File priceFile = catalog.getFileValue(ItemNames.catalog.INTEGRATION, AppContext.getFilesDirPath(false));
		price = new ExcelPriceList(priceFile, CODE, NAME, DESCRIPTION, PIC, PICS, PRICE, TAG) {
			@Override
			protected void processRow() throws Exception {
				String code = getValue(CODE);
				UniqueArrayList<String> AdditionalHeaders = null;
				switch (code) {
					case "Раздел:":
						String sectionName = getValue(NAME);
						String sectionCode = getValue(DESCRIPTION);
						String sectionParentCode = getValue(PIC);
						if (StringUtils.isBlank(sectionCode))
							throw new Exception(sectionName + " section code missing");
						Item section = ItemQuery.loadSingleItemByParamValue(ItemNames.SECTION, ItemNames.section.CATEGORY_ID, sectionCode);
						if (section == null)
							section = ItemQuery.loadSingleItemByParamValue(ItemNames.SECTION, ItemNames.section.NAME, sectionName);
						if (section == null) {
							Item parentSection = (StringUtils.isBlank(sectionParentCode)) ? currentSection : ItemQuery.loadSingleItemByParamValue(ItemNames.SECTION, ItemNames.section.CATEGORY_ID, sectionParentCode);
							ItemType sectionType = ItemTypeRegistry.getItemType(ItemNames.SECTION);
							section = Item.newChildItem(sectionType, parentSection);
						}
						section.setValue(ItemNames.section.CATEGORY_ID, sectionCode);
						section.setValue(ItemNames.section.NAME, sectionName);
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(section).noFulltextIndex().ingoreComputed());
						currentSection = section;
						//Чтобы не трогать разделы, которых нет в файле.
						sections.add(section);
						break;
					case "code":
						price.initSectionHeaders(CODE, NAME, DESCRIPTION, PIC, PICS, PRICE, TAG);
						break;
					default:

						String name = getValue(NAME);
						String description = getValue(DESCRIPTION);
						String tag = getValue(TAG);
						String pic = getValue(PIC);
						String picsS = getValue(PICS);
						BigDecimal price = getCurrencyValue(PRICE);

						Item productItem = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product.CODE, code);
						productItem = (productItem == null) ? Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT), currentSection) : productItem;
						Product product = Product.get(productItem);
						product.set_code(code);
						product.set_name(name);
						if (StringUtils.isNotBlank(description)) {
							product.set_text(description);
						}
						if (price != null) {
							product.set_price(price);
						}

						ArrayList<String> picsArr = new ArrayList<>();
						if (StringUtils.isNotBlank(pic)) {
							picsArr.add(pic);
						}
						if (StringUtils.isNotBlank(picsS)) {
							String[] p = picsS.split(",");

							picsArr.add(pic);
							for (String s : p) {
								picsArr.add(s);
							}

						}
						if (picsArr.size() > 0) {
							pics.put(code, picsArr);
						}
						if (StringUtils.isNotBlank(tag)) {
							String[] tags = tag.split(";");
							for (String t : tags) {
								product.setValueUnique(ItemNames.product.TAG, t);
							}
						}
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(product).noFulltextIndex().ingoreComputed());
						AdditionalHeaders = getHeaders();
						XmlDocumentBuilder paramsXML = XmlDocumentBuilder.newDocPart();
						for (String sName : AdditionalHeaders) {
							String v = getValue(sName);
							if (!StringUtils.startsWith(sName, "#")) continue;
							String paramName = sName.replace("#", "").replaceAll("\\s+", " ").trim();
							v = (StringUtils.isBlank(v)) ? "" : v.replaceAll("\\s+", " ").trim();

							paramsXML.startElement("parameter")
									.startElement("name").addText(paramName).endElement()
									.startElement("value").addText(v).endElement()
									.endElement();

						}
						Item paramsXMLItem = new ItemQuery("params_xml").setParentId(product.getId(), false).loadFirstItem();
						paramsXMLItem = (paramsXMLItem != null)? paramsXMLItem : Item.newChildItem(ItemTypeRegistry.getItemType("params_xml"), product);
						paramsXMLItem.setValue("xml", paramsXML.toString());
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(paramsXMLItem).noFulltextIndex().ingoreComputed());
				}
			}

			@Override
			protected void processSheet() throws Exception {
				String sectionName = getSheetName();
				String[] tmp = StringUtils.split(sectionName, '|');
				sectionName = tmp[0].trim();
				String sectionCode = null;
				if (tmp.length == 2) {
					sectionCode = tmp[1].trim();
				}
				currentSection = ItemQuery.loadSingleItemByParamValue(ItemNames.SECTION, ItemNames.section.NAME, sectionName);
				if (currentSection == null) {
					currentSection = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.SECTION), catalog);
					currentSection.setValue(ItemNames.section.NAME, sectionName);
					if (StringUtils.isNotBlank(sectionCode))
						currentSection.setValue(ItemNames.section.CATEGORY_ID, sectionCode);
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(currentSection).noFulltextIndex().ingoreComputed());
				} else if (StringUtils.isBlank(currentSection.getStringValue(ItemNames.section.CATEGORY_ID)) && StringUtils.isNotBlank(sectionCode)) {
					currentSection.setValue(ItemNames.section.CATEGORY_ID, sectionCode);
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(currentSection).noFulltextIndex().ingoreComputed());
				}
			}
		};
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Создание прайс-листа");
		info.setProcessed(0);
		info.setLineNumber(0);
		info.setToProcess(price.getTotalLinesCount());
		price.iterate();
		info.setOperation("Скачивание и прикрепление изображений");
		downloadPictures();
		refreshFiltersAndItemTypes();
		info.setOperation("Индексация названий товаров");
		//LuceneIndexMapper.getSingleton().reindexAll();
		info.setOperation("Интеграция завершена");
		price.close();
	}

	private void refreshFiltersAndItemTypes() throws Exception{
		sections = new ItemQuery(ItemNames.SECTION).loadItems();
		info.setOperation("Создание классов и фильтров");
		info.setToProcess(sections.size());
		info.setProcessed(0);
		for (Item section : sections) {
			List<Item> products = new ItemQuery(ItemNames.PRODUCT).setParentId(section.getId(), false).loadItems();
			if (products.size() > 0) {

				// Анализ параметров продуктов
				Params params = new Params(section.getStringValue(ItemNames.section.NAME), "s" + section.getId());
				for (Item product : products) {
					List<Item> oldParams = new ItemQuery(ItemNames.PARAMS).setParentId(product.getId(), false).loadItems();
					Item paramsXml = new ItemQuery(ItemNames.PARAMS_XML).setParentId(product.getId(), false).loadFirstItem();
					if (paramsXml != null) {
						for (Item oldParam : oldParams) {
							executeAndCommitCommandUnits(ItemStatusDBUnit.delete(oldParam));
						}
						String xml = "<params>" + paramsXml.getStringValue(ItemNames.params_xml.XML) + "</params>";
						Document paramsTree = Jsoup.parse(xml, "localhost", Parser.xmlParser());
						Elements paramEls = paramsTree.getElementsByTag("parameter");
						for (Element paramEl : paramEls) {
							String caption = StringUtils.trim(paramEl.getElementsByTag("name").first().ownText());
							String value = StringUtils.trim(paramEl.getElementsByTag("value").first().ownText());
							if (StringUtils.isNotBlank(caption)) {
								params.addParameter(caption, value);
							}
						}
					}
				}
				executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(10, null));

				// Создание фильтра
				String className = "p" + section.getId();
				String classCaption = section.getStringValue(ItemNames.section.NAME);
				// Создать фильтр и установить его в айтем
				FilterDefinition filter = FilterDefinition.create("");
				filter.setRoot(className);
				for (String paramName : params.paramTypes.keySet()) {
					if (params.notInFilter.contains(paramName))
						continue;
					String caption = params.paramCaptions.get(paramName);
					String unit = params.paramUnits.get(paramName);
					InputDef input = new InputDef("droplist", caption, unit, "");
					filter.addPart(input);
					input.addPart(new CriteriaDef("=", paramName, params.paramTypes.get(paramName), ""));
				}
				section.setValue(ItemNames.section.PARAMS_FILTER, filter.generateXML());
				executeAndCommitCommandUnits(SaveItemDBUnit.get(section));

				// Создать класс для продуктов из этого раздела
				ItemType newClass = new ItemType(className, 0, classCaption, "", "",
						"params", null, false, true, false, false);
				for (String paramName : params.paramTypes.keySet()) {
					String type = params.paramTypes.get(paramName).toString();
					String caption = params.paramCaptions.get(paramName);
					String unit = params.paramUnits.get(paramName);
					newClass.putParameter(new ParameterDescription(paramName, 0, type, false, 0,
							"", caption, unit, "", false, false, null, null));
				}
				executeAndCommitCommandUnits(new SaveNewItemTypeDBUnit(newClass));

			}
			info.increaseProcessed();
		}

		DataModelBuilder.newForceUpdate().tryLockAndReloadModel();

		info.setOperation("Заполнение параметров товаров");
		info.setToProcess(sections.size());
		info.setProcessed(0);
		for (Item section : sections) {
			String className = "p" + section.getId();
			ItemType paramDesc = ItemTypeRegistry.getItemType(className);
			List<Item> products = new ItemQuery(ItemNames.PRODUCT).setParentId(section.getId(), false).loadItems();
			if (products.size() > 0) {
				for (Item product : products) {
					Item paramsXml = new ItemQuery(ItemNames.PARAMS_XML).setParentId(product.getId(), false).loadFirstItem();
					if (paramsXml != null) {
						String xml = "<params>" + paramsXml.getStringValue(ItemNames.params_xml.XML) + "</params>";
						Document paramsTree = Jsoup.parse(xml, "localhost", Parser.xmlParser());
						Elements paramEls = paramsTree.getElementsByTag("parameter");
						Item params = Item.newChildItem(paramDesc, product);
						for (Element paramEl : paramEls) {
							String name = StringUtils.trim(paramEl.getElementsByTag("name").first().ownText());
							name = Strings.createXmlElementName(name);
							String value = StringUtils.trim(paramEl.getElementsByTag("value").first().ownText());
							Pair<DataType.Type, String> valuePair = Params.testValueHasUnit(value);
							if (StringUtils.isNotBlank(valuePair.getRight())) {
								value = value.split("\\s")[0];
							}
							if (paramDesc.hasParameter(name)) {
								params.setValueUI(name, value);
							} else {
								info.pushLog("No parameter {} in section {}", name, section.getStringValue("name"));
							}
						}
						executeAndCommitCommandUnits(SaveItemDBUnit.get(params));
					}
				}
			}
			info.increaseProcessed();
		}
	}

	private void downloadPictures() throws Exception {
		info.setSheetName("Документ закончен. Постобработка.");

		for (Map.Entry<String, ArrayList<String>> e : pics.entrySet()) {
			try {

				String code = e.getKey();
				Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product.CODE, code);
				ArrayList<String> urls = e.getValue();
				for (int i = 0; i < urls.size(); i++) {
					URL pictureUrl = new URL(urls.get(i));
					if (i > 0) {
						product.setValueUnique(ItemNames.product.GALLERY, pictureUrl);
					} else {
						product.setValue(ItemNames.product.MAIN_PIC, pictureUrl);
					}

				}
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex());
			} catch (Exception ex) {
				info.addLog("Не удается загрузить изображение " + e.getKey(), ex.getMessage());
				continue;
			}
		}
	}

	@Override
	protected void terminate() throws Exception {

	}

	/**
	 * Типы и названия параметров
	 * Тип параметра может быть одним из трех
	 * integer
	 * double
	 * string
	 * @author E
	 *
	 */
	public static class Params {
		private final String className;
		private final String classCaption;
		private LinkedHashMap<String, DataType.Type> paramTypes = new LinkedHashMap<>();
		private LinkedHashMap<String, String> paramCaptions = new LinkedHashMap<>();
		private HashMap<String, String> paramUnits = new HashMap<>();
		private HashSet<String> notInFilter = new HashSet<>();
		private static final NumberFormat eng_format = NumberFormat.getInstance(new Locale("en"));
		private static final NumberFormat ru_format = NumberFormat.getInstance(new Locale("ru"));

		public Params(String caption, String className) {
			this.classCaption = caption;
			this.className = Strings.createXmlElementName(className);
		}

		public void addParameter(String name, String value) {
			String paramName = Strings.createXmlElementName(name);
			if (!paramTypes.containsKey(paramName)) {
				paramTypes.put(paramName, DataType.Type.INTEGER);
				paramCaptions.put(paramName, name);
			}
			DataType.Type currentType = paramTypes.get(paramName);
			Pair<DataType.Type, String> test = testValueHasUnit(value);
			if (currentType.equals(DataType.Type.INTEGER) && test.getLeft() != DataType.Type.INTEGER) {
				paramTypes.put(paramName, test.getLeft());
			} else if (currentType.equals(DataType.Type.DOUBLE) && test.getLeft() == DataType.Type.STRING) {
				paramTypes.put(paramName, DataType.Type.STRING);
			}
			if (test.getRight() != null) {
				paramUnits.put(paramName, test.getRight());
			}
		}

		private void addNotInFilter(String name) {
			String paramName = Strings.createXmlElementName(name);
			notInFilter.add(paramName);
		}

		private static boolean testDouble(String value) {
			ParsePosition pp = new ParsePosition(0);
			ru_format.parse(value, pp);
			if (pp.getIndex() != value.length()) {
				pp = new ParsePosition(0);
				eng_format.parse(value, pp);
				if (pp.getIndex() != value.length())
					return false;
			}
			return true;
		}

		private static Pair<DataType.Type, String> testValueHasUnit(String value) {
			try {
				Integer.parseInt(value);
				return new Pair<>(DataType.Type.INTEGER, null);
			} catch (NumberFormatException nfe1) {
				if (testDouble(value)) {
					return new Pair<>(DataType.Type.DOUBLE, null);
				} else {
					if (value.matches("^-?[0-9]+[\\.,]?[0-9]*\\s+[^-\\s]+$")) {
						String[] parts = value.split("\\s");
						String numStr = parts[0];
						String unit = parts.length > 1 ? parts[1] : null;
						try {
							Integer.parseInt(numStr);
							return new Pair<>(DataType.Type.INTEGER, unit);
						} catch (NumberFormatException nfe2) {
							if (testDouble(numStr)) {
								return new Pair<>(DataType.Type.DOUBLE, unit);
							} else {
								return new Pair<>(DataType.Type.STRING, null);
							}
						}
					} else {
						return new Pair<>(DataType.Type.STRING, null);
					}
				}
			}
		}
	}

}
