package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TermobrestProductHandler extends DefaultHandler implements CatalogConst {
	private static final String PARENT_EL = "parent";
	private static final String IMG_EL = "img";
	private static final String PRODUCTS_TAG = "products";
	private static final String ID_ATTR = "id";
	private static final String FILE_PREFIX = "sitefiles/";
	private static final String EXTRA_PAGE = "product_extra";
	private static final String MODS = "Модификации";
	private static final String DOC_EL = "doc";
	private static final String PARAMETER = "parameter";
	private static final String VALUE = "value";

	private IntegrateBase.Info info;
	private User initiator;
	private Locator locator;
	private boolean productsInProgress = false;
	private StringBuilder tagText = new StringBuilder();

	private Set<String> tags;
	private Map<String, String> params;
	private Map<String, String[]> documents;
	private boolean isMod = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (PRODUCTS_TAG.equals(qName)) {
			productsInProgress = true;
		}
		if (!productsInProgress) return;
		info.setLineNumber(locator.getLineNumber());
		if (PRODUCT_ITEM.equals(qName)) {
			params = new LinkedHashMap<>();
			documents = new LinkedHashMap<>();
			tags = new LinkedHashSet<>();
			params.put(CODE_PARAM, attributes.getValue(ID_ATTR));
		} else if(TEXT_PARAM.equals(qName)){
			isMod = MODS.equals(attributes.getValue(NAME));
		}else if(DOC_EL.equals(qName)){
			String[]v = new String[2];
			v[0] = attributes.getValue("face");
			v[1] = attributes.getValue("back");
			documents.put(attributes.getValue(NAME),v);
		}
		tagText = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (PRODUCTS_TAG.equals(qName)) {
			productsInProgress = false;
			info.setLineNumber(locator.getLineNumber());
		}
		if (!productsInProgress) return;
		info.setLineNumber(locator.getLineNumber());
		if (PRODUCT_ITEM.equals(qName)) {
			String code = params.get(CODE_PARAM);
			try {
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
				if (product == null) {
					Item section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, params.get(PARENT_EL));
					product = ItemUtils.newChildItem(PRODUCT_ITEM, section);
				}else {
					DelayedTransaction.executeSingle(initiator, ItemStatusDBUnit.restore(product.getId()));
				}
				product.setValue(CODE_PARAM, code);
				product.setValue(NAME_PARAM, params.get(NAME));
				product.setValueUI(TEXT_PARAM, params.get(TEXT_PARAM));
				Path img = Paths.get(AppContext.getContextPath(), FILE_PREFIX, params.get(IMG_EL));
				if(Files.isRegularFile(img)){
					product.setValue(MAIN_PIC_PARAM, img.toFile());
				}

				product.setValueUI(DESCRIPTION_PARAM, buildDocuments());

				processTags(product);

				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());
				info.increaseProcessed();
				createExtraPage(params.get(EXTRA_PAGE), product);
				createAssocExtraPage(product);
			} catch (Exception e) {
				ServerLogger.error("Integration error", e);
				info.addError(ExceptionUtils.getStackTrace(e), locator.getLineNumber(), locator.getColumnNumber());
			}
		}else if(TEXT_PARAM.equals(qName)){
			String key = isMod? EXTRA_PAGE : TEXT_PARAM;
			params.put(key, tagText.toString().trim());
		}else if(TAG_PARAM.equals(qName)){
			tags.add(tagText.toString().trim());
		}
		else if(!DOC_EL.equals(qName)){
			params.put(qName, tagText.toString().trim());
		}
	}

	private void processTags(Item product) throws Exception {
		XmlDocumentBuilder paramsXml = XmlDocumentBuilder.newDocPart();
		product.clearValue(TAG_PARAM);
		for (String tag : tags) {
			String[] nv = StringUtils.split(tag, ':');
			if(nv.length == 2) {
				String name = nv[0].substring(0, 1) + nv[0].substring(1).toLowerCase();
				String value = nv[1];

				paramsXml.startElement(PARAMETER)
						.startElement(NAME).addText(name).endElement()
						.startElement(VALUE).addText(value).endElement()
						.endElement();
				if (name.equalsIgnoreCase("МАТЕРИАЛ КОРПУСА")) {
					product.setValueUI(TAG_PARAM, value);
				}
			}else{
				product.setValueUI(TAG_PARAM, tag);
			}
		}
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());

		ItemQuery q = new ItemQuery(PARAMS_XML_ITEM);
		q.setParentId(product.getId(), false);
		Item paramsItem = q.loadFirstItem();
		paramsItem = paramsItem == null? ItemUtils.newChildItem(PARAMS_XML_ITEM, product) : paramsItem;
		paramsItem.setValue(XML_PARAM, paramsXml.toString());
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(paramsItem).noFulltextIndex().ignoreFileErrors());
	}

	private void createAssocExtraPage(Item product) throws Exception {
		ItemQuery q = new ItemQuery(EXTRA_PAGE, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
		q.setParentId(product.getId(), false, "general");
		q.addParameterCriteria(NAME, "Структура обозначения", "=", null, Compare.SOME);

		Item structurePage = q.loadFirstItem();

		q = new ItemQuery(EXTRA_PAGE, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
		q.setParentId(product.getId(), false, "general");
		q.addParameterCriteria(NAME, "Предназначение", "=", null, Compare.SOME);

		Item usePage = q.loadFirstItem();

		Item section = null;
		if(usePage == null || structurePage == null){
			section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, params.get(PARENT_EL));
			String name = section.getStringValue(NAME);
			String code = section.getStringValue(CODE_PARAM);
			q = new ItemQuery("shared_item_section", Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			q.addParameterCriteria(NAME, name+" "+code, "=", null, Compare.SOME);
			section = q.loadFirstItem();
		}

		if(structurePage == null && section != null){
			findAndAddAssocPage(product,section,"Структура обозначения");

		}
		if(usePage == null && section != null){
			findAndAddAssocPage(product,section,"Предназначение");
		}
	}

	private void findAndAddAssocPage(Item product, Item section, String name) throws Exception {
		ItemQuery q = new ItemQuery(EXTRA_PAGE, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
		q.setParentId(section.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
		q.addParameterCriteria(NAME, name, "=", null, Compare.SOME);
		Item page = q.loadFirstItem();
		if(page != null) {
			DelayedTransaction.executeSingle(initiator, CreateAssocDBUnit.childExistsSoft(page, product, ItemTypeRegistry.getAssocId("general")));
		}
	}

	private void createExtraPage(String s, Item product) throws Exception {
		ItemQuery q = new ItemQuery(EXTRA_PAGE);
		q.setParentId(product.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
		q.addParameterCriteria(NAME, MODS, "=", null, Compare.SOME);
		Item page = q.loadFirstItem();
		page = page == null? ItemUtils.newChildItem(EXTRA_PAGE, product) : page;
		page.setValue(NAME, MODS);
		page.setValue(TEXT_PARAM, s);
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(page).noFulltextIndex().ignoreFileErrors());
	}

	private String buildDocuments() {
		if (documents.isEmpty()) return "";

		XmlDocumentBuilder builder = XmlDocumentBuilder.newDocPart();
		builder.startElement("ul");
		for (String key : documents.keySet()) {
			String[] sides = documents.get(key);
			builder.startElement("li");

			builder.startElement("a", "href", FILE_PREFIX + sides[0], "target", "_blank").addText(key).endElement();
			if (StringUtils.isNotBlank(sides[1])) {
				builder.startElement("span", "style", "padding-left: 10px;").endElement();
				builder.startElement("a", "href", FILE_PREFIX + sides[1], "target", "_blank").addText("Обратная сторона").endElement();
			}
			builder.endElement();
		}
		builder.endElement();
		return builder.toString();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (!productsInProgress) return;
		tagText.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public TermobrestProductHandler(IntegrateBase.Info info, User initiator) {
		this.info = info;
		this.initiator = initiator;
		info.setProcessed(0);
	}
}
