package ecommander.fwk;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class POIExcelWrapper implements Closeable {
	private Workbook xlsWorkbook = null;
	private POIFSFileSystem npoifs = null;
	private OPCPackage pkg = null;

	private POIExcelWrapper(File file) {
		xlsWorkbook = null;
		try {
			FileMagic fileType = FileMagic.valueOf(file);
			if (fileType == FileMagic.OOXML) {
				xlsWorkbook = XSSFWorkbookFactory.createWorkbook(file, true);
			} else {
				xlsWorkbook = WorkbookFactory.create(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static POIExcelWrapper create(String fileName) {
		return createInt(new File(fileName));
	}

	public static POIExcelWrapper create(File file) {
		return createInt(file);
	}

	public static POIExcelWrapper create(Path file) {
		return createInt(file.toFile());
	}

	private static POIExcelWrapper createInt(File file) {
		if (!file.exists())
			return null;
		POIExcelWrapper instance = new POIExcelWrapper(file);
		if (instance.xlsWorkbook == null)
			return null;
		return instance;
	}

	public Workbook getWorkbook() {
		return xlsWorkbook;
	}

	public void close() throws IOException {
		if (npoifs != null) {
			npoifs.close();
		}
		if (pkg != null) {
			pkg.close();
		}
	}
}
