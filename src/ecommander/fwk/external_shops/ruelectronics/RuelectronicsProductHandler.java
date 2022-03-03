package ecommander.fwk.external_shops.ruelectronics;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.User;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RuelectronicsProductHandler extends DefaultHandler {
	private Item catalog;
	private Item currency;
	private IntegrateBase.Info info;
	private User user;

	private Locator locator;
	private boolean productReady = false;
	private StringBuilder paramValueBuilder = new StringBuilder();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

	}

	public RuelectronicsProductHandler(Item catalog, Item currency, IntegrateBase.Info info, User user) {
		this.catalog = catalog;
		this.currency = currency;
		this.info = info;
		this.user = user;
	}
}
