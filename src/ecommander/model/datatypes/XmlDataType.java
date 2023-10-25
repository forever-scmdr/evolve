package ecommander.model.datatypes;

import ecommander.fwk.JsoupUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Тип данных XML
 * Created by E on 2/3/2018.
 */
public class XmlDataType extends StringDataType {

	public XmlDataType(Type type) {
		super(type);
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (StringUtils.isNotBlank((String) value))
			return (String) value;
			//return JsoupUtils.prepareValidXml((String) value);
		return "";
	}
}
