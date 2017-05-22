package ecommander.controllers;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ecommander.pages.SingleItemHttpPostFormDeprecated;
import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.persistence.mappers.SessionItemMapper;
import ecommander.persistence.mappers.SessionObjectStorage;
import ecommander.model.User;
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
	private static final int COOKIE_EXPIRE = 10 * 24 * 60 * 60;

	@Override
	public void close() throws Exception {
		closeDBConnection();
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
	private Connection dbConnection;
	private SessionObjectStorage storage = null;
	private User user = null;
	private HashMap<String, String> cookies = null;

	private SessionContext(HttpServletRequest request) {
		this.request = request;
	}

	public static SessionContext createSessionContext(HttpServletRequest request) {
		return new SessionContext(request);
	}
	/**
	 * Получить сеансовое хранилище
	 * @param create - надо ли создавать новый сеанс, в случае если сеанс еще не создан
	 * @return
	 */
	public final SessionObjectStorage getStorage(boolean create) {
		if (storage == null) {
			if (hasSession() || create) {
				storage = (SessionObjectStorage)forceGetSession().getAttribute(STORAGE_SESSION_NAME);
				if (storage == null) {
					storage = SessionItemMapper.createSessionStorage();
					forceGetSession().setAttribute(STORAGE_SESSION_NAME, storage);
				}
			} else {
				return SessionItemMapper.createSessionStorage();
			}
		}
		return storage;
	}
	
	public void setUser(User user) {
		this.user = user;
		forceGetSession().setAttribute(USER_SESSION_NAME, user);
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
			forceGetSession().setAttribute(VARIABLE_SESSION_NAME_PREFIX + varName, varValue);
		else if (!StringUtils.isBlank(varName))
			forceGetSession().removeAttribute(VARIABLE_SESSION_NAME_PREFIX + varName);
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
			forceGetSession().setAttribute(PROGRESS_SESSION_NAME_PREFIX + progressName, new Progress(percent, size, total, unit, message));
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
	 * @param name
	 * @return
	 */
	public String getCookie(String name) {
		if (request == null)
			return null;
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
	 */
	public void saveForm(SingleItemHttpPostFormDeprecated form) {
		forceGetSession().setAttribute(FORM_SESSION_NAME_PREFIX + form.getFormId(), form);
	}
	/**
	 * Удалить форму из сеанса
	 * @param form
	 */
	public void removeForm(SingleItemHttpPostFormDeprecated form) {
		if (hasSession())
			forceGetSession().removeAttribute(FORM_SESSION_NAME_PREFIX + form.getFormId());
	}
	/**
	 * Получить из сеанса ранее сохраненную форму
	 * Если форма сохранена не была, возвращается null
	 * @param formId
	 * @return
	 */
	public SingleItemHttpPostFormDeprecated getForm(String formId) {
		if (hasSession())
			return (SingleItemHttpPostFormDeprecated) forceGetSession().getAttribute(FORM_SESSION_NAME_PREFIX + formId);
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
	 * Получить подключение к базе данных
	 * @return
	 */
	public Connection getDBConnection() {
		try {
			if (dbConnection == null || dbConnection.isClosed())
				dbConnection = MysqlConnector.getConnection(request);
		} catch (Exception e) {
			ServerLogger.error("Unable to get new MySQL connection", e);
		}
		return dbConnection;
	}
	/**
	 * Закрыть подключение
	 */
	private void closeDBConnection() {
		MysqlConnector.closeConnection(dbConnection);
		dbConnection = null;
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
	
}
