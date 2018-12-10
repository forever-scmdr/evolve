package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by user on 05.12.2018.
 */
public class CreateExcelPriceList extends IntegrateBase implements CatalogConst {
	//workbook styles
	private HSSFWorkbook workBook;
	private CellStyle headerStyle;
	private CellStyle noPriceStyle;
	private CellStyle noCodeStyle;
	private CellStyle sectionStyle;
	//file Constants
	protected static final String CODE_FILE = "Код";
	protected static final String NAME_FILE = "Название";
	protected static final String PRICE_FILE = "Цена";
	protected static final String QTY_FILE = "Количество";
	protected static final String AVAILABLE_FILE = "Наличие";


	@Override
	protected void integrate() throws Exception {
		setOperation("Загрузка каталога товаров");
		Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		if (catalog == null) {
			info.pushLog("Каталог не найден");
			return;
		}
		ItemQuery q = new ItemQuery(SECTION_ITEM).setParentId(catalog.getId(), false);
		List<Item> sections = q.loadItems();
		info.pushLog("Обнаружено разделов первого уровня: " + sections.size());

		workBook = new HSSFWorkbook();
		initCellStyles();

		for (Item section : sections) {
			Sheet sh = initializeSheet(section);
			long sectionId = section.getId();
			int rowIndex = processProducts(sh, 0, sectionId);
			processSubsections(sh, rowIndex, sectionId);
		}
		setOperation("Запись файла");
		FileOutputStream fileOutputStream = new FileOutputStream(AppContext.getFilesDirPath(false) + "/" + "pricelist.xls");
		workBook.write(fileOutputStream);
		fileOutputStream.close();
	}



	private Sheet initializeSheet(Item section) {
		String sheetName = section.getValue(NAME_PARAM) + " | " + section.getStringValue(CATEGORY_ID_PARAM);
		setOperation(section.getValue(NAME_PARAM)+". Обработка подразделов.");
		Sheet sh = workBook.createSheet(sheetName);
		int rowIdx = -1;
		int colIdx = -1;
		Row row = sh.createRow(++rowIdx);
		row.createCell(++colIdx).setCellValue(CODE_FILE);
		row.getCell(colIdx).setCellStyle(headerStyle);
		sh.setColumnWidth(colIdx, 20 * 256);
		row.createCell(++colIdx).setCellValue(NAME_FILE);
		row.getCell(colIdx).setCellStyle(headerStyle);
		sh.setColumnWidth(colIdx, 75 * 256);
		row.createCell(++colIdx).setCellValue(PRICE_FILE);
		row.getCell(colIdx).setCellStyle(headerStyle);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(QTY_FILE);
		row.getCell(colIdx).setCellStyle(headerStyle);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(AVAILABLE_FILE);
		row.getCell(colIdx).setCellStyle(headerStyle);
		sh.setColumnWidth(colIdx, 25 * 256);
		return sh;
	}

	private int processProducts(Sheet sh, int rowIndex, long sectionId) throws Exception {
		List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(sectionId, false).loadItems();
		int rowI = rowIndex;
		info.pushLog("Найдено продуктов:"+products.size());
		info.setToProcess(products.size());
		info.setProcessed(0);
		for(Item product : products){
			int colIdx = -1;
			Row row = sh.createRow(++rowI);
			CellStyle cellStyle = chooseCellStyle(product);
			BigDecimal price = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);
			String priceValue = (price.doubleValue() > 0.001)? price.toString() : "";
			String qtyValue = (product.getDoubleValue(QTY_PARAM) != null)? String.valueOf(product.getDoubleValue(QTY_PARAM)) : "";

			row.createCell(++colIdx).setCellValue(product.getStringValue(CODE_PARAM, ""));
			row.createCell(++colIdx).setCellValue(product.getStringValue(NAME_PARAM, ""));
			row.createCell(++colIdx).setCellValue(priceValue);
			row.createCell(++colIdx).setCellValue(qtyValue);
			row.createCell(++colIdx).setCellValue(String.valueOf(product.getByteValue(AVAILABLE_PARAM, (byte)0)));

			if(cellStyle != null){
				for(int i = 0; i < colIdx+1; i++){
					row.getCell(i).setCellStyle(cellStyle);
				}
			}
			info.increaseProcessed();
		}
		return rowI;
	}

	private CellStyle chooseCellStyle(Item product) {
		if(StringUtils.isBlank(product.getStringValue(CODE_PARAM))) return noCodeStyle;
		if(product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO).doubleValue() < 0.001) return noPriceStyle;
		return null;
	}

	private int processSubsections(Sheet sh, int rowIndex, long sectionId) throws Exception {
		List<Item> sections = new ItemQuery(SECTION_ITEM).setParentId(sectionId, false).loadItems();
		int rowI = rowIndex;
		for(Item section : sections){
			info.pushLog(section.getStringValue(NAME_PARAM));
			info.setProcessed(0);
			Row row = sh.createRow(++rowI);
			int colIdx = -1;
			row.createCell(++colIdx).setCellStyle(sectionStyle);
			row.createCell(++colIdx).setCellValue(section.getStringValue(NAME_PARAM));
			row.getCell(colIdx).setCellStyle(sectionStyle);
			rowI = processProducts(sh, rowI, section.getId());
			processSubsections(sh, rowI, section.getId());
		}
		return rowI;
	}

	private void initCellStyles() {
		headerStyle = workBook.createCellStyle();
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(IndexedColors.YELLOW1.getIndex());

		noPriceStyle = workBook.createCellStyle();
		noPriceStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		noPriceStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

		noCodeStyle = workBook.createCellStyle();
		noCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		noCodeStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());

		sectionStyle = workBook.createCellStyle();
		sectionStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		sectionStyle.setAlignment(HorizontalAlignment.CENTER);
		sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sectionStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
	}

	@Override
	protected void terminate() throws Exception {

	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}
}
