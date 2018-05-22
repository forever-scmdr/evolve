package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 21.05.2018.
 */
public class MakeExcelPrice extends Command {

	private static final Format DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss");
	private CellStyle headerStyle = null;
	private CellStyle errorStyle = null;
	private static final Object MUTEX = new Object();



	@Override
	public ResultPE execute() throws Exception {
		synchronized (MUTEX){
			ItemQuery q = new ItemQuery(ItemNames.PRODUCT);
			List<Item> products = q.loadItems();

			int rowIdx = -1;
			int colIdx = -1;

			Workbook wb = new HSSFWorkbook();
			Sheet sheet = wb.createSheet("Цены за " + DATE_FORMAT.format(new Date()));
			Row row = sheet.createRow(++rowIdx);

			initCellStyles(wb);

			try {
				row.createCell(++colIdx).setCellValue("Арт.");
				sheet.setColumnWidth(colIdx, 15 * 256);
				row.createCell(++colIdx).setCellValue("Название");
				sheet.setColumnWidth(colIdx, 25 * 256);
				row.createCell(++colIdx).setCellValue("Цена");
				sheet.setColumnWidth(colIdx, 8 * 256);

				for (int i = 0; i <= colIdx; i++)
					row.getCell(i).setCellStyle(headerStyle);

				for (Item product : products) {
					colIdx = -1;
					row = sheet.createRow(++rowIdx);
					row.createCell(++colIdx).setCellValue(product.getStringValue(ItemNames.product.CODE));
					row.createCell(++colIdx).setCellValue(product.getStringValue(ItemNames.product.NAME));
					Cell price = row.createCell(++colIdx);
					BigDecimal cost = product.getDecimalValue(ItemNames.product.PRICE, BigDecimal.ZERO);
					price.setCellValue(cost.doubleValue());
					if (cost.doubleValue() == 0) {
						price.setCellStyle(errorStyle);
					}
				}

				FileOutputStream fileOutputStream = new FileOutputStream(AppContext.getFilesDirPath(false) + "/" + "pricelist.xls");
				wb.write(fileOutputStream);
				fileOutputStream.close();

			} catch (Exception e) {
				ServerLogger.error("Can not create Excel Pricelist", e);
				StackTraceElement[] stackTrace = e.getStackTrace();
				StringBuilder sb = new StringBuilder();
				for(StackTraceElement err : stackTrace){
					sb.append(err.toString()+"\n");
				}
				return getResult("error").addVariable("stack", sb.toString());
			}
		}
		return getResult("success");
	}

	private void initCellStyles(Workbook wb) {
		headerStyle = wb.createCellStyle();
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		Font font = wb.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		headerStyle.setFont(font);

		errorStyle = wb.createCellStyle();
		errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		errorStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	}
}
