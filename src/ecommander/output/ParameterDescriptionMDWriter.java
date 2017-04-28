package ecommander.output;

import java.util.ArrayList;
import java.util.Arrays;

import ecommander.model.DataModelXmlElementNames;
import org.apache.commons.lang3.StringUtils;

import ecommander.model.ParameterDescription;
/**
 * Выводит определение параметра
 * 

	<param name="name" ag-id="44" ag-hash="1212323434" type="string" quantifier="single" caption="Название" description="" domain="" format="" owner-id="22"/>

 * 
 * @author EEEE
 *
 */
public class ParameterDescriptionMDWriter extends MetaDataWriter implements DataModelXmlElementNames {

	private ParameterDescription paramDesc;

	public ParameterDescriptionMDWriter(Object paramDesc) {
		super();
		this.paramDesc = (ParameterDescription) paramDesc;
	}
	
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		ArrayList<String> attrs = new ArrayList<>(Arrays.asList(
				NAME, paramDesc.getName(),
				CAPTION, paramDesc.getCaption(),
				TYPE, paramDesc.getType().toString(),
				MULTIPLE, paramDesc.isMultiple() + "",
				DOMAIN, paramDesc.getDomainName(),
				FORMAT, paramDesc.getFormat(),
				AG_ID, paramDesc.getId() + "",
				AG_HASH, paramDesc.getName().hashCode() + "",
				DESCRIPTION, paramDesc.getDescription(),
				DEFAULT, paramDesc.getDefaultValue(),
				OWNER_ID, paramDesc.getOwnerItemId() + "",
				VIRTUAL, paramDesc.isVirtual() + "",
				HIDDEN, paramDesc.isHidden() + ""
		));
		xml.startElement(PARAMETER, attrs.toArray(new Object[0]));
		writeSubwriters(xml);
		xml.endElement();
		return xml;
	}

	@Override
	public String toString() {
		return "param: " + paramDesc.getName();
	}

}