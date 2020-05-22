package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by user on 05.12.2018.
 */
public class CreateExcelPriceList extends IntegrateBase implements CatalogConst {
	//workbook styles
	private HSSFWorkbook workBook;
	private CellStyle headerStyle;
	private CellStyle auxStyle;
	private CellStyle noPriceStyle;
	private CellStyle noCodeStyle;
	private CellStyle sectionStyle;
	private CellStyle auxHeaderStyle;
	//file Constants
	protected static final String CODE_FILE = "Код";
	protected static final String IS_DEVICE_FILE = "Отдельный товар";
	protected static final String NAME_FILE = "Наименование";
	protected static final String PRICE_FILE = "Цена";
	protected static final String PRICE_OLD_FILE = "Старая цена";
	protected static final String PRICE_ORIGINAL_FILE = "Цена в оригинале";
	protected static final String CURRENCY_ID_FILE = "Код валюты цены";
	protected static final String QTY_FILE = "Количество";
	protected static final String UNIT_FILE = "Единица измерения";
	protected static final String AVAILABLE_FILE = "Наличие";
	protected static final String AUX_TYPE_FILE = "ID типа товара";
	protected static final String MANUAL = "Документация";
	protected static final String VALUE_SEPARATOR = ";";


	private static final LinkedHashSet<String> BUILT_IN_PARAMS = new LinkedHashSet<String>() {{
		add(CODE_PARAM);
		add(NAME_PARAM);
		add(PRICE_PARAM);
		add(PRICE_OLD_PARAM);
		add(PRICE_ORIGINAL_PARAM);
		add(CURRENCY_ID_PARAM);
		add(QTY_PARAM);
		add(AVAILABLE_PARAM);
		add("small_pic");
		add("old_file");
		add("vendor_code");
		add("offer_id");
		add("extra_xml");
		add("unit");
	}};

	//page vars
	private static final String PROD_PARAMS_VAR = "prod_params";
	private static final String AUX_PARAMS_VAR = "aux_params";
	private static final String LINE_PRODUCTS_VAR = "line_products";
	private static final String PRODUCTS_VAR = "existing_products";
	private static final String SECTION_VAR = "sec";
	private static final String PRICE_ONLY_VAR = "price_only";//sets all vars to write only prices. cancels all other settings
	private static final String MANUALS_VAR = "manuals";
	private static final String YES = "yes";
	private static final String NO = "no";

	private boolean writeAllProductParams = false;
	private boolean writeAuxParams = false;
	private boolean writeProducts = true;
	private boolean writeManuals = true;
	private boolean writeLineProducts = true;
	private boolean writeLineProductsHeader = true;
	private boolean writeHierarchy = true;
	private boolean hasUnits = false;
	private long secId = 0L;



