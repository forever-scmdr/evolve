package ecommander.special.portal;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.integration.ExcelDocumentGenerator;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.model.datatypes.TupleDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.providers.OuterInputData;
import extra._generated.ItemNames;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Команда, которая преобразует строку, введенную пользователем, в XML структуру для BOM запроса
 * Структура:
 *
 * <bom>
 *     <query qty="100">112233</query>
 *     <query qty="20">device 334</query>
 *     <query qty="50">qwerty 334 mmt</query>
 * </bom>
 *
 */
public class ManageBomCommand extends Command implements ItemNames {

    public static final String REGISTERED = "registered";

    private final String CACHE_DIR = "files/search";

    @Override
    public ResultPE execute() throws Exception {
        return null;
    }

    /**
     * Создать BOM XML запрос из переданной строки
     * @return
     * @throws EcommanderException
     */
    public ResultPE format() throws EcommanderException {
        ResultPE result = getResult("xml");
        if (getItemForm() == null)
            return result;
        String query = getItemForm().getSingleStringExtra("q");
        if (StringUtils.isBlank(query)) {
            query = createQueryFromExcel();
        }
        if (StringUtils.isNotBlank(query)) {
            OuterInputData input = OuterInputData.createForBomParsing(query);
            int maxQuerySize = 0;
            XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
            xml.startElement("bom");
            for (String singleQuery : input.getQueries().keySet()) {
                xml.addElement("query", singleQuery, "qty", input.getQueries().get(singleQuery));
                maxQuerySize = Math.max(maxQuerySize, singleQuery.length());
            }
            xml.addElement("max_size", maxQuerySize);
            xml.endElement();
            result.setValue(xml.toString());
        }
        // установить переменную страницы, чтобы можно было потом использовать (эта переменная сейчас в itemform)
        setPageVariable("q", query);
        return result;
    }

