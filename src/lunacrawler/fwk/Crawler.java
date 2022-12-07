package lunacrawler.fwk;

import ecommander.fwk.*;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.naming.NamingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Сам краулер, который создает потоки и фактически выполняет всю работу по поиску и скачиванию урлов
 */
public class Crawler implements DBConstants.Parse {
	public static final byte NOT_CHECKED = 0;
	public static final byte SUCCESS_SAVED = 1;
	public static final byte SUCCESS_NOT_SAVED = 2;
	public static final byte SITEMAP_NOT_CHECKED = 3;
	public static final byte SITEMAP_SUCCESS = 4;
	public static final byte SITEMAP_ERROR = 9;
	public static final byte ERROR = 10;

	private static class Url {
		private final long serial;
		private final String url;
		private String fileName;
		private byte status;
		private String comment;

		public Url(long serial, String url, String fileName, byte status, String comment) {
			this.serial = serial;
			this.url = url;
			this.fileName = fileName;
			this.status = status;
			this.comment = comment;
		}

		public Url(String url) {
			this.url = url;
			this.serial = 0;
			this.status = (byte) 0;
			this.fileName = "";
			this.comment = "";
		}

		public Url(String url, byte status) {
			this.url = url;
			this.serial = 0;
			this.status = status;
			this.fileName = "";
			this.comment = "";
		}

		@Override
		public String toString() {
			return "{" + status + ", " + url + "}";
		}
	}


	private final Set<String> urlsInProgress = Collections.synchronizedSet(new HashSet<>());
	private final int politenessMs;

	private volatile boolean shouldContinue = true;
	private volatile boolean hasMoreUrls = true;
	private final URI base;
	private final Map<Integer, Boolean> threadHadUrls = Collections.synchronizedMap(new HashMap<>());
	private final int threadCount;
	private Collection<String> seedUrls;
	private final String filesSaveDir;
	private volatile LinkedList<String> proxies = null;
	private CrawlerController controller;


	private volatile int totalFoundUrls = 0;
	private volatile int totalVisitedUrls = 0;
	private volatile int minuteVisitedUrls = 0;
	private volatile long minuteStarted = 0;
	private volatile int visitsPerMinute = 0;


	Crawler(int politenessMs, URI base, int threadCount, Collection<String> seedUrls, String filesSaveDir, CrawlerController controller) {
		this.politenessMs = politenessMs;
		this.base = base;
		this.threadCount = threadCount;
		this.seedUrls = seedUrls;
		this.filesSaveDir = filesSaveDir;
		this.controller = controller;
	}

	/**
	 * Задать список прокси серверов
	 * @param proxies
	 */
	public synchronized void setProxies(LinkedList<String> proxies) {
		this.proxies = proxies;
	}

