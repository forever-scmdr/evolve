package ecommander.fwk.applied;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.Pair;
import ecommander.model.Item;
import ecommander.model.ItemTreeNode;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class CreateDiscountsItemFactory implements ItemEventCommandFactory, ItemNames {

    private static class CreateDiscountsItemCommand extends DBPersistenceCommandUnit implements ItemNames {

        @Override
        public void execute() throws Exception {
            Item catalog = ItemQuery.loadSingleItemByName(CATALOG);
            ArrayList<Item> sections = new ItemQuery(SECTION).setParentId(catalog.getId(), true).setNeedTree(true).loadItems();
            ItemTreeNode sectionTree = ItemTreeNode.createRoot(catalog);
            getItemTree(sectionTree, sections);
            Item systemItem = ItemUtils.ensureSingleRootAnonymousItem(SYSTEM, getTransactionContext().getInitiator());
            Item discounts = ItemUtils.ensureSingleChild(DISCOUNTS, getTransactionContext().getInitiator(), systemItem);
            discounts.clearValue(discounts_.SECTION_DISCOUNT);
            setSectionDiscount(sectionTree, discounts);
            executeCommandInherited(SaveItemDBUnit.get(discounts));
        }
    }

    @Override
    public PersistenceCommandUnit createCommand(Item item) throws Exception {
        return new CreateDiscountsItemCommand();
    }

    /**
     * Заполнить дерево разделов начиная с каталога как корня дерева
     * @param parentNode
     * @param itemsToAdd
     */
    private static void getItemTree(ItemTreeNode parentNode, ArrayList<Item> itemsToAdd) {
        Iterator<Item> itemsIter = itemsToAdd.iterator();
        while (itemsIter.hasNext()) {
            Item checkItem = itemsIter.next();
            if (checkItem.getContextParentId() == parentNode.getItem().getId()) {
                parentNode.addChild(checkItem);
                itemsIter.remove();
            }
        }
        for (ItemTreeNode newParent : parentNode.getChildren()) {
            if (itemsToAdd.size() > 0) {
                getItemTree(newParent, itemsToAdd);
            }
        }
    }

    /**
     * Установить скидку во всех подразделах раздела
     * @param parentSection
     */
    private static void setSectionDiscount(ItemTreeNode parentSection, Item discountsItem) {
        if (!parentSection.hasChildren())
            return;
        BigDecimal discount = parentSection.getItem().getDecimalValue(section_.DISCOUNT, BigDecimal.ZERO);
        if (!discount.equals(BigDecimal.ZERO)) {
            for (ItemTreeNode child : parentSection.getChildren()) {
                BigDecimal childDiscount = child.getItem().getDecimalValue(section_.DISCOUNT, BigDecimal.ZERO);
                if (childDiscount.equals(BigDecimal.ZERO)) {
                    child.getItem().setValue(section_.DISCOUNT, discount);
                }
            }
        }
        for (ItemTreeNode child : parentSection.getChildren()) {
            BigDecimal childDiscount = child.getItem().getDecimalValue(section_.DISCOUNT, BigDecimal.ZERO);
            if (!childDiscount.equals(BigDecimal.ZERO)) {
                discountsItem.setValue(discounts_.SECTION_DISCOUNT, new Pair<>(child.getItem().getKeyUnique(), child.getItem().outputValue(section_.DISCOUNT)));
            }
            if (child.hasChildren()) {
                setSectionDiscount(child, discountsItem);
            }
        }
    }
}
