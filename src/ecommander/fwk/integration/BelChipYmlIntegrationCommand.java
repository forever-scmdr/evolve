package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class BelChipYmlIntegrationCommand extends IntegrateBase implements CatalogConst {

	private static final String INTEGRATION_DIR = "ym_integrate";
	private static final String FILE_URL = "https://belchip.by/sitefiles/yandex_market.xml";
	public static final long PRODUCT_LIFESPAN = 180 * 24 * 3600000;
	//1GB
	private static final int MAX_FILE_SIZE = 1073741824;
	//public static final long PRODUCT_LIFESPAN = 600000;
	private long now = new Date().getTime();
	private File xmls;


	@Override
	protected boolean makePreparations() throws Exception {
		URL fileUrl = new URL(FILE_URL);
		Path destPath = Paths.get(AppContext.getRealPath(INTEGRATION_DIR), "yandex_market.xml");
		FileUtils.deleteQuietly(destPath.toFile());
		info.setCurrentJob("Скачивание файла");
		FileUtils.copyURLToFile(fileUrl, destPath.toFile());
		info.pushLog("Файл скачан");
		xmls = destPath.toFile();
//		File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
//		if (!integrationDir.exists()) {
//			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
//			return false;
//		}
//		xmls = FileUtils.listFiles(integrationDir, new String[]{"xml"}, true);
//		if (xmls.size() == 0) {
//			info.addError("Не найдены XML файлы в директории " + INTEGRATION_DIR, "init");
//			return false;
//		}
//		info.pushLog("Файлов найдено: " + xmls.size());
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		YMarketCatalogCreationHandler secHandler = new YMarketCatalogCreationHandler(catalog, info, getInitiator());
		info.setProcessed(0);
		//for (File xml : xmls) {
			// Удалить DOCTYPE
			if (removeDoctype(xmls)) {
				parser.parse(xmls, secHandler);
				info.increaseProcessed();
			} else {
				addError("Невозможно удалить DOCTYPE " + xmls, xmls.getName());
			}
		//}

		// Создание самих товаров
		info.setCurrentJob("");
		info.setOperation("Создание разделов");
		info.pushLog("Подготовка каталога и типов завершена.");
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		DefaultHandler prodHandler = new BelchipYandexProductCreationHandler(secHandler.getSections(), info, getInitiator());
		//for (File xml : xmls) {
			parser.parse(xmls, prodHandler);
		//}
		info.pushLog("Создание товаров завершено");

		info.setCurrentJob("Удаление долго отсутствовавших товров");
		info.pushLog("Удаление долго отсутствовавших товров");
		removeOldHiddenProducts();
		info.pushLog("Удаление долго отсутствовавших товров завершено");

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

		new CreateParametersAndFiltersCommand(this).integrate();

		info.pushLog("Создание фильтров завершено");
		info.pushLog("Интеграция успешно завершена");
		info.setOperation("Интеграция завершена");
	}

	private void removeOldHiddenProducts() throws Exception {
		ItemQuery q = new ItemQuery(PRODUCT_ITEM, Item.STATUS_HIDDEN);
		q.addParameterCriteria(CODE_PARAM, "b_%", "like", null, Compare.SOME);
		int i = 0;
		final int lim = 500;
		List<Item> hiddenDevices;
		info.setToProcess(0);
		info.setProcessed(0);
		do {
			i++;
			hiddenDevices = q.setLimit(lim, i).loadItems();
			for(Item hidden : hiddenDevices){
				if(now - hidden.getTimeUpdated() > PRODUCT_LIFESPAN){
					executeAndCommitCommandUnits(ItemStatusDBUnit.delete(hidden.getId()).ignoreUser(true).noFulltextIndex());
					info.increaseProcessed();
				}
			}
		}while (hiddenDevices.size() == lim);
	}

	@Override
	protected void terminate() throws Exception {
	}

	private void attachImages() throws Exception {
		List<Item> sections = new ItemQuery(SECTION_ITEM).loadItems();
		for (Item section : sections) {
			File mainPic = section.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(section.isFileProtected()));
			if (!mainPic.isFile()) {
				ItemQuery q = new ItemQuery(PRODUCT_ITEM);
				q.setLimit(50);
				q.setParentId(section.getId(), true);
				//q.addParameterCriteria(MAIN_PIC_PARAM, "-", "!=", null, Compare.SOME);
				List<Item> products = q.loadItems();
				for (Item prod : products) {
					mainPic = prod.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(prod.isFileProtected()));
					if (mainPic.isFile()) {
						section.setValue(MAIN_PIC_PARAM, mainPic);
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
			while ((currentLine = reader.readLine()) != null) {
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
}
