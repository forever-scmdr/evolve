package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ResizeImagesFactory;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class NaskladeProductCreationHandler extends DefaultHandler implements CatalogConst {

	private static final Set<String> SINGLE_ELEMENTS = new HashSet(){{
		add(NAME_ELEMENT);
		add(URL_ELEMENT);
		add(PRICE_ELEMENT);
		add(VENDOR_ELEMENT);
		add(VENDOR_CODE_ELEMENT);
		add(CATEGORY_ID_ELEMENT);
		//add(OLDOPTPRICE_ELEMENT);
		//add(NEXT_DELIVERY_ELEMENT);
		add(COUNTRY_OF_ORIGIN_ELEMENT);
		add(DESCRIPTION_ELEMENT);
		add(QUANTITY_ELEMENT);
		add("desc");
		add("price_sd1_2");
		add("price_sd2_2");
		add("orig_country");
		add("barcode");
		add("net_weight");
		add("gross_weight");
		add("length");
		add("width");
		add("height");
	}};
	private static final Set<String> MULTIPLE_ELEMENTS = new HashSet(){{
		add(STATUS_ELEMENT);
		add(PICTURE_ELEMENT);
	}};
	private static final Set<String> EXTRA_PAGE_ELEMENTS = new HashSet(){{
		add("tech");
		add("package");
		add("video");
	}};
	private static final Set<String> WARRANTY_ELEMENTS = new HashSet(){{
		add("warranty");
		add("service_center");
	}};

	private static final Map<String, String> ELEMENT_PARAM_DICTIONARY = new HashMap(){{
		put(NAME_ELEMENT, NAME_PARAM);
		put(URL_ELEMENT, URL_PARAM);
		put(PRICE_ELEMENT, PRICE_PARAM);
		put(VENDOR_ELEMENT, VENDOR_PARAM);
		put(VENDOR_CODE_ELEMENT, VENDOR_PARAM);
		put(CATEGORY_ID_ELEMENT, CATEGORY_ID_PARAM);
		put(COUNTRY_OF_ORIGIN_ELEMENT, COUNTRY_PARAM);
		put(DESCRIPTION_ELEMENT, TEXT_PARAM);
		put("desc", DESCRIPTION_PARAM);
		put(QUANTITY_ELEMENT, QTY_PARAM);
		put("price_sd1_2", PRICE_OPT_PARAM);
		put("price_sd2_2", "price_opt_2");
		put("orig_country", "orig_country");
		put("barcode", "barcode");
		put("net_weight", "net_weight");
		put("gross_weight", "gross_weight");
		put("length", "length");
		put("width", "width");
		put("height", "height");
		put(STATUS_ELEMENT, "label");
		put("tech", "Характеристики");
		put("package", "Упаковка");
		put("video", "Видео");
		put("warranty", "months");
		put("service_center", "service_center");
	}};

	//private int picCounter = 0;

	private Map<String, Item> sections;
	private IntegrateBase.Info info;
	private User initiator;

	private HashMap<String, String> singleParams;
	private HashMap<String, LinkedHashSet<String>> multipleParams;
	private HashMap<String, String> extraPageParams;
	private HashMap<String, String> warrantyParams;
	private LinkedHashMap<String, String> specialParams;

	private Locator locator;
	private boolean parameterReady = false;
	private String paramName;
	private StringBuilder paramValue = new StringBuilder();
	private boolean isInsideOffer = false;
	private ItemType productType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
	private int productStartLineNumber = 0;

	public NaskladeProductCreationHandler(Map<String, Item> sections, IntegrateBase.Info info, User initiator) {
		this.sections = sections;
		this.info = info;
		this.initiator = initiator;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		try {
			if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
				HashSet<String> productContainers = new HashSet<>();
				String code = singleParams.get(ID_ATTR);

				if (StringUtils.isBlank(code)){
					info.addError("No code", "line: " + productStartLineNumber);
					info.increaseProcessed();
					isInsideOffer = false;
					parameterReady = false;
					return;
				}

				//String secCode = singleParams.get(CATEGORY_ID_ELEMENT);
				Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, OFFER_ID_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
				boolean isExistingProduct = product != null;
				String secCode = singleParams.getOrDefault(CATEGORY_ID_ELEMENT, "");
				if (!isExistingProduct) {
					Item section = sections.get(secCode);
					if (section != null) {
						product = Item.newChildItem(productType, section);
						productContainers.add(secCode);
					} else {
						info.addError("Не найден раздел с номером " + secCode, locator.getLineNumber(), locator.getColumnNumber());
						return;
					}
				}

				product.setValue(CODE_PARAM, code);
				product.setValue(OFFER_ID_PARAM, code);
				product.setValue(AVAILABLE_PARAM, StringUtils.equalsIgnoreCase(singleParams.get(AVAILABLE_ATTR), TRUE_VAL) ? (byte) 1 : (byte) 0);

				for(String element : singleParams.keySet()){
					if(ELEMENT_PARAM_DICTIONARY.containsKey(element)){
						String param = ELEMENT_PARAM_DICTIONARY.get(element);
						String value = singleParams.get(element);
						value = value.trim();
						product.setValueUI(param, value);
					}
				}
				for(String element : multipleParams.keySet()){
					if(ELEMENT_PARAM_DICTIONARY.containsKey(element)){
						String param = ELEMENT_PARAM_DICTIONARY.get(element);
						Set<String> values = multipleParams.get(element);
						product.clearValue(param);
						for(String value : values) {
							value = value.trim();
							product.setValueUI(param, value);
						}
					}
				}
				boolean isSaved = addPics(product);
				if(!isSaved){
					DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());
				}

				if (isExistingProduct){
					addWarrantyAndPages(product);
				}else{
					addWarrantyAndPagesQuick(product);
				}

				info.increaseProcessed();
				isInsideOffer = false;
			}

			else if (isInsideOffer && SINGLE_ELEMENTS.contains(qName) && parameterReady) {
				singleParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			else if(isInsideOffer && EXTRA_PAGE_ELEMENTS.contains(qName) && parameterReady){
				extraPageParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			else if(isInsideOffer && WARRANTY_ELEMENTS.contains(qName) && parameterReady){
				warrantyParams.put(paramName, StringUtils.trim(paramValue.toString()));
			}

			else if (isInsideOffer && MULTIPLE_ELEMENTS.contains(qName) && parameterReady) {
				LinkedHashSet<String> vals = multipleParams.computeIfAbsent(qName, k -> new LinkedHashSet<>());
				if (StringUtils.isNotBlank(StringUtils.trim(paramValue.toString())))
					vals.add(paramValue.toString());
			}

			parameterReady = false;
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			info.setLineNumber(locator.getLineNumber());
			info.setLinePosition(locator.getColumnNumber());
			info.addError(e);
		}
	}

	private void addWarrantyAndPages(Item product) throws Exception {
		Byte[] assId = new Byte[]{new Byte(ItemTypeRegistry.getPrimaryAssocId())};
		ArrayList<Item> subs = ItemQuery.loadByParentId(product.getId(), assId);
		Item warranty = null;
		Item tech = null;
		Item video = null;
		Item pack = null;

		for(Item item : subs){
			if(item.getTypeName().equals("warranty")){
				warranty = item;
			}else if(item.getTypeName().equals("product_extra")){
				String name = item.getStringValue(NAME_PARAM);
				if(ELEMENT_PARAM_DICTIONARY.get("tech").equals(name)){
					tech = item;
				}else if(ELEMENT_PARAM_DICTIONARY.get("package").equals(name)){
					pack = item;
				}else if(ELEMENT_PARAM_DICTIONARY.get("video").equals(name)){
					video = item;
				}
			}
		}

		if(warranty == null){
			warranty = Item.newChildItem(ItemTypeRegistry.getItemType("warranty"), product);
		}
		if(tech == null){
			tech = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), product);
		}
		if(video == null){
			video = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), product);
		}
		if(pack == null){
			pack = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), product);
		}

		String months = warrantyParams.get("warranty");
		String serviceCenter = warrantyParams.get("service_center");
		warranty.setValueUI(ELEMENT_PARAM_DICTIONARY.get("service_center"), serviceCenter);
		warranty.setValueUI(ELEMENT_PARAM_DICTIONARY.get("warranty"), months);
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(warranty).noFulltextIndex().ignoreFileErrors());

		tech.setValueUI(ItemNames.product_extra_.NAME, ELEMENT_PARAM_DICTIONARY.get("tech"));
		String text = extraPageParams.get("tech").trim();
		tech.setValueUI(ItemNames.product_extra_.TEXT, text);
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(tech).noFulltextIndex().ignoreFileErrors());

		pack.setValueUI(ItemNames.product_extra_.NAME, ELEMENT_PARAM_DICTIONARY.get("package"));
		text = extraPageParams.get("package").trim();
		pack.setValueUI(ItemNames.product_extra_.TEXT, text);
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(pack).noFulltextIndex().ignoreFileErrors());

		video.setValueUI(ItemNames.product_extra_.NAME, ELEMENT_PARAM_DICTIONARY.get("video"));
		text = extraPageParams.get("video").trim();
		video.setValueUI(ItemNames.product_extra_.TEXT, text);
		DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(video).noFulltextIndex().ignoreFileErrors());
	}

	private void addWarrantyAndPagesQuick(Item product) throws Exception {
		if(StringUtils.isNotBlank(warrantyParams.get("warranty"))){
			Item warranty = Item.newChildItem(ItemTypeRegistry.getItemType("warranty"), product);
			String months = warrantyParams.get("warranty");
			String serviceCenter = warrantyParams.get("service_center");
			warranty.setValueUI(ELEMENT_PARAM_DICTIONARY.get("service_center"), serviceCenter);
			warranty.setValueUI(ELEMENT_PARAM_DICTIONARY.get("warranty"), months);
			DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(warranty).noFulltextIndex().ignoreFileErrors());
		}
		if(StringUtils.isNotBlank(extraPageParams.get("tech"))){
			Item tech = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), product);
			tech.setValueUI(ItemNames.product_extra_.NAME, ELEMENT_PARAM_DICTIONARY.get("tech"));
			String text = extraPageParams.get("tech").trim();
			tech.setValueUI(ItemNames.product_extra_.TEXT, text);
			DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(tech).noFulltextIndex().ignoreFileErrors());
		}
		if(StringUtils.isNotBlank(extraPageParams.get("package"))){
			Item tech = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), product);
			tech.setValueUI(ItemNames.product_extra_.NAME, ELEMENT_PARAM_DICTIONARY.get("package"));
			String text = extraPageParams.get("package").trim();
			tech.setValueUI(ItemNames.product_extra_.TEXT, text);
			DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(tech).noFulltextIndex().ignoreFileErrors());
		}
		if(StringUtils.isNotBlank(extraPageParams.get("video"))){
			Item tech = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), product);
			tech.setValueUI(ItemNames.product_extra_.NAME, ELEMENT_PARAM_DICTIONARY.get("video"));
			String text = extraPageParams.get("video").trim();
			tech.setValueUI(ItemNames.product_extra_.TEXT, text);
			DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(tech).noFulltextIndex().ignoreFileErrors());
		}
	}

	private boolean addPics(Item product) throws MalformedURLException {
		//if(picCounter > 49) return false;
		Set<String> picUrls = multipleParams.get(PICTURE_ELEMENT);
		if(picUrls == null){
			info.addLog("No pics for ["+product.getValue(CODE_PARAM)+"]", String.valueOf(productStartLineNumber));
			return false;
		}
		boolean needSave = false;
		ArrayList<File> galleryPics = product.getFileValues(GALLERY_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
		for (File galleryPic : galleryPics) {
			if (!galleryPic.isFile()) {
				product.removeEqualValue(GALLERY_PARAM, galleryPic.getName());
			}
		}
		if(picUrls.size() > 1){
			int i = 0;
			for (String picUrl : picUrls) {
				i++;
				if(i == 1) continue;
				try {
					String fileName = Strings.getFileName(picUrl);
					if (!product.containsValue(GALLERY_PARAM, fileName) && !product.containsValue(GALLERY_PARAM, GALLERY_PARAM + "_" + fileName)) {
						product.setValue(GALLERY_PARAM, new URL(picUrl));
						needSave = true;
					}
				} catch (Exception e) {
					info.addError("Неверный формат картинки: " + picUrl, picUrl);
				}

			}
		}

		// Генерация маленького изображения
		boolean noMainPic = product.isValueEmpty(MAIN_PIC_PARAM);
		if (!noMainPic) {
			File mainPic = product.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
			if (!mainPic.isFile()) {
				product.clearValue(MAIN_PIC_PARAM);
				noMainPic = true;
			}
		}
		boolean isSaved = false;
		if (needSave && picUrls.size() > 0) {
			if (noMainPic && picUrls.size() > 0) {
				product.setValue(MAIN_PIC_PARAM, new URL(picUrls.iterator().next()));
			}
			try {
				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors());
				DelayedTransaction.executeSingle(initiator, new ResizeImagesFactory.ResizeImages(product));
				DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(product).noFulltextIndex());
				isSaved = true;
			} catch (Exception e) {
				info.addError("Some error while saving files", product.getStringValue(NAME_PARAM));
			}
		}

		//picCounter++;
		return isSaved;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		parameterReady = false;
		paramValue = new StringBuilder();
		// Продукт
		if (StringUtils.equalsIgnoreCase(qName, OFFER_ELEMENT)) {
			productStartLineNumber = locator.getLineNumber();
			singleParams = new HashMap<>();
			multipleParams = new LinkedHashMap<>();
			extraPageParams = new LinkedHashMap<>();
			warrantyParams = new LinkedHashMap<>();
			singleParams.put(ID_ATTR, attributes.getValue(ID_ATTR));
			singleParams.put(AVAILABLE_ATTR, attributes.getValue(AVAILABLE_ATTR));
			isInsideOffer = true;
		}
		// Параметры продуктов (общие)
		else if (isInsideOffer && (SINGLE_ELEMENTS.contains(qName) || MULTIPLE_ELEMENTS.contains(qName)) || EXTRA_PAGE_ELEMENTS.contains(qName) || WARRANTY_ELEMENTS.contains(qName)) {
			paramName = qName;
			parameterReady = true;
		}
		// Пользовательские параметры продуктов
		else if (isInsideOffer && StringUtils.equalsIgnoreCase(PARAM_ELEMENT, qName)) {
			paramName = attributes.getValue(NAME_ATTR);
			parameterReady = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(parameterReady)
			paramValue.append(ch, start, length);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}
}
