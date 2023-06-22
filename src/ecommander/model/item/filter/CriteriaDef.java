package ecommander.model.item.filter;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.controllers.output.XmlDocumentBuilder;
import ecommander.model.datatypes.DataType.Type;

/**
 * Содержит критерий сравнения, т. е. название парамтера и знак сравнения
 * Содержит:
 * - знак сравнения
 * - ID парамтера
 * - шаблон сравнения
 * 
 * Вывод:
 * 
	<criteria sign="like" param="sys" pattern="*v*" id="3" type="integer" />
 * 
 * TODO <fix> добавить тип сравнения COMPARE_TYPE
 * 
 * @author EEEE
 *
 */
public class CriteriaDef extends FilterDefPart {
	
	static final String CRITERIA_ELEMENT = "criteria";
	static final String ID_ATTRIBUTE = "id";
	static final String PARAM_ATTRIBUTE = "param";
	static final String PATTERN_ATTRIBUTE = "pattern";
	static final String SIGN_ATTRIBUTE = "sign";
	static final String TYPE_ATTRIBUTE = "type";
	
	private String sign;
	private String paramName;
	private Type type;
	private String pattern;
	
	public CriteriaDef(String sign, String paramName, String paramTypeName, String pattern) {
		this.sign = sign;
		this.paramName = paramName;
		this.pattern = pattern;
		this.type = null;
		if (!StringUtils.isBlank(paramTypeName))
			type = Type.fromString(paramTypeName);
		if (pattern == null) this.pattern = Strings.EMPTY;
	}
	
	public CriteriaDef(String sign, String paramName, Type paramType, String pattern) {
		this.sign = sign;
		this.paramName = paramName;
		this.pattern = pattern;
		this.type = paramType;
		if (pattern == null) this.pattern = Strings.EMPTY;
	}

	public void update(String sign, String paramName, String paramTypeName, String pattern) {
		this.sign = sign;
		this.paramName = paramName;
		this.pattern = pattern;
		this.type = null;
		if (!StringUtils.isBlank(paramTypeName))
			type = Type.fromString(paramTypeName);
		if (pattern == null) this.pattern = Strings.EMPTY;
	}
	
	public void update(String sign, String paramName, Type paramType, String pattern) {
		this.sign = sign;
		this.paramName = paramName;
		this.pattern = pattern;
		this.type = paramType;
		if (pattern == null) this.pattern = Strings.EMPTY;
	}
	
	@Override
	void outputXML(XmlDocumentBuilder doc) {
		doc.addEmptyElement(CRITERIA_ELEMENT, ID_ATTRIBUTE, getId(), PARAM_ATTRIBUTE, paramName, SIGN_ATTRIBUTE, sign, PATTERN_ATTRIBUTE,
				pattern);
		if (type != null)
			doc.insertAttributes(TYPE_ATTRIBUTE, type.toString());
	}

	public String getSign() {
		return sign;
	}

	public String getParamName() {
		return paramName;
	}

	public String getPattern() {
		return pattern;
	}
	
	public boolean hasPattern() {
		return !StringUtils.isBlank(pattern);
	}

	@Override
	protected void visit(FilterDefinitionVisitor visitor) {
		// Ничего не делать
	}
}
