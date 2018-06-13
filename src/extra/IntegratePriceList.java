package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.UniqueArrayList;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
						break;
					case "code":
						price.initSectionHeaders(CODE, NAME, DESCRIPTION, PIC, PICS, PRICE, TAG);
						//AdditionalHeaders = getHeaders();
						//setCurrentParamsType(AdditionalHeaders);
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
		updateFiltersAndItemtypes();
		info.setOperation("Интеграция завершена");
		price.close();
	}

	private void updateFiltersAndItemtypes() throws Exception {

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
}
