package ecommander.pages;

import ecommander.controllers.SessionContext;
import ecommander.model.User;
import ecommander.pages.CommandPE.CommandContainer;
import ecommander.pages.var.*;

import java.util.*;

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
	private LinkedHashMap<String, Variable> variables;
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
			Collection<String> cacheVars, String criticalItem) {
		super(pageName, pageTemplateName, cacheable, cacheVars, criticalItem);
		this.identifiedElements = new HashMap<>();
		this.executables = new ArrayList<>();
		this.variables = new LinkedHashMap<>();
		this.sessionContext = sessionContext;
		User user;
		if (sessionContext != null)
			user = sessionContext.getUser();
		else
			user = User.getDefaultUser();
		new RequestVariablePE(NOW_VALUE, RequestVariablePE.Scope.request, VariablePE.Style.path, System.currentTimeMillis() + "")
				.createExecutableClone(this,this);
		new RequestVariablePE(USERNAME_VALUE, RequestVariablePE.Scope.request, VariablePE.Style.path, user.getName())
				.createExecutableClone(this,this);
		new RequestVariablePE(PAGENAME_VALUE, RequestVariablePE.Scope.request, VariablePE.Style.path, this.name)
				.createExecutableClone(this, this);
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
	public final Variable getVariable(String varName) {
		return variables.get(varName);
	}
	/**
	 * Вернуть все переменные страницы в правлиьном порядке
	 * @return
	 */
	public final Collection<Variable> getAllVariables() {
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
		HashSet<RequestVariablePE> initVars = new HashSet<>(getInitVariablesPEList());
		ArrayList<RequestVariablePE> negativeVars = new ArrayList<>(); // только нетагивные переменные
		for (VariablePE var : requestLink.getAllVariables()) {
			RequestVariablePE variable = (RequestVariablePE) var;
			if (!variable.isNegative()) {
				VariablePE initialVar = getInitVariablePE(variable.getName());
				if (initialVar != null && initialVar.getVariable() instanceof SessionStaticVariable) {
					((SessionStaticVariable) initialVar.getVariable()).update(variable.getVariable());
				}
				addVariable(variable.getVariable());
				initVars.remove(initialVar);
			} else {
				negativeVars.add(variable);
			}
		}
		// Добавить все отсутствующие в ссылке начальные переменные
		for (RequestVariablePE initialVar : initVars) {
			if (initialVar.getVariable() instanceof SessionStaticVariable) {
				((SessionStaticVariable) initialVar.getVariable()).restore();
				if (initialVar.isEmpty()) {
					if (initialVar.hasDefaultValue()) {
						StaticVariable valVar = new StaticVariable(initialVar.getName(), initialVar.getDefaultValue());
						((SessionStaticVariable) initialVar.getVariable()).update(valVar);
						addVariable(initialVar.getVariable());
					} else {
						addVariable(new StaticVariable(initialVar.getName()));
					}
				} else {
					addVariable(initialVar.getVariable());
				}
			} else {
				addVariable(new StaticVariable(initialVar.getName(), initialVar.getDefaultValue()));
			}
		}
		// Удалить значения, переданные в негативных переменных
		for (RequestVariablePE negVar : negativeVars) {
			VariablePE initialVar = getInitVariablePE(negVar.getPositiveName());
			if (initialVar != null && initialVar.getVariable() instanceof SessionStaticVariable) {
				for (Object val : negVar.getVariable().getAllValues()) {
					initialVar.getVariable().removeValue(val);
				}
			}
			Variable originalVar = getVariable(negVar.getPositiveName());
			if (originalVar != null) {
				for (Object value : negVar.getVariable().getAllValues()) {
					originalVar.removeValue(value);
				}
			}
		}
		// Добавить переменную - ссылку на эту страницу
		addVariablePE(new RequestVariablePE(PAGEURL_VALUE, linkUrl));
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
				result = exec.execute();
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
	public final void addVariable(Variable variable, boolean...isNegative) {
		// Заменить элемент в переменных страницы или добавить его
		variables.put(variable.getName(), variable);
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
			for(Variable pageVar : variables.values()) {
				if (pageVar != null && !pageVar.isEmpty() && !cacheVars.contains(pageVar.getName()))
					return false;
			}
		}
		return true;
	}

	@Override
	protected boolean validateShallow(String elementPath, ValidationResults results) {
		boolean success = super.validateShallow(elementPath, results);
		// Проверить наличие критического айтема
		if (hasCriticalItem()) {
			if (getItemPEById(criticalItem) == null) {
				results.addError(elementPath + " > " + getKey(),
						"There is no item '" + criticalItem + "' on this page which is marked as Critical Item");
			}
		}
		return success;
	}
}