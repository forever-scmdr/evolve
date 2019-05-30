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

	static class UpdateMinPrice extends DBPersistenceCommandUnit {

		public static final String PRODUCT = "product";
		public static final String LINE_PRODUCT = "line_product";
		public static final String HAS_LINES = "has_lines";
		public static final String PRICE = "price";

		private static final BigDecimal ZERO = new BigDecimal(0);
		private static final BigDecimal MANY = new BigDecimal(Long.MAX_VALUE);

		private Item lineProduct;
		private boolean isDelete;

		public UpdateMinPrice(Item item, boolean isDelete) {
			this.lineProduct = item;
			this.isDelete = isDelete;
		}

		@Override
		public void execute() throws Exception {
			BigDecimal linePrice = lineProduct.getDecimalValue(PRICE, ZERO);
			Item product = new ItemQuery(PRODUCT).setChildId(lineProduct.getId(), false)
					.loadFirstItem(getTransactionContext().getConnection());
			if (product != null) {
				BigDecimal productMinPrice = product.getDecimalValue(PRICE, linePrice.add(MANY));
				if (productMinPrice.compareTo(ZERO) == 0)
					productMinPrice = MANY;
				if (linePrice.compareTo(ZERO) > 0 && !isDelete) {
					if (productMinPrice.compareTo(linePrice) > 0) {
						product.setValue(PRICE, linePrice);
					} else {
						Item min = new ItemQuery(LINE_PRODUCT).setAggregation(PRICE, "MIN", "ASC").setParentId(product.getId(), false)
								.loadFirstItem(getTransactionContext().getConnection());
						if (min != null) {
							BigDecimal minPrice = min.getDecimalValue(PRICE, MANY);
							if (minPrice.compareTo(MANY) < 0) {
								product.setValue(PRICE, minPrice);
							}
						}

						if (min == null) {
							min = new ItemQuery(LINE_PRODUCT).setParentId(product.getId(), false)
									.loadFirstItem(getTransactionContext().getConnection());
							if (min == null)
								product.setValue(HAS_LINES, min == null ? (byte) 0 : (byte) 1);
						}
					}
				}
				if (!isDelete) {
					product.setValue(HAS_LINES, (byte) 1);
				}
				if (product.hasChanged()) {
					executeCommand(SaveItemDBUnit.get(product).noTriggerExtra());
				}
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new UpdateMinPrice(item, false);
	}

}
