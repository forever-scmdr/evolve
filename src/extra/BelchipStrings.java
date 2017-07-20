package extra;

import java.util.Locale;

import ecommander.controllers.AppContext;

public class BelchipStrings {
	private static String RUSSIAN = "аАвВеЕкКмМнНоОрРсСтТхХ";
	private static String ENGLISH = "aAbBeEkKmMhHoOpPcCtTxX";
	
	public static String fromRtoE(String russian) {
        StringBuilder english = new StringBuilder("");
        char[] russianChars = russian.toCharArray();
        for(int i = 0; i < russianChars.length; i++) {
            int alphabetIndex = RUSSIAN.indexOf(russianChars[i]);
            if(alphabetIndex != -1)
            	english.append(ENGLISH.charAt(alphabetIndex));
            else
            	english.append(russianChars[i]);
        }
        return english.toString();
	}
	/**
	 * Разложить строку на составные части (заменив симвлоы с русских на английские)
	 * @param value
	 * @return
	 */
	public static String preanalyze(String value) {
		Locale loc = AppContext.getCurrentLocale();
		if (loc == null)
			loc = new Locale("ru");
		String[] parts = value.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			sb.append(' ').append(fromRtoE(part).toLowerCase(loc));
		}
		return sb.toString();
	}
	
//	private static String tokenize(String arg) {
//		StringBuilder sb = new StringBuilder();
//		try {
//			TokenStream stream = new RussianAnalyzer().tokenStream(null, arg);
//			stream.reset();
//			while (stream.incrementToken()) {
//				sb.append(stream.getAttribute(CharTermAttribute.class).toString()).append(' ');
//			}
//			stream.end();
//			stream.close();
//		} catch (IOException e) {
//			// not thrown b/c we're using a string reader...
//			throw new RuntimeException(e);
//		}
//		return sb.toString();
//	}
	
	public static void main(String[] args) {
		System.out.println(preanalyze("0805 560pf (X7R) 50v  10%"));
//		System.out.println(tokenize(createPreanalyzed("0805 560pf (X7R) 50v  10%")));

	}
}
