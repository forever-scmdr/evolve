package ecommander.fwk;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class POIUtils {
	public static class CellXY {
		public int row;
		public int column;
		private CellXY(int row, int col) {
			this.row = row;
			this.column = col;
		}
	}
	
	/**
	 * Скопировать указанную строку в указанное место,
	 * Если в указанном месте уже есть строка, то она смещается вниз
	 * @param workbook
	 * @param worksheet
	 * @param sourceRowNum
	 * @param destinationRowNum
	 */
	public static void copyRow(HSSFWorkbook workbook, HSSFSheet worksheet, int sourceRowNum, int destinationRowNum) {
        // Get the source / new row
        HSSFRow newRow = worksheet.getRow(destinationRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
            if (sourceRowNum >= destinationRowNum)
            	sourceRowNum++;
        } else {
            newRow = worksheet.createRow(destinationRowNum);
        }
        // Get the source / new row
        HSSFRow sourceRow = worksheet.getRow(sourceRowNum);
        
        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            HSSFCell oldCell = sourceRow.getCell(i);
            HSSFCell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                newCell = null;
                continue;
            }

            // Copy style from old cell and apply to new cell
            HSSFCellStyle newCellStyle = workbook.createCellStyle();
			newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
//			newCellStyle.setFillForegroundColor(oldCell.getCellStyle().getFillForegroundColor());
//			newCellStyle.setFillPattern(oldCell.getCellStyle().getFillPattern());
//			newCellStyle.setBorderTop(oldCell.getCellStyle().getBorderTop());
//			newCellStyle.setBorderBottom(oldCell.getCellStyle().getBorderBottom());
//			newCellStyle.setBorderLeft(oldCell.getCellStyle().getBorderLeft());
//			newCellStyle.setBorderRight(oldCell.getCellStyle().getBorderRight());
			newCell.setCellStyle(newCellStyle);
            
            // If there is a cell comment, copy
            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }

        // If there are are any merged regions in the source row, copy to new row
        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
                        (newRow.getRowNum() +
                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
                                        )),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                worksheet.addMergedRegion(newCellRangeAddress);
            }
        }
    }
	
	/**
	 * Скопировать указанную строку в указанное место,
	 * Если в указанном месте уже есть строка, то она смещается вниз
	 * @param workbook
	 * @param worksheet
	 * @param sourceRowNum
	 * @param destinationRowNum
	 */
	public static void copyRow(XSSFWorkbook workbook, XSSFSheet worksheet, int sourceRowNum, int destinationRowNum) {
        // Get the source / new row
        XSSFRow newRow = worksheet.getRow(destinationRowNum);

        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
            if (sourceRowNum >= destinationRowNum)
            	sourceRowNum++;
        }
        newRow = worksheet.createRow(destinationRowNum);
        
        // Get the source / new row
        XSSFRow sourceRow = worksheet.getRow(sourceRowNum);
        
        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            XSSFCell oldCell = sourceRow.getCell(i);
            XSSFCell newCell = newRow.createCell(i);

            // If the old cell is null jump to next cell
            if (oldCell == null) {
                newCell = null;
                continue;
            }

            // Copy style from old cell and apply to new cell
            XSSFCellStyle newCellStyle = workbook.createCellStyle();
			newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
			StylesTable newStylesSource = newCell.getSheet().getWorkbook().getStylesSource();
			StylesTable oldStylesSource = oldCell.getSheet().getWorkbook().getStylesSource();
			for (XSSFCellFill fill : oldStylesSource.getFills()) {
				XSSFCellFill fillNew = new XSSFCellFill(fill.getCTFill(), oldStylesSource.getIndexedColors());
				newStylesSource.putFill(fillNew);
			}
			for (XSSFCellBorder border : oldStylesSource.getBorders()) {
				XSSFCellBorder borderNew = new XSSFCellBorder(border.getCTBorder());
				newStylesSource.putBorder(borderNew);
			}