	@Override
	protected void integrate() throws Exception {
		setOperation("Загрузка каталога товаров");
		Item catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
		if (catalog == null) {
			info.pushLog("Каталог не найден");
			return;
		}

		List<Item> sections = new ArrayList<>();
		String fileSuffix = "";
		if (secId == 0) {
			ItemQuery q = new ItemQuery(SECTION_ITEM).setParentId(catalog.getId(), false);
			sections = q.loadItems();
		} else {
			Item section = ItemQuery.loadById(secId);
			fileSuffix = section.getStringValue(NAME_PARAM).replaceAll("/", " ");
			sections.add(section);
		}
		info.pushLog("Обнаружено разделов первого уровня: " + sections.size());

		workBook = new HSSFWorkbook();
		initCellStyles();

		for (Item section : sections) {
			Sheet sh = initializeSheet(section);
			int rowIndex = -1;
			long id = section.getId();
			int colIdx = -1;
			rowIndex = initializeHeader(sh,rowIndex);
			if(writeHierarchy) {

				Row row = sh.createRow(++rowIndex);
				String[] secInfo = getSectionName(section);
				row.createCell(++colIdx).setCellValue("разд:" + secInfo[0]);
				row.getCell(colIdx).setCellStyle(sectionStyle);
				row.createCell(++colIdx).setCellValue(secInfo[1]);
				row.getCell(colIdx).setCellStyle(sectionStyle);
				row.createCell(++colIdx).setCellValue(secInfo[2]);
				row.getCell(colIdx).setCellStyle(sectionStyle);
			}
			boolean isEmpty = new ItemQuery(PRODUCT_ITEM).setParentId(id, false).loadFirstItem() == null;
			if(!isEmpty) {
				ItemType auxType = getAuxType(section.getId());
				if(writeHierarchy) {
					rowIndex = (auxType == null) ? initializeHeader(sh, rowIndex) : initializeHeader(sh, rowIndex, auxType);
				}
				rowIndex = processProducts(sh, rowIndex, id);
			}
			processSubsections(sh, rowIndex, id);
		}
		setOperation("Запись файла");
		String optionsSuffix = (writeHierarchy)? "" : "min-";
		String fileName = "pricelist-"+ optionsSuffix + fileSuffix + ".xls";
		pushLog(fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(AppContext.getFilesDirPath(false) + "/" + fileName);
		workBook.write(fileOutputStream);
		fileOutputStream.close();
	}


	private Sheet initializeSheet(Item section) throws Exception {
		String[]secInfo = getSectionName(section);
		String sheetName = secInfo[2];
		setOperation(section.getValue(NAME_PARAM) + ". Обработка подразделов.");
		return workBook.createSheet(sheetName);
	}

	private String[] getSectionName(Item section) throws Exception {
		String name = section.getStringValue(NAME_PARAM,"");
		String categoryId = section.getStringValue(CATEGORY_ID_PARAM,"");
		boolean needsSave = false;
		if(StringUtils.isBlank(categoryId)){
			categoryId = String.valueOf(section.getId());
			section.setValue(CATEGORY_ID_PARAM, categoryId);
			executeCommandUnit(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
			needsSave = true;
		}
		String parentId = section.getStringValue(PARENT_ID_PARAM,"");
		if(StringUtils.isBlank(parentId)){
			Item parent = new ItemQuery(SECTION_ITEM).setChildId(section.getId(), false).loadFirstItem();
			if(parent != null){
				parentId = parent.getStringValue(CATEGORY_ID_PARAM,"");
				if(StringUtils.isBlank(parentId)){
					parentId = String.valueOf(parent.getId());
					parent.setValue(CATEGORY_ID_PARAM, parentId);
					executeCommandUnit(SaveItemDBUnit.get(parent).noFulltextIndex().noTriggerExtra());
				}
				section.setValue(PARENT_ID_PARAM, parentId);
				executeCommandUnit(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
				needsSave = true;
			}

		}
		if(needsSave) commitCommandUnits();
		return new String[]{categoryId, parentId, name,};
	}

	private int initializeHeader(Sheet sh, int rowIndex, ItemType... auxType){
		int rowIdx = rowIndex;
		int colIdx = -1;

		//built-in params
		Row row = sh.createRow(++rowIdx);
		row.createCell(++colIdx).setCellValue(CODE_FILE);
		sh.setColumnWidth(colIdx, 20 * 256);
		if(writeLineProducts && writeLineProductsHeader) {
			row.createCell(++colIdx).setCellValue(IS_DEVICE_FILE);
			sh.setColumnWidth(colIdx, 20 * 256);
		}
		row.createCell(++colIdx).setCellValue(NAME_FILE);
		sh.setColumnWidth(colIdx, 75 * 256);
		row.createCell(++colIdx).setCellValue(PRICE_FILE);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(PRICE_OLD_FILE);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(PRICE_ORIGINAL_FILE);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(CURRENCY_ID_FILE);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(QTY_FILE);
		sh.setColumnWidth(colIdx, 25 * 256);
		row.createCell(++colIdx).setCellValue(UNIT_FILE);
		sh.setColumnWidth(colIdx, 20 * 256);
		row.createCell(++colIdx).setCellValue(AVAILABLE_FILE);
		sh.setColumnWidth(colIdx, 20 * 256);

		//Write all product params
		if (writeAllProductParams) {
			ItemType productItemType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
			for (ParameterDescription param : productItemType.getParameterList()) {
				String paramName = param.getName();
				if (BUILT_IN_PARAMS.contains(paramName)) continue;
				String caption = param.getCaption();
				row.createCell(++colIdx).setCellValue(caption);
				row.getCell(colIdx).setCellStyle(headerStyle);
				sh.setColumnWidth(colIdx, 20 * 256);
			}
		}

		//Write manuals
		if(writeManuals){
			row.createCell(++colIdx).setCellValue(MANUAL);
			row.getCell(colIdx).setCellStyle(headerStyle);
		}

		for (int i = 0; i < colIdx+1; i++) {
			row.getCell(i).setCellStyle(headerStyle);
		}

		//Write aux params
		row.createCell(++colIdx).setCellValue(AUX_TYPE_FILE);
		row.getCell(colIdx).setCellStyle(auxStyle);
		if(auxType.length > 0 && writeAuxParams){
			ItemType aux = auxType[0];
			if(aux != null) {

				row.getCell(colIdx).setCellStyle(auxHeaderStyle);
				sh.setColumnWidth(colIdx, 10 * 256);
				for (ParameterDescription auxParam : aux.getParameterList()) {
					String caption = auxParam.getCaption();
					row.createCell(++colIdx).setCellValue(caption);
					row.getCell(colIdx).setCellStyle(auxHeaderStyle);
					sh.setColumnWidth(colIdx, 20 * 256);
				}
			}
		}

		return rowIdx;
	}

	private int processProducts(Sheet sh, int rowIndex, long sectionId) throws Exception {
		int rowI = rowIndex;
		if (!writeProducts) return rowI;

		List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(sectionId, false).loadItems();
		info.pushLog("Найдено продуктов:" + products.size());
		info.setToProcess(products.size());
		info.setProcessed(0);

		ItemType productItemType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
		ItemType paramsType = getAuxType(sectionId);

		for (Item product : products) {
			Item aux = null;
			//write aux params
			if(writeAuxParams) {
				aux = new ItemQuery(PARAMS_ITEM).setParentId(product.getId(), false).loadFirstItem();
				if(aux != null && !aux.getItemType().equals(paramsType)){
					paramsType = aux.getItemType();
					rowI = initializeHeader(sh, rowI, paramsType);

				}

			}
			Row row = sh.createRow(++rowI);
			int colIdx = -1;
			CellStyle cellStyle = chooseCellStyle(product);
//			double price;
//			if(priceType == DataType.Type.DECIMAL || priceType == DataType.Type.CURRENCY || priceType == DataType.Type.CURRENCY_PRECISE) {
//				price = product.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO).doubleValue();
//			}else{
//				price = product.getDoubleValue(PRICE_PARAM,0d);
//			}

			String priceValue = product.outputValue(PRICE_PARAM);//(price > 0.001) ? String.valueOf(Math.round(price*100d)/100d) : "";
			String qtyValue = product.outputValue(QTY_PARAM);//(product.getDecimalValue(QTY_PARAM) != null) ? String.valueOf(product.getDecimalValue(QTY_PARAM)) : "";
			String priceOldValue = product.outputValue(PRICE_OLD_PARAM);
			String priceOrigValue = product.outputValue(PRICE_ORIGINAL_PARAM);
			String currencyID = product.outputValue(CURRENCY_ID_PARAM);

			row.createCell(++colIdx).setCellValue(product.getStringValue(CODE_PARAM, ""));
			if(writeLineProducts && writeLineProductsHeader) {
				row.createCell(++colIdx).setCellValue("+");
			}
			row.createCell(++colIdx).setCellValue(product.getStringValue(NAME_PARAM, ""));
			row.createCell(++colIdx).setCellValue(priceValue);
			row.createCell(++colIdx).setCellValue(priceOldValue);
			row.createCell(++colIdx).setCellValue(priceOrigValue);
			row.createCell(++colIdx).setCellValue(currencyID);
			row.createCell(++colIdx).setCellValue(qtyValue);
			if(hasUnits) {
				row.createCell(++colIdx).setCellValue(product.getStringValue("unit", ""));
			}
			row.createCell(++colIdx).setCellValue(String.valueOf(product.getByteValue(AVAILABLE_PARAM, (byte) 0)));


			//write all product params
			if(writeAllProductParams){
				colIdx = writeParams(row, product, colIdx, productItemType);
			}

			if(writeManuals){
				StringBuilder manuals = new StringBuilder();
				int i=0;
				for(Item manual : new ItemQuery(MANUAL_PARAM).setParentId(product.getId(), false).loadItems()){
					if(i>0)manuals.append(VALUE_SEPARATOR);
					manuals	.append(manual.getId()).append('|')
							.append(manual.getStringValue(NAME_PARAM)).append('|')
							.append(manual.getStringValue(LINK_PARAM));
					i++;
				}
				row.createCell(++colIdx).setCellValue(manuals.toString());
			}

			if (cellStyle != null) {
				for (int i = 0; i < colIdx + 1; i++) {
					row.getCell(i).setCellStyle(cellStyle);
				}
			}
			if(writeAuxParams) {
				writeAux(row, aux, colIdx);
			}

			info.increaseProcessed();
			if (writeLineProducts) {
				if (product.getByteValue(HAS_LINE_PRODUCTS, (byte) 0) == 1) {
					List<Item> lineProducts = new ItemQuery(LINE_PRODUCT_ITEM).setParentId(product.getId(), false).loadItems();
					info.setToProcess(info.getToProcess() + lineProducts.size());
					info.pushLog(product.getStringValue(NAME_PARAM) + ". Обнаружено вложенных товаров: " + lineProducts.size());
					String parentCode = product.getStringValue(CODE_PARAM, "");
					for (Item lineProduct : lineProducts) {
						colIdx = -1;
						//write aux params
						if (writeAuxParams) {
							aux = new ItemQuery(PARAMS_ITEM).setParentId(lineProduct.getId(), false).loadFirstItem();
							if (aux != null && !aux.getItemType().equals(paramsType)) {
								paramsType = aux.getItemType();
								rowI = initializeHeader(sh, rowI, paramsType);
							}
						}
						row = sh.createRow(++rowI);
						cellStyle = chooseCellStyle(lineProduct);
//						if(priceType == DataType.Type.DECIMAL || priceType == DataType.Type.CURRENCY || priceType == DataType.Type.CURRENCY_PRECISE) {
//							price = lineProduct.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO).doubleValue();
//						}else{
//							price = lineProduct.getDoubleValue(PRICE_PARAM,0d);
//						}
						priceValue = lineProduct.outputValue(PRICE_PARAM);//(price > 0.001) ? String.valueOf(price) : "";
						qtyValue = lineProduct.outputValue(QTY_PARAM);//(lineProduct.getDoubleValue(QTY_PARAM) != null) ? String.valueOf(lineProduct.getDoubleValue(QTY_PARAM)) : "";
						priceOldValue = lineProduct.outputValue(PRICE_OLD_PARAM);
						priceOrigValue = lineProduct.outputValue(PRICE_ORIGINAL_PARAM);
						currencyID = lineProduct.outputValue(CURRENCY_ID_PARAM);

						row.createCell(++colIdx).setCellValue(lineProduct.getStringValue(CODE_PARAM, ""));
						row.createCell(++colIdx).setCellValue("");
						row.createCell(++colIdx).setCellValue(lineProduct.getStringValue(NAME_PARAM, ""));
						row.createCell(++colIdx).setCellValue(priceValue);
						row.createCell(++colIdx).setCellValue(priceOldValue);
						row.createCell(++colIdx).setCellValue(priceOrigValue);
						row.createCell(++colIdx).setCellValue(currencyID);
						row.createCell(++colIdx).setCellValue(qtyValue);
						row.createCell(++colIdx).setCellValue(lineProduct.getStringValue("unit", ""));
						row.createCell(++colIdx).setCellValue(String.valueOf(lineProduct.getByteValue(AVAILABLE_PARAM, (byte) 0)));

						//write all product params/
						if (writeAllProductParams) {
							colIdx = writeParams(row, lineProduct, colIdx, productItemType);
						}

						if (cellStyle != null) {
							for (int i = 0; i < colIdx + 1; i++) {
								row.getCell(i).setCellStyle(cellStyle);
							}
						}

						//write aux params
						if (writeAuxParams) {
							aux = new ItemQuery(PARAMS_ITEM).setParentId(lineProduct.getId(), false).loadFirstItem();
							writeAux(row, aux, colIdx);
						}
						info.increaseProcessed();
					}
				}
			}
		}
		return rowI;
	}

	private int writeParams(Row row, Item product, int colIdx, ItemType productItemType){
		for (ParameterDescription param : productItemType.getParameterList()) {
			String paramName = param.getName();
			if (BUILT_IN_PARAMS.contains(paramName)) continue;
			ArrayList<Object> pv = product.getValues(paramName);
			String value = (pv.size() == 0)? "" : (pv.size() == 1)? pv.get(0).toString() : join(pv);
			row.createCell(++colIdx).setCellValue(value);
		}
		return colIdx;
	}

	private void writeAux(Row row, Item aux, int colIdx){
		if(aux == null) return;
		row.createCell(++colIdx).setCellValue(aux.getTypeId());
		row.getCell(colIdx).setCellStyle(auxStyle);
		for (ParameterDescription param : aux.getItemType().getParameterList()){
			ArrayList<Object> pv = aux.getValues(param.getName());
			String value = (pv.size() == 0)? "" : (pv.size() == 1)? (pv.get(0) == null)? "" : pv.get(0).toString() : join(pv);
			row.createCell(++colIdx).setCellValue(value);
			row.getCell(colIdx).setCellStyle(auxStyle);
		}
	}

	protected static String join(ArrayList<Object> pv) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pv.size(); i++){
			if(i>0)sb.append(VALUE_SEPARATOR);
			String os = pv.get(i).toString();
			os = (StringUtils.isAllBlank(os))? "" : os;
			sb.append(os);
		}
		return sb.toString();
	}

	private CellStyle chooseCellStyle(Item product) {
		if (StringUtils.isBlank(product.getStringValue(CODE_PARAM))) return noCodeStyle;
		if (StringUtils.isBlank(product.outputValue(PRICE_PARAM))) return noPriceStyle;
		return null;
	}

	private int processSubsections(Sheet sh, int rowIndex, long sectionId) throws Exception {
		List<Item> sections = new ItemQuery(SECTION_ITEM).setParentId(sectionId, false).loadItems();
		int rowI = rowIndex;
		for (Item section : sections) {
			info.pushLog(section.getStringValue(NAME_PARAM));
			info.setProcessed(0);
			if(writeHierarchy) {
				Row row = sh.createRow(++rowI);
				int colIdx = -1;
				String[] secInfo = getSectionName(section);
				row.createCell(++colIdx).setCellValue("разд:" + secInfo[0]);
				row.getCell(colIdx).setCellStyle(sectionStyle);
				row.createCell(++colIdx).setCellValue(secInfo[1]);
				row.getCell(colIdx).setCellStyle(sectionStyle);
				row.createCell(++colIdx).setCellValue(secInfo[2]);
				row.getCell(colIdx).setCellStyle(sectionStyle);
			}
			boolean noSubs = new ItemQuery(SECTION_ITEM).setParentId(section.getId(), false).loadFirstItem() == null;
			ItemType auxType = getAuxType(section.getId());
			if(noSubs){
				if(writeHierarchy) {
					rowI = (auxType == null) ? initializeHeader(sh, rowI) : initializeHeader(sh, rowI, auxType);
				}
				rowI = processProducts(sh, rowI, section.getId());
				continue;
			}
			rowI = processSubsections(sh, rowI, section.getId());
		}
		return rowI;
	}

	private ItemType getAuxType(long sectionId) throws Exception {
		if(!writeAuxParams) return null;
		boolean noSubs = new ItemQuery(SECTION_ITEM).setParentId(sectionId, false).loadFirstItem() == null;
		if(!noSubs) return null;
		Item auxParams = new ItemQuery(PARAMS_ITEM).setParentId(sectionId, true).loadFirstItem();
		if (auxParams != null) return auxParams.getItemType();
		return null;
	}

	private void initCellStyles() {
		headerStyle = workBook.createCellStyle();
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());

		auxStyle = workBook.createCellStyle();
		auxStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		auxStyle.setAlignment(HorizontalAlignment.CENTER);
		auxStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		auxStyle.setBorderLeft(BorderStyle.THIN);
		auxStyle.setBorderRight(BorderStyle.THIN);
		auxStyle.setBorderTop(BorderStyle.THIN);
		auxStyle.setBorderBottom(BorderStyle.THIN);
		auxStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());

