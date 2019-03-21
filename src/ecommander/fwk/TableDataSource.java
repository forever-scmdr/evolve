package ecommander.fwk;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.TreeSet;

/**
 * Created by E on 14/1/2019.
 */
public interface TableDataSource extends Closeable {
	String getValue(int colIndex);

	Double getDoubleValue(int colIndex);

	BigDecimal getCurrencyValue(int colIndex, BigDecimal... defaultVal);

	String getValue(String colName);

	Double getDoubleValue(String colName);

	BigDecimal getCurrencyValue(String colName, BigDecimal... defaultVal);

	void iterate(TableDataRowProcessor processor) throws Exception;

	TreeSet<String> getHeaders();

	int getRowNum();
}
