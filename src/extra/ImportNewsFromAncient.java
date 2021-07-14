package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

public class ImportNewsFromAncient extends IntegrateBase {
	private LinkedHashMap<String,String> urls = new LinkedHashMap<>();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	@Override
	protected void integrate() throws Exception {
		setOperation("Перенос новостией и статей со старого сатйа");
		String linkToAncientNews = getVarSingleValue("link");
		Item newsContainer = ItemQuery.loadSingleItemByParamValue(ItemNames.NEWS, ItemNames.news_.NAME, "Новости");
		if (newsContainer == null){
			Item newsWrap = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.NEWS_WRAP, getInitiator());
			newsContainer = ItemUtils.newChildItem(ItemNames.NEWS, newsWrap);
			newsContainer.setValue(ItemNames.news_.NAME, "Новости");
			newsContainer.setKeyUnique("novosti");
			executeAndCommitCommandUnits(SaveItemDBUnit.get(newsContainer).noFulltextIndex().noTriggerExtra().ignoreUser(true));
		}
		setOperation("Загрузка информации со старого сайта");
		Document doc = Jsoup.parse(new URL(linkToAncientNews), 5000);
		Elements newsItemEls = doc.select("news_item");
		info.pushLog("Загрузка информации завершена");
		setOperation("Запись информации в базу данных нового сайта");

		for(Element newsEl : newsItemEls){
			Item newsItem = ItemUtils.newChildItem(ItemNames.NEWS_ITEM, newsContainer);

			String name = newsEl.select("header").text();
			String keyUnique = newsEl.attr("key");
			Element briefEl = newsEl.select("short").first();
			Element textEl = newsEl.select("text").first();
			String brief = briefEl != null? processHtml(briefEl.html()) : "";
			String text = textEl != null? processHtml(textEl.html()) : "";
			String date = newsEl.select("date").text() + " 10:00";
			newsItem.setValueUI("date", date);

			newsItem.setValueUI("header",name);
			newsItem.setValueUI("short",brief);
			newsItem.setValueUI("text",text);
			newsItem.setKeyUnique(keyUnique);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(newsItem).noFulltextIndex().noTriggerExtra().ignoreUser(true));

			String link = newsEl.select("show_news_item").first().text();
			String newLink = '/'+keyUnique+'/';
			urls.put(link,newLink);

			info.increaseProcessed();
		}

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
		Files.write(Paths.get(AppContext.getContextPath(), fileName), rdrList.toString().getBytes(StandardCharsets.UTF_8));
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
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void terminate() throws Exception {

	}
}
