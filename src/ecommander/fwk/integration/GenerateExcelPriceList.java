package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.ServerLogger;
import ecommander.model.*;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by user on 15.06.2018.
 */
public class GenerateExcelPriceList extends Command implements CatalogConst {
	private CellStyle headerStyle = null;
	private CellStyle errorStyle = null;
	private CellStyle auxStyle = null;
	private CellStyle sectionStyle = null;
	private static final Object MUTEX = new Object();

	private static final String CODE_FILE = "code";
	private static final String NAME_FILE = "Наменование";
	private static final String TEXT_FILE = "Описание {html}";
	private static final String PIC_FILE = "Картинка";
	private static final String GAL_FILE = "Галерея";
	private static final String PRICE_FILE = "Цена";
	private static final String TAG_FILE = "Тег";
//	private static final String SEO_FILE = "SEO";
	private static final String SEC_START = "Раздел:";
	private static final Format DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss");


	@Override
	public ResultPE execute() throws Exception {
		synchronized (MUTEX) {
			try {
				Workbook wb = new HSSFWorkbook();
				initCellStyles(wb);

				Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
				ItemQuery q = new ItemQuery(SECTION_ITEM);
				q.setParentId(catalog.getId(), false);
				List<Item> sections = q.loadItems();

				for (Item section : sections) {
					String sheetName = section.getValue(NAME_PARAM) + " | " + section.getStringValue(CATEGORY_ID_PARAM);
					Sheet sh = wb.createSheet(sheetName);
					int rowIdx = -1;
					int colIdx = -1;
					Row row = sh.createRow(++rowIdx);
					row.createCell(++colIdx).setCellValue(CODE_FILE);
					sh.setColumnWidth(colIdx, 20 * 256);
					row.createCell(++colIdx).setCellValue(NAME_FILE);
					sh.setColumnWidth(colIdx, 75 * 256);
					row.createCell(++colIdx).setCellValue(TEXT_FILE);
					sh.setColumnWidth(colIdx, 75 * 256);
					row.createCell(++colIdx).setCellValue(PIC_FILE);
					sh.setColumnWidth(colIdx, 25 * 256);
					row.createCell(++colIdx).setCellValue(GAL_FILE);
					sh.setColumnWidth(colIdx, 25 * 256);
					row.createCell(++colIdx).setCellValue(PRICE_FILE);
					sh.setColumnWidth(colIdx, 25 * 256);
					row.createCell(++colIdx).setCellValue(TAG_FILE);
					sh.setColumnWidth(colIdx, 25 * 256);
//					row.createCell(++colIdx).setCellValue(SEO_FILE);
//					sh.setColumnWidth(colIdx, 25 * 256);
					q = new ItemQuery(SECTION_ITEM);
					q.setParentId(section.getId(), false);
					List<Item> subsections = new ItemQuery(SECTION_ITEM).setParentId(section.getId(), false).loadItems();
					List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), false).loadItems();
					if (products.size() > 0) {
						Item params = new ItemQuery(PARAMS_ITEM).setParentId(products.get(0).getId(), false).loadFirstItem();
						if (params != null) {
							ItemType paramsType = ItemTypeRegistry.getItemType(params.getTypeId());
							for (ParameterDescription aux : paramsType.getParameterList()) {
								row.createCell(++colIdx).setCellValue("#" + aux.getCaption());
								row.getCell(colIdx).setCellStyle(auxStyle);
							}
						}
						for (Item product : products) {
							rowIdx = recordProduct(rowIdx, sh, product);
						}
					}
					for (Item sub : subsections) {
						rowIdx = recordSection(rowIdx, sh, sub, section.getStringValue(CATEGORY_ID_PARAM));
					}
				}
				FileUtils.deleteQuietly(new File(AppContext.getFilesDirPath(false) + "/" + "integration_file.xls"));
				FileOutputStream fileOutputStream = new FileOutputStream(AppContext.getFilesDirPath(false) + "/" + "integration_file.xls");
				wb.write(fileOutputStream);
				fileOutputStream.close();
			} catch (Exception e) {
				ServerLogger.error("Can not create Excel Pricelist", e);
				StackTraceElement[] stackTrace = e.getStackTrace();
				StringBuilder sb = new StringBuilder();
				for (StackTraceElement err : stackTrace) {
					sb.append(err.toString() + "\n");
				}
				return getResult("error").addVariable("stack", sb.toString());
			}
		}
		ResultPE res = getResult("success");
