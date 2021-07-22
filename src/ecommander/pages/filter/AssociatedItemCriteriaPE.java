package ecommander.pages.filter;

import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.ExecutablePagePE;
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
	private String[] assocName;
	private boolean isParent = false;
	private boolean isUserFiltered = false;

	public AssociatedItemCriteriaPE(String itemName, boolean isParent, boolean isUserFiltered, String... assocName) {
		this.itemName = itemName;
		this.assocName = assocName;
		if (assocName == null || assocName.length == 0 || StringUtils.isBlank(assocName[0])) {
			this.assocName = new String[1];
			this.assocName[0] = ItemTypeRegistry.getPrimaryAssoc().getName();
		} else {
			this.assocName = assocName;
		}
		this.isParent = isParent;
		this.isUserFiltered = isUserFiltered;
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
		return new AssociatedItemCriteriaPE(itemName, isParent, isUserFiltered, assocName);
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
		if (itemDesc == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + itemName + "' item in site model");
		for (String an : assocName) {
			if (ItemTypeRegistry.getAssoc(an) == null)
				results.addError(elementPath + " > " + getKey(), "there is no '" + an + "' assoc in site model");
		}
		// Установить данные для последующей валидации (ItemDescription страничного айтема)
		results.pushBufferData(itemDesc);
		return results.isSuccessful();
	}

	public String getItemName() {
		return itemName;
	}

	public String[] getAssocName() {
		return assocName;
	}

	public boolean isParent() {
		return isParent;
	}

	@Override
	protected void postValidate(ValidationResults results) {
		results.popBufferData();
	}

	public boolean isUserFiltered() {
		return isUserFiltered;
	}
}
