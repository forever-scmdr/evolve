package ecommander.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.common.Timer;
import ecommander.controllers.output.PageWriter;
import ecommander.controllers.output.XmlDocumentBuilder;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.PageModelRegistry;
import ecommander.pages.elements.ResultPE;
import ecommander.pages.elements.ResultPE.ResultType;
import ecommander.pages.elements.variables.VariablePE;
/**
 * Контроллер кэша
 * @author EEEE
 *
 */
public class PageController {
	
	private String requestUrl; // для работы кеша
	private final String domainName; // для работы кеша
	private final boolean useCache; // надо ли использовать кеш
	private ByteArrayOutputStream out; // куда выводится результирующий документ
	private ExecutablePagePE page; // страница для выполнения
	
	
	private PageController(String requestUrl, String domainName, boolean useCache) {
		this.requestUrl = requestUrl;
		this.domainName = domainName;
		this.useCache = useCache;
	}
	
	public static PageController newUsingCache(String requestUrl, String domainName) {
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
			ArrayList<File> files = new ArrayList<File>();
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
	
	public void processPage(ExecutablePagePE page, HttpServletResponse resp) throws Exception {
		this.page = page;
		this.out = new ByteArrayOutputStream();
		String result = processPageInt(page);
		out.flush();
		out.close();
		// Дополнительные заголовки
		Map<String, String> headers = this.page.getResponseHeaders();
		for (String header : headers.keySet()) {
			resp.setHeader(header, headers.get(header));
		}
		// Переменные, хранящиеся в куки
		page.getSessionContext().flushCookies(resp);
		// Редирект, т.к. форвард уже был выполнен в методе processPageInt
		if (result != null) {
			// Внешняя ссылка
			if (result.startsWith("http://")) {
				resp.sendRedirect(result);
			} 
			// Внутренняя ссылка
			else {
				if (!page.getUrlBase().endsWith("/"))
					result = "/" + result;
				resp.sendRedirect(page.getUrlBase() + result);
			}
		} else {
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
	
	private String processPageInt(ExecutablePagePE page) throws IOException, Exception {
		this.page = page;
		if (useCache)
			return processCacheablePage();
		else
			return processSimplePage();
	}
	
	/**
	 * Если в буфере есть страница, то этот метод ее возвращает, если нет, то генерирует, возвращает и записывает в буфер
	 * 
	 * @param url
	 * @param out
	 * @param pageGenerator
	 * @throws IOException
	 * @throws Exception
	 */
	private String processCacheablePage() throws Exception {
		String xslFileName = AppContext.getStylesDirPath() + page.getTemplate() + ".xsl";
		long timeStart = System.currentTimeMillis();
		if (AppContext.isCacheEnabled() && page.isCacheable()) {
			// Удалить часть, добавляемую аяксом (_=123456789123123)
			if (StringUtils.contains(requestUrl, "_=")) {
				int i = requestUrl.indexOf("_=");
				requestUrl = requestUrl.substring(0, i);
			}
			requestUrl = StringUtils.replaceChars(requestUrl, '|', '_');
			requestUrl = StringUtils.replaceChars(requestUrl, '/', '_');
			requestUrl = StringUtils.replaceChars(requestUrl, '?', '_');
			String cacheFileName = domainName + "/" + page.getSessionContext().getUser().getGroup() + "/" + requestUrl + ".html";
			File cachedFile = new File(AppContext.getCacheHtmlDirPath() + cacheFileName);
			File xslFile = new File(xslFileName);
			if (cachedFile.exists() && xslFile.lastModified() < cachedFile.lastModified() && cachedFile.length() > 0) {
				Timer.getTimer().start(Timer.GET_FROM_CACHE);
				FileInputStream fis = new FileInputStream(cachedFile);
				byte[] buffer = new byte[4096];
				int byteCount = 0;
				while ((byteCount = fis.read(buffer)) >= 0) {
					out.write(buffer, 0, byteCount);
				}
				fis.close();
				ServerLogger.debug("CACHE: " + requestUrl + " SENT IN " + (System.currentTimeMillis() - timeStart) + " MILLIS");
				Timer.getTimer().stop(Timer.GET_FROM_CACHE);
			} else {
				if (cachedFile.exists())
					cachedFile.delete();
				// Создать директорию для кэшированных файлов, если нельзя создать файл
				cachedFile.getParentFile().mkdirs();
				cachedFile.createNewFile();
				// Выполняем страницу
				String redirectUrl = processSimplePage();
				Timer.getTimer().start(Timer.GENERATE_CACHE);
				FileOutputStream fos = new FileOutputStream(cachedFile);
				out.writeTo(fos);
				fos.flush();
				fos.close();
				Timer.getTimer().stop(Timer.GET_FROM_CACHE);
				ServerLogger.debug("DYNAMIC: " + requestUrl + " GENERATED IN " + (System.currentTimeMillis() - timeStart) + " MILLIS");
				// Редирект в случае если он нужен
				if (redirectUrl != null) {
					return redirectUrl;
				}
			}
		} else {
			String redirectUrl = processSimplePage();
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
	 * @param page
	 * @param out
	 * @return - ссылка для внешнего редиректа
	 * @throws IOException
	 * @throws Exception
	 */
	private String processSimplePage() throws IOException, Exception {
		String xslFileName = AppContext.getStylesDirPath() + page.getTemplate() + ".xsl";
		// Загрузка страницы и выполнение команд страницы
		Timer.getTimer().start(Timer.LOAD_DB_ITEMS);
		ResultPE result = page.execute();
		Timer.getTimer().stop(Timer.LOAD_DB_ITEMS);
		// Если команда требует очисти кеша, очистить его
		if (page.isCacheClearNeeded())
			clearCache();
		// Работа с результатом выполнения страницы
		if (result != null && result.getType() != ResultType.none) {
			// Результат выполнения - XML документ
			if (result.getType() == ResultType.xml && !StringUtils.isBlank(result.getValue())) {
				XmlDocumentBuilder xml = XmlDocumentBuilder.newDocFull(result.getValue());
				if (page.transformationNeeded())
					XmlXslOutputController.outputXmlTransformed(out, xml, xslFileName);
				else
					XmlXslOutputController.outputXml(out, xml);
			}
			// Результат - простой текст, не требующий преобразований
			else if (result.getType() == ResultType.plain_text && !StringUtils.isBlank(result.getValue())) {
				byte[] data = result.getValue().getBytes(StandardCharsets.UTF_8);
				out.write(data);
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
				// Сериализовать и спарсить заново ссылку, чтобы в случае если имя страницы было урлом, 
				// корректно добавлялись бы значения переменных TODO <fix> перенести в класс LinkPE
				else {
					baseLink = LinkPE.parseLink(baseLink.serialize());
				}
				// Если результат выполнения - динамическая ссылка - установить дополнительные переменные в ссылку
				if (result.hasVariables()) {
					for (VariablePE var : result.getVariables()) {
						if (var.isEmpty())
							baseLink.removeVariable(var.getName());
						else
							baseLink.addVariable(var);
					}
				}
				// Это надо для того, чтобы все переменные в ссылке сделать статическими
				String linkUrl = baseLink.serialize();
				if (result.getType() == ResultType.forward) {
					// Загрузка страницы
					ExecutablePagePE newPage = PageModelRegistry.testAndGetRegistry().getExecutablePage(linkUrl, page.getUrlBase(),
							page.getSessionContext());
					return processPageInt(newPage);
				} else {
					return linkUrl;
				}
			}
		}
		// Иначе - вывести содержимое страницы
		else {
			XmlDocumentBuilder xml = new PageWriter(page).generateXml();
			if (page.transformationNeeded())
				XmlXslOutputController.outputXmlTransformed(out, xml, xslFileName);
			else
				XmlXslOutputController.outputXml(out, xml);
		}
		return null;
	}
}