//		res.setVariable("file_name", "integration_file.xls");
//		res.setVariable("file_caption", "Файл интеграции "+DATE_FORMAT.format(new Date()));
		res.setVariable("h1", "Создание файла интеграции завершено успешно");
		return getResult("success");
	}

	private int recordSection(int rowIdx, Sheet sheet, Item section, String parentCode) throws Exception {
		Row row = sheet.createRow(++rowIdx);
		int colIdx = -1;
		//insert section start keyword
		row.createCell(++colIdx).setCellValue(SEC_START);
		sheet.setColumnWidth(colIdx, 20 * 256);
		row.getCell(colIdx).setCellStyle(sectionStyle);

		row.createCell(++colIdx).setCellValue(section.getStringValue(NAME_PARAM));
		sheet.setColumnWidth(colIdx, 75 * 256);
		row.getCell(colIdx).setCellStyle(sectionStyle);
		row.createCell(++colIdx).setCellValue(section.getStringValue(CATEGORY_ID_PARAM));
		sheet.setColumnWidth(colIdx, 75 * 256);
		row.getCell(colIdx).setCellStyle(sectionStyle);
		row.createCell(++colIdx).setCellValue(section.getStringValue(parentCode));
		sheet.setColumnWidth(colIdx, 75 * 256);
		row.getCell(colIdx).setCellStyle(sectionStyle);

//		ItemQuery q = new ItemQuery(ItemNames.SEO);
//		q.setParentId(section.getId(), false);
//		Item seo = q.loadFirstItem();
//		if(seo != null){
//			StringBuilder sb = new StringBuilder();
//			Collection <Parameter> parameters = seo.getAllParameters();
//			for(Parameter p : parameters){
//				if(!p.isMultiple()){
//					sb.append(p.getParamId()).append(":").append(p.getValue()).append('ᐁ');
//				}
//			}
//		}

		colIdx = -1;

		ItemQuery q = new ItemQuery(PRODUCT_ITEM);
		q.setParentId(section.getId(), false);
		List<Item> products = q.loadItems();
		if (products.size() != 0) {
			row = sheet.createRow(++rowIdx);
			row.createCell(++colIdx).setCellValue(CODE_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);
			row.createCell(++colIdx).setCellValue(NAME_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);
			row.createCell(++colIdx).setCellValue(TEXT_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);
			row.createCell(++colIdx).setCellValue(PIC_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);
			row.createCell(++colIdx).setCellValue(GAL_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);
			row.createCell(++colIdx).setCellValue(PRICE_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);
			row.createCell(++colIdx).setCellValue(TAG_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);
//			row.createCell(++colIdx).setCellValue(SEO_FILE);
			row.getCell(colIdx).setCellStyle(headerStyle);

			Item params = new ItemQuery(PARAMS_ITEM).setParentId(products.get(0).getId(), false).loadFirstItem();
			if (params != null) {
				ItemType paramsType = ItemTypeRegistry.getItemType(params.getTypeId());
				for (ParameterDescription aux : paramsType.getParameterList()) {
					row.createCell(++colIdx).setCellValue("#" + aux.getCaption());
					row.getCell(colIdx).setCellStyle(auxStyle);
				}
			}
			for (Item product : products) {
				rowIdx = recordProduct(rowIdx, sheet, product);
			}
		}

		q = new ItemQuery(SECTION_ITEM);
		q.setParentId(section.getId(), false);
		List<Item> sections = q.loadItems();
		for (Item sub : sections) {
			rowIdx = recordSection(rowIdx, sheet, sub, section.getStringValue(CATEGORY_ID_PARAM));
		}
		return rowIdx;
	}

	private int recordProduct(int rowIdx, Sheet sheet, Item product) throws Exception {
		int colIdx = -1;
		Row row = sheet.createRow(++rowIdx);

		row.createCell(++colIdx).setCellValue(product.getStringValue(CODE_PARAM, ""));
		row.createCell(++colIdx).setCellValue(product.getStringValue(NAME_PARAM, ""));
		row.createCell(++colIdx).setCellValue(product.getStringValue(TEXT_PARAM, ""));
		row.createCell(++colIdx).setCellValue("");
		row.createCell(++colIdx).setCellValue("");
		row.createCell(++colIdx).setCellValue(String.valueOf(product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO)));
		row.createCell(++colIdx).setCellValue(product.getStringValue("tag"));
		Item params = new ItemQuery(PARAMS_ITEM).setParentId(product.getId(), false).loadFirstItem();
		if (params != null) {
			ItemType paramsType = ItemTypeRegistry.getItemType(params.getTypeId());
			for (String pName : paramsType.getParameterNames()) {
				Object value = params.getValue(pName);
				String pValue = (value == null)? "" : String.valueOf(value);
				pValue = StringUtils.isBlank(pValue) ? "" : pValue;
				row.createCell(++colIdx).setCellValue(pValue);
			}
		}

		return rowIdx;
	}

	private void initCellStyles(Workbook wb) {
		headerStyle = wb.createCellStyle();
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(IndexedColors.YELLOW1.getIndex());

		errorStyle = wb.createCellStyle();
		errorStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		errorStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

		auxStyle = wb.createCellStyle();
		auxStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		auxStyle.setAlignment(HorizontalAlignment.CENTER);
		auxStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		auxStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());

		sectionStyle = wb.createCellStyle();
		sectionStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		sectionStyle.setAlignment(HorizontalAlignment.CENTER);
		sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sectionStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
	}

}
