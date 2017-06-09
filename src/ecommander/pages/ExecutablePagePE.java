package ecommander.pages;

import ecommander.controllers.SessionContext;
import ecommander.model.User;
import ecommander.pages.CommandPE.CommandContainer;
import ecommander.pages.variables.SessionStaticVariablePE;
import ecommander.pages.variables.StaticVariablePE;
import ecommander.pages.var.VariablePE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Модель страницы, которая предназначена для загрузки.
 * для каждого запроса со стороны клиента создается экземпляр ExecutablePageModel
 * @author EEEE
 *
 */
public class ExecutablePagePE extends PagePE implements ExecutableItemContainer, LinkPE.LinkContainer, CommandContainer, ExecutablePE {
	
	/**
	 * Предопределенные переменные страницы
	 */
	public static final String NOW_VALUE = "$now"; // текущая дата на сервере
	public static final String USERNAME_VALUE = "$username"; // текущий ползователь
	public static final String PAGENAME_VALUE = "$pagename"; // название текущей страницы
	public static final String PAGEURL_VALUE = "$pageurl"; // URL текущей страницы со всеми переменными
	
	private HashMap<String, ExecutableItemPE> identifiedElements = null; // отображение ID-название айтема => PageItem
	// сабайтемы вместе с командами (одни айтемы могут загружаться перед командами, другие - после)
	private ArrayList<ExecutablePE> executables;
	// Переменные, переданные при вызове страницы (взяты из Link или из форм)
	private LinkedHashMap<String, VariablePE> variables;
	// Контекст сеанса
	private SessionContext sessionContext;
	// Ссылки для перехода в результате выполнения команд
	private HashMap<String, LinkPE> identifiedLinks;
	// Ссылка, которая ведет на данную страницу (по которой эта страницы получена)
	private LinkPE requestLink;
	// Базовая ссылка, все урлы страницы должны быть относительны этой ссылке
	private String urlBase;
	// Форма, которая пришла в результате POST запроса (форма на базе одного айтема)
	private MultipleHttpPostForm itemForm;
	// Нужно ли очищать кеш после выполнения всех команд этой страницы
	private boolean cacheClearNeeded = false;
	
	ExecutablePagePE(String pageName, String pageTemplateName, boolean cacheable, SessionContext sessionContext,
			Collection<String> cacheVars) {
		super(pageName, pageTemplateName, cacheable, cacheVars);
		this.identifiedElements = new HashMap<>();
		this.executables = new ArrayList<>();
		this.variables = new LinkedHashMap<>();
		this.sessionContext = sessionContext;
		User user;
		if (sessionContext != null)
			user = sessionContext.getUser();
		else
			user = User.getDefaultUser();
		addVariable(new StaticVariablePE(NOW_VALUE, System.currentTimeMillis() + ""));
		addVariable(new StaticVariablePE(USERNAME_VALUE, user.getName()));
		addVariable(new StaticVariablePE(PAGENAME_VALUE, this.name));
	}
	/**
	 * Получить ранее зарегистрированный элемент
	 * @param elementId
	 * @return
	 */
	public final ExecutableItemPE getItemPEById(String elementId) {
		return identifiedElements.get(elementId);
	}
	/**
	 * Получить контекст сеанса
	 * @return
	 */
	public final SessionContext getSessionContext() {
		return sessionContext;
	}
	/**
	 * Зарегисрировать страничный айтем
	 * @param registrableElement
	 */
	final void registerItemPE(ExecutableItemPE registrableElement) {
		if (registrableElement.hasId())
			identifiedElements.put(registrableElement.getId(), registrableElement);
	}
	/**
	 * Получить страничную переменную
	 * @param varName
	 * @return
	 */
	public final VariablePE getVariable(String varName) {
		return variables.get(varName);
	}
	/**
	 * Вернуть все переменные страницы в правлиьном порядке
	 * @return
	 */
	public final Collection<VariablePE> getAllVariables() {
		return variables.values();
	}
	/**
	 * Установить значение переменной.
	 * Чаще всего это значение было передано странице через URL
	 * @param link
	 * @param linkUrl - передается для того, чтобы не вызывать лишний раз serialize в ссылке
	 * @param baseLink
	 */
	final void setRequestLink(LinkPE link, String linkUrl, String baseLink) {
		this.requestLink = link;
		this.urlBase = baseLink;
		for (VariablePE variable : requestLink.getAllVariables()) {
			if (variables.containsKey(variable.getName()) && variables.get(variable.getName()) instanceof SessionStaticVariablePE)
				((SessionStaticVariablePE) variables.get(variable.getName())).update(variable);
			else {
				addVariable(variable);
			}
		}
		addVariable(new StaticVariablePE(PAGEURL_VALUE, linkUrl));
	}
	/**
	 * Установить формы, переданные через POST - переменные разный айтемов и форму на базе определенного типа айтема
	 * @param itemForm
	 */
	public void setPostData(MultipleHttpPostForm itemForm) {
		this.itemForm = itemForm;
	}
	
