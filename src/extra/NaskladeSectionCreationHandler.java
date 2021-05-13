package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class NaskladeSectionCreationHandler extends DefaultHandler implements CatalogConst {
	private static final ItemType SECTION_DESC = ItemTypeRegistry.getItemType(SECTION_ITEM);

	private Locator locator;
	private boolean categoryReady = false;
	private StringBuilder tagText = new StringBuilder();
	private HashMap<String, Item> sectionMap = new HashMap<>();
	private HashMap<String, SectionInfo> newSectionParent = new HashMap<>(); // код раздела => название раздела, код родителя
	private HashMap<String, LinkedHashSet<Item>> groupMap = new HashMap<>();

	private Item catalog;
	private IntegrateBase.Info info;
	private User owner;

	private Item currentSection;
	private String currentSectionCode;

	private boolean endOfSecs = false;
	private boolean groupReady = false;

	public NaskladeSectionCreationHandler(Item catalog, IntegrateBase.Info info, User owner) {
		this.catalog = catalog;
		this.info = info;
		this.owner = owner;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (endOfSecs) return;
		try {
			if (CATEGORY_ELEMENT.equalsIgnoreCase(qName)) {
				String name = attributes.getValue(NAME_ATTR);
				String parentCode = attributes.getValue("parent");
				currentSectionCode = attributes.getValue(ID_ATTR);

				currentSection = getSection(currentSectionCode);
				Item parent = StringUtils.isNotBlank(parentCode) ? getSection(parentCode) : catalog;

				if (currentSection == null) {
					if (parent == null) {
						newSectionParent.put(currentSectionCode, new SectionInfo(name, parentCode));
					} else {
						currentSection = Item.newChildItem(SECTION_DESC, parent);
						currentSection.setValue(PARENT_ID_PARAM, parentCode);
						currentSection.setValue(CATEGORY_ID_PARAM, currentSectionCode);
						currentSection.setValue(NAME_PARAM, name);
						sectionMap.put(currentSectionCode, currentSection);
					}
				} else {
					currentSection.clearValue("group");
				}
			} else if ("group".equalsIgnoreCase(qName)) {
				groupReady = true;
				tagText = new StringBuilder();
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (endOfSecs) return;

		try {
			if (CATEGORIES_ELEMENT.equalsIgnoreCase(qName)) {

				while (!newSectionParent.isEmpty()) {
					HashSet<String> newCodes = new HashSet<>(newSectionParent.keySet());
					for (String newCode : newCodes) {
						SectionInfo sec = newSectionParent.remove(newCode);
						if (sectionMap.containsKey(sec.parent)) {
							Item section = Item.newChildItem(SECTION_DESC, sectionMap.get(sec.parent));
							section.setValue(PARENT_ID_PARAM, sec.parent);
							section.setValue(CATEGORY_ID_PARAM, newCode);
							if(section.isNew()) {
								section.setValue(NAME_PARAM, sec.name);
							}
							try {
								DelayedTransaction.executeSingle(owner, SaveItemDBUnit.get(section).noTriggerExtra());
								info.increaseProcessed();
								for (String group : section.getStringValues("group")){
									addToGroupMap(group, section);
								}
							} catch (Exception e) {
								ServerLogger.error("Integration error", e);
								info.addError(e.getMessage(), "Section " + newCode);
							}
						}
					}
				}

				endOfSecs = true;
				return;
			}
			else if ("group".equalsIgnoreCase(qName)) {
				String g = StringUtils.trimToEmpty(tagText.toString());
				if (StringUtils.isNotBlank(g)) {
					if (currentSection != null) {
						currentSection.setValueUI("group", g);
						addToGroupMap(g, currentSection);
					} else {
						SectionInfo sectionInfo = newSectionParent.get(currentSectionCode);
						sectionInfo.groups.add(g);
					}
				}
				groupReady = false;
			}
			else if(CATEGORY_ELEMENT.equalsIgnoreCase(qName)){
				if(currentSection != null){
					DelayedTransaction.executeSingle(owner, SaveItemDBUnit.get(currentSection).noTriggerExtra());
					info.increaseProcessed();
					currentSection = null;
					currentSectionCode = null;
				}
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.addError(e.getMessage(), locator.getLineNumber(), locator.getColumnNumber());
		}
	}

	private void addToGroupMap(String group, Item section){
		LinkedHashSet<Item> secs = groupMap.get(group);
		if(secs == null){
			secs = new LinkedHashSet<>();
			secs.add(section);
			groupMap.put(group, secs);
		}else{
			secs.add(section);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(endOfSecs)return;
		tagText.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public HashMap<String, LinkedHashSet<Item>> getSections() {
		return groupMap;
	}

	private Item getSection(String sectionCode) throws Exception {
		if (sectionMap.containsKey(sectionCode)) {
			return sectionMap.get(sectionCode);
		} else {
			Item section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, currentSectionCode, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			if (section != null) {
				sectionMap.put(sectionCode, section);
				return section;
			}
		}
		return null;
	}

	private class SectionInfo {
		String name;
		String parent;
		Set<String> groups = new HashSet<>();

		public SectionInfo(String name, String parent) {
			this.name = name;
			this.parent = parent;
		}
	}
}
