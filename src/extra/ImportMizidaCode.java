package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class ImportMizidaCode extends Command {
	@Override
	public ResultPE execute() throws Exception {
		String fileName = getVarSingleValue("file");
		File file = new File(AppContext.getRealPath(fileName));
		String content = FileUtils.readFileToString(file, Strings.SYSTEM_ENCODING);
		String[] lines = StringUtils.split(content, "\n");
		for (String line : lines) {
			line = StringUtils.trim(line);
			String[] parts = StringUtils.split(line, '\t');
			Item product = ItemQuery.loadSingleItemByParamValue("product", "code", parts[0], Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			if (product != null) {
				product.setValueUI("vendor_code", parts[1]);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noTriggerExtra());
			}
		}
		return null;
	}
}
