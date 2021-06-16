package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DeleteAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class NaskladeCreateCatalogCommand extends IntegrateBase implements CatalogConst {
	private static final String INTEGRATION_DIR = "ym_integrate";
	private static final String FILE_URL = "http://opt.nasklade.by/sitefiles/1/11/catalog_export.xml";
	private File xml;
	Item catalog;

	@Override
	protected void integrate() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		setOperation("Создание разделов");
		pushLog("Создание разделов");
		catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		NaskladeSectionCreationHandler secHandler = new NaskladeSectionCreationHandler(catalog, info, getInitiator());

		//for (File xml : xml) {
			// Удалить DOCTYPE
			if (removeDoctype(xml)) {
				parser.parse(xml, secHandler);
				info.increaseProcessed();
			} else {
				addError("Невозможно удалить DOCTYPE " + xml, xml.getName());
			}
		//}
		pushLog("Создание разделов завершено");

		setOperation("Создание товаров");
		pushLog("Создание товаров");
		setProcessed(0);

		DefaultHandler prodHandler = new NaskladeProductCreationHandler(secHandler.getSections(), info, getInitiator());

		//for (File xml : xmls) {
			parser.parse(xml, prodHandler);
		//}

		info.pushLog("Создание товаров завершено");

		info.pushLog("Прикрепление сопутствующих товаров");
		info.setOperation("Прикрепление сопутствующих товаров");

		addRelatedProducts();

		info.pushLog("Прикрепление сопутствующих товаров завершено");

		info.pushLog("Прикрепление картинок к разделам");
		info.setOperation("Прикрепление картинок к разделам");

		attachImages();

		info.pushLog("Прикрепление картинок к разделам завершено");
		info.pushLog("Индексация");
		info.setOperation("Индексация");

		LuceneIndexMapper.getSingleton().reindexAll();

		info.pushLog("Индексация завершена");
		Path log = Paths.get(AppContext.getContextPath(), getClass().getSimpleName()+"_log.txt");
		Files.write(log, ("start: "+ new Date()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
	}

	private void addRelatedProducts() throws Exception {
		info.setProcessed(0);
		info.setToProcess(0);

		long id = 0;
		final int LIMIT = 50;
		List<Item> products = ItemMapper.loadByName(ItemNames.PRODUCT, LIMIT, id);
		while (products.size() > 0){
			for (Item product : products){
				id = product.getId();
				resolveRelated(product);
				info.increaseProcessed();
			}
			products =  ItemMapper.loadByName(ItemNames.PRODUCT, LIMIT, id);
		}
	}

	private void resolveRelated(Item product) throws Exception{
		ItemQuery q = new ItemQuery("related_list");
		q.setParentId(product.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
		List<Item> relatedLists = q.loadItems();
		if(relatedLists.size() > 1){
			String em = (product.getStringValue(CODE_PARAM) + "has "+relatedLists.size()+" related lists!");
			throw new Exception(em);
		}else if(relatedLists.size() == 0){
			List<Item> relatedProducts = ItemQuery.loadByParentId(product.getId(),  new Byte[]{ItemTypeRegistry.getAssocId("related")});
			for(Item r : relatedProducts){
				executeCommandUnit(new DeleteAssocDBUnit(r, product.getId(), ItemTypeRegistry.getAssoc("related").getId()));
			}
			commitCommandUnits();
		}else {
			List<String> codes = relatedLists.get(0).getStringValues(CODE_PARAM);
			List<Item> relatedProducts = ItemQuery.loadByParentId(product.getId(),  new Byte[]{ItemTypeRegistry.getAssocId("related")});
			for(String code : codes){
				boolean isNewRelatedProduct = true;
				//do nothing if in list and related
				Iterator<Item> relatedProductIterator = relatedProducts.iterator();
				while (relatedProductIterator.hasNext()){
					Item r = relatedProductIterator.next();
					if(r.getTypeId() != product.getTypeId()){
						executeCommandUnit(new DeleteAssocDBUnit(r, product.getId(), ItemTypeRegistry.getAssoc("related").getId()));
						continue;
					}
					if(r.getStringValue(CODE_PARAM).equals(code)){
						relatedProductIterator.remove();
						isNewRelatedProduct= false;
					}
				}
				//add new related device
				if(isNewRelatedProduct){
					Item relatedProduct = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
					if(relatedProduct != null) {
						executeCommandUnit(CreateAssocDBUnit.childExistsSoft(relatedProduct, product.getId(), ItemTypeRegistry.getAssoc("related").getId()));
					}
				}
			}
			//remove if not in list
			for(Item r : relatedProducts){
				executeCommandUnit(new DeleteAssocDBUnit(r, product.getId(), ItemTypeRegistry.getAssoc("related").getId()));
			}
			commitCommandUnits();
		}
	}

	@Override
	protected boolean makePreparations() throws Exception {
		Path log = Paths.get(AppContext.getContextPath(), getClass().getSimpleName()+"_log.txt");
		Files.write(log, ("start: "+ new Date()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		URL fileUrl = new URL(FILE_URL);
		Path destPath = Paths.get(AppContext.getRealPath(INTEGRATION_DIR), "catalog_export.xml");
		FileUtils.deleteQuietly(destPath.toFile());
		info.setCurrentJob("Скачивание файла");
		FileUtils.copyURLToFile(fileUrl, destPath.toFile());
		info.pushLog("Файл скачан");
		xml = destPath.toFile();
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
