package ecommander.persistence.itemquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DataTypeMapper;
import ecommander.persistence.mappers.DBConstants;
/**
 * Одиночный критерий - Один параметр, одно значение
 * @author EEEE
 *
 */
class SingleParamCriteria extends FilterParameterCriteria {

	private String value; // строковое значение параметра, с которым он будет сравниваться, передается пользователем или берется из страничной переменной
	private String sign; // знак для сравнения (<, >, like, rlike, =, ...)
	private String pattern; // шаблон для сравнения, если используестя sign LIKE или RLIKE
	private boolean isEmptySet = false;
	
	SingleParamCriteria(ParameterDescription param, ItemType item, String value, String sign, String pattern, String tableName,
			Compare type) {
		super(param, item, tableName);
		this.value = value;
		this.sign = sign.trim();
		if (StringUtils.isBlank(this.sign))
			this.sign = "=";
		this.pattern = pattern;
		if ((type == Compare.SOME || type == Compare.EVERY) && StringUtils.isBlank(value))
			isEmptySet = true;
	}

	@Override
	protected final void appendParameterValue(TemplateQuery query) {
		query = query.getSubquery(ItemQuery.WHERE_OPT).getSubquery(ItemQuery.FILTER_CRITS_OPT);
		query.sql(" AND " + tableName + "." + DBConstants.ItemIndexes.VALUE + " " + sign + " ");
		DataTypeMapper.appendPreparedStatementRequestValue(param.getType(), query, value, pattern);
	}

	public BooleanQuery appendLuceneQuery(BooleanQuery query, Occur occur) {
		if (param.isFulltextFilterable()) {
			if (!sign.equals("=") && !sign.equals("!="))
				return query;
			Term term = new Term(param.getName(), value);
			query.add(new TermQuery(term), occur);
		}
		return query;
	}

	public String getParentColumnName() {
		return tableName + '.' + DBConstants.ItemIndexes.ITEM_PARENT;
	}

	@Override
	public boolean isEmptySet() {
		return isEmptySet;
	}

	public boolean isNotBlank() {
		return !StringUtils.isBlank(value);
	}
	
}