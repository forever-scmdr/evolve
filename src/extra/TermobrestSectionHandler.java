package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedHashMap;

public class TermobrestSectionHandler extends DefaultHandler implements CatalogConst {
    private static final ItemType SECTION_DESC = ItemTypeRegistry.getItemType(SECTION_ITEM);
    private static final ItemType SHARED_DESC = ItemTypeRegistry.getItemType("shared_item_section");
    private static final ItemType TEXT_ITEM_DESC = ItemTypeRegistry.getItemType("product_extra");
    private static final String SEC_EL = "section";
    private static final String ID_ATTR = "id";
    private static final String PARENT_ATTR = "parent_id";
    private static final String END_EL = "sections";
    private static final String EXTRA_PAGE_NAME = "Структура обозначения";
    private Item catalog;
    private Item sharedSection;
    private IntegrateBase.Info info;
    private User initiator;
    private boolean endOfSecs = false;
    private StringBuilder tagText = new StringBuilder();
    private Locator locator;
    private Item currentSec;
    private LinkedHashMap<String, Item> secCodeMap = new LinkedHashMap<>();


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        info.setLineNumber(locator.getLineNumber());
        if (endOfSecs) return;
        try {
            if(SEC_EL.equals(qName)){
                String code = attributes.getValue(ID_ATTR);
                String parentCode = attributes.getValue(PARENT_ATTR);

                currentSec = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
                Item parent = StringUtils.isBlank(parentCode)? catalog : secCodeMap.get(parentCode);
                currentSec = currentSec == null? Item.newChildItem(SECTION_DESC, parent): currentSec;
                currentSec.setValue(CODE_PARAM, code);
                secCodeMap.put(code, currentSec);
            }else {
                tagText = new StringBuilder();
            }
        } catch (Exception e) {
            ServerLogger.error("Integration error", e);
            info.addError(ExceptionUtils.getStackTrace(e), locator.getLineNumber(), locator.getColumnNumber());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(END_EL.equals(qName)){
            endOfSecs = true;
            info.setLineNumber(locator.getLineNumber());
        }
        if (endOfSecs) return;
        info.setLineNumber(locator.getLineNumber());
        if(NAME.equals(qName)){
            currentSec.setValue(NAME_PARAM, tagText.toString());
        }else if(TEXT_PARAM.equals(qName)){
            String value = tagText.toString().trim();
            try {
                String code = currentSec.getStringValue(CODE_PARAM);
                String name = currentSec.getStringValue(NAME_PARAM);
                Item sharedTextSection = ItemQuery.loadSingleItemByParamValue("shared_item_section", NAME_PARAM, name + " "+code);
                if(sharedTextSection == null){
                    sharedTextSection = Item.newChildItem(SHARED_DESC, sharedSection);
                    sharedTextSection.setValue(NAME_PARAM, name + " "+code);
                    DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(sharedTextSection).noFulltextIndex().ignoreFileErrors());
                    Item text = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), sharedTextSection);
                    text.setValue(NAME_PARAM, EXTRA_PAGE_NAME);
                    text.setValue(TEXT_PARAM, value);
                    DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(text).noFulltextIndex().ignoreFileErrors());
                }else{
                    ItemQuery q = new ItemQuery("product_extra");
                    q.setParentId(sharedTextSection.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
                    q.addParameterCriteria(NAME_PARAM, EXTRA_PAGE_NAME, "=", null, Compare.SOME);
                    Item textItem = q.loadFirstItem();
                    textItem = textItem == null? Item.newChildItem(TEXT_ITEM_DESC, sharedTextSection) : textItem;
                    textItem.setValue(TEXT_PARAM, value);
                    DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(textItem).noFulltextIndex().ignoreFileErrors());
                }
            }catch (Exception e){
                ServerLogger.error("Integration error", e);
                info.addError(ExceptionUtils.getStackTrace(e), locator.getLineNumber(), locator.getColumnNumber());
            }
        }else if(SEC_EL.equals(qName)){
            try {
                DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(currentSec).noFulltextIndex().ignoreFileErrors());
                info.increaseProcessed();
            } catch (Exception e) {
                ServerLogger.error("Integration error", e);
                info.addError(ExceptionUtils.getStackTrace(e), locator.getLineNumber(), locator.getColumnNumber());
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (endOfSecs) return;
        tagText.append(ch, start, length);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public TermobrestSectionHandler(Item catalog, Item sharedSection, IntegrateBase.Info info, User owner) {
        this.catalog = catalog;
        this.info = info;
        this.initiator = owner;
        this.sharedSection = sharedSection;
        info.setProcessed(0);
    }

}
