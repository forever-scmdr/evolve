package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 21.12.2018.
 */
public class SetSectionPics extends IntegrateBase implements CatalogConst {
	private List<Item> sectionBuffer = new LinkedList();
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Прикрепление изображений к разделам");
		Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		List<Item>sections = new ItemQuery(SECTION_ITEM).setParentId(catalog.getId(), false).loadItems();
		processSections(sections);
	}

	private void processSections(Collection<Item> sections) throws Exception {
		String folder = AppContext.getFilesDirPath(false);
		for(Item section : sections){
			long id = section.getId();
			List<Item> subs = new ItemQuery(SECTION_ITEM).setParentId(id, false).loadItems();
			List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(id, false).loadItems();
			sectionBuffer.add(section);
			for(Item product : products){
				File mainPic = product.getFileValue(MAIN_PIC_PARAM, folder);
				if(mainPic != null && mainPic.isFile()){
					for(Item sec : sectionBuffer){
						sec.setValue(MAIN_PIC_PARAM, mainPic);
						executeCommandUnit(SaveItemDBUnit.get(sec).noFulltextIndex());
					}
					commitCommandUnits();

					break;
				}
			}
			if(products.size() > 0) sectionBuffer.clear();
			processSections(subs);
		}
	}

	@Override
	protected void terminate() throws Exception {

	}
}
