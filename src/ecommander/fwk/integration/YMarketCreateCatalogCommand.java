package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra.UniaProductCreationHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Создание каталога продукции по файлу Yandex Market
 * Created by E on 16/3/2018.
 */
public class YMarketCreateCatalogCommand extends IntegrateBase implements CatalogConst {
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

		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		// Создание (обновление) каталога товаров
		info.setOperation("Создание разделов каталога");
		info.pushLog("Создание разделов");
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		info.setProcessed(0);
		HashMap<String, YMarketCatalogCreationHandler> secHandlers = new HashMap<>();
		for (File xml : xmls) {
			if (StringUtils.containsIgnoreCase(xml.getName(), "products")) {
				Item productsSection = new ItemQuery("section").setParentId(catalog.getId(), false)
						.addParameterEqualsCriteria("code", "products").loadFirstItem();
				if (productsSection == null) {
					productsSection = Item.newChildItem(ItemTypeRegistry.getItemType("section"), catalog);
					productsSection.setValue("name", "Товары");
					productsSection.setValue("code", "products");
					executeAndCommitCommandUnits(SaveItemDBUnit.get(productsSection));
				}
				YMarketCatalogCreationHandler prodSecHandler = new YMarketCatalogCreationHandler(productsSection, info, getInitiator());
				secHandlers.put(xml.getName(), prodSecHandler);
				// Удалить DOCTYPE
				if (removeDoctype(xml)) {
					parser.parse(xml, prodSecHandler);
					info.increaseProcessed();
				} else {
					addError("Невозможно удалить DOCTYPE " + xml, xml.getName());
				}

			} else if (StringUtils.containsIgnoreCase(xml.getName(), "parts")) {
				Item partsSection = new ItemQuery("section").setParentId(catalog.getId(), false)
						.addParameterEqualsCriteria("code", "parts").loadFirstItem();
				if (partsSection == null) {
					partsSection = Item.newChildItem(ItemTypeRegistry.getItemType("section"), catalog);
					partsSection.setValue("name", "Запчасти");
					partsSection.setValue("code", "parts");
					executeAndCommitCommandUnits(SaveItemDBUnit.get(partsSection));
				}
				new YMarketCatalogCreationHandler(partsSection, info, getInitiator());
				YMarketCatalogCreationHandler partsSecHandler = new YMarketCatalogCreationHandler(partsSection, info, getInitiator());
				secHandlers.put(xml.getName(), partsSecHandler);
				// Удалить DOCTYPE
				if (removeDoctype(xml)) {
					parser.parse(xml, partsSecHandler);
					info.increaseProcessed();
				} else {
					addError("Невозможно удалить DOCTYPE " + xml, xml.getName());
				}
			}
		}

		// Создание самих товаров
		info.pushLog("Подготовка каталога завершена.");
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);

		List<Item> sectionsWithNewItemTypes = new ArrayList<>();

		for (File xml : xmls) {

			YMarketCatalogCreationHandler secHandler = secHandlers.get(xml.getName());
			String productItemTypeName = StringUtils.containsIgnoreCase(xml.getName(), "parts")? "part" : "complex_product";
			UniaProductCreationHandler prodHandler = new UniaProductCreationHandler(secHandler.getSections(),info,getInitiator());
			prodHandler.setProductType(productItemTypeName);
			parser.parse(xml, prodHandler);
			sectionsWithNewItemTypes.addAll(prodHandler.getSectionsWithNewItemTypes());
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

		new CreateParametersAndFiltersCommand(this).doCreate(sectionsWithNewItemTypes);
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
				q.setLimit(100);
				q.setParentId(section.getId(), true);
//				q.addParameterCriteria("pic_link", "", "!=", null, Compare.SOME);
				List<Item> products = q.loadItems();
				for(Item prod : products){
					List<String>mainPics = prod.outputValues("pic_link");
					if(mainPics.size() > 0){
						section.setValueUI("main_pic_path", mainPics.get(0));
						executeAndCommitCommandUnits(SaveItemDBUnit.get(section));
						break;
					}
				}
			}
		}
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
