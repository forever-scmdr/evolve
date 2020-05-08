package extra;

import ecommander.controllers.AppContext;
import ecommander.controllers.MainExecutionController;
import ecommander.controllers.PageController;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.*;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

/**
 * Created by user on 05.12.2018.
 */
public class CreateSearchExcel extends Command implements CatalogConst {
	//workbook styles
	private HSSFWorkbook workBook;
	private CellStyle headerStyle;
	private CellStyle normalStyle;
	private CellStyle queryStyle;
	private CellStyle noPriceStyle;
	private CellStyle noCodeStyle;
	private CellStyle sectionStyle;
	private CellStyle auxHeaderStyle;
	private boolean isAdmin = false;
	//file Constants


	protected static final String QUERY = "Запрос";
	protected static final String NAME = "Название";
	protected static final String NAME_EXTRA = "Описание";
	protected static final String VENDOR = "Производитель";
	protected static final String QTY = "Кол.";
	protected static final String AVAILABLE = "Срок поставки";
	protected static final String UNIT = "Ед.изм.";
	protected static final String MIN_QTY = "Мин.заказ";
	protected static final String PRICE = "Цена";
	protected static final String SUM = "Сумма";
	protected static final String INITIAL_PRICE = "Нач.цена";
	protected static final String STORE = "Склад";
	protected static final String UPDATED = "Обновлено";
	protected static final String REQUEST = "Заказ";


	@Override
	public ResultPE execute() throws Exception {
		isAdmin = getInitiator().getRole(User.USER_DEFAULT_GROUP) == User.ADMIN;

		LinkPE link = getLink("xml_link");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ExecutablePagePE page = getExecutablePage(link.serialize());
		PageController.newSimple().executePage(page, bos);
		Document doc = Jsoup.parse(bos.toString("UTF-8"), "", Parser.xmlParser());

		workBook = new HSSFWorkbook();
		initCellStyles();

		Sheet sh = workBook.createSheet("Запрос товаров");
		int rowIndex = -1;
		Row row = sh.createRow(++rowIndex);
		rowIndex = initializeHeader(sh, rowIndex);
		Elements prods = doc.getElementsByTag("product");
		String currentQuery = "";
		int resultsPerQuery = 0;
		for (Element prod : prods) {
			int colIdx = -1;
			String query = JsoupUtils.nodeText(prod, "query");
			boolean isNewQuery = !StringUtils.equalsIgnoreCase(query, currentQuery);
			resultsPerQuery = isNewQuery ? 0 : resultsPerQuery + 1;
			if (resultsPerQuery >= 10)
				continue;
			row = sh.createRow(++rowIndex);
			row.createCell(++colIdx).setCellValue(query);
			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "name"));
			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "name_extra"));
			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "vendor"));
			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "qty"));
			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "available"));
			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "unit"));
			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "min_qty"));

			StringBuilder price = new StringBuilder();
			StringBuilder sum = new StringBuilder();
			Elements test = prod.getElementsByTag("total_price").first().getElementsByTag("num_price");
			if (test.isEmpty()) {
				price.append("запрос цены");
				sum.append("запрос цены");
			} else {
				for (Element priceLine : prod.getElementsByTag("unit_price").first().getElementsByTag("num_price")) {
					if (StringUtils.isNotBlank(price)) {
						price.append("\r\n");
					}
					price.append(priceLine.ownText());
				}
				for (Element sumLine : prod.getElementsByTag("total_price").first().getElementsByTag("num_price")) {
					if (StringUtils.isNotBlank(sum)) {
						sum.append("\r\n");
					}
					sum.append(sumLine.ownText());
				}
			}

			row.createCell(++colIdx).setCellValue(price.toString());
			row.createCell(++colIdx).setCellValue(sum.toString());

			if (isAdmin) {
				row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "price_original"));
				row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod.getElementsByTag("plain_section").first(), "name"));
				row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod.getElementsByTag("plain_section").first(), "date"));
			}

			row.createCell(++colIdx).setCellValue(JsoupUtils.nodeText(prod, "request_qty"));

			for (Cell cell : row) {
				cell.setCellStyle(isNewQuery ? headerStyle : normalStyle);
			}
			if (!isNewQuery)
				row.getCell(0).setCellStyle(queryStyle);
			currentQuery = query;
		}
		String fileName = "query_" + System.currentTimeMillis() + ".xls";
		String fileFullName = AppContext.getFilesDirPath(false) + fileName;
		FileOutputStream fileOutputStream = new FileOutputStream(fileFullName);
		workBook.write(fileOutputStream);
		fileOutputStream.close();
		String fileUrl = AppContext.getFilesUrlPath(false) + fileName;

		return new ResultPE("file", ResultPE.ResultType.redirect).setValue(fileUrl);
	}



	private int initializeHeader(Sheet sh, int rowIndex, ItemType... auxType){
		int rowIdx = rowIndex;
		int colIdx = -1;

		//built-in params
		Row row = sh.createRow(++rowIdx);
		row.createCell(++colIdx).setCellValue(QUERY);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(NAME);
		sh.setColumnWidth(colIdx, 30 * 256);
		row.createCell(++colIdx).setCellValue(NAME_EXTRA);
		sh.setColumnWidth(colIdx, 30 * 256);
		row.createCell(++colIdx).setCellValue(VENDOR);
		sh.setColumnWidth(colIdx, 20 * 256);
		row.createCell(++colIdx).setCellValue(QTY);
		sh.setColumnWidth(colIdx, 8 * 256);
		row.createCell(++colIdx).setCellValue(AVAILABLE);
		sh.setColumnWidth(colIdx, 12 * 256);
		row.createCell(++colIdx).setCellValue(UNIT);
		sh.setColumnWidth(colIdx, 10 * 256);
		row.createCell(++colIdx).setCellValue(MIN_QTY);
		sh.setColumnWidth(colIdx, 8 * 256);
		row.createCell(++colIdx).setCellValue(PRICE);
		sh.setColumnWidth(colIdx, 12 * 256);
		row.createCell(++colIdx).setCellValue(SUM);
		sh.setColumnWidth(colIdx, 20 * 256);
		if (isAdmin) {
			row.createCell(++colIdx).setCellValue(INITIAL_PRICE);
			sh.setColumnWidth(colIdx, 12 * 256);
			row.createCell(++colIdx).setCellValue(STORE);
			sh.setColumnWidth(colIdx, 20 * 256);
			row.createCell(++colIdx).setCellValue(UPDATED);
			sh.setColumnWidth(colIdx, 20 * 256);
		}
		row.createCell(++colIdx).setCellValue(REQUEST);
		sh.setColumnWidth(colIdx, 8 * 256);

		for (Cell cell : row) {
			cell.setCellStyle(sectionStyle);
		}

		return rowIdx;
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
