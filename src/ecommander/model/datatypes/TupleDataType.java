package ecommander.model.datatypes;

import ecommander.fwk.Pair;
import ecommander.fwk.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * Парное значение в виде ключ:значение. По сути представляет собой простую строку, но разделенную на две части
 * Хранится в БД как строка, ищестя как строка. Если надо искать только по ключу (все значения подходят), то надо
 * использовать ... sign="like" pattern="v%".
 * Если надо искать конкретное значение определенного ключа, то надо передать все подходящие значения целой строки
 * вида ключ_1$+$значение_1, ключ_1$+$значение_2, ключ_1$+$значение_3 ...
 */
public class TupleDataType extends FormatDataType {

	public static final String KEY_META = "key";
	public static final String VALUE_META = "value";

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
			return value.hasRigth() ? value.getLeft() + format + value.getRight() : value.getLeft();
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
		return outputTuple(value, formatter);
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
			Pair<String, String> pair = (Pair<String, String>) value;
			metas.put(KEY_META, pair.getLeft());
			metas.put(VALUE_META, pair.hasRigth() ? pair.getRight() : "");
		}
		return metas;
	}

	public static String outputTuple(Object value, Object formatter) {
		if (formatter == null) formatter = DEFAULT_FORMAT;
		return ((TupleFormatter) formatter).output((Pair<String, String>) value);
	}

	/**
	 * Создать объект нужного типа для tuple (Pair<String, String>), который потом можно
	 * устанавливать как значение параметра айтема
	 * @param name
	 * @param value
	 * @return
	 */
	public static Object newTuple(String name, String value) {
		return new Pair<>(name, value);
	}
}
