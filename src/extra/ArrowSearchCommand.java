package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.User;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.pages.var.Variable;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Поиск товавров по ключевым словам на сайтах arrow.com
 * Используется arrow REST API v4 http://api.arrow.com
 * Режим поиска - token. Руководстство по API: http://developers.arrow.com/api/index.php/site/page?view=v4isSearchToken
 * @author anton
 */

public class ArrowSearchCommand extends Command {


	@Override
	public ResultPE execute() throws Exception {
		JSONObject searchResult = loadJsonFromFile();
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDoc();
		xml.startElement("page", "name", getPageName());

		addPageBasics(xml);
		addGeneralResponseInfo(xml, searchResult);

		xml.endElement();

		ResultPE result = getResult("success");
		result.setValue(xml.toString());
		return result;
	}

	/**
	 * Добавляет общие сведения об ответе (статус, количество результатов, ошибки).
	 *
	 */

	private void addGeneralResponseInfo(XmlDocumentBuilder xml, JSONObject searchResult) {
	}

	/**
	 * Добавляет базовую информацию о странице (source_link, base, user, variables).
	 * @param xml
	 */
	private void addPageBasics(XmlDocumentBuilder xml){
		//source link
		xml.addElement("source_link", getRequestLink().getOriginalUrl());

		//user
		User u = getInitiator();
		xml.startElement("user", "id", u.getUserId(), "name", u.getName(), "visual", false);
		for(User.Group group : u.getGroups()){
			xml.addEmptyElement("group", "name", group.name, "id", group.id, "role", group.role);
		}
		xml.endElement();

		//base
		xml.addElement("base", getUrlBase());

		//variables
		xml.startElement("variables");
		for(Variable var : getAllVariables()){
			String varName = var.getName();
			for(String val : var.writeAllValues()){
				xml.addElement(varName, val);
			}
		}
		xml.endElement();
	}

	private JSONObject loadJsonFromFile() throws IOException {
		String fileContent = FileUtils.readFileToString(Paths.get(AppContext.getContextPath(), "arrow.json").toFile(), "UTF-8");
		return new JSONObject(fileContent);
	}

}
