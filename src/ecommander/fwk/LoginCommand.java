package ecommander.fwk;

import org.apache.commons.lang3.StringUtils;

import ecommander.controllers.LoginServlet;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.pages.ResultPE.ResultType;
import ecommander.model.User;
import ecommander.model.UserMapper;

import java.sql.Connection;

/**
 * Стандартная команда, которая осуществляет вход и выход пользователя
 * Работает в двух режимах.
 * 
 * Режим AJAX
 * Принимает 3 аргумента - имя пользователя, пароль и действие (вход - in или выход - out)
 * Если заданы имя пользователя и пароль, то считается что действие - вход
 * Если действие - выход, то имя пользователя и пароль не обязательны
 * 
 * Режим forward
 * Принимает 4 аргумента - имя пользователя, пароль, URL, на который надо зайти
 * и действие (должно быть forward). В этом случае команда пересылает запрос
 * стандартному сервлету логина - LoginServlet
 * 
 * Возаращает 4 результата:
 * 		in (вход)
 * 		out (выход) 
 * 		not_correct (логин и пароль некорректны или не заданы параметры команды)
 * 		error (ошибка)
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
	public static final String RESULT = "result";
	
	@Override
	public ResultPE execute() throws Exception {
		String action = getVarSingleValue(ACTION);
		String userName = getVarSingleValue(USERNAME);
		String password = getVarSingleValue(PASSWORD);
		try {
			if (!StringUtils.isBlank(userName) && StringUtils.isBlank(action))
				action = IN;
			if (StringUtils.isBlank(action) && StringUtils.isBlank(userName))
				return getResult(RESULT).setValue(NOT_CORRECT);
			if (action.equalsIgnoreCase(FORWARD)) {
				String target = getVarSingleValue(URL);
				String url = LoginServlet.LOGIN_ACTION 
						+ ".login?" + LoginServlet.NAME_INPUT + "=" + userName 
						+ "&" + LoginServlet.PASSWORD_INPUT + "=" + password 
						+ "&" + LoginServlet.TARGET_INPUT + "=" + target;
				return getResultingUrl(url, ResultType.forward);
			}
			if (action.equalsIgnoreCase(OUT)) {
				endUserSession();
				return getResult(OUT);
			}
			User user = null;
			try (Connection conn = MysqlConnector.getConnection()) {
				user = UserMapper.getUser(userName, password, conn);
			}
			if (user != null) {
				startUserSession(user);
				return getResult(RESULT);
			} else {
				return getResult(RESULT).setValue(NOT_CORRECT);
			}
		} catch (Exception e) {
			ServerLogger.error("Auth process error", e);
			return getResult(RESULT).setValue(ERROR);
		}
	}

}
