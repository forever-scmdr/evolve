package ecommander.controllers.output;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.item.ParameterDescription;
/**
 * Выводит определение параметра
 * 

	<param name="name" id="44" type="string" quantifier="single" caption="Название" description="" domain="" format="" owner-id="22"/>
	
	Возможен и такой вариант (с name-old и type-old)
	
	<parameter name="name" old-name="old" id="44" type="string" old-type="byte" 
		quantifier="single" caption="Название" description="" domain="" format="" owner-id="22" hidden="false"/>

 * 
 * @author EEEE
 *
 */
public class ParameterDescriptionMDWriter extends MetaDataWriter {
	
	public static final String PARAMETER_TAG = "parameter";

	public static final String NAME_ATTRIBUTE = "name";
	public static final String NAME_OLD_ATTRIBUTE = "name-old";
	public static final String ID_ATTRIBUTE = "id";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String TYPE_OLD_ATTRIBUTE = "type-old";
	public static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	public static final String CAPTION_ATTRIBUTE = "caption";
	public static final String DESCRIPTION_ATTRIBUTE = "description";
	public static final String DOMAIN_ATTRIBUTE = "domain";
	public static final String FORMAT_ATTRIBUTE = "format";
	public static final String OWNER_ID_ATTRIBUTE = "owner-id";
	public static final String VIRTUAL_ATTRIBUTE = "virtual";
	public static final String HIDDEN_ATTRIBUTE = "hidden";
	
	private ParameterDescription paramDesc;
	private String nameOld;
	private String typeOld;

	public ParameterDescriptionMDWriter(Object paramDesc) {
		super();
		this.paramDesc = (ParameterDescription) paramDesc;
	}
	
	public XmlDocumentBuilder write(XmlDocumentBuilder xml) {
		ArrayList<String> attrs = new ArrayList<String>(Arrays.asList(
				NAME_ATTRIBUTE, paramDesc.getName(), 
				ID_ATTRIBUTE, paramDesc.getId() + "", 
				TYPE_ATTRIBUTE, paramDesc.getType().toString(), 
				QUANTIFIER_ATTRIBUTE, paramDesc.getQuantifier() + "",
				CAPTION_ATTRIBUTE, paramDesc.getCaption(),
				DESCRIPTION_ATTRIBUTE, paramDesc.getDescription(),
				DOMAIN_ATTRIBUTE, paramDesc.getDomainName(),
				FORMAT_ATTRIBUTE, paramDesc.getFormat(),
				OWNER_ID_ATTRIBUTE, paramDesc.getOwnerItemId() + "",
				VIRTUAL_ATTRIBUTE, paramDesc.isVirtual() + "",
				HIDDEN_ATTRIBUTE, paramDesc.isHidden() + ""
		));
		if (!StringUtils.isBlank(nameOld)) {
			attrs.add(NAME_OLD_ATTRIBUTE);
			attrs.add(nameOld);
		}
		if (!StringUtils.isBlank(typeOld)) {
			attrs.add(TYPE_OLD_ATTRIBUTE);
			attrs.add(typeOld);
		}
		if (additional.size() > 0) {
			xml.startElement(PARAMETER_TAG, attrs.toArray(new Object[0]));
			for (MetaDataWriter part : additional) {
				part.write(xml);
			}
			xml.endElement();
		} else {
			xml.addEmptyElement(PARAMETER_TAG, attrs.toArray(new Object[0]));
		}
		return xml;
	}

	public void setNameOld(String nameOld) {
		this.nameOld = nameOld;
	}
	
	public void setTypeOld(String typeOld) {
		this.typeOld = typeOld;
	}
	
	@Override
	public String toString() {
		return "param: " + paramDesc.getName();
	}

}