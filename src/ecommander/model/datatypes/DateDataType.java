package ecommander.model.datatypes;

import ecommander.fwk.Strings;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
/**
 * Конструирование значения из строки:
 * 		1) предполагается, что значение вводится в виде строки даты определенного формата типа День.Месяц.Год 
 * 		   или что-то типа того. Значение получается с помощью соответствующего форматтера
 * 		2) если на первом шаге произошло исключение, то предполагается, что значение вводится в виде числа Long,
 * 		   т.е. миллисекунды. Тогда используестя простой метод parseLong
 * 		3) если произошлоисключение, возвращается 0
 * @author E
 *
 */
public class DateDataType extends FormatDataType {
	
//	public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm");
//	public static SimpleDateFormat DAY_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
//	public static SimpleDateFormat REPORT_FORMATTER = new SimpleDateFormat("dd.MM.yyyy_HH.mm");

	private static final String MILLIS_META = "millis"; // дата или время в миллисекундах
	
	public static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").withZoneUTC();
	public static DateTimeFormatter DAY_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy").withZoneUTC();
	public static DateTimeFormatter REPORT_FORMATTER = DateTimeFormat.forPattern("dd.MM.yyyy_HH.mm").withZoneUTC();
	
	private static int TIMEZONE_HOUR_OFFSET = 100;
	
	public DateDataType(Type type) {
		super(type);
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		return parseDate(stringValue, (DateTimeFormatter)formatter);
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		return outputDate((Long) value, (DateTimeFormatter) formatter);
	}

	@Override
	public Object createFormatter(String format) {
		return getFormatter(format);
	}
	
	@Override
	public boolean hasMeta() {
		return true;
	}

	@Override
	public HashMap<String, String> createMeta(Object value, Object... extraParams) {
		HashMap<String, String> meta = new HashMap<>();
		meta.put(MILLIS_META, value.toString());
		return meta;
	}

	/**
	 * Преобразовать строку в дату
	 * @param day
	 * @return
	 */
	public static LocalDateTime parseDay(String day) {
		try {
			return (DAY_FORMATTER).parseLocalDateTime(day);
		} catch (IllegalArgumentException e1) {
			return null;
		}
	}
	/**
	 * Преобразовать строку в дату
	 * @param stringValue
	 * @param format
	 * @return
	 */
	public static Long parseDate(String stringValue, DateTimeFormatter... format) {
		if (stringValue == null)
			return null;
		DateTimeFormatter formatter = null;
		if (format.length == 0 || format[0] == null)
			formatter = DATE_FORMATTER;
		else
			formatter = format[0];
		try	{
			return formatter.parseDateTime(stringValue).getMillis();
		}
		catch (Exception e) {
			return parseDate(stringValue);
		}
	}
	/**
	 * Преобразовать строку в дату с помощью стандартных шаблонов
	 * @param stringValue
	 * @return
	 */
	public static Long parseDate(String stringValue) {
		try	{
			return DATE_FORMATTER.parseDateTime(stringValue).getMillis();
		}
		catch (Exception e) {
			try {
				return DAY_FORMATTER.parseDateTime(stringValue).dayOfMonth().roundFloorCopy().getMillis();
			} catch (Exception e1) {
				try {
					return new Long(stringValue);
				} catch (NumberFormatException e2) {
					return null;
				}
			}
		}
	}
	
	
	public static String outputDate(Long millis, DateTimeFormatter... format) {
		if (millis == null) return Strings.EMPTY;
		DateTimeFormatter formatter = null;
		if (format.length != 0)
			formatter = format[0];

		String s = (formatter != null)? formatter.print(millis) : DATE_FORMATTER.print(millis);

		return s;
	}
	
	public static void setTimeZoneHourOffset(int offset) {
		DATE_FORMATTER = DATE_FORMATTER.withZone(DateTimeZone.forOffsetHours(offset));
		DAY_FORMATTER = DAY_FORMATTER.withZone(DateTimeZone.forOffsetHours(offset));
		REPORT_FORMATTER = REPORT_FORMATTER.withZone(DateTimeZone.forOffsetHours(offset));
		TIMEZONE_HOUR_OFFSET = offset;
	}
	/**
	 * Создать форматтер для даты и времени
	 * @param format
	 * @return
	 */
	public static DateTimeFormatter getFormatter(String format) {
		if (TIMEZONE_HOUR_OFFSET < 100)
			return DateTimeFormat.forPattern(format).withZone(DateTimeZone.forOffsetHours(TIMEZONE_HOUR_OFFSET));
		return DateTimeFormat.forPattern(format).withZoneUTC();
	}
	
	public static void main(String[] args) {
		setTimeZoneHourOffset(3);
		System.out.println(parseDate("10.05.2016"));
		System.out.println(parseDate("10.05.2016 22:55"));
		System.out.println(parseDate("10.5.2016 22:55"));
		System.out.println(outputDate(parseDate("10.05.2016")));
		System.out.println(outputDate(parseDate("10.05.2016 22:55")));
		System.out.println(outputDate(parseDate("10.5.2016 22:55")));
		System.out.println();
		System.out.println();
		long millis = System.currentTimeMillis();
		System.out.println(outputDate(millis));
		System.out.println(parseDate("cool"));
	}
}
