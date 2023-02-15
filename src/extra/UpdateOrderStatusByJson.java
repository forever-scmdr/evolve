package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class UpdateOrderStatusByJson extends Command {

	private static final String JSON_ORDER_STATUS_DIR = "integrate_status";

	@Override
	public ResultPE execute() throws Exception {
		String filesDirName = getVarSingleValueDefault("status_dir", JSON_ORDER_STATUS_DIR);
		String path = AppContext.getRealPath(filesDirName);
		try (Stream<Path> stream = Files.list(Paths.get(path))) {
			stream.forEach(file -> {
				try {
					String content = Files.readString(file, StandardCharsets.UTF_8);
					Document doc = JsoupUtils.parseJsonAsXml(content, "update");
					for (Element order : doc.getElementsByTag("update")) {
						String orderNum = JsoupUtils.getTagFirstValue(order, "order");
						String newStatus = JsoupUtils.getTagFirstValue(order, "status");
						if (StringUtils.isNotBlank(orderNum)) {
							Item purchase = ItemQuery.loadSingleItemByParamValue(ItemNames.PURCHASE, ItemNames.purchase_.NUM, orderNum);
							if (purchase != null) {
								purchase.setValueUI(ItemNames.purchase_.STATUS, newStatus);
								executeAndCommitCommandUnits(SaveItemDBUnit.get(purchase));
							}
						}
					}
				} catch (IOException e) {
					ServerLogger.error("error reading file " + file, e);
				} catch (Exception e) {
					ServerLogger.error("some DB loading error", e);
				}
			});
		}
		return null;
	}
}
