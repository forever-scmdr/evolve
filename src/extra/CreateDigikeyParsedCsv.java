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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class CreateDigikeyParsedCsv extends IntegrateBase {

	private static final String INTEGRATE_DIR = "integrate";

	private ParsedInfoProvider infoProvider;

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
		PrintWriter writer = null;

		// create products
		boolean hasHeader = false;
		int prodPerFileCount = 0;
		int fileCount = 0;
		for (Element productElTree : products) {

			if (prodPerFileCount >= 1000000) {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
				hasHeader = false;
				prodPerFileCount = 0;
				fileCount++;
			}


			if (!hasHeader) {
				File csvFile = new File(AppContext.getRealPath(INTEGRATE_DIR), "result" + fileCount + ".csv");
				//csvFile.mkdirs();
				FileOutputStream fos = new FileOutputStream(csvFile);
				writer = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));

				// create header
				StringBuilder line = new StringBuilder();
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
								line.append(',');
							String paramName = param.getElementsByTag("name").text();
							line.append(StringEscapeUtils.escapeCsv(paramName));
						} else {
							break;
						}
					}
				}
				writer.println(line.toString());
				hasHeader = true;
			}


			StringBuilder line = new StringBuilder();
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
				for (int i = 1; i < 100; i++) {
					Element param = productDoc.getFirst("parameter_" + i);
					if (param != null) {
						if (i > 1)
							line.append(',');
						String value = param.getElementsByTag("value").text();
						line.append(StringEscapeUtils.escapeCsv(value));
					} else {
						break;
					}
				}
				writer.println(line.toString());
				info.increaseProcessed();
				prodPerFileCount++;
			} catch (Exception e) {
				ServerLogger.error("Product save error", e);
				info.addError("Product save error, ID = " + code + ", name = " + name, "catalog");
			}
		}

		if (writer != null) {
			writer.flush();
			writer.close();
		}
		// save file
		//FileUtils.write(new File(csvFile, "result.csv"), csv, StandardCharsets.UTF_8);
	}

	@Override
	protected void terminate() throws Exception {

	}
}
