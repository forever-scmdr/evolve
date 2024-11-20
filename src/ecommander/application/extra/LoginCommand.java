package ecommander.application.extra;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
import ecommander.users.User;
import ecommander.users.UserMapper;

/**
 * Стандартная команда, которая осуществляет вход и выход пользователя
 * Принимает 3 аргумента - имя пользователя, пароль и действие (вход - in или выход - out)
 * Если заданы имя пользователя и пароль, то считается что действие - вход
 * Если действие - выход, то имя пользователя и пароль не обязательны
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
	
	public static final String NOT_CORRECT = "not_correct";
	public static final String ERROR = "error";
	
	public static final String IN = "in";
	public static final String OUT = "out";
	public static final String COOKIE_IN = "cookie_in";
	public static final String ETERNAL_LOGIN = "remember";
	
	@Override
	public ResultPE execute() throws Exception {
		String action = getVarSingleValue(ACTION);
		String userName = getVarSingleValue(USERNAME);
		String password = getVarSingleValue(PASSWORD);
		String remember = getVarSingleValue(ETERNAL_LOGIN);
		
		try {
			if (!StringUtils.isBlank(userName) && StringUtils.isBlank(action))
				action = IN;
				action = (remember != null && remember.equals("yes"))? ETERNAL_LOGIN : action;
			if (StringUtils.isBlank(action) && StringUtils.isBlank(userName))
				return getResult(NOT_CORRECT);
			if (action.equalsIgnoreCase(OUT)) {
				endUserSession();
				ResultPE res = getResult(OUT);
				res.setVariable("cookie", "killMe!");
				return getResult(OUT);
			}
			
			else if (COOKIE_IN.equalsIgnoreCase(action) && StringUtils.isNotBlank(getVarSingleValue("cookie"))){
				long userId = decryptUserId(getVarSingleValue("cookie"));
				User u = UserMapper.getUser(userId);
				startUserSession(u);
				ResultPE res = getResult(IN);
				res.setVariable("cookie", encryptUserId(userId));
				return getResult(IN);
			}
			else if (ETERNAL_LOGIN.equalsIgnoreCase(action)){
				User user = UserMapper.getUser(userName, password);
				startUserSession(user);
				ResultPE res = getResult(IN);
				res.setVariable("cookie", encryptUserId(user.getUserId()));
				return res;
			}
			User user = UserMapper.getUser(userName, password);
			if (user != null) {
				startUserSession(user);
				return getResult(IN);
			} else {
				return getResult(NOT_CORRECT);
			}
		} catch (Exception e) {
			ServerLogger.error("Auth process error", e);
			return getResult(ERROR);
		}
	}

	private String encryptUserId(long id){
		String binaryString = Long.toBinaryString(id);
		int radix = (int)Math.round(2+Math.random()*13);
		long x = Long.parseLong(binaryString, radix);
		return Integer.toHexString(radix)+Long.toHexString(x);
	}

	private long decryptUserId(String s){
		int radix = Integer.parseInt(s.substring(0, 1), 16);
		long xS = Long.parseLong(s.substring(1), 16);
		String binS = Long.toString(xS, radix);
		return Long.parseLong(binS, 2);
	}
	
//	public static void main(String[] args){
//		LoginCommand lc = new LoginCommand();
//		long l = (long) 100;
//		String enc = lc.encryptUserId(l);
//		System.out.println(enc);
//		System.out.println("-----");
//		System.out.println(lc.decryptUserId(enc));
//	} 
}
