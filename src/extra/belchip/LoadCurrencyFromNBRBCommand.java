package extra.belchip;

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

public class LoadCurrencyFromNBRBCommand extends Command {
	
	private static final String NBNB_URL = "http://www.nbrb.by/Services/XmlExRates.aspx?ondate=%s";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("YYYY-M-d");

	@Override
	public ResultPE execute() throws Exception {
		List<Item> currencies = new ItemQuery("currency").loadItems();
		if(!currencies.isEmpty()) {
			String date = FORMAT.format(new Date());
			String url = String.format(NBNB_URL, date);
			Document doc = Jsoup.parse(new URL(url), 5000);
			
			for(Item currency : currencies) {
				Element el = doc.select("CharCode:contains("+currency.getStringValue("name")+")").first();
				Elements siblings = el.siblingElements();
				currency.setValueUI("ratio", siblings.select("Rate").text());
				currency.setValueUI("scale", siblings.select("Scale").text());
				executeCommandUnit(SaveItemDBUnit.get(currency).ignoreUser(true));
			}
			commitCommandUnits();
		}
		return getResult("success");
	}

}
