package ecommander.fwk.external_shops.mouser;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.external_shops.AbstractLoadAdditionalContentCommand;
import ecommander.pages.ResultPE;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoadDetailsCommand extends AbstractLoadAdditionalContentCommand {

	private static String TABLE_SELECTOR = ".specs-table";


	@Override
	protected String extractContent(Document html) {
		Elements tables  = html.select(TABLE_SELECTOR);
		for (Element table : tables){
			table.removeClass(TABLE_SELECTOR);
			Elements trs = table.select("tr");
			trs.eq(0).remove();
			for(Element tr : trs){
				tr.children().eq(2).remove();
				tr.removeAttr("class").removeAttr("id");
				Elements tds = tr.select("td");
				tds.removeAttr("class").removeAttr("id");
				Elements k = tds.eq(0);
				k.html(k.text());
			}
			return trs.outerHtml();
		}
		return "";
	}

	@Override
	protected ResultPE defineResult(String content) throws EcommanderException {
		ResultPE result = getResult("success");
		result.setValue(content);
		return result;
	}
}