	/**
	 * Стартовать все потоки краулера
	 * @throws InterruptedException
	 */
	void start() throws InterruptedException {

		// Проверка - обычный парсинг или парсинг sitemap.xml
		String testSeed = seedUrls.iterator().next();
		boolean isSitemapCrawl = StringUtils.containsIgnoreCase(testSeed, "sitemap") && StringUtils.containsIgnoreCase(testSeed, ".xml");

		// Сохранение сидов
		TemplateQuery insert = new TemplateQuery("insert url");
		for (String seed : seedUrls) {
			Url seedUrl = new Url(seed);
			if (isSitemapCrawl) {
				seedUrl.status = SITEMAP_NOT_CHECKED;
			}
			buildSaveUrlQuery(insert, seedUrl);
			getInfo().pushLog("Seed {} added", seed);
		}
		insert.sql(" ON DUPLICATE KEY UPDATE " + PR_URL + " = VALUES(" + PR_URL + ")");
		try (Connection conn = MysqlConnector.getConnection();
		     PreparedStatement ps = insert.prepareQuery(conn)) {
			ps.executeUpdate();
		} catch (Exception e) {
			ServerLogger.error("SQL error", e);
			getInfo().addError(e);
			return;
		}

		totalVisitedUrls = 0;
		minuteVisitedUrls = 0;
		minuteStarted = System.currentTimeMillis();

		// Если надо обрабатывать sitemap.xml
		threadHadUrls.clear();
		hasMoreUrls = true;
		if (isSitemapCrawl) {
			// Сначала запустить все потоки скачки сайтмапа
			ArrayList<Thread> sitemapThreads = new ArrayList<>();
			for (int i = 0; i < threadCount; i++) {
				threadHadUrls.put(i, true);
				Thread sitemapThread = new Thread(new GetSitemapUrlsThread(i));
				sitemapThread.setDaemon(true);
				sitemapThread.start();
				getInfo().pushLog("Starting Sitemap Processor #{}", i);
				sitemapThreads.add(sitemapThread);
				Thread.sleep(politenessMs + Math.round(Math.random() * politenessMs));
			}
			// Потом надо подождать пока они все завершатся
			for (Thread sitemapThread : sitemapThreads) {
				sitemapThread.join();
			}
			getInfo().pushLog("");
			getInfo().pushLog("#################");
			getInfo().pushLog("Sitemap parsing is finished");
			getInfo().pushLog("#################");
			getInfo().pushLog("");
		}

		// Если надо просто обходить сайт
		threadHadUrls.clear();
		hasMoreUrls = true;
		for (int i = 0; i < threadCount; i++) {
			threadHadUrls.put(i, true);
			Thread crawlThread = new Thread(isSitemapCrawl ? new SitemapCrawlThread(i) : new DirectCrawlThread(i));
			crawlThread.setDaemon(true);
			crawlThread.start();
			getInfo().pushLog("Starting Crawler #{}", i);
			Thread.sleep(politenessMs + Math.round(Math.random() * politenessMs));
		}
	}



	/**********************************************************************************************************
	 **********************************************************************************************************
	 *
	 *                                    Класс DirectCrawlThread
	 *
	 **********************************************************************************************************
	 **********************************************************************************************************
	 *
	 *
	 *
	 * Поток простого карулера.
	 * Предназначен для прямого парсинга сайта путем перебора всех ссылок на всех страницах сайта начиная с сидов
	 */
	private class DirectCrawlThread implements Runnable {

		public final int id;
		public byte notCheckedUrlStatus = NOT_CHECKED;
		public byte errorUrlStatus = ERROR;

		public DirectCrawlThread(int id) {
			this.id = id;
		}

		@Override
		public void run() {
			getInfo().setOperation("Обход сайта, поиск ссылок и скачивание страниц");
			while (shouldContinue && hasMoreUrls) {
				try {
					boolean thereAraUrlsInProgress = false;
					//long startMillis = System.currentTimeMillis();
					Url nextUrl = loadNextUrl(notCheckedUrlStatus);
					if (nextUrl == null) {
						nextUrl = loadNextUrl(errorUrlStatus);
					}
					if (nextUrl != null) {
						synchronized (urlsInProgress) {
							if (urlsInProgress.contains(nextUrl.url)) {
								continue;
							}
							urlsInProgress.add(nextUrl.url);
						}
						processUrl(nextUrl);
						urlsInProgress.remove(nextUrl.url);
						hasNewUrls(this, true);
					} else {
						hasNewUrls(this, false);
					}
					/*
					long millisForProcess = Math.abs(System.currentTimeMillis() - startMillis);
					if (millisForProcess < politenessMs)
						Thread.sleep(politenessMs - millisForProcess);

					 */
					Thread.sleep(politenessMs);
				} catch (Exception e) {
					ServerLogger.error("SQL exception", e);
				}
			}
			getInfo().pushLog("Crawler thread {} finished crawling", id);
		}

