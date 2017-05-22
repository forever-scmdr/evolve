package ecommander.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.UserNotAllowedException;
import ecommander.pages.SingleItemHttpPostFormDeprecated;
import ecommander.pages.LinkPE;
/**
 * Подразумевается, что этот сервлет обрабатывает только 
 * @author EEEE
 *
 */
public class ItemFormServlet extends BasicServlet {

	private static final long serialVersionUID = 3101931730901906063L;
	private static final int ITEM_FORM_PREFIX_LENGTH = LinkPE.ITEM_FORM_PREFIX.length();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServerLogger.debug("Get method: Page output started");
		String targetUrl = getUserUrl(req);
		targetUrl = targetUrl.substring(ITEM_FORM_PREFIX_LENGTH);
		try {
			LinkPE target = LinkPE.parseLink(targetUrl);
			SingleItemHttpPostFormDeprecated itemForm = new SingleItemHttpPostFormDeprecated(req, target);
			MainExecutionController mainController = new MainExecutionController(req, resp, target.serialize());
			mainController.setPostItemForm(itemForm);
			mainController.execute(getBaseUrl(req), getServletContext());
		} catch (UserNotAllowedException e) {
			// Редирект на страницу логина
			processUserNotAllowed(req, resp, targetUrl);
		} catch (Exception e) {
			handleError(req, resp, e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServerLogger.warn("GET method invoked in SingleItemHttpPostFormDeprecated servlet: " + getBaseUrl(req));
	}
//
//	public static void main(String[] args) {
//		System.out.println(Pattern.matches("^([^\\.]+[^/])$", "coolee"));
//	}
}
