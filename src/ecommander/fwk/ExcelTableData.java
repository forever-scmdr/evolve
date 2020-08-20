package ecommander.fwk;

import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.DoubleDataType;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Прайс-лист
 * Created by E on 1/3/2018.
 */
public class ExcelTableData implements TableDataSource {
	private POIExcelWrapper doc;
	private boolean isValid = false;
	private Sheet currentSheet;
	private HashMap<String, Integer> currentHeader = new HashMap<>();
	private Row currentRow;
	private POIUtils.CellXY headerCell;
	private FormulaEvaluator eval;
	private ArrayList<SheetHeader> validSheets = new ArrayList<>();
	private String fileName;
	private ArrayList<String> missingColumns = null;
	public static final char PREFIX = '☬';

	public ExcelTableData(String fileName, String... mandatoryCols) {
		this.fileName = Strings.getFileName(fileName);
		this.doc = POIUtils.openExcel(fileName);
		init(mandatoryCols);
	}

	public ExcelTableData(File file, String... mandatoryCols) {
		this.fileName = file.getName();
		this.doc = POIUtils.openExcel(file);
		init(mandatoryCols);
	}

	public ExcelTableData(Path path, String... mandatoryCols) {
		this.fileName = path.getFileName().toString();
		this.doc = POIUtils.openExcel(path);
		init(mandatoryCols);
	}

	public ExcelTableData(FileItem fileItem, String... mandatoryCols) throws Exception {
		this.fileName = fileItem.getName();
		String prefix = StringUtils.substringBeforeLast(fileName, ".");
		String suffix = "." + StringUtils.substringAfterLast(fileName, ".");
		File temp = File.createTempFile(prefix + System.currentTimeMillis(), suffix);
		temp.deleteOnExit();
		fileItem.write(temp);
		this.doc = POIUtils.openExcel(temp);
		init(mandatoryCols);
	}

	public void reInit(String... mandatoryCols) throws Exception {
		boolean rowChecked = false;
		headerCell = new POIUtils.CellXY(currentRow.getRowNum(), -1);
		String firstMandatory = mandatoryCols[0];
		headerCell = POIUtils.findNextContaining(currentSheet, eval, firstMandatory, headerCell);
		rowChecked = true;
		ArrayList<String> missingColumnsTest = new ArrayList<>();
		for (String checkCol : mandatoryCols) {
			ArrayList<POIUtils.CellXY> found = POIUtils.findCellInRowContaining(eval, checkCol, currentRow);
			if (found.size() == 0) {
				missingColumnsTest.add(checkCol);
				rowChecked = false;
			}
		}
		if (!rowChecked) {
			missingColumns = missingColumnsTest;
			throw new Exception("Missing columns: "+ missingColumns.toString());
		}
		if (rowChecked) {
			HashMap<String, Integer> headers = new HashMap<>();
			for (Cell cell : currentRow) {
				String colHeader = StringUtils.trim(POIUtils.getCellAsString(cell, eval));
				if (StringUtils.isNotBlank(colHeader)) {
					//fix duplicate keys
					if(headers.containsKey(colHeader)) {
						headers.put(PREFIX + StringUtils.lowerCase(colHeader), cell.getColumnIndex());
					}else{
						headers.put(StringUtils.lowerCase(colHeader), cell.getColumnIndex());
					}
				}
			}
			currentHeader = headers;
		}
	}

	private void init(String... mandatoryCols) {
		if (doc == null)
			return;
		Workbook wb = doc.getWorkbook();
		if(wb == null) {
			ServerLogger.error("Workbook not exists");
		}
		eval = wb.getCreationHelper().createFormulaEvaluator();

		ArrayList<String> missingColumnsTest;
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			headerCell = null;
			// Все названия колонок должны быть в одной строке
			Sheet sheet = wb.getSheetAt(i);
			boolean rowChecked = false;
			headerCell = new POIUtils.CellXY(-1, -1);
			if (mandatoryCols.length > 0) {
				String firstMandatory = mandatoryCols[0];
				while (headerCell != null) {
					headerCell = POIUtils.findNextContaining(sheet, eval, firstMandatory, headerCell);
					if (headerCell != null) {
						missingColumnsTest = new ArrayList<>();
						Row row = sheet.getRow(headerCell.row);
						rowChecked = true;
						for (String checkCol : mandatoryCols) {
							ArrayList<POIUtils.CellXY> found = POIUtils.findCellInRowContaining(eval, checkCol, row);
							if (found.size() == 0) {
								missingColumnsTest.add(checkCol);
								rowChecked = false;
							}
						}
						if (!rowChecked && (missingColumns == null || missingColumnsTest.size() < missingColumns.size())) {
							missingColumns = missingColumnsTest;
							ServerLogger.error(missingColumns.toString());
						}
					}
					if (rowChecked)
						break;
				}
				if (rowChecked && headerCell != null) {
					Row row = sheet.getRow(headerCell.row);
					HashMap<String, Integer> headers = new HashMap<>();
					for (Cell cell : row) {
						String colHeader = StringUtils.trim(POIUtils.getCellAsString(cell, eval));
						if (StringUtils.isNotBlank(colHeader)) {
							headers.put(StringUtils.lowerCase(colHeader), cell.getColumnIndex());
						}
					}
					SheetHeader sh = new SheetHeader(sheet, headers, headerCell);
					validSheets.add(sh);
					isValid = true;
				}
			} else {
				SheetHeader sh = new SheetHeader(sheet, null, null);
				validSheets.add(sh);
				isValid = true;
			}
		}

