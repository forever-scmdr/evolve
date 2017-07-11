package ecommander.pages.filter;

import ecommander.fwk.EcommanderException;
import ecommander.model.Compare;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Критерий приндалежности к определенному предку, или наличию определенного потомка
 * Created by E on 13/6/2017.
 */
public class ParentalCriteriaPE implements FilterCriteria {
	private final String pageItemId; // страничный ID предшественников или потомков айтема
	private final String sign; // IN или NOT IN
	private final Compare compType; // строгий критерий или нет
	private final String assocName;
	private final boolean isPredecessor;
	private ExecutablePagePE parentPage;

	public ParentalCriteriaPE(String assocName, String pageItemId, String sign, Compare compType, boolean isPredecessor) {
		this.pageItemId = pageItemId;
		this.sign = " " + sign + " ";
		this.compType = compType;
		this.isPredecessor = isPredecessor;
		if (StringUtils.isBlank(assocName))
			this.assocName = ItemTypeRegistry.getPrimaryAssoc().getName();
		else
			this.assocName = assocName;
	}

	@Override
	public void process(FilterCriteriaContainer cont) throws EcommanderException {
		cont.processParentalCriteria(this);
	}

	@Override
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		ParentalCriteriaPE clone = new ParentalCriteriaPE(assocName, pageItemId, sign, compType, isPredecessor);
		clone.parentPage = parentPage;
		return clone;
	}

	@Override
	public void validate(String elementPath, ValidationResults results) {
		if (parentPage.getItemPEById(pageItemId) == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + pageItemId + "' page item on current page");
	}

	@Override
	public String getKey() {
		return isPredecessor ? "predecessor" : "successor";
	}

	@Override
	public String getElementName() {
		return isPredecessor ? "predecessor" : "successor";
	}

	public ArrayList<Long> getLoadedItemIds() {
		return parentPage.getItemPEById(pageItemId).getFoundItemRefIds();
	}

	public String getPageItemId() {
		return pageItemId;
	}

	public String getSign() {
		return sign;
	}

	public Compare getCompType() {
		return compType;
	}

	public boolean isPredecessor() {
		return isPredecessor;
	}

	public boolean isSuccessor() {
		return !isPredecessor;
	}

	public String getAssocName() {
		return assocName;
	}
}
