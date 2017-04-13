package ecommander.pages.filter;

import ecommander.pages.PageElement;
import ecommander.pages.ValidationResults;
import ecommander.pages.variables.VariablePE;
import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.Strings;
import ecommander.fwk.EcommanderException;
import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElementContainer;
import ecommander.persistence.itemquery.fulltext.FulltextQueryCreatorRegistry;
/**
 * Полнотекстовый критерий поиска
 * @author E
 *
 */
public class FulltextCriteriaPE implements PageElement {

	public static final String ELEMENT_NAME = "fulltext";
	
	private VariablePE query;
	private String paramName;
	private int maxResultCount;
	private String typesStr = FulltextQueryCreatorRegistry.DEFAULT;
	private String[] types = new String[] {FulltextQueryCreatorRegistry.DEFAULT};
	private Compare compType = Compare.ANY;
	private float threshold = -1;
	
	public FulltextCriteriaPE(String types, VariablePE queryVar, int maxResultCount, String paramName, Compare compType, float threshold) {
		this.query = queryVar;
		this.paramName = paramName;
		this.maxResultCount = maxResultCount;
		if (!StringUtils.isBlank(types)) {
			this.typesStr = types;
			this.types = StringUtils.split(types, Strings.SPACE);
		}
		if (compType != null)
			this.compType = compType;
		if (threshold < 1 && threshold >= 0)
			this.threshold = threshold;
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new FulltextCriteriaPE(typesStr, (VariablePE) query.createExecutableClone(null, parentPage), maxResultCount, paramName,
				compType, threshold);
	}

	public void validate(String elementPath, ValidationResults results) {
		if (query != null)
			query.validate(elementPath, results);
		ItemType desc = (ItemType)results.getBufferData();
		if (!StringUtils.isBlank(paramName) && desc.getFulltextParameterList(paramName) == null) {
			results.addError(elementPath + " > " + getKey(), "'" + desc.getName() + "' item does not contain fulltext '" + paramName + "'");
		}
		for (String type : types) {
			try {
				FulltextQueryCreatorRegistry.getCriteria(type);
			} catch (Exception e) {
				results.addError(elementPath + " > " + getKey(), "there is no '" + type + "' class or named fulltext query");
			}
		}
	}

	public String getKey() {
		return "Fulltext criteria";
	}

	public boolean isAllFields() {
		return StringUtils.isBlank(paramName);
	}
	
	public boolean isValid() {
		return !query.isEmpty();
	}
	
	public int getMaxResultCount() {
		return maxResultCount;
	}
	
	public String getParamName() {
		return paramName;
	}
	
//	public String getQuery() {
//		StringBuilder queryStr = new StringBuilder();
//		for (String val : query.outputArray()) {
//			queryStr.append(val).append(' ');
//		}
//		return queryStr.toString();
//	}
	
	public String[] getQueries() {
		return query.outputArray().toArray(new String[0]);
	}
	
	public String[] getTypes() {
		return types;
	}
	
	public String getElementName() {
		return ELEMENT_NAME;
	}
	
	public Compare getCompareType() {
		return compType;
	}
	
	public float getThreshold() {
		return threshold;
	}
}
