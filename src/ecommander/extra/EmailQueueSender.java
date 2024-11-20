package ecommander.extra;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import ecommander.application.extra.EmailUtils;
import ecommander.application.extra.IntegrateBase;
import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.PagePE;
import ecommander.persistence.commandunits.DeleteItemBDUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

public class EmailQueueSender extends IntegrateBase  {

	public static final String DEFAULT_EMAIL_PAGE = "agent_email";
	
	private Item queue;
	
	@Override
	protected boolean makePreparations() {
		try {
			queue = ItemQuery.loadSingleItemByName(ItemNames.EMAIL_QUEUE);
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			addError("Невозможно загрузить очередь отправки, описание ошибки в логе", "root");
			return false;
		}
		if (queue == null) {
			addError("Не найдена очередь почтовых сообщений", 0, 0);
			return false;
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Отправка писем из очереди");
		List<Item> messages = ItemQuery.newItemQuery(ItemNames.EMAIL_QUEUE_ITEM).setPredecessorId(queue.getId()).loadItems();
		int processed = 0;
		for (Item message : messages) {
			setProcessed(processed);
			
			String emailTo = message.getStringValue(ItemNames.email_queue_item.ADDRESS_TO);
			String url = message.getStringValue(ItemNames.email_queue_item.EMAIL_URL);
			if (StringUtils.isBlank(emailTo) || StringUtils.isBlank(url)) {
				addError("Не заданы обязательные параметры письма. Задание удаляется", "sender");
				executeAndCommitCommandUnits(new DeleteItemBDUnit(message));
				processed++;
				continue;
			}
			ExecutablePagePE template = null;
			try {
				LinkPE link = LinkPE.parseLink(url);
				link.addStaticVariable("email", emailTo);
				template = getExecutablePage(link.serialize());
			} catch (Exception e) {
				addError("Неверный формат ссылки на шаблон письма " + url + ". Задание удаляется", e.getMessage());
				executeAndCommitCommandUnits(new DeleteItemBDUnit(message));
				processed++;
				continue;
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PageController.newSimple().executePage(template, bos);
			Document templateDoc = Jsoup.parse(bos.toString(Strings.SYSTEM_ENCODING));
			
			// Найти в документе тэг <topic>, взять из него заголовок и удалить
			String topic = "Topic not set";
			Elements topics = templateDoc.getElementsByTag("topic");
			if (topics.size() > 0) {
				topic = topics.first().ownText();
				topics.remove();
			}

			// Найти в документе все картинки и сделать из них отдельные части многочастного письма
			ArrayList<BodyPart> imageParts = new ArrayList<BodyPart>();
			Elements imgs = templateDoc.getElementsByTag("img");
			int index = 0;
			for (Element img : imgs) {
				String relPath = img.attr("src");
				String partName = "image" + index++;
				img.attr("src", "cid:" + partName);
				BodyPart imgPart = new MimeBodyPart();
				File imgFile = new File(AppContext.getRealPath(relPath));
				if (!imgFile.exists()) {
					addError("Не найден файл изображения " + relPath + ". Задание удаляется", "sender");
					executeAndCommitCommandUnits(new DeleteItemBDUnit(message));
					processed++;
					continue;
				}
				DataSource fds = new FileDataSource(imgFile);
				imgPart.setDataHandler(new DataHandler(fds));
				imgPart.setHeader("Content-ID", "<" + partName + ">");
				imageParts.add(imgPart);
			}
			
			// Создать сообщение
			OutputSettings settings = new OutputSettings();
			settings.charset(Charset.forName("UTF-8"));
			settings.escapeMode(EscapeMode.xhtml);
			templateDoc.outputSettings(settings);
			Multipart mp = new MimeMultipart("related");
			BodyPart textPart = new MimeBodyPart();
			textPart.setContent(templateDoc.outerHtml(), template.getResponseHeaders().get(PagePE.CONTENT_TYPE_HEADER) + ";charset=UTF-8");
			mp.addBodyPart(textPart);
			for (BodyPart imgPart : imageParts) {
				mp.addBodyPart(imgPart);
			}
			
			// Отправить сообщение
			try {
				EmailUtils.sendGmailDefault(emailTo, topic, mp);
			} catch (Exception e) {
				addError("Ошбика при отправке сообщения. Задание остается в очереди", e.getMessage());
				processed++;
				continue;
			}
			
			// Сообщение об отправке сообщения
			pushLog("Сообщение для " + emailTo + " отправлено");
			// Добавить агенту это письмо в полученные
			Item agent = ItemQuery.loadById(message.getLongValue(ItemNames.email_queue_item.AGEND_ID, -1L));
			if (agent != null) {
				agent.setValue(ItemNames.agent.EMAILS_SENT, message.getLongValue(ItemNames.email_queue_item.EMAIL_ID, -1L));
				executeCommandUnit(new UpdateItemDBUnit(agent).fulltextIndex(false));
			}
			executeAndCommitCommandUnits(new DeleteItemBDUnit(message));
			processed++;
			
			setProcessed(processed);
		}
	}
	
}