		/**
		 * Обработать один урл
		 * Сохранить файл, если надо
		 * Сохранить в БД все урлы со страницы
		 * @param url
		 * @return - если найдены новые урлы, возвращается true
		 */
		protected boolean processUrl(Url url) {
			boolean urlsFound = false;
			try {
//				org.jsoup.Connection con = Jsoup.connect(url.url).timeout(10000);
//				setProxyIfHadSome(con);
//				Document doc = con.get();St
				String html = OkWebClient.getInstance().getString(url.url);
				if (StringUtils.isNotBlank(html)) {
					Document doc = JsoupUtils.parseXml(html);
					doc.setBaseUri(base.toString());
					String template = controller.getStyleForUrl(url.url);

					// Дальнейшая обработка только для урлов, соответсвующих шаблонам в файле urls.txt
					if (template != null) {

						// Если надо сохранять эту страницу, то сохранить ее файл
						if (!StringUtils.equalsIgnoreCase(template, CrawlerController.NO_TEMPLATE)) {
							doc.body().attr("source", url.url);

							// Проверить заблокировано или нет
							String title = StringUtils.trim(doc.title());
							if (StringUtils.equalsIgnoreCase(title, "Blocked") || StringUtils.containsIgnoreCase(title, "Site Connectivity Issues")) {
								throw new Exception("BLOCKED");
							}

							// Удалить ненужное
							deleteInsubstancialData(doc);

							// Сохранить в файле
							saveDocToZipFile(doc, url);

							url.status = SUCCESS_SAVED;
						} else {
							url.status = SUCCESS_NOT_SAVED;
						}

						// Пройтись по всем ссылкам страницы и сохранить их в БД
						urlsFound = saveAllUrlsToDB(doc, url);

						updateUrl(url);
						ServerLogger.debug("CRAWL UPDATE URL " + url.url);
					}
					countAndInformVisited("#" + id, url.url);
				} else {
					throw new Exception("HTTP CONNECTIVITY ERROR: " + "empty"/*con.response().statusCode() + " " + con.response().statusMessage()*/);
				}
			} catch (Exception e) {
				StringWriter writer = new StringWriter();
				PrintWriter out = new PrintWriter(writer);
				e.printStackTrace(out);
				url.comment = writer.toString();
				url.status = ERROR;
				try {
					updateUrl(url);
				} catch (Exception ex) {
					ServerLogger.error("MYSQL error", ex);
				}
				ServerLogger.error("Connection error", e);
				getInfo().pushLog("Crawler {} - URL ERROR {} for {}", "#" + id, e.getLocalizedMessage(), url.url);
			}
			return urlsFound;
		}

		/**
		 * Удалить несущественные элементы
		 * @param doc
		 */
		protected void deleteInsubstancialData(Document doc) {
			/*
			doc.select("script").remove();
			doc.select("header").remove();
			doc.select("div[id=header__storage]").remove();
			doc.select("div[id=settingsModal]").remove();
			doc.select("div[data-testid=footer-test]").remove();

			 */
		}

		/**
		 * Сохранить документ урла в хаархивированный файл
		 * @param doc
		 * @param url
		 * @throws Exception
		 */
		protected void saveDocToZipFile(Document doc, Crawler.Url url) throws Exception {
			String result = JsoupUtils.outputHtmlDoc(doc);
			String fileName = url.url;
			try {
				String strUrl = URLDecoder.decode(url.url, "UTF-8");
				fileName = Strings.createFileName(strUrl);
				Files.createDirectories(Paths.get(filesSaveDir));
				// Если результат парсинга урла не пустая строка - записать этот результат в файл
				if (!StringUtils.isBlank(result)) {
					String dirName = filesSaveDir + getUrlDirName(url.url);
					Files.createDirectories(Paths.get(dirName));
					File compressedFile = Paths.get(dirName + fileName).toFile();
					ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(compressedFile));
					ZipEntry e = new ZipEntry(fileName + ".html");
					zout.putNextEntry(e);
					byte[] data = result.getBytes(StandardCharsets.UTF_8);
					zout.write(data, 0, data.length);
					zout.closeEntry();
					zout.close();

					//Files.write(Paths.get(resultTempSrcDir + fileName), result.getBytes(UTF_8));
				}
			} catch (Exception e) {
				throw new Exception("FILE SAVE ERROR: " + fileName + " crawler #" + id);
			}
		}

