package ecommander.fwk;

import com.ibm.icu.text.RuleBasedNumberFormat;
import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Locale;


/**
 * Методы для работы со строками.
 * Включают в себя методы для работы с файлами настроек и текстовых констант.
 * 
 * @author Karlov
 */
public class Strings
{
	public static final String SYSTEM_ENCODING = "UTF-8";
	public static final String EMPTY = "";
	public static final char SPACE = ' ';
	public static final String SLASH = "/";
	/**
	 * This method ensures that the output String has only valid XML unicode characters as specified by the XML 1.0 standard. For reference,
	 * please see <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>. This method will return an empty String if the
	 * input is null or empty.
	 * 
	 * @param in
	 *            The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public static String stripNonValidXMLCharacters(String in) {
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.
		if (in == null || ("".equals(in)))
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD)) || ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}
	/*******************************************************************************************************
	 *                                        Транслитерация
	 *******************************************************************************************************/
	
	private static final String DIGITS = "1234567890";
	private static final String RUSSIAN_MATCH_LETTERS = DIGITS + "_abcdefghijklmnopqrstuvwxyzабвгдеёжзиыйклмнопрстуфхцчшщэюя. ,?/\\|:-\"='%";
	private static final String[] ENGLISH_REPLACEMENT_LETTERS = {
		"1","2","3","4","5","6","7","8","9","0","_",
		"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",		
		"a","b","v","g","d","e","yo","g","z","i","y","i","k","l","m","n","o","p","r","s","t",
		"u","f","h","ts","ch","sh","sch","e","yu","ya",".","_","","ask","_","_","_","_","_","","","","_"
	};
	private static final String PASSWORD_LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static Charset ASCII_CHARSET = Charset.forName("ISO-8859-1");
	
	/**
	 * Производит транслитерацию
	 * @param russian
	 * @return
	 */
    public static String translit(String russian) {
        StringBuilder english = new StringBuilder("");
        char[] russianChars = russian.toLowerCase().toCharArray();
        for(int i = 0; i < russianChars.length; i++) {
            int alphabetIndex = RUSSIAN_MATCH_LETTERS.indexOf(russianChars[i]);
            if(alphabetIndex != -1)
            	english.append(ENGLISH_REPLACEMENT_LETTERS[alphabetIndex]);
        }
        return english.toString();
    }
    /**
     * Создать строку, которая не содержит русских символов, спецсимволов и может являться именем XML элемента
     * @param invalid
     * @return
     */
    public static String createXmlElementName(String invalid) {
    	String halfValid = translit(invalid.trim());
    	if (StringUtils.isBlank(halfValid))
    		return null;
    	if (StringUtils.contains(DIGITS, halfValid.charAt(0)) || halfValid.charAt(0) == '.')
    		return StringUtils.substring("_" + halfValid, 0, 254);
    	return StringUtils.substring(halfValid, 0, 254);
    }

    /**
     * Проверяет, является ли строка 
     * @param string
     * @return
     */
    public static boolean isPureASCII(String string) {
    	return ASCII_CHARSET.newEncoder().canEncode(string);
    }
    /**
     * Преобразование строки в long со значением по умолчанию в случае если разбор невозможен
     * @param numberStr
     * @param defaultValue
     * @return
     */
    public static long parseLongDefault(String numberStr, long defaultValue) {
    	try {
    		return Long.parseLong(numberStr);
    	} catch (Exception e) {
    		return defaultValue;
    	}
    }
    /**
     * Преобразование строки в int со значением по умолчанию в случае если разбор невозможен
     * @param numberStr
     * @param defaultValue
     * @return
     */
    public static int parseIntDefault(String numberStr, int defaultValue) {
    	try {
    		return Integer.parseInt(numberStr);
    	} catch (Exception e) {
    		return defaultValue;
    	}
    }
    /**
     * Сгенерировать случайный пароль
     * @param length
     * @return
     */
    public static String generatePassword(int length) {
		SecureRandom random = new SecureRandom();
    	String pass = new String();
		byte[] ba = random.generateSeed(length);
		for (byte b : ba) {
			int idx = Math.abs(b % PASSWORD_LETTERS.length());
			pass += PASSWORD_LETTERS.charAt(idx);
		}
		return pass;
    }
    /**
     * Выводит слово с правильным окончанием в зависимости от количества
     * @param number - количество
     * @param forms - 3 формы слова (1, 2, много)
     * @return
     */
    public static String numberEnding(double number, String... forms) {
    	long round = Math.round(number);
    	long mod100 = round % 100;
    	long mod10 = round % 10;
    	if (mod100 > 10 && mod100 < 20)
    		return forms[2];
    	if (mod10 == 1)
    		return forms[0];
    	if (mod10 > 0 && mod10 < 5)
    		return forms[1];
    	return forms[2];
    }
    /**
     * Выводит число прописью на русском
     * @param number
     * @return
     */
    public static String numberToRusWords(double number) {
		RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"), RuleBasedNumberFormat.SPELLOUT);
		return nf.format(number);
    }

	/**
	 * Очистить HTML от невалидных частей
	 * @param html
	 * @return
	 */
    public static String cleanHtml(String html) {
	    CleanerProperties props = new CleanerProperties();

		// set some properties to non-default values
	    props.setTranslateSpecialEntities(true);
	    props.setTransResCharsToNCR(true);
	    props.setOmitComments(true);
	    props.setOmitDoctypeDeclaration(true);
	    props.setOmitCdataOutsideScriptAndStyle(true);
	    props.setPruneTags("script");
	    props.setNamespacesAware(false);
	    //props.setDeserializeEntities(true);
	    //props.setRecognizeUnicodeChars(true);
	    //props.setTranslateSpecialEntities(true);
	    //props.setTransSpecialEntitiesToNCR(true);
	    props.setOmitXmlDeclaration(true);

		// do parsing
	    TagNode tagNode = new HtmlCleaner(props).clean(html);

		// serialize to xml file
	    return new PrettyXmlSerializer(props).getAsString(tagNode);
	    //return new PrettyHtmlSerializer(props).getAsString(tagNode);
    }

	/**
	 * Получить название файла из пути к файлу
	 * @param fileName
	 * @return
	 */
	public static String getFileName(String fileName) {
		return StringUtils.lowerCase(translit(StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, '/') + 1)));
	}

	public static String createFileName(String string) {
		return translit(StringUtils.lowerCase(string));
	}

    public static void main(String[] args) {
    	System.out.println(translit("подъёмник?"));
    }
}
