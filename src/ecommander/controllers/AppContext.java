package ecommander.controllers;

import ecommander.fwk.ServerLogger;
import ecommander.model.datatypes.DateDataType;
import ecommander.persistence.commandunits.DeleteComplex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Интерфейс для хранения всех сведений о работе приложения, в будущем, для влияния на работу всего приложения,
 * независимо от сеансов (например, выполнение запланизованных заданий)
 * @author EEEE
 */
public class AppContext {
	private static boolean CACHE_ENABLED;
	private static Locale LOCALE;
	private static String ITEM_NAMES_CLASS;
	private static String WELCOME_PAGE;
	private static String PROTOCOL_SCHEME;
	private static String TEST_HTTPS_HEADER;
	private static String TEST_HTTPS_HEADER_VALUE;
	private static boolean IS_HTTPS;
	private static String SERVER_NAME;
	private static int SERVER_PORT;
	private static boolean HAS_TEST_HTTPS_VALUE;
	
	private static String MAIN_XML_MODELS_DIR;
	private static String MAIN_DATA_MODEL_FILE;
	private static String USER_DATA_MODEL_FILE;
	private static String CACHE_HTML_FOLDER;
	private static String CACHE_XML_FOLDER;
	private static String LUCENE_INDEX_FOLDER;
	private static String GLOBAL_VARIABLES_FILE;
	private static String FILES_DIR;
	private static String PROTECTED_FILES_DIR;
	private static String PROTECTED_FILES_URL;
	private static String PAGESMODEL_FILE;
	private static String XSL_ROOT;
	private static String DOMAINS_FILE;
	private static String URL_FILE;
	private static String USERS_FILE;
	private static String LOG_FILE;
	private static String _REAL_BASE_PATH;

	private static Properties props = new Properties();

	private static final HashMap<String, String> FWK_PROPS = new HashMap<>(); // недоделанный аналог GLOBAL_PROPS
	private static final HashMap<String, String> GLOBAL_PROPS = new HashMap<>(); // переменные с произвольными названиями и значениями

	static void init(ServletContext servletContext) {
		String contextRoot = "";
		try {
			props.clear();
			props.load(servletContext.getResourceAsStream("/WEB-INF/settings.properties"));
			MAIN_XML_MODELS_DIR = props.getProperty("paths.rel_base_folder");
			MAIN_DATA_MODEL_FILE = props.getProperty("paths.data_model");
			USER_DATA_MODEL_FILE = props.getProperty("paths.data_model_custom");
			CACHE_HTML_FOLDER = props.getProperty("paths.cache_folder");
			CACHE_XML_FOLDER = props.getProperty("paths.cache_xml_folder");
			LUCENE_INDEX_FOLDER = props.getProperty("paths.lucene_index_folder");
			GLOBAL_VARIABLES_FILE = props.getProperty("paths.global_settings");
			FILES_DIR = props.getProperty("paths.files_folder");
			PROTECTED_FILES_DIR = "restrict_files/";
			PROTECTED_FILES_URL = "protected/";
			PAGESMODEL_FILE = props.getProperty("paths.pages_model");
			XSL_ROOT = props.getProperty("paths.styles_folder");
			DOMAINS_FILE = props.getProperty("paths.domains");
			URL_FILE = props.getProperty("paths.urls");
			USERS_FILE = props.getProperty("paths.users");

			CACHE_ENABLED = props.getProperty("settings.enable_cache").equalsIgnoreCase("yes");
			LOCALE = new Locale(props.getProperty("locale.language"), props.getProperty("locale.country"));
			ITEM_NAMES_CLASS = props.getProperty("generated.constants_class");
			WELCOME_PAGE = props.getProperty("url.welcome_page");
			PROTOCOL_SCHEME = props.getProperty("url.scheme", "http");
			TEST_HTTPS_HEADER = props.getProperty("url.https.test.header", "x-forwarded-proto");
			TEST_HTTPS_HEADER_VALUE = props.getProperty("url.https.test.value");
			HAS_TEST_HTTPS_VALUE = StringUtils.isNotBlank(TEST_HTTPS_HEADER_VALUE);
			IS_HTTPS = StringUtils.equalsIgnoreCase(PROTOCOL_SCHEME, "https");
			SERVER_NAME = props.getProperty("url.server_name", null);
			SERVER_PORT = NumberUtils.toInt(props.getProperty("url.server_port", "-1"), -1);

			// Настройки с префиксом fwk. нужны для различных прикладных случаев. Не для всех сайтов нужны одни и
			// те же настройки fwk. Поэтому они хранятся просто в HashMap
			for (String propertyName : props.stringPropertyNames()) {
				if (StringUtils.startsWith(propertyName, "fwk.")) {
					FWK_PROPS.put(propertyName, props.getProperty(propertyName, ""));
				}
			}
			// часовая зона
			try {
				DateDataType.setTimeZoneHourOffset(Integer.parseInt(props.getProperty("locale.hour_offset")));
			} catch (Exception e) { /**/ }
			
			contextRoot = props.getProperty("paths.context_root");
		} catch (Exception e) {/* значит строка не заполнена */}
		if (StringUtils.isBlank(contextRoot))
			_REAL_BASE_PATH = servletContext.getRealPath("") + "/";
		else
			_REAL_BASE_PATH = contextRoot;
		LOG_FILE = _REAL_BASE_PATH + "WEB-INF/log4j.properties";

		// Глобальные настройки. нужны для различных прикладных случаев. Не для всех сайтов нужны одни и
		// те же настройки fwk. Поэтому они хранятся просто в HashMap
		// Их можно также и задавать программно (в коде), они при этом сохраняются в файле и потом считваются при
		// перезапуске сервера
		if (StringUtils.isNotBlank(GLOBAL_VARIABLES_FILE)) {
			File globalSettingsFile = new File(getRealPath(GLOBAL_VARIABLES_FILE));
			try {
				if (globalSettingsFile.exists()) {
					List<String> lines = FileUtils.readLines(globalSettingsFile, StandardCharsets.UTF_8);
					for (String line : lines) {
						String name = StringUtils.normalizeSpace(StringUtils.substringBefore(line, "="));
						String value = StringUtils.normalizeSpace(StringUtils.substringAfter(line, "="));
						GLOBAL_PROPS.put(name, value);
					}
				}
			} catch (Exception e) {
				ServerLogger.error("Can not read global settings file " + GLOBAL_VARIABLES_FILE, e);
			}
		}

		// Удаления
		DeleteComplex.startDeletions();
	}

