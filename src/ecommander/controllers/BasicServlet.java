package ecommander.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.common.exceptions.EcommanderException;
import ecommander.common.exceptions.UserNotAllowedException;
import ecommander.common.exceptions.ValidationException;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.variables.VariablePE;

public abstract class BasicServlet extends HttpServlet {
	
	public static final String PREFIX = "eco/";
	private static final int PREFIX_LENGTH = PREFIX.length();
	protected static final String LINK_PARAMETER_NAME = "q";

	public static final String LOGIN_PAGE_NAME = "/login.jsp";
	public static final String ERROR_PAGE_NAME = "/error.jsp";
	public static final String ERROR_STATIC_PAGE_NAME = "/error.html";
	
	public static final String MODEL_ERRORS_NAME = "model_errors";
	public static final String PAGES_ERRORS_NAME = "pages_errors";
	public static final String EXCEPTION_NAME = "exception";
	/**
	 * 
	 */
	private static final long serialVersionUID = 892605216488781362L;
	/**
	 * Обработать запрос
	 * @param response
	 * @param link
	 */
	protected void processUrl(HttpServletRequest request, HttpServletResponse response, String linkString) {
		try {
			MainExecutionController mainController = new MainExecutionController(request, response, linkString);
			mainController.execute(getBaseUrl(request), getServletContext());
		} catch (UserNotAllowedException e) {
			// Редирект на страницу логина
			processUserNotAllowed(request, response, linkString);
		} catch (Exception e) {
			handleError(request, response, e);
		}
	}
	/**
	 * Редирект на страницу логина
	 * @param response
	 * @param linkString
	 */
	protected void processUserNotAllowed(HttpServletRequest request, HttpServletResponse response, String linkString) {
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		response.setHeader(
				"Location",
				getContextPath(request) + LOGIN_PAGE_NAME + "?" + LINK_PARAMETER_NAME + "=" + linkString);
		response.setContentType("text/html");
	}
	/**
	 * Обработать ошибку - вернуть страницу с ошибкой
	 * @param request
	 * @param response
	 * @param e
	 */
	public static void handleError(HttpServletRequest request, HttpServletResponse response, Exception e) {
		try {
			ServerLogger.error("CMS Exception", e);
			if (e instanceof ValidationException) {
				ValidationException errors = (ValidationException) e;
				request.setAttribute(MODEL_ERRORS_NAME, errors.getResults().getLineErrors());
				request.setAttribute(PAGES_ERRORS_NAME, errors.getResults().getStructureErrors());
				if (errors.getResults().getException() != null)
					request.setAttribute(EXCEPTION_NAME, printException(errors.getResults().getException()));
			} else if (e instanceof ClientAbortException) {
				return;
			} else {
				request.setAttribute(EXCEPTION_NAME, printException(e));
			}
			RequestDispatcher requestDispatcher = request.getRequestDispatcher(ERROR_PAGE_NAME);
			requestDispatcher.forward(request, response);
		} catch (Exception e1) {
			ServerLogger.error("unable to send error page", e1);
			response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "http://" + getContextPath(request) + ERROR_STATIC_PAGE_NAME);
			response.setContentType("text/html");
		}
	}
	/**
	 * Напечатать эксепшен в виде строки
	 * @param e
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String printException(Throwable e) throws UnsupportedEncodingException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter printer = new PrintWriter(bos);
		e.printStackTrace(printer);
		printer.flush();
		printer.close();
		return bos.toString("UTF-8");
	}
	/**
	 * Отправить обычный файл, находящийся по заданному относительно CONTEXT_ROOT адресу
	 * @param response
	 * @param fileUrl
	 * @throws IOException
	 * @throws EcommanderException 
	 */
	protected void sendFile(HttpServletResponse response, String fileUrl) throws IOException {
		File requestedFile = new File(AppContext.getRealPath(fileUrl));
		if (requestedFile.exists() && requestedFile.isFile()) {
			FileInputStream fis = new FileInputStream(requestedFile);
			byte[] buffer = new byte[4096];
			int byteCount = 0;
			while ((byteCount = fis.read(buffer)) >= 0) {
				response.getOutputStream().write(buffer, 0, byteCount);
			}
			response.getOutputStream().flush();
			fis.close();
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	/**
	 * Получить часть URL после названия сайта
	 * @param request
	 * @return
	 */
	public static String getUserUrl(HttpServletRequest request) {
		// Строка вида /spas/eeee/test.htm (/spas - это ContextPath)
		String userUrl = request.getRequestURI();
		if (!StringUtils.isBlank(request.getContextPath())) {
			userUrl = userUrl.substring(request.getContextPath().length());
		}
		if (userUrl.equals("/"))
			return AppContext.getWelcomePageName();
		userUrl = userUrl.substring(PREFIX_LENGTH);
		// Удалить переменную _ которая добавляется jQuery при отправке ajax запросов
		String queryString = request.getQueryString();
		int jqueryVarIndex = StringUtils.indexOf(queryString, "_=");
		if (jqueryVarIndex > 0) {
			queryString = StringUtils.substring(queryString, 0, jqueryVarIndex - 1);
		} else if (jqueryVarIndex == 0) {
			queryString = null;
		}		
		if (queryString != null) {
			userUrl += '?' + queryString;
		}
		// Убирается идущий спереди слэш (/)
		return userUrl.substring(1);
	}
	/**
	 * Добавляет дополнительные параметры к базовому УРЛ в формате CMS
	 * (в виде http://forintek-bel.by/command.eco?q=catalog/device_type:v:/device_field:v:Маркировка шита/manufacturer:v:Markem
	 * или в виде http://forintek-bel.by/catalog/manufacturer:v:Markem.htm)
	 * и осуществляет редирект на полученный URL
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Строка вида /spas/eeee/test.htm (/spas - это ContextPath)
		String url = getUserUrl(request);
		LinkPE link = LinkPE.parseLink(url);
		Map<String, List<String>> params = new HashMap<String, List<String>>();
		if (ServletFileUpload.isMultipartContent(request)) {
			DiskFileItemFactory filesFactory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(filesFactory);
			String encoding = Strings.SYSTEM_ENCODING;
			upload.setHeaderEncoding(encoding);
			List<FileItem> values = new ArrayList<FileItem>();
			try {
				values = upload.parseRequest(request);
			} catch (FileUploadException e) {
				ServerLogger.error(e);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			for (FileItem fileItem : values) {
				List<String> vals = params.get(fileItem.getFieldName());
				if (vals == null) {
					vals = new ArrayList<String>();
					params.put(fileItem.getFieldName(), vals);
				}
	    		if (fileItem.isFormField())
	    			vals.add(fileItem.getString(encoding));
	    		else if (!StringUtils.isBlank(fileItem.getName()))
	    			vals.add(fileItem.getName());
			}
		} else {
			for (String paramName : request.getParameterMap().keySet()) {
				params.put(paramName, Arrays.asList(request.getParameterMap().get(paramName)));
			}
		}
		// Удалить уже установленные параметры (которые переданы через исходную ссылку)
		for (VariablePE var : link.getAllVariables()) {
			params.remove(var.getName());
		}
		// Перебираются все входные парамтеры. (повторные вхождения переменных уже удалены)
		for (String paramName : params.keySet()) {
			List<String> values = params.get(paramName);
			for (String value : values) {
				link.addStaticVariable(paramName, value);
			}
		}
		// Редирект на нужную страницу с уже добавленными параметрами
/*		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", request.getContextPath() + "/" + link.serialize());
		response.setContentType("text/html");*/
		processUrl(request, response, link.serialize());
	}
	/*
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Строка вида /spas/eeee/test.htm (/spas - это ContextPath)
		String url = getUserUrl(request);
		LinkPE link = LinkPE.parseLink(url);
		Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());
		// Удалить уже установленные параметры (которые переданы через исходную ссылку)
		for (VariablePE var : link.getAllVariables()) {
			params.remove(var.getName());
		}
		// Перебираются все входные парамтеры. (повторные вхождения переменных уже удалены)
		for (String paramName : params.keySet()) {
			String[] values = params.get(paramName);
			for (String value : values) {
				if (!StringUtils.isBlank(value)) {
					link.addStaticVariable(paramName, value);
				}
			}
		}
		// Редирект на нужную страницу с уже добавленными параметрами
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", request.getContextPath() + "/" + link.serialize());
		response.setContentType("text/html");
	}
	*/
	
	public static String getContextPath(HttpServletRequest request) {
		return
				AppContext.getProtocolScheme() + "://" + request.getServerName() +
				(request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
				request.getContextPath();
	}
	/**
	 * Получить базовый урл, т.е. урл, который должен быть в теге <base>
	 * @param request
	 * @return
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		return StringUtils.isBlank(request.getContextPath()) ? getContextPath(request) : getContextPath(request) + "/";
	}

	/**
	 * Проверяет, соответствует ли протокол запроса протоколу, установленному в настройках сайта
	 * Если не соответствует - отправить редирект на соответствующий урл с нужным протоколом
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static boolean checkProtocolScheme(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestScheme = "http";
		String checkHeaderValue = request.getHeader(AppContext.getTestHttpsHeader());
		if (checkHeaderValue != null &&
				(!AppContext.hasTestHttpsValue() || StringUtils.equalsIgnoreCase(AppContext.getTestHttpsHeaderValue(), checkHeaderValue))) {
			requestScheme = "https";
		}
		if (StringUtils.equalsIgnoreCase(requestScheme, AppContext.getProtocolScheme())) {
			return true;
		}
		// Строка вида /spas/eeee/test.htm (/spas - это ContextPath)
		String userUrl = request.getRequestURI();
		if (userUrl.charAt(0) == '/')
			userUrl = userUrl.substring(1);
		if (StringUtils.startsWith(userUrl, PREFIX))
			userUrl = userUrl.substring(PREFIX_LENGTH);
		userUrl = userUrl.replaceFirst(AppContext.getWelcomePageName(), "");

		// Удалить переменную _ которая добавляется jQuery при отправке ajax запросов
		if (StringUtils.isNotBlank(request.getQueryString())) {
			userUrl += '?' + request.getQueryString();
		}
		while (userUrl.length() > 0 && userUrl.charAt(0) == '/') {
			userUrl = userUrl.substring(1);
		}

		String contextPath = AppContext.getProtocolScheme() + "://" + request.getServerName() +
				(request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort()) +
				request.getContextPath();
		if (!StringUtils.endsWith(contextPath, "/"))
			contextPath += "/";

		//response.sendRedirect(contextPath + userUrl);
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", contextPath + userUrl);
		return false;
	}
}
