package ecommander.pages;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import ecommander.pages.variables.StaticVariablePE;
import ecommander.pages.variables.VariablePE;
import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.PageNotFoundException;
import ecommander.fwk.UserNotAllowedException;
import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.mappers.SessionItemMapper;
import ecommander.model.User;
/**
 * Базовый класс для всех команд
 * @author EEEE
 *
 */
public abstract class Command implements AutoCloseable {
	
	private ExecutablePagePE page;
	private HashMap<String, ResultPE> results = null; // резльтаты выполнения команды
	private SynchronousTransaction transaction = null;
	private SessionItemMapper sessionMapper = null;
	private HashMap<String, HashSet<String>> required = null;

	void init(ExecutablePagePE page, HashMap<String, HashSet<String>> required) {
		this.page = page;
		this.required = required;
		transaction = new SynchronousTransaction(page.getSessionContext().getUser());
	}

	@Override
	public void close() throws Exception {
		if (transaction != null)
			transaction.finalize();
	}
	/**
	 * Установить значение переменной сеанса (получение значения переменной осуществляется в методах getVar*)
	 * @param name
	 * @param value
	 */
	protected final void setSessionVariable(String name, String value) {
		page.getSessionContext().setVariableValue(name, value);
	}
	/**
	 * Получить любой установленный ранее объект из сеанса
	 * @param name
	 * @return
	 */
	protected final Object getSessionObject(String name) {
		return page.getSessionContext().getObject(name);
	}
	
	protected final String getUrlBase() {
		return page.getUrlBase();
	}
	/**
	 * Установить значение переменной куки
	 * @param name
	 * @param value
	 */
	protected final void setCookieVariable(String name, String value) {
		page.getSessionContext().setCookie(name, value);
	}
	/**
	 * Установить страничную переменную
	 * Для того, чтобы удалить переменную, надо передать пустое значение
	 * @param name
	 * @param value
	 */
	protected final void setPageVariable(String name, String value) {
		if (StringUtils.isBlank(value)) {
			page.removeVariable(name);
		} else {
			VariablePE var = page.getVariable(name);
			if (var == null)
				page.addVariable(new StaticVariablePE(name, value));
			else
				((StaticVariablePE) var).addValue(value);
		}
	}
	/**
	 * Сохранить текущую форму в сеансе
	 */
	protected final void saveSessionForm() {
		if (page.getItemFrom() != null)
			page.getSessionContext().saveForm(page.getItemFrom());
	}
	/**
	 * Удалить текущую форму из сеанса
	 */
	protected final void removeSessionForm() {
		if (page.getItemFrom() != null)
			page.getSessionContext().removeForm(page.getItemFrom());
	}
	/**
	 * Получить другую страницу
	 * @param url
	 * @return
	 * @throws PageNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws UserNotAllowedException
	 */
	protected final ExecutablePagePE getExecutablePage(String url) throws PageNotFoundException, UnsupportedEncodingException,
			UserNotAllowedException {
		return PageModelRegistry.getRegistry().getExecutablePage(url, page.getUrlBase(), page.getSessionContext());
	}
	/**
	 * Получить маппер для сохранения и загрузки объектов сеанса
	 * Маппер кешируестя в этом методе как поле класса, поэтому нет смысла кешировать его отдельно в переменной
	 * @return
	 */
	protected final SessionItemMapper getSessionMapper() {
		if (sessionMapper == null)
			sessionMapper = SessionItemMapper.getMapper(page.getSessionContext());
		return sessionMapper;
	}
	/**
	 * Получить пользователя, который вызвал выполнение данной комманды
	 * @return
	 */
	protected final User getInitiator() {
		return page.getSessionContext().getUser();
	}
	/**
	 * Получить единственное значение переменной страницы
	 * @param varName
	 * @return
	 */
	protected final String getVarSingleValue(String varName) {
		return getVarSingleValueDefault(varName, null);
	}
	/**
	 * Получить единственное значение переменной страницы
	 * Если переменная не задана или ноа пуста - вернуть значение по умолчанию
	 * @param varName
	 * @param defaultValue
	 * @return
	 */
	protected final String getVarSingleValueDefault(String varName, String defaultValue) {
		VariablePE var = page.getVariable(varName);
		if (var == null || var.isEmpty())
			return defaultValue;
		return var.outputArray().get(0);
	}
	/**
	 * Вернуть несколько значений страничной переменной
	 * @param varName
	 * @return
	 */
	protected final List<String> getVarValues(String varName) {
		VariablePE var = page.getVariable(varName);
		if (var == null || var.isEmpty())
			return new ArrayList<String>();
		return var.outputArray();
	}
	/**
	 * Вернуть форму айтема
	 * @return
	 */
	protected final ItemHttpPostForm getItemForm() {
		return page.getItemFrom();
	}
	/**
	 * Возвращает список полей (параметров) формы, которые обязательны для заполнения, но не заполнены
	 * @param requiredName
	 * @return
	 */
	protected final Set<String> getFormUnsetRequired(String requiredName) {
		HashSet<String> reqParams = required.get(requiredName);
		if (reqParams == null || reqParams.size() == 0)
			return null;
		HashSet<String> unset = new HashSet<>();
		for (String paramName : reqParams) {
			if (!getItemForm().isParameterSet(paramName))
				unset.add(paramName);
		}
		if (unset.size() == 0)
			return null;
		return unset;
	}
	/**
	 * Создать временный айтем для сохранения в сеансе и для временного использования на базе
	 * отправленной пользователем формы
	 * @return
	 * @throws Exception
	 */
	protected final Item createTemporaryFormItem() throws Exception {
		return getItemForm().createItem(getInitiator().getUserId(), getInitiator().getGroupId());
	}
	/**
	 * Вернуть переменные айтемов (объект ItemVariablesContainer)
	 */
	protected final ItemVariablesContainer getItemVariables() {
		return page.getItemVariables();
	}
	/**
	 * Вернуть все айтемы, загруженные заданным страничным айтемом (с заданным ID)
	 * Айтемы возвращаются в виде упорядоченного отображения (в порядке сортировки)
	 * ID => айтем
	 * @param itemPageId
	 * @return
	 */
	protected final LinkedHashMap<Long, Item> getLoadedItems(String itemPageId) {
		LinkedHashMap<Long, Item> items = new LinkedHashMap<>();
		ExecutableItemPE.AllFoundIterator iter = page.getItemPEById(itemPageId).getAllFoundItemIterator();
		while (iter.next()) {
			items.put(iter.getCurrentItem().getId(), iter.getCurrentItem());
		}
		return items;
	}
	/**
	 * Вернуть единственный найденный айтем страничного айтема
	 * @param itemPageId
	 * @return
	 */
	protected final Item getSingleLoadedItem(String itemPageId) {
		return page.getItemPEById(itemPageId).getSingleFoundItem();
	}
	/**
	 * Добавить команду для выполнения синхронно (результаты после вызова метода выполнения 
	 * синхронных команд executeSynchronousCommandUnits можно использовать в этой команде)
	 * @param commandUnit
	 * @throws Exception 
	 */
	protected final void executeCommandUnit(PersistenceCommandUnit commandUnit) throws Exception {
		transaction.executeCommandUnit(commandUnit);
	}
	/**
	 * Закомитить команды. Дает возможность использовать результаты выполнения этих команд другим подключениям
	 * @throws Exception
	 */
	protected final void commitCommandUnits() throws Exception {
		transaction.commit();
	}
	/**
	 * Откатить изменения команд
	 * @throws Exception 
	 */
	protected final void rollbackCommandUnits() throws Exception {
		transaction.rollback();
	}
	/**
	 * Добавить и сразу выполнить команду или несколько команд
	 * @param commandUnit
	 * @throws Exception
	 */
	protected final void executeAndCommitCommandUnits(PersistenceCommandUnit... commandUnit) throws Exception {
		for (PersistenceCommandUnit unit : commandUnit) {
			transaction.executeCommandUnit(unit);
		}
		transaction.commit();
	}
	/**
	 * Добавить результат
	 * @param result
	 */
	public final void addResult(ResultPE result) {
		if (results == null) {
			results = new HashMap<String, ResultPE>();
		}
		results.put(result.getName(), result);
	}
	/**
	 * Закончить сеанс пользователя
	 */
	protected final void endUserSession() {
		page.getSessionContext().userExit();
	}
	/**
	 * ачать сеанс для определенного пользователя
	 * @param user
	 */
	protected final void startUserSession(User user) {
		page.getSessionContext().setUser(user);
	}
	
