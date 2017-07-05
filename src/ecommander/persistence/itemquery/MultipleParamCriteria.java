package ecommander.persistence.itemquery;

import ecommander.model.Compare;
import ecommander.model.ItemType;
import ecommander.model.ParameterDescription;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DataTypeMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import java.util.Collection;

/**
 * Множественный критерий - Один параметр, много подходящий значений
 * @author EEEE
 *
 */
class MultipleParamCriteria extends ParameterCriteria {

	private Collection<String> values;	// массив строковых значений параметра, с которым он будет сравниваться, передается пользователем 
								// или берется из страничной переменной
	private String sign;	// знак для сравнения ( = или != (<>))
	private boolean isEmptySet = false;
	private boolean isBlank = false;
	
	private static final String IN = " IN ";
	private static final String NOT_IN = " NOT IN ";
	
	MultipleParamCriteria(ParameterDescription param, ItemType item, Collection<String> values, String sign, String tableName,
			Compare type) {
		super(param, item, tableName);
		this.values = values;
		sign = sign.trim();
		if (StringUtils.isBlank(sign) || sign.equals("="))
			this.sign = IN;
		else
			this.sign = NOT_IN;
		isBlank = (values == null || values.size() == 0);
		if ((type == Compare.SOME || type == Compare.EVERY) && isBlank && StringUtils.equals(this.sign, IN))
			isEmptySet = true;
	}

	@Override
	protected void appendParameterValue(TemplateQuery query) {
		query.getSubquery(WHERE).AND().col(INDEX_TABLE + "." + II_VALUE, " " + sign + " ");
		if (values.size() > 0) {
			query.sql("(");
			DataTypeMapper.appendPreparedStatementRequestValues(param.getType(), query, values);
			query.sql(")");
		} else {
			query.sql("(NULL)");
		}
	}

	public BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur) {
		if (param.isFulltextFilterable()) {
			if (!sign.equals(IN) && !sign.equals(NOT_IN))
				return queryBuilder;
			BooleanQuery.Builder innerQuery = new BooleanQuery.Builder();
			Occur innerOccur = Occur.SHOULD;
			if (sign.equals(NOT_IN))
				innerOccur = Occur.MUST_NOT;
			for (String value : values) {
				innerQuery.add(new TermQuery(new Term(param.getName(), value)), innerOccur);
			}
			queryBuilder.add(innerQuery.build(), occur);
		}
		return queryBuilder;
	}

	@Override
	public boolean isEmptySet() {
		return isEmptySet;
	}

	public boolean isNotBlank() {
		return !isBlank;
	}
	
}
