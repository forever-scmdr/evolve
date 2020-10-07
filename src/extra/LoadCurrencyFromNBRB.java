package extra;

import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.List;

public class LoadCurrencyFromNBRB extends Command implements CatalogConst {
	private static final String NBNB_URL = "http://www.nbrb.by/Services/XmlExRates.aspx?ondate=";
	private static final DateTimeFormatter REPORT_FORMATTER = DateTimeFormat.forPattern("YYYY-M-d").withZoneUTC();
	@Override
	public ResultPE execute() throws Exception {
		Document doc = Jsoup.parse(new URL(NBNB_URL + REPORT_FORMATTER.print(DateTime.now())), 5000);
		List<Item> ratios = new ItemQuery("ratio").loadItems();

		for(Item ratioItem : ratios){
			String nbrbId = ratioItem.getStringValue("nbrb_id","");
			if(StringUtils.isBlank(nbrbId)) continue;
			Elements els = doc.getElementsByAttributeValue("Id", nbrbId);
			if(els.size() != 0){
				String currencyRatio = els.first().select("Rate").html();
				String scale = els.first().select("Scale").html();
				ratioItem.setValueUI("currency_ratio", currencyRatio);
				ratioItem.setValueUI("scale", scale);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(ratioItem).ignoreUser().ignoreFileErrors().noFulltextIndex());
			}
		}
		return getResult("success");
	}
}
