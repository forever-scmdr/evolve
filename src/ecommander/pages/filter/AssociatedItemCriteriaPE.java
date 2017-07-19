package ecommander.pages.filter;

import ecommander.model.Assoc;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ItemPE;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

/**
 * Критерий параметра не самого искомого айтема, а айтема, который как-либо ассоциирован с искомым
 * Т.е. найти айтем, который ассоциирован с другим айтемом, имеющим некоторый параметр
 * Например, найти все товары, продавцы которых располагаются в Минске (продавец и товар - отдельные айтемы)
 * Created by User on 19.07.2017.
 */
public class AssociatedItemCriteriaPE extends PageElementContainer implements FilterCriteriaPE {
	private static final String PARENT = "parent";
	private static final String CHILD = "child";

	private String itemName;
	private String assocName;
	private boolean isParent = false;

	public AssociatedItemCriteriaPE(String itemName, String assocName, boolean isParent) {
		this.itemName = itemName;
		this.assocName = assocName;
		this.isParent = isParent;
	}

	@Override
	public void process(FilterCriteriaContainer cont) throws Exception {
		cont.processAssociatedCriteria(this);
	}

	@Override
	public String getKey() {
		return getElementName() + " " + itemName + " " + assocName;
	}

	@Override
	public String getElementName() {
		return isParent ? PARENT : CHILD;
	}

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new AssociatedItemCriteriaPE(itemName, assocName, isParent);
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
		Assoc assoc = ItemTypeRegistry.getAssoc(assocName);
		if (itemDesc == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + itemName + "' item in site model");
		if (assoc == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + assocName + "' assoc in site model");
		return results.isSuccessful();
	}

	public String getItemName() {
		return itemName;
	}

	public String getAssocName() {
		return assocName;
	}

	public boolean isParent() {
		return isParent;
	}
}
