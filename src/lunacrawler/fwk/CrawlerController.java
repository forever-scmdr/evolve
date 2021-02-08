package lunacrawler.fwk;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ecommander.fwk.*;
import edu.uci.ics.crawler4j.url.WebURL;
import lunacrawler.UrlModifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import ecommander.controllers.AppContext;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import net.sf.saxon.TransformerFactoryImpl;
/**
 * В стилях самостоятельные сущности должны содержать атрибут ID. По этому ID они будут идентифицироваться на разных страницах
 * одного сайта. Поэтому ID должен получаться как часть URL, ведущего на страницу
 *


 Правила создания результирующего файла


 Каждая страница сайта преобразуется в отдельный XML документ с помощью соответствующего файла стилей, который
 выбирается на основе информации из urls.txt, далее - страничный документ.

 Этот страничный документ состоит из элементов, соответствующих смыслу страницы, например product для страницы
 товара. Каждый такой элемент содержит любые вложенные элементы, например price для цены или description.

 Для того, чтобы можно было собирать информацию об одном и том же объекте (например, товаре) с разных страниц
 сайта (например, страница описания, страница фотогалереи, страница сравнения цен), этот объект должен быть
 уникально обозначен. Поэтому для каждого такого элемента вводится атрибут id, в котором должен содержаться
 уникальный идентификатор объекта, общий для всех страниц, где присутствует этот объект. Обычно это часть URL,
 содержащая ID объекта в базе данных сайта. Таким образом, каждый обособленный элемент страничного документа
 (например, товар), должен содержать атрибут id, одинаковый для всех страничных документов,
 посвященных соответсвующему объекту (пример: <product id="{tokenize(@id,'-')[position() = last()]}").

 Важно! При сохранении исходного html страниц с сайта в тэг body добавляется атрибут source, где хранится URL
 этой страницы. Его можно использовать при XSL преобразовании в частности для того, чтобы вычислить уникальный
 id объекта.

 При добавлении страничного документа в результирующий документ происходит поиск в результирующем документе
 всех элементов, id которых содержатся также и в текущем страничном документе. Если такие элементы найдены,
 то информация из элементов страничного документа просто дописывается в них. При этом могут возникнуть
 ситуации, когда некоторые части элементов из страничного документа уже пристутствуют в результирующем
 документе. При возникновении таких ситуаций можно выбрать один из трех вариантов действий:
 дописывать информацию, ничего не делать, дописывать информацию, если она отличается от содержащейся.
 Например, из нескольких страниц поступают разделы каталога с названием. Сами разделы создаются только один
 раз, когда первый раз встречается их id. А их названия должны указываться только один раз. Поэтому создается
 элемент <section> - раздел. В него вложен элемент (параметр) <name>, в котором указывается название раздела.
 Чтобы название добавлялось только один раз, в элемент name добавляется атрибут action со значением ignore.
 (<name action="ignore">...</name>). Значения элемента action:
 ignore - ничего не делать
 append - дописывать
 append-if-differs - допичвать в случае, если такого значения еще нет в родительском элементе

 В результате создаются файлы - по одному на каждый созданный в результате парсинга элемент (по одному на каждый
 раздел, товар, и т.д.) В каждом таком файле содержатся все полученные и собранные по описанным выше правилам
 сведения в виде XML. Также создается один файл, представляющий иерархию вложенности элементов. Он назвается
 hierarchy.xml. В нем в виде дерева представлена вложенность элементов с указанием тэга элемента в его ID. Например
 <section id="1">
    <section id="2">
        <product id="3"/>
        <product id="4"/>
        <product id="5"/>
    </section>
	 <section id="6">
		 <product id="7"/>
		 <product id="8"/>
		 <product id="9"/>
	 </section>
 </section>
 <section id="10">
 ...
 </section>

 Для создания такого дерева нужно чтобы каждый объект, который куда-то вложен, содержал ссылку на непосредственного
 родителя в виде тэга h_parent. Этот тэг содержит атрибут parent с уникальным идентификатором
 родителя этого объекта (например, разделы каталога, в которые вложен товар). Если элемент не должен быть никуда вложен,
 в нем элемент parent отсутствует. Также элемент h_parent содержит атрибут element, который содержит навзание
 элемента, в который должет быть вложен текущий элемент.

 Скачка файлов
 Если надо скачать некоторый файл или картинку, то к элементу добавляется атрибут download="URL", где URL -
 URL файла или картинки.

 Пример:

 <xsl:template match="/">

 // Должен быть один корневой элемент и он должен обязательно называться result
 <result>

	 // Выбор всех дивов, содержащих описания товаров (применение для них шаблонов)
	 <xsl:apply-templates select="//div[contains(@id, 'catalog-item-')]"/>

	 // Для всех ul, содержаших названия разделов, создать элементы section c соответствующим названием
	 // (вложенным элементом name) и id равным урлу страницы раздела
	 <xsl:for-each select="//ul[contains(@class, 'breadcrumb-navigation')]/li[position() &gt; 1]/a">
		 <section id="{@href}">
		    <name action="ignore"><xsl:value-of select="span"/></name>
		 </section>
	 </xsl:for-each>
 </result>
 </xsl:template>

 <xsl:template match="div">

	 // Запись продукта с вложенными элементами - параметрами продукта
	 // генерация id продукта на базе имеющейся на странице информации
	 <product id="{tokenize(@id,'-')[position() = last()]}_opt">
		 <name><xsl:value-of select=".//div[contains(@class, 'el-title')]/div[contains(@class, 'text')]/h1"/></name>
		 <brand><xsl:value-of select=".//div[contains(@class, 'el-title')]/div[contains(@class, 'text')]/h2/a"/></brand>
		 <short>

			 // Открытие и закрытие xml сущностей (CDATA) для вывода размеченного html текста
			 <xsl:call-template name="CDATA_START"/>
			 <xsl:value-of select=".//div[contains(@class, 'el-offers')]//div[contains(@class, 'inner')]/*"/>
			 <xsl:call-template name="CDATA_END"/>
		 </short>
		 <description>
			 <xsl:call-template name="CDATA_START"/>
			 <xsl:copy-of select=".//div[contains(@class, 'el-information')]/div[contains(@class, 'description')]/*"/>
			 <xsl:call-template name="CDATA_END"/>
		 </description>

		 // Картинка, которая должна быть скачана
		 // url для скачивания предоставляется в атрибуте элемента download
		 <picture download="http://sportoptovik.ru{.//div[contains(@class, 'el-preview')]/img/@src}">main.jpg</picture>

		 // Добавление элементов h_parent для корректной вставки в иерархию
		 // Иерархия создается в первом шаблоне этого примера
		 <xsl:for-each select="//ul[contains(@class, 'breadcrumb-navigation')]/li[position() &gt; 1]/a">
			 <h_parent parent="{@href}" element="section"/>
		 </xsl:for-each>
	 </product>
 </xsl:template>

 *
 * @author E
 *
 */
