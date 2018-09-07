package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Присваивание тэгов товарам в отчетах
 * СБрос тэгов происходит в случае, если установлена переменная страницы reset_tags = true
 * Created by E on 4/9/2018.
 */
public class TagSales extends IntegrateBase implements ItemNames {
	public static final String RESET_TAGS = "reset_tags";

	public TagSales() {
	}

	public TagSales(Command outer) {
		super(outer);
	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		boolean resetTags = Boolean.parseBoolean(getVarSingleValue(RESET_TAGS));
		if (resetTags) {
			info.setOperation("Сброс тэгов товаров");
			info.setProcessed(0);
			ItemQuery saleQuery = new ItemQuery(SALE).addParameterEqualsCriteria(sale_.HAS_TAGS, "1").setLimit(10);
			List<Item> sales = saleQuery.loadItems();
			while (sales.size() > 0) {
				for (Item sale : sales) {
					sale.setValue(sale_.HAS_TAGS, (byte) 0);
					executeCommandUnit(SaveItemDBUnit.get(sale).noFulltextIndex());
				}
				commitCommandUnits();
				info.setProcessed(info.getProcessed() + sales.size());
			}
		}


		info.setOperation("Присваивание тэгов товарам из отчетов");
		info.setProcessed(0);
		List<Item> masks = new ItemQuery(GROUP_MASK).loadItems();
		ItemQuery saleQuery = new ItemQuery(SALE).addParameterEqualsCriteria(sale_.HAS_TAGS, "0").setLimit(10);
		List<Item> sales = saleQuery.loadItems();
		while (sales.size() > 0) {
			for (Item mask : masks) {
				Pattern pattern;
				String regEx = mask.getStringValue(type_mask_.MASK);
				String tag = mask.getStringValue(type_mask_.NAME);
				try {
					pattern = Pattern.compile(regEx);
				} catch (PatternSyntaxException pse) {
					info.addError("Ошибка компиляции регулярного выражения для тэга " + tag + ". " + pse.getMessage(),"Товарные группы");
					continue;
				}
				for (Item sale : sales) {
					Matcher matcher = pattern.matcher(sale.getStringValue(sale_.DEVICE));
					if (matcher.find())
						sale.setValue(sale_.TAG, tag);
				}
			}

			for (Item sale : sales) {
				sale.setValue(sale_.HAS_TAGS, (byte) 1);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(sale).noFulltextIndex());
			}

			info.setProcessed(info.getProcessed() + sales.size());

			sales = saleQuery.loadItems();
		}
		info.pushLog("Присваивание тэгов товарам завершено");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
