package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

public class LoadCurrencyFromNBRB extends Command implements CatalogConst {
	//private static final String NBNB_URL = "http://www.nbrb.by/Services/XmlExRates.aspx?ondate=";
	private static final String NBNB_URL = "http://www.nbrb.by/Services/XmlExRates.aspx";
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("YYYY-M-d");

	@Override
	public ResultPE execute() throws Exception {
		List<Item> currencies = new ItemQuery("currency").loadItems();
		if(currencies.size() == 0){
			setPageVariable("result", "No currencies created. Nothing to fetch");
			return null;
		}
		Document doc = Jsoup.parse(new URL(NBNB_URL /*+ FORMAT.format(new Date())*/), 5000);
		for (Item currency : currencies){
			String ISOCode = currency.getStringValue(NAME,"");
			Elements infoFromBank = doc.getElementsContainingOwnText(ISOCode);
			if(infoFromBank == null || infoFromBank.isEmpty()){
				setPageVariable(currency.getStringValue(NAME), "currency not found");
				continue;
			}
			Element curs = infoFromBank.first().parent();
			try{
				String scale = curs.select("Scale").text();
				String rate = curs.select("Rate").text();
				String title = curs.select("Name").text();
				currency.setValueUI("scale", scale);
				currency.setValueUI("ratio", rate);
				currency.setValueUI("title", title);
			}catch (Exception e){
				ResultPE res = getResult("fail");
				XmlDocumentBuilder rb = XmlDocumentBuilder.newDocPart();
				doc.outputSettings().escapeMode(Entities.EscapeMode.extended);
				rb.addElement("currency_name", currency.getValue(NAME));
				rb.addElement("info_from_bank", infoFromBank + "");
				rb.addElements(doc.children().first().html());
				res.setValue(rb.toString());
				return res;
			}
			executeAndCommitCommandUnits(SaveItemDBUnit.get(currency).ignoreUser(true));
		}
		setPageVariable("result", "success");
		return null;
	}
}
