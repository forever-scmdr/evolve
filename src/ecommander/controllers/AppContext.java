package ecommander.controllers;

import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.datatypes.DateDataType;

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
	
	private static String MAIN_XML_MODELS_DIR;
	private static String MAIN_DATA_MODEL_FILE;
	private static String USER_DATA_MODEL_FILE;
	private static String CACHE_HTML_FOLDER;
	private static String CACHE_XML_FOLDER;
	private static String LUCENE_INDEX_FOLDER;
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
	
	static void init(ServletContext servletContext) {
		String contextRoot = "";
		try {
			Properties props = new Properties();
			props.load(servletContext.getResourceAsStream("/WEB-INF/settings.properties"));
			MAIN_XML_MODELS_DIR = props.getProperty("paths.rel_base_folder");
			MAIN_DATA_MODEL_FILE = props.getProperty("paths.data_model");
			USER_DATA_MODEL_FILE = props.getProperty("paths.data_model_custom");
			CACHE_HTML_FOLDER = props.getProperty("paths.cache_folder");
			CACHE_XML_FOLDER = props.getProperty("paths.cache_xml_folder");
			LUCENE_INDEX_FOLDER = props.getProperty("paths.lucene_index_folder");
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
	}

	public static String getContextPath() {
		return _REAL_BASE_PATH;
	}
	
	public static String getRealPath(String relPath) {
		return _REAL_BASE_PATH + relPath;
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
}
