package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.EmailUtils;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Warranty_form;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.imageio.ImageIO;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by E on 17/4/2019.
 */
public class CreateCertificate extends Command implements ItemNames.warranty_form_ {

	private static final String CERT_BASE = "img/xxl.jpg";
	private static final String ERROR = "error";
	private static final String SUCCESS = "success";
	private static final String MESSAGE = "message";
	private static final String NOT_FOUND = "not_found";

	private static final int PIC_HEIGHT = 700;

	@Override
	public ResultPE execute() throws Exception {
		Warranty_form form = Warranty_form.get(getItemForm().getItemTree().getFirstChild().getItem());
		if (StringUtils.isNotBlank(form.get_url())) {
			return getResult(SUCCESS);
		}
		if (StringUtils.isAnyBlank(form.get_code(), form.get_email(), form.get_owner(), form.get_phone(), form.get_seller(), form.get_serial())) {
			ResultPE error = getResult(ERROR);
			error.addVariable(MESSAGE, "Заполните, пожалуйста, все поля анкеты. Они необходимы для оформления сертификата");
			saveSessionForm("warranty");
			return error;
		}
		Item product = ItemQuery.loadSingleItemByParamValue(ItemNames.PRODUCT, ItemNames.product_.CODE, form.get_code());
		if (product == null) {
			return getResult(NOT_FOUND);
		}
		File baseCertFile = new File(AppContext.getRealPath(CERT_BASE));
		if (!baseCertFile.exists()) {
			ResultPE error = getResult(ERROR);
			error.addVariable(MESSAGE, "Получение сертификата временно невозможно. Попробуйте позже или обратитесь к менеджерам через обратную связь");
			return error;
		}
		ServerLogger.error("\t\t\t\tWARRANTY START");
		// Проверка даты сертификата
		DateTime now = new DateTime(System.currentTimeMillis(), DateTimeZone.UTC);
		DateTime purchaseDate = new DateTime(form.get_date(), DateTimeZone.UTC);
		if (purchaseDate.plusMonths(1).isBefore(now)) {
			ResultPE error = getResult(ERROR);
			error.addVariable(MESSAGE, "С момента покупки прошло более месяца, поэтому получение сертификата в автоматическом режиме невозможно. " +
					"Воспользуйтесь формой обратной связи для связи с менеджерами");
			return error;
		}
		DateTime certDate = purchaseDate.plusYears(3);

		ServerLogger.error("\t\t\t\tWARRANTY MAIN");
		File productPicFile = product.getFileValue(ItemNames.product_.GALLERY, AppContext.getFilesDirPath(product.isFileProtected()));
		try {
			// Увеличение нормера сертификата
			Item counter = ItemUtils.ensureSingleRootItem(ItemNames.COUNTER, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			int count = counter.getIntValue(ItemNames.counter_.WARRANTY_COUNT, 500) + 1;
			if (count > 99999)
				count = 1;
			String certNumber = String.format("%05d", count);
			ServerLogger.error("\t\t\t\tWARRANTY COUNTER");

			BufferedImage cert = ImageIO.read(baseCertFile);
			ServerLogger.error("\t\t\t\tWARRANTY PIC");
			Graphics graph = cert.getGraphics();
			if (productPicFile != null && productPicFile.exists()) {
				BufferedImage productPic = ImageIO.read(productPicFile);
				double resizeQuotient = (double) PIC_HEIGHT / productPic.getHeight();
				int newWidth = (int) (productPic.getWidth() * resizeQuotient);
				Image tmp = productPic.getScaledInstance(newWidth, PIC_HEIGHT, Image.SCALE_SMOOTH);
				graph.drawImage(tmp, 290, 1800, null);
			}
			ServerLogger.error("\t\t\t\tWARRANTY DRAWN");
			Font big = new Font(Font.SANS_SERIF, Font.PLAIN, 87);
			Font normal = new Font(Font.SANS_SERIF, Font.PLAIN, 54);
			graph.setColor(Color.black);
			graph.setFont(big);
			graph.drawString(certNumber, 1510, 760);
			graph.setFont(normal);
			graph.drawString(form.get_owner(), 730, 1010);
			graph.drawString(product.getStringValue(ItemNames.product_.NAME), 950, 1120);
			graph.drawString(form.get_code(), 540, 1225);
			graph.drawString(form.get_serial(), 770, 1330);
			graph.drawString(form.get_seller(), 585, 1440);
			graph.drawString(form.outputValue(ItemNames.warranty_form_.DATE), 680, 1545);
			graph.drawString(DateDataType.outputDate(certDate.getMillis(), DateDataType.DAY_FORMATTER), 955, 1650);
			graph.dispose();
			ServerLogger.error("\t\t\t\tWARRANTY DISPOSED");

			File newCertFile = new File(AppContext.getRealPath("img/cert" + form.get_serial() + ".jpg"));
			ImageIO.write(cert, "jpg", newCertFile);

			ServerLogger.error("\t\t\t\tWARRANTY WRITTEN");
			// Отправка сертификата на почту
			// Формирование тела письма
			Multipart mp = new MimeMultipart();
			String mailMessage = "Копия сертификата №" + certNumber;
			ByteArrayOutputStream certBos = new ByteArrayOutputStream();
			ImageIO.write(cert, "jpg", certBos);
			ByteArrayInputStream certBis = new ByteArrayInputStream(certBos.toByteArray());
			DataSource dataSource = new ByteArrayDataSource(certBis, "image/jpeg");
			MimeBodyPart filePart = new MimeBodyPart();
			filePart.setDataHandler(new DataHandler(dataSource));
			filePart.setFileName(newCertFile.getName());
			mp.addBodyPart(filePart);
			MimeBodyPart textPart = new MimeBodyPart();
			mp.addBodyPart(textPart);
			textPart.setContent(mailMessage, "text/plain;charset=UTF-8");
			ServerLogger.error("\t\t\t\tWARRANTY BEFORE_EMAIL");
			// Отправка письма
			EmailUtils.sendGmailDefault(form.get_email(), "Сертификат на технику Metabo", mp);
			ServerLogger.error("\t\t\t\tWARRANTY EMAIL 1");
			// Отправка письма админу
			if (StringUtils.isNotBlank(getVarSingleValue("admin_email"))) {
				EmailUtils.sendGmailDefault(getVarSingleValue("admin_email"), "Сертификат на технику Metabo", mp);
			}
			ServerLogger.error("\t\t\t\tWARRANTY EMAIL 2");

			counter.setValue(ItemNames.counter_.WARRANTY_COUNT, count);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(counter).noTriggerExtra().ignoreUser().noFulltextIndex());
			return getResult("success").addVariable("serial", form.get_serial());
		} catch (Exception e) {
			ServerLogger.error("Unable to manipulate pic file", e);
			ResultPE error = getResult(ERROR);
			error.addVariable(MESSAGE, "Получение сертификата временно невозможно. Попробуйте позже или обратитесь к менеджерам через обратную связь");
			return error;
		}
	}
}
