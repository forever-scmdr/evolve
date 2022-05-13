package ecommander.model.datatypes;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

public class DecimalDataType extends FormatDataType {

	private static final DecimalFormat dotDelimFormat = (DecimalFormat) NumberFormat.getInstance(new Locale("en"));
	private static final DecimalFormat commaDelimformat = (DecimalFormat) NumberFormat.getInstance(new Locale("ru"));
	static {
		dotDelimFormat.setParseBigDecimal(true);
		commaDelimformat.setParseBigDecimal(true);
		dotDelimFormat.setMaximumFractionDigits(10);
		commaDelimformat.setMaximumFractionDigits(10);
	}

	private int scale;

	public static final int DECIMAL = 6;
	public static final int CURRENCY = 2;
	public static final int CURRENCY_PRECISE = 4;

	public DecimalDataType(Type type, int scale) {
		super(type);
		this.scale = scale;
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (formatter == null)
			formatter = commaDelimformat;
		else
			((DecimalFormat) formatter).setParseBigDecimal(true);
		if (value != null)
			((BigDecimal)value).setScale(scale, BigDecimal.ROUND_HALF_EVEN);
		return super.outputValue(value, formatter);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		if (StringUtils.isBlank(stringValue))
			return null;
		if (formatter != null) {
			ParsePosition pp = new ParsePosition(0);
			BigDecimal num = null;
			String toParse = stringValue.replaceAll("\\s", "");
			num = (BigDecimal) ((DecimalFormat)formatter).parse(toParse, pp);
			if (pp.getIndex() == stringValue.length()) {
				return num.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
			}
		}
		return parse(stringValue, scale);
	}
	
	public static BigDecimal parse(String value, int scale) {
		if (value == null)
			return null;
		value = value.replaceAll("\\s", "");
		ParsePosition pp = new ParsePosition(0);
		BigDecimal num = null;
		try	{
			num = (BigDecimal) commaDelimformat.parse(value, pp);
			if (pp.getIndex() != value.length()) {
				num = (BigDecimal) dotDelimFormat.parse(value);
			}
			if (num == null)
				return null;
			return num.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
		}
		catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(parse("1,3355", 6));
		System.out.println(parse("123456789,3355", 6));
		System.out.println(parse("123456789,10203040506070", 2));
		System.out.println(parse("123456789,10203040506070", 4));
	}
}
