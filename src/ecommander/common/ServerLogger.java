/*
 * Created on 29.11.2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ecommander.common;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;


/**
 * Ведет лог для сервера.
 * Настройки в файле log4j.properties
 * @author karlov
 */
public class ServerLogger {
	protected static final Logger logger = Logger.getLogger(ServerLogger.class);

	public static void init(String fineName) {
		RollingFileAppender fileApp = new RollingFileAppender();
		fileApp.setName("FileLogger");
		fileApp.setEncoding("UTF-8");
		fileApp.setMaxBackupIndex(5);
		fileApp.setMaxFileSize("6MB");
		fileApp.setFile(fineName);
		ConsoleAppender ca = (ConsoleAppender) logger.getAppender("stdout");
		if (ca != null)
			fileApp.setLayout(ca.getLayout());
		else
			fileApp.setLayout(new PatternLayout("[%p]-[%d{HH:mm:ss}]-[%t] - %m%n"));
		fileApp.setThreshold(logger.getLevel());
		fileApp.setAppend(true);
		fileApp.activateOptions();
		logger.addAppender(fileApp);
	}
	
	public static void debug(Object o) {
		logger.debug(o);
	}

	public static void warn(Object o) {
		logger.warn(o);
	}

	public static void error(Throwable t) {
		logger.error("Error", t);
	}

	public static void error(Object o) {
		logger.error(o);
	}
	
	public static void error(String s) {
		logger.error(s);
	}
	
	public static void debug(Object o, Throwable t) {
		logger.debug(o, t);
	}

	public static void warn(Object o, Throwable t) {
		logger.warn(o, t);
	}

	public static void error(Object o, Throwable t) {
		logger.error(o, t);
	}
	
	public static boolean isDebugMode() {
		return logger.isDebugEnabled();
	}
}