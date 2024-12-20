package ecommander.special.portal.extra;

import ecommander.model.Item;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import extra.CartManageCommand;
import extra._generated.Bought;

import java.util.LinkedHashMap;

public class ManagerCartManageCommand extends CartManageCommand {

    public static final String BOUGHT_ID = "bought";
    public static final String PURCHASE_ID = "order";
    private Item purchase;

    /**
     * Загружает корзину из заказа. ID заказа передается в переменной
     */
    private void loadPurchase() throws Exception {
        LinkedHashMap<Long, Item> boughts = getLoadedItems(BOUGHT_ID);
        purchase = getSingleLoadedItem(PURCHASE_ID);
        if (boughts.size() > 0) {
            boolean hasBoughts = false;
            for (Item boughtItem : boughts.values()) {
                Bought boughtDB = Bought.get(boughtItem);
                Item bought = addProduct(boughtDB.getId(), boughtDB.get_qty(), boughtDB.get_outer_product());
                hasBoughts |= bought != null;
            }
            if (hasBoughts) {
                recalculateCart();
            }
        }
    }

    /**
     * Помимо создания корзины в сеансе также загружает и добавляет туда все заказанные товары из айтема заказа
     * @throws Exception
     */
    @Override
    protected void ensureCart() throws Exception {
        if (cart == null) {
            cart = getSessionMapper().getSingleRootItemByName(CART_ITEM);
            if (cart == null) {
                cart = getSessionMapper().createSessionRootItem(CART_ITEM);
                getSessionMapper().saveTemporaryItem(cart);
                loadPurchase();
            }
        }
    }

    @Override
    protected void saveCookie() {
        // ничего не делать
    }

    @Override
    public ResultPE addToCart() throws Exception {
        super.addToCart();
        if (lastBought != null) {
            Item boughtToSave = new Item(lastBought);
            boughtToSave.setContextPrimaryParentId(purchase.getId());
            boughtToSave.setOwner(purchase.getOwnerGroupId(), purchase.getOwnerUserId());
            //executeAndCommitCommandUnits(SaveItemDBUnit.get(boughtToSave).ignoreUser());
        }
        getSessionMapper().removeItems(cart.getId());
        cart = null;
        return getResult("refresh_order");
    }
}
