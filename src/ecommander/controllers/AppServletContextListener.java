package ecommander.controllers;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ecommander.common.ServerLogger;
import ecommander.persistence.mappers.LuceneIndexMapper;

public class AppServletContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		try {
			LuceneIndexMapper.closeWriter();
		} catch (IOException e) {
			ServerLogger.error("Can't close lucene index writer on servlet context shutdown", e);
		}
	}

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		// Инициализация конектста приложения (установка базового пути к файлам)
		AppContext.init(context);
		ServerLogger.init(AppContext.getFilesDirPath() + "eco.log");
	}

}
