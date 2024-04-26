package ecommander.special.portal;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.special.portal.outer.providers.OuterInputData;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

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
public class FormatBomQueryCommand extends Command {

    private final String CACHE_DIR = "files/search";

    @Override
    public ResultPE execute() throws Exception {
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
        return result;
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
