package ecommander.fwk;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class POIExcelWrapper implements Closeable {
	private Workbook xlsWorkbook = null;
	private POIFSFileSystem npoifs = null;
	private OPCPackage pkg = null;
	
	private POIExcelWrapper(File file) {
		try {
			try {
				npoifs = new POIFSFileSystem(file);
				xlsWorkbook = WorkbookFactory.create(npoifs);
			} catch (OfficeXmlFileException e) {
				ServerLogger.error("Trying to create from OPCPackage");
				pkg = OPCPackage.open(file);
				xlsWorkbook = new XSSFWorkbook(pkg);
			//	ServerLogger.error("Unable to parse Excel file", e);
			//	xlsWorkbook = null;
			}
		} catch (Exception ofe) {
			try {
				//pkg = OPCPackage.open(file);
				ServerLogger.error("Trying to create Directly");
				xlsWorkbook = new XSSFWorkbook(file);
			} catch (Exception e) {
				try {
					ServerLogger.error("Trying to create XSSFWorkbook");
					xlsWorkbook = new XSSFWorkbook(new FileInputStream(file));
				}catch (Exception e1) {
					ServerLogger.error("Unable to parse Excel file", e);
					xlsWorkbook = null;
				}
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
