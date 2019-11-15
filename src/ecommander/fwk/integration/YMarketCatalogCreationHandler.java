package ecommander.fwk.integration;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
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
import java.util.List;

/**
 * Создание разделов каталога
 * Created by E on 19/3/2018.
 */
public class YMarketCatalogCreationHandler extends DefaultHandler implements CatalogConst {

	private Locator locator;
	private boolean categoryReady = false;
	private StringBuilder chars = new StringBuilder();
	private HashMap<String, Pair<Item, Boolean>> categories = new HashMap<>(); // код раздела => раздел, является финальным
	private HashMap<String, Pair<String, String>> newSectionParent = new HashMap<>(); // код раздела => название раздела, код родителя
	private Item catalog;
	private IntegrateBase.Info info;
	private User owner;

	private Item currentSection;
	private String code;
	private ItemType sectionDesc;
	private HashSet<String> ignoreCodes;
	private boolean justPrice;

	public YMarketCatalogCreationHandler(Item catalog, IntegrateBase.Info info, User owner, HashSet<String> ignoreCodes, boolean justPrice) {
		this.catalog = catalog;
		this.sectionDesc = ItemTypeRegistry.getItemType(SECTION_ITEM);
		this.owner = owner;
		this.info = info;
		this.justPrice = justPrice;
		for(String ignoreCode : ignoreCodes){
			info.addLog("ignore: "+ignoreCode);
		}
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
				code = attributes.getValue(ID_ATTR);
				// пропустить некоторые разделы
				if (ignoreCodes != null && ignoreCodes.contains(code)) {
					info.addLog(code+" ignored.");
					currentSection = null;
					return;
				}
				String parentCode = attributes.getValue(PARENT_ID_ATTR);
				currentSection = categories.get(code).getLeft();
				if (currentSection == null) {
					currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, code);
					if (currentSection != null)
						categories.put(code, new Pair<>(currentSection, true));
				}
				Item parentSection = catalog;
				if (StringUtils.isNotBlank(parentCode)) {
					parentSection = categories.get(parentCode).getLeft();
					if (parentSection == null) {
						parentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, parentCode);
						if (parentSection != null)
							categories.put(parentCode, new Pair<>(parentSection, false));
					} else {
						categories.get(parentCode).setRight(false);
					}
				}

				if (currentSection == null && !justPrice) {
					if (parentSection == null) {
						//currentSection = Item.newChildItem(sectionDesc, catalog);
						newSectionParent.put(code, new Pair<>(null, parentCode));
						currentSection = null;
						info.addError("Не найден родительский раздел для раздела " + code, locator.getLineNumber(), locator.getColumnNumber());
					} else {
						currentSection = Item.newChildItem(sectionDesc, parentSection);
						currentSection.setValue(PARENT_ID_PARAM, parentCode);
						currentSection.setValue(CATEGORY_ID_PARAM, code);
						categories.put(code, new Pair<>(currentSection, true));
					}
				}

				// Скрыть все товары раздела
				int hiddenCount = 0;
				long lastProductId = 0;
				if (currentSection != null) {
					info.setCurrentJob("скрывается " + currentSection.getStringValue(NAME_PARAM));
					// Проверка, есть ли у раздела товары
					Item product = new ItemQuery(PRODUCT_ITEM, Item.STATUS_NORMAL, Item.STATUS_HIDDEN)
							.setParentId(currentSection.getId(), false).setLimit(1).loadFirstItem();
					if (product != null) {
						DelayedTransaction.executeSingle(owner, ItemStatusDBUnit.hideChildren(currentSection));
					}
					/*
					ItemQuery proudctsQuery = new ItemQuery(PRODUCT_ITEM, Item.STATUS_NORMAL, Item.STATUS_HIDDEN, Item.STATUS_DELETED)
							.setParentId(currentSection.getId(), false).setLimit(1000);
					List<Item> visibleProducts;
					DelayedTransaction transaction = new DelayedTransaction(owner);
					do {
						Timer.getTimer().start("loading products");
						visibleProducts = proudctsQuery.setIdSequential(lastProductId).loadItems();
						long nanos = Timer.getTimer().getNanos("loading products");
						Timer.getTimer().stop("loading products");
						if(nanos/1000000 > 100){
							//String queryLog = String.format(proudctsQuery.getSqlForLog() + ". Took: %,d ms.", nanos/1000000);
							info.addSlowQuery(proudctsQuery.getSqlForLog(), nanos);
							//info.pushLog(queryLog);
						}
						for (Item visibleProduct : visibleProducts) {
							transaction.addCommandUnit(ItemStatusDBUnit.hide(visibleProduct));
							lastProductId = visibleProduct.getId();
							if (transaction.getCommandCount() >= 10) {
								Timer.getTimer().start("hiding");
								transaction.execute();
								nanos = Timer.getTimer().getNanos("hiding");
								if(nanos/1000000 > 100){
									//String queryLog = String.format(proudctsQuery.getSqlForLog() + ". Took: %,d ms.", nanos/1000000);
									info.addSlowQuery("Скрываются товары", nanos);
									//info.pushLog(queryLog);
								}
							}
						}
						//Timer.getTimer().start("hiding");
						transaction.execute();
						//nanos = Timer.getTimer().getNanos("hiding");
						//Timer.getTimer().stop("hiding");
						//info.addLog(String.format("hiding: %,d", nanos/1000000));
						hiddenCount += visibleProducts.size();
						info.setCurrentJob("скрывается " + currentSection.getStringValue(NAME_PARAM) + " * скрыто товаров " + hiddenCount);
					} while (visibleProducts.size() > 0);
//					ItemQuery sectionsQuery = new ItemQuery(SECTION_ITEM).setParentId(currentSection.getId(), true).setLimit(1);
//					if (sectionsQuery.loadFirstItem() == null) {
//						info.setCurrentJob("скрывается " + currentSection.getStringValue(NAME_PARAM));
//						DelayedTransaction.executeSingle(owner, ItemStatusDBUnit.hideChildren(currentSection));
//					}
					*/
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
			// Теперь сохранить все новые разделы
			boolean newParentsListModified = true;
			while (!newSectionParent.isEmpty() && newParentsListModified) {
				newParentsListModified = false;
				HashSet<String> newCodes = new HashSet<>(newSectionParent.keySet());
				for (String newCode : newCodes) {
					Pair<String, String> sec = newSectionParent.get(newCode);
					if (categories.containsKey(sec.getRight())) {
						categories.get(sec.getRight()).setRight(false);
						Item section = Item.newChildItem(sectionDesc, categories.get(sec.getRight()).getLeft());
						section.setValue(PARENT_ID_PARAM, sec.getRight());
						section.setValue(CATEGORY_ID_PARAM, newCode);
						section.setValue(NAME_PARAM, sec.getLeft());
						try {
							DelayedTransaction.executeSingle(owner, SaveItemDBUnit.get(section).noTriggerExtra());
							categories.put(newCode, new Pair<>(section, true));
							newSectionParent.remove(newCode);
							newParentsListModified = true;
						} catch (Exception e) {
							ServerLogger.error("Integration error", e);
							info.addError(e.getMessage(), "Section " + newCode);
						}
					}
				}
			}
		} else if (StringUtils.equalsIgnoreCase(CATEGORY_ELEMENT, qName)) {
			try {
				if (currentSection != null) {
					currentSection.setValue(NAME_PARAM, StringUtils.trimToEmpty(chars.toString()));
					DelayedTransaction.executeSingle(owner, SaveItemDBUnit.get(currentSection).noTriggerExtra());
					currentSection = null;
					code = null;
				} else {
					// пропустить некоторые разделы
					if (ignoreCodes != null && ignoreCodes.contains(code))
						return;
					Pair<String, String> secParent = newSectionParent.get(code);
					if (secParent != null) {
						secParent.setLeft(StringUtils.trimToEmpty(chars.toString()));
					}
				}
				info.increaseProcessed();
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

	public HashMap<String, Pair<Item, Boolean>> getSections() {
		return categories;
	}
}
