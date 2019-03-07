package lunacrawler.fwk;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
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
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
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

		public void error(TransformerException exception) {
			errors += "\nERROR " + exception.getMessageAndLocation();
		}

		public void fatalError(TransformerException exception) {
			errors += "\nFATAL ERROR " + exception.getMessageAndLocation();
		}

		public void warning(TransformerException exception) {
			errors += "\nWARNING " + exception.getMessageAndLocation();
		}

		public boolean hasErrors() {
			return !StringUtils.isEmpty(errors);
		}
	}

	private abstract static class DownloadThread implements Runnable {

		private final LinkedList<String> proxies;
		private final int urlsPerProxy;
		private LinkedBlockingDeque<Parse_item> items; // Список должен быть синхронизирован (потокобезопасен)

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

		public void reinit(LinkedBlockingDeque<Parse_item> itemsToProcess) {
			this.items = itemsToProcess;
		}

		private void terminate() {
			this.terminate = true;
		}
	}

	public enum State {
		INIT("Инициализация"), PREPARE_URLS("Подготовка урлов"), HTML("Получение HTML"),
		TRANSFORM("XSL преобразование"), FILES("Загрузка файлов"), FINISHED("Работа закончена");

		private String desc;
		State(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	public static final String ID = "id";
	public static final String DOWNLOAD = "download"; // в этом атрибуте указывается урл на скачивание
	public static final String TYPE = "type"; // Если в элементе стоит type="html", то нужно парсить этот html и скачивать
	public static final String HTML = "html"; // картинки
	public static final String IMG = "img"; // картинки

	public static final String NUMBER_OF_CRAWLERS = "parsing.number_of_crawlers"; // количество параллельных потоков запросов
	public static final String POLITENESS = "parsing.politeness"; // количество миллисекунд между запросами
	public static final String PROXIES = "parsing.proxies_file"; // файл со списком прокси серверов
	public static final String URLS_PER_PROXY = "parsing.urls_per_proxy"; // количество запрошенных урлов перед переключением на следующий прокси
	public static final String URLS = "parsing.urls"; // начальный урл, маски урлов и соответствующие им файлы стилей
	public static final String STYLES_DIR = "parsing.styles_dir"; // директория, в которой лежат файлы со стилями

	public static final String NO_TEMPLATE = "-";
	public static final String UTF_8 = "UTF-8";

	private State state = State.INIT;
	private LinkedList<String> proxies;
	private LinkedHashMap<String, String> urlStyles;
	private LinkedBlockingDeque<Parse_section> sectionsToProcess; // все разделы с айтемами для парскнга
	private Parse_section currentSection;
	private LinkedBlockingDeque<Parse_item> itemsToProcess; // Айтемы, которые нужно парсить
	private ConcurrentHashMap<String, String> sectionUniqueUrls; // Уникальные ULRы в рамках одного раздела
	// Уникальность нужно отслеживать в рамках раздела, а не вообще, т.к. в разных разделах могут быть
	// одинаковые товары
	private final HashSet<DownloadThread> workers;
	private int urlsPerProxy;
	private int numberOfCrawlers = 1;
	private String stylesDir;

	private volatile int toProcessCount = 0;
	private volatile int processedCount = 0;

	private IntegrateBase.Info info = null;

	public SingleItemCrawlerController(IntegrateBase.Info outerInfo) throws Exception {
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
		if (!stylesDir.endsWith("/"))
			stylesDir += "/";
		info.pushLog("Output directories created");

		// Коллекция для айтемов
		itemsToProcess = new LinkedBlockingDeque<>();
		workers = new HashSet<>();
		sectionsToProcess = new LinkedBlockingDeque<>();
		sectionUniqueUrls = new ConcurrentHashMap<>();
	}
	/**
	 * Загрузить все айтемы для обработки и запустить обработку
	 * @throws Exception
	 */
	public void startStage(State...initState) throws Exception {
		info.pushLog("Начало работы");
		if (sectionsToProcess.size() == 0) {
			List<Item> items = new ItemQuery(Parse_section._NAME).loadItems();
			for (Item item : items) {
				sectionsToProcess.add(Parse_section.get(item));
			}
		}
		if (initState.length > 0) {
			this.state = initState[0];
		}
		if (state == State.FINISHED) {
			return;
		}
		if (state == State.INIT) {
			state = State.PREPARE_URLS;
			prepareUrls();
			startStage();
		} else if (state == State.PREPARE_URLS) {
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
			info.pushLog("Работа завершена");
		}
	}

	/**
	 * Сбросить результаты заданной стадии парсинга (и последующих стадий)
	 * @param state
	 * @throws Exception
	 */
	public State resetToStage(State state) throws Exception {
		info.setOperation("Сброс состояния до " + state);
		State nextState = State.INIT;
		if (state == State.HTML || state == State.TRANSFORM || state == State.FILES) {
			List<Item> sections = new ItemQuery(Parse_section._NAME).loadItems();
			int secCount = sections.size();
			info.setLineNumber(secCount);
			for (Item section : sections) {
				List<Item> items = new ItemQuery(Parse_item._NAME).setParentId(section.getId(), false).loadItems();
				info.setToProcess(items.size());
				int i = 0;
				for (Item item : items) {
					Parse_item pitem = Parse_item.get(item);
					if (state == State.PREPARE_URLS) {
						DelayedTransaction.executeSingle(User.getDefaultUser(), ItemStatusDBUnit.delete(pitem));
						nextState = State.INIT;
						info.setProcessed(++i);
						continue;
					} else if (state == State.HTML) {
						pitem.set_downloaded((byte) 0);
						pitem.set_parsed((byte) 0);
						pitem.set_got_files((byte) 0);
						nextState = State.PREPARE_URLS;
					} else if (state == State.TRANSFORM) {
						pitem.set_parsed((byte) 0);
						//pitem.set_got_files((byte) 0);
						nextState = State.HTML;
					} else if (state == State.FILES) {
						pitem.set_got_files((byte) 0);
						pitem.clearValue(Parse_item.FILE);
						nextState = State.TRANSFORM;
					}
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(pitem).noFulltextIndex());
					info.setProcessed(++i);
				}
				info.setLineNumber(--secCount);
			}
		}
		return nextState;
	}

	public void terminate() {
		synchronized (workers) {
			for (DownloadThread worker : workers) {
				worker.terminate();
			}
		}
	}

	private void workerFinished(DownloadThread thread, String paramName) {
		synchronized (workers) {
			workers.remove(thread);
			if (workers.size() == 0) {
				try {
					if (!nextSection(paramName, thread))
						startStage();
				} catch (Exception e) {
					info.addError("Some exception " + e.getMessage(), "shown previously");
				}
			}
		}
	}

	/**
	 * Перейти к рассмотрению следующего раздела каталога для парсинга.
	 * Это значит загрузить айтемы для парсинга, которые соответствуют некоторому критерию (не скачаны,
	 * не разобраны, файлы не скачаны). Критерий может быть не задан, тогда загружаются все айтемы для
	 * парсинга.
	 * Также в этом методе перезапускается поток, который выполняет действия с айтемами для парсинга.
	 * Этот поток завершается для одного раздела и стартует новый поток для следующего раздела.
	 * Поток может быть равным null, тогда он не стартует.
	 * @param paramName
	 * @param worker
	 * @return
	 * @throws Exception
	 */
	private synchronized boolean nextSection(String paramName, DownloadThread worker) throws Exception {
		info.setLineNumber(sectionsToProcess.size());
		itemsToProcess = new LinkedBlockingDeque<>();
		sectionUniqueUrls = new ConcurrentHashMap<>();
		while (itemsToProcess.size() == 0 && sectionsToProcess.size() > 0) {
			currentSection = sectionsToProcess.poll();
			ItemQuery query = new ItemQuery(Parse_item._NAME).setParentId(currentSection.getId(), false);
			if (StringUtils.isNotBlank(paramName)) {
				query
						.addParameterCriteria(paramName, "0", "=", null, Compare.ANY)
						.addParameterCriteria(Parse_item.DUPLICATED, "0", "=", null, Compare.ANY);
			}
			List<Item> items = query.loadItems();
			for (Item item : items) {
				Parse_item pi = Parse_item.get(item);
				String url = StringUtils.trim(pi.get_url());
				if (!sectionUniqueUrls.containsKey(url)) {
					sectionUniqueUrls.put(url, url);
					itemsToProcess.add(pi);
				}
			}
		}
		if (itemsToProcess.size() > 0) {
			toProcessCount = itemsToProcess.size();
			info.setToProcess(toProcessCount);
			processedCount = 0;
			if (worker != null) {
				worker.reinit(itemsToProcess);
				new Thread(worker).start();
			}
			return true;
		}
		currentSection = null;
		return false;
	}

	private void prepareUrls() throws Exception {
		info.setOperation("Удаление старых результатов");
		workers.clear();
		int secCount = sectionsToProcess.size();
		int processedCount = 0;

		// Сначала удаление ранее созданных айтемов для разбора
		info.setToProcess(secCount);
		for (Parse_section section : sectionsToProcess) {
			List<Item> pis = new ItemQuery(Parse_item._NAME).setParentId(section.getId(), false).loadItems();
			for (Item pi : pis) {
				DelayedTransaction.executeSingle(User.getDefaultUser(), ItemStatusDBUnit.delete(pi));
			}
			info.setProcessed(++processedCount);
		}

		// Создание новых айтемов для разбора
		info.setOperation("Подготовка нового списка урлов");
		processedCount = 0;
		info.setProcessed(processedCount);
		ItemType piType = ItemTypeRegistry.getItemType(Parse_item._NAME);
		for (Parse_section section : sectionsToProcess) {
			currentSection = section;
			// Новый список урлов (параметр раздела)
			String urlsStr = currentSection.get_item_urls();
			LinkedHashSet<String> urls = new LinkedHashSet<>();
			String[] split = StringUtils.split(urlsStr, '\n');
			if (split == null || split.length == 0) {
				info.setProcessed(++processedCount);
				continue;
			}
			for (String str : split) {
				urls.add(StringUtils.trim(str));
			}

			// Создание новых айтемов для разбора из урлов с проверкой на уникальность
			for (String url : urls) {
				Parse_item pi = Parse_item.get(Item.newChildItem(piType, currentSection));
				Item original = new ItemQuery(Parse_item._NAME)
						.addParameterCriteria(Parse_item.URL, url, "=", null, Compare.SOME)
						.loadFirstItem();
				pi.set_duplicated(original == null ? (byte) 0 : (byte) 1);
				pi.set_url(url);
				DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(pi));
				if (original == null) {
					info.pushLog("NEW - {}", url);
				} else {
					info.pushLog("DUPLICATED - {}", url);
				}
			}

			// Сохранение урлов в бекап и удаление из раздела для парсинга
			String[] oldUrls = StringUtils.split(currentSection.get_item_urls_backup(), '\n');
			if (!urls.isEmpty() && oldUrls != null && oldUrls.length > 0) {
				urls.add("");
				for (String oldUrl : oldUrls) {
					urls.add(oldUrl);
				}
			}
			currentSection.set_item_urls_backup(StringUtils.join(urls, '\n'));
			currentSection.set_item_urls(null);
			DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(currentSection));

			info.setProcessed(++processedCount);
		}

	}

	/**
	 * Скачать HTML с сайтов
	 * @throws Exception
	 */
	private void downloadHtml() throws Exception {
		info.setOperation("Скачивание HTML файлов");
		workers.clear();
		nextSection(Parse_item.DOWNLOADED, null);
		for (int i = 0; i < numberOfCrawlers; i++) {
			DownloadThread worker = new DownloadThread(proxies, urlsPerProxy, itemsToProcess) {
				@Override
				protected void processItem(Parse_item item, String proxy) throws Exception {
					String html;
					try {
						html = WebClient.getCleanHtml(item.get_url(), proxy);
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
					setTestXSLLink(item);
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(item).noFulltextIndex());
					info.pushLog("URL {} downloaded", item.get_url());
					info.setProcessed(++processedCount);
				}

				@Override
				protected void afterFinished() {
					workerFinished(this, Parse_item.DOWNLOADED);
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
		workers.clear();
		nextSection(Parse_item.PARSED, null);
		for (int i = 0; i < numberOfCrawlers; i++) {
			DownloadThread worker = new DownloadThread(proxies, urlsPerProxy, itemsToProcess) {
				@Override
				protected void processItem(Parse_item item, String proxy) throws Exception {
					File xslFile = new File(stylesDir + getStyleForUrl(item.get_url()));
					if (!xslFile.exists()) {
						info.pushLog("No xsl file '{}' found", stylesDir + getStyleForUrl(item.get_url()));
						return;
					}
					if (StringUtils.isBlank(item.get_html())) {
						info.pushLog("Item with url '{}' has no content", item.get_url());
						return;
					}
					Errors errors = new Errors();
					TransformerFactory factory = TransformerFactoryImpl.newInstance();
					Transformer transformer;
					try {
						factory.setErrorListener(errors);
						transformer = factory.newTransformer(new StreamSource(xslFile));

						// Подготовка HTML (убирание необъявленных сущностей и т.д.)
						//Document jsoupDoc = Jsoup.parse(item.get_html());
						//String html = JsoupUtils.outputHtmlDoc(jsoupDoc);
						String html = Strings.cleanHtml(item.get_html());

						// Преборазование очищенного HTML
						Reader reader = new StringReader(html);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						transformer.transform(new StreamSource(reader), new StreamResult(bos));
						item.set_xml(bos.toString(UTF_8));
						item.set_parsed((byte) 1);
						setTestXSLLink(item);
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(item).noFulltextIndex());
						info.pushLog("URL {} transformed", item.get_url());
						info.setProcessed(++processedCount);
					} catch (TransformerConfigurationException e) {
						info.pushLog(errors.errors, e.getMessageAndLocation());
					} catch (TransformerException e) {
						info.pushLog("Transforming error: {} message: {}", item.get_url(), e.getMessageAndLocation());
					} catch (UnsupportedEncodingException e) {
						info.pushLog("Unsupported charset {}", e);
					} catch (Exception e) {
						info.pushLog("Error saving XML of an url: {} message {}", item.get_url(), e);
					}
					if (errors.hasErrors()) {
						info.pushLog("There were errors while transforming source html file {}", item.get_url());
						info.pushLog(errors.errors);
					}
				}

				@Override
				protected void afterFinished() {
					workerFinished(this, Parse_item.PARSED);
				}
			};
			workers.add(worker);
			new Thread(worker).start();
		}
	}

	private void downloadFiles() throws Exception {
		info.setOperation("Скачивание файлов");
		workers.clear();
		nextSection(Parse_item.GOT_FILES, null);
		for (int i = 0; i < numberOfCrawlers; i++) {
			DownloadThread worker = new DownloadThread(proxies, urlsPerProxy, itemsToProcess) {
				@Override
				protected void processItem(Parse_item item, String proxy) throws Exception {
					Document result = Jsoup.parse(item.get_xml());
					try {
						// Прямые загрузки (download="url")
						Elements downloads = result.getElementsByAttribute(DOWNLOAD);
						for (Element download : downloads) {
							URL url = new URL(normalizeDownloadUrl(download.attr(DOWNLOAD), item.get_url()));
							item.setValue(Parse_item.FILE, url);
						}

						// HTML с картинками (img)
						Elements htmls = result.getElementsByAttributeValue(TYPE, HTML);
						for (Element html : htmls) {
							Elements imgs = html.getElementsByTag("img");
							for (Element img : imgs) {
								URL url = new URL(normalizeDownloadUrl(img.attr("src"), item.get_url()));
								item.setValue(Parse_item.HTML_PIC, url);
							}
						}

						item.set_got_files((byte) 1);
						DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(item).noFulltextIndex());
						info.pushLog("URL {} got files", item.get_url());
						info.setProcessed(++processedCount);
					} catch (Exception e) {
						info.pushLog("Error while dowloading files. Url: " + item.get_url(), e);
					}
				}

				@Override
				protected void afterFinished() {
					workerFinished(this, Parse_item.GOT_FILES);
				}
			};
			workers.add(worker);
			new Thread(worker).start();
		}
	}

	private static String normalizeDownloadUrl(String fileUrl, String mainParseUrl) {
		if (StringUtils.startsWith(fileUrl, "//")) {
			String protocol = StringUtils.substringBefore(mainParseUrl, "//");
			return protocol + fileUrl;
		}
		URI picUri = URI.create(fileUrl);
		if (!picUri.isAbsolute()) {
			URI baseSiteUri = URI.create(mainParseUrl);
			return baseSiteUri.getScheme() + "://" + baseSiteUri.getHost() + fileUrl;
		}
		return fileUrl;
	}


	/**
	 * Выбирает нужный XSL файл для преобразования HTML, полученного с заданного урла
	 * @param url
	 * @return
	 */
	public String getStyleForUrl(String url) {
		for (Entry<String, String> entry : urlStyles.entrySet()) {
			if (StringUtils.equals(url, entry.getKey()) || url.matches(entry.getKey()))
				return entry.getValue();
		}
		return null;
	}

	private void setTestXSLLink(Parse_item pi) {
		pi.set_test_url("test_parse_item?pi=" + pi.getId());
	}

	public static void main(String[] args) {
		System.out.println(normalizeDownloadUrl("/files/megafile.xml", "http://petronik.ru/catalog/id/218/"));
	}
}