	public static String getContextPath() {
		return _REAL_BASE_PATH;
	}

	public static String getRealPath(String relPath) {
		Path path = Paths.get(relPath);
		if (!path.isAbsolute()) {
			path = Paths.get(_REAL_BASE_PATH, relPath);
		}
		if (path.toFile().isDirectory() || relPath.length() - StringUtils.lastIndexOf(relPath, ".") > 5) {
			return path.toString() + "/";
		}
		return path.toString();
	}
	
	public static String getMainModelPath() {
		return getRealPath(MAIN_XML_MODELS_DIR + MAIN_DATA_MODEL_FILE);
	}
	
	public static String getUserModelPath() {
		return getRealPath(MAIN_XML_MODELS_DIR + USER_DATA_MODEL_FILE);
	}
	
	public static String getCacheHtmlDirPath() {
		return getRealPath(CACHE_HTML_FOLDER);
	}

	public static String getCacheXmlDirPath() {
		return getRealPath(CACHE_XML_FOLDER);
	}
	
	public static String getLuceneIndexPath() {
		return getRealPath(LUCENE_INDEX_FOLDER);
	}
	
	public static String getCommonFilesDirPath() {
		return getRealPath(FILES_DIR);
	}

	public static String getProtectedFilesDirPath() {
		return getRealPath(PROTECTED_FILES_DIR);
	}

	public static String getFilesDirPath(boolean isProtected) {
		if (isProtected)
			return getProtectedFilesDirPath();
		return getCommonFilesDirPath();
	}

	public static String getCommonFilesUrlPath() {
		return FILES_DIR;
	}

	public static String getProtectedFilesUrlPath() {
		return PROTECTED_FILES_URL;
	}

	public static String getFilesUrlPath(boolean isProtected) {
		if (isProtected)
			return getProtectedFilesUrlPath();
		return getCommonFilesUrlPath();
	}

	public static String getFilePathByUrlPath(String fileUrl, boolean isProtected) {
		if (isProtected) {
			return getProtectedFilesDirPath() + StringUtils.substringAfter(fileUrl, getProtectedFilesUrlPath());
		} else {
			return getCommonFilesDirPath() + StringUtils.substringAfter(fileUrl, getCommonFilesUrlPath());
		}
	}

