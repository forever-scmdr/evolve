package ecommander.model.datatypes;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Класс для определения одного типа данных, такого, как string или date, или file
 * @author EEEE
 *
 */
public abstract class DataType {
	
	public enum Type {
		BYTE("byte"),
		INTEGER("integer"),
		LONG("long"),
		DATE("date"),
		DOUBLE("double"),
		DECIMAL("decimal"),
		CURRENCY("currency"),
		CURRENCY_PRECISE("currency-precise"),
		STRING("string"),
		TINY_TEXT("tiny-text"),
		SHORT_TEXT("short-text"),
		TEXT("text"),
		PLAIN_TEXT("plain-text"),
		FILE("file"),
		PICTURE("picture"),
		FILTER("filter"),
		XML("xml");

		private final String text;

		Type(String text) {
			this.text = text;
		}

		public String getName() {
			return text;
		}

		@Override
		public String toString() {
			return text;
		}

		public static Type get(String text) {
			if (text != null) {
				for (Type b : Type.values()) {
					if (text.equalsIgnoreCase(b.text)) {
						return b;
					}
				}
			}
			throw new IllegalArgumentException("No constant with text " + text + " found");
		}

	}
	
	private static HashSet<Type> BIG_TEXT_TYPES = new HashSet<Type>();
	static {
		BIG_TEXT_TYPES.add(Type.TINY_TEXT);
		BIG_TEXT_TYPES.add(Type.SHORT_TEXT);
		BIG_TEXT_TYPES.add(Type.TEXT);
		BIG_TEXT_TYPES.add(Type.PLAIN_TEXT);
		BIG_TEXT_TYPES.add(Type.FILTER);
		BIG_TEXT_TYPES.add(Type.XML);
	}
	
	private Type type = Type.STRING;
	
	DataType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}
	/**
	 * Создать значение соответствующего типа по строке
	 * @param stringValue
	 * @param formatter
	 * @return
	 */
	public abstract Object createValue(String stringValue, Object formatter);
	/**
	 * Вывести значение в виде строки
	 * @param value
	 * @return
	 */
	public abstract String outputValue(Object value, Object formatter);
	/**
	 * Есть ли дополнительные сведения для значения этого типа данных помимо собственно самого значения
	 * Например, это могут быть размер и тип файла для значения типа файл
	 * @return
	 */
	public abstract boolean hasMeta();
	/**
	 * Получить дополнительные сведения о переданном значении в зависимости от его типа.
	 * Например, это могут быть размер и тип файла для значения типа файл
	 * С помощью параметра extraParams можно передавать различные внешние параметры, которые не
	 * зависят непосредственно от типа данных и значения.
	 * @param value
	 * @param extraParams
	 * @return
	 */
	public abstract HashMap<String, String> getMeta(Object value, Object... extraParams);
	/**
	 * Файловый ли тип данных
	 * @return
	 */
	public boolean isFile() {
		return false;
	}
	/**
	 * Тип данных - длинный текст
	 * @return
	 */
	public boolean isBigText() {
		return BIG_TEXT_TYPES.contains(type);
	}

	@Override
	public String toString() {
		return type.toString();
	}
}