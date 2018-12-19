package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.ExcelPriceList;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.*;
import ecommander.persistence.commandunits.*;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by user on 12.12.2018.
 */
public class ImportProductsFromExcel extends IntegrateBase implements CatalogConst {
	ExcelPriceList priceWB;
	Item catalog;
	Item currentSection;
	Item currentSubsection;
	private boolean newItemTypes = false;
	private HashSet<Long> sectionsWithNewItemTypes = new HashSet<>();
	//page vars
	//private static final String SEC_VAR = "sec";
	private static final String WITH_EXISTING_SECS = "with_existing_sections"; // what to do with existing sections (UPDATE, COPY, CREATE, COPY_IF_PARENT_DIFFERS, MOVE_IF_PARENT_DIFFERS, DELETE)
	private static final String WITH_EXISTING_PRODUCTS = "with_existing_products"; // what to do with existing products (UPDATE)
	private static final String IF_BLANK = "if_blank"; // what to do if the cell is blank; (IGNORE, CLEAR)
	private static final String WITH_PICS = "with_pics"; // where to look for product files (SEARCH_BY_CODE, SEARCH_BY_CELL_VALUE, DOWNLOAD)
	private static final String IF_FILE_EXISTS = "if_file_exists";

	//default page var values
	private enum varValues {
		UPDATE, COPY, CREATE, COPY_IF_PARENT_DIFFERS, MOVE_IF_PARENT_DIFFERS, DELETE, IGNORE, CLEAR, SEARCH_BY_CODE,
		UPDATE_IF_DIFFER, SEARCH_BY_CELL_VALUE, DOWNLOAD
	}

	private static HashMap<String, String> HEADER_PARAM = new HashMap() {{
		put(CreateExcelPriceList.CODE_FILE.toLowerCase(), CODE_PARAM);
		put(CreateExcelPriceList.NAME_FILE.toLowerCase(), NAME_PARAM);
		put(CreateExcelPriceList.PRICE_FILE.toLowerCase(), PRICE_PARAM);
		put(CreateExcelPriceList.QTY_FILE.toLowerCase(), QTY_PARAM);
		put(CreateExcelPriceList.AVAILABLE_FILE.toLowerCase(), AVAILABLE_PARAM);

	}};

	//default settings
	private HashMap<String, varValues> settings = new HashMap<String, varValues>() {{
		put(WITH_EXISTING_PRODUCTS, varValues.UPDATE);
		put(WITH_EXISTING_SECS, varValues.UPDATE);
		put(IF_BLANK, varValues.IGNORE);
		put(WITH_PICS, varValues.DOWNLOAD);
	}};


