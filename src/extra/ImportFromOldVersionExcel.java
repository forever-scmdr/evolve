package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.POIUtils;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import ecommander.persistence.mappers.LuceneIndexMapper;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportFromOldVersionExcel extends CreateParametersAndFiltersCommand implements CatalogConst {
    private static final long ABSENT_PRODUCT_LIFETIME = 24 * 30 * 60 * 60 * 60 * 1000;
    Workbook priceWB;
    Item catalog;
    Item currentSection;
    Item currentProduct;
    private HashSet<Long> sectionsWithNewItemTypes = new HashSet<>();
    private FormulaEvaluator eval;
    private HashMap<Integer, String> PARAM_INDEXES = new HashMap<>();
    private HashMap<Integer, String> AUX_PARAMS = new HashMap<>();
    private static final String FILE_UPLOAD_FOLDER = "pdf";
    private static final int LOAD_BATCH_SIZE = 1000;
    private static final int HIDE_BATCH_SIZE = 500;
    private static final int DELETE_BATCH_SIZE = 100;

    //File constants
    private final static HashMap<String, String> HEADER_PARAMS = new HashMap<>();

    static {
        //HEADER_PARAMS.put("артикул", CODE_PARAM);
        HEADER_PARAMS.put("наименование", NAME_PARAM);
        HEADER_PARAMS.put("наличие", QTY_PARAM);
        HEADER_PARAMS.put("ед. изм", UNIT_PARAM);
        HEADER_PARAMS.put("кратность", MIN_QTY_PARAM);
        HEADER_PARAMS.put("цена 1", PRICE_PARAM);
        HEADER_PARAMS.put("себестоимость", PRICE_OPT_PARAM);
        HEADER_PARAMS.put("наценка 1", MARGIN_PARAM);
        HEADER_PARAMS.put("описание", DESCRIPTION_PARAM);
        HEADER_PARAMS.put("картинка", MAIN_PIC_PARAM);
        HEADER_PARAMS.put("pdf", "pdf");
    }

    ;

    @Override
    protected boolean makePreparations() throws Exception {
        //PARAM_INDEXES.put(0, CODE_PARAM);
        catalog = ItemQuery.loadSingleItemByName(CATALOG_ITEM);
        addLog(catalog.toString());
        File excelFile = catalog.getFileValue("big_integration", AppContext.getFilesDirPath(false));
        info.setCurrentJob("Opening: " + excelFile.getAbsolutePath());
        if (excelFile == null || !excelFile.isFile()) {
            info.addError("Excel file does not exist.", "");
            catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 0);
            executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
            return false;
        }
        priceWB = POIUtils.openExcel(excelFile).getWorkbook();
        eval = priceWB.getCreationHelper().createFormulaEvaluator();
        return true;
    }

    @Override
    protected void integrate() throws Exception {
        catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 1);
        executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
        hideAllProducts();
        setOperation("Обновлние каталога");
        setProcessed(0);
        setLineNumber(0);
        //parsing from Excel
        info.setToProcess(getLinesCount(priceWB));
        parse(priceWB);
        info.setCurrentJob("");
        priceWB.close();
        //clear junk
        clearJunk();
        //creating filters and item types
        createFiltersAndItemTypes();
        catalog.setValue(INTEGRATION_PENDING_PARAM, (byte) 0);

        deleteHidden();

        executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).noFulltextIndex().noTriggerExtra());
        //indexation
        info.setOperation("Индексация названий товаров");
        LuceneIndexMapper.getSingleton().reindexAll();
        setOperation("Интеграция завершена");
    }

    private void hideAllProducts() throws Exception {
        setOperation("Скрываем товары");
        setProcessed(0);
        LinkedList<Item> products = new LinkedList<>();
        products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, 0L));
        long id = 0;
        int counter = 0;
        while (products.size() > 0) {
            while (products.size() != 0) {
                Item product = products.poll();
                id = product.getId();
                List<String> tags = product.getStringValues(TAG_PARAM);
                if(!tags.contains("external_shop")) {
                    executeCommandUnit(ItemStatusDBUnit.hide(product).ignoreUser(true).noFulltextIndex());
                    counter++;
                }
                if (counter >= HIDE_BATCH_SIZE) commitCommandUnits();
                info.increaseProcessed();
            }
            products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, id));
        }
        commitCommandUnits();
    }

    private void deleteHidden() throws Exception {
        setOperation("Удаляем долго отсутствующие товары");
        setProcessed(0);
        Queue<Item> products = new LinkedList<>();

        long now = new Date().getTime();
        products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, 0L, Item.STATUS_HIDDEN));
        long id = 0;
        int counter = 0;
        while (products.size() > 0) {
            while (products.size() != 0) {
                Item product = products.poll();
                id = product.getId();
                if(now - product.getTimeUpdated() > ABSENT_PRODUCT_LIFETIME){
                    executeCommandUnit(ItemStatusDBUnit.delete(product.getId()).ignoreUser(true).noFulltextIndex());
                    counter++;
                    info.increaseProcessed();
                    if(counter > DELETE_BATCH_SIZE){
                        commitCommandUnits();
                    }
                }
            }
            products.addAll(ItemMapper.loadByName(ItemNames.PRODUCT, LOAD_BATCH_SIZE, id));
        }
        commitCommandUnits();
    }

    private void clearJunk() throws Exception {
        setOperation("Удаление товаров без артикула");
        long startID = 0;
        ArrayList<Item> products;
        info.setProcessed(0);
        int counter = 0;
        int deletedCounter = 0;
        while ((products = ItemMapper.loadByName(PRODUCT_ITEM, DELETE_BATCH_SIZE, startID, Item.STATUS_NORMAL, Item.STATUS_HIDDEN)).size() > 0) {
            for (Item product : products) {
                startID = product.getId();
                if (StringUtils.isBlank(product.getStringValue(CODE_PARAM))) {
                    executeAndCommitCommandUnits(ItemStatusDBUnit.delete(product.getId()));
                    deletedCounter++;
                }
                info.increaseProcessed();
                counter++;
            }
        }
        pushLog("Отсутсвующих товаров: " + counter);
        pushLog("Удалено товаров" + deletedCounter);
    }

    private void parse(Workbook priceWB) throws Exception {
        eval = priceWB.getCreationHelper().createFormulaEvaluator();
        for (int i = 0; i < priceWB.getNumberOfSheets(); i++) {
            Sheet sheet = priceWB.getSheetAt(i);
            info.setCurrentJob("Лист: " + sheet.getSheetName());
            Iterator<Row> rows = sheet.iterator();
            HashMap<Integer, String> cellValues = new HashMap<Integer, String>(2);
            cellValues.put(0, CODE_PARAM);
            cellValues.put(1, NAME_PARAM);
            while (rows.hasNext()) {
                Row row = rows.next();
                int rowIdx = row.getRowNum();
                info.setLineNumber(rowIdx + 1);
                if(getFilledCellsCount(row) < 1) continue;

                //SECTION
                String sectionCode = getCellAsString(row.getCell(0));
                String sectionName = getCellAsString(row.getCell(1));

                sectionName = sectionName.matches("Прочее \\(\\d+\\)") ? "Прочее" : sectionName;

                if (getFilledCellsCount(row) == 2 && StringUtils.isNotBlank(sectionCode) && StringUtils.isNotBlank(sectionName)) {

                    boolean isNew = false;

                    //section exists
                    currentSection = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, sectionCode);

                    //section not exists
                    if (currentSection == null) {
                        if (sectionCode.indexOf('.') == -1) {
                            currentSection = ItemUtils.newChildItem(SECTION_ITEM, catalog);
                        } else {
                            String parentCode = StringUtils.substringBeforeLast(sectionCode, ".");
                            Item parent = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, CODE_PARAM, parentCode);
                            currentSection = ItemUtils.newChildItem(SECTION_ITEM, parent);
                        }
                        isNew = !"Прочее".equals(sectionName);
                    }

                    currentSection.setValue(CODE_PARAM, sectionCode);
                    currentSection.setValue(NAME_PARAM, sectionName);
                    executeAndCommitCommandUnits(SaveItemDBUnit.get(currentSection).noFulltextIndex());
                    //if(isNew) sectionsWithNewItemTypes.add(currentSection.getId());

                }

                //HEADERS
                else if (StringUtils.isBlank(getCellAsString(row.getCell(0))) && "наименование".equalsIgnoreCase(getCellAsString(row.getCell(1)))) {
                    initHeaders(row);
                }

                //PRODUCT
                else if (StringUtils.isNotBlank(sectionCode)) {

                    HashMap<String, String> userDefined = new HashMap<>();
                    XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();

                    Iterator<Cell> iterator = row.cellIterator();
                    while (iterator.hasNext()) {
                        Cell cell = iterator.next();
                        int index = cell.getColumnIndex();
                        String paramName = PARAM_INDEXES.get(index);


                        String cellValue = getCellAsString(cell);


                        if (paramName == null) {
                            paramName = AUX_PARAMS.get(index);
                            if (paramName == null) continue;

                            userDefined.put(paramName, cellValue);

                            xml.startElement("parameter")
                                    .startElement("name")
                                    .addText(firstUpperCase(paramName))
                                    .endElement()
                                    .startElement("value")
                                    .addText(cellValue)
                                    .endElement()
                                    .endElement();
                        } else if (CODE_PARAM.equals(paramName)) {
                            List<Item> duplicates = ItemQuery.loadByParamValue(PRODUCT_ITEM, CODE_PARAM, cellValue, Item.STATUS_HIDDEN, Item.STATUS_NORMAL);
                            //Collections.sort(duplicates, (o1, o2) -> Long.compare(o1.getId(), o2.getId()));
                            currentProduct = duplicates.size() == 0? null : duplicates.remove(0);

                            for(Item duplicate : duplicates){
                                executeAndCommitCommandUnits(ItemStatusDBUnit.delete(duplicate.getId()).ignoreUser(true).noFulltextIndex());
                            }

                            if (currentProduct == null) {
                                currentProduct = ItemUtils.newChildItem(PRODUCT_ITEM, currentSection);
                                currentProduct.setValue(CODE_PARAM, cellValue);
                            } else {
                                executeCommandUnit(ItemStatusDBUnit.restore(currentProduct.getId()));
                            }
                        } else if (MAIN_PIC_PARAM.equals(paramName)) {
                            String[] pics = StringUtils.split(cellValue, '|');
                            if (pics.length > 0) {
                                if (needNewFile(currentProduct, pics[0])) {
                                    if (!pics[0].equals("no-image.png")) {
                                        File f = Paths.get(FILE_UPLOAD_FOLDER, pics[0].trim()).toFile();
                                        if (f.isFile()) {
                                            currentProduct.setValue(MAIN_PIC_PARAM, f);
                                        }
                                    }

                                }
                                if (pics.length > 1) {
                                    boolean needClear = true;
                                    for (int j = 1; j < pics.length; j++) {
                                        File f = Paths.get(FILE_UPLOAD_FOLDER, pics[j].trim()).toFile();
                                        if (f.isFile()) {
                                            if (needClear) {
                                                currentProduct.clearValue(GALLERY_PARAM);
                                                needClear = false;
                                            }
                                            currentProduct.setValue(GALLERY_PARAM, f);
                                        }
                                    }
                                }
                                String v = currentProduct.getStringValue(MAIN_PIC_PARAM, "");
                                if (v.equals("no-image.png")) {
                                    currentProduct.clearValue(MAIN_PIC_PARAM);
                                    currentProduct.clearValue(SMALL_PIC_PARAM);
                                }
                            }
                        } else if ("pdf".equalsIgnoreCase(paramName)) {
                            String[] split = StringUtils.split(cellValue, '|');
                            currentProduct.clearValue("pdf");
                            for (String s : split) {
                                currentProduct.setValue("pdf", s.trim());
                            }
                        } else {
                            currentProduct.setValueUI(paramName, cellValue);
                        }
                    }
                    if (currentProduct == null) {
                        info.increaseProcessed();
                        continue;
                    }
                    String searchString = generateSearchParam(userDefined);
                    currentProduct.setValueUI("search", searchString);
                    String unit = currentProduct.getStringValue(UNIT_PARAM, "шт");
                    double min = currentProduct.getDoubleValue(MIN_QTY_PARAM, 1d);
                    double qty = currentProduct.getDoubleValue(QTY_PARAM, 0d);
                    currentProduct.setValue(QTY_PARAM, qty);
                    currentProduct.setValue(UNIT_PARAM, unit);
                    currentProduct.setValue(STEP_PARAM, min);
                    byte available = currentProduct.getDoubleValue(QTY_PARAM, 0d) > 0 && currentProduct.getDecimalValue(PRICE_PARAM, BigDecimal.ZERO) != BigDecimal.ZERO ? (byte) 1 : (byte) 0;
                    currentProduct.setValue(AVAILABLE_PARAM, available);
                    executeAndCommitCommandUnits(SaveItemDBUnit.get(currentProduct).noFulltextIndex().ignoreFileErrors().ignoreUser().noTriggerExtra());

                    if (StringUtils.isNotBlank(xml.toString())) {
                        ItemQuery query = new ItemQuery(ItemTypeRegistry.getItemType(PARAMS_XML_ITEM)).setParentId(currentProduct.getId(), false);
                        List<Item> items = query.loadItems();
                        Item paramsXML;
                        if (items == null || items.size() == 0) {
                            paramsXML = ItemUtils.newChildItem(PARAMS_XML_ITEM, currentProduct);
                        } else if (items.size() > 1) {
                            for (Item item : items) {
                                executeAndCommitCommandUnits(ItemStatusDBUnit.delete(item.getId()).ignoreFileErrors().ignoreUser());
                            }
                            paramsXML = ItemUtils.newChildItem(PARAMS_XML_ITEM, currentProduct);
                        } else {
                            paramsXML = items.get(0);
                        }
                        paramsXML.setValueUI(XML_PARAM, xml.toString());
                        if (!"Прочее".equals(currentSection.getStringValue(NAME_PARAM, "")))
                            sectionsWithNewItemTypes.add(currentSection.getId());
                        executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXML).noFulltextIndex().ignoreFileErrors().ignoreUser().noTriggerExtra());
                    }

                }
                info.increaseProcessed();
            }
        }
    }

    private int getFilledCellsCount(Row row) {
        int c = 0;
        Iterator<Cell> iterator = row.cellIterator();
        while (iterator.hasNext()) {
            Cell y = iterator.next();
            if (StringUtils.isNotBlank(getCellAsString(y))) c++;
        }
        return c;
    }

    private String generateSearchParam(HashMap<String, String> userDefined) {
        StringBuilder sb = new StringBuilder();
        sb.append(currentSection.getStringValue(NAME_PARAM, "")).append(' ');
        sb.append(processString(currentProduct.getStringValue(NAME_PARAM, ""))).append(' ');
        sb.append(currentProduct.getStringValue(CODE_PARAM, ""));

        userDefined.forEach((k, v) -> {
            sb.append(' ').append(k);
            sb.append(' ').append(processString(v));
        });
        return sb.toString();
    }

    public static String processString(String arg) {
        if (StringUtils.isBlank(arg)) return "";
        String regexp = "(?<number>\\d+([.,/]\\d+)*)";
        Pattern p = Pattern.compile(regexp);
        Matcher m = p.matcher(arg);
        String x;
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String n = m.group("number");
            m.appendReplacement(sb, " " + n + " ");
        }
        m.appendTail(sb);
        x = sb.toString();
        x = x.replaceAll("[+-]", "");
        x = x.replaceAll("\\(", "");
        x = x.replaceAll("\\)", "");
        x = x.replaceAll("\\s+", " ");
        x = x.trim();
        return x;
    }

    private boolean needNewFile(Item product, String path) {
        if (StringUtils.isBlank(path) || "no-image.png".equals(path)) return false;
        File existingFile = product.getFileValue(MAIN_PIC_PARAM, AppContext.getFilesDirPath(product.isFileProtected()));
        File newFile = Paths.get(FILE_UPLOAD_FOLDER, path).toFile();
        if (!newFile.isFile()) return false;
        if (existingFile.isFile() && !existingFile.getName().equals(newFile.getName())) return true;
        int existingFileHash = existingFile.hashCode();
        int newFileHash = newFile.hashCode();
        return existingFileHash != newFileHash;
    }

    private void initHeaders(Row row) {
        Iterator<Cell> iterator = row.cellIterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            String cellValue = getCellAsString(cell).toLowerCase().trim();
            if (StringUtils.isBlank(cellValue)) continue;
            String paramName = HEADER_PARAMS.get(cellValue);
            if (StringUtils.isBlank(paramName)) {
                AUX_PARAMS.put(cell.getColumnIndex(), cellValue);
            } else {
                PARAM_INDEXES.put(cell.getColumnIndex(), paramName);
            }
        }
        PARAM_INDEXES.put(0,CODE_PARAM);
    }

    private String getCellAsString(Cell cell) {
        return POIUtils.getCellAsString(cell, eval);
    }

    private int getLinesCount(Workbook priceWB) {
        int rows = 0;
        for (int i = 0; i < priceWB.getNumberOfSheets(); i++) {
            Sheet sheet = priceWB.getSheetAt(i);
            int first = sheet.getFirstRowNum();
            int last = sheet.getLastRowNum();
            rows += last - first + 1;
        }
        return rows;
    }

    private void createFiltersAndItemTypes() throws Exception {
        if (sectionsWithNewItemTypes.size() == 0) return;
        setOperation("Создание классов и фильтров");
        List<Item> sections = ItemQuery.loadByIdsLong(sectionsWithNewItemTypes);
        doCreate(sections);
    }

    private String firstUpperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
