package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anton 11.01.2021
 */
public class ImportProductsFromExcel extends CreateParametersAndFiltersCommand {
	//settings via page variables
	//values
	private enum varValues {
		UPDATE, COPY, CREATE, COPY_IF_PARENT_DIFFERS, MOVE_IF_PARENT_DIFFERS, DELETE, IGNORE, CLEAR, SEARCH_BY_CODE,
		UPDATE_IF_DIFFER, SEARCH_BY_CELL_VALUE, DOWNLOAD, SEARCH_BY_MAIN_PIC;
	}

	//keys
	private static final String WITH_EXISTING_SECS_VAR = "with_existing_sections"; // what to do with existing sections (UPDATE, COPY, CREATE, COPY_IF_PARENT_DIFFERS, MOVE_IF_PARENT_DIFFERS, DELETE)
	private static final String WITH_EXISTING_PRODUCTS_VAR = "with_existing_products"; // what to do with existing products (UPDATE)
	private static final String IF_BLANK_VAR = "if_blank"; // what to do if the cell is blank; (IGNORE, CLEAR)
	private static final String WITH_FILES_VAR = "with_pics"; // where to look for product files (SEARCH_BY_CODE, SEARCH_BY_CELL_VALUE, DOWNLOAD)
	private static final String FILE_LOCATION_VAR = "file_location";
	private static final String PICS_FOLDER_VAR = "pics_folder";
	private static final String ON_MAIN = "выгодные предложения";

	//default settings
	private HashMap<String, varValues> settings = new HashMap<String, varValues>() {{
		put(WITH_EXISTING_PRODUCTS_VAR, varValues.UPDATE);
		put(WITH_EXISTING_SECS_VAR, varValues.UPDATE);
		put(IF_BLANK_VAR, varValues.IGNORE);
		put(WITH_FILES_VAR, varValues.SEARCH_BY_CELL_VALUE);
	}};
	private static final String DEFAULT_FILE_LOCATION = "upload";
	private static final String DEFAULT_PICS_FOLDER = "";

	//vars
	Collection<File> files;
	ExcelPriceList priceWorkbook;
	Item catalog;

	//memory
	private LinkedHashMap<String, ArrayList<String>> paramGroups;
	private HashSet<Long> sectionsWithNewItemTypes = new HashSet<>();
	private HashSet<String> duplicateCodes = new HashSet<>();

	//Excel file columns
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

	private static final ItemType PARAMS_XML_ITEM_TYPE = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
	private static final ItemType PRODUCT_ITEM_TYPE = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
	private static final ItemType LINE_PRODUCT_ITEM_TYPE = ItemTypeRegistry.getItemType(LINE_PRODUCT_ITEM);
	private static final Pattern PARAM_WITH_GROUP = Pattern.compile("\\[(?<group>.*)\\]\\s?(?<param>.*)");

	@Override
	public boolean makePreparations() throws Exception {
		//init settings
		initSettings();

		//load integration file
		files = loadExcelFiles();
		//TODO возможность разбора множества файлов
		if (files.size() > 1) {
			addError("Найдено больше одного файла Excel", "поиск файлов интеграции");
			return false;
		}
		if (files.size() < 1 || !files.iterator().next().isFile()) {
			addError("Файл интеграции не найден", "поиск файлов интеграции");
			return false;
		}
		//load catalog
		//catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), User.NO_GROUP_ID, User.ANONYMOUS_ID);
		catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);

