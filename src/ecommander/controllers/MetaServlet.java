package ecommander.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ecommander.common.MysqlConnector;
import ecommander.migration.FilterEntityIdToNameConverter;
import ecommander.migration.OldModelConverter;
import ecommander.migration.VeryOldItemsImporter;
import ecommander.persistence.TransactionContext;
import ecommander.persistence.mappers.LuceneIndexMapper;

public class MetaServlet extends BasicServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6967843764716842592L;
	
	public static final String LINK_PARAMETER_NAME = "q";
	public static final String ACTION_CREATE_USERS = "create_users";
	public static final String ACTION_CREATE_DOMAINS = "create_domains";
	public static final String REINDEX = "reindex";
	public static final String MIGRATE_ITEMS = "migrate_items";
	public static final String CONVERT_FILTERS = "convert_filters";
	public static final String IMPORT_ITEMS = "import_items";
	
	private static final String SUCCESS = "<html><body><table width='100%' height='100%'><tr>" 
		+ "<td align='center' valign='middle'><h1>SUCCESS</h1></td></tr></table></body></html>";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter(LINK_PARAMETER_NAME);
		try {
			// Старт приложения, если он еще не был осуществлен
			if (action.equalsIgnoreCase(ACTION_CREATE_USERS)) {
				new UserCreationController().readAndCreateUsers();
			} else if (action.equalsIgnoreCase(REINDEX)) {
				StartController.start(getServletContext());
				LuceneIndexMapper.reindexAll();
			} else if (action.equalsIgnoreCase(MIGRATE_ITEMS)) {
				TransactionContext ctx = new TransactionContext(MysqlConnector.getConnection(), null);
				try {
					String src = request.getParameter("src");
					String dest = request.getParameter("dest");
					OldModelConverter converter = new OldModelConverter(src, dest);
					converter.setTransactionContext(ctx);
					converter.execute();
				} finally {
					MysqlConnector.closeConnection(ctx.getConnection());
				}
			} else if (action.equalsIgnoreCase(IMPORT_ITEMS)) {
				TransactionContext ctx = new TransactionContext(MysqlConnector.getConnection(), null);
				try {
					VeryOldItemsImporter importer = new VeryOldItemsImporter();
					importer.setTransactionContext(ctx);
					importer.execute();
				} finally {
					MysqlConnector.closeConnection(ctx.getConnection());
				}
			} else if (action.equalsIgnoreCase(CONVERT_FILTERS)) { // TODO <fix> удалить
				TransactionContext ctx = new TransactionContext(MysqlConnector.getConnection(), null);
				try {
					FilterEntityIdToNameConverter converter = new FilterEntityIdToNameConverter();
					converter.setTransactionContext(ctx);
					converter.execute();
				} finally {
					MysqlConnector.closeConnection(ctx.getConnection());
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
