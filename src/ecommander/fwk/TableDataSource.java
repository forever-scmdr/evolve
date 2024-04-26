package ecommander.fwk;

import java.io.Closeable;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by E on 14/1/2019.
 */
public interface TableDataSource extends Closeable {
	String getValue(int colIndex);

	Double getDoubleValue(int colIndex);

	BigDecimal getDecimalValue(int colIndex, int digitsAfterDot, BigDecimal... defaultVal);

	String getValue(String colName);

	int getLastColIndex();

	Double getDoubleValue(String colName);

	BigDecimal getDecimalValue(String colName, int digitsAfterDot, BigDecimal... defaultVal);

	void iterate(TableDataRowProcessor processor) throws Exception;

	Collection<String> getHeaders();

	int getRowNum();
}
