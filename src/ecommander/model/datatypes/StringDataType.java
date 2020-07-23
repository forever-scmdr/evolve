package ecommander.model.datatypes;

import ecommander.fwk.Strings;
import org.apache.commons.lang3.StringUtils;

import java.text.Format;
import java.text.MessageFormat;

public class StringDataType extends FormatDataType {

	public StringDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		return stringValue;
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (value == null) return Strings.EMPTY;
		return (String)value;
	}

	@Override
	public Format createFormatter(String format) {
		return new MessageFormat(format);
	}

	@Override
	public boolean isEmpty(Object value) {
		return value == null || StringUtils.isBlank(value.toString());
	}
}
