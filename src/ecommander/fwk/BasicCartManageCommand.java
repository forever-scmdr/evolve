package ecommander.fwk;

import ecommander.fwk.EcommanderException;
import ecommander.model.Item;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
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

	private static final String CART_COOKIE = "cart_cookie";

	private Cart cart;

	public ResultPE addToCart() throws EcommanderException {

		return getResult("ajax");
	}

	private void addProduct(String code, double qty) {

	}

	/**
	 * Загрузить корзину из сеанса или создать новую корзину
	 * @throws Exception
	 */
	private void ensureCart() throws Exception {
		if (cart == null) {
			Item cartTemp = getSessionMapper().getSingleRootItemByName(ItemNames.CART);
			if (cartTemp == null) {
				cartTemp = getSessionMapper().createSessionRootItem(ItemNames.CART);
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
			Item cartTemp = getSessionMapper().getSingleRootItemByName(ItemNames.CART);
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
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(ItemNames.BOUGHT, cart.getId());
		ArrayList<String> codeQtys = new ArrayList<>();
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(ItemNames.PRODUCT, bought.getId());
			double quantity = bought.getDoubleValue(ItemNames.bought.QTY);
			codeQtys.add(product.getParameterByName(ItemNames.product.CODE) + ":" + quantity);
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
		ArrayList<Item> boughts = getSessionMapper().getItemsByName(ItemNames.BOUGHT, cart.getId());
		BigDecimal sum = new BigDecimal(0); // полная сумма
		double zeroQuantity = 0;
		double regularQuantity = 0;
		boolean result = true;

		// Обычные заказы и заказы с нулевым количеством на складе
		for (Item bought : boughts) {
			Item product = getSessionMapper().getSingleItemByName(ItemNames.PRODUCT, bought.getId());
			double maxQuantity = product.getDoubleValue(ItemNames.product.QTY, 1000000d);
			double quantity = bought.getDoubleValue(ItemNames.bought.QTY);
			if (quantity <= 0) {
				getSessionMapper().removeItems(bought.getId(), ItemNames.BOUGHT);
				result = false;
			} else {
				// Первоначальная сумма
				BigDecimal productSum = product.getDecimalValue(ItemNames.product.PRICE).multiply(new BigDecimal(quantity));
				if (maxQuantity <= 0) {
					productSum = new BigDecimal(0);
					zeroQuantity += quantity;
				} else {
					regularQuantity += quantity;
				}
				bought.setValue(ItemNames.bought.SUM, productSum);
				sum.add(productSum);
				// Сохранить bought
				getSessionMapper().saveTemporaryItem(bought);
			}
		}
		cart.setValue(ItemNames.cart.SUM, sum);
		cart.setValue(ItemNames.cart.QTY, regularQuantity);
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
