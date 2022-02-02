package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Создание каталога продукции по файлу Yandex Market
 * Created by E on 16/3/2018.
 */
public class YMarketCreateCatalogCommand extends IntegrateBase implements CatalogConst {
	private static final String INTEGRATION_DIR = "ym_integrate";
	private static final String GET_PRICE_PARAM = "get_price";
	private static final int LOAD_BATCH_SIZE = 1000;
	private static final int HIDE_BATCH_SIZE = 1000;

	private static boolean getPrice = false;

	@Override
	protected boolean makePreparations() throws Exception {
		getPrice = StringUtils.equalsAnyIgnoreCase(getVarSingleValueDefault(GET_PRICE_PARAM, "yes"), "yes", "true");
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

		//Скрыть все товары
		hideAllProducts();

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
			// Удалить DOCTYPE
			if (removeDoctype(xml)) {
				parser.parse(xml, secHandler);
				info.increaseProcessed();
			} else {
				addError("Невозможно удалить DOCTYPE " + xml, xml.getName());
			}
		}

		// Удаление всех пользовательских параметров товаров (айтемов и типов)
//		info.pushLog("Удаление параметров товаров");
//		int processed = 0;
//		info.setProcessed(processed);
//		ItemQuery paramsQuery = new ItemQuery(PARAMS_ITEM);
//		paramsQuery.setLimit(10);
//		List<Item> itemsToDelete = paramsQuery.loadItems();
//		while (itemsToDelete.size() > 0) {
//			for (Item item : itemsToDelete) {
//				executeCommandUnit(ItemStatusDBUnit.delete(item));
//			}
//			commitCommandUnits();
//			processed += itemsToDelete.size();
//			info.setProcessed(processed);
//			itemsToDelete = paramsQuery.loadItems();
//		}
//
//		LinkedHashSet<String> typesToDelete = ItemTypeRegistry.getItemExtenders(PARAMS_ITEM);
//		typesToDelete.remove(PARAMS_ITEM);
//		for (String typeToDelete : typesToDelete) {
//			executeAndCommitCommandUnits(new DeleteItemTypeBDUnit(ItemTypeRegistry.getItemType(typeToDelete).getTypeId()));
//		}
//
//		DataModelBuilder.newForceUpdate().tryLockAndReloadModel();

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

		info.pushLog("Создание товаров завершено");
		info.pushLog("Прикрепление картинок к разделам");
		info.setOperation("Прикрепление картинок к разделам");

		attachImages();

		info.pushLog("Прикрепление картинок к разделам завершено");
		info.pushLog("Индексация");
		info.setOperation("Индексация");

		LuceneIndexMapper.getSingleton().reindexAll();

		info.pushLog("Индексация завершена");
		info.setOperation("Создание фильтров");
		info.pushLog("Создание фильтров");

		//new CreateParametersAndFiltersCommand(this).integrate();

		info.pushLog("Создание фильтров завершено");
		info.pushLog("Интеграция успешно завершена");
		info.setOperation("Интеграция завершена");
	}

	private void attachImages() throws Exception {
		List<Item> sections = new ItemQuery(SECTION_ITEM).loadItems();
		for(Item section : sections){
			File mainPic = section.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(section.isFileProtected()));
			if(!mainPic.isFile()){
				ItemQuery q = new ItemQuery(PRODUCT_ITEM);
				q.setLimit(50);
				q.setParentId(section.getId(), true);
				//q.addParameterCriteria(MAIN_PIC_PARAM, "-", "!=", null, Compare.SOME);
				List<Item> products = q.loadItems();
				for(Item prod : products){
					mainPic = prod.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(prod.isFileProtected()));
					if(mainPic.isFile()){
						section.setValue(MAIN_PIC_PARAM, mainPic);
						executeAndCommitCommandUnits(SaveItemDBUnit.get(section));
						break;
					}
				}
			}
		}
	}

	private void hideAllProducts() throws Exception {
		setOperation("Скрываем товары");
		setProcessed(0);
		LinkedList<Item> products = new LinkedList<>();
		products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, 0L));
		long id = 0;
		int counter = 0;
		while (products.size() > 0) {
			while (products.size() != 0) {
				Item product = products.poll();
				id = product.getId();
				List<String> tags = product.getStringValues(TAG_PARAM);
				if(!tags.contains("external_shop")) {
					executeCommandUnit(ItemStatusDBUnit.hide(product).ignoreUser(true).noFulltextIndex());
					counter++;
				}
				if (counter >= HIDE_BATCH_SIZE) commitCommandUnits();
				info.increaseProcessed();
			}
			products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, id));
		}
		commitCommandUnits();
	}

	private boolean removeDoctype(File file) {
		File tempFile = new File("__temp__.xml");
		final String DOCTYPE = "!DOCTYPE";
		boolean containsDoctype = false;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			int i = 0;
			String currentLine;
			while ((currentLine = reader.readLine()) != null && i < 5) {
				i++;
				if (StringUtils.contains(currentLine, DOCTYPE)) {
					containsDoctype = true;
					break;
				}
			}
		} catch (IOException e) {
			info.addError("Невозможно прочитать файл " + file.getName(), file.getName());
		}
		if (!containsDoctype)
			return true;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		     BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String currentLine;
			boolean doctypeNotRemoved = true;
			while((currentLine = reader.readLine()) != null) {
				if (doctypeNotRemoved && StringUtils.contains(currentLine, DOCTYPE)) {
					doctypeNotRemoved = false;
					continue;
				}
				writer.write(currentLine);
				writer.newLine();
			}
		} catch (IOException e) {
			info.addError("Невозможно удалить DOCTYPE " + file.getName(), file.getName());
		}
		boolean success = file.delete();
		success &= tempFile.renameTo(file);
		return success;
	}

	@Override
	protected void terminate() {

	}
}
