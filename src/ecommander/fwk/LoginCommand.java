package ecommander.fwk;

import ecommander.controllers.LoginServlet;
import ecommander.model.User;
import ecommander.model.UserMapper;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.pages.ResultPE.ResultType;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.HashSet;

/**
 * Стандартная команда, которая осуществляет вход и выход пользователя
 * Работает в двух режимах.
 * 
 * Режим AJAX
 * Принимает 3 аргумента - имя пользователя, пароль и действие
 *
 * Действия:
 *      in          вход
 *      out         выход
 *      forward     перенаправление на страницу login сервлета (выводит стандартную страницу входа)
 *
 * Параметры:
 *      username    имя пользователя
 *      password    пароль пользователя
 *      action      действие
 *      url         урл для действия forward
 *
 * Результаты:
 * 		not_correct логин и пароль некорректны или не заданы параметры команды
 * 		error       ошибка
 * 	    out         выход
 * 	    <группа>    ссылка для определенной группы пользователей (для каждой группы своя)
 * 	    success     успешный результат по умолчанию если не задан результат для группы пользователя
 *
 * Если заданы имя пользователя и пароль, то считается что действие - вход
 * Если действие - выход, то имя пользователя и пароль не обязательны
 * 
 * Режим forward
 * Принимает 4 аргумента - имя пользователя, пароль, URL, на который надо зайти
 * и действие (должно быть forward). В этом случае команда пересылает запрос
 * стандартному сервлету логина - LoginServlet
 *
 * 
 * @author EEEE
 *
 */
public class LoginCommand extends Command {
	
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String ACTION = "action";
	public static final String URL = "url";
	
	public static final String NOT_CORRECT = "not_correct";
	public static final String ERROR = "error";
	
	public static final String IN = "in";
	public static final String OUT = "out";
	public static final String FORWARD = "forward";
	public static final String SUCCESS = "success";
	
	@Override
	public ResultPE execute() throws Exception {
		String action = getVarSingleValue(ACTION);
		String userName = getVarSingleValue(USERNAME);
		String password = getVarSingleValue(PASSWORD);
		try {
			if (!StringUtils.isBlank(userName) && StringUtils.isBlank(action))
				action = IN;
			if (StringUtils.isBlank(action) && StringUtils.isBlank(userName))
				return getResult(NOT_CORRECT);
			if (action.equalsIgnoreCase(FORWARD)) {
				String target = getVarSingleValue(URL);
				String url = LoginServlet.LOGIN_ACTION 
						+ ".login?" + LoginServlet.NAME_INPUT + "=" + userName 
						+ "&" + LoginServlet.PASSWORD_INPUT + "=" + password 
						+ "&" + LoginServlet.TARGET_INPUT + "=" + target;
				setCookieVariable("minqty", "");
				return getResultingUrl(url, ResultType.forward);
			}
			if (action.equalsIgnoreCase(OUT)) {
				endUserSession();
				setCookieVariable("minqty_opt", "");
				setCookieVariable("minqty", "");
				return getResult(OUT);
			}
			User user;
			try (Connection conn = MysqlConnector.getConnection()) {
				user = UserMapper.getUser(userName, password, conn);
			}
			if (user != null) {
				startUserSession(user);
				HashSet<User.Group> groups = user.getGroups();
				// Начала логин в группы, где пользователь не админ
				for (User.Group group : groups) {
					if (hasResult(group.name) && group.role == User.SIMPLE)
						return getResult(group.name);
				}
				// Потом логн в первую попавшуюся группу
				for (User.Group group : groups) {
					if (hasResult(group.name))
						return getResult(group.name);
				}
				// Если нужных страниц нет, просто возвращается SUCCESS
				return getResult(SUCCESS);
			} else {
				return getResult(NOT_CORRECT);
			}
		} catch (Exception e) {
			ServerLogger.error("Auth process error", e);
			return getResult(ERROR);
		}
	}

}
