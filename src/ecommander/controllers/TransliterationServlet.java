package ecommander.controllers;

import ecommander.fwk.PageNotFoundException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.fwk.UserNotAllowedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Сервлет, который обрабатывает запросы eco/
 * Такие сервлеты имеют следующий формат
 * http://site.com/eco/clinic/tri_dantista|55/translit_value|N/common_var/common_value
 * clinic - имя страницы
 * tri_dantista|55 - значение первой по порядку переменной на странице page_name в форме транслита (отделяется знаком /, | для выделения значения)
 * translit_value - значение второй по порядку переменной на странице page_name в форме транслита (отделяется знаком /)
 * common_var/common_value - имя и значение обычной переменной в формате CMS (отделяется /)
 * eco/ - начало урлов, используемое этим сервлетом
 */
public class TransliterationServlet extends BasicServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1847991927782653866L;

	/**
	 *
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Строка вида /spas/eeee/test.htm (/spas - это ContextPath)
		String userUrl = getUserUrl(request);
		Timer.getTimer().start(Timer.REQUEST_PROCESS, userUrl);
		// Если заданного URL нет в маппинге, то считать что запрошен файл и передать его
		try {
			ServerLogger.debug("Get method: Page output started");
//			ServerLogger.error("--------------------------           " + userUrl + "           --------------------------");
			MainExecutionController mainController = new MainExecutionController(request, response, userUrl);
			mainController.execute(getBaseUrl(request), getServletContext());
		} catch (UserNotAllowedException e) {
			processUserNotAllowed(request, response, userUrl);
		} catch (PageNotFoundException e) {
			sendFile(response, userUrl);
		} catch (Exception e) {
			handleError(request, response, e);
		} finally {
			Timer.getTimer().stop(Timer.REQUEST_PROCESS);
			Timer.getTimer().flush();
		}
	}
}
