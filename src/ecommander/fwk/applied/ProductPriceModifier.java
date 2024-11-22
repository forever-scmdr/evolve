package ecommander.fwk.applied;

import ecommander.controllers.SessionContext;
import ecommander.fwk.ItemSessionCache;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.Pair;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.User_jur;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Модификация цены товара в соотвествии с типом заказчика (оптовик) и со скидкой на определенный раздел товаров.
 * Должен использоваться в команде обработки корзины заказов
 * Также содержит команду, которая модифицирует цены на странице (изменяет айтемы товаров, которые загружены на странице)
 *
 * Если на странице несколько списков товаров, которые надо модифицировать, или страницчный ID этого списка не prod,
 * то надо определять переменную modify_prod_ids, в которой перечислять через пробел или запятую все ID
 */
public class ProductPriceModifier extends Command implements ItemNames {

    public static final String PROD_PAGE_IDS_VAR_NAME = "modify_prod_ids";
    private static final String DISCOUNTS_EXTRA_NAME = "percents";
    private static final String REGISTERED_GROUP = "registered";

    /**
     * Модифицировать цену одного товара. Старая цена сохраняется в параметре PRICE_OPT_OLD
     * @param product
     * @param parentSection
     * @param userItem
     * @param session
     */
    public static void setPrice(Item product, Item parentSection, Item userItem, SessionContext session) {
        if (userItem != null && userItem.getByteValue(User_jur.OPT_PRICE, (byte) 0) == (byte) 1 && !isAlreadyModified(product)) {
            HashMap<String, String> userDiscounts = userItem.getTupleValuesMap(user_jur_.DISCOUNTS);
            BigDecimal discountPercent = DecimalDataType.parse(userDiscounts.get(parentSection.getKeyUnique()), 4);
            if (discountPercent == null) {
                Item discountsItem = getDiscounts(session);
                if (discountsItem == null && userDiscounts.isEmpty())
                    return;
                HashMap<String, BigDecimal> percents = (HashMap<String, BigDecimal>) discountsItem.getExtra(DISCOUNTS_EXTRA_NAME);
                if (percents == null)
                    return;
                discountPercent = percents.get(parentSection.getKeyUnique());
            }
            if (discountPercent == null)
                return;
            BigDecimal price = product.getDecimalValue(described_product_.PRICE_OPT, BigDecimal.ZERO);
            if (price.equals(BigDecimal.ZERO)) {
                price = product.getDecimalValue(described_product_.PRICE, BigDecimal.ZERO);
                if (price.equals(BigDecimal.ZERO)) {
                    return;
                } else {
                    product.setValue(described_product_.PRICE_OPT, price);
                }
            }
            product.setValue(described_product_.PRICE_OPT_OLD, price);
            BigDecimal discountedPrice = price.subtract(price.multiply(discountPercent.divide(new BigDecimal(100), RoundingMode.HALF_EVEN)));
            product.setValue(described_product_.PRICE_OPT, discountedPrice);
        }
    }

    private static Item getDiscounts(SessionContext session) {
        return ItemSessionCache.getSingle(DISCOUNTS, session, () -> {
            try {
                Item discounts = ItemQuery.loadSingleItemByName(DISCOUNTS);
                if (discounts == null) {
                    Item system = ItemUtils.ensureSingleRootAnonymousItem(SYSTEM, User.getDefaultUser());
                    discounts = Item.newChildItem(ItemTypeRegistry.getItemType(DISCOUNTS), system);
                    DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(discounts));
                }
                HashMap<String, BigDecimal> sectionPercents = new HashMap<>();
                for (Pair<String, String> tupleValue : discounts.getTupleValues(discounts_.SECTION_DISCOUNT)) {
                    BigDecimal percent = DecimalDataType.parse(tupleValue.getRight(), 4);
                    if (percent != null && percent.compareTo(BigDecimal.ZERO) > 0) {
                        sectionPercents.put(tupleValue.getLeft(), percent);
                    }
                }
                discounts.setExtra(DISCOUNTS_EXTRA_NAME, sectionPercents);
                return Collections.singletonList(discounts);
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * Проверка, изменена ли уже цена, или еще не изменена
     * Проверяется параметр PRICE_OPT_OLD, т.к. в норме (без модификации цены), он должен быть пустым
     * @param product
     * @return
     */
    public static boolean isAlreadyModified(Item product) {
        return !product.getDecimalValue(described_product_.PRICE_OPT_OLD, BigDecimal.ZERO).equals(BigDecimal.ZERO);
    }

    @Override
    public ResultPE execute() throws Exception {
        if (!getInitiator().inGroup(REGISTERED_GROUP)) {
            return null;
        }
        Item user = new ItemQuery(USER).setUser(getInitiator()).loadFirstItem();
        if (user == null || user.getByteValue(User_jur.OPT_PRICE, (byte) 0) == (byte) 0) {
            return null;
        }
        String idsString = getVarSingleValueDefault(PROD_PAGE_IDS_VAR_NAME, "prod");
        String[] ids = StringUtils.split(idsString, ", ;");
        HashMap<Long, Item> parentSectionCache = new HashMap<>();
        for (String id : ids) {
            LinkedHashMap<Long, Item> prods = getLoadedItems(id);
            for (Item prod : prods.values()) {
                Item section = parentSectionCache.get(prod.getContextParentId());
                if (section == null) {
                    section = new ItemQuery(SECTION).setChildId(prod.getId(), false).loadFirstItem();
                    if (section == null) {
                        continue;
                    }
                    if (prod.getContextParentId() == section.getId()) {
                        parentSectionCache.put(section.getId(), section);
                    }
                }
                setPrice(prod, section, user, getSessionContext());
            }
        }
        return null;
    }
}
