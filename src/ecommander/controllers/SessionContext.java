package ecommander.controllers;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.User;
import ecommander.pages.MultipleHttpPostForm;
import ecommander.persistence.mappers.SessionItemMapper;
import ecommander.persistence.mappers.SessionObjectStorage;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map.Entry;
/**
 * Интерфейс для работы со всем, что касается сеансов, а именно:
 * - установка текущего пользователя (после успешной аутентификации)
 * - получение текущего пользователя
 * - удаление текущего пользователя (пользователь явно уходит с сайта)
 * - Получение интерфейса сеансового хранилица (для сохранения временных объектов)
 * - установка или удаление значения переменной страницы
 * - получение значения переменной страницы
 * 
 * Также этот класс позволяет работать с cookie аналогично сеансовым переменным
 *
 * TODO хранить счетчик объектов в сеансе, чтобы после удаления последнего объекта можно было бы завершать сеанс
 *
 * @author EEEE
 *
 */
public class SessionContext implements AutoCloseable {

	private static final String STORAGE_SESSION_NAME = "session_storage";
	private static final String USER_SESSION_NAME = "session_user";
	private static final String VARIABLE_SESSION_NAME_PREFIX = "var_";
	private static final String PROGRESS_SESSION_NAME_PREFIX = "progress_";
	private static final String FORM_SESSION_NAME_PREFIX = "form_";
	private static final String CONTENT_UPDATE_VAR_NAME = "adm$content$update";

	private static final String SESSION_OBJECT_COUNT = "$object_count$";

	private static final int COOKIE_EXPIRE = 10 * 24 * 60 * 60;
	private static final long INITIAL_GENERATED_ID = -100L;

	@Override
	public void close() throws Exception {
		if (hasSession()) {
			storage = (SessionObjectStorage) forceGetSession().getAttribute(STORAGE_SESSION_NAME);
			if (storage != null && storage.isEmpty())
				removeSessionObject(STORAGE_SESSION_NAME);
		}
	}

	public static class Progress implements Serializable {
		private static final long serialVersionUID = -8802030992284237402L;

		public final double percent;
		public final double size;
		public final double total;
		public final String unit;
		public final String message;

		private Progress(double percent, double size, double total, String unit, String message) {
			super();
			this.percent = percent;
			this.size = size;
			this.total = total;
			this.unit = unit;
			this.message = message;
		}
	}
	
	private HttpServletRequest request;
	private SessionObjectStorage storage = null;
	private User user = null;
	private HashMap<String, String> cookies = null;
	// Генератор ID для новых айтемов. Предполагается, что при повторной загрузке одной и той же страницы
	// сгенерируются одни и те же ID (это нужно для восстановления ранее сохраненных введеннй пользователем значений полей)
	private long _id_generator = INITIAL_GENERATED_ID;

	private SessionContext(HttpServletRequest request) {
		this.request = request;
	}

	public static SessionContext createSessionContext(HttpServletRequest request) {
		return new SessionContext(request);
	}

	/**
	 * Создать контекст сеанса только с одним установленным пользователем
	 * (без фактического сеанса, из всех данных только пользователь)
	 * @param user
	 * @return
	 */
	public static SessionContext userOnlySessionContext(User user) {
		SessionContext context = new SessionContext(null);
		context.user = user;
		return context;
	}

	/**
	 * Добавить занчение в сеанс и увеличить счетчик, если раньше в сеансе не было значения с таким именем
	 * @param name
	 * @param object
	 */
	private void setSessionObject(String name, Object object) {
		if (object == null) {
			removeSessionObject(name);
		} else {
			HttpSession session = forceGetSession();
			int objectCount = (Integer) ObjectUtils.defaultIfNull(session.getAttribute(SESSION_OBJECT_COUNT), 0);
			Object oldValue = session.getAttribute(name);
			session.setAttribute(name, object);
			if (oldValue == null) {
				objectCount++;
				session.setAttribute(SESSION_OBJECT_COUNT, objectCount);
			}
		}
	}

