package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.XmlDataSource;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class YMarketUpdateSectionCodes extends IntegrateBase {

    private static final String INTEGRATION_DIR = "ym_integrate";

    @Override
    protected boolean makePreparations() throws Exception {
        return true;
    }

    @Override
    protected void integrate() throws Exception {
        File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
        if (!integrationDir.exists()) {
            info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
            return;
        }
        Collection<File> xmls = FileUtils.listFiles(integrationDir, new String[] {"xml"}, true);
        if (xmls.size() == 0) {
            info.addError("Не найдены XML файлы в директории " + INTEGRATION_DIR, "init");
            return;
        }
        info.setToProcess(xmls.size());
        // Создание (обновление) каталога товаров
        info.setOperation("Обновление кодов разделов");
        info.pushLog("Обновление кодов разделов");
        for (File xml : xmls) {
            info.pushLog("Файл " + xml.getName());
            XmlDataSource ds = new XmlDataSource(xml.getAbsolutePath(), StandardCharsets.UTF_8);
            XmlDataSource.Node node = ds.findNextNode("category", 5000);
            while (node != null) {
                ds.scanCurrentNode();
                Document doc = node.getDoc();
                Element cat = doc.getElementsByTag("category").first();
                if (cat == null)
                    continue;
                String id = cat.attr("id");
                String name = StringUtils.normalizeSpace(cat.ownText());
                Item section = ItemQuery.loadSingleItemByParamValue(ItemNames.SECTION, ItemNames.section_.NAME, name);
                if (section != null) {
                    section.setValue(ItemNames.section_.CATEGORY_ID, id);
                    executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
                    info.increaseProcessed();
                }
                node = ds.findNextNode("category", 5000);
            }
            ds.closeDocument();
        }
    }

    @Override
    protected void terminate() throws Exception {

    }
}