		/**
		 * Сохранить все ссылки со страницы в БД
		 * @param doc
		 * @param url
		 * @return
		 * @throws MalformedURLException
		 * @throws SQLException
		 * @throws NamingException
		 */
		protected boolean saveAllUrlsToDB(Document doc, Crawler.Url url) throws MalformedURLException, SQLException, NamingException {
			boolean urlsFound = false;

			// Далее берем все ссылки и добавляем их в БД
			TemplateQuery insert = new TemplateQuery("insert url");
			for (Element link : doc.select("a[href]")) {

				// Преобразовать урл в нормальный формат (добавить base)
				String linkUrlStr = link.attr("abs:href");

				// Пропустить все ссылки на другие домены
				if (isUrlAbsolute(linkUrlStr)) {
					URL testUrl = new URL(linkUrlStr);
					if (!StringUtils.equalsIgnoreCase(testUrl.getHost(), base.getHost()))
						continue;
				} else {
					continue;
				}

				// Сохранить новый урл (только в том случае если он нужен)
				if (controller.getStyleForUrl(linkUrlStr) != null) {
					urlsFound = true;
					Url urlToSave = new Url(linkUrlStr);
					buildSaveUrlQuery(insert, urlToSave);
				}
			}
			// Сохранение в БД
			if (urlsFound) {
				insert.sql(" ON DUPLICATE KEY UPDATE " + PR_URL + " = VALUES(" + PR_URL + ")");
				try (Connection conn = MysqlConnector.getConnection();
				     PreparedStatement ps = insert.prepareQuery(conn)) {
					ps.executeUpdate();
				}
			} else {
				getInfo().pushLog("NO URLS FOUND: {}", url.url);
			}
			return urlsFound;
		}

