package ecommander.pages;

import ecommander.controllers.SessionContext;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.pages.var.RequestVariablePE;
import ecommander.pages.var.VariablePE;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;

/**
 * Модель страницы, эта модель просто описывает страницу
 * @author EEEE
 *
 */
public class PagePE extends PageElementContainer implements VariablePE.VariableContainer {
	public static final String ELEMENT_NAME = "page";
	public static final String CONTENT_TYPE_HEADER = "Content-Type";
	public static final String TEXT_HTML_CONTENT_TYPE = "text/html";
	public static final String TEXT_XML_CONTENT_TYPE = "text/xml";
	
	// Группы пользователей, для которых доступна эта страница
	protected Set<String> userGroups = null;
	protected final String name; // Если название страницы пустое, то выводить XML страницы без XSL преобразования
	protected final String template;
	protected final boolean cacheable; // Можно ли кешировать эту страницу
	protected final String criticalItem; // Айтем, при отсутствии (не нахождении) которого вместо страницы должна выводиться 404 ошибка
	protected LinkedHashSet<String> cacheVars = null; 		// переменные, которые используются для уникального кеширования. Если страница содержит переменные, 
															// не содержащиеся в этом списке, то если эти переменные установлены, страница не кешируется
	private LinkedHashMap<String, RequestVariablePE> varPEDefs = null; // важен порядок следования переменных. Когда используенся уникальный текстовый ключ айтема,
															  // имя переменной не передается через ссылку. В этом случае для определения имени переменной
															  // используется ее порядковый номер в списке переменных.

	private String schedule; // Расписание запусков страницы (Cron)
	private HashMap<String, String> headers = null;

	public PagePE(String pageName, String pageTemplateName, boolean cacheable, Collection<String> cacheVars, String criticalItem) {
		super();
		this.name = pageName;
		this.template = pageTemplateName;
		this.cacheable = cacheable;
		userGroups = new HashSet<>(5);
		headers = new HashMap<>(5);
		if (cacheVars != null && !cacheVars.isEmpty()) {
			this.cacheVars = new LinkedHashSet<>(5);
			this.cacheVars.addAll(cacheVars);
		}
		if (StringUtils.isNotBlank(criticalItem))
			this.criticalItem = criticalItem;
		else
			this.criticalItem = null;
	}
	
	public boolean isCacheable() {
		return cacheable;
	}

	public String getPageName() {
		return name;
	}

	public String getTemplate() {
		return template;
	}
	/**
	 * Получить все заголовки, которые должны быть отправлены клиенту
	 * @return
	 */
	public Map<String, String> getResponseHeaders() {
		if (!headers.containsKey(CONTENT_TYPE_HEADER)) {
			if (transformationNeeded())
				headers.put(CONTENT_TYPE_HEADER, TEXT_HTML_CONTENT_TYPE);
			else
				headers.put(CONTENT_TYPE_HEADER, TEXT_XML_CONTENT_TYPE);
		}
		return headers;
	}
	/**
	 * Добавить HEADER для отправки с ответом сервера
	 * @param name
	 * @param value
	 */
	public void addHeader(String name, String value) {
		headers.put(name, value);
	}
	/**
	 * Этот метод надо использовать вместо createExecutableClone() без параметра
	 * для клонирования модели страницы (для получения модели страницы, предназначенной для загрузки)
	 * @param sessionContext
	 * @param link
	 * @param linkUrl - передается для того, чтобы не вызывать лишний раз serialize в ссылке
	 * @param baseLink
	 * @return
	 */
	public ExecutablePagePE createExecutableClone(SessionContext sessionContext, LinkPE link, String linkUrl, String baseLink) {
		ExecutablePagePE clone = new ExecutablePagePE(name, template, cacheable, sessionContext, cacheVars, criticalItem);
		clone.userGroups.addAll(userGroups);
		if (headers.size() > 0) {
			for (Entry<String, String> header : headers.entrySet()) {
				clone.addHeader(header.getKey(), header.getValue());
			}
		}
		//clone.varPEDefs.addAll(varPEDefs);
		if (varPEDefs != null) {
			for (VariablePE var : varPEDefs.values()) {
				var.createExecutableClone(clone, clone);
			}
		}
		if (link != null)
			clone.setRequestLink(link, linkUrl, baseLink);
		for (PageElement element : getAllNested())
			clone.addElement(element.createExecutableClone(clone, clone));
		return clone;
	}
	/**
	 * Доступна ли эта страница определенному пользователю
	 * @param user
	 * @return
	 */
	public boolean isUserAuthorized(User user) {
		if (userGroups.isEmpty())
			return true;
		if (user.isAnonimous())
			return false;
		for (String userGroup : userGroups) {
			if (user.inGroup(userGroup))
				return true;
		}
		return false;
	}
	/**
	 * Добавить группу
	 * @param groupName
	 */
	public void addAuthorityGroup(String groupName) {
		userGroups.add(groupName);
	}
	/**
	 * Надо ли выводить чистый XML без XSL преобразования
	 * @return
	 */
	public boolean transformationNeeded() {
		return !StringUtils.isBlank(template);
	}

	@Override
	protected PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage) {
		throw new RuntimeException("Nerver use method createExecutableShallowClone() for page models");
	}
	/**
	 * Добавить название переменной в список (для сохранения порядка следования переменных)
	 */
	public final void addVariablePE(VariablePE variablePE) {
		if (varPEDefs == null)
			varPEDefs = new LinkedHashMap<>(5);
		varPEDefs.put(variablePE.getName(), (RequestVariablePE) variablePE);
	}

	public String getKey() {
		return "Page '" + name + "'";
	}
	/**
	 * Получить список переменных, которые были перечислены в разделе <request> страницы
	 * @return
	 */
	public Collection<RequestVariablePE> getInitVariablesPEList() {
		if (varPEDefs != null)
			return varPEDefs.values();
		return new ArrayList<>(0);
	}
	/**
	 * Получить переменную страницы, которая была указана в списке <request>
	 * Если переменной не было, возвращается null
	 * @param name
	 * @return
	 */
	public RequestVariablePE getInitVariablePE(String name) {
		if (varPEDefs != null)
			return varPEDefs.get(name);
		return null;
	}
	/**
	 * Установить график автоматических запусков страницы
	 * @param schedule
	 */
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	/**
	 * Надо ли запускать страницу на автовыполнение согласно графику
	 * @return
	 */
	public boolean hasSchedule() {
		return !StringUtils.isBlank(schedule);
	}
	/**
	 * Получить график автоматических запусков страницы
	 * @return
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * Есть ли у страницы критический айтем
	 * @return
	 */
	public boolean hasCriticalItem() {
		return criticalItem != null;
	}

	/**
	 * Получить страничный ID критического айтема
	 * @return
	 */
	public String getCriticalItem() {
		return criticalItem;
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		// Есть ли название у страницы
		if (StringUtils.isBlank(name))
			results.addError(elementPath + " > " + getKey(), "page name is not set");
		// Проверить наличие групп пользователей
		for (String userGroup : userGroups) {
			if (!UserGroupRegistry.groupExists(userGroup))
				results.addError(elementPath + " > " + getKey(), "There is no usergroup '" + userGroup + "' in this site model");
		}
		return true;
	}

	public String getElementName() {
		return ELEMENT_NAME;
	}

}