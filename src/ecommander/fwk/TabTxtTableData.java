package ecommander.fwk;

import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.DoubleDataType;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by E on 14/1/2019.
 */
public class TabTxtTableData implements TableDataSource {
	private boolean isValid = false;
	private HashMap<String, Integer> header = new HashMap<>();
	private String[] currentRow;
	private int currentRowNum = -1;
	private int headerRow = -1;
	private String fileName;
	private ArrayList<String> missingColumns = null;

	public TabTxtTableData(String fileName, String... mandatoryCols) {
		this.fileName = Strings.getFileName(fileName);
		init(mandatoryCols);
	}

	public TabTxtTableData(File file, String... mandatoryCols) {
		this.fileName = file.getName();
		init(mandatoryCols);
	}

	public TabTxtTableData(Path path, String... mandatoryCols) {
		this.fileName = path.getFileName().toString();
		init(mandatoryCols);
	}

	private void init(String... mandatoryCols) {
		if (StringUtils.isBlank(fileName))
			return;
		if (!Files.exists(Paths.get(fileName)))
			return;

		ArrayList<String> missingColumnsTest;

		// Все названия колонок должны быть в одной строке
		boolean rowChecked = false;
		String line;
		String[] cols = {};
		if (mandatoryCols.length > 0) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
				line = br.readLine();
				while (line != null && headerRow < 1000 && !rowChecked) {
					headerRow++;
					cols = StringUtils.split(line, '\t');
					missingColumnsTest = new ArrayList<>();
					rowChecked = true;
					for (String checkCol : mandatoryCols) {
						if (!StringUtils.equalsAnyIgnoreCase(checkCol, cols)) {
							missingColumnsTest.add(checkCol);
							rowChecked = false;
						}
					}
					if (!rowChecked && (missingColumns == null || missingColumnsTest.size() < missingColumns.size())) {
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

	public final String getValue(int colIndex) {
		return StringUtils.trim(currentRow[colIndex]);
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
		Integer colIdx = header.get(StringUtils.lowerCase(colName));
		if (colIdx == null)
			return null;
		return getValue(colIdx);
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
			String message = "TXT file is not valid";
			if (missingColumns != null && missingColumns.size() > 0) {
				message = "Отсутствуют обязательные колонки: " + StringUtils.join(missingColumns, ", ");
			}
			throw new EcommanderException(ErrorCodes.VALIDATION_FAILED, message);
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"))) {
			for (int i = 0; i < headerRow; i++) {
				br.readLine();
			}
			currentRowNum = headerRow;
			String line;
			while ((line = br.readLine()) != null) {
				currentRow = StringUtils.split(line, '\t');
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
		return 0;
	}

	@Override
	public void close() throws IOException {

	}
}
