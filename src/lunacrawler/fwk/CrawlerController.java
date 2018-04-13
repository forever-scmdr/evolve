package lunacrawler.fwk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ecommander.fwk.IntegrateBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Tag;
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

 Результирующий документ может содержать иерархическую структуту элементов, например, такую, как каталог
 товаров магазина с разделением на разделы и подразделы. Для того, чтобы обозначить место некоторого элемента
 в такой иерархии, в него включаются элементы h_parent, содежращие атрибут parent с уникальным идентификатором
 предков этого объекта (например, разделы каталога, в которые вложен товар). Количество элементов parent
 соответствует уровню вложенности элемета в иерархии. Если элемент не должен быть никуда вложен, в нем
 элементы parent отсутствуют. Также элемент h_parent содержит атрибут element, который содержит навзание
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
	public static final String H_PARENT = "h_parent"; // hierarchy parent
	public static final String PARENT = "parent";
	public static final String ELEMENT = "element";
	public static final String ACTION = "action";
	public static final String ATTR_ACTION = "attr-action";
	public static final String IGNORE = "ignore";
	public static final String APPEND = "append";
	public static final String APPEND_IF_DIFFERS = "append-if-differs";
	public static final String DOWNLOAD = "download";
	
	public enum Mode { get, parse, append, files, all};
	
	public static final String PROPS_RESOURCE = "lunacrawler/props/settings.properties"; // файл с настройками

	public static final String MODE = "mode"; // режим работы: только скачивание (get), только парсинг (parse), и то и другое (all)
	public static final String STORAGE_DIR = "storage_dir"; // директория для хранения временных файлов программой crawler4j
	public static final String NUMBER_OF_CRAWLERS = "number_of_crawlers"; // количество параллельных потоков запросов
	public static final String POLITENESS = "politeness"; // количество миллисекунд между запросами
	public static final String MAX_PAGES = "max_pages"; // максимальное количество разобранных страниц
	public static final String MAX_DEPTH = "max_depth"; // максимальная глубина вложенности урлов начиная с начальных страниц (seed)
	public static final String PROXIES = "proxies_file"; // файл со списком прокси серверов
	public static final String URLS_PER_PROXY = "urls_per_proxy"; // количество запрошенных урлов перед переключением на следующий прокси
	public static final String URLS = "urls"; // начальный урл, маски урлов и соответствующие им файлы стилей
	public static final String STYLES_DIR = "styles_dir"; // директория, в которой лежат файлы со стилями
	public static final String RESULT_DIR = "result_dir"; // директория, в которой лежат файлы со стилями
	public static final String RESULT_FILE = "result_file"; // файл с результатом парсинга

	public static final String NO_TEMPLATE = "-";
	public static final String UTF_8 = "UTF-8";
	
	private static CrawlerController singleton = null;
	
	private CrawlConfig CONFIG = null;
	private CrawlController CONTROLLER = null;
	private Properties props = null;
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
	private String resultTempFilesDir = null;
	private String resultFile = null;

	private int filesToTransform = 0;
	private int filesToDownload = 0;
	private int filesToAppend = 0;

	private String nodeCacheFileName = null;
	private HashMap<String, Element> nodeCache = new HashMap<>();
	
	private static IntegrateBase.Info info = null;
	
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
	
	private CrawlerController() throws Exception {

		// Загрузка настроек
		InputStream is = getClass().getClassLoader().getResourceAsStream(PROPS_RESOURCE);
		props = new Properties();
		if (is != null) {
			props.load(is);
		} else {
			throw new Exception(PROPS_RESOURCE + " file not found");
		}

		// Список прокси серверов
		proxies = new LinkedList<>();
		String proxyFileName = AppContext.getRealPath(props.getProperty(PROXIES, null));
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
		String urlFileName = AppContext.getRealPath(props.getProperty(URLS, null));
		if (new File(urlFileName).exists()) {
			try(BufferedReader br = new BufferedReader(new FileReader(new File(urlFileName)))) {
			    int lineNum = 1;
				for(String line; (line = br.readLine()) != null; lineNum++) {
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
			        		urlStyles.put(parts[0].toLowerCase(), parts[1]);
			        	}
			        }
			    }
			} catch (Exception e) {
				throw new Exception("Can not read URLs list", e);
			}
		}

		// Другие настройки
		info.pushLog("Start creating output directories");
		urlsPerProxy = Integer.parseInt(props.getProperty(URLS_PER_PROXY, "0"));
		numberOfCrawlers = Integer.parseInt(props.getProperty(NUMBER_OF_CRAWLERS, "1"));
		maxPages = Integer.parseInt(props.getProperty(MAX_PAGES, "-1"));
		maxDepth = Integer.parseInt(props.getProperty(MAX_DEPTH, "-1"));
		stylesDir = AppContext.getRealPath(props.getProperty(STYLES_DIR, null));
		if (stylesDir != null && !stylesDir.endsWith("/"))
			stylesDir += "/";
		resultDir = AppContext.getRealPath(props.getProperty(RESULT_DIR, null));
		if (resultDir != null && !resultDir.endsWith("/"))
			resultDir += "/";
		if (resultDir != null) {
			resultTempSrcDir = resultDir + "_src/";
			resultTempTransformedDir = resultDir + "_transformed/";
			resultTempFilesDir = resultDir + "_files/";
		}
		resultFile = AppContext.getRealPath(props.getProperty(RESULT_FILE, null));
		info.pushLog("Output directories created");
	}
	/**
	 * Выполнить проход по всем доступным урлам согласно настройкам из файлов
	 * @param crawlerClass
	 * @throws Exception
	 */
	private void start(Class<? extends BasicCrawler> crawlerClass,  Mode mode) throws Exception {
		info.pushLog("Current mode : {}", mode);
		if (mode == Mode.get || mode == Mode.all)
			initAndStartCrawler(crawlerClass);
		if (mode == Mode.parse || mode == Mode.all)
			buildFinalResult();
		if (mode == Mode.files || mode == Mode.all)
			downloadFiles();
		if (mode == Mode.append)
			buildRresult_OLD();
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
		int politeness = Integer.parseInt(props.getProperty(POLITENESS, "50"));
		String storageDir = AppContext.getRealPath(props.getProperty(STORAGE_DIR, ""));
		CONFIG = new CrawlConfig();
		CONFIG.setCrawlStorageFolder(storageDir);
		CONFIG.setPolitenessDelay(politeness);
		CONFIG.setIncludeBinaryContentInCrawling(false);
		CONFIG.setResumableCrawling(true);
		CONFIG.setMaxPagesToFetch(maxPages);
		CONFIG.setMaxDepthOfCrawling(maxDepth);

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
	public static void startCrawling(Class<? extends BasicCrawler> crawlerClass, IntegrateBase.Info info, Mode mode) throws Exception {
		CrawlerController.info = info;
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
			String fileName = URLEncoder.encode(page.getWebURL().getURL().toLowerCase(), UTF_8);
			Files.createDirectories(Paths.get(resultTempSrcDir));
			// Если результат парсинга урла не пустая строка - записать этот результат в файл
			if (!StringUtils.isBlank(result)) {
				Files.write(Paths.get(resultTempSrcDir + fileName), result.getBytes(UTF_8));
			}
		} catch (Exception e) {
			info.pushLog("Can not write parsing results to a file", e);
		}
		// Проверка, нужно ли менять прокси
		if (proxies.size() > 0 && urlsPerProxy > 0) {
			// Взять первый прокси из очереди и поместить его в конец
			String proxy = proxies.pop();
			proxies.add(proxy);
			String[] parts = StringUtils.split(proxy, ':');
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
	static CrawlerController getSingleton() {
		try {
			if (singleton == null)
				singleton = new CrawlerController();
			return singleton;
		} catch (Exception e) {
			info.pushLog("Error", e);
			throw new RuntimeException(e);
		}
	}
	/**
	 * Сормировать окончательный файл с результатами парсинга
	 */
	private void buildFinalResult() {
		//
		// Шаг 1. Преобразовать все файлы из изначального вида в XML вид
		//
		transformSource();
		//
		// Шаг 2. Собрать результаты преобразований в один файл
		//
		buildRresult_OLD();
		
		info.pushLog("\n\n\n\n------------------  Finished transforming  -----------------------\n\n\n\n");
	}
	/**
	 * Загрузить все файлы, которые упоминаются в результирующем документе
	 */
	private void downloadFiles() {
		try {
			Document result = Jsoup.parse(new File(resultFile), UTF_8);
			Elements downloads = result.getElementsByAttribute(DOWNLOAD);
			filesToDownload = downloads.size();
			final ConcurrentLinkedQueue<Element> downloadQueue = new ConcurrentLinkedQueue<>();
			downloadQueue.addAll(downloads);
			final int NUM_THREADS = 10;
			Thread[] threads = new Thread[NUM_THREADS];
			for (int i = 0; i < NUM_THREADS; i++) {
				threads[i] = new Thread() {
					@Override
					public void run() {
						Element download = null;
						while ((download = downloadQueue.poll()) != null) {
							// Найти первого родителя с заданным ID
							Element idOwner = download;
							String id = null;
							for (; idOwner != null && StringUtils.isBlank(id); idOwner = idOwner.parent()) {
								id = idOwner.attr(ID);
							}
							if (!StringUtils.isBlank(id)) {
								String fileName = download.ownText();
								Path itemDir = Paths.get(resultTempFilesDir + id);
								try {
									if (!Files.exists(itemDir))
										Files.createDirectories(itemDir);
									Path file = Paths.get(resultTempFilesDir + id + "/" + fileName);
									if (!Files.exists(file) || Files.size(file) == 0) {
										String url = download.attr(DOWNLOAD);
										//url = URLEncoder.encode(url, "UTF-8");
										info.pushLog("Downloading: {}\tTo download: {}", url, filesToDownload);
										try {
											FileUtils.copyURLToFile(URI.create(url).toURL(), file.toFile());
										} catch (Exception e) {
											info.pushLog("Can not download file. Error: " + e.getMessage());
										}
									}
								} catch (Exception e) {
									info.pushLog("Error downloading: {}", download.attr(DOWNLOAD));
								}
							}
							filesToDownload--;
						}

					}
				};
				threads[i].start();
			}
/*
			for (Element download : downloads) {
				// ����� ������� �������� � �������� ID
				Element idOwner = download;
				String id = null;
				for (; idOwner != null && StringUtils.isBlank(id); idOwner = idOwner.parent()) {
					id = idOwner.attr(ID);
				}
				if (!StringUtils.isBlank(id)) {
					String fileName = download.ownText();
					Path itemDir = Paths.get(resultTempFilesDir + id);
					if (!Files.exists(itemDir))
						Files.createDirectories(itemDir);
					Path file = Paths.get(resultTempFilesDir + id + "/" + fileName);
					if (!Files.exists(file) || Files.size(file) == 0) {
						String url = download.attr(DOWNLOAD);
						url = UrlUtil.encode(url, "UTF-8");
						logger.info("Downloading: {}\tTo download: {}", url, filesToDownload);
						try {
							FileUtils.copyURLToFile(URI.create(url).toURL(), file.toFile(), 5000, 5000);
						} catch (Exception e) {
							logger.error("Can not download file. Error: " + e.getMessage());
						}
					}
				}
				filesToDownload--;
			}
			*/
			info.pushLog("Finished downloading files");
		} catch (IOException e) {
			info.pushLog("Can not parse result file", e);
		}
	}
	/**
	 * Преобразовать все файлы из изначального вида в XML вид
	 */
	public void transformSource() {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resultTempSrcDir))) {
			Path transformedDir = Paths.get(resultTempTransformedDir);
			if (!Files.exists(transformedDir))
				Files.createDirectories(transformedDir);
			TransformerFactory factory = TransformerFactoryImpl.newInstance();
			filesToTransform = Paths.get(resultTempSrcDir).toFile().list().length;
			// Для каждого файла из временной директории
			for (Path entry : stream) {
				String content = new String(Files.readAllBytes(entry), UTF_8);
				String url = URLDecoder.decode(entry.getFileName().toString(), UTF_8);
				File xslFile = new File(stylesDir + getStyleForUrl(url));
				if (!xslFile.exists()) {
					info.pushLog("No xsl file '{}' found", stylesDir + getStyleForUrl(url));
					continue;
				}
				Path resultFile = Paths.get(resultTempTransformedDir + entry.getFileName().toString());
				Errors errors = new Errors();
				Transformer transformer = null;
				try {
					info.pushLog("Transforming: {}\tTo transform: {}", url, filesToTransform);
					factory.setErrorListener(errors);
					transformer = factory.newTransformer(new StreamSource(xslFile));
					Reader reader = new StringReader(content);
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
				filesToTransform--;
			}
		} catch (Exception e) {
			info.pushLog("Error while transforming source html file", e);
		}
	}


	public void buildResult() {
		// 1. Первый проход по всем файлам - создание транзитивного замыкания предков в виде
		// хеш-отображения ID -> список родителей (с выделением непосредственных)

		// 1.1. Проход по файлам и создание списка связности
		// (в дальнейшем возможно сохранение промежуточных результатов в БД)

		// 1.2. Создание транзитивного замыкания исключительно в памяти,
		// поскольку нет необходимости файлового ввода-вывода, это по идее быстро

		// 2. Второй проход по файлам и формирование итогового XML

		// 2.1. Проход по транзитивному замыканию в порятке от корневых айтемов в глубь с удалением
		// пройденных элементов и в замыкании и соответсрвующих файлов
		// Рекурсивный проход вглубь. После посещения элемент сразу удаляется

		// 2.2. По мере прохода постоянно формируется один сквозной XML файл в прямом порядке
	}










	/**
	 * Собрать результаты преобразований в один файл
	 */
	public void buildRresult_OLD() {
		info.pushLog("Creating output document");
		Document resultDoc = Jsoup.parse("<result></result>"); // Результирующий документ JSoup
		try {
			OutputSettings settings = new OutputSettings();
			settings.charset(Charset.forName("UTF-8"));
			//settings.syntax(Syntax.xml);
			settings.escapeMode(EscapeMode.xhtml);
			info.pushLog("test 1 {}", resultDoc);
			resultDoc.outputSettings(settings);
			//resultDoc.outputSettings().escapeMode(EscapeMode.xhtml);
		} catch (Exception e) {
			info.pushLog("Some error", "<pre>" + ExceptionUtils.getStackTrace(e) + "</pre>");
		}
		info.pushLog("test 2 {}", resultDoc);
		Element resultEl = resultDoc.getElementsByTag("result").first();
		try {
			info.pushLog("Searching transformed files in {}", resultTempTransformedDir);
			DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(resultTempTransformedDir));
			filesToAppend = Paths.get(resultTempTransformedDir).toFile().list().length;
			// Для каждого файла из временной директории
			for (Path entry : stream) {
				String url = URLDecoder.decode(entry.getFileName().toString(), UTF_8);
				info.pushLog("Appending: {}\t To append: {}" , url, filesToAppend);
				Document pageDoc = Jsoup.parse(entry.toFile(), UTF_8);
				insertPart_OLD(resultEl, pageDoc);
				filesToAppend--;
			}
			Files.write(Paths.get(resultFile), resultDoc.getElementsByTag("result").first().outerHtml().getBytes(UTF_8));
		} catch (Exception e) {
			info.pushLog("Can not create final result file", e);
		}
		info.pushLog("Transformation finished");
	}
	/**
	 * Вставить информацию из страничного документа в результирующий документ
	 * @param resultDoc
	 * @param pageDoc
	 */
	private void insertPart_OLD(Element resultDoc, Document pageDoc) {
		Elements items = pageDoc.select("*[" + ID + "]");
		initElementCache("result");
		for (Element partItem : items) {
			// Элемент результирующего докумнета, в котором происходит поиск добавляемого элемента
			Element insertTo = resultDoc;
			// Предки в прямом порядке
			Elements parents = partItem.getElementsByTag(H_PARENT);
			for (Element parent : parents) {
				String parentId = parent.attr(PARENT);
				Element nextInsertTo = getElementById(insertTo, parentId);
				if (nextInsertTo == null) {
					nextInsertTo = new Element(Tag.valueOf(parent.attr(ELEMENT)), insertTo.baseUri());
					nextInsertTo.attr(ID, parent.attr(PARENT));
					insertTo.appendChild(nextInsertTo);
				}
				insertTo = nextInsertTo;
				parent.remove();
			}
			// Добавление элемента
			String itemId = partItem.attr(ID);
			Element resultItem = getElementById(insertTo, itemId);
			if (resultItem == null) {
				insertTo.append(partItem.outerHtml());
			} else {
				// Добавление всех вложенных элементов по одиночке
				insertTo = resultItem;
				for (Element element : partItem.children()) {
					String action = StringUtils.defaultIfBlank(element.attr(ACTION), IGNORE);
					element.removeAttr(ACTION);
					// Игнорирование
					if (action.equalsIgnoreCase(IGNORE)) {
						if (insertTo.select(">" + element.tagName()).isEmpty()) {
							//insertTo.prepend(element.outerHtml());
							insertTo.prependChild(element);
						}
					}
					// Дописываение
					else if (action.equalsIgnoreCase(APPEND)) {
						//insertTo.append(element.outerHtml());
						insertTo.appendChild(element);
					}
					// Дописывание, если еще нет такого значения
					else if (action.equalsIgnoreCase(APPEND_IF_DIFFERS)) {
						boolean append = true;
						for (Element existing : insertTo.getElementsByTag(element.tagName())) {
							if (StringUtils.equalsIgnoreCase(existing.html(), element.html())) {
								append = false;
								break;
							}
						}
						if (append) {
							//insertTo.append(element.outerHtml());
							insertTo.appendChild(element);
						}
					}
				}
				// ���������� ���� ���������
				String attrAction = StringUtils.defaultIfBlank(insertTo.attr(ATTR_ACTION), IGNORE);
				insertTo.removeAttr(ATTR_ACTION);
				for (Attribute attribute : partItem.attributes()) {
					String currentVal = insertTo.attr(attribute.getKey());
					// �������������
					if (attrAction.equalsIgnoreCase(IGNORE)) {
						if (StringUtils.isBlank(currentVal))
							insertTo.attr(attribute.getKey(), attribute.getValue());
					}
					// ������������
					else if (attrAction.equalsIgnoreCase(APPEND)) {
						if (StringUtils.isBlank(currentVal))
							insertTo.attr(attribute.getKey(), attribute.getValue());
						else
							insertTo.attr(attribute.getKey(), currentVal + "," + attribute.getValue());
					}
					// �����������, ���� ��� ��� ������ ��������
					else if (attrAction.equalsIgnoreCase(APPEND_IF_DIFFERS)) {
						List<String> vals = Arrays.asList(StringUtils.split(currentVal));
						if (!vals.contains(attribute.getValue())) {
							vals.add(attribute.getValue());
							insertTo.attr(attribute.getKey(), StringUtils.join(vals, ","));
						}
					}
				}
			}
		}
	}
	/**
	 * ������� �������� ����� �� ������� ��� ������������� ����
	 * @param url
	 * @return
	 */
	String getStyleForUrl(String url) {
		for (Entry<String, String> entry : urlStyles.entrySet()) {
			if (StringUtils.equals(url, entry.getKey()) || url.matches(entry.getKey()))
				return entry.getValue();
		}
		return null;
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
}
