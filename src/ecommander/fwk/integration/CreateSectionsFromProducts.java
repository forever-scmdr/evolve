package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
		List<Item> loadedProducts = new ItemQuery(PRODUCT_ITEM).loadItems();
		int i = 0;
		info.setToProcess(loadedProducts.size());
		info.setOperation("Удаляю старые картинки");
		info.setProcessed(0);
		for(Item product : loadedProducts){
			File small = product.getFileValue("small_pic", AppContext.getFilesDirPath(product.isFileProtected()));
			if(small != null && small.isFile()){
				FileUtils.deleteQuietly(small);
			}
			String folder = AppContext.getFilesDirPath(product.isFileProtected()) + product.getRelativeFilesPath();
			String sfn = "small_pic_" + product.outputValue("main_pic");
			sfn = folder+sfn;
			info.increaseProcessed();
			File his = new File(sfn);
			if(his.exists()){
				FileUtils.deleteQuietly(his);
			}
			product.clearParameter("small_pic");
			executeCommandUnit(SaveItemDBUnit.get(product).ignoreFileErrors().noFulltextIndex().noTriggerExtra());
			i++;
			if(i>49) {
				i= 0;
				commitCommandUnits();
			}
		}
		commitCommandUnits();
		i = 0;
		info.setOperation("Создаю новые картинки");
		info.setProcessed(0);
		for(Item product : loadedProducts){
			executeCommandUnit(SaveItemDBUnit.forceUpdate(product).noFulltextIndex());
			i++;
			info.increaseProcessed();
			if(i>49) {
				i= 0;
				commitCommandUnits();
			}
		}
		commitCommandUnits();

//		info.setOperation("Обработака товаров");
//		loadedProducts = new LinkedList<>();
//		info.setOperation("Перенос товаров");
//		loadedProducts.addAll(new ItemQuery(SECTION_ITEM).loadItems());
//		info.setToProcess(loadedProducts.size());
//		Item section;
//		while((section = loadedProducts.poll()) != null){
//			ArrayList<Item> sameName = new ArrayList<>();
//			long sectionId = section.getId();
//			Item sub = new ItemQuery(SECTION_ITEM).setParentId(sectionId, false).loadFirstItem();
//			Item seo = new ItemQuery("seo").setParentId(sectionId, false).loadFirstItem();
//			List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(sectionId, false).loadItems();
//			boolean deleteAtOnce = sub == null && products.size() == 0 && (seo == null || StringUtils.isBlank(seo.getStringValue("text")));
//			if(deleteAtOnce){
//				transaction.executeCommandUnit(ItemStatusDBUnit.delete(sectionId));
//			}else{
//				String prevName = "";
//				for(Item product : products){
//					String name = product.getStringValue(NAME_PARAM,"");
//					if(StringUtils.isBlank(prevName)) prevName = name;
//					if(name.equals(prevName)){
//						sameName.add(product);
//					}else{
//						createAssoc(sameName);
//						prevName = name;
//						sameName = new ArrayList<>();
//						sameName.add(product);
//					}
//				}
//				if(products.size() > 1) {
//					createAssoc(sameName);
//				}
//			}
//			info.increaseProcessed();
//		}
//		transaction.commit();
//		executeAndCommitCommandUnits(new CleanAllDeletedItemsDBUnit(10, null));
//		info.setOperation("Индексация названий товаров");
//		LuceneIndexMapper.getSingleton().reindexAll();
	}

	private void createAssoc(ArrayList<Item> products) throws Exception {
		if(products.size()<2)return;
		for(Item product : products){
			for(Item other : products) {
				if (other.equals(product)) continue;
				long id = product.getId();
				byte ass = ItemTypeRegistry.getAssocId("other_colors");
				try {
					executeCommandUnit(CreateAssocDBUnit.childExistsSoft(other, id, ass));
				} catch (EcommanderException e){}
			}
			info.increaseProcessed();
		}
		commitCommandUnits();
	}

	@Override
	protected void terminate() throws Exception {

	}
}
