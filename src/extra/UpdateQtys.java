package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.XmlDataSource;
import ecommander.model.Item;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class UpdateQtys extends IntegrateBase implements ItemNames {

	private static final String INTEGRATE_DIR = "integrate";

	private File dir;

	@Override
	protected boolean makePreparations() throws Exception {
		String dirVar = getVarSingleValue("dir");
		dir = new File(StringUtils.isBlank(dirVar) ? AppContext.getRealPath(INTEGRATE_DIR) : dirVar);
		if (!dir.exists()) {
			info.addError("Не найдена директория интеграции " + dir, "init");
			return false;
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Обновление прайс-листа");
		info.setProcessed(0);
		Collection<File> xmls = FileUtils.listFiles(dir, new String[] {"xml"}, true);
		if (xmls.size() == 0) {
			info.addError("Не найдены XML файлы в директории " + dir, "init");
			return;
		}
		for (File xml : xmls) {
			XmlDataSource doc = new XmlDataSource(xml.getAbsolutePath(), StandardCharsets.UTF_8);
			XmlDataSource.Node node = null;
			while ((node = doc.findNextNode("offer")) != null) {
				Document nodeDoc = node.getDoc();
				Element offerEl = nodeDoc.getElementsByTag("offer").first();
				if (offerEl == null) {
					info.addError("Неверный формат " + node.getXml(), "process");
					continue;
				}
				String code = offerEl.attr("id");
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.CODE, code);
				if (product == null) {
					info.addError("Не найден товар " + code, "process");
					continue;
				}
				node = doc.scanCurrentNode();
				product.setValue(product_.EXTRA_XML, node.getXml());
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noTriggerExtra().noFulltextIndex());
				info.increaseProcessed();
			}
			doc.finishDocument();
		}
		info.setOperation("Интеграция завершена");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
