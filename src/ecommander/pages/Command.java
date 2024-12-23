package ecommander.pages;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.PageNotFoundException;
import ecommander.fwk.UserNotAllowedException;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.pages.var.CookieStaticVariable;
import ecommander.pages.var.StaticVariable;
import ecommander.pages.var.Variable;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.mappers.SessionItemMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.*;
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


	public Command() {

	}


	public Command(Command outer) {
		this.page = outer.page;
		this.transaction = outer.transaction;
		this.sessionMapper = outer.sessionMapper;
	}


	void init(ExecutablePagePE page) {
		this.page = page;
		transaction = new SynchronousTransaction(page.getSessionContext().getUser());
	}

	@Override
	public void close() throws Exception {
		if (transaction != null)
			transaction.close();
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

	protected final String getPageName() {
		return page.getPageName();
	}
	/**
	 * Установить значение переменной куки
	 * @param name
	 * @param values
	 */
	protected final void setCookieVariable(String name, Object...values) {
		CookieStaticVariable cookie = new CookieStaticVariable(page, name);
		StaticVariable staticVar = new StaticVariable(name, values);
		cookie.update(staticVar);
	}

	/**
	 * Получить значение переменной куки
	 * Переменная не обязательно должна присутствовать на странице.
	 * Если переменная куки есть на странице, то предпочтительно использовать метод
	 * getVarValues или getVarSingleValue или другие подобные
	 * @param name
	 * @return
	 */
	protected final ArrayList<Object> getCookieVarValues(String name) {
		CookieStaticVariable cookie = new CookieStaticVariable(page, name);
		cookie.restore();
		return cookie.getAllValues();
	}

	/**
	 * Получить значение переменной куки
	 * Переменная не обязательно должна присутствовать на странице.
	 * Если переменная куки есть на странице, то предпочтительно использовать метод
	 * getVarValues или getVarSingleValue или другие подобные
	 * Значение возвращается в виде одной строки (если фактических значений несколько, то
	 * они возвращаются через разделитель, в виде, как они хранятся у пользтвателя)
	 * @param name
	 * @return
	 */
	protected final String getCookieVarPlainValue(String name) {
		CookieStaticVariable cookie = new CookieStaticVariable(page, name);
		return cookie.getCookiePlain();
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
			Variable var = page.getVariable(name);
			if (var == null)
				page.addVariable(new StaticVariable(name, value));
			else
				((StaticVariable) var).addValue(value);
		}
	}
	/**
	 * Сохранить текущую форму в сеансе
	 * @param formName - произвольное название формы, нужно для того, чтобы потом правильно восстановить из сеанса
	 */
	protected final void saveSessionForm(String formName) {
		if (page.getItemFrom() != null)
			page.getSessionContext().saveForm(page.getItemFrom(), formName);
	}
	/**
	 * Удалить текущую форму из сеанса
	 * @param formName
	 */
	protected final void removeSessionForm(String formName) {
		if (page.getItemFrom() != null)
			page.getSessionContext().removeForm(formName);
	}

	/**
	 * Получить сохраненную в сеансе форму
	 * @param formName
	 * @return
	 */
	protected final MultipleHttpPostForm getSessionForm(String formName) {
		return page.getSessionContext().getForm(formName);
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
		Variable var = page.getVariable(varName);
		if (var == null || var.isEmpty())
			return defaultValue;
		return var.writeSingleValue();
	}
	/**
	 * Вернуть несколько значений страничной переменной
	 * @param varName
	 * @return
	 */
	protected final List<Object> getVarValues(String varName) {
		Variable var = page.getVariable(varName);
		if (var == null || var.isEmpty())
			return new ArrayList<>();
		return var.getAllValues();
	}
	/**
	 * Вернуть форму айтема
	 * @return
	 */
	protected final MultipleHttpPostForm getItemForm() {
		return page.getItemFrom();
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
	 * Вернуть все айтемы, загруженные заданным страничным айтемом (с заданным ID)
	 * Айтемы возвращаются в виде упорядоченного отображения (в порядке сортировки)
	 * ID => айтем
	 * Возвращаются только айтемы, вложенные в заданный родительский айтем
	 * @param itemPageId
	 * @param parentId
	 * @return
	 */
	protected final LinkedHashMap<Long, Item> getLoadedChildItems(String itemPageId, Long parentId) {
		LinkedHashMap<Long, Item> items = new LinkedHashMap<>();
		ArrayList<Item> children = page.getItemPEById(itemPageId).getFoundItemsByParent(parentId);
		for (Item child : children) {
			items.put(child.getId(), child);
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
	 * Получить текущее подключение
	 * Его можно корректно получить, т.к. транзакция синхронная, т.е. можно выполнять команды прямо в потоке
	 * выполнения этой транзакции, в том числе и внешние команды, которые не требуют записи в БД, но участвуют
	 * в транзакции (операции чтения вновь измененных значений)
	 * @return
	 * @throws Exception
	 */
	protected final Connection getDBConnection() throws Exception {
		return transaction.getConn();
	}
	/**
	 * Добавить результат
	 * @param result
	 */
	public final void addResult(ResultPE result) {
		if (results == null) {
			results = new HashMap<>();
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
		transaction.switchInitiator(user);
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
	 * Проверка, есть ли результат с заданным именем на странице
	 * @param name
	 * @return
	 */
	protected final boolean hasResult(String name) {
		return results.containsKey(name);
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

	protected final boolean hasCriticalItem() {
		ExecutableItemPE pageItem = page.getItemPEById(page.getCriticalItem());
		if (!pageItem.hasFoundItems() && !pageItem.isLoadedFromCache()) {
			return false;
		}
		return true;
	}
}
