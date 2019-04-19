package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Warranty_form;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
		File productPicFile = product.getFileValue(ItemNames.product_.GALLERY, AppContext.getFilesDirPath(product.isFileProtected()));
		try {
			BufferedImage cert = ImageIO.read(baseCertFile);
			Graphics graph = cert.getGraphics();
			if (productPicFile != null && productPicFile.exists()) {
				BufferedImage productPic = ImageIO.read(productPicFile);
				double resizeQuotient = (double) PIC_HEIGHT / productPic.getHeight();
				int newWidth = (int) (productPic.getWidth() * resizeQuotient);
				Image tmp = productPic.getScaledInstance(newWidth, PIC_HEIGHT, Image.SCALE_SMOOTH);
				graph.drawImage(tmp, 290, 1800, null);
			}
			Font big = new Font(Font.SANS_SERIF, Font.PLAIN, 87);
			Font normal = new Font(Font.SANS_SERIF, Font.PLAIN, 54);
			graph.setColor(Color.black);
			graph.setFont(big);
			graph.drawString("cool", 1510, 695);
			graph.setFont(normal);
			graph.drawString(form.get_owner(), 730, 970);
			graph.drawString(product.getStringValue(ItemNames.product_.NAME), 1076, 930);
			graph.drawString(form.get_code(), 540, 1085);
			graph.drawString(form.get_serial(), 770, 1290);
			graph.drawString(form.get_seller(), 580, 1400);
			graph.drawString(form.outputValue(ItemNames.warranty_form_.DATE), 680, 1500);
			graph.drawString(form.outputValue(ItemNames.warranty_form_.DATE), 955, 1610);
			graph.dispose();

			File newCertFile = new File(AppContext.getRealPath("img/cert" + form.get_serial() + ".jpg"));
			ImageIO.write(cert, "jpg", newCertFile);
		} catch (Exception e) {
			ServerLogger.error("Unable to manipulate pic file", e);
			ResultPE error = getResult(ERROR);
			error.addVariable(MESSAGE, "Получение сертификата временно невозможно. Попробуйте позже или обратитесь к менеджерам через обратную связь");
			return error;
		}
		return null;
	}
}
