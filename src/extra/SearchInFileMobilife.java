package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Collection;

public class SearchInFileMobilife extends Command {

	private static final String INTEGRATION_DIR = "integrate";

	@java.lang.Override
	public ResultPE execute() throws Exception {

		final String query = getVarSingleValueDefault("q", "nonexisting");
		if (StringUtils.isBlank(query) || query.length() < 7) {

		}

		String integrationDirName = getVarSingleValueDefault("dir", INTEGRATION_DIR);
		File integrationDir = new File(AppContext.getRealPath(integrationDirName));
		if (!integrationDir.exists()) {
			return getNotFoundResult("incorrect_query");
		}

		// Найти все файлы
		Collection<File> files = FileUtils.listFiles(integrationDir, null, true);
		if (files.isEmpty()) {
			return getNotFoundResult("not_found");
		}
		File first = files.iterator().next();
		BufferedReader br;
		String line;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(first), "cp1251"));
			try {
				while((line = br.readLine()) != null) {
					if (StringUtils.indexOf(line, query) > 0) {
						line = StringUtils.substringAfter(line, "VALUE");
						line = StringUtils.substringAfter(line, "(");
						line = StringUtils.substringBeforeLast(line, ")");
						String[] parts = StringUtils.split(line, ',');
						XmlDocumentBuilder doc = XmlDocumentBuilder.newDocPart();
						doc.addElement("result", "found");
						doc.startElement("item");
						doc.addElement("num", prepare(parts[0]));
						doc.addElement("imei", prepare(parts[1]));
						doc.addElement("status", prepare(parts[2]));
						doc.addElement("cost", prepare(parts[3]));
						doc.endElement();
						return getResult("xml").setValue(doc.toString());
					}
				}
				br.close();
			} catch (IOException e) {
				ServerLogger.error("error scanning file", e);
				return getNotFoundResult("not_found");
			}
		} catch (FileNotFoundException e) {
			ServerLogger.error("error scanning file", e);
			return getNotFoundResult("not_found");
		}
		return getNotFoundResult("not_found");
	}

	private ResultPE getNotFoundResult(String resultType) throws EcommanderException {
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		xml.addElement("result", resultType);
		xml.addElement("message", "Заказы с указанным номером или IMEI не найдены");
		return getResult("xml").setValue(xml.toString());
	}

	private String prepare(String value) {
		return StringUtils.remove(value, '\'');
	}
}
