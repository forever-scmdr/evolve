package lunacrawler.fwk;

import ecommander.fwk.ServerLogger;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.net.URLDecoder;

public abstract class BasicCrawler extends WebCrawler {
	
	private static volatile int urlsToProcess = 0;
	private static volatile int visited = 0;
	private static long startTime = 0;
	private static final double MINUTE_MILLIS = 60 * 1000;

	@Override
	public final boolean shouldVisit(Page page, WebURL url) {
		CrawlerController.getSingleton().modifyUrl(url);
		String href = url.getURL();
		try {
			href = URLDecoder.decode(href, "UTF-8");
		} catch (Exception e) {
			//
		}
		if (page.isRedirect()) {
			CrawlerController.getSingleton().changeUrlFor(page.getWebURL().getURL(), page.getRedirectedToUrl());
		}
		if (CrawlerController.getSingleton().getStyleForUrl(href) != null) {
			urlsToProcess++;
			//CrawlerController.getInfo().pushLog("Should visit: {}\tTo visit: {}", href, urlsToProcess);
			return true;
		}
		//CrawlerController.getInfo().pushLog("NOT VISITING: {}", href);
		return false;
	}

	@Override
	public final void visit(Page page) {
		String href = page.getWebURL().getURL();
		try {
			href = URLDecoder.decode(href, "UTF-8");
		} catch (Exception e) {
			//
		}
		String templateFileName = CrawlerController.getSingleton().getStyleForUrl(href);
		visited++;
		double minutes = (System.currentTimeMillis() - CrawlerController.getSingleton().getStartTime()) / MINUTE_MILLIS;
		long perMinute = Math.round(visited / minutes);
		CrawlerController.getInfo().pushLog("Visiting: {}\t{} to visit\t{} minutes\t{} visited\t{} VPM", href, urlsToProcess, Math.round(minutes), visited, perMinute);
		try {
			if (!templateFileName.equals(CrawlerController.NO_TEMPLATE)) {
				//CrawlerController.getInfo().pushLog("Saving contents: {}", href);
				CrawlerController.getSingleton().pageProcessed(page, processUrl(page), this);
			}
		} catch (Exception e) {
			ServerLogger.error("Error", e);
			throw new RuntimeException("Unable to switch proxy and reinit crawler");
		}
		urlsToProcess--;
	}
	/**
	 * Обработать УРЛ и вернуть результат парсинга в виде строки
	 * @param page
	 * @return
	 */
	protected abstract String processUrl(Page page);
}
