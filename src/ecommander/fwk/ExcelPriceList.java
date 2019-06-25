package ecommander.fwk;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.nio.file.Path;

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
