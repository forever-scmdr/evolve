package lunacrawler.fwk;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.WebClient;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Parse_item;
import net.sf.saxon.TransformerFactoryImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Контроллер парсинга айтемов, заданных урлом в системе управления. Не берется в расчет иерархия каталога
 * и прочее, только один урл - один айтем
 *
 * @author E
 *
 */
public class SingleItemCrawlerController {

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

	private abstract static class DownloadThread implements Runnable {

		private final LinkedList<String> proxies;
		private final int urlsPerProxy;
		private final LinkedBlockingDeque<Parse_item> items; // Список должен быть синхронизирован (потокобезопасен)

		private String currentProxy = null;
		private int perProxyCount = 0;
		private volatile boolean terminate = false;

		public DownloadThread(LinkedList<String> proxies, int urlsPerProxy, LinkedBlockingDeque<Parse_item> itemsToProcess) {
			this.proxies = proxies;
			this.urlsPerProxy = urlsPerProxy;
			this.perProxyCount = 0;
			this.items = itemsToProcess;
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

		protected abstract void processItem(Parse_item item, String proxy) throws Exception;

		protected abstract void afterFinished();

		@Override
		public void run() {
			while (items.size() > 0) {
				if (terminate)
					return;
				Parse_item item;
				try {
					item = items.pop();
				} catch (NoSuchElementException e) {
					break;
				}
				switchProxy();
				try {
					processItem(item, currentProxy);
				} catch (Exception e) {
					ServerLogger.error("Error while processing parsed item", e);
				}
			}
			afterFinished();
		}

		private void terminate() {
			this.terminate = true;
		}
	}

	public enum State {
		INIT("Инициализация"), HTML("Получение HTML"), TRANSFORM("XSL преобразование"),
		FILES("Загрузка файлов"), FINISHED("Работа закончена");

