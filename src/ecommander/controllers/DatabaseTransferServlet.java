package ecommander.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.exceptions.ValidationException;
import ecommander.pages.elements.ValidationResults;

public class DatabaseTransferServlet extends BasicServlet {

	private static final long serialVersionUID = -5274943298989915653L;

	public static final String SERVER_SRC = "serv_src";
	public static final String SERVER_DEST = "serv_dest";
	public static final String DB_SRC = "db_src";
	public static final String DB_DEST = "db_dest";
	public static final String USER_SRC = "user_src";
	public static final String USER_DEST = "user_dest";
	public static final String PASS_SRC = "pass_src";
	public static final String PASS_DEST = "pass_dest";
	
	private String serverNameSrc;
	private String serverNameDest;
	private String dbNameSrc;
	private String dbNameDest;
	private String userSrc;
	private String userDest;
	private String passSrc;
	private String passDest;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	}

	private void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			serverNameSrc = req.getParameter(SERVER_SRC);
			serverNameDest = req.getParameter(SERVER_DEST);
			dbNameSrc = req.getParameter(DB_SRC);
			dbNameDest = req.getParameter(DB_DEST);
			userSrc = req.getParameter(USER_SRC);
			userDest = req.getParameter(USER_DEST);
			passSrc = req.getParameter(PASS_SRC);
			passDest = req.getParameter(PASS_DEST);
			ValidationResults results = new ValidationResults();
			if (StringUtils.isBlank(serverNameSrc) || StringUtils.isBlank(serverNameDest) ||
					StringUtils.isBlank(dbNameSrc) || StringUtils.isBlank(dbNameDest) ||
					StringUtils.isBlank(userSrc) || StringUtils.isBlank(userDest) ||
					StringUtils.isBlank(passSrc) || StringUtils.isBlank(passDest)) {
				results.addError(0, "Не установлены параметры подключения");
				throw new ValidationException("Не установлены параметры подключения", results);
			}
			InputStream in = ClassLoader.getSystemResourceAsStream("sql.transfer_db.sql");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			CharBuffer buf = CharBuffer.allocate(8196);
			while(reader.read(buf) >= 0);
			buf.flip();
			
		} catch (Exception e) {
			handleError(req, resp, e);
		}

	}
	
}