	/**
	 * Удалить значение из сеанса и уменьшить счетчик.
	 * Если счетчик достиг 0, завершить сеанс
	 * @param name
	 */
	private void removeSessionObject(String name) {
		HttpSession session = forceGetSession();
		int objectCount = (Integer) ObjectUtils.defaultIfNull(session.getAttribute(SESSION_OBJECT_COUNT), 0);
		Object oldValue = session.getAttribute(name);
		session.removeAttribute(name);
		if (oldValue != null) {
			objectCount--;
		}
		if (objectCount <= 0)
			session.invalidate();
	}
	/**
	 * Получить сеансовое хранилище
	 * @param create - надо ли создавать новый сеанс, в случае если сеанс еще не создан
	 * @return
	 */
	public final SessionObjectStorage getStorage(boolean create) {
		if (storage == null) {
			if (hasSession() || create) {
				storage = (SessionObjectStorage) forceGetSession().getAttribute(STORAGE_SESSION_NAME);
				if (storage == null) {
					storage = SessionItemMapper.createSessionStorage();
					setSessionObject(STORAGE_SESSION_NAME, storage);
				}
			} else {
				return SessionItemMapper.createSessionStorage();
			}
		}
		return storage;
	}
	
	public void setUser(User user) {
		this.user = user;
		setSessionObject(USER_SESSION_NAME, user);
	}

	public void userExit() {
		forceGetSession().invalidate();
		user = User.getDefaultUser();
	}

	public User getUser() {
		if (user == null) {
			if (hasSession())
				user = (User)forceGetSession().getAttribute(USER_SESSION_NAME);
			if (user == null) {
				user = User.getDefaultUser();
				//session.setAttribute(USER_SESSION_NAME, user);
			}
		}
		return user;
	}
	/**
	 * Установить или удалить значение переменной (значение переменной - строка)
	 * @param varName
	 * @param varValue
	 */
	public void setVariableValue(String varName, String varValue) {
		if (!StringUtils.isBlank(varName) && !StringUtils.isBlank(varValue))
			setSessionObject(VARIABLE_SESSION_NAME_PREFIX + varName, varValue);
		else if (!StringUtils.isBlank(varName))
			removeSessionObject(VARIABLE_SESSION_NAME_PREFIX + varName);
	}
	/**
	 * Вернуть значение переменной страницы.
	 * Если сеанс не существует (не начат), то он не создается, а значение переменной возвращается равной null
	 * @param varName
	 * @return
	 */
	public String getVariableValue(String varName) {
		if (hasSession())
			return (String) forceGetSession().getAttribute(VARIABLE_SESSION_NAME_PREFIX + varName);
		return null;
	}
	/**
	 * Установить или удалить значение переменной (значение переменной - объект)
	 * @param varName
	 * @param varValue
	 */
	public void setVariableObject(String varName, Object varValue) {
		if (!StringUtils.isBlank(varName) && varValue != null)
			setSessionObject(VARIABLE_SESSION_NAME_PREFIX + varName, varValue);
		else
			removeSessionObject(VARIABLE_SESSION_NAME_PREFIX + varName);
	}
	/**
	 * Вернуть значение переменной-объекта.
	 * Если сеанс не существует (не начат), то он не создается, а значение переменной возвращается равной null
	 * @param varName
	 * @return
	 */
	public Object getVariableObject(String varName) {
		if (hasSession())
			return forceGetSession().getAttribute(VARIABLE_SESSION_NAME_PREFIX + varName);
		return null;
	}
	/**
	 * Установить текущий прогресс по длительной операции
	 * @param progressName
	 * @param size
	 * @param total
	 * @param percent
	 * @param unit
	 * @param message
	 */
	public void setProgress(String progressName, double size, double total, double percent, String unit, String message) {
		if (hasSession())
			setSessionObject(PROGRESS_SESSION_NAME_PREFIX + progressName, new Progress(percent, size, total, unit, message));
	}
	/**
	 * Получить текущий прогресс по длительной операции с заданным именем
	 * @param progressName
	 * @return
	 */
	public Progress getProgress(String progressName) {
		if (hasSession())
			return (Progress) forceGetSession().getAttribute(PROGRESS_SESSION_NAME_PREFIX + progressName);
		return null;
	}