		private String desc;
		State(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	public static final String ID = "id";
	public static final String DOWNLOAD = "download";

	public static final String NUMBER_OF_CRAWLERS = "parsing.number_of_crawlers"; // количество параллельных потоков запросов
	public static final String POLITENESS = "parsing.politeness"; // количество миллисекунд между запросами
	public static final String PROXIES = "parsing.proxies_file"; // файл со списком прокси серверов
	public static final String URLS_PER_PROXY = "parsing.urls_per_proxy"; // количество запрошенных урлов перед переключением на следующий прокси
	public static final String URLS = "parsing.urls"; // начальный урл, маски урлов и соответствующие им файлы стилей
	public static final String STYLES_DIR = "parsing.styles_dir"; // директория, в которой лежат файлы со стилями

	public static final String NO_TEMPLATE = "-";
	public static final String UTF_8 = "UTF-8";

	private static SingleItemCrawlerController singleton = null;

	private State state = State.INIT;
	private LinkedList<String> proxies;
	private LinkedHashMap<String, String> urlStyles;
	private LinkedBlockingDeque<Parse_item> itemsToProcess;
	private final HashSet<DownloadThread> workers;
	private int urlsPerProxy;
	private int numberOfCrawlers = 1;
	private String stylesDir;

	private volatile int toProcessCount = 0;
	private volatile int processedCount = 0;

	private IntegrateBase.Info info = null;

	private SingleItemCrawlerController(IntegrateBase.Info outerInfo) throws Exception {
		this.info = outerInfo;

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
		String urlFileName = AppContext.getRealPath(AppContext.getProperty(URLS, null));
		if (new File(urlFileName).exists()) {
			try(BufferedReader br = new BufferedReader(new FileReader(new File(urlFileName)))) {
			    int lineNum = 1;
				for(String line; (line = br.readLine()) != null; lineNum++) {
			        line = line.trim();
			    	if (!StringUtils.isBlank(line) && !line.startsWith("#")) {
			        	String[] parts = StringUtils.split(line, ' ');
			        	if (parts.length == 1) {
			        		URL url = new URL(parts[0]);
			        		//String seed = URLCanonicalizer.getCanonicalURL(parts[0]);
			        		urlStyles.put(url.toString(), NO_TEMPLATE);
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
		urlsPerProxy = Integer.parseInt(AppContext.getProperty(URLS_PER_PROXY, "0"));
		numberOfCrawlers = Integer.parseInt(AppContext.getProperty(NUMBER_OF_CRAWLERS, "1"));
		stylesDir = AppContext.getRealPath(AppContext.getProperty(STYLES_DIR, null));
		if (stylesDir != null && !stylesDir.endsWith("/"))
			stylesDir += "/";
		info.pushLog("Output directories created");

		// Коллекция для айтемов
		itemsToProcess = new LinkedBlockingDeque<>();
		workers = new HashSet<>();
	}
	/**
	 * Загрузить все айтемы для обработки и запустить обработку
	 * @throws Exception
	 */
	public void startStage(State...initState) throws Exception {
		info.pushLog("Начало работы");
		if (initState.length > 0) {
			this.state = initState[0];
		}
		if (state == State.FINISHED) {
			return;
		}
		if (state == State.INIT) {
			state = State.HTML;
			downloadHtml();
		} else if (state == State.HTML) {
			state = State.TRANSFORM;
			parseHtml();
		} else if (state == State.TRANSFORM) {
			state = State.FILES;
			downloadFiles();
		} else if (state == State.FILES) {
			state = State.FINISHED;
		}
		info.pushLog("Работа завершена");
	}

	/**
	 * Сбросить результаты заданной стадии парсинга (и последующих стадий)
	 * @param state
	 * @throws Exception
	 */
	public void resetToStage(State state) throws Exception {
		if (state == State.HTML || state == State.TRANSFORM || state == State.FILES) {
			List<Item> items = new ItemQuery(ItemNames.PARSE_ITEM).loadItems();
			for (Item item : items) {
				Parse_item pitem = Parse_item.get(item);
				if (state == State.HTML) {
					pitem.set_downloaded((byte) 0);
					pitem.set_parsed((byte) 0);
					pitem.set_got_files((byte) 0);
				} else if (state == State.TRANSFORM) {
					pitem.set_parsed((byte) 0);
					pitem.set_got_files((byte) 0);
				} else if (state == State.FILES) {
					pitem.set_got_files((byte) 0);
				}
				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(pitem));
			}
		}
	}

	private void terminateInt() {
		synchronized (workers) {
			for (DownloadThread worker : workers) {
				worker.terminate();
			}
		}
	}

	private void workerFinished(DownloadThread thread) {
		synchronized (workers) {
			workers.remove(thread);
			if (workers.size() == 0) {
				try {
					startStage();
				} catch (Exception e) {
					info.addError("Some exception " + e.getMessage(), "shown previously");
				}
			}
		}
	}

	/**
	 * Скачать HTML с сайтов
	 * @throws Exception
	 */
	private void downloadHtml() throws Exception {
		info.setOperation("Скачивание HTML файлов");
		itemsToProcess = new LinkedBlockingDeque<>();
		ArrayList<Item> items = ItemQuery.loadByParamValue(ItemNames.PARSE_ITEM, ItemNames.parse_item.DOWNLOADED, "0");
		for (Item item : items) {
			itemsToProcess.add(Parse_item.get(item));
		}
		toProcessCount = itemsToProcess.size();
		processedCount = 0;
		for (int i = 0; i < numberOfCrawlers; i++) {
			DownloadThread worker = new DownloadThread(proxies, urlsPerProxy, itemsToProcess) {
				@Override
				protected void processItem(Parse_item item, String proxy) throws Exception {
					String html;
					try {
						html = WebClient.getString(item.get_url(), proxy);
					} catch (HttpResponseException re) {
						info.addError("Url status code " + re.getStatusCode(), item.get_url());
						return;
					} catch (Exception e) {
						info.addError("Unknown download problem: " + e.getMessage(), item.get_url());
						return;
					}
					if (StringUtils.isEmpty(html)) {
						info.addError("Empty response", item.get_url());
						return;
					}
					item.set_html(html);
					item.set_downloaded((byte) 1);
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(item));
					info.pushLog("URL {} downloaded", item.get_url());
					info.setProcessed(++processedCount);
				}

				@Override
				protected void afterFinished() {
					workerFinished(this);
				}
			};
			workers.add(worker);
			new Thread(worker).start();
		}
	}

	/**
	 * Преборазовать HTML в XML
	 * @throws Exception
	 */
	private void parseHtml() throws Exception {
		info.setOperation("Преобразование HTML в XML");
		ArrayList<Item> items = ItemQuery.loadByParamValue(ItemNames.PARSE_ITEM, ItemNames.parse_item.PARSED, "0");
		ArrayList<Parse_item> parseItems = new ArrayList<>();
		for (Item item : items) {
			parseItems.add(Parse_item.get(item));
		}
		toProcessCount = parseItems.size();
		processedCount = 0;
		for (Parse_item item : parseItems) {
			File xslFile = new File(stylesDir + getStyleForUrl(item.get_url()));
			if (!xslFile.exists()) {
				info.pushLog("No xsl file '{}' found", stylesDir + getStyleForUrl(item.get_url()));
				continue;
			}
			if (StringUtils.isBlank(item.get_html())) {
				info.pushLog("Item with url '{}' has no content", item.get_url());
				continue;
			}
			Errors errors = new Errors();
			TransformerFactory factory = TransformerFactoryImpl.newInstance();
			Transformer transformer;
			try {
				info.pushLog("Transforming: {}\tTo transform: {}", item.get_url(), toProcessCount);
				factory.setErrorListener(errors);
				transformer = factory.newTransformer(new StreamSource(xslFile));
				Reader reader = new StringReader(item.get_html());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				transformer.transform(new StreamSource(reader), new StreamResult(bos));
				item.set_xml(bos.toString(UTF_8));
				item.set_parsed((byte) 1);
				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(item));
				info.pushLog("URL {} transformed", item.get_url());
				info.setProcessed(++processedCount);
			} catch (TransformerConfigurationException e) {
				info.pushLog(errors.errors, e);
			} catch (TransformerException e) {
				info.pushLog("Error while transforming html input. Url: " + item.get_url(), e);
			} catch (UnsupportedEncodingException e) {
				info.pushLog("Unsupported charset", e);
			} catch (Exception e) {
				info.pushLog("Error saving XML of an url: " + item.get_url(), e);
			}
			if (errors.hasErrors()) {
				info.pushLog("There were errors while transforming source html file {}", item.get_url());
				info.pushLog(errors.errors);
			}
		}
	}

	private void downloadFiles() throws Exception {
		info.setOperation("Скачивание файлов");
		ArrayList<Item> items = ItemQuery.loadByParamValue(ItemNames.PARSE_ITEM, ItemNames.parse_item.GOT_FILES, "0");
		itemsToProcess = new LinkedBlockingDeque<>();
		for (Item item : items) {
			itemsToProcess.add(Parse_item.get(item));
		}
		toProcessCount = itemsToProcess.size();
		processedCount = 0;
		for (int i = 0; i < numberOfCrawlers; i++) {
			DownloadThread worker = new DownloadThread(proxies, urlsPerProxy, itemsToProcess) {
				@Override
				protected void processItem(Parse_item item, String proxy) throws Exception {
					Document result = Jsoup.parse(item.get_xml());
					Elements downloads = result.getElementsByAttribute(DOWNLOAD);
					for (Element download : downloads) {
						URL url = new URL(download.attr(DOWNLOAD));
						item.setValue(ItemNames.parse_item.FILE, url);
					}
					try {
						item.set_got_files((byte) 1);
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(item));
						info.pushLog("URL {} got files", item.get_url());
						info.setProcessed(++processedCount);
					} catch (Exception e) {
						info.pushLog("Error while dowloading files. Url: " + item.get_url(), e);
					}
				}

				@Override
				protected void afterFinished() {
					workerFinished(this);
				}
			};
			workers.add(worker);
			new Thread(worker).start();
		}
	}

	public static void terminate() {
		if (singleton != null)
			singleton.terminateInt();
	}

	/**
	 * Вернуть контроллер
	 * @return
	 */
	public static SingleItemCrawlerController getSingleton(IntegrateBase.Info info) {
		try {
			if (singleton == null)
				singleton = new SingleItemCrawlerController(info);
			return singleton;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Выбирает нужный XSL файл для преобразования HTML, полученного с заданного урла
	 * @param url
	 * @return
	 */
	private String getStyleForUrl(String url) {
		for (Entry<String, String> entry : urlStyles.entrySet()) {
			if (StringUtils.equals(url, entry.getKey()) || url.matches(entry.getKey()))
				return entry.getValue();
		}
		return null;
	}
}
