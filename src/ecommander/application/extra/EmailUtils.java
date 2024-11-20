package ecommander.application.extra;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.exceptions.EcommanderException;
import ecommander.model.item.Item;
import ecommander.persistence.itemquery.ItemQuery;

public class EmailUtils {
	private static final HashMap<String, String> PORTS = new HashMap<String, String>() {
		private static final long serialVersionUID = 2361116501952410474L;
		{
			put("smtp.gmail.com", "587");
			put("smtp.yandex.ru", "25");
		}
	};
	public static void sendGmail(String server, final String from, final String password, String to, String topic, Multipart mp)
			throws Exception {
		
		String port = (PORTS.get(server) == null) ? "25" : PORTS.get(server);
		Properties props = new Properties();
		// При использовании статического метода Transport.send()
		// необходимо указать через какой хост будет передано сообщение
		props.put("mail.smtp.host", server);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable","true");
		props.put("mail.smtp.ssl.trust", server);
		// Включение debug-режима
		//props.put("mail.debug", "true");
		props.put("mail.mime.charset", "utf-8");
		props.put("mail.smtp.port", port);

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		// Создание объекта сообщения
		MimeMessage msg = new MimeMessage(session);
		// Установка атрибутов сообщения
		msg.setFrom(new InternetAddress(from));
		String[] addresses = StringUtils.split(to, ',');
		InternetAddress[] arr = new InternetAddress[addresses.length];
		for (int i = 0; i < addresses.length; i++) {
			String s = addresses[i].trim();
			arr[i] = new InternetAddress(s, "Термобрест");
			
		}
		msg.addRecipients(Message.RecipientType.BCC, arr);
		msg.setSubject(topic, "UTF-8");
		msg.setContent(mp);
		msg.setSentDate(new Date());

		Transport tr = session.getTransport("smtp");
		tr.connect(from, password);
//		tr.connect("smtp.gmail.com", 587, emailFromLogin, "ghbdtnghbdtn");
		msg.saveChanges(); // don't forget this
		tr.sendMessage(msg, msg.getAllRecipients());
		
		//Transport.send(msg);
		//Strings.getStringFor("message.email_success");

	}
	/**
	 * Отправка письма с настройками, взятыми из стандартного айтема, который загружается из БД
	 * @param to
	 * @param topic
	 * @param mp
	 * @throws Exception
	 */
	public static void sendGmailDefault(String to, String topic, Multipart mp) throws Exception {
		ItemQuery query = new ItemQuery(ItemQuery.Type.ITEM, "feedback_params");
		List<Item> items = query.loadItems();
		if (items.size() != 1)
			throw new EcommanderException("Feedback parameters are not set correctly");
		Item feedbackParams = items.get(0);
		String emailFrom = (String) feedbackParams.getValue("email_from");
		String serverFrom = (String) feedbackParams.getValue("server_from");
		String emailFromPassword = (String) feedbackParams.getValue("email_from_password");
		String encoding = (String) feedbackParams.getValue("encoding");
		if (StringUtils.isBlank(to) || StringUtils.isBlank(serverFrom) || StringUtils.isBlank(emailFrom)
				|| StringUtils.isBlank(emailFromPassword) || StringUtils.isBlank(encoding))
			throw new EcommanderException("Feedback parameters are not set correctly");
		// Отправка письма
		EmailUtils.sendGmail(serverFrom, emailFrom, emailFromPassword, to, topic, mp);
	}
	/**
	 * Отправка письма с настройками, взятыми из стандартного айтема, который загружается из БД
	 * Поддерживатеся отправка только текста
	 * @param to
	 * @param topic
	 * @param message
	 * @throws Exception 
	 */
	public static void sendTextGmailDefault(String to, String topic, String message) throws Exception {
		Multipart mp = new MimeMultipart();
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(message, "text/plain;charset=UTF-8");
		mp.addBodyPart(textPart);
		EmailUtils.sendGmailDefault(to, topic, mp);
	}
}