	@Override
	protected boolean makePreparations() throws Exception {
		catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		initSettings();
		Path contextPath = Paths.get(AppContext.getContextPath(), "upload");
		Collection<File> files = FileUtils.listFiles(contextPath.toFile(), new String[]{"xls", "xlsx"}, false);

		ItemType productItemType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
		ItemType paramsXMLItemType = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
		for (ParameterDescription param : productItemType.getParameterList()) {
			if (HEADER_PARAM.containsValue(param.getName())) continue;
			HEADER_PARAM.put(param.getCaption().toLowerCase(), param.getName());
		}

		if (files.size() > 1) {
			addError("Обнаружено более одного доакумента Microsoft Excel", "/upload");
		}
		File f = (File) files.toArray()[0];
		priceWB = new ExcelPriceList(f, CreateExcelPriceList.CODE_FILE, CreateExcelPriceList.NAME_FILE, CreateExcelPriceList.PRICE_FILE, CreateExcelPriceList.QTY_FILE, CreateExcelPriceList.AVAILABLE_FILE) {
			@Override
			protected void processRow() throws Exception {
				info.setLineNumber(getRowNum()+1);
				String code = getValue(CreateExcelPriceList.CODE_FILE);
				String name = getValue(CreateExcelPriceList.NAME_FILE);
				if(StringUtils.isBlank(name)) return;
				if (StringUtils.isBlank(code)) {
					name = getValue(CreateExcelPriceList.NAME_FILE);
					if (name.indexOf('|') != -1) {
						processSubsection(name);
					}
				} else if (code.equals(CreateExcelPriceList.CODE_FILE)) {
					reInit(CreateExcelPriceList.CODE_FILE, CreateExcelPriceList.NAME_FILE, CreateExcelPriceList.PRICE_FILE, CreateExcelPriceList.QTY_FILE, CreateExcelPriceList.AVAILABLE_FILE);
				} else {
					if (currentSubsection == null) currentSubsection = currentSection;
					Item product = getExistingProduct(code);
					TreeSet<String> headers = getHeaders();
					Path picsFolder = contextPath.resolve("product_pics");
					varValues withPictures = settings.get(WITH_PICS);
					// product NOT exists
					if (product == null) {
						Item parent = (code.indexOf('@') == -1) ? currentSubsection : new ItemQuery(PRODUCT_ITEM).setParentId(currentSubsection.getId(), false).addParameterCriteria(CODE_PARAM, StringUtils.substringAfter(code, "@"), "=", null, Compare.SOME).loadFirstItem();
//						ItemType itemType = (code.indexOf('@') == -1) ? ItemTypeRegistry.getItemType(PRODUCT_ITEM) : ItemTypeRegistry.getItemType(LINE_PRODUCT_ITEM);
						product = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT_ITEM), parent);
						code = (code.indexOf('@') == -1) ? code : StringUtils.substringBefore(code, "@");
						//set product params
						for (String header : headers) {
							String paramName = HEADER_PARAM.get(header);
							if (!productItemType.getParameterNames().contains(paramName) || CreateExcelPriceList.MANUAL.equalsIgnoreCase(header)) continue;
							String cellValue = getValue(header);
							if (CODE_PARAM.equals(paramName)) {
								product.setValue(CODE_PARAM, code);
								product.setValue(VENDOR_CODE_PARAM, code);
								product.setValue(OFFER_ID_PARAM, code);

							} else if (MAIN_PIC_PARAM.equals(paramName)) {
								if (withPictures == varValues.IGNORE) continue;
								switch (withPictures) {
									case SEARCH_BY_CODE:
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
//												} else {
//													product.setValue(FILES_PARAM, f);
//												}
											}
										}
										break;
									case SEARCH_BY_CELL_VALUE:
										mainPicPath = picsFolder.resolve(cellValue);
										product.setValue(MAIN_PIC_PARAM, mainPicPath.toFile());
										break;
									case DOWNLOAD: {
										if(StringUtils.isNotBlank(cellValue)) {
											try {
												URL url = new URL(cellValue.trim());
												product.setValue(paramName, url);
											}catch (Exception e){}

										}
									}
									default:
										break;
								}
							}else if (GALLERY_PARAM.equalsIgnoreCase(paramName) || TEXT_PICS_PARAM.equalsIgnoreCase(paramName)) {
								//else if (GALLERY_PARAM.equalsIgnoreCase(paramName) || TEXT_PICS_PARAM.equalsIgnoreCase(paramName) || FILES_PARAM.equalsIgnoreCase(paramName)) {
								if (withPictures == varValues.IGNORE || withPictures == varValues.SEARCH_BY_CODE)
									continue;
								if(StringUtils.isBlank(cellValue))continue;
								String[] arr = cellValue.split("\",\\s*\"");
								for (String s : arr) {
									switch(withPictures){
										case SEARCH_BY_CELL_VALUE:
											File p = picsFolder.resolve(s).toFile();
											if (p.exists() && p.isFile()) product.setValue(paramName, p);
											break;
										case DOWNLOAD:
											if(StringUtils.isBlank(s))continue;
											try{URL url = new URL(s);
											product.setValue(paramName, url);}catch (Exception e){}
											break;
										default: break;
									}

								}
							} else {
								product.setValueUI(paramName, cellValue);
							}
						}
						executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreFileErrors(true).noFulltextIndex());

