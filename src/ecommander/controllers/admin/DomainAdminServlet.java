package ecommander.controllers.admin;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.controllers.StartController;
import ecommander.persistence.DelayedTransaction;
import ecommander.persistence.commandunits.DeleteDomainDBUnit;
import ecommander.persistence.commandunits.SaveNewDomainDBUnit;
import ecommander.persistence.commandunits.UpdateDomainDBUnit;
import ecommander.users.UserGroupRegistry;
import ecommander.view.domain.Domain;
import ecommander.view.domain.DomainRegistry;

/**
 * Контроллер Struts 2 для управления пользователями
 * @author EEEE
 *
 */
public class DomainAdminServlet extends BasicAdminServlet {
	
	private static final long serialVersionUID = -5179113852523319232L;
	/**
	 * Экшены
	 */
	public static final String DOMAINS_INITIALIZE_ACTION = "admin_domains_initialize";
	public static final String CREATE_DOMAIN_ACTION = "admin_create_domain";
	public static final String DELETE_DOMAIN_ACTION = "admin_delete_domain";
	public static final String SET_DOMAIN_ACTION = "admin_set_domain";
	public static final String UPDATE_DOMAIN_ACTION = "admin_update_domain";
	public static final String ADD_VALUE_ACTION = "admin_add_domain_value";
	public static final String DELETE_VALUE_ACTION = "admin_delete_domain_value";
	/**
	 * Инпуты
	 */
	public static final String NAME_INPUT = "name";
	public static final String VIEW_TYPE_INPUT = "viewType";
	public static final String FORMAT_INPUT = "format";
	public static final String VALUE_INPUT = "value";
	public static final String CURRENT_NAME_INPUT = "currentName";
	/**
	 * Переходы
	 */
	private static final String DOMAINS = "/admin/admin_domains.jsp";
	
	private String name;
	private String viewType;
	private String format;
	private String value;
	private String currentName;