	public static String getPagesModelPath() {
		return getRealPath(MAIN_XML_MODELS_DIR + PAGESMODEL_FILE);
	}
	
	public static String getStylesDirPath() {
		return getRealPath(XSL_ROOT);
	}
	
	public static String getDomainsModelPath() {
		return getRealPath(MAIN_XML_MODELS_DIR + DOMAINS_FILE);
	}
	
	public static String getUrlFilePath() {
		return getRealPath(URL_FILE);
	}
	
	public static String getUsersPath() {
		return getRealPath(MAIN_XML_MODELS_DIR + USERS_FILE);
	}

	public static String getLogPropsPath() {
		return LOG_FILE;
	}

	public static String getModelPath() {
		return getRealPath(MAIN_XML_MODELS_DIR);
	}
	
	public static boolean isCacheEnabled() {
		return CACHE_ENABLED;
	}
	
	public static Locale getCurrentLocale() {
		return LOCALE;
	}
	
	public static String getItemNamesClassName() {
		return ITEM_NAMES_CLASS;
	}
	
	public static String getWelcomePageName() {
		return WELCOME_PAGE;
	}

	public static String getProtocolScheme() {
		return PROTOCOL_SCHEME;
	}

	public static String getTestHttpsHeader() {
		return TEST_HTTPS_HEADER;
	}

	public static String getTestHttpsHeaderValue() {
		return TEST_HTTPS_HEADER_VALUE;
	}

	public static boolean isHttpsProtocolScheme() {
		return IS_HTTPS;
	}

	public static String getProperty(String propertyName, String defaultValue) {
		return props.getProperty(propertyName, defaultValue);
	}

	public static boolean hasTestHttpsValue() {
		return HAS_TEST_HTTPS_VALUE;
	}

	/**
	 * Имя сервера для ссылок.
	 * Иногда не возможно взять его из HttpServletRequest
	 * @param req
	 * @return
	 */
	public static String getServerNamePort(HttpServletRequest req) {
		String serverName = StringUtils.isNotBlank(SERVER_NAME) ? SERVER_NAME : req.getServerName();
		int port = SERVER_PORT >= 0 ? SERVER_PORT : req.getServerPort();
		return serverName + (port == 80 || port == 443 ? "" : ":" + port);
	}

	public static String getServerName(HttpServletRequest req) {
		return StringUtils.isNotBlank(SERVER_NAME) ? SERVER_NAME : req.getServerName();
	}

	/**
	 * Различные настройки, которые специфичны для единичных сайтов, не имеют смысла в общем контексте приложения
	 * (не нужны для всех случаев)
	 * @param propName
	 * @return
	 */
	public static String getFwkProperty(String propName) {
		return FWK_PROPS.get(propName);
	}

	/**
	 * Получить глобальную настройку, уникальную для данного приложения (не актуальную для всех приложений)
	 * @param name
	 * @return
	 */
	public static String getGlobalVar(String name, String... defaultValue) {
		synchronized (GLOBAL_PROPS) {
			String value = GLOBAL_PROPS.get(name);
			if (StringUtils.isBlank(value)) {
				return defaultValue.length > 0 ? defaultValue[0] : value;
			}
			return value;
		}
	}

	/**
	 * Установить новую глобальную настройку.
	 * Все глобальные настройки сразу сохраняются в файл
	 * !!! Устанавливать глобальные настройки только изредка, т.к. они блокируются и производится запись в файл !!!
	 * @param name
	 * @param value
	 */
	public static void setGlobalVar(String name, String value) {
		synchronized (GLOBAL_PROPS) {
			if (StringUtils.isBlank(name)) {
				return;
			}
			if (StringUtils.isBlank(value)) {
				GLOBAL_PROPS.remove(name);
			} else {
				GLOBAL_PROPS.put(name, value);
			}
			ArrayList<String> lines = new ArrayList<>();
			for (String key : GLOBAL_PROPS.keySet()) {
				lines.add(key + "=" + GLOBAL_PROPS.get(key));
			}
			try {
				File file = new File(getRealPath(GLOBAL_VARIABLES_FILE));
				FileUtils.writeLines(file, lines);
			} catch (Exception e) {
				ServerLogger.error("Unable to save global variables to file " + GLOBAL_VARIABLES_FILE, e);
			}
		}
	}
}
