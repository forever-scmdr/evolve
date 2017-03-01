package ecommander.admin;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import ecommander.controllers.BasicServlet;
import ecommander.controllers.LoginServlet;
import ecommander.controllers.SessionContext;
import ecommander.controllers.StartController;
import ecommander.model.DataModelBuilder;
import ecommander.model.DomainBuilder;
import ecommander.model.User;
/**
 * Базовый класс для всех админских модулей
 * @author EEEE
 *
 */
public abstract class BasicAdminServlet extends HttpServlet {

	private static final long serialVersionUID = -6905939626507781404L;
	
	// Текущий пользователь
	private User user = null;
	// Сообщение об ошибке
	private String errorMessage = null;
	
	public void setUser(User user) {
		this.user = user;
	}
	
	protected User getCurrentAdmin() {
		return user;
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			StartController.start(getServletContext());
			DomainBuilder.testActuality();
			processRequest(req, resp);
		} catch (Exception e) {
			BasicServlet.handleError(req, resp, e);
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			StartController.start(getServletContext());
			DomainBuilder.testActuality();
			processRequest(req, resp);
		} catch (Exception e) {
			BasicServlet.handleError(req, resp, e);
		}
	}
	/**
	 * Обработать запрос
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	abstract protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception;
	/**
	 * Проверяет наличие залогиненного юзера
	 * @param redirectLink - страница, на которую осуществить редирект после того, как пользователь будет залогинен
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	protected boolean checkUser(HttpServletRequest req, HttpServletResponse resp, String redirectLink) throws ServletException, IOException {
		User user = SessionContext.createSessionContext(req).getUser();
		if (user.isAnonimous()) {
			req.setAttribute(LoginServlet.TARGET_INPUT, redirectLink);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(BasicServlet.LOGIN_PAGE_NAME);
			dispatcher.forward(req, resp);
			return false;
		}
		setUser(user);
		return true;
	}
	/**
	 * Вернуть название экшена (название экшена - это часть запрашиваемого URL)
	 * @return
	 */
	protected String getAction(HttpServletRequest req) {
		return req.getServletPath().replaceFirst("/", "").replaceFirst("\\..*", "");
	}
	/**
	 * Осуществить forward (переход на указанный урл без изменения урла в строке браузера)
	 * @param url
	 * @throws IOException 
	 * @throws ServletException 
	 */
	protected void forward(HttpServletRequest req, HttpServletResponse resp, String url) throws ServletException, IOException {
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(url);
		dispatcher.forward(req, resp);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * Получить домен сайта из строки запроса
	 * @param req
	 * @return
	 */
	protected static String getContextPath(HttpServletRequest req) {
		String domain = req.getServerName() + (req.getServerPort() == 80 ? "" : ":" + req.getServerPort()) + req.getContextPath();
		return "http://" + (StringUtils.isBlank(req.getContextPath()) ? domain : domain + "/");
	}
	/**
	 * Получить полную строку запроса из объекта запроса
	 * @param request
	 * @return
	 */
	public static String getRequestStrig(HttpServletRequest request) {
		return 
				request.getScheme() + "://" + request.getServerName() + 
				("http".equals(request.getScheme()) && request.getServerPort() == 80 || 
				"https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
				request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
	}
}
