package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Collection;
import java.util.HashSet;

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
		String ignoreVar = getVarSingleValue("ignore");
		HashSet<String> ignore = new HashSet<>();
		for (String code : StringUtils.split(ignoreVar, ',')) {
			ignore.add((String) code);
		}
		info.setOperation("Создание разделов каталога и типов товаров");
		info.pushLog("Создание разделов");
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		YMarketCatalogCreationHandler secHandler = new YMarketCatalogCreationHandler(catalog, info, getInitiator(), ignore);
		info.setProcessed(0);
		for (File xml : xmls) {
			// Удалить DOCTYPE
//			if (removeDoctype(xml)) {
			parser.parse(xml, secHandler);
//			info.increaseProcessed();
//			} else {
//				addError("Невозможно удалить DOCTYPE " + xml, xml.getName());
//			}
		}

		// Создание самих товаров
		info.pushLog("Подготовка каталога и типов завершена.");
		info.pushLog("Создание товаров");
		info.setOperation("Создание товаров");
		info.setProcessed(0);
		YMarketProductCreationHandler prodHandler = new YMarketProductCreationHandler(secHandler.getSections(), info, getInitiator());
		for (File xml : xmls) {
			parser.parse(xml, prodHandler);
		}
		//executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(20, null).noFulltextIndex());

		info.pushLog("Создание товаров завершено");
		info.pushLog("Индексация");
		info.setOperation("Индексация");

		LuceneIndexMapper.getSingleton().reindexAll();

		info.pushLog("Индексация завершена");
		info.pushLog("Интеграция успешно завершена");
		info.setOperation("Интеграция завершена");
	}


	private static boolean removeDoctype(File file) throws FileNotFoundException {
		File tempFile = new File("__temp__.xml");
		final String DOCTYPE = "!DOCTYPE";
		boolean containsDoctype = false;
		final String LINE_SEPARATOR = System.getProperty("line.separator");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Strings.SYSTEM_ENCODING))) {
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

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tempFile), Strings.SYSTEM_ENCODING);
		     BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Strings.SYSTEM_ENCODING))) {
			String currentLine;
			boolean doctypeNotRemoved = true;
			while((currentLine = reader.readLine()) != null) {
				if (doctypeNotRemoved && StringUtils.contains(currentLine, DOCTYPE)) {
					doctypeNotRemoved = false;
					continue;
				}
				writer.write(currentLine);
				writer.write(LINE_SEPARATOR);
			}
		} catch (IOException e) {
			info.addError("Невозможно удалить DOCTYPE " + file.getName(), file.getName());
		}
		boolean success = file.delete();
		success &= tempFile.renameTo(file);
		return success;
	}

	@Override
	protected void terminate() throws Exception {

	}
}
