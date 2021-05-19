package extra;

import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LoadCurrencyFromNBRB extends Command implements CatalogConst {
	private static final String NBNB_URL = "http://www.nbrb.by/Services/XmlExRates.aspx?ondate=";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("YYYY-M-d");

	@Override
	public ResultPE execute() throws Exception {
		List<Item> currencies = new ItemQuery("currency").loadItems();
		if(currencies.size() == 0){
			return getResult("success");
		}
		Document doc = Jsoup.parse(new URL(NBNB_URL + FORMAT.format(new Date())), 5000);
		for (Item currency : currencies){
			Elements infoFromBank = doc.getElementsContainingOwnText(currency.getStringValue(NAME));
			Element curs = infoFromBank.first().parent();
			String scale = curs.select("Scale").text();
			String rate = curs.select("Rate").text();
			String title = curs.select("Name").text();
			currency.setValueUI("scale", scale);
			currency.setValueUI("ratio", rate);
			currency.setValueUI("title", title);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(currency).ignoreUser(true));
		}
		return getResult("success");
	}
}
