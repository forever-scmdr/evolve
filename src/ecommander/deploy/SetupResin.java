package ecommander.deploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Класс делает следующее:
 * 1. Считывает установленные пользователем настройки из файла.
 * 2. Создает нужные файлы настроек CMS
 * 3. Редактирует конфигурационный файл resin.
 * 4. Перезапускает resin.
 * 5. Создает структуру базы данных.
 * 6. Запускает урл, который создает модель данных и пользователей (meta?q=create_model, meta?q=create_users)
 * @author EEEE
 *
 */
public class SetupResin {
	private String contextRoot;
	private String siteDomain;
	private String databaseUrl;
	private String databaseSuperUser;
	private String databaseSuperPassword;
	
	private String newDatabaseName;
	private String newDatabaseUser;
	private String newDatabasePassword;
	
	private Properties permanentProps = new Properties();
	
    private static String NL = System.getProperty("line.separator");
	
	public SetupResin(String contextRoot) {
		this.contextRoot = contextRoot;
	}
	/**
	 * Считывание настроек из файла
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void initialize() throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(contextRoot + "/WEB-INF/setup/setup.properties"));
		permanentProps.load(new FileInputStream(contextRoot + "/WEB-INF/setup/templates/permanent.properties"));
		siteDomain = props.getProperty("site.domain");
		newDatabaseName = props.getProperty("database.databasename");
		newDatabaseUser = props.getProperty("database.username");
		newDatabasePassword = props.getProperty("database.userpassword");
		databaseUrl = permanentProps.getProperty("database.url");
		databaseSuperUser = permanentProps.getProperty("database.username");
		databaseSuperPassword = permanentProps.getProperty("database.userpassword");
	}
	/**
	 * Создание файлов или их редактирование
	 * @throws IOException
	 */
	public void createFiles() throws IOException {
		System.out.println();
		System.out.println("Creating and modifying files:");
		System.out.println();
		// Log4J
		if (permanentProps.getProperty("log4j.properties.relpath").trim().length() != 0) {
			System.out.print("Log4j...     ");
			String log4jFile = readFile(contextRoot + "/WEB-INF/setup/templates/log4j_properties.txt");
			log4jFile = log4jFile.replaceAll("<<context_root>>", contextRoot);
			writeFile(contextRoot + permanentProps.getProperty("log4j.properties.relpath"), log4jFile);
			System.out.println("OK");
		}
		// struts.properties
		if (permanentProps.getProperty("struts.properties.relpath").trim().length() != 0) {
			System.out.print("struts.properties...     ");
			String strutsPropertiesFile = readFile(contextRoot + "/WEB-INF/setup/templates/struts_properties.txt");
			strutsPropertiesFile = strutsPropertiesFile.replaceAll("<<context_root>>", contextRoot);
			writeFile(contextRoot + permanentProps.getProperty("struts.properties.relpath"), strutsPropertiesFile);
			System.out.println("OK");
		}
		// settings.properties
		if (permanentProps.getProperty("settings.properties.relpath").trim().length() != 0)	{
			System.out.print("settings.properties...     ");
			String settingsPropertiesFile = readFile(contextRoot + "/WEB-INF/setup/templates/settings_properties.txt");
			settingsPropertiesFile = settingsPropertiesFile.replaceAll("<<context_root>>", contextRoot);
			writeFile(contextRoot + permanentProps.getProperty("settings.properties.relpath"), settingsPropertiesFile);
			System.out.println("OK");
		}
		// resin-web.xml
		if (permanentProps.getProperty("resin-web.xml.relpath").trim().length() != 0) {
			System.out.print("resin-web.xml...     ");
			String resinWebXmlFile = readFile(contextRoot + "/WEB-INF/setup/templates/resin-web_xml.txt");
			resinWebXmlFile = resinWebXmlFile.replaceAll("!:database_url:!", databaseUrl + "/" + newDatabaseName);
			resinWebXmlFile = resinWebXmlFile.replaceAll("!:database_user:!", newDatabaseUser);
			resinWebXmlFile = resinWebXmlFile.replaceAll("!:database_password:!", newDatabasePassword);
			writeFile(contextRoot + permanentProps.getProperty("resin-web.xml.relpath"), resinWebXmlFile);
			System.out.println("OK");
		}
		// resin.conf
		if (permanentProps.getProperty("resin.conf.abspath").trim().length() != 0) {
			System.out.print("resin.conf...     ");
			String resinConfFragment = readFile(contextRoot + "/WEB-INF/setup/templates/resin_conf.txt");
			resinConfFragment = resinConfFragment.replaceAll("!:domain_name:!", siteDomain);
			resinConfFragment = resinConfFragment.replaceAll("!:context_root:!", contextRoot);
			String resinConfFile = readFile(permanentProps.getProperty("resin.conf.abspath"));
			String beginMarker = "    <!--" + siteDomain + "_begin-->";
			String endMarker = "<!--" + siteDomain + "_end-->" + NL;
			// Удаление старой информации
			if (resinConfFile.indexOf(beginMarker) > 0) {
				String firstPart = resinConfFile.substring(0, resinConfFile.indexOf(beginMarker));
				String secondPart = resinConfFile.substring(resinConfFile.indexOf(endMarker) + endMarker.length(), resinConfFile.length());
				resinConfFile = firstPart + secondPart;
			}
			// Добавление новой информации
			resinConfFile = resinConfFile.replaceAll("<!--EXPANDHERE-->", resinConfFragment);
			writeFile(permanentProps.getProperty("resin.conf.abspath"), resinConfFile);
			System.out.println("OK");
		}
	}
	/**
	 * Создает базу данных, таблицы и юзера
	 */
	public void createDatabase() {
		System.out.println();
		System.out.println("Creating database and it's user:");
		System.out.println();
		// Подключение
		Connection con = null;
		Statement stmt = null;
		try {
			if (newDatabaseName.trim().length() != 0) {
				System.out.print("Connecting to MySQL server...     ");
				Class.forName("org.gjt.mm.mysql.Driver").newInstance();
				con = DriverManager.getConnection(databaseUrl + "/mysql?allowMultiQueries=true&useUnicode=true&characterEncoding=utf8",
						databaseSuperUser, databaseSuperPassword);
				if(!con.isClosed())
					System.out.println("OK");
				stmt = con.createStatement();
				// Удалить базу данных
				System.out.print("Creating new database...     ");
				String sql = "DROP DATABASE IF EXISTS " + newDatabaseName;
				stmt.executeUpdate(sql);
				// Создать базу данных, если она еще не существует
				sql = "CREATE DATABASE IF NOT EXISTS " + newDatabaseName + " CHARACTER SET = utf8";
				stmt.executeUpdate(sql);
				System.out.println("OK");
				// Создать юзера
				System.out.print("Creating new user...     ");
				sql = "CREATE USER '" + newDatabaseUser + "'@'localhost' IDENTIFIED BY '" + newDatabasePassword + "'";
				try {
					stmt.executeUpdate(sql);
				} catch (SQLException sqle) {
					System.out.print("User '" + newDatabaseUser + "' may already exist. Proceed with granting privilegies...     ");
				}
				sql = "GRANT ALL ON " + newDatabaseName + ".* TO '" + newDatabaseUser + "'@'localhost'";
				stmt.executeUpdate(sql);
				System.out.println("OK");
				// Создать таблицы базы данных
				if (permanentProps.getProperty("database.createsql.relpath").trim().length() != 0) {
					System.out.print("Creating tables...     ");
					stmt.executeQuery("USE " + newDatabaseName);
					String sqlFile = readFile(contextRoot + permanentProps.getProperty("database.createsql.relpath"), "utf-8");
					stmt.executeUpdate(sqlFile);
//					String command = "mysqlimport --user=" + databaseSuperUser + " --password=" + databaseSuperPassword + " "
//							+ newDatabaseName + " " + contextRoot + permanentProps.getProperty("database.createsql.relpath");
//					System.out.println(command);
//					executeShellCommand(command);
					System.out.println("OK");
				}
			}
		} catch(Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if(con != null)
					con.close();
			} catch(SQLException e) {}
		}
	}
	/**
	 * Перезапускает резин и запускает команды http://{site}/meta?q=create_model и http://{site}/meta?q=create_users
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void initializeSite() throws IOException, InterruptedException {
		System.out.println();
		System.out.println("Site initialization:");
		System.out.println();
		// Перезапустить resin
		System.out.print("Restarting resin...     ");
		executeShellCommand("service resin stop");
		executeShellCommand("service resin start");
		System.out.println("OK");
		// Создать структуру базы данных и пользователей
		if (permanentProps.getProperty("database.createsql.relpath").trim().length() != 0) {		
			System.out.print("Creating site data model...     ");
			executeShellCommand("wget http://" + siteDomain + "/meta?q=create_model -O -");
			System.out.println("OK");
			System.out.print("Creating site administrators...     ");
			executeShellCommand("wget http://" + siteDomain + "/meta?q=create_users -O -");
			System.out.println("OK");
		}
	}
	/**
	 * Чтение из файла в строку
	 * @param fileName
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	private String readFile(String fileName, String encoding) throws IOException {
		FileInputStream fis = new FileInputStream(new File(fileName));
		InputStreamReader isr = null;
		if (encoding != null)
			isr = new InputStreamReader(fis, encoding);
		else 
			isr = new InputStreamReader(fis);
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		StringBuilder result = new StringBuilder();
		if ((line = reader.readLine()) != null)
			result.append(line);
		while ((line = reader.readLine()) != null){
			result.append(NL);
			result.append(line);
		}
		reader.close();
		return result.toString();
	}
	/**
	 * Чтение из файла в строку
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private String readFile(String fileName) throws IOException {
		return readFile(fileName, null);
	}
	/**
	 * Запись строки в файл
	 * @param fileName
	 * @param text
	 * @throws IOException
	 */
	private void writeFile(String fileName, String text) throws IOException  {
		Writer out = new OutputStreamWriter(new FileOutputStream(fileName));
		try {
			out.write(text);
		}
		finally {
			out.close();
		}
	}
	/**
	 * Выполняет команду shell
	 * @param command
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void executeShellCommand(String command) throws IOException, InterruptedException {
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec(command) ;
		pr.waitFor() ;
	}
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		SetupResin sr = new SetupResin(args[0]);
		try {
			System.out.println("START SETUP");
			sr.initialize();
			sr.createFiles();
			sr.createDatabase();
			System.out.println();
			System.out.println("SETUP SUCCESSFUL");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
			System.out.println("AN ERROR HAS OCCURED. SETUP WAS NOT SUCCESSFUL");
		}
	}
}