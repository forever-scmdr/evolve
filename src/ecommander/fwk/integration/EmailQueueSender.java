package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.UserGroupRegistry;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PagePE;
import ecommander.persistence.commandunits.ChangeItemOwnerDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class EmailQueueSender extends IntegrateBase {

	public static final String DEFAULT_EMAIL_PAGE = "agent_email";
	public static final String SYSTEM = "system";
	public static final String EMAIL_QUEUE = "email_queue";
	public static final String EMAIL_QUEUE_ITEM = "email_queue_item";
	public static final String ADDRESS_TO = "address_to";
	public static final String EMAIL_URL = "email_url";
	public static final String DATE_ADDED = "date_added";
	
	private Item queue;

	public EmailQueueSender() {

	}

	private EmailQueueSender(Command outer) {
		super(outer);
	}

	@Override
	protected boolean makePreparations() {
		try {
			queue = ItemQuery.loadSingleItemByName(EMAIL_QUEUE);
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			addError("Невозможно загрузить очередь отправки, описание ошибки в логе", "root");
			return false;
		}
		return queue != null;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Отправка писем из очереди");
		List<Item> messages = new ItemQuery(EMAIL_QUEUE_ITEM).setParentId(queue.getId(), false).loadItems();
		int processed = 0;
		for (Item message : messages) {
			setProcessed(processed);
			
			String emailTo = message.getStringValue(ADDRESS_TO);
			String url = message.getStringValue(EMAIL_URL);
			if (StringUtils.isBlank(emailTo) || StringUtils.isBlank(url)) {
				addError("Не заданы обязательные параметры письма. Задание удаляется", "sender");
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(message).noFulltextIndex().noTriggerExtra().ignoreUser());

				continue;
			}
			ExecutablePagePE template;
			try {
				template = getExecutablePage(url);
			} catch (Exception e) {
				addError("Неверный формат ссылки на шаблон письма " + url + ". Задание удаляется", e.getMessage());
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(message).noFulltextIndex().noTriggerExtra().ignoreUser());
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
					executeAndCommitCommandUnits(ItemStatusDBUnit.delete(message).noFulltextIndex().noTriggerExtra().ignoreUser());
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
			executeAndCommitCommandUnits(ItemStatusDBUnit.delete(message).noFulltextIndex().noTriggerExtra().ignoreUser());
			processed++;
			
			setProcessed(processed);
		}
	}

	private void addEmailToQueueInt(String addressTo, String emailUrl) throws Exception {
		Item system = ItemUtils.ensureSingleRootAnonymousItem(SYSTEM, getInitiator());
		Item queue = ItemUtils.ensureSingleAnonymousItem(EMAIL_QUEUE, getInitiator(), system.getId());
		if (queue.getOwnerGroupId() != UserGroupRegistry.getGroup("0dmin")) {
			executeAndCommitCommandUnits(ChangeItemOwnerDBUnit.newGroup(queue, UserGroupRegistry.getGroup("0dmin")).ignoreUser(true));
			queue = ItemUtils.ensureSingleAnonymousItem(EMAIL_QUEUE, getInitiator(), system.getId());
		}
		ItemType queueItemType = ItemTypeRegistry.getItemType(EMAIL_QUEUE_ITEM);
		Item queueItem = Item.newChildItem(queueItemType, queue);
		queueItem.setValue(DATE_ADDED, System.currentTimeMillis());
		queueItem.setValue(ADDRESS_TO, addressTo);
		queueItem.setValue(EMAIL_URL, emailUrl);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(queueItem).ignoreUser());
	}

	/**
	 * Добавить email в очередь отправки
	 * @param outer
	 * @param addressTo
	 * @param emailUrl
	 * @throws Exception
	 */
	public static void addEmailToQueue(Command outer, String addressTo, String emailUrl) throws Exception {
		EmailQueueSender sender = new EmailQueueSender(outer);
		sender.addEmailToQueueInt(addressTo, emailUrl);
	}

	@Override
	protected void terminate() throws Exception {
		// ничего не делать
	}
	
}
