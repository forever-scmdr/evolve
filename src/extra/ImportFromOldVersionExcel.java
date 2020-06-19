package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.POIUtils;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ImportFromOldVersionExcel extends CreateParametersAndFiltersCommand implements CatalogConst {
	Workbook priceWB;
	Item catalog;
	Item currentSection;
	Item currentSubsection;
	Item currentProduct;
	private boolean newItemTypes = false;
	private HashSet<Long> sectionsWithNewItemTypes = new HashSet<>();
	private HashSet<String> duplicateCodes = new HashSet<>();
	private static final ItemType PRODUCT_ITEM_TYPE = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
	private static final ItemType PARAMS_XML_ITEM_TYPE = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
	private FormulaEvaluator eval = priceWB.getCreationHelper().createFormulaEvaluator();
	private HashMap<Integer, String> PARAM_INDEXES = new HashMap(){{PARAM_INDEXES.put(0, CODE_PARAM);}};
	private HashMap<Integer, String> AUX_PARAMS =  new HashMap<>();

	//File constants
	private final static HashMap<String, String> HEADER_PARAMS = new HashMap(){{
		HEADER_PARAMS.put("наименование", NAME_PARAM);
		HEADER_PARAMS.put("наличие", QTY_PARAM);
		HEADER_PARAMS.put("ед. изм", UNIT_PARAM);
		HEADER_PARAMS.put("кратность", MIN_QTY_PARAM);
		HEADER_PARAMS.put("цена 1", PRICE_PARAM);
		HEADER_PARAMS.put("себестоимость", PRICE_OPT_PARAM);
		HEADER_PARAMS.put("наценка 1", MARGIN_PARAM);
		HEADER_PARAMS.put("описание", DESCRIPTION_PARAM);
		HEADER_PARAMS.put("картинка", MAIN_PIC_PARAM);
		HEADER_PARAMS.put("pdf", "pdf");
	}};




	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		File excelFile = catalog.getFileValue("big_integration", AppContext.getFilesDirPath(false));
		if(!excelFile.isFile()){
			info.addError("Excel file does not exist.","");
			return false;
		}
		priceWB = POIUtils.openExcel(excelFile).getWorkbook();
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 1);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
		setOperation("Обновлние каталога");
		setProcessed(0);
		setLineNumber(0);
		//parsing from Excel
		info.setToProcess(getLinesCount(priceWB));
		parse(priceWB);
		priceWB.close();
		//creating filters and item types
		createFiltersAndItemTypes();
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 0);
		//indexation
		info.setOperation("Индексация названий товаров");
		LuceneIndexMapper.getSingleton().reindexAll();
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
		setOperation("Интеграция завершена");
	}

	private void parse(Workbook priceWB) throws Exception {
		eval =  priceWB.getCreationHelper().createFormulaEvaluator();
		for (int i = 0; i < priceWB.getNumberOfSheets(); i++) {
			Sheet sheet = priceWB.getSheetAt(i);
			info.setCurrentJob("Лист: " + sheet.getSheetName());
			Iterator<Row> rows = sheet.iterator();
			HashMap<Integer, String> cellValues = new HashMap<Integer, String>(2);
			cellValues.put(0, CODE_PARAM);
			cellValues.put(1, NAME_PARAM);
			while (rows.hasNext()){
				Row row = rows.next();
				int rowIdx = row.getRowNum();
				info.setLineNumber(rowIdx + 1);
				//SECTION
				if(row.getLastCellNum() == 1){
					String sectionCode = getCellAsString(row.getCell(0));
					String sectionName = getCellAsString(row.getCell(1));

					boolean isNew = false;

					//section exists
					currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, sectionCode);

					//section not exists
					if(currentSection == null){
						if(sectionCode.indexOf('.') == -1){
							currentSection = ItemUtils.newChildItem(SECTION_ITEM, catalog);
						}else {
							String parentCode = StringUtils.substringBeforeLast(sectionCode, ".");
							Item parent = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, parentCode);
							currentSection = ItemUtils.newChildItem(SECTION_ITEM, parent);
						}
						isNew = !"Прочее".equals(sectionName);
					}

					currentSection.setValue(CODE_PARAM, sectionCode);
					currentSection.setValue(NAME_PARAM, sectionName);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noFulltextIndex());
					if(isNew) sectionsWithNewItemTypes.add(currentSection.getId());
				}

				//HEADERS
				else if(StringUtils.isBlank(getCellAsString(row.getCell(0)))){
					initHeaders(row);
				}

				//PRODUCT
				else{
					Item product;
					Item paramsXml;
					Iterator<Cell> iterator = row.cellIterator();
					while (iterator.hasNext()){
						Cell cell = iterator.next();
						int index = cell.getColumnIndex();
						String paramName = PARAM_INDEXES.get(index);
						XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();

						String cellValue = getCellAsString(cell);

						if(paramName == null){
							paramName = AUX_PARAMS.get(index);
							if(paramName == null) continue;
							xml.startElement("parameter")
									.startElement("name")
									.addText(firstUpperCase(paramName))
									.endElement()
									.startElement("value")
									.addText(cellValue)
									.endElement()
									.endElement();
						}else if(CODE_PARAM.equals(paramName)){
							product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, cellValue);
							if(product == null){
								product = ItemUtils.newChildItem(PRODUCT_ITEM, currentSection);
								product.setValue(CODE_PARAM, cellValue);
							}
						}else{

						}
					}
				}
			}
		}
	}

	private void initHeaders(Row row) {
		Iterator<Cell> iterator = row.cellIterator();
		while (iterator.hasNext()){
			Cell cell = iterator.next();
			String cellValue = getCellAsString(cell).toLowerCase().trim();
			if(StringUtils.isBlank(cellValue)) continue;
			String paramName = HEADER_PARAMS.get(cellValue);
			if(StringUtils.isBlank(paramName)){
				AUX_PARAMS.put(cell.getColumnIndex(), cellValue);
			}else{
				PARAM_INDEXES.put(cell.getColumnIndex(), cellValue);
			}
		}
	}

	private String getCellAsString(Cell cell){
		return  POIUtils.getCellAsString(cell, eval);
	}

	private int getLinesCount(Workbook priceWB) {
		int rows = 0;
		for (int i = 0; i < priceWB.getNumberOfSheets(); i++) {
			Sheet sheet = priceWB.getSheetAt(i);
			int first = sheet.getFirstRowNum();
			int last = sheet.getLastRowNum();
			rows += last - first + 1;
		}
		return rows;
	}

	private void createFiltersAndItemTypes() throws Exception {
		if (sectionsWithNewItemTypes.size() == 0) return;
		setOperation("Создание классов и фильтров");
		List<Item> sections = ItemQuery.loadByIdsLong(sectionsWithNewItemTypes);
		doCreate(sections);
	}

	private String firstUpperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
