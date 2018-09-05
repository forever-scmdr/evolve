package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.Parameter;
import ecommander.model.User;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

/**
 * Created by user on 15.08.2018.
 */
public class CreateSectionsFromProducts extends IntegrateBase implements CatalogConst{
	private LinkedList<Item> loadedProducts = new LinkedList<>();
//	private LinkedList<Item> loadedParams = new LinkedList<>();
	private ArrayList<Long> itemsToDelete = new ArrayList<>();

	@Override
	protected boolean makePreparations() throws Exception {
//		loadedProducts.addAll(new ItemQuery(PRODUCT_ITEM).loadItems());
//		if(loadedProducts.size() == 0)return false;
//		info.setProcessed(0);
//		info.setToProcess(loadedProducts.size());
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Обработака товаров");
		String filesRepositoryPath = AppContext.getCommonFilesDirPath();
//		Item product;
		loadedProducts = new LinkedList<>();
//		while((product = loadedProducts.poll()) != null){
//			ArrayList<File> gal = product.getFileValues(GALLERY_PARAM, filesRepositoryPath);
//			if(gal.size() == 0){info.increaseProcessed(); continue;}
//			long id = product.getId();
//			ItemQuery q = new ItemQuery("seo");
//			q.setParentId(id, false);
//			Item seo = q.loadFirstItem();
//			q = new ItemQuery(PARAMS_ITEM);
//			q.setParentId(id, false);
//			Item params = q.loadFirstItem();
//			q = new ItemQuery(PARAMS_XML_ITEM);
//			q.setParentId(id, false);
//			Item paramsXML = q.loadFirstItem();
//			q = new ItemQuery(SECTION_ITEM);
//			q.setChildId(id, false);
//			Item section = q.loadFirstItem();
//			Item microSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), section);
//			microSection.setValue(PARENT_ID_PARAM, String.valueOf(section.getId()));
//			microSection.setValue(CATEGORY_ID_PARAM, String.valueOf(id));
//			microSection.setValue(NAME_PARAM, product.getValue(NAME_PARAM));
//			microSection.setValue(MAIN_PIC_PARAM, product.getFileValue(MAIN_PIC_PARAM, filesRepositoryPath));
//			DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(microSection).noFulltextIndex().ingoreComputed());
//			if(seo != null) {
//				Item microSeo = Item.newChildItem(ItemTypeRegistry.getItemType("seo"), microSection);
//				Item.updateParamValues(seo, microSeo);
//				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(microSeo).noFulltextIndex().ingoreComputed());
//			}
//			if(params != null) {
//				Item newSectionParams = Item.newChildItem(ItemTypeRegistry.getItemType(params.getTypeId()), microSection);
//				Item.updateParamValues(params, newSectionParams);
//				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(newSectionParams).noFulltextIndex().ingoreComputed());
//			}
//			for(File f : gal){
//				Item newProduct = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT_ITEM), microSection);
//				Item.updateParamValues(product, newProduct);
//				newProduct.setValue(MAIN_PIC_PARAM, f);
//				newProduct.clearParameter(GALLERY_PARAM);
//				String code = product.getStringValue(CODE_PARAM);
//				newProduct.setValue(CODE_PARAM, code + StringUtils.substringBeforeLast(f.getName(), "."));
//				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(newProduct).noFulltextIndex().ingoreComputed());
//				if(params != null) {
//					Item newParams = Item.newChildItem(ItemTypeRegistry.getItemType(params.getTypeId()), newProduct);
//					Item.updateParamValues(params, newParams);
//					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(newParams).noFulltextIndex().ingoreComputed());
//				}
//				if(paramsXML != null) {
//					Item newParamsXML = Item.newChildItem(ItemTypeRegistry.getItemType(PARAMS_XML_ITEM), newProduct);
//					Item.updateParamValues(paramsXML, newParamsXML);
//					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(newParamsXML).noFulltextIndex().ingoreComputed());
//				}
//			}
//			product.clearParameter(GALLERY_PARAM);
//			DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(product).noFulltextIndex().ingoreComputed());
//			transaction.executeCommandUnit(new MoveItemDBUnit(product, microSection));
//			transaction.commit();
//			info.increaseProcessed();
//		}
		info.setOperation("Перенос товаров");
		loadedProducts.addAll(new ItemQuery(SECTION_ITEM).loadItems());
		info.setToProcess(loadedProducts.size());
		Item section;
		while((section = loadedProducts.poll()) != null){
			section.clearParameter(PARAMS_FILTER_PARAM);
			DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(section).noFulltextIndex().ingoreComputed());
			ItemQuery q = new ItemQuery(SECTION_ITEM);
			long sectionId = section.getId();
			q.setParentId(sectionId, false);
			List<Item> sub = q.loadItems();
			if(sub.size() == 0){
				q = new ItemQuery(PARAMS_XML_ITEM);
				q.setParentId(sectionId, true);
				LinkedList<Item> tmpParamsXML = new LinkedList<>();
				tmpParamsXML.addAll(q.loadItems());
				boolean ats = allTheSame(tmpParamsXML, sectionId);
				if(ats){
					List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), false).loadItems();
					info.setToProcess(info.getToProcess() + products.size());
					for(Item product : products){
						for(Item other : products){
							if(other.equals(product)) continue;
							long id = product.getId();
							byte ass = ItemTypeRegistry.getAssocId("other_colors");
							transaction.executeCommandUnit(new CreateAssocDBUnit(other,id,ass, false));
						}
						transaction.commit();
						Item parentSec = new ItemQuery(SECTION_ITEM).setChildId(sectionId, false).loadFirstItem();
						transaction.executeCommandUnit(new MoveItemDBUnit(product, parentSec.getId()).ignoreFileErrors());
						info.increaseProcessed();
					}
					transaction.commit();
					transaction.executeCommandUnit(ItemStatusDBUnit.delete(sectionId));
				}
			}
			info.increaseProcessed();
		}
		executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(10, null));
		info.setOperation("Индексация названий товаров");
		LuceneIndexMapper.getSingleton().reindexAll();
	}

	private boolean allTheSame(Collection<Item> items, long sectionId) throws Exception {
		if(items.size() < 2) return false;
		boolean allTheSame = true;
		String xml = null;
		for (Item item : items){
			String currentParams = item.getStringValue(XML_PARAM);
			if(xml != null && !currentParams.equalsIgnoreCase(xml)){
				return false;
			}
			xml = currentParams;
		}
		return allTheSame;
	}

	@Override
	protected void terminate() throws Exception {

	}
}