	public final MultipleHttpPostForm getItemFrom() {
		return itemForm;
	}
	/**
	 * Получить ссылку, по которому была получена данная страница
	 * @return
	 */
	public final LinkPE getRequestLink() {
		return requestLink;
	}
	/**
	 * Вернуть базу всех урлов страницы
	 * @return
	 */
	public final String getUrlBase() {
		return urlBase;
	}
	
	/**
	 * Загружает информацию страницы.
	 * Запускает на выполнение все команды, которые есть в странице
	 * Если переход не требуется, возвращается NULL
	 * Если переход требуется, то возвращается ссылка, на которую необходимо произвести переход
	 * Ссылка находится либо в виде ссылки либо в виде переменной
	 * @throws Exception 
	 */
	public final ResultPE execute() throws Exception {
		ResultPE result = null;
		try {
			for (ExecutablePE exec : executables) {
				ResultPE innerResult = exec.execute();
				if (innerResult != null)
					result = innerResult;
			}
		} finally {
			sessionContext.close();
		}
		return result;
	}

	public final void addExecutableItem(ExecutableItemPE itemPE) {
		registerItemPE(itemPE);
		executables.add(itemPE);
	}
	/**
	 * Не использовать напрямую.
	 * Вызывается автоматически при клонировании (поддержка интерфейса)
	 */
	@Override
	public final void addVariable(VariablePE variablePE) {
		// Заменить элемент в переменных страницы и добавить его
		variables.put(variablePE.getName(), variablePE);
	}
	/**
	 * Удаляет переменную из страницы
	 * @param varName
	 */
	final void removeVariable(String varName) {
		variables.remove(varName);
	}
	/**
	 * Надо ли очищать кеш после выполнения команд этой страницы
	 * @return
	 */
	public final boolean isCacheClearNeeded() {
		return cacheClearNeeded;
	}
	
	public final void addLink(LinkPE linkPE) {
		if (identifiedLinks == null)
			identifiedLinks = new HashMap<>();
		identifiedLinks.put(linkPE.getLinkName(), linkPE);
	}

	public final LinkPE getLink(String linkName) {
		if (identifiedLinks == null)
			return null;
		return identifiedLinks.get(linkName);
	}
	
	public final void addCommand(CommandPE commandPE) {
		executables.add(commandPE);
		cacheClearNeeded |= commandPE.isCacheClearNeeded();
	}
	
	@Override
	public final boolean isCacheable() {
		if (!cacheable)
			return false;
		if (cacheVars != null) {
			for(VariablePE pageVar : variables.values()) {
				if (pageVar != null && !pageVar.isEmpty() && !cacheVars.contains(pageVar.getName()))
					return false;
			}
		}
		return true;
	}
	
	

}