	@Override
	protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String result = Strings.EMPTY;
		if (!checkUser(req, resp, DOMAINS_INITIALIZE_ACTION + ".user")) return;
		start(req);
		String actionName = getAction(req);
		if (actionName.equalsIgnoreCase(DOMAINS_INITIALIZE_ACTION))
			result = initialize(req);
		else if (actionName.equalsIgnoreCase(CREATE_DOMAIN_ACTION))
			result = addDomain(req);
		else if (actionName.equalsIgnoreCase(DELETE_DOMAIN_ACTION))
			result = removeDomain(req);
		else if (actionName.equalsIgnoreCase(SET_DOMAIN_ACTION))
			result = setDomain(req);
		else if (actionName.equalsIgnoreCase(UPDATE_DOMAIN_ACTION))
			result = updateDomain(req);
		else if (actionName.equalsIgnoreCase(ADD_VALUE_ACTION))
			result = addValue(req);
		else if (actionName.equalsIgnoreCase(DELETE_VALUE_ACTION))
			result = deleteValue(req);
		// Форвард
		req.setAttribute("data", this);
		forward(req, resp, result);
	}
	/**
	 * Действия, необходимые для инициализации
	 * @throws Exception
	 */
	protected void start(HttpServletRequest req) throws Exception {
		name = Strings.EMPTY;
		viewType = Strings.EMPTY;
		format = Strings.EMPTY;
		value = Strings.EMPTY;
		currentName = Strings.EMPTY;
		// Старт приложения, если он еще не был осуществлен
		StartController.start(getServletContext());
		name = req.getParameter(NAME_INPUT);
		viewType = req.getParameter(VIEW_TYPE_INPUT);
		format = req.getParameter(FORMAT_INPUT);
		value = req.getParameter(VALUE_INPUT);
		currentName = req.getParameter(CURRENT_NAME_INPUT);
	}
	/**
	 * Начало работы с юзерами - загрузка списка всех юзеров
	 * @return
	 */
	protected String initialize(HttpServletRequest req) {
		name = Strings.EMPTY;
		value = Strings.EMPTY;
		currentName = Strings.EMPTY;
		req.setAttribute("message", "Можете создавать новые домены");
		return DOMAINS;
	}
	/**
	 * Выбран один из доменов для редактирования
	 * @return
	 */
	protected String setDomain(HttpServletRequest req) {
		currentName = name;
		req.setAttribute("message", "Редактирование домена");
		return DOMAINS;
	}
	/**
	 * Добавление значения к текущему домену
	 * @throws Exception 
	 */
	protected String addValue(HttpServletRequest req) throws Exception {
		if (StringUtils.isBlank(value)) {
			req.setAttribute("message", "Задайте, пожалуйста, добавляемое значение");
			return DOMAINS;
		}
		Domain domain = DomainRegistry.getDomain(currentName);
		if (!domain.addValue(value)) {
			req.setAttribute("message", "Задайте, пожалуйста, корректное добавляемое значение");
			return DOMAINS;
		}
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		transaction.addCommandUnit(new UpdateDomainDBUnit(domain));
		transaction.execute();
		req.setAttribute("message", "Добавлено значение домена.");
		return DOMAINS;
	}
	/**
	 * Удаление значения из домена
	 * @throws Exception 
	 */
	protected String deleteValue(HttpServletRequest req) throws Exception {
		if (StringUtils.isBlank(value)) {
			req.setAttribute("message", "Задайте, пожалуйста, удаляемое значение");
			return DOMAINS;
		}
		Domain domain = DomainRegistry.getDomain(currentName);
		domain.removeValue(value);
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		transaction.addCommandUnit(new UpdateDomainDBUnit(domain));
		transaction.execute();
		req.setAttribute("message", "Удалено значение домена.");
		return DOMAINS;
	}
	/**
	 * Создание нового домена
	 * @throws Exception 
	 */
	protected String addDomain(HttpServletRequest req) throws Exception {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(viewType)) {
			req.setAttribute("message", "Задайте, пожалуйста, название и тип домена");
			return DOMAINS;
		}
		Domain domain = new Domain(name, viewType, format);
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		transaction.addCommandUnit(new SaveNewDomainDBUnit(domain));
		transaction.execute();
		DomainRegistry.addDomain(domain);
		currentName = name;
		req.setAttribute("message", "Домен создан и доступен для редактирования.");
		return DOMAINS;
	}
	/**
	 * Удаление домена
	 * @throws Exception 
	 */
	protected String removeDomain(HttpServletRequest req) throws Exception {
		if (StringUtils.isBlank(name)) {
			req.setAttribute("message", "Задайте, пожалуйста, название домена");
			return DOMAINS;
		}
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		transaction.addCommandUnit(new DeleteDomainDBUnit(name));
		transaction.execute();
		DomainRegistry.removeDomain(name);
		if (currentName != null && name.compareToIgnoreCase(currentName) == 0)
			currentName = null;
		req.setAttribute("message", "Домен удален");
		return DOMAINS;
	}
	/**
	 * Сохранение
	 * @throws Exception 
	 */
	protected String updateDomain(HttpServletRequest req) throws Exception {
		if (StringUtils.isBlank(name) || StringUtils.isBlank(viewType)) {
			req.setAttribute("message", "Задайте, пожалуйста, название и тип домена");
			return DOMAINS;
		}
		Domain domainToDelete = DomainRegistry.getDomain(currentName);
		Domain newDomain = new Domain(name, viewType, format);
		newDomain.setValues(domainToDelete.getValues());
		DelayedTransaction transaction = new DelayedTransaction(getCurrentAdmin());
		transaction.addCommandUnit(new DeleteDomainDBUnit(currentName));
		transaction.addCommandUnit(new SaveNewDomainDBUnit(newDomain));
		transaction.execute();
		DomainRegistry.removeDomain(currentName);
		DomainRegistry.addDomain(newDomain);
		currentName = name;
		req.setAttribute("message", "Параметры домена успешно сохранены");
		return DOMAINS;
	}

	public boolean hasDomainSet() {
		return !StringUtils.isBlank(currentName);
	}
	
	public Collection<String> getGroupNames() {
		return UserGroupRegistry.getGroupNames();
	}

	public DomainAdminServlet getSelf() {
		return this;
	}
	
	public String getCurrentName() {
		return currentName;
	}
	
}
