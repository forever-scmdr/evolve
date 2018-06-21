package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.Strings;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.filter.CriteriaDef;
import ecommander.model.filter.FilterDefinition;
import ecommander.model.filter.InputDef;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewItemTypeDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
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
	private static final String COMMENT_PATTERN = "<!--(?<comment>.*)-->";

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
				String name = getValue(NAME);
				String description = getValue(DESCRIPTION);
				String tag = getValue(TAG);
				String pic = getValue(PIC);
				String picsS = getValue(PICS);
				BigDecimal price = getCurrencyValue(PRICE);
				TreeSet<String> AdditionalHeaders = getHeaders();
				Iterator<String> headersIter = AdditionalHeaders.iterator();
				while (headersIter.hasNext()){
					String s = headersIter.next();
					if(!s.startsWith("#")) headersIter.remove();
				}
				String typeName = currentSection.getStringValue(ItemNames.section.NAME);
				ItemType paramsType = ItemTypeRegistry.getItemType(Strings.translit(typeName));
				if (StringUtils.isBlank(currentSection.getStringValue("params_filter"))) {
					createFilters(currentSection, AdditionalHeaders);
				}
				if (paramsType == null) {
					if (currentSection.getValue("params_filter") == null) {
						createFilters(currentSection, AdditionalHeaders);
					}
					paramsType = createItemType(Strings.translit(typeName), typeName, AdditionalHeaders);
					executeAndCommitCommandUnits(new SaveNewItemTypeDBUnit(paramsType));
					DataModelBuilder.newForceUpdate().tryLockAndReloadModel();
				}

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
				if(StringUtils.isNotBlank(pic)){
					picsArr.add(pic);
				}
				if (StringUtils.isNotBlank(picsS)) {
					String[] p = picsS.split(",");

					picsArr.add(pic);
					for (String s : p) {
						picsArr.add(s);
					}

				}
				if(picsArr.size() > 0){
					pics.put(code, picsArr);
				}
				if (StringUtils.isNotBlank(tag)) {
					String[] tags = tag.split(";");
					for (String t : tags) {
						product.setValueUnique("tag", t);
					}
				}
				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(product).noFulltextIndex().ingoreComputed());

				Item params = new ItemQuery("params").setParentId(product.getId(), false).loadFirstItem();
				if (params == null) {
					params = Item.newChildItem(ItemTypeRegistry.getItemType(Strings.translit(typeName)), product);

				}
				for (String sname : AdditionalHeaders) {
					String v = getValue(sname);
					if (StringUtils.isBlank(v)) continue;
					String paramName = sname.replace("#", "");
					paramName = Strings.translit(paramName);
					params.setValue(paramName, v);
				}
				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(params).noFulltextIndex().ingoreComputed());
			}

			@Override
			protected void processSheet() throws Exception {
				String sectionName = getSheetName();
				currentSection = ItemQuery.loadSingleItemByParamValue(ItemNames.SECTION, ItemNames.section.NAME, sectionName);
				if (currentSection == null) {
					currentSection = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.SECTION), catalog);
					currentSection.setValue(ItemNames.section.NAME, sectionName);
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(currentSection).noFulltextIndex().ingoreComputed());
				}
			}
		};
		return true;
	}

	private void createFilters(Item currentSection, TreeSet<String> headers) throws Exception {
		FilterDefinition filter = FilterDefinition.create("");
		filter.setRoot(Strings.translit(currentSection.getStringValue(ItemNames.section.NAME)));
		for (String paramName : headers) {
			paramName = paramName.replace("#", "");
			InputDef input = new InputDef("droplist", paramName, "", "");
			paramName = Strings.translit(paramName);
			filter.addPart(input);
			input.addPart(new CriteriaDef("=", paramName, DataType.Type.STRING.toString(), ""));
		}
		currentSection.setValue("params_filter", filter.generateXML());
		executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection));
	}

	private ItemType createItemType(String typeName, String typeCaption, TreeSet<String> headers) throws Exception {
		ItemType newClass = new ItemType(typeName, 0, typeCaption, "", "",
				"params", null, false, true, false, false);
		for (String paramName : headers) {
			paramName = paramName.replace("#", "");

			ParameterDescription pd;
			pd = new ParameterDescription(Strings.translit(paramName), 0, DataType.Type.STRING.toString(), false, 0,
					"", paramName, "", "", false, false, null, null);
			newClass.putParameter(pd);

		}
		executeAndCommitCommandUnits(new SaveNewItemTypeDBUnit(newClass));
		return newClass;
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
		info.setOperation("Интеграция завершена");
		price.close();
	}

	private void downloadPictures() throws Exception {

		for(Map.Entry<String, ArrayList<String>> e : pics.entrySet()){
			try {

			String code = e.getKey();
			Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product.CODE, code);
			ArrayList<String> urls = e.getValue();
			for(int i = 0; i < urls.size(); i++){
				URL pictureUrl = new URL(urls.get(i));
				if(i > 0){
					product.setValueUnique(ItemNames.product.GALLERY, pictureUrl);
				}else{
					product.setValue(ItemNames.product.MAIN_PIC, pictureUrl);
				}

			}
			executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex());
			}catch (Exception ex){
				info.addLog("Не удается загрузить изображение "+ e.getKey(), ex.getMessage());
				continue;
			}
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
