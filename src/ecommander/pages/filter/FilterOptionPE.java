package ecommander.pages.filter;

import ecommander.model.Compare;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

/**
 * Блок критериев фильтра (в основном фильтре опции объединяются знаком OR)
 * Сам фильтр также является наследником этого класса
 * Created by User on 19.07.2017.
 */
public class FilterOptionPE extends PageElementContainer implements FilterCriteriaPE {
	public static final String ELEMENT_NAME = "option";

	@Override
	public String getKey() {
		return ELEMENT_NAME;
	}

	@Override
	public String getElementName() {
		return ELEMENT_NAME;
	}

	@Override
	public void process(FilterCriteriaContainer cont) throws Exception {
		cont.processOption(this);
	}

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new FilterOptionPE();
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		return results.isSuccessful();
	}

	public void addCriteria(ParameterCriteriaPE criteria) {
		addElement(criteria);
	}

	public void addPredecessor(String assocName, String predecessorId, String sign, Compare compType) {
		if (StringUtils.isBlank(sign))
			sign = " IN ";
		addElement(new ParentalCriteriaPE(assocName, predecessorId, sign, compType, true));
	}

	public void addSuccessors(String assocName, String successorId, String sign, Compare compType) {
		if (StringUtils.isBlank(sign))
			sign = " IN ";
		addElement(new ParentalCriteriaPE(assocName, successorId, sign, compType, false));
	}
}
