package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

/**
 * Создание каталога продукции по файлу Yandex Market
 * Created by E on 16/3/2018.
 */
public class CreateUsersCommand extends IntegrateBase {

	@Override
	protected boolean makePreparations() throws Exception {

		return true;
	}

	@Override
	protected void integrate() throws Exception {
		Item userCatalog = ItemQuery.loadSingleItemByName(ItemNames.REGISTERED_CATALOG);
		if (userCatalog == null || userCatalog.isValueEmpty(ItemNames.registered_catalog_.USERS_FILE)) {
			info.addError("Разбор невозможен. Не найден каталог пользователей", "каталог пользователей");
			return;
		}
		File xmlFile = userCatalog.getFileValue(ItemNames.registered_catalog_.USERS_FILE, AppContext.getFilesDirPath(userCatalog.isFileProtected()));
		if (!xmlFile.exists()) {
			info.addError("Разбор невозможен. Не найден каталог пользователей", "каталог пользователей");
			return;
		}

		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		// Создание (обновление) каталога товаров
		info.setOperation("Создание и обновление пользователей");
		info.pushLog("Начало работы");
		info.setProcessed(0);
		UserCreationHandler prodHandler = new UserCreationHandler(info, getInitiator());
		try {
			parser.parse(xmlFile, prodHandler);
		} catch (Exception e) {
			info.addError("Разбор файла невозможен. Файл не валиден", e.getLocalizedMessage());
			return;
		}

		info.pushLog("Работа завершена");
		info.setOperation("Интеграция завершена");
	}


	@Override
	protected void terminate() throws Exception {

	}
}
