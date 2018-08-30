package ecommander.pages.filter;

import ecommander.fwk.Strings;
import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import ecommander.pages.var.Variable;
import ecommander.persistence.itemquery.fulltext.FulltextQueryCreatorRegistry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Полнотекстовый критерий поиска
 *
 * Формат типа запроса (на примере)
 * (default prefix)(ecommander.extra.FuzzyQueryBuilder)
 *
 * То, что находится в круглых скобках - это группы. Группы выполняются последовательно, в порядке написания.
 * В группах идут типы запросов. Они тоже выполняются последовательно, в порядке написания.
 * Сначала выполняется первый тип запроса из первой группы. Если количество результатов, которое он выдал, меньше заданного
 * максимального количества, то выполняется второй тип запроса в группе, если опять результатов меньше, то третий и так далее.
 * Переход к следующей группе происходит только в случае когда предыдущая группа не выдала результатов.
 *
 * @author E
 *
 */
public class FulltextCriteriaPE implements FilterCriteriaPE {

	public static final String ELEMENT_NAME = "fulltext";
	
	private Variable query;
	private String[] paramNames;
	private int maxResultCount;
	private String typesStr = FulltextQueryCreatorRegistry.DEFAULT;
	private List<String[]> types = Collections.singletonList(new String[] {FulltextQueryCreatorRegistry.DEFAULT});
	private Compare compType = Compare.ANY;
	private float threshold = -1;
	
	public FulltextCriteriaPE(String types, Variable queryVar, int maxResultCount, String[] paramNames, Compare compType, float threshold) {
		this.query = queryVar;
		if (paramNames == null)
			this.paramNames = new String[0];
		else
			this.paramNames = paramNames;
		this.maxResultCount = maxResultCount;
		if (!StringUtils.isBlank(types)) {
			this.typesStr = types;
			String[] typesGroups = StringUtils.split(types, "()");
			this.types = new ArrayList<>();
			for (String typesGroup : typesGroups) {
				if (StringUtils.isNotBlank(typesGroup)) {
					this.types.add(StringUtils.split(typesGroup, Strings.SPACE));
				}
			}
		}
		if (compType != null)
			this.compType = compType;
		if (threshold < 1 && threshold >= 0)
			this.threshold = threshold;
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		return new FulltextCriteriaPE(typesStr, query.getInited(parentPage), maxResultCount, paramNames,
				compType, threshold);
	}

	public void validate(String elementPath, ValidationResults results) {
		if (query != null)
			query.validate(elementPath, results);
		ItemType desc = (ItemType)results.getBufferData();
		if (paramNames.length > 0) {
			for (String paramName : paramNames) {
				if (desc.getFulltextParameterList(paramName) == null)
					results.addError(elementPath + " > " + getKey(), "'" + desc.getName()
							+ "' item does not contain fulltext '" + paramName + "'");
			}
		}
		for (String[] group : types) {
			for (String type : group) {
				try {
					FulltextQueryCreatorRegistry.getCriteria(type);
				} catch (Exception e) {
					results.addError(elementPath + " > " + getKey(), "there is no '" + type + "' class or named fulltext query");
				}
			}
		}
	}

	public String getKey() {
		return "Fulltext criteria";
	}

	public boolean isAllFields() {
		return paramNames.length == 0;
	}
	
	public boolean isValid() {
		return !query.isEmpty();
	}
	
	public int getMaxResultCount() {
		return maxResultCount;
	}
	
	public String[] getParamNames() {
		return paramNames;
	}
	
//	public String getQuery() {
//		StringBuilder queryStr = new StringBuilder();
//		for (String val : query.outputArray()) {
//			queryStr.append(val).append(' ');
//		}
//		return queryStr.toString();
//	}
	
	public String[] getQueries() {
		return query.getLocalValues().toArray(new String[0]);
	}
	
	public List<String[]> getTypes() {
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

	@Override
	public void process(FilterCriteriaContainer cont) throws Exception {
		cont.processFulltextCriteriaPE(this);
	}
}
