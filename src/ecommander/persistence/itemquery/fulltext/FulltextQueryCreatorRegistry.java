package ecommander.persistence.itemquery.fulltext;

import ecommander.fwk.EcommanderException;

import java.util.HashMap;
/**
 * Различные притерии полнотекстового поиска
 * @author E
 *
 */
public class FulltextQueryCreatorRegistry {
	public static final String PREFIX = "prefix"; // начала слов + полное совпадение для большей релевантности
	public static final String TERM = "term"; // простой поиск с учетом стемминга (полное совпадение)
	public static final String DEFAULT = "default"; // то же что и TERM, этот вариант по умолчанию
	public static final String NEAR = "near"; // полное совпадение слов + взаимное расположение рядом
	public static final String STRAIGHT = "straight"; // полное совпадение слов + взаимное расположение рядом + в том же порядке что и в запросе
	public static final String FIRST = "first"; // полное совпадение слов + взаимное расположение рядом + все слова находятся в начале документа
	public static final String EQUAL = "equal"; // полное совпадение слов (возможна полько перестановка местами)
	public static final String WILDCARD = "wildcard"; // полное включение запроса в часть слова разультата
	public static final String PARSED = "parsed"; // разбор запроса по правилам Lucene Query Parser (только с указанием параметра)
	
	private static FulltextQueryCreatorRegistry singleton;
	
	private static FulltextQueryCreatorRegistry getSingleton() {
		if (singleton == null) {
			singleton = new FulltextQueryCreatorRegistry();
		}
		return singleton;
	}
	
	private HashMap<String, LuceneQueryCreator> creators;
	
	private FulltextQueryCreatorRegistry() {
		creators = new HashMap<>();
		TermFulltextQuery termQuery = new TermFulltextQuery();
		creators.put(PREFIX, new TermPrefixFulltextQuery());
		creators.put(TERM, termQuery);
		creators.put(DEFAULT, termQuery);
		creators.put(NEAR, new NearFulltextQuery());
		creators.put(STRAIGHT, new NearStraightFulltextQuery());
		creators.put(FIRST, new FirstFulltextQuery());
		creators.put(EQUAL, new EqualsFulltextQuery());
		creators.put(WILDCARD, new WildcardFulltextQuery());
		creators.put(PARSED, new ParsedQuery());
	}
	
	private LuceneQueryCreator getQueryCreator(String type) throws Exception {
		LuceneQueryCreator creator = creators.get(type);
		if (creator == null) {
			creator = (LuceneQueryCreator) Class.forName(type).getConstructor().newInstance();
			creators.put(type, creator);
		}
		return creator;
	}
	/**
	 * Создать полнотекстовый критерий определенного типа
	 * @param type
	 * @return
	 * @throws EcommanderException
	 */
	public static LuceneQueryCreator getCriteria(String type) throws Exception {
		return getSingleton().getQueryCreator(type);
	}
	/**
	 * Создать полнотекстовый критерий по умолчанию
	 * @return
	 * @throws EcommanderException
	 */
	public static LuceneQueryCreator getCriteria() throws Exception {
		return getSingleton().getQueryCreator(DEFAULT);
	}
}
