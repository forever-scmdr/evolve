package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.filter.CriteriaDef;
import ecommander.model.filter.FilterDefinition;
import ecommander.model.filter.InputDef;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;

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
public class YMarketCreateCatalogCommand extends IntegrateBase implements YMarketConst {
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
		info.setOperation("Содание разделов каталога и типов товаров");
		info.pushLog("Создание разделов");
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		YMarketCatalogCreationHandler secHandler = new YMarketCatalogCreationHandler(catalog, info, getInitiator());
		info.setProcessed(0);
		for (File xml : xmls) {
			parser.parse(xml, secHandler);
			info.increaseProcessed();
		}

		// Разбор всех параметров товаров по разделам. Операции с БД не происходят
		info.setToProcess(xmls.size());
		info.pushLog("Разбор типов товаров");
		info.setProcessed(0);
		YMarketProductClassHandler classHandler = new YMarketProductClassHandler(info);
		for (File xml : xmls) {
			parser.parse(xml, classHandler);
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

		// Создание новых типов айтемов
		info.pushLog("Создание новых параметров товаров. Создание фильтров");
		info.setToProcess(classHandler.getParams().size());
		info.setProcessed(0);
		info.setToProcess(0);
		for (String categoryId : classHandler.getParams().keySet()) {
			Item section = secHandler.getSections().get(categoryId);
			if (section != null) {
				YMarketProductClassHandler.Params sectionParams = classHandler.getParams().get(categoryId);
				String className = "p" + categoryId;
				String classCaption = section.getStringValue(NAME_PARAM);
				// Создать фильтр и установить его в айтем
				FilterDefinition filter = FilterDefinition.create("");
				filter.setRoot(className);
				for (String paramName : sectionParams.paramTypes.keySet()) {
					if (sectionParams.notInFilter.contains(paramName))
						continue;
					String caption = sectionParams.paramCaptions.get(paramName);
					InputDef input = new InputDef("droplist", caption, "", "");
					filter.addPart(input);
					input.addPart(new CriteriaDef("=", paramName, sectionParams.paramTypes.get(paramName), ""));
				}
				section.setValue(PARAMS_FILTER_PARAM, filter.generateXML());
				executeAndCommitCommandUnits(SaveItemDBUnit.get(section));

				// Создать класс для продуктов из этого раздела
				ItemType newClass = new ItemType(className, 0, classCaption, "", "",
						PARAMS_ITEM, null, false, true, false, false);
				for (String paramName : sectionParams.paramTypes.keySet()) {
					String type = sectionParams.paramTypes.get(paramName).toString();
					String caption = sectionParams.paramCaptions.get(paramName);
					newClass.putParameter(new ParameterDescription(paramName, 0, type, false, 0,
							"", caption, "", "", false, false, null, null));
				}
				executeAndCommitCommandUnits(new SaveNewItemTypeDBUnit(newClass));
			}
			info.increaseProcessed();
		}

		DataModelBuilder.newForceUpdate().tryLockAndReloadModel();

		// Создание самих товаров
		info.pushLog("Подготовка каталога и типов завершена.");
		info.pushLog("Создание товаров");
		info.setOperation("Содание товаров");
		info.setProcessed(0);
		YMarketProductCreationHandler prodHandler = new YMarketProductCreationHandler(secHandler.getSections(), info, getInitiator());
		for (File xml : xmls) {
			parser.parse(xml, prodHandler);
		}

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
