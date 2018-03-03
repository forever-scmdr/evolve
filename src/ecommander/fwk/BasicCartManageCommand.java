package ecommander.fwk;

import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.Command;
import ecommander.pages.InputValues;
import ecommander.pages.MultipleHttpPostForm;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.Cart;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Управление корзиной
 * Created by E on 2/3/2018.
 */
public class BasicCartManageCommand extends Command {

	private static final String PRODUCT_ITEM = "product";
	private static final String CART_ITEM = "cart";
	private static final String BOUGHT_ITEM = "bought";
	private static final String PRICE_PARAM = "price";
	private static final String QTY_PARAM = "qty";
	private static final String SUM_PARAM = "sum";
	private static final String CODE_PARAM = "code";
	private static final String PROCESSED_PARAM = "processed";



	private static final String CART_COOKIE = "cart_cookie";

	private Item cart;

	/**
	 * Добавить товар в корзину
	 * @return
	 * @throws Exception
	 */
	public ResultPE addToCart() throws Exception {
		String code = getVarSingleValue(CODE_PARAM);
		double quantity = 0;
		try {
			quantity = DoubleDataType.parse(getVarSingleValue(QTY_PARAM));
		} catch (Exception e) {/**/}
		addProduct(code, quantity);
		recalculateCart();
		return getResult("ajax");
	}


	public ResultPE delete() throws Exception {
		// В этом случае ID не самого продукта, а объекта bought
		long boughtId = Long.parseLong(getVarSingleValue(BOUGHT_ITEM));
		getSessionMapper().removeItems(boughtId, BOUGHT_ITEM);
		recalculateCart();
		return getResult("cart");
	}


	public ResultPE recalculate() throws Exception {
		updateQtys();
		recalculateCart();
		return getResult("cart");
	}



	private void updateQtys() throws Exception {
		MultipleHttpPostForm form = getItemForm();
		// Обновление параметров
		loadCart();
		if (cart == null)
			return;
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		for (Item bought : boughts) {
			InputValues vals = form.getItemInput(bought.getId());
			if (vals != null && StringUtils.isNotBlank(vals.getString(QTY_PARAM))) {
				double quantity = -1;
				try {
					quantity = DoubleDataType.parse(vals.getString(QTY_PARAM));
				} catch (NumberFormatException e) { /**/ }
				if (quantity > 0) {
					Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
					double maxQuantity = product.getDoubleValue(QTY_PARAM, 1000000d);
					if (maxQuantity > 0)
						quantity = maxQuantity > quantity ? quantity : maxQuantity;
					bought.setValue(QTY_PARAM, quantity);
					getSessionMapper().saveTemporaryItem(bought);
				} else {
					getSessionMapper().removeItems(bought.getId());
				}
			}
		}
	}



	private void addProduct(String code, double qty) throws Exception {
		ensureCart();
		// Если корзина уже была отправлена, создать ее заново
		byte processed = cart.getByteValue(PROCESSED_PARAM, (byte)0);
		if (processed == (byte) 1) {
			getSessionMapper().removeItems(cart.getId());
			cart = getSessionMapper().createSessionRootItem(CART_ITEM);
			getSessionMapper().saveTemporaryItem(cart);
		}
		// Проверка, есть ли уже такой девайс в корзине (если есть, изменить количество)
		Item boughtProduct = getSessionMapper().getSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
		if (boughtProduct == null) {
			if (qty <= 0)
				return;
			Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
			Item bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			double maxQuantity = product.getDoubleValue(QTY_PARAM, 1000000d);
			if (maxQuantity > 0)
				qty = maxQuantity > qty ? qty : maxQuantity;
			bought.setValue(QTY_PARAM, qty);
			// Сохраняется bought
			getSessionMapper().saveTemporaryItem(bought);
			// Сохраняется девайс
			product.setContextPrimaryParentId(bought.getId());
			getSessionMapper().saveTemporaryItem(product, PRODUCT_ITEM);
		} else {
			Item bought = getSessionMapper().getItem(boughtProduct.getContextParentId(), BOUGHT_ITEM);
			if (qty <= 0) {
				getSessionMapper().removeItems(bought.getId());
				return;
			}
			double maxQuantity = boughtProduct.getDoubleValue(QTY_PARAM, 1000000d);
			if (maxQuantity > 0)
				qty = maxQuantity > qty ? qty : maxQuantity;
			bought.setValue(QTY_PARAM, qty);
			getSessionMapper().saveTemporaryItem(bought);
		}
	}

	/**
	 * Загрузить корзину из сеанса или создать новую корзину
	 * @throws Exception
	 */
	private void ensureCart() throws Exception {
		if (cart == null) {
			Item cartTemp = getSessionMapper().getSingleRootItemByName(CART_ITEM);
			if (cartTemp == null) {
				cartTemp = getSessionMapper().createSessionRootItem(CART_ITEM);
				getSessionMapper().saveTemporaryItem(cartTemp);
			}
			cart = Cart.get(cartTemp);
		}
	}

	/**
	 * Загрузить корзину, но не создавать в случае если корзина не найдена
	 */
	private void loadCart() throws Exception {
		if (cart == null) {
			Item cartTemp = getSessionMapper().getSingleRootItemByName(CART_ITEM);
			if (cartTemp != null) {
				cart = Cart.get(cartTemp);
			}
		}
	}

	/**
	 * Сохранить корзину в куки на всякий случай (если будет разрыв сеанса, корзину можно восстановить)
	 * @throws Exception
	 */
	private void saveCookie() throws Exception {
		ensureCart();
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		ArrayList<String> codeQtys = new ArrayList<>();
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double quantity = bought.getDoubleValue(QTY_PARAM);
			codeQtys.add(product.getStringValue(CODE_PARAM) + ":" + quantity);
		}
		if (codeQtys.size() > 0) {
			String cookie = StringUtils.join(codeQtys, '/');
			setCookieVariable(CART_COOKIE, cookie);
		} else {
			setCookieVariable(CART_COOKIE, null);
		}
	}

	/**
	 * Восстановить корзину из куки
	 * @return
	 * @throws Exception
	 */
	public ResultPE restoreFromCookie() throws Exception {
		loadCart();
		if (cart != null)
			return null;
		String cookie = getVarSingleValue(CART_COOKIE);
		if (StringUtils.isBlank(cookie))
			return null;
		String[] codeQtys = StringUtils.split(cookie, '/');
		for (String codeQty : codeQtys) {
			String[] pair = StringUtils.split(codeQty, ':');
			double qty = DoubleDataType.parse(pair[1]);
			addProduct(pair[0], qty);
		}
		recalculateCart();
		return null;
	}

	/**
	 * Пересчитывает данные для одного enterprise_bought, когда в корзине произошли какие-то изменения
	 * @throws Exception
	 */
	private boolean recalculateCart() throws Exception {
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
		BigDecimal sum = new BigDecimal(0); // полная сумма
		double zeroQuantity = 0;
		double regularQuantity = 0;
		boolean result = true;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			double maxQuantity = product.getDoubleValue(QTY_PARAM, 1000000d);
			double quantity = bought.getDoubleValue(QTY_PARAM);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), BOUGHT_ITEM);
				result = false;
			} else {
				// Первоначальная сумма
				BigDecimal productSum = product.getDecimalValue(PRICE_PARAM).multiply(new BigDecimal(quantity));
				if (maxQuantity <= 0) {
					productSum = new BigDecimal(0);
					zeroQuantity += quantity;
				} else {
					regularQuantity += quantity;
				}
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

	@Override
	public ResultPE execute() throws Exception {
		return null;
	}
}
