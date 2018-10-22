package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.*;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Создание каталога продукции по файлу Yandex Market
 * Created by E on 16/3/2018.
 */
public class YMarketCreateCatalogCommand extends IntegrateBase implements CatalogConst {
	private static final String INTEGRATION_DIR = "ym_integrate";
	private static final String GET_PRICE_PARAM = "get_price";

	private static boolean getPrice = false;

	@Override
	protected boolean makePreparations() throws Exception {
		getPrice = StringUtils.equalsAnyIgnoreCase(getVarSingleValueDefault(GET_PRICE_PARAM, "no"), "yes", "true");
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

		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		// Создание (обновление) каталога товаров
		info.setOperation("Создание разделов каталога и типов товаров");
		info.pushLog("Создание разделов");
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		YMarketCatalogCreationHandler secHandler = new YMarketCatalogCreationHandler(catalog, info, getInitiator());
		info.setProcessed(0);
		for (File xml : xmls) {
			parser.parse(xml, secHandler);
			info.increaseProcessed();
		}

		// Удаление всех пользовательских параметров товаров (айтемов и типов)
		info.pushLog("Удаление параметров товаров");
		ItemQuery paramsQuery = new ItemQuery(PARAMS_ITEM);
		paramsQuery.setLimit(100);
		List<Item> itemsToDelete = paramsQuery.loadItems();
		while (itemsToDelete.size() > 0) {
			for (Item item : itemsToDelete) {
				executeCommandUnit(ItemStatusDBUnit.delete(item));
			}
			commitCommandUnits();
			executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(100, null));
			itemsToDelete = paramsQuery.loadItems();
		}
		LinkedHashSet<String> typesToDelete = ItemTypeRegistry.getItemExtenders(PARAMS_ITEM);
		typesToDelete.remove(PARAMS_ITEM);
		for (String typeToDelete : typesToDelete) {
			executeAndCommitCommandUnits(new DeleteItemTypeBDUnit(ItemTypeRegistry.getItemType(typeToDelete).getTypeId()));
		}

		DataModelBuilder.newForceUpdate().tryLockAndReloadModel();

		// Создание самих товаров
		info.pushLog("Подготовка каталога и типов завершена.");
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		YMarketProductCreationHandler prodHandler = new YMarketProductCreationHandler(secHandler.getSections(), info, getInitiator());
		prodHandler.getPrice(getPrice);
		for (File xml : xmls) {
			parser.parse(xml, prodHandler);
		}
		executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(20, null));

		info.pushLog("Создание товаров завершено");
		info.pushLog("Индексация");
		info.setOperation("Индексация");

		LuceneIndexMapper.getSingleton().reindexAll();

		info.pushLog("Индексация завершена");
		info.pushLog("Интеграция успешно завершена");
		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
