package ecommander.model.datatypes;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.Strings;
import ecommander.controllers.AppContext;

public abstract class FormatDataType extends DataType {
	
	FormatDataType(Type type) {
		super(type);
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (value == null) return Strings.EMPTY;
		if (formatter != null) return ((Format)formatter).format(value);
		return value.toString();
	}

	public Object createFormatter(String format) {
		DecimalFormat formatter = (DecimalFormat)NumberFormat.getInstance(AppContext.getCurrentLocale());
		if (StringUtils.containsNone(format, '0', '#'))
			return null;
		try {
			formatter.applyPattern(format);
		} catch (IllegalArgumentException e) {
			return null;
		}
		return formatter;
	}

	@Override
	public boolean hasMeta() {
		return false; // По умолчанию нет метаданных
	}

	@Override
	public HashMap<String, String> createMeta(Object value, Object... extraParams) {
		return null; // По умолчанию нет метаданных
	}
	
	
}
