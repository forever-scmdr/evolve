package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.JsoupUtils;
import ecommander.model.ItemType;
import ecommander.model.User;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class ExcelDocumentGenerator {
	//workbook styles
	private XSSFWorkbook workBook;

	private Sheet sheet;
	private Row row;
	int rowIdx = 0;
	int colIdx = 0;
	private CellStyle headerStyle;
	private CellStyle normalStyle;
	private CellStyle queryStyle;
	private CellStyle noPriceStyle;
	private CellStyle noCodeStyle;
	private CellStyle sectionStyle;
	private CellStyle auxHeaderStyle;
	private boolean isAdmin = false;
	//file Constants

	public void createDocument() {
		if (workBook == null) {
			workBook = new XSSFWorkbook();
			initCellStyles();
		}
		sheet = null;
		row = null;
		rowIdx = 0;
		colIdx = 0;
	}

	public void addSheet(String sheetName) {
		sheet = workBook.createSheet(sheetName);
		row = null;
		rowIdx = 0;
		colIdx = 0;
	}

	public void addRow() {
		row = sheet.createRow(rowIdx++);
		colIdx = 0;
	}

	public void addHeaderCell(String text, int size) {
		Cell cell = row.createCell(colIdx);
		cell.setCellValue(text);
		sheet.setColumnWidth(colIdx, size * 256);
		cell.setCellStyle(sectionStyle);
		colIdx++;
	}

	public void addCell(String text, int size) {
		Cell cell = row.createCell(colIdx);
		cell.setCellValue(text);
		sheet.setColumnWidth(colIdx, size * 256);
		cell.setCellStyle(normalStyle);
		colIdx++;
	}

	/**
	 * Возвращает урл нового файла. Можно использовать так
	 * return new ResultPE("file", ResultPE.ResultType.redirect).setValue(fileUrl);
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String saveDoc(String fileName) throws IOException {
		String fileFullName = AppContext.getFilesDirPath(false) + fileName;
		FileOutputStream fileOutputStream = new FileOutputStream(fileFullName);
		workBook.write(fileOutputStream);
		fileOutputStream.close();
		String fileUrl = AppContext.getFilesUrlPath(false) + fileName;
		return fileUrl;
	}

	private void initCellStyles() {
		headerStyle = workBook.createCellStyle();
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		//headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
		headerStyle.setWrapText(true);

		normalStyle = workBook.createCellStyle();
		normalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		normalStyle.setWrapText(true);

		queryStyle = workBook.createCellStyle();
		queryStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		queryStyle.setAlignment(HorizontalAlignment.CENTER);
		queryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		queryStyle.setBorderLeft(BorderStyle.THIN);
		queryStyle.setBorderRight(BorderStyle.THIN);
		queryStyle.setBorderTop(BorderStyle.THIN);
		queryStyle.setBorderBottom(BorderStyle.THIN);
		queryStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());

		sectionStyle = workBook.createCellStyle();
		sectionStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		sectionStyle.setAlignment(HorizontalAlignment.CENTER);
		sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sectionStyle.setBorderLeft(BorderStyle.THIN);
		sectionStyle.setBorderRight(BorderStyle.THIN);
		sectionStyle.setBorderTop(BorderStyle.THIN);
		sectionStyle.setBorderBottom(BorderStyle.THIN);
		sectionStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
	}


}