		ServerLogger.error("Valid sheets: "+ validSheets.size());

		if (validSheets.size() > 0){
			currentSheet = validSheets.get(0).sheet;
			headerCell = validSheets.get(0).headerCell;
			currentHeader = validSheets.get(0).header;
		}
	}

	public final String getSheetName(){
		String shitName = currentSheet.getSheetName();
		return shitName;
	}

	public final String getValue(int colIndex) {
		return StringUtils.trim(POIUtils.getCellAsString(currentRow.getCell(colIndex), eval));
	}

	public final Double getDoubleValue(int colIndex) {
		String val = getValue(colIndex);
		return DoubleDataType.parse(val);
	}

	public final BigDecimal getCurrencyValue(int colIndex, BigDecimal...defaultVal) {
		String val = getValue(colIndex);
		BigDecimal bd = DecimalDataType.parse(val, DecimalDataType.CURRENCY);
		if (bd == null) {
			return (defaultVal == null || defaultVal.length == 0) ? null : defaultVal[0];
		}
		return bd;
	}

	public final String getValue(String colName) {
		Integer colIdx = currentHeader.get(StringUtils.lowerCase(colName));
		if (colIdx == null)
			return null;
		return getValue(colIdx);
	}

	public final int getColIndex(String colName){
		Integer colIdx = currentHeader.get(StringUtils.lowerCase(colName));
		return (colIdx != null)? colIdx : -1;
	}

	public final Double getDoubleValue(String colName) {
		String val = getValue(colName);
		return DoubleDataType.parse(val);
	}

	public final BigDecimal getCurrencyValue(String colName, BigDecimal...defaultVal) {
		String val = getValue(colName);
		BigDecimal bd = DecimalDataType.parse(val, DecimalDataType.CURRENCY);
		if (bd == null) {
			return (defaultVal == null || defaultVal.length == 0) ? null : defaultVal[0];
		}
		return bd;
	}

	public final void iterate(TableDataRowProcessor processor) throws Exception {
		if (!isValid) {
			String message = "Excel file is not valid";
			if (missingColumns != null && missingColumns.size() > 0) {
				message = "Отсутствуют обязательные колонки: " + StringUtils.join(missingColumns, ", ");
			}
			throw new EcommanderException(ErrorCodes.VALIDATION_FAILED, message);
		}

		for(SheetHeader sh : validSheets){
			currentSheet = sh.sheet;
			headerCell = sh.headerCell;
			currentHeader = sh.header;
			//-- do everything except processing rows;
			processSheet();
			//-- process rows
			Iterator<Row> rowIter = currentSheet.rowIterator();
			if (headerCell != null) {
				while (rowIter.hasNext() && rowIter.next().getRowNum() < headerCell.row) {
					// пропустить первые строки включая заголовок
				}
			}
			while (rowIter.hasNext()) {
				currentRow = rowIter.next();
				processor.processRow(this);
			}
		}
	}

	public final TreeSet<String> getHeaders(){
		TreeSet<String> a = new TreeSet<>();
		a.addAll(currentHeader.keySet());
		return a;
	}

	public int getLinesCount() {
		return currentSheet.getLastRowNum() - headerCell.row;
	}
	public int getTotalLinesCount(){
		int tlc = 0;
		for(SheetHeader sh : validSheets){
			Sheet s = sh.sheet;
			tlc += s.getLastRowNum();
			if (sh.headerCell != null)
				tlc -= sh.headerCell.row;
		}
		return tlc;
	}

	protected void processSheet() throws Exception {
		// Default - do nothing
	}

	protected static class SheetHeader{
		private Sheet sheet;
		private HashMap<String, Integer> header = new HashMap<>();
		private POIUtils.CellXY headerCell;
		protected SheetHeader(Sheet sheet, HashMap<String, Integer> header, POIUtils.CellXY headerCell){
			this.sheet = sheet;
			this.header = header;
			this.headerCell = headerCell;
		}
	}

	public final int getRowNum() {
		return currentRow.getRowNum();
	}

	public final String getFileName() {
		return fileName;
	}

	@Override
	public void close() throws IOException {
		if (doc != null)
			doc.close();
	}
}
