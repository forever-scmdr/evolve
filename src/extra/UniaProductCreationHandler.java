package extra;

import ecommander.fwk.*;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.*;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
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
	private static HashMap<String, String> PRODUCT_PARAMS_MAP = new HashMap<>();
	private static HashMap<String, String> OPTION_PARAMS_MAP = new HashMap<>();
	private static HashMap<String, String> SERIAL_PARAMS = new HashMap<>();

	private enum ComplectationChild {SERIAL, OPTION, COMPLECTATION}

	;


	static {
		PRODUCT_PARAMS_MAP.put("Завод", VENDOR_PARAM);
		PRODUCT_PARAMS_MAP.put("factory", VENDOR_PARAM);
		PRODUCT_PARAMS_MAP.put("number", VENDOR_CODE_PARAM);
		PRODUCT_PARAMS_MAP.put("picture", "pic_link");
		PRODUCT_PARAMS_MAP.put("id", CODE_PARAM);
		PRODUCT_PARAMS_MAP.put("price", PRICE_PARAM);
		PRODUCT_PARAMS_MAP.put("qantity_factory", "qty_factory");
		PRODUCT_PARAMS_MAP.put("qantity_smolensk", "qty_smolensk");
		PRODUCT_PARAMS_MAP.put("qantity_stored", "qty_store");
		PRODUCT_PARAMS_MAP.put("qantity_reserv", "qty_reserve");
		PRODUCT_PARAMS_MAP.put("qantity_free", "qty");
		PRODUCT_PARAMS_MAP.put("name", "name");
		PRODUCT_PARAMS_MAP.put("description", TEXT_PARAM);


		OPTION_PARAMS_MAP.put("id_opcii", CODE_PARAM);
		OPTION_PARAMS_MAP.put("part_id", CODE_PARAM);
		OPTION_PARAMS_MAP.put("name_opcii", NAME);
		OPTION_PARAMS_MAP.put("part_name", NAME);
		OPTION_PARAMS_MAP.put("price", PRICE_PARAM);
		OPTION_PARAMS_MAP.put("price_opt", "");

		SERIAL_PARAMS.put("id_tehniki", CODE_PARAM);
		SERIAL_PARAMS.put("name_tehniki", NAME);
		SERIAL_PARAMS.put("serial", "serial");
		SERIAL_PARAMS.put("time", "time");
	}

	private IntegrateBase.Info info; // информация для пользователя
	private HashMap<String, LinkedHashSet<String>> productParams = new HashMap<>();
	private LinkedHashMap<String, String> specialParams = new LinkedHashMap<>();
	private ItemType productType;
	private Set<Item> sectionsWithNewItemTypes = new HashSet<>();
	private User initiator;
	private boolean isInsideProduct = false;
	private boolean isInsideOptions = false;
	private HashMap<String, Item> existingItems = new HashMap<>();
	private List<HashMap<String, String>> optionsBuffer = new LinkedList<>();
	private List<Complectation> complectationBuffer = new LinkedList<>();
	private HashMap<String, String> currentOption = new HashMap<>();
	HashMap<String, String> currentSerial = new HashMap<>();
	private Complectation currentCompl;
	private Assoc optionAssoc = ItemTypeRegistry.getAssoc("option");
	private String currentSection;
	private boolean isInsideComplectation;
	private ComplectationChild complStaus;
	private static final String IMG_HOST = "http://62.109.11.85";


	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {

			if (StringUtils.equalsIgnoreCase(qName, PRODUCT_ELEMENT)) {
				String code = productParams.get(CODE_PARAM).iterator().next();
				Item product = ItemQuery.loadSingleItemByParamValue(productType.getName(), CODE_PARAM, code);
				Item section = existingItems.get(currentSection);

				if (section == null) {
					info.pushLog(code + "no section: " + currentSection);
					return;
				}

				if (product == null) {
					product = ItemUtils.newChildItem(productType.getName(), section);
				}
				for (String paramName : productParams.keySet()) {
					if (StringUtils.isBlank(paramName)) continue;
					for (String value : productParams.get(paramName)) {
						if (value != null) {
							value = "pic_link".equals(paramName) && StringUtils.isNotBlank(value) ? IMG_HOST + value : value;
							product.setValueUI(paramName, value);
							if (NAME.equalsIgnoreCase(paramName)) {
								product.setKeyUnique(Strings.translit(value));
							}
						} else {
							//product.clearValue(paramName);
							info.pushLog(productParams.get("name") + " " + code + " no value for: " + paramName);
						}
					}
				}

				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreUser());
				processSpecialParameters(product);
				processOptions(product);
				processComplectations(product);

				info.increaseProcessed();
				resetProduct();
			} else if ("options".equalsIgnoreCase(qName)) {
				isInsideOptions = false;
				optionsBuffer.add(currentOption);
			} else if ("complectation".equalsIgnoreCase(qName)) {
				isInsideComplectation = false;
				addNotEmptyComplectationChildren();

			} else if (isInsideProduct && !isInsideOptions && !isInsideComplectation) {
				processProductParams();
			} else if (isInsideOptions) {
				if (ItemTypeRegistry.getItemType("option").hasParameter(paramName)) {
					currentOption.put(paramName, StringUtils.normalizeSpace(paramValue.toString()));
				}
			} else if (isInsideComplectation) {
				String value = StringUtils.normalizeSpace(paramValue.toString());
				if (complStaus == ComplectationChild.SERIAL) {
					currentSerial.put(paramName, value);
				} else if (complStaus == ComplectationChild.OPTION) {
					if (ItemTypeRegistry.getItemType("option").hasParameter(paramName)) {
						currentOption.put(paramName, StringUtils.normalizeSpace(paramValue.toString()));
					}
				} else if (StringUtils.isNotBlank(paramName)) {
					currentCompl.params.put(paramName, value);
				}
			}
		} catch (Exception e) {
			handleException(e);
		}
	}


	private void addNotEmptyComplectationChildren() {
		if (!currentOption.isEmpty())
			currentCompl.options.add(currentOption);
		if (!currentSerial.isEmpty())
			currentCompl.serials.add(currentSerial);
		if (!currentCompl.isEmpty())
			complectationBuffer.add(currentCompl);
	}

	private void processComplectations(Item product) throws Exception {
		int i = 0;
		for (Complectation complectation : complectationBuffer) {
			i++;
			complectation.params.put("number", String.valueOf(i));
			presistComplectation(complectation, product);
		}
	}

	private void presistComplectation(Complectation complectationTemplate, Item product) throws Exception {
		Item complectation = ItemUtils.newChildItem("complectation", product);
		for (Map.Entry<String, String> e : complectationTemplate.params.entrySet()) {
			complectation.setValueUI(e.getKey(), e.getValue());
		}
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(complectation).noFulltextIndex().ignoreUser());
		optionsBuffer = complectationTemplate.options;
		processOptions(complectation);
		optionsBuffer = new LinkedList<>();
		int i = 0;
		for (HashMap<String, String> serialTemplate : complectationTemplate.serials) {
			Item serial = ItemUtils.newChildItem("base_complectation_product", complectation);
			i++;
			serial.setValue("number", i);
			for (Map.Entry<String, String> e : serialTemplate.entrySet()) {
				serial.setValueUI(e.getKey(), e.getValue());
			}
			DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(serial).noFulltextIndex().ignoreUser());
		}
	}

	private void processOptions(Item product) throws Exception {
		ensureOptions();
		attachOptionsToProduct(product);
	}

	private void attachOptionsToProduct(Item product) throws Exception {
		for (HashMap<String, String> opt : optionsBuffer) {
			String code = opt.get(CODE_PARAM);
			Item option = existingItems.get(code);
			DelayedTransaction.executeSingle(initiator, CreateAssocDBUnit.childExistsSoft(option, product.getId(), optionAssoc.getId()).ignoreUser());
		}
	}

	/**
	 * ensures that options are up to date (updates existing or creates if not exist)
	 */
	private void ensureOptions() throws Exception {
		Item section = existingItems.get(currentSection);
		for (HashMap<String, String> opt : optionsBuffer) {
			String code = opt.get(CODE_PARAM);
			Item option = existingItems.containsKey(code) ? existingItems.get(code) : ItemUtils.newChildItem("option", section);
			for (Map.Entry<String, String> e : opt.entrySet()) {
				option.setValueUI(e.getKey(), e.getValue());
			}
			DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(option).noFulltextIndex().ignoreUser());
			if (!existingItems.containsKey(code)) {
				existingItems.put(code, option);
			}
		}
	}

	private void processProductParams() {
		if (PRICE_OPT_PARAM.equalsIgnoreCase(paramName) || StringUtils.isBlank(paramName)) {
			return;
		}
		if (productType.hasParameter(paramName)) {
			LinkedHashSet<String> values = productParams.getOrDefault(paramName, new LinkedHashSet<>());
			String v = "pic_link".equalsIgnoreCase(paramName) ? paramValue.toString() : StringUtils.normalizeSpace(paramValue.toString());
			values.add(v);
			if (!productParams.containsKey(paramName)) {
				productParams.put(paramName, values);
			}
		} else if ("section_id".equalsIgnoreCase(paramName)) {
			currentSection = paramValue.toString();
		} else {
			specialParams.put(paramName, StringUtils.normalizeSpace(paramValue.toString()));
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		parameterReady = false;
		paramName = "";
		paramValue = new StringBuilder();

		if ("product".equalsIgnoreCase(qName)) {
			isInsideProduct = true;

		} else if ("options".equalsIgnoreCase(qName)) {
			isInsideOptions = true;
		} else if ("complectation".equalsIgnoreCase(qName)) {
			isInsideComplectation = true;
			complStaus = ComplectationChild.SERIAL;
			currentSerial = new HashMap<>();
			currentOption = new HashMap<>();
			currentCompl = new Complectation();
		} else if (isInsideProduct) {
			//processing complectation
			if (isInsideComplectation) {
				parameterReady = true;
				complStaus = checkComplStatus(qName);
				if (complStaus == ComplectationChild.SERIAL) {
					paramName = SERIAL_PARAMS.getOrDefault(qName, PRODUCT_PARAMS_MAP.get(qName));
					if (CODE_PARAM.equalsIgnoreCase(paramName) && !currentSerial.isEmpty()) {
						currentCompl.serials.add(currentSerial);
						currentSerial = new HashMap<>();
					} else if ("qty_reserve".equalsIgnoreCase(paramName)) {
						String time = attributes.getValue("time");
						if (StringUtils.isNotBlank(time))
							currentSerial.put("reserve_time", time);
					} else if ("qty_store".equalsIgnoreCase(paramName)) {
						String time = attributes.getValue("time");
						if (StringUtils.isNotBlank(time))
							currentSerial.put("stored_time", time);
					}
				} else if (complStaus == ComplectationChild.OPTION) {
					paramName = OPTION_PARAMS_MAP.get(qName);
					if (CODE_PARAM.equalsIgnoreCase(paramName) && !currentSerial.isEmpty()) {
						if (!currentOption.isEmpty()) {
							currentCompl.options.add(currentOption);
						}
						currentOption = new HashMap<>();
					}
				} else {
					paramName = PRODUCT_PARAMS_MAP.get(qName);
				}
			}
			//processing options
			else if (isInsideOptions) {
				paramName = OPTION_PARAMS_MAP.getOrDefault(qName, qName);
				parameterReady = true;
				if (CODE_PARAM.equalsIgnoreCase(paramName)) {
					if (!currentOption.isEmpty()) {
						optionsBuffer.add(currentOption);
					}
					currentOption = new HashMap<>();
				}
			}
			//processing params
			else {
				if (PRODUCT_PARAMS_MAP.containsKey(qName) || "section_id".equalsIgnoreCase(qName)) {
					paramName = PRODUCT_PARAMS_MAP.getOrDefault(qName, qName);
					parameterReady = true;
				} else if ("param".equalsIgnoreCase(qName)) {
					String caption = attributes.getValue(NAME);
					parameterReady = true;
					paramName = PRODUCT_PARAMS_MAP.getOrDefault(caption, caption);
				}
			}
		}
	}

	private ComplectationChild checkComplStatus(String qName) {
		if (PRICE_PARAM.equals(qName)) return ComplectationChild.COMPLECTATION;
		if (complStaus == ComplectationChild.SERIAL && !OPTION_PARAMS_MAP.containsKey(qName)) {
			return ComplectationChild.SERIAL;
		} else if (OPTION_PARAMS_MAP.containsKey(qName)) {
			if (currentOption.isEmpty() && !OPTION_PARAMS_MAP.get(qName).equals(CODE_PARAM)) {
				return ComplectationChild.COMPLECTATION;
			}
			return ComplectationChild.OPTION;
		}
		return ComplectationChild.COMPLECTATION;
	}

	private void processSpecialParameters(Item product) throws Exception {
		if (!specialParams.isEmpty()) {
			Byte[] ass = new Byte[]{ItemTypeRegistry.getPrimaryAssocId()};
			ArrayList<Item> children = ItemQuery.loadByParentId(product.getId(), ass);
			populateAuxParamsOrFallback(product, children);
			populateXml(product, children);
		}

	}

	private Item ensureChild(String itemTypeName, Item parent, Collection<Item> existingChildren) {
		for (Item el : existingChildren) {
			if (el.getTypeName().equals(itemTypeName)) {
				return el;
			}
		}
		return ItemUtils.newChildItem(itemTypeName, parent);
	}

	private void populateXml(Item product, ArrayList<Item> children) throws Exception {
		Item paramsXml = ensureChild(PARAMS_XML_ITEM, product, children);

		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		for (Map.Entry<String, String> entry : specialParams.entrySet()) {
			xml.startElement("parameter")
					.addElement("name", entry.getKey())
					.addElement("value", entry.getValue())
					.endElement();
		}
		paramsXml.setValue(XML_PARAM, xml.toString());
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(paramsXml).ignoreUser().noFulltextIndex().noTriggerExtra());
	}

	/**
	 * Finds the item type for "params" and ensures it existence
	 * if item type does not exist or has changed adds the section to the list of sections with new item types.
	 *
	 * @param product  - parent of the "params" item
	 * @param children - list of existing item children
	 */
	private void populateAuxParamsOrFallback(Item product, List<Item> children) throws Exception {

		// load itemType for the aux params item
		String paramsItemTypeName = CreateParametersAndFiltersCommand.createClassName(existingItems.get(currentSection));
		ItemType paramsItemType = ItemTypeRegistry.getItemType(paramsItemTypeName);
		if (paramsItemType == null) {
			sectionsWithNewItemTypes.add(existingItems.get(currentSection));
			return;
		}

		Item aux = ensureChild(paramsItemTypeName, product, children);
		for (Map.Entry<String, String> param : specialParams.entrySet()) {
			String name = Strings.createXmlElementName(param.getKey());
			String value = param.getValue();
			try {
				aux.setValueUI(name, value);
			} catch (Exception e) {
				//fallback if parameter not exists
				sectionsWithNewItemTypes.add(existingItems.get(currentSection));
				return;
			}
		}
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(aux).ignoreUser().noFulltextIndex().noTriggerExtra());
	}

	@Override
	public void startDocument() {
		if (productType.hasChild("option", "option")) {
			try {
				List<Item> options = new ItemQuery("option").loadItems();
				for (Item option : options) {
					existingItems.put(option.getStringValue(CODE_PARAM), option);
				}

				info.setCurrentJob("Удаление комплектаций");

				ItemQuery productsQuery = new ItemQuery("complectation");
				productsQuery.setLimit(1000);

				List<Item> oldComplectations;
				while ((oldComplectations = productsQuery.loadItems()).size() > 0) {
					for (Item old : oldComplectations) {
						DelayedTransaction.executeSingle(initiator, ItemStatusDBUnit.delete(old.getId()).ignoreUser(true).noFulltextIndex());
						info.increaseProcessed();
					}
				}
				info.setProcessed(0);
				info.setCurrentJob("Создание товаров");

			} catch (Exception e) {
				handleException(e);
			}
		}
	}

	private void resetProduct() {
		productParams = new HashMap<>();
		specialParams = new LinkedHashMap<>();
		isInsideProduct = false;
		currentSection = "";
		optionsBuffer = new LinkedList<>();
		currentOption = new HashMap<>();
		currentSerial = new HashMap<>();
		complectationBuffer = new LinkedList<>();
	}

	public UniaProductCreationHandler(HashMap<String, Item> sections, IntegrateBase.Info info, User initiator) {
		super();
		this.info = info;
		this.initiator = initiator;
		existingItems.putAll(sections);
	}

	public void setProductType(String productTypeName) {
		this.productType = ItemTypeRegistry.getItemType(productTypeName);
	}

	public Collection<Item> getSectionsWithNewItemTypes() {
		return sectionsWithNewItemTypes;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (parameterReady)
			paramValue.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	private void handleException(Throwable e) {
		ServerLogger.error("Integration error", e);
		info.setLineNumber(locator.getLineNumber());
		info.setLinePosition(locator.getColumnNumber());
		info.addError(e);
	}

	private static class Complectation {
		private List<HashMap<String, String>> options = new LinkedList<>();
		private List<HashMap<String, String>> serials = new LinkedList<>();
		private HashMap<String, String> params = new HashMap<>();

		boolean isEmpty() {
			return options.isEmpty() && serials.isEmpty() && params.isEmpty();
		}
	}
}
