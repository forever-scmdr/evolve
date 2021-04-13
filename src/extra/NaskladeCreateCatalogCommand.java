package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.YMarketCatalogCreationHandler;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Collection;
import java.util.List;

public class NaskladeCreateCatalogCommand extends IntegrateBase implements CatalogConst {
	private static final String INTEGRATION_DIR = "ym_integrate";
	private Collection<File> xmls;

	@Override
	protected void integrate() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		setOperation("Создание разделов");
		pushLog("Создание разделов");
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		YMarketCatalogCreationHandler secHandler = new YMarketCatalogCreationHandler(catalog, info, getInitiator());

		for (File xml : xmls) {
			// Удалить DOCTYPE
			if (removeDoctype(xml)) {
				parser.parse(xml, secHandler);
				info.increaseProcessed();
			} else {
				addError("Невозможно удалить DOCTYPE " + xml, xml.getName());
			}
		}
		pushLog("Создание разделов завершено");

		setOperation("Создание товаров");
		pushLog("Создание товаров");
		setProcessed(0);

		DefaultHandler prodHandler = new NaskladeProductCreationHandler(secHandler.getSections(), info, getInitiator());

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
	}

	@Override
	protected boolean makePreparations() throws Exception {
		File integrationDir = new File(AppContext.getRealPath(INTEGRATION_DIR));
		if (!integrationDir.exists()) {
			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return false;
		}
		xmls = FileUtils.listFiles(integrationDir, new String[] {"xml"}, true);
		if (xmls.size() == 0) {
			info.addError("Не найдены XML файлы в директории " + INTEGRATION_DIR, "init");
			return false;
		}
		info.setToProcess(xmls.size());
		return true;
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
	protected void terminate() throws Exception {}
}
