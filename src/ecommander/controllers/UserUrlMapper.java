package ecommander.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ServerLogger;
import ecommander.pages.variables.VariablePE;


/**
 * Класс для установления соответствия между URL, которые задал пользователь и URL, которые используются
 * системой управления.
 * Работает следующим образом:
 * 1. Преобразует CMS URL в User URL и наоборот (работает в две стороны)
 * 2. Сначала ищет полное соответствие входной строки выходной, если соотвтетсрвие не найдено, пункт 3.
 * 3. Делит входную строку на подстроки (по /) и ищет соответствие для каждой
 *    подстроки. Полученные строки соединяет при помощи тех же символов (/)
 *    но уже для другой системы урлов. Если соответсвие строк не найдено, пункт 4.
 * 4. Выводит файл html, который передался в строке (для юзера) или выводит обычную строку CMS URL (для цмс).
 * 
 * Формат файла - каждая строка состоит из двух частей, разделенных пробелом
 * Комментарий - символ # в начале строки
 * 
 * @author EEEE
 *
 */
public class UserUrlMapper {

	/************************************** СТАТИЧЕСКАЯ ЧАСТЬ *********************************************/
	
	//public static final String FILE_EXTENSION = ".html";
	//private static final int FILE_EXTENSION_LENGHT = FILE_EXTENSION.length();
	
	private static UserUrlMapper instance;
	
	private static UserUrlMapper getMapper() {
		if (instance == null)
			instance = new UserUrlMapper();
		return instance;
	}
	
	/************************************** ЧАСТЬ ЭКЗЕМПЛЯРА КЛАССА *********************************************/
	
	private Hashtable<String, String> userCmsTable;
	private Hashtable<String, String> cmsUserTable;
	private long fileModified = 0;
	private long fileChecked = 0;
	private boolean enabled = false;

	public UserUrlMapper() {
		checkFile();
	}
	/**
	 * Загружает данные из файла в 2 таблицы 
	 */
	private void loadFile() {
		Scanner fileScanner = null;
		try {
			if (StringUtils.isBlank(AppContext.getUrlFilePath()))
				return;
			File urlFile = new File(AppContext.getUrlFilePath());
			if (!urlFile.exists())
				return;
			userCmsTable = new Hashtable<String, String>();
			cmsUserTable = new Hashtable<String, String>();
			fileScanner = new Scanner(urlFile);
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine().trim();
				if (StringUtils.isBlank(line) || line.startsWith("#"))
					continue;
				int space = line.indexOf(' ');
				if (space == -1) space = line.indexOf('\t');
				if (space != -1) {
					String cmsUrl = line.substring(0, space).trim();
					String userUrl = line.substring(space).trim();
					userCmsTable.put(userUrl, cmsUrl);
					cmsUserTable.put(cmsUrl, userUrl);
					enabled = true;
				}
			}
		} catch (FileNotFoundException e) {
			ServerLogger.error("File with urls ('" + AppContext.getUrlFilePath() + "') was not found", e);
		} finally {
			if (fileScanner != null) fileScanner.close();
		}
	}
	/**
	 * Получает пользовательский URL по CMS URL
	 * @param cmsUrl
	 * @return
	 */
	private String getUserUrlInternal(String cmsUrl) {
		if (enabled) {
			if (cmsUserTable.containsKey(cmsUrl))
				return cmsUserTable.get(cmsUrl);// + FILE_EXTENSION;
			else {
				String[] parts = StringUtils.split(cmsUrl, VariablePE.COMMON_DELIMITER);
				if (parts.length > 0) {
					String result = new String();
					String userPart = cmsUserTable.get(parts[0]);
					if (userPart == null)
						return cmsUrl;
					result += userPart;
					for (int i = 1; i < parts.length; i++) {
						userPart = cmsUserTable.get(parts[i]);
						if (userPart == null)
							return cmsUrl;
						result += "/" + userPart;
					}
					return result;// + FILE_EXTENSION;
				}
				return cmsUrl;
			}
		}
		return cmsUrl;
	}
	/**
	 * Получает пользовательский URL по CMS URL
	 * @param cmsUrl
	 * @return
	 */
	public static String getUserUrl(String cmsUrl) {
		return getMapper().getUserUrlInternal(cmsUrl);
	}
	/**
	 * Получает CMS URL по пользовательскому URL
	 * @param userUrl
	 * @return
	 */
	private String getCmsUrlInternal(String userUrl) {
		checkFile();
		if (enabled) {
			// Удаление .html
			//userUrl = userUrl.substring(0, userUrl.length() - FILE_EXTENSION_LENGHT);
			if (userCmsTable.containsKey(userUrl))
				return userCmsTable.get(userUrl);
			else {
				String[] parts = StringUtils.split(userUrl, '/');
				if (parts.length > 0) {
					String result = new String();
					String cmsPart = userCmsTable.get(parts[0]);
					if (cmsPart == null)
						return userUrl;
					result += cmsPart;
					for (int i = 1; i < parts.length; i++) {
						cmsPart = userCmsTable.get(parts[i]);
						if (cmsPart == null)
							return userUrl;
						result += VariablePE.COMMON_DELIMITER + cmsPart;
					}
					return result;
				}
				return userUrl;
			}
		}
		return userUrl;
	}
	/**
	 * Получает CMS URL по пользовательскому URL
	 * @param userUrl
	 * @return
	 */
	public static String getCmsUrl(String userUrl) {
		return getMapper().getCmsUrlInternal(userUrl);
	}
	/**
	 * Проверяет, надо ли перезагружать файл каждые 5 секунд
	 */
	private void checkFile() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - fileChecked > 5000) {
			fileChecked = currentTime;
			File urlFile = new File(AppContext.getUrlFilePath());
			if (urlFile.lastModified() > fileModified) {
				fileModified = urlFile.lastModified();
				loadFile();				
			}
		}
	}
}