package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.XmlDataSource;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

public class UpdatePricesXML extends IntegrateBase implements CatalogConst {
    private static final String INTEGRATE_DIR = "integrate_xml/";
    private static final String REPORT_DIR = "report/";
//    private static final String REPORT_PREFIX = "report_";
    private static final String XML_FILE_NAME = INTEGRATE_DIR + "metabo_import.xml";
//    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy_HH.mm").withZoneUTC();
    public static final BigDecimal ZERO = new BigDecimal(0);

	private File reportDir;
	private XmlDataSource priceFile;
	private File priceXmlFile;
	private File reportFile;
	private File backupFile;

	@Override
	protected boolean makePreparations() throws Exception {
		priceXmlFile = new File(AppContext.getRealPath(XML_FILE_NAME));
		if (!priceXmlFile.exists())
			return false;
		priceFile = new XmlDataSource(AppContext.getRealPath(XML_FILE_NAME), StandardCharsets.UTF_8);
		reportDir = new File(AppContext.getRealPath(INTEGRATE_DIR + REPORT_DIR));
		reportDir.mkdirs();
		long date = System.currentTimeMillis();
		reportFile = new File (AppContext.getRealPath(INTEGRATE_DIR + REPORT_DIR + "report.txt"));
		backupFile = new File(AppContext.getRealPath(INTEGRATE_DIR + REPORT_DIR + "metabo_import.xml"));
		return true;
	}

	@Override
	protected void integrate() throws Exception {
	    info.setOperation("Обновление цен");
	    info.setProcessed(0);
        HashSet<String> updatedCodes = new HashSet<>();
        StringBuilder report = new StringBuilder("Products not found:\r\n");
		priceFile.findNextNode("Property", "name", "Data");
		priceFile.findNextNode("Value");
        while (priceFile.findNextNode("Value") != null) {
			String code = null;
			try {
				XmlDataSource.Node entry = priceFile.scanCurrentNode();
				Document doc = entry.getDoc();
				code = StringUtils.trim(doc.select("Property[name=Articul] Value").first().ownText());
                Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
                if (product == null) {
                    report.append(code).append('\t').append(doc.select("Property[name=TName] Value").first().ownText()).append("\r\n");
                    info.increaseProcessed();
                    continue;
                }
				String priceStr = doc.select("Property[name=PriceCost] Value").first().ownText();
				String qtyStr = doc.select("Property[name=KolOst] Value").first().ownText();
                BigDecimal qty = DecimalDataType.parse(qtyStr, 4);
                product.setValueUI(PRICE_PARAM, priceStr);
                product.setValueUI(QTY_PARAM, qtyStr);
                if (qty.compareTo(ZERO) > 0)
                    product.setValueUI(AVAILABLE_PARAM, "1");
                executeAndCommitCommandUnits(SaveItemDBUnit.get(product));
                updatedCodes.add(code);
                info.increaseProcessed();
			} catch (Exception e) {
				info.addError(e.getLocalizedMessage(), code);
			}
		}
		FileUtils.cleanDirectory(reportDir);
        FileUtils.write(reportFile, report, StandardCharsets.UTF_8);
		priceFile.finishDocument();
		info.addLog("Завершено обновление цен");
		info.addLog("Обновление отсутсвующих товаров");

        info.setOperation("Обновление статуса отсутсвующих товаров");
        info.setProcessed(0);
        ItemQuery query = new ItemQuery(PRODUCT_ITEM).setLimit(10);
        List<Item> products;
        long lastId = 0;
        do {
            query.setIdSequential(lastId);
            products = query.loadItems();
            for (Item product : products) {
            	lastId = product.getId();
                if (!updatedCodes.contains(product.getStringValue(CODE_PARAM, ""))) {
                    product.setValueUI(QTY_PARAM, "0");
                    product.setValueUI(AVAILABLE_PARAM, "0");
                    executeAndCommitCommandUnits(SaveItemDBUnit.get(product));
                    info.increaseProcessed();
                }
            }
        } while (products.size() > 0);
		info.addLog("Завершено обновление отсутсвующих товаров");
		FileUtils.moveFile(priceXmlFile, backupFile);

        info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
