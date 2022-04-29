package extra.belchip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ecommander.fwk.*;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.DataModelBuilder;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import extra._generated.Catalog;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;


import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;

/**
 * Интеграция файла XML Результаты валидации и выполнения в след. виде
 * 
 * 
 * <page> <message>Валидация показала наличие следующих ошибок. Интеграция не произведена</message> <error line="10" coloumn="30">Сообщение об
 * ошибке 1</error> <error line="20" coloumn="30">Сообщение об ошибке 2</error> .............. <error line="500" coloumn="40">Сообщение об ошибке
 * 50</error> </page>
 * 
 * 
 * @author EEEE
 * 
 */
public class Integrate_2 extends IntegrateBase {

	private static final String INTEGRATION_FILE = "integrate/WebFile.xml";
	private static final String ANALOGS_FILE = "integrate/analogs.txt";

	/**
	 * Основные элементы - айтемы
	 */
	private File integration;
	private int deletedCount = 0;
	private int deletedBase = 0;

	private Item catalog; // Корневой айтем каталога продукции

	@Override
	protected void integrate() throws Exception {
		// Загрузить каталог продукции
		List<Item> catalogs = new ItemQuery(ItemNames.CATALOG).loadItems();
//		if (catalogs.size() != 1) {
//			info.addError("Каталог должен быть создан и содрежать файл интеграции", "");
//			return;
//		}
		//catalog = catalogs.get(0);
		// Проверить, есть ли файл для интеграции
		//integration = catalog.getFileValue("integration", AppContext.getFilesDirPath(catalog.isFileProtected()));
		integration = new File(AppContext.getRealPath(INTEGRATION_FILE));
		if (/*integration == null || */!integration.exists()) {
			info.addError("Не найден файл интеграции", "");
			return;
		}

		final Integrate_2 integrate = this;

		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		// Подсчет строк
		parser.parse(integration, new LineCounter(info));

		// Удаление каталога и файла с классами
		info.addLog("Удаление старого каталога");
		info.setOperation("Удаление старого каталога");
		info.setProcessed(0);
		/*
		// Удалить все разделы
		List<Item> sections = new ItemQuery(ItemNames.SECTION).loadItems();
		for (Item section : sections) {
			integrate.deletedBase += integrate.deletedCount;
			integrate.deletedCount = 0;
			ItemStatusDBUnit delete = ItemStatusDBUnit.delete(section);
			executeAndCommitCommandUnits(delete.ignoreUser(true).noFulltextIndex().noTriggerExtra());
		}
		*/
		for (Item cat : catalogs) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(cat).noFulltextIndex().noTriggerExtra());
		}
		catalog = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.CATALOG, getInitiator());
		catalog.setValueUI(Catalog.INTEGRATION_PENDING, "1");
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser(true));

		info.setProcessed(0);
		info.addLog("Каталог удален. Начало валидации");


		// Валидация
		info.setOperation("Валидация файла интеграции");
		info.addLog("Начало процесса валидации");
		if (info.getErrorCount() == 0)
			parser.parse(integration, new ValidationHandler(catalog, info));


		// Постороение списка разделов
		info.setOperation("Создание разделов каталога");
		info.addLog("Валидация завершена. Начало создания разделов каталога");
		if (info.getErrorCount() == 0)
			parser.parse(integration, new ProductClassHandler(catalog, info));
		info.addLog("Создание разделов и классов завершено. Начало создания продукции");
		info.setOperation("Создание продукции каталога");
		// Запись товаров в каталог
		if (info.getErrorCount() == 0)
			parser.parse(integration, new CatalogCreationHandler(catalog, info));
		info.setOperation("Заполнение аналогов и сопутствующих товаров");
		//File analogList = catalog.getFileValue(Catalog.ANALOGS, AppContext.getFilesDirPath(catalog.isFileProtected()));
		File analogList = new File(AppContext.getRealPath(ANALOGS_FILE));
		if (analogList.exists() && analogList.isFile()) {
			new AnalogHandler(info).parse(analogList.toPath());
		} else {
			info.addLog("Список аналогов не найден.");
		}

		info.addLog("Создание типов товаров и фильтров");
		info.setOperation("Создание типов товаров и фильтров");
		CreateParametersAndFiltersCommand filterCommand = new CreateParametersAndFiltersCommand(this);
		filterCommand.doCreateParametersAndFilters(info);

		info.setOperation("Текстовая индексация");
		info.addLog("Создание продукции завершено. Начало текстовой индексации");
		LuceneIndexMapper.getSingleton().reindexAll();
		info.addLog("Индексация завершена");
		// Установление даты обновления каталога
		long h = (long) 3 * 60 * 60 * 1000;
		catalog.setValue(Catalog.DATE, System.currentTimeMillis() + h);
		catalog.setValueUI(Catalog.INTEGRATION_PENDING, "0");
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser(true));
		PageController.clearCache();

	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}


	@Override
	protected void terminate() throws Exception {

	}

	private ResultPE buildResult() throws EcommanderException, IOException {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("page");
		info.output(doc);
		doc.endElement();
		ResultPE result = null;
		try {
			result = getResult("success");
			ServerLogger.warn(info.getTimer().writeTotals());
		} catch (EcommanderException e) {
			ServerLogger.error("no result found", e);
			return null;
		}
		result.setValue(doc.toString());
		return result;
	}



	private void enableModelCustomGroupWrite() throws Exception {
		Path modelCustomFolder = Paths.get(AppContext.getModelPath());
		Path modelCustomFile = Paths.get(AppContext.getModelPath(), "model_custom.xml");
		
		if(!modelCustomFile.toFile().isFile()) {info.addLog("model_custom.xml does not exist."); return;}
		//UserPrincipal owner = Files.getOwner(modelCustomFolder);
		GroupPrincipal group = Files.readAttributes(modelCustomFolder, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();

		//Files.setOwner(modelCustomFile, owner);
		Files.getFileAttributeView(modelCustomFile, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).setGroup(group);

		HashSet<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);

		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);

		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		Files.setPosixFilePermissions(modelCustomFile, perms);

	}

	public void receiveDeletedCount(int deletedCount) {
		this.deletedCount = deletedCount;
		info.setProcessed(this.deletedBase + deletedCount);
	}

}