    /**
     * Новый список
     * @return
     * @throws Exception
     */
    public ResultPE saveNew() throws Exception {
        User user = getInitiator();
        if (!user.inGroup(REGISTERED))
            return null;
        Item userItem = new ItemQuery(USER).setUser(user).loadFirstItem();
        if (userItem == null)
            return null;
        Item catalog = ItemUtils.ensureSingleItem(BOM_CATALOG, user, userItem.getId(), UserGroupRegistry.getGroup(REGISTERED), user.getUserId());
        Item newList = Item.newChildItem(ItemTypeRegistry.getItemType(BOM_LIST), catalog);
        String query = getInputSingleValueDefault("q", null);
        if (StringUtils.isNotBlank(query)) {
            String name = getInputSingleValueDefault("name", "Новый BOM-лист");
            String desc = getInputSingleValueDefault("desc", "");
            OuterInputData input = OuterInputData.createForBomParsing(query);
            for (String line : input.getQueries().keySet()) {
                String qty = input.getQueries().get(line) + "";
                newList.setValue(bom_list_.LINE, TupleDataType.newTuple(line, qty));
            }
            if (newList.isValueNotEmpty(bom_list_.LINE)) {
                Item maxPosition = new ItemQuery(BOM_LIST).setParentId(catalog.getId(), false)
                        .setAggregation(bom_list_.SORT_POSITION, "MAX", "ASC")
                        .loadFirstItem();
                int position = 0;
                if (maxPosition != null) {
                    position = maxPosition.getIntValue(bom_list_.SORT_POSITION, 0);
                }
                position++;
                newList.setValue(bom_list_.SORT_POSITION, position);
                newList.setValue(bom_list_.NAME, name);
                newList.setValue(bom_list_.DESCRIPTION, desc);
                newList.setValue(bom_list_.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
                executeAndCommitCommandUnits(SaveItemDBUnit.get(newList));
            }
        }
        String refreshStr = getInputSingleValueDefault("refresh", "false");
        boolean refresh = StringUtils.equalsAnyIgnoreCase(refreshStr, "true", "yes");
        return refresh ? getResult("saved_refresh") : getResult("saved");
    }

    /**
     * Обновить только название и описание бом листа
     * @return
     * @throws Exception
     */
    public ResultPE updateName() throws Exception {
        User user = getInitiator();
        if (!user.inGroup(REGISTERED))
            return null;
        Item bom = getSingleLoadedItem("bom");
        if (bom == null)
            return null;
        String name = getInputSingleValueDefault("name", null);
        String desc = getInputSingleValueDefault("desc", null);
        if (StringUtils.isBlank(name))
            return null;
        bom.setValue(bom_list_.NAME, name);
        bom.setValue(bom_list_.DESCRIPTION, desc);
        executeAndCommitCommandUnits(SaveItemDBUnit.get(bom));
        return getResult("name_updated");
    }

    /**
     * Удалить строку из списка BOM
     * @return
     * @throws Exception
     */
    public ResultPE deleteLine() throws Exception {
        User user = getInitiator();
        if (!user.inGroup(REGISTERED))
            return null;
        Item bom = getSingleLoadedItem("bom");
        if (bom == null)
            return null;
        ArrayList<String> vals = getInputValues("name");
        if (vals.size() == 0)
            return null;
        for (String tupleStr : vals) {
            tupleStr = StringUtils.replace(tupleStr, "$ $", "$+$");
            Object tuple = TupleDataType.parse(tupleStr, TupleDataType.DEFAULT_SEPARATOR);
            bom.removeEqualValue(bom_list_.LINE, tuple);
        }
        executeAndCommitCommandUnits(SaveItemDBUnit.get(bom));
        return getResult("list_updated");
    }

    /**
     * Добавить строку к списку BOM
     * @return
     * @throws Exception
     */
    public ResultPE addLine() throws Exception {
        User user = getInitiator();
        if (!user.inGroup(REGISTERED))
            return null;
        Item bom = getSingleLoadedItem("bom");
        if (bom == null)
            return null;
        String name = getInputSingleValueDefault("name", null);
        String qty = getInputSingleValueDefault("qty", null);
        if (StringUtils.isBlank(name) || StringUtils.isBlank(qty))
            return null;
        Object tuple = TupleDataType.newTuple(name, qty);
        bom.setValue(bom_list_.LINE, tuple);
        executeAndCommitCommandUnits(SaveItemDBUnit.get(bom));
        return getResult("list_updated");
    }

    /**
     * Удалить выбранный BOM список
     * @return
     * @throws Exception
     */
    public ResultPE deleteBom() throws Exception {
        User user = getInitiator();
        if (!user.inGroup(REGISTERED))
            return null;
        LinkedHashMap<Long, Item> boms = getLoadedItems("bom");
        if (boms.size() ==0)
            return null;
        for (Item bom : boms.values()) {
            executeAndCommitCommandUnits(ItemStatusDBUnit.delete(bom));
        }
        commitCommandUnits();
        return getResult("all_updated");
    }

    public ResultPE xlsExport() throws Exception {
        User user = getInitiator();
        if (!user.inGroup(REGISTERED))
            return null;
        LinkedHashMap<Long, Item> boms = getLoadedItems("bom");
        if (boms.size() ==0)
            return null;
        ExcelDocumentGenerator gen = new ExcelDocumentGenerator();
        gen.createDocument();
        for (Item bom : boms.values()) {
            gen.addSheet(bom.getStringValue(bom_list_.NAME));
            gen.addRow();
            gen.addHeaderCell("Партномер", 50);
            gen.addHeaderCell("Кол-во", 10);
            for (Pair<String, String> value : bom.getTupleValues(bom_list_.LINE)) {
                gen.addRow();
                gen.addCell(value.getLeft(), 50);
                gen.addCell(value.getRight(), 10);
            }
        }
        String fileName = "bom_" + System.currentTimeMillis() + ".xlsx";
        String url = gen.saveDoc(fileName);
        return new ResultPE("file", ResultPE.ResultType.redirect).setValue(url);
    }

    /**
     * Прочитать запросы из файла (эксель или csv или текстового)
     * @return
     */
    private String createQueryFromExcel() {
        final StringBuilder sb = new StringBuilder();
        try {
            FileItem fi = getItemForm().getSingleFileExtra("file");
            String ext = StringUtils.substringAfterLast(fi.getName(), ".");
            TableDataSource src;
            if (StringUtils.equalsAnyIgnoreCase(ext, "xls", "xlsx")) {
                src = new ExcelTableData(fi);
            } else if (StringUtils.equalsIgnoreCase(ext, "csv")) {
                src = new CharSeparatedTxtTableData(fi, StandardCharsets.UTF_8, true);
            } else {
                src = new CharSeparatedTxtTableData(fi, StandardCharsets.UTF_8, false);
            }
            src.iterate(new TableDataRowProcessor() {
                @Override
                public void processRow(TableDataSource src) throws Exception {
                    StringBuilder sbLine = new StringBuilder();
                    for (int i = 0; i <= src.getLastColIndex(); i++) {
                        String value = src.getValue(i);
                        if (StringUtils.isNotBlank(value)) {
                            if (sbLine.length() > 0) {
                                sbLine.append(" ");
                            }
                            // это для того, чтобы числа с пробелом вроде 1 000 воспринимались одним целым
                            String noBlank = value.replaceAll("\\s", "").replace("\u00a0","");
                            if (StringUtils.isNumericSpace(noBlank)) {
                                int test = NumberUtils.toInt(noBlank, -5555);
                                if (test > 0)
                                    value = test + "";
                            }
                            sbLine.append(value);
                        }
                    }
                    if (StringUtils.isNotBlank(sbLine)) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(sbLine);
                    }
                }
            });
        } catch (Exception e) {
            ServerLogger.error("Error parsing excel search query", e);
        }
        return sb.toString();
    }


    /**
     * Очистить кеш результатов запросов
     * @return
     * @throws EcommanderException
     */
    public ResultPE clearCache() throws EcommanderException {
        String cacheDirName = AppContext.getRealPath(CACHE_DIR);
        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
        try {
            FileUtils.deleteDirectory(new File(cacheDirName));
            xml.addElement("message", "Кеш очищен");
        } catch (Exception e) {
            xml.addElement("message", "Ошибка удаления фидектории " + cacheDirName);
            xml.addElement("error", ExceptionUtils.getStackTrace(e));
        }
        return getResult("xml").setValue(xml.toString());
    }

}