	public final boolean hasResults() {
		return results != null;
	}
	/**
	 * Действия, которые выполняются командой.
	 * Возвращает результат - название результата.
	 * Если переход осуществлять не надо, возвращается NULL.
	 * @throws Exception
	 */
	public abstract ResultPE execute() throws Exception;
	/**
	 * Найти результат по его названию для последующего возвращения при выполении команды
	 * Если результат не найден, возвращается резлуьтат по умолчанию, т. е. 
	 * ссылка прямого перехода (redirect) название которой на странице равно названию
	 * запрашиваемого результата
	 * @param name
	 * @return
	 * @throws EcommanderException
	 */
	protected final ResultPE getResult(String name) throws EcommanderException {
		ResultPE result = results.get(name);
		if (result == null)
			return new ResultPE(name, ResultPE.ResultType.redirect);
		return result;
	}
	/**
	 * Результат, значением которого является готовая ссылка для перехода на страницу
	 * @param url
	 * @param type
	 * @return
	 */
	protected final ResultPE getResultingUrl(String url, ResultPE.ResultType type) {
		return new ResultPE("direct_url", type.name()).setValue(url);
	}
	/**
	 * Получить все рузльтаты, объявленные в команде
	 * @return
	 */
	protected final Collection<ResultPE> getAllResults() {
		return results.values();
	}
	/**
	 * Найти результат по его названию для последующего возвращения при выполении команды
	 * При этом все выполненные (и те, которые будут выполнены) команды текущей страницы отменяются
	 * Если результат не найден, выбрасывается исключение
	 * @param name
	 * @return
	 * @throws EcommanderException
	 */
	protected final ResultPE getRollbackResult(String name) throws EcommanderException {
		ResultPE result = getResult(name);
		result.rollback();
		return result;
	}
}
