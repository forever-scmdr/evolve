package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.persistence.commandunits.CleanDeletedItemsDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.LinkedHashMap;

public class ImportNewsAndArticlesFromAncientXML extends IntegrateBase {
	private static final String URL = "http://www.interpartner.by/news_and_articles_xml";
	private LinkedHashMap<String,String> urls = new LinkedHashMap<>();

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Перенос новостией и статей со старого сатйа");
		setOperation("Удаление старых записей. Подождите!");
		CleanDeletedItemsDBUnit delete = new CleanDeletedItemsDBUnit(500);
		executeAndCommitCommandUnits(delete);

		Item newsContainer = ItemQuery.loadSingleItemByParamValue(ItemNames.NEWS, ItemNames.news_.NAME, "Новости");
		Item articlesContainer = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.ARTICLES, getInitiator());
		if (newsContainer == null){
			Item newsWrap = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.NEWS_WRAP, getInitiator());
			newsContainer = ItemUtils.newChildItem(ItemNames.NEWS, newsWrap);
			newsContainer.setValue(ItemNames.news_.NAME, "Новости");
			newsContainer.setKeyUnique("novosti");
			executeAndCommitCommandUnits(SaveItemDBUnit.get(newsContainer).noFulltextIndex().noTriggerExtra().ignoreUser(true));
		}
		info.pushLog("Удаление старых записей завершено");
		setOperation("Загрузка информации со старого сайта");
		Document doc = Jsoup.parse(new URL(URL), 5000);
		Elements newsItemEls = doc.select("news_item, article");
		info.pushLog("Загрузка информации завершена");
		setOperation("Запись информации в базу данных нового сайта");
		for(Element newsEl : newsItemEls){
			Item parent = newsEl.nodeName().equals(ItemNames.NEWS_ITEM)? newsContainer : articlesContainer;
			Item newsItem = ItemUtils.newChildItem(ItemNames.NEWS_ITEM, parent);
			String name = newsEl.select("header").text();
			String keyUnique = newsEl.attr("key");
			Element briefEl = newsEl.select("short").first();
			Element textEl = newsEl.select("text").first();
			String brief = briefEl != null? processHtml(briefEl.html()) : "";
			String text = textEl != null? processHtml(textEl.html()) : "";
			String link = newsEl.select("show_news_item").first().text();
			String newLink = '/'+keyUnique+'/';
			urls.put(link,newLink);
			String ds = newsEl.select("date").attr("millis");
			if(StringUtils.isNotBlank(ds)) {
				long date = Long.parseLong(ds);
				newsItem.setValue("date", date);
			}
			newsItem.setValueUI("header",name);
			newsItem.setValueUI("short",brief);
			newsItem.setValueUI("text",text);
			newsItem.setKeyUnique(keyUnique);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(newsItem).noFulltextIndex().noTriggerExtra().ignoreUser(true));
			Element seoEl = newsEl.select("seo").first();
			if(seoEl != null){
				Element titleEl = seoEl.select("title").first();
				Element descrEl = seoEl.select("description").first();
				Element keywordsEl = seoEl.select("description").first();

				String title = titleEl != null? titleEl.text() : "";
				String description = descrEl != null? descrEl.text() : "";
				String keywords = keywordsEl != null? keywordsEl.text() : "";

				Item seo = ItemUtils.newChildItem(ItemNames.SEO,newsItem);
				seo.setValue(ItemNames.seo_.TITLE, title);
				seo.setValue(ItemNames.seo_.DESCRIPTION, description);
				seo.setValue(ItemNames.seo_.KEYWORDS, keywords);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(seo).noTriggerExtra().ignoreUser(true).noFulltextIndex());
			}
			info.increaseProcessed();
		}
		info.pushLog("апись информации в базу данных нового сайта завершена");
		setOperation("Созднание фрагмента карты редиректов");
		XmlDocumentBuilder rdrList = XmlDocumentBuilder.newDocPart();

		urls.forEach((oldUrl, newUrl )->{
			rdrList.startElement("rule")
					.startElement("from", "casesensitive", "false").addText(oldUrl).endElement()
					.startElement("to", "type", "permanent-redirect", "qsappend", "false", "last", "true").addText(newUrl).endElement()
					.endElement();
		});

		info.pushLog("Созднание фрагмента карты редиректов завершено");
		setOperation("Сохранение  фрагмента карты редиректов");
		String fileName = "redirect_news_part.txt";
		FileWriter fileWriter = new FileWriter(new File(AppContext.getContextPath()+File.separator+fileName));
		fileWriter.write(rdrList.toString());
		fileWriter.close();
		info.pushLog("Сохранение фрагмента карты редиректов завершено");
	}

	private String processHtml(String input){
		if(StringUtils.isBlank(input)) return "";
		String s = input.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
		s = s.replaceAll("&amp;", "&");
		s = s.replaceAll("<p>&nbsp;</p>","");
		return s;
	}

	@Override
	protected void terminate() throws Exception {

	}
}
