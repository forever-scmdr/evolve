package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;

import java.math.BigDecimal;

/**
 * Обновляет минимальную цену товара из каталога, если в нем был изменен строковый товар
 * Created by E on 19/11/2018.
 */
public class UpdateProductMinPriceFactory implements ItemEventCommandFactory {

	public static final String PRODUCT = "catalog_product";
	public static final String HAS_LINES = "has_lines";
	public static final String PRICE = "price";

	private static class UpdateMinPrice extends DBPersistenceCommandUnit {

		private Item lineProduct;

		public UpdateMinPrice(Item item) {
			this.lineProduct = item;
		}

		@Override
		public void execute() throws Exception {
			if (lineProduct.isValueNotEmpty(PRICE)) {
				BigDecimal linePrice = lineProduct.getDecimalValue(PRICE);
				if (linePrice.compareTo(new BigDecimal(0)) > 0) {
					Item product = new ItemQuery(PRODUCT).setChildId(lineProduct.getId(), false).loadFirstItem();
					if (product != null) {
						if (product.getDecimalValue(PRICE, linePrice.add(new BigDecimal(1))).compareTo(linePrice) > 0) {
							product.setValue(PRICE, linePrice);
						}
						product.setValue(HAS_LINES, (byte) 1);
						if (product.hasChanged()) {
							executeCommand(SaveItemDBUnit.get(product, false));
						}
					}
				}
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new UpdateMinPrice(item);
	}
}
