package ecommander.controllers;

import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.fwk.ValidationException;
import ecommander.model.Item;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.MultipleHttpPostForm;
import ecommander.pages.PageModelRegistry;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Класс, который координирует все действия по обработке запроса пользователя
 * @author EEEE
 *
 */
public class MainExecutionController {


	// Форма, которая была отправлена пользователем, устанавливается из экшена struts
	private MultipleHttpPostForm itemForm = null;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private String requestUrl;

	public MainExecutionController(HttpServletRequest req, HttpServletResponse resp, String requestUrl) {
		this.req = req;
		this.resp = resp;
		this.requestUrl = requestUrl;
	}
	/**
	 * Если юзеру не разрешен доступ к этой странице, то возвращаетс false
	 * Иначе возвращается true
	 * @param baseUrl
	 * @param servletContext
	 * @throws Exception
	 */
	public void execute(String baseUrl, ServletContext servletContext) throws Exception {
		Timer.getTimer().start(Timer.INIT);
		// Старт приложения, если он еще не был осуществлен
		StartController.getSingleton().start(servletContext);
		// Создание контекста сеанса
		try (SessionContext sessContext = SessionContext.createSessionContext(req)) {
			// Загрузка страницы
			Pair<ExecutablePagePE, Item> pair = PageModelRegistry.testAndGetRegistry().getExecutablePage(requestUrl, baseUrl, sessContext);
			ExecutablePagePE page = pair.getLeft();
			Item exclusiveItem = pair.getRight();
			// Переадресация, если дата изменения раньше чем if-modified-since
			String modifiedStr = req.getHeader(HttpHeaders.IF_MODIFIED_SINCE);
			if (exclusiveItem != null) {
				resp.setHeader(HttpHeaders.LAST_MODIFIED, DateDataType.MODIFIED_FORMATTER.print(exclusiveItem.getTimeUpdated()));
                if (StringUtils.isNotBlank(modifiedStr)) {
                    DateTime ifModifiedSince = DateDataType.MODIFIED_FORMATTER.parseDateTime(modifiedStr);
                    if (!ifModifiedSince.isBefore(exclusiveItem.getTimeUpdated())) {
                        resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                        return;
                    }
                }
			}
			// Установить переменные, если есть команды на странице
			page.setPostData(itemForm);
			Timer.getTimer().stop(Timer.INIT);
			// Выполнить страницу (загрузить и выполнить команды) или взять ее из кеша
			PageController.newUsingCache(requestUrl, req.getServerName()).processPage(page, resp);
		} catch (ValidationException ve) {
			for (ValidationResults.StructureMessage error : ve.getResults().getStructureErrors()) {
				ServerLogger.error("pages.xml: " + error.originator + " - " + error.message);
			}
			if (ve.getResults().getException() != null)
				ServerLogger.error("Cause: ", ve.getResults().getException());
			throw ve;
		}
	}

	public void setPostItemForm(MultipleHttpPostForm itemPostForm) {
		itemForm = itemPostForm;
	}

}