//			for (XSSFFont font : oldStylesSource.getFonts()) {
//				XSSFFont fontNew = new XSSFFont(font.getCTFont());
//				fontNew.registerTo(newStylesSource);
//			}
//			newCellStyle.setFillForegroundColor(oldCell.getCellStyle().getFillForegroundColor());
//			newCellStyle.setFillPattern(oldCell.getCellStyle().getFillPattern());
//			newCellStyle.setBorderTop(oldCell.getCellStyle().getBorderTop());
//			newCellStyle.setBorderBottom(oldCell.getCellStyle().getBorderBottom());
//			newCellStyle.setBorderLeft(oldCell.getCellStyle().getBorderLeft());
//			newCellStyle.setBorderRight(oldCell.getCellStyle().getBorderRight());
			newCell.setCellStyle(newCellStyle);
            
            // If there is a cell comment, copy
            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }

        // If there are are any merged regions in the source row, copy to new row
//        ArrayList<CellRangeAddress> mergedRegions = new ArrayList<CellRangeAddress>();
//        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
//        	mergedRegions.add(worksheet.getMergedRegion(i));
//        }
//		for (CellRangeAddress cellRangeAddress : mergedRegions) {
//			if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
//				CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
//						(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
//						cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
//				worksheet.addMergedRegion(newCellRangeAddress);
//			}
//		}
		for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
			CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
			if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
				CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
						(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
						cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
				worksheet.addMergedRegion(newCellRangeAddress);
			}
		}
    }
	/**
	 * Создать цвет
	 * @param workbook
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static HSSFColor setColor(HSSFWorkbook workbook, byte r, byte g, byte b) {
		HSSFPalette palette = workbook.getCustomPalette();
		HSSFColor hssfColor = null;
		try {
			hssfColor = palette.findColor(r, g, b);
			if (hssfColor == null) {
				palette.setColorAtIndex(HSSFColor.LAVENDER.index, r, g, b);
				hssfColor = palette.getColor(HSSFColor.LAVENDER.index);
			}
		} catch (Exception e) {
			ServerLogger.error(e);
		}
		return hssfColor;
	}
	/**
	 * Найти строки и столбцы, содержащие заданный текст
	 * @param sheet
	 * @param cellContent
	 * @param justFirst - если true, то выходить после первой найденной ячейки
	 * @return
	 */
	public static ArrayList<CellXY> findRowContaining(Sheet sheet, FormulaEvaluator evaluator, String cellContent, boolean justFirst) {
		ArrayList<CellXY> result = new ArrayList<>();
		Iterator<Row> rowIter = sheet.iterator();
		while (rowIter.hasNext()) {
			Row row = rowIter.next();
			Iterator<Cell> cellIter = row.iterator();
			while (cellIter.hasNext()) {
				Cell cell = cellIter.next();
				String cellValue = getCellAsString(cell, evaluator);
				if (StringUtils.containsIgnoreCase(cellValue, cellContent)) {
					result.add(new CellXY(row.getRowNum(), cell.getColumnIndex()));
					if (justFirst)
						return result;
				}
			}
		}
		return result;
	}
	/**
	 * Найти первую ячейку с заданным текстом
	 * @param sheet
	 * @param cellContent
	 * @return
	 */
	public static CellXY findFirstContaining(Sheet sheet, FormulaEvaluator evaluator, String cellContent) {
		ArrayList<CellXY> result = findRowContaining(sheet, evaluator, cellContent, true);
		if (result.isEmpty())
			return null;
		return result.get(0);
	}
	/**
	 * Найти столбцы в строке, содержащие заданный текст
	 * @param evaluator
	 * @param cellContent
	 * @param row
	 * @return
	 */
	public static ArrayList<CellXY> findCellInRowContaining(FormulaEvaluator evaluator, String cellContent, Row row) {
		ArrayList<CellXY> result = new ArrayList<>();
		Iterator<Cell> cellIter = row.iterator();
		while (cellIter.hasNext()) {
			Cell cell = cellIter.next();
			String cellValue = getCellAsString(cell, evaluator);
			if (StringUtils.containsIgnoreCase(cellValue, cellContent)) {
				result.add(new CellXY(row.getRowNum(), cell.getColumnIndex()));
			}
		}
		return result;
	}
	/**
	 * Получить значение из ячейки в виде строки
	 * @param cell
	 * @param evaluator
	 * @return
	 */
	public static String getCellAsString(Cell cell, FormulaEvaluator evaluator) {
		if (cell == null)
			return null;
		if (cell.getCellTypeEnum() == CellType.FORMULA) {
			CellValue cellValue = evaluator.evaluate(cell);
			return cellValue.formatAsString();
		}
		DataFormatter df = new DataFormatter();
		return df.formatCellValue(cell);
	}
	
	private static String round(double number, double... quotient) {
		double num = quotient.length > 0 ? Math.round(number * quotient[0]) / quotient[0] : number;
		if (quotient.length > 0 && quotient[0] <= 1)
			return ((long) num) + "";
		return num + "";
	}
	
	public static ArrayList<XWPFRun> findDocRuns(XWPFDocument doc, String searchStr) {
		ArrayList<XWPFRun> resultRuns = new ArrayList<>();
		for (XWPFParagraph p : doc.getParagraphs()) {
			List<XWPFRun> runs = p.getRuns();
			if (runs != null) {
				for (XWPFRun r : runs) {
					String text = r.getText(0);
					if (text != null && text.contains(searchStr)) {
						resultRuns.add(r);
					}
				}
			}
		}
		for (XWPFTable tbl : doc.getTables()) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph p : cell.getParagraphs()) {
						for (XWPFRun r : p.getRuns()) {
							String text = r.getText(0);
							if (text.contains(searchStr)) {
								resultRuns.add(r);
							}
						}
					}
				}
			}
		}
		return resultRuns;
	}


	public static void replaceXlsTextDirect(Sheet sheet, FormulaEvaluator evaluator, String searchStr, String replace, boolean justFirst) {
		ArrayList<CellXY> allCoords = findRowContaining(sheet, evaluator, searchStr, justFirst);
		for (CellXY coords : allCoords) {
			Cell cell = sheet.getRow(coords.row).getCell(coords.column);
			String newText = StringUtils.replace(cell.getStringCellValue(), searchStr, replace);
			cell.setCellValue(newText);
		}
	}
	/**
	 * Заменяет искомый текст в первой найденной ячейке в заданной строке на нужный текст
	 * @param sheet
	 * @param rowNum
	 * @param searchStr
	 * @param replace
	 */
	public static void replaceXlsTextDirect(Sheet sheet, FormulaEvaluator evaluator, int rowNum, String searchStr, String replace) {
		ArrayList<CellXY> allCoords = findCellInRowContaining(evaluator, searchStr, sheet.getRow(rowNum));
		if (allCoords.size() > 0) {
			CellXY coords = allCoords.get(0);
			Cell cell = sheet.getRow(coords.row).getCell(coords.column);
			String newText = StringUtils.replace(cell.getStringCellValue(), searchStr, replace);
			cell.setCellValue(newText);
		}
	}
	
	public static void replaceDocTextDirect(XWPFDocument doc, String searchStr, String replace) {
		ArrayList<XWPFRun> runs = POIUtils.findDocRuns(doc, searchStr);
		for (XWPFRun run : runs) {
			String text = run.getText(0);
			text = text.replace(searchStr, replace);
			run.setText(text, 0);
		}
	}
	
	public static XWPFRun findSingleDocRun(XWPFDocument doc, String searchStr) {
		ArrayList<XWPFRun> runs = findDocRuns(doc, searchStr);
		if (runs.size() == 0)
			return null;
		return runs.get(0);
	}
	/**
	 * Удаляет строку с заданным номером из документа
	 * @param sheet
	 * @param rowIndex
	 */
	public static void removeExcelRow(Sheet sheet, int rowIndex) {
	    int lastRowNum = sheet.getLastRowNum();
	    if (rowIndex >= 0 && rowIndex < lastRowNum) {
	        sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
	    }
	    if (rowIndex == lastRowNum) {
	        Row removingRow = sheet.getRow(rowIndex);
	        if (removingRow != null) {
	            sheet.removeRow(removingRow);
	        }
	    }
	}

	public static POIExcelWrapper openExcel(String fileName) {
		return POIExcelWrapper.create(fileName);
	}

	public static POIExcelWrapper openExcel(File file) {
		return POIExcelWrapper.create(file);
	}

	public static POIExcelWrapper openExcel(Path path) {
		return POIExcelWrapper.create(path);
	}
}
