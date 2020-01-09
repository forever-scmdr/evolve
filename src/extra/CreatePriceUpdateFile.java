package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by user on 08.04.2019.
 */
public class CreatePriceUpdateFile extends IntegrateBase implements CatalogConst, ItemNames{
	//workbook styles
	private HSSFWorkbook workBook;
	private CellStyle headerStyle;
	private CellStyle noPriceStyle;
	private CellStyle noCodeStyle;

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		createWorkBook();
		int rowIndex = -1;
		rowIndex = createHeaders(rowIndex);
		setOperation("Загрузка списка товаров");
		List<Item> products = new ItemQuery(PRODUCT_ITEM).loadItems();
		pushLog("Найдено товаров:" + products.size());
		info.setToProcess(products.size());
		info.setProcessed(0);
		info.setLineNumber(rowIndex+1);
		Sheet sheet = workBook.getSheetAt(0);
		setOperation("Формирование файла");
		for(Item product : products) {
			Row row = sheet.createRow(++rowIndex);
			String code = product.getStringValue(CODE_PARAM);
			String price = product.outputValue(PRICE_PARAM).replaceAll("[^\\d,.]", "");
			CellStyle cellStyle = (StringUtils.isBlank(code))? noCodeStyle : (StringUtils.isBlank(price))? noPriceStyle : null;
			int colIdx = -1;
			row.createCell(++colIdx).setCellValue(code);
			row.createCell(++colIdx).setCellValue(product.getStringValue(NAME_PARAM));
			row.createCell(++colIdx).setCellValue(product.outputValue("price_old").replaceAll("[^\\d,.]", ""));
			row.createCell(++colIdx).setCellValue(price);
			row.createCell(++colIdx).setCellValue(product.getByteValue(AVAILABLE_PARAM, (byte)0) == 1? "+" : "-");
			List<Item> presents = ItemQuery.loadByParamValue(PRODUCT_PRESENT, product_present.PRODUCT_CODE, code);
			StringBuilder sb = new StringBuilder();
			int j = 0;
			for(Item present : presents){
				if(j > 0)sb.append(',');
				sb.append(present.getStringValue(product_present.PRESENT_CODE));
				String qty = present.outputValue("qty");
				if(StringUtils.isNotBlank(qty)){
					sb.append(':').append(qty);
				}
				j++;
			}
			row.createCell(++colIdx).setCellValue(sb.toString());
			if(cellStyle != null){
				for(int i = 0; i < colIdx+1; i++){
					row.getCell(i).setCellStyle(cellStyle);
				}
			}
		}
		setOperation("Запись файла");
		String fileName  = "pricelist-metabo.xls";
		FileOutputStream fileOutputStream = new FileOutputStream(AppContext.getFilesDirPath(false) + "/" + fileName);
		workBook.write(fileOutputStream);
		fileOutputStream.close();
		pushLog(fileName);
	}

	private int createHeaders(int rowIndex) {
		int idx = rowIndex+1;
		Sheet sh = workBook.createSheet();
		Row row = sh.createRow(idx);
		int colIdx = -1;

		row.createCell(++colIdx).setCellValue(UpdatePrices.CODE_HEADER);
		row.createCell(++colIdx).setCellValue(UpdatePrices.NAME_HEADER);
		row.createCell(++colIdx).setCellValue(UpdatePrices.PRICE_OLD_HEADER);
		row.createCell(++colIdx).setCellValue(UpdatePrices.PRICE_NEW_HEADER);
		row.createCell(++colIdx).setCellValue(UpdatePrices.AVAILABLE_HEADER);
		row.createCell(++colIdx).setCellValue(UpdatePrices.PRESENT_HEADER);

		for(int i = 0; i< colIdx+1; i++){
			row.getCell(i).setCellStyle(headerStyle);
		}

		return idx;
	}


	private void createWorkBook() {
		workBook = new HSSFWorkbook();

		headerStyle = workBook.createCellStyle();
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());

		noPriceStyle = workBook.createCellStyle();
		noPriceStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		noPriceStyle.setBorderLeft(BorderStyle.THIN);
		noPriceStyle.setBorderRight(BorderStyle.THIN);
		noPriceStyle.setBorderTop(BorderStyle.THIN);
		noPriceStyle.setBorderBottom(BorderStyle.THIN);
		noPriceStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

		noCodeStyle = workBook.createCellStyle();
		noCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		noCodeStyle.setBorderLeft(BorderStyle.THIN);
		noCodeStyle.setBorderRight(BorderStyle.THIN);
		noCodeStyle.setBorderTop(BorderStyle.THIN);
		noCodeStyle.setBorderBottom(BorderStyle.THIN);
		noCodeStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());
		
	}

	@Override
	protected void terminate() throws Exception {

	}
}
