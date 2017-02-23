package ecommander.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Хранилище для всех доменов и их значений.
 * Все время находится в загруженном состоянии
 * @author EEEE
 *
 */
public class DomainRegistry {

	public static final String CHECKBOX = "checkbox";
	public static final String COMBOBOX = "combobox";
	public static final String RADIOGROUP = "radiogroup";
	
	private static DomainRegistry singleton = null;
	
	private LinkedHashMap<String, Domain> domains = null;
	
	private DomainRegistry() {
		domains = new LinkedHashMap<String, Domain>();
	}
	
	private static DomainRegistry getSingleton() {
		if (singleton == null) 
			singleton = new DomainRegistry();
		return singleton;
	}
	
	public static void addDomain(Domain domain) {
		getSingleton().domains.put(domain.getName(), domain);
	}
	
	public static void removeDomain(String domainName) {
		getSingleton().domains.remove(domainName);
	}
	
	public static Domain getDomain(String domainName) {
		return getSingleton().domains.get(domainName);
	}
	
	public static boolean domainExists(String domainName) {
		return getSingleton().domains.containsKey(domainName);
	}
	
	public static void clearRegistry() {
		singleton = new DomainRegistry();
	}
	
	public static Iterator<Domain> getAllDomainsIterator() {
		return getSingleton().domains.values().iterator();
	}
	/**
	 * Создает и возвращает список названий доменов в отсортированном по алфавиту виде
	 * @return
	 * @throws Exception 
	 */
	public static ArrayList<String> getDomainNames() {
		ArrayList<String> domainNames = new ArrayList<String>();
		domainNames.addAll(getSingleton().domains.keySet());
		Collections.sort(domainNames);
		return domainNames;
	}
}
