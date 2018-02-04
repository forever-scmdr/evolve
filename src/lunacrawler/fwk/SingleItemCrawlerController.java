package lunacrawler.fwk;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import net.sf.saxon.TransformerFactoryImpl;
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

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Контроллер парсинга айтемов, заданных урлом в системе управления. Не берется в расчет иерархия каталога
 * и прочее, только один урл - один айтем
 *
 * @author E
 *
 */
public class SingleItemCrawlerController {

	public static final String PARSE_ITEM = "parse_item"; // Название айтема, который подразумевает парсинг

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

	public static final String NUMBER_OF_CRAWLERS = "number_of_crawlers"; // количество параллельных потоков запросов
	public static final String POLITENESS = "politeness"; // количество миллисекунд между запросами
	public static final String PROXIES = "proxies_file"; // файл со списком прокси серверов
	public static final String URLS_PER_PROXY = "urls_per_proxy"; // количество запрошенных урлов перед переключением на следующий прокси
	public static final String URLS = "urls"; // начальный урл, маски урлов и соответствующие им файлы стилей
	public static final String STYLES_DIR = "styles_dir"; // директория, в которой лежат файлы со стилями

	public static final String NO_TEMPLATE = "-";
	public static final String UTF_8 = "UTF-8";

	private static SingleItemCrawlerController singleton = null;

	private Properties props;
	private LinkedList<String> proxies;
	private LinkedHashMap<String, String> urlStyles;
	private int urlsPerProxy;
	private int numberOfCrawlers = 1;
	private String stylesDir;

	private int filesToTransform = 0;
	private int filesToDownload = 0;

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

	private static class DownloadThread<T> implements Runnable {

		private final LinkedList<String> proxies;
		private final int urlsPerProxy;

		private String currentProxy = null;
		private int perProxyCount = 0;

		public DownloadThread(LinkedList<String> proxies, int urlsPerProxy) {
			this.proxies = proxies;
			this.urlsPerProxy = urlsPerProxy;
			this.perProxyCount = 0;
		}

		private void switchProxy() {
			if (proxies != null && proxies.size() > 0 && urlsPerProxy > 0) {
				perProxyCount++;
				if (perProxyCount >= urlsPerProxy) {
					currentProxy = proxies.pop();
					proxies.add(currentProxy);
					perProxyCount = 0;
				}
			}
		}

		protected void processResult()

		@Override
		public void run() {

		}
	}

	private SingleItemCrawlerController() throws Exception {

		// Загрузка настроек
		InputStream is = getClass().getClassLoader().getResourceAsStream(PROPS_RESOURCE);
		props = new Properties();
		if (is != null) {
			props.load(is);
		} else {
			throw new Exception(PROPS_RESOURCE + " file not found");
		}

		// Список прокси серверов
		proxies = new LinkedList<String>();
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
		stylesDir = AppContext.getRealPath(props.getProperty(STYLES_DIR, null));
		if (stylesDir != null && !stylesDir.endsWith("/"))
			stylesDir += "/";
		info.pushLog("Output directories created");
	}
	/**
	 * Выполнить проход по всем доступным урлам согласно настройкам из файлов
	 * @param crawlerClass
	 * @throws Exception
	 */
	private void start(Class<? extends BasicCrawler> crawlerClass,  Mode mode) throws Exception {
		info.pushLog("Current mode : {}", mode);

	}
	
	private void terminateInt() {
		CONTROLLER.shutdown();
	}

	
	public static void terminate() {
		getSingleton().terminateInt();
	}
	/**
	 * Добавить результат парсинга одного URL (вызывается из экземпляра WebCrawler)
	 * Результат записывается в файл с названием ,которое соответствует урлу
	 * @param crawler - кролер, который вернул результат
	 * @param result - результат парсинга
	 */
	void pageProcessed(Page page, String result, BasicCrawler crawler) {
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
	static SingleItemCrawlerController getSingleton() {
		try {
			if (singleton == null)
				singleton = new SingleItemCrawlerController();
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
		buildRresult();
		
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
	/**
	 * Собрать результаты преобразований в один файл
	 */
	public void buildRresult() {
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
				insertPart(resultEl, pageDoc);
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
	private void insertPart(Element resultDoc, Document pageDoc) {
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
			nodeCache = new HashMap<String, Element>();
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
