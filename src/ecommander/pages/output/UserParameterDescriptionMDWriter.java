package ecommander.pages.output;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.DataModelXmlElementNames;
import ecommander.model.ParameterDescription;
/**
 * Выводит определение параметра
 * 

	<param name="name" type="string" quantifier="single" caption="Название" description="" format="">Раздел 1</param>

 * 
 * @author EEEE
 *
 */
public class UserParameterDescriptionMDWriter extends MetaDataWriter implements DataModelXmlElementNames {

	private ParameterDescription paramDesc;
	private String value;

	public UserParameterDescriptionMDWriter(ParameterDescription paramDesc, String value) {
		super();
		this.paramDesc = (ParameterDescription) paramDesc;
		this.value = value;
	}
	
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		xml.startElement(PARAM,
				ID, paramDesc.getId(),
				NAME, paramDesc.getName(),
				TYPE, paramDesc.getType(),
				MULTIPLE, paramDesc.isMultiple() + "",
				CAPTION, paramDesc.getCaption(),
				DESCRIPTION, paramDesc.getDescription(),
				FORMAT, paramDesc.getFormat());
		xml.addText(value);
		xml.endElement();
		return xml;
	}

	@Override
	public String toString() {
		return "param: " + paramDesc.getName();
	}

}