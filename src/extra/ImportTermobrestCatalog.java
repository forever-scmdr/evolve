package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Params_xml;
import extra._generated.Product;
import extra._generated.Section;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Импорт экселевский файлов для термобреста
 */
public class ImportTermobrestCatalog extends IntegrateBase implements ItemNames {

	private static class DeviceDefinition {
		public final String idName;
		public final String caption;
		public final boolean useForPic;
		public final boolean useForDesc;
		public final HashMap<String, String> values;

		public DeviceDefinition(String idName, String caption, boolean useForPic, boolean useForDesc) {
			this.idName = idName;
			this.caption = caption;
			this.useForPic = useForPic;
			this.useForDesc = useForDesc;
			this.values = new HashMap<>();
		}
	}

	private static final String INTEGRATION_DIR = "integrate";

	private Item catalog;
	private ItemType productType;
	private ItemType paramsXmlType;
	private HashMap<String, DeviceDefinition> defs = new HashMap<>();


	private static final String ID_PREFIX = "ID_";
	private static final String CODE = "УНИКАЛЬНЫЙ КОД";
	private static final String ONE_C_NAME = "Номенклатура по 1С";
	private static final String ONE_C_CODE = "Артикул по 1 С";
	private static final String SHOW = "Показ";
	private static final String PRICE_RUB = "Цена, РФ";
	private static final String PRICE_RUB_OLD = "Цена, РФ (старая)";
	private static final String PRICE_EURO = "Цена, ЕВРО";
	private static final String PRICE_EURO_OLD = "Цена, ЕВРО (старая)";
	private static final String PRICE_BYN = "Цена, РБ";
	private static final String PRICE_BYN_OLD = "Цена, РБ (старая)";
	private static final String TAG = "Ярлык товара";
	private static final String LENGTH = "Длина, мм";
	private static final String WIDTH = "Ширина, мм";
	private static final String HEIGHT = "Высота, мм";
	private static final String WEIGHT = "Вес, кг";
	private static final String TEMPERATURE = "Температура рабочей среды, °С";
	private static final String POWER = "Номинальная мощность, Вт, не более";
	private static final String LOCATION_ON_TUBE = "Положение на трубопроводе";
	private static final String RESISTANCE = "Коэффициент сопротивления";
	private static final String DURABILITY = "Полный ресурс (до списания) включений, не менее";


	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemUtils.ensureSingleRootAnonymousItem(CATALOG, getInitiator());
		productType = ItemTypeRegistry.getItemType(PRODUCT);
		paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML);
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		final String integrationDirName = getVarSingleValueDefault("dir", INTEGRATION_DIR);
		File integrationDir = new File(AppContext.getRealPath(integrationDirName));
		if (!integrationDir.exists()) {
			info.addError("Не найдена директория интеграции " + integrationDirName, "init");
			return;
		}

		// Найти все файлы
		Collection<File> excels = FileUtils.listFiles(integrationDir, null, true);
		if (excels.size() == 0) {
			info.addError("Не найдены файлы в директории " + integrationDirName, "init");
			return;
		}
		info.setToProcess(excels.size());

		// Удалить каталог и создать новый

		Item oldCatalog = ItemQuery.loadSingleItemByName(ItemNames.CATALOG);
		if (oldCatalog != null)
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(oldCatalog));
		Item catalog = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.CATALOG, getInitiator());

		// Создание самих товаров

		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		for (File excel : excels) {
			if (!StringUtils.endsWithAny(excel.getName(), "xls", "xlsx", "txt", "csv"))
				continue;

			// Сначала создать объект определений для продукции
			POIExcelWrapper doc = POIUtils.openExcel(excel);
			Workbook wb = doc.getWorkbook();
			FormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = null;
			try {
				sheet = wb.getSheetAt(1);
			} catch (Exception e) { /**/ }
			if (sheet == null) {
				info.addError("Не найден второй лист в файле '" + excel.getName() + "'", "integrate");
				continue;
			}
			POIUtils.CellXY idCell = new POIUtils.CellXY(-1, -1);
			boolean idScanNotFinished = true;
			while (idScanNotFinished) {
				idCell = POIUtils.findNextContaining(sheet, eval, "ID_", idCell);
				if (idCell == null) {
					idScanNotFinished = false;
				} else {
					int currentRowNum = idCell.row;
					idCell = new POIUtils.CellXY(currentRowNum + 1, -1); // для того, чтобы поиск происходил со след. строки
					try {
						Row row = sheet.getRow(currentRowNum);
						String id = POIUtils.getCellAsString(row.getCell(0), eval);
						boolean inDesc = StringUtils.equals(POIUtils.getCellAsString(row.getCell(1), eval), "1");
						boolean useForPic = StringUtils.equals(POIUtils.getCellAsString(row.getCell(2), eval), "1");
						row = sheet.getRow(++currentRowNum);
						String caption = POIUtils.getCellAsString(row.getCell(1), eval);
						DeviceDefinition def = new DeviceDefinition(id, caption, useForPic, inDesc);
						defs.put(id, def);
						while (currentRowNum < sheet.getLastRowNum()) {
							row = sheet.getRow(++currentRowNum);
							String code = POIUtils.getCellAsString(row.getCell(0), eval);
							if (StringUtils.isBlank(code) || StringUtils.startsWithIgnoreCase(code, "ID"))
								break;
							String value = POIUtils.getCellAsString(row.getCell(1), eval);
							def.values.put(code, value);
						}
					} catch (Exception e) {
						ServerLogger.error("Error parsing Excel", e);
					}
				}
			}

			// Создадние раздела
			String sectionName = wb.getSheetName(0);
			final Section section = Section.get(ItemUtils.ensureSingleChild(ItemNames.SECTION, getInitiator(), catalog));
			section.set_name(sectionName);
			ArrayList<String> paramsInShort = new ArrayList<>();
			for (int i = 0; i < 20; i++) {
				DeviceDefinition def = defs.get("ID_" + i);
				if (def != null && def.useForDesc)
					paramsInShort.add(def.caption);
			}
			section.set_params_short(StringUtils.join(paramsInShort, "|"));
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section));

			// Закрыть файл
			doc.close();

			// Потом опять пройтись по его первому листу с помощью TableDataSource (создать девайсы)
			ExcelTableData data = new ExcelTableData(excel, "ID_1", ONE_C_CODE);
			TableDataRowProcessor proc = src -> {
				String code = "";
				try {
					String[] ids = new String[20];
					for (int i = 0; i < 20; i++) {
						String id = src.getValue(ID_PREFIX + i);
						if (StringUtils.isNotBlank(id))
							ids[i] = id;
					}
					code = StringUtils.join(ids, null);
					if (StringUtils.isNotBlank(code)) {
						Double showDbl = src.getDoubleValue(SHOW);
						boolean show = showDbl != null && showDbl > 0;

						// Создание товара
						extra._generated.Product prod =  Product.get(Item.newChildItem(productType, section));
						prod.set_code(code);

						// Основные прямые параметры
						prod.set_name(src.getValue(ONE_C_NAME));
						prod.set_vendor_code(src.getValue(ONE_C_CODE));
						prod.set_price_RUB(src.getDecimalValue(PRICE_RUB, 2));
						prod.set_price_RUB_old(src.getDecimalValue(PRICE_RUB_OLD, 2));
						prod.set_price_EUR(src.getDecimalValue(PRICE_EURO, 2));
						prod.set_price_EUR_old(src.getDecimalValue(PRICE_EURO_OLD, 2));
						prod.set_price(src.getDecimalValue(PRICE_BYN, 2));
						prod.set_price_old(src.getDecimalValue(PRICE_BYN_OLD, 2));
						prod.add_tag(src.getValue(TAG));

						// Создать XML для параметров
						XmlDocumentBuilder params = XmlDocumentBuilder.newDocPart();

						// Сначала заполнить параметры по таблице ID (второй лист)
						StringBuilder picName = new StringBuilder();
						for (int i = 0; i < 20; i++) {
							String idName = ID_PREFIX + i;
							String idValue = src.getValue(idName);
							if (StringUtils.isNotBlank(idValue)) {
								DeviceDefinition def = defs.get("ID_" + i);
								if (def != null) {
									String value = def.values.get(idValue);
									if (StringUtils.isNotBlank(value)) {
										params.startElement("parameter");
										params.addElement("name", def.caption);
										params.addElement("value", value);
										params.endElement();
										picName.append(idValue);
									}
								}
							}
						}
						prod.set_url(picName.toString() + ".jpg");

						// Потом заполнить параметры из основной таблицы на первом листе

						boolean afterTag = false;
						for (String headerName : src.getHeaders()) {
							if (afterTag) {
								String value = src.getValue(headerName);
								if (StringUtils.isNotBlank(value)) {
									params.startElement("parameter")
											.addElement("name", headerName)
											.addElement("value", value)
											.endElement();
								}
							}
							afterTag |= StringUtils.equalsIgnoreCase(headerName, TAG);
						}

						executeAndCommitCommandUnits(SaveItemDBUnit.get(prod));
						if (!show)
							executeAndCommitCommandUnits(ItemStatusDBUnit.hide(prod));
						Params_xml paramsXml = Params_xml.get(Item.newChildItem(paramsXmlType, prod));
						paramsXml.set_xml(params.toString());
						executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXml));

						info.increaseProcessed();
					}


				} catch (Exception e) {
					ServerLogger.error("line process error", e);
					info.addError("Ошибка формата строки (" + code + ")", src.getRowNum(), 0);
				}
			};
			data.iterate(proc);

			info.pushLog("Создание товаров завершено. Создание фильтров");
			info.setOperation("Создание фильтров");
			info.setProcessed(0);
			executeOtherIntegration(new CreateParametersAndFiltersCommand(this));
		}

	}

	@Override
	protected void terminate() throws Exception {

	}
}
