package ecommander.controllers;

import ecommander.fwk.MessageError;
import ecommander.model.DataModelBuilder;
import ecommander.pages.PageModelBuilder;
import ecommander.persistence.mappers.LuceneIndexMapper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MetaServlet extends BasicServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6967843764716842592L;
	
	public static final String LINK_PARAMETER_NAME = "q";
	public static final String ACTION_UPDATE_MODEL = "update_model";
	public static final String ACTION_FORCE_MODEL = "force_model";
	public static final String REINDEX = "reindex";
	//public static final String MIGRATE_ITEMS = "migrate_items";
	//public static final String IMPORT_ITEMS = "import_items";
	public static final String ITEMS_TO_BE_DELETED_ATTR = "items_to_be_deleted";

	public static final String SESSION_CONFIRM_CREATE_MODEL = "create_model_force_session";

	public static final String CONFIRM_CREATE_MODEL_JSP = "/create_model_confirm.jsp";
	
	private static final String SUCCESS = "<html><body><table width='100%' height='100%'><tr>" 
		+ "<td align='center' valign='middle'><h1>SUCCESS</h1></td></tr></table></body></html>";

	/**
	 *
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(LINK_PARAMETER_NAME);
		try {
			if (action.equalsIgnoreCase(REINDEX)) {
				StartController.getSingleton().start(getServletContext());
				LuceneIndexMapper.getSingleton().reindexAll();
			} else if (action.equalsIgnoreCase(ACTION_UPDATE_MODEL)) {
				DataModelBuilder modelBuilder = DataModelBuilder.newSafeUpdate();
				boolean hasDeletions = !modelBuilder.tryLockAndReloadModel();
				if (hasDeletions) {
					request.getSession().setAttribute(SESSION_CONFIRM_CREATE_MODEL, true);
					request.setAttribute(ITEMS_TO_BE_DELETED_ATTR, modelBuilder.getItemsToBeDeleted());
					RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(CONFIRM_CREATE_MODEL_JSP);
					dispatcher.forward(request, response);
					return;
				}
			} else if (action.equalsIgnoreCase(ACTION_FORCE_MODEL)) {
				boolean confirmed = (Boolean) request.getSession().getAttribute(SESSION_CONFIRM_CREATE_MODEL);
				if (confirmed) {
					DataModelBuilder modelBuilder = DataModelBuilder.newForceUpdate();
					modelBuilder.tryLockAndReloadModel();
					PageModelBuilder.invalidate();
				} else {
					throw new MessageError("Force create model not confirmed", "Not confirmed");
				}
			}
			response.setContentType("text/html");
			response.getOutputStream().write(SUCCESS.getBytes(StandardCharsets.UTF_8));
			response.getOutputStream().flush();
		} catch (Exception e) {
			handleError(request, response, e);
		}
	}

	/**
	 *
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
