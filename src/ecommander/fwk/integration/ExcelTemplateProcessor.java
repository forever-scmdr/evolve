package ecommander.fwk.integration;

import ecommander.fwk.POIExcelWrapper;
import ecommander.fwk.POIUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelTemplateProcessor {

	private File templatePath;

	private POIExcelWrapper excel;
	private Workbook wb;
	private Sheet sheet;
	private FormulaEvaluator eval;

	public ExcelTemplateProcessor(File templatePath) {
		this.templatePath = templatePath;
	}

	/**
	 * Открытие шаблона документа excel
	 */
	private void createDoc() {
		if (excel == null) {
			excel = POIExcelWrapper.create(templatePath);
			wb = excel.getWorkbook();
			sheet = wb.getSheetAt(wb.getActiveSheetIndex());
			eval = wb.getCreationHelper().createFormulaEvaluator();
		}
	}

	/**
	 * Скопировать строку.
	 * Нужно в случае если необходимо создать несколько однотипных строк из одной шаблонной строки.
	 * Например, товары в заказе (в заказе много товаров, но в шаблоне только одна строка для шаблона товара)
	 * @param searchString
	 * @return - false в случае если не найдена заданная строка
	 */
	public boolean duplicateRowContaining(String searchString) {
		createDoc();
		POIUtils.CellXY cellXY = POIUtils.findFirstContaining(sheet, eval, searchString);
		if (cellXY == null)
			return false;
		if (wb instanceof HSSFWorkbook) {
			POIUtils.copyRow((HSSFWorkbook) wb, (HSSFSheet) sheet, cellXY.row, cellXY.row + 1);
		} else if (wb instanceof XSSFWorkbook) {
			POIUtils.copyRow((XSSFWorkbook) wb, (XSSFSheet) sheet, cellXY.row, cellXY.row + 1);
		} else  {
			return false;
		}
		return true;
	}

	/**
	 * Удалить ненужную строку
	 * @param searchString
	 * @return - false в случае если не найдена заданная строка
	 */
	public boolean deleteRow(String searchString) {
		createDoc();
		POIUtils.CellXY cellXY = POIUtils.findFirstContaining(sheet, eval, searchString);
		if (cellXY == null)
			return false;
		POIUtils.removeExcelRow(sheet, cellXY.row);
		return true;
	}

	/**
	 * Заменить первое вхождение строки в документе на заданную строку
	 * @param searchString
	 * @param replacement
	 * @return - false в случае если не найдена заданная строка
	 */
	public boolean replace(String searchString, String replacement) {
		createDoc();
		POIUtils.CellXY cellXY = POIUtils.findFirstContaining(sheet, eval, searchString);
		if (cellXY == null)
			return false;
		POIUtils.replaceXlsTextDirect(sheet, eval, cellXY.row, searchString, replacement);
		return true;
	}

	/**
	 * Записать полученный результат в указанный файл
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public File writeExcelFile(File filePath) throws IOException {
		createDoc();
		// Генерация выходного файла
		if (filePath.exists())
			filePath.delete();
		wb.write(new FileOutputStream(filePath));
		excel.close();
		return filePath;
	}

	/**
	 * Расширение файла (тип файла Excel) в зависимости от типа документа
	 * @return
	 */
	public String getExtension() {
		createDoc();
		if (wb instanceof XSSFWorkbook) {
			return ".xlsx";
		}
		return ".xls";
	}
}
