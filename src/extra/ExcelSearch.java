package extra;

import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.ServerLogger;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by E on 14/12/2018.
 */
public class ExcelSearch extends Command  {
	@Override
	public ResultPE execute() throws Exception {
		try {
			FileItem fi = getItemForm().getSingleFileExtra("file");
			final ResultPE success = getResult("success");
			ExcelPriceList excel = new ExcelPriceList(fi) {
				@Override
				protected void processRow() throws Exception {
					String query = getValue(0);
					if (StringUtils.isNotBlank(query)) {
						success.addVariable("q", query);
						String number = getValue(1);
						if (StringUtils.isNotBlank(number)) {
							success.addVariable("n", query + ":" + number);
						}
					}
				}

				@Override
				protected void processSheet() throws Exception {

				}
			};
			excel.iterate();
			return success;
		} catch (Exception e) {
			ServerLogger.error("Error parsing excel search query", e);
			return getResult("error");
		}
	}
}
