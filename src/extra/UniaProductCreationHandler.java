package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.*;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

public class UniaProductCreationHandler extends DefaultHandler implements CatalogConst {

	//SAX-parser standard fields
	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();

	private static ItemType paramsXmlType;
	private static ItemType optionType = ItemTypeRegistry.getItemType("option");
	private static HashMap<String, String> BUILT_IN_PARAMS = new HashMap<>();
	private static HashMap<String, String> PARAMS_MAP = new HashMap<>();

	static {
		BUILT_IN_PARAMS.put("Завод", VENDOR_PARAM);
		BUILT_IN_PARAMS.put("factory", VENDOR_PARAM);
		BUILT_IN_PARAMS.put("number", VENDOR_CODE_PARAM);

		PARAMS_MAP.put("id", CODE_PARAM);

	}

	private IntegrateBase.Info info; // информация для пользователя
	private HashMap<String, String> singleParams = new HashMap<>();
	private HashMap<String, LinkedHashSet<String>> multipleParams = new HashMap<>();
	private HashSet<Item> children = new HashSet<>();
	private LinkedHashMap<String, String> specialParams = new LinkedHashMap<>();
	private ItemType productType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
	private List<Item> sectionsWithNewItemTypes = new LinkedList<Item>();
	private User initiator;
	private boolean isInsideProduct = false;
	private boolean isInsideOptions = false;
	private boolean isGallery = false;
	private HashMap<String, Item> existingItems = new HashMap<>();
	private Assoc optionAssoc = ItemTypeRegistry.getAssoc("option");
	private String currentSection;

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {

			if (StringUtils.equalsIgnoreCase(qName, PRODUCT_ELEMENT)) {
				String code = singleParams.get(CODE_PARAM);
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, code);
				Item section = existingItems.get(currentSection);
				if (product == null) {

					product = ItemUtils.newChildItem(productType.getName(), section);
				}

				for (String paramName : singleParams.keySet()) {
					product.setValueUI(paramName, singleParams.get(paramName));
				}
				for (String paramName : multipleParams.keySet()) {
					for (String value : multipleParams.get(paramName)) {
						product.setValueUI(paramName, value);
					}
				}

				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreUser());

				processSpecialParameters(product);

				for (Item child : children) {
					if (child.getItemType().equals(optionType)) {
						DelayedTransaction.executeSingle(initiator, CreateAssocDBUnit.childExistsSoft(child, product.getId(), optionAssoc.getId()).ignoreUser());
					}
				}

				info.increaseProcessed();
				resetProduct();
			}
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.setLineNumber(locator.getLineNumber());
			info.setLinePosition(locator.getColumnNumber());
			info.addError(e);
		}
	}

	private void processSpecialParameters(Item product) throws Exception {
		Byte[] ass = new Byte[] {ItemTypeRegistry.getPrimaryAssocId()};
		ArrayList<Item> children = ItemQuery.loadByParentId(product.getId(),ass);
		Item paramsXml;
		String paramsItemTypeName = CreateParametersAndFiltersCommand.createClassName(existingItems.get(currentSection));
		ItemType paramsItemType = ItemTypeRegistry.getItemType(paramsItemTypeName);
		if(paramsXmlType == null){
			sectionsWithNewItemTypes.add(existingItems.get(currentSection));
		}else{
			Item paramsItem;
		}

	}

	private void resetProduct() {
		singleParams = new HashMap<>();
		multipleParams = new HashMap<>();
		children = new HashMap<>();
		specialParams = new LinkedHashMap<>();
		isInsideOptions = false;
		isInsideProduct = false;
		isGallery = false;
		currentSection = "";
	}

	public UniaProductCreationHandler(HashMap<String, Item> sections, IntegrateBase.Info info, User initiator) {
		this.info = info;
		this.initiator = initiator;
		existingItems.putAll(sections);
	}

	public void setProductType(String productTypeName) {
		this.productType = ItemTypeRegistry.getItemType(productTypeName);
	}
}
