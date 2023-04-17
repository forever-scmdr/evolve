package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class ExportMizidaCode extends Command {
	@Override
	public ResultPE execute() throws Exception {
		List<Item> allProds = new ItemQuery("product", Item.STATUS_NORMAL, Item.STATUS_HIDDEN).loadItems();
		StringBuilder content = new StringBuilder();
		String fileName = getVarSingleValue("file");
		for (Item allProd : allProds) {
			String code = allProd.getStringValue("code");
			String mizida_code = allProd.getStringValue("mizida_code", "");
			if (StringUtils.isNotBlank(code)) {
				content.append(code).append('\t').append(mizida_code).append("\r\n");
			}
		}
		File file =  new File(AppContext.getRealPath(fileName));
		FileUtils.write(file, content, Strings.SYSTEM_ENCODING);
		return null;
	}
}
