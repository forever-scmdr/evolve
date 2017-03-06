package ecommander.controllers;

import javax.servlet.ServletContext;

import ecommander.model.DataModelBuilder;
import ecommander.model.DomainBuilder;
import ecommander.pages.PageModelBuilder;
import ecommander.model.UserMapper;

/**
 * Обрабатывает запуск приложения
 * @author EEEE
 *
 */
public class StartController {
	
	private static final String STARTED = "started";

	private static volatile StartController singleton;

	private StartController() {

	}

	public static StartController getSingleton() {
		if (singleton == null) {
			singleton = new StartController();
		}
		return singleton;
	}

	public void start(ServletContext servletContext) throws Exception {
		if (servletContext.getAttribute(STARTED) == null) {
			synchronized (this) {
				if (servletContext.getAttribute(STARTED) == null) {
					// Загружаются домены
					DomainBuilder.testActuality();
					// Загружаются описания айтемов (из XML файла + БД)
					DataModelBuilder.newLoader().tryLockAndReloadModel();
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
}