						//MANUALS
						for(String header : headers){
							if(CreateExcelPriceList.MANUAL.equalsIgnoreCase(header)){
								String cellValue = getValue(header);
								String[]m = cellValue.split("_END_");
								for(String manual : m){
									Item manualItem = Item.newChildItem(ItemTypeRegistry.getItemType(MANUAL_PARAM), product);
									manualItem.setValue(NAME_PARAM, "Документ");
									manualItem.setValue(LINK_PARAM, manual);
									executeCommandUnit(SaveItemDBUnit.get(manualItem).noFulltextIndex().noTriggerExtra());
								}
								commitCommandUnits();
							}
						}

					}
					//PRODUCT EXISTS
					else {
						//set product params
						for (String header : headers) {
							String paramName = HEADER_PARAM.get(header);
							if (!productItemType.getParameterNames().contains(paramName) || CreateExcelPriceList.MANUAL.equalsIgnoreCase(header)) continue;
							String cellValue = getValue(header);
							varValues ifBlank = settings.get(IF_BLANK);
							if (StringUtils.isBlank(cellValue) && ifBlank == varValues.IGNORE) continue;
							if (CODE_PARAM.equals(paramName)) {
								if (StringUtils.isBlank(product.getStringValue(VENDOR_CODE_PARAM))) {
									product.setValueUI(VENDOR_CODE_PARAM, code);
								}
								if (StringUtils.isBlank(product.getStringValue(OFFER_ID_PARAM))) {
									product.setValueUI(OFFER_ID_PARAM, code);
								}
							} else if (MAIN_PIC_PARAM.equals(paramName)) {
								Object mainPic = product.getValue(MAIN_PIC_PARAM, "");

								if (mainPic.toString().equals(cellValue) && StringUtils.isNotBlank(mainPic.toString()))
									continue;

								if (StringUtils.isBlank(cellValue) && ifBlank == varValues.CLEAR && withPictures == varValues.SEARCH_BY_CODE) {
									File mainPicFile = picsFolder.resolve(code + ".jpg").toFile();
									if (mainPicFile.exists()) {
										product.setValue(MAIN_PIC_PARAM, mainPicFile);
										product.clearParameter("medium_pic");
										product.clearParameter("small_pic");
									}
								} else if (StringUtils.isBlank(mainPic.toString())) {
									switch (withPictures) {
										case SEARCH_BY_CODE:
											File mainPicFile = picsFolder.resolve(code + ".jpg").toFile();
											if (mainPicFile.exists()) {
												product.setValue(MAIN_PIC_PARAM, mainPicFile);
												product.clearParameter("medium_pic");
												product.clearParameter("small_pic");
											}
											break;
										case SEARCH_BY_CELL_VALUE:
											if (StringUtils.isBlank(cellValue)) break;
											mainPicFile = picsFolder.resolve(cellValue).toFile();
											if (mainPicFile.exists()) {
												product.setValue(MAIN_PIC_PARAM, mainPicFile);
												product.clearParameter("medium_pic");
												product.clearParameter("small_pic");
											}
											break;

										default:
											break;
									}
								}
							} else if (GALLERY_PARAM.equals(paramName)) {
								String currVa = getStr(product, paramName);
								if (currVa.equals(cellValue.trim()) && StringUtils.isNotBlank(currVa)) continue;
								else if ((StringUtils.isBlank(cellValue) && ifBlank == varValues.CLEAR) || StringUtils.isBlank(currVa)) {
									product.clearParameter(paramName);
									if (withPictures == varValues.SEARCH_BY_CODE) {
										Path filesPath = picsFolder.resolve(code);
										File additionalFiles = filesPath.toFile();
										if (additionalFiles.exists()) {
											for (File f : FileUtils.listFiles(filesPath.toFile(), null, false)) {
												if (f.getName().matches("[^\\s]+(\\.(?i)(jpe?g|png|gif|bmp|svg))$")) {
													product.setValue(GALLERY_PARAM, f);
												}
											}
										}
									}
								} else {
									product = setMultipleFileParam(product, paramName, cellValue, picsFolder);
								}
							} else if (TEXT_PICS_PARAM.equals(paramName)) {
								String currVa = getStr(product, paramName);
								if (currVa.equals(cellValue.trim()) && StringUtils.isNotBlank(currVa)) continue;
								else if ((StringUtils.isBlank(cellValue) && ifBlank == varValues.CLEAR) || StringUtils.isBlank(currVa)) {
									product.clearParameter(paramName);
									if (withPictures == varValues.SEARCH_BY_CODE) {
										Path filesPath = picsFolder.resolve(code);
										File additionalFiles = filesPath.toFile();
										if (additionalFiles.exists()) {
											for (File f : FileUtils.listFiles(filesPath.toFile(), null, false)) {
												if(!f.isDirectory()) continue;
												for (File textPic : FileUtils.listFiles(f, null, false)) {
													product.setValue(TEXT_PICS_PARAM, textPic);
												}
											}
										}
									}
								} else {
									product = setMultipleFileParam(product, paramName, cellValue, picsFolder);
								}
							}
//							else if (FILES_PARAM.equals(paramName)) {
//								String currVa = getStr(product, paramName);
//								if (currVa.equals(cellValue.trim()) && StringUtils.isNotBlank(currVa)) continue;
//								else if ((StringUtils.isBlank(cellValue) && ifBlank == varValues.CLEAR) || StringUtils.isBlank(currVa)) {
//									product.clearParameter(FILES_PARAM);
//									if (withPictures == varValues.SEARCH_BY_CODE) {
//										Path filesPath = picsFolder.resolve(code);
//										File additionalFiles = filesPath.toFile();
//										if (additionalFiles.exists()) {
//											for (File f : FileUtils.listFiles(filesPath.toFile(), null, false)) {
//												if (!f.getName().matches("[^\\s]+(\\.(?i)(jpe?g|png|gif|bmp|svg))$")) {
//													product.setValue(FILES_PARAM, f);
//												}
//											}
//										}
//									}
//								} else {
//									product = setMultipleFileParam(product, paramName, cellValue, picsFolder);
//								}
//							}
							else {
								if (StringUtils.isBlank(cellValue) && ifBlank == varValues.IGNORE) continue;
								product.setValueUI(paramName, cellValue);
							}

						}
						executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreFileErrors(true).noFulltextIndex());
						//MANUALS
						for(String header : headers){
							if(CreateExcelPriceList.MANUAL.equalsIgnoreCase(header)){
								String cellValue = getValue(header);
								String[]m = cellValue.split("}\\s?\\{");
								for(String manual : m){
									Item manualItem;
									if(manual.indexOf('|') == -1) {
										manualItem = Item.newChildItem(ItemTypeRegistry.getItemType(MANUAL_PARAM), product);
										manualItem.setValue(NAME_PARAM, "Документ");
										manualItem.setValue(LINK_PARAM, manual);

									}else{
										String[]x = manual.split("[|]");
										long id = Long.parseLong(x[0].trim());
										manualItem = ItemQuery.loadById(id);
										manualItem.setValue(LINK_PARAM, manual);
									}
									executeCommandUnit(SaveItemDBUnit.get(manualItem).noFulltextIndex().noTriggerExtra());
								}
								commitCommandUnits();
							}
						}
					}
					//process auxType
					if (headers.contains(CreateExcelPriceList.AUX_TYPE_FILE.toLowerCase())) {
						String auxTypeString = getValue(CreateExcelPriceList.AUX_TYPE_FILE.toLowerCase());
						ItemType auxType = null;
						Item paramsXML = new ItemQuery(paramsXMLItemType).setParentId(product.getId(), false).loadFirstItem();
						paramsXML = (paramsXML == null) ? Item.newChildItem(paramsXMLItemType, product) : paramsXML;
						if (StringUtils.isNotBlank(auxTypeString)) {
							auxType = ItemTypeRegistry.getItemType(Integer.parseInt(auxTypeString));
						}
						Item aux = null;
						HashMap<String, String> auxParams = new HashMap<>();
						if (auxType != null) {
							aux = new ItemQuery(PARAMS_ITEM).setParentId(product.getId(), false).loadFirstItem();
							aux = (aux == null) ? Item.newChildItem(auxType, product) : aux;

							for (ParameterDescription pd : auxType.getParameterList()) {
								auxParams.put(pd.getCaption().toLowerCase(), pd.getName());
							}
						} else {
							newItemTypes = true;
						}
						XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
						for (String header : headers) {
							String paramName = HEADER_PARAM.get(header);
							if (productItemType.getParameterNames().contains(paramName) || CreateExcelPriceList.AUX_TYPE_FILE.equalsIgnoreCase(header) || CreateExcelPriceList.MANUAL.equalsIgnoreCase(header)) continue;
							String cellValue = getValue(header);
							xml.startElement("parameter")
									.startElement("name")
									.addText(firstUpperCase(header))
									.endElement()
									.startElement("value")
									.addText(cellValue)
									.endElement()
									.endElement();

							if(auxType == null) continue;
							String param = auxParams.get(header.toLowerCase());
							if(!auxType.getParameterNames().contains(param)) continue;
							if (StringUtils.isNotBlank(auxParams.get(param))) aux.setValueUI(auxParams.get(header.toLowerCase()), cellValue);
						}
						paramsXML.setValueUI(XML_PARAM, xml.toString());
						executeCommandUnit(SaveItemDBUnit.get(paramsXML).noFulltextIndex());
						if (auxType != null) {
							executeCommandUnit(SaveItemDBUnit.get(aux).noFulltextIndex());
						}
						commitCommandUnits();
					}
				}

			}

			private Item setMultipleFileParam(Item item, String paramName, String values, Path folder){
				String[] apv = values.split("\",\\s?\"");
				LinkedHashSet<File> existingFiles = new LinkedHashSet<>();
				for (String s : apv) {
					s = StringUtils.startsWith(s, "\"") ? s.substring(1) : s;
					s = StringUtils.endsWith(s, "\"") ? s.substring(0, s.length() - 2) : s;
					File f = folder.resolve(s).toFile();
					if (f.exists()) {
						existingFiles.add(f);
					}
				}
				if (existingFiles.size() > 0) {
					item.clearParameter(paramName);
					for (File f : existingFiles) {
						item.setValue(paramName, f);
					}
				}
				return item;
			}

			private String getStr(Item item, String paramName) {
				ArrayList<Object> pv = item.getValues(paramName);
				return (pv.size() == 0) ? "" : (pv.size() == 1) ? pv.get(0).toString() : CreateExcelPriceList.join(pv);
			}

			private Item getExistingProduct(String code) throws Exception {
				Item prod;
//				if (code.indexOf('@') == -1) {
					prod = new ItemQuery(PRODUCT_ITEM).setParentId(currentSubsection.getId(), false).addParameterCriteria(CODE_PARAM, code, "=", null, Compare.SOME).loadFirstItem();
//				}
//				else {
//					String[] codes = code.split("@");
//					Item parent = new ItemQuery(PRODUCT_ITEM).setParentId(currentSubsection.getId(), false).addParameterCriteria(CODE_PARAM, codes[1], "=", null, Compare.SOME).loadFirstItem();
//					prod = new ItemQuery(LINE_PRODUCT_ITEM).setParentId(parent.getId(), false).addParameterCriteria(CODE_PARAM, codes[0], "=", null, Compare.SOME).loadFirstItem();
//				}
				return prod;
			}

			@Override
			protected void processSheet() throws Exception {
				String sheetName = getSheetName();
				String[] secInfo = sheetName.split("[|]");
				String sectionName = secInfo[0].trim();
				setOperation("Обработка раздела: \"" + sectionName + "\"");
				String sectionCode = secInfo[1].trim();
				Item existingSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, sectionCode);
				if (existingSection != null) {
					switch (settings.get(WITH_EXISTING_SECS)) {
						case UPDATE:
							if (existingSection != null) {
								updateSectionName(existingSection, sectionName);
								currentSection = existingSection;
							} else {
								currentSection = createSection(secInfo);
							}
							break;
						case CREATE:
							currentSection = createSection(secInfo);
							break;
						case DELETE:
							executeCommandUnit(ItemStatusDBUnit.delete(existingSection.getId()).noFulltextIndex());
							executeCommandUnit(new CleanAllDeletedItemsDBUnit(20, null));
							currentSection = createSection(secInfo);
							break;
						case COPY:
							if (existingSection != null) {
								String sectionParentId = secInfo[2].trim();
								Item parent = getDeclaredParent(sectionParentId);
								info.setOperation("Копирую раздел: \"" + sectionName + "\".Это долгий и трудный процесс.");
								info.pushLog("Попробуйте создавать новые разделы и копировать в них продукты. Это будет быстрее.");
								executeAndCommitCommandUnits(new CopyItemDBUnit(existingSection, parent).ignoreFileErrors(true));
								info.pushLog("Уфф! Скопировал.");
								setOperation("Обработка раздела: \"" + sectionName + "\"");
								Item recentlyCopiedSection = ItemQuery.loadByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, sectionCode).get(1);
								currentSection = recentlyCopiedSection;
							} else {
								currentSection = createSection(secInfo);
							}
							break;
						case COPY_IF_PARENT_DIFFERS:
							if (existingSection != null) {
								String sectionParentId = secInfo[2].trim();
								long currentParentId = existingSection.getContextParentId();
								Item declaredParent = getDeclaredParent(sectionParentId);
								if (declaredParent.getId() != currentParentId) {
									info.setOperation("Копирую раздел: \"" + sectionName + "\".Это долгий и трудный процесс.");
									info.pushLog("Попробуйте создавать новые разделы и копировать в них продукты. Это будет быстрее.");
									executeAndCommitCommandUnits(new CopyItemDBUnit(existingSection, declaredParent).ignoreFileErrors(true));
									info.pushLog("Уфф! Скопировал.");
									setOperation("Обработка раздела: \"" + sectionName + "\"");
								}
								ItemQuery q = new ItemQuery(SECTION_ITEM).setParentId(declaredParent.getId(), false).addParameterCriteria(CATEGORY_ID_PARAM, secInfo[1], "=", null, Compare.SOME);
								Item recentlyCopiedSection = q.loadFirstItem();
								currentSection = recentlyCopiedSection;
							} else {
								currentSection = createSection(secInfo);
							}
							break;
						case MOVE_IF_PARENT_DIFFERS:
							if (existingSection != null) {
								String sectionParentId = secInfo[2].trim();
								Item currentParent = new ItemQuery(SECTION_ITEM).setChildId(existingSection.getId(), false).loadFirstItem();
								if (currentParent == null) {
									currentParent = catalog;
								}
								long currentParentId = currentParent.getId();
								Item declaredParent = getDeclaredParent(sectionParentId);
								if (declaredParent.getId() != currentParentId) {
									info.setOperation("Перемещаю раздел: \"" + sectionName + "\".Это долгий и трудный процесс. Вы уверены, что оно Вам надо?");
									info.pushLog("Попробуйте создавать новые разделы и перемещать в них продукты. Это будет быстрее.");
									executeAndCommitCommandUnits(new MoveItemDBUnit(existingSection, declaredParent).ignoreFileErrors(true));
									info.pushLog("Уфф! Переместил.");
									setOperation("Обработка раздела: \"" + sectionName + "\"");
								}
								currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, sectionCode);
							} else {
								currentSection = createSection(secInfo);
							}
							break;
						default:
							break;
					}
				} else {
					currentSection = createSection(secInfo);
				}
			}
		};
		return true;
	}

	private void processSubsection(String name) throws Exception {
		String[] secInfo = name.split("[|]");
		String sName = secInfo[0].trim();
		String sCode = secInfo[1].trim();
		String sParentCode = secInfo[2].trim();
		Item existingSection = new ItemQuery(SECTION_ITEM).setParentId(currentSection.getId(), true).addParameterCriteria(CATEGORY_ID_PARAM, sCode, "=", null, Compare.SOME).loadFirstItem();
		Item declaredParent = getDeclaredParent(sParentCode, currentSection.getId());
		if (existingSection != null) {
			varValues v = (settings.get(WITH_EXISTING_SECS) == varValues.COPY) ? varValues.COPY_IF_PARENT_DIFFERS : settings.get(WITH_EXISTING_SECS);
			switch (v) {
				case DELETE:
					executeCommandUnit(ItemStatusDBUnit.delete(existingSection.getId()).noFulltextIndex());
					executeCommandUnit(new CleanAllDeletedItemsDBUnit(20, null));
					Item newSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), declaredParent);
					newSection.setValue(NAME_PARAM, sName);
					newSection.setValue(CATEGORY_ID_PARAM, sCode);
					newSection.setValue(PARENT_ID_PARAM, sParentCode);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(existingSection).noFulltextIndex());
					info.pushLog("Создан раздел: " + sName);
					break;
				case COPY_IF_PARENT_DIFFERS:
					info.setOperation("Копирую раздел: " + sName);
					executeAndCommitCommandUnits(new CopyItemDBUnit(existingSection, declaredParent));
					info.pushLog("Копирование завершено");
					info.setOperation("Обработка раздела: " + currentSection.getStringValue(NAME_PARAM));
					existingSection = new ItemQuery(SECTION_ITEM).setParentId(declaredParent.getId(), false).addParameterCriteria(CATEGORY_ID_PARAM, sCode, "=", null, Compare.SOME).loadFirstItem();
					break;
				case MOVE_IF_PARENT_DIFFERS:
					info.setOperation("Перемещаю раздел: " + sName);
					executeAndCommitCommandUnits(new MoveItemDBUnit(existingSection, declaredParent));
					info.pushLog("Копирование завершено");
					info.setOperation("Обработка раздела: " + currentSection.getStringValue(NAME_PARAM));
					existingSection = new ItemQuery(SECTION_ITEM).setParentId(declaredParent.getId(), false).addParameterCriteria(CATEGORY_ID_PARAM, sCode, "=", null, Compare.SOME).loadFirstItem();
					break;
				case UPDATE:
					if (!existingSection.getStringValue(NAME_PARAM, "").equals(sName)) {
						updateSectionName(existingSection, sName);
					}
				default:
					break;
			}
			currentSubsection = existingSection;
		} else {
			Item newSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), declaredParent);
			newSection.setValue(NAME_PARAM, sName);
			newSection.setValue(CATEGORY_ID_PARAM, sCode);
			newSection.setValue(PARENT_ID_PARAM, sParentCode);
			executeAndCommitCommandUnits(SaveItemDBUnit.get(newSection).noFulltextIndex());
			info.pushLog("Создан раздел: " + sName);
			currentSubsection = newSection;
		}
	}

	private void updateSectionName(Item section, String name) throws Exception {
		if (!section.getStringValue(NAME_PARAM, "").equals(name)) {
			section.setValue(NAME_PARAM, name);
			info.addLog("Обновлено название раздела " + section.getStringValue(CODE_PARAM) + ": \"" + section.getStringValue(NAME_PARAM, "") + "\"");
			executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noFulltextIndex().ignoreFileErrors(true).noTriggerExtra());
		}
	}


	private Item getDeclaredParent(String sectionParentId, long superId) throws Exception {
		if (currentSection.getStringValue(CATEGORY_ID_PARAM, "").equals(sectionParentId)) return currentSection;
		Item parent = (StringUtils.isBlank(sectionParentId)) ? catalog : new ItemQuery(SECTION_ITEM).setParentId(superId, true).addParameterCriteria(CATEGORY_ID_PARAM, sectionParentId, "=", null, Compare.SOME).loadFirstItem();
		parent = (parent == null) ? getDeclaredParent(sectionParentId) : parent;
		return parent;
	}

	private Item getDeclaredParent(String sectionParentId) throws Exception {
		Item parent = (StringUtils.isBlank(sectionParentId)) ? catalog : ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CATEGORY_ID_PARAM, sectionParentId);
		parent = (parent == null) ? catalog : parent;
		return parent;
	}

	private Item createSection(String[] secInfo) throws Exception {
		String sectionName = secInfo[0].trim();
		info.pushLog("Найден раздел: " + sectionName);
		String sectionParentId = (secInfo.length == 3)? secInfo[2].trim() : "";
		Item parent = getDeclaredParent(sectionParentId);
		Item newSection = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), parent);
		newSection.setValue(NAME_PARAM, sectionName);
		newSection.setValue(CATEGORY_ID_PARAM, secInfo[1].trim());
		newSection.setValue(PARENT_ID_PARAM, sectionParentId);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(newSection).noTriggerExtra().noFulltextIndex());
		info.pushLog("Создан раздел: " + sectionName);
		return newSection;
	}

	private void initSettings() {
		String v = null;
		if (StringUtils.isNotBlank(getVarSingleValue(WITH_EXISTING_PRODUCTS))) {
			v = getVarSingleValue(WITH_EXISTING_PRODUCTS);
			settings.replace(WITH_EXISTING_PRODUCTS, varValues.valueOf(v.toUpperCase()));
		}
		if (StringUtils.isNotBlank(getVarSingleValue(WITH_EXISTING_SECS))) {
			v = getVarSingleValue(WITH_EXISTING_SECS);
			settings.replace(WITH_EXISTING_SECS, varValues.valueOf(v.toUpperCase()));
		}
		if (StringUtils.isNotBlank(getVarSingleValue(IF_BLANK))) {
			v = getVarSingleValue(IF_BLANK);
			settings.replace(IF_BLANK, varValues.valueOf(v.toUpperCase()));
		}
		if (StringUtils.isNotBlank(getVarSingleValue(WITH_PICS))) {
			v = getVarSingleValue(WITH_PICS);
			settings.replace(WITH_PICS, varValues.valueOf(v.toUpperCase()));
		}
	}

	@Override
	protected void integrate() throws Exception {
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 1);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
		info.setOperation("Обновлние каталога");
		info.setProcessed(0);
		info.setLineNumber(0);
		info.setToProcess(priceWB.getLinesCount());
		priceWB.iterate();
		info.setOperation("Интеграция завершена");
		priceWB.close();
		catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 0);
		executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
	}

	@Override
	protected void terminate() throws Exception {

	}

	private String firstUpperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
