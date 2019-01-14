package ecommander.fwk;

import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.DoubleDataType;
import org.apache.commons.fileupload.FileItem;
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
public abstract class ExcelPriceList extends ExcelTableData implements TableDataRowProcessor {

	public ExcelPriceList(String fileName, String... mandatoryCols) {
		super(fileName, mandatoryCols);
	}

	public ExcelPriceList(File file, String... mandatoryCols) {
		super(file, mandatoryCols);
	}

	public ExcelPriceList(Path path, String... mandatoryCols) {
		super(path, mandatoryCols);
	}

	public ExcelPriceList(FileItem fileItem, String... mandatoryCols) throws Exception {
		super(fileItem, mandatoryCols);
	}

	protected abstract void processSheet() throws Exception;
	protected abstract void processRow() throws Exception;

	@Override
	public void processRow(TableDataSource src) throws Exception {
		processRow();
	}

	public final void iterate() throws Exception {
		iterate(this);
	}
}
