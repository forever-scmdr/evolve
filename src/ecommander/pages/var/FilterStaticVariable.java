package ecommander.pages.var;

import ecommander.fwk.FilterProcessException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Страничная переменная, которая содержит данные, которые ввел пользователь сайта для фильтрации
 * Формат переменной фильтра
 * 
 * 1:fv:android:fl:3:fv:5.0:fl:12:fv:200:fl::fp:3:fl::fs:555:asc
 * 
 * :fl: - разделитель значений
 * 1:fv:android
 * 
 * 
 * @author EEEE
 *
 */
public class FilterStaticVariable extends StaticVariable {

	private static final int DELIM_LENGTH = 4;
	public static final String TOKEN_DELIM = "-fl-";
	public static final char VALUE_DELIM = '~';
	public static final String SORTING = "-fs-";
	public static final String PAGE = "-fp-";

	private HashMap<Integer, ArrayList<String>> paramValues;
	private int sortingParamId;
	private String sortingDirection;
	private int pageNumber = 1;
	private boolean isParsed = false;

	public FilterStaticVariable(String varId, String value) {
		super(null, varId, value);
	}

	/**
	 * Получить все отправленные пользователем поля ввода
	 * @return
	 * @throws FilterProcessException
	 */
	public Set<Integer> getPostedInputs() throws FilterProcessException {
		parse();
		if (paramValues != null)
			return new HashSet<>(paramValues.keySet());
		return new HashSet<>();
	}
	/**
	 * Получить значение параметра
	 * @param inputId
	 * @return
	 * @throws FilterProcessException
	 */
	public ArrayList<String> getValue(int inputId) throws FilterProcessException {
		parse();
		if (paramValues != null && paramValues.containsKey(inputId))
			return paramValues.get(inputId);
		return new ArrayList<String>();
	}
	
	public boolean hasSorting() {
		return sortingParamId > 0;
	}
	
	public int getSortingParamId() {
		return sortingParamId;
	}
	
	public String getSortingDirection() {
		return sortingDirection;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	private void parse() throws FilterProcessException {
		if (!isParsed) {
			String filterUrlStr = (String) getSingleValue();
			if (paramValues == null && !StringUtils.isBlank(filterUrlStr)) {
				paramValues = new HashMap<Integer, ArrayList<String>>(10);
				String[] tokens = StringUtils.splitByWholeSeparator(filterUrlStr, TOKEN_DELIM);
				for (String token : tokens) {
					// Номер страницы
					if (token.startsWith(PAGE)) {
						try {
							pageNumber = Integer.parseInt(token.substring(DELIM_LENGTH));
						} catch (NumberFormatException e) {
							pageNumber = 1;
						}
					} 
					// ID параметра сортировки
					else if (token.startsWith(SORTING)) {
						try {
							String[] parts = StringUtils.split(token.substring(DELIM_LENGTH), VALUE_DELIM);
							sortingParamId = Integer.parseInt(parts[0]);
							sortingDirection = parts[1];
						} catch (NumberFormatException e) {
							sortingParamId = 0;
							sortingDirection = "asc";
						}
					} 
					// Стандартный случай - парамтер фильтрации (ID поля ввода и значение, введенное в него)
					else {
						try {
							int index = token.indexOf(VALUE_DELIM);
							int inputId = Integer.parseInt(token.substring(0, index));
							String value = token.substring(index + 1);
							if (!StringUtils.isBlank(value)) {
								ArrayList<String> values = paramValues.get(inputId);
								if (values == null) {
									values = new ArrayList<String>();
									paramValues.put(inputId, values);
								}
								values.add(value);
							}
						} catch (Exception e) {
							new FilterProcessException("can not parse filter input string", e);
						}
					}
				}
			}
			isParsed = true;
		}
	}

}
