package ecommander.model.datatypes;

import ecommander.fwk.JsoupUtils;

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
		return JsoupUtils.prepareValidXml((String) value);
	}
}
