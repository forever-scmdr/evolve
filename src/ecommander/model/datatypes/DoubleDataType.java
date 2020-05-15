package ecommander.model.datatypes;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public class DoubleDataType extends FormatDataType {

	private static final NumberFormat dotDelimFormat = NumberFormat.getInstance(new Locale("en"));
	private static final NumberFormat commaDelimformat = NumberFormat.getInstance(new Locale("ru"));

	static {
		dotDelimFormat.setMaximumFractionDigits(8);
		commaDelimformat.setMaximumFractionDigits(8);
	}

	public DoubleDataType(Type type) {
		super(type);
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (formatter == null)
			formatter = commaDelimformat;
		return super.outputValue(value, formatter);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		if (StringUtils.isBlank(stringValue))
			return null;
		if (formatter != null) {
			ParsePosition pp = new ParsePosition(0);
			Number num = null;
			String toParse = stringValue.replaceAll(" ", "");
			num = ((DecimalFormat)formatter).parse(toParse, pp);
			if (pp.getIndex() == stringValue.length()) {
				return num.doubleValue();
			}
		}
		return parse(stringValue);
	}
	
	public static Double parse(String value) {
		if (value == null)
			return null;
		ParsePosition pp = new ParsePosition(0);
		Number num = null;
		try	{
			num = commaDelimformat.parse(value, pp);
			if (pp.getIndex() != value.length()) {
				num = dotDelimFormat.parse(value);
			}
			if (num == null)
				return null;
			return num.doubleValue();
		}
		catch (Exception e) {
			return null;
		}
	}

}
