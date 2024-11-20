package ecommander.extra;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import ecommander.application.extra.EmailUtils;
import ecommander.common.ServerLogger;
import ecommander.controllers.PageController;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.PageModelRegistry;
import ecommander.pages.elements.PagePE;
import ecommander.pages.elements.ResultPE;
import ecommander.pages.elements.variables.StaticVariablePE;
import ecommander.pages.elements.variables.VariablePE.Style;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

public class NewsSender extends Command {

	@Override
	public ResultPE execute() throws Exception {
		Item rss = ItemQuery.loadSingleItemByName(ItemNames.RSS);
		Long date = ItemQuery.loadSingleItemByName(ItemNames.RSS).getLongValue(ItemNames.rss.DATE);
		String dts = (date == null) ? "0" : date.toString();
		rss.setValue(ItemNames.rss.DATE, new Date().getTime());
		try {

			// -- подписчики
			List<Item> subscribers = ItemQuery.newItemQuery(ItemNames.AGENT).loadItems();
			// -- разделы новостей
			List<Item> newsSections = ItemQuery.newItemQuery(ItemNames.NEWS_SECTION).loadItems();

			for (Item sec :  newsSections) {
				Multipart mp = null;
				MimeBodyPart textPart = null;
				HashSet<String> emails = new HashSet<String>();
				for (Item subscriber : subscribers) {
					if (hasTag(subscriber, ItemNames.agent.TAGS, sec.getStringValue(ItemNames.news_section.NAME))) {
							emails.add(subscriber.getStringValue(ItemNames.agent.EMAIL));
					}	
				}
				if (emails.size() > 0) {
					String emailList = join(emails, ", ");
					StaticVariablePE sv = new StaticVariablePE("sec", sec.getKeyUnique());
					StaticVariablePE dv = new StaticVariablePE("date", dts);
					sv.setStyle(Style.translit);
					mp = new MimeMultipart();
					textPart = new MimeBodyPart();
					mp.addBodyPart(textPart);
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					LinkPE link = LinkPE.newDirectLink("link", "email_news", false);
					link.addVariable(sv);
					link.addVariable(dv);
					ExecutablePagePE emailPage = PageModelRegistry.testAndGetRegistry().getExecutablePage(link.serialize(), "",
							getSessionContext());
					PageController.newSimple().executePage(emailPage, bos);
					String pageContent = bos.toString("UTF-8");
					if(pageContent.equals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) continue;
					textPart.setContent(pageContent,
							emailPage.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER) + ";charset=UTF-8");
					EmailUtils.sendGmailDefault(emailList, "Рассылка ООО «Термобрест»: " + sec.getStringValue(ItemNames.news_section.NAME), mp);
				}
			}
		} catch (Exception e) {
			ServerLogger.error(e);
			return getResult("error");
		}
		executeAndCommitCommandUnits(new UpdateItemDBUnit(rss));
		return getResult("success");
	}

	private boolean hasTag(Item item, String paramName, String paramValue) {
		return item.getStringValues(paramName).contains(paramValue);
	}

	private String join(Set<String> set, String sep) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		int ss = set.size();
		for (String e : set) {
			e = (i < ss - 1) ? e + sep : e;
			sb.append(e);
			i++;
		}
		return sb.toString();
	}

}
