package ecommander.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import ecommander.fwk.ServerLogger;
import ecommander.controllers.AppContext;
import ecommander.output.PageElementWriter;
import ecommander.output.PageElementWriterRegistry;
import ecommander.output.XmlDocumentBuilder;
/**
 * Класс, который считывает закешированный айтем из XML файла и добавляет полученный XML фрагмент в документ для итоговой страницы
 * @author EEEE
 *
 */
public class CacheablePEManager {
	/**
	 * Загрузить страничный элемент из кеша.
	 * Если элемент был загружен из кеша, возвращается true.
	 * Если требуемого элемента не оказалось в кеше (он не был закеширован ранее), возвращается false
	 * @param element
	 * @return
	 * @throws Exception
	 */
	public static boolean getCache(CacheablePE element) throws Exception {
		File xmlFile = getCacheFile(element);
		if (!xmlFile.exists()) return false;
		FileInputStream fis = new FileInputStream(xmlFile);
		InputStreamReader isr = new InputStreamReader(fis, "UTF8");
		char[] buffer = new char[4096];
		int charCount = 0;
		StringBuilder sb = new StringBuilder();
		try {
			while ((charCount = isr.read(buffer)) >= 0) {
				sb.append(buffer, 0, charCount);
			}
			isr.close();
			fis.close();
		}
		finally {
			isr.close();
			fis.close();
		}
		element.setCachedContents(sb.toString());
		return true;
	}
	
	private static File getCacheFile(CacheablePE element) {
		return new File(AppContext.getCacheXmlDirPath() + element.getCacheableId() + ".xml");
	}
	/**
	 * Создать файл кеша для кешируемого элемента
	 * @param element
	 * @throws Exception
	 */
	public static void createCache(CacheablePE element) throws Exception {
		File xmlFile = getCacheFile(element);
		// Создание директории, если она еще не существует
		// Создать директорию для кэшированных файлов, если нельзя создать файл
		try {
			xmlFile.getParentFile().mkdirs();
			xmlFile.createNewFile();					
		} catch (IOException e) {
			File cacheDir = new File(AppContext.getCacheXmlDirPath());
			if (!cacheDir.exists()) cacheDir.mkdir();
		}
		PageElementWriter writer = PageElementWriterRegistry.getCacheWriter(element);
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		FileOutputStream fstream = new FileOutputStream(xmlFile);
		OutputStreamWriter out = new OutputStreamWriter(fstream, "UTF-8");
		try {
			writer.write(element, xml);
		} catch (Exception e) {
			ServerLogger.error("Unable to cache nonsingle item '" + element.getCacheableId() + "' that have parents");
			fstream.close();
			out.close();
			throw e;
		}
		out.write(xml.toString());
		out.flush();
		out.close();
	}
}
