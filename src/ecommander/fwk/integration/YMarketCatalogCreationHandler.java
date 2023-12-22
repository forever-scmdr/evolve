package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Создание разделов каталога
 * Created by E on 19/3/2018.
 */
public class YMarketCatalogCreationHandler extends DefaultHandler implements CatalogConst {

	private Locator locator;
	private boolean categoryReady = false;
	private StringBuilder chars = new StringBuilder();
	private HashMap<String, Item> categories = new HashMap<>();
	private HashMap<String, Pair<String, String>> newSectionParent = new HashMap<>(); // код раздела => название раздела, код родителя
	private Item catalog;
	private IntegrateBase.Info info;
	private User owner;

	private Item currentSection;
	private String code;
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
		if (StringUtils.equalsIgnoreCase(SECTIONS_ELEMENT, qName)) {
			categoryReady = true;
			return;
		}
		try {
			// Раздел
			if (StringUtils.equalsIgnoreCase(SECTION_ELEMENT, qName) && categoryReady) {
				code = attributes.getValue(ID_ATTR);
				String parentCode = attributes.getValue(PARENT_ID_ATTR);
				currentSection = categories.get(code);
				if (currentSection == null) {
					currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
					if (currentSection != null)
						categories.put(code, currentSection);
				}
				Item parentSection = catalog;
				if (StringUtils.isNotBlank(parentCode)) {
					parentSection = categories.get(parentCode);
					if (parentSection == null) {
						parentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, parentCode);
						if (parentSection != null)
							categories.put(parentCode, parentSection);
					}
				}

				if (currentSection == null) {
					if (parentSection == null) {
						//currentSection = Item.newChildItem(sectionDesc, catalog);
						newSectionParent.put(code, new Pair<>(null, parentCode));
						currentSection = null;
					} else {
						currentSection = Item.newChildItem(sectionDesc, parentSection);
						currentSection.setValue(PARENT_ID_PARAM, parentCode);
						currentSection.setValue(CATEGORY_ID_PARAM, code);
						categories.put(code, currentSection);
					}
				} else if (currentSection.isStatusHidden()) {
					DelayedTransaction.executeSingle(owner, ItemStatusDBUnit.restoreJustSelf(currentSection).noFulltextIndex().noTriggerExtra());
				}
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(code+ " " + e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
			String stackTrace = ExceptionUtils.getStackTrace(e);
			StringBuilder sb = new StringBuilder();
			for(String s : stackTrace.split("\n")){
				sb.append("<p>").append(s).append("</p>");
			}
			info.addError(sb.toString(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (StringUtils.equalsIgnoreCase(SECTIONS_ELEMENT, qName)) {
			categoryReady = false;
			// Теперь сохранить все новые разделы
			while (!newSectionParent.isEmpty()) {
				HashSet<String> newCodes = new HashSet<>(newSectionParent.keySet());
				for (String newCode : newCodes) {
					Pair<String, String> sec = newSectionParent.get(newCode);
					if (categories.containsKey(sec.getRight())) {
						Item section = Item.newChildItem(sectionDesc, categories.get(sec.getRight()));
						section.setValue(PARENT_ID_PARAM, sec.getRight());
						section.setValue(CATEGORY_ID_PARAM, newCode);
						section.setValue(NAME_PARAM, sec.getLeft());
						try {
							DelayedTransaction.executeSingle(owner, SaveItemDBUnit.get(section).noTriggerExtra());
							categories.put(newCode, section);
							newSectionParent.remove(newCode);
						} catch (Exception e) {
							ServerLogger.error("Integration error", e);
							info.addError(e.getMessage(), "Section " + newCode);
						}
					}
				}
			}

		} else if (StringUtils.equalsIgnoreCase(SECTION_ELEMENT, qName)) {
			try {
				if (currentSection != null) {
					currentSection.setValue(NAME_PARAM, StringUtils.trimToEmpty(chars.toString()));
					DelayedTransaction.executeSingle(owner, SaveItemDBUnit.get(currentSection).noTriggerExtra());
					currentSection = null;
					code = null;
				} else {
					Pair<String, String> secParent = newSectionParent.get(code);
					if (secParent != null) {
						secParent.setLeft(StringUtils.trimToEmpty(chars.toString()));
					}
				}
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
