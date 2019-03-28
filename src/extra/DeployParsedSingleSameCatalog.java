package extra;

import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import lunacrawler.fwk.Parse_item;

import java.util.List;

/**
 * Размещает на сайте информацию, полученную с помощью парсинга
 * Created by E on 15/2/2018.
 */
public class DeployParsedSingleSameCatalog extends DeployParsedSingle {

	@Override
	protected boolean makePreparations() throws Exception {
		info.setOperation("Перенос разобранных товаров в каталог");
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		int processed = 0;
		info.limitLog(5000);
		List<Item> parseSections = new ItemQuery(ItemNames.PARSE_SECTION).loadItems();
		for (Item ps : parseSections) {
			Item section = new ItemQuery(ItemNames.SECTION).setChildId(ps.getId(), false).loadFirstItem();
			info.pushLog("Обработка раздела {}", section.getStringValue(NAME));
			List<Item> secPIs = new ItemQuery(ItemNames.PARSE_ITEM).setParentId(ps.getId(), false).loadItems();
			if (secPIs.size() == 0)
				continue;

			// Создать и заполнить все товары
			for (Item item : secPIs) {
				Parse_item pi = Parse_item.get(item);
				Item prod = deployParsed(pi, section, true);
				if (prod == null) {
					info.pushLog("ОШИБКА ! Товар {} НЕ ДОБАВЛЕН в раздел {}", pi.get_url(), section.getStringValue("name"));
					continue;
				} else {
					commitCommandUnits();
				}
				info.setProcessed(++processed);
			}
		}
		LuceneIndexMapper.getSingleton().finishUpdate();
	}

}