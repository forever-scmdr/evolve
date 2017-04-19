package ecommander.persistence.itemquery.fulltext;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import ecommander.fwk.EcommanderException;
/**
 * Различные притерии полнотекстового поиска
 * @author E
 *
 */
public class FulltextQueryCreatorRegistry {
	public static final String PREFIX = "prefix"; // начала слов + полное совпадение для большей релевантности
	public static final String DEFAULT = "default"; // простой поиск с учетом стемминга (полное совпадение)
	public static final String NEAR = "near"; // полное совпадение слов + взаимное расположение рядом
	public static final String FIRST = "first"; // полное совпадение слов + взаимное расположение рядом + все слова находятся в начале документа
	public static final String EQUAL = "equal"; // полное совпадение слов (возможна полько перестановка местами)
	public static final String WILDCARD = "wildcard"; // полное совпадение слов (возможна полько перестановка местами)
	
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
		creators.put(PREFIX, new TermPrefixFulltextQuery());
		creators.put(DEFAULT, new TermFulltextQuery());
		creators.put(NEAR, new NearFulltextQuery());
		creators.put(FIRST, new FirstFulltextQuery());
		creators.put(EQUAL, new EqualsFulltextQuery());
		creators.put(WILDCARD, new WildcardFulltextQuery());
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
