package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by user on 20.08.2018.
 */
public class ImportFromOldSite extends IntegrateBase implements CatalogConst {
	private File integrationDoc;

	@Override
	protected boolean makePreparations() throws Exception {
		integrationDoc = new File(AppContext.getContextPath() + "catalog_takt.xml");
		return integrationDoc.exists();
	}

	@Override
	protected void integrate() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(integrationDoc, new Handler());
	}

	@Override
	protected void terminate() throws Exception {

	}

	private class Handler extends DefaultHandler {
		private Locator locator;
		private boolean fatalError = false;
		private HashMap<Long, Item> hierarchy = new HashMap<>();
		private Item catalog;
		private Item currentItem;
		private String base = "http://takt.by/";
		private int lineNumber;
		private StringBuilder paramValue;
		private String path;

		public Handler() throws Exception {
			catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
			currentItem = catalog;
			base = getVarSingleValue("site");
		}

		@Override
		public void endDocument() throws SAXException {}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			try {
				lineNumber = locator.getLineNumber();
				info.setLineNumber(lineNumber);
				if ("brand".equalsIgnoreCase(qName)) {
					currentItem = Item.newChildItem(ItemTypeRegistry.getItemType(MAIN_SECTION_ITEM), catalog);
					long id = Long.parseLong(attributes.getValue("id"));
					currentItem.setValue(CATEGORY_ID_PARAM, attributes.getValue("id"));
					currentItem.setValue(PARENT_ID_PARAM, String.valueOf(catalog.getId()));
					hierarchy.put(id, currentItem);
				} else if (SECTION_ITEM.equalsIgnoreCase(qName)) {
					long parentId = getParentId(attributes.getValue("path"));
					currentItem = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), hierarchy.get(parentId));
					long id = Long.parseLong(attributes.getValue("id"));
					currentItem.setValue(CATEGORY_ID_PARAM, String.valueOf(id));
					currentItem.setValue(PARENT_ID_PARAM, String.valueOf(parentId));
					//currentItem.setValue(NAME_PARAM, attributes.getValue("id"));
					hierarchy.put(id, currentItem);

				} else if (PRODUCT_ITEM.equalsIgnoreCase(qName)) {
					long parentId = getParentId(attributes.getValue("path"));
					currentItem = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT_ITEM), hierarchy.get(parentId));
					long id = Long.parseLong(attributes.getValue("id"));
					currentItem.setValue(CODE_PARAM, String.valueOf(id));
					path = attributes.getValue("path");

				}else if("picture_pair".equalsIgnoreCase(qName)){
					path = attributes.getValue("path");
				}
				else if (NAME.equalsIgnoreCase(qName)) {
					paramValue = new StringBuilder();
				} else if (currentItem != null && currentItem.getTypeName().equals(PRODUCT_ITEM)) {
					if (SHORT_PARAM.equalsIgnoreCase(qName) || TEXT_PARAM.equalsIgnoreCase(qName) || TECH_PARAM.equalsIgnoreCase(qName)) {
						paramValue = new StringBuilder();
					} else if (MAIN_PIC_PARAM.equalsIgnoreCase(qName) || "big".equalsIgnoreCase(qName) || PICTURE_ELEMENT.equalsIgnoreCase(qName)) {
						paramValue = new StringBuilder();
						paramValue.append(base);
						paramValue.append(path);
					}
				}
			}catch(Exception e){
				info.addError(e + e.getMessage(), lineNumber, locator.getColumnNumber());
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			lineNumber = locator.getLineNumber();
			info.setLineNumber(lineNumber);
			if("brand".equalsIgnoreCase(qName) || SECTION_ITEM.equalsIgnoreCase(qName) || PRODUCT_ITEM.equalsIgnoreCase(qName)){
				try {
					DelayedTransaction.executeSingle(User.getDefaultUser(), SaveItemDBUnit.get(currentItem, true));
				} catch (Exception e) {
					info.addError(e.toString() +" : "+e.getMessage(), lineNumber, locator.getColumnNumber());
				}
			}else if(MAIN_PIC_PARAM.equalsIgnoreCase(qName)){
				currentItem.setValue(MAIN_PIC_PARAM, paramValue.toString());
			}else if(NAME.equals(qName)){
				String val = paramValue.toString();
				currentItem.setValue(NAME_PARAM, val);
			}
			else if(PICTURE_ELEMENT.equals(qName)){
				currentItem.setValue(TEXT_PICS_PARAM, paramValue.toString());
			}
			else if("big".equals(qName)){
				currentItem.setValue(GALLERY_PARAM, paramValue.toString());
			}
			else if(TECH_PARAM.equalsIgnoreCase(qName) || TEXT_PARAM.equalsIgnoreCase(qName)){
				String val = paramValue.toString();
				val = val.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
				if(TECH_PARAM.equalsIgnoreCase(qName)) currentItem.setValue(DESCRIPTION_PARAM, val);
				else  currentItem.setValue(TEXT_PARAM, val);
			}
		}


		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (fatalError || paramValue == null) {return;}
			paramValue.append(ch, start, length);
		}

		private long getParentId(String path){
			String[] tmp = StringUtils.split(path,'/');
			return Long.parseLong(tmp[tmp.length-2]);
		}
	}
}
