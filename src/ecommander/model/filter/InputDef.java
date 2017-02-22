package ecommander.model.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.common.exceptions.EcommanderException;
import ecommander.controllers.output.XmlDocumentBuilder;

/**
 * Один инпут
 * Содержит:
 * - подпись к полю ввода
 * - тип поля ввода
 * - домен
 * - дополнительное описание
 * 
 * Вывод:
 * 
	<input type="droplist" domain="systems" caption="system name" description="name of the system" id="2">
		<criteria sign="like" param="sys" pattern="*v*" id="3" />
	</input>

 * @author EEEE
 *
 */
public class InputDef extends FilterDefPartContainer {

	static final String INPUT_TAG = "input";
	static final String DOMAIN_ATTRIBUTE_TAG = "domain";
	static final String VALUE_TAG = "value";
	static final String TYPE_ATTRIBUTE = "type";
	static final String NAME_ATTRIBUTE = "name";
	static final String CAPTION_ATTRIBUTE = "caption";
	static final String DESCRIPTION_ATTRIBUTE = "description";
	static final String ID_ATTRIBUTE = "id";
	
	public static enum INPUT_TYPE {
		checkbox, radiogroup, checkgroup, text, droplist;
	}

	private INPUT_TYPE type;
	private String caption;
	private String description;
	private String domain;
	private ArrayList<String> domainValues = new ArrayList<String>();
	
	public InputDef(String type, String caption, String description, String domain) {
		this.type = INPUT_TYPE.valueOf(type);
		this.caption = caption;
		this.description = description;
		this.domain = domain;
		if (domain == null) this.domain = Strings.EMPTY;
		if (caption == null) this.caption = Strings.EMPTY;
		if (description == null) this.description = Strings.EMPTY;
	}
	
	public void update(String type, String caption, String description, String domain) {
		this.type = INPUT_TYPE.valueOf(type);
		this.caption = caption;
		this.description = description;
		this.domain = domain;
		if (domain == null) this.domain = Strings.EMPTY;
		if (caption == null) this.caption = Strings.EMPTY;
		if (description == null) this.description = Strings.EMPTY;
	}
	
	public boolean hasDomain() {
		return !StringUtils.isBlank(domain);
	}

	@Override
	protected void outputStartTag(XmlDocumentBuilder doc) {
		doc.startElement(INPUT_TAG, ID_ATTRIBUTE, getId(), TYPE_ATTRIBUTE, type, DOMAIN_ATTRIBUTE_TAG, domain, CAPTION_ATTRIBUTE, caption,
				DESCRIPTION_ATTRIBUTE, description);
		if (domainValues.size() > 0) {
			doc.startElement(DOMAIN_ATTRIBUTE_TAG);
			for (String value : domainValues) {
				doc.startElement(VALUE_TAG).addText(value).endElement();
			}
			doc.endElement();
		}
	}

	public INPUT_TYPE getType() {
		return type;
	}

	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public String getDomain() {
		return domain;
	}
	
	public final void addDomainValue(String value) {
		domainValues.add(value);
	}
	
	public final boolean hasDomainValues() {
		return domainValues.size() > 0;
	}
	/**
	 * Получить все критерии этого поля ввода
	 * @return
	 */
	public List<FilterDefPart> getCriterias() {
		return parts;
	}
	
	@Override
	protected void visitSelf(FilterDefinitionVisitor visitor) throws EcommanderException {
		visitor.visitInput(this);
	}
}
