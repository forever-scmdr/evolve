package ecommander.controllers.output;

import ecommander.model.ParameterDescription;
/**
 * Выводит определение параметра
 * 

	<param name="name" type="string" quantifier="single" caption="Название" description="" format="">Раздел 1</param>

 * 
 * @author EEEE
 *
 */
public class ParameterDescriptionSimpleMDWriter extends MetaDataWriter {
	
	public static final String PARAM_TAG = "param";

	public static final String NAME_ATTRIBUTE = "name";
	public static final String ID_ATTRIBUTE = "id";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	public static final String CAPTION_ATTRIBUTE = "caption";
	public static final String DESCRIPTION_ATTRIBUTE = "description";
	public static final String DOMAIN_ATTRIBUTE = "domain";
	public static final String FORMAT_ATTRIBUTE = "format";
	public static final String OWNER_ID_ATTRIBUTE = "owner-id";
	
	private ParameterDescription paramDesc;
	private String value;

	public ParameterDescriptionSimpleMDWriter(ParameterDescription paramDesc, String value) {
		super();
		this.paramDesc = (ParameterDescription) paramDesc;
		this.value = value;
	}
	
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.startElement(PARAM_TAG, 
				ID_ATTRIBUTE, paramDesc.getId(), 
				NAME_ATTRIBUTE, paramDesc.getName(), 
				TYPE_ATTRIBUTE, paramDesc.getType(), 
				QUANTIFIER_ATTRIBUTE, paramDesc.getQuantifier(),
				CAPTION_ATTRIBUTE, paramDesc.getCaption(),
				DESCRIPTION_ATTRIBUTE, paramDesc.getDescription(),
				FORMAT_ATTRIBUTE, paramDesc.getFormat());
		xml.addText(value);
		xml.endElement();
		return xml;
	}

	@Override
	public String toString() {
		return "param: " + paramDesc.getName();
	}

}