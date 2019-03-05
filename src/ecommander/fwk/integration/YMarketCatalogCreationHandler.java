package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * Создание разделов каталога
 * Created by E on 19/3/2018.
 */
public class YMarketCatalogCreationHandler extends DefaultHandler implements CatalogConst {

	private Locator locator;
	private boolean categoryReady = false;
	private StringBuilder chars = new StringBuilder();
	private HashMap<String, Item> categories = new HashMap<>();
	private Item catalog;
	private IntegrateBase.Info info;
	private User owner;

	private Item currentSection;
	private ItemType sectionDesc;

	public YMarketCatalogCreationHandler(Item catalog, IntegrateBase.Info info, User owner) {
		this.catalog = catalog;
		this.sectionDesc = ItemTypeRegistry.getItemType(SECTION_ITEM);
		this.owner = owner;
		this.info = info;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		chars = new StringBuilder();
		if (StringUtils.equalsIgnoreCase(CATEGORIES_ELEMENT, qName)) {
			categoryReady = true;
			return;
		}
		try {
			// Раздел
			if (StringUtils.equalsIgnoreCase(CATEGORY_ELEMENT, qName) && categoryReady) {
				String code = attributes.getValue(ID_ATTR);
				String parentCode = attributes.getValue(PARENT_ID_ATTR);
					currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, code);
				if (currentSection == null) {
					if (StringUtils.isBlank(parentCode)) {
						currentSection = Item.newChildItem(sectionDesc, catalog);
					} else {
						currentSection = Item.newChildItem(sectionDesc, categories.get(parentCode));
						currentSection.setValue(PARENT_ID_PARAM, parentCode);
					}
					currentSection.setValue(CATEGORY_ID_PARAM, code);
				}
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (StringUtils.equalsIgnoreCase(CATEGORIES_ELEMENT, qName)) {
			categoryReady = false;
		} else if (StringUtils.equalsIgnoreCase(CATEGORY_ELEMENT, qName)) {
			try {
				currentSection.setValue(NAME_PARAM, StringUtils.trimToEmpty(chars.toString()));
				DelayedTransaction.executeSingle(owner, SaveItemDBUnit.get(currentSection, false));
				categories.put(currentSection.getStringValue(CATEGORY_ID_PARAM), currentSection);
				currentSection = null;
			} catch (Exception e) {
				ServerLogger.error("Integration error", e);
				info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			}
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		chars.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public HashMap<String, Item> getSections() {
		return categories;
	}
}
