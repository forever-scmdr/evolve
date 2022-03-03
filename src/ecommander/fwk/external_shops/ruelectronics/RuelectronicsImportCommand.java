package ecommander.fwk.external_shops.ruelectronics;

import ecommander.controllers.AppContext;
import ecommander.fwk.WebClient;
import ecommander.fwk.external_shops.AbstractShopImport;
import ecommander.fwk.integration.CatalogConst;
import org.apache.commons.io.FileUtils;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.nio.file.Paths;

public class RuelectronicsImportCommand extends AbstractShopImport implements CatalogConst {
	private static final String SHOP_NAME = "ruelectronics.com";
	private static final String INTEGRATION_DIR = "upload/ruelectronics";
	private static final String DOWNLOAD_URL = "http://zip-2002.ru/vip/price/zipstock.xml";
	private static final String CATALOG_FILE_NAME = "ruelectronics.xml";

	private File xmlData;

	@Override
	protected boolean downloadData() throws Exception {
		File oldFile = Paths.get(AppContext.getRealPath(INTEGRATION_DIR), CATALOG_FILE_NAME).toFile();
		if(oldFile.exists()){
			FileUtils.deleteQuietly(oldFile);
		}
		WebClient.saveFile(DOWNLOAD_URL, AppContext.getRealPath(INTEGRATION_DIR), CATALOG_FILE_NAME);
		xmlData = Paths.get(AppContext.getRealPath(INTEGRATION_DIR), CATALOG_FILE_NAME).toFile();
		return xmlData.isFile();
	}

	@Override
	protected void processData() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		DefaultHandler handler = new RuelectronicsProductHandler(catalog, currency, info, getInitiator());
		parser.parse(xmlData, handler);
	}

	@Override
	protected String getShopName() {
		return SHOP_NAME;
	}
}
