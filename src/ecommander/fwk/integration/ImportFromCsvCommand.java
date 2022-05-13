package ecommander.fwk.integration;

import com.google.common.base.Charsets;
import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;

public class ImportFromCsvCommand extends IntegrateBase implements CatalogConst {

	private static final String ORPHANS_SECTION = "Товары вне разделов";
	private static final HashMap<String, String> HEADER_PARAMS = new HashMap<>();
	private static final HashMap<String, String> LABEL_PARAMS = new HashMap<>();
	private static final HashMap<String, String> LABEL_VALUES = new HashMap<>();
	private static final HashMap<String, String> SEO_PARAMS = new HashMap<>();
	private static final String SECTION_HEADER = "folder : Категория";

	static {
		LABEL_VALUES.put("new", "Новинка");
		LABEL_VALUES.put("special", "Спецпредложение");
		LABEL_VALUES.put("note", "Анонс товара");

		LABEL_PARAMS.put("new : Новинка", "label:new");
		LABEL_PARAMS.put("special : Спецпредложение", "label:special");
		LABEL_PARAMS.put("note : Анонс товара", "label:note");
		LABEL_PARAMS.put("tags : Теги", "tag");

		HEADER_PARAMS.put(NAME, "name : Название");
		//HEADER_PARAMS.put(TEXT_PARAM, "body : Описание");
		HEADER_PARAMS.put(UNIT_PARAM, "unit : Единица измерения");
		HEADER_PARAMS.put(VENDOR_CODE_PARAM, "article : Артикул");
		HEADER_PARAMS.put(CODE_PARAM, "code_1c : 1C");
		HEADER_PARAMS.put(QTY_PARAM, "amount : Количество");
		HEADER_PARAMS.put(PRICE_PARAM, "price : Цена");
		HEADER_PARAMS.put(PRICE_OLD_PARAM, "price_old : Старая цена");
		HEADER_PARAMS.put(VENDOR_PARAM, "vendor : Производитель");
		//HEADER_PARAMS.put(MAIN_PIC_PARAM, "image : Иллюстрация");

		SEO_PARAMS.put(ItemNames.seo_.H1, "seo_h1 : Заголовок (H1)");
		SEO_PARAMS.put(ItemNames.seo_.KEYWORDS, "seo_keywords : Keywords");
		SEO_PARAMS.put(ItemNames.seo_.DESCRIPTION, "seo_description : Description");
		SEO_PARAMS.put(ItemNames.seo_.TITLE, "seo_title : Title");
	}

	private CharSeparatedTxtTableData data;
	private Item catalog;


	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		File csvData = catalog.getFileValue(ItemNames.catalog_.BIG_INTEGRATION, AppContext.getFilesDirPath(catalog.isFileProtected()));
		if (!csvData.isFile()) {
			addError("no CSV catalog found!", "loading catalog file attempt");
			return false;
		}
		data = new CharSeparatedTxtTableData(csvData.getAbsolutePath(), Charsets.UTF_8, true, HEADER_PARAMS.get(CODE_PARAM), HEADER_PARAMS.get(NAME));
		return true;
	}


	private class CsvRowToProductProcessor implements TableDataRowProcessor {

		private HashMap<String, Item> cacheForSecs = new HashMap<>();

		@Override
		public void processRow(TableDataSource src) throws Exception {
			if (src.getRowNum() == 0) return;
			info.setLineNumber(src.getRowNum());
			String code = getValueByParamName(CODE_PARAM, src);
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code, Item.STATUS_HIDDEN, Item.STATUS_NORMAL);
			if (product == null) {
				String parentSectionName = src.getValue(SECTION_HEADER);
				product = createProduct(code, parentSectionName);
			}

			populateProduct(product, src);
			processLabelsAndTags(product, src);

			executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreUser().noFulltextIndex());

			Item seo = getSeo(product);
			populateSeo(seo, src);

			executeAndCommitCommandUnits(SaveItemDBUnit.get(seo).ignoreUser().noFulltextIndex());
			info.increaseProcessed();
		}

		private Item getSeo(Item product) throws Exception {
			Item seo = ItemQuery.loadSingleItemByParamValue(ItemNames.SEO, ItemNames.seo_.KEY_UNIQUE, product.getKeyUnique());
			return seo == null ? ItemUtils.newChildItem(ItemNames.SEO, product) : seo;
		}

		private Item getSection(String name) throws Exception {
			name = name.replaceAll("^\\s+|\\s\\s+|\\s+$", "");

			String processedName = name.toLowerCase();

			//if cache contains section - return section
			Item section = cacheForSecs.get(processedName);
			if (section != null) return section;

			//if DB contains section - put into cache and return section
			section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, NAME, name, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			if (section != null) {
				cacheForSecs.put(processedName, section);
				return section;
			}

			//create and put section into cache if not exists
			section = ItemUtils.newChildItem(SECTION_ITEM, catalog);
			section.setValueUI(NAME, name);
			if (ORPHANS_SECTION.equals(name)) {
				executeCommandUnit(ItemStatusDBUnit.hide(section).ignoreUser().noFulltextIndex());
			}
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().ignoreUser());
			cacheForSecs.put(processedName, section);
			return section;
		}

		private void processLabelsAndTags(Item product, TableDataSource source) throws Exception {
			for (String k : LABEL_PARAMS.keySet()) {
				String v = LABEL_PARAMS.get(k);
				String cellValue = source.getValue(k);
				String[] arr = StringUtils.split(v, ':');
				if (arr[0].equals("label")) {
					String label = LABEL_VALUES.get(arr[1]);
					if (cellValue == "1") {
						product.setValueUI("label", label);
					} else {
						product.removeEqualValue("label", label);
					}
				}
				if (TAG_PARAM.equals(arr[0])) {
					product.clearValue(TAG_PARAM);
					String[] tags = StringUtils.split(cellValue, '|');
					for (String tag : tags) {
						product.setValueUI(TAG_PARAM, tag);
					}
				}
			}
		}

		private Item createProduct(String code, String parentSectionName) throws Exception {
			Item parentSection = StringUtils.isBlank(parentSectionName) ? getSection(ORPHANS_SECTION) : getSection(parentSectionName);
			Item product = ItemUtils.newChildItem(PRODUCT_ITEM, parentSection);
			return product;
		}

		private void populateProduct(Item product, TableDataSource source) throws Exception {
			for (String k : HEADER_PARAMS.keySet()) {
				product.setValueUI(k, getValueByParamName(k, source));
			}
		}

		private void populateSeo(Item seo, TableDataSource source) throws Exception {
			for (String k : SEO_PARAMS.keySet()) {
				String colName = SEO_PARAMS.get(k);
				seo.setValueUI(k, source.getValue(colName));
			}
		}

		private String getValueByParamName(String paramName, TableDataSource src) {
			String colName = HEADER_PARAMS.get(paramName);
			return src.getValue(colName);
		}
	}

	@Override
	protected void integrate() throws Exception {
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 1);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra().ignoreUser());
		setOperation("Обновлние каталога");
		setProcessed(0);
		setLineNumber(0);

		data.iterate(new CsvRowToProductProcessor());

		info.setOperation("Индексация названий товаров");
		LuceneIndexMapper.getSingleton().reindexAll();
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra().ignoreUser());
		setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
