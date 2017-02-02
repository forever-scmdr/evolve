package ecommander.model.item.filter;

import org.xml.sax.Attributes;

/**
 * Создает из XML все типы частей фильтра
 * @author EEEE
 *
 */
class FilterDefPartXMLFactory {
	
	public static FilterDefPart createPart(String name, Attributes attributes) {
		if (CriteriaGroupDef.GROUP_ELEMENT.equals(name))
			return createGroup(attributes);
		if (InputDef.INPUT_TAG.equals(name))
			return createInput(attributes);
		if (CriteriaDef.CRITERIA_ELEMENT.equals(name))
			return createCriteria(attributes);
		if (FilterRootDef.FILTER_ELEMENT.equals(name))
			return createFilterRoot(attributes);
		return null;
	}
	
	private static CriteriaGroupDef createGroup(Attributes attributes) {
		return new CriteriaGroupDef(
				attributes.getValue(CriteriaGroupDef.NAME_ATTRIBUTE), 
				attributes.getValue(CriteriaGroupDef.COMMENT_ATTRIBUTE), 
				attributes.getValue(CriteriaGroupDef.SIGN_ATTRIBUTE));
	}
	
	private static InputDef createInput(Attributes attributes) {
		return new InputDef(
				attributes.getValue(InputDef.TYPE_ATTRIBUTE), 
				attributes.getValue(InputDef.CAPTION_ATTRIBUTE), 
				attributes.getValue(InputDef.DESCRIPTION_ATTRIBUTE),
				attributes.getValue(InputDef.DOMAIN_ATTRIBUTE_TAG));
	}
	
	private static CriteriaDef createCriteria(Attributes attributes) {
		return new CriteriaDef(
				attributes.getValue(CriteriaDef.SIGN_ATTRIBUTE), 
				attributes.getValue(CriteriaDef.PARAM_ATTRIBUTE),
				attributes.getValue(CriteriaDef.TYPE_ATTRIBUTE),
				attributes.getValue(CriteriaDef.PATTERN_ATTRIBUTE));
	}
	
	private static FilterRootDef createFilterRoot(Attributes attributes) {
		return new FilterRootDef(attributes.getValue(FilterRootDef.ITEM_DESC_ATTRIBUTE));
	}
}
