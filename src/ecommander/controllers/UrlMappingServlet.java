package ecommander.controllers;

import ecommander.fwk.ServerLogger;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Класс для установления соответствия между URL, которые задал пользователь и URL, которые используются
 * системой управления. На вход приходит URL, определенный пользователем.
 */
public class UrlMappingServlet extends BasicServlet {
	private static final long serialVersionUID = 1L;
	private Properties urlMapping = new Properties();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UrlMappingServlet() {
        super();
        try {
			urlMapping.load(new FileInputStream(AppContext.getUrlFilePath()));
		} catch (FileNotFoundException e) {
			ServerLogger.error("File with urls ('" + AppContext.getUrlFilePath() + "') was not found", e);
		} catch (IOException e) {
			ServerLogger.error("Unknown problem with url file ('" + AppContext.getUrlFilePath() + "')", e);
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Строка вида /spas/eeee/test.html (/spas - это ContextPath)
			String userUrl = request.getRequestURI();
			if (!StringUtils.isBlank(request.getContextPath())) {
				userUrl = userUrl.substring(request.getContextPath().length());
			}
			// Убирается идущий спереди слэш (/)
			userUrl = userUrl.substring(1);
			String linkString = UserUrlMapper.getCmsUrl(userUrl);
			// Если заданного URL нет в маппинге, то считать что запрошен файл и передать его
//			if (linkString.indexOf(LinkPEWriter.COMMON_URL_BASE) != -1) {
				// Удалить часть урла (c.eco?q=)
			ServerLogger.debug("Get method: Page output started");
			processUrl(request, response, linkString);
//			} else {
//				sendFile(response, userUrl);
//			}
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
