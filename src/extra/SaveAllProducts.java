package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;
import extra._generated.ItemNames;

import java.util.List;

public class SaveAllProducts extends IntegrateBase {
	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Пересохранение товаров");
		info.setProcessed(0);
		List<Item> products = ItemMapper.loadByName(ItemNames.PRODUCT, 500, 0);
		long id = 0;
		while (products.size() > 0){
			for (Item product : products) {
				id = product.getId();
				product.forceInitialInconsistent();
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra().ignoreUser(true));
				info.increaseProcessed();
			}
			products = ItemMapper.loadByName(ItemNames.PRODUCT, 500, id);
		}
		info.setOperation("Пересохранение завершено");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
