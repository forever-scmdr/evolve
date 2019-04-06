package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand {

	public static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	public static final HashSet<String> MANDATORY_JUR = new HashSet<>();
	static {
		MANDATORY_PHYS.add(ItemNames.user_phys_.NAME);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.ADDRESS);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.EMAIL);
		MANDATORY_PHYS.add(ItemNames.user_phys_.PHONE);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.SHIP_TYPE);

		//MANDATORY_JUR.add(ItemNames.user_jur_.ACCOUNT);
		//MANDATORY_JUR.add(ItemNames.user_jur_.ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_CODE);
		MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_NAME);
		//MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_PHONE);
		MANDATORY_JUR.add(ItemNames.user_jur_.PHONE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.DIRECTOR);
		//MANDATORY_JUR.add(ItemNames.user_jur_.EMAIL);
		MANDATORY_JUR.add(ItemNames.user_jur_.ORGANIZATION);
		//MANDATORY_JUR.add(ItemNames.user_jur_.SHIP_TYPE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.UNP);
	}

	private static final String MIN_PARAM = "min";
	private static final String MAX_PARAM = "max";
	private static final String QUOTIENT_PARAM = "quotient";
	private static final String PRICE_INTERVAL_ITEM = "price_interval";
	private static final String PRICE_CATALOG_ITEM = "price_catalog";

	private ArrayList<Pair<BigDecimal, BigDecimal>> priceIntervals = null; // Значение, Коэффифиент
	private BigDecimal defaultQuotient = new BigDecimal(1.4d);

	@Override
	protected boolean validate() throws Exception {
		Item form = getItemForm().getTransientSingleItem();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
		boolean hasError = false;
		if (isPhys) {
			for (String mandatory : MANDATORY_PHYS) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("customer_jur");
			saveSessionForm("customer_phys");
		} else {
			for (String mandatory : MANDATORY_JUR) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("customer_phys");
			saveSessionForm("customer_jur");
		}
		return !hasError;
	}


	@Override
	protected boolean recalculateCart() throws Exception {
		loadCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		BigDecimal sum = new BigDecimal(0); // полная сумма
		double regularQuantity = 0;
		boolean result = true;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double maxQuantity = product.getDecimalValue(QTY_PARAM, new BigDecimal(MAX_QTY)).doubleValue();
			double quantity = bought.getDoubleValue(QTY_PARAM);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
				result = false;
			} else {
				// Первоначальная сумма
				BigDecimal price = product.getDecimalValue(PRICE_PARAM, new BigDecimal(0d));
				BigDecimal productSum = price.multiply(new BigDecimal(quantity));
				if (maxQuantity <= 0) {
					productSum = new BigDecimal(0);
				} else {
					regularQuantity += quantity;
				}
				// Умножить сумму на коэффициент, зависящий от суммы (количества заказанных товаров)
				productSum = productSum.multiply(getTotalQuotient(productSum));
				bought.setValue(PRICE_PARAM, price);
				bought.setValue(SUM_PARAM, productSum);
				sum = sum.add(productSum);
				// Сохранить bought
				getSessionMapper().saveTemporaryItem(bought);
			}
		}
		cart.setValue(SUM_PARAM, sum);
		cart.setValue(QTY_PARAM, regularQuantity);
		// Сохранить корзину
		getSessionMapper().saveTemporaryItem(cart);
		saveCookie();
		return result && regularQuantity > 0;
	}

	/**
	 * Загрузить интервалы цен и коэфиициенты для этих интервалов
	 */
	private void loadPriceIntervals() {
		if (priceIntervals == null) {
			priceIntervals = new ArrayList<>();
			try {
				Item priceCatalog = ItemUtils.ensureSingleRootItem(PRICE_CATALOG_ITEM,
						User.getDefaultUser(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
				List<Item> quotients = new ItemQuery(PRICE_INTERVAL_ITEM)
						.setParentId(priceCatalog.getId(), false).addSorting(MAX_PARAM, "ASC").loadItems();
				for (Item quotient : quotients) {
					priceIntervals.add(new Pair<>(quotient.getDecimalValue(MAX_PARAM), quotient.getDecimalValue(QUOTIENT_PARAM)));
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to load price intervals", e);
			}
		}
	}

	/**
	 * Вычислить коэффициент для товара учитвая количество заказанных единиц
	 * @param productSum
	 * @return
	 */
	private BigDecimal getTotalQuotient(BigDecimal productSum) {
		loadPriceIntervals();
		BigDecimal currentQuotient = new BigDecimal(1d);
		for (Pair<BigDecimal, BigDecimal> priceInterval : priceIntervals) {
			currentQuotient = priceInterval.getRight();
			if (productSum.compareTo(priceInterval.getLeft()) <= 0) {
				break;
			}
		}
		return currentQuotient.multiply(defaultQuotient);
	}
}