public class CrawlerController {

	public static final String ID = "id";
	public static final String URL = "url";
	public static final String H_PARENT = "h_parent"; // hierarchy parent
	public static final String PARENT = "parent";
	public static final String ELEMENT = "element";
	public static final String ACTION = "action";
	public static final String ATTR_ACTION = "attr-action";
	public static final String IGNORE = "ignore";
	public static final String APPEND = "append";
	public static final String APPEND_IF_DIFFERS = "append-if-differs";
	public static final String DOWNLOAD = "download";

	public enum Mode { get, transform, join, compile, files, all_but_get, all};

	public static final String MODE = "mode"; // режим работы: только скачивание (get), только парсинг (parse), и то и другое (all)
	public static final String STORAGE_DIR = "parsing.storage_dir"; // директория для хранения временных файлов программой crawler4j
	public static final String NUMBER_OF_CRAWLERS = "parsing.number_of_crawlers"; // количество параллельных потоков запросов
	public static final String POLITENESS = "parsing.politeness"; // количество миллисекунд между запросами
	public static final String MAX_PAGES = "parsing.max_pages"; // максимальное количество разобранных страниц
	public static final String MAX_DEPTH = "parsing.max_depth"; // максимальная глубина вложенности урлов начиная с начальных страниц (seed)
	public static final String PROXIES = "parsing.proxies_file"; // файл со списком прокси серверов
	public static final String URLS_PER_PROXY = "parsing.urls_per_proxy"; // количество запрошенных урлов перед переключением на следующий прокси
	public static final String URLS = "parsing.urls"; // начальный урл, маски урлов и соответствующие им файлы стилей
	public static final String STYLES_DIR = "parsing.styles_dir"; // директория, в которой лежат файлы со стилями
	public static final String RESULT_DIR = "parsing.result_dir"; // директория, в которой лежат файлы со стилями

	public static final String NO_TEMPLATE = "-";
	public static final String UTF_8 = "UTF-8";

	private static CrawlerController singleton = null;

	private CrawlConfig CONFIG = null;
	private CrawlController CONTROLLER = null;
	private LinkedList<String> proxies = null;
	private LinkedHashMap<String, String> urlStyles = null;
	private LinkedHashSet<String> seedUrls = null;
	private int urlsPerProxy = 0;
	private int numberOfCrawlers = 1;
	private int maxPages = -1;
	private int maxDepth = -1;
	private String stylesDir = null;
	private String resultDir = null;
	private String resultTempSrcDir = null;
	private String resultTempTransformedDir = null;
	private String resultTempJoinedDir = null;
	private String resultTempCompiledDir = null;
	private String resultTempFilesDir = null;

	private UrlModifier urlModifier = null;

	private int currentProxyUrlsCount = 0;

