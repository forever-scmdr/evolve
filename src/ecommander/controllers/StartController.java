package ecommander.controllers;

import javax.servlet.ServletContext;

import ecommander.controllers.parsing.DataModelBuilder;
import ecommander.controllers.parsing.DomainBuilder;
import ecommander.controllers.parsing.PageModelBuilder;
import ecommander.users.UserMapper;

/**
 * Обрабатывает запуск приложения
 * @author EEEE
 *
 */
public class StartController {
	
	private static final String STARTED = "started";
	
	public static void start(ServletContext servletContext) throws Exception {
		if (servletContext.getAttribute(STARTED) == null) {
			synchronized (STARTED) {
				if (servletContext.getAttribute(STARTED) == null) {
					// Загружаются домены
					DomainBuilder.testActuality();
					// Загружаются описания айтемов (из XML файла + БД)
					DataModelBuilder.testActuality();
					// Загружаются пользователи
					UserMapper.loadUserGorups();
					// Загружается модели страниц (из XML файла)
					PageModelBuilder.testActuality();
					// Флаг о том, что приложение запущено
					servletContext.setAttribute(STARTED, STARTED);
				}
			}
		}
	}
	
	public static void restart(ServletContext servletContext) throws Exception {
		// Флаг о том, что приложение запущено
		synchronized (STARTED) {
			servletContext.setAttribute(STARTED, null);
		}
		start(servletContext);
	}
}