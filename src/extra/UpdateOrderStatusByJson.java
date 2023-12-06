package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.EmailQueueSender;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

public class UpdateOrderStatusByJson extends Command {

	private static final String JSON_ORDER_STATUS_DIR = "integrate_status";

	@Override
	public ResultPE execute() throws Exception {
		String filesDirName = getVarSingleValueDefault("status_dir", JSON_ORDER_STATUS_DIR);
		String path = AppContext.getRealPath(filesDirName);
		Path updatesPath = Paths.get(path);
		if (!Files.exists(updatesPath))
			return null;
		Path backupPath = Paths.get(path + "/~backup");
		if (!Files.exists(backupPath))
			Files.createDirectories(backupPath);

		try (Stream<Path> stream = Files.list(Paths.get(path))) {
			System.out.println(stream);
			stream.forEach(file -> {
				if (!Files.isDirectory(file)) {
					try {
						String content = FileUtils.readFileToString(file.toFile(), StandardCharsets.UTF_8);
						//String content = Files.readString(file, StandardCharsets.UTF_8);
						Document doc = JsoupUtils.parseJsonAsXml(content, "update");
						for (Element order : doc.getElementsByTag("update")) {
							String orderNum = JsoupUtils.getTagFirstValue(order, "order");
							String newStatus = JsoupUtils.getTagFirstValue(order, "status");
							if (StringUtils.isNotBlank(orderNum)) {
								Item purchase = ItemQuery.loadSingleItemByParamValue(ItemNames.PURCHASE, ItemNames.purchase_.NUM, orderNum);
								if (purchase != null) {
									purchase.setValueUI(ItemNames.purchase_.STATUS, newStatus);
									executeAndCommitCommandUnits(SaveItemDBUnit.get(purchase).ignoreUser());
									// Добавление письма в очередь отправки
									User user = User.get(new ItemQuery(ItemNames.USER).setChildId(purchase.getId(), true).loadFirstItem());
									if (user != null) {
										LinkPE link = LinkPE.newDirectLink("status_email", "status_email", false);
										link.addStaticVariable("pur", purchase.getId() + "");
										EmailQueueSender.addEmailToQueue(this, user.get_email(), link.serialize());
									}
								}
							}
						}

						// Скопировать файл в бекап и удалить
						Files.move(file, backupPath.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						ServerLogger.error("error reading file " + file, e);
					} catch (Exception e) {
						ServerLogger.error("some DB loading error", e);
					}
				}
			});
		}

		return null;
	}
}
