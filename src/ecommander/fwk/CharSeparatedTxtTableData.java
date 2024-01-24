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
import java.util.*;

/**
 * Created by E on 14/1/2019.
 */
public class CharSeparatedTxtTableData implements TableDataSource {

	public static final String UTF8_BOM = "\uFEFF";

	private boolean isValid = false;
	private LinkedHashMap<String, Integer> header = new LinkedHashMap<>();
	private String[] currentRow;
	private int currentRowNum = -1;
	private int headerRow = -1;
	private File file;
	private ArrayList<String> missingColumns = null;
	private Charset fileCharset;
	private boolean isCsv = false;
	private char SEPARATOR_CHAR = '\t';

	public CharSeparatedTxtTableData(String fileName, Charset charset, String... mandatoryCols) {
		File f = new File(fileName);
		populateAndInit(f,charset,'\t', mandatoryCols);
	}

	public CharSeparatedTxtTableData(String fileName, Charset charset, boolean isCsv, String... mandatoryCols) {
		File f = new File(fileName);
		char sep = isCsv? ';' : '\t';
		populateAndInit(f,charset,sep, mandatoryCols);
	}

	public CharSeparatedTxtTableData(String fileName, Charset charset, char separator, String... mandatoryCols){
		File f = new File(fileName);
		populateAndInit(f,charset,separator, mandatoryCols);
	}

	public CharSeparatedTxtTableData(File file, Charset charset, String... mandatoryCols) {
		populateAndInit(file,charset,'\t', mandatoryCols);
	}

	public CharSeparatedTxtTableData(Path path, Charset charset, String... mandatoryCols) {
		populateAndInit(path.toFile(), charset,'\t', mandatoryCols);
	}

	public CharSeparatedTxtTableData(Path path, Charset charset, char separator, String... mandatoryCols) {
		populateAndInit(path.toFile(), charset,separator, mandatoryCols);
	}

	public CharSeparatedTxtTableData(File file, Charset charset, char separator, String... mandatoryCols){
		populateAndInit(file, charset, separator, mandatoryCols);
	}
	private void populateAndInit(File file, Charset charset, char separator, String... mandatoryCols){
		this.file = file;
		this.fileCharset = charset;
		SEPARATOR_CHAR = separator;
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
		if (currentRow.length > colIndex)
			return StringUtils.trim(currentRow[colIndex]);
		return null;
	}

	public final Double getDoubleValue(int colIndex) {
		String val = getValue(colIndex);
		return DoubleDataType.parse(val);
	}

	@Override
	public BigDecimal getDecimalValue(int colIndex, int digitsAfterDot, BigDecimal... defaultVal) {
		return null;
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
		Integer colIdx = header.get(StringUtils.lowerCase(colName));
		if (colIdx == null)
			return null;
		return getValue(colIdx);
	}

	public final Double getDoubleValue(String colName) {
		String val = getValue(colName);
		return DoubleDataType.parse(val);
	}

	@Override
	public BigDecimal getDecimalValue(String colName, int digitsAfterDot, BigDecimal... defaultVal) {
		return null;
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

	public final LinkedHashSet<String> getHeaders() {
		return new LinkedHashSet<>(header.keySet());
	}

	@Override
	public int getRowNum() {
		return currentRowNum;
	}

	@Override
	public void close() throws IOException {

	}
}
