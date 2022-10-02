package ecommander.fwk.external_shops.mouser;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.external_shops.AbstractLoadAdditionalContentCommand;
import ecommander.pages.ResultPE;
import org.apache.commons.text.StringEscapeUtils;
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
			table.select("tr:eq(0)").remove();
			Elements trs = table.select("tr");
			for(Element tr : trs){
				tr.children().eq(2).remove();
				tr.removeAttr("class").removeAttr("id");
				Elements tds = tr.select("td");
				tds.removeAttr("class").removeAttr("id");
				tds.select("input").remove();
				tds.select("*").removeAttr("onclick");
				tds.select("a").remove();
				Elements k = tds.eq(0);
				k.html(k.text());
			}
			return StringEscapeUtils.escapeXml10(trs.outerHtml());
		}
		return "";
	}

	@Override
	protected ResultPE defineResult(String content) throws EcommanderException {
		ResultPE result = getResult("result");
		result.setValue(content);
		return result;
	}
}
