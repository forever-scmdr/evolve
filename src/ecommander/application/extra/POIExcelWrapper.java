package ecommander.application.extra;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import ecommander.common.ServerLogger;

public class POIExcelWrapper {
	private Workbook xlsWorkbook = null;
	private NPOIFSFileSystem npoifs = null;
	private OPCPackage pkg = null;
	
	private POIExcelWrapper(File file) {
		try {
			try {
				npoifs = new NPOIFSFileSystem(file);
				xlsWorkbook = WorkbookFactory.create(npoifs);
			} catch (IOException e) {
				ServerLogger.error("Unable to parse Excel file", e);
				xlsWorkbook = null;
			}
		} catch (OfficeXmlFileException ofe) {
			try {
				pkg = OPCPackage.open(file);
				xlsWorkbook = WorkbookFactory.create(pkg);
			} catch (Exception e) {
				ServerLogger.error("Unable to parse Excel file", e);
				xlsWorkbook = null;
			}
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
