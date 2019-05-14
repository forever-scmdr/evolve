package ecommander.controllers;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.*;
import ecommander.pages.ResultPE.ResultType;
import ecommander.pages.output.PageWriter;
import ecommander.pages.var.StaticVariable;
import ecommander.pages.var.Variable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
/**
 * Контроллер кэша
 * @author EEEE
 *
 */
public class PageController {
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	
	private String requestUrl; // для работы кеша
	private final String domainName; // для работы кеша
	private final boolean useCache; // надо ли использовать кеш
	private ByteArrayOutputStream out; // куда выводится результирующий документ
	private ExecutablePagePE page; // страница для выполнения
	private String contentType; // тип данных ответа

	private boolean loadedFromCache = false; // была ли загрузка из кеша (или из БД)

	private PageController(String requestUrl, String domainName, boolean useCache) {
		this.requestUrl = requestUrl;
		this.domainName = domainName;
		this.useCache = useCache;
	}
	
	static PageController newUsingCache(String requestUrl, String domainName) {
		return new PageController(requestUrl, domainName, true);
	}
	
	public static PageController newSimple() {
		return new PageController(null, null, false);
	}
	/**
	 * Очистить буфер Должен вызываться после того, как были произведены изменения в админской части, 
	 * либо был изменен соответствующий xsl файл.
	 * @throws IOException 
	 */
	public synchronized static void clearCache() {
		if (AppContext.isCacheEnabled()) {
			File cacheHtmlDir = new File(AppContext.getCacheHtmlDirPath());
			File cacheXmlDir = new File(AppContext.getCacheXmlDirPath());
			ArrayList<File> files = new ArrayList<>();
			File[] html = cacheHtmlDir.listFiles();
			File[] xml = cacheXmlDir.listFiles();
			if (html != null) Collections.addAll(files, html);
			if (xml != null) Collections.addAll(files, xml);
			for (File file : files) {
				if (file.isDirectory())
					FileUtils.deleteQuietly(file);
				else
					file.delete();
			}
		}
	}
	
