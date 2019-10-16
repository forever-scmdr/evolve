package extra;

import ecommander.fwk.Strings;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;

public class FixKeyUnique extends Command implements CatalogConst {

	@Override
	public ResultPE execute() throws Exception {
		processItems(PRODUCT_ITEM);
		processItems(SECTION_ITEM);
		return getResult("success");
	}

	private void processItems(String itemName) throws Exception {
		LinkedList<Item> itemsToFix = new LinkedList<Item>();
		long id = -1;
		itemsToFix.addAll(ItemMapper.loadByName(itemName, 1000, id));
		HashSet<String> existingKeys = new HashSet();
		while (itemsToFix.size() > 0) {
			Item product;
			while ((product = itemsToFix.poll()) != null) {
				id = product.getId();
				String keyUnique = product.getKeyUnique();
				String expectedKeyUnique = StringUtils.substring(Strings.translit(product.getKey()), 0, 98);
				//remove all da fuckin dots
				expectedKeyUnique = expectedKeyUnique.replaceAll("\\.", "");

				if ((!keyUnique.equals(expectedKeyUnique) && !keyUnique.equals(expectedKeyUnique + id)) || existingKeys.contains(keyUnique)) {
					if (existingKeys.contains(expectedKeyUnique)) {
						product.setKeyUnique(expectedKeyUnique + id);
					}else {
						product.setKeyUnique(expectedKeyUnique);
						existingKeys.add(expectedKeyUnique);
					}
					executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noTriggerExtra().noFulltextIndex().ignoreFileErrors().ignoreUser());
				}else{
					existingKeys.add(keyUnique);
				}
			}
			itemsToFix.addAll(ItemMapper.loadByName(itemName, 1000, id));
		}
	}
}
