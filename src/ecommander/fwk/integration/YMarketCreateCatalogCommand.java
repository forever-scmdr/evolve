package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra.CacheAndCleanHidden;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
		boolean justPrice = Boolean.parseBoolean(StringUtils.lowerCase(getVarSingleValueDefault("just_price", "false")));
		if (!integrationDir.exists()) {
			info.addError("Не найдена директория интеграции " + INTEGRATION_DIR, "init");
			return;
		}
		Collection<File> xmls = FileUtils.listFiles(integrationDir, new String[]{"xml"}, true);
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
		String notIgnoreVar = getVarSingleValue("not_ignore");
		HashSet<String> ignore = new HashSet<>();
		HashSet<String> notIgnore = new HashSet<>();
		if (StringUtils.isNotBlank(notIgnoreVar))
			notIgnore.addAll(Arrays.asList(StringUtils.split(notIgnoreVar, ',')));
		for (String code : StringUtils.split(ignoreVar, ',')) {
			if (!notIgnore.contains(code)) ignore.add(code);
		}
		info.setOperation("Создание разделов каталога и типов товаров");
		info.pushLog("Создание разделов");
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		YMarketCatalogCreationHandler secHandler = new YMarketCatalogCreationHandler(catalog, info, getInitiator(), ignore, justPrice);
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
		YMarketProductCreationHandler prodHandler =
				new YMarketProductCreationHandler(secHandler.getSections(), info, getInitiator(), ignore, notIgnore, justPrice);
		for (File xml : xmls) {
			parser.parse(xml, prodHandler);
		}
		if (!justPrice)
			postProcessBookinistic("16546");
		//executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(20, null).noFulltextIndex());

		info.setOperation("Создание кеша удаляемых товаров");
		info.pushLog("Создание кеша удаляемых товаров");

		CacheAndCleanHidden cacheCommand = new CacheAndCleanHidden(this);
		cacheCommand.integrate();

		info.pushLog("Создание кеша завершено");
		info.pushLog("Создание товаров завершено");
		info.pushLog("Индексация");
		info.setOperation("Индексация");

		if (!justPrice)
			LuceneIndexMapper.getSingleton().reindexAll();

		info.pushLog("Индексация завершена");
		info.pushLog("Интеграция успешно завершена");
		info.setOperation("Интеграция завершена");
	}

	private void postProcessBookinistic(String sectionCode) throws Exception {
		setOperation("Повторное расставление цен на букинистические товары");

		Item course = new ItemQuery("course").loadFirstItem();
		BigDecimal level_1 = course.getDecimalValue("level_1");
		BigDecimal quotient_1 = course.getDecimalValue("quotient_1");
		BigDecimal level_2 = course.getDecimalValue("level_2");
		BigDecimal quotient_2 = course.getDecimalValue("quotient_2");
		BigDecimal quotient_3 = course.getDecimalValue("quotient_3");
		BigDecimal quotient_buk = course.getDecimalValue("quotient_bukinistic", BigDecimal.ZERO);


		Item section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, sectionCode);
		int page = 1;
		ItemQuery q = new ItemQuery(PRODUCT_ITEM, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
		q.setParentId(section.getId(), false);
		List<Item> prods;
		info.setProcessed(0);
		while ((prods = q.setLimit(1000, page).loadItems()).size() > 0) {
			for (Item prod : prods) {
				info.setCurrentJob("товар " + prod.getStringValue(NAME_PARAM));
				BigDecimal price = prod.getDecimalValue(PRICE_ORIGINAL_PARAM);
				BigDecimal priceOld = prod.getDecimalValue("price_old_original");
				if (price != null) {
					if (price.compareTo(level_1) < 0) {
						price = price.multiply(quotient_1);
					} else if (quotient_buk.compareTo(BigDecimal.ZERO) != 0)
						price = price.multiply(quotient_buk);
					else if (price.compareTo(level_2) < 0) {
						price = price.multiply(quotient_2);
					} else {
						price = price.multiply(quotient_3);
					}
					prod.setValue(PRICE_PARAM, price.setScale(1, RoundingMode.CEILING));
				}
				if(priceOld != null){
					if (price.compareTo(level_1) < 0) {
						priceOld =   priceOld.multiply(quotient_1);
					}
					else if (quotient_buk.compareTo(BigDecimal.ZERO) != 0) priceOld =  priceOld.multiply(quotient_buk);
					else if (price.compareTo(level_2) < 0) {
						priceOld =  priceOld.multiply(quotient_2);
					}
					else {
						priceOld = priceOld.multiply(quotient_3);
					}
					prod.setValue(PRICE_OLD_PARAM, priceOld.setScale(1, RoundingMode.CEILING));
				}
				prod.removeEqualValue("tag", "Букинистическое издание");
				prod.setValue("tag", "Букинистика");
				executeAndCommitCommandUnits(SaveItemDBUnit.get(prod).noTriggerExtra().ignoreUser().noFulltextIndex().ignoreFileErrors());
				info.increaseProcessed();
			}
			page++;
		}
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
			while ((currentLine = reader.readLine()) != null) {
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
