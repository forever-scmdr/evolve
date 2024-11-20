package ecommander.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ecommander.common.ServerLogger;
import ecommander.common.exceptions.UserNotAllowedException;
import ecommander.pages.elements.ItemVariablesContainer;
import ecommander.pages.elements.LinkPE;

/**
 * Контроллер, который создает и передает на обработку класс ItemVariablesContainer
 * @author EEEE
 *
 */
public class ItemVariablesServlet extends BasicServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1163550145914712571L;
	private static final int ITEM_VARS_PREFIX_LENGTH = LinkPE.ITEM_VARS_PREFIX.length();

	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServerLogger.debug("Get method: Page output started");
		String targetUrl = getUserUrl(req);
		targetUrl = targetUrl.substring(ITEM_VARS_PREFIX_LENGTH);
		try {
			ItemVariablesContainer varContainer = new ItemVariablesContainer(req);
			MainExecutionController mainController = new MainExecutionController(req, resp, targetUrl);
			mainController.setPostItemVariables(varContainer);
			mainController.execute(getBaseUrl(req), getServletContext());
		} catch (UserNotAllowedException e) {
			processUserNotAllowed(req, resp, targetUrl);
		} catch (Exception e) {
			handleError(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}
	
	
}