		auxHeaderStyle = workBook.createCellStyle();
		auxHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		auxHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		auxHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		auxHeaderStyle.setBorderLeft(BorderStyle.THIN);
		auxHeaderStyle.setBorderRight(BorderStyle.THIN);
		auxHeaderStyle.setBorderTop(BorderStyle.THIN);
		auxHeaderStyle.setBorderBottom(BorderStyle.THIN);

		auxHeaderStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());


		noPriceStyle = workBook.createCellStyle();
		noPriceStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		noPriceStyle.setBorderLeft(BorderStyle.THIN);
		noPriceStyle.setBorderRight(BorderStyle.THIN);
		noPriceStyle.setBorderTop(BorderStyle.THIN);
		noPriceStyle.setBorderBottom(BorderStyle.THIN);
		noPriceStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());

		noCodeStyle = workBook.createCellStyle();
		noCodeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		noCodeStyle.setBorderLeft(BorderStyle.THIN);
		noCodeStyle.setBorderRight(BorderStyle.THIN);
		noCodeStyle.setBorderTop(BorderStyle.THIN);
		noCodeStyle.setBorderBottom(BorderStyle.THIN);
		noCodeStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());

		sectionStyle = workBook.createCellStyle();
		sectionStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		sectionStyle.setAlignment(HorizontalAlignment.CENTER);
		sectionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		sectionStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
	}

	@Override
	protected void terminate() throws Exception {

	}

	@Override
	protected boolean makePreparations() throws Exception {
		String prodParamsVar = getVarSingleValue(PROD_PARAMS_VAR);
		String auxParamsVar = getVarSingleValue(AUX_PARAMS_VAR);
		String productsVar = getVarSingleValue(PRODUCTS_VAR);
		String manualsVar = getVarSingleValue(MANUALS_VAR);
		String lineProductsVar = getVarSingleValue(LINE_PRODUCTS_VAR);
		String priceOnlyVar = getVarSingleValue(PRICE_ONLY_VAR);

		writeAllProductParams = (YES.equals(prodParamsVar)) || writeAllProductParams && !NO.equals(prodParamsVar);
		writeAuxParams = (YES.equals(auxParamsVar)) || writeAuxParams && !NO.equals(auxParamsVar);
		writeProducts = (YES.equals(productsVar)) || writeProducts && !NO.equals(productsVar);
		writeManuals = (YES.equals(manualsVar)) || writeManuals && !NO.equals(manualsVar);
		writeLineProducts = (YES.equals(lineProductsVar)) || writeLineProducts && !NO.equals(lineProductsVar);

		writeManuals = ItemTypeRegistry.getItemType(MANUAL_PARAM) != null && writeManuals;
		hasUnits = ItemTypeRegistry.getItemType(PRODUCT_ITEM).getParameter("unit") != null;
		writeLineProducts = ItemTypeRegistry.getItemType(LINE_PRODUCT_ITEM) != null && writeLineProducts;

		//write prices only
		if (YES.equalsIgnoreCase(priceOnlyVar)) {
			writeAllProductParams = false;
			writeAuxParams = false;
			writeHierarchy = false;
			writeLineProductsHeader = false;
			writeManuals = false;
		}

		String sectionId = getVarSingleValue(SECTION_VAR);
		if (StringUtils.isNotBlank(sectionId)) secId = Long.parseLong(sectionId);
		return true;
	}
}
