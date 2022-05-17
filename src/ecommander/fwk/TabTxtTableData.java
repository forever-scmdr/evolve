package ecommander.fwk;

import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.DoubleDataType;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by E on 14/1/2019.
 */
public class TabTxtTableData implements TableDataSource {

	public static final String UTF8_BOM = "\uFEFF";

	private boolean isValid = false;
	private HashMap<String, Integer> header = new HashMap<>();
	private String[] currentRow;
	private int currentRowNum = -1;
	private int headerRow = -1;
	private File file;
	private ArrayList<String> missingColumns = null;
	private Charset fileCharset;
	private boolean isCsv = false;
	private char SEPARATOR_CHAR = '\t';

	public TabTxtTableData(String fileName, Charset charset, String... mandatoryCols) {
		this.file = new File(fileName);
		this.fileCharset = charset;
		init(mandatoryCols);
	}

	public TabTxtTableData(String fileName, Charset charset, boolean isCsv, String... mandatoryCols) {
		this.file = new File(fileName);
		this.fileCharset = charset;
		this.isCsv = isCsv;
		if (isCsv)
			SEPARATOR_CHAR = ';';
		init(mandatoryCols);
	}

	public TabTxtTableData(File file, Charset charset, boolean isCsv, String... mandatoryCols) {
		this.file = file;
		this.fileCharset = charset;
		this.isCsv = isCsv;
		if (isCsv)
			SEPARATOR_CHAR = ';';
		init(mandatoryCols);
	}

	public TabTxtTableData(File file, Charset charset, String... mandatoryCols) {
		this.file = file;
		this.fileCharset = charset;
		init(mandatoryCols);
	}

	public TabTxtTableData(Path path, Charset charset, String... mandatoryCols) {
		this.file = path.toFile();
		this.fileCharset = charset;
		init(mandatoryCols);
	}

	private void init(String... mandatoryCols) {
		if (!file.exists())
			return;

		ArrayList<String> missingColumnsTest;

		// Все названия колонок должны быть в одной строке
		boolean rowChecked = false;
		String line;
		String[] cols = {};
		if (mandatoryCols.length > 0) {
			try (BufferedReader br = Files.newBufferedReader(file.toPath(), fileCharset)) {
				line = StringUtils.trim(br.readLine());
				if (StringUtils.startsWith(line, UTF8_BOM))
					line = line.substring(1);
				while (line != null && headerRow < 1000 && !rowChecked) {
					headerRow++;
					cols = StringUtils.splitPreserveAllTokens(line, SEPARATOR_CHAR);
					for (int i = 0; i < cols.length; i++) {
						cols[i] = prepareValue(cols[i]);
					}
					missingColumnsTest = new ArrayList<>();
					rowChecked = true;
					for (String checkCol : mandatoryCols) {
						if (!StringUtils.equalsAnyIgnoreCase(checkCol, cols)) {
							missingColumnsTest.add(checkCol);
							rowChecked = false;
						}
					}
					if (!rowChecked) {
						line = StringUtils.trim(br.readLine());
						if (missingColumns == null || missingColumnsTest.size() < missingColumns.size())
							missingColumns = missingColumnsTest;
					}
				}
			} catch (FileNotFoundException e) {
				ServerLogger.error("file not found", e);
			} catch (IOException e) {
				ServerLogger.error("file IO error", e);
			}
			if (rowChecked) {
				for (int i = 0; i < cols.length; i++) {
					if (StringUtils.isNotBlank(cols[i])) {
						header.put(StringUtils.lowerCase(cols[i]), i);
					}
				}
				isValid = true;
			}
		} else {
			isValid = true;
		}
	}

	private String prepareValue(String unrepared) {
		String result = StringUtils.trim(unrepared);
		if (isCsv) {
			StringBuilder sb = new StringBuilder(result);
			if (sb.length() > 0 && sb.charAt(0) == '"')
				sb.deleteCharAt(0);
			if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '"')
				sb.deleteCharAt(sb.length() - 1);
			result = StringUtils.replace(sb.toString(), "\"\"", "\"");
		}
		return result;
	}

	public final String getValue(int colIndex) {
		return StringUtils.trim(currentRow[colIndex]);
	}

	public final Double getDoubleValue(int colIndex) {
		String val = getValue(colIndex);
		return DoubleDataType.parse(val);
	}

	public final BigDecimal getDecimalValue(int colIndex, int digitsAfterDot, BigDecimal...defaultVal) {
		String val = getValue(colIndex);
		BigDecimal bd = DecimalDataType.parse(val, digitsAfterDot);
		if (bd == null) {
			return (defaultVal == null || defaultVal.length == 0) ? null : defaultVal[0];
		}
		return bd;
	}

	public final String getValue(String colName) {
		Integer colIdx = header.get(StringUtils.lowerCase(colName));
		if (colIdx == null)
			return null;
		return getValue(colIdx);
	}

	public final Double getDoubleValue(String colName) {
		String val = getValue(colName);
		return DoubleDataType.parse(val);
	}

	public final BigDecimal getDecimalValue(String colName, int digitsAfterDot, BigDecimal...defaultVal) {
		String val = getValue(colName);
		BigDecimal bd = DecimalDataType.parse(val, digitsAfterDot);
		if (bd == null) {
			return (defaultVal == null || defaultVal.length == 0) ? null : defaultVal[0];
		}
		return bd;
	}

	public final void iterate(TableDataRowProcessor processor) throws Exception {
		if (!isValid) {
			String message = "TXT file is not valid";
			if (missingColumns != null && missingColumns.size() > 0) {
				message = "Отсутствуют обязательные колонки: " + StringUtils.join(missingColumns, ", ");
			}
			throw new EcommanderException(ErrorCodes.VALIDATION_FAILED, message);
		}
		try (BufferedReader br = Files.newBufferedReader(file.toPath(), fileCharset)) {
			for (int i = 0; i <= headerRow; i++) {
				br.readLine();
			}
			currentRowNum = headerRow;
			String line;
			while ((line = br.readLine()) != null) {
				currentRow = StringUtils.splitPreserveAllTokens(line, SEPARATOR_CHAR);
				for (int i = 0; i < currentRow.length; i++)
					currentRow[i] = prepareValue(currentRow[i]);
				processor.processRow(this);
				currentRowNum++;
			}
		} catch (FileNotFoundException e) {
			ServerLogger.error("file not found", e);
		} catch (IOException e) {
			ServerLogger.error("file IO error", e);
		}
	}

	public final TreeSet<String> getHeaders(){
		TreeSet<String> a = new TreeSet<>();
		a.addAll(header.keySet());
		return a;
	}

	@Override
	public int getRowNum() {
		return currentRowNum;
	}

	@Override
	public void close() throws IOException {

	}
}
