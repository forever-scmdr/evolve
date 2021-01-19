package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by user on 20.09.2018.
 */
public class OnlinerUploadCommand extends IntegrateBase implements CatalogConst {
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Загрузка каталога");
		List<Item> sections = new ItemQuery(SECTION_ITEM).loadItems();
		List<Item> products = null;
		JSONObject catalog = new JSONObject();
		JSONArray sectionsJSON = new JSONArray();
		for(Item section : sections){
			ItemQuery q = new ItemQuery(PRODUCT_ITEM);
			q.setParentId(section.getId(), false);
			String sectionName = section.getStringValue(NAME_PARAM);
			List<Item> secChildren =  q.loadItems();
			if(secChildren.size() == 0) continue;
			JSONObject jsonObjectSection = new JSONObject();
			jsonObjectSection.put("id", section.getId());
			jsonObjectSection.put("title", section.getStringValue(NAME_PARAM));
			JSONObject tmp = new JSONObject();
			tmp.put("section", jsonObjectSection);

			sectionsJSON.put(tmp);

			for(Item product : secChildren){
				product.setExtra("category", sectionName);
			}
			if(products == null){
				products = q.loadItems();
			}else{
				products.addAll(q.loadItems());
			}
		}
		info.pushLog(sectionsJSON.toString());
	}

	@Override
	protected void terminate() throws Exception {

	}
}
