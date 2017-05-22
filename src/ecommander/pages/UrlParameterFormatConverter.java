package ecommander.pages;

import org.apache.commons.lang3.StringUtils;

/**
 * Содержит методы по преобразованию различных внутренних вещей в формат HTML
 * @author EEEE
 * @deprecated
 */
public class UrlParameterFormatConverter {
	// Разделитель названия поля ввода и ID айтема, когда айтем ассоциирован с полями ввода
	private static final char ID_INUT_NAME_DELIMITER = '~';
	/**
	 * Создает полное название поля ввода, с которым ассоциирован айтем
	 * 
	 * @param itemId
	 * @param inputName
	 * @return
	 */
	public static String createInputName(int itemTypeId, long itemId, String inputName) {
		return inputName + ID_INUT_NAME_DELIMITER + itemTypeId + ID_INUT_NAME_DELIMITER + itemId;
	}
	/**
	 * Возвращает массив из строк, который содержит название инпута, название айтема и ID айтема
	 * - название инпута
	 * - название айтема
	 * - id айтема
	 * @param fullInputName
	 * @return
	 */
	public static String[] splitInputName(String fullInputName) {
		return StringUtils.split(fullInputName, ID_INUT_NAME_DELIMITER);
	}
//	
//	public static void main(String[] args) {
//		System.out.println("new_quantity:device:8".split(":"));
//	}
}