	private String nodeCacheFileName = null;
	private HashMap<String, Element> nodeCache = new HashMap<>();

	private static volatile IntegrateBase.Info info = null;

	private volatile long startTime = 0;

	private static class Errors implements ErrorListener {

		private String errors = "";

		public void error(TransformerException exception) throws TransformerException {
			errors += "\nERROR " + exception.getMessageAndLocation();
		}

		public void fatalError(TransformerException exception) throws TransformerException {
			errors += "\nFATAL ERROR " + exception.getMessageAndLocation();
		}

		public void warning(TransformerException exception) throws TransformerException {
			errors += "\nWARNING " + exception.getMessageAndLocation();
		}

		public boolean hasErrors() {
			return !StringUtils.isEmpty(errors);
		}
	}

	private static class ParsedItem {
		private String id;
		private String element;
		private String url;

		public ParsedItem(String id, String element, String url) {
			this.id = id;
			this.element = element;
			this.url = url;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ParsedItem parsedItem = (ParsedItem) o;
			return id.equals(parsedItem.id);
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}

	private static class Download {
		private final String id;
		private final String url;
		private final String fileName;

		public Download(String id, String downloadUrl, String fileName) {
			this.id = id;
			this.url = downloadUrl;
			this.fileName = fileName;
		}
	}


	private CrawlerController() throws Exception {

		info = new IntegrateBase.Info();

		// Список прокси серверов
		proxies = new LinkedList<>();
		String proxyFileName = AppContext.getRealPath(AppContext.getProperty(PROXIES, null));
		if (new File(proxyFileName).exists()) {
			try(BufferedReader br = new BufferedReader(new FileReader(new File(proxyFileName)))) {
			    for(String line; (line = br.readLine()) != null; ) {
			        if (!StringUtils.isBlank(line))
			        	proxies.add(line);
			    }
			} catch (Exception e) {
				throw new Exception("Can not read proxies list", e);
			}
		}

		// Начальные урлы и список стилей для урлов
		urlStyles = new LinkedHashMap<>();
		seedUrls = new LinkedHashSet<>();
		String urlFileName = AppContext.getRealPath(AppContext.getProperty(URLS, null));
		if (new File(urlFileName).exists()) {
			try {
				List<String> lines = Files.readAllLines(Paths.get(urlFileName), Charset.forName(UTF_8));
			    int lineNum = 1;
				for (String line : lines) {
			        line = line.trim();
			    	if (!StringUtils.isBlank(line) && !line.startsWith("#")) {
			        	String[] parts = StringUtils.split(line, ' ');
			        	if (parts.length == 1) {
			        		String seed = URLCanonicalizer.getCanonicalURL(parts[0]);
			        		seedUrls.add(seed);
			        		urlStyles.put(seed, NO_TEMPLATE);
			        	} else {
					        // Проверка правильности регулярного выражения
					        // Для этого используются все части, после второй (третяя и далее)
							for (int i = 2; i < parts.length; i++) {
								if (!parts[i].matches(parts[0]))
									info.pushLog("Supplied test url {} does not match regex {} on line {}", parts[i], parts[0], lineNum);
								else
									info.pushLog("Testing regex: {} - OK", parts[0]);
							}
			        		urlStyles.put(parts[0], parts[1]);
			        	}
			        }
			    }
			} catch (Exception e) {
				ServerLogger.error("Can not read URLs list", e);
				throw e;
			}
		}

		// Другие настройки
		info.pushLog("Start creating output directories");
		urlsPerProxy = Integer.parseInt(AppContext.getProperty(URLS_PER_PROXY, "0"));
		numberOfCrawlers = Integer.parseInt(AppContext.getProperty(NUMBER_OF_CRAWLERS, "1"));
		maxPages = Integer.parseInt(AppContext.getProperty(MAX_PAGES, "-1"));
		maxDepth = Integer.parseInt(AppContext.getProperty(MAX_DEPTH, "-1"));
		stylesDir = AppContext.getRealPath(AppContext.getProperty(STYLES_DIR, null));
		if (stylesDir != null && !stylesDir.endsWith("/"))
			stylesDir += "/";
		resultDir = AppContext.getRealPath(AppContext.getProperty(RESULT_DIR, null));
		if (resultDir != null && !resultDir.endsWith("/"))
			resultDir += "/";
		if (resultDir != null) {
			resultTempSrcDir = resultDir + "_src/";
			resultTempTransformedDir = resultDir + "_transformed/";
			resultTempJoinedDir = resultDir + "_joined/";
			resultTempCompiledDir = resultDir + "_compiled/";
			resultTempFilesDir = resultDir + "_files/";
		}
		info.pushLog("Output directories created");
		info.limitLog(1000);
	}
	/**
	 * Выполнить проход по всем доступным урлам согласно настройкам из файлов
	 * @param crawlerClass
	 * @throws Exception
	 */
	private void start(Class<? extends BasicCrawler> crawlerClass,  Mode mode) throws Exception {
		info.pushLog("Current mode : {}", mode);
		if (mode == Mode.get || mode == Mode.all) {
			initAndStartCrawler(crawlerClass);
		}
		if (mode == Mode.transform || mode == Mode.all || mode == Mode.all_but_get) {
			transformSource();
		}
		if (mode == Mode.join || mode == Mode.all || mode == Mode.all_but_get) {
			joinData();
		}
		if (mode == Mode.compile || mode == Mode.all || mode == Mode.all_but_get) {
			compileAndBuildResult();
		}
		if (mode == Mode.files || mode == Mode.all || mode == Mode.all_but_get) {
			downloadFiles();
		}
	}
	
