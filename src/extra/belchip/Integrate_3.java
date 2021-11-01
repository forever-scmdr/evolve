package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand_3;
import ecommander.model.Item;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.Catalog;
import extra._generated.ItemNames;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
import java.util.HashSet;
import java.util.List;

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
public class Integrate_3 extends IntegrateBase {

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
		if (catalogs.size() != 1) {
			info.addError("Каталог должен быть создан и содрежать файл интеграции", "");
			return;
		}
		catalog = catalogs.get(0);
		// Проверить, есть ли файл для интеграции
		//integration = catalog.getFileValue("integration", AppContext.getFilesDirPath(catalog.isFileProtected()));
		integration = new File(AppContext.getRealPath(INTEGRATION_FILE));
		if (/*integration == null || */!integration.exists()) {
			info.addError("Не найден файл интеграции", "");
			return;
		}

		final Integrate_3 integrate = this;

		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		// Подсчет строк
		parser.parse(integration, new LineCounter(info));
		// Валидация
		info.setOperation("Валидация файла интеграции");
		info.addLog("Начало процесса валидации");
		if (info.getErrorCount() == 0)
			parser.parse(integration, new ValidationHandler(catalog, info));
		// Удаление каталога и файла с классами

		catalog.setValueUI(Catalog.INTEGRATION_PENDING, "1");
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser(true));

		info.addLog("Валидация завершена. Скрытие старого каталога");
		info.setOperation("Скрытие старого каталога");
		info.setProcessed(0);
		// Удалить все разделы
		List<Item> sections = new ItemQuery(ItemNames.SECTION).setParentId(catalog.getId(), false).loadItems();
		for (Item section : sections) {
			integrate.deletedBase += integrate.deletedCount;
			integrate.deletedCount = 0;
			ItemStatusDBUnit hide = ItemStatusDBUnit.hide(section);
			executeAndCommitCommandUnits(hide.ignoreUser(true).noFulltextIndex());
		}
		info.setProcessed(0);
		info.addLog("Каталог скрыт. Начало обновления разделов каталога");
		info.setOperation("Обновление разделов каталога");
		// Постороение списка разделов
		ProductClassHandler_3 sectionCreationHandler = new ProductClassHandler_3(catalog, info);
		if (info.getErrorCount() == 0)
			parser.parse(integration, sectionCreationHandler);
		info.addLog("Обновление разделов и классов завершено. Начало создания продукции");
		info.setOperation("Обновление продукции каталога");
		// Запись товаров в каталог
		if (info.getErrorCount() == 0)
			parser.parse(integration, new CatalogCreationHandler_3(catalog, info));
		info.setOperation("Заполнение аналогов и сопутствующих товаров");
		//File analogList = catalog.getFileValue(Catalog.ANALOGS, AppContext.getFilesDirPath(catalog.isFileProtected()));
		File analogList = new File(AppContext.getRealPath(ANALOGS_FILE));
		if (analogList.exists() && analogList.isFile()) {
			new AnalogHandler(info).parse(analogList.toPath());
		} else {
			info.addLog("Список аналогов не найден.");
		}

		info.addLog("Актуализация типов товаров и фильтров");
		info.setOperation("Актуализация типов товаров и фильтров");
		CreateParametersAndFiltersCommand_3 filterCommand = new CreateParametersAndFiltersCommand_3(this, sectionCreationHandler.getChangedSectionCodes());
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
			ServerLogger.error(info.getTimer().writeTotals());
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