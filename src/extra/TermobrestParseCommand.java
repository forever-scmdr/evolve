package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class TermobrestParseCommand extends IntegrateBase implements CatalogConst {

    private static final long LIFESPAN = 3600000 * 24 * 20; //20 days
    private static final String INTEGRATION_DIR = "ym_integrate";
    private static final int LOAD_BATCH_SIZE = 1000;
    private static final int STATUS_BATCH_SIZE = 500;
    private File xml;
    Item catalog;
    Item sharedSection;

    @Override
    protected boolean makePreparations() throws Exception {
        try{
        xml = Files.list(Paths.get(AppContext.getContextPath(),INTEGRATION_DIR)).filter(f ->
                Files.isRegularFile(f)
                        && StringUtils.endsWith(f.getFileName().toString(), ".xml"))
                .findFirst().get().toFile();
        }catch (Exception e){
            ServerLogger.error("Insertion error",e);
            info.addError(e);
            pushLog("файл не найден");
            return false;
        }
        return true;
    }

    @Override
    protected void integrate() throws Exception {
        setOperation("Скрываем все товары");
        hideAllProducts();
        pushLog("Все товары скрыты");

        setOperation("Создание разделов");
        pushLog("Создание разделов");

        catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
        sharedSection = ensureSharedSection();
        TermobrestSectionHandler secHandler = new TermobrestSectionHandler(catalog, sharedSection, info, getInitiator());

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(xml, secHandler);
        pushLog("Создание разделов завершено");

        setOperation("Создание товаров");
        TermobrestProductHandler productHandler = new TermobrestProductHandler(info, getInitiator());
        parser.parse(xml, productHandler);
        pushLog("Создание товаров завершено");
    }

    private Item ensureSharedSection() throws Exception {
        Item s = ItemQuery.loadSingleItemByParamValue("shared_item_section",NAME_PARAM,"Структура обозначения");
        if (s == null){
            Item parent = ItemUtils.ensureSingleRootItem("shared_items", getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
            s = Item.newChildItem(ItemTypeRegistry.getItemType("shared_item_section"), parent);
            s.setValue(NAME_PARAM, "Структура обозначения");
            executeAndCommitCommandUnits(SaveItemDBUnit.get(s).noTriggerExtra().noFulltextIndex().ignoreUser());
        }
        return s;
    }

    private void hideAllProducts() throws Exception {
        setProcessed(0);
        Queue<Item> products = new LinkedList<>();
        products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, 0L));
        long id = 0;
        int counter = 0;
        while (products.size() > 0) {
            while (products.size() != 0) {
                Item product = products.poll();
                id = product.getId();
                executeCommandUnit(ItemStatusDBUnit.hide(product).ignoreUser(true).noFulltextIndex());
                counter++;
                if (counter >= STATUS_BATCH_SIZE) commitCommandUnits();
                info.increaseProcessed();
            }
            products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, id));
        }
        commitCommandUnits();
    }

    private void deleteLongAbsentProducts() throws Exception {
        setProcessed(0);
        Queue<Item> products = new LinkedList<>();

        long now = new Date().getTime();
        products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, 0L, Item.STATUS_HIDDEN));
        long id = 0;
        int counter = 0;
        while (products.size() > 0) {
            while (products.size() != 0) {
                Item product = products.poll();
                id = product.getId();
                if(now - product.getTimeUpdated() > LIFESPAN){
                    executeCommandUnit(ItemStatusDBUnit.delete(product.getId()).ignoreUser(true).noFulltextIndex());
                    counter++;
                    info.increaseProcessed();
                    if(counter > STATUS_BATCH_SIZE){
                        commitCommandUnits();
                    }
                }
            }
            products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, id));
        }
        commitCommandUnits();
    }


    @Override
    protected void terminate() throws Exception {}
}