	private void terminateInt() {
		CONTROLLER.shutdown();
	}
	/**
	 * Создать объект crawler4j и начать скачку страниц
	 * @param crawlerClass
	 * @throws Exception
	 */
	private void initAndStartCrawler(Class<? extends BasicCrawler> crawlerClass) throws Exception {
		// ������������ crawler4j
		int politeness = Integer.parseInt(AppContext.getProperty(POLITENESS, "none"));
		String storageDir = AppContext.getRealPath(AppContext.getProperty(STORAGE_DIR, ""));
		CONFIG = new CrawlConfig();
		CONFIG.setCrawlStorageFolder(storageDir);
		CONFIG.setPolitenessDelay(politeness);
		CONFIG.setIncludeBinaryContentInCrawling(false);
		CONFIG.setResumableCrawling(true);
		CONFIG.setMaxPagesToFetch(maxPages);
		CONFIG.setMaxDepthOfCrawling(maxDepth);
		CONFIG.setUserAgentString("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		CONFIG.setConnectionTimeout(60000);
		CONFIG.setSocketTimeout(60000);

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(CONFIG);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CONTROLLER = new CrawlController(CONFIG, pageFetcher, robotstxtServer);

		for (String seed : seedUrls) {
			CONTROLLER.addSeed(seed);
		}

		startTime = System.currentTimeMillis();
		CONTROLLER.start(crawlerClass, numberOfCrawlers);
		
		info.pushLog("\n\n\n\n------------------  Finished crawling  -----------------------\n\n\n\n");
	}
	/**
	 * Выполнить проход по всем доступным урлам согласно настройкам из файлов
	 * @param crawlerClass
	 * @throws Exception
	 */
	public static void startCrawling(Class<? extends BasicCrawler> crawlerClass, IntegrateBase.Info info, Mode mode,
	                                 UrlModifier... modifier) throws Exception {
		getSingleton(true).info = info;
		if (modifier != null && modifier.length > 0)
			getSingleton().urlModifier = modifier[0];
		getSingleton().start(crawlerClass, mode);
	}
	
	public static void terminate() {
		getSingleton().terminateInt();
	}
	/**
	 * Добавить результат парсинга одного URL (вызывается из экземпляра WebCrawler)
	 * Результат записывается в файл с названием, которое соответствует урлу
	 * @param crawler - кролер, который вернул результат
	 * @param result - результат парсинга
	 */
	void pageProcessed(Page page, String result, BasicCrawler crawler) throws IllegalAccessException, InstantiationException {
		// Записать в файл изначальный полученный html
		try {
			String url = page.getWebURL().getURL();
			url = URLDecoder.decode(url, "UTF-8");
			String fileName = Strings.createFileName(url);
			Files.createDirectories(Paths.get(resultTempSrcDir));
			// Если результат парсинга урла не пустая строка - записать этот результат в файл
			if (!StringUtils.isBlank(result)) {
				Files.write(Paths.get(resultTempSrcDir + fileName), result.getBytes(UTF_8));
			}
		} catch (Exception e) {
			ServerLogger.error("Error downloading html file", e);
			info.pushLog("Can not write parsing results to a file", e);
		}
		currentProxyUrlsCount++;
		// Проверка, нужно ли менять прокси
		if (proxies.size() > 0 && urlsPerProxy > 0 && currentProxyUrlsCount >= urlsPerProxy) {
			// Взять первый прокси из очереди и поместить его в конец
			String proxy = proxies.pop();
			proxies.add(proxy);
			String actualProxy = StringUtils.split(proxy, ' ')[0];
			String[] parts = StringUtils.split(actualProxy, ':');
			if (parts.length > 1) {
				// Изменение настроек контроллера ,создание нового объекта PageFetcher
				String address = parts[0];
				int port = Integer.parseInt(parts[1]);
				CONTROLLER.getPageFetcher().shutDown();
				CONFIG.setProxyHost(address);
				CONFIG.setProxyPort(port);
				if (parts.length == 4) {
					CONFIG.setProxyUsername(parts[2]);
					CONFIG.setProxyPassword(parts[3]);
				}
				currentProxyUrlsCount = 0;
				PageFetcher newFetcher = new PageFetcher(CONFIG);
				CONTROLLER.setPageFetcher(newFetcher);
				// Установка нового PageFetcher в объект текущего кролера (WebCrawler)
				crawler.init(crawler.getMyId(), CONTROLLER);
			}
		}
	}
	/**
	 * Вернуть контроллер
	 * @return
	 */
	static CrawlerController getSingleton(boolean...forceCreate) {
		try {
			if (singleton == null || (forceCreate.length > 0 && forceCreate[0]))
				singleton = new CrawlerController();
			return singleton;
		} catch (Exception e) {
			ServerLogger.error("Crawler not inited", e);
			info.pushLog("Error", e);
			throw new RuntimeException(e);
		}
	}
	/**
	 * Загрузить все файлы, которые упоминаются в результирующем документе
	 */
	private void downloadFiles() {
		info.setProcessed(0);
		info.setOperation("Загрузка файлов");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resultTempCompiledDir))) {
			final ConcurrentLinkedQueue<Download> downloadQueue = new ConcurrentLinkedQueue<>();
			final int NUM_THREADS = 10;
			Thread[] threads = new Thread[NUM_THREADS];
			info.pushLog("Creating download queue");
			info.setProcessed(0);
			for (Path path : stream) {
				Document result = Jsoup.parse(path.toFile(), UTF_8);
				Elements downloads = result.getElementsByAttribute(DOWNLOAD);
				for (Element download : downloads) {
					// Найти первого родителя с заданным ID
					Element idOwner = download;
					String id = null;
					for (; idOwner != null && StringUtils.isBlank(id); idOwner = idOwner.parent()) {
						id = idOwner.attr(ID);
					}
					if (!StringUtils.isBlank(id)) {
						String fileName = download.ownText();
						String url = download.attr(DOWNLOAD);
						if (StringUtils.isBlank(fileName))
							fileName = Strings.getFileName(url);
						downloadQueue.add(new Download(id, url, fileName));
					}
				}
				info.increaseProcessed();
			}
			info.pushLog("Queue created. Start downloading.");
			info.setProcessed(0);
			info.setToProcess(downloadQueue.size());
			for (int i = 0; i < NUM_THREADS; i++) {
				threads[i] = new Thread() {
					@Override
					public void run() {
						Download download;
						while ((download = downloadQueue.poll()) != null) {
							Path itemDir = Paths.get(resultTempFilesDir + download.id);
							try {
								if (!Files.exists(itemDir))
									Files.createDirectories(itemDir);
								Path file = Paths.get(resultTempFilesDir + download.id + "/" + download.fileName);
								if (!Files.exists(file) || Files.size(file) == 0) {
									try {
										FileUtils.copyURLToFile(URI.create(download.url).toURL(), file.toFile());
									} catch (Exception e) {
										info.pushLog("Can not download file. Error: " + e.getMessage());
									}
								}
							} catch (Exception e) {
								info.pushLog("Error downloading: {}", download.url);
							}
							info.increaseProcessed();
						}

					}
				};
				threads[i].setDaemon(true);
				threads[i].start();
			}
			for (Thread thread : threads) {
				thread.join();
			}
			info.pushLog("Finished downloading files");
		} catch (IOException e) {
			info.pushLog("Can not parse result file");
		} catch (InterruptedException e) {
			ServerLogger.error("Thread join error", e);
			info.pushLog("Ошибка объединения потоков скачивания файлов");
		}
	}
	/**
	 * Преобразовать все файлы из изначального вида в XML вид
	 */
	public void transformSource() {
		TransformerFactory factory = TransformerFactoryImpl.newInstance();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resultTempSrcDir))) {
			Path transformedDir = Paths.get(resultTempTransformedDir);
			if (Files.exists(transformedDir))
				FileUtils.deleteDirectory(transformedDir.toFile());
			Files.createDirectories(transformedDir);
			//info.setToProcess(Paths.get(resultTempSrcDir).toFile().list().length);
			info.setProcessed(0);
			info.setOperation("XSLT преобразование HTML в XML");
			// Для каждого файла из временной директории
			for (Path entry : stream) {
				Path resultFile = Paths.get(resultTempTransformedDir + entry.getFileName().toString());
				String html = Strings.cleanHtml(new String(Files.readAllBytes(entry), UTF_8));
				Document pageDoc = Jsoup.parse(html);

				String url = pageDoc.getElementsByTag("body").first().attr("source");

				File xslFile = new File(stylesDir + getStyleForUrl(url));
				if (!xslFile.exists()) {
					info.pushLog("No xsl file '{}' found", stylesDir + getStyleForUrl(url));
					continue;
				}
				Errors errors = new Errors();
				Transformer transformer;
				try {
					info.pushLog("Transforming: {}\tTo transform: {}", url, info.getToProcess() - info.getProcessed());
					factory.setErrorListener(errors);
					transformer = factory.newTransformer(new StreamSource(xslFile));
					Reader reader = new StringReader(html);
					OutputStream os = Files.newOutputStream(resultFile);
					transformer.transform(new StreamSource(reader), new StreamResult(os));
					os.close();
				} catch (TransformerConfigurationException e) {
					info.pushLog(errors.errors, e);
				} catch (TransformerException e) {
					info.pushLog("Error while transforming html input. Url: " + url, e);
				} catch (UnsupportedEncodingException e) {
					info.pushLog("Unsupported charset", e);
				}
				if (errors.hasErrors()) {
					info.pushLog("There were errors while transforming source html file {}", url);
					info.pushLog(errors.errors);
				}
				info.increaseProcessed();
			}
		} catch (Exception e) {
			info.pushLog("Error while transforming source html file", e);
		}
		info.pushLog("ЗАВЕРШЕНО: XSLT преобразование HTML в XML");
	}

	/**
	 * Преобразовать HTML файл в XML в тестовом режиме
	 * @param url
	 * @return
	 */
	public String transformUrlInt(String url) {
		// Записать в файл изначальный полученный html
		try {
			String fileName = Strings.createFileName(url);
			Path file = Paths.get(resultTempSrcDir + fileName);
			String content = new String(Files.readAllBytes(file), UTF_8);
			return transformStringInt(content, url);
		} catch (Exception e) {
			return handleTransformationException(e);
		}
	}

	/**
	 * Преобразовать заданный в виде строки HTML в XML
	 * @param source - строка HTML
	 * @param url - для определения нужного XSL файла
	 * @return
	 * @throws TransformerException
	 * @throws UnsupportedEncodingException
	 */
	private String transformStringInt(String source, String url) {
		try {
			File xslFile = new File(stylesDir + getStyleForUrl(url));
			if (!xslFile.exists()) {
				return "<result>NO XSL FILE FOUND</result>";
			}
			TransformerFactory factory = TransformerFactoryImpl.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xslFile));
			Reader reader = new StringReader(source);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			transformer.transform(new StreamSource(reader), new StreamResult(bos));
			return bos.toString(UTF_8);
		} catch (Exception e) {
			return handleTransformationException(e);
		}
	}

	/**
	 * Обработать исключение, которое может получиться в процессе XSL трансформации
	 * @param e
	 * @return
	 */
	private String handleTransformationException(Exception e) {
		info.pushLog("Can not write parsing results to a file", e);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(bos));
		try {
			return bos.toString(UTF_8);
		} catch (UnsupportedEncodingException e1) {
			ServerLogger.error("no encoding", e1);
			return null;
		}
	}



	public static String transformUrl(String url) {
		return getSingleton().transformUrlInt(url);
	}


	public static String transformString(String html, String url) {
		return getSingleton().transformStringInt(html, url);
	}


	/**
	 * Объединяет данные из разных XML файлов в один файл, посвященный одному объекту
	 */
	public void joinData() {
		info.setOperation("Сбор и объединение данных из разных источников");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resultTempTransformedDir))) {
			Path joinedDir = Paths.get(resultTempJoinedDir);
			if (Files.exists(joinedDir)) {
				FileUtils.deleteDirectory(joinedDir.toFile());
			}
			Files.createDirectories(joinedDir);
			//info.setToProcess(Paths.get(resultTempTransformedDir).toFile().list().length);
			info.setProcessed(0);
			// Для каждого файла из временной директории
			for (Path xmlFile : stream) {
				String xml = new String(Files.readAllBytes(xmlFile), UTF_8);
				Document pageDoc = Jsoup.parse(xml, "localhost", Parser.xmlParser());
				Elements items = pageDoc.select("result > *[" + ID + "]");
				for (Element partItem : items) {
					String itemId = partItem.attr(ID);
					if (StringUtils.isBlank(itemId))
						continue;
					String fileName = Strings.createFileName(itemId) + ".xml";
					Path file = joinedDir.resolve(fileName);
					Files.write(file, partItem.outerHtml().getBytes(UTF_8),
							StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
				}
				info.increaseProcessed();
			}
		} catch (Exception e) {
			ServerLogger.error("Error while joining xml files", e);
			info.pushLog("Error while joining xml files", e);
		}
		info.pushLog("ЗАВЕРШЕНО: Сбор и объединение данных из разных источников");
	}


	/**
	 * Удалить дублирование и создать иерархию
	 * @throws IOException
	 */
	public void compileAndBuildResult() {
		Path compiledDir = Paths.get(resultTempCompiledDir);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resultTempJoinedDir))) {
			if (Files.exists(compiledDir)) {
				FileUtils.deleteDirectory(compiledDir.toFile());
			}
			Files.createDirectories(compiledDir);
			info.setOperation("Удаление дублирующихся данных");
			//info.setToProcess(Paths.get(resultTempJoinedDir).toFile().list().length);
			info.setProcessed(0);
			for (Path xmlFile : stream) {
				// Сначала скомпилировать информацию (удалить дубли, объединить элементы)
				// с сохранить в файл
				String xml = "<result>" + new String(Files.readAllBytes(xmlFile), UTF_8) + "</result>";
				Document doc = Jsoup.parse(xml, "localhost", Parser.xmlParser());
				Document newDoc = null;
				Element newItemToAppend = null;
				String itemId = null;
				Elements data = doc.getElementsByTag("result").first().children();
				for (Element item : data) {
					if (newDoc == null) {
						itemId = item.id();
						if (StringUtils.isBlank(itemId))
							continue;
						String elementName = item.tagName();
						newDoc = new Document("localhost");
						newItemToAppend = new Element(elementName);
						for (Attribute attribute : item.attributes()) {
							newItemToAppend.attr(attribute.getKey(), attribute.getValue());
						}
						newDoc.appendChild(newItemToAppend);
					}
					for (Element property : item.children()) {
						String action = StringUtils.defaultIfBlank(property.attr(ACTION), IGNORE);
						property.removeAttr(ACTION);
						boolean containsProperty = !newItemToAppend.select(":root > " + property.tagName()).isEmpty();
						if (!containsProperty) {
							newItemToAppend.appendChild(property);
						} else {
							// Дописываение
							if (action.equalsIgnoreCase(APPEND) ||
									(property.tagName().equalsIgnoreCase(H_PARENT) &&
											newItemToAppend.select("h_parent[parent=" + property.attr("parent") + "]").isEmpty())) {
								newItemToAppend.appendChild(property);
							}
							// Дописывание, если еще нет такого значения
							else if (action.equalsIgnoreCase(APPEND_IF_DIFFERS)) {
								boolean append = true;
								for (Element existing : newItemToAppend.getElementsByTag(property.tagName())) {
									if (StringUtils.equalsIgnoreCase(existing.html(), property.html())) {
										append = false;
										break;
									}
								}
								if (append) {
									newItemToAppend.appendChild(property);
								}
							}
						}
					}
				}
				if (newItemToAppend != null) {
					String fileName = Strings.createFileName(itemId) + ".xml";
					Path file = compiledDir.resolve(fileName);
					Files.write(file, newItemToAppend.outerHtml().getBytes(UTF_8));
				}
				info.increaseProcessed();
			}
		} catch (IOException e) {
			ServerLogger.error("Error while normalizing item personal file", e);
			info.pushLog("Error while normalizing item personal file", e);
		}
		info.pushLog("ЗАВЕРШЕНО: Удаление дублирующихся данных");

		// Теперь построить дерево иерархии по результатам компиляции

		// 1. Первый проход по всем файлам - создание списка связности в виде
		// хеш-отображения ID родителя -> список непосредственных потомков
		// Выделение корневых элементов - элементов, которые не содержат родителя (или он пустой)

		// 1.1. Проход по файлам и создание списка связности
		// (в дальнейшем возможно сохранение промежуточных результатов в БД)

		HashMap<ParsedItem, UniqueArrayList<ParsedItem>> parentChildren = new HashMap<>();
		LinkedHashSet<ParsedItem> roots = new LinkedHashSet<>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resultTempCompiledDir))) {
			info.setOperation("Дерево иерархии (вложенности). Поиск предков и потомков");
			info.setProcessed(0);
			info.setToProcess(Paths.get(resultTempCompiledDir).toFile().list().length);
			for (Path xmlFile : stream) {
				String xml = new String(Files.readAllBytes(xmlFile), UTF_8);
				Document pageDoc = Jsoup.parse(xml, "localhost", Parser.xmlParser());
				Element fullItem = pageDoc.children().first();
				Elements hrefEls = pageDoc.getElementsByTag(URL);
				String href = hrefEls.first() != null ? hrefEls.first().ownText() : "";
				ParsedItem item = new ParsedItem(fullItem.attr(ID), fullItem.tagName(), href);
				Elements directParents = fullItem.getElementsByTag(H_PARENT);
				boolean hasValidParents = false;
				// Добавить запись для каждого отдельного родителя (считается что он непосредственный)
				// в список связности
				if (directParents.size() > 0) {
					for (Element directParent : directParents) {
						String parentId = null;
						if (directParent != null) {
							parentId = directParent.attr(PARENT);
						}
						hasValidParents |= StringUtils.isNotBlank(parentId);
						if (StringUtils.isNotBlank(parentId)) {
							ParsedItem parent = new ParsedItem(parentId, directParent.attr(ELEMENT), directParent.attr(URL));
							if (parentChildren.containsKey(parent)) {
								parentChildren.get(parent).add(item);
							} else {
								UniqueArrayList<ParsedItem> children = new UniqueArrayList<>();
								parentChildren.put(parent, children);
								children.add(item);
							}
							// Добавить родителя в список потенциальных корневых айтемов
							roots.add(parent);
						}
					}
				}
				// Если у айтема не указаны родители - добавить его в список потенциальных корневых айтемов
				if (!hasValidParents) {
					roots.add(item);
				}
				info.increaseProcessed();
			}
		} catch (IOException e) {
			ServerLogger.error("Error while reading item file", e);
			info.pushLog("Error while reading item file", e);
		}

		// 1.2. Удалить из списка потенциальных корневых айтемов те айтемы, которые присутствуют в списках
		// потомков какого-либо родителя

		for (ParsedItem parent : parentChildren.keySet()) {
			UniqueArrayList<ParsedItem> children = parentChildren.get(parent);
			roots.removeAll(children);
		}
		info.pushLog("ЗАВЕРШЕНО: Дерево иерархии (вложенности). Поиск предков и потомков");


		info.setOperation("Дерево иерархии (вложенности). Запись дерева в XML файл");
		info.setProcessed(0);

		// 2. Второй проход и формирование итогового XML

		// 2.1. Проход по списку потомков рекурсивно вглубь начиная с корневых элементов с удалением
		// пройденных элементов в списке
		// Рекурсивный проход вглубь. После посещения элемент сразу удаляется
		// По мере прохода постоянно формируется один сквозной XML файл в прямом порядке

		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("data");
		for (ParsedItem root : roots) {
			insertItem(doc, parentChildren, root);
		}
		doc.endElement();
		Path file = compiledDir.resolve("_tree_.xml");
		try {
			Files.write(file, doc.toString().getBytes(UTF_8));
		} catch (Exception e) {
			ServerLogger.error("Error while creating result tree file", e);
			info.pushLog("Error while creating result tree file", e);
		}

		info.pushLog("ЗАВЕРШЕНО: Дерево иерархии (вложенности). Запись дерева в XML файл");
	}


	/**
	 * Вставить один айтем в иерархию
	 * @param xml
	 * @param parentChildren
	 * @param parent
	 */
	private void insertItem(XmlDocumentBuilder xml, HashMap<ParsedItem, UniqueArrayList<ParsedItem>> parentChildren, ParsedItem parent) {
		xml.startElement(parent.element, ID, parent.id, URL, parent.url);
		// Добавить всех потомков
		UniqueArrayList<ParsedItem> children = parentChildren.get(parent);
		if (children != null) {
			for (ParsedItem child : children) {
				insertItem(xml, parentChildren, child);
			}
		}
		xml.endElement();
	}

	/**
	 * Найти XSL файл, который соответствует заданному урлу (нужен для преобразования этого урла)
	 * @param url
	 * @return
	 */
	String getStyleForUrl(String url) {
		String style = null;
		for (Entry<String, String> entry : urlStyles.entrySet()) {
			if (StringUtils.equals(url, entry.getKey()) || url.matches(entry.getKey())) {
				style = entry.getValue();
				if (!StringUtils.equalsIgnoreCase(StringUtils.trim(style), NO_TEMPLATE))
					return style;
			}
		}
		return style;
	}
	
	private void initElementCache(String fileName) {
		if (!StringUtils.equalsIgnoreCase(nodeCacheFileName, fileName)) {
			nodeCacheFileName = fileName;
			nodeCache = new HashMap<>();
		}
	}
	
	private void dropNodeCache() {
		nodeCacheFileName = null;
	}
	/**
	 * Кешированное получение элемента из другого элемента по ID
	 * @param root
	 * @param id
	 * @return
	 */
	private Element getElementById(Element root, String id) {
		if (nodeCacheFileName != null) {
			Element cached = nodeCache.get(id);
			if (cached == null) {
				cached = root.getElementById(id);
				if (cached != null)
					nodeCache.put(id, cached);
			}
			return cached;
		}
		return root.getElementById(id);
	}
	
	void changeUrlFor(String oldUrl, String newUrl) {
		String style = urlStyles.get(oldUrl);
		urlStyles.put(newUrl, style);
		urlStyles.remove(oldUrl);
	}

	public long getStartTime() {
		return startTime;
	}
	
	public static IntegrateBase.Info getInfo() {
		return info;
	}

	public void modifyUrl(WebURL url) {
		if (urlModifier != null)
			urlModifier.modifyUrl(url);
	}

	public static void main(String[] args) {
		System.out.println("https://www.metabo.com/ru/index.php?cl=search&order=&ldtype=infogrid&_artperpage=100&pgNr=0&searchparam="
				.matches("https://www\\.metabo\\.com/ru/index\\.php\\?cl=search(&amp;)?&?&order=?(&amp;)?&?ldtype=infogrid(&amp;)?&?_artperpage=100(&amp;)?&?pgNr=.{0,2}(&amp;)?&?searchparam=?"));
	}
}
