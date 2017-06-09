package ecommander.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.UserNotAllowedException;
import ecommander.pages.LinkPE;
import ecommander.pages.variables.FilterStaticVariablePE;
import ecommander.pages.var.VariablePE;
/**
 * Подразумевается, что этот сервлет обрабатывает только 
 * @author EEEE
 *
 */
public class FilterFormServlet extends BasicServlet {

	private static final long serialVersionUID = -249439767363279922L;
	private static final int FILTER_PREFIX_LENGTH = LinkPE.FILTER_PREFIX.length();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ServerLogger.debug("Post method: Page output started");
		String targetUrl = getUserUrl(req);
		targetUrl = targetUrl.substring(FILTER_PREFIX_LENGTH);
		String sortingStr = Strings.EMPTY;
		String pageStr = Strings.EMPTY;
		String varName = Strings.EMPTY;
		req.getQueryString();
		try {
			LinkPE targetLink = LinkPE.parseLink(targetUrl);
			varName = targetLink.getVariable(LinkPE.VAR_VARIABLE).output(); // Название переменной для пользовательского фильтра
			targetLink.removeVariable(LinkPE.VAR_VARIABLE); // чтобы не выводить лишнюю переменную, которая все равно добавится потом
			Map<String, String[]> params = new HashMap<String, String[]>(req.getParameterMap());
			// Удалить все лишние пеерменные, которые содержатся в targetUrl
			params.remove(LinkPE.VAR_VARIABLE); // на всякий случай
			for(VariablePE targetPageVar : targetLink.getAllVariables()) {
				params.remove(targetPageVar.getName());
			}
			if (params.containsKey(FilterStaticVariablePE.SORTING))
				sortingStr = params.remove(FilterStaticVariablePE.SORTING)[0];
			if (params.containsKey(FilterStaticVariablePE.PAGE))
				pageStr = params.remove(FilterStaticVariablePE.PAGE)[0];
			StringBuilder filterStr = new StringBuilder();
			// Все переданные через POST параметры.
			// Имена параметров фильтра представляют собой число, имена обычных параметров не могут быть числом
			for (String paramName : params.keySet()) {
				String[] values = params.get(paramName);
				if (StringUtils.isNumeric(paramName)) {
					for (String value : values) {
						if (!StringUtils.isBlank(value)) {
							if (filterStr.length() > 0)
								filterStr.append(FilterStaticVariablePE.TOKEN_DELIM);
							filterStr.append(paramName).append(FilterStaticVariablePE.VALUE_DELIM).append(value);
						}
					}
				} else {
					for (String value : values) {
						if (!StringUtils.isBlank(value)) {
							targetLink.addStaticVariable(paramName, value);
						}
					}
				}
			}
			if (!StringUtils.isBlank(sortingStr))
				filterStr.append(FilterStaticVariablePE.TOKEN_DELIM).append(FilterStaticVariablePE.SORTING).append(sortingStr);
			if (!StringUtils.isBlank(pageStr))
				filterStr.append(FilterStaticVariablePE.TOKEN_DELIM).append(FilterStaticVariablePE.PAGE).append(pageStr);
			
			FilterStaticVariablePE filterVar = new FilterStaticVariablePE(varName, filterStr.toString());
			targetLink.addVariable(filterVar);
			
			MainExecutionController mainController = new MainExecutionController(req, resp, targetLink.serialize());
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
		doPost(req, resp);
	}

}