		//load common product parameters
		for (ParameterDescription param : PRODUCT_ITEM_TYPE.getParameterList()) {
			if (HEADER_PARAM.containsValue(param.getName())) continue;
			HEADER_PARAM.put(param.getCaption().toLowerCase(), param.getName());
		}
		return true;
	}

	private class IntegrationExcelDocument extends ExcelPriceList {
		boolean hasAuxParams = false;
		Item currentSection;
		Item currentProduct;
		ItemType currentAuxItemType;
		Path picsFolder;
		private Item homepage;


		@Override
		protected void processRow() throws Exception {
			info.setLineNumber(getRowNum() + 1);
			String code = getValue(CreateExcelPriceList.CODE_FILE);
			if (StringUtils.isBlank(code)) return;
			checkDuplicateCodes(code);

			//Section
			if (StringUtils.startsWith(code, "разд:")) {
				hasAuxParams = false;
				int codeIndex = getColIndex(CreateExcelPriceList.CODE_FILE);
				code = StringUtils.substringAfter(code, "разд:").trim();
				String parentCode = getValue(codeIndex + 1);
				String name = getValue(codeIndex + 2);
				String hide = getValue(codeIndex + 3);
				initSection(code, name, parentCode, hide);
			}
			//headers
			else if (code.equals(CreateExcelPriceList.CODE_FILE)) {
				reInit(CreateExcelPriceList.CODE_FILE, CreateExcelPriceList.NAME_FILE, CreateExcelPriceList.PRICE_FILE);
				hasAuxParams = hasAuxParams();
				if (hasAuxParams) {
					paramGroups = buildGroupMap();
					Item paramsXml = new ItemQuery(PARAMS_XML_ITEM_TYPE).setParentId(currentSection.getId(), false).loadFirstItem();
					paramsXml = paramsXml == null ? Item.newChildItem(ItemTypeRegistry.getItemType(PARAMS_XML_ITEM), currentSection) : paramsXml;
					paramsXml.setValue(XML_PARAM, createEtalonXmlFromMap());
					executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXml).noTriggerExtra().ignoreFileErrors().noFulltextIndex().ignoreUser(true));
					loadCurrentAuxItemType();
				}
			}
			//product
			else {
				boolean isProduct = "+".equals(getValue(CreateExcelPriceList.IS_DEVICE_FILE));
				Item product = ensureProduct(code, isProduct);
				populateProduct(product);
				postProcessProduct(product);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreFileErrors(true).noFulltextIndex().ignoreUser());

				String ass = getValue(ON_MAIN);
				//if (StringUtils.isNotBlankge(ass = tValue(ON_MAIN))) {

					if ("1".equals(ass)) {
						executeAndCommitCommandUnits(CreateAssocDBUnit.childExistsSoft(product, homepage.getId(), ItemTypeRegistry.getAssoc("general").getId()));
					} else {
						executeAndCommitCommandUnits(new DeleteAssocDBUnit(product, homepage.getId(), ItemTypeRegistry.getAssoc("general").getId()));
					}
				//}

				if (hasAuxParams) {
					processAuxType(product);
				}
				createExtraPages(product);
				//createManuals(product);
			}
		}

		private void createExtraPages(Item product) throws Exception {
			ItemQuery extraPagesQuery = new ItemQuery("product_extra");
			extraPagesQuery.setParentId(product.getId(), false, ItemTypeRegistry.getPrimaryAssoc().getName());
			for (Item tab : extraPagesQuery.loadItems()) {
				executeCommandUnit(ItemStatusDBUnit.delete(tab.getId()).noFulltextIndex());
			}
			commitCommandUnits();
			int extraCount = 0;
			try {
				extraCount = Integer.parseInt(getValue(CreateExcelPriceList.EXTRA_COLS));
			} catch (Exception e) {
			}
			if (extraCount == 0) return;
			for (int i = getHeaders().size(); i < getHeaders().size() + extraCount; i++) {
				String cellValue = getValue(i);
				if (StringUtils.isBlank(cellValue)) continue;
				cellValue = cellValue.trim();

				String name = StringUtils.substringBetween(cellValue, "<h>", "</h>");
				String text = StringUtils.substringAfter(cellValue, "</h>");
				Item page = Item.newChildItem(ItemTypeRegistry.getItemType("product_extra"), product);
				page.setValue(NAME_PARAM, name);
				page.setValue(TEXT_PARAM, text);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(page).ignoreUser(true).noFulltextIndex().ignoreUser());
			}
		}

		private void processAuxType(Item product) throws Exception {
			Item paramsXML = ensureParamsXML(product);
			Item aux = ensureAuxItem(product);
			String xml = createXmlFromMap();
			paramsXML.setValue(XML_PARAM, xml);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXML).noFulltextIndex().ignoreUser());
			if (!hasNewParams() && aux != null) {
				if(populateAuxItem(aux)) {
					executeAndCommitCommandUnits(SaveItemDBUnit.get(aux).noFulltextIndex().ignoreUser());
				}else {
					sectionsWithNewItemTypes.add(currentSection.getId());
				}
			} else {
				sectionsWithNewItemTypes.add(currentSection.getId());
			}
		}

		private boolean populateAuxItem(Item aux) {
			HashMap<String, String> auxParams = new HashMap<>();
			for (ParameterDescription pd : currentAuxItemType.getParameterList()) {
				auxParams.put(pd.getCaption().toLowerCase(), pd.getName());
			}
			AtomicBoolean paramTypeAndDescriptionOk = new AtomicBoolean(true);
			paramGroups.forEach((k, v) -> {
				for (String header : v) {
					String cellValue = getValue(header);
					cellValue = StringUtils.isAllBlank(cellValue) ? "" : cellValue;
					Matcher m = PARAM_WITH_GROUP.matcher(header);
					String paramCaption = m.matches() ? m.group("param").trim().toLowerCase() : header.toLowerCase();
					String paramName = auxParams.get(paramCaption);
					ParameterDescription pd =  currentAuxItemType.getParameter(paramName);
					DataType.Type paramType = pd.getDataType().getType();
					String descriptionAttribute = pd.getDescription();
					try {
 						if(checkParamType(aux, paramName, cellValue)) {
 							if(paramType != DataType.Type.STRING) {
 								cellValue = cellValue.replace(descriptionAttribute, "").trim();
							}
							aux.setValueUI(paramName, cellValue);
						}else{
							paramTypeAndDescriptionOk.set(false);
 							return;
						}
					} catch (Exception e) {
						info.addError(e);
					}
				}
			});
			return paramTypeAndDescriptionOk.get();
		}

		/**
		 * Checks if parameter type or unit DID NOT change.
		 * Returns false if parameter value type or it's unit does not match type or unit, specified int site model
		 */
		private boolean checkParamType(Item aux, String paramName, String cellValue){
			//if value is blank assume all is ok
			if(StringUtils.isBlank(cellValue)) return true;
			ItemType auxItemType = aux.getItemType();
			ParameterDescription parameterDescription = auxItemType.getParameter(paramName);
			DataType.Type paramType = parameterDescription.getDataType().getType();

			//if type is string any value will do
			if(paramType == DataType.Type.STRING) return true;

			//if type is integer check for ',', '.' or unit changes
			if(paramType == DataType.Type.INTEGER){
				String value = cellValue.replaceAll("\\s", "");
				String number = cellValue.replaceAll("[^0-9\\-]", "");
				String unit = StringUtils.substringAfter(value, number);
				try {
					Integer.parseInt(number);
				}catch (NumberFormatException e){
					return false;
				}
				if(!unit.equals(parameterDescription.getDescription())) return false;
			}
			//if type is double check for unit changes
			else if(paramType == DataType.Type.DOUBLE){
				String value = cellValue.replace(',', '.').replaceAll("\\s", "");
				String number = cellValue.replaceAll("[^0-9\\.\\-]", "");
				String unit = StringUtils.substringAfter(value, number);
				try {
					Double.parseDouble(number);
				}catch (NumberFormatException e){
					return false;
				}
				if(!unit.equals(parameterDescription.getDescription())) return false;
			}
			return true;
		}

		private boolean hasNewParams() {
			if (currentAuxItemType == null) {
				return true;
			}
			HashMap<String, String> auxParams = new HashMap<>();
			for (ParameterDescription pd : currentAuxItemType.getParameterList()) {
				auxParams.put(pd.getCaption().toLowerCase(), pd.getName());
			}
			for (Map.Entry<String, ArrayList<String>> e : paramGroups.entrySet()) {
				for (String s : e.getValue()) {
					Matcher m = PARAM_WITH_GROUP.matcher(s);
					String paramName = m.matches() ? m.group("param") : s;
					paramName = paramName.toLowerCase();
					if (!auxParams.containsKey(paramName)) {
						return true;
					}
				}
			}
			return false;
		}


		private LinkedHashMap<String, ArrayList<String>> buildGroupMap() {
			LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<>();
			for (String header : getHeaders()) {
				String paramName = HEADER_PARAM.get(header);
				if (
						PRODUCT_ITEM_TYPE.getParameterNames().contains(paramName)
								|| CreateExcelPriceList.AUX_TYPE_FILE.equalsIgnoreCase(header)
								|| CreateExcelPriceList.MANUAL.equalsIgnoreCase(header)
								|| CreateExcelPriceList.IS_DEVICE_FILE.equalsIgnoreCase(header)
								|| CreateExcelPriceList.EXTRA_COLS.equalsIgnoreCase(header)
								|| ON_MAIN.equalsIgnoreCase(header)
				)
					continue;
				String originalHeader = getOriginalHeader(header);
				Matcher m = PARAM_WITH_GROUP.matcher(originalHeader);
				boolean hasGroup = m.matches();
				String group = hasGroup ? m.group("group").trim() : "";
				boolean needAdd = map.get(group) == null;
				ArrayList<String> groupParams = needAdd ? new ArrayList<>() : map.get(group);
				if (needAdd) {
					map.put(group, groupParams);
				}
				groupParams.add(header);
			}
			return map;
		}

		private String createXmlFromMap() {
			XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
			paramGroups.forEach((k, v) -> {
				xml.startElement("group", "name", k);
				for (String header : v) {
					String cellValue = getValue(header);
					cellValue = StringUtils.isAllBlank(cellValue) ? "" : cellValue;
					Matcher m = PARAM_WITH_GROUP.matcher(header);
					String param = m.matches() ? m.group("param").trim() : header;

					xml.startElement("parameter")
							.startElement("name")
							.addText(firstUpperCase(param))
							.endElement()
							.startElement("value")
							.addText(cellValue)
							.endElement()
							.endElement();
				}
				xml.endElement();
			});
			return xml.toString();
		}

		private String createEtalonXmlFromMap() {
			XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
			paramGroups.forEach((k, v) -> {
				xml.startElement("group", "name", k);
				for (String header : v) {
					Matcher m = PARAM_WITH_GROUP.matcher(header);
					String param = m.matches() ? m.group("param").trim() : header;
					xml.startElement("parameter")
							.startElement("name")
							.addText(firstUpperCase(param))
							.endElement()
							.endElement();
				}
				xml.endElement();
			});
			return xml.toString();
		}

		private Item ensureAuxItem(Item product) throws Exception {
			if (currentAuxItemType != null) {
				Item aux = new ItemQuery(PARAMS_ITEM).setParentId(product.getId(), false).loadFirstItem();
				return (aux == null) ? Item.newChildItem(currentAuxItemType, product) : aux;
			}
			return null;
		}

		private Item ensureParamsXML(Item product) throws Exception {
			Item paramsXML = new ItemQuery(PARAMS_XML_ITEM_TYPE).setParentId(product.getId(), false).loadFirstItem();
			return (paramsXML == null) ? Item.newChildItem(PARAMS_XML_ITEM_TYPE, product) : paramsXML;
		}

		private void postProcessProduct(Item product) {
			String code = product.getStringValue(CODE_PARAM);
			product.setValue(OFFER_ID_PARAM, product.getStringValue(OFFER_ID_PARAM, code));
			product.setValue(VENDOR_CODE_PARAM, product.getStringValue(VENDOR_CODE_PARAM, code));
			if (settings.get(WITH_FILES_VAR) == varValues.SEARCH_BY_CODE) {
				Path mainPicPath = picsFolder.resolve(code + ".jpg");
				Path filesPath = picsFolder.resolve(code);
				File mainPic = mainPicPath.toFile();
				if (mainPic.exists()) product.setValue(MAIN_PIC_PARAM, mainPic);
				File additionalFiles = filesPath.toFile();
				if (additionalFiles.exists()) {
					for (File f : FileUtils.listFiles(filesPath.toFile(), null, false)) {
						if (f.getName().matches(".+(\\.(?i)(jpe?g|png|gif|bmp|svg))$")) {
							product.setValue(GALLERY_PARAM, f);
						} else if (f.isDirectory()) {
							for (File textPic : FileUtils.listFiles(f, null, false)) {
								product.setValue(TEXT_PICS_PARAM, textPic);
							}
						}
					}
				}
			}
		}

		private void populateProduct(Item product) throws Exception {
			for (String header : getHeaders()) {
				String paramName = HEADER_PARAM.get(header);
				if (!PRODUCT_ITEM_TYPE.getParameterNames().contains(paramName)) continue;

				String cellValue = getValue(header);

				varValues ifBlank = settings.get(IF_BLANK_VAR);
				if (StringUtils.isBlank(cellValue) && ifBlank == varValues.IGNORE) {
					continue;
				} else if (StringUtils.isBlank(cellValue) && ifBlank == varValues.CLEAR) {
					product.clearValue(paramName);
					if (paramName.equals(MAIN_PIC_PARAM)) {
						product.clearValue(SMALL_PIC_PARAM);
					}
					continue;
				}

				ParameterDescription parameterDescription = PRODUCT_ITEM_TYPE.getParameter(paramName);
				boolean isFile = parameterDescription.getType().isFile();
				String[] values = parameterDescription.isMultiple() ? cellValue.split(CreateExcelPriceList.VALUE_SEPARATOR) : new String[]{normalizeSpace(cellValue)};
				if (parameterDescription.isMultiple()) {
					product.clearValue(paramName);
				}
				if (isFile) {
					for (String v : values) {
						setFileParamValue(product, paramName, v.trim());
					}
				} else {
					for (String v : values) {
						product.setValueUI(paramName, v);
					}
				}
			}
		}

		private void setFileParamValue(Item product, String paramName, String value) throws MalformedURLException {
			varValues withPics = settings.get(WITH_FILES_VAR);
			if (withPics != varValues.SEARCH_BY_CELL_VALUE && !paramName.equals(MAIN_PIC_PARAM)) return;
			if (withPics == varValues.SEARCH_BY_MAIN_PIC && paramName.equals(MAIN_PIC_PARAM)) {
				if (StringUtils.isBlank(value)) return;
				value = StringUtils.replaceChars(value, '\\', System.getProperty("file.separator").charAt(0));
				File mainPicFile = picsFolder.resolve(value).toFile();
				if (mainPicFile.isFile()) {
					product.setValue(MAIN_PIC_PARAM, mainPicFile);
					product.clearValue("small_pic");

					File parentFolder = mainPicFile.getParentFile();
					String fileName = mainPicFile.getName();
					String regEx = StringUtils.substringBeforeLast(fileName, ".") + "_\\d+." + StringUtils.substringAfterLast(fileName, ".");
					File[] fileList = parentFolder.listFiles((file, s) -> s.matches(regEx));
					TreeMap<Integer, File> sorted = new TreeMap<>();
					for (File f : fileList) {
						String n = StringUtils.substringBeforeLast(f.getName(), ".");
						n = StringUtils.substringAfterLast(n, "_");
						sorted.put(Integer.parseInt(n), f);
					}
					product.clearValue(GALLERY_PARAM);
					for (Map.Entry<Integer, File> f : sorted.entrySet()) {
						product.setValue(GALLERY_PARAM, f.getValue());
					}
				} else {
					pushLog("No file: " + mainPicFile.getAbsolutePath());
				}
			} else {
				//fast copy inside the site
				boolean fromSameDomain = StringUtils.startsWith(value, getUrlBase());
				value = (fromSameDomain) ? value.replace(getUrlBase(), "") : value;
				if (StringUtils.startsWith(value, "http://") || StringUtils.startsWith(value, "https://")) {
					URL url = new URL(value);
					product.setValue(paramName, url);

				} else {
					value = StringUtils.replaceChars(value, '\\', System.getProperty("file.separator").charAt(0));
					String contextPath = AppContext.getContextPath();
					Path picPath = (fromSameDomain) ? Paths.get(contextPath, value) : picsFolder.resolve(value);

					if (picPath.toFile().isFile()) {
						boolean needsSet = !paramName.equals(MAIN_PIC_PARAM);

						if (!needsSet) {
							File oldMainPic = product.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
							if (!oldMainPic.isFile()) {
								needsSet = true;
								product.clearValue(SMALL_PIC_PARAM);
							} else {
								String n1 = Strings.getFileName(picPath.getFileName().toString());
								String n2 = oldMainPic.getName();
								needsSet = !n1.equals(n2) && !(MAIN_PIC_PARAM + "_" + n1).equals(n2);
								if (needsSet) {
									product.clearValue(SMALL_PIC_PARAM);
								}
							}
						}
						if (needsSet) {
							product.setValue(paramName, picPath.toFile());
						}
					} else {
						pushLog("Продукт " + product.getValue(CODE_PARAM) + ". Нет файла: " + value);
					}
				}
			}
		}

		private Item ensureProduct(String code, boolean isProduct) throws Exception {
			Item prod;
			if (isProduct) {
				prod = new ItemQuery(PRODUCT_ITEM, Item.STATUS_NORMAL, Item.STATUS_HIDDEN).setParentId(currentSection.getId(), false).addParameterCriteria(CODE_PARAM, code, "=", null, Compare.SOME).loadFirstItem();
				if (prod == null) {
					prod = Item.newChildItem(PRODUCT_ITEM_TYPE, currentSection);
				}
				currentProduct = prod;
			} else {
				prod = new ItemQuery(LINE_PRODUCT_ITEM, Item.STATUS_NORMAL, Item.STATUS_HIDDEN).setParentId(currentProduct.getId(), false).addParameterCriteria(CODE_PARAM, code, "=", null, Compare.SOME).loadFirstItem();
				if (prod == null) {
					prod = Item.newChildItem(LINE_PRODUCT_ITEM_TYPE, currentProduct);
				}
			}
			return prod;
		}

		private void loadCurrentAuxItemType() {
			String n1 = "p" + currentSection.getStringValue(CATEGORY_ID_PARAM, "").toLowerCase();
			String n2 = "p" + currentSection.getId();
			currentAuxItemType = ItemTypeRegistry.getItemType(n1);
			if (currentAuxItemType == null) currentAuxItemType = ItemTypeRegistry.getItemType(n2);
			if (currentAuxItemType == null) {
				sectionsWithNewItemTypes.add(currentSection.getId());
			}
		}

		private boolean hasAuxParams() {
			for (String header : getHeaders()) {
				String paramName = HEADER_PARAM.get(header);
				if ((PRODUCT_ITEM_TYPE.getParameterNames().contains(paramName)
						|| CreateExcelPriceList.AUX_TYPE_FILE.equalsIgnoreCase(header)
						|| CreateExcelPriceList.MANUAL.equalsIgnoreCase(header)
						|| CreateExcelPriceList.EXTRA_COLS.equalsIgnoreCase(header)
						|| CreateExcelPriceList.IS_DEVICE_FILE.equalsIgnoreCase(header))
						|| ON_MAIN.equalsIgnoreCase(header)
				)
					continue;
				return true;
			}
			return false;
		}

		@Override
		protected void processSheet() {
			currentProduct = null;
			currentSection = null;
			currentAuxItemType = null;
		}

		public IntegrationExcelDocument(File file, String... mandatoryCols) throws Exception {
			super(file, mandatoryCols);
			String pics_location = StringUtils.isBlank(getVarSingleValue(PICS_FOLDER_VAR)) ? DEFAULT_PICS_FOLDER : getVarSingleValue(PICS_FOLDER_VAR);
			picsFolder = Paths.get(AppContext.getContextPath(), pics_location);
			homepage = ItemQuery.loadSingleItemByName("main_page");
		}

		private void initSection(String code, String name, String parentCode, String hide) throws Exception {
			currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, code, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);

			//section not exists
			if (currentSection == null) {
				saveNewSection(code, name, parentCode);
				if ("+".equals(hide)) {
					currentSection.setValue("hide", (byte) 1);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).ignoreUser().noTriggerExtra().noFulltextIndex());
				}
				return;
			}

			//section exists
			switch (settings.get(WITH_EXISTING_SECS_VAR)) {
				case UPDATE:
					updateSection(name, parentCode);
					break;
				//pervert existing section options
				case DELETE:
					executeCommandUnit(ItemStatusDBUnit.delete(currentSection.getId()).noFulltextIndex());
					info.pushLog("Удален раздел: " + currentSection.getStringValue(NAME_PARAM, ""));
					saveNewSection(code, name, parentCode);
					break;
				case COPY:
					copySection(parentCode);
					updateSection(code, name);
					break;
				case COPY_IF_PARENT_DIFFERS:
					if (isParentDifferent(parentCode)) {
						copySection(parentCode);
					}
					updateSection(name, parentCode);
					break;
				case MOVE_IF_PARENT_DIFFERS:
					if (isParentDifferent(parentCode)) {
						moveSection(parentCode);
					}
					updateSection(name, parentCode);
					break;
			}
			if ("+".equals(hide)) {
				currentSection.setValue("hide", (byte) 1);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).ignoreUser().noTriggerExtra().noFulltextIndex());
			}
		}

		private void moveSection(String parentCode) throws Exception {
			Item declaredParent = StringUtils.isBlank(parentCode) ? catalog : ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, parentCode, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			info.pushLog("Перемещение раздела \"" + currentSection.getStringValue(NAME_PARAM, "") + "\". Это долгий процесс.");
			executeAndCommitCommandUnits(new MoveItemDBUnit(currentSection, declaredParent).ignoreUser());
			info.pushLog("Перемещение завершено");
			currentSection = new ItemQuery(SECTION_ITEM).setParentId(declaredParent.getId(), false).addParameterCriteria(CATEGORY_ID_PARAM, currentSection.getStringValue(CATEGORY_ID_PARAM, ""), "=", null, Compare.SOME).loadFirstItem();
		}

		private void copySection(String parentCode) throws Exception {
			Item declaredParent = StringUtils.isBlank(parentCode) ? catalog : ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, parentCode, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			info.pushLog("Будет создана копия раздела \"" + currentSection.getStringValue(NAME_PARAM, "") + "\". Это долгий процесс.");
			executeAndCommitCommandUnits(new CopyItemDBUnit(currentSection, declaredParent).ignoreUser());
			info.pushLog("Копирование завершено");
			List<Item> secs = new ItemQuery(SECTION_ITEM).setParentId(declaredParent.getId(), false).addParameterCriteria(CATEGORY_ID_PARAM, currentSection.getStringValue(CATEGORY_ID_PARAM, ""), "=", null, Compare.SOME).loadItems();
			currentSection = secs.get(0);
			for (Item sec : secs) {
				currentSection = sec.getId() > currentSection.getId() ? sec : currentSection;
			}
		}

		private boolean isParentDifferent(String parentCode) throws Exception {
			long parentId = currentSection.getContextParentId();
			Item declaredParent = StringUtils.isBlank(parentCode) ? catalog : ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, parentCode, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			return declaredParent.getId() != parentId;
		}

		private void saveNewSection(String code, String name, String parentCode) throws Exception {
			Item declaredParent = StringUtils.isBlank(parentCode) ? catalog : ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, parentCode, Item.STATUS_NORMAL, Item.STATUS_HIDDEN);
			currentSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), declaredParent);
			currentSection.setValue(CATEGORY_ID_PARAM, code);
			currentSection.setValue(CODE_PARAM, code);
			currentSection.setValue(NAME_PARAM, name);
			currentSection.setValue(PARENT_ID_PARAM, parentCode);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noTriggerExtra().noFulltextIndex().ignoreUser());
			sectionsWithNewItemTypes.add(currentSection.getId());
			info.pushLog("Создан раздел: " + name);
		}

		private void updateSection(String name, String parentCode) throws Exception {
			if (StringUtils.isNotBlank(parentCode)) {
				currentSection.setValue(PARENT_ID_PARAM, parentCode);
			}
			if (!currentSection.getStringValue(NAME_PARAM, "").equals(name)) {
				currentSection.setValue(NAME_PARAM, name);
				info.addLog("Обновлено название раздела " + currentSection.getStringValue(CODE_PARAM) + ": \"" + currentSection.getStringValue(NAME_PARAM, "") + "\"");
			}
			executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noTriggerExtra().noFulltextIndex().ignoreUser());
		}

		private void checkDuplicateCodes(String code) {
			if (duplicateCodes.contains(code) && !CreateExcelPriceList.CODE_FILE.equalsIgnoreCase(code)) {
				info.addError("Повторяющийся артикул: " + code, "");
				return;
			}
			if (!CreateExcelPriceList.CODE_FILE.equalsIgnoreCase(code)) {
				duplicateCodes.add(code);
			}
		}
	}

	private Collection<File> loadExcelFiles() throws Exception {
		String loadOption = getVarSingleValue(FILE_LOCATION_VAR);
		loadOption = StringUtils.isBlank(loadOption) ? DEFAULT_FILE_LOCATION : loadOption;
		if (loadOption.indexOf(':') == -1) {
			Path p = Paths.get(AppContext.getContextPath(), loadOption);
			if (p.toFile().isDirectory()) {
				Collection<File> files = FileUtils.listFiles(p.toFile(), new String[]{"xls", "xlsx"}, false);
				return files;

			} else if (p.toFile().isFile()) {
				return Arrays.asList(p.toFile());
			}
		} else {
			String[] param = StringUtils.split(loadOption, ':');
			Item item = ItemQuery.loadSingleItemByName(param[0]);
			return item.getFileValues(param[1], AppContext.getFilesDirPath(item.isFileProtected()));
		}
		return new ArrayList<>();
	}

	private void initSettings() {
		String v;
		if (StringUtils.isNotBlank(getVarSingleValue(WITH_EXISTING_PRODUCTS_VAR))) {
			v = getVarSingleValue(WITH_EXISTING_PRODUCTS_VAR);
			settings.replace(WITH_EXISTING_PRODUCTS_VAR, ImportProductsFromExcel.varValues.valueOf(v.toUpperCase()));
		}
		if (StringUtils.isNotBlank(getVarSingleValue(WITH_EXISTING_SECS_VAR))) {
			v = getVarSingleValue(WITH_EXISTING_SECS_VAR);
			settings.replace(WITH_EXISTING_SECS_VAR, varValues.valueOf(v.toUpperCase()));
		}
		if (StringUtils.isNotBlank(getVarSingleValue(IF_BLANK_VAR))) {
			v = getVarSingleValue(IF_BLANK_VAR);
			settings.replace(IF_BLANK_VAR, varValues.valueOf(v.toUpperCase()));
		}
		if (StringUtils.isNotBlank(getVarSingleValue(WITH_FILES_VAR))) {
			v = getVarSingleValue(WITH_FILES_VAR);
			settings.replace(WITH_FILES_VAR, varValues.valueOf(v.toUpperCase()));
		}
	}


	@Override
	protected void integrate() throws Exception {
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 1);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra().ignoreUser());
		setOperation("Обновлние каталога");
		setProcessed(0);
		setLineNumber(0);
		//parsing from Excel
		for (File f : files) {
			priceWorkbook = new IntegrationExcelDocument(f, CreateExcelPriceList.CODE_FILE, CreateExcelPriceList.NAME_FILE, CreateExcelPriceList.PRICE_FILE);
			info.setToProcess(priceWorkbook.getLinesCount());
			priceWorkbook.iterate();
			priceWorkbook.close();
		}
		//creating filters and item types
		createFiltersAndItemTypesIfNeeded();
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 0);
		//indexation
		info.setOperation("Индексация названий товаров");
		LuceneIndexMapper.getSingleton().reindexAll();
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra().ignoreUser());
		setOperation("Интеграция завершена");
	}

	private void createFiltersAndItemTypesIfNeeded() throws Exception {
		if (sectionsWithNewItemTypes.size() == 0) return;
		setOperation("Создание классов и фильтров");
		List<Item> sections = ItemQuery.loadByIdsLong(sectionsWithNewItemTypes, new Byte[]{Item.STATUS_NORMAL});
		doCreate(sections);
	}

	@Override
	protected void terminate() throws Exception {
	}

	private String normalizeSpace(String s) {
		if (s == null) return null;
		return s.trim().replaceAll("\\s+", " ");
	}

	private String firstUpperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}