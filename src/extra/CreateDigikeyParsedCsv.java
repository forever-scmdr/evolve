package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class CreateDigikeyParsedCsv extends IntegrateBase {

	private static final String INTEGRATE_DIR = "integrate";

	private ParsedInfoProvider infoProvider;
	private StringBuilder csv = new StringBuilder();

	@Override
	protected boolean makePreparations() throws Exception {
		infoProvider = new ParsedInfoProvider();
		return infoProvider.isValid();
	}


	@Override
	protected void integrate() throws Exception {
		info.setToProcess(0);
		info.setProcessed(0);
		info.limitLog(300);
		Document tree = infoProvider.getTree();
		Element root = tree.getElementsByTag("data").first();
		Elements products = root.select("product");

		// create header
		if (products.size() > 0) {
			ParsedInfoProvider.InfoAccessor productDoc = infoProvider.getAccessorJsoup(products.first());
			if (productDoc == null) {
				info.addError("Документ для товара '" + products.first() + "' содержит ошибки", "header");
				return;
			}
			for (int i = 1; i < 100; i++) {
				Element param = productDoc.getFirst("parameter_" + i);
				if (param != null) {
					if (i > 1)
						csv.append(',');
					String paramName = param.getElementsByTag("name").text();
					csv.append(StringEscapeUtils.escapeCsv(paramName));
				} else {
					break;
				}
			}
		}

		// create products
		for (Element productElTree : products) {
			String code = null;
			String name = null;
			try {
				code = productElTree.attr("id");
				if (StringUtils.isBlank(code))
					continue;
				ParsedInfoProvider.InfoAccessor productDoc;
				try {
					productDoc = infoProvider.getAccessor(code);
				} catch (Exception e) {
					ServerLogger.error("Error parsing product xml file", e);
					info.addError("Документ для товара '" + code + "' содержит ошибки", code);
					continue;
				}
				csv.append("\r\n");
				for (int i = 1; i < 100; i++) {
					Element param = productDoc.getFirst("parameter_" + i);
					if (param != null) {
						if (i > 1)
							csv.append(',');
						String value = param.getElementsByTag("value").text();
						csv.append(StringEscapeUtils.escapeCsv(value));
					} else {
						break;
					}
				}
			} catch (Exception e) {
				ServerLogger.error("Product save error", e);
				info.addError("Product save error, ID = " + code + ", name = " + name, "catalog");
			}
		}

		// save file
		File csvFile = new File(AppContext.getRealPath(INTEGRATE_DIR));
		csvFile.mkdirs();
		FileUtils.write(new File(csvFile, "result.csv"), csv, StandardCharsets.UTF_8);
	}

	@Override
	protected void terminate() throws Exception {

	}
}