		/**
		 * Добавить прокси для подключения
		 * @param connection
		 */
		protected void setProxyIfHadSome(org.jsoup.Connection connection) {
			if (CollectionUtils.isEmpty(proxies))
				return;
			int index = (int) Math.floor(Math.random() * proxies.size());
			String proxy = proxies.get(index);
			String actualProxy = StringUtils.split(proxy, ' ')[0];
			String[] parts = StringUtils.split(actualProxy, ':');
			if (parts.length > 1) {
				// Изменение настроек контроллера ,создание нового объекта PageFetcher
				String address = parts[0];
				int port = Integer.parseInt(parts[1]);
				connection.proxy(address, port);
			}
		}

	}



	/**********************************************************************************************************
	 **********************************************************************************************************
	 *
	 *                                    Класс SitemapCrawlThread
	 *
	 **********************************************************************************************************
	 **********************************************************************************************************
	 *
	 *
	 *
	 * Поток для парсинга урлов, найденных в sitemap.xml, т.е. для парсинга фактических страниц сайта
	 * Отличается тем, что просто не сохраняет ссылки со страницы в БД
	 */
	private class SitemapCrawlThread extends DirectCrawlThread {

		public SitemapCrawlThread(int id) {
			super(id);
		}

		@Override
		protected boolean saveAllUrlsToDB(Document doc, Url url) {
			return false;
		}
	}




	/**********************************************************************************************************
	 **********************************************************************************************************
	 *
	 *                                    Класс SitemapCrawlThread
	 *
	 **********************************************************************************************************
	 **********************************************************************************************************
	 *
	 *
	 *
	 * Поток для парсинга файлов вида sitemap.xml (не самих страниц сайта)
	 * Из них сохраняются ссылки на страницы и ссылки на другие файлы sitemap в БД
	 * Ничего другого не происходит
	 */
	private class GetSitemapUrlsThread extends DirectCrawlThread {

		public GetSitemapUrlsThread(int id) {
			super(id);
			this.notCheckedUrlStatus = SITEMAP_NOT_CHECKED;
			this.errorUrlStatus = SITEMAP_ERROR;
		}

		@Override
		protected boolean processUrl(Url url) {
			boolean urlsFound = false;
			try {
				//String xml = WebClient.getString(url.url);
				//String xml = FluentWebClient.getString(url.url);
				String xml = OkWebClient.getInstance().getString(url.url);
				if (StringUtils.isNotBlank(xml)) {
					Document doc = JsoupUtils.parseXml(xml);

//				org.jsoup.Connection con = Jsoup.connect(url.url).timeout(10000);
//				setProxyIfHadSome(con);
//				Document doc = con.get();
//				if (con.response().statusCode() == 200) {
					url.status = SITEMAP_SUCCESS;
					TemplateQuery insert = new TemplateQuery("insert url");
					for (Element link : doc.select("sitemap")) {
						// Преобразовать урл в нормальный формат (добавить base)
						String linkUrlStr = StringUtils.normalizeSpace(link.selectFirst("loc").ownText());
						Url urlToSave = new Url(linkUrlStr, SITEMAP_NOT_CHECKED);
						buildSaveUrlQuery(insert, urlToSave);
						urlsFound = true;
					}
					for (Element link : doc.select("url")) {
						// Преобразовать урл в нормальный формат (добавить base)
						String linkUrlStr = StringUtils.normalizeSpace(link.selectFirst("loc").ownText());
						String urlStyle = controller.getStyleForUrl(linkUrlStr);
						if (urlStyle != null && !urlStyle.equals(CrawlerController.NO_TEMPLATE)) {
							Url urlToSave = new Url(linkUrlStr);
							buildSaveUrlQuery(insert, urlToSave);
						}
					}
					if (!insert.isEmpty()) {
						try (Connection conn = MysqlConnector.getConnection();
						     PreparedStatement ps = insert.prepareQuery(conn)) {
							ps.executeUpdate();
						} catch (Exception e) {
							ServerLogger.error("SQL error", e);
							getInfo().addError(e);
						}
					}
					updateUrl(url);
					countAndInformVisited("#" + id, url.url);
				} else {
					//throw new Exception("HTTP CONNECTIVITY ERROR: " + con.response().statusCode() + " " + con.response().statusMessage());
					throw new Exception("HTTP CONNECTIVITY ERROR");
				}
			} catch (Exception e) {
				StringWriter writer = new StringWriter();
				PrintWriter out = new PrintWriter(writer);
				e.printStackTrace(out);
				url.comment = writer.toString();
				url.status = SITEMAP_ERROR;
				try {
					updateUrl(url);
				} catch (Exception ex) {
					ServerLogger.error("MYSQL error", ex);
				}
				ServerLogger.error("Connection error", e);
				getInfo().pushLog("Crawler {} - URL ERROR {} for {}", "#" + id, e.getLocalizedMessage(), url.url);
			}
			return urlsFound;
		}
	}


	public void stopCrawling() {
		shouldContinue = false;
	}

	/**
	 * Загрузить следующий по очереди урл нужного статуса
	 * (сначала загружаются новые урлы, потом пробуются вновь неудачно загруженные)
	 * @param status
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	private Url loadNextUrl(byte status) throws SQLException, NamingException {
		TemplateQuery select = new TemplateQuery("select urls");
		select.SELECT("*").FROM(PARSE_TBL).WHERE().col(PR_STATUS).byte_(status).ORDER_BY(PR_SERIAL).LIMIT(threadCount);
		ArrayList<Url> urls = new ArrayList<>();
		try (Connection conn = MysqlConnector.getConnection();
		     PreparedStatement ps = select.prepareQuery(conn)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				urls.add(new Url(rs.getLong(PR_SERIAL), rs.getString(PR_URL), rs.getString(PR_FILENAME), rs.getByte(PR_STATUS), rs.getString(PR_COMMENT)));
			}
		}
		for (Url url : urls) {
			if (!urlsInProgress.contains(url.url))
				return url;
		}
		return null;
	}

	/**
	 * Добавить к запросу новую строку для нового урла
	 * @param query
	 * @param url
	 * @return
	 */
	private TemplateQuery buildSaveUrlQuery(TemplateQuery query, Url url) {
		if (query.isEmpty()) {
			query.INSERT_INTO(PARSE_TBL, PR_URL, PR_STATUS).sql(" VALUES ");
		} else {
			query.com();
		}
		query.sql("(").string(url.url).sql(", ").byte_(url.status).sql(")");
		return query;
	}

	/**
	 * Обновить статус, комментарий (ошибку) или файл
	 * @param url
	 * @throws SQLException
	 * @throws NamingException
	 */
	private void updateUrl(Url url) throws SQLException, NamingException {
		TemplateQuery update = new TemplateQuery("update url");
		update.UPDATE(PARSE_TBL).SET()
				.col(PR_FILENAME).string(url.fileName)._col(PR_STATUS).byte_(url.status)._col(PR_COMMENT).string(url.comment)
				.WHERE().col(PR_URL).string(url.url);
		try (Connection conn = MysqlConnector.getConnection();
		     PreparedStatement ps = update.prepareQuery(conn)) {
			ps.executeUpdate();
		}
	}


	/**
	 * Отметка, были ли новые урлы в процессе разбора страницы.
	 * Если были, то продолжать разбор
	 * Если во всех потоках закончились новые урлы, то завершать разбор
	 * @param thread
	 * @param hasUrls
	 */
	private synchronized void hasNewUrls(DirectCrawlThread thread, boolean hasUrls) {
		threadHadUrls.put(thread.id, hasUrls);
		boolean anyHadUrls = false;
		for (Boolean hadUrls : threadHadUrls.values()) {
			anyHadUrls |= hadUrls;
		}
		hasMoreUrls = anyHadUrls;
	}


	private boolean isUrlAbsolute(String url) {
		return url.startsWith("http://") || url.startsWith("https://");
	}

	/**
	 * Подсчитать посещенные урлы и вывести в лог
	 * @param url
	 */
	private synchronized void countAndInformVisited(String crawlerId, String url) {
		totalVisitedUrls++;
		minuteVisitedUrls++;
		long now = System.currentTimeMillis();
		if (Math.abs(now - minuteStarted) > 60000) {
			visitsPerMinute = minuteVisitedUrls;
			minuteStarted = now;
			minuteVisitedUrls = 0;
			TemplateQuery foundQuery = new TemplateQuery("found query");
			foundQuery.SELECT("COUNT(" + PR_SERIAL + ")").FROM(PARSE_TBL);
			try (Connection conn = MysqlConnector.getConnection();
			     PreparedStatement ps = foundQuery.prepareQuery(conn)) {
				ResultSet rs = ps.executeQuery();
				if (rs.next())
					totalFoundUrls = rs.getInt(1);
			} catch (Exception e) {
				ServerLogger.error("SQL error", e);
				getInfo().addError(e);
			}
		}
		getInfo().setProcessed(totalVisitedUrls);
		getInfo().setToProcess(totalFoundUrls);
		getInfo().setLineNumber(visitsPerMinute);
		getInfo().pushLog("Crawler: {}; Visited: {}; Found: {}; VPM: {};  URL: {}", crawlerId, totalVisitedUrls, totalFoundUrls, visitsPerMinute, url);
	}


	private IntegrateBase.Info getInfo() {
		return controller.getInfo();
	}

	public static String getUrlDirName(String url) {
		return (Math.abs(url.hashCode()) % 1000) + "/";
	}
}