	void processPage(ExecutablePagePE page, HttpServletResponse resp) throws Exception {
		this.page = page;
		this.out = new ByteArrayOutputStream();
		String result = processPageInt(page);
		out.flush();
		out.close();
		// Дополнительные заголовки
		Map<String, String> headers = this.page.getResponseHeaders();
		for (String header : headers.keySet()) {
			if (StringUtils.equalsIgnoreCase(header, CONTENT_TYPE_HEADER)) {
				contentType = headers.get(header);
			} else {
				resp.setHeader(header, headers.get(header));
			}
		}
		// Переменные, хранящиеся в куки
		page.getSessionContext().flushCookies(resp);
		// Проверить, найден ли критический айтем. Если нет - вернуть ошибку 404
		// При загрузке из кеша айтем никогда не содержит найденные, поэтому исключить
		// эту ситуацию
		if (page.hasCriticalItem() && !loadedFromCache) {
			ExecutableItemPE pageItem = page.getItemPEById(page.getCriticalItem());
			if (!pageItem.hasFoundItems() && !pageItem.isLoadedFromCache()) {
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
		}
		// Редирект, т.к. форвард уже был выполнен в методе processPageInt
		if (result != null) {
			// Внешняя ссылка
			if (result.startsWith("http://") || result.startsWith("https://")) {
				resp.sendRedirect(result);
			} 
			// Внутренняя ссылка
			else {
				if (!page.getUrlBase().endsWith("/"))
					result = "/" + result;
				resp.sendRedirect(page.getUrlBase() + result);
			}
		} else {
			resp.setContentType(contentType);
			resp.setCharacterEncoding("UTF-8");
			out.writeTo(resp.getOutputStream());
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
		}
	}
	
	public void executePage(ExecutablePagePE page, ByteArrayOutputStream out) throws Exception {
		this.out = out;
		processPageInt(page);
		this.out.flush();
		this.out.close();
	}
	
	private String processPageInt(ExecutablePagePE page) throws Exception {
		this.page = page;
		if (useCache)
			return processCacheablePage();
		else
			return processSimplePage(true);
	}
	
	/**
	 * Если в буфере есть страница, то этот метод ее возвращает, если нет, то генерирует, возвращает и записывает в буфер
	 * 
	 * @throws IOException
	 * @throws Exception
	 * @return
	 */
	private String processCacheablePage() throws Exception {
		String xslFileName = AppContext.getStylesDirPath() + page.getTemplate() + ".xsl";
		long timeStart = System.currentTimeMillis();
		if (AppContext.isCacheEnabled() && page.isCacheable()) {
			String cacheFileName = page.getCacheableId();
			cacheFileName = domainName + "/" + page.getSessionContext().getUser().getGroupRolesStr() + "/" + cacheFileName + ".html";
			String fullFileName = AppContext.getCacheHtmlDirPath() + "/" + cacheFileName;
			if (fullFileName.length() >= 255) {
				int hash = fullFileName.hashCode();
				fullFileName = StringUtils.substring(fullFileName, 0, 230);
				fullFileName += hash + ".html";
			}
			File cachedFile = new File(fullFileName);
			File xslFile = new File(xslFileName);
			if (cachedFile.exists() && xslFile.lastModified() < cachedFile.lastModified() && cachedFile.length() > 0) {
				Timer.getTimer().start(Timer.GET_FROM_CACHE);
				Files.copy(cachedFile.toPath(), out);
				out.flush();
				ServerLogger.debug("CACHE: " + requestUrl + " SENT IN " + (System.currentTimeMillis() - timeStart) + " MILLIS");
				Timer.getTimer().stop(Timer.GET_FROM_CACHE);
				loadedFromCache = true;
			} else {
				if (cachedFile.exists())
					cachedFile.delete();
				// Создать директорию для кэшированных файлов, если нельзя создать файл
				cachedFile.getParentFile().mkdirs();
				cachedFile.createNewFile();
				// Выполняем страницу
				String redirectUrl = processSimplePage(true);
				Timer.getTimer().start(Timer.GENERATE_CACHE);
				try {
					FileOutputStream fos = new FileOutputStream(cachedFile, false);
					out.writeTo(fos);
					fos.flush();
					fos.close();
				} catch (FileNotFoundException fnf) {
					ServerLogger.error("File " + cachedFile + " can not be created", fnf);
				}
				Timer.getTimer().stop(Timer.GET_FROM_CACHE);
				ServerLogger.debug("DYNAMIC: " + requestUrl + " GENERATED IN " + (System.currentTimeMillis() - timeStart) + " MILLIS");
				// Редирект в случае если он нужен
				if (redirectUrl != null) {
					return redirectUrl;
				}
			}
		} else {
			String redirectUrl = processSimplePage(true);
			// Редирект в случае если он нужен
			if (redirectUrl != null) {
				return redirectUrl;
			}
		}
		return null;
	}

	/**
	 * Вывести содержимое страницы в поток вывода. Страница полностью выполнятеся, учитывается только кеш XML
	 * Возвращается ссылка для внешнего перехода, если он должен быть осуществлен. Иначе возвращается null
	 * @return - ссылка для внешнего редиректа
	 * @throws IOException
	 * @throws Exception
	 */
	private String processSimplePage(boolean redo) throws Exception {
		String xslFileName = AppContext.getStylesDirPath() + page.getTemplate() + ".xsl";
		// Загрузка страницы и выполнение команд страницы
		Timer.getTimer().start(Timer.LOAD_DB_ITEMS);
		// Сброс сеансового генератора ID (чтобы для каждого выпонения страницы ID начинались с одного и того же начального)
		if (page.getSessionContext() != null)
			page.getSessionContext().resetIdGenerator();
		ResultPE result = page.execute();
		Timer.getTimer().stop(Timer.LOAD_DB_ITEMS);
		// Если команда требует очисти кеша, очистить его
		if (page.isCacheClearNeeded())
			clearCache();
		// Работа с результатом выполнения страницы
		if (result != null) {
			// Результат выполнения - XML документ
			if (result.getType() == ResultType.xml/* && !StringUtils.isBlank(result.getValue())*/) {
				XmlDocumentBuilder xml = XmlDocumentBuilder.newDocFull(result.getValue());
				if (page.transformationNeeded()) {
					XmlXslOutputController.outputXmlTransformed(out, xml, xslFileName);
					contentType = "text/html";
				} else {
					XmlXslOutputController.outputXml(out, xml);
					contentType = "application/xml";
				}
			}
			// Результат - простой текст, не требующий преобразований
			else if (result.getType() == ResultType.plain_text && !StringUtils.isBlank(result.getValue())) {
				byte[] data = result.getValue().getBytes(StandardCharsets.UTF_8);
				out.write(data);
				contentType = "text/plain";
			}
			// Результат выполнения - ссылка
			// Выполняется либо внутренний (forward) либо внешний (redirect) переход на новую страницу
			else {
				LinkPE baseLink = page.getLink(result.getName());
				// Если указанная ссылка не найдена на странице, значит значение результата само являестя ссылкой
				// Если ссылка не парсится, значит она не в формате CMS и добавить в нее переменные не получится - 
				// вернут ее в неизменном виде
				if (baseLink == null) {
					try {
						baseLink = LinkPE.parseLink(result.getValue());
					} catch (Exception e) {
						return result.getValue();
					}
				}
				// Если нет такой страницы на сайте - вернуть просто как ссылку
				if (PageModelRegistry.getRegistry().getPageModel(baseLink.getPageName()) == null)
					return result.getValue();
				// Сериализовать и спарсить заново ссылку, чтобы в случае если имя страницы было урлом, 
				// корректно добавлялись бы значения переменных TODO <fix> перенести в класс LinkPE
				else {
					baseLink = LinkPE.parseLink(baseLink.serialize());
				}
				// Если результат выполнения - динамическая ссылка - установить дополнительные переменные в ссылку
				if (result.hasVariables()) {
					for (StaticVariable var : result.getVariables()) {
						if (var.isEmpty()) {
							baseLink.removeVariable(var.getName());
						} else {
							for (Object val : var.getAllValues()) {
								baseLink.addStaticVariable(var.getName(), val.toString());
							}
						}
					}
				}
				// Это надо для того, чтобы все переменные в ссылке сделать статическими
				String linkUrl = baseLink.serialize();
				if (result.getType() == ResultType.forward) {
					// Загрузка страницы
					ExecutablePagePE newPage = PageModelRegistry.testAndGetRegistry().getExecutablePage(linkUrl, page.getUrlBase(),
							page.getSessionContext());
					newPage.setPostData(page.getItemFrom());
					return processPageInt(newPage);
				} else {
					return linkUrl;
				}
			}
		}
		// Иначе - вывести содержимое страницы
		else {
			try {
				XmlDocumentBuilder xml = new PageWriter(page).generateXml();
				if (page.transformationNeeded()) {
					XmlXslOutputController.outputXmlTransformed(out, xml, xslFileName);
					contentType = "text/html";
				} else {
					XmlXslOutputController.outputXml(out, xml);
					contentType = "application/xml";
				}
			} catch (Exception e) {
				if (redo) {
					ServerLogger.error("Page transfor error", e);
					clearCache();
					Variable pageUrlVar = page.getVariable(ExecutablePagePE.PAGEURL_VALUE);
					String pageUrl = pageUrlVar == null ? "" : pageUrlVar.writeSingleValue();
					LinkPE requestLink = page.getRequestLink();
					String urlBase = page.getUrlBase();
					page = PageModelRegistry.testAndGetRegistry().getExecutablePage(requestUrl, urlBase, page.getSessionContext());
					processSimplePage(false);
				} else {
					throw e;
				}
			}
		}
		return null;
	}
}