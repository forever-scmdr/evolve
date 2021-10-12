package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.ExcelTableData;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateExcelPriceList;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class InterPartnerExcelImport extends CreateParametersAndFiltersCommand implements CatalogConst {
	private HashMap<Item, File> files = new HashMap<>();
	private Item currentStore;
	private Item catalog;
	private Item stores;
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private boolean newItemTypes = false;
	private long date;
	private HashSet<Long> sectionsWithNewItemTypes = new HashSet<>();
	//private HashSet<Long> memoryKillerAndDisgrace = new HashSet<>();

	private static HashMap<String, String> HEADER_PARAM = new HashMap() {{
		put(CreateExcelPriceList.CODE_FILE.toLowerCase(), CODE_PARAM);
		put(CreateExcelPriceList.NAME_FILE.toLowerCase(), NAME_PARAM);
		put(CreateExcelPriceList.PRICE_FILE.toLowerCase(), PRICE_PARAM);
		put(CreateExcelPriceList.PRICE_OLD_FILE.toLowerCase(), PRICE_OLD_PARAM);
		put(CreateExcelPriceList.PRICE_ORIGINAL_FILE.toLowerCase(), PRICE_ORIGINAL_PARAM);
		put(CreateExcelPriceList.CURRENCY_ID_FILE.toLowerCase(), CURRENCY_ID_PARAM);
		put(CreateExcelPriceList.QTY_FILE.toLowerCase(), QTY_PARAM);
		put(CreateExcelPriceList.AVAILABLE_FILE.toLowerCase(), AVAILABLE_PARAM);

	}};


	private boolean replacePriceAnyway = true;

	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		stores = ItemQuery.loadSingleItemByName("stores");
		List<Item> stores = new ItemQuery("store").loadItems();
		if (stores.size() == 0) {
			addLog("Склады не созданы");
			return false;
		}
		for (Item store : stores) {
			//String fileName = store.getStringValue("big_integration");
			//if (!StringUtils.isBlank(fileName)) {
			//	File f = Paths.get(AppContext.getContextPath(), "upload", fileName).toFile();
				File f = store.getFileValue("big_integration", AppContext.getFilesDirPath(store.isFileProtected()));
				if (f.isFile()) {
					files.put(store, f);
				} else {
					pushLog("Отсутствует файл: " + f.getName());
				}
			//}
		}
		//Init HEADER_PARAM
		for (ParameterDescription param : ItemTypeRegistry.getItemType(PRODUCT_ITEM).getParameterList()) {
			if (HEADER_PARAM.containsValue(param.getName())) continue;
			HEADER_PARAM.put(param.getCaption().toLowerCase(), param.getName());
		}
		return true;
	}

	private boolean checkHash(File file) throws Exception {
		int currentHash = file.hashCode();
		int oldHash = currentStore.getIntValue("old_file_hash", 0);
		//Если залили по FTP
		int currentStoreHash = currentStore.getIntValue("file_hash", 0);
		if (currentStoreHash != currentHash) {
			currentStore.setValue("file_hash", currentHash);
			currentStore.setValue("big_integration", file);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(currentStore).noTriggerExtra().ignoreUser().noFulltextIndex());
		}
		return currentHash != oldHash;
	}

	@Override
	protected void integrate() throws Exception {
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 1);
		fixGallery();
		revealProducts();
		//date = stores.getLongValue("date");
		date = new Date().getTime();
		for (Map.Entry<Item, File> entry : files.entrySet()) {
			File f = entry.getValue();
			currentStore = entry.getKey();
			//if (checkHash(f)) {
				//replacePriceAnyway =
				//if (dateDiffers(f)) {
				//	stores.setValue("date", date);
				//	executeAndCommitCommandUnits(SaveItemDBUnit.get(stores));
				//}
				parseExcel(f);
				//if(replacePriceAnyway) replacePriceAnyway = false;
			//} else {
			//	info.setCurrentJob("");
			//	pushLog("Файл " + f.getName() + " был разобран ранее.");
			//}
		}
		stores.setValue("date", date);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(stores));

		hideProducts(ItemNames.PRODUCT);
		hideProducts(ItemNames.LINE_PRODUCT);

		setPricesForLineProductParents();

		info.setCurrentJob("");
		createFiltersAndItemTypes();
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 0);
		//indexation
		info.setOperation("Индексация названий товаров");
		LuceneIndexMapper.getSingleton().reindexAll();
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
		setOperation("Интеграция завершена");
	}

	private void setPricesForLineProductParents() throws Exception {
		setOperation("переназначене цен множественных товаров");
		ItemQuery q = new ItemQuery(PRODUCT_ITEM);
		q.addParameterCriteria(HAS_LINE_PRODUCTS, "1", "=", null, Compare.SOME);
		int page = 1;
		q.setLimit(500, page);
		List<Item> products;
		info.setProcessed(0);
		while ((products = q.loadItems()).size() > 0){
			for(Item product : products){
				info.setCurrentJob(product.getStringValue(CODE_PARAM));
				ItemQuery lineProductQuery = new ItemQuery(LINE_PRODUCT_ITEM);
				lineProductQuery.setParentId(product.getId(), false);
				BigDecimal lowestPrice = new BigDecimal(Long.MAX_VALUE);
				lowestPrice.setScale(2);
				for(Item lineProduct : lineProductQuery.loadItems()){
					lowestPrice = lowestPrice.min(lineProduct.getDecimalValue(PRICE_PARAM, lowestPrice));
				}
				if(lowestPrice.compareTo(new BigDecimal(Long.MAX_VALUE)) < 0) {
					product.setValue(PRICE_PARAM, lowestPrice);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreFileErrors().ignoreUser().noFulltextIndex().noTriggerExtra());
				}
				info.increaseProcessed();
			}
			page++;
			q.setLimit(500, page);
		}
	}

	private void fixGallery() throws Exception {
		setOperation("Очитска галерей");
		info.setProcessed(0);
		List<Item> products = ItemMapper.loadByName(ItemNames.PRODUCT, 500, 0);
		long id = 0;
		while (products.size() > 0) {
			for (Item product : products) {
				if (isBadGallery(product)) {
					fixSingleProductGallery(product);
				}
				id = product.getId();
				info.increaseProcessed();
			}
			products = ItemMapper.loadByName(ItemNames.PRODUCT, 500, id);
		}
	}

	private void fixSingleProductGallery(Item product) throws Exception {
		ArrayList<String> outputValues = product.outputValues(GALLERY_PARAM);
		HashSet<String> distinct = new HashSet<>();
		distinct.addAll(outputValues);
		for (String v : distinct) {
			int counter = 0;
			for (String ex : outputValues) {
				if (ex.equals(v)) counter++;
				if (counter > 1) {
					product.removeEqualValue(GALLERY_PARAM, ex);
				}
			}
		}
		executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noTriggerExtra().ignoreFileErrors().ignoreUser().noFulltextIndex());
	}

	private boolean isBadGallery(Item product) throws Exception {
		ArrayList<String> outputValues = product.outputValues(GALLERY_PARAM);
		if (outputValues.size() > 1) {
			HashSet<String> distinct = new HashSet<>();
			distinct.addAll(outputValues);
			if (outputValues.size() != distinct.size()) return true;

			for(String v : outputValues){
				if(!StringUtils.startsWith(v,"gallery_")){
					product.removeEqualValue(GALLERY_PARAM, "gallery_" + v);
				}
			}
			int sz = outputValues.size();
			outputValues = product.outputValues(GALLERY_PARAM);
			if(outputValues.size() != sz){
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).noFulltextIndex().ignoreFileErrors().noTriggerExtra());
			}
		}
		return false;
	}

	private void hideProducts(String typeName) throws Exception {
		setOperation("Скрытие товаров, отсутствующих на складах");
		info.setProcessed(0);
		//List<Item> products = ItemMapper.loadByName(typeName, 500, 0);
		//long id = 0;
		//while (products.size() > 0) {
			for (Item product : new ItemQuery(typeName).loadItems()) {
				long id = product.getId();
				info.setCurrentJob("hiding " + product.getTypeName() + " id: " + product.getTypeId() + ". trg = 1022");
				if (product.getTimeUpdated() < date && product.getStatus() == Item.STATUS_NORMAL) {
					executeAndCommitCommandUnits(ItemStatusDBUnit.hide(id));
				}
				info.increaseProcessed();
			}
		//	products = ItemMapper.loadByName(ItemNames.PRODUCT, 500, id);
		//}

		info.setOperation("Пересохранение завершено");
	}

	private void revealProducts() throws Exception {
		setOperation("Восстановление скрытых товаров");
		info.setProcessed(0);
		List<Item> products = new ItemQuery(PRODUCT_ITEM, Item.STATUS_HIDDEN).loadItems();
		products.addAll(new ItemQuery(LINE_PRODUCT_ITEM, Item.STATUS_HIDDEN).loadItems());
		info.setToProcess(products.size());
		for (Item product : products) {
			executeAndCommitCommandUnits(ItemStatusDBUnit.restore(product.getId()));
			info.increaseProcessed();
		}
		info.setOperation("Пересохранение завершено");
	}

	private void createFiltersAndItemTypes() throws Exception {
		if (sectionsWithNewItemTypes.size() == 0) return;
		setOperation("Создание классов и фильтров");
		List<Item> sections = ItemQuery.loadByIdsLong(sectionsWithNewItemTypes);
		doCreate(sections);
	}

	private void parseExcel(File excelFile) throws Exception {
		setOperation(String.format("Разбор файла %s. Склад: %s", excelFile.getName(), currentStore.getStringValue(NAME_PARAM)));
		info.setToProcess(0);
		setProcessed(0);
		InterpartnerDocument doc = new InterpartnerDocument(excelFile, CreateExcelPriceList.CODE_FILE, CreateExcelPriceList.NAME_FILE, CreateExcelPriceList.PRICE_FILE, CreateExcelPriceList.QTY_FILE, CreateExcelPriceList.AVAILABLE_FILE);

		doc.iterate();
		doc.close();

		pushLog("Файл " + excelFile.getName() + " разобран.");
	}

	@Override
	protected void terminate() throws Exception {

	}

	//Excel document implementation
	private class InterpartnerDocument extends ExcelPriceList {
		private HashSet<String> duplicateCodes = new HashSet<>();
		private Item currentSection;
		private Item currentProduct;
		private int storeNumber;
		final ItemType LINE_PRODUCT_ITEM_TYPE = ItemTypeRegistry.getItemType(LINE_PRODUCT_ITEM);
		final ItemType paramsXMLItemType = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
		final ItemType PRODUCT_ITEM_TYPE = ItemTypeRegistry.getItemType(PRODUCT_ITEM);

		public InterpartnerDocument(File file, String... mandatoryCols) {
			super(file, mandatoryCols);
			storeNumber = currentStore.getIntValue("number");
		}

		@Override
		protected void processRow() throws Exception {
			info.setLineNumber(getRowNum() + 1);
			String code = getValue(CreateExcelPriceList.CODE_FILE);

			if (StringUtils.isBlank(code)) {
				addError("Отсутствует код товара: " + getValue(CreateExcelPriceList.NAME_FILE) + ". Лист: " + getSheetName(), "");
				return;
			}
			//check duplicate codes
			if (duplicateCodes.contains(code) && !CreateExcelPriceList.CODE_FILE.equalsIgnoreCase(code)) {
				info.addError("Файл: " + getFileName() + ".Повторяющийся артикул: " + code, "");
			} else if (!CreateExcelPriceList.CODE_FILE.equalsIgnoreCase(code)) {
				duplicateCodes.add(code);
			}

			//section
			if (StringUtils.startsWith(code, "разд:")) {
				code = StringUtils.substringAfter(code, "разд:").trim();
				int codeIndex = getColIndex(CreateExcelPriceList.CODE_FILE);
				String parentCode = getValue(codeIndex + 1);
				String name = getValue(codeIndex + 2);
				String[] secInfo = new String[]{name, code, parentCode};
				currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, code);
				if (currentSection == null) {
					String sectionName = secInfo[0].trim();
					String sectionParentId = (secInfo.length == 3) ? secInfo[2].trim() : "";
					Item parent = (StringUtils.isBlank(sectionParentId)) ? catalog : ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, sectionParentId);
					parent = (parent == null) ? catalog : parent;
					currentSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), parent);
					currentSection.setValue(NAME_PARAM, sectionName);
					currentSection.setValue(CATEGORY_ID_PARAM, secInfo[1].trim());
					currentSection.setValue(PARENT_ID_PARAM, sectionParentId);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noTriggerExtra().noFulltextIndex());
					newItemTypes = true;
					sectionsWithNewItemTypes.add(currentSection.getId());
					info.pushLog("Создан раздел: " + sectionName);
				} else if (!currentSection.getStringValue(NAME_PARAM, "").equals(name)) {
					currentSection.setValue(NAME_PARAM, name);
					info.addLog("Обновлено название раздела " + currentSection.getStringValue(CODE_PARAM) + ": \"" + currentSection.getStringValue(NAME_PARAM, "") + "\"");
					executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noFulltextIndex().ignoreFileErrors(false).noTriggerExtra());
				}
			}
			//parameters
			else if (code.equals(CreateExcelPriceList.CODE_FILE)) {
				reInit(CreateExcelPriceList.CODE_FILE, CreateExcelPriceList.NAME_FILE, CreateExcelPriceList.PRICE_FILE, CreateExcelPriceList.QTY_FILE, CreateExcelPriceList.AVAILABLE_FILE);
			}
			//product
			else {
				LinkedHashSet<String> headers = getHeaders();
				boolean isProduct = "+".equals(getValue(CreateExcelPriceList.IS_DEVICE_FILE));
				Item product = getExistingProduct(code, isProduct);
				ItemType itemType = (isProduct) ? PRODUCT_ITEM_TYPE : LINE_PRODUCT_ITEM_TYPE;
				if (catalog.equals(product)) return;
				if (product == null) {
					Item parent = (isProduct) ? currentSection : currentProduct;
					product = Item.newChildItem(itemType, parent);
				}

				//fix
				replacePriceAnyway = product.getLongValue(DATE_PARAM, 0L) < date;
				product.setValue(DATE_PARAM, date);
				if(replacePriceAnyway){
					clearQtys(product);
				}
				for (String header : headers) {
					String paramName = HEADER_PARAM.get(header);
					if (!itemType.getParameterNames().contains(paramName) || CreateExcelPriceList.MANUAL.equalsIgnoreCase(header))
						continue;
					String cellValue = getValue(header);
					cellValue = StringUtils.isAllBlank(cellValue) ? "" : cellValue;
					if (CODE_PARAM.equals(paramName)) {
						product.setValue(CODE_PARAM, code);
						product.setValue(VENDOR_CODE_PARAM, code);
						product.setValue(OFFER_ID_PARAM, code);//x

					} else if (MAIN_PIC_PARAM.equals(paramName) && StringUtils.isNotBlank(cellValue)) {
						setPicture(cellValue, paramName, product);
					} else if (GALLERY_PARAM.equalsIgnoreCase(paramName) || TEXT_PICS_PARAM.equalsIgnoreCase(paramName)) {
						if (StringUtils.isBlank(cellValue)) continue;
						String[] arr = cellValue.split(CreateExcelPriceList.VALUE_SEPARATOR);
						LinkedHashSet<String> distinctList = new LinkedHashSet<>();
						distinctList.addAll(Arrays.asList(arr));
						for (String s : distinctList) {
							setPicture(s.trim(), paramName, product);
						}
					} else if (QTY_PARAM.equalsIgnoreCase(paramName)) {
						double currentQty = replacePriceAnyway ? 0d : product.getDoubleValue(QTY_PARAM, 0d);
						double q = StringUtils.isBlank(cellValue) ? 0d : DoubleDataType.parse(cellValue) == null ? 0d : DoubleDataType.parse(cellValue);
						product.setValue(QTY_PARAM, currentQty + q);
						String additionParamName = "qty_" + storeNumber;
						product.setValue(additionParamName, q);
						product.setValue(AVAILABLE_PARAM, currentQty > 0? (byte) 1 : (byte)0);
					} else if (PRICE_PARAM.equalsIgnoreCase(paramName)) {
						//561429
						BigDecimal currentPrice = replacePriceAnyway ? BigDecimal.ZERO : product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO);
						BigDecimal price = StringUtils.isBlank(cellValue) ? BigDecimal.ZERO : DecimalDataType.parse(cellValue, 2);
						String additionParamName = "price_" + storeNumber;
						product.setValue(additionParamName, price);
						price = price.max(currentPrice);
						product.setValue(PRICE_PARAM, price);
					} else {
						if (StringUtils.isBlank(cellValue)) continue;
						ParameterDescription pd = itemType.getParameter(paramName);
						if (pd.isMultiple()) {
							String[] values = cellValue.split(CreateExcelPriceList.VALUE_SEPARATOR);
							for (String val : values) {
								product.setValueUI(paramName, val);
							}

						} else {
							product.setValueUI(paramName, cellValue);
						}
					}
				}
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreUser(true).ignoreFileErrors(true).noFulltextIndex());

				//very dumb fix
				if (isProduct) currentProduct = product;
				//AUX
				if (hasAuxParams(headers)) {
					String auxTypeString = getValue(CreateExcelPriceList.AUX_TYPE_FILE.toLowerCase());
					ItemType auxType = null;
					List<Item>paramsXMLs = new ItemQuery(paramsXMLItemType).setParentId(product.getId(), false).loadItems();
					Item paramsXML = paramsXMLs.size() > 0? paramsXMLs.remove(0) : Item.newChildItem(paramsXMLItemType, product);
					for(Item itm : paramsXMLs){
						executeAndCommitCommandUnits(ItemStatusDBUnit.delete(itm).ignoreUser(true).noFulltextIndex());
					}
					HashMap<String, String> auxParams = new HashMap<>();
					Item aux = null;
					if (StringUtils.isNotBlank(auxTypeString)) {
						auxType = ItemTypeRegistry.getItemType(Integer.parseInt(auxTypeString));
					} else {
						auxType = ItemTypeRegistry.getItemType("p" + currentSection.getStringValue(CATEGORY_ID_PARAM,""));
						auxType = auxType == null?  ItemTypeRegistry.getItemType("p" + currentSection.getId()) : auxType;
					}
					if (auxType != null) {
						List<Item>auxes = new ItemQuery("params").setParentId(product.getId(), false).loadItems();
						aux = auxes.size() == 1? auxes.remove(0) : Item.newChildItem(auxType, product);

						if(auxes.size() > 1 || !aux.getItemType().equals(auxType)){
							aux = Item.newChildItem(auxType, product);
							for(Item a : auxes){
								executeAndCommitCommandUnits(ItemStatusDBUnit.delete(a.getId()).ignoreUser(true).noFulltextIndex());
							}
						}

						for (ParameterDescription pd : auxType.getParameterList()) {
							auxParams.put(pd.getCaption().toLowerCase(), pd.getName());
						}
					} else {
						newItemTypes = true;
						sectionsWithNewItemTypes.add(currentSection.getId());
					}

					XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
					for (String header : headers) {
						String paramName = HEADER_PARAM.get(header);
						if (PRODUCT_ITEM_TYPE.getParameterNames().contains(paramName) || CreateExcelPriceList.AUX_TYPE_FILE.equalsIgnoreCase(header) || CreateExcelPriceList.MANUAL.equalsIgnoreCase(header) || CreateExcelPriceList.IS_DEVICE_FILE.equalsIgnoreCase(header))
							continue;
						String cellValue = getValue(header);
						cellValue = StringUtils.isAllBlank(cellValue) ? "" : cellValue;
						xml.startElement("parameter")
								.startElement("name")
								.addText(firstUpperCase(header.replace("" + ExcelTableData.PREFIX, "")))
								.endElement()
								.startElement("value")
								.addText(cellValue)
								.endElement()
								.endElement();

						if (auxType == null) continue;
						String param = auxParams.get(header.toLowerCase().replaceAll("\\s+"," ").trim());
						//Если добавился новый параметр
						if (!auxType.getParameterNames().contains(param)) {
							newItemTypes = true;
							sectionsWithNewItemTypes.add(currentSection.getId());
							continue;
						}

						if (auxType.getParameterNames().contains(param)){
							DataType.Type paramType = auxType.getParameter(param).getDataType().getType();
							if(paramType == DataType.Type.CURRENCY || paramType == DataType.Type.CURRENCY_PRECISE || paramType == DataType.Type.DECIMAL || paramType == DataType.Type.DOUBLE){
								String clear = cellValue.replaceAll("[^0-9\\.,]", "");
								aux.setValueUI(param, clear);
							}
							else {
								aux.setValueUI(param, cellValue);
							}
						}
					}

					paramsXML.setValue(XML_PARAM, xml.toString());
					executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXML).noFulltextIndex());

					if (auxType != null) {
						executeAndCommitCommandUnits(SaveItemDBUnit.get(aux).noFulltextIndex().noTriggerExtra());
					}
				}
				info.increaseProcessed();
			}
		}

		private void clearQtys(Item product){
			product.clearValue(QTY_PARAM);
			product.clearValue(AVAILABLE_PARAM);
			for(String pn : product.getItemType().getParameterNames()){
				if(pn.matches(QTY_PARAM+"_\\d+")){
					product.clearValue(pn);
				}
			}
		}

		private void setPicture(String cellValue, String paramName, Item product) throws MalformedURLException {
			if (StringUtils.isBlank(cellValue)) return;
			Path picsFolder = Paths.get(AppContext.getContextPath()).resolve("");
			cellValue = cellValue.replace(getUrlBase(), "").trim();
			if (cellValue.matches("^(https?|ftp)://.*$")) {
				URL url = new URL(cellValue);
				String name = StringUtils.substringAfterLast(cellValue, "/");
				name = StringUtils.substringBefore(name, "?");
				ArrayList<String> existingValues = product.outputValues(paramName);
				String processedValue = Strings.createFileName(Strings.createFileName(name));
				if (!existingValues.contains(processedValue)) {
					product.setValue(paramName, url);
				}
			} else {
				try {
					cellValue = StringUtils.replaceChars(cellValue, '\\', System.getProperty("file.separator").charAt(0));
					Path mainPicPath = picsFolder.resolve(cellValue);
					if (mainPicPath.toFile().isFile()) {
						ArrayList<String> existingValues = product.outputValues(paramName);
						String processedValue = Strings.createFileName(mainPicPath.toFile().getName());
						if (!existingValues.contains(processedValue) && !existingValues.contains(paramName + "_" + processedValue)) {
							product.setValue(paramName, mainPicPath.toFile());
						}
					} else if (StringUtils.isNotBlank(cellValue)) {
						//pushLog("No file: " + mainPicPath.toAbsolutePath());
					}
				} catch (Exception e) {
					info.addError(e);
				}
			}
		}

		private Item getExistingProduct(String code, boolean isProduct) throws Exception {
			Item prod;
			if (isProduct) {
				prod = new ItemQuery(PRODUCT_ITEM).setParentId(currentSection.getId(), false).addParameterCriteria(CODE_PARAM, code, "=", null, Compare.SOME).loadFirstItem();
			} else {
				try {
					prod = new ItemQuery(LINE_PRODUCT_ITEM).setParentId(currentProduct.getId(), false).addParameterCriteria(CODE_PARAM, code, "=", null, Compare.SOME).loadFirstItem();
				} catch (NullPointerException e) {
					addError("Отсутствует родитель товара: " + code, "");
					return catalog;
				}

			}
			return prod;
		}

		private boolean hasAuxParams(Collection<String> headers) {
			if (!headers.contains(CreateExcelPriceList.AUX_TYPE_FILE.toLowerCase())) return false;
			for (String header : headers) {
				String paramName = HEADER_PARAM.get(header);
				if (PRODUCT_ITEM_TYPE.getParameterNames().contains(paramName) || CreateExcelPriceList.AUX_TYPE_FILE.equalsIgnoreCase(header) || CreateExcelPriceList.MANUAL.equalsIgnoreCase(header) || CreateExcelPriceList.IS_DEVICE_FILE.equalsIgnoreCase(header))
					continue;
				return true;
			}
			return false;
		}

		@Override
		protected void processSheet() throws Exception {
			currentSection = null;
			currentProduct = null;
			info.setCurrentJob(getSheetName());
		}
	}

	private String firstUpperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	public static void main(String[] args){
		try {
			BufferedImage img1 = ImageIO.read(Paths.get("J:/Dopbox 3/pic1.png").toFile());
			BufferedImage img2 = ImageIO.read(Paths.get("J:/Dopbox 3/pic2.png").toFile());
			System.out.println(isSamePicture(img1, img2));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean isSamePicture(BufferedImage img1, BufferedImage img2){
		long startTime = System.nanoTime();
		if(img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()){
			System.out.println(String.format("elapsed: %tN",  System.nanoTime() - startTime));
			return false;
		}
		for(int y = 0; y< img1.getHeight(); y++){
			for(int x = 0; x < img1.getWidth(); x++){
				int color1 = img1.getRGB(x,y);
				int color2 = img2.getRGB(x,y);
				if(color1 != color2){
					return false;
				}
			}
		}
		System.out.println(String.format("elapsed: %tN",  System.nanoTime() - startTime));
		return true;
	}
}
