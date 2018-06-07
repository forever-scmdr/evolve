package ecommander.fwk;

import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.DoubleDataType;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.Closeable;
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
public abstract class ExcelPriceList implements Closeable {
	private POIExcelWrapper doc;
	private boolean isValid = true;
	private Sheet currentSheet;
	private HashMap<String, Integer> currentHeader = new HashMap<>();
	private Row currentRow;
	private POIUtils.CellXY headerCell;
	private FormulaEvaluator eval;
	private ArrayList<SheetHeader> validSheets = new ArrayList<>();

	public ExcelPriceList(String fileName, String... mandatoryCols) {
		this.doc = POIUtils.openExcel(fileName);
		init(mandatoryCols);
	}

	public ExcelPriceList(File file, String... mandatoryCols) {
		this.doc = POIUtils.openExcel(file);
		init(mandatoryCols);
	}

	public ExcelPriceList(Path path, String... mandatoryCols) {
		this.doc = POIUtils.openExcel(path);
		init(mandatoryCols);
	}

	private void init(String... mandatoryCols) {
		if (doc == null)
			return;
		Workbook wb = doc.getWorkbook();
		eval = wb.getCreationHelper().createFormulaEvaluator();

		headerCell = null;
		boolean sheetChecked = false;
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			// Все названия колонок должны быть в одной строке
			Sheet sheet = wb.getSheetAt(i);
			boolean rowChecked = true;
			for (String mandatoryCol : mandatoryCols) {
				headerCell = POIUtils.findFirstContaining(sheet, eval, mandatoryCol);
				Row row = sheet.getRow(headerCell.row);
				for (String checkCol : mandatoryCols) {
					ArrayList<POIUtils.CellXY> found = POIUtils.findCellInRowContaining(eval, checkCol, row);
					if (found.size() > 0)
						continue;
					rowChecked = false;
				}
			}
			if (rowChecked) {
				sheetChecked = true;
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
			}
		}

		if (!sheetChecked || headerCell == null) {
			isValid = false;
			return;
		}
	}

	public void initSectionHeaders(Row row, String... mandatoryCols) throws Exception {
		for (String checkCol : mandatoryCols) {
			ArrayList<POIUtils.CellXY> found = POIUtils.findCellInRowContaining(eval, checkCol, row);
			if (found.size() > 0)
				continue;
			throw new Exception("Sheet:"+currentSheet.getSheetName()+". Row "+row.getRowNum()+" contains no \""+checkCol+"\" header");
		}
		HashMap<String, Integer> headers = new HashMap<>();
		for (Cell cell : row) {
			String colHeader = StringUtils.trim(POIUtils.getCellAsString(cell, eval));
			if (StringUtils.isNotBlank(colHeader)) {
				headers.put(StringUtils.lowerCase(colHeader), cell.getColumnIndex());
			}
		}
		SheetHeader sh = new SheetHeader(currentSheet, headers, headerCell);
	}

	public final String getSheetName(){
		String shitName = currentSheet.getSheetName();
		return shitName;
	}

	public final String getValue(int colIndex, double... roundQuotient) {
		return POIUtils.getCellAsString(currentRow.getCell(colIndex), eval);
	}

	public final Double getDoubleValue(int colIndex, double... roundQuotient) {
		String val = getValue(colIndex, roundQuotient);
		return DoubleDataType.parse(val);
	}

	public final BigDecimal getCurrencyValue(int colIndex, double... roundQuotient) {
		String val = getValue(colIndex, roundQuotient);
		return DecimalDataType.parse(val, DecimalDataType.CURRENCY);
	}

	public final String getValue(String colName, double... roundQuotient) {
		Integer colIdx = currentHeader.get(StringUtils.lowerCase(colName));
		if (colIdx == null)
			return null;
		return getValue(colIdx);
	}

	public final Double getDoubleValue(String colName, double... roundQuotient) {
		String val = getValue(colName, roundQuotient);
		return DoubleDataType.parse(val);
	}

	public final BigDecimal getCurrencyValue(String colName, double... roundQuotient) {
		String val = getValue(colName, roundQuotient);
		return DecimalDataType.parse(val, DecimalDataType.CURRENCY);
	}

	public final void iterate() throws Exception {
		if (!isValid)
			throw new EcommanderException(ErrorCodes.VALIDATION_FAILED, "Excel file is not valid");

		for(SheetHeader sh : validSheets){
			currentSheet = sh.sheet;
			headerCell = sh.headerCell;
			currentHeader = sh.header;
			//-- do everything except processing rows;
			processSheet();
			//-- process rows
			Iterator<Row> rowIter = currentSheet.rowIterator();
			for (int i = 0; i <= headerCell.row && rowIter.hasNext(); i++) {
				rowIter.next();
			}
			while (rowIter.hasNext()) {
				currentRow = rowIter.next();
				processRow();
			}
		}
	}

	public final UniqueArrayList<String> getHeaders(){
		UniqueArrayList<String> a = new UniqueArrayList<>();
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
			tlc += s.getLastRowNum() - sh.headerCell.row;
		}
		return tlc;
	}

	protected abstract void processRow() throws Exception;
	protected abstract void processSheet() throws Exception;
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

	@Override
	public void close() throws IOException {
		if (doc != null)
			doc.close();
	}
}
