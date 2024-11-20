
package ecommander.extra._generated;

import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;

public class Question
    extends Item
{

    private final static String _ITEM_TYPE_NAME = "question";

    private Question(Item item) {
        super(item);
    }

    public static Question get(Item item) {
        boolean isCompatible = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName()).contains(_ITEM_TYPE_NAME);
        if (!isCompatible) {
            throw new ClassCastException(("Wrapper 'question' can not be created around '"+(item.getTypeName()+"' object")));
        }
        return new Question(item);
    }

    public static Question newChild(Item parent) {
        return get(newChildItem(ItemTypeRegistry.getItemType(_ITEM_TYPE_NAME), parent));
    }

    public void set_question(String value) {
        setValue("question", value);
    }

    public String get_question() {
        return getStringValue("question");
    }

    public String getDefault_question(String defaultVal) {
        return getStringValue("question", defaultVal);
    }

    public boolean contains_question(String value) {
        return containsValue("question", value);
    }

    public void set_answer(String value) {
        setValue("answer", value);
    }

    public String get_answer() {
        return getStringValue("answer");
    }

    public String getDefault_answer(String defaultVal) {
        return getStringValue("answer", defaultVal);
    }

    public boolean contains_answer(String value) {
        return containsValue("answer", value);
    }

}
