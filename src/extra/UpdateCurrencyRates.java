package extra;

import ecommander.fwk.ItemUtils;
import ecommander.fwk.WebClient;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

/**
 * Created by E on 6/6/2019.
 */
public class UpdateCurrencyRates extends Command implements ItemNames {

	private static final String NBNB_URL = "http://www.nbrb.by/Services/XmlExRates.aspx";
	private static final String RATE_SUFFIX = "_rate";
	private static final String SCALE_SUFFIX = "_scale";
	private static final String UPDATE_SUFFIX = "_update";
	private static final String CURRENCIES = "currencies";
	private static final String CATALOG = "catalog";
	private static final byte ONE = (byte) 1;

	@Override
	public ResultPE execute() throws Exception {
		if (!StringUtils.equalsIgnoreCase(getVarSingleValue("action"), "start"))
			return null;
		String xml = WebClient.getString(NBNB_URL);
		if (StringUtils.isNotBlank(xml)) {
			xml = xml.substring(xml.indexOf('<'));
		}
		Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		Item currencies = ItemUtils.ensureSingleItem(CURRENCIES, getInitiator(), catalog.getId(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
		for (String paramName : currencies.getItemType().getParameterNames()) {
			if (StringUtils.endsWithIgnoreCase(paramName, RATE_SUFFIX)) {
				String currencyCode = StringUtils.substringBefore(paramName, RATE_SUFFIX);
				Element currencyNameNode = doc.getElementsContainingOwnText(currencyCode).first();
				if (currencyNameNode != null && currencies.getByteValue(currencyCode + UPDATE_SUFFIX, ONE) == ONE) {
					Element currencyNode = currencyNameNode.parent();
					currencies.setValueUI(currencyCode + RATE_SUFFIX, currencyNode.getElementsByTag("Rate").first().ownText());
					currencies.setValueUI(currencyCode + SCALE_SUFFIX, currencyNode.getElementsByTag("Scale").first().ownText());
				}
			}
		}
		executeAndCommitCommandUnits(SaveItemDBUnit.get(currencies));
		return null;
	}
}
