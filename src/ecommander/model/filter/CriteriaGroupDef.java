package ecommander.model.filter;

import ecommander.fwk.EcommanderException;
import ecommander.output.XmlDocumentBuilder;
import ecommander.model.LOGICAL_SIGN;

/**
 * Группа критериев с одним названием, описанием и логическим знаком, который 
 * применяется для объединения всех критериев
 * 
 * Вывод:
 * 
	<group name="System" sign="AND" id="1" comment="Operating system and its version">
		<input type="droplist" domain="systems" caption="system name" description="name of the system" id="2">
			<criteria sign="like" param="sys" pattern="*v*" id="3" />
		</input>
		<input type="text" caption="version" description="system version" id="4">
			<criteria sign="=" param="ver" id="5" />
		</input>
	</group>
 * 
 * @author EEEE
 *
 */
public class CriteriaGroupDef extends FilterDefPartContainer {

	static final String GROUP_ELEMENT = "group";
	static final String NAME_ATTRIBUTE = "name";
	static final String COMMENT_ATTRIBUTE = "comment";
	static final String ID_ATTRIBUTE = "id";
	static final String SIGN_ATTRIBUTE = "sign";

	
	protected String name;
	protected String comment;
	protected LOGICAL_SIGN sign;
	
	public CriteriaGroupDef(String name, String comment, String signStr) {
		this.name = name;
		this.comment = comment;
		this.sign = LOGICAL_SIGN.getSign(signStr);
	}

	public void update(String name, String comment, String signStr) {
		this.name = name;
		this.comment = comment;
		this.sign = LOGICAL_SIGN.getSign(signStr);
	}
	
	@Override
	protected void outputStartTag(XmlDocumentBuilder doc) {
		doc.startElement(GROUP_ELEMENT, SIGN_ATTRIBUTE, sign, ID_ATTRIBUTE, getId(), NAME_ATTRIBUTE, name, COMMENT_ATTRIBUTE, comment);
	}

	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public LOGICAL_SIGN getSign() {
		return sign;
	}

	@Override
	protected void visitSelf(FilterDefinitionVisitor visitor) throws EcommanderException {
		visitor.visitGroup(this);
	}
}