	/**
	 * Удалить прогресс из сеанса
	 * @param progressName
	 */
	public void removeProgress(String progressName) {
		removeSessionObject(PROGRESS_SESSION_NAME_PREFIX + progressName);
	}
	/**
	 * Вернуть любой объект (ранее установленный) из сеанса
	 * @param name
	 * @return
	 */
	public Object getObject(String name) {
		if (hasSession())
			return forceGetSession().getAttribute(name);
		return null;
	}
	/**
	 * Установить куки
	 * @param name
	 * @param value
	 */
	public void setCookie(String name, String value) {
		if (cookies == null)
			cookies = new HashMap<>();
		cookies.put(name, value);
	}
	/**
	 * Вернуть куки, переданный с запросом.
	 * Если куки не найден, возвращается null
	 * Если куки с искомым именем был установлен ранее при выполнении команды, то возвращается он,
	 * а куки из request игнорируется
	 * @param name
	 * @return
	 */
	public String getCookie(String name) {
		if (request == null)
			return null;
		if (cookies != null && cookies.containsKey(name))
			return cookies.get(name);
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
			return null;
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name))
				try {
					return URLDecoder.decode(cookie.getValue(), Strings.SYSTEM_ENCODING);
				} catch (UnsupportedEncodingException e) {
					ServerLogger.error("Unable to decode cookie", e);
					return null;
				}
		}
		return null;
	}
	/**
	 * Сохранить форму ввода пользователя в сеансе (например, если возникли ошибки валидации формы)
	 * Вызывается не автоматически, и только в процессе выполнения команд по требованию
	 * @param form
	 * @param formId
	 */
	public void saveForm(MultipleHttpPostForm form, String formId) {
		setSessionObject(FORM_SESSION_NAME_PREFIX + formId, form);
	}
	/**
	 * Удалить форму из сеанса
	 * @param formId
	 */
	public void removeForm(String formId) {
		if (hasSession())
			removeSessionObject(FORM_SESSION_NAME_PREFIX + formId);
	}
	/**
	 * Получить из сеанса ранее сохраненную форму
	 * Если форма сохранена не была, возвращается null
	 * @param formId
	 * @return
	 */
	public MultipleHttpPostForm getForm(String formId) {
		if (hasSession())
			return (MultipleHttpPostForm) forceGetSession().getAttribute(FORM_SESSION_NAME_PREFIX + formId);
		return null;
	}
	/**
	 * Записать все установленные куки в ответ сервера
	 * @param resp
	 */
	void flushCookies(HttpServletResponse resp) {
		if (cookies != null) {
			for (Entry<String, String> vals : cookies.entrySet()) {
				try {
					Cookie cookie;
					if (StringUtils.isBlank(vals.getValue())) {
						cookie = new Cookie(vals.getKey(), "");
						cookie.setMaxAge(0); // удаление куки
					} else {
						cookie = new Cookie(vals.getKey(), URLEncoder.encode(vals.getValue(), Strings.SYSTEM_ENCODING));
						cookie.setMaxAge(COOKIE_EXPIRE);
					}
					cookie.setPath("/");
					resp.addCookie(cookie);
				} catch (Exception e) {
					ServerLogger.error("Unable to encode cookie", e);
				}
			}
		}
	}
	/**
	 * Установить время существования сеанса (после последнего действия пользователя)
	 * @param seconds
	 */
	public void setSessionTimeoutSeconds(int seconds) {
		if (hasSession())
			forceGetSession().setMaxInactiveInterval(seconds);
	}
	/**
	 * Проверить, есть ли сеанс
	 * @return
	 */
	private boolean hasSession() {
		return request != null && request.getSession(false) != null;
	}
	/**
	 * Вернуть существующий сеанс, создать сеанс, если он еще не существует
	 * @return
	 */
	private HttpSession forceGetSession() {
		return request.getSession(true);
	}
	/**
	 * Установить режим визуального редактирования сайта
	 * @param contentUpdateOn
	 */
	public final void setContentUpdateMode(boolean contentUpdateOn) {
		setVariableValue(CONTENT_UPDATE_VAR_NAME, Boolean.toString(contentUpdateOn));
	}
	/**
	 * Установлен ли режим визуального редактирования сайта
	 * @return
	 */
	public final boolean isContentUpdateMode() {
		return Boolean.parseBoolean(getVariableValue(CONTENT_UPDATE_VAR_NAME));
	}

	/**
	 * Сгенерировать новый ID для новых айтемов данного запроса
	 * @return
	 */
	public final long getNewId() {
		return _id_generator--;
	}

	/**
	 * Возврат первоначального значения для генератора сеансовых ID айтемов
	 */
	public final void resetIdGenerator() {
		_id_generator = INITIAL_GENERATED_ID;
	}
}
