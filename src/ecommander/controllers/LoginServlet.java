package ecommander.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ecommander.users.User;
import ecommander.users.UserMapper;

/**
 * Отвечает за аутентификацию пользователя
 * @author EEEE
 *
 */
public class LoginServlet extends BasicServlet {

	public static final String NAME_INPUT = "name";
	public static final String PASSWORD_INPUT = "password";
	public static final String TARGET_INPUT = "target";
	
	public static final String LOGIN_ACTION = "login";
	public static final String LOGOUT_ACTION = "logout";
	/**
	 * 
	 */
	private static final long serialVersionUID = 9037531651966389223L;
	
	private String name;
	private String password;
	private String target;
	private String action;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) {
		SessionContext sessionCtx = null;
		try {
			name = request.getParameter(NAME_INPUT);
			password = request.getParameter(PASSWORD_INPUT);
			target = request.getParameter(TARGET_INPUT);
			action = request.getServletPath().replaceFirst("/", "").replaceFirst("\\..*", "");
			sessionCtx = SessionContext.createSessionContext(request);
			if (action.equalsIgnoreCase(LOGOUT_ACTION)) {
				sessionCtx.userExit();
			} else if (action.equalsIgnoreCase(LOGIN_ACTION)) {
				User user = UserMapper.getUser(name, password);
				if (user != null)
					sessionCtx.setUser(user);
			}
			response.sendRedirect(target);
		} catch (Exception e) {
			try {
				response.sendRedirect(target);
			} catch (Exception e1) {
				handleError(request, response, e1);
			}
		} finally {
			if (sessionCtx != null)
				sessionCtx.closeDBConnection();			
		}
	}
}
