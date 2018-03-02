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

/**
 * Прайс-лист
 * Created by E on 1/3/2018.
 */
public abstract class ExcelPriceList implements Closeable {
	private POIExcelWrapper doc;
	private boolean isValid = true;
	private Sheet sheet;
	private HashMap<String, Integer> header = new HashMap<>();
	private Row currentRow;
	private POIUtils.CellXY headerCell;
	private FormulaEvaluator eval;

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
			sheet = wb.getSheetAt(i);
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
				break;
			}
		}

		if (!sheetChecked || headerCell == null) {
			isValid = false;
			return;
		}

		Row row = sheet.getRow(headerCell.row);
		for (Cell cell : row) {
			String colHeader = StringUtils.trim(POIUtils.getCellAsString(cell, eval));
			if (StringUtils.isNotBlank(colHeader)) {
				header.put(StringUtils.lowerCase(colHeader), cell.getColumnIndex());
			}
		}
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
		Integer colIdx = header.get(StringUtils.lowerCase(colName));
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
		Iterator<Row> rowIter = sheet.rowIterator();
		for (int i = 0; i <= headerCell.row && rowIter.hasNext(); i++) {
			rowIter.next();
		}
		while (rowIter.hasNext()) {
			currentRow = rowIter.next();
			processRow();
		}
	}

	public int getLinesCount() {
		return sheet.getLastRowNum() - headerCell.row;
	}

	protected abstract void processRow() throws Exception;

	@Override
	public void close() throws IOException {
		if (doc != null)
			doc.close();
	}
}
