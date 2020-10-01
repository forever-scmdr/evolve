package extra;

import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;

public class LoadCurrencyFromNBRB extends Command implements CatalogConst {
	private static final String NBNB_URL = "http://www.nbrb.by/Services/XmlExRates.aspx";
	@Override
	public ResultPE execute() throws Exception {
		Document doc = Jsoup.parse(new URL(NBNB_URL), 5000);
		Elements els = doc.getElementsByAttributeValue("Id", "298");
		String ratio = els.first().select("Rate").html();

		Elements usd = doc.getElementsByAttributeValue("Id", "145");
		String usdRatio = usd.first().select("Rate").html();

		Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		catalog.setValueUI("currency_ratio", ratio);
		catalog.setValueUI("currency_ratio_usd", usdRatio);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser().ignoreFileErrors().noFulltextIndex());
		return getResult("success");
	}
}