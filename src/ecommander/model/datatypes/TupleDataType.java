package ecommander.model.datatypes;

import ecommander.fwk.Pair;
import ecommander.fwk.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class TupleDataType extends FormatDataType {

	public static final String KEY_META = "key";

	private static TupleFormatter DEFAULT_FORMAT = new TupleFormatter();

	private static class TupleFormatter {
		private static String DEFAULT_SEPARATOR = "$+$";

		private String format = DEFAULT_SEPARATOR;

		private TupleFormatter(String format) {
			if (StringUtils.isNotBlank(format))
				this.format = format;
		}

		private TupleFormatter() {

		}

		private Pair<String, String> parse(String str) {
			String[] values = StringUtils.splitByWholeSeparator(str, format);
			if (values == null || values.length == 0 || StringUtils.isBlank(values[0]))
				return null;
			return values.length == 1 ? new Pair<>(values[0], null) : new Pair<>(values[0], values[1]);
		}

		private String output(Pair<String, String> value) {
			if (value == null || StringUtils.isBlank(value.getLeft()))
				return Strings.EMPTY;
			return StringUtils.isBlank(value.getRight()) ? value.getLeft() : value.getRight();
		}
	}

	public TupleDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		if (formatter == null) formatter = DEFAULT_FORMAT;
		return ((TupleFormatter) formatter).parse(stringValue);
	}

	@Override
	public Object createFormatter(String format) {
		return new TupleFormatter(format);
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (formatter == null) formatter = DEFAULT_FORMAT;
		return ((TupleFormatter) formatter).output((Pair<String, String>) value);
	}

	@Override
	public boolean hasMeta() {
		return true;
	}

	@Override
	public HashMap<String, String> createMeta(Object value, Object... extraParams) {
		HashMap<String, String> metas = null;
		if (value != null) {
			metas = new HashMap<>();
			metas.put(KEY_META, ((Pair<String, String>) value).getLeft());
		}
		return metas;
	}

	public static String createCombinedValue(Pair<String, String> value, Object formatter) {
		if (formatter == null) formatter = DEFAULT_FORMAT;
		return StringUtils.isBlank(value.getRight()) ? value.getLeft() : value.getLeft() + ((TupleFormatter) formatter).format + value.getRight();
	}
}
