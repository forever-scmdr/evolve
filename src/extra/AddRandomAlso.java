package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AddRandomAlso extends IntegrateBase implements CatalogConst {
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Загрузка разделов");
		LinkedList<Item> allSections = new LinkedList<>();
		allSections.addAll(new ItemQuery(SECTION_ITEM).loadItems());
		Item currentSection;
		setOperation("Добавление кодов похожих товаров");
		while((currentSection = allSections.poll()) != null){
			info.setCurrentJob(currentSection.getStringValue(NAME));
			processSection(currentSection);
		}
	}

	private void processSection(Item currentSection) throws Exception {
		List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(currentSection.getId(), false).loadItems();
		if(products.size() == 0) return;
		else if(products.size() < 6){
			for(Item p : products){
				p.clearValue(SIMILAR_CODE_PARAM);
				for(Item ass : products){
					if(p == ass) continue;
					p.setValue(SIMILAR_CODE_PARAM, ass.getValue(CODE_PARAM));
				}
				executeAndCommitCommandUnits(SaveItemDBUnit.get(p).noFulltextIndex().ignoreUser().noTriggerExtra().ignoreFileErrors());
				info.increaseProcessed();
			}
		}else{
			ArrayList<Integer> idx = new ArrayList<>();
			for(int i= 0; i < products.size(); i++){
				idx.add(i);
			}
			for(Item p : products){
				p.clearValue(SIMILAR_CODE_PARAM);
				Collections.shuffle(idx);
				int s = 0;
				for(int j = 0; j < 6 && s < 5; j++){
					Item ass = products.get(idx.get(j));
					if(p == ass) continue;
					p.setValue(SIMILAR_CODE_PARAM, ass.getValue(CODE_PARAM));
					s++;
				}
				executeAndCommitCommandUnits(SaveItemDBUnit.get(p).noFulltextIndex().ignoreUser().noTriggerExtra().ignoreFileErrors());
				info.increaseProcessed();
			}
		}
		info.pushLog("Готово ("+products.size()+"): " +currentSection.getStringValue(NAME));
	}


	@Override
	protected void terminate() throws Exception {

	